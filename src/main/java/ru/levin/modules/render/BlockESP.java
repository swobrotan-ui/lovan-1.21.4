package ru.levin.modules.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import ru.levin.manager.IMinecraft;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.MultiSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.events.Event;
import ru.levin.events.impl.render.EventRender3D;
import ru.levin.util.render.RenderUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FunctionAnnotation(name = "BlockESP", desc = "Usefull блоки через стены с заливкой", type = Type.Render)
public class BlockESP extends Function {

    private final SliderSetting radius = new SliderSetting("Радиус", 20f, 1f, 40f, 1f);
    private final ModeSetting mode = new ModeSetting("Режим", "Обводка", "Обводка", "Заливка", "Оба");
    private final SliderSetting opacity = new SliderSetting("Прозрачность", 0.55f, 0.1f, 1f, 0.05f);

    private final MultiSetting blocks = new MultiSetting(
            "Блоки",
            Arrays.asList("Сундуки", "Спавнеры", "Эндер сундуки", "Руда", "Незеритовые обломки"),
            new String[]{
                    "Сундуки", "Трапперed сундуки", "Спавнеры", "Варочные",
                    "Эндер сундуки", "Детектор рельс", "Руда", "Андезит",
                    "Обсидиан", "Структуры", "Поршни", "Шалкер", "Незеритовые обломки"
            }
    );

    private final Map<Block, ColorInfo> blockColors = new HashMap<>();
    private final Map<String, ColorInfo> customBlocks = new HashMap<>();

    public BlockESP() {
        addSettings(radius, mode, opacity, blocks);
        initColors();
    }

    public void addCustomBlock(String blockId, Color color) {
        Block block = Registries.BLOCK.get(Identifier.of(blockId));
        if (block != Blocks.AIR) {
            customBlocks.put(blockId, new ColorInfo(color, false, blockId));
        }
    }

    public void removeCustomBlock(String blockId) {
        customBlocks.remove(blockId);
    }

    public Map<String, ColorInfo> getCustomBlocks() {
        return customBlocks;
    }

    private void initColors() {
        blockColors.put(Blocks.CHEST, new ColorInfo(new Color(139, 69, 19), true, "Сундуки"));
        blockColors.put(Blocks.TRAPPED_CHEST, new ColorInfo(new Color(180, 80, 20), true, "Трапперed сундуки"));
        blockColors.put(Blocks.SPAWNER, new ColorInfo(new Color(200, 50, 255), true, "Спавнеры"));
        blockColors.put(Blocks.BREWING_STAND, new ColorInfo(new Color(0, 180, 255), true, "Варочные"));
        blockColors.put(Blocks.ENDER_CHEST, new ColorInfo(new Color(75, 0, 130), true, "Эндер сундуки"));
        blockColors.put(Blocks.DETECTOR_RAIL, new ColorInfo(new Color(255, 165, 0), false, "Детектор рельс"));
        addOreColors();
        blockColors.put(Blocks.ANDESITE, new ColorInfo(new Color(120, 120, 120), false, "Андезит"));
        blockColors.put(Blocks.OBSIDIAN, new ColorInfo(new Color(20, 10, 40), false, "Обсидиан"));
        blockColors.put(Blocks.SHULKER_BOX, new ColorInfo(new Color(255, 105, 180), true, "Шалкер"));
        blockColors.put(Blocks.PISTON, new ColorInfo(new Color(100, 100, 100), false, "Поршни"));
        blockColors.put(Blocks.STICKY_PISTON, new ColorInfo(new Color(120, 120, 120), false, "Поршни"));
        blockColors.put(Blocks.JIGSAW, new ColorInfo(new Color(255, 255, 0), false, "Структуры"));
        blockColors.put(Blocks.STRUCTURE_BLOCK, new ColorInfo(new Color(255, 255, 0), false, "Структуры"));
    }

