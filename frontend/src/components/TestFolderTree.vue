<template>
  <div class="space-y-1">
    <div class="flex items-center gap-1 text-sm" :style="{ paddingLeft: `${level * 16}px` }">
      <button
        type="button"
        class="flex h-6 w-6 items-center justify-center rounded hover:bg-slate-100"
        :class="{
          'text-slate-500': canToggle,
          'cursor-default text-transparent': !canToggle
        }"
        @click="handleToggle"
      >
        <span v-if="canToggle">{{ isExpanded ? '▾' : '▸' }}</span>
      </button>
      <button
        type="button"
        class="flex-1 rounded px-2 py-1 text-left hover:bg-slate-100"
        :class="{
          'bg-lime-100 text-lime-700': node.path === selectedFolder,
          'text-slate-700': node.path !== selectedFolder
        }"
        @click="handleSelectFolder"
      >
        <span class="font-medium">{{ node.name }}</span>
        <span v-if="node.path && node.path !== ''" class="ml-2 text-xs text-slate-400">/{{ node.path }}</span>
      </button>
    </div>
    <div v-if="isExpanded" class="space-y-1">
      <div v-for="child in node.children" :key="child.path">
        <TestFolderTree
          :node="child"
          :expanded="expanded"
          :selected-folder="selectedFolder"
          :selected-test-id="selectedTestId"
          :level="level + 1"
          @toggle-folder="$emit('toggle-folder', $event)"
          @select-folder="$emit('select-folder', $event)"
          @select-test="$emit('select-test', $event)"
          @delete-test="$emit('delete-test', $event)"
        />
      </div>
      <ul v-if="node.tests.length" class="space-y-1">
        <li
          v-for="test in node.tests"
          :key="test.id ?? `${test.name}-${test.description}`"
          class="flex items-center justify-between"
          :style="{ paddingLeft: `${(level + 1) * 16 + 12}px` }"
        >
          <button
            type="button"
            class="flex-1 rounded px-2 py-1 text-left hover:bg-slate-100"
            :class="{
              'bg-slate-900 text-white': isSelected(test),
              'text-slate-700': !isSelected(test)
            }"
            @click="$emit('select-test', test)"
          >
            <p class="font-medium">{{ test.name }}</p>
            <p v-if="test.description" class="text-xs" :class="isSelected(test) ? 'text-slate-200' : 'text-slate-500'">
              {{ test.description }}
            </p>
          </button>
          <button
            type="button"
            class="ml-2 rounded px-2 py-1 text-xs font-semibold text-red-600 hover:bg-red-50"
            @click="$emit('delete-test', test)"
          >
            Borrar
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, toRefs } from 'vue';
import type { TestCase } from '../types';
import type { FolderNode } from '../types/testFolders';

defineOptions({ name: 'TestFolderTree' });

const props = withDefaults(
  defineProps<{
    node: FolderNode;
    expanded: string[];
    selectedFolder: string;
    selectedTestId?: string;
    level?: number;
  }>(),
  {
    level: 0
  }
);

const emit = defineEmits<{
  (e: 'toggle-folder', path: string): void;
  (e: 'select-folder', path: string): void;
  (e: 'select-test', test: TestCase): void;
  (e: 'delete-test', test: TestCase): void;
}>();

const { node, expanded, selectedFolder, selectedTestId, level } = toRefs(props);

const expandedSet = computed(() => new Set(expanded.value));
const isRoot = computed(() => node.value.path === '');
const isExpanded = computed(() => isRoot.value || expandedSet.value.has(node.value.path));
const hasChildren = computed(() => node.value.children.length > 0);
const hasTests = computed(() => node.value.tests.length > 0);
const canToggle = computed(() => hasChildren.value || hasTests.value);

function handleToggle() {
  if (!canToggle.value) {
    return;
  }
  emit('toggle-folder', node.value.path);
}

function handleSelectFolder() {
  emit('select-folder', node.value.path);
}

function isSelected(test: TestCase) {
  return Boolean(test.id && selectedTestId.value && test.id === selectedTestId.value);
}
</script>
