# JTech TaskList - Frontend

Interface web moderna para gerenciamento de tarefas (TODO List) desenvolvida com Vue 3, TypeScript e Vite.

## 📋 Visão Geral

Este é o frontend da aplicação TODO List da JTech. Oferece uma interface intuitiva para criar, listar, atualizar e deletar tarefas, com suporte a paginação, filtros por status e integração completa com a API RESTful backend.

**Funcionalidades principais:**
- ✅ Criar, ler, atualizar e deletar tarefas
- 📄 Paginação de tarefas
- 🔍 Filtrar tarefas por status (Pendente/Concluída)
- 🎨 Interface responsiva e moderna
- 🔄 Sincronização em tempo real com backend
- ♿ Type-safe com TypeScript completo

## 🛠️ Stack Utilizada

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| **Vue** | 3.x | Framework UI reativo |
| **TypeScript** | 5.x | Type-safety e melhor DX |
| **Vite** | 5.x | Build tool rápido |
| **Pinia** | 2.x | Gerenciamento de estado |
| **Axios** | 1.x | Cliente HTTP |
| **Vitest** | 1.x | Testes unitários |
| **ESLint** | 9.x | Linting de código |
| **Nginx** | latest | Proxy reverso em produção |

## 📦 Pré-requisitos

- **Node.js** >= 18.x
- **npm** >= 9.x (ou yarn/pnpm)
- Backend JTech TaskList rodando (ver instruções no [backend README](../jtech-tasklist-backend/README.md))

## 🚀 Como Rodar Localmente

### 1. Instalação de Dependências

```bash
cd jtech-tasklist-frontend
npm install
```

### 2. Configurar Variáveis de Ambiente

Crie um arquivo `.env.local` na raiz do projeto:

```env
# URL da API backend (padrão: http://localhost:8080)
VITE_API_URL=http://localhost:8080
```

Se não definir, o frontend usará caminho relativo `/api` (útil para proxy via Nginx).

### 3. Iniciar Servidor de Desenvolvimento

```bash
npm run dev
```

Acesse em `http://localhost:5173`

**Nota:** O backend deve estar rodando em `http://localhost:8080` (conforme configurado em `.env.local`).

## 🧪 Como Rodar os Testes

### Testes Unitários

```bash
npm run test:unit
```

Executa todos os testes com Vitest e gera relatório de cobertura.

**Testes cobertos:**
- `src/services/__tests__/api.test.ts` - Cliente HTTP e interceptadores
- `src/stores/__tests__/taskStore.test.ts` - Gerenciamento de estado Pinia
- `src/types/__tests__/task.test.ts` - Validação de tipos

### Linting de Código

```bash
npm run lint
```

Verifica conformidade com ESLint usando configuração projeto.

### Build para Produção

```bash
npm run build
```

Cria otimizado em `dist/` pronto para deploy.

## 📁 Estrutura de Pastas

```
jtech-tasklist-frontend/
├── src/
│   ├── views/                      # Páginas da aplicação
│   │   ├── TasksView.vue          # Gerenciador principal de tarefas
│   │   ├── HomeView.vue           # Página inicial
│   │   └── AboutView.vue          # Página sobre (links para docs)
│   │
│   ├── stores/                    # Gerenciamento de estado (Pinia)
│   │   ├── taskStore.ts           # Store com ações CRUD
│   │   └── __tests__/
│   │       └── taskStore.test.ts  # Testes da store
│   │
│   ├── services/                  # Camada de integração com API
│   │   ├── api.ts                 # Cliente Axios com configuração
│   │   └── __tests__/
│   │       └── api.test.ts        # Testes do cliente HTTP
│   │
│   ├── types/                     # Tipos TypeScript
│   │   ├── task.ts                # Interfaces Task, TaskStatus
│   │   └── __tests__/
│   │       └── task.test.ts       # Testes de tipos
│   │
│   ├── components/                # Componentes Vue reutilizáveis
│   │   ├── HelloWorld.vue
│   │   ├── TheWelcome.vue
│   │   ├── WelcomeItem.vue
│   │   └── icons/
│   │
│   ├── router/                    # Configuração Vue Router
│   │   └── index.ts
│   │
│   ├── assets/                    # Arquivos estáticos (CSS)
│   │   ├── base.css
│   │   └── main.css
│   │
│   ├── App.vue                    # Componente raiz
│   └── main.ts                    # Entry point
│
├── public/                        # Arquivos públicos (favicon, etc)
│
├── nginx.conf                     # Configuração Nginx para produção
├── Dockerfile                     # Build em multi-stage
├── package.json
├── vite.config.ts                 # Configuração Vite
├── vitest.config.ts               # Configuração Vitest
├── tsconfig.json                  # Configuração TypeScript
├── eslint.config.ts               # Configuração ESLint
└── README.md                      # Este arquivo
```

## 🔌 Integração com API Backend

### Cliente HTTP (api.ts)

```typescript
import axios from 'axios'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' }
})
```

**Interceptadores Implementados:**
- **Response**: Converte status em Português ("pendente"/"concluída") → Inglês ("PENDING"/"DONE")
- **Error**: Extrai mensagens de erro do backend (RFC 7807)

### Exemplo de Uso

```typescript
import { useTaskStore } from '@/stores/taskStore'

export default {
  setup() {
    const taskStore = useTaskStore()
    
    onMounted(() => {
      taskStore.fetchTasks() // GET /tasks
    })
    
    const createNewTask = async () => {
      await taskStore.createTask({
        title: 'Nova Tarefa',
        description: 'Descrição opcional',
        status: 'PENDING'
      })
    }
    
    return { taskStore, createNewTask }
  }
}
```

### Endpoints Disponíveis

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| **POST** | `/api/tasks` | Criar nova tarefa |
| **GET** | `/api/tasks` | Listar tarefas (com paginação) |
| **GET** | `/api/tasks/{id}` | Obter tarefa por ID |
| **PUT** | `/api/tasks/{id}` | Atualizar tarefa |
| **DELETE** | `/api/tasks/{id}` | Deletar tarefa |

Para documentação completa da API, acesse Swagger: `http://localhost:8080/swagger-ui.html`

## 💾 Gerenciamento de Estado (Pinia)

### TaskStore

```typescript
const taskStore = useTaskStore()

// State
taskStore.tasks              // Array de tarefas
taskStore.loading            // Status de carregamento
taskStore.error              // Mensagem de erro
taskStore.currentPage        // Página atual (0-indexed)
taskStore.pageSize           // Itens por página
taskStore.totalPages         // Total de páginas
taskStore.selectedStatus     // Filtro de status

// Actions
await taskStore.fetchTasks()           // Buscar tarefas
await taskStore.createTask(task)       // Criar tarefa
await taskStore.updateTask(id, task)   // Atualizar tarefa
await taskStore.deleteTask(id)         // Deletar tarefa
taskStore.nextPage()                   // Próxima página
taskStore.previousPage()               // Página anterior

// Computed
taskStore.isEmpty            // boolean
taskStore.canNextPage        // boolean
taskStore.canPreviousPage    // boolean
```

## 🔄 Cenários de Deployment

### 1. Docker Compose (Recomendado para Desenvolvimento)

```bash
docker-compose up
```

- Frontend: `http://localhost` (via Nginx)
- Backend: `http://localhost:8080` (interno)
- API proxy: `/api` → backend

### 2. Frontend Local + Backend Docker

```bash
# Terminal 1: Backend
cd jtech-tasklist-backend
docker-compose up

# Terminal 2: Frontend
cd jtech-tasklist-frontend
npm install && npm run dev
```

Acesse: `http://localhost:5173`

### 3. Frontend Docker + Backend Local

```bash
# Terminal 1: Backend
cd jtech-tasklist-backend
./gradlew bootRun

# Terminal 2: Frontend
cd jtech-tasklist-frontend
docker build -t jtech-frontend .
docker run -p 80:80 jtech-frontend
```

Acesse: `http://localhost`

### 4. Tudo Local

```bash
# Terminal 1: Backend
cd jtech-tasklist-backend
./gradlew bootRun

# Terminal 2: Frontend
cd jtech-tasklist-frontend
npm install && npm run dev
```

Acesse: `http://localhost:5173`

## 🛠️ Decisões Técnicas

### 1. Por que Vue 3 + Composition API?

Vue 3 oferece reatividade moderna, melhor type-safety com TypeScript e menor bundle size comparado a React/Angular. Composition API proporciona melhor organização de lógica reutilizável.

### 2. Por que Pinia em vez de Vuex?

Pinia é recomendado no Vue 3 (sucessor oficial do Vuex), oferece melhor DX, menor bundle, e API mais intuitiva com composables.

### 3. Por que Vite em vez de Vue CLI?

Vite oferece HMR (Hot Module Replacement) instantâneo, build 10-100x mais rápido e configuração mais simples. É o padrão moderno para projetos Vue.

### 4. Por que Axios?

Axios oferece boa cobertura de recursos (interceptadores, timeouts, cancelamento) com API simples e bem documentada. Alternativa: Fetch API nativa (mais leve mas menos recursos).

### 5. Conversão de Status

Backend usa português ("pendente"/"concluída"), frontend usa inglês ("PENDING"/"DONE"). Interceptadores do Axios fazem conversão automática para melhor UX internacionalizado.

## 🐛 Troubleshooting

### Problema: CORS Error ao conectar no backend

**Sintoma:** `Access to XMLHttpRequest at 'http://localhost:8080' has been blocked by CORS policy`

**Solução:**
1. Verificar se backend está rodando: `http://localhost:8080/swagger-ui.html`
2. Verificar `VITE_API_URL` em `.env.local`
3. Se usar Docker, certificar que `docker-compose` está rodando
4. Verificar configuração CORS em `application.yml` do backend

### Problema: Página em branco após deploy

**Sintoma:** `http://localhost` abre mas exibe branco, console sem erros

**Solução:**
1. Verificar se Nginx está respondendo: `curl -I http://localhost`
2. Verificar logs do Nginx: `docker-compose logs nginx`
3. Certificar que `npm run build` foi executado e `dist/` existe
4. Verificar configuração de `base` em `vite.config.ts`

### Problema: Testes falham com "Cannot find module"

**Sintoma:** Vitest error ao rodar `npm run test:unit`

**Solução:**
1. Certificar que `npm install` foi executado
2. Limpar cache: `npm run test:unit -- --clearCache`
3. Verificar `vitest.config.ts` e caminhos de alias

### Problema: TypeScript erros em desenvolvimento

**Sintoma:** Erros de tipo em `.vue` files mesmo com Volar instalado

**Solução:**
1. Instalar Volar: `code --install-extension Vue.volar`
2. Desabilitar Vetur conflitante
3. Executar `npm run type-check` para validação completa
4. Reiniciar VSCode

## 📈 Melhorias Futuras

- [ ] **Autenticação** - Suporte a login/JWT tokens
- [ ] **Categorias** - Organizar tarefas por categoria/projeto
- [ ] **Tags** - Sistema de etiquetas personalizadas
- [ ] **Prioridade** - Campo de prioridade (Alta/Média/Baixa)
- [ ] **Datas** - Data de vencimento e lembretes
- [ ] **Anexos** - Suporte a upload de arquivos
- [ ] **Temas** - Suporte a dark mode
- [ ] **Offline** - Service Workers para funcionalidade offline
- [ ] **Compartilhamento** - Compartilhar tarefas entre usuários
- [ ] **Analytics** - Dashboard com estatísticas

## 👥 Contribuindo

Para contribuir ao projeto, siga as convenções estabelecidas:
- Use TypeScript strict mode
- Escreva testes para novas funcionalidades
- Siga ESLint rules (execute `npm run lint`)
- Commits descritivos e convencionais

## 📄 Licença

Este projeto é parte do desafio técnico JTech e segue os termos definidos pela empresa.

## 📞 Suporte

Para dúvidas ou problemas:
1. Consulte o [backend README](../jtech-tasklist-backend/README.md)
2. Verifique [Troubleshooting](#-troubleshooting)
3. Acesse Swagger da API: `http://localhost:8080/swagger-ui.html`
