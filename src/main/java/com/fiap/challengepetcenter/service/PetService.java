package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.DTO.PetRequestDTO;
import com.fiap.challengepetcenter.DTO.PetResponseDTO;
import com.fiap.challengepetcenter.DTO.UserRequestDTO;
import com.fiap.challengepetcenter.DTO.UserResponseDTO;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.PetRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    /*
        @Transactional
        public PetResponseDTO salvar(PetRequestDTO requestDTO) {

            Pet pet = new Pet();
            pet.setUser(requestDTO.userId()); // Long userId,
            pet.setNome(requestDTO.nome());
            pet.setEspecie(requestDTO.especie());
            pet.setRaca(requestDTO.raca());
            pet.setDataNascimento(requestDTO.dataNascimento());
            pet.setObservacoes(requestDTO.observacoes());

            Pet petSalvo = petRepository.save(pet);

            return PetResponseDTO.fromEntity(petSalvo);
        }

    */
    public List<Pet> listarTodos() {
        return petRepository.findAll();
    }

    public Pet buscarPorId(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado"));
    }

    public List<Pet> buscarPorUserId(Long userId) {
        return petRepository.findByUserId(userId);
    }

    public List<Pet> buscarPorNome(String nome) {
        return petRepository.findByNome(nome);
    }

    public Pet atualizar(Long id, Pet petAtualizado) {
        Pet petExistente = buscarPorId(id);
        petExistente.setUser(petAtualizado.getUser());
        petExistente.setNome(petAtualizado.getNome());
        petExistente.setEspecie(petAtualizado.getEspecie());
        petExistente.setRaca(petAtualizado.getRaca());
        petExistente.setDataNascimento(petAtualizado.getDataNascimento());
        petExistente.setObservacoes(petAtualizado.getObservacoes());

        return petRepository.save(petExistente);
    }

    public void deletar(Long id) {
        if (!petRepository.existsById(id)) {
            throw new RuntimeException("Pet não encontrado");
        }
        petRepository.deleteById(id);
    }
}
