package net.entityoutliner.ui;

import net.entityoutliner.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class ColorWidget extends ToggleWidget<Color> {

    public ColorWidget(int size, @Nonnull Color value, @Nonnull Callback<Color> callback) {
        super(size, value, callback, Text.translatable("options.chat.color"));
    }

    @Nonnull
    @Override
    protected Color getNextValue(@Nonnull final Color previous) {
        return Color.colors[(previous.ordinal() + 1) % Color.colors.length];
    }

    @Nullable
    @Override
    protected Tooltip getTooltip(@Nonnull final Color value) {
        return null;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int color = ColorHelper.Argb.getArgb(255, this.value.red, this.value.green, this.value.blue);
        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color);
        Color border = this.isFocused() ? Color.WHITE : Color.BLACK;
        context.drawBorder(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorHelper.Argb.getArgb(255, border.red, border.green, border.blue));
    }
}
