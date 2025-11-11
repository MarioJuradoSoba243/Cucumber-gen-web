package com.cucumbergen.backend.entity;

import com.cucumbergen.backend.util.JsonMapConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a test case with dynamic attributes.
 */
@Entity
@Table(name = "test_case")
public class TestCase extends AbstractAuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "folder_path", nullable = false)
    private String folderPath = "";

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "attributes", columnDefinition = "TEXT")
    private Map<String, Object> attributes = new HashMap<>();

    @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<TestAttributeValue> attributeValues = new HashSet<>();

    @Transient
    private boolean attributesInitialized;

    /**
     * Ensures the identifier is always populated before persisting.
     */
    @PrePersist
    protected void ensureIdentifier() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributesInitialized = true;
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesInitialized = true;
    }

    public Set<TestAttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(Set<TestAttributeValue> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public boolean isAttributesInitialized() {
        return attributesInitialized;
    }
}
