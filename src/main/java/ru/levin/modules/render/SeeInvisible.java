package ru.levin.modules.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import ru.levin.events.Event;
import ru.levin.events.impl.render.EventRender3D;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.SliderSetting;

@FunctionAnnotation(name = "SeeInvisible", desc = "Показывает невидимых игроков и мобов", type = Type.Render)
public class SeeInvisible extends Function {

    private final BooleanSetting playersOnly = new BooleanSetting("Только игроки", false);
    private final SliderSetting opacity = new SliderSetting("Прозрачность", 0.5f, 0.1f, 1.0f, 0.05f);
    private final BooleanSetting glow = new BooleanSetting("Подсветка", true);

    public SeeInvisible() {
        addSettings(playersOnly, opacity, glow);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender3D) {
            renderInvisible();
        }
    }

    private void renderInvisible() {
        if (mc.world == null || mc.player == null) return;

        float alpha = opacity.get().floatValue();

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (!(entity instanceof LivingEntity)) continue;

            LivingEntity living = (LivingEntity) entity;

            // Check if entity is invisible
            if (!living.isInvisible()) continue;

            // Check if players only
            if (playersOnly.get() && !(living instanceof PlayerEntity)) continue;

            // The actual rendering is handled by mixins that check this module state
            // and adjust the entity rendering alpha/glow
            SeeInvisibleRenderer.setEntityTransparency(living, alpha);
            SeeInvisibleRenderer.setEntityGlow(living, glow.get());
        }
    }

    public boolean shouldRenderInvisible(Entity entity) {
        if (!state) return false;
        if (!(entity instanceof LivingEntity)) return false;
        if (!entity.isInvisible()) return false;
        if (playersOnly.get() && !(entity instanceof PlayerEntity)) return false;
        return true;
    }

    public float getOpacity() {
        return opacity.get().floatValue();
    }

    public boolean shouldGlow() {
        return glow.get();
    }
}
