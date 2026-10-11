CREATE TABLE registros
(
    id            NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entrada_id    NUMBER NOT NULL,
    tipo          VARCHAR2(50) NOT NULL,
    subtipo       VARCHAR2(50),
    valor         NUMBER(10,2),
    unidade       VARCHAR2(20),
    nota          VARCHAR2(500),
    horario       TIMESTAMP,
    atualizado_em TIMESTAMP,

    CONSTRAINT fk_registro_entrada
        FOREIGN KEY (entrada_id) REFERENCES diario_entradas (id)
);