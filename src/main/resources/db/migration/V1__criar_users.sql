CREATE TABLE users
(
    id           NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome         VARCHAR2(100) NOT NULL,
    email        VARCHAR2(150) UNIQUE NOT NULL,
    senha        VARCHAR2(255) NOT NULL,
    telefone     VARCHAR2(20),
    tipo_usuario VARCHAR2(20) NOT NULL,
    ativo        NUMBER(1) DEFAULT 1,
    ultimo_login TIMESTAMP,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_tipo_usuario
        CHECK (tipo_usuario IN ('TUTOR', 'VETERINARIO')),

    CONSTRAINT chk_users_ativo
        CHECK (ativo IN (0, 1))
);