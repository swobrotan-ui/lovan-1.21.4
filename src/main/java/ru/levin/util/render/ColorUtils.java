package ru.levin.util.render;

public class ColorUtils {
    
    public static int rgb(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }
    
    public static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    public static int getColor(int index) {
        // Simple gradient effect
        int color1 = 0xFF6B6B;
        int color2 = 0x4ECDC4;
        return gradient(color1, color2, index * 16, 10);
    }
    
    public static int gradient(int color1, int color2, int index, int max) {
        if (index <= 0) return color1;
        if (index >= max) return color2;
        
        float ratio = (float) index / max;
        
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        
        return (r << 16) | (g << 8) | b;
    }
    
    public static int setAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0xFFFFFF);
    }
}
