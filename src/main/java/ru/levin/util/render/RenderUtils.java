package ru.levin.util.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class RenderUtils {
    
    public static void drawRoundedRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
        // Simple rounded rectangle implementation
        fill(matrices, x + radius, y, x + width - radius, y + height, color);
        fill(matrices, x, y + radius, x + width, y + height - radius, color);
        
        // Corners
        fillCircle(matrices, x + radius, y + radius, radius, color);
        fillCircle(matrices, x + width - radius, y + radius, radius, color);
        fillCircle(matrices, x + radius, y + height - radius, radius, color);
        fillCircle(matrices, x + width - radius, y + height - radius, radius, color);
    }
    
    private static void fill(MatrixStack matrices, float x, float y, float width, float height, int color) {
        // Simple fill implementation
        // This would need proper implementation with DrawContext
    }
    
    private static void fillCircle(MatrixStack matrices, float x, float y, float radius, int color) {
        // Simple circle fill implementation
        // This would need proper implementation
    }
}
