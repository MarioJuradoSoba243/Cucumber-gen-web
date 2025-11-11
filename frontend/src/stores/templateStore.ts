import { defineStore } from 'pinia';
import client from '../api/client';
import type { TemplateDefinition } from '../types';

interface TemplateState {
  templates: TemplateDefinition[];
  loading: boolean;
}

export const useTemplateStore = defineStore('templates', {
  state: (): TemplateState => ({
    templates: [],
    loading: false
  }),
  actions: {
    async loadTemplates() {
      this.loading = true;
      try {
        const { data } = await client.get<TemplateDefinition[]>('/templates');
        this.templates = data;
      } finally {
        this.loading = false;
      }
    },
    async saveTemplate(payload: TemplateDefinition) {
      if (payload.id) {
        const { data } = await client.put<TemplateDefinition>(`/templates/${payload.id}`, payload);
        this.templates = this.templates.map((template) => (template.id === payload.id ? data : template));
        return data;
      }
      const { data } = await client.post<TemplateDefinition>('/templates', payload);
      this.templates.push(data);
      return data;
    }
  }
});
