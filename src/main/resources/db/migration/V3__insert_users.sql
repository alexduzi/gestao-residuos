-- Senha: admin123
INSERT INTO USUARIO (ID_USUARIO, NOME, EMAIL, SENHA_HASH, ROLE)
VALUES (SEQ_USUARIO.NEXTVAL,
        'Administrador',
        'admin@esg.com',
        '$2b$10$AB0zqUIJC9OjzIQHVpqzPefSiV/YstkK8UOq8dh2ppUIVm2VDXjKu',
        'ADMIN');

-- Senha: user123
INSERT INTO USUARIO (ID_USUARIO, NOME, EMAIL, SENHA_HASH, ROLE)
VALUES (SEQ_USUARIO.NEXTVAL,
        'Usuário Padrão',
        'user@esg.com',
        '$2b$10$TS7dEerCL0DgA4w5IqaveeVv4rgE4J5f44UIY3.cvk/blGGQhQS9q',
        'USER');