DROP INDEX IF EXISTS idx_gherkin_sentence_content;

CREATE TABLE gherkin_sentence_new (
    id TEXT PRIMARY KEY,
    content TEXT NOT NULL,
    folder_path VARCHAR(255),
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

INSERT INTO gherkin_sentence_new (id, content, folder_path, created_at, updated_at)
SELECT id, content, NULL, created_at, updated_at
FROM gherkin_sentence;

DROP TABLE gherkin_sentence;

ALTER TABLE gherkin_sentence_new RENAME TO gherkin_sentence;

CREATE UNIQUE INDEX IF NOT EXISTS ux_gherkin_sentence_folder_path_content
    ON gherkin_sentence (COALESCE(folder_path, ''), content);
