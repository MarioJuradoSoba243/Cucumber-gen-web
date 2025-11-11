<template>
  <div class="min-h-screen">
    <header class="bg-slate-900 text-white shadow">
      <div class="mx-auto flex max-w-6xl items-center justify-between px-4 py-4">
        <h1 class="text-2xl font-semibold">Cucumber Generator</h1>
        <nav class="flex gap-4 text-sm font-medium">
          <RouterLink to="/tests" class="hover:text-lime-300" active-class="text-lime-400">Tests</RouterLink>
          <RouterLink to="/attributes" class="hover:text-lime-300" active-class="text-lime-400">Atributos</RouterLink>
          <RouterLink to="/templates" class="hover:text-lime-300" active-class="text-lime-400">Plantillas</RouterLink>
          <RouterLink to="/generation" class="hover:text-lime-300" active-class="text-lime-400">Generación</RouterLink>
        </nav>
      </div>
    </header>
    <main class="mx-auto max-w-6xl px-4 py-8">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router';
</script>
