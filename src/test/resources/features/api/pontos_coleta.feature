# language: pt
@api @pontos-coleta
Funcionalidade: API de pontos de coleta (/pontos-coleta)
  Como administrador da plataforma
  Quero manter os pontos de coleta e seus volumes
  Para acompanhar a ocupação de cada ponto da cidade

  Contexto:
    Dado que estou autenticado como administrador

  Cenário: Listar todos os pontos de coleta
    Quando envio uma requisição GET para "/pontos-coleta"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "ponto-coleta"
    E a lista da resposta não deve estar vazia

  Cenário: Buscar ponto de coleta por id
    Quando envio uma requisição GET para "/pontos-coleta/1"
    Então o status code da resposta deve ser 200
    E a resposta deve obedecer ao contrato "ponto-coleta"
    E o campo "endereco" da resposta deve ser "Av. Paulista, 1000 - Sao Paulo/SP"
    E o campo "tipo.descricao" da resposta deve ser "Plastico"
    E o campo "capacidadeMaxKg" da resposta deve ser o número 500

  Cenário: Buscar ponto de coleta inexistente retorna 404
    Quando envio uma requisição GET para "/pontos-coleta/999999"
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"
    E o campo "error" da resposta deve ser "Ponto de coleta nao encontrado: 999999"

  Cenário: Listar pontos de coleta por tipo de resíduo
    Quando envio uma requisição GET para "/pontos-coleta/tipo/2"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "ponto-coleta"
    E todos os itens da lista devem ter o campo "tipo.idTipo" igual a "2"

  Cenário: Tipo de resíduo sem pontos de coleta retorna lista vazia
    Quando envio uma requisição GET para "/pontos-coleta/tipo/8"
    Então o status code da resposta deve ser 200
    E a lista da resposta deve conter 0 itens

  Cenário: Cadastrar, atualizar, alterar o volume e excluir um ponto de coleta
    Quando envio uma requisição POST para "/pontos-coleta" com o corpo:
      """
      {"endereco": "Rua Augusta, 1500 - Sao Paulo/SP", "idTipo": 4, "capacidadeMaxKg": 350.00, "volumeAtualKg": 10.00}
      """
    Então o status code da resposta deve ser 201
    E a resposta deve obedecer ao contrato "ponto-coleta"
    E o campo "tipo.idTipo" da resposta deve ser "4"
    E guardo o campo "idPonto" da resposta como "idPonto"
    E o cabeçalho "Location" da resposta deve terminar com "/pontos-coleta/{idPonto}"
    Quando envio uma requisição PUT para "/pontos-coleta/{idPonto}" com o corpo:
      """
      {"endereco": "Rua Augusta, 1600 - Sao Paulo/SP", "idTipo": 3, "capacidadeMaxKg": 400.00, "volumeAtualKg": 20.00}
      """
    Então o status code da resposta deve ser 200
    E a resposta deve obedecer ao contrato "ponto-coleta"
    E o campo "endereco" da resposta deve ser "Rua Augusta, 1600 - Sao Paulo/SP"
    E o campo "tipo.descricao" da resposta deve ser "Vidro"
    Quando envio uma requisição PATCH para "/pontos-coleta/{idPonto}/volume" com o corpo:
      """
      {"volumeAtualKg": 150.50}
      """
    Então o status code da resposta deve ser 200
    E a resposta deve obedecer ao contrato "ponto-coleta"
    E o campo "volumeAtualKg" da resposta deve ser o número 150,50
    Quando envio uma requisição DELETE para "/pontos-coleta/{idPonto}"
    Então o status code da resposta deve ser 204
    E a resposta não deve ter corpo
    Quando envio uma requisição GET para "/pontos-coleta/{idPonto}"
    Então o status code da resposta deve ser 404

  Cenário: Cadastro de ponto de coleta com tipo de resíduo inexistente retorna 404
    Quando envio uma requisição POST para "/pontos-coleta" com o corpo:
      """
      {"endereco": "Rua Sem Tipo, 1", "idTipo": 999999, "capacidadeMaxKg": 100, "volumeAtualKg": 0}
      """
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"
    E o campo "error" da resposta deve ser "Tipo de residuo nao encontrado: 999999"

  Cenário: Cadastro de ponto de coleta com dados inválidos é rejeitado
    Quando envio uma requisição POST para "/pontos-coleta" com o corpo:
      """
      {"endereco": "", "capacidadeMaxKg": -10, "volumeAtualKg": -1}
      """
    Então o status code da resposta deve ser 422
    E a resposta deve obedecer ao contrato "erro-validacao"
    E a lista de erros de validação deve conter o campo "endereco"
    E a lista de erros de validação deve conter o campo "idTipo"
    E a lista de erros de validação deve conter o campo "capacidadeMaxKg"
    E a lista de erros de validação deve conter o campo "volumeAtualKg"

  Cenário: Atualizar ponto de coleta inexistente retorna 404
    Quando envio uma requisição PUT para "/pontos-coleta/999999" com o corpo:
      """
      {"endereco": "Rua Inexistente, 1", "idTipo": 1, "capacidadeMaxKg": 100, "volumeAtualKg": 0}
      """
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"

  Cenário: Atualizar volume com valor negativo é rejeitado
    Quando envio uma requisição PATCH para "/pontos-coleta/1/volume" com o corpo:
      """
      {"volumeAtualKg": -5}
      """
    Então o status code da resposta deve ser 422
    E a resposta deve obedecer ao contrato "erro-validacao"
    E a lista de erros de validação deve conter o campo "volumeAtualKg"

  Cenário: Excluir ponto de coleta inexistente retorna 404
    Quando envio uma requisição DELETE para "/pontos-coleta/999999"
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"

  Cenário: Acesso sem token é negado
    Dado que não estou autenticado
    Quando envio uma requisição GET para "/pontos-coleta"
    Então o status code da resposta deve ser 403
