package net.entityoutliner;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;

import javax.annotation.Nonnull;

@Environment(EnvType.CLIENT)
public class OutlineConfig {

    private Color color;
    private boolean notification;
    private EntityAge age;

    public OutlineConfig(@Nonnull Color color, boolean notification, @Nonnull EntityAge age) {
        this.color = color;
        this.notification = notification;
        this.age = age;
    }

    @Nonnull
    public Color getColor() {
        return this.color;
    }

    public void setColor(@Nonnull Color color) {
        this.color = color;
    }

    public boolean isNotification() {
        return this.notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    @Nonnull
    public EntityAge getAge() {
        return this.age;
    }

    public void setAge(@Nonnull EntityAge age) {
        this.age = age;
    }

    @Nonnull
    public static OutlineConfig of(@Nonnull EntityType<?> entityType) {
        return new OutlineConfig(Color.of(entityType.getSpawnGroup()), false, EntityAge.BOTH);
    }
}