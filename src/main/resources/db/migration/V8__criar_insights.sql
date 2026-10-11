CREATE TABLE insights
(
    id           NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pet_id       NUMBER NOT NULL,
    tipo         VARCHAR2(100) NOT NULL,
    descricao    VARCHAR2(1000) NOT NULL,
    origem_regra VARCHAR2(100),
    nivel_alerta VARCHAR2(10),
    data_inicio  DATE,
    data_fim     DATE,
    criado_em    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    gerado_por   VARCHAR2(50),
    status       VARCHAR2(20),
    contexto     CLOB,

    CONSTRAINT fk_insight_pet
        FOREIGN KEY (pet_id) REFERENCES pets (id),

    CONSTRAINT chk_nivel_alerta
        CHECK (nivel_alerta IN ('BAIXO', 'MEDIO', 'ALTO')),

    CONSTRAINT chk_insight_status
        CHECK (status IN ('ATIVO', 'RESOLVIDO', 'IGNORADO'))
);