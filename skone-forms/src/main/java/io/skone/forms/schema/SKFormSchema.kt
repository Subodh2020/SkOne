package io.skone.forms.schema

import io.skone.common.annotation.SKExperimental

/**
 * Dynamic form model interfaces shaped like a JSON Schema subset.
 *
 * **Interfaces only** — no JSON parser or network loader in this milestone.
 * Future modules / apps provide concrete implementations from JSON documents.
 */

/**
 * Root form schema.
 *
 * **Experimental** — schema interfaces may change before stabilization.
 */
@SKExperimental
public interface SKFormSchema {
    public val id: String
    public val title: String?
    public val description: String?
    public val fields: List<SKFormFieldSchema>
    public val required: Set<String>
}

/**
 * Single field schema entry.
 *
 * **Experimental** — schema interfaces may change before stabilization.
 */
@SKExperimental
public interface SKFormFieldSchema {
    public val id: String
    public val type: SKFormFieldType
    public val title: String?
    public val description: String?
    public val defaultValue: Any?
    public val validations: List<SKFormValidationSchema>
    public val format: String?
    public val mask: String?
    public val ai: SKFormFieldAISchema?
    public val metadata: Map<String, String>
}

/**
 * Supported logical field types for dynamic forms.
 *
 * **Experimental** — schema interfaces may change before stabilization.
 */
@SKExperimental
public enum class SKFormFieldType {
    String,
    Number,
    Boolean,
    Date,
    DateTime,
    Time,
    Email,
    Phone,
    Password,
    SingleSelect,
    MultiSelect,
    Object,
    Array,
    Unknown,
}

/**
 * Declarative validation descriptor (schema-level).
 *
 * **Experimental** — schema interfaces may change before stabilization.
 */
@SKExperimental
public interface SKFormValidationSchema {
    public val type: String
    public val message: String?
    public val params: Map<String, String>
}

/**
 * AI assist descriptor on a schema field.
 *
 * **Experimental** — schema interfaces may change before stabilization.
 */
@SKExperimental
public interface SKFormFieldAISchema {
    public val enabled: Boolean
    public val capabilities: Set<String>
    public val promptTemplate: String?
}

/**
 * Factory bridge: apps convert [SKFormSchema] → registered [io.skone.forms.field.SKFormField]s.
 * Implementation arrives with production form widgets / JSON module.
 *
 * **Experimental** — schema interfaces may change before stabilization.
 */
@SKExperimental
public interface SKFormSchemaBinder {
    public fun bind(schema: SKFormSchema): List<io.skone.forms.field.SKFormField>
}
