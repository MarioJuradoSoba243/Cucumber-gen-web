<template>
  <section class="space-y-6">
    <header class="flex items-center justify-between">
      <div>
        <h2 class="text-xl font-semibold">Casos de prueba</h2>
        <p class="text-sm text-slate-600">
          Crea y gestiona los tests junto con sus atributos personalizados.
        </p>
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

    <div class="grid gap-6 lg:grid-cols-[1fr_2fr]">
      <aside class="space-y-3">
        <div class="rounded-lg border border-slate-200 bg-white shadow-sm">
          <header class="flex items-center justify-between border-b border-slate-200 px-4 py-3">
            <div>
              <h3 class="text-sm font-semibold text-slate-700">Carpetas y tests</h3>
              <p class="text-xs text-slate-500">Selecciona una carpeta para editar o crear tests.</p>
            </div>
            <button
              type="button"
              class="rounded border border-lime-500 px-3 py-1 text-xs font-semibold text-lime-600 hover:bg-lime-50"
              @click="createFolder"
            >
              Nueva carpeta
            </button>
          </header>
          <div class="px-2 py-3">
            <TestFolderTree
              :node="folderTree"
              :expanded="expandedFolders"
              :selected-folder="selectedFolder"
              :selected-test-id="selectedTestId"
              @toggle-folder="toggleFolder"
              @select-folder="selectFolder"
              @select-test="editTest"
              @delete-test="removeTest"
            />
            <p v-if="!folderTree.children.length && !folderTree.tests.length" class="px-2 text-sm text-slate-500">
              Aún no hay tests. Crea uno nuevo para comenzar.
            </p>
          </div>
        </div>
      </aside>

      <form class="space-y-5 rounded-lg border border-slate-200 bg-white p-6 shadow-sm" @submit.prevent="handleSubmit">
        <h3 class="text-lg font-semibold">
          {{ currentTest.id ? 'Editar test' : 'Crear test' }}
        </h3>
        <div class="grid gap-4 md:grid-cols-2">
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
            <label class="text-sm font-medium text-slate-700" for="test-folder">Carpeta</label>
            <input
              id="test-folder"
              v-model="currentTest.folderPath"
              type="text"
              placeholder="(raíz)"
              class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none"
            />
            <p class="text-xs text-slate-500">
              Usa "/" para separar carpetas. Selecciona una carpeta en el árbol para rellenarla automáticamente.
            </p>
          </div>
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
          <template v-else>
            <div v-if="activeAttributeDefinitions.length === 0" class="rounded border border-dashed border-slate-300 p-3 text-sm text-slate-500">
              No hay atributos obligatorios. Añade los opcionales desde el selector inferior.
            </div>
            <div class="grid gap-3 md:grid-cols-2">
              <div
                v-for="definition in activeAttributeDefinitions"
                :key="definition.id ?? definition.key"
                class="flex flex-col gap-2 rounded border border-slate-200 p-3 md:p-4"
              >
                <div class="flex items-center justify-between gap-2">
                  <label :for="`attribute-${definition.key}`" class="text-sm font-medium text-slate-700">
                    {{ definition.key }}
                    <span v-if="definition.required" class="text-red-500">*</span>
                  </label>
                  <button
                    v-if="!definition.required"
                    type="button"
                    class="text-xs font-semibold text-slate-500 hover:text-red-600"
                    @click="removeOptionalAttribute(definition.key)"
                  >
                    Quitar
                  </button>
                </div>
                <select
                  v-if="definition.allowedValues.length > 0"
                  :id="`attribute-${definition.key}`"
                  v-model="formAttributes[definition.key]"
                  :required="definition.required"
                  class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none md:max-w-xs"
                >
                  <option value="" disabled>Selecciona un valor</option>
                  <option v-for="option in definition.allowedValues" :key="option" :value="option">
                    {{ option }}
                  </option>
                </select>
                <div v-else-if="definition.type === 'boolean'" class="flex items-center gap-2">
                  <input
                    :id="`attribute-${definition.key}`"
                    type="checkbox"
                    :checked="formAttributes[definition.key] === 'true'"
                    @change="toggleBoolean(definition.key, $event)"
                    class="h-4 w-4 rounded border-slate-300 text-lime-600 focus:ring-lime-500"
                  />
                  <span class="text-xs text-slate-500">Marca la casilla para activar</span>
                </div>
                <input
                  v-else
                  :id="`attribute-${definition.key}`"
                  v-model="formAttributes[definition.key]"
                  :required="definition.required"
                  type="text"
                  class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none md:max-w-xs"
                />
              </div>
            </div>
            <div v-if="optionalAttributeOptions.length > 0" class="rounded border border-dashed border-slate-300 p-3">
              <label class="text-sm font-medium text-slate-700" for="optional-attribute-select">
                Añadir atributo opcional
              </label>
              <div class="mt-2 flex flex-wrap items-end gap-2">
                <select
                  id="optional-attribute-select"
                  v-model="nextOptionalAttribute"
                  class="w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-lime-500 focus:outline-none md:max-w-xs"
                >
                  <option value="">Selecciona un atributo</option>
                  <option
                    v-for="definition in optionalAttributeOptions"
                    :key="definition.id ?? definition.key"
                    :value="definition.key"
                  >
                    {{ definition.key }}
                  </option>
                </select>
                <button
                  type="button"
                  class="rounded bg-lime-500 px-3 py-2 text-sm font-semibold text-white hover:bg-lime-600 disabled:cursor-not-allowed disabled:bg-slate-300"
                  :disabled="!nextOptionalAttribute"
                  @click="addOptionalAttribute"
                >
                  Añadir
                </button>
              </div>
            </div>
          </template>
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
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import TestFolderTree from '../components/TestFolderTree.vue';
import { useTestStore } from '../stores/testStore';
import { useAttributeStore } from '../stores/attributeStore';
import type { AttributeDefinition, TestCase } from '../types';
import type { FolderNode } from '../types/testFolders';

