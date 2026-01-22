# 📋 Desafio Técnico Fullstack 1 – JTech

> **API RESTful para Gerenciamento de Tarefas (TODO List)**

## 📝 Visão Geral do Projeto

Este projeto é uma solução completa para o Desafio Técnico Fullstack 1 da JTech. Trata-se de uma API RESTful robusta para gerenciamento de tarefas, desenvolvida com foco em **Clean Architecture**, testabilidade e boas práticas de desenvolvimento.

Embora o foco principal seja o backend em Java/Spring Boot, inclui um frontend em Vue.js 3 para demonstrar a integração completa (fullstack) e proporcionar uma melhor experiência de uso.

---

## 🚀 Funcionalidades Principais

- ✅ Criar tarefas com título, descrição e status
- ✅ Listar tarefas com suporte a paginação
- ✅ Buscar tarefa específica por ID
- ✅ Atualizar dados de tarefas existentes
- ✅ Remover tarefas do sistema
- ✅ Documentação interativa via Swagger

---

## 🛠️ Stack Tecnológica

### Backend

| Componente | Tecnologia |
|-----------|-----------|
| Linguagem | Java 21 |
| Framework | Spring Boot 3 |
| Persistência | Spring Data JPA / Hibernate |
| Migrações | Flyway |
| Testes | JUnit 5, Mockito, Spring Boot Test |
| Documentação | Swagger UI (OpenAPI 3) |

### Frontend

| Componente | Tecnologia |
|-----------|-----------|
| Framework | Vue.js 3 (Composition API) |
| Linguagem | TypeScript |
| Gerenciamento de Estado | Pinia |
| Build Tool | Vite |
| Testes | Vitest |

### Infraestrutura

| Componente | Tecnologia |
|-----------|-----------|
| Banco de Dados | PostgreSQL (Docker/Prod) & H2 (Dev/Testes) |
| Proxy/Web Server | Nginx |
| Containerização | Docker & Docker Compose |

---

## 📋 Pré-requisitos

> ⚠️ **Recomendado: Use Docker Compose**
> 
> É a forma mais confiável e requer apenas Docker.

### Para Docker Compose (Recomendado)

- Docker >= 20.10
- Docker Compose >= 2.0

### Para Rodar Backend Local

- Java 21+ (exatamente 21, não versões mais novas como 25)
- Gradle 8.x (vem incluído via `./gradlew`)

### Para Rodar Frontend Local

- Node.js >= 20.19.x ou >= 22.12.x (**NÃO** 18.x)
- npm >= 9.x ou yarn/pnpm equivalente

### Verificar Versões Instaladas

```bash
# Java
java -version
# Deve mostrar: Java 21.x

# Node e npm
node --version
# Deve mostrar: >= 20.19 ou >= 22.12
npm --version
# Deve mostrar: >= 9

# Docker
docker --version
# Deve mostrar: >= 20.10
docker-compose --version
# Deve mostrar: >= 2.0
```

---

## ⚡ Quick Start

### 🐳 Opção 1: Docker Compose (Recomendado)

Para subir todo o ecossistema (Backend, Banco de Dados, Nginx e Frontend) em um único comando:

```bash
docker-compose up -d --build
```

> ℹ️ **Migrations Automáticas**
> 
> As migrations do banco de dados (Flyway) são executadas **automaticamente** quando o backend inicia:
> - `V1__create_tasks.sql` → Cria a tabela `tasks`
> - `V2__seed_tasks.sql` → Popula dados de exemplo (opcional)
> 
> Você **não precisa fazer nada manualmente**. O Spring Boot detecta o perfil `docker` e executa as migrations automaticamente.

**Acessos Disponíveis:**

| Serviço | URL |
|---------|-----|
| Interface Web | http://localhost |
| API (via Proxy) | http://localhost/api/tasks |
| Swagger UI | http://localhost/swagger-ui.html |
| API Direta | http://localhost:8080/tasks |

**Verificar Status do Backend:**

Se o backend não iniciar, verifique os logs:

```bash
# Ver logs do backend
docker logs tasklist-backend

# Ver logs do banco de dados
docker logs tasklist-postgres

# Ver status de todos os containers
docker-compose ps
```

### 💻 Opção 2: Backend Local (Modo H2)

Caso prefira rodar apenas o backend sem Docker (utiliza banco em memória):

```bash
cd jtech-tasklist-backend
./gradlew bootRun
```

---

## 💡 Exemplo de Uso da API

### Criar Tarefa

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Estudar Clean Architecture",
    "description": "Revisar princípios e aplicar no projeto",
    "status": "pendente"
  }'
```

### Listar Tarefas

```bash
curl http://localhost:8080/api/tasks
```

---

## 📁 Estrutura do Projeto

O projeto segue os princípios de **Clean Architecture / Ports & Adapters**:

```
jtech-tasklist-backend/
├── src/main/java/br/com/jtech/tasklist/
│   ├── adapters/
│   │   ├── input/                 # Controllers e DTOs (Entrada)
│   │   └── output/                # Repositories e Gateways (Saída)
│   ├── application/
│   │   ├── core/                  # Regras de Negócio e Casos de Uso
│   │   ├── ports/                 # Interfaces de entrada/saída
│   │   └── config/                # Beans e configurações do Spring
│   └── config/
│       ├── usecases/              # Configuração de UseCases
│       └── infra/                 # Configuração de infraestrutura
└── src/test/                      # Testes Unitários e de Integração

jtech-tasklist-frontend/
├── src/
│   ├── views/                     # Páginas da aplicação
│   ├── stores/                    # Gerenciamento de estado (Pinia)
│   ├── services/                  # Integração com API
│   ├── types/                     # Tipos TypeScript
│   ├── components/                # Componentes Vue reutilizáveis
│   ├── router/                    # Configuração Vue Router
│   └── assets/                    # Estilos CSS
└── public/                        # Arquivos estáticos
```

---

## 🧪 Testes

### Via Docker (Recomendado)

Ambiente isolado, sem dependências locais:

```bash
docker-compose -f docker-compose.tests.yml up --build --exit-code-from test-backend
```

**Isso executa:**
- ✅ Testes unitários do backend (JUnit 5)
- ✅ Testes de integração (Spring Boot Test)
- ✅ Testes unitários do frontend (Vitest)
- ✅ Geração de relatórios de cobertura

### Local - Backend (requer Java 21+)

```bash
cd jtech-tasklist-backend

# Testes unitários
./gradlew test

# Apenas testes de integração
./gradlew test --tests "*Integration*"

# Gerar relatório de cobertura (Jacoco)
./gradlew jacocoTestReport
# Resultado: build/reports/jacoco/test/html/index.html
```

### Local - Frontend (requer Node.js 20.19+)

```bash
cd jtech-tasklist-frontend

# Testes com Vitest
npm run test:unit

# Verificar ESLint
npm run lint

# Validação TypeScript
npm run type-check
```

---

## 🤔 Decisões Técnicas

| Decisão | Justificativa |
|---------|--------------|
| **Clean Architecture** | Garantir que a lógica de negócio seja independente de frameworks externos |
| **Tratamento de Erros RFC 7807** | Retornar respostas padronizadas para erros de validação (400) e recursos não encontrados (404) |
| **Paginação com Pageable** | Evitar problemas de performance com grandes volumes de dados |
| **Dual Database Support** | H2 para desenvolvimento local, PostgreSQL para Docker/produção |
| **Vue 3 + TypeScript** | Frontend type-safe com reatividade moderna e melhor DX |
| **Nginx Proxy** | Simplifica deploy, resolve CORS naturalmente, centraliza portas |

---

## 🐛 Troubleshooting

### ❌ Erro: "Unable to build Hibernate SessionFactory" ou "Missing table 'tasks'"

**Causa:** Migrations do Flyway não foram executadas no banco de dados PostgreSQL

**Solução:**

1. Verifique se o PostgreSQL está saudável:
   ```bash
   docker-compose ps
   # postgres deve estar com status "healthy" antes do backend iniciar
   ```

2. Verifique os logs do backend:
   ```bash
   docker logs tasklist-backend
   ```

3. Se o banco de dados foi criado sem as migrations, você pode:
   ```bash
   # Parar os containers
   docker-compose down
   
   # Remover o volume do banco de dados (⚠️ isso apaga os dados)
   docker volume rm desafio_jtech_postgres_data
   
   # Recriá-lo do zero
   docker-compose up -d --build
   ```

4. Verifique se as migrations estão presentes:
   ```bash
   ls jtech-tasklist-backend/src/main/resources/db/migration/
   # Deve mostrar: V1__create_tasks.sql e V2__seed_tasks.sql
   ```

5. Se ainda não funcionar, verifique o arquivo `application-docker.yml`:
   - Flyway deve estar `enabled: true`
   - Locations deve apontar para `classpath:db/migration`

### ❌ Erro: "Node.js 18.20.8. Vite requires Node.js version 20.19+ or 22.12+"

**Causa:** Node instalado é versão 18, mas Vite requer 20+

**Solução:**
- Atualizar Node.js: https://nodejs.org/
- Ou usar Docker Compose (que já tem versões corretas)
- Ou usar `nvm`:
  ```bash
  nvm install 20
  nvm use 20
  ```

### ❌ Erro: "Gradle build uses Java 21 but found Java 25"

**Causa:** Projeto configurado para Java 21, mas sistema tem versão diferente

**Solução:**
- Instalar Java 21: https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html
- Ou usar Docker Compose (não requer Java local)
- Ou usar `sdkman`:
  ```bash
  sdk install java 21.0.1-tem
  sdk use java 21.0.1-tem
  ```

### ❌ Erro: "Cannot find module @rollup/rollup-darwin-x64"

**Causa:** Dependências npm corrompidas ou incompletas

**Solução:**
```bash
cd jtech-tasklist-frontend
rm -rf node_modules package-lock.json
npm install
```

### ❌ Erro: CORS error ao conectar API do frontend

**Causa:** Frontend tentando acessar backend com URL errada

**Solução:**
- Se usando Docker Compose: URLs já estão corretas
- Se frontend local + backend Docker:
  ```bash
  # Criar .env.local em jtech-tasklist-frontend/
  echo "VITE_API_URL=http://localhost:8080" > jtech-tasklist-frontend/.env.local
  
  # Reiniciar npm run dev
  ```

### ❌ Erro: Docker Compose não inicia

**Causa:** Portas 80, 8080 ou 5432 já em uso

**Solução:**
```bash
# Ver o que está usando as portas
lsof -i :80
lsof -i :8080
lsof -i :5432

# Ou parar todos os containers
docker-compose down
docker ps  # Verificar se realmente parou
```

### ❌ Erro: Frontend mostra página em branco

**Causa:** Build não foi executado ou Nginx não encontra arquivos

**Solução:**
```bash
cd jtech-tasklist-frontend
npm run build  # Gerar arquivos em dist/

# Reiniciar Docker Compose
docker-compose down && docker-compose up --build
```

---

## 🎯 Melhorias Futuras

- [ ] Implementação de Autenticação JWT
- [ ] Suporte a PATCH para atualizações parciais
- [ ] Categorização de tarefas com Tags
- [ ] Dashboards de produtividade no Frontend
- [ ] Cache com Redis
- [ ] WebSocket para real-time updates

---

## 📄 Notas Finais

> ℹ️ Este projeto foi desenvolvido para fins avaliativos do Desafio Técnico Fullstack 1 da JTech.
> 
> O frontend é um complemento opcional para demonstrar habilidades fullstack completas.
