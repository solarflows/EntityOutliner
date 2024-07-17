package net.entityoutliner;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public enum EntityAge {
    BABY, ADULT, BOTH;

    public final static EntityAge[] ages = values();

    @Nonnull
    public static Optional<EntityAge> of(@Nullable String name) {
        return Arrays.stream(values())
            .filter(c -> c.name().equals(name))
            .findFirst();
    }
}
