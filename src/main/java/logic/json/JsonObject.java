package logic.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/** Minimal Gson-backed JSON object adapter used by the legacy UI code. */
public final class JsonObject {
    private static final Gson GSON = new Gson();
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    private final com.google.gson.JsonObject delegate;

    public JsonObject() {
        this.delegate = new com.google.gson.JsonObject();
    }

    public JsonObject(String json) {
        JsonElement element = JsonParser.parseString(json == null || json.isBlank() ? "{}" : json);
        this.delegate = element.isJsonObject() ? element.getAsJsonObject() : new com.google.gson.JsonObject();
    }

    JsonObject(com.google.gson.JsonObject delegate) {
        this.delegate = delegate == null ? new com.google.gson.JsonObject() : delegate;
    }

    com.google.gson.JsonObject raw() {
        return delegate;
    }

    public boolean has(String key) {
        return delegate.has(key);
    }

    public boolean isNull(String key) {
        return !delegate.has(key) || delegate.get(key).isJsonNull();
    }

    public String getString(String key) {
        JsonElement value = delegate.get(key);
        if (value == null || value.isJsonNull()) {
            throw new IllegalArgumentException("Missing JSON string key: " + key);
        }
        return value.getAsString();
    }

    public String optString(String key, String fallback) {
        JsonElement value = delegate.get(key);
        return value == null || value.isJsonNull() ? fallback : value.getAsString();
    }

    public double optDouble(String key, double fallback) {
        JsonElement value = delegate.get(key);
        if (value == null || value.isJsonNull()) {
            return fallback;
        }
        try {
            return value.getAsDouble();
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    public JsonObject getJSONObject(String key) {
        JsonElement value = delegate.get(key);
        if (value == null || !value.isJsonObject()) {
            throw new IllegalArgumentException("Missing JSON object key: " + key);
        }
        return new JsonObject(value.getAsJsonObject());
    }

    public JsonObject put(String key, Object value) {
        if (value == null) {
            delegate.add(key, JsonNull.INSTANCE);
        } else if (value instanceof Number number) {
            delegate.add(key, new JsonPrimitive(number));
        } else if (value instanceof Boolean bool) {
            delegate.add(key, new JsonPrimitive(bool));
        } else if (value instanceof Character character) {
            delegate.add(key, new JsonPrimitive(character));
        } else if (value instanceof JsonObject object) {
            delegate.add(key, object.raw());
        } else if (value instanceof JsonArray array) {
            delegate.add(key, array.raw());
        } else {
            delegate.add(key, new JsonPrimitive(value.toString()));
        }
        return this;
    }

    @Override
    public String toString() {
        return GSON.toJson(delegate);
    }

    public String toString(int indentFactor) {
        return indentFactor > 0 ? PRETTY_GSON.toJson(delegate) : toString();
    }
}
