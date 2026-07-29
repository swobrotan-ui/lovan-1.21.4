package ru.levin.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.levin.events.Event;
import ru.levin.events.impl.render.EventRender3D;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.MultiSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.util.color.ColorUtil;
import ru.levin.util.render.RenderUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@FunctionAnnotation(name = "Shadows", type = Type.Render, desc = "Тени игроков на земле через стены")
public class Shadows extends Function {

    private final MultiSetting targets = new MultiSetting(
            "Отображать у",
            Arrays.asList("Друзей", "Меня"),
            new String[]{"Игроков", "Друзья", "Меня"}
    );

    private final ModeSetting shape = new ModeSetting("Форма", "Овал", "Овал", "Квадрат", "Связанная");
    private final SliderSetting opacity = new SliderSetting("Прозрачность", 0.55f, 0.1f, 1f, 0.05f);
    private final SliderSetting expand = new SliderSetting("Размер", 1.0f, 0.3f, 2.5f, 0.1f);
    private final SliderSetting smoothing = new SliderSetting("Сглаживание", 0.3f, 0f, 1f, 0.05f);

    public Shadows() {
        addSettings(targets, shape, opacity, expand, smoothing);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventRender3D e)) return;
        if (mc.world == null || mc.player == null) return;

        float alpha = opacity.get().floatValue() * 0.8f;
        float expand = this.expand.get().floatValue();
        float smooth = smoothing.get().floatValue();

        MatrixStack matrices = e.getMatrixStack();
        matrices.push();

        RenderSystem.disableCull();
        RenderUtil.enableRender(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        List<AbstractClientPlayerEntity> players = Manager.SYNC_MANAGER.getPlayers();
        for (AbstractClientPlayerEntity abstractPlayer : players) {
            if (!(abstractPlayer instanceof PlayerEntity player)) continue;
            if (!shouldRender(player)) continue;

            float tickDelta = e.getDeltatick().getTickDelta(true);
            double px = player.prevX + (player.getX() - player.prevX) * tickDelta;
            double pz = player.prevZ + (player.getZ() - player.prevZ) * tickDelta;

            Vec3d camPos = mc.gameRenderer.getCamera().getPos();
            double x = px - camPos.x;
            double z = pz - camPos.z;

            int color = Manager.FRIEND_MANAGER.isFriend(player.getName().getString())
                    ? RenderUtil.injectAlpha(new Color(0, 255, 128).getRGB(), (int) (alpha * 255))
                    : RenderUtil.injectAlpha(ColorUtil.getColorStyle(270), (int) (alpha * 255));

            float w = player.getWidth() * 0.5f * expand;
            float d = player.getWidth() * 0.5f * expand;

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            BufferBuilder buffer = IMinecraft.tessellator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            switch (shape.get()) {
                case "Овал" -> drawOval(buffer, matrix, x, z, w, d, color, smooth);
                case "Квадрат" -> drawSquare(buffer, matrix, x, z, w, d, color, smooth);
                case "Связанная" -> drawConnected(buffer, matrix, player, camPos, tickDelta, w, d, color, smooth);
            }

            RenderUtil.render3D.endBuilding(buffer);
        }

        RenderUtil.disableRender();
        RenderSystem.disableDepthTest();
        RenderSystem.enableCull();
        matrices.pop();
    }

    private void drawOval(BufferBuilder buffer, Matrix4f matrix, double x, double z, float w, float d, int color, float smooth) {
        int segments = 24;
        float r = ColorUtil.getRed(color) / 255f;
        float g = ColorUtil.getGreen(color) / 255f;
        float b = ColorUtil.getBlue(color) / 255f;
        float a = ColorUtil.getAlpha(color) / 255f;

        for (int i = 0; i < segments; i++) {
            float ang1 = (float) (2.0 * Math.PI * i / segments);
            float ang2 = (float) (2.0 * Math.PI * (i + 1) / segments);

            float cos1 = (float) Math.cos(ang1);
            float sin1 = (float) Math.sin(ang1);
            float cos2 = (float) Math.cos(ang2);
            float sin2 = (float) Math.sin(ang2);

            buffer.vertex(matrix, (float) x, 0f, (float) z).color(r, g, b, a * smooth);
            buffer.vertex(matrix, (float) (x + cos1 * w), 0f, (float) (z + sin1 * d)).color(r, g, b, a);
            buffer.vertex(matrix, (float) (x + cos2 * w), 0f, (float) (z + sin2 * d)).color(r, g, b, a);
        }
    }

    private void drawSquare(BufferBuilder buffer, Matrix4f matrix, double x, double z, float w, float d, int color, float smooth) {
        float r = ColorUtil.getRed(color) / 255f;
        float g = ColorUtil.getGreen(color) / 255f;
        float b = ColorUtil.getBlue(color) / 255f;
        float a = ColorUtil.getAlpha(color) / 255f;

        buffer.vertex(matrix, (float) x, 0f, (float) z).color(r, g, b, a * smooth);
        buffer.vertex(matrix, (float) (x - w), 0f, (float) (z - d)).color(r, g, b, a);
        buffer.vertex(matrix, (float) (x + w), 0f, (float) (z - d)).color(r, g, b, a);

        buffer.vertex(matrix, (float) x, 0f, (float) z).color(r, g, b, a * smooth);
        buffer.vertex(matrix, (float) (x + w), 0f, (float) (z + d)).color(r, g, b, a);
        buffer.vertex(matrix, (float) (x + w), 0f, (float) (z - d)).color(r, g, b, a);

        buffer.vertex(matrix, (float) x, 0f, (float) z).color(r, g, b, a * smooth);
        buffer.vertex(matrix, (float) (x - w), 0f, (float) (z + d)).color(r, g, b, a);
        buffer.vertex(matrix, (float) (x + w), 0f, (float) (z + d)).color(r, g, b, a);

        buffer.vertex(matrix, (float) x, 0f, (float) z).color(r, g, b, a * smooth);
        buffer.vertex(matrix, (float) (x - w), 0f, (float) (z - d)).color(r, g, b, a);
        buffer.vertex(matrix, (float) (x - w), 0f, (float) (z + d)).color(r, g, b, a);
    }

    private void drawConnected(BufferBuilder buffer, Matrix4f matrix, PlayerEntity player, Vec3d camPos, float tickDelta, float w, float d, int color, float smooth) {
        List<AbstractClientPlayerEntity> players = Manager.SYNC_MANAGER.getPlayers();
        PlayerEntity me = mc.player;

        double myX = me.prevX + (me.getX() - me.prevX) * tickDelta;
        double myZ = me.prevZ + (me.getZ() - me.prevZ) * tickDelta;
        float mx = (float) (myX - camPos.x);
        float mz = (float) (myZ - camPos.z);

        for (AbstractClientPlayerEntity otherAbstract : players) {
            if (!(otherAbstract instanceof PlayerEntity other)) continue;
            if (other == player || !shouldRender(other)) continue;

            double ox = other.prevX + (other.getX() - other.prevX) * tickDelta;
            double oz = other.prevZ + (other.getZ() - other.prevZ) * tickDelta;
            float oxf = (float) (ox - camPos.x);
            float ozf = (float) (oz - camPos.z);

            float r = ColorUtil.getRed(color) / 255f;
            float g = ColorUtil.getGreen(color) / 255f;
            float b = ColorUtil.getBlue(color) / 255f;
            float a = ColorUtil.getAlpha(color) / 255f * smooth;

            buffer.vertex(matrix, mx, 0.02f, mz).color(r, g, b, a);
            buffer.vertex(matrix, oxf, 0.02f, ozf).color(r, g, b, a);
        }
    }

    private boolean shouldRender(PlayerEntity entity) {
        if (entity == mc.player) {
            if (mc.options.getPerspective() == Perspective.FIRST_PERSON) return false;
            return targets.get("Меня");
        }
        if (targets.get("Друзья") && Manager.FRIEND_MANAGER.isFriend(entity.getName().getString())) return true;
        return targets.get("Игроков");
    }
}
