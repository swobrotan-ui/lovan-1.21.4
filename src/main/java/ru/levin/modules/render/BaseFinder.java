package ru.levin.modules.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import ru.levin.manager.ClientManager;
import ru.levin.manager.IMinecraft;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.MultiSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.events.Event;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.events.impl.render.EventRender3D;
import ru.levin.util.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("All")
@FunctionAnnotation(name = "BaseFinder", desc = "Ищет базы игроков по обсидиану, сундукам и шалкер-боксам", type = Type.Render)
public class BaseFinder extends Function {

    private final MultiSetting blocks = new MultiSetting(
            "Блоки",
            List.of("Обсидиан", "Сундуки", "Эндер сундуки", "Шалкер боксы"),
            new String[]{"Обсидиан", "Сундуки", "Эндер сундуки", "Шалкер боксы"}
    );
    private final SliderSetting radius = new SliderSetting("Радиус", 48f, 8f, 200f, 1f);
    private final SliderSetting minBlocks = new SliderSetting("Мин. блоков", 3f, 1f, 100f, 1f);
    private final SliderSetting cluster = new SliderSetting("Радиус кластера", 6f, 1f, 32f, 1f);
    private final SliderSetting interval = new SliderSetting("Интервал (мс)", 2000f, 200f, 10000f, 100f);
    private final ModeSetting mode = new ModeSetting("Подсветка", "Оба", "Обводка", "Заливка", "Оба");
    private final BooleanSetting notifications = new BooleanSetting("Уведомления", true);
    private final BooleanSetting sound = new BooleanSetting("Звук", true);
    private final BooleanSetting goToBase = new BooleanSetting("Идти к базе", true);
    private final BooleanSetting sprint = new BooleanSetting("Спринт", true);
    private final SliderSetting stopDistance = new SliderSetting("Дистанция стопа", 3f, 1f, 30f, 1f);

    private final List<Base> bases = new ArrayList<>();
    private long lastScan = 0L;

    public BaseFinder() {
        addSettings(blocks, radius, minBlocks, cluster, interval, mode, notifications, sound, goToBase, sprint, stopDistance);
    }

    @Override
    public void onEnable() {
        bases.clear();
    }

    @Override
    public void onDisable() {
        releaseKeys();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender3D e3d) {
            if (mc.world == null || mc.player == null) return;

            long now = System.currentTimeMillis();
            if (now - lastScan >= interval.get().longValue()) {
                lastScan = now;
                scan();
            }

            render(e3d);
            return;
        }

