package com.fiap.challengepetcenter.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DiarioEntradaRequestDTO(

        @NotNull(message = "O pet é obrigatório")
        Long petId,

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        @Size(max = 1000, message = "O resumo deve ter no máximo 1000 caracteres")
        String resumo,

        @Size(max = 50, message = "O humor geral deve ter no máximo 50 caracteres")
        String humorGeral,

        @NotBlank(message = "O status é obrigatório")
        @Size(max = 20, message = "O status deve ter no máximo 20 caracteres")
        String status

) {
}
