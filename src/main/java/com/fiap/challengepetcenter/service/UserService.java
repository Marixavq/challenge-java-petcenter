package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User salvar(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        return userRepository.save(user);
    }

    public List<User> listarTodos() {
        return userRepository.findAll();
    }

    public User buscarPorId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public User atualizar(Long id, User userAtualizado) {
        User userExistente = buscarPorId(id);
        userExistente.setNome(userAtualizado.getNome());
        userExistente.setEmail(userAtualizado.getEmail());
        userExistente.setSenha(userAtualizado.getSenha());
        userExistente.setTelefone(userAtualizado.getTelefone());
        userExistente.setTipoUsuario(userAtualizado.getTipoUsuario());
        userExistente.setAtivo(userAtualizado.getAtivo());
        return userRepository.save(userExistente);
    }

    public void deletar(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        userRepository.deleteById(id);
    }


}
