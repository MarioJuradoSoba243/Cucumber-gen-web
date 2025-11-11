<template>
  <section class="space-y-6">
    <header class="flex items-center justify-between">
      <div>
        <h2 class="text-xl font-semibold">Plantillas Gherkin</h2>
        <p class="text-sm text-slate-600">Define y versiona las plantillas con placeholders.</p>
      </div>
      <button
        class="rounded bg-lime-500 px-4 py-2 text-sm font-semibold text-white shadow hover:bg-lime-600"
        @click="resetForm"
      >
        Nueva plantilla
      </button>
    </header>

    <div v-if="feedback" class="rounded border border-lime-300 bg-lime-50 px-4 py-2 text-sm text-lime-800">
      {{ feedback }}
    </div>

    <div class="grid gap-6 lg:grid-cols-[2fr_1fr]">
      <form class="space-y-4 rounded-lg border border-slate-200 bg-white p-6 shadow-sm" @submit.prevent="handleSubmit">
        <h3 class="text-lg font-semibold">{{ form.id ? 'Editar plantilla' : 'Crear plantilla' }}</h3>
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="template-name">Nombre</label>
          <input
            id="template-name"
            v-model="form.name"
            type="text"
            required
            class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
          />
        </div>
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="template-description">Descripción</label>
          <input
            id="template-description"
            v-model="form.description"
            type="text"
            class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
          />
        </div>
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="template-content">Contenido Gherkin</label>
          <textarea
            id="template-content"
            v-model="form.content"
            required
            rows="12"
            class="w-full rounded border border-slate-300 px-3 py-2 font-mono text-sm focus:border-lime-500 focus:outline-none"
          ></textarea>
        </div>
        <button type="submit" class="rounded bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800">
          {{ form.id ? 'Guardar cambios' : 'Crear plantilla' }}
        </button>
      </form>

      <aside class="space-y-3">
        <div class="rounded-lg border border-slate-200 bg-white shadow-sm">
          <header class="border-b border-slate-200 px-4 py-3">
            <h3 class="text-sm font-semibold text-slate-700">Plantillas</h3>
          </header>
          <ul class="divide-y divide-slate-200">
            <li v-for="template in templateStore.templates" :key="template.id" class="px-4 py-3 text-sm">
              <div class="flex items-start justify-between gap-3">
                <div>
                  <p class="font-medium text-slate-800">{{ template.name }}</p>
                  <p class="text-xs text-slate-500">{{ template.description }}</p>
                </div>
                <button class="text-xs font-semibold text-lime-600 hover:underline" @click="editTemplate(template)">
                  Editar
                </button>
              </div>
            </li>
          </ul>
        </div>
      </aside>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useTemplateStore } from '../stores/templateStore';
import type { TemplateDefinition } from '../types';

const templateStore = useTemplateStore();
const feedback = ref('');

const emptyTemplate: TemplateDefinition = {
  name: '',
  description: '',
  content: ''
};

const form = reactive<TemplateDefinition>({ ...emptyTemplate });

onMounted(async () => {
  await templateStore.loadTemplates();
});

function resetForm() {
  Object.assign(form, { ...emptyTemplate });
  delete form.id;
  feedback.value = '';
}

function editTemplate(template: TemplateDefinition) {
  Object.assign(form, JSON.parse(JSON.stringify(template)));
  feedback.value = '';
}

async function handleSubmit() {
  try {
    await templateStore.saveTemplate(form);
    feedback.value = 'Plantilla guardada correctamente.';
    if (!form.id) {
      resetForm();
    }
  } catch (error) {
    console.error(error);
    feedback.value = 'No se pudo guardar la plantilla.';
  }
}
</script>
