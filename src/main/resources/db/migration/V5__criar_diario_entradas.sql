CREATE TABLE diario_entradas
(
    id            NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pet_id        NUMBER NOT NULL,
    data          DATE   NOT NULL,
    criado_em     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    resumo        VARCHAR2(1000),
    humor_geral   VARCHAR2(50),
    status        VARCHAR2(20),

    CONSTRAINT fk_diario_pet
        FOREIGN KEY (pet_id) REFERENCES pets (id),

    CONSTRAINT uq_pet_data
        UNIQUE (pet_id, data),

    CONSTRAINT chk_diario_status
        CHECK (status IN ('COMPLETO', 'PARCIAL', 'VAZIO'))
);