package ru.levin.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.injection.At;
import ru.levin.events.Event;
import ru.levin.events.impl.player.EventAttack;
import ru.levin.events.impl.render.EventRender3D;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.util.animations.impl.EaseInOutQuad;
import ru.levin.util.color.ColorUtil;
import ru.levin.util.math.RayTraceUtil;
import ru.levin.util.render.RenderUtil;
import ru.levin.util.render.providers.ResourceProvider;

import java.awt.*;

import static ru.levin.util.math.MathUtil.interpolate;
import static ru.levin.util.math.MathUtil.interpolateFloat;
import static ru.levin.util.render.RenderUtil.*;

@SuppressWarnings("All")
@FunctionAnnotation(name = "TargetESP", desc = "Красивый указатель на вашем противнике + swing анимации", type = Type.Render)
public class TargetESP extends Function {
    public final ModeSetting mode = new ModeSetting("Мод","Призраки","Маркер","Маркер2","Маркер3","Призраки","Кружок","Фиолетовое кольцо");
    public final ModeSetting hitColor = new ModeSetting("Hit Color", "Red", "Red", "Blue", "Green", "Yellow", "Purple", "Orange");
    public final BooleanSetting manualTarget = new BooleanSetting("Ручные удары", true, "Показывает ESP даже если KillAura выключен, при ручном ударе");

    private final float[] SCALE_CACHE = new float[101];
    private final EaseInOutQuad animation = new EaseInOutQuad(800, 1);
    private Entity lastTarget = null;
    private double scale = 0.0D;
    private Entity lastManualTarget = null;
    private long lastManualTargetTime = 0L;
    private float swingProgress = 0f;
    private float lastSwingProgress = 0f;

    public TargetESP() {
        addSettings(mode, hitColor, manualTarget);
        for (int i = 0; i <= 100; i++) SCALE_CACHE[i] = Math.max(0.28f * (i / 100f), 0.2f);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventAttack attack) {
            if (manualTarget.get()) {
                lastManualTarget = attack.getTarget();
                lastManualTargetTime = System.currentTimeMillis();
            }
        }

        if (!(event instanceof EventRender3D renderEvent)) return;
        Entity currentTarget = Manager.FUNCTION_MANAGER.attackAura.target;

        if (currentTarget == null && manualTarget.get()) {
            currentTarget = lastManualTarget;
        }

        if (currentTarget != null) {
            long timeSinceManual = System.currentTimeMillis() - lastManualTargetTime;
            if (currentTarget == lastManualTarget && timeSinceManual > 3000L) {
                lastManualTarget = null;
            }
        }

        if (currentTarget != null && (lastTarget == null || !lastTarget.equals(currentTarget))) {
            animation.setDirection(net.minecraft.util.math.Direction.AxisDirection.POSITIVE);
        } else if (currentTarget == null) {
            animation.setDirection(net.minecraft.util.math.Direction.AxisDirection.NEGATIVE);
        }

        lastTarget = currentTarget;

