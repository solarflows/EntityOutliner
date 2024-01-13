package net.entityoutliner;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.ColorHelper;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public enum Color {
    WHITE(255, 255, 255),
    BLACK(0, 0, 0),
    RED(255, 0, 0),
    ORANGE(255, 127, 0),
    YELLOW(255, 255, 0),
    GREEN(0, 255, 0),
    BLUE(0, 0, 255),
    PURPLE(127, 0, 127),
    PINK(255, 155, 182);

    public final int red;
    public final int green;
    public final int blue;

    private static final Map<SpawnGroup, Color> spawnGroupColors = Map.of(
        SpawnGroup.AMBIENT, Color.PURPLE,
        SpawnGroup.AXOLOTLS, Color.PINK,
        SpawnGroup.CREATURE, Color.YELLOW,
        SpawnGroup.MISC, Color.WHITE,
        SpawnGroup.MONSTER, Color.RED,
        SpawnGroup.UNDERGROUND_WATER_CREATURE, Color.ORANGE,
        SpawnGroup.WATER_AMBIENT, Color.GREEN,
        SpawnGroup.WATER_CREATURE, Color.BLUE
    );

    public final static Color[] colors = Color.values();

    Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public static Optional<Color> of(String name) {
        return Arrays.stream(colors)
            .filter(c -> c.name().equals(name))
            .findFirst();
    }

    public static Color of(SpawnGroup group) {
        return spawnGroupColors.getOrDefault(group, Color.RED);
    }

    public int toRGB() {
        return ColorHelper.Argb.getArgb(255, this.red, this.green, this.blue);
    }
}
