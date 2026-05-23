package com.fiap.challengepetcenter.model;

import java.time.LocalDateTime;

public class Comentario {
    private Long id;
    private DiarioEntrada entrada;
    private User user;
    private String comentario;
    private LocalDateTime criadoEm;
    private LocalDateTime editadoEm;


}