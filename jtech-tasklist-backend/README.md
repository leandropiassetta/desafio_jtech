![Jtech Logo](http://www.jtech.com.br/wp-content/uploads/2015/06/logo.png)

# JTech Task List API

REST API completa para gerenciamento de tarefas (TODO List) desenvolvida em Java com Spring Boot, seguindo princípios de arquitetura limpa e boas práticas de desenvolvimento.

## 📋 Visão Geral

Uma API RESTful robusta que implementa um sistema completo de gerenciamento de tarefas com suporte a CRUD, paginação, filtros, validações e tratamento de erros em conformidade com a RFC 7807 (Problem Details).

## 🛠️ Stack Tecnológica

### Backend
- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.5** - Framework web
- **Spring Data JPA** - Persistência de dados
- **Hibernate** - ORM
- **Flyway** - Migrations de banco de dados
- **SpringDoc OpenAPI 2.0.4** - Documentação interativa da API
- **H2 Database** - Banco padrão (em memória)
- **PostgreSQL 15** - Banco opcional (perfil docker)
- **JUnit 5 + Mockito** - Testes automatizados

## 🏗️ Arquitetura

O projeto segue o padrão de **Arquitetura em Camadas (Clean Architecture)** com separação clara de responsabilidades:

```
br.com.jtech.tasklist
├── application/
│   ├── core/
│   │   ├── domains/          # Entidades de negócio (Task, TaskStatus)
│   │   └── usecases/         # Lógica de negócio (CreateTaskUseCase, etc)
│   └── ports/
│       ├── input/            # Interfaces de entrada
│       └── output/           # Interfaces de saída (TaskRepositoryPort)
├── adapters/
│   ├── input/
│   │   ├── controllers/      # REST Controllers (TaskController)
│   │   └── protocols/        # DTOs (TaskRequest, TaskResponse)
│   └── output/
│       └── repositories/     # JPA Entities, Spring Data Repositories, Adapters
└── config/
    ├── usecases/             # Configuração de beans dos UseCases
    └── infra/
        ├── cors/             # Configuração CORS
        ├── handlers/         # GlobalExceptionHandler
        ├── exceptions/       # Exceções customizadas (ApiError, ApiValidationError)
        └── swagger/          # OpenAPI 3.0 Configuration
```

### Padrões Implementados

- **Ports & Adapters**: Inversão de dependência via `TaskRepositoryPort`
- **Use Case Pattern**: Lógica isolada em classes de caso de uso
- **DTO Pattern**: Separação entre modelos de domínio e comunicação
- **Adapter Pattern**: Conversão entre `Task` (domain) e `TaskEntity` (persistência)

## 🚀 Como Rodar Localmente

### Pré-requisitos
- Java 21+
- Maven ou Gradle 8.14+
- Git

### Instalação e Execução (H2 - Padrão)

1. **Clonar repositório**
```bash
git clone <repo-url>
cd jtech-tasklist-backend
```

2. **Compilar projeto**
```bash
./gradlew clean build -x test
```

3. **Rodar aplicação**
```bash
./gradlew bootRun
```

A API será iniciada em **http://localhost:8080**

### Acessar Swagger/OpenAPI
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Acessar Console H2
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User**: `sa`
- **Password**: (deixar em branco)

---

## 🐳 Como Rodar com Docker (PostgreSQL)

### Pré-requisitos
- Docker e Docker Compose instalados

### Executar Stack Completa

```bash
cd composer/
docker-compose up -d
```

Isso iniciará:
- **Backend** em http://localhost:8080 (com perfil `docker`)
- **PostgreSQL** em `localhost:5432`
- **Banco**: `tasklist_db` / User: `postgres` / Pass: `postgres`

### Parar containers
```bash
docker-compose down
```

### Verificar logs
```bash
docker-compose logs -f backend
docker-compose logs -f postgres
```

---

## 🧪 Como Rodar Testes

### Testes Unitários
```bash
./gradlew test
```

### Testes com Cobertura (Jacoco)
```bash
./gradlew test jacocoTestReport
# Relatório em: build/reports/jacoco/test/html/index.html
```

### Testes Específicos
```bash
./gradlew test --tests CreateTaskUseCaseTest
./gradlew test --tests TaskControllerTest
```

---

## 📡 API Endpoints

### 1. Criar Tarefa
```http
POST /tasks
Content-Type: application/json

{
  "title": "Comprar leite",
  "description": "Ir ao mercado",
  "status": "PENDING"
}
```
**Response**: `201 Created`

### 2. Listar Tarefas
```http
GET /tasks?page=0&size=10&sort=id,desc&status=PENDING
```
**Query Params**:
- `page`: número da página (0-indexed)
- `size`: tamanho da página
- `sort`: ordenação (ex: `id,desc`)
- `status`: filtro por status (PENDING ou DONE, opcional)

**Response**: `200 OK` - Retorna `Page<TaskResponse>`

### 3. Obter Tarefa por ID
```http
GET /tasks/1
```
**Response**: `200 OK` ou `404 Not Found`

### 4. Atualizar Tarefa (Full Update)
```http
PUT /tasks/1
Content-Type: application/json

{
  "title": "Comprar leite e pão",
  "description": "Ir ao mercado",
  "status": "DONE"
}
```
**Obs**: `title` e `status` são obrigatórios; `description` é opcional e pode ser `null`.

**Response**: `200 OK`, `400 Bad Request`, ou `404 Not Found`

### 5. Deletar Tarefa
```http
DELETE /tasks/1
```
**Response**: `204 No Content` ou `404 Not Found`

---

## ❌ Tratamento de Erros

### Formato Problem Details (RFC 7807)

**404 Not Found**
```json
{
  "status": 404,
  "message": "Task not found",
  "timestamp": "2026-01-20 10:30:45",
  "debugMessage": "Task with id 999 was not found"
}
```

**400 Bad Request - Validação**
```json
{
  "status": 400,
  "message": "Validation error",
  "timestamp": "2026-01-20 10:30:45",
  "subErrors": [
    {
      "object": "taskRequest",
      "field": "title",
      "rejectedValue": "",
      "message": "Title is required"
    },
    {
      "object": "taskRequest",
      "field": "title",
      "rejectedValue": "texto com mais de 120 caracteres...",
      "message": "Title must not exceed 120 characters"
    }
  ]
}
```

---

## 📦 Estrutura de Pastas

```
jtech-tasklist-backend/
├── src/
│   ├── main/
│   │   ├── java/br/com/jtech/tasklist/
│   │   │   ├── StartTasklist.java           # Classe principal
│   │   │   ├── application/                 # Domínio e UseCases
│   │   │   ├── adapters/                    # Controllers, DTOs, Repositories
│   │   │   └── config/                      # Configurações
│   │   └── resources/
│   │       ├── application.yml              # Config H2 padrão
│   │       ├── application-docker.yml       # Config PostgreSQL
│   │       ├── db/migration/
│   │       │   ├── V1__create_tasks.sql    # Schema
│   │       │   └── V2__seed_tasks.sql      # Dados iniciais
│   │       └── banner.txt
│   └── test/
│       ├── java/.../usecases/               # Testes UseCase
│       ├── java/.../controllers/            # Testes Controller
│       └── resources/application-test.properties
├── composer/
│   └── docker-compose.yml                   # Stack Docker
├── Dockerfile                               # Build multi-stage
├── build.gradle                             # Dependências Gradle
└── README.md
```

---

## 🔐 Segurança

### CORS Configurado
- **Origem permitida**: `http://localhost:5173` (Frontend Vue)
- **Métodos**: GET, POST, PUT, DELETE, OPTIONS
- **Headers**: Aceita todos (`*`)
- **Credentials**: Habilitados
- **Max Age**: 3600 segundos

### Validações Aplicadas
- `@NotBlank` em título (obrigatório)
- `@Size` em title (máx 120) e description (máx 1000)
- Validação em UseCases (status default PENDING)
- GlobalExceptionHandler centralizado

---

## 🔧 Decisões Técnicas

### 1. **H2 como Padrão, PostgreSQL Opcional**
- H2 oferece zero setup para desenvolvimento local
- PostgreSQL ativado via profile `docker` para cenários reais
- Flyway gerencia schema em ambos

### 2. **ID Long em vez de UUID**
- Performance em queries com índices
- Simplicidade na REST API
- Identity/Auto-increment nativo em ambos drivers

### 3. **Status Enum em vez de String**
- Type safety em Java
- Validação automática de valores
- Reduz erros em runtime

### 4. **PUT para Update Completo (não PATCH)**
- Requisição sempre contém título + status
- Descrição opcional, pode ser null
- Simplifica cliente e validações

### 5. **Flyway Migrations**
- V1: Schema inicial com DDL
- V2: Seed de dados para testes
- SQL compatible com H2 e PostgreSQL

### 6. **Global Exception Handler**
- Centraliza tratamento de erros
- RFC 7807 Problem Details
- Sub-erros de validação estruturados

---

## 🚀 Melhorias Futuras

1. **Autenticação & Autorização**
   - JWT Bearer Token
   - Spring Security
   - Roles (USER, ADMIN)

2. **Buscas Avançadas**
   - Full-text search no título/descrição
   - Filtros por data range (createdAt, updatedAt)
   - Suporte a Criteria Builder ou QueryDSL

3. **Cache**
   - Redis para tarefas frequentes
   - Cache-busting com eventos Kafka

4. **Eventos & Auditoria**
   - Event Sourcing para mudanças
   - Registro de quem criou/editou
   - Soft delete com timestamp

5. **Observabilidade**
   - Metrics com Micrometer
   - Distributed Tracing (Jaeger/Zipkin)
   - Logs estruturados JSON

6. **Testes de Integração**
   - TestContainers para PostgreSQL real
   - Test fixtures e builders
   - Mutation testing (PIT)

7. **API Versioning**
   - Suporte a múltiplas versões (`/v2/tasks`)
   - Migration path para clientes

8. **Rate Limiting & Throttling**
   - Proteção contra abuso
   - Algoritmo Token Bucket

---

## 📝 Padrões de Commit

```
feat: adiciona endpoint POST /tasks
fix: corrige validação de title em TaskRequest
test: adiciona testes para CreateTaskUseCase
chore: atualiza versão Flyway em build.gradle
docs: atualiza README com instruções Docker
```

---

## 👥 Contribuindo

1. Crie branch: `git checkout -b feature/nova-funcionalidade`
2. Commit mudanças: `git commit -m 'feat: descrição'`
3. Push: `git push origin feature/nova-funcionalidade`
4. Abra Pull Request

---

## 📄 Licença

Proprietary - JTech Soluções em Informática

---

## 📞 Suporte

Para dúvidas ou issues, abra uma issue no repositório ou contate `dev-team@jtech.com.br`

---

**Desenvolvido com ❤️ por JTech | 2026**
