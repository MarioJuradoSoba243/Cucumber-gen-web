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
            ref="contentTextarea"
            class="w-full rounded border border-slate-300 px-3 py-2 font-mono text-sm focus:border-lime-500 focus:outline-none"
          ></textarea>
        </div>
        <button type="submit" class="rounded bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800">
          {{ form.id ? 'Guardar cambios' : 'Crear plantilla' }}
        </button>
      </form>

      <aside class="space-y-3">
        <div class="rounded-lg border border-slate-200 bg-white shadow-sm">
          <header class="flex items-start justify-between gap-3 border-b border-slate-200 px-4 py-3">
            <div>
              <h3 class="text-sm font-semibold text-slate-700">Sentencias Gherkin</h3>
              <p class="text-xs text-slate-500">Importa y haz clic para insertarlas en la plantilla.</p>
            </div>
            <button
              class="rounded bg-slate-900 px-3 py-1 text-xs font-semibold text-white hover:bg-slate-800"
              type="button"
              @click="triggerImport"
            >
              Importar
            </button>
            <input
              ref="importInput"
              accept=".txt,text/plain"
              class="hidden"
              type="file"
              @change="handleFileImport"
            />
          </header>
          <div class="max-h-60 space-y-2 overflow-y-auto px-4 py-3">
            <p v-if="gherkinSentenceStore.loading" class="text-xs text-slate-500">Cargando sentencias...</p>
            <p v-else-if="gherkinSentenceStore.sentences.length === 0" class="text-xs text-slate-500">
              No hay sentencias importadas todavía.
            </p>
            <ul v-else class="space-y-2">
              <li v-for="sentence in gherkinSentenceStore.sentences" :key="sentence.id">
                <button
                  class="w-full rounded border border-slate-200 bg-slate-50 px-3 py-2 text-left text-xs font-medium text-slate-700 transition hover:border-lime-300 hover:bg-lime-50"
                  type="button"
                  @click="insertSentence(sentence.content)"
                >
                  <code class="whitespace-pre-line break-words">{{ sentence.content }}</code>
                </button>
              </li>
            </ul>
          </div>
        </div>

        <div class="rounded-lg border border-slate-200 bg-white shadow-sm">
          <header class="border-b border-slate-200 px-4 py-3">
            <h3 class="text-sm font-semibold text-slate-700">Atributos disponibles</h3>
          </header>
          <div class="max-h-56 space-y-3 overflow-y-auto px-4 py-3 text-xs">
            <p v-if="attributeStore.loading" class="text-slate-500">Cargando atributos...</p>
            <p v-else-if="attributeStore.attributes.length === 0" class="text-slate-500">
              No se han definido atributos todavía.
            </p>
            <ul v-else class="space-y-3">
              <li v-for="attribute in attributeStore.attributes" :key="attribute.id" class="space-y-1">
                <p class="font-semibold text-slate-700">{{ attribute.key }}</p>
                <p class="text-[11px] text-slate-500">
                  Tipo: {{ attribute.type }} · {{ attribute.required ? 'Obligatorio' : 'Opcional' }}
                </p>
                <p v-if="attribute.allowedValues?.length" class="text-[11px] text-slate-500">
                  Valores permitidos: {{ attribute.allowedValues.join(', ') }}
                </p>
              </li>
            </ul>
          </div>
        </div>

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
import { nextTick, onMounted, reactive, ref } from 'vue';
import { useAttributeStore } from '../stores/attributeStore';
import { useGherkinSentenceStore } from '../stores/gherkinSentenceStore';
import { useTemplateStore } from '../stores/templateStore';
import type { TemplateDefinition } from '../types';

const templateStore = useTemplateStore();
const attributeStore = useAttributeStore();
const gherkinSentenceStore = useGherkinSentenceStore();
const feedback = ref('');
const importInput = ref<HTMLInputElement | null>(null);
const contentTextarea = ref<HTMLTextAreaElement | null>(null);

const emptyTemplate: TemplateDefinition = {
  name: '',
  description: '',
  content: ''
};

const form = reactive<TemplateDefinition>({ ...emptyTemplate });

onMounted(async () => {
  try {
    await Promise.all([
      templateStore.loadTemplates(),
      attributeStore.loadAttributes(),
      gherkinSentenceStore.loadSentences()
    ]);
  } catch (error) {
    console.error(error);
    feedback.value = 'No se pudieron cargar todos los datos.';
  }
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

function triggerImport() {
  importInput.value?.click();
}

async function handleFileImport(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) {
    return;
  }

  try {
    const content = await file.text();
    const sentences = content
      .split(/\r?\n/)
      .map((line) => line.trim())
      .filter((line) => line.length > 0 && !line.startsWith('#'));

    await gherkinSentenceStore.importSentences(sentences);
    if (sentences.length === 0) {
      feedback.value = 'Se limpiaron las sentencias Gherkin disponibles.';
    } else {
      feedback.value = `Se importaron ${gherkinSentenceStore.sentences.length} sentencias Gherkin.`;
    }
  } catch (error) {
    console.error(error);
    feedback.value = 'No se pudieron importar las sentencias.';
  } finally {
    input.value = '';
  }
}

function insertSentence(sentence: string) {
  const current = form.content ?? '';
  const trimmed = current.trimEnd();
  const updated = trimmed.length > 0 ? `${trimmed}\n${sentence}` : sentence;
  form.content = `${updated}\n`;

  nextTick(() => {
    if (contentTextarea.value) {
      const element = contentTextarea.value;
      element.focus();
      const length = element.value.length;
      element.setSelectionRange(length, length);
    }
  });
}
</script>