        if (currentTarget != null) {
            updateSwingAnimation();
            if (mode.is("Маркер") || mode.is("Маркер2") || mode.is("Маркер3")) {
                render(currentTarget);
            } else if (mode.is("Призраки")) {
                renderGhosts(14, 8, 1.8f, 3f, currentTarget);
            } else if (mode.is("Кружок")) {
                cicle(currentTarget, renderEvent.getMatrixStack(), renderEvent.getDeltatick().getTickDelta(true));
            } else if (mode.is("Фиолетовое кольцо")) {
                lovanRing(currentTarget, renderEvent.getMatrixStack(), renderEvent.getDeltatick().getTickDelta(true));
            }
        }
    }

    private void updateSwingAnimation() {
        if (mc.player != null && mc.player.isUsingItem()) {
            lastSwingProgress = swingProgress;
            swingProgress = Math.min(1f, swingProgress + 0.15f);
        } else {
            swingProgress = Math.max(0f, swingProgress - 0.08f);
        }
    }

    public float getSwingProgress() {
        return swingProgress;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        lastManualTarget = null;
        lastManualTargetTime = 0L;
        swingProgress = 0f;
        lastSwingProgress = 0f;
    }

    public void renderGhosts(int espLength, int factor, float shaking, float amplitude, Entity target) {
        if (target == null) return;

        Camera camera = mc.gameRenderer.getCamera();
        if (camera == null) return;
        float hitProgress = RayTraceUtil.getHitProgress(target);
        float delta = mc.getRenderTickCounter().getTickDelta(true);
        Vec3d camPos = camera.getPos();
        double tX = interpolate(target.prevX, target.getX(), delta) - camPos.x;
        double tY = interpolate(target.prevY, target.getY(), delta) - camPos.y;
        double tZ = interpolate(target.prevZ, target.getZ(), delta) - camPos.z;
        float age = interpolateFloat(target.age - 1, target.age, delta);

        boolean canSee = mc.player.canSee(target);

        RenderUtil.enableRender(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture(0, ResourceProvider.firefly);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        if (canSee) {
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
        } else {
            RenderSystem.disableDepthTest();
        }

        BufferBuilder buffer = IMinecraft.tessellator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        float pitch = camera.getPitch();
        float yaw = camera.getYaw();
        float ghostAlpha = (float) animation.getOutput();

        float swingScale = 1f + swingProgress * 0.15f;

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i <= espLength; i++) {
                float offset = (float) i / espLength;
                double radians = Math.toRadians(((i / 1.5f + age) * factor + j * 120) % (factor * 360));
                double sinQuad = Math.sin(Math.toRadians(age * 2.5f + i * (j + 1)) * amplitude) / shaking;

                MatrixStack matrices = new MatrixStack();
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw + 180f));
                matrices.translate(tX + Math.cos(radians) * target.getWidth() * swingScale, tY + 1 + sinQuad, tZ + Math.sin(radians) * target.getWidth() * swingScale);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));

                Matrix4f matrix = matrices.peek().getPositionMatrix();
                int baseColor;
                if (hitProgress > 0) {
                    baseColor = getHitColor();
                } else {
                    baseColor = ColorUtil.getColorStyle((int) (180 * offset));
                }

                int color = applyOpacity(baseColor, offset * ghostAlpha);

                float scale = SCALE_CACHE[Math.min((int)(offset * 100), 100)];
                buffer.vertex(matrix, -scale,  scale, 0).texture(0f, 1f).color(color);
                buffer.vertex(matrix,  scale,  scale, 0).texture(1f, 1f).color(color);
                buffer.vertex(matrix,  scale, -scale, 0).texture(1f, 0).color(color);
                buffer.vertex(matrix, -scale, -scale, 0).texture(0f, 0).color(color);
            }
        }
        RenderUtil.render3D.endBuilding(buffer);

        if (canSee) {
            RenderSystem.depthMask(true);
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.disableBlend();
    }

    private void cicle(Entity target, MatrixStack matrices, float tickDelta) {
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        double x = MathHelper.lerp(tickDelta, target.lastRenderX, target.getX()) - camPos.x;
        double z = MathHelper.lerp(tickDelta, target.lastRenderZ, target.getZ()) - camPos.z;
        double y = MathHelper.lerp(tickDelta, target.lastRenderY, target.getY()) - camPos.y + Math.min(Math.sin(System.currentTimeMillis() / 400.0) + 0.95, target.getHeight());

        disableDepth();
        RenderUtil.enableRender(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = IMinecraft.tessellator().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        int baseColor = ColorUtil.getColorStyle(360);
        float r = ((baseColor >> 16) & 0xFF) / 255f;
        float g = ((baseColor >> 8) & 0xFF) / 255f;
        float b = (baseColor & 0xFF) / 255f;

        float alpha = (float) animation.getOutput();

        float radius = target.getWidth() * 0.8f;

        for (float i = 0; i <= Math.PI * 2 + (Math.PI * 5 / 100); i += Math.PI * 5 / 100) {
            double vecX = x + radius * Math.cos(i);
            double vecZ = z + radius * Math.sin(i);

            buffer.vertex(matrix, (float) vecX, (float) (y - Math.cos(System.currentTimeMillis() / 400.0) / 2), (float) vecZ).color(r, g, b, 0.01f * alpha);
            buffer.vertex(matrix, (float) vecX, (float) y, (float) vecZ).color(r, g, b, 1f * alpha);
        }

        RenderUtil.render3D.endBuilding(buffer);
        endRender();
    }

    private void lovanRing(Entity target, MatrixStack matrices, float tickDelta) {
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        double x = MathHelper.lerp(tickDelta, target.lastRenderX, target.getX()) - camPos.x;
        double z = MathHelper.lerp(tickDelta, target.lastRenderZ, target.getZ()) - camPos.z;
        double yBase = MathHelper.lerp(tickDelta, target.lastRenderY, target.getY()) - camPos.y;

        double bob = Math.sin(System.currentTimeMillis() / 400.0) * 0.15;
        double y = yBase + 0.05 + bob;

        disableDepth();
        RenderUtil.enableRender(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = IMinecraft.tessellator().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        float alpha = (float) animation.getOutput();

        float radiusInner = target.getWidth() * 0.95f;
        float radiusOuter = radiusInner + 0.18f;

        Color inner = new Color(155, 110, 255);
        Color outer = new Color(42, 28, 76);

        float step = (float) (Math.PI * 5 / 180);

        double t = (System.currentTimeMillis() % 2000L) / 2000.0;

        for (float angle = 0; angle <= Math.PI * 2 + step; angle += step) {
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            double xInner = x + radiusInner * cos;
            double zInner = z + radiusInner * sin;
            double xOuter = x + radiusOuter * cos;
            double zOuter = z + radiusOuter * sin;

            double highlightCenter = t * Math.PI * 2.0;
            double diff = Math.abs(MathHelper.wrapDegrees((float) Math.toDegrees(angle - highlightCenter)));
            double highlightFactor = Math.max(0.0, 1.0 - diff / 90.0);

            float outerAlpha = (float) (alpha * (0.25f + 0.25f * highlightFactor));
            float innerAlpha = (float) (alpha * (0.6f + 0.4f * highlightFactor));

            buffer.vertex(matrix, (float) xOuter, (float) y, (float) zOuter)
                    .color(outer.getRed() / 255f, outer.getGreen() / 255f, outer.getBlue() / 255f, outerAlpha);
            buffer.vertex(matrix, (float) xInner, (float) y, (float) zInner)
                    .color(inner.getRed() / 255f, inner.getGreen() / 255f, inner.getBlue() / 255f, innerAlpha);
        }

        RenderUtil.render3D.endBuilding(buffer);
        endRender();
    }

    private static void disableDepth() {
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
    }

    private static void endRender() {
        RenderUtil.disableRender();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    private void render(Entity target) {
        Camera camera = mc.gameRenderer.getCamera();
        if (camera == null) return;

        scale = animation.getOutput();
        if (scale == 0.0) return;

        float delta = mc.getRenderTickCounter().getTickDelta(true);
        float hitProgress = RayTraceUtil.getHitProgress(target);
        Vec3d camPos = camera.getPos();
        double tX = interpolate(target.prevX, target.getX(), delta) - camPos.x;
        double tY = interpolate(target.prevY, target.getY(), delta) - camPos.y;
        double tZ = interpolate(target.prevZ, target.getZ(), delta) - camPos.z;
        MatrixStack matrices = setupMatrices(camera, target, delta, tX, tY, tZ);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        disableDepth();
        RenderUtil.enableRender(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        if (mode.is("Маркер")) {
            RenderSystem.setShaderTexture(0, ResourceProvider.marker);
        }
        if (mode.is("Маркер2")) {
            RenderSystem.setShaderTexture(0, ResourceProvider.marker2);
        }
        if (mode.is("Маркер3")) {
            RenderSystem.setShaderTexture(0, ResourceProvider.marker3);
        }

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        float alpha = (float) animation.getOutput();
        int[] baseColors = hitProgress > 0 ? new int[]{getHitColor(), ColorUtil.getColorStyle(0), getHitColor(), ColorUtil.getColorStyle(270)} : new int[]{ColorUtil.getColorStyle(90), ColorUtil.getColorStyle(0), ColorUtil.getColorStyle(180), ColorUtil.getColorStyle(270)};

        int[] colors = applyAlphaToColors(baseColors, alpha);

        BufferBuilder buffer = IMinecraft.tessellator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix,0,1.5f,0).texture(0f,1f).color(colors[0]);
        buffer.vertex(matrix,1.5f,1.5f,0).texture(1f,1f).color(colors[1]);
        buffer.vertex(matrix,1.5f,0,0).texture(1f,0).color(colors[2]);
        buffer.vertex(matrix,0,0,0).texture(0f,0).color(colors[3]);
        RenderUtil.render3D.endBuilding(buffer);

        endRender();
    }

    private MatrixStack setupMatrices(Camera camera, Entity target, float delta, double tX, double tY, double tZ) {
        MatrixStack matrices = new MatrixStack();
        float pitch = camera.getPitch();
        float yaw = camera.getYaw();

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw + 180f));
        matrices.translate(tX, tY + target.getEyeHeight(target.getPose()) / 2f, tZ);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));

        float swingPulse = 1f + swingProgress * 0.3f;
        float interpolatedAngle = interpolateFloat(1f, 1f, delta);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(interpolatedAngle));

        float radians = (float) Math.toRadians(System.currentTimeMillis() % 3600 / 5f);
        matrices.multiplyPositionMatrix(new Matrix4f().rotate(radians, 0, 0, 1));
        matrices.scale(swingPulse, swingPulse, swingPulse);
        matrices.translate(-0.75, -0.75, -0.01);
        return matrices;
    }

    private int[] applyAlphaToColors(int[] colors, float alpha) {
        int[] out = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);
            out[i] = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * alpha)).getRGB();
        }
        return out;
    }

    private int getHitColor() {
        return switch (hitColor.get()) {
            case "Red" -> Color.RED.getRGB();
            case "Blue" -> Color.BLUE.getRGB();
            case "Green" -> Color.GREEN.getRGB();
            case "Yellow" -> Color.YELLOW.getRGB();
            case "Purple" -> new Color(128, 0, 128).getRGB();
            case "Orange" -> Color.ORANGE.getRGB();
            default -> Color.RED.getRGB();
        };
    }
}
