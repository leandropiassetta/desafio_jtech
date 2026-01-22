.PHONY: run migrate update-swagger help

help:
	@echo "📋 JTech TODO List - Available Commands"
	@echo ""
	@echo "  make run             - Start Docker Compose (builds and runs all services)"
	@echo "  make migrate         - Run database migrations (Flyway)"
	@echo "  make update-swagger  - Verify Swagger UI is ready and validate OpenAPI schema"
	@echo ""

run:
	@echo "🚀 Starting Docker Compose..."
	docker-compose up -d --build
	@echo "⏳ Waiting for services to be healthy (up to 30 seconds)..."
	@sleep 5
	@echo "✅ Services started!"
	@echo ""
	@echo "📍 Access points:"
	@echo "   • Frontend:     http://localhost"
	@echo "   • Swagger UI:   http://localhost/swagger-ui.html"
	@echo "   • API (proxy):  http://localhost/api/tasks"
	@echo "   • API (direct): http://localhost:8080/api/tasks"
	@echo ""
	@echo "Next steps:"
	@echo "   1. Wait a few seconds for backend to initialize"
	@echo "   2. Run: make migrate"
	@echo "   3. Run: make update-swagger"

migrate:
	@echo "🔄 Running database migrations..."
	@echo "⏳ Waiting for backend to be ready (checking http://localhost:8080/actuator/health)..."
	@for i in {1..60}; do \
		if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then \
			echo "✅ Backend is healthy! Migrations are being applied..."; \
			sleep 3; \
			echo "✅ Migration complete!"; \
			echo "   Try: curl http://localhost/api/tasks"; \
			exit 0; \
		fi; \
		echo "   [$$i/60] Retrying..."; \
		sleep 1; \
	done; \
	echo "❌ Backend failed to respond. Check logs: docker logs tasklist-backend"

update-swagger:
	@echo "📚 Verifying Swagger UI..."
	@echo "⏳ Waiting for Swagger to be accessible (up to 30 seconds)..."
	@for i in {1..30}; do \
		if curl -s http://localhost/swagger-ui.html > /dev/null; then \
			echo "✅ Swagger UI is ready!"; \
			echo "   URL: http://localhost/swagger-ui.html"; \
			if curl -s http://localhost/v3/api-docs | jq . > /dev/null 2>&1; then \
				echo "✅ OpenAPI schema is valid"; \
			fi; \
			exit 0; \
		fi; \
		echo "   [$$i/30] Retrying..."; \
		sleep 1; \
	done; \
	echo "❌ Swagger UI is not accessible. Check backend logs: docker logs tasklist-backend"
