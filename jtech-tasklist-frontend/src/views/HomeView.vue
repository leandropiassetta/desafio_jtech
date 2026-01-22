<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goToTasks = () => {
  router.push('/tasks')
}
</script>

<template>
  <main class="home">
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">📝 TaskList</h1>
        <p class="hero-subtitle">Organize suas tarefas com simplicidade e eficiência</p>
        <p class="hero-description">
          Crie, edite e acompanhe suas tarefas em um único lugar. Interface moderna, responsiva e intuitiva
          para gerenciar seu dia a dia.
        </p>
        <button class="btn btn-primary btn-lg" @click="goToTasks">Começar agora →</button>
      </div>
      <div class="hero-illustration">
        <div class="illustration-placeholder">✓</div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.home {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  padding: var(--ds-space-lg);
}

.hero {
  display: flex;
  gap: var(--ds-space-3xl);
  align-items: center;
  max-width: 900px;
  width: 100%;
}

.hero-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--ds-space-md);
}

.hero-title {
  font-size: var(--ds-font-size-3xl);
  font-weight: var(--ds-font-weight-bold);
  color: var(--ds-color-text-primary);
  margin: 0;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: var(--ds-font-size-lg);
  font-weight: var(--ds-font-weight-semibold);
  color: var(--ds-color-primary);
  margin: 0;
}

.hero-description {
  font-size: var(--ds-font-size-base);
  color: var(--ds-color-text-secondary);
  line-height: 1.6;
  margin: 0;
}

.btn {
  align-self: flex-start;
  padding: var(--ds-space-sm) var(--ds-space-lg);
  border: none;
  border-radius: var(--ds-radius-md);
  font-size: var(--ds-font-size-base);
  font-weight: var(--ds-font-weight-semibold);
  cursor: pointer;
  transition: all var(--ds-transition-base) ease-in-out;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
}

.btn-primary {
  background-color: var(--ds-color-primary);
  color: white;
}

.btn-primary:hover {
  background-color: var(--ds-color-primary-dark);
  transform: translateY(-2px);
  box-shadow: var(--ds-shadow-md);
}

.btn-primary:active {
  transform: translateY(0);
}

.btn-lg {
  padding: var(--ds-space-md) var(--ds-space-xl);
  font-size: var(--ds-font-size-lg);
}

.hero-illustration {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.illustration-placeholder {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, var(--ds-color-primary-light), var(--ds-color-primary));
  border-radius: var(--ds-radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80px;
  font-weight: var(--ds-font-weight-bold);
  color: white;
  box-shadow: var(--ds-shadow-lg);
}

@media (max-width: 768px) {
  .hero {
    flex-direction: column;
    gap: var(--ds-space-xl);
  }

  .hero-title {
    font-size: var(--ds-font-size-2xl);
  }

  .hero-subtitle {
    font-size: var(--ds-font-size-base);
  }

  .illustration-placeholder {
    width: 150px;
    height: 150px;
    font-size: 60px;
  }

  .btn {
    align-self: center;
  }
}

@media (max-width: 480px) {
  .home {
    padding: var(--ds-space-md);
  }

  .hero {
    gap: var(--ds-space-lg);
  }

  .hero-title {
    font-size: var(--ds-font-size-xl);
  }

  .hero-description {
    font-size: var(--ds-font-size-sm);
  }

  .illustration-placeholder {
    width: 120px;
    height: 120px;
    font-size: 48px;
  }
}
</style>
