<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div id="app">
    <!-- Header Sticky -->
    <header class="app-header">
      <div class="header-content">
        <h1 class="header-title">
          <span class="header-title-emoji">📝</span>
          TaskList
        </h1>

        <nav class="app-nav">
          <RouterLink to="/" class="nav-link">Home</RouterLink>
          <RouterLink to="/tasks" class="nav-link">Tarefas</RouterLink>
          <RouterLink to="/about" class="nav-link">Sobre</RouterLink>
        </nav>
      </div>
    </header>

    <!-- Conteúdo Principal -->
    <main class="app-main">
      <RouterView />
    </main>

    <!-- Footer -->
    <footer class="app-footer">
      <p>&copy; 2026 JTech TaskList. Desenvolvido com Vue 3 + Spring Boot.</p>
    </footer>
  </div>
</template>

<style scoped>
/* Estilos específicos do App.vue usando design tokens */
</style>
