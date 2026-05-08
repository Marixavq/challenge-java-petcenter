package com.fiap.challengepetcenter.model;

import java.time.LocalDate;

public class PetVeterinario {

    private Long id;
    private Pet pet;
    private Veterinario veterinario;
    private LocalDate dataInicio;
    private Boolean ativo;
    private String observacoes;
}