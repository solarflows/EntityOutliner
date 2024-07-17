package net.entityoutliner.ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
abstract class ToggleWidget<V> extends ClickableWidget {
    protected V value;
    private final Callback<V> callback;

    protected ToggleWidget(int size, @Nonnull V value, @Nonnull Callback<V> callback, Text text) {
        super(0, 0, size, size, text);
        this.callback = callback;

        setValue(value);
    }

    @Nonnull
    abstract protected V getNextValue(@Nonnull V previous);

    @Nullable
    abstract protected Tooltip getTooltip(@Nonnull V value);

    @Override
    public void onClick(double mouseX, double mouseY) {
        setValue(this.getNextValue(this.value));
        if (this.callback != null) {
            this.callback.onChange(this, this.value);
        }
    }

    protected void setValue(@Nonnull V value) {
        this.value = value;
        this.setTooltip(getTooltip(this.value));
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    @Environment(EnvType.CLIENT)
    public interface Callback<V> {

        void onChange(@Nonnull ToggleWidget<V> source, @Nonnull V value);
    }
}
