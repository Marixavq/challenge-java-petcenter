CREATE TABLE pet_veterinario
(
    id             NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pet_id         NUMBER NOT NULL,
    veterinario_id NUMBER NOT NULL,
    data_inicio    DATE DEFAULT SYSDATE,
    ativo          NUMBER(1) DEFAULT 1,
    observacoes    VARCHAR2(500),

    CONSTRAINT fk_pv_pet
        FOREIGN KEY (pet_id) REFERENCES pets (id),

    CONSTRAINT fk_pv_vet
        FOREIGN KEY (veterinario_id) REFERENCES veterinarios (id),

    CONSTRAINT chk_pv_ativo
        CHECK (ativo IN (0, 1))
);