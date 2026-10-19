# language: pt
@api @notificacoes
Funcionalidade: API de notificações (/notificacoes)
  Como usuário da plataforma
  Quero receber e marcar como lidas as notificações sobre os pontos de coleta
  Para ficar informado sobre a situação dos pontos que acompanho

  Cenário: Listar as notificações de um usuário
    Dado que estou autenticado como usuário comum
    Quando envio uma requisição GET para "/notificacoes/usuario/2"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "notificacao"
    E todos os itens da lista devem ter o campo "nomeUsuario" igual a "Usuário Padrão"

  Cenário: Administrador envia notificação e o usuário a marca como lida
    Dado que estou autenticado como administrador
    Quando envio uma requisição POST para "/notificacoes" com o corpo:
      """
      {"idUsuario": 2, "idPonto": 1, "mensagem": "Coleta de plastico agendada para amanha."}
      """
    Então o status code da resposta deve ser 201
    E a resposta deve obedecer ao contrato "notificacao"
    E o campo "nomeUsuario" da resposta deve ser "Usuário Padrão"
    E o campo "enderecoPonto" da resposta deve ser "Av. Paulista, 1000 - Sao Paulo/SP"
    E o campo "lida" da resposta deve ser "N"
    E guardo o campo "idNotificacao" da resposta como "idNotificacao"
    E o cabeçalho "Location" da resposta deve terminar com "/notificacoes/{idNotificacao}"
    Dado que estou autenticado como usuário comum
    Quando envio uma requisição PATCH para "/notificacoes/{idNotificacao}/lida"
    Então o status code da resposta deve ser 200
    E a resposta deve obedecer ao contrato "notificacao"
    E o campo "lida" da resposta deve ser "S"

  Cenário: Notificação para usuário inexistente retorna 404
    Dado que estou autenticado como administrador
    Quando envio uma requisição POST para "/notificacoes" com o corpo:
      """
      {"idUsuario": 999999, "idPonto": 1, "mensagem": "Mensagem perdida"}
      """
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"
    E o campo "error" da resposta deve ser "Usuario nao encontrado: 999999"

  Cenário: Notificação com mensagem acima de 300 caracteres é rejeitada
    Dado que estou autenticado como administrador
    Quando envio uma requisição POST para "/notificacoes" com o corpo:
      """
      {"idUsuario": 2, "idPonto": 1, "mensagem": "Mensagem muito longa que ultrapassa o limite permitido pela API de notificacoes. Mensagem muito longa que ultrapassa o limite permitido pela API de notificacoes. Mensagem muito longa que ultrapassa o limite permitido pela API de notificacoes. Mensagem muito longa que ultrapassa o limite permitido pela API."}
      """
    Então o status code da resposta deve ser 422
    E a resposta deve obedecer ao contrato "erro-validacao"
    E a lista de erros de validação deve conter o campo "mensagem"

  Cenário: Marcar como lida uma notificação inexistente retorna 404
    Dado que estou autenticado como usuário comum
    Quando envio uma requisição PATCH para "/notificacoes/999999/lida"
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"

  Cenário: Usuário comum não pode enviar notificações
    Dado que estou autenticado como usuário comum
    Quando envio uma requisição POST para "/notificacoes" com o corpo:
      """
      {"idUsuario": 2, "idPonto": 1, "mensagem": "Tentativa sem permissao"}
      """
    Então o status code da resposta deve ser 403
