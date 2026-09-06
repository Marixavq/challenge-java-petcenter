package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.DTO.PetVeterinarioResponseDTO;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.model.PetVeterinario;
import com.fiap.challengepetcenter.repository.PetVeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetVeterinarioService {

    private final PetVeterinarioRepository petVeterinarioRepository;

    @Autowired
    public PetVeterinarioService(PetVeterinarioRepository petVeterinarioRepository) {
        this.petVeterinarioRepository = petVeterinarioRepository;
    }

    @Transactional(readOnly = true)
    public Page<PetVeterinarioResponseDTO> listarTodos(Pageable pageable) {
        return petVeterinarioRepository.findAll(pageable)
                .map(PetVeterinarioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public PetVeterinarioResponseDTO buscarPorId(Long id) {
        PetVeterinario petVeterinario = petVeterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vínculo entre pet e veterináio não encontrado com ID: " + id));
        return PetVeterinarioResponseDTO.fromEntity(petVeterinario);
    }

    @Transactional(readOnly = true)
    public Page<PetVeterinarioResponseDTO> buscarPorVeterinarioId(Long veterinarioId, Pageable pageable) {
        return petVeterinarioRepository.findByVeterinario_Id(veterinarioId, pageable)
                .map(PetVeterinarioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<PetVeterinarioResponseDTO> buscarPorPetId(Long petId, Pageable pageable) {
        return petVeterinarioRepository.findByPet_Id(petId, pageable)
                .map(PetVeterinarioResponseDTO::fromEntity);
    }

    @Transactional
    public void deletar(Long id) {
        if (!petVeterinarioRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Vínculo entre pet e veterinário não encontrado com ID: " + id);
        }

        petVeterinarioRepository.deleteById(id);
    }

}
