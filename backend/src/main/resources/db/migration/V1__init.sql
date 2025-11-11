CREATE TABLE IF NOT EXISTS attribute_definition (
    id TEXT PRIMARY KEY,
    attribute_key TEXT NOT NULL UNIQUE,
    attribute_type TEXT NOT NULL,
    allowed_values TEXT,
    required INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS test_case (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    attributes TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS test_attribute_value (
    id TEXT PRIMARY KEY,
    test_case_id TEXT NOT NULL,
    attribute_definition_id TEXT NOT NULL,
    value TEXT,
    FOREIGN KEY (test_case_id) REFERENCES test_case(id) ON DELETE CASCADE,
    FOREIGN KEY (attribute_definition_id) REFERENCES attribute_definition(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS template (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    content TEXT NOT NULL,
    description TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_attribute_definition_key ON attribute_definition(attribute_key);
CREATE INDEX IF NOT EXISTS idx_test_attribute_value_test_case ON test_attribute_value(test_case_id);
