package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.VeterinarioRequestDTO;
import com.fiap.challengepetcenter.dto.response.VeterinarioResponseDTO;
import com.fiap.challengepetcenter.exception.AcessoNegadoException;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.exception.RegraNegocioException;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.model.Veterinario;
import com.fiap.challengepetcenter.repository.PetVeterinarioRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
import com.fiap.challengepetcenter.repository.VeterinarioRepository;

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
public class VeterinarioService {

    private static final Logger log = LoggerFactory.getLogger(VeterinarioService.class);

    private final VeterinarioRepository veterinarioRepository;
    private final UserRepository userRepository;
    private final PetVeterinarioRepository petVeterinarioRepository;
    private final Counter veterinariosCriados;
    private final Timer tempoCriacaoVeterinario;
    private final Timer tempoListagemVeterinarios;
    private final Timer tempoBuscaVeterinarioPorUsuario;

    @Autowired
    public VeterinarioService(VeterinarioRepository veterinarioRepository, UserRepository userRepository, PetVeterinarioRepository petVeterinarioRepository, MeterRegistry registry) {
        this.veterinarioRepository = veterinarioRepository;
        this.userRepository = userRepository;
        this.petVeterinarioRepository = petVeterinarioRepository;

        this.veterinariosCriados = Counter.builder("petcenter.veterinarios.criados")
                .description("Total de veterinários criados com sucesso")
                .register(registry);

        this.tempoCriacaoVeterinario = Timer.builder("petcenter.veterinarios.criacao.tempo")
                .description("Tempo de execução da criação de veterinários")
                .register(registry);

        this.tempoListagemVeterinarios = Timer.builder("petcenter.veterinarios.listagem.tempo")
                .description("Tempo de execução da listagem paginada de veterinários")
                .register(registry);

        this.tempoBuscaVeterinarioPorUsuario = Timer.builder("petcenter.veterinarios.busca.usuario.tempo")
                .description("Tempo de execução da busca paginada de veterinários por usuário")
                .register(registry);
    }

    @Transactional
    public VeterinarioResponseDTO salvar(VeterinarioRequestDTO requestDTO) {
        long inicio = System.nanoTime();

        User usuarioLogado = getUsuarioAutenticado();

        Veterinario veterinario = new Veterinario();
        veterinario.setUser(usuarioLogado);
        veterinario.setCrmv(requestDTO.crmv());
        veterinario.setEspecialidade(requestDTO.especialidade());
        veterinario.setDescricao(requestDTO.descricao());

        Veterinario veterinarioSalvo = veterinarioRepository.save(veterinario);

        long duracao = System.nanoTime() - inicio;
        tempoCriacaoVeterinario.record(duracao, TimeUnit.NANOSECONDS);

        veterinariosCriados.increment();

        log.info("Veterinário ID {} criado com sucesso", veterinarioSalvo.getId());

        return VeterinarioResponseDTO.fromEntity(veterinarioSalvo);
    }

    @Transactional(readOnly = true)
    public Page<VeterinarioResponseDTO> listarTodos(Pageable pageable) {
        long inicio = System.nanoTime();

        Page<VeterinarioResponseDTO> veterinarios = veterinarioRepository.findAll(pageable)
                .map(VeterinarioResponseDTO::fromEntity);

        long duracao = System.nanoTime() - inicio;
        tempoListagemVeterinarios.record(duracao, TimeUnit.NANOSECONDS);

        return veterinarios;
    }

    @Transactional(readOnly = true)
    public VeterinarioResponseDTO buscarPorId(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinario não encontrado com ID: " + id));
        return VeterinarioResponseDTO.fromEntity(veterinario);
    }

    @Transactional(readOnly = true)
    public Page<VeterinarioResponseDTO> buscarPorUserId(Long userId, Pageable pageable) {
        long inicio = System.nanoTime();

        Page<VeterinarioResponseDTO> resultado = veterinarioRepository.findByUserId(userId, pageable)
                .map(VeterinarioResponseDTO::fromEntity);

        long duracao = System.nanoTime() - inicio;
        tempoBuscaVeterinarioPorUsuario.record(duracao, TimeUnit.NANOSECONDS);

        return resultado;
    }

    @Transactional
    public VeterinarioResponseDTO atualizar(Long id, VeterinarioRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        Veterinario veterinarioExistente = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado com ID: " + id));

        if (!veterinarioExistente.getUser().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não pode atualizar o perfil de outro veterinário");
        }

        veterinarioExistente.setCrmv(requestDTO.crmv());
        veterinarioExistente.setEspecialidade(requestDTO.especialidade());
        veterinarioExistente.setDescricao(requestDTO.descricao());

        Veterinario veterinarioAtualizado = veterinarioRepository.save(veterinarioExistente);

        log.info("Veterinário ID {} atualizado com sucesso", veterinarioAtualizado.getId());

        return VeterinarioResponseDTO.fromEntity(veterinarioAtualizado);
    }

    @Transactional
    public void deletar(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        Veterinario veterinarioExistente = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado com ID: " + id));

        if (!veterinarioExistente.getUser().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não pode excluir o perfil de outro veterinário");
        }

        if (petVeterinarioRepository.existsByVeterinario_IdAndAtivo(id, true)) {
            throw new RegraNegocioException("Não é possível excluir o veterinário pois existem pets vinculados a ele");
        }

        veterinarioRepository.deleteById(id);

        log.info("Veterinário ID {} excluído com sucesso", id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }
}
