package com.fiap.challengepetcenter.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DiarioEntrada {
    private Long id;
    private Pet pet;
    private LocalDate data;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private String resumo;
    private String humorGeral;
    private String status;
}