# Gestão de Resíduos e Reciclagem (ESG): Testes Automatizados com BDD

API RESTful em Spring Boot para gestão de resíduos e reciclagem (tema ESG), desenvolvida na
disciplina Java Advanced (FIAP). Nesta atividade (Fase 7, Testes Automatizados), o projeto
ganhou uma suíte de testes automatizados escrita em **BDD com Gherkin**. A suíte valida:

- as **regras de negócio ESG** (alerta de capacidade, registro de coleta, controle de acesso);
- **todas as 28 rotas da API**, conferindo status code, corpo JSON e contrato via JSON Schema.

| Requisito do desafio | Como foi atendido |
|---|---|
| Mínimo de 3 cenários BDD (positivos e negativos) | **5 cenários de negócio** em Gherkin (`features/negocio/`) |
| Testes de API para todas as APIs | **49 cenários** em `features/api/`, cobrindo os **28 endpoints** |
| Validação de status code | Step `o status code da resposta deve ser ...` em todos os cenários |
| Validação do corpo da resposta (JSON) | Steps que verificam campos, listas e cabeçalhos da resposta |
| Testes de contrato com JSON Schema | 9 schemas em `src/test/resources/schemas/`, aplicados em todo cenário que tem corpo de resposta |
| Execução simples e documentada | Um único comando: `./mvnw test` (seção abaixo) |

---

## Como executar os testes

**Pré-requisito:** apenas o **JDK 21**. O Maven Wrapper (`./mvnw`) baixa o Maven sozinho. Não é
preciso banco de dados, Docker nem nenhuma variável de ambiente: os testes sobem a própria API
com um banco H2 em memória.

```bash
# todos os testes (22 unitários + 54 cenários Cucumber)
./mvnw test

# só a suíte BDD/API (Cucumber)
./mvnw test -Dtest=RunCucumberTest

# só os 5 cenários de negócio, ou só uma API específica (filtro por tag)
./mvnw test -Dtest=RunCucumberTest -Dcucumber.filter.tags="@negocio"
./mvnw test -Dtest=RunCucumberTest -Dcucumber.filter.tags="@pontos-coleta"
```

No Windows, use `mvnw.cmd` no lugar de `./mvnw`.

Tags disponíveis: `@negocio`, `@api`, `@autenticacao`, `@tipos-residuo`, `@pontos-coleta`,
`@coletas`, `@alertas`, `@consolidado`, `@notificacoes`.

**Relatórios gerados** a cada execução:

| Relatório | Caminho |
|---|---|
| Cucumber HTML (abrir no navegador) | `target/cucumber-reports/cucumber.html` |
| Cucumber JSON | `target/cucumber-reports/cucumber.json` |
| Surefire (JUnit) | `target/surefire-reports/` |

O terminal também mostra cada cenário passo a passo (plugin `pretty` do Cucumber).

Resultado esperado no final:

