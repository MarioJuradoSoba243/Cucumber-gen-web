package com.cucumbergen.backend.entity;

import com.cucumbergen.backend.util.JsonListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Defines a reusable attribute that can be associated with a test case.
 */
@Entity
@Table(name = "attribute_definition")
public class AttributeDefinition {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "attribute_key", nullable = false, unique = true)
    private String key;

    @Column(name = "attribute_type", nullable = false)
    private String type;

    @Convert(converter = JsonListConverter.class)
    @Column(name = "allowed_values", columnDefinition = "TEXT")
    private List<String> allowedValues = new ArrayList<>();

    @Column(name = "required", nullable = false)
    private boolean required;

    /**
     * Ensures ids are populated for new entities.
     */
    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<String> allowedValues) {
        this.allowedValues = allowedValues;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
