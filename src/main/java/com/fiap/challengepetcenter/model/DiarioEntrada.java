package com.fiap.challengepetcenter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diario_entrada")
public class DiarioEntrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    @NotNull(message = "O pet é obrigatório")
    private Pet pet;

    @NotNull(message = "A data é obrigatória")
    private LocalDate data;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    @Size(max = 1000, message = "O resumo deve ter no máximo 1000 caracteres")
    private String resumo;

    @Size(max = 50, message = "O humor geral deve ter no máximo 50 caracteres")
    private String humorGeral;

    @NotBlank(message = "O status é obrigatório")
    @Size(max = 20, message = "O status deve ter no máximo 20 caracteres")
    private String status;

    public DiarioEntrada() {
    }

    public DiarioEntrada(Long id, Pet pet, LocalDate data, String resumo, String humorGeral, String status) {
        this.id = id;
        this.pet = pet;
        this.data = data;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        this.resumo = resumo;
        this.humorGeral = humorGeral;
        this.status = status;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getHumorGeral() {
        return humorGeral;
    }

    public void setHumorGeral(String humorGeral) {
        this.humorGeral = humorGeral;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