```
[INFO] Tests run: 54, Failures: 0, Errors: 0, Skipped: 0 -- in br.com.fiap.gestaoresiduos.bdd.RunCucumberTest
[INFO] Tests run: 76, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Stack de testes

| Ferramenta | Versão | Papel |
|---|---|---|
| Cucumber (`cucumber-java`, `cucumber-spring`, `cucumber-junit-platform-engine`) | 7.18.1 | BDD: interpreta os `.feature` em Gherkin (em português) e liga cada passo ao código Java |
| JUnit 5 Platform Suite | gerenciado pelo Spring Boot | `RunCucumberTest`, o runner executado pelo Surefire |
| RestAssured | 5.5.0 | Envia as requisições HTTP para a API |
| networknt json-schema-validator | 1.5.1 | Testes de contrato (JSON Schema draft-07) |
| AssertJ | gerenciado pelo Spring Boot | Asserções |
| Spring Boot Test + H2 | 3.3.4 | Sobe a API real em porta aleatória, com banco em memória |

### Como funciona

1. O Surefire executa `RunCucumberTest`, uma suíte JUnit 5 que ativa o motor do Cucumber
   sobre `src/test/resources/features`.
2. `CucumberSpringConfiguration` (`@SpringBootTest(webEnvironment = RANDOM_PORT)`) sobe a
   **API completa** (Tomcat, Spring Security/JWT, JPA, Flyway) uma única vez, no profile
   `bdd`.
3. Os steps chamam a API **por HTTP, de verdade**, via RestAssured. Os testes de API não usam
   mocks: passam pelo filtro JWT, pelas regras de autorização, pela validação, pelo service e
   pelo banco.
4. Cada resposta é validada em três níveis: **status code**, **conteúdo do JSON** e
   **contrato (JSON Schema)**.

### Banco de dados dos testes (profile `bdd`)

H2 em memória em **modo de compatibilidade Oracle**, com o Flyway ligado
(`src/test/resources/application-bdd.properties`). Ele roda:

- as **mesmas migrations de produção** (`src/main/resources/db/migration/`):
  - `V1`: schema;
  - `V2`: tipos de resíduo, pontos de coleta, coletas, alertas e consolidados;
  - `V3`: usuários `admin@esg.com` / `admin123` (ADMIN) e `user@esg.com` / `user123` (USER);
- **mais os dados criados só para os testes** em
  `src/test/resources/db/testdata/V100__dados_teste_bdd.sql`: notificações para o
  usuário comum e para o admin, e um tipo de resíduo (`Isopor`, id 8) sem nenhum ponto de
  coleta vinculado, usado nos cenários de "lista vazia".

Como os testes rodam as migrations reais, eles também garantem que os scripts SQL de produção
continuam válidos. Os cenários que alteram dados criam os próprios registros via API (ponto de
coleta, tipo de resíduo etc.), então não dependem da ordem de execução.

---

## Estrutura dos testes

```
src/test/
├── java/br/com/fiap/gestaoresiduos/
│   ├── bdd/
│   │   ├── RunCucumberTest.java              # runner (suíte JUnit 5 + Cucumber)
│   │   ├── CucumberSpringConfiguration.java  # sobe a API (@SpringBootTest, profile bdd)
│   │   ├── steps/
│   │   │   ├── Hooks.java                    # configura o RestAssured (porta, /api/v1)
│   │   │   ├── AutenticacaoSteps.java        # login como admin / usuário comum / sem token
│   │   │   ├── RequisicaoSteps.java          # envio de GET/POST/PUT/PATCH/DELETE
│   │   │   ├── RespostaSteps.java            # status code, campos JSON, contrato, cabeçalhos
│   │   │   └── RegrasNegocioSteps.java       # passos em linguagem de negócio (cenários ESG)
│   │   └── support/
│   │       ├── ApiCliente.java               # chamadas RestAssured + login
│   │       ├── ContextoCenario.java          # estado do cenário (token, resposta, variáveis)
│   │       └── ValidadorContrato.java        # validação JSON Schema (networknt)
│   ├── service/ ...                          # testes unitários (Mockito)
│   └── infra/security/ ...                   # testes unitários do JWT
└── resources/
    ├── application-bdd.properties            # profile dos testes BDD (H2 + Flyway)
    ├── db/testdata/V100__dados_teste_bdd.sql # dados de teste
    ├── features/
    │   ├── negocio/                          # 5 cenários BDD das regras ESG
    │   │   ├── alerta_capacidade.feature
    │   │   ├── registro_coleta.feature
    │   │   └── controle_acesso.feature
    │   └── api/                              # testes de todas as APIs
    │       ├── autenticacao.feature
    │       ├── tipos_residuo.feature
    │       ├── pontos_coleta.feature
    │       ├── coletas.feature
    │       ├── alertas.feature
    │       ├── consolidado.feature
    │       └── notificacoes.feature
    └── schemas/                              # contratos JSON Schema (draft-07)
        ├── auth.json  tipo-residuo.json  ponto-coleta.json  coleta-realizada.json
        ├── alerta-capacidade.json  consolidado-reciclagem.json  notificacao.json
        └── erro.json  erro-validacao.json
```

---

## Cenários BDD de negócio (5)

Escritos em Gherkin em português (`# language: pt`). Cobrem as regras ESG centrais do sistema,
com caminhos felizes e de falha.

| # | Funcionalidade | Cenário | Tipo |
|---|---|---|---|
| 1 | Alerta automático de capacidade | Volume atingindo 90% da capacidade gera um alerta pendente | positivo |
| 2 | Alerta automático de capacidade | Volume abaixo de 90% da capacidade não gera alerta | negativo (limite) |
| 3 | Registro de coleta realizada | Coleta registrada esvazia o ponto, resolve os alertas e alimenta o consolidado mensal | positivo |
| 4 | Registro de coleta realizada | Coleta em um ponto de coleta inexistente é rejeitada | negativo |
| 5 | Controle de acesso ao registro de coletas | Usuário sem perfil de administrador não consegue registrar coleta | negativo |

