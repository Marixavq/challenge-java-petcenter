package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.AlertaRequestDTO;
import com.fiap.challengepetcenter.dto.response.AlertaResponseDTO;
import com.fiap.challengepetcenter.exception.AcessoNegadoException;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.model.Alerta;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.model.Veterinario;
import com.fiap.challengepetcenter.repository.AlertaRepository;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
import com.fiap.challengepetcenter.repository.VeterinarioRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertaService {

    private static final Logger log = LoggerFactory.getLogger(AlertaService.class);

    private final PetRepository petRepository;
    private final AlertaRepository alertaRepository;
    private final UserRepository userRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final Counter alertasCriados;
    private final Timer tempoCriacaoAlerta;
    private final Timer tempoListagemAlertas;
    private final Timer tempoListagemAlertasPorPet;

    @Autowired
    public AlertaService(PetRepository petRepository, AlertaRepository alertaRepository, UserRepository userRepository, VeterinarioRepository veterinarioRepository, MeterRegistry registry) {
        this.petRepository = petRepository;
        this.alertaRepository = alertaRepository;
        this.userRepository = userRepository;
        this.veterinarioRepository = veterinarioRepository;

        this.alertasCriados = Counter.builder("petcenter.alertas.criados")
                .description("Total de alertas criados com sucesso")
                .register(registry);

        this.tempoCriacaoAlerta = Timer.builder("petcenter.alertas.criacao.tempo")
                .description("Tempo de execução da criação de alertas")
                .register(registry);

        this.tempoListagemAlertas = Timer.builder("petcenter.alertas.listagem.tempo")
                .description("Tempo de execução da listagem paginada de alertas")
                .register(registry);

        this.tempoListagemAlertasPorPet = Timer.builder("petcenter.alertas.listagem.pet.tempo")
                .description("Tempo de execução da listagem paginada de alertas por pet")
                .register(registry);
    }

    @Transactional
    public AlertaResponseDTO salvar(AlertaRequestDTO requestDTO) {
        long inicio = System.nanoTime();

        User usuarioLogado = getUsuarioAutenticado();

        Veterinario veterinario = veterinarioRepository.findByUserId(usuarioLogado.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado"));

        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado com ID: " + requestDTO.petId()));

        Alerta alerta = new Alerta();
        alerta.setPet(pet);
        alerta.setVeterinario(veterinario);
        alerta.setTipo(requestDTO.tipo());
        alerta.setTitulo(requestDTO.titulo());
        alerta.setDescricao(requestDTO.descricao());
        alerta.setDataInicio(requestDTO.dataInicio());
        alerta.setFrequenciaHoras(requestDTO.frequenciaHoras());
        alerta.setDataFim(requestDTO.dataFim());
        alerta.setAtivo(true);

        Alerta alertaSalvo = alertaRepository.save(alerta);

        long duracao = System.nanoTime() - inicio;
        tempoCriacaoAlerta.record(duracao, TimeUnit.NANOSECONDS);

        alertasCriados.increment();

        log.info("Alerta ID {} criado com sucesso", alertaSalvo.getId());

        return AlertaResponseDTO.fromEntity(alertaSalvo);
    }

    @Transactional(readOnly = true)
    public Page<AlertaResponseDTO> listarTodos(Pageable pageable) {
        long inicio = System.nanoTime();

        Page<AlertaResponseDTO> alertas = alertaRepository.findAll(pageable)
                .map(AlertaResponseDTO::fromEntity);

        long duracao = System.nanoTime() - inicio;
        tempoListagemAlertas.record(duracao, TimeUnit.NANOSECONDS);

        return alertas;
    }

    @Transactional(readOnly = true)
    public AlertaResponseDTO buscarPorId(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta não encontrado com ID: " + id));
        return AlertaResponseDTO.fromEntity(alerta);
    }

    @Transactional(readOnly = true)
    public Page<AlertaResponseDTO> listarPorPet(Long petId, Pageable pageable) {
        long inicio = System.nanoTime();

        if (!petRepository.existsById(petId)) {
            throw new RecursoNaoEncontradoException("Pet não encontrado com ID: " + petId);
        }

        Page<AlertaResponseDTO> resultado = alertaRepository.findByPetId(petId, pageable)
                .map(AlertaResponseDTO::fromEntity);

        long duracao = System.nanoTime() - inicio;
        tempoListagemAlertasPorPet.record(duracao, TimeUnit.NANOSECONDS);

        return resultado;
    }

    @Transactional
    public AlertaResponseDTO desativar(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta não encontrado com ID: " + id));

        boolean ehTutorDoPet = alerta.getPet().getUser().getId()
                .equals(usuarioLogado.getId());

        boolean ehVeterinarioDoAlerta = alerta.getVeterinario().getUser().getId()
                .equals(usuarioLogado.getId());

        if (!ehTutorDoPet && !ehVeterinarioDoAlerta) {
            throw new AcessoNegadoException("Você não tem permissão para desativar este alerta");
        }

        alerta.setAtivo(false);

        Alerta alertaSalvo = alertaRepository.save(alerta);

        log.info("Alerta ID {} desativado pelo usuário ID {}", alertaSalvo.getId(), usuarioLogado.getId());

        return AlertaResponseDTO.fromEntity(alertaSalvo);

    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }
}
