package logic.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/** Minimal Gson-backed JSON array adapter used by the legacy UI code. */
public final class JsonArray {
    private static final Gson GSON = new Gson();
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    private final com.google.gson.JsonArray delegate;

    public JsonArray() {
        this.delegate = new com.google.gson.JsonArray();
    }

    public JsonArray(String json) {
        JsonElement element = JsonParser.parseString(json == null || json.isBlank() ? "[]" : json);
        this.delegate = element.isJsonArray() ? element.getAsJsonArray() : new com.google.gson.JsonArray();
    }

    JsonArray(com.google.gson.JsonArray delegate) {
        this.delegate = delegate == null ? new com.google.gson.JsonArray() : delegate;
    }

    com.google.gson.JsonArray raw() {
        return delegate;
    }

    public int length() {
        return delegate.size();
    }

    public JsonObject optJSONObject(int index) {
        if (index < 0 || index >= delegate.size()) {
            return null;
        }
        JsonElement value = delegate.get(index);
        return value != null && value.isJsonObject() ? new JsonObject(value.getAsJsonObject()) : null;
    }

    public JsonArray put(JsonObject object) {
        delegate.add(object == null ? null : object.raw());
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
