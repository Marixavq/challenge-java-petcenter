CREATE TABLE pets
(
    id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         NUMBER NOT NULL,
    nome            VARCHAR2(100) NOT NULL,
    especie         VARCHAR2(50) NOT NULL,
    raca            VARCHAR2(100),
    data_nascimento DATE,
    observacoes     VARCHAR2(500),

    CONSTRAINT fk_pet_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);
