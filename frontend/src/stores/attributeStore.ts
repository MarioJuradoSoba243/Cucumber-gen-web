import { defineStore } from 'pinia';
import client from '../api/client';
import type { AttributeDefinition } from '../types';

interface AttributeState {
  attributes: AttributeDefinition[];
  loading: boolean;
}

export const useAttributeStore = defineStore('attributes', {
  state: (): AttributeState => ({
    attributes: [],
    loading: false
  }),
  actions: {
    async loadAttributes() {
      this.loading = true;
      try {
        const { data } = await client.get<AttributeDefinition[]>('/attributes');
        this.attributes = data;
      } finally {
        this.loading = false;
      }
    },
    async createAttribute(payload: AttributeDefinition) {
      const { data } = await client.post<AttributeDefinition>('/attributes', payload);
      this.attributes.push(data);
      return data;
    }
  }
});
