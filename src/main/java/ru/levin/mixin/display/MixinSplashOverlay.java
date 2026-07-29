package ru.levin.mixin.display;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.levin.mixin.iface.SplashOverlayAccessor;

@Mixin(SplashOverlay.class)
public class MixinSplashOverlay {

    private static final Identifier BACKGROUND = Identifier.of("sodiumextra", "images/icons/mainmenu/background.png");
    private static final Identifier LOGO = Identifier.of("sodiumextra", "images/logo/lovan.png");
    private static final Identifier WHITE = Identifier.of("sodiumextra", "textures/misc/white.png");

    private static boolean drawTexturedQuad(DrawContext context, Identifier texture, float x, float y, float w, float h, int color) {
        ShaderProgram shader = RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        if (shader == null) return false;
        RenderSystem.setShaderTexture(0, texture);

        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(mat, x, y, 0).texture(0f, 0f).color(color);
        buffer.vertex(mat, x + w, y, 0).texture(1f, 0f).color(color);
        buffer.vertex(mat, x + w, y + h, 0).texture(1f, 1f).color(color);
        buffer.vertex(mat, x, y + h, 0).texture(0f, 1f).color(color);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        return true;
    }

    private static boolean drawSolidQuad(DrawContext context, float x, float y, float w, float h, int color) {
        return drawTexturedQuad(context, WHITE, x, y, w, h, color);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderHook(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        try {
            int width = context.getScaledWindowWidth();
            int height = context.getScaledWindowHeight();

            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            if (!drawTexturedQuad(context, BACKGROUND, 0, 0, width, height, 0xFFFFFFFF)) {
                return;
            }

            float progress = 0f;
            try {
                progress = ((SplashOverlayAccessor) this).getProgress();
            } catch (Throwable ignored) {
            }
            progress = MathHelper.clamp(progress, 0f, 1f);

            float barW = width - 40f;
            float barH = 2f;
            float barX = 20f;
            float barY = height - 28f;

            int bg = 0x2FFFFFFF;
            int fg = 0xDFFFFFFF;

            if (!drawSolidQuad(context, barX, barY, barW, barH, bg)) {
                return;
            }
            if (!drawSolidQuad(context, barX, barY, barW * progress, barH, fg)) {
                return;
            }

            float logoW = 220f;
            float logoH = 72f;
            float logoX = width / 2f - logoW / 2f;
            float logoY = 34f;

            if (!drawTexturedQuad(context, LOGO, logoX, logoY, logoW, logoH, 0xFFFFFFFF)) {
                return;
            }

            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();

            ci.cancel();
        } catch (Throwable ignored) {
            // Fallback to vanilla overlay rendering if core shaders/textures aren't ready yet
        }
    }
}
