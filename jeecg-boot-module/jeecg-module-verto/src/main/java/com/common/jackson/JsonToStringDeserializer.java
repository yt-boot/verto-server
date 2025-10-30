package com.verto.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * A flexible deserializer that converts any JSON value (object/array/primitive)
 * into its String representation. Useful for entity fields that persist JSON
 * as TEXT/VARCHAR in the database, while allowing clients to send structured JSON.
 */
public class JsonToStringDeserializer extends JsonDeserializer<String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.getCurrentToken();
        // If the value is already a string, return as-is
        if (token == JsonToken.VALUE_STRING) {
            return p.getValueAsString();
        }
        // Otherwise, read as tree and serialize to string
        JsonNode node = p.readValueAsTree();
        return mapper.writeValueAsString(node);
    }
}