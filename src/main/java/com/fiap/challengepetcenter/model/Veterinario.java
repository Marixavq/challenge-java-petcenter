package com.fiap.challengepetcenter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "veterinarios")
@Schema(
        name = "Veterinario",
        description = "Representa um veterinário no sistema API PetCenter"
)
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "ID único do veterinário",
            example = "1", accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true, nullable = false, length = 20)
    private String crmv;

    @Column(nullable = false, length = 100)
    private String especialidade;

    @Column(nullable = false, length = 500)
    private String descricao;

    public Veterinario() {
    }

    public Veterinario(Long id, User user, String crmv, String especialidade, String descricao) {
        this.id = id;
        this.user = user;
        this.crmv = crmv;
        this.especialidade = especialidade;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}