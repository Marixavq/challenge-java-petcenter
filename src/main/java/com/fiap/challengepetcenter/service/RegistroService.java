package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.RegistroRequestDTO;
import com.fiap.challengepetcenter.dto.response.RegistroResponseDTO;
import com.fiap.challengepetcenter.model.DiarioEntrada;
import com.fiap.challengepetcenter.model.Registro;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import com.fiap.challengepetcenter.repository.RegistroRepository;
import com.fiap.challengepetcenter.repository.UserRepository;

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
public class RegistroService {

    private static final Logger log = LoggerFactory.getLogger(RegistroService.class);

    private final RegistroRepository registroRepository;
    private final DiarioEntradaRepository diarioEntradaRepository;
    private final UserRepository userRepository;
    private final Counter registrosCriados;
    private final Timer tempoCriacaoRegistro;
    private final Timer tempoListagemRegistros;

    @Autowired
    public RegistroService(RegistroRepository registroRepository, DiarioEntradaRepository diarioEntradaRepository, UserRepository userRepository, MeterRegistry registry) {
        this.registroRepository = registroRepository;
        this.diarioEntradaRepository = diarioEntradaRepository;
        this.userRepository = userRepository;

        this.registrosCriados = Counter.builder("petcenter.registros.criados")
                .description("Total de registros criados com sucesso")
                .register(registry);

        this.tempoCriacaoRegistro = Timer.builder("petcenter.registros.criacao.tempo")
                .description("Tempo de execução da criação de registros")
                .register(registry);

        this.tempoListagemRegistros = Timer.builder("petcenter.registros.listagem.tempo")
                .description("Tempo de execução da listagem paginada de registros")
                .register(registry);
    }

    @Transactional
    public RegistroResponseDTO salvar(RegistroRequestDTO requestDTO) {
        long inicio = System.nanoTime();

        User usuarioLogado = getUsuarioAutenticado();

        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(requestDTO.entradaId())
                .orElseThrow(() -> new RuntimeException("DiarioEntrada não encontrado com ID: " + requestDTO.entradaId()));

        if (!diarioEntrada.getPet().getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode criar um registro no diário de outro usuário");
        }

        Registro registro = new Registro();
        registro.setEntrada(diarioEntrada);
        registro.setTipo(requestDTO.tipo());
        registro.setSubtipo(requestDTO.subtipo());
        registro.setValor(requestDTO.valor());
        registro.setUnidade(requestDTO.unidade());
        registro.setNota(requestDTO.nota());

        Registro registroSalvo = registroRepository.save(registro);

        long duracao = System.nanoTime() - inicio;
        tempoCriacaoRegistro.record(duracao, TimeUnit.NANOSECONDS);

        registrosCriados.increment();

        log.info("Registro ID {} criado com sucesso", registroSalvo.getId());

        return RegistroResponseDTO.fromEntity(registroSalvo);
    }

    @Transactional(readOnly = true)
    public Page<RegistroResponseDTO> listarTodos(Pageable pageable) {
        long inicio = System.nanoTime();

        Page<RegistroResponseDTO> registros = registroRepository.findAll(pageable)
                .map(RegistroResponseDTO::fromEntity);

        long duracao = System.nanoTime() - inicio;
        tempoListagemRegistros.record(duracao, TimeUnit.NANOSECONDS);

        return registros;
    }

    @Transactional(readOnly = true)
    public RegistroResponseDTO buscarPorId(Long id) {
        Registro registro = registroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado com ID: " + id));
        return RegistroResponseDTO.fromEntity(registro);
    }

    @Transactional
    public RegistroResponseDTO atualizar(Long id, RegistroRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        Registro registroExistente = registroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado com ID: " + id));

        if (!registroExistente.getEntrada().getPet().getUser().getId()
                .equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode atualizar um registro de outro usuário");
        }

        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(requestDTO.entradaId())
                .orElseThrow(() -> new RuntimeException("DiarioEntrada não encontrado com ID: " + requestDTO.entradaId()
                ));

        if (!diarioEntrada.getPet().getUser().getId()
                .equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode associar o registro ao diário de outro usuário");
        }

        registroExistente.setEntrada(diarioEntrada);
        registroExistente.setTipo(requestDTO.tipo());
        registroExistente.setSubtipo(requestDTO.subtipo());
        registroExistente.setValor(requestDTO.valor());
        registroExistente.setUnidade(requestDTO.unidade());
        registroExistente.setNota(requestDTO.nota());

        Registro registroAtualizado = registroRepository.save(registroExistente);

        log.info("Registro ID {} atualizado com sucesso", registroAtualizado.getId());

        return RegistroResponseDTO.fromEntity(registroAtualizado);
    }

    @Transactional
    public void deletar(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        Registro registroExistente = registroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado com ID: " + id));

        if (!registroExistente.getEntrada().getPet().getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode excluir um registro de outro usuário");
        }

        log.info("Registro ID {} excluído com sucesso", id);

        registroRepository.deleteById(id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

}

