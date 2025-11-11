<template>
  <section class="space-y-6">
    <header class="flex items-center justify-between">
      <div>
        <h2 class="text-xl font-semibold">Casos de prueba</h2>
        <p class="text-sm text-slate-600">Crea y gestiona los tests junto con sus atributos personalizados.</p>
      </div>
      <button
        class="rounded bg-lime-500 px-4 py-2 text-sm font-semibold text-white shadow hover:bg-lime-600"
        @click="prepareNewTest"
      >
        Nuevo test
      </button>
    </header>

    <div v-if="feedback" class="rounded border border-lime-300 bg-lime-50 px-4 py-2 text-sm text-lime-800">
      {{ feedback }}
    </div>

    <div class="grid gap-6 lg:grid-cols-[2fr_1fr]">
      <form class="space-y-4 rounded-lg border border-slate-200 bg-white p-6 shadow-sm" @submit.prevent="handleSubmit">
        <h3 class="text-lg font-semibold">
          {{ currentTest.id ? 'Editar test' : 'Crear test' }}
        </h3>
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="test-name">Nombre</label>
          <input
            id="test-name"
            v-model="currentTest.name"
            type="text"
            required
            class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
          />
        </div>
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="test-description">Descripción</label>
          <textarea
            id="test-description"
            v-model="currentTest.description"
            class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
            rows="3"
          ></textarea>
        </div>

        <div class="space-y-4">
          <h4 class="text-sm font-semibold text-slate-700">Atributos personalizados</h4>
          <div v-if="attributeStore.attributes.length === 0" class="text-sm text-slate-500">
            No hay atributos definidos. Agrega atributos desde la pestaña "Atributos".
          </div>
          <div v-for="definition in attributeStore.attributes" :key="definition.id" class="space-y-1">
            <label :for="`attribute-${definition.key}`" class="text-sm font-medium text-slate-700">
              {{ definition.key }}
              <span v-if="definition.required" class="text-red-500">*</span>
            </label>
            <select
              v-if="definition.allowedValues.length > 0"
              :id="`attribute-${definition.key}`"
              v-model="formAttributes[definition.key]"
              :required="definition.required"
              class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
            >
              <option value="" disabled>Selecciona un valor</option>
              <option v-for="option in definition.allowedValues" :key="option" :value="option">
                {{ option }}
              </option>
            </select>
            <input
              v-else-if="definition.type === 'boolean'"
              :id="`attribute-${definition.key}`"
              type="checkbox"
              :checked="formAttributes[definition.key] === 'true'"
              @change="toggleBoolean(definition.key, $event)"
              class="h-4 w-4 rounded border-slate-300 text-lime-600 focus:ring-lime-500"
            />
            <input
              v-else
              :id="`attribute-${definition.key}`"
              v-model="formAttributes[definition.key]"
              :required="definition.required"
              type="text"
              class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
            />
          </div>
        </div>

        <div class="flex gap-3">
          <button
            type="submit"
            class="rounded bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800"
          >
            {{ currentTest.id ? 'Guardar cambios' : 'Crear test' }}
          </button>
          <button
            type="button"
            class="rounded border border-slate-300 px-4 py-2 text-sm text-slate-700 hover:bg-slate-100"
            @click="prepareNewTest"
          >
            Cancelar
          </button>
        </div>
      </form>

      <aside class="space-y-3">
        <div class="rounded-lg border border-slate-200 bg-white shadow-sm">
          <header class="border-b border-slate-200 px-4 py-3">
            <h3 class="text-sm font-semibold text-slate-700">Listado</h3>
          </header>
          <ul class="divide-y divide-slate-200">
            <li v-for="test in testStore.tests" :key="test.id" class="px-4 py-3 text-sm">
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-slate-800">{{ test.name }}</p>
                  <p class="text-xs text-slate-500">{{ test.description }}</p>
                </div>
                <div class="flex gap-2">
                  <button class="text-xs font-semibold text-lime-600 hover:underline" @click="editTest(test)">
                    Editar
                  </button>
                  <button class="text-xs font-semibold text-red-600 hover:underline" @click="removeTest(test)">
                    Borrar
                  </button>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </aside>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useTestStore } from '../stores/testStore';
import { useAttributeStore } from '../stores/attributeStore';
import type { TestCase } from '../types';

const testStore = useTestStore();
const attributeStore = useAttributeStore();

const emptyTest: TestCase = {
  name: '',
  description: '',
  attributes: {}
};

const currentTest = reactive<TestCase>({ ...emptyTest });
const formAttributes = reactive<Record<string, string>>({});
const feedback = ref('');

onMounted(async () => {
  await Promise.all([testStore.loadTests(), attributeStore.loadAttributes()]);
  prepareNewTest();
});

watch(
  () => attributeStore.attributes,
  (definitions) => {
    for (const definition of definitions) {
      if (!(definition.key in formAttributes)) {
        formAttributes[definition.key] = '';
      }
    }
  },
  { deep: true }
);

const payload = computed((): TestCase => ({
  id: currentTest.id,
  name: currentTest.name,
  description: currentTest.description,
  attributes: { ...formAttributes }
}));

function prepareNewTest() {
  Object.assign(currentTest, { ...emptyTest });
  Object.keys(formAttributes).forEach((key) => delete formAttributes[key]);
  for (const definition of attributeStore.attributes) {
    formAttributes[definition.key] = '';
  }
  feedback.value = '';
}

function editTest(test: TestCase) {
  Object.assign(currentTest, JSON.parse(JSON.stringify(test)));
  Object.keys(formAttributes).forEach((key) => delete formAttributes[key]);
  for (const definition of attributeStore.attributes) {
    const value = test.attributes?.[definition.key];
    formAttributes[definition.key] = value === undefined || value === null ? '' : String(value);
  }
  feedback.value = '';
}

async function handleSubmit() {
  try {
    if (currentTest.id) {
      await testStore.updateTest(currentTest.id, payload.value);
      feedback.value = 'Test actualizado correctamente.';
    } else {
      await testStore.createTest(payload.value);
      feedback.value = 'Test creado correctamente.';
      prepareNewTest();
    }
  } catch (error) {
    console.error(error);
    feedback.value = 'Ocurrió un error al guardar el test.';
  }
}

async function removeTest(test: TestCase) {
  if (!test.id) {
    return;
  }
  if (confirm(`¿Eliminar el test "${test.name}"?`)) {
    try {
      await testStore.deleteTest(test.id);
      feedback.value = 'Test eliminado correctamente.';
      if (currentTest.id === test.id) {
        prepareNewTest();
      }
    } catch (error) {
      console.error(error);
      feedback.value = 'No se pudo eliminar el test.';
    }
  }
}

function toggleBoolean(key: string, event: Event) {
  const target = event.target as HTMLInputElement;
  formAttributes[key] = target.checked ? 'true' : 'false';
}
</script>
