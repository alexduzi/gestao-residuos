# language: pt
@api @coletas
Funcionalidade: API de coletas realizadas (/coletas)
  Como operador da empresa de reciclagem
  Quero consultar e registrar as coletas realizadas
  Para manter o histórico rastreável do que foi recolhido em cada ponto

  Contexto:
    Dado que estou autenticado como administrador

  Cenário: Listar todas as coletas realizadas
    Quando envio uma requisição GET para "/coletas"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "coleta-realizada"
    E a lista da resposta não deve estar vazia

  Cenário: Buscar coleta realizada por id
    Quando envio uma requisição GET para "/coletas/1"
    Então o status code da resposta deve ser 200
    E a resposta deve obedecer ao contrato "coleta-realizada"
    E o campo "idPonto" da resposta deve ser "1"
    E o campo "dataColeta" da resposta deve ser "2025-09-15"
    E o campo "pesoRecolhidoKg" da resposta deve ser o número 95
    E o campo "empresaResponsavel" da resposta deve ser "EcoRecicla LTDA"

  Cenário: Buscar coleta inexistente retorna 404
    Quando envio uma requisição GET para "/coletas/999999"
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"
    E o campo "error" da resposta deve ser "Coleta nao encontrada: 999999"

  Cenário: Listar coletas de um ponto de coleta
    Quando envio uma requisição GET para "/coletas/ponto/2"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "coleta-realizada"
    E todos os itens da lista devem ter o campo "idPonto" igual a "2"

  Cenário: Registrar uma nova coleta
    Dado que existe um ponto de coleta de "Metal" com capacidade máxima de 250 kg e volume atual de 120 kg
    Quando envio uma requisição POST para "/coletas" com o corpo:
      """
      {"idPonto": {idPonto}, "dataColeta": "2030-03-05", "pesoRecolhidoKg": 120.00, "empresaResponsavel": "MetalReciclados LTDA"}
      """
    Então o status code da resposta deve ser 201
    E a resposta deve obedecer ao contrato "coleta-realizada"
    E o campo "idPonto" da resposta deve ser "{idPonto}"
    E o campo "dataColeta" da resposta deve ser "2030-03-05"
    E guardo o campo "idColeta" da resposta como "idColeta"
    E o cabeçalho "Location" da resposta deve terminar com "/coletas/{idColeta}"

  Cenário: Registrar coleta com dados inválidos é rejeitado
    Quando envio uma requisição POST para "/coletas" com o corpo:
      """
      {"idPonto": 1, "pesoRecolhidoKg": 0, "empresaResponsavel": ""}
      """
    Então o status code da resposta deve ser 422
    E a resposta deve obedecer ao contrato "erro-validacao"
    E a lista de erros de validação deve conter o campo "dataColeta"
    E a lista de erros de validação deve conter o campo "pesoRecolhidoKg"
    E a lista de erros de validação deve conter o campo "empresaResponsavel"
