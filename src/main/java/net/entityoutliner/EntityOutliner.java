package net.entityoutliner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.entityoutliner.ui.ColorWidget.Color;
import net.entityoutliner.ui.EntitySelector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityType;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class EntityOutliner implements ClientModInitializer {
    private static final Gson GSON = new Gson();
    public static boolean outliningEntities;

    @Override
    public void onInitializeClient() {
        final KeyBinding config = new KeyBinding(
            "key.entity-outliner.selector",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_SEMICOLON,
            "title.entity-outliner.title"
        );

        final KeyBinding outline = new KeyBinding(
            "key.entity-outliner.outline",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "title.entity-outliner.title"
        );

        KeyBindingHelper.registerKeyBinding(config);
        KeyBindingHelper.registerKeyBinding(outline);

        loadConfig();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (outline.wasPressed()) {
                outliningEntities = !outliningEntities;
            }

            if (config.isPressed()) {
                client.setScreen(new EntitySelector(null));
            }
        });
    }

    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("entityoutliner.json");
    }

    public static void saveConfig() {
        final JsonObject config = new JsonObject();

        JsonArray outlinedEntities = new JsonArray();
        for (Map.Entry<EntityType<?>, Color> entry : EntitySelector.outlinedEntityTypes.entrySet()) {
            final JsonArray list = new JsonArray(2);
            list.add(EntityType.getId(entry.getKey()).toString());
            list.add(entry.getValue().name());
            outlinedEntities.add(list);
        }
        config.add("outlinedEntities", outlinedEntities);

        try {
            Files.write(getConfigPath(), GSON.toJson(config).getBytes());
        } catch (IOException ex) {
            logException(ex, "Failed to save EntityOutliner config");
        }
    }

    private void loadConfig() {
        try {
            JsonObject config = GSON.fromJson(new String(Files.readAllBytes(getConfigPath())), JsonObject.class);
            if (config.has("outlinedEntities")) {
                final JsonArray outlinedEntities = config.getAsJsonArray("outlinedEntities");
                for (JsonElement e : outlinedEntities) {
                    final JsonArray list = e.getAsJsonArray();
                    final Optional<EntityType<?>> entityType = list.size() > 0 ? EntityType.get(list.get(0).getAsString()) : Optional.empty();
                    if (entityType.isEmpty()) {
                        continue;
                    }
                    final Optional<Color> color = list.size() > 1 ? Color.of(list.get(1).getAsString()) : Optional.empty();

                    EntitySelector.outlinedEntityTypes.put(entityType.get(), color.orElse(Color.of(entityType.get().getSpawnGroup())));
                }
            }
        } catch (Throwable ex) {
            logException(ex, "Failed to load EntityOutliner config");
        }
    }

    public static void logException(Throwable ex, String message) {
        System.err.printf("[EntityOutliner] %s (%s: %s)", message, ex.getClass().getSimpleName(), ex.getLocalizedMessage());
    }
}