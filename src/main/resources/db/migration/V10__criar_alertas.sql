CREATE TABLE alertas
(
    id               NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    pet_id           NUMBER    NOT NULL,
    veterinario_id   NUMBER    NOT NULL,

    tipo             VARCHAR2(20) NOT NULL,
    titulo           VARCHAR2(150) NOT NULL,
    descricao        VARCHAR2(1000),

    data_inicio      TIMESTAMP NOT NULL,
    frequencia_horas NUMBER,
    data_fim         TIMESTAMP,

    ativo            NUMBER(1) DEFAULT 1 NOT NULL,

    CONSTRAINT fk_alerta_pet
        FOREIGN KEY (pet_id)
            REFERENCES pets (id),

    CONSTRAINT fk_alerta_veterinario
        FOREIGN KEY (veterinario_id)
            REFERENCES veterinarios (id),

    CONSTRAINT chk_alerta_tipo
        CHECK (
            tipo IN (
                     'CONSULTA',
                     'REMEDIO',
                     'ALIMENTACAO',
                     'EXERCICIO',
                     'OUTROS'
                )
            ),

    CONSTRAINT chk_alerta_frequencia
        CHECK (
            frequencia_horas IS NULL
                OR frequencia_horas > 0
            ),

    CONSTRAINT chk_alerta_ativo
        CHECK (ativo IN (0, 1)),

    CONSTRAINT chk_alerta_datas
        CHECK (
            data_fim IS NULL
                OR data_fim >= data_inicio
            )
);