const testStore = useTestStore();
const attributeStore = useAttributeStore();

const emptyTest: TestCase = {
  name: '',
  description: '',
  folderPath: '',
  attributes: {}
};

const currentTest = reactive<TestCase>({ ...emptyTest });
const formAttributes = reactive<Record<string, string>>({});
const activeAttributeKeys = ref<string[]>([]);
const nextOptionalAttribute = ref('');
const feedback = ref('');
const selectedFolder = ref('');
const expandedFolders = ref<string[]>(['']);
const manualFolders = ref<string[]>([]);
const selectedTestId = ref<string | undefined>(undefined);

function normalizeFolderPathInput(value: string | undefined): string {
  if (!value) {
    return '';
  }
  const sanitized = value.trim().replace(/\\/g, '/');
  if (!sanitized) {
    return '';
  }
  const segments = sanitized
    .split('/')
    .map((segment) => segment.trim())
    .filter((segment) => segment.length > 0 && segment !== '.');
  for (const segment of segments) {
    if (segment === '..') {
      throw new Error('La ruta de carpeta no puede contener "..".');
    }
  }
  return segments.join('/');
}

function safeNormalize(path: string | undefined): string | undefined {
  try {
    return normalizeFolderPathInput(path);
  } catch {
    return undefined;
  }
}

const folderTree = computed<FolderNode>(() => {
  const root: FolderNode = { name: 'Raíz', path: '', children: [], tests: [] };
  const map = new Map<string, FolderNode>();
  map.set('', root);

  const ensureNode = (path: string): FolderNode => {
    if (!path) {
      return root;
    }
    const existing = map.get(path);
    if (existing) {
      return existing;
    }
    const segments = path.split('/');
    const parentPath = segments.length > 1 ? segments.slice(0, -1).join('/') : '';
    const parent = ensureNode(parentPath);
    const node: FolderNode = {
      name: segments[segments.length - 1],
      path,
      children: [],
      tests: []
    };
    map.set(path, node);
    parent.children.push(node);
    return node;
  };

  const registerPath = (path: string) => {
    if (!path) {
      return;
    }
    ensureNode(path);
  };

  for (const folder of manualFolders.value) {
    const normalizedFolder = safeNormalize(folder);
    if (normalizedFolder !== undefined) {
      registerPath(normalizedFolder);
    }
  }

  for (const test of testStore.tests) {
    const normalizedFolder = safeNormalize(test.folderPath ?? '');
    const node = ensureNode(normalizedFolder ?? '');
    node.tests.push(test);
  }

  const sortTree = (node: FolderNode) => {
    node.children.sort((a, b) => a.name.localeCompare(b.name));
    node.tests.sort((a, b) => a.name.localeCompare(b.name));
    node.children.forEach(sortTree);
  };

  sortTree(root);
  return root;
});

