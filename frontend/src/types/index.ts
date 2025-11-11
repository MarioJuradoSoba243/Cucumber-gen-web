export interface TestCase {
  id?: string;
  name: string;
  description?: string;
  folderPath: string;
  attributes: Record<string, string | number | boolean | null>;
}

export interface CloneTestPayload {
  name?: string;
  description?: string;
  folderPath?: string;
  attributes?: Record<string, string | number | boolean | null>;
}

export interface AttributeDefinition {
  id?: string;
  key: string;
  type: string;
  allowedValues: string[];
  required: boolean;
}

export interface TemplateDefinition {
  id?: string;
  name: string;
  content: string;
  description?: string;
}

export interface GherkinSentence {
  id: string;
  content: string;
  folderPath: string | null;
}

export interface GeneratedFeature {
  testId: string;
  filename: string;
  content: string;
}

export interface GenerationRequestPayload {
  templateId: string;
  testIds: string[];
  filenamePattern: string;
  missingPolicy: 'SKIP' | 'DEFAULT' | 'KEEP';
  defaults: Record<string, string>;
}
