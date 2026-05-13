package com.fiap.challengepetcenter.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PetRequestDTO(

        @NotNull(message = "O tutor é obrigatório")
        Long userId,

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100)
        String nome,

        @NotBlank(message = "Espécie é obrigatória")
        String especie,

        String raca,

        LocalDate dataNascimento,

        String observacoes

) {
}
