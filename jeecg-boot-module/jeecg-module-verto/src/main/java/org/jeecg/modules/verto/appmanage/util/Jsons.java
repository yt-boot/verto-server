package org.jeecg.modules.verto.appmanage.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jsons {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static JsonNode parseOrNull(String json) {
        if (json == null || json.isBlank()) return null;
        try { return MAPPER.readTree(json); } catch (Exception e) { return null; }
    }

    public static String getString(JsonNode node, String field, String defaultVal) {
        if (node == null) return defaultVal;
        JsonNode v = node.get(field);
        return v == null || v.isNull() ? defaultVal : v.asText();
    }

    public static boolean getBoolean(JsonNode node, String field, boolean defaultVal) {
        if (node == null) return defaultVal;
        JsonNode v = node.get(field);
        return v == null || v.isNull() ? defaultVal : v.asBoolean();
    }

    public static Integer getInteger(JsonNode node, String field, Integer defaultVal) {
        if (node == null) return defaultVal;
        JsonNode v = node.get(field);
        return v == null || v.isNull() ? defaultVal : v.isInt() ? v.intValue() : tryParseInt(v.asText(), defaultVal);
    }

    public static String getString(String json, String field, String defaultVal) {
        return getString(parseOrNull(json), field, defaultVal);
    }

    public static boolean getBoolean(String json, String field, boolean defaultVal) {
        return getBoolean(parseOrNull(json), field, defaultVal);
    }

    public static int getInteger(String json, String field, int defaultVal) {
        Integer v = getInteger(parseOrNull(json), field, defaultVal);
        return v == null ? defaultVal : v;
    }

    public static String toJsonString(Object object) {
        if (object == null) return null;
        try { return MAPPER.writeValueAsString(object); } catch (Exception e) { return null; }
    }

    private static Integer tryParseInt(String s, Integer defaultVal) {
        if (s == null) return defaultVal;
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return defaultVal; }
    }
}