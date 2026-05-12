package com.fiap.challengepetcenter.DTO;

import com.fiap.challengepetcenter.model.DiarioEntrada;

import java.time.LocalDate;

public record DiarioEntradaResponseDTO(
        Long id,
        Long idPet,
        String nomePet,
        LocalDate data
) {

    public static DiarioEntradaResponseDTO fromEntity(DiarioEntrada diarioEntrada) {
        return new DiarioEntradaResponseDTO(
                diarioEntrada.getId(),
                diarioEntrada.getPet().getId(),
                diarioEntrada.getPet().getNome(),
                diarioEntrada.getData()

        );
    }
}