package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.SolicitacaoRequestDTO;
import com.fiap.challengepetcenter.dto.response.PetResponseDTO;
import com.fiap.challengepetcenter.dto.response.SolicitacaoResponseDTO;
import com.fiap.challengepetcenter.exception.AcessoNegadoException;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.exception.RegraNegocioException;
import com.fiap.challengepetcenter.model.*;
import com.fiap.challengepetcenter.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.TimeUnit;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SolicitacaoService {

    private static final Logger log = LoggerFactory.getLogger(SolicitacaoService.class);

    private final SolicitacaoRepository solicitacaoRepository;
    private final UserRepository userRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final PetRepository petRepository;
    private final PetVeterinarioRepository petVeterinarioRepository;
    private final Counter solicitacoesCriadas;
    private final Timer tempoCriacaoSolicitacao;
    private final Timer tempoListagemSolicitacoes;
    private final Counter solicitacoesAceitas;
    private final Counter solicitacoesRecusadas;
    private final Timer tempoAceitarSolicitacao;

    @Autowired
    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository, UserRepository userRepository, VeterinarioRepository veterinarioRepository, PetRepository petRepository, PetVeterinarioRepository petVeterinarioRepository, MeterRegistry registry) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.userRepository = userRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.petRepository = petRepository;
        this.petVeterinarioRepository = petVeterinarioRepository;

        this.solicitacoesCriadas = Counter.builder("petcenter.solicitacoes.criadas")
                .description("Total de solicitações criadas com sucesso")
                .register(registry);

        this.tempoCriacaoSolicitacao = Timer.builder("petcenter.solicitacoes.criacao.tempo")
                .description("Tempo de execução da criação de solicitações")
                .register(registry);

        this.tempoListagemSolicitacoes = Timer.builder("petcenter.solicitacoes.listagem.tempo")
                .description("Tempo de execução da listagem de solicitações")
                .register(registry);

        this.solicitacoesAceitas = Counter.builder("petcenter.solicitacoes.aceitas")
                .description("Total de solicitações aceitas com sucesso")
                .register(registry);

        this.solicitacoesRecusadas = Counter.builder("petcenter.solicitacoes.recusadas")
                .description("Total de solicitações recusadas com sucesso")
                .register(registry);

        this.tempoAceitarSolicitacao = Timer.builder("petcenter.solicitacoes.aceitacao.tempo")
                .description("Tempo de execução da aceitação da solicitações")
                .register(registry);
    }

    @Transactional
    public SolicitacaoResponseDTO salvar(SolicitacaoRequestDTO requestDTO) {
        long inicio = System.nanoTime();

        Veterinario veterinario = veterinarioRepository.findById(requestDTO.veterinarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado com ID: " + requestDTO.veterinarioId()));

        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado com ID: " + requestDTO.petId()));

        User usuarioLogado = getUsuarioAutenticado();

        if (!pet.getUser().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não pode criar uma solicitação para o pet de outro usuário");
        }

        log.info("Criando solicitação para o pet ID {} com veterinário ID {}",
                pet.getId(), veterinario.getId());

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setPet(pet);
        solicitacao.setTutor(usuarioLogado);
        solicitacao.setVeterinario(veterinario);
        solicitacao.setMensagem(requestDTO.mensagem());
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setCriadoEm(LocalDateTime.now());

        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);

        long duracao = System.nanoTime() - inicio;
        tempoCriacaoSolicitacao.record(duracao, TimeUnit.NANOSECONDS);

        solicitacoesCriadas.increment();

        log.info("Solicitação ID {} criada com status {}", solicitacaoSalva.getId(), solicitacaoSalva.getStatus());

        return SolicitacaoResponseDTO.fromEntity(solicitacaoSalva);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponseDTO> listarTodos(Pageable pageable) {
        long inicio = System.nanoTime();

        Page<SolicitacaoResponseDTO> solicitacoes = solicitacaoRepository.findAll(pageable)
                .map(SolicitacaoResponseDTO::fromEntity);

        long duracao = System.nanoTime() - inicio;
        tempoListagemSolicitacoes.record(duracao, TimeUnit.NANOSECONDS);

        return solicitacoes;
    }

    @Transactional(readOnly = true)
    public SolicitacaoResponseDTO buscarPorId(Long id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada com ID: " + id));
        return SolicitacaoResponseDTO.fromEntity(solicitacao);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponseDTO> buscarPorVeterinarioId(Long veterinarioId, Pageable pageable) {
        return solicitacaoRepository.findByVeterinarioId(veterinarioId, pageable)
                .map(SolicitacaoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponseDTO> buscarPorPetId(Long petId, Pageable pageable) {
        return solicitacaoRepository.findByPetId(petId, pageable)
                .map(SolicitacaoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponseDTO> buscarPorTutorId(Long tutorId, Pageable pageable) {
        return solicitacaoRepository.findByTutorId(tutorId, pageable)
                .map(SolicitacaoResponseDTO::fromEntity);
    }

    @Transactional
    public SolicitacaoResponseDTO aceitar(Long id) {
        long inicio = System.nanoTime();

        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada com ID: " + id));

        User usuarioLogado = getUsuarioAutenticado();

        if (!solicitacao.getVeterinario().getUser().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não pode responder a uma solicitação destinada a outro veterinário");
        }

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RegraNegocioException("A solicitação já foi respondida");
        }

        solicitacao.setStatus(StatusSolicitacao.ACEITA);
        solicitacao.setRespondidoEm(LocalDateTime.now());

        PetVeterinario petVeterinario = new PetVeterinario();
        petVeterinario.setPet(solicitacao.getPet());
        petVeterinario.setVeterinario(solicitacao.getVeterinario());
        petVeterinario.setDataInicio(LocalDate.now());
        petVeterinario.setAtivo(true);
        petVeterinario.setObservacoes(solicitacao.getMensagem());

        petVeterinarioRepository.save(petVeterinario);

        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);

        solicitacoesAceitas.increment();

        log.info("Solicitação de ID {} aceita", solicitacao.getId());

        SolicitacaoResponseDTO resultado = SolicitacaoResponseDTO.fromEntity(solicitacaoSalva);

        long duracao = System.nanoTime() - inicio;
        tempoAceitarSolicitacao.record(duracao, TimeUnit.NANOSECONDS);

        return resultado;
    }

    @Transactional
    public SolicitacaoResponseDTO recusar(Long id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada com ID: " + id));

        User usuarioLogado = getUsuarioAutenticado();

        if (!solicitacao.getVeterinario().getUser().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não pode responder a uma solicitação destinada a outro veterinário");
        }

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RegraNegocioException("A solicitação já foi respondida");
        }

        solicitacao.setStatus(StatusSolicitacao.RECUSADA);
        solicitacao.setRespondidoEm(LocalDateTime.now());

        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);

        solicitacoesRecusadas.increment();
        
        log.info("Solicitação de ID {} recusada", solicitacao.getId());

        return SolicitacaoResponseDTO.fromEntity(solicitacaoSalva);
    }

    @Transactional
    public void deletar(Long id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada com ID: " + id));

        User usuarioLogado = getUsuarioAutenticado();

        if (!solicitacao.getTutor().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não pode excluir a solicitação de outro usuário");
        }

        if (solicitacao.getStatus() == StatusSolicitacao.ACEITA) {
            throw new RegraNegocioException("Não é possível excluir uma solicitação que já foi aceita");
        }

        solicitacaoRepository.delete(solicitacao);

        log.info("Solicitação ID {} excluída com sucesso", id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

}