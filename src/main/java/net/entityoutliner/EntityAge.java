package net.entityoutliner;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public enum EntityAge {
    BABY, ADULT, BOTH;

    public final static EntityAge[] ages = values();

    public static Optional<EntityAge> of(String name) {
        return Arrays.stream(values())
            .filter(c -> c.name().equals(name))
            .findFirst();
    }
}
