package com.hamusuke.fallingattack.config.json;

import com.electronwill.nightconfig.core.file.FileWatcher;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonConfig {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();
    protected final JsonObject jsonObject = new JsonObject();
    private final File file;

    public JsonConfig(String fileName) {
        this.file = FMLPaths.CONFIGDIR.get().resolve(fileName + ".json").toFile();
        try {
            this.file.createNewFile();
            this.load();
            FileWatcher fileWatcher = FileWatcher.defaultInstance();
            fileWatcher.addWatch(this.file, () -> {
                LOGGER.info("Config file " + this.file.getName() + " changed, reloading");
                this.load();
            });
        } catch (Exception e) {
            LOGGER.warn("Error occurred while constructing json config", e);
        }
    }

    private synchronized void load() {
        try (FileReader fileReader = new FileReader(this.file, StandardCharsets.UTF_8)) {
            JsonObject jsonObject = GSON.fromJson(fileReader, JsonObject.class);
            if (jsonObject != null) {
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    this.jsonObject.add(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Error occurred while loading " + this.file.getName(), e);
        }
    }

    protected synchronized void save() {
        try (JsonWriter jsonWriter = new JsonWriter(new FileWriter(this.file, StandardCharsets.UTF_8))) {
            jsonWriter.setIndent("    ");
            GSON.toJson(this.jsonObject, jsonWriter);
            jsonWriter.flush();
        } catch (Exception e) {
            LOGGER.warn("Error occurred while saving json", e);
        }
    }
}
