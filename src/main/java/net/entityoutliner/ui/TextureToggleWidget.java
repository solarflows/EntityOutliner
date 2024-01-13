package net.entityoutliner.ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;

@Environment(EnvType.CLIENT)
abstract class TextureToggleWidget<V> extends ToggleWidget<V> {

    protected TextureToggleWidget(int size, @Nonnull V value, @Nonnull Callback<V> callback, Text text) {
        super(size, value, callback, text);
    }

    @Nonnull
    abstract protected Identifier getTexture();

    abstract protected int getTextureSize();

    abstract protected int getTotalTextures();

    abstract protected int getValueIndex(@Nonnull V value);

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        final int index = this.getValueIndex(this.value);
        final int size = this.getTextureSize();
        final int length = getTotalTextures();
        context.drawTexture(getTexture(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), index * size, this.isFocused() ? size : 0, size, size, length * size, 2 * size);
    }
}
