import type { TestCase } from './index';

export interface FolderNode {
  name: string;
  path: string;
  children: FolderNode[];
  tests: TestCase[];
}
