package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.response.PetVeterinarioResponseDTO;
import com.fiap.challengepetcenter.exception.AcessoNegadoException;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.model.PetVeterinario;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.PetVeterinarioRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
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
public class PetVeterinarioService {

    private static final Logger log = LoggerFactory.getLogger(PetVeterinarioService.class);

    private final PetVeterinarioRepository petVeterinarioRepository;
    private final UserRepository userRepository;
    private final Timer tempoListagemPetVeterinario;

    @Autowired
    public PetVeterinarioService(PetVeterinarioRepository petVeterinarioRepository, UserRepository userRepository, MeterRegistry registry) {
        this.petVeterinarioRepository = petVeterinarioRepository;
        this.userRepository = userRepository;

        this.tempoListagemPetVeterinario = Timer.builder("petcenter.petVeterinario.listagem.tempo")
                .description("Tempo de execução da listagem paginada de pets")
                .register(registry);
    }

    @Transactional(readOnly = true)
    public Page<PetVeterinarioResponseDTO> listarTodos(Pageable pageable) {
        long inicio = System.nanoTime();

        Page<PetVeterinarioResponseDTO> petsVeterinarios = petVeterinarioRepository.findAll(pageable)
                .map(PetVeterinarioResponseDTO::fromEntity);

        long duracao = System.nanoTime() - inicio;
        tempoListagemPetVeterinario.record(duracao, TimeUnit.NANOSECONDS);

        return petsVeterinarios;
    }

    @Transactional(readOnly = true)
    public PetVeterinarioResponseDTO buscarPorId(Long id) {
        PetVeterinario petVeterinario = petVeterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vínculo entre pet e veterinário não encontrado com ID: " + id));
        return PetVeterinarioResponseDTO.fromEntity(petVeterinario);
    }

    @Transactional(readOnly = true)
    public Page<PetVeterinarioResponseDTO> buscarPorVeterinarioId(Long veterinarioId, Pageable pageable) {
        return petVeterinarioRepository.findByVeterinarioId(veterinarioId, pageable)
                .map(PetVeterinarioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<PetVeterinarioResponseDTO> buscarPorPetId(Long petId, Pageable pageable) {
        return petVeterinarioRepository.findByPetId(petId, pageable)
                .map(PetVeterinarioResponseDTO::fromEntity);
    }

    @Transactional
    public void deletar(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        PetVeterinario petVeterinario = petVeterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vínculo entre pet e veterinário não encontrado com ID: " + id));

        if (!petVeterinario.getVeterinario().getUser().getId()
                .equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não pode excluir o vínculo de outro veterinário");
        }

        petVeterinarioRepository.deleteById(id);

        log.info("Relacionamento entre pet e veterinário com ID {} excluído com sucesso", id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }
}