const definitionMap = computed(() => {
  const map = new Map<string, AttributeDefinition>();
  for (const definition of attributeStore.attributes) {
    map.set(definition.key, definition);
  }
  return map;
});

const activeAttributeDefinitions = computed(() =>
  activeAttributeKeys.value
    .map((key) => definitionMap.value.get(key))
    .filter((definition): definition is AttributeDefinition => Boolean(definition))
);

const optionalAttributeOptions = computed(() =>
  attributeStore.attributes.filter(
    (definition) => !definition.required && !activeAttributeKeys.value.includes(definition.key)
  )
);

const payload = computed((): TestCase => {
  const attributes: Record<string, string> = {};
  for (const key of activeAttributeKeys.value) {
    const value = formAttributes[key];
    if (value !== undefined) {
      attributes[key] = value;
    }
  }
  return {
    id: currentTest.id,
    name: currentTest.name,
    description: currentTest.description,
    folderPath: currentTest.folderPath ?? '',
    attributes
  };
});

onMounted(async () => {
  await Promise.all([testStore.loadTests(), attributeStore.loadAttributes()]);
  prepareNewTest();
});

watch(
  () => attributeStore.attributes,
  (definitions) => {
    const allowedKeys = new Set(definitions.map((definition) => definition.key));
    const requiredKeys = definitions.filter((definition) => definition.required).map((definition) => definition.key);
    const preservedActive = activeAttributeKeys.value.filter((key) => allowedKeys.has(key));
    setActiveAttributeKeys([...requiredKeys, ...preservedActive]);
  },
  { deep: true }
);

watch(optionalAttributeOptions, () => {
  updateNextOptionalAttribute();
});

function setActiveAttributeKeys(keys: string[]) {
  const uniqueKeys: string[] = [];
  const seen = new Set<string>();
  for (const key of keys) {
    if (!seen.has(key)) {
      seen.add(key);
      uniqueKeys.push(key);
    }
  }
  activeAttributeKeys.value = uniqueKeys;
  syncFormAttributes();
  updateNextOptionalAttribute();
}

function syncFormAttributes() {
  const activeSet = new Set(activeAttributeKeys.value);
  for (const key of Object.keys(formAttributes)) {
    if (!activeSet.has(key)) {
      delete formAttributes[key];
    }
  }
  for (const key of activeAttributeKeys.value) {
    if (!(key in formAttributes)) {
      formAttributes[key] = '';
    }
  }
}

function updateNextOptionalAttribute() {
  const options = optionalAttributeOptions.value;
  if (options.length === 0) {
    nextOptionalAttribute.value = '';
    return;
  }
  if (!options.some((option) => option.key === nextOptionalAttribute.value)) {
    nextOptionalAttribute.value = options[0].key;
  }
}

function expandToPath(path: string) {
  const set = new Set(expandedFolders.value);
  set.add('');
  if (path) {
    const segments = path.split('/');
    let current = '';
    for (const segment of segments) {
      current = current ? `${current}/${segment}` : segment;
      set.add(current);
    }
  }
  expandedFolders.value = Array.from(set);
}

function prepareNewTest() {
  const normalizedFolder = safeNormalize(selectedFolder.value) ?? '';
  Object.assign(currentTest, { ...emptyTest, attributes: {}, folderPath: normalizedFolder, id: undefined });
  Object.keys(formAttributes).forEach((key) => delete formAttributes[key]);
  const requiredKeys = attributeStore.attributes
    .filter((definition) => definition.required)
    .map((definition) => definition.key);
  setActiveAttributeKeys(requiredKeys);
  for (const key of activeAttributeKeys.value) {
    formAttributes[key] = '';
  }
  selectedTestId.value = undefined;
  feedback.value = '';
}

