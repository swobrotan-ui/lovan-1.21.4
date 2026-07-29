package ru.levin.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.MultiSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.events.impl.render.EventRender3D;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.util.color.ColorUtil;
import ru.levin.util.IEntity;
import ru.levin.util.render.RenderUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@FunctionAnnotation(name = "Trails", type = Type.Render, desc = "Плавные градиентные следы за игроками")
public class Trails extends Function {

    private final MultiSetting targets = new MultiSetting("Отображать у",
            Arrays.asList("Друзей", "Меня"),
            new String[]{"Игроков", "Друзья", "Меня"});

    private final ModeSetting mode = new ModeSetting("Тип", "Градиент", "Градиент", "Радуга", "Один цвет");
    private final SliderSetting width = new SliderSetting("Толщина", 2.5f, 0.5f, 6f, 0.5f);
    private final SliderSetting fadeSpeed = new SliderSetting("Затухание", 0.6f, 0.1f, 2f, 0.1f);
    private final SliderSetting maxPoints = new SliderSetting("Плотность", 8f, 3f, 20f, 1f);

    private final long trailLifetimeMs = 350L;

    public Trails() {
        addSettings(targets, mode, width, fadeSpeed, maxPoints);
    }

    @Override
    public void onEvent(Event event) {
        long now = System.currentTimeMillis();
        if (event instanceof EventUpdate) {
            for (PlayerEntity entity : Manager.SYNC_MANAGER.getPlayers()) {
                if (!shouldRenderTrails(entity)) continue;
                List<Trail> trails = ((IEntity) entity).exosWareFabric1_21_4$getTrails();
                trails.removeIf(t -> t.isExpired(now));
            }
            return;
        }

        if (event instanceof EventRender3D renderEvent) {
            float tickDelta = renderEvent.getDeltatick().getTickDelta(true);
            Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
            for (PlayerEntity entity : Manager.SYNC_MANAGER.getPlayers()) {
                if (!shouldRenderTrails(entity)) continue;
                Vec3d interp = interpolateEntityPosition(entity, tickDelta);
                List<Trail> trails = ((IEntity) entity).exosWareFabric1_21_4$getTrails();
                if (trails.isEmpty()) {
                    trails.add(new Trail(interp, getTrailColor(entity), now));
                } else {
                    Trail last = trails.get(trails.size() - 1);
                    double spacing = last.pos.distanceTo(interp);
                    double minSpacing = Math.max(0.03, 0.2 / maxPoints.get().floatValue());
                    if (spacing >= minSpacing) {
                        trails.add(new Trail(interp, getTrailColor(entity), now));
                    }
                }
                render(renderEvent, entity, cameraPos, now);
            }
        }
    }

    private int getTrailColor(PlayerEntity entity) {
        if (Manager.FRIEND_MANAGER.isFriend(entity.getName().getString())) {
            return new Color(0, 255, 128).getRGB();
        }
        return ColorUtil.getColorStyle(360);
    }

    private boolean shouldRenderTrails(PlayerEntity entity) {
        if (entity == mc.player) {
            if (mc.options.getPerspective() == Perspective.FIRST_PERSON) return false;
            return targets.get("Меня");
        }
        if (targets.get("Друзей") && Manager.FRIEND_MANAGER.isFriend(entity.getName().getString())) return true;
        return targets.get("Игроков");
    }

    private Vec3d interpolateEntityPosition(PlayerEntity entity, float tickDelta) {
        double ix = entity.prevX + (entity.getX() - entity.prevX) * tickDelta;
        double iy = entity.prevY + (entity.getY() - entity.prevY) * tickDelta;
        double iz = entity.prevZ + (entity.getZ() - entity.prevZ) * tickDelta;
        return new Vec3d(ix, iy, iz);
    }

    private void render(EventRender3D event, PlayerEntity entity, Vec3d cameraPos, long now) {
        List<Trail> trails = ((IEntity) entity).exosWareFabric1_21_4$getTrails();
        if (trails.size() < 2) return;

        event.getMatrixStack().push();
        RenderSystem.disableCull();
        RenderUtil.enableRender(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        float fade = 1f / fadeSpeed.get().floatValue();
        float w = width.get().floatValue();
        Matrix4f matrix = event.getMatrixStack().peek().getPositionMatrix();
        BufferBuilder buffer = IMinecraft.tessellator().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        for (int i = 0; i < trails.size() - 1; i++) {
            Trail cur = trails.get(i);
            Trail next = trails.get(i + 1);
            if (cur.isExpired(now) || next.isExpired(now)) continue;

            float age = (float) (now - cur.time) / trailLifetimeMs;
            float alpha = Math.max(0f, 1f - age * fade);
            if (alpha <= 0.01f) continue;

            int baseColor = cur.color;
            if (mode.is("Радуга")) {
                baseColor = ColorUtil.getColorStyle((System.currentTimeMillis() / 8 + i * 10) % 360);
            }

            int color = RenderUtil.injectAlpha(baseColor, (int) (alpha * 200));

            float cx1 = (float) (cur.pos.x - cameraPos.x);
            float cy1 = (float) (cur.pos.y - cameraPos.y);
            float cz1 = (float) (cur.pos.z - cameraPos.z);
            float cx2 = (float) (next.pos.x - cameraPos.x);
            float cy2 = (float) (next.pos.y - cameraPos.y);
            float cz2 = (float) (next.pos.z - cameraPos.z);

            buffer.vertex(matrix, cx1 - w, cy1, cz1).color(color);
            buffer.vertex(matrix, cx1 + w, cy1, cz1).color(color);
            buffer.vertex(matrix, cx2 + w, cy2, cz2).color(color);
            buffer.vertex(matrix, cx2 - w, cy2, cz2).color(color);
        }

        RenderUtil.render3D.endBuilding(buffer);
        RenderUtil.disableRender();
        RenderSystem.disableDepthTest();
        event.getMatrixStack().pop();
    }

    public class Trail {
        public final Vec3d pos;
        public final int color;
        public final long time;

        public Trail(Vec3d pos, int color, long time) {
            this.pos = pos;
            this.color = color;
            this.time = time;
        }

        public boolean isExpired(long now) {
            return (now - time) > trailLifetimeMs;
        }
    }
}
