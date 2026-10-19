# language: pt
@api @autenticacao
Funcionalidade: API de autenticação (/auth)
  Como cliente da API de gestão de resíduos
  Quero me cadastrar e fazer login
  Para obter um token JWT e acessar os recursos protegidos

  Cenário: Cadastro de novo usuário retorna token com perfil USER
    Quando envio uma requisição POST para "/auth/register" com o corpo:
      """
      {"nome": "Maria Recicla", "email": "maria.{unico}@esg.com", "senha": "senha123"}
      """
    Então o status code da resposta deve ser 201
    E a resposta deve obedecer ao contrato "auth"
    E o campo "email" da resposta deve ser "maria.{unico}@esg.com"
    E o campo "role" da resposta deve ser "USER"
    E o cabeçalho "Location" da resposta deve terminar com "/auth/register/maria.{unico}@esg.com"

  Cenário: Cadastro com e-mail já existente é rejeitado
    Quando envio uma requisição POST para "/auth/register" com o corpo:
      """
      {"nome": "Outro Admin", "email": "admin@esg.com", "senha": "senha123"}
      """
    Então o status code da resposta deve ser 409
    E a resposta deve obedecer ao contrato "erro"
    E o campo "error" da resposta deve ser "Email ja cadastrado."

  Cenário: Cadastro com dados inválidos retorna os erros de validação
    Quando envio uma requisição POST para "/auth/register" com o corpo:
      """
      {"nome": "", "email": "email-invalido", "senha": "123"}
      """
    Então o status code da resposta deve ser 422
    E a resposta deve obedecer ao contrato "erro-validacao"
    E a lista de erros de validação deve conter o campo "nome"
    E a lista de erros de validação deve conter o campo "email"
    E a lista de erros de validação deve conter o campo "senha"

  Cenário: Login do administrador retorna token com perfil ADMIN
    Quando envio uma requisição POST para "/auth/login" com o corpo:
      """
      {"email": "admin@esg.com", "senha": "admin123"}
      """
    Então o status code da resposta deve ser 200
    E a resposta deve obedecer ao contrato "auth"
    E o campo "role" da resposta deve ser "ADMIN"

  Cenário: Login com senha incorreta é negado
    Quando envio uma requisição POST para "/auth/login" com o corpo:
      """
      {"email": "admin@esg.com", "senha": "senha-errada"}
      """
    Então o status code da resposta deve ser 403
    E a resposta deve obedecer ao contrato "erro"
    E o campo "error" da resposta deve ser "Credenciais invalidas."

  Cenário: Login com e-mail não cadastrado retorna 404
    Quando envio uma requisição POST para "/auth/login" com o corpo:
      """
      {"email": "ninguem.{unico}@esg.com", "senha": "qualquer"}
      """
    Então o status code da resposta deve ser 404
    E a resposta deve obedecer ao contrato "erro"
