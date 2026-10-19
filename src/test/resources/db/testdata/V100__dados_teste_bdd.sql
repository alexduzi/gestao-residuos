-- Dados de teste usados apenas pelo profile "bdd" (nunca rodam em dev/staging/prod).
-- Complementam o seed de producao (V2/V3), que nao tem notificacoes nem um tipo de residuo
-- sem pontos de coleta vinculados.
-- Usuarios ja existentes (V3): 1 = admin@esg.com (ADMIN), 2 = user@esg.com (USER).

-- Tipo de residuo 8: sem nenhum ponto de coleta nem consolidado (consultas por tipo
-- devem retornar lista vazia).
INSERT INTO TIPO_RESIDUO (ID_TIPO, DESCRICAO, RECICLAVEL, PERIGOSO) VALUES (SEQ_TIPO_RESIDUO.NEXTVAL, 'Isopor', 'S', 'N');

-- Notificacoes do usuario comum (id 2): uma nao lida e uma ja lida.
INSERT INTO NOTIFICACAO (ID_NOTIFICACAO, ID_USUARIO, ID_PONTO, MENSAGEM, DATA_ENVIO, LIDA) VALUES (SEQ_NOTIFICACAO.NEXTVAL, 2, 3, 'Ponto de coleta de vidro proximo da capacidade maxima.', TO_DATE('2025-09-24', 'YYYY-MM-DD'), 'N');
INSERT INTO NOTIFICACAO (ID_NOTIFICACAO, ID_USUARIO, ID_PONTO, MENSAGEM, DATA_ENVIO, LIDA) VALUES (SEQ_NOTIFICACAO.NEXTVAL, 2, 1, 'Coleta de plastico realizada com sucesso.', TO_DATE('2025-09-15', 'YYYY-MM-DD'), 'S');

-- Notificacao do administrador (id 1).
INSERT INTO NOTIFICACAO (ID_NOTIFICACAO, ID_USUARIO, ID_PONTO, MENSAGEM, DATA_ENVIO, LIDA) VALUES (SEQ_NOTIFICACAO.NEXTVAL, 1, 5, 'Ponto de coleta de eletronicos atingiu 90% da capacidade.', TO_DATE('2025-09-30', 'YYYY-MM-DD'), 'N');
