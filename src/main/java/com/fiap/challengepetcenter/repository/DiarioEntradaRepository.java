package com.fiap.challengepetcenter.repository;

import com.fiap.challengepetcenter.model.DiarioEntrada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DiarioEntradaRepository extends JpaRepository<DiarioEntrada, Long> {

    List<DiarioEntrada> findByData(LocalDate data);

    boolean existsByPetId(Long id);
}
