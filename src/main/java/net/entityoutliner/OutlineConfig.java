package net.entityoutliner;

import net.entityoutliner.ui.ColorWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;

@Environment(EnvType.CLIENT)
public class OutlineConfig {

    private ColorWidget.Color color;
    private boolean notification;

    public OutlineConfig(ColorWidget.Color color, boolean notification) {
        this.color = color;
        this.notification = notification;
    }

    public ColorWidget.Color getColor() {
        return this.color;
    }

    public void setColor(ColorWidget.Color color) {
        this.color = color;
    }

    public boolean isNotification() {
        return this.notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public static OutlineConfig of(EntityType<?> entityType) {
        return new OutlineConfig(ColorWidget.Color.of(entityType.getSpawnGroup()), false);
    }
}
