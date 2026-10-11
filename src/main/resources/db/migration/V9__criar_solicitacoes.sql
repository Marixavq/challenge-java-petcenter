CREATE TABLE solicitacoes
(
    id             NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pet_id         NUMBER NOT NULL,
    tutor_id       NUMBER NOT NULL,
    veterinario_id NUMBER NOT NULL,
    status         VARCHAR2(20) NOT NULL,
    mensagem       VARCHAR2(500),
    criado_em      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    respondido_em  TIMESTAMP,

    CONSTRAINT fk_sol_pet
        FOREIGN KEY (pet_id) REFERENCES pets (id),

    CONSTRAINT fk_sol_tutor
        FOREIGN KEY (tutor_id) REFERENCES users (id),

    CONSTRAINT fk_sol_vet
        FOREIGN KEY (veterinario_id) REFERENCES veterinarios (id),

    CONSTRAINT chk_sol_status
        CHECK (status IN ('PENDENTE', 'ACEITA', 'RECUSADA'))
);
