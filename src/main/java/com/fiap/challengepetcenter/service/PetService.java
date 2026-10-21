package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.PetRequestDTO;
import com.fiap.challengepetcenter.dto.response.PetResponseDTO;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.UserRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Service
public class PetService {

    private static final Logger log = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final DiarioEntradaRepository diarioEntradaRepository;
    private final Counter petsCriados;
    private final Timer tempoCriacaoPet;
    private final Timer tempoListagemPets;

    @Autowired
    public PetService(PetRepository petRepository, UserRepository userRepository, DiarioEntradaRepository diarioEntradaRepository, MeterRegistry registry) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.diarioEntradaRepository = diarioEntradaRepository;

        this.petsCriados = Counter.builder("petcenter.pets.criados")
                .description("Total de pets criados com sucesso")
                .register(registry);

        this.tempoCriacaoPet = Timer.builder("petcenter.pets.criacao.tempo")
                .description("Tempo de execução da criação de alertas")
                .register(registry);

        this.tempoListagemPets = Timer.builder("petcenter.pets.listagem.tempo")
                .description("Tempo de execução da listagem paginada de pets")
                .register(registry);
    }

    @Transactional
    public PetResponseDTO salvar(PetRequestDTO requestDTO) {
        long inicio = System.nanoTime();

        User usuarioLogado = getUsuarioAutenticado();

        Pet pet = new Pet();
        pet.setUser(usuarioLogado);
        pet.setNome(requestDTO.nome());
        pet.setEspecie(requestDTO.especie());
        pet.setRaca(requestDTO.raca());
        pet.setDataNascimento(requestDTO.dataNascimento());
        pet.setObservacoes(requestDTO.observacoes());

        Pet petSalvo = petRepository.save(pet);

        long duracao = System.nanoTime() - inicio;
        tempoCriacaoPet.record(duracao, TimeUnit.NANOSECONDS);

        petsCriados.increment();

        log.info("Pet ID {} criado com sucesso", petSalvo.getId());

        return PetResponseDTO.fromEntity(petSalvo);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> listarTodos(Pageable pageable) {
        long inicio = System.nanoTime();

        Page<PetResponseDTO> pets = petRepository.findAll(pageable)
                .map(PetResponseDTO::fromEntity);

        long duracao = System.nanoTime() - inicio;
        tempoListagemPets.record(duracao, TimeUnit.NANOSECONDS);

        return pets;
    }

    @Transactional(readOnly = true)
    public PetResponseDTO buscarPorId(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com ID: " + id));
        return PetResponseDTO.fromEntity(pet);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorUserId(Long userId, Pageable pageable) {
        return petRepository.findByUserId(userId, pageable)
                .map(PetResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorNome(String nome, Pageable pageable) {
        return petRepository.findByNomeContaining(nome, pageable)
                .map(PetResponseDTO::fromEntity);
    }

    @Transactional
    public PetResponseDTO atualizar(Long id, PetRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        Pet petExistente = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com ID: " + id));

        if (!petExistente.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode atualizar um pet de outro usuário");
        }

        petExistente.setNome(requestDTO.nome());
        petExistente.setEspecie(requestDTO.especie());
        petExistente.setRaca(requestDTO.raca());
        petExistente.setDataNascimento(requestDTO.dataNascimento());
        petExistente.setObservacoes(requestDTO.observacoes());

        Pet petAtualizado = petRepository.save(petExistente);

        log.info("Pet ID {} atualizado com sucesso", petAtualizado.getId());

        return PetResponseDTO.fromEntity(petAtualizado);
    }

    @Transactional
    public void deletar(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        Pet petExistente = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com ID: " + id));

        if (!petExistente.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode excluir um pet de outro usuário");
        }

        if (diarioEntradaRepository.existsByPetId(id)) {
            throw new RuntimeException("Não é possível excluir o pet pois existem entradas de diário vinculadas a ele");
        }

        petRepository.deleteById(id);

        log.info("Pet ID {} excluído com sucesso", id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

}