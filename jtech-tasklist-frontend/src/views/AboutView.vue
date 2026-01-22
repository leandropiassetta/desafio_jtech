<template>
  <main class="about">
    <article class="about-content">
      <h1 class="about-title">Sobre o Desafio</h1>

      <section class="about-section">
        <h2 class="section-title">📋 O Projeto</h2>
        <p class="section-text">
          TaskList é uma aplicação full-stack desenvolvida como desafio técnico da JTech. O objetivo é demonstrar
          habilidades em desenvolvimento web moderno, arquitetura limpa, testes e boas práticas de engenharia de software.
        </p>
      </section>

      <section class="about-section">
        <h2 class="section-title">🛠️ Stack Tecnológico</h2>
        <div class="stack-grid">
          <div class="stack-item">
            <div class="stack-icon">🎨</div>
            <div class="stack-details">
              <h3>Frontend</h3>
              <p>Vue 3 • TypeScript • Pinia • Vite</p>
            </div>
          </div>
          <div class="stack-item">
            <div class="stack-icon">⚙️</div>
            <div class="stack-details">
              <h3>Backend</h3>
              <p>Spring Boot • Java • Gradle • PostgreSQL</p>
            </div>
          </div>
          <div class="stack-item">
            <div class="stack-icon">📦</div>
            <div class="stack-details">
              <h3>DevOps</h3>
              <p>Docker • Docker Compose • CI/CD</p>
            </div>
          </div>
          <div class="stack-item">
            <div class="stack-icon">✅</div>
            <div class="stack-details">
              <h3>Qualidade</h3>
              <p>JUnit • Jacoco • ESLint • Vitest</p>
            </div>
          </div>
        </div>
      </section>

      <section class="about-section">
        <h2 class="section-title">📚 Documentação</h2>
        <p class="section-text">
          A API backend foi documentada com Swagger (OpenAPI) e está disponível em:
        </p>
        <a href="http://localhost:8080/swagger-ui.html" target="_blank" rel="noopener noreferrer" class="doc-link">
          Swagger UI →
        </a>
      </section>

      <footer class="about-footer">
        <p>Desenvolvido com ❤️ como desafio técnico</p>
        <p class="footer-date">2026</p>
      </footer>
    </article>
  </main>
</template>

<style scoped>
.about {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  padding: var(--ds-space-lg);
}

.about-content {
  max-width: 800px;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: var(--ds-space-xl);
}

.about-title {
  font-size: var(--ds-font-size-3xl);
  font-weight: var(--ds-font-weight-bold);
  color: var(--ds-color-text-primary);
  margin: 0 0 var(--ds-space-lg) 0;
  line-height: 1.2;
}

.about-section {
  display: flex;
  flex-direction: column;
  gap: var(--ds-space-md);
  padding: var(--ds-space-lg);
  background-color: var(--ds-color-bg-secondary);
  border-radius: var(--ds-radius-lg);
  border: 1px solid var(--ds-color-border);
}

.section-title {
  font-size: var(--ds-font-size-xl);
  font-weight: var(--ds-font-weight-semibold);
  color: var(--ds-color-text-primary);
  margin: 0 0 var(--ds-space-sm) 0;
}

.section-text {
  font-size: var(--ds-font-size-base);
  color: var(--ds-color-text-secondary);
  line-height: 1.6;
  margin: 0;
}

.stack-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--ds-space-md);
  margin-top: var(--ds-space-md);
}

.stack-item {
  display: flex;
  gap: var(--ds-space-md);
  padding: var(--ds-space-md);
  background-color: var(--ds-color-bg-tertiary);
  border-radius: var(--ds-radius-md);
  border: 1px solid var(--ds-color-border);
}

.stack-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.stack-details h3 {
  font-size: var(--ds-font-size-base);
  font-weight: var(--ds-font-weight-semibold);
  color: var(--ds-color-text-primary);
  margin: 0 0 var(--ds-space-xs) 0;
}

.stack-details p {
  font-size: var(--ds-font-size-sm);
  color: var(--ds-color-text-secondary);
  margin: 0;
}

.doc-link {
  display: inline-flex;
  align-items: center;
  gap: var(--ds-space-sm);
  padding: var(--ds-space-sm) var(--ds-space-md);
  background-color: var(--ds-color-primary);
  color: white;
  text-decoration: none;
  border-radius: var(--ds-radius-md);
  font-weight: var(--ds-font-weight-semibold);
  transition: all var(--ds-transition-base) ease-in-out;
  width: fit-content;
}

.doc-link:hover {
  background-color: var(--ds-color-primary-dark);
  transform: translateY(-2px);
  box-shadow: var(--ds-shadow-md);
}

.doc-link:active {
  transform: translateY(0);
}

.features-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--ds-space-sm);
}

.features-list li {
  font-size: var(--ds-font-size-base);
  color: var(--ds-color-text-secondary);
  line-height: 1.6;
}

.code-block {
  background-color: var(--ds-color-bg-tertiary);
  border-radius: var(--ds-radius-md);
  padding: var(--ds-space-md);
  overflow-x: auto;
  border: 1px solid var(--ds-color-border);
}

.code-block pre {
  margin: 0;
  font-family: 'Courier New', monospace;
  font-size: var(--ds-font-size-sm);
  color: var(--ds-color-text-secondary);
  line-height: 1.5;
}

.about-footer {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--ds-space-xs);
  padding-top: var(--ds-space-lg);
  border-top: 1px solid var(--ds-color-border);
  text-align: center;
}

.about-footer p {
  font-size: var(--ds-font-size-sm);
  color: var(--ds-color-text-tertiary);
  margin: 0;
}

.footer-date {
  font-weight: var(--ds-font-weight-semibold);
}

@media (max-width: 768px) {
  .about {
    padding: var(--ds-space-md);
  }

  .about-title {
    font-size: var(--ds-font-size-2xl);
  }

  .section-title {
    font-size: var(--ds-font-size-lg);
  }

  .stack-grid {
    grid-template-columns: 1fr;
  }

  .about-section {
    padding: var(--ds-space-md);
  }
}

@media (max-width: 480px) {
  .about {
    padding: var(--ds-space-sm);
  }

  .about-content {
    gap: var(--ds-space-lg);
  }

  .about-title {
    font-size: var(--ds-font-size-xl);
  }

  .about-section {
    padding: var(--ds-space-md);
  }

  .section-title {
    font-size: var(--ds-font-size-base);
  }

  .section-text {
    font-size: var(--ds-font-size-sm);
  }

  .code-block pre {
    font-size: 11px;
  }
}
</style>
