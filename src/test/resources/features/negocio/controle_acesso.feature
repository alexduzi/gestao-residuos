# language: pt
@negocio
Funcionalidade: Controle de acesso ao registro de coletas
  Como responsável pela governança dos dados ambientais
  Quero que apenas administradores possam registrar coletas
  Para garantir a integridade e a rastreabilidade dos indicadores de reciclagem

  Cenário: Usuário sem perfil de administrador não consegue registrar coleta
    Dado que existe um ponto de coleta de "Vidro" com capacidade máxima de 400 kg e volume atual de 100 kg
    E que estou autenticado como usuário comum
    Quando eu registrar uma coleta de 100 kg no ponto em "2030-02-10" pela empresa "VidroLimpo ME"
    Então o status code da resposta deve ser 403
    E o volume atual do ponto de coleta deve ser 100 kg
    E não deve existir coleta registrada para o ponto de coleta
