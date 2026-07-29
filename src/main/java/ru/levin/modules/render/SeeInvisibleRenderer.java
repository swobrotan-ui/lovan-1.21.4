package ru.levin.modules.render;

import net.minecraft.entity.Entity;
import java.util.HashMap;
import java.util.Map;

public class SeeInvisibleRenderer {
    private static final Map<Entity, Float> entityTransparency = new HashMap<>();
    private static final Map<Entity, Boolean> entityGlow = new HashMap<>();

    public static void setEntityTransparency(Entity entity, float alpha) {
        entityTransparency.put(entity, alpha);
    }

    public static void setEntityGlow(Entity entity, boolean glow) {
        entityGlow.put(entity, glow);
    }

    public static float getTransparency(Entity entity) {
        return entityTransparency.getOrDefault(entity, 1.0f);
    }

    public static boolean shouldGlow(Entity entity) {
        return entityGlow.getOrDefault(entity, false);
    }

    public static void clear() {
        entityTransparency.clear();
        entityGlow.clear();
    }
}
