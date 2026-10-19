# language: pt
@negocio
Funcionalidade: Alerta automático de capacidade dos pontos de coleta
  Como gestor ambiental da operação de reciclagem
  Quero ser alertado quando um ponto de coleta estiver próximo da lotação
  Para agendar a coleta antes que o resíduo transborde e cause impacto ambiental

  Contexto:
    Dado que existe um ponto de coleta de "Plastico" com capacidade máxima de 200 kg e volume atual de 0 kg
    E que estou autenticado como usuário comum

  Cenário: Volume atingindo 90% da capacidade gera um alerta pendente
    Quando o volume do ponto de coleta for atualizado para 180 kg
    Então o status code da resposta deve ser 200
    E a resposta deve obedecer ao contrato "ponto-coleta"
    E o campo "volumeAtualKg" da resposta deve ser o número 180
    E deve existir 1 alerta pendente para o ponto de coleta com a mensagem "ALERTA ESG: O ponto de coleta atingiu ou excedeu 90% de sua capacidade."

  Cenário: Volume abaixo de 90% da capacidade não gera alerta
    Quando o volume do ponto de coleta for atualizado para 179,99 kg
    Então o status code da resposta deve ser 200
    E a resposta deve obedecer ao contrato "ponto-coleta"
    E o campo "volumeAtualKg" da resposta deve ser o número 179,99
    E não deve existir alerta para o ponto de coleta
