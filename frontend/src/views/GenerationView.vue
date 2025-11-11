<template>
  <section class="space-y-6">
    <header>
      <h2 class="text-xl font-semibold">Generación de features</h2>
      <p class="text-sm text-slate-600">Selecciona una plantilla y los tests para generar archivos .feature.</p>
    </header>

    <div v-if="feedback" :class="feedbackClass" class="rounded px-4 py-2 text-sm">
      {{ feedback }}
    </div>

    <form class="space-y-4 rounded-lg border border-slate-200 bg-white p-6 shadow-sm" @submit.prevent="handlePreview">
      <div class="grid gap-4 md:grid-cols-2">
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="generation-template">Plantilla</label>
          <select
            id="generation-template"
            v-model="form.templateId"
            required
            class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
          >
            <option value="" disabled>Selecciona una plantilla</option>
            <option v-for="template in templateStore.templates" :key="template.id" :value="template.id">
              {{ template.name }}
            </option>
          </select>
        </div>
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="filename-pattern">Patrón de nombre</label>
          <input
            id="filename-pattern"
            v-model="form.filenamePattern"
            placeholder="{name}.feature"
            required
            class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
          />
        </div>
      </div>

      <div class="space-y-1">
        <label class="text-sm font-medium text-slate-700" for="missing-policy">Política de faltantes</label>
        <select
          id="missing-policy"
          v-model="form.missingPolicy"
          class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
        >
          <option value="DEFAULT">Usar valores por defecto</option>
          <option value="KEEP">Mantener placeholders</option>
          <option value="SKIP">Omitir tests incompletos</option>
        </select>
      </div>

      <div class="space-y-1">
        <label class="text-sm font-medium text-slate-700" for="defaults">Valores por defecto (JSON)</label>
        <textarea
          id="defaults"
          v-model="defaultsText"
          rows="4"
          class="w-full rounded border border-slate-300 px-3 py-2 font-mono text-sm focus:border-lime-500 focus:outline-none"
          placeholder='{"remitente": "Desconocido"}'
        ></textarea>
        <p class="text-xs text-slate-500">Se aplica cuando falte un valor y la política lo permita.</p>
      </div>

      <div>
        <h3 class="text-sm font-semibold text-slate-700">Selecciona tests</h3>
        <div class="mt-2 grid gap-2 md:grid-cols-2">
          <label
            v-for="test in testStore.tests"
            :key="test.id"
            class="flex cursor-pointer items-start gap-2 rounded border border-slate-200 bg-slate-50 px-3 py-2 text-sm hover:border-lime-400"
          >
            <input
              type="checkbox"
              :value="test.id"
              v-model="form.testIds"
              class="mt-1 h-4 w-4 rounded border-slate-300 text-lime-600 focus:ring-lime-500"
            />
            <span>
              <span class="block font-semibold text-slate-800">{{ test.name }}</span>
              <span class="text-xs text-slate-500">{{ test.description }}</span>
            </span>
          </label>
        </div>
      </div>

      <div class="flex gap-3">
        <button type="submit" class="rounded bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800">
          Generar preview
        </button>
        <button
          type="button"
          class="rounded border border-slate-300 px-4 py-2 text-sm text-slate-700 hover:bg-slate-100"
          @click="handleDownload"
        >
          Descargar ZIP
        </button>
      </div>
    </form>

    <section v-if="previews.length > 0" class="space-y-4">
      <h3 class="text-lg font-semibold">Preview</h3>
      <article v-for="feature in previews" :key="feature.filename" class="rounded-lg border border-slate-200 bg-white shadow-sm">
        <header class="border-b border-slate-200 px-4 py-3">
          <h4 class="text-sm font-semibold text-slate-700">{{ feature.filename }}</h4>
        </header>
        <pre class="whitespace-pre-wrap px-4 py-3 text-sm text-slate-800">{{ feature.content }}</pre>
      </article>
    </section>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import client from '../api/client';
import { useTemplateStore } from '../stores/templateStore';
import { useTestStore } from '../stores/testStore';
import type { GeneratedFeature, GenerationRequestPayload } from '../types';

const templateStore = useTemplateStore();
const testStore = useTestStore();

const previews = ref<GeneratedFeature[]>([]);
const feedback = ref('');
const defaultsText = ref('{}');

const form = reactive<GenerationRequestPayload>({
  templateId: '',
  testIds: [],
  filenamePattern: '{name}',
  missingPolicy: 'DEFAULT',
  defaults: {}
});

const feedbackClass = computed(() =>
  feedback.value.includes('error') ? 'border border-red-300 bg-red-50 text-red-800' : 'border border-lime-300 bg-lime-50 text-lime-800'
);

onMounted(async () => {
  await Promise.all([templateStore.loadTemplates(), testStore.loadTests()]);
});

function parseDefaults(): Record<string, string> {
  try {
    const parsed = JSON.parse(defaultsText.value || '{}');
    if (parsed && typeof parsed === 'object' && !Array.isArray(parsed)) {
      return Object.fromEntries(Object.entries(parsed).map(([key, value]) => [key, String(value)]));
    }
  } catch (error) {
    console.error(error);
  }
  throw new Error('El JSON de valores por defecto no es válido.');
}

async function handlePreview() {
  try {
    form.defaults = parseDefaults();
    const { data } = await client.post<GeneratedFeature[]>('/generation/preview', form);
    previews.value = data;
    feedback.value = 'Preview generado correctamente.';
  } catch (error) {
    console.error(error);
    feedback.value = 'Se produjo un error durante la generación de la preview.';
  }
}

async function handleDownload() {
  try {
    form.defaults = parseDefaults();
    const { data } = await client.post<ArrayBuffer>('/generation/download', form, {
      responseType: 'arraybuffer'
    });
    const blob = new Blob([data], { type: 'application/zip' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'features.zip';
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(url);
    feedback.value = 'Archivo descargado correctamente.';
  } catch (error) {
    console.error(error);
    feedback.value = 'Se produjo un error al descargar el archivo.';
  }
}
</script>
