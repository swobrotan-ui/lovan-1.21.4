package ru.levin.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.levin.events.Event;
import ru.levin.events.impl.render.EventRender3D;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.render.RenderUtil;

import java.awt.*;

@SuppressWarnings("All")
@FunctionAnnotation(name = "Chinahat", desc = "Китайская шляпка над головой.", type = Type.Render)
public class Chinahat extends Function {

    private final BooleanSetting renderOwn = new BooleanSetting("Для себя", false);
    private final BooleanSetting renderOthers = new BooleanSetting("Для остальных", true);

    private final SliderSetting width = new SliderSetting("Ширина", 1.0, 0.2, 3.0, 0.1);
    private final SliderSetting height = new SliderSetting("Высота", 0.25, 0.05, 0.8, 0.05);
    private final SliderSetting alpha = new SliderSetting("Прозрачность", 0.85, 0.0, 1.0, 0.05);

    private final SliderSetting widthOthers = new SliderSetting("Ширина для других", 1.0, 0.2, 3.0, 0.1);
    private final SliderSetting heightOthers = new SliderSetting("Высота для других", 0.25, 0.05, 0.8, 0.05);
    private final SliderSetting alphaOthers = new SliderSetting("Прозрачность для других", 0.85, 0.0, 1.0, 0.05);

    public Chinahat() {
        addSettings(renderOwn, renderOthers, width, height, alpha, widthOthers, heightOthers, alphaOthers);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventRender3D e)) return;
        if (mc.world == null || mc.player == null) return;

        RenderTickCounter tick = e.getDeltatick();
        float tickDelta = tick.getTickDelta(true);

        Vec3d camPos = mc.gameRenderer.getCamera().getPos();

        RenderSystem.disableCull();
        RenderUtil.enableRender(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == null) continue;

            if (p == mc.player) {
                if (!renderOwn.get()) continue;
                if (mc.options.getPerspective() == Perspective.FIRST_PERSON) continue;
            } else {
                if (!renderOthers.get()) continue;
            }

            boolean self = p == mc.player;
            float r = (float) (self ? width.get().doubleValue() : widthOthers.get().doubleValue());
            float h = (float) (self ? height.get().doubleValue() : heightOthers.get().doubleValue());
            float a = (float) (self ? alpha.get().doubleValue() : alphaOthers.get().doubleValue());
            a = 0.3f + MathHelper.clamp(a, 0f, 1f) * 0.7f;

            int rgb = Manager.STYLE_MANAGER != null ? Manager.STYLE_MANAGER.getSecondColor() : new Color(46, 196, 182).getRGB();
            int color = RenderUtil.injectAlpha(rgb, (int) (a * 255));

            renderHat(e.getMatrixStack(), p, camPos, tickDelta, r, h, color);
        }

        RenderUtil.disableRender();
        RenderSystem.enableCull();
    }

    private void renderHat(MatrixStack matrices, PlayerEntity p, Vec3d camPos, float tickDelta, float radius, float coneHeight, int color) {
        Vec3d pos = interpolate(p, tickDelta);

        double x = pos.x - camPos.x;
        double y = pos.y - camPos.y;
        double z = pos.z - camPos.z;

        double hatY = y + p.getHeight();

        matrices.push();
        matrices.translate(x, hatY, z);

        drawCone(matrices, radius, coneHeight, color);

        matrices.pop();
    }

    private Vec3d interpolate(PlayerEntity p, float tickDelta) {
        double ix = p.prevX + (p.getX() - p.prevX) * tickDelta;
        double iy = p.prevY + (p.getY() - p.prevY) * tickDelta;
        double iz = p.prevZ + (p.getZ() - p.prevZ) * tickDelta;
        return new Vec3d(ix, iy, iz);
    }

    private void drawCone(MatrixStack matrices, float radius, float coneHeight, int color) {
        int segments = 32;

        float a = ((color >>> 24) & 0xFF) / 255f;
        float r = ((color >>> 16) & 0xFF) / 255f;
        float g = ((color >>> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        Matrix4f mat = matrices.peek().getPositionMatrix();

        BufferBuilder tri = IMinecraft.tessellator().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < segments; i++) {
            float ang1 = (float) (2.0 * Math.PI * i / segments);
            float ang2 = (float) (2.0 * Math.PI * (i + 1) / segments);

            float x1 = radius * (float) Math.cos(ang1);
            float z1 = radius * (float) Math.sin(ang1);
            float x2 = radius * (float) Math.cos(ang2);
            float z2 = radius * (float) Math.sin(ang2);

            tri.vertex(mat, x1, 0f, z1).color(r, g, b, a);
            tri.vertex(mat, x2, 0f, z2).color(r, g, b, a);
            tri.vertex(mat, 0f, coneHeight, 0f).color(r, g, b, a);
        }
        RenderUtil.render3D.endBuilding(tri);

        BufferBuilder fan = IMinecraft.tessellator().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        fan.vertex(mat, 0f, 0f, 0f).color(r, g, b, a);
        for (int i = 0; i <= segments; i++) {
            float ang = (float) (2.0 * Math.PI * i / segments);
            float x = radius * (float) Math.cos(ang);
            float z = radius * (float) Math.sin(ang);
            fan.vertex(mat, x, 0f, z).color(r, g, b, a);
        }
        RenderUtil.render3D.endBuilding(fan);
    }
}
