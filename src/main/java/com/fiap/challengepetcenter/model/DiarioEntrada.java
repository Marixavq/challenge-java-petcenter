package com.fiap.challengepetcenter.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diario_entrada")
public class DiarioEntrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
    private LocalDate data;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private String resumo;
    private String humorGeral;
    private String status;
}