package com.fiap.challengepetcenter.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record RegistroRequestDTO(

        @NotNull(message = "A entrada é obrigatória")
        Long entradaId,

        @NotBlank(message = "O tipo é obrigatório")
        String tipo,

        String subtipo,
        Double valor,
        String unidade,
        String nota

) {
}
