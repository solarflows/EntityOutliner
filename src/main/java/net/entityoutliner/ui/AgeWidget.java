package net.entityoutliner.ui;

import net.entityoutliner.EntityAge;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AgeWidget extends TextureToggleWidget<EntityAge> {
    private final static Identifier TEXTURE = new Identifier("entityoutliner", "textures/gui/age_widget.png");
    private final static Tooltip TOOLTIP_BABY = Tooltip.of(Text.translatable("button.entity-outliner.age.tooltip-baby"));
    private final static Tooltip TOOLTIP_ADULT = Tooltip.of(Text.translatable("button.entity-outliner.age.tooltip-adult"));
    private final static Tooltip TOOLTIP_BOTH = Tooltip.of(Text.translatable("button.entity-outliner.age.tooltip-both"));

    public AgeWidget(int size, EntityAge value, Callback<EntityAge> callback) {
        super(size, value, callback, Text.empty());
    }

    @Override
    protected Identifier getTexture() {
        return TEXTURE;
    }

    @Override
    protected int getTextureSize() {
        return 20;
    }

    @Override
    protected int getTotalTextures() {
        return EntityAge.ages.length;
    }

    @Override
    protected EntityAge getNextValue(final EntityAge previous) {
        return EntityAge.ages[(previous.ordinal() + 1) % EntityAge.ages.length];
    }

    @Override
    protected int getValueIndex(final EntityAge value) {
        return value.ordinal();
    }

    @Override
    protected Tooltip getTooltip(final EntityAge value) {
        return switch (value) {
            case BABY -> TOOLTIP_BABY;
            case ADULT -> TOOLTIP_ADULT;
            case BOTH -> TOOLTIP_BOTH;
        };
    }
}
