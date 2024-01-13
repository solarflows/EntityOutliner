package net.entityoutliner.ui;

import net.entityoutliner.EntityOutliner;
import net.entityoutliner.OutlineConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Environment(EnvType.CLIENT)
public class EntityListWidget extends ElementListWidget<EntityListWidget.Entry> {

    public EntityListWidget(MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
        this.centerListVertically = false;
    }

    public void addListEntry(EntityListWidget.Entry entry) {
        super.addEntry(entry);
    }

    public void clearListEntries() {
        super.clearEntries();
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPositionX() {
        // make positioning more flexible
        return (this.width + this.getRowWidth()) / 2 + 14;
    }

    @Environment(EnvType.CLIENT)
    public static abstract class Entry extends ElementListWidget.Entry<EntityListWidget.Entry> {
    }

    @Environment(EnvType.CLIENT)
    public static class EntityEntry extends EntityListWidget.Entry {
        private final static int DEFAULT_SIZE = 20;

        private final CheckboxWidget checkbox;
        private final ClickableWidget[] options;
        private final List<ClickableWidget> children;

        private EntityEntry(CheckboxWidget checkbox, ClickableWidget... options) {
            this.checkbox = checkbox;
            this.options = options;

            final ClickableWidget[] children = new ClickableWidget[options.length + 1];
            children[0] = checkbox;
            System.arraycopy(options, 0, children, 1, options.length);
            this.children = List.of(children);
        }

        public static EntityListWidget.EntityEntry create(EntityType<?> entityType, TextRenderer font) {
            final boolean visible = EntityOutliner.entityTypeOutlineConfig.containsKey(entityType);
            final OutlineConfig outlineConfig = EntityOutliner.entityTypeOutlineConfig.getOrDefault(entityType, OutlineConfig.of(entityType));

            final ColorWidget color = new ColorWidget(DEFAULT_SIZE, outlineConfig.getColor(),
                (source, value) -> outlineConfig.setColor(value)
            );
            color.visible = visible;

            final NotificationWidget notify = new NotificationWidget(
                DEFAULT_SIZE,
                outlineConfig.isNotification(),
                (source, value) -> outlineConfig.setNotification(value)
            );
            notify.visible = visible;

            final CheckboxWidget checkbox = CheckboxWidget.builder(entityType.getName(), font)
                .checked(visible)
                .callback((source, checked) -> {
                    color.visible = checked;
                    notify.visible = checked;
                    if (!checked) {
                        EntityOutliner.entityTypeOutlineConfig.remove(entityType);
                    } else {
                        EntityOutliner.entityTypeOutlineConfig.put(entityType, outlineConfig);
                    }
                })
                .build();

            return new EntityListWidget.EntityEntry(checkbox, color, notify);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final int gap = 5;
            int width = entryWidth - gap * this.options.length;
            for (ClickableWidget e : this.options) {
                width -= e.getWidth();
            }
            this.checkbox.setWidth(width);

            for (ClickableWidget c : this.children) {
                c.setDimensionsAndPosition(c.getWidth(), entryHeight, x, y);
                c.render(context, mouseX, mouseY, tickDelta);
                x += c.getWidth() + gap;
            }
        }

        @Override
        public List<? extends Element> children() {
            return this.children;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.children;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class HeaderEntry extends EntityListWidget.Entry {

        private final TextWidget title;
        private final List<TextWidget> children;

        private HeaderEntry(TextWidget title) {
            this.title = title;
            this.children = List.of(title);
        }

        public static EntityListWidget.HeaderEntry create(SpawnGroup category, TextRenderer font) {
            Text title;
            if (category != null) {
                StringBuilder builder = new StringBuilder();
                for (String term : category.getName().split("\\p{Punct}|\\p{Space}")) {
                    builder.append(StringUtils.capitalize(term));
                    builder.append(' ');
                }
                builder.deleteCharAt(builder.length() - 1);
                title = Text.of(builder.toString());
            } else {
                title = Text.translatable("gui.entity-outliner.no_results");
            }
            TextWidget text = new TextWidget(title, font);
            return new EntityListWidget.HeaderEntry(text);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.title.setDimensionsAndPosition(entryWidth, entryHeight, x, y);
            this.title.render(context, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Element> children() {
            return this.children;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }
    }
}
