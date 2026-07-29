package rich.screens.loading;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import ru.levin.util.render.RenderUtil;

public class Loading {

    private static Loading instance;

    private static final float FIXED_GUI_SCALE = 2.0f;

    private static final String[] LOADING_TEXTS = {
            "Запуск паста клиент",
            "Панчан ответь",
            "Панчан где связь",
            "Панчан молодец"
    };

    private static final long TEXT_DISPLAY_DURATION = 2200L;
    private static final long LAST_TEXT_DISPLAY_DURATION = 2500L;
    private static final long TEXT_TRANSITION_DURATION = 400L;
    private static final float ZOOM_LEVEL = 1.08f;

    private float animatedProgress = 0f;
    private float targetProgress = 0f;
    private float pulseTime = 0f;
    private long lastRenderTime = 0L;
    private long startTime = 0L;
    private boolean initialized = false;

    private int currentTextIndex = 0;
    private float currentTextOffsetY = 0f;
    private float currentTextAlpha = 1f;
    private float newTextOffsetY = -12f;
    private float newTextAlpha = 0f;
    private long lastTextChangeTime = 0L;
    private boolean isTransitioning = false;
    private long transitionStartTime = 0L;

    private float backgroundAlpha = 0f;
    private float contentAlpha = 0f;
    private boolean isFadingOut = false;
    private boolean readyToClose = false;

    private boolean resourcesLoaded = false;
    private boolean allTextsShown = false;
    private long lastTextShownTime = 0L;

    private final Particle[] particles = new Particle[40];
    private float progressGlow = 0f;

    public Loading() {
        instance = this;
        this.startTime = Util.getMeasuringTimeMs();
        this.lastTextChangeTime = this.startTime;
        initParticles();
    }

    public static Loading getInstance() {
        if (instance == null) {
            instance = new Loading();
        }
        return instance;
    }

