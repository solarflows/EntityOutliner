package net.entityoutliner.ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class NotificationWidget extends TextureToggleWidget<Boolean> {
    private final static Identifier TEXTURE = new Identifier("entityoutliner", "textures/gui/notification_widget.png");
    private final static Tooltip TOOLTIP_ON = Tooltip.of(Text.translatable("button.entity-outliner.notification.tooltip-on"));
    private final static Tooltip TOOLTIP_OFF = Tooltip.of(Text.translatable("button.entity-outliner.notification.tooltip-off"));

    public NotificationWidget(int size, boolean value, @Nonnull Callback<Boolean> callback) {
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
        return 2;
    }

    @Nonnull
    @Override
    protected Boolean getNextValue(@Nonnull final Boolean previous) {
        return !previous;
    }

    @Override
    protected int getValueIndex(@Nonnull final Boolean value) {
        return value ? 0 : 1;
    }

    @Nullable
    @Override
    protected Tooltip getTooltip(@Nonnull final Boolean value) {
        return value ? TOOLTIP_ON : TOOLTIP_OFF;
    }
}