### `alerta_capacidade.feature`

```gherkin
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
```

### `registro_coleta.feature`

```gherkin
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
```

### `controle_acesso.feature`

```gherkin
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
```

Os números decimais nos `.feature` usam **vírgula** (`179,99`). Como os arquivos estão em
português, o Cucumber interpreta `{bigdecimal}` no formato pt-BR, em que o ponto é separador de
milhar: `150.50` seria lido como `15050`.

---

## Testes de API (todas as rotas)

Cada endpoint tem pelo menos um cenário de sucesso, que valida status code, campos do JSON e
contrato. Quase todos também têm cenários de falha: 404 para recurso inexistente, 422 para
dados inválidos, 403 para falta de permissão, 409 para conflito de regra e 400 para violação
de integridade.

| Endpoint | Perfil exigido | Cenários (`features/api/`) |
|---|---|---|
| `POST /auth/register` | público | 201 + contrato `auth` + `Location`; 409 e-mail duplicado; 422 dados inválidos |
| `POST /auth/login` | público | 200 admin; 403 senha errada; 404 e-mail inexistente |
| `GET /tipos-residuo` | autenticado | 200 + lista `tipo-residuo` |
| `GET /tipos-residuo/{id}` | autenticado | 200 + campos; 404 |
| `POST /tipos-residuo` | ADMIN | 201 + `Location`; 422 S/N inválido; 403 usuário comum |
| `PUT /tipos-residuo/{id}` | ADMIN | 200; 404 |
| `DELETE /tipos-residuo/{id}` | ADMIN | 204 sem corpo e depois 404 no GET; 400 tipo em uso; 404 |
| `GET /pontos-coleta` | autenticado | 200 + lista `ponto-coleta`; 403 sem token |
| `GET /pontos-coleta/{id}` | autenticado | 200 + campos (inclusive `tipo` aninhado); 404 |
| `GET /pontos-coleta/tipo/{idTipo}` | autenticado | 200, todos os itens do tipo pedido; lista vazia |
| `POST /pontos-coleta` | ADMIN | 201 + `Location`; 404 tipo inexistente; 422 dados inválidos |
| `PUT /pontos-coleta/{id}` | ADMIN | 200; 404 |
| `PATCH /pontos-coleta/{id}/volume` | autenticado | 200; 422 volume negativo (e as regras de alerta nos cenários de negócio) |
| `DELETE /pontos-coleta/{id}` | ADMIN | 204 e depois 404 no GET; 404 |
| `GET /coletas` | autenticado | 200 + lista `coleta-realizada` |
| `GET /coletas/{id}` | autenticado | 200 + campos; 404 |
| `GET /coletas/ponto/{idPonto}` | autenticado | 200, todos os itens do ponto pedido |
| `POST /coletas` | ADMIN | 201 + `Location`; 422 dados inválidos; 404 e 403 nos cenários de negócio |
| `GET /alertas` | ADMIN | 200 + lista `alerta-capacidade`; 403 usuário comum |
| `GET /alertas/pendentes` | ADMIN | 200, todos com `statusResolvido = N` |
| `GET /alertas/ponto/{idPonto}` | autenticado | 200, todos os itens do ponto pedido |
| `PATCH /alertas/{id}/resolver` | ADMIN | 200 `statusResolvido = S`; 409 já resolvido; 404 |
| `GET /consolidado` | autenticado | 200 + lista `consolidado-reciclagem` |
| `GET /consolidado/mes/{MM-yyyy}` | autenticado | 200 + valores do mês |
| `GET /consolidado/tipo/{idTipo}` | autenticado | 200, todos os itens do tipo; lista vazia |
| `GET /notificacoes/usuario/{idUsuario}` | autenticado | 200 + lista `notificacao` |
| `POST /notificacoes` | ADMIN | 201 + `Location`; 404 usuário inexistente; 422 mensagem > 300 caracteres; 403 usuário comum |
| `PATCH /notificacoes/{id}/lida` | autenticado | 200 `lida = S`; 404 |

Todos os caminhos ficam sob o prefixo `/api/v1`.

### Testes de contrato (JSON Schema)

Os contratos ficam em `src/test/resources/schemas/` (JSON Schema **draft-07**, validados com o
`networknt json-schema-validator`). Eles são estritos:

