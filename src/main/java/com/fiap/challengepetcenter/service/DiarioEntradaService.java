package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.DiarioEntradaRequestDTO;
import com.fiap.challengepetcenter.dto.response.DiarioEntradaResponseDTO;
import com.fiap.challengepetcenter.exception.AcessoNegadoException;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.exception.RegraNegocioException;
import com.fiap.challengepetcenter.model.DiarioEntrada;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.RegistroRepository;
import com.fiap.challengepetcenter.repository.UserRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
public class DiarioEntradaService {

    private static final Logger log = LoggerFactory.getLogger(DiarioEntradaService.class);

    private final DiarioEntradaRepository diarioEntradaRepository;
    private final PetRepository petRepository;
    private final RegistroRepository registroRepository;
    private final UserRepository userRepository;
    private final Counter diarioEntradasCriados;
    private final Timer tempoCriacaoDiarioEntradas;
    private final Timer tempoListagemDiarioEntradas;
    private final Timer tempoBuscaDiarioEntradasPorData;

    @Autowired
    public DiarioEntradaService(DiarioEntradaRepository diarioEntradaRepository, PetRepository petRepository, RegistroRepository registroRepository, UserRepository userRepository, MeterRegistry registry) {
        this.diarioEntradaRepository = diarioEntradaRepository;
        this.petRepository = petRepository;
        this.registroRepository = registroRepository;
        this.userRepository = userRepository;

        this.diarioEntradasCriados = Counter.builder("petcenter.diarioEntradas.criados")
                .description("Total de entradas no diário criados com sucesso")
                .register(registry);

        this.tempoCriacaoDiarioEntradas = Timer.builder("petcenter.diarioEntradas.criacao.tempo")
                .description("Tempo de execução da criação de entradas no diário")
                .register(registry);

        this.tempoListagemDiarioEntradas = Timer.builder("petcenter.diarioEntradas.listagem.tempo")
                .description("Tempo de execução da listagem paginada de entradas no diário")
                .register(registry);

        this.tempoBuscaDiarioEntradasPorData = Timer.builder("petcenter.diarioEntradas.busca.data.tempo")
                .description("Tempo de execução da busca paginada de entradas no diário por data")
                .register(registry);
    }

    @Transactional
    public DiarioEntradaResponseDTO salvar(DiarioEntradaRequestDTO requestDTO) {
        long inicio = System.nanoTime();

        User usuarioLogado = getUsuarioAutenticado();

        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado com ID: " + requestDTO.petId()));

        if (!pet.getUser().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não pode criar uma entrada para o pet de outro usuário");
        }

        DiarioEntrada diarioEntrada = new DiarioEntrada();
        diarioEntrada.setPet(pet);
        diarioEntrada.setData(requestDTO.data());
        diarioEntrada.setResumo(requestDTO.resumo());
        diarioEntrada.setHumorGeral(requestDTO.humorGeral());
        diarioEntrada.setStatus(requestDTO.status());

        DiarioEntrada diarioEntradaSalvo = diarioEntradaRepository.save(diarioEntrada);

        long duracao = System.nanoTime() - inicio;
        tempoCriacaoDiarioEntradas.record(duracao, TimeUnit.NANOSECONDS);

        diarioEntradasCriados.increment();

        log.info("Entrada do diário ID {} criada com sucesso", diarioEntradaSalvo.getId());

        return DiarioEntradaResponseDTO.fromEntity(diarioEntradaSalvo);
    }

    @Transactional(readOnly = true)
    public Page<DiarioEntradaResponseDTO> listarTodos(Pageable pageable) {
        long inicio = System.nanoTime();

        Page<DiarioEntradaResponseDTO> entradas = diarioEntradaRepository.findAll(pageable)
                .map(DiarioEntradaResponseDTO::fromEntity);

        long duracao = System.nanoTime() - inicio;
        tempoListagemDiarioEntradas.record(duracao, TimeUnit.NANOSECONDS);

        return entradas;
    }

    @Transactional(readOnly = true)
    public DiarioEntradaResponseDTO buscarPorId(Long id) {
        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("DiarioEntrada não encontrado com ID: " + id));
        return DiarioEntradaResponseDTO.fromEntity(diarioEntrada);
    }

    @Transactional(readOnly = true)
    public Page<DiarioEntradaResponseDTO> buscarPorData(LocalDate data, Pageable pageable) {
        long inicio = System.nanoTime();

        Page<DiarioEntrada> entradas = diarioEntradaRepository.findByData(data, pageable);
        Page<DiarioEntradaResponseDTO> resultado = entradas.map(DiarioEntradaResponseDTO::fromEntity);

        long duracao = System.nanoTime() - inicio;
        tempoBuscaDiarioEntradasPorData.record(duracao, TimeUnit.NANOSECONDS);

        return resultado;
    }

    @Transactional
    public DiarioEntradaResponseDTO atualizar(Long id, DiarioEntradaRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        DiarioEntrada diarioEntradaExistente = diarioEntradaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("DiarioEntrada não encontrado com ID: " + id));

        if (!diarioEntradaExistente.getPet().getUser().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não pode atualizar uma entrada de outro usuário");
        }

        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado com ID: " + requestDTO.petId()));

        if (!pet.getUser().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não pode associar a entrada ao pet de outro usuário");
        }

        diarioEntradaExistente.setPet(pet);
        diarioEntradaExistente.setData(requestDTO.data());
        diarioEntradaExistente.setResumo(requestDTO.resumo());
        diarioEntradaExistente.setHumorGeral(requestDTO.humorGeral());
        diarioEntradaExistente.setStatus(requestDTO.status());

        DiarioEntrada diarioEntradaAtualizado = diarioEntradaRepository.save(diarioEntradaExistente);

        log.info("Entrada do diário ID {} atualizada com sucesso", diarioEntradaAtualizado.getId());

        return DiarioEntradaResponseDTO.fromEntity(diarioEntradaAtualizado);

    }

    @Transactional
    public void deletar(Long id) {
        User usuarioLogado = getUsuarioAutenticado();

        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("DiarioEntrada não encontrado com ID: " + id));

        if (!diarioEntrada.getPet().getUser().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não pode excluir uma entrada de outro usuário");
        }

        if (registroRepository.existsByEntradaId(id)) {
            throw new RegraNegocioException("Não é possível excluir o DiarioEntrada pois existem registros vinculados a ele"
            );
        }

        diarioEntradaRepository.deleteById(id);

        log.info("Entrada do diário ID {} excluída com sucesso", id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }
}

