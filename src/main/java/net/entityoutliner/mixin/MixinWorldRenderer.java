package net.entityoutliner.mixin;

import net.entityoutliner.Color;
import net.entityoutliner.EntityOutliner;
import net.entityoutliner.OutlineConfig;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    @Inject(method = "renderEntity", at = @At("HEAD"))
    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (EntityOutliner.outliningEntities
            && vertexConsumers instanceof OutlineVertexConsumerProvider outlineVertexConsumers
            && EntityOutliner.shouldOutline(entity)) {

            Integer colorValue = null;
            if (EntityOutliner.preferTeamColor) {
                colorValue = getTeamColor(entity);
            }

            final OutlineConfig outlineConfig = EntityOutliner.entityTypeOutlineConfig.get(entity.getType());

            int red, green, blue;
            if (colorValue != null) {
                red = ColorHelper.Argb.getRed(colorValue);
                green = ColorHelper.Argb.getGreen(colorValue);
                blue = ColorHelper.Argb.getBlue(colorValue);
            } else {
                final Color color = outlineConfig.getColor();
                red = color.red;
                green = color.green;
                blue = color.blue;
            }

            outlineVertexConsumers.setColor(red, green, blue, 255);
        }
    }

    @Nullable
    private static Integer getTeamColor(@Nonnull Entity entity) {
        final Team team = entity.getScoreboardTeam();
        if (team == null) {
            return null;
        }

        final Formatting teamColor = team.getColor();
        if (teamColor == null) {
            return null;
        }

        if (!teamColor.isColor()) {
            return null;
        }

        return teamColor.getColorValue();
    }
}