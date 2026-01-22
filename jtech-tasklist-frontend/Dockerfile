# Stage 1: Build
FROM node:22-alpine AS builder

WORKDIR /app

# Copiar apenas package*.json para aproveitar cache do Docker
COPY package*.json ./

# Instalar dependências
RUN npm ci

# Copiar código-fonte
COPY . .

# Build da aplicação
RUN npm run build

# Stage 2: Runtime
FROM nginx:alpine

# Copiar configuração nginx customizada
COPY nginx.conf /etc/nginx/nginx.conf

# Copiar arquivos built do stage anterior
COPY --from=builder /app/dist /usr/share/nginx/html

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:80/ || exit 1

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
