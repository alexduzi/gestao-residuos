# ESG Gestão de Resíduos e Reciclagem — API RESTful

API RESTful desenvolvida com Spring Boot para gerenciamento de resíduos e reciclagem, tema ESG da disciplina de Java Advanced (FIAP).

---

## Tecnologias utilizadas

- Java 21
- Spring Boot 3.3.4
- Spring Security + JWT (jjwt 0.12.6)
- Spring Data JPA + Hibernate
- Oracle Database (ojdbc11)
- Flyway (migrações de banco)
- Spring Boot Actuator (health check)
- SpringDoc OpenAPI (Swagger UI)
- Lombok
- Docker + Docker Compose
- GitHub Actions (CI/CD)
- Azure Container Registry + Azure App Service (deploy)

---

## Como executar localmente com Docker

```bash
cp .env.example .env
# edite .env se quiser trocar DB_USER/DB_PASS/JWT_SECRET
docker compose up --build
```

Isso sobe dois containers: `oracle` (Oracle XE 21, leva ~1–3 min para inicializar na primeira
vez) e `app` (a API, que só inicia depois que o Oracle responde saudável). A API fica
disponível em `http://localhost:8080`; Swagger UI em `http://localhost:8080/swagger-ui.html`;
health check em `http://localhost:8080/actuator/health`. Detalhes completos, variações (Docker
puro sem compose, build/push de imagem, limpeza) e troubleshooting na seção
[Containerização](#containerização) abaixo.

---

## Estrutura do projeto

```
src/
├── config/               # CORS
├── controller/           # Endpoints REST
├── domain/               # Entidades JPA
├── dto/
│   ├── request/          # Payloads de entrada com validações
│   └── response/         # Payloads de saída (Records)
├── exception/            # Exceptions customizadas e handler global
├── infra/
│   └── security/         # JWT filter, UserDetailsService, SecurityConfig
├── repository/           # Interfaces JpaRepository
└── service/              # Regras de negócio

resources/
└── db/migration/
    ├── V1__create_tables.sql       # DDL completo
    ├── V2__insert_initial_data.sql # Dados iniciais de domínio
    └── V3__insert_users.sql        # Usuários padrão do sistema
```

---

## Configuração

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Acesso ao Oracle Database (instância local, Docker ou FIAP cloud)

### Variáveis de ambiente

| Variável       | Padrão              | Descrição                        |
|----------------|---------------------|----------------------------------|
| `DB_USER`      | `system`            | Usuário do Oracle                |
| `DB_PASS`      | `oracle`            | Senha do Oracle                  |
| `JWT_SECRET`   | *(valor embutido)*  | Secret Base64 para assinar o JWT |
| `JWT_DURATION` | `86400`             | Duração do token em segundos     |
| `CORS_ORIGINS` | `*`                 | Origens permitidas por CORS      |

Um `.env.example` está disponível na raiz do projeto — copie para `.env` (`cp .env.example
.env`) antes de rodar `docker compose up`.

Não esqueça de trocar as variáveis no arquivo application-dev.properties

spring.datasource.username=${DB_USER:sua_matricula_fiap}

spring.datasource.password=${DB_PASS:sua_senha}

### Profiles

| Profile | Banco | Flyway | Uso |
|---|---|---|---|
| `test` | H2 in-memory (`ddl-auto=create-drop`) | Off | Padrão — CI, testes locais rápidos |
| `staging` | H2 in-memory **em modo Oracle** (`MODE=Oracle`) | On | Deploy em staging (Azure) |
| `dev` | Oracle (XE local via Docker Compose, ou FIAP se fora do compose) | On | Desenvolvimento com banco real |
| `prod` | Oracle da FIAP (`oracle.fiap.com.br`) | On | Deploy em produção (Azure) |

O profile ativo padrão é `test`. Para rodar com Oracle localmente, passe
`--spring.profiles.active=dev` ou configure `SPRING_PROFILES_ACTIVE=dev`.

`staging` e `prod` existem para o pipeline de CI/CD (ver seção
[Pipeline CI/CD](#pipeline-cicd)): `staging` roda em H2 com o Flyway ligado, executando as
**mesmas migrations** (`db/migration/`) que rodam contra o Oracle real — é efêmero de
propósito (reseta a cada deploy), já que não existe uma segunda instância Oracle disponível
além da da FIAP. `prod` aponta para o Oracle da FIAP com as credenciais reais.

---

## Rodando localmente

### Opção 1 — Maven direto (profile test, H2)

```bash
./mvnw spring-boot:run
```

### Opção 2 — Maven com Oracle local (profile dev)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Opção 3 — Docker Compose (app + Oracle XE)

```bash
docker-compose up --build
```

A API sobe em `http://localhost:8080`.

---

## Containerização

**Dockerfile** — build multistage:

1. **Estágio `build`** (`eclipse-temurin:21-jdk-alpine`): copia `.mvn/`, `mvnw` e `pom.xml`,
   baixa dependências (`dependency:go-offline`, aproveitando cache de camada do Docker), só
   depois copia `src/` e empacota (`clean package -DskipTests` — os testes já rodaram na etapa
   de CI antes da imagem ser construída, não precisam rodar de novo aqui).
2. **Estágio runtime** (`eclipse-temurin:21-jre-alpine`, bem menor que a imagem de build):
   copia só o jar final, instala `curl` (necessário para o `HEALTHCHECK`), roda como usuário
   não-root `spring:spring`, expõe a porta 8080.
3. `HEALTHCHECK` embutido na imagem (`curl --fail http://localhost:8080/actuator/health`) —
   o Docker marca o container como `unhealthy` se a aplicação não responder, o que o
   `docker-compose.yml` usa via `depends_on: condition: service_healthy` em qualquer serviço
   que dependa da API, e que o Azure App Service também consegue usar como probe.

**docker-compose.yml** — orquestra dois serviços:

- `oracle` (`gvenzl/oracle-xe:21-slim`) — volume nomeado `oracle_data` para persistir os dados
  do banco entre restarts, `healthcheck` próprio (`healthcheck.sh` da imagem) para o `app` só
  subir depois que o Oracle estiver realmente pronto a aceitar conexões.
- `app` — variáveis de ambiente injetadas do `.env` (com fallback via `${VAR:-default}` caso
  o `.env` não exista), `depends_on: oracle: condition: service_healthy`, e seu próprio
  `healthcheck` batendo em `/actuator/health`.

Os dois serviços compartilham a rede default criada automaticamente pelo Compose (comunicação
`app` → `oracle` pelo nome do serviço, `oracle:1521`).

O projeto pode ser executado de duas formas com Docker:

- **A)** `docker-compose` — sobe a aplicação **junto** com um Oracle XE local em containers (banco isolado, descartável).
- **B)** `docker` puro — sobe **apenas** o container da aplicação, apontando para um Oracle externo (FIAP cloud, instância em outro servidor, RDS, etc.). Útil quando o desenvolvedor já tem um banco disponível e quer evitar rodar o Oracle XE localmente.

### A) Docker Compose (app + Oracle XE)

Sobe os dois serviços (`oracle` e `app`) definidos no `docker-compose.yml`. O Oracle XE leva ~1–3 min para inicializar na primeira vez; o `healthcheck` faz a aplicação esperar o banco ficar pronto antes de subir.

Conexão usada pela aplicação dentro do compose:

- Host: `oracle` (nome do serviço)
- Service: `XEPDB1` (PDB padrão da imagem `gvenzl/oracle-xe:21-slim`)
- Usuário/senha: `app` / `app` (criados via `APP_USER` / `APP_USER_PASSWORD`)

```bash
# Build e subir em primeiro plano (logs no terminal)
docker-compose up --build

# Subir em background (detached)
docker-compose up --build -d

# Ver logs
docker-compose logs -f app
docker-compose logs -f oracle

# Parar containers (mantém volumes)
docker-compose stop

# Derrubar containers e rede (mantém volumes)
docker-compose down

# Derrubar tudo, inclusive o volume do Oracle (zera o banco)
docker-compose down -v

# Rebuild forçando sem cache
docker-compose build --no-cache
```

### B) Docker puro (app apontando para Oracle externo)

Use este modo quando quiser rodar **somente** o container da aplicação e apontar para um banco em outro servidor (ex.: `oracle.fiap.com.br`).

```bash
# 1. Build da imagem
docker build -t esg-residuos-app:latest .

# 2. Rodar o container apontando para o Oracle remoto
docker run -d \
  --name esg-residuos-app \
  -p 8080:8080 \
  -e DB_USER=seu_usuario \
  -e DB_PASS=sua_senha \
  -e JWT_SECRET=sua-secret-base64 \
  -e JWT_DURATION=86400 \
  -e SPRING_DATASOURCE_URL='jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL' \
  esg-residuos-app:latest
```

> A `SPRING_DATASOURCE_URL` sobrescreve a URL definida em `application-dev.properties`. Aponte para o host/porta/SID do seu Oracle.
> Se o banco estiver em `localhost` da máquina host (fora do container), use `host.docker.internal` (Mac/Windows) ou `--network host` (Linux) no lugar de `localhost`.

### Build e publicação da imagem

```bash
# Build local com tag
docker build -t esg-residuos-app:latest .

# Build com tag versionada
docker build -t esg-residuos-app:1.0.0 .

# Tag para um registry (Docker Hub, GHCR, ECR, etc.)
docker tag esg-residuos-app:latest seuusuario/esg-residuos-app:latest
docker tag esg-residuos-app:latest ghcr.io/seuusuario/esg-residuos-app:1.0.0

# Login no registry
docker login                          # Docker Hub
docker login ghcr.io                  # GitHub Container Registry

# Push
docker push seuusuario/esg-residuos-app:latest
docker push ghcr.io/seuusuario/esg-residuos-app:1.0.0
```

### Acessar o container em execução

```bash
# Listar containers rodando
docker ps

# Abrir um shell (sh — a imagem é alpine, não tem bash)
docker exec -it esg-residuos-app sh

# Executar um comando único dentro do container
docker exec -it esg-residuos-app ls /app
docker exec -it esg-residuos-app java -version

# Acessar o container do Oracle (compose)
docker exec -it oracle-esg sh
docker exec -it oracle-esg sqlplus system/oracle@//localhost:1521/XEPDB1
docker exec -it oracle-esg sqlplus app/app@//localhost:1521/XEPDB1

# Ver logs ao vivo
docker logs -f esg-residuos-app
```

### Limpeza (prune / delete)

```bash
# Parar e remover um container específico
docker stop esg-residuos-app && docker rm esg-residuos-app

# Remover uma imagem específica
docker rmi esg-residuos-app:latest

# Forçar remoção (mesmo se houver containers usando)
docker rmi -f esg-residuos-app:latest

# Remover containers parados
docker container prune

# Remover imagens dangling (sem tag, geradas em rebuilds)
docker image prune

# Remover TODAS as imagens não usadas por nenhum container
docker image prune -a

# Remover volumes não usados (cuidado: apaga dados do Oracle se o container estiver removido)
docker volume prune

# Faxina geral: containers parados + redes não usadas + imagens dangling + cache de build
docker system prune

# Faxina TOTAL (inclui imagens não usadas e volumes — destrutivo)
docker system prune -a --volumes
```

> **Atenção:** `docker system prune -a --volumes` e `docker volume prune` apagam dados persistidos (incluindo o volume `oracle_data` do compose). Só execute se realmente quiser zerar tudo.

---

## Usuários padrão

Criados automaticamente pela migração V3 ao rodar com o perfil `dev`.

| E-mail           | Senha      | Role    |
|------------------|------------|---------|
| `admin@esg.com`  | `admin123` | `ADMIN` |
| `user@esg.com`   | `user123`  | `USER`  |

> O endpoint `POST /api/v1/auth/register` sempre cria usuários com role `USER`. Para criar um `ADMIN`, use o usuário padrão acima ou atualize diretamente no banco.

---

## Documentação da API

Com a aplicação rodando, acesse:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

O arquivo `api.http` na raiz do projeto contém todos os endpoints prontos para uso no IntelliJ IDEA HTTP Client ou VS Code REST Client.

---

## Endpoints

| Grupo              | Prefixo                        | Autenticação |
|--------------------|--------------------------------|--------------|
| Autenticação       | `/api/v1/auth`                 | Público      |
| Tipos de Resíduo   | `/api/v1/tipos-residuo`        | USER / ADMIN |
| Pontos de Coleta   | `/api/v1/pontos-coleta`        | USER / ADMIN |
| Coletas Realizadas | `/api/v1/coletas`              | USER / ADMIN |
| Alertas            | `/api/v1/alertas`              | USER / ADMIN |
| Consolidado        | `/api/v1/consolidado`          | USER         |
| Notificações       | `/api/v1/notificacoes`         | USER / ADMIN |

Operações de escrita (POST, PUT, PATCH, DELETE) exigem role `ADMIN`.

---

## Regras de negócio principais

- **Registro de coleta** (`POST /coletas`) dispara automaticamente:
  1. Zera o `volumeAtualKg` do ponto de coleta
  2. Atualiza ou cria o registro em `CONSOLIDADO_RECICLAGEM` do mês correspondente
  3. Resolve alertas pendentes do ponto

- **Atualização de volume** (`PATCH /pontos-coleta/{id}/volume`) gera automaticamente um `AlertaCapacidade` se o volume atingir ou ultrapassar 90% da capacidade máxima.

---

## Banco de dados

### Migrações

O Flyway gerencia o esquema automaticamente no profile `dev`. As migrations ficam em `src/main/resources/db/migration/` e são executadas em ordem (`V1`, `V2`, `V3`).

### Zerar o banco (Oracle)

Caso precise resetar completamente o esquema — por exemplo, para reexecutar as migrations do zero — rode o script abaixo no SQL*Plus, SQL Developer ou qualquer cliente Oracle conectado ao schema correto:

```sql
BEGIN
  -- 1. Apaga TODAS as tabelas e suas constraints
  FOR t IN (SELECT table_name FROM user_tables) LOOP
    EXECUTE IMMEDIATE 'DROP TABLE "' || t.table_name || '" CASCADE CONSTRAINTS';
  END LOOP;
  -- 2. Apaga as sequences, ignorando as protegidas pelo sistema
  FOR s IN (SELECT sequence_name FROM user_sequences) LOOP
    BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE "' || s.sequence_name || '"';
    EXCEPTION
      WHEN OTHERS THEN
        IF SQLCODE = -32794 THEN
          NULL;
        ELSE
          RAISE;
        END IF;
    END;
  END LOOP;
END;
/
```

Após executar, reinicie a aplicação com o profile `dev` — o Flyway recria tudo automaticamente.

> **Atenção:** esse script apaga todos os dados e objetos do schema atual. Não execute em ambientes compartilhados sem alinhamento com a equipe.

---

## Pipeline CI/CD

**Ferramenta**: GitHub Actions. Workflow em [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml),
disparado em todo push/PR para `main`.

**Etapas** (jobs, em ordem, cada um dependendo do anterior via `needs:`):

1. **`build-and-test`** — roda em todo push e pull request. Sobe o profile `test` (H2
   in-memory, sem depender do Oracle da FIAP), executa `./mvnw test`, depois empacota o jar
   (`./mvnw package -DskipTests`). Publica os relatórios de teste e o jar como artifacts do
   workflow.
2. **`build-and-push-image`** — só em push na `main`. Autentica no Azure via OIDC (sem secret
   estático de Service Principal), faz login no Azure Container Registry e publica a imagem
   Docker com duas tags (SHA do commit e `latest`). A imagem é construída **uma única vez** e
   reaproveitada nos dois deploys seguintes (build once, deploy twice).
3. **`deploy-staging`** — GitHub Environment `staging`. Deploy da imagem publicada no Azure App
   Service de staging com `SPRING_PROFILES_ACTIVE=staging` (H2 em memória, modo Oracle, Flyway
   roda as mesmas migrations de `db/migration/` — não precisa de credencial de banco nenhuma) e
   faz um smoke test em `GET /actuator/health` antes de considerar o job bem-sucedido.
4. **`deploy-production`** — GitHub Environment `production`, só roda depois que o deploy em
   staging passou. Mesma lógica de deploy + smoke test, apontando para o App Service de
   produção com `SPRING_PROFILES_ACTIVE=prod` e as credenciais reais do Oracle da FIAP
   (`DB_USER`/`DB_PASS`). Este Environment tem um **required reviewer** configurado no GitHub —
   o pipeline continua totalmente automatizado, só pausa esperando uma aprovação manual antes
   de tocar em produção.

**Por que staging e produção usam bancos diferentes**: não existe uma segunda instância Oracle
disponível além da da FIAP (usada em produção), então staging roda em H2 efêmero com o Flyway
ligado — isso já valida que os scripts de migration (`VARCHAR2`, `NUMBER`, `SEQ.NEXTVAL`,
`TO_DATE`, `CHECK`) rodam sem erro antes de tocarem o Oracle real, sem precisar duplicar
credenciais nem arriscar dados de um ambiente vazando pro outro. O efeito colateral aceito é
que o banco de staging reseta a cada deploy/restart (não guarda nada entre execuções).

**Infraestrutura de destino**: Azure Container Registry (imagem) + dois Azure App Service for
Containers, um por ambiente (`staging` e `production`), cada um com suas próprias variáveis de
ambiente configuradas como secrets/variables do respectivo GitHub Environment.

---

## Prints do funcionamento

> Preencher após o primeiro deploy real em staging e produção.

- [ ] Print do job `build-and-test` passando (GitHub Actions).
- [ ] Print do job `build-and-push-image` publicando a imagem no ACR.
- [ ] Print do job `deploy-staging` e do smoke test (`{"status":"UP"}`) contra a URL de staging.
- [ ] Print da aprovação manual do Environment `production` e do `deploy-production` passando.
- [ ] Print do smoke test contra a URL de produção.
- [ ] Print do Swagger UI (`/swagger-ui.html`) funcionando em staging e produção.

---

## Checklist de entrega

| Item | OK |
|---|---|
| Projeto compactado em `.ZIP` com estrutura organizada | ☐ |
| Dockerfile funcional | ☑ |
| `docker-compose.yml` ou arquivos Kubernetes | ☑ |
| Pipeline com etapas de build, teste e deploy | ☑ |
| `README.md` com instruções e prints | ☐ (falta anexar os prints) |
| Documentação técnica com evidências (PDF ou PPT) | ☐ |
| Deploy realizado nos ambientes staging e produção | ☐ (recursos Azure ainda não provisionados) |

---

## Equipe

Projeto desenvolvido para a disciplina de Java Advanced — FIAP 2026.
