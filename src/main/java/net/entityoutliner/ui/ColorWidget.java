package net.entityoutliner.ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class ColorWidget extends PressableWidget {
    private Color color;
    private final EntityType<?> entityType;

    private ColorWidget(int x, int y, int width, int height, Text message, EntityType<?> entityType) {
        super(x, y, width, height, message);
        this.entityType = entityType;

        if (EntitySelector.outlinedEntityTypes.containsKey(this.entityType))
            onShow();
    }

    public ColorWidget(int x, int y, int width, int height, EntityType<?> entityType) {
        this(x, y, width, height, Text.translatable("options.chat.color"), entityType);
    }

    public void onShow() {
        this.color = EntitySelector.outlinedEntityTypes.get(this.entityType);
    }

    @Override
    public void onPress() {
        this.color = this.color.next();
        EntitySelector.outlinedEntityTypes.put(this.entityType, this.color);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int color = ColorHelper.Argb.getArgb(255, this.color.red, this.color.green, this.color.blue);
        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color);
        Color border = this.isFocused() ? Color.WHITE : Color.BLACK;
        context.drawBorder(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorHelper.Argb.getArgb(255, border.red, border.green, border.blue));
    }

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

        private final static Color[] colors = Color.values();

        Color(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public static Color of(SpawnGroup group) {
            return spawnGroupColors.get(group);
        }

        public Color next() {
            return get((this.ordinal() + 1) % colors.length);
        }

        public Color get(int index) {
            return colors[index];
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
