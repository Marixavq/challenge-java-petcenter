package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.PetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    public Pet salvar(Pet pet) {
        return petRepository.save(pet);
    }

    public List<Pet> listarTodos() {
        return petRepository.findAll();
    }

    public Pet buscarPorId(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado"));
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
