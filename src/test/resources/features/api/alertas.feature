# language: pt
@api @alertas
Funcionalidade: API de alertas de capacidade (/alertas)
  Como gestor ambiental
  Quero consultar e resolver os alertas de capacidade
  Para priorizar as coletas dos pontos mais cheios

  Cenário: Administrador lista todos os alertas
    Dado que estou autenticado como administrador
    Quando envio uma requisição GET para "/alertas"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "alerta-capacidade"
    E a lista da resposta não deve estar vazia

  Cenário: Usuário comum não pode listar todos os alertas
    Dado que estou autenticado como usuário comum
    Quando envio uma requisição GET para "/alertas"
    Então o status code da resposta deve ser 403

  Cenário: Administrador lista apenas os alertas pendentes
    Dado que estou autenticado como administrador
    Quando envio uma requisição GET para "/alertas/pendentes"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "alerta-capacidade"
    E todos os itens da lista devem ter o campo "statusResolvido" igual a "N"

  Cenário: Listar alertas de um ponto de coleta
    Dado que estou autenticado como usuário comum
    Quando envio uma requisição GET para "/alertas/ponto/5"
    Então o status code da resposta deve ser 200
    E a resposta deve ser uma lista que obedece ao contrato "alerta-capacidade"
    E todos os itens da lista devem ter o campo "idPonto" igual a "5"

  Cenário: Resolver um alerta pendente e impedir resolvê-lo duas vezes
    Dado que existe um ponto de coleta de "Eletronico" com capacidade máxima de 100 kg e volume atual de 0 kg
    E que estou autenticado como administrador
    E envio uma requisição PATCH para "/pontos-coleta/{idPonto}/volume" com o corpo:
      """
      {"volumeAtualKg": 95}
      """
    E envio uma requisição GET para "/alertas/ponto/{idPonto}"
    E guardo o campo "[0].idAlerta" da resposta como "idAlerta"
    Quando envio uma requisição PATCH para "/alertas/{idAlerta}/resolver"
    Então o status code da resposta deve ser 200
    E a resposta deve obedecer ao contrato "alerta-capacidade"
    E o campo "idAlerta" da resposta deve ser "{idAlerta}"
    E o campo "statusResolvido" da resposta deve ser "S"
    Quando envio uma requisição PATCH para "/alertas/{idAlerta}/resolver"
    Então o status code da resposta deve ser 409
    E a resposta deve obedecer ao contrato "erro"
    E o campo "error" da resposta deve ser "Alerta ja esta resolvido."

  Cenário: Resolver alerta inexistente retorna 404
    Dado que estou autenticado como administrador
    Quando envio uma requisição PATCH para "/alertas/999999/resolver"
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"
    E o campo "error" da resposta deve ser "Alerta nao encontrado: 999999"
