# language: pt
@api @tipos-residuo
Funcionalidade: API de tipos de resíduo (/tipos-residuo)
  Como administrador da plataforma
  Quero manter o cadastro de tipos de resíduo
  Para classificar corretamente o que é reciclável e o que é perigoso

  Contexto:
    Dado que estou autenticado como administrador

  Cenário: Listar todos os tipos de resíduo
    Quando envio uma requisição GET para "/tipos-residuo"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "tipo-residuo"
    E a lista da resposta não deve estar vazia

  Cenário: Buscar tipo de resíduo por id
    Quando envio uma requisição GET para "/tipos-residuo/1"
    Então o status code da resposta deve ser 200
    E a resposta deve obedecer ao contrato "tipo-residuo"
    E o campo "idTipo" da resposta deve ser "1"
    E o campo "descricao" da resposta deve ser "Plastico"
    E o campo "reciclavel" da resposta deve ser "S"
    E o campo "perigoso" da resposta deve ser "N"

  Cenário: Buscar tipo de resíduo inexistente retorna 404
    Quando envio uma requisição GET para "/tipos-residuo/999999"
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"
    E o campo "error" da resposta deve ser "Tipo de residuo nao encontrado: 999999"

  Cenário: Cadastrar, atualizar e excluir um tipo de resíduo
    Quando envio uma requisição POST para "/tipos-residuo" com o corpo:
      """
      {"descricao": "Oleo de cozinha", "reciclavel": "S", "perigoso": "N"}
      """
    Então o status code da resposta deve ser 201
    E a resposta deve obedecer ao contrato "tipo-residuo"
    E o campo "descricao" da resposta deve ser "Oleo de cozinha"
    E guardo o campo "idTipo" da resposta como "idTipo"
    E o cabeçalho "Location" da resposta deve terminar com "/tipos-residuo/{idTipo}"
    Quando envio uma requisição PUT para "/tipos-residuo/{idTipo}" com o corpo:
      """
      {"descricao": "Oleo vegetal usado", "reciclavel": "S", "perigoso": "S"}
      """
    Então o status code da resposta deve ser 200
    E a resposta deve obedecer ao contrato "tipo-residuo"
    E o campo "descricao" da resposta deve ser "Oleo vegetal usado"
    E o campo "perigoso" da resposta deve ser "S"
    Quando envio uma requisição DELETE para "/tipos-residuo/{idTipo}"
    Então o status code da resposta deve ser 204
    E a resposta não deve ter corpo
    Quando envio uma requisição GET para "/tipos-residuo/{idTipo}"
    Então o status code da resposta deve ser 404

  Cenário: Cadastro com valores fora do domínio S/N é rejeitado
    Quando envio uma requisição POST para "/tipos-residuo" com o corpo:
      """
      {"descricao": "Bateria", "reciclavel": "X", "perigoso": "talvez"}
      """
    Então o status code da resposta deve ser 422
    E a resposta deve obedecer ao contrato "erro-validacao"
    E a lista de erros de validação deve conter o campo "reciclavel"
    E a lista de erros de validação deve conter o campo "perigoso"

  Cenário: Atualizar tipo de resíduo inexistente retorna 404
    Quando envio uma requisição PUT para "/tipos-residuo/999999" com o corpo:
      """
      {"descricao": "Inexistente", "reciclavel": "S", "perigoso": "N"}
      """
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"

  Cenário: Excluir tipo de resíduo em uso por pontos de coleta é bloqueado
    Quando envio uma requisição DELETE para "/tipos-residuo/1"
    Então o status code da resposta deve ser 400
    E a resposta deve obedecer ao contrato "erro"

  Cenário: Excluir tipo de resíduo inexistente retorna 404
    Quando envio uma requisição DELETE para "/tipos-residuo/999999"
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"

  Cenário: Usuário comum não pode cadastrar tipo de resíduo
    Dado que estou autenticado como usuário comum
    Quando envio uma requisição POST para "/tipos-residuo" com o corpo:
      """
      {"descricao": "Tentativa", "reciclavel": "S", "perigoso": "N"}
      """
    Então o status code da resposta deve ser 403
