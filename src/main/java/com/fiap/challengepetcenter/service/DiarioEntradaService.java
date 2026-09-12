package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.DiarioEntradaRequestDTO;
import com.fiap.challengepetcenter.dto.response.DiarioEntradaResponseDTO;
import com.fiap.challengepetcenter.model.DiarioEntrada;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.RegistroRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DiarioEntradaService {

    private final DiarioEntradaRepository diarioEntradaRepository;
    private final PetRepository petRepository;
    private final RegistroRepository registroRepository;
    private final UserRepository userRepository;

    @Autowired
    public DiarioEntradaService(DiarioEntradaRepository diarioEntradaRepository, PetRepository petRepository, RegistroRepository registroRepository, UserRepository userRepository) {
        this.diarioEntradaRepository = diarioEntradaRepository;
        this.petRepository = petRepository;
        this.registroRepository = registroRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DiarioEntradaResponseDTO salvar(DiarioEntradaRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com ID: " + requestDTO.petId()));

        if (!pet.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode criar uma entrada para o pet de outro usuário");
        }

        DiarioEntrada diarioEntrada = new DiarioEntrada();
        diarioEntrada.setPet(pet);
        diarioEntrada.setData(requestDTO.data());
        diarioEntrada.setResumo(requestDTO.resumo());
        diarioEntrada.setHumorGeral(requestDTO.humorGeral());
        diarioEntrada.setStatus(requestDTO.status());

        DiarioEntrada diarioEntradaSalvo = diarioEntradaRepository.save(diarioEntrada);

        return DiarioEntradaResponseDTO.fromEntity(diarioEntradaSalvo);
    }


    @Transactional(readOnly = true)
    public Page<DiarioEntradaResponseDTO> listarTodos(Pageable pageable) {
        Page<DiarioEntrada> entradas = diarioEntradaRepository.findAll(pageable);
        return entradas.map(DiarioEntradaResponseDTO::fromEntity);

    }

    @Transactional(readOnly = true)
    public DiarioEntradaResponseDTO buscarPorId(Long id) {
        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DiarioEntrada não encontrado com ID: " + id));
        return DiarioEntradaResponseDTO.fromEntity(diarioEntrada);
    }

    @Transactional(readOnly = true)
    public Page<DiarioEntradaResponseDTO> buscarPorData(LocalDate data, Pageable pageable) {
        Page<DiarioEntrada> entradas = diarioEntradaRepository.findByData(data, pageable);
        return entradas.map(DiarioEntradaResponseDTO::fromEntity);
    }

    @Transactional
    public DiarioEntradaResponseDTO atualizar(Long id, DiarioEntradaRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        DiarioEntrada diarioEntradaExistente = diarioEntradaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DiarioEntrada não encontrado com ID: " + id));

        if (!diarioEntradaExistente.getPet().getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode atualizar uma entrada de outro usuário");
        }

        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com ID: " + requestDTO.petId()));

        if (!pet.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode associar a entrada ao pet de outro usuário");
        }

        diarioEntradaExistente.setPet(pet);
        diarioEntradaExistente.setData(requestDTO.data());
        diarioEntradaExistente.setResumo(requestDTO.resumo());
        diarioEntradaExistente.setHumorGeral(requestDTO.humorGeral());
        diarioEntradaExistente.setStatus(requestDTO.status());

        DiarioEntrada diarioEntradaAtualizado = diarioEntradaRepository.save(diarioEntradaExistente);

        return DiarioEntradaResponseDTO.fromEntity(diarioEntradaAtualizado);

    }

    @Transactional
    public void deletar(Long id) {
        User usuarioLogado = getUsuarioAutenticado();

        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DiarioEntrada não encontrado com ID: " + id));

        if (!diarioEntrada.getPet().getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode excluir uma entrada de outro usuário");
        }

        if (registroRepository.existsByEntradaId(id)) {
            throw new RuntimeException("Não é possível excluir o DiarioEntrada pois existem registros vinculados a ele"
            );
        }

        diarioEntradaRepository.deleteById(id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}

