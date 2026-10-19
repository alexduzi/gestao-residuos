# language: pt
@negocio
Funcionalidade: Registro de coleta realizada
  Como operador da empresa de reciclagem
  Quero registrar as coletas realizadas nos pontos de coleta
  Para manter a rastreabilidade do resíduo e os indicadores de reciclagem atualizados

  Cenário: Coleta registrada esvazia o ponto, resolve os alertas e alimenta o consolidado mensal
    Dado que existe um tipo de resíduo reciclável "Papelao"
    E que existe um ponto de coleta desse tipo com capacidade máxima de 300 kg e volume atual de 0 kg
    E que o volume do ponto foi atualizado para 285 kg, gerando um alerta pendente
    E que estou autenticado como administrador
    Quando eu registrar uma coleta de 285 kg no ponto em "2030-01-15" pela empresa "EcoRecicla LTDA"
    Então o status code da resposta deve ser 201
    E a resposta deve obedecer ao contrato "coleta-realizada"
    E o campo "pesoRecolhidoKg" da resposta deve ser o número 285
    E o volume atual do ponto de coleta deve ser 0 kg
    E não deve existir alerta pendente para o ponto de coleta
    E o consolidado de reciclagem do tipo em "01/2030" deve totalizar 285 kg

  Cenário: Coleta em um ponto de coleta inexistente é rejeitada
    Dado que estou autenticado como administrador
    Quando eu registrar uma coleta de 50 kg no ponto de id 999999 em "2030-01-15" pela empresa "EcoRecicla LTDA"
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"
    E o campo "error" da resposta deve ser "Ponto de coleta nao encontrado: 999999"
