package net.entityoutliner.ui;

import net.entityoutliner.EntityAge;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class AgeWidget extends TextureToggleWidget<EntityAge> {
    private final static Identifier TEXTURE = new Identifier("entityoutliner", "textures/gui/age_widget.png");
    private final static Tooltip TOOLTIP_BABY = Tooltip.of(Text.translatable("button.entity-outliner.age.tooltip-baby"));
    private final static Tooltip TOOLTIP_ADULT = Tooltip.of(Text.translatable("button.entity-outliner.age.tooltip-adult"));
    private final static Tooltip TOOLTIP_BOTH = Tooltip.of(Text.translatable("button.entity-outliner.age.tooltip-both"));

    public AgeWidget(int size, @Nonnull EntityAge value, @Nonnull Callback<EntityAge> callback) {
        super(size, value, callback, Text.empty());
    }

    @Nonnull
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

    @Nonnull
    @Override
    protected EntityAge getNextValue(@Nonnull final EntityAge previous) {
        return EntityAge.ages[(previous.ordinal() + 1) % EntityAge.ages.length];
    }

    @Override
    protected int getValueIndex(@Nonnull final EntityAge value) {
        return value.ordinal();
    }

    @Nullable
    @Override
    protected Tooltip getTooltip(@Nonnull final EntityAge value) {
        return switch (value) {
            case BABY -> TOOLTIP_BABY;
            case ADULT -> TOOLTIP_ADULT;
            case BOTH -> TOOLTIP_BOTH;
        };
    }
}
