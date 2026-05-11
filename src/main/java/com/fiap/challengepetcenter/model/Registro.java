package com.fiap.challengepetcenter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "resgistros")
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "O user é obrigatório")
    private DiarioEntrada entrada;

    @NotBlank(message = "O tipo é obrigatório")
    private String tipo;

    private String subtipo;
    private Double valor;
    private String unidade;
    private String nota;

    private LocalDateTime horario;

    private LocalDateTime atualizadoEm;

    public Registro() {
    }

    public Registro(DiarioEntrada entrada, Long id, String tipo, String subtipo, Double valor, String unidade, String nota) {
        this.entrada = entrada;
        this.id = id;
        this.tipo = tipo;
        this.subtipo = subtipo;
        this.valor = valor;
        this.unidade = unidade;
        this.nota = nota;
        this.horario = LocalDateTime.now();
        ;
        this.atualizadoEm = LocalDateTime.now();
        ;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiarioEntrada getEntrada() {
        return entrada;
    }

    public void setEntrada(DiarioEntrada entrada) {
        this.entrada = entrada;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(String subtipo) {
        this.subtipo = subtipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}