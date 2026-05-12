package com.fiap.challengepetcenter.DTO;

import com.fiap.challengepetcenter.model.DiarioEntrada;
import com.fiap.challengepetcenter.model.Registro;

import java.time.LocalDate;

public record RegistroResponseDTO(

        Long id,
        Long idDiarioEntrada,
        String tipo,
        String subtipo,
        Double valor,
        String unidade,
        String nota
) {

    public static RegistroResponseDTO fromEntity(Registro registro) {
        return new RegistroResponseDTO(
                registro.getId(),
                registro.getEntrada().getId(),
                registro.getTipo(),
                registro.getSubtipo(),
                registro.getValor(),
                registro.getUnidade(),
                registro.getNota()
        );
    }
}