- todos os campos são obrigatórios (`required`);
- campos não previstos são rejeitados (`additionalProperties: false`);
- os tipos são conferidos (inteiro, número, string);
- os domínios também: `S`/`N`, roles `USER`/`ADMIN`, datas `yyyy-MM-dd`, `mesAno` `MM/yyyy`,
  token JWT com três partes, limites de tamanho e pesos maiores que zero.

Para respostas em lista, cada item é validado contra o schema do recurso. Respostas de erro são
validadas contra `erro.json` (`timestamp`, `status`, `error`, `path`) ou
`erro-validacao.json` (422 com a lista `errors[].fieldName/message`).

Exemplo de falha de contrato (se a API passasse a devolver `idTipo` como texto):

```
[violacoes do contrato 'tipo-residuo']
Expecting empty but was: ["$.idTipo: integer found, string expected"]
```

---

## Defeitos encontrados pelos testes

A escrita dos testes revelou dois defeitos reais na API. Os dois foram corrigidos:

1. **Alerta de capacidade disparando abaixo de 90%.** `PontoColetaService` calculava
   `volume / capacidade` com 4 casas decimais e arredondamento `HALF_UP` antes de comparar com
   0,90. Um ponto de 200 kg com 179,99 kg (89,995%) virava `0.9000` e gerava um alerta
   indevido. O cenário "Volume abaixo de 90% da capacidade não gera alerta" falhava. A correção
   compara o volume diretamente com `capacidade × 0,90`, sem arredondamento.
2. **`GET /consolidado/mes/{mesAno}` nunca retornava dados.** O mês é gravado como `MM/yyyy`,
   mas a barra não chega ao parâmetro do path: `/consolidado/mes/10/2025` vira outra rota
   (403) e `10%2F2025` é rejeitado pelo Tomcat (400). Já o formato `MM-yyyy`, que era o
   documentado em `docs/api.http`, devolvia sempre uma lista vazia. A correção:
   `ConsolidadoReciclagemService` aceita `MM-yyyy` no path e converte para `MM/yyyy` antes de
   consultar.

---

## Evidências de execução

Os prints da execução dos testes ficam na pasta [`evidencias/`](evidencias/):

- execução no terminal (`./mvnw test` com `BUILD SUCCESS`);
- relatório HTML do Cucumber;
- configuração do projeto (`pom.xml`, runner, profile `bdd`).

---

## Integração contínua (opcional)

A entrega executa os testes **localmente**. Mesmo assim, o repositório mantém o workflow do
GitHub Actions em [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml), herdado da
atividade anterior de DevOps. Como a suíte Cucumber roda pelo Surefire dentro do `./mvnw test`,
o job `build-and-test` desse workflow **já executaria todos os cenários BDD e de API sem
nenhuma alteração**, porque eles não dependem de banco externo nem de secrets.

---

## Executar a API localmente (opcional)

Os testes não precisam disto. Serve para explorar a API manualmente (Swagger ou
[`docs/api.http`](docs/api.http)).

**Sem Docker**, com H2 em memória e os mesmos dados de produção (V1 a V3):

```bash
SPRING_PROFILES_ACTIVE=staging ./mvnw spring-boot:run
```

**Com Docker Compose**, com Oracle XE em container:

```bash
cp .env.example .env
docker compose up --build
```

O Compose sobe dois containers:

- `oracle` (Oracle XE 21, com volume nomeado `oracle_data` e healthcheck). Na primeira vez,
  leva de 1 a 3 minutos para inicializar.
- `app`: a API, que só inicia depois que o Oracle está saudável.

Endereços da API em execução:

- API: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`

O `Dockerfile` usa build multistage (o JDK fica só no estágio de build e o runtime usa apenas o
JRE), roda com usuário não-root e tem `HEALTHCHECK` em `/actuator/health`.

---

## Tecnologias utilizadas

- Java 21, Spring Boot 3.3.4
- Spring Security + JWT (jjwt 0.12.6)
- Spring Data JPA + Hibernate, Flyway
- Oracle Database (ojdbc11) / H2 (testes)
- SpringDoc OpenAPI (Swagger UI), Spring Boot Actuator, Lombok
- **Testes:** Cucumber 7.18.1 (Gherkin), RestAssured 5.5.0, networknt json-schema-validator
  1.5.1, JUnit 5, AssertJ, Mockito
- Docker + Docker Compose

---

## Documentação funcional

<details>
<summary>Estrutura do projeto, endpoints, regras de negócio e configuração</summary>

### Estrutura do projeto

```
src/main/
├── java/br/com/fiap/gestaoresiduos/
│   ├── config/               # CORS, OpenAPI
│   ├── controller/           # Endpoints REST
│   ├── domain/               # Entidades JPA
│   ├── dto/
│   │   ├── request/          # Payloads de entrada com validações
│   │   └── response/         # Payloads de saída (Records)
│   ├── exception/            # Exceptions customizadas e handler global
│   ├── infra/security/       # JWT filter, UserDetailsService, SecurityConfig
│   ├── repository/           # Interfaces JpaRepository
│   └── service/              # Regras de negócio
└── resources/db/migration/
    ├── V1__create_tables.sql       # DDL completo
    ├── V2__insert_initial_data.sql # Dados iniciais de domínio
    └── V3__insert_users.sql        # Usuários padrão do sistema
```

### Profiles

| Profile | Banco | Flyway | Uso |
|---|---|---|---|
| `test` | H2 in-memory (schema gerado pelo JPA) | Off | Padrão; teste de contexto |
| `bdd` | H2 in-memory em modo Oracle | On (+ dados de teste) | Testes BDD/API (só no classpath de teste) |
| `staging` | H2 in-memory em modo Oracle | On | Rodar a API localmente sem Oracle |
| `dev` | Oracle (XE local via Docker Compose, ou FIAP) | On | Desenvolvimento com banco real |
| `prod` | Oracle da FIAP | On | Produção |

### Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `test` | Profile ativo |
| `DB_USER` | `system` | Usuário do Oracle |
| `DB_PASS` | `oracle` | Senha do Oracle |
| `JWT_SECRET` | *(valor embutido)* | Secret Base64 para assinar o JWT |
| `JWT_DURATION` | `86400` | Duração do token em segundos |
| `CORS_ORIGINS` | `*` | Origens permitidas por CORS |

### Usuários padrão (criados pela migration V3)

| E-mail | Senha | Role |
|---|---|---|
| `admin@esg.com` | `admin123` | `ADMIN` |
| `user@esg.com` | `user123` | `USER` |

`/auth/register` sempre cria usuários com role `USER`.

### Endpoints

| Grupo | Prefixo | Autenticação |
|---|---|---|
| Autenticação | `/api/v1/auth` | Público |
| Tipos de Resíduo | `/api/v1/tipos-residuo` | USER / ADMIN |
| Pontos de Coleta | `/api/v1/pontos-coleta` | USER / ADMIN |
| Coletas Realizadas | `/api/v1/coletas` | USER / ADMIN |
| Alertas | `/api/v1/alertas` | USER / ADMIN (listagem geral e pendentes: só ADMIN) |
| Consolidado | `/api/v1/consolidado` | USER / ADMIN |
| Notificações | `/api/v1/notificacoes` | USER / ADMIN |

As operações de escrita (POST, PUT, PATCH, DELETE) exigem role `ADMIN`, com duas exceções:
`PATCH /pontos-coleta/{id}/volume` e `PATCH /notificacoes/{id}/lida` aceitam qualquer usuário
autenticado.

Documentação completa em `/swagger-ui.html` (com a aplicação rodando) ou no arquivo
[`docs/api.http`](docs/api.http).

### Regras de negócio principais

- **Registro de coleta** (`POST /coletas`):
  - zera o `volumeAtualKg` do ponto de coleta;
  - atualiza ou cria o `CONSOLIDADO_RECICLAGEM` do mês correspondente;
  - resolve os alertas pendentes do ponto.
- **Atualização de volume** (`PATCH /pontos-coleta/{id}/volume`): gera automaticamente um
  `AlertaCapacidade` quando o volume atinge ou ultrapassa 90% da capacidade máxima.

</details>

---

## Checklist de entrega

| Item | OK |
|---|---|
| No mínimo 3 cenários BDD em Gherkin, positivos e negativos (feitos: 5) | ☑ |
| Testes de API para todas as APIs (28 endpoints) | ☑ |
| Validação de status code | ☑ |
| Validação do corpo da resposta (JSON) | ☑ |
| Testes de contrato com JSON Schema | ☑ |
| Execução simples e documentada (`./mvnw test`) | ☑ |
| Código-fonte em `.zip` com testes, Dockerfile, scripts e README | ☐ |
| Documentação (PDF/PPT) com cenários Gherkin, prints de configuração e evidências | ☐ |

---

## Equipe

Projeto desenvolvido para a disciplina de Testes Automatizados, FIAP 2026.
