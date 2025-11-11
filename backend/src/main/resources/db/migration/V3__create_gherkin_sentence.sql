CREATE TABLE IF NOT EXISTS gherkin_sentence (
    id TEXT PRIMARY KEY,
    content TEXT NOT NULL UNIQUE,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_gherkin_sentence_content ON gherkin_sentence(content);
