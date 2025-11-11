import { defineStore } from 'pinia';
import client from '../api/client';
import type { GherkinSentence } from '../types';

interface GherkinSentenceState {
  sentences: GherkinSentence[];
  loading: boolean;
}

export const useGherkinSentenceStore = defineStore('gherkinSentences', {
  state: (): GherkinSentenceState => ({
    sentences: [],
    loading: false
  }),
  actions: {
    async loadSentences() {
      this.loading = true;
      try {
        const { data } = await client.get<GherkinSentence[]>('/gherkin-sentences');
        this.sentences = data;
      } finally {
        this.loading = false;
      }
    },
    async importSentences(sentences: string[]) {
      const { data } = await client.post<GherkinSentence[]>('/gherkin-sentences/import', { sentences });
      this.sentences = data;
      return data;
    }
  }
});
