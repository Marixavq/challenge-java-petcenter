package com.fiap.challengepetcenter.model;

import java.time.LocalDateTime;

public class Registro {
    private Long id;
    private DiarioEntrada entrada;
    private String tipo;
    private String subtipo;
    private Double valor;
    private String unidade;
    private String nota;
    private LocalDateTime horario;
    private LocalDateTime atualizadoEm;
}