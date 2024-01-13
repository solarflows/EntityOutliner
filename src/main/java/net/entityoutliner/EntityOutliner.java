package net.entityoutliner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.entityoutliner.ui.ColorWidget;
import net.entityoutliner.ui.EntitySelector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EntityOutliner implements ClientModInitializer {
    private static final Gson GSON = new Gson();

    public static boolean outliningEntities;
    public static boolean preferTeamColor = true;
    public final static Map<EntityType<?>, OutlineConfig> entityTypeOutlineConfig = new HashMap<>();

    public final static Set<EntityType<?>> babyTypes = Set.of(
        EntityType.BEE,
        EntityType.CAT,
        EntityType.CHICKEN,
        EntityType.COW,
        EntityType.DONKEY,
        EntityType.FOX,
        EntityType.HOGLIN,
        EntityType.HORSE,
        EntityType.MOOSHROOM,
        EntityType.MULE,
        EntityType.PANDA,
        EntityType.POLAR_BEAR,
        EntityType.OCELOT,
        EntityType.PIG,
        EntityType.PIGLIN,
        EntityType.RABBIT,
        EntityType.SALMON,
        EntityType.SHEEP,
        EntityType.STRIDER,
        EntityType.TURTLE,
        EntityType.GOAT,
        EntityType.VILLAGER,
        EntityType.WOLF,
        EntityType.ZOMBIE,
        EntityType.ZOMBIFIED_PIGLIN
    );

    public static boolean shouldOutline(Entity entity) {
        if (!outliningEntities) {
            return false;
        }
        final OutlineConfig outlineConfig = EntityOutliner.entityTypeOutlineConfig.get(entity.getType());

        return outlineConfig != null && (
            outlineConfig.getAge() == EntityAge.BOTH ||
                !(entity instanceof LivingEntity livingEntity) ||
                (livingEntity.isBaby() ^ outlineConfig.getAge() == EntityAge.ADULT)
        );
    }

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

        final MutableText chatPrefix = Text.literal("[").formatted(Formatting.GRAY)
            .append(Text.translatable("title.entity-outliner.title")
                .formatted(Formatting.RED)
            ).append(Text.of("]")
            ).append(Text.literal(" ").formatted(Formatting.RESET));

        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            final MinecraftClient client = MinecraftClient.getInstance();
            final EntityType<?> entityType = entity.getType();
            if (outliningEntities && client.player != null && isNotificationEnabled(entityType)) {
                final OutlineConfig outlineConfig = entityTypeOutlineConfig.get(entityType);
                final ColorWidget.Color color = outlineConfig.getColor();
                client.player.sendMessage(chatPrefix.copy().append(
                        Text.translatable("chat.entity-outliner.found",
                            entity.getType().getName()
                                .copy()
                                .withColor(color.toRGB()),
                            Text.literal(entity.getPos().toString())
                                .withColor(color.toRGB())
                        )),
                    false
                );
            }
        });
    }

    private static boolean isNotificationEnabled(EntityType<?> entityType) {
        final OutlineConfig outlineConfig = entityTypeOutlineConfig.get(entityType);
        return outlineConfig != null && outlineConfig.isNotification();
    }

    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("entityoutliner.json");
    }

    public static void saveConfig() {
        final JsonObject config = new JsonObject();

        JsonArray outlinedEntities = new JsonArray();
        for (Map.Entry<EntityType<?>, OutlineConfig> entry : entityTypeOutlineConfig.entrySet()) {
            final OutlineConfig outlineConfig = entry.getValue();
            final JsonArray list = new JsonArray(4);
            list.add(EntityType.getId(entry.getKey()).toString());
            list.add(outlineConfig.getColor().name());
            if (outlineConfig.isNotification() || outlineConfig.getAge() != EntityAge.BOTH) {
                // use new config format only if required
                list.add(outlineConfig.isNotification());
                if (outlineConfig.getAge() != EntityAge.BOTH) {
                    list.add(outlineConfig.getAge().name());
                }
            }
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
                    final Optional<ColorWidget.Color> color = list.size() > 1 ? ColorWidget.Color.of(list.get(1).getAsString()) : Optional.empty();
                    final boolean notification = list.size() > 2 && list.get(2).getAsBoolean();
                    final Optional<EntityAge> age = list.size() > 3 && babyTypes.contains(entityType.get()) ? EntityAge.of(list.get(3).getAsString()) : Optional.empty();

                    entityTypeOutlineConfig.put(entityType.get(), new OutlineConfig(
                        color.orElse(ColorWidget.Color.of(entityType.get().getSpawnGroup())),
                        notification,
                        age.orElse(EntityAge.BOTH)
                    ));
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