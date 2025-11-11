import { defineStore } from 'pinia';
import client from '../api/client';
import type { CloneTestPayload, TestCase } from '../types';

interface TestState {
  tests: TestCase[];
  loading: boolean;
}

export const useTestStore = defineStore('tests', {
  state: (): TestState => ({
    tests: [],
    loading: false
  }),
  actions: {
    async loadTests() {
      this.loading = true;
      try {
        const { data } = await client.get<TestCase[]>('/tests');
        this.tests = data;
      } finally {
        this.loading = false;
      }
    },
    async createTest(payload: TestCase) {
      const { data } = await client.post<TestCase>('/tests', payload);
      this.tests.push(data);
      return data;
    },
    async updateTest(id: string, payload: TestCase) {
      const { data } = await client.put<TestCase>(`/tests/${id}`, payload);
      this.tests = this.tests.map((test) => (test.id === id ? data : test));
      return data;
    },
    async deleteTest(id: string) {
      await client.delete(`/tests/${id}`);
      this.tests = this.tests.filter((test) => test.id !== id);
    },
    async cloneTest(id: string, overrides: CloneTestPayload = {}) {
      const { data } = await client.post<TestCase>(`/tests/${id}/clone`, overrides);
      this.tests.push(data);
      return data;
    }
  }
});
