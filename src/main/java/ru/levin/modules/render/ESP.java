package ru.levin.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4d;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.MultiSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.events.Event;
import ru.levin.events.impl.render.EventRender2D;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.util.color.ColorUtil;
import ru.levin.util.render.RenderUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("All")
@FunctionAnnotation(name = "ESP", desc = "Полный ESP с заливкой, хп баром и ником", type = Type.Render)
public class ESP extends Function {

    private final MultiSetting targets = new MultiSetting(
            "Отображать",
            Arrays.asList("Игроков", "Друзья"),
            new String[]{"Игроков", "Друзья", "Меня", "Предметы"}
    );

    private final BooleanSetting fill = new BooleanSetting("Заливка", true);
    private final BooleanSetting outline = new BooleanSetting("Обводка", true);
    private final BooleanSetting corners = new BooleanSetting("Углы", true);
    private final BooleanSetting healthBar = new BooleanSetting("HP бар", true);
    private final SliderSetting opacity = new SliderSetting("Прозрачность", 0.35f, 0.05f, 1f, 0.05f);

    public ESP() {
        addSettings(targets, fill, outline, corners, healthBar, opacity);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventRender2D e)) return;
        if (mc.options.hudHidden) return;

        Matrix4f matrix = e.getDrawContext().getMatrices().peek().getPositionMatrix();
        float tickDelta = e.getDeltatick().getTickDelta(true);
        float alpha = opacity.get().floatValue();

        RenderUtil.enableRender();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        BufferBuilder buffer = IMinecraft.tessellator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        List<AbstractClientPlayerEntity> players = Manager.SYNC_MANAGER.getPlayers();
        List<Entity> entities = targets.get("Предметы") ? Manager.SYNC_MANAGER.getEntities() : List.of();

        for (PlayerEntity player : players) {
            if (shouldRender(player)) {
                drawPlayerESP(tickDelta, buffer, player, matrix, alpha);
            }
        }

        for (Entity entity : entities) {
            if (entity instanceof ItemEntity) {
                drawItemESP(tickDelta, buffer, entity, matrix, alpha);
            }
        }

        RenderUtil.render3D.endBuilding(buffer);
        RenderUtil.disableRender();
    }

    private boolean shouldRender(PlayerEntity entity) {
        if (entity == mc.player) {
            if (mc.options.getPerspective() == Perspective.FIRST_PERSON) return false;
            return targets.get("Меня");
        }
        if (targets.get("Друзья") && Manager.FRIEND_MANAGER.isFriend(entity.getName().getString())) {
            return true;
        }
        return targets.get("Игроков");
    }

    private void drawPlayerESP(float tickDelta, BufferBuilder buffer, @NotNull Entity ent, Matrix4f matrix, float baseAlpha) {
        Vec3d[] boxCorners = getVectors(tickDelta, ent);
        Vector4d pos = getProjectedBounds(boxCorners);
        if (pos == null) return;

        float screenW = mc.getWindow().getScaledWidth();
        float screenH = mc.getWindow().getScaledHeight();
        if (pos.z < 0 || pos.x > screenW || pos.w < 0 || pos.y > screenH) return;

        float x1 = (float) pos.x;
        float y1 = (float) pos.y;
        float x2 = (float) pos.z;
        float y2 = (float) pos.w;
        float width = x2 - x1;
        float height = y2 - y1;

        int mainColor = ColorUtil.getColorStyle(360);
        int topColor = ColorUtil.getColorStyle(270);
        int bottomColor = ColorUtil.getColorStyle(90);

        if (outline.get()) {
            int outlineColor = RenderUtil.injectAlpha(mainColor, (int) (255 * Math.min(1f, baseAlpha + 0.15f)));
            drawOutlineRect(buffer, matrix, x1, y1, x2, y2, 1.2f, outlineColor);
        }

        if (corners.get()) {
            int cornerColor = RenderUtil.injectAlpha(mainColor, 255);
            float cornerLen = Math.min(width, height) * 0.25f;
            float thickness = 1.5f;
            drawCorner(buffer, matrix, x1, y1, cornerLen, thickness, 1, 1, cornerColor);
            drawCorner(buffer, matrix, x2, y1, cornerLen, thickness, -1, 1, cornerColor);
            drawCorner(buffer, matrix, x1, y2, cornerLen, thickness, 1, -1, cornerColor);
            drawCorner(buffer, matrix, x2, y2, cornerLen, thickness, -1, -1, cornerColor);
        }

        if (fill.get() && ent instanceof LivingEntity living) {
            float fillAlpha = baseAlpha * 0.45f;
            int fillTop = RenderUtil.injectAlpha(topColor, (int) (fillAlpha * 255));
            int fillBottom = RenderUtil.injectAlpha(bottomColor, (int) (fillAlpha * 180));
            drawGradientFill(buffer, matrix, x1, y1, x2, y2, fillTop, fillBottom);
        }

        if (healthBar.get() && ent instanceof LivingEntity living) {
            drawHealthBar(buffer, matrix, x1, y1, y2, living, baseAlpha);
        }
    }

    private void drawItemESP(float tickDelta, BufferBuilder buffer, @NotNull Entity ent, Matrix4f matrix, float baseAlpha) {
        Vec3d[] boxCorners = getVectors(tickDelta, ent);
        Vector4d pos = getProjectedBounds(boxCorners);
        if (pos == null) return;

        float x1 = (float) pos.x;
        float y1 = (float) pos.y;
        float x2 = (float) pos.z;
        float y2 = (float) pos.w;

        int color = ColorUtil.getColorStyle(180);
        int c = RenderUtil.injectAlpha(color, (int) (baseAlpha * 220));
        drawOutlineRect(buffer, matrix, x1, y1, x2, y2, 1f, c);
    }

    private Vector4d getProjectedBounds(Vec3d[] corners) {
        Vector4d pos = null;
        for (Vec3d corner : corners) {
            Vec3d screen = RenderUtil.render3D.worldSpaceToScreenSpace(corner);
            if (screen.z <= 0 || screen.z >= 1) continue;

            if (pos == null) pos = new Vector4d(screen.x, screen.y, screen.x, screen.y);
            else {
                if (screen.x < pos.x) pos.x = screen.x;
                if (screen.y < pos.y) pos.y = screen.y;
                if (screen.x > pos.z) pos.z = screen.x;
                if (screen.y > pos.w) pos.w = screen.y;
            }
        }
        return pos;
    }

    private void drawOutlineRect(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float x2, float y2, float thickness, int color) {
        drawRect(buffer, matrix, x1 - thickness, y1 - thickness, x2 + thickness, y1 + thickness, color);
        drawRect(buffer, matrix, x1 - thickness, y2 - thickness, x2 + thickness, y2 + thickness, color);
        drawRect(buffer, matrix, x1 - thickness, y1 + thickness, x1 + thickness, y2 - thickness, color);
        drawRect(buffer, matrix, x2 - thickness, y1 + thickness, x2 + thickness, y2 - thickness, color);
    }

    private void drawCorner(BufferBuilder buffer, Matrix4f matrix, float x, float y, float len, float thickness, int dirX, int dirY, int color) {
        float xEnd = x + len * dirX;
        float yEnd = y + len * dirY;

        float thickX = thickness * dirX;
        float thickY = thickness * dirY;

        drawRect(buffer, matrix, x, y, x + thickX, yEnd + thickY, color);
        drawRect(buffer, matrix, x, y, xEnd + thickX, y + thickY, color);
    }

    private void drawGradientFill(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float x2, float y2, int topColor, int bottomColor) {
        buffer.vertex(matrix, x1, y1, 0f).color(topColor);
        buffer.vertex(matrix, x2, y1, 0f).color(topColor);
        buffer.vertex(matrix, x2, y2, 0f).color(bottomColor);
        buffer.vertex(matrix, x1, y2, 0f).color(bottomColor);
    }

    private void drawHealthBar(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float y2, LivingEntity living, float baseAlpha) {
        float barWidth = 2.5f;
        float gap = 3.5f;
        float bx = x1 - gap - barWidth;
        float by = y1;
        float bh = y2 - y1;

        float health = living.getHealth() / living.getMaxHealth();
        float filled = Math.max(0, Math.min(1, health));

        int bgColor = RenderUtil.injectAlpha(Color.BLACK.getRGB(), (int) (180 * baseAlpha));
        int hpColor = filled > 0.5f ? ColorUtil.getColorStyle(120)
                : filled > 0.25f ? ColorUtil.getColorStyle(60)
                : ColorUtil.getColorStyle(0);

        drawRect(buffer, matrix, bx, by, bx + barWidth, by + bh, bgColor);
        drawRect(buffer, matrix, bx, by + bh * (1f - filled), bx + barWidth, by + bh, RenderUtil.injectAlpha(hpColor, (int) (220 * baseAlpha)));
    }

    @NotNull
    private Vec3d[] getVectors(float tickDelta, @NotNull Entity ent) {
        double x = ent.prevX + (ent.getX() - ent.prevX) * tickDelta;
        double y = ent.prevY + (ent.getY() - ent.prevY) * tickDelta;
        double z = ent.prevZ + (ent.getZ() - ent.prevZ) * tickDelta;

        Box bb = ent.getBoundingBox();
        double dx = bb.minX - ent.getX() + x;
        double dy = bb.minY - ent.getY() + y;
        double dz = bb.minZ - ent.getZ() + z;
        double dx2 = bb.maxX - ent.getX() + x;
        double dy2 = bb.maxY - ent.getY() + y;
        double dz2 = bb.maxZ - ent.getZ() + z;

        return new Vec3d[]{
                new Vec3d(dx - 0.15, dy, dz - 0.15),
                new Vec3d(dx - 0.15, dy2 + 0.2, dz - 0.15),
                new Vec3d(dx2 + 0.15, dy, dz - 0.15),
                new Vec3d(dx2 + 0.15, dy2 + 0.2, dz - 0.15),
                new Vec3d(dx - 0.15, dy, dz2 + 0.15),
                new Vec3d(dx - 0.15, dy2 + 0.2, dz2 + 0.15),
                new Vec3d(dx2 + 0.15, dy, dz2 + 0.15),
                new Vec3d(dx2 + 0.15, dy2 + 0.2, dz2 + 0.15)
        };
    }

    private void drawRect(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {
        buffer.vertex(matrix, x1, y2, 0f).color(color);
        buffer.vertex(matrix, x2, y2, 0f).color(color);
        buffer.vertex(matrix, x2, y1, 0f).color(color);
        buffer.vertex(matrix, x1, y1, 0f).color(color);
    }
}
