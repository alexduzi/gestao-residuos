# language: pt
@api @consolidado
Funcionalidade: API de consolidado de reciclagem (/consolidado)
  Como responsável pelos indicadores ESG
  Quero consultar o total reciclado por tipo de resíduo e por mês
  Para acompanhar a evolução da reciclagem e reportar os resultados

  Contexto:
    Dado que estou autenticado como usuário comum

  Cenário: Listar todo o consolidado de reciclagem
    Quando envio uma requisição GET para "/consolidado"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "consolidado-reciclagem"
    E a lista da resposta não deve estar vazia

  Cenário: Consultar o consolidado de um mês
    Quando envio uma requisição GET para "/consolidado/mes/10-2025"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "consolidado-reciclagem"
    E a lista da resposta deve conter 1 item
    E o campo "[0].tipoResiduo" da resposta deve ser "Eletronico"
    E o campo "[0].mesAno" da resposta deve ser "10/2025"
    E o campo "[0].totalRecicladoKg" da resposta deve ser o número 150

  Cenário: Consultar o consolidado de um tipo de resíduo
    Quando envio uma requisição GET para "/consolidado/tipo/3"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "consolidado-reciclagem"
    E todos os itens da lista devem ter o campo "tipoResiduo" igual a "Vidro"

  Cenário: Tipo de resíduo sem reciclagem consolidada retorna lista vazia
    Quando envio uma requisição GET para "/consolidado/tipo/8"
    Então o status code da resposta deve ser 200
    E a lista da resposta deve conter 0 itens
