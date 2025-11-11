package com.cucumbergen.backend.dto.generation;

/**
 * Defines how missing attribute values should be handled during generation.
 */
public enum MissingPolicy {
    /** Skip test cases missing values. */
    SKIP,
    /** Substitute defaults for missing values. */
    DEFAULT,
    /** Leave placeholders untouched when values are missing. */
    KEEP
}
