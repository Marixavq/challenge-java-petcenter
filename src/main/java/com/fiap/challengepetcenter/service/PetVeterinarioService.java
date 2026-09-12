package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.response.PetVeterinarioResponseDTO;
import com.fiap.challengepetcenter.model.PetVeterinario;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.PetVeterinarioRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
import com.fiap.challengepetcenter.repository.VeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetVeterinarioService {

    private final PetVeterinarioRepository petVeterinarioRepository;
    private final UserRepository userRepository;


    @Autowired
    public PetVeterinarioService(
            PetVeterinarioRepository petVeterinarioRepository,
            UserRepository userRepository) {

        this.petVeterinarioRepository = petVeterinarioRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<PetVeterinarioResponseDTO> listarTodos(Pageable pageable) {
        return petVeterinarioRepository.findAll(pageable)
                .map(PetVeterinarioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public PetVeterinarioResponseDTO buscarPorId(Long id) {
        PetVeterinario petVeterinario = petVeterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vínculo entre pet e veterinário não encontrado com ID: " + id));
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
                .orElseThrow(() -> new RuntimeException("Vínculo entre pet e veterinário não encontrado com ID: " + id));

        if (!petVeterinario.getVeterinario().getUser().getId()
                .equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode excluir o vínculo de outro veterinário");
        }

        petVeterinarioRepository.deleteById(id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

}