    private void initParticles() {
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle();
        }
    }

    private static class Particle {
        float x, y, speed, size, alpha, drift;

        Particle() {
            reset(true);
        }

        void reset(boolean randomY) {
            x = (float) (Math.random() * 2000);
            y = randomY ? (float) (Math.random() * 1200) : 1200 + (float)(Math.random() * 100);
            speed = 8f + (float) (Math.random() * 25f);
            size = 1f + (float) (Math.random() * 2.5f);
            alpha = 0.08f + (float) (Math.random() * 0.2f);
            drift = -15f + (float)(Math.random() * 30f);
        }

        void update(float dt) {
            y -= speed * dt;
            x += drift * dt;
            if (y < -20) reset(false);
        }
    }

    private int getFixedScaledWidth() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) return 960;
        return (int) Math.ceil((double) client.getWindow().getFramebufferWidth() / FIXED_GUI_SCALE);
    }

    private int getFixedScaledHeight() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) return 540;
        return (int) Math.ceil((double) client.getWindow().getFramebufferHeight() / FIXED_GUI_SCALE);
    }

    public void render(int width, int height, float opacity, DrawContext drawContext) {
        MatrixStack matrixStack = drawContext.getMatrices();
        long currentTime = Util.getMeasuringTimeMs();

        if (!initialized) {
            lastRenderTime = currentTime;
            initialized = true;
        }

        float deltaTime = (currentTime - lastRenderTime) / 1000f;
        lastRenderTime = currentTime;
        deltaTime = MathHelper.clamp(deltaTime, 0.001f, 0.1f);

        updateAnimations(deltaTime, currentTime);

        int fixedWidth = getFixedScaledWidth();
        int fixedHeight = getFixedScaledHeight();

        RenderUtil.drawTexture(matrixStack, "images/icons/mainmenu/background.png", 0, 0, fixedWidth, fixedHeight, 0f,
                withAlpha(0xFFFFFFFF, (int) (backgroundAlpha * opacity * 255)));

        float finalContentAlpha = contentAlpha * opacity;

        if (finalContentAlpha > 0.001f) {
            renderLogo(fixedWidth, fixedHeight, finalContentAlpha, drawContext);
            renderProgressBar(fixedWidth, fixedHeight, finalContentAlpha, drawContext);
        }

    }

    private void updateAnimations(float deltaTime, long currentTime) {
        pulseTime += deltaTime;
        animatedProgress = MathHelper.lerp(deltaTime * 5f, animatedProgress, targetProgress);
        progressGlow += deltaTime * 3f;
        backgroundAlpha = MathHelper.lerp(deltaTime * 4f, backgroundAlpha, 1f);
        if (backgroundAlpha > 0.99f) backgroundAlpha = 1f;

        if (!isFadingOut) {
            contentAlpha = MathHelper.lerp(deltaTime * 2.5f, contentAlpha, 1f);
            if (contentAlpha > 0.99f) contentAlpha = 1f;
        } else {
            contentAlpha -= deltaTime * 1.8f;
            if (contentAlpha < 0f) {
                contentAlpha = 0f;
                readyToClose = true;
            }
        }

        if (!isFadingOut) {
            updateTextAnimation(currentTime, deltaTime);
        }

        if (allTextsShown && resourcesLoaded && !isFadingOut) {
            long elapsed = currentTime - lastTextShownTime;
            if (elapsed >= LAST_TEXT_DISPLAY_DURATION) {
                isFadingOut = true;
            }
        }
    }

    private void updateTextAnimation(long currentTime, float deltaTime) {
        if (allTextsShown) return;

        if (!isTransitioning) {
            long elapsed = currentTime - lastTextChangeTime;

            if (currentTextIndex >= LOADING_TEXTS.length - 1) {
                if (!allTextsShown) {
                    allTextsShown = true;
                    lastTextShownTime = currentTime;
                }
                return;
            }

            if (elapsed >= TEXT_DISPLAY_DURATION) {
                isTransitioning = true;
                transitionStartTime = currentTime;
            }
        }

        if (isTransitioning) {
            long elapsed = currentTime - transitionStartTime;
            float rawProgress = MathHelper.clamp((float) elapsed / TEXT_TRANSITION_DURATION, 0f, 1f);
            float eased = easeOutCubic(rawProgress);

            currentTextOffsetY = 14f * eased;
            currentTextAlpha = MathHelper.clamp(1f - eased * 1.5f, 0f, 1f);

            newTextOffsetY = -12f * (1f - eased);
            newTextAlpha = MathHelper.clamp(eased * 1.3f, 0f, 1f);

            if (rawProgress >= 1f) {
                isTransitioning = false;
                currentTextIndex++;
                currentTextOffsetY = 0f;
                currentTextAlpha = 1f;
                newTextOffsetY = -12f;
                newTextAlpha = 0f;
                lastTextChangeTime = currentTime;

                if (currentTextIndex >= LOADING_TEXTS.length - 1) {
                    allTextsShown = true;
                    lastTextShownTime = currentTime;
                }
            }
        }
    }

    private void renderParticles(int width, int height, float opacity, float dt, DrawContext drawContext) {
        MatrixStack matrixStack = drawContext.getMatrices();
        for (Particle p : particles) {
            p.update(dt);
            int alpha = (int)(p.alpha * opacity * 255);
            if (alpha <= 0) continue;

            int color = withAlpha(0xFFFFFFFF, alpha);
            RenderUtil.drawRoundedRect(matrixStack, p.x, p.y, p.size, p.size, p.size / 2f, color);
        }
    }

    private void renderLogo(int width, int height, float opacity, DrawContext drawContext) {
        MatrixStack matrixStack = drawContext.getMatrices();
        float centerX = width / 2f;
        float centerY = height / 2f - 40;

        int textAlpha = (int) (opacity * 255);
        float fontSize = 44f;
        
        float breathe = (float) Math.sin(pulseTime * 1.2f) * 1.5f;

        String icon = "A";
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float iconW = textRenderer.getWidth(icon);
        float iconH = textRenderer.fontHeight;

        float iconX = centerX - iconW / 2f;
        float iconY = centerY - iconH / 2f + breathe;

        float glowPulse = 0.4f + 0.2f * (float) Math.sin(pulseTime * 1.5f);
        int glowAlpha = (int)(textAlpha * glowPulse);
        int glowColor = withAlpha(0xFFFF7276, glowAlpha);
        drawContext.drawText(textRenderer, Text.of(icon), (int)iconX, (int)(iconY + 3), glowColor, false);

        int shadowColor = withAlpha(0xFF000000, textAlpha / 3);
        drawContext.drawText(textRenderer, Text.of(icon), (int)iconX, (int)(iconY + 2), shadowColor, false);

        int mainColor = withAlpha(0xFFFFFFFF, textAlpha);
        drawContext.drawText(textRenderer, Text.of(icon), (int)iconX, (int)iconY, mainColor, false);
    }

    private void renderSubtitle(int width, int height, float opacity, DrawContext drawContext) {
        MatrixStack matrixStack = drawContext.getMatrices();
        float centerX = width / 2f;
        float subtitleY = height / 2f - 8;

        int alpha = (int)(opacity * 180);
        float fontSize = 9f;

        String subtitle = "R I C H  C L I E N T";
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float subtitleW = textRenderer.getWidth(subtitle);
        
        float lineW = 30;
        float lineY = subtitleY + fontSize / 2f;
        float gap = 8;
        int lineAlpha = (int)(opacity * 40);
        int lineColor = withAlpha(0xFFFF7276, lineAlpha);

        RenderUtil.drawRoundedRect(matrixStack, centerX - subtitleW / 2f - gap - lineW, lineY, lineW, 0.5f, 0f, lineColor);
        RenderUtil.drawRoundedRect(matrixStack, centerX + subtitleW / 2f + gap, lineY, lineW, 0.5f, 0f, lineColor);

        drawContext.drawText(textRenderer, Text.of(subtitle), (int)(centerX - subtitleW / 2f), (int)subtitleY, withAlpha(0xFFFFFFFF, alpha), false);
    }

    private void renderProgressBar(int width, int height, float opacity, DrawContext drawContext) {
        MatrixStack matrixStack = drawContext.getMatrices();
        float centerX = width / 2f;
        float barY = height / 2f + 12;
        float barW = 180;
        float barH = 3;
        float barX = centerX - barW / 2f;

        int bgAlpha = (int)(opacity * 60);
        RenderUtil.drawRoundedRect(matrixStack, barX, barY, barW, barH, 1.5f, withAlpha(0xFFFFFFFF, bgAlpha));
        
        if (animatedProgress > 0) {
            float filledW = barW * MathHelper.clamp(animatedProgress, 0f, 1f);
            
            int a1 = (int)(opacity * 200);
            // Use single color instead of gradient
            int fillColor = withAlpha(0xFFFF7276, a1);
            RenderUtil.drawRoundedRect(matrixStack, barX, barY, filledW, barH, 1.5f, fillColor);
            
            float glowPos = (float)((progressGlow * 0.3f) % 1.0);
            float glowX = barX + filledW * glowPos;
            float glowW = 20;
            if (glowX + glowW > barX + filledW) glowW = barX + filledW - glowX;
            if (glowW > 0) {
                int shineA = (int)(opacity * 80);
                RenderUtil.drawRoundedRect(matrixStack, glowX, barY, glowW, barH, 1.5f, withAlpha(0xFFFFFFFF, shineA));
            }
        }
        
        String pct = (int)(animatedProgress * 100) + "%";
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        drawContext.drawText(textRenderer, Text.of(pct), (int)(barX + barW + 8), (int)(barY - 1), withAlpha(0xFFFFFFFF, (int)(opacity * 120)), false);
    }

    private void renderLoadingText(int width, int height, float opacity, long currentTime, DrawContext drawContext) {
        float fontSize = 10f;
        float baseY = height / 2f + 30;
        float centerX = width / 2f;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        if (currentTextAlpha > 0.01f && currentTextIndex < LOADING_TEXTS.length) {
            String currentText = LOADING_TEXTS[currentTextIndex];
            float currentWidth = textRenderer.getWidth(currentText);
            int alpha = (int) (opacity * currentTextAlpha * 220);

            drawContext.drawText(textRenderer, Text.of(currentText), (int) (centerX - currentWidth / 2f), (int) (baseY + currentTextOffsetY),
                    withAlpha(0xFFFFFFFF, alpha), false);
        }

        if (isTransitioning && newTextAlpha > 0.01f) {
            int nextIndex = currentTextIndex + 1;
            if (nextIndex < LOADING_TEXTS.length) {
                String nextText = LOADING_TEXTS[nextIndex];
                float nextWidth = textRenderer.getWidth(nextText);
                int alpha = (int) (opacity * newTextAlpha * 220);

                drawContext.drawText(textRenderer, Text.of(nextText), (int) (centerX - nextWidth / 2f), (int) (baseY + newTextOffsetY),
                        withAlpha(0xFFFFFFFF, alpha), false);
            }
        }
    }

    private void renderBottomInfo(int width, int height, float opacity, DrawContext drawContext) {
        MatrixStack matrixStack = drawContext.getMatrices();
        float y = height - 20;
        int alpha = (int)(opacity * 50);
        float fontSize = 6f;
        
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        
        String version = "PastaRicha · 1.21.11";
        drawContext.drawText(textRenderer, Text.of(version), 15, (int)y, withAlpha(0xFFFFFFFF, alpha), false);
        
        String copyright = "Panhan Client";
        float copyrightW = textRenderer.getWidth(copyright);
        drawContext.drawText(textRenderer, Text.of(copyright), (int)(width - copyrightW - 15), (int)y, withAlpha(0xFFFFFFFF, alpha), false);

        if (!allTextsShown) {
            int dotCount = ((int)(pulseTime * 2f)) % 4;
            String dots = ".".repeat(dotCount);
            drawContext.drawText(textRenderer, Text.of(dots), (int)(width / 2f - textRenderer.getWidth("...") / 2f), (int)y, withAlpha(0xFFFF7276, (int)(opacity * 80)), false);
        }
    }
    private float easeOutCubic(float x) {
        return 1f - (float) Math.pow(1f - x, 3);
    }
    public void markComplete() {
        resourcesLoaded = true;
    }

    public boolean isContentFadedOut() {
        return isFadingOut && contentAlpha <= 0.01f;
    }

    public boolean isReadyToClose() {
        return readyToClose;
    }

    public boolean isComplete() {
        return allTextsShown && resourcesLoaded;
    }

    public boolean isFadingOut() {
        return isFadingOut;
    }

    public float getContentAlpha() {
        return contentAlpha;
    }

    public void setProgress(float progress) {
        this.targetProgress = MathHelper.clamp(progress, 0f, 1f);
    }

    public float getProgress() {
        return targetProgress;
    }

    public void reset() {
        animatedProgress = 0f;
        targetProgress = 0f;
        pulseTime = 0f;
        lastRenderTime = 0L;
        startTime = Util.getMeasuringTimeMs();
        initialized = false;
        currentTextIndex = 0;
        currentTextOffsetY = 0f;
        currentTextAlpha = 1f;
        newTextOffsetY = -12f;
        newTextAlpha = 0f;
        lastTextChangeTime = startTime;
        isTransitioning = false;
        transitionStartTime = 0L;
        backgroundAlpha = 0f;
        contentAlpha = 0f;
        isFadingOut = false;
        readyToClose = false;
        resourcesLoaded = false;
        allTextsShown = false;
        lastTextShownTime = 0L;
        initParticles();
    }

    public long getStartTime() {
        return startTime;
    }

    private int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (MathHelper.clamp(alpha, 0, 255) << 24);
    }
}
