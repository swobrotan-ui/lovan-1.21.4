package ru.levin.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.levin.events.Event;
import ru.levin.events.impl.render.EventRender3D;
import ru.levin.manager.IMinecraft;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.color.ColorUtil;
import ru.levin.util.render.RenderUtil;

@FunctionAnnotation(name = "MyDick", desc = "У тебя будет писюн (18+)", type = Type.Render)
public class MyDick extends Function implements IMinecraft {

    private final SliderSetting length = new SliderSetting("Длина", 0.6f, 0.1f, 1.5f, 0.1f);
    private final SliderSetting thickness = new SliderSetting("Толщина", 0.12f, 0.05f, 0.3f, 0.01f);
    private final SliderSetting yOff = new SliderSetting("Смещение Y", 0.65f, 0.0f, 1.5f, 0.01f);
    private final SliderSetting zOff = new SliderSetting("Смещение Z", -0.35f, -1.0f, 0.5f, 0.01f);
    private final SliderSetting angle = new SliderSetting("Угол", 90f, 0f, 180f, 1f);

    public MyDick() {
        addSettings(length, thickness, yOff, zOff, angle);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventRender3D render3D)) return;
        if (mc.player == null || mc.world == null) return;

        MatrixStack ms = render3D.getMatrixStack();
        float pt = render3D.getDeltatick().getLastFrameDuration();

        double ix = mc.player.prevX + (mc.player.getX() - mc.player.prevX) * pt;
        double iy = mc.player.prevY + (mc.player.getY() - mc.player.prevY) * pt;
        double iz = mc.player.prevZ + (mc.player.getZ() - mc.player.prevZ) * pt;

        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        Vec3d p = new Vec3d(ix, iy, iz).subtract(camPos);

        double vx = (mc.player.getX() - mc.player.prevX);
        double vz = (mc.player.getZ() - mc.player.prevZ);
        float yaw = mc.player.bodyYaw;
        float rad = (float) Math.toRadians(yaw);
        float sideInertia = (float) (vx * Math.cos(rad) + vz * Math.sin(rad)) * 150f;

        ms.push();
        ms.translate(p.x, p.y + (mc.player.isSneaking() ? yOff.get().floatValue() - 0.2f : yOff.get().floatValue()), p.z);
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        ms.translate(0, 0, zOff.get().floatValue());
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(angle.get().floatValue() + (float) Math.sin(System.currentTimeMillis() / 400.0) * 3f));
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sideInertia));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        BufferBuilder bb = IMinecraft.tessellator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        int skinColor = ColorUtil.getColorStyle(0);
        int headColor = ColorUtil.getColorStyle(320);

        float th = thickness.get().floatValue();

        drawSphere(ms, bb, -th * 0.7f, 0, 0, th * 1.1f, skinColor);
        drawSphere(ms, bb, th * 0.7f, 0, 0, th * 1.1f, skinColor);

        drawCylinder(ms, bb, th, length.get().floatValue(), skinColor);
        drawSphere(ms, bb, 0, length.get().floatValue(), 0, th * 1.2f, headColor);

        BufferRenderer.drawWithGlobalProgram(bb.end());

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        ms.pop();
    }

    private void drawSphere(MatrixStack ms, VertexConsumer b, float x, float y, float z, float r, int color) {
        Matrix4f m = ms.peek().getPositionMatrix();
        int c = color;
        int vSeg = 10;
        int hSeg = 10;
        for (int i = 0; i < vSeg; i++) {
            for (int j = 0; j < hSeg; j++) {
                float v0 = (float) i / vSeg * (float) Math.PI;
                float v1 = (float) (i + 1) / vSeg * (float) Math.PI;
                float h0 = (float) j / hSeg * (float) Math.PI * 2;
                float h1 = (float) (j + 1) / hSeg * (float) Math.PI * 2;
                vt(b, m, x, y, z, r, v0, h0, c);
                vt(b, m, x, y, z, r, v1, h0, c);
                vt(b, m, x, y, z, r, v1, h1, c);
                vt(b, m, x, y, z, r, v0, h1, c);
            }
        }
    }

    private void vt(VertexConsumer b, Matrix4f m, float x, float y, float z, float r, float v, float h, int c) {
        float px = x + r * (float) (Math.sin(v) * Math.cos(h));
        float py = y + r * (float) Math.cos(v);
        float pz = z + r * (float) (Math.sin(v) * Math.sin(h));
        b.vertex(m, px, py, pz).color(c);
    }

    private void drawCylinder(MatrixStack ms, VertexConsumer b, float r, float h, int color) {
        Matrix4f m = ms.peek().getPositionMatrix();
        int c = color;
        int seg = 12;
        for (int i = 0; i < seg; i++) {
            float a0 = (float) i / seg * (float) Math.PI * 2;
            float a1 = (float) (i + 1) / seg * (float) Math.PI * 2;
            float x0 = (float) Math.cos(a0) * r;
            float z0 = (float) Math.sin(a0) * r;
            float x1 = (float) Math.cos(a1) * r;
            float z1 = (float) Math.sin(a1) * r;
            b.vertex(m, x0, 0, z0).color(c);
            b.vertex(m, x0, h, z0).color(c);
            b.vertex(m, x1, h, z1).color(c);
            b.vertex(m, x1, 0, z1).color(c);
        }
    }
}
