<template>
  <section class="space-y-6">
    <header>
      <h2 class="text-xl font-semibold">Atributos personalizados</h2>
      <p class="text-sm text-slate-600">Define los atributos disponibles para los casos de prueba.</p>
    </header>

    <div v-if="feedback" class="rounded border border-lime-300 bg-lime-50 px-4 py-2 text-sm text-lime-800">
      {{ feedback }}
    </div>

    <form class="space-y-4 rounded-lg border border-slate-200 bg-white p-6 shadow-sm" @submit.prevent="handleSubmit">
      <div class="grid gap-4 md:grid-cols-2">
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="attr-key">Clave</label>
          <input
            id="attr-key"
            v-model="form.key"
            type="text"
            required
            class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
          />
        </div>
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="attr-type">Tipo</label>
          <select
            id="attr-type"
            v-model="form.type"
            required
            class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
          >
            <option value="">Selecciona un tipo</option>
            <option value="string">Texto</option>
            <option value="number">Número</option>
            <option value="boolean">Booleano</option>
          </select>
        </div>
      </div>

      <div class="space-y-1">
        <label class="text-sm font-medium text-slate-700" for="attr-values">Valores permitidos</label>
        <input
          id="attr-values"
          v-model="form.allowedValues"
          type="text"
          placeholder="Valores separados por coma"
          class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
        />
        <p class="text-xs text-slate-500">Deja vacío para permitir cualquier valor.</p>
      </div>

      <label class="flex items-center gap-2 text-sm font-medium text-slate-700">
        <input v-model="form.required" type="checkbox" class="h-4 w-4 rounded border-slate-300 text-lime-600 focus:ring-lime-500" />
        Requerido
      </label>

      <button type="submit" class="rounded bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800">
        Crear atributo
      </button>
    </form>

    <section class="rounded-lg border border-slate-200 bg-white shadow-sm">
      <header class="border-b border-slate-200 px-4 py-3">
        <h3 class="text-sm font-semibold text-slate-700">Atributos registrados</h3>
      </header>
      <table class="min-w-full divide-y divide-slate-200 text-sm">
        <thead class="bg-slate-50 text-left text-xs uppercase tracking-wide text-slate-500">
          <tr>
            <th class="px-4 py-2">Clave</th>
            <th class="px-4 py-2">Tipo</th>
            <th class="px-4 py-2">Valores permitidos</th>
            <th class="px-4 py-2">Requerido</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-200">
          <tr v-for="attribute in attributeStore.attributes" :key="attribute.id">
            <td class="px-4 py-2 font-medium text-slate-700">{{ attribute.key }}</td>
            <td class="px-4 py-2">{{ attribute.type }}</td>
            <td class="px-4 py-2">
              <span v-if="attribute.allowedValues.length > 0">{{ attribute.allowedValues.join(', ') }}</span>
              <span v-else class="text-slate-400">Libre</span>
            </td>
            <td class="px-4 py-2">{{ attribute.required ? 'Sí' : 'No' }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useAttributeStore } from '../stores/attributeStore';

const attributeStore = useAttributeStore();
const feedback = ref('');

const form = reactive({
  key: '',
  type: '',
  allowedValues: '',
  required: false
});

onMounted(async () => {
  await attributeStore.loadAttributes();
});

async function handleSubmit() {
  try {
    await attributeStore.createAttribute({
      key: form.key,
      type: form.type,
      allowedValues: form.allowedValues
        .split(',')
        .map((value) => value.trim())
        .filter((value) => value.length > 0),
      required: form.required
    });
    feedback.value = 'Atributo creado correctamente.';
    form.key = '';
    form.type = '';
    form.allowedValues = '';
    form.required = false;
  } catch (error) {
    console.error(error);
    feedback.value = 'No se pudo crear el atributo.';
  }
}
</script>