        if (event instanceof EventMotion motion) {
            if (goToBase.get() && !bases.isEmpty()) {
                travel(motion);
            } else {
                releaseKeys();
            }
        }
    }

    private void travel(EventMotion motion) {
        if (mc.player == null) return;

        Base target = null;
        double best = Double.MAX_VALUE;
        for (Base b : bases) {
            double dx = b.center.getX() + 0.5 - mc.player.getX();
            double dy = b.center.getY() + 0.5 - mc.player.getY();
            double dz = b.center.getZ() + 0.5 - mc.player.getZ();
            double d = dx * dx + dy * dy + dz * dz;
            if (d < best) {
                best = d;
                target = b;
            }
        }

        if (target == null) return;

        double dx = target.center.getX() + 0.5 - mc.player.getX();
        double dy = target.center.getY() + 0.5 - mc.player.getY();
        double dz = target.center.getZ() + 0.5 - mc.player.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        if (dist <= stopDistance.get().doubleValue() && Math.abs(dy) <= 3) {
            bases.remove(target);
            releaseKeys();
            return;
        }

        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        motion.setYaw(yaw);

        mc.options.forwardKey.setPressed(true);
        if (sprint.get()) mc.options.sprintKey.setPressed(true);

        if (mc.player.getAbilities().flying) {
            double horiz = Math.max(dist, 0.1);
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, horiz));
            motion.setPitch(pitch);
        } else if (dy > 1.2 && mc.player.isOnGround()) {
            mc.player.jump();
        }
    }

    private void releaseKeys() {
        if (mc.options == null) return;
        mc.options.forwardKey.setPressed(false);
        mc.options.sprintKey.setPressed(false);
    }

    private boolean isTarget(Block block) {
        if (blocks.get("Обсидиан") && block == Blocks.OBSIDIAN) return true;
        if (blocks.get("Сундуки") && (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST)) return true;
        if (blocks.get("Эндер сундуки") && block == Blocks.ENDER_CHEST) return true;
        if (blocks.get("Шалкер боксы") && block == Blocks.SHULKER_BOX) return true;
        return false;
    }

    private void scan() {
        int r = radius.get().intValue();
        BlockPos pp = mc.player.getBlockPos();
        int minY = Math.max(mc.world.getBottomY(), pp.getY() - r);
        int maxY = Math.min(mc.world.getTopYInclusive(), pp.getY() + r);

        int clusterRange = cluster.get().intValue();
        int clusterSq = clusterRange * clusterRange;

        List<BlockPos> found = new ArrayList<>();

        for (int x = pp.getX() - r; x <= pp.getX() + r; x++) {
            for (int z = pp.getZ() - r; z <= pp.getZ() + r; z++) {
                int cx = x >> 4;
                int cz = z >> 4;
                if (!mc.world.getChunkManager().isChunkLoaded(cx, cz)) continue;
                for (int y = minY; y <= maxY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (isTarget(state.getBlock())) {
                        found.add(pos);
                    }
                }
            }
        }

        if (found.isEmpty()) return;

        List<List<BlockPos>> clusters = new ArrayList<>();
        for (BlockPos pos : found) {
            List<BlockPos> best = null;
            int bestDist = Integer.MAX_VALUE;
            for (List<BlockPos> c : clusters) {
                int dx = c.get(0).getX() - pos.getX();
                int dz = c.get(0).getZ() - pos.getZ();
                int dy = c.get(0).getY() - pos.getY();
                int d = dx * dx + dy * dy + dz * dz;
                if (d <= clusterSq && d < bestDist) {
                    best = c;
                    bestDist = d;
                }
            }
            if (best == null) {
                List<BlockPos> c = new ArrayList<>();
                c.add(pos);
                clusters.add(c);
            } else {
                best.add(pos);
            }
        }

        int need = minBlocks.get().intValue();
        for (List<BlockPos> c : clusters) {
            if (c.size() < need) continue;

            int sx = 0, sy = 0, sz = 0;
            int minX = Integer.MAX_VALUE, minY2 = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY2 = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
            for (BlockPos p : c) {
                sx += p.getX(); sy += p.getY(); sz += p.getZ();
                minX = Math.min(minX, p.getX()); minY2 = Math.min(minY2, p.getY()); minZ = Math.min(minZ, p.getZ());
                maxX = Math.max(maxX, p.getX()); maxY2 = Math.max(maxY2, p.getY()); maxZ = Math.max(maxZ, p.getZ());
            }
            BlockPos center = new BlockPos(sx / c.size(), sy / c.size(), sz / c.size());
            Box box = new Box(minX, minY2, minZ, maxX + 1, maxY2 + 1, maxZ + 1);

            Base existing = null;
            int mergeSq = (clusterRange * 2) * (clusterRange * 2);
            for (Base b : bases) {
                int dx = b.center.getX() - center.getX();
                int dy = b.center.getY() - center.getY();
                int dz = b.center.getZ() - center.getZ();
                if (dx * dx + dy * dy + dz * dz <= mergeSq) {
                    existing = b;
                    break;
                }
            }

            if (existing == null) {
                Base b = new Base(center, box, c.size());
                bases.add(b);
                if (notifications.get()) {
                    ClientManager.message("§6[BaseFinder] §fНайдена база: §a" + center.getX() + " " + center.getY() + " " + center.getZ() + " §7(" + c.size() + " бл.)");
                    if (sound.get()) {
                        try {
                            ru.levin.util.player.AudioUtil.playSound("nuron.wav");
                        } catch (Exception ignored) {
                        }
                    }
                }
            } else {
                existing.box = box;
                existing.count = c.size();
            }
        }
    }

    private void render(EventRender3D event) {
        if (bases.isEmpty()) return;
        boolean doFill = mode.is("Заливка") || mode.is("Оба");
        boolean doOutline = mode.is("Обводка") || mode.is("Оба");
        if (!doFill && !doOutline) return;

        int color = new Color(255, 80, 80).getRGB();
        Matrix4f matrix = RenderUtil.render3D.lastWorldSpaceMatrix;
        int outlineColor = RenderUtil.injectAlpha(color, 200);

        if (doFill) {
            BufferBuilder fillBuffer = IMinecraft.tessellator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            for (Base b : bases) {
                fillBox(fillBuffer, b.box, matrix);
            }
            RenderUtil.render3D.endBuilding(fillBuffer);
        }

        if (doOutline) {
            for (Base b : bases) {
                RenderUtil.render3D.drawHoleOutline(b.box.contract(0.01), outlineColor, 1.5f);
            }
        }
    }

    private void fillBox(BufferBuilder buffer, Box box, Matrix4f matrix) {
        float alpha = 0.30f;
        float minX = (float) box.minX, minY = (float) box.minY, minZ = (float) box.minZ;
        float maxX = (float) box.maxX, maxY = (float) box.maxY, maxZ = (float) box.maxZ;

        buffer.vertex(matrix, minX, minY, minZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, maxX, minY, minZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, maxX, minY, maxZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, minX, minY, maxZ).color(1f, 0.31f, 0.31f, alpha);

        buffer.vertex(matrix, minX, maxY, minZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, maxX, maxY, minZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, minX, maxY, maxZ).color(1f, 0.31f, 0.31f, alpha);

        buffer.vertex(matrix, minX, minY, minZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, minX, minY, maxZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, minX, maxY, maxZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, minX, maxY, minZ).color(1f, 0.31f, 0.31f, alpha);

        buffer.vertex(matrix, maxX, minY, minZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, maxX, minY, maxZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, maxX, maxY, minZ).color(1f, 0.31f, 0.31f, alpha);

        buffer.vertex(matrix, minX, minY, maxZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, maxX, minY, maxZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, minX, maxY, maxZ).color(1f, 0.31f, 0.31f, alpha);

        buffer.vertex(matrix, minX, minY, minZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, minX, maxY, minZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, maxX, maxY, minZ).color(1f, 0.31f, 0.31f, alpha);
        buffer.vertex(matrix, maxX, minY, minZ).color(1f, 0.31f, 0.31f, alpha);
    }

    private static class Base {
        BlockPos center;
        Box box;
        int count;

        Base(BlockPos center, Box box, int count) {
            this.center = center;
            this.box = box;
            this.count = count;
        }
    }
}