    private void addOreColors() {
        blockColors.put(Blocks.COAL_ORE, new ColorInfo(new Color(30, 30, 30), true, "Руда"));
        blockColors.put(Blocks.COPPER_ORE, new ColorInfo(new Color(180, 100, 60), true, "Руда"));
        blockColors.put(Blocks.IRON_ORE, new ColorInfo(new Color(210, 160, 140), true, "Руда"));
        blockColors.put(Blocks.GOLD_ORE, new ColorInfo(new Color(255, 215, 0), true, "Руда"));
        blockColors.put(Blocks.DIAMOND_ORE, new ColorInfo(new Color(0, 200, 255), true, "Руда"));
        blockColors.put(Blocks.EMERALD_ORE, new ColorInfo(new Color(0, 255, 100), true, "Руда"));
        blockColors.put(Blocks.LAPIS_ORE, new ColorInfo(new Color(30, 100, 255), true, "Руда"));
        blockColors.put(Blocks.REDSTONE_ORE, new ColorInfo(new Color(255, 30, 30), true, "Руда"));
        blockColors.put(Blocks.NETHER_QUARTZ_ORE, new ColorInfo(new Color(240, 230, 220), true, "Руда"));
        blockColors.put(Blocks.DEEPSLATE_COAL_ORE, new ColorInfo(new Color(30, 30, 30), true, "Руда"));
        blockColors.put(Blocks.DEEPSLATE_COPPER_ORE, new ColorInfo(new Color(180, 100, 60), true, "Руда"));
        blockColors.put(Blocks.DEEPSLATE_IRON_ORE, new ColorInfo(new Color(210, 160, 140), true, "Руда"));
        blockColors.put(Blocks.DEEPSLATE_GOLD_ORE, new ColorInfo(new Color(255, 215, 0), true, "Руда"));
        blockColors.put(Blocks.DEEPSLATE_DIAMOND_ORE, new ColorInfo(new Color(0, 200, 255), true, "Руда"));
        blockColors.put(Blocks.DEEPSLATE_EMERALD_ORE, new ColorInfo(new Color(0, 255, 100), true, "Руда"));
        blockColors.put(Blocks.DEEPSLATE_LAPIS_ORE, new ColorInfo(new Color(30, 100, 255), true, "Руда"));
        blockColors.put(Blocks.DEEPSLATE_REDSTONE_ORE, new ColorInfo(new Color(255, 30, 30), true, "Руда"));
        // Ancient Debris / Netherite — яркие цвета, чтобы не сливались с базой
        blockColors.put(Blocks.ANCIENT_DEBRIS, new ColorInfo(new Color(255, 140, 0), true, "Незеритовые обломки"));
        blockColors.put(Blocks.NETHERITE_BLOCK, new ColorInfo(new Color(130, 80, 200), true, "Незеритовые обломки"));
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventRender3D)) return;
        if (mc.world == null || mc.player == null) return;

        int r = radius.get().intValue();
        BlockPos playerPos = mc.player.getBlockPos();
        float alpha = opacity.get().floatValue();
        boolean doFill = mode.is("Заливка") || mode.is("Оба");
        boolean doOutline = mode.is("Обводка") || mode.is("Оба");

        if (!doFill && !doOutline) return;

        var tessellator = IMinecraft.tessellator();
        Matrix4f matrix = RenderUtil.render3D.lastWorldSpaceMatrix;
        BufferBuilder fillBuffer = doFill ? tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR) : null;
        int fillAlphaValue = (int) (alpha * 80);

        for (int x = playerPos.getX() - r; x <= playerPos.getX() + r; x++) {
            for (int y = playerPos.getY() - r; y <= playerPos.getY() + r; y++) {
                for (int z = playerPos.getZ() - r; z <= playerPos.getZ() + r; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    Block block = state.getBlock();
                    ColorInfo info = blockColors.get(block);
                    if (info == null) {
                        String id = Registries.BLOCK.getId(block).toString();
                        info = customBlocks.get(id);
                    }
                    if (info == null || !blocks.get(info.displayName)) continue;

                    int argb = info.color.getRGB();
                    float red = ((argb >> 16) & 0xFF) / 255F;
                    float green = ((argb >> 8) & 0xFF) / 255F;
                    float blue = (argb & 0xFF) / 255F;
                    float fillA = fillAlphaValue / 255F;

                    if (doFill) {
                        drawFilledBox(fillBuffer, pos, red, green, blue, fillA, matrix);
                    }

                    if (doOutline) {
                        int outlineColor = RenderUtil.injectAlpha(argb, (int) (alpha * 255));
                        Box outlineBox = new Box(pos).contract(0.01);
                        RenderUtil.render3D.drawHoleOutline(outlineBox, outlineColor, info.glow ? 2.5f : 1.5f);
                    }
                }
            }
        }

        if (doFill) {
            RenderUtil.render3D.endBuilding(fillBuffer);
        }
    }

    private void drawFilledBox(BufferBuilder buffer, BlockPos pos, float r, float g, float b, float a, Matrix4f matrix) {
        float minX = pos.getX();
        float minY = pos.getY();
        float minZ = pos.getZ();
        float maxX = minX + 1;
        float maxY = minY + 1;
        float maxZ = minZ + 1;

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
    }

    private record ColorInfo(Color color, boolean glow, String displayName) {
    }
}
