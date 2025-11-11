package com.cucumbergen.backend.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Tests for {@link SQLitePlatform}.
 */
class SQLitePlatformTest {

    private final SQLitePlatform platform = new SQLitePlatform();

    @Test
    void supportsIdentityStrategy() {
        assertTrue(platform.supportsIdentity(), "SQLite should support identity columns");
        assertFalse(platform.supportsSequenceObjects(), "SQLite does not support sequence objects");
    }

    @Test
    @SuppressWarnings("unchecked")
    void fieldTypesMatchSQLiteAffinity() {
        Hashtable<Class<?>, FieldTypeDefinition> fieldTypes =
                (Hashtable<Class<?>, FieldTypeDefinition>)
                        ReflectionTestUtils.invokeMethod(platform, "buildFieldTypes");

        assertEquals("TEXT", fieldTypes.get(String.class).getName(), "Strings map to TEXT");
        assertEquals("INTEGER", fieldTypes.get(Boolean.class).getName(), "Booleans map to INTEGER");
        assertEquals("REAL", fieldTypes.get(Double.class).getName(), "Floating points map to REAL");
        assertEquals("BLOB", fieldTypes.get(byte[].class).getName(), "Binary data maps to BLOB");
    }

    @Test
    void identityQueryUsesLastInsertRowId() {
        ValueReadQuery query = platform.buildSelectQueryForIdentity();
        assertEquals("SELECT last_insert_rowid()", query.getSQLString(),
                "SQLite identity query should read last_insert_rowid()");
    }
}
