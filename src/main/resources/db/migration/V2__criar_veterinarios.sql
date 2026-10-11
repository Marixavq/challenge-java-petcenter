-- 2. VETERINARIOS

CREATE TABLE veterinarios
(
    id            NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id       NUMBER NOT NULL,
    crmv          VARCHAR2(50) NOT NULL,
    especialidade VARCHAR2(100),
    descricao     VARCHAR2(500),

    CONSTRAINT fk_vet_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);