package com.fiap.challengepetcenter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros")
@Schema(
        name = "Registro",
        description = "Representa um registro associado a uma entrada no diário no sistema API PetCenter"
)
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "ID único do registro",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_id", nullable = false)
    private DiarioEntrada entrada;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(length = 50)
    private String subtipo;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(length = 20)
    private String unidade;

    @Column(length = 500)
    private String nota;

    private LocalDateTime horario;

    @Column(name = "atualizado_em")
    @Schema(
            description = "Data e hora da última atualização do registro",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime atualizadoEm;

    public Registro() {
    }

    public Registro(DiarioEntrada entrada, Long id, String tipo, String subtipo, BigDecimal valor, String unidade, String nota) {
        this.entrada = entrada;
        this.id = id;
        this.tipo = tipo;
        this.subtipo = subtipo;
        this.valor = valor;
        this.unidade = unidade;
        this.nota = nota;
        this.horario = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
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

    @PrePersist
    protected void onCreate() {
        this.horario = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}