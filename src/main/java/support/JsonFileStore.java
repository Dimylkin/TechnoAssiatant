package support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import logic.json.JsonArray;
import logic.json.JsonObject;

/** Small JSON persistence helper for app config and request history. */
public final class JsonFileStore {
    private JsonFileStore() {
    }

    public static JsonObject readObject(Path path) throws IOException {
        if (!isReadable(path)) {
            return new JsonObject();
        }
        return new JsonObject(Files.readString(path, StandardCharsets.UTF_8));
    }

    public static JsonArray readArray(Path path) throws IOException {
        if (!isReadable(path)) {
            return new JsonArray();
        }
        String content = Files.readString(path, StandardCharsets.UTF_8).trim();
        return content.isEmpty() ? new JsonArray() : new JsonArray(content);
    }

    public static void writeObject(Path path, JsonObject object) throws IOException {
        write(path, object.toString(2));
    }

    public static void writeArray(Path path, JsonArray array) throws IOException {
        write(path, array.toString(2));
    }

    private static boolean isReadable(Path path) throws IOException {
        return Files.exists(path) && Files.size(path) > 0;
    }

    private static void write(Path path, String content) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }
}
