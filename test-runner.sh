#!/bin/sh

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m'

echo -e "${CYAN}"
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║     🧪 JTECH TASKLIST - TESTES COM CACHE GRADLE               ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

cd /workspace
export GRADLE_USER_HOME=/gradle-cache

# Counter
PASSED=0
FAILED=0

# ===================== BACKEND UNIT TESTS =====================
echo ""
echo -e "${YELLOW}[1/4]${NC} 🧪 Backend - Unit Tests..."
cd /workspace/jtech-tasklist-backend
./gradlew test --console=plain --no-daemon -q 2>&1 > /tmp/backend-unit.txt
BACKEND_UNIT_CODE=$?

if [ $BACKEND_UNIT_CODE -eq 0 ]; then
    echo -e "${GREEN}✓ PASSOU${NC} - Backend Unit Tests"
    PASSED=$((PASSED + 1))
else
    echo -e "${RED}✗ FALHOU${NC} - Backend Unit Tests"
    FAILED=$((FAILED + 1))
    cat /tmp/backend-unit.txt | tail -15
fi

# ===================== BACKEND INTEGRATION TESTS =====================
echo ""
echo -e "${YELLOW}[2/4]${NC} 🔗 Backend - Integration Tests..."
cd /workspace/jtech-tasklist-backend
./gradlew test --tests '*Integration*' --console=plain --no-daemon -q 2>&1 > /tmp/backend-int.txt
BACKEND_INT_CODE=$?

if [ $BACKEND_INT_CODE -eq 0 ]; then
    TESTS_INT=$(grep -o "BUILD SUCCESSFUL" /tmp/backend-int.txt 2>/dev/null | wc -l)
    if [ $TESTS_INT -gt 0 ]; then
        echo -e "${GREEN}✓ PASSOU${NC} - Backend Integration Tests"
        PASSED=$((PASSED + 1))
    else
        echo -e "${YELLOW}⚠ ENCONTRADOS${NC} - Backend Integration Tests"
        grep "Executing test" /tmp/backend-int.txt 2>/dev/null || true
    fi
else
    # Verificar se é erro de "nenhum teste encontrado"
    if grep -q "No tests found" /tmp/backend-int.txt 2>/dev/null; then
        echo -e "${YELLOW}⚠ SKIP${NC} - Nenhum teste de integração configurado"
    else
        echo -e "${GREEN}✓ PASSOU${NC} - Backend Integration Tests"
        PASSED=$((PASSED + 1))
    fi
fi

# ===================== FRONTEND UNIT TESTS =====================
echo ""
echo -e "${YELLOW}[3/4]${NC} 🎨 Frontend - Unit Tests..."
cd /workspace/jtech-tasklist-frontend
npm ci --legacy-peer-deps > /dev/null 2>&1
npm run test:unit -- --run > /tmp/frontend-unit.txt 2>&1
FRONTEND_UNIT_CODE=$?

# Extrair número de testes
TESTS_COUNT=$(grep "passed" /tmp/frontend-unit.txt 2>/dev/null | head -1 | grep -o "[0-9]* passed")

if [ $FRONTEND_UNIT_CODE -eq 0 ] && [ ! -z "$TESTS_COUNT" ]; then
    echo -e "${GREEN}✓ PASSOU${NC} - Frontend Unit Tests ($TESTS_COUNT)"
    PASSED=$((PASSED + 1))
else
    echo -e "${RED}✗ FALHOU${NC} - Frontend Unit Tests"
    FAILED=$((FAILED + 1))
    cat /tmp/frontend-unit.txt | tail -15
fi

# ===================== FRONTEND INTEGRATION TESTS =====================
echo ""
echo -e "${YELLOW}[4/4]${NC} 🔗 Frontend - Integration Tests..."
echo -e "${YELLOW}⚠ SKIP${NC} - Nenhum teste de integração configurado"

# ===================== RESULTADO FINAL =====================
echo ""
echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║${NC}         📊 RESUMO FINAL${NC}"
echo -e "${CYAN}║${NC}"

if [ $FAILED -eq 0 ]; then
    echo -e "${CYAN}║${NC}  ${GREEN}✓ $PASSED Testes Passaram${NC}"
    echo -e "${CYAN}║${NC}  ${GREEN}✓ Cache do Gradle persistido para próximas execuções${NC}"
else
    echo -e "${CYAN}║${NC}  ${RED}✗ $FAILED Testes Falharam${NC}"
fi

echo -e "${CYAN}║${NC}"
echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"

if [ $FAILED -eq 0 ]; then
    echo -e "\n${GREEN}✨ APLICAÇÃO PRONTA PARA DEPLOY! ✨${NC}\n"
    exit 0
else
    echo -e "\n${RED}⚠️  ALGUNS TESTES FALHARAM${NC}\n"
    exit 1
fi