function editTest(test: TestCase) {
  const normalizedFolder = safeNormalize(test.folderPath ?? '') ?? '';
  Object.assign(currentTest, JSON.parse(JSON.stringify(test)));
  currentTest.folderPath = normalizedFolder;
  Object.keys(formAttributes).forEach((key) => delete formAttributes[key]);
  const attributesFromTest = test.attributes ?? {};
  const keysFromTest = Object.keys(attributesFromTest).filter((key) => definitionMap.value.has(key));
  const requiredKeys = attributeStore.attributes
    .filter((definition) => definition.required)
    .map((definition) => definition.key);
  setActiveAttributeKeys([...requiredKeys, ...keysFromTest]);
  for (const key of activeAttributeKeys.value) {
    const rawValue = attributesFromTest[key];
    if (rawValue === undefined || rawValue === null) {
      formAttributes[key] = '';
    } else if (typeof rawValue === 'boolean') {
      formAttributes[key] = rawValue ? 'true' : 'false';
    } else {
      formAttributes[key] = String(rawValue);
    }
  }
  selectedFolder.value = normalizedFolder;
  selectedTestId.value = test.id;
  expandToPath(normalizedFolder);
  feedback.value = '';
}

async function handleSubmit() {
  let normalizedFolder = '';
  try {
    normalizedFolder = normalizeFolderPathInput(currentTest.folderPath ?? selectedFolder.value);
  } catch (error) {
    feedback.value = error instanceof Error ? error.message : 'La ruta de carpeta no es válida.';
    return;
  }

  currentTest.folderPath = normalizedFolder;
  selectedFolder.value = normalizedFolder;
  expandToPath(normalizedFolder);

  try {
    if (currentTest.id) {
      const updated = await testStore.updateTest(currentTest.id, payload.value);
      Object.assign(currentTest, updated);
      selectedTestId.value = currentTest.id;
      feedback.value = 'Test actualizado correctamente.';
    } else {
      await testStore.createTest(payload.value);
      manualFolders.value = manualFolders.value.filter((folder) => folder !== normalizedFolder);
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
      if (selectedTestId.value === test.id) {
        selectedTestId.value = undefined;
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

function addOptionalAttribute() {
  if (!nextOptionalAttribute.value) {
    return;
  }
  const key = nextOptionalAttribute.value;
  if (!definitionMap.value.has(key)) {
    return;
  }
  setActiveAttributeKeys([...activeAttributeKeys.value, key]);
  if (!(key in formAttributes)) {
    formAttributes[key] = '';
  }
}

function removeOptionalAttribute(key: string) {
  const definition = definitionMap.value.get(key);
  if (!definition || definition.required) {
    return;
  }
  const remainingKeys = activeAttributeKeys.value.filter((item) => item !== key);
  setActiveAttributeKeys(remainingKeys);
  delete formAttributes[key];
}

function selectFolder(path: string) {
  selectedFolder.value = path;
  currentTest.folderPath = path;
  selectedTestId.value = undefined;
  expandToPath(path);
}

function toggleFolder(path: string) {
  if (path === '') {
    return;
  }
  const set = new Set(expandedFolders.value);
  if (set.has(path)) {
    set.delete(path);
  } else {
    set.add(path);
  }
  expandedFolders.value = Array.from(set);
}

function createFolder() {
  const folderName = prompt('Nombre de la nueva carpeta');
  if (!folderName) {
    return;
  }
  try {
    const normalizedNew = normalizeFolderPathInput(folderName);
    const base = selectedFolder.value;
    const combined = normalizeFolderPathInput([base, normalizedNew].filter(Boolean).join('/'));
    if (!manualFolders.value.includes(combined)) {
      manualFolders.value.push(combined);
    }
    selectedFolder.value = combined;
    currentTest.folderPath = combined;
    expandToPath(combined);
    feedback.value = 'Carpeta preparada. Crea un test para guardarla.';
  } catch (error) {
    feedback.value = error instanceof Error ? error.message : 'No se pudo crear la carpeta.';
  }
}
</script>
