package net.entityoutliner;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;

@Environment(EnvType.CLIENT)
public class OutlineConfig {

    private Color color;
    private boolean notification;
    private EntityAge age;

    public OutlineConfig(Color color, boolean notification, EntityAge age) {
        this.color = color;
        this.notification = notification;
        this.age = age;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isNotification() {
        return this.notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public EntityAge getAge() {
        return this.age;
    }

    public void setAge(EntityAge age) {
        this.age = age;
    }

    public static OutlineConfig of(EntityType<?> entityType) {
        return new OutlineConfig(Color.of(entityType.getSpawnGroup()), false, EntityAge.BOTH);
    }
}