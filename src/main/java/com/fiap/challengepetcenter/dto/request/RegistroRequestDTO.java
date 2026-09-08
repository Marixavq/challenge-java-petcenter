package com.fiap.challengepetcenter.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegistroRequestDTO(

        @Schema(
                description = "ID do diário de entrada associado ao registro",
                example = "1"
        )
        @NotNull(message = "A entrada é obrigatória")
        Long entradaId,

        @Schema(
                description = "Tipo do registro",
                example = "Alimentação"
        )
        @NotBlank(message = "O tipo é obrigatório")
        @Size(max = 50, message = "O tipo deve ter no máximo 50 caracteres")
        String tipo,

        @Schema(
                description = "Subtipo do registro",
                example = "Ração seca"
        )
        @Size(max = 50, message = "O subtipo deve ter no máximo 50 caracteres")
        String subtipo,

        @Schema(
                description = "Valor numérico relacionado ao registro",
                example = "250.00"
        )
        BigDecimal valor,

        @Schema(
                description = "Unidade de medida do valor",
                example = "gramas"
        )
        @Size(max = 20, message = "A unidade deve ter no máximo 20 caracteres")
        String unidade,

        @Schema(
                description = "Observações adicionais do registro",
                example = "Pet comeu normalmente"
        )
        @Size(max = 500, message = "A nota deve ter no máximo 500 caracteres")
        String nota
) {
}