package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.UserRequestDTO;
import com.fiap.challengepetcenter.dto.response.UserResponseDTO;
import com.fiap.challengepetcenter.model.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;
    private final Counter usuariosCriados;
    private final Timer tempoCriacaoUsuario;
    private final Timer tempoListagemUsuarios;

    @Autowired
    public UserService(UserRepository userRepository, PetRepository petRepository, PasswordEncoder passwordEncoder, MeterRegistry registry) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.passwordEncoder = passwordEncoder;

        this.usuariosCriados = Counter.builder("petcenter.usuarios.criados")
                .description("Total de usuários criados com sucesso")
                .register(registry);

        this.tempoCriacaoUsuario = Timer.builder("petcenter.usuarios.criacao.tempo")
                .description("Tempo de execução da criação de usuários")
                .register(registry);

        this.tempoListagemUsuarios = Timer.builder("petcenter.pets.listagem.tempo")
                .description("Tempo de execução da listagem paginada de pets")
                .register(registry);
    }

    @Transactional
    public UserResponseDTO salvar(UserRequestDTO requestDTO) {
        long inicio = System.nanoTime();

        if (userRepository.existsByEmail(requestDTO.email())) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User();
        user.setNome(requestDTO.nome());
        user.setEmail(requestDTO.email());
        user.setSenha(passwordEncoder.encode(requestDTO.senha()));
        user.setTelefone(requestDTO.telefone());
        user.setTipoUsuario(requestDTO.tipoUsuario());
        user.setAtivo(true);

        User userSalvo = userRepository.save(user);

        long duracao = System.nanoTime() - inicio;
        tempoCriacaoUsuario.record(duracao, TimeUnit.NANOSECONDS);

        usuariosCriados.increment();

        log.info("Usuário ID {} criado com sucesso", userSalvo.getId());

        return UserResponseDTO.fromEntity(userSalvo);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> listarTodos(Pageable pageable) {
        long inicio = System.nanoTime();

        Page<UserResponseDTO> usuarios = userRepository.findAll(pageable)
                .map(UserResponseDTO::fromEntity);

        long duracao = System.nanoTime() - inicio;
        tempoListagemUsuarios.record(duracao, TimeUnit.NANOSECONDS);

        return usuarios;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO buscarPorId(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        validarProprioUsuario(id, usuarioLogado);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO buscarPorEmail(String email) {

        User usuarioLogado = getUsuarioAutenticado();

        if (!usuarioLogado.getEmail().equals(email)) {
            throw new RuntimeException("Você não pode acessar os dados de outro usuário");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public UserResponseDTO atualizar(Long id, UserRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        validarProprioUsuario(id, usuarioLogado);

        User userExistente = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        if (!userExistente.getEmail().equals(requestDTO.email())
                && userRepository.existsByEmail(requestDTO.email())) {
            throw new RuntimeException("Email já cadastrado");
        }

        userExistente.setNome(requestDTO.nome());
        userExistente.setEmail(requestDTO.email());
        userExistente.setSenha(passwordEncoder.encode(requestDTO.senha()));
        userExistente.setTelefone(requestDTO.telefone());

        User userAtualizado = userRepository.save(userExistente);

        log.info("Usuário ID {} atualizado com sucesso", userAtualizado.getId());

        return UserResponseDTO.fromEntity(userAtualizado);
    }

    @Transactional
    public void deletar(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        validarProprioUsuario(id, usuarioLogado);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        if (petRepository.existsByUserId(id)) {
            throw new RuntimeException("Não é possível excluir o usuário pois existem pets vinculados a ele");
        }

        userRepository.deleteById(id);

        log.info("Usuário ID {} excluído com sucesso", id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private void validarProprioUsuario(Long id, User usuarioLogado) {
        if (!id.equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode acessar os dados de outro usuário");
        }
    }


}
