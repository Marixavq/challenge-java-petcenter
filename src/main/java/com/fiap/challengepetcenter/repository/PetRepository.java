package com.fiap.challengepetcenter.repository;

import com.fiap.challengepetcenter.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByNome(String nome);

    List<Pet> findByEspecie(String especie);

    List<Pet> findByUserId(Long userId);
}
