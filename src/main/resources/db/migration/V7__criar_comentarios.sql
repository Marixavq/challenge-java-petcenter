CREATE TABLE comentarios
(
    id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entrada_id NUMBER NOT NULL,
    user_id    NUMBER NOT NULL,
    comentario VARCHAR2(1000) NOT NULL,
    criado_em  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    editado_em TIMESTAMP,

    CONSTRAINT fk_comentario_entrada
        FOREIGN KEY (entrada_id) REFERENCES diario_entradas (id),

    CONSTRAINT fk_comentario_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);
