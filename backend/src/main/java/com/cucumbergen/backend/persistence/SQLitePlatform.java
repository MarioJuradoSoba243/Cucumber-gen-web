package com.cucumbergen.backend.persistence;

import java.math.BigDecimal;
import java.util.Hashtable;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.queries.ValueReadQuery;

/**
 * EclipseLink database platform tailored for SQLite dialect specifics.
 */
public class SQLitePlatform extends DatabasePlatform {

    /**
     * Creates a new platform with sensible defaults for SQLite.
     */
    public SQLitePlatform() {
        super();
        this.pingSQL = "SELECT 1";
        this.driverName = "org.sqlite.JDBC";
    }

    @Override
    protected Hashtable<Class<?>, FieldTypeDefinition> buildFieldTypes() {
        Hashtable<Class<?>, FieldTypeDefinition> fieldTypes = super.buildFieldTypes();
        fieldTypes.put(Boolean.class, new FieldTypeDefinition("INTEGER"));
        fieldTypes.put(Boolean.TYPE, new FieldTypeDefinition("INTEGER"));
        fieldTypes.put(Integer.class, new FieldTypeDefinition("INTEGER"));
        fieldTypes.put(Integer.TYPE, new FieldTypeDefinition("INTEGER"));
        fieldTypes.put(Long.class, new FieldTypeDefinition("INTEGER"));
        fieldTypes.put(Long.TYPE, new FieldTypeDefinition("INTEGER"));
        fieldTypes.put(Short.class, new FieldTypeDefinition("INTEGER"));
        fieldTypes.put(Short.TYPE, new FieldTypeDefinition("INTEGER"));
        fieldTypes.put(Byte.class, new FieldTypeDefinition("INTEGER"));
        fieldTypes.put(Byte.TYPE, new FieldTypeDefinition("INTEGER"));
        fieldTypes.put(Float.class, new FieldTypeDefinition("REAL"));
        fieldTypes.put(Float.TYPE, new FieldTypeDefinition("REAL"));
        fieldTypes.put(Double.class, new FieldTypeDefinition("REAL"));
        fieldTypes.put(Double.TYPE, new FieldTypeDefinition("REAL"));
        fieldTypes.put(BigDecimal.class, new FieldTypeDefinition("NUMERIC"));
        fieldTypes.put(String.class, new FieldTypeDefinition("TEXT"));
        fieldTypes.put(java.sql.Date.class, new FieldTypeDefinition("TEXT"));
        fieldTypes.put(java.sql.Time.class, new FieldTypeDefinition("TEXT"));
        fieldTypes.put(java.sql.Timestamp.class, new FieldTypeDefinition("TEXT"));
        fieldTypes.put(byte[].class, new FieldTypeDefinition("BLOB"));
        return fieldTypes;
    }

    @Override
    public boolean supportsIdentity() {
        return true;
    }

    @Override
    public boolean supportsSequenceObjects() {
        return false;
    }

    @Override
    public ValueReadQuery buildSelectQueryForIdentity() {
        return new ValueReadQuery("SELECT last_insert_rowid()");
    }

    @Override
    public String getSelectForUpdateString() {
        return "";
    }

    @Override
    public boolean supportsForeignKeyConstraints() {
        return true;
    }

    @Override
    public boolean supportsGlobalTempTables() {
        return false;
    }

    @Override
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }

    @Override
    protected String getCreateTempTableSqlSuffix() {
        return "";
    }
}
