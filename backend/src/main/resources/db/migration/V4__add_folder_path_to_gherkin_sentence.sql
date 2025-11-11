ALTER TABLE gherkin_sentence
    DROP CONSTRAINT IF EXISTS gherkin_sentence_content_key;

DROP INDEX IF EXISTS idx_gherkin_sentence_content;

ALTER TABLE gherkin_sentence
    ADD COLUMN folder_path VARCHAR(255);

CREATE UNIQUE INDEX IF NOT EXISTS ux_gherkin_sentence_folder_path_content
    ON gherkin_sentence (COALESCE(folder_path, ''), content);
