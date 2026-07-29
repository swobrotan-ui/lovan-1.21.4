package ru.levin.screens.dropdown;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.gl.ShaderProgramKeys;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Hand;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.glfw.GLFW;
import ru.levin.manager.ClientManager;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.manager.themeManager.Style;
import ru.levin.modules.Function;
import ru.levin.modules.Type;
import ru.levin.modules.setting.*;
import ru.levin.protect.AES;
import ru.levin.screens.dropdown.impl.*;
import ru.levin.screens.dropdown.search.SearchState;
import ru.levin.util.animations.impl.EaseInOutQuad;
import ru.levin.util.color.ColorUtil;
import ru.levin.manager.fontManager.FontUtils;
import ru.levin.util.math.MathUtil;
import ru.levin.util.render.RenderAddon;
import ru.levin.util.render.RenderUtil;
import ru.levin.util.render.Scissor;
import ru.levin.util.render.providers.ResourceProvider;
import java.awt.*;
import java.util.*;

import static ru.levin.util.render.RenderUtil.drawBlur;

public class ClickGUI extends Screen implements IMinecraft {
    private boolean isClose;

    private final int PANEL_WIDTH = 110;
    private final int PANEL_HEIGHT = 248;
    private final int PANEL_MARGIN = 5;

    private final Color GUI_COLOR = Manager.FUNCTION_MANAGER.clickGUI.getGuiColor();

    private final int TITLE_MARGIN_TOP = 4;
    private final int TITLE_HEIGHT = 16;

    private final int FUNCTION_HEIGHT = 16;
    private final int FUNCTION_GAP = 4;

    private final int SCROLL_AREA_Y_OFFSET = TITLE_MARGIN_TOP + TITLE_HEIGHT;
    private final int SCROLL_AREA_HEIGHT = PANEL_HEIGHT - SCROLL_AREA_Y_OFFSET - 5;

    private final Set<Type> renderCategories = EnumSet.of(Type.Combat, Type.Move, Type.Render, Type.Player, Type.Misc);
    private static final Map<Type, Float> scrollOffsets = new HashMap<>();
    private static final Map<Type, Float> scrollTargets = new HashMap<>();
    private final Map<Function, Float> arrowRotationProgress = new HashMap<>();
    private final EaseInOutQuad animationOpen = new EaseInOutQuad(250, 1);
    private double animation;

    private final Map<Function, Float> expandProgress = new HashMap<>();
    private final Map<Function, Float> hoverProgress = new HashMap<>();

    private final SearchState searchState;
    private boolean bindMenuOpen = false;
    private boolean bindMenuListening = false;
    private Function bindMenuFunction = null;
    private int bindMenuX = 0;
    private int bindMenuY = 0;

    private boolean cfgMenuOpen = false;
    private float cfgScrollOffset = 0f;
    private float cfgScrollTarget = 0f;
    private boolean cfgNameFocused = false;
    private String cfgNameText = "";
    private int cfgNameCursor = 0;

    private SliderSetting draggingSlider = null;
    private int draggingSliderX = 0;
    private int draggingSliderWidth = 0;

    private final BooleanSettingRenderer booleanSettingRenderer = new BooleanSettingRenderer();
    private final BindBooleanSettingRenderer bindbooleanSettingRenderer = new BindBooleanSettingRenderer();
    private final BindSettingRenderer bindSettingRenderer = new BindSettingRenderer();
    private final ModeSettingRenderer modeSettingRenderer = new ModeSettingRenderer();
    private final MultiSettingRenderer multiSettingRenderer = new MultiSettingRenderer();
    private final SliderSettingRenderer sliderSettingRenderer = new SliderSettingRenderer();
    private final TextSettingRenderer textSettingRenderer = new TextSettingRenderer();

    private final int SEARCH_HEIGHT = 20;
    private final int SEARCH_MARGIN_BOTTOM = 10;
    private final int SEARCH_MAX_WIDTH = 170;

    private final int THEME_HEIGHT = 16;
    private final int THEME_MARGIN_BOTTOM = 40;
    private final int THEME_MAX_WIDTH = 180;
    private static float themeScrollOffset = 0;
    private static float themeScrollTarget = 0;
    private final int VISIBLE_THEMES = 11;

    private float themeMenuAnim = 0f;
    private float themeMenuTarget = 0f;
    private float themeAlphaAnim = 0f;
    private static final float THEME_ANIM_SPEED = 0.2f;
    private static boolean themeMenu;
    private float themeNameAnim = 0f;

    private final float SCROLL_SPEED = 12f;
    private final float SCROLL_LERP_FACTOR = 20f;
    private final float SCROLL_SMOOTH_FACTOR = 12f;

    private final float THEME_SCROLL_SPEED = 15f;
    private final float THEME_SCROLL_LERP_FACTOR = 15f;

    private static boolean colorPickerOpen = false;
    private static int selectedColor1 = Color.WHITE.getRGB();
    private static int selectedColor2 = Color.WHITE.getRGB();

    private float picker1CursorX = 0.5f;
    private float picker1CursorY = 0.5f;
    private float picker2CursorX = 0.5f;
    private float picker2CursorY = 0.5f;
    private boolean draggingPicker1 = false;
    private boolean draggingPicker2 = false;
    private float colorPickerAnim = 0f;

    private Function previewFunction = null;

    private static final int PREVIEW_OVERLAY_W = 240;
    private static final int PREVIEW_OVERLAY_H = 180;

    private boolean hasPreviewPill(Function f) {
        if (f == null || f.name == null) return false;
        return f.name.equalsIgnoreCase("TargetESP")
                || f.name.equalsIgnoreCase("SwingAnimations")
                || f.name.equalsIgnoreCase("ViewModel");
    }

    private void renderPreviewOverlay(DrawContext ctx, Function f, int x, int y, int w, int h) {
        int bg = new Color(0, 0, 0, 55).getRGB();
        RenderUtil.drawRoundedRect(ctx.getMatrices(), x, y, w, h, 14, bg);
        RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, w, h, 14, 0.8f, ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.10f));

        if (f.name.equalsIgnoreCase("TargetESP")) {
            Entity t = Manager.FUNCTION_MANAGER.attackAura != null ? Manager.FUNCTION_MANAGER.attackAura.target : null;
            if (t == null) t = mc.player;

            if (t instanceof LivingEntity le) {
                int cx = x + w / 2;
                int cy = y + h - 14;
                int size = 62;
                InventoryScreen.drawEntity(ctx, cx, cy, size, 0, 0, 0f, 0f, 0f, le);

                renderEffectIconsAround(ctx, le, x + w / 2f, y + h * 0.32f, 36f);
            }
        } else {
            if (mc.player == null) return;
            ItemStack item = mc.player.getMainHandStack();
            if (item.isEmpty()) item = mc.player.getOffHandStack();
            if (item.isEmpty()) return;

            float tickDelta = IMinecraft.tickCounter().getTickDelta(true);
            int light = 15728880;

            ctx.getMatrices().push();
            ctx.getMatrices().translate(x + w / 2f, y + h * 0.70f, 200);
            float sc = 30f;
            ctx.getMatrices().scale(sc, -sc, sc);

            VertexConsumerProvider.Immediate consumers = mc.getBufferBuilders().getEntityVertexConsumers();
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) mc.player;

            if (f.name.equalsIgnoreCase("SwingAnimations")) {
                Manager.FUNCTION_MANAGER.swingAnimations.renderFirstPersonItem(player, tickDelta, 0f, Hand.MAIN_HAND, 0.65f, item, 0f, ctx.getMatrices(), consumers, light);
            } else {
                var vm = Manager.FUNCTION_MANAGER.viewModel;
                ctx.getMatrices().translate(vm.right_x.get().floatValue(), vm.right_y.get().floatValue(), vm.right_z.get().floatValue());
                mc.getItemRenderer().renderItem(player, item, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, false, ctx.getMatrices(), consumers, mc.world, light, 0, player.getId() + ModelTransformationMode.FIRST_PERSON_RIGHT_HAND.ordinal());
            }
            consumers.draw();
            ctx.getMatrices().pop();

            renderEffectIconsAround(ctx, mc.player, x + w / 2f, y + h * 0.28f, 32f);
        }
    }

    private void renderEffectIconsAround(DrawContext ctx, LivingEntity le, float cx, float cy, float radius) {
        if (le == null) return;
        StatusEffectSpriteManager spriteManager = mc.getStatusEffectSpriteManager();
        java.util.ArrayList<StatusEffectInstance> effects = new java.util.ArrayList<>(le.getStatusEffects());
        if (effects.isEmpty()) return;

        int max = Math.min(8, effects.size());
        float icon = 10f;
        for (int i = 0; i < max; i++) {
            StatusEffectInstance eff = effects.get(i);
            Sprite sprite = spriteManager.getSprite(eff.getEffectType());
            float ang = (float) (i * (Math.PI * 2.0 / max));
            float x = cx + (float) Math.cos(ang) * radius - icon / 2f;
            float y = cy + (float) Math.sin(ang) * radius - icon / 2f;
            drawSprite(ctx.getMatrices(), sprite, x, y, icon, icon, ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.92f));
        }
    }

    private void drawSprite(net.minecraft.client.util.math.MatrixStack matrices, Sprite sprite, float x, float y, float w, float h, int color) {
        RenderUtil.enableRender();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, sprite.getAtlasId());

        float a = ((color >>> 24) & 0xFF) / 255f;
        float r = ((color >>> 16) & 0xFF) / 255f;
        float g = ((color >>> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();

        var mat = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(mat, x, y + h, 0).texture(u0, v1).color(r, g, b, a);
        buffer.vertex(mat, x + w, y + h, 0).texture(u1, v1).color(r, g, b, a);
        buffer.vertex(mat, x + w, y, 0).texture(u1, v0).color(r, g, b, a);
        buffer.vertex(mat, x, y, 0).texture(u0, v0).color(r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderUtil.disableRender();
    }

    public ClickGUI() {
        super(Text.literal("ClickGUI"));
        this.searchState = new SearchState();
        if (scrollOffsets.isEmpty() && scrollTargets.isEmpty()) {
            renderCategories.forEach(cat -> {
                scrollOffsets.put(cat, 0f);
                scrollTargets.put(cat, 0f);
            });
        }
    }

    private int getSettingsIconSize() {
        return 12;
    }

    @Override
    public void init() {
        super.init();

        isClose = false;
        animationOpen.setDirection(Direction.AxisDirection.POSITIVE);
        animationOpen.reset();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        animation = animationOpen.getOutput();
        if (isClose && animationOpen.finished(Direction.AxisDirection.NEGATIVE)) {
            super.close();
            return;
        }
        if (Manager.FUNCTION_MANAGER.clickGUI.guiType.is("Категории")) {
            close();
        }
        if (animation <= 0.01) return;

        super.render(ctx, mouseX, mouseY, delta);
        ctx.getMatrices().push();

        for (Type category : renderCategories) {
            float target = scrollTargets.get(category);
            float current = scrollOffsets.get(category);
            float newOffset = MathUtil.lerp(current, target, SCROLL_LERP_FACTOR);
            scrollOffsets.put(category, newOffset);
        }

        int totalWidth = renderCategories.size() * (PANEL_WIDTH + PANEL_MARGIN) - PANEL_MARGIN;
        int startX = (width - totalWidth) / 2;
        int startY = (height - PANEL_HEIGHT) / 2;

        RenderAddon.sizeAnimation(ctx.getMatrices(), width / 2, height / 2, animation);

        int idx = 0;
        for (Type category : renderCategories) {
            renderPanel(ctx, startX + idx++ * (PANEL_WIDTH + PANEL_MARGIN), startY, category, mouseX, mouseY);
        }

        Function hoveredFunction = getHoveredFunction(mouseX, mouseY, startX, startY);
        if (hoveredFunction != null && hoveredFunction.desc != null && !hoveredFunction.desc.isEmpty()) {
            drawDescription(ctx, hoveredFunction.desc, startY);
        }
        DescriptionRenderQueue.renderAll(ctx);

        {
            int iconSize = getSettingsIconSize();
            int iconX = startX + totalWidth - iconSize;
            int iconY = startY - iconSize - 6;
            RenderUtil.drawTexture(ctx.getMatrices(), "images/icons/mainmenu/icons/settings.png", iconX, iconY, iconSize, iconSize, 1, Color.WHITE.getRGB());
        }

        // нижний поиск оставляем, выбор цвета/тем (renderButtomTheme, renderTheme) убираем из GUI
        renderSearchField(ctx);
        renderBindMenu(ctx, mouseX, mouseY);

        // large preview overlay (not part of panels)
        Function pf = previewFunction;
        if (pf != null && pf.expanded && hasPreviewPill(pf)) {
            int px = startX + totalWidth + 10;
            int py = startY + 10;
            renderPreviewOverlay(ctx, pf, px, py, PREVIEW_OVERLAY_W, PREVIEW_OVERLAY_H);
        }

        ctx.getMatrices().pop();
    }

    private void renderBindMenu(DrawContext ctx, int mouseX, int mouseY) {
        if (!bindMenuOpen || bindMenuFunction == null) return;

        int menuW = 92;
        int menuH = 38;

        int x = MathHelper.clamp(bindMenuX, 4, ctx.getScaledWindowWidth() - menuW - 4);
        int y = MathHelper.clamp(bindMenuY, 4, ctx.getScaledWindowHeight() - menuH - 4);

        int bg = new Color(18, 18, 24, 190).getRGB();
        RenderUtil.drawRoundedRect(ctx.getMatrices(), x, y, menuW, menuH, 6, bg);

        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), "Bind", x + 6, y + 5, Color.WHITE.getRGB());

        int buttonX = x + 6;
        int buttonY = y + 18;
        int buttonW = menuW - 12;
        int buttonH = 14;
        boolean hovered = RenderUtil.isHovered(mouseX, mouseY, buttonX, buttonY, buttonW, buttonH);

        int themeBase = Manager.STYLE_MANAGER.getFirstColor();
        int btnColor = hovered ? ColorUtil.reAlphaInt(themeBase, 195) : ColorUtil.reAlphaInt(themeBase, 165);
        RenderUtil.drawRoundedRect(ctx.getMatrices(), buttonX, buttonY, buttonW, buttonH, 4, btnColor);

        String keyText;
        if (bindMenuListening) {
            keyText = "...";
        } else {
            int code = bindMenuFunction.getBindCode();
            keyText = code == 0 ? "None" : ClientManager.getKey(code);
        }

        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), keyText, buttonX + 6, buttonY + 3, Color.WHITE.getRGB());
    }
    private Function getHoveredFunction(int mouseX, int mouseY, int startX, int startY) {
        int idx = 0;
        for (Type category : renderCategories) {
            int panelX = startX + idx++ * (PANEL_WIDTH + PANEL_MARGIN);

            if (mouseX < panelX || mouseX > panelX + PANEL_WIDTH || mouseY < startY || mouseY > startY + PANEL_HEIGHT) {
                continue;
            }

            float offset = scrollOffsets.get(category);

            float currentY = startY + SCROLL_AREA_Y_OFFSET - offset;

            for (Function function : Manager.FUNCTION_MANAGER.getFunctions(category)) {
                if (!isFunctionVisible(function)) continue;

                int functionHeight = FUNCTION_HEIGHT;

                int fullSettingsHeight = computeSettingsHeight(function);
                float progress = expandProgress.getOrDefault(function, function.expanded ? 1f : 0f);
                float eased = easeInOut(progress);
                int animatedSettingsHeight = (int) Math.round(fullSettingsHeight * eased);

                int totalHeight = functionHeight + animatedSettingsHeight;

                if (mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH && mouseY >= currentY && mouseY <= currentY + functionHeight && mouseY >= startY + SCROLL_AREA_Y_OFFSET &&
                        mouseY <= startY + SCROLL_AREA_Y_OFFSET + SCROLL_AREA_HEIGHT) {
                    return function;
                }

                if (animatedSettingsHeight > 0) {
                    float settingY = currentY + functionHeight;
                    int remaining = animatedSettingsHeight;

                    for (Setting setting : function.getSettings()) {
                        if (!setting.isVisible()) continue;

                        int settingHeight = getSettingRendererHeight(setting, PANEL_WIDTH - 20);
                        if (settingHeight <= 0) continue;

                        int visible = Math.max(0, Math.min(settingHeight, remaining));
                        if (visible <= 0) break;

                        if (mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH &&
                                mouseY >= settingY && mouseY <= settingY + visible &&
                                mouseY >= startY + SCROLL_AREA_Y_OFFSET &&
                                mouseY <= startY + SCROLL_AREA_Y_OFFSET + SCROLL_AREA_HEIGHT) {
                            if (settingY + settingHeight < startY + SCROLL_AREA_Y_OFFSET) {
                                settingY += settingHeight;
                                remaining -= settingHeight;
                                continue;
                            }
                            return function;
                        }

                        settingY += settingHeight;
                        remaining -= settingHeight;
                        if (remaining <= 0) break;
                    }
                }

                currentY += totalHeight + FUNCTION_GAP;
            }
        }
        return null;
    }

    private void drawDescription(DrawContext ctx, String desc, int startY) {
        int descWidth = (int) FontUtils.durman[19].getWidth(desc);
        int descHeight = 20;

        int descX = (width - descWidth) / 2;
        int descY = startY - descHeight - 10;


        if (Manager.FUNCTION_MANAGER.clickGUI.blur.get() && Manager.FUNCTION_MANAGER.clickGUI.blurSetting.get("Описание")) {
            drawBlur(ctx.getMatrices(), descX - 6, descY - 3.5f, descWidth + 12, descHeight, 12, 8, -1);
        }

        RenderUtil.drawRoundedRect(ctx.getMatrices(), descX - 6, descY - 3.5f, descWidth + 12, descHeight, 6, GUI_COLOR.getRGB());
        FontUtils.durman[19].drawLeftAligned(ctx.getMatrices(), desc, descX, descY, Color.WHITE.getRGB());
    }

    private float updateExpandAnimation(Function f) {
        float target = f.expanded ? 1f : 0f;
        float prog = expandProgress.getOrDefault(f, target);

        prog = MathUtil.lerp(prog, target, 15f);

        if (Math.abs(target - prog) < 0.001f) prog = target;
        expandProgress.put(f, prog);

        return prog;
    }

    private float easeInOut(float t) {
        t = MathHelper.clamp(t, 0f, 1f);
        return t * t * (3f - 2f * t);
    }

    private float updateHoverAnimation(Function f, boolean hovered) {
        float target = hovered ? 1f : 0f;
        float prog = hoverProgress.getOrDefault(f, target);
        prog = MathUtil.lerp(prog, target, 12f);
        if (Math.abs(target - prog) < 0.001f) prog = target;
        hoverProgress.put(f, prog);
        return prog;
    }

    private String trimToWidth(String text, float maxWidth) {
        if (text == null) return "";
        if (FontUtils.sf_medium[16].getWidth(text) <= maxWidth) return text;

        String suffix = "...";
        float suffixW = FontUtils.sf_medium[16].getWidth(suffix);
        if (suffixW >= maxWidth) return suffix;

        int len = text.length();
        while (len > 0) {
            String candidate = text.substring(0, len) + suffix;
            if (FontUtils.sf_medium[16].getWidth(candidate) <= maxWidth) return candidate;
            len--;
        }
        return suffix;
    }

    private void renderPanel(DrawContext ctx, int x, int y, Type category, int mouseX, int mouseY) {
        ru.levin.modules.render.ClickGUI clickGUI = Manager.FUNCTION_MANAGER.clickGUI;
        boolean plain = false;
        boolean liquid = false;
        boolean glass = true;

        if (clickGUI.blur.get() && clickGUI.blurSetting.get("Панели")) {
            float blurRadius = 10f;
            drawBlur(ctx.getMatrices(), x, y, PANEL_WIDTH, PANEL_HEIGHT, 12, blurRadius, -1);
        }

        // базовый цвет панели GUI
        int panelColor = GUI_COLOR.getRGB();
        // если тема почти чёрно-белая, используем тёмно-серый цвет панелей
        int themeBase = Manager.STYLE_MANAGER.getFirstColor();
        Color themeCol = new Color(themeBase, true);
        if (Math.abs(themeCol.getRed() - themeCol.getGreen()) < 15 &&
            Math.abs(themeCol.getGreen() - themeCol.getBlue()) < 15) {
            int a = GUI_COLOR.getAlpha();
            panelColor = new Color(18, 18, 24, a).getRGB();
        }

        if (plain) {
            int shadow1 = new Color(0, 0, 0, 55).getRGB();
            int shadow2 = new Color(0, 0, 0, 28).getRGB();
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x - 2, y - 2, PANEL_WIDTH + 4, PANEL_HEIGHT + 4, 8, shadow2);
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x - 1, y - 1, PANEL_WIDTH + 2, PANEL_HEIGHT + 2, 7, shadow1);
        }

        if (glass) {
            int shadow1 = new Color(0, 0, 0, 40).getRGB();
            int shadow2 = new Color(0, 0, 0, 18).getRGB();
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x - 2, y - 2, PANEL_WIDTH + 4, PANEL_HEIGHT + 4, 8, shadow2);
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x - 1, y - 1, PANEL_WIDTH + 2, PANEL_HEIGHT + 2, 7, shadow1);
        }

        panelColor = ColorUtil.applyAlpha(panelColor, 0.72f);

        RenderUtil.drawRoundedRect(ctx.getMatrices(), x, y, PANEL_WIDTH, PANEL_HEIGHT, 6, panelColor);

        if (plain) {
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, PANEL_WIDTH, PANEL_HEIGHT, 6, 0.9f,
                    ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.08f));
        }

        if (glass) {
            if (liquid) {
                int sheen1 = ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.05f);
                int sheen2 = ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.0f);
                RenderUtil.rectRGB(ctx.getMatrices(), x + 7, y + 4, PANEL_WIDTH - 14, 14, 3,
                        sheen1,
                        sheen1,
                        sheen2,
                        sheen2
                );
            }
        }

        String title = switch (category) {
            case Combat -> "Combat";
            case Move -> "Movement";
            case Render -> "Visuals";
            case Player -> "Player";
            case Misc -> "Miscellaneous";
            default -> category.name();
        };
        FontUtils.sf_bold[20].drawLeftAligned(ctx.getMatrices(), title, x + (PANEL_WIDTH - (int) FontUtils.sf_bold[20].getWidth(title)) / 2, y + TITLE_MARGIN_TOP + 1, Color.WHITE.getRGB());

        {
            int maxBefore = calculateMaxScroll(category);
            float clampedTarget = MathHelper.clamp(scrollTargets.get(category), 0f, (float) maxBefore);
            float clampedOffset = MathHelper.clamp(scrollOffsets.get(category), 0f, (float) maxBefore);
            scrollTargets.put(category, clampedTarget);
            scrollOffsets.put(category, clampedOffset);
        }

        float offset = scrollOffsets.compute(category, (k, v) -> {
            float target = scrollTargets.get(k);
            float current = v;
            float lerped = MathUtil.lerp(current, target, SCROLL_LERP_FACTOR);
            float smoothed = MathUtil.lerp(current, lerped, SCROLL_SMOOTH_FACTOR);

            return smoothed;
        });

        renderScrollbar(ctx, x, y, category, offset);
        ctx.getMatrices().push();
        Scissor.push();
        Scissor.setFromComponentCoordinates(x, y + SCROLL_AREA_Y_OFFSET, PANEL_WIDTH, SCROLL_AREA_HEIGHT);

        float currentY = y + SCROLL_AREA_Y_OFFSET - offset;

        for (Function f : Manager.FUNCTION_MANAGER.getFunctions(category)) {
            if (!isFunctionVisible(f)) continue;

            if (f.expanded && hasPreviewPill(f)) {
                previewFunction = f;
            }

            int functionHeight = FUNCTION_HEIGHT;
            float prog = updateExpandAnimation(f);
            float eased = easeInOut(prog);

            int fullSettingsHeight = computeSettingsHeight(f);
            int animatedSettingsHeight = (int) Math.round(fullSettingsHeight * eased);
            int totalHeight = functionHeight + animatedSettingsHeight;

            if (currentY + totalHeight < y + SCROLL_AREA_Y_OFFSET || currentY > y + PANEL_HEIGHT) {
                currentY += totalHeight + FUNCTION_GAP;
                continue;
            }

            int a = clickGUI.alphaModules.get().intValue();
            boolean hovered = mouseX >= x && mouseX <= x + PANEL_WIDTH && mouseY >= currentY && mouseY <= currentY + functionHeight;
            float hoverAnim = updateHoverAnimation(f, hovered);
            int baseAlpha = Math.min(255, a + Math.round(12f * hoverAnim));
            // используем тот же themeBase, что и для панели выше
            int themeColor = ColorUtil.reAlphaInt(themeBase, baseAlpha);
            int baseBg = f.state
                    ? themeColor
                    : ColorUtil.interpolateColor(new Color(26, 24, 40, baseAlpha).getRGB(), themeColor, 0.45f);
            int bg = ColorUtil.interpolateColor(baseBg, ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.18f), hoverAnim * 0.25f);
            int baseTextColor = f.state ? Color.WHITE.getRGB() : new Color(185, 185, 185).getRGB();
            int textColor = ColorUtil.interpolateColor(baseTextColor, Color.WHITE.getRGB(), hoverAnim * 0.5f);

            // функции делаем более закруглёнными относительно общего параметра rounding
            float moduleRounding = Math.max(2f, clickGUI.rounding.get().floatValue() + 2f);
            if (clickGUI.filling.get()) {
                RenderUtil.drawRoundedRect(ctx.getMatrices(), x + 4, currentY - 1, PANEL_WIDTH - 8, functionHeight, moduleRounding, bg);
            }
            if (clickGUI.strike.get() && f.state) {
                int borderAlpha = Math.min(255, Math.round(baseAlpha * 0.85f));
                int borderColor = ColorUtil.reAlphaInt(themeBase, borderAlpha);
                RenderUtil.drawRoundedBorder(ctx.getMatrices(), x + 4, currentY - 1, PANEL_WIDTH - 8, functionHeight, moduleRounding, 0.25f, borderColor);
            }

            if (plain) {
                int b = hovered ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.10f) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f);
                RenderUtil.drawRoundedBorder(ctx.getMatrices(), x + 4, currentY - 1, PANEL_WIDTH - 8, functionHeight, moduleRounding, 0.8f, b);
            }

            if (glass) {
                int b = hovered ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.14f) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.08f);
                RenderUtil.drawRoundedBorder(ctx.getMatrices(), x + 4, currentY - 1, PANEL_WIDTH - 8, functionHeight, moduleRounding, 0.8f, b);
            }

            boolean hasSettings = !f.getSettings().isEmpty();
            int dotsX = x + PANEL_WIDTH - 15;

            String textToRender;
            textToRender = f.name;

            int textX = x + 9;
            int textY = (int) (currentY + (functionHeight - FontUtils.sf_medium[16].getHeight()) / 2f);
            float maxTextWidth = hasSettings
                    ? (dotsX - 6) - textX
                    : (x + PANEL_WIDTH - 8) - textX;
            textToRender = trimToWidth(textToRender, Math.max(0, maxTextWidth));
            FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), textToRender, textX, textY, textColor);

            if (animatedSettingsHeight > 0) {
                float settingY = currentY + functionHeight;
                ctx.getMatrices().push();
                Scissor.push();
                Scissor.setFromComponentCoordinates(x + 1, (int) settingY, PANEL_WIDTH - 2, animatedSettingsHeight);

                int settingsX = x + 10;
                int contentW = PANEL_WIDTH - 20;

                for (Setting setting : f.getSettings()) {
                    if (!setting.isVisible()) continue;
                    int settingHeight = 0;

                    if (setting instanceof BooleanSetting booleanSetting) {
                        settingHeight = booleanSettingRenderer.getHeight();
                        booleanSettingRenderer.render(ctx, booleanSetting, settingsX, (int) settingY, contentW, settingHeight);
                    } else if (setting instanceof BindBooleanSetting bindBooleanSetting) {
                        settingHeight = bindbooleanSettingRenderer.getHeight();
                        bindbooleanSettingRenderer.render(ctx, bindBooleanSetting, settingsX, (int) settingY, contentW, settingHeight);
                    } else if (setting instanceof BindSetting bindSetting) {
                        settingHeight = bindSettingRenderer.getHeight();
                        bindSettingRenderer.render(ctx, bindSetting, settingsX, (int) settingY - 2, contentW, settingHeight);
                    } else if (setting instanceof ModeSetting modeSetting) {
                        settingHeight = modeSettingRenderer.getHeight(modeSetting, contentW);
                        modeSettingRenderer.render(ctx, modeSetting, settingsX, (int) settingY, contentW, settingHeight);
                    } else if (setting instanceof MultiSetting multiSetting) {
                        settingHeight = multiSettingRenderer.getHeight(multiSetting, contentW);
                        multiSettingRenderer.render(ctx, multiSetting, settingsX, (int) settingY, contentW, settingHeight);
                    } else if (setting instanceof SliderSetting sliderSetting) {
                        settingHeight = sliderSettingRenderer.getHeight();
                        sliderSettingRenderer.render(ctx, sliderSetting, settingsX, (int) settingY - 2, contentW, settingHeight);
                    } else if (setting instanceof TextSetting textSetting) {
                        settingHeight = textSettingRenderer.getHeight();
                        textSettingRenderer.render(ctx, textSetting, settingsX, (int) settingY, contentW, settingHeight);
                    }

                    settingY += settingHeight;
                }

                Scissor.pop();
                ctx.getMatrices().pop();
            }

            if (hasSettings) {
                int dotsY = (int) (currentY + (functionHeight - FontUtils.sf_medium[16].getHeight()) / 2f);
                FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), "...", dotsX, dotsY, new Color(170, 170, 170).getRGB());
            }

            currentY += totalHeight + FUNCTION_GAP;
        }

        clampScrollForCategory(category);
        Scissor.pop();
        ctx.getMatrices().pop();
    }

    private void renderScrollbar(DrawContext ctx, int x, int y, Type category, float offset) {
        int maxScroll = calculateMaxScroll(category);
        if (maxScroll <= 0) return;

        int scrollbarWidth = 3;
        int scrollbarX = x + PANEL_WIDTH - scrollbarWidth - 1;

        int scrollbarHeight = SCROLL_AREA_HEIGHT - 30;
        int scrollbarY = y + SCROLL_AREA_Y_OFFSET + 15;

        int scrollbarBgColor = new Color(0, 0, 0, 50).getRGB();
        RenderUtil.drawRoundedRect(ctx.getMatrices(), scrollbarX, scrollbarY, scrollbarWidth, scrollbarHeight, 1, scrollbarBgColor);

        float scrollProgress = offset / maxScroll;
        int thumbHeight = Math.max(6, (int) (scrollbarHeight * (SCROLL_AREA_HEIGHT / (float) (SCROLL_AREA_HEIGHT + maxScroll))));
        int thumbY = scrollbarY + (int) (scrollProgress * (scrollbarHeight - thumbHeight));

        int thumbColor = new Color(255, 255, 255, 150).getRGB();
        RenderUtil.drawRoundedRect(ctx.getMatrices(), scrollbarX, thumbY, scrollbarWidth, thumbHeight, 1, thumbColor);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int panelStartY = (height - PANEL_HEIGHT) / 2;

        // theme strip scroll
        if (themeMenuTarget > 0.01f) {
            int themeWidth = getThemeWidth();
            int themeX = getThemeX(themeWidth);
            int themeY = getThemeY();
            if (mouseX >= themeX && mouseX <= themeX + themeWidth && mouseY >= themeY && mouseY <= themeY + THEME_HEIGHT) {
                themeScrollTarget = Math.max(0f, themeScrollTarget - (float) scrollY * THEME_SCROLL_SPEED);
                return true;
            }
        }

        int totalWidth = renderCategories.size() * (PANEL_WIDTH + PANEL_MARGIN) - PANEL_MARGIN;
        int startX = (width - totalWidth) / 2;
        int startY = (height - PANEL_HEIGHT) / 2;

        int i = 0;
        for (Type category : renderCategories) {
            int px = startX + i++ * (PANEL_WIDTH + PANEL_MARGIN);
            if (mouseX >= px && mouseX <= px + PANEL_WIDTH && mouseY >= startY + SCROLL_AREA_Y_OFFSET && mouseY <= startY + SCROLL_AREA_Y_OFFSET + SCROLL_AREA_HEIGHT) {
                int maxScroll = calculateMaxScroll(category);
                if (maxScroll > 0) {
                    scrollTargets.compute(category, (k, v) -> Math.max(0, Math.min(v - (float) scrollY * SCROLL_SPEED, maxScroll)));
                    return true;
                }
                return false;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private int calculateMaxScroll(Type category) {
        int totalHeight = 0;
        for (Function f : Manager.FUNCTION_MANAGER.getFunctions(category)) {
            if (!isFunctionVisible(f)) continue;
            int functionHeight = FUNCTION_HEIGHT;

            int fullSettingsHeight = computeSettingsHeight(f);
            float prog = expandProgress.getOrDefault(f, f.expanded ? 1f : 0f);
            float eased = easeInOut(prog);
            int animated = (int) Math.round(fullSettingsHeight * eased);

            totalHeight += functionHeight + animated + FUNCTION_GAP;
        }
        int maxScroll = totalHeight - SCROLL_AREA_HEIGHT;
        return Math.max(0, maxScroll);
    }

    @Override
    public boolean charTyped(char c, int keyCode) {
        if (searchState.focused) {
            String prevText = searchState.text;
            if (searchState.text.length() < 30) {
                String before = searchState.text.substring(0, searchState.cursorPosition);
                String after = searchState.text.substring(searchState.cursorPosition);
                searchState.text = before + c + after;
                searchState.cursorPosition++;
            }
            if (!prevText.equals(searchState.text)) {
                resetScrollForAllCategories();
            }
            return true;
        }

        for (Type category : renderCategories) {
            for (Function function : Manager.FUNCTION_MANAGER.getFunctions(category)) {
                if (!function.expanded) continue;
                for (Setting setting : function.getSettings()) {
                    if (setting instanceof TextSetting textSetting && textSetting.isFocused()) {
                        if (textSettingRenderer.charTyped(textSetting, c, keyCode)) {
                            return true;
                        }
                    }
                }
            }
        }

        return super.charTyped(c, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isClose) {
            return true;
        }

        if (bindMenuListening && bindMenuFunction != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                bindMenuFunction.setBindCode(0);
            } else {
                bindMenuFunction.setBindCode(keyCode);
            }
            bindMenuListening = false;
            bindMenuOpen = false;
            bindMenuFunction = null;
            return true;
        }

        if (bindMenuOpen && keyCode == GLFW.GLFW_KEY_ESCAPE) {
            bindMenuListening = false;
            bindMenuOpen = false;
            bindMenuFunction = null;
            return true;
        }

        for (Type category : renderCategories) {
            for (Function function : Manager.FUNCTION_MANAGER.getFunctions(category)) {
                if (!isFunctionVisible(function)) continue;
                if (function.expanded) {
                    for (Setting setting : function.getSettings()) {
                        if (!setting.isVisible()) continue;
                        if (setting instanceof BindBooleanSetting bindBooleanSetting) {
                            if (bindBooleanSetting.isListeningForBind()) {
                                if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                                    bindBooleanSetting.setKey(0);
                                } else {
                                    bindBooleanSetting.setKey(keyCode);
                                }
                                bindBooleanSetting.setListeningForBind(false);
                                return true;
                            }
                        }
                        if (setting instanceof BindSetting bindSetting) {
                            if (bindSetting.isBinding()) {
                                if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                                    bindSetting.setKey(-1);
                                } else {
                                    bindSetting.setKey(keyCode);
                                }
                                bindSetting.setBinding(false);
                                return true;
                            }
                        }
                        if (setting instanceof TextSetting textSetting) {
                            if (textSetting.isFocused()) {
                                if (textSettingRenderer.keyPressed(textSetting, keyCode, scanCode, modifiers)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (searchState.focused) {
            String prevText = searchState.text;
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE -> {
                    if (searchState.cursorPosition > 0 && !searchState.text.isEmpty()) {
                        searchState.text = searchState.text.substring(0, searchState.cursorPosition - 1) + searchState.text.substring(searchState.cursorPosition);
                        searchState.cursorPosition--;
                    }
                    if (!prevText.equals(searchState.text)) {
                        resetScrollForAllCategories();
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_DELETE -> {
                    if (searchState.cursorPosition < searchState.text.length()) {
                        searchState.text = searchState.text.substring(0, searchState.cursorPosition) + searchState.text.substring(searchState.cursorPosition + 1);
                    }
                    if (!prevText.equals(searchState.text)) {
                        resetScrollForAllCategories();
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_LEFT -> {
                    if (searchState.cursorPosition > 0) searchState.cursorPosition--;
                    return true;
                }
                case GLFW.GLFW_KEY_RIGHT -> {
                    if (searchState.cursorPosition < searchState.text.length()) searchState.cursorPosition++;
                    return true;
                }
                case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_ESCAPE -> {
                    searchState.focused = false;
                    return true;
                }
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void resetScrollForAllCategories() {
        for (Type category : renderCategories) {
            scrollTargets.put(category, 0f);
            scrollOffsets.put(category, 0f);
        }
    }

    @Override
    public void tick() {
        super.tick();
        long currentTime = System.currentTimeMillis();
        if (currentTime - searchState.lastCursorBlink >= 500) {
            searchState.cursorVisible = !searchState.cursorVisible;
            searchState.lastCursorBlink = currentTime;
        }
    }

    @Override
    public void close() {
        if (isClose) {
            return;
        }
        draggingPicker1 = false;
        draggingPicker2 = false;
        isClose = true;
        animationOpen.setDirection(Direction.AxisDirection.NEGATIVE);
        animationOpen.reset();
    }

    private int getSearchWidth() {
        return SEARCH_MAX_WIDTH;
    }

    private int getSearchX(int searchWidth) {
        return (width - searchWidth) / 2;
    }

    private int getSearchY() {
        return (height + PANEL_HEIGHT) / 2 + SEARCH_MARGIN_BOTTOM;
    }

    private void renderSearchField(DrawContext ctx) {
        int searchWidth = getSearchWidth();
        int searchX = getSearchX(searchWidth);
        int searchY = getSearchY();

        int bgColor = new Color(22, 20, 38, 190).getRGB();
        // если тема почти чёрно-белая, подгоняем фон под общий тёмно-серый стиль GUI
        int themeBase = Manager.STYLE_MANAGER.getFirstColor();
        Color themeCol = new Color(themeBase, true);
        if (Math.abs(themeCol.getRed() - themeCol.getGreen()) < 15 &&
            Math.abs(themeCol.getGreen() - themeCol.getBlue()) < 15) {
            bgColor = new Color(18, 18, 24, 210).getRGB();
        }
        ru.levin.modules.render.ClickGUI clickGUI = Manager.FUNCTION_MANAGER.clickGUI;
        if (clickGUI.blur.get() && clickGUI.blurSetting.get("Поиск")) {
            drawBlur(ctx.getMatrices(), searchX, searchY, searchWidth, SEARCH_HEIGHT, 6, 12, -1);
        }
        RenderUtil.drawRoundedRect(ctx.getMatrices(), searchX, searchY, searchWidth, SEARCH_HEIGHT, 6, bgColor);

        String displayText;
        int textColor;
        int textX;

        if (searchState.text.isEmpty() && !searchState.focused) {
            displayText = "Поиск...";
            textColor = new Color(255, 255, 255, 120).getRGB();
            int textWidth = (int) FontUtils.sf_medium[18].getWidth(displayText);
            textX = searchX + (searchWidth - textWidth) / 2;
        } else {
            String text = searchState.text;
            if (searchState.focused && searchState.cursorVisible) {
                int pos = Math.min(searchState.cursorPosition, text.length());
                text = text.substring(0, pos) + "|" + text.substring(pos);
            }
            displayText = text;
            textColor = Color.WHITE.getRGB();
            textX = searchX + 6;
        }
        int textY = (int) (searchY + (SEARCH_HEIGHT - FontUtils.sf_medium[18].getHeight()) / 2);
        FontUtils.sf_medium[18].drawLeftAligned(ctx.getMatrices(), displayText, textX, textY, textColor);
    }

    private int getThemeWidth() {
        return THEME_MAX_WIDTH;
    }

    private int getThemeX(int searchWidth) {
        return 8;
    }

    private int getThemeY() {
        return height - THEME_HEIGHT - 8;
    }
    private void renderButtomTheme(DrawContext ctx, double mouseX, double mouseY) {
        int themeWidth = getThemeWidth();
        int themeX = getThemeX(themeWidth);
        int themeY = getThemeY();

        int buttonX = themeX + themeWidth + 6;
        int buttonY = themeY;
        int buttonWidth = 16;
        int buttonHeight = 16;
        boolean hovered = mouseX >= buttonX && mouseX <= buttonX + buttonWidth && mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
        int base = hovered ? GUI_COLOR.brighter().getRGB() : GUI_COLOR.getRGB();
        int color = ColorUtil.applyAlpha(base, 0.85f);

        RenderUtil.drawRoundedRect(ctx.getMatrices(), buttonX, buttonY, buttonWidth, buttonHeight, 2, color);
        int c1 = Manager.STYLE_MANAGER.getFirstColor();
        int c2 = Manager.STYLE_MANAGER.getSecondColor();
        RenderUtil.rectRGB(ctx.getMatrices(), buttonX + 3, buttonY + 3, 10, 10, 2,
                ColorUtil.gradient(3, 0, c1, c2),
                ColorUtil.gradient(3, 90, c1, c2),
                ColorUtil.gradient(3, 180, c1, c2),
                ColorUtil.gradient(3, 270, c1, c2)
        );
    }

    private void renderTheme(DrawContext ctx, int mouseX, int mouseY) {
        float targetAlpha = themeMenuTarget > 0.01f ? 1f : 0f;
        themeAlphaAnim += (targetAlpha - themeAlphaAnim) * 0.15f;
        if (themeAlphaAnim < 0.01f) return;

        themeMenuAnim += (themeMenuTarget - themeMenuAnim) * THEME_ANIM_SPEED;
        if (themeMenuAnim < 0.01f) return;

        int themeWidth = getThemeWidth();
        int themeX = getThemeX(themeWidth);
        int themeY = getThemeY();
        float offsetY = (1f - themeMenuAnim) * 10f;
        themeScrollOffset = MathUtil.lerp(themeScrollOffset, themeScrollTarget, THEME_SCROLL_LERP_FACTOR);

        int panelColor = ColorUtil.applyAlpha(GUI_COLOR.getRGB(), themeAlphaAnim);

        if (Manager.FUNCTION_MANAGER.clickGUI.blur.get() && Manager.FUNCTION_MANAGER.clickGUI.blurSetting.get("Темы")) {
            drawBlur(ctx.getMatrices(), themeX, themeY + offsetY, themeWidth, THEME_HEIGHT, 3, 12, -1);
        }
        RenderUtil.drawRoundedRect(ctx.getMatrices(), themeX, themeY + offsetY, themeWidth, THEME_HEIGHT, 3, panelColor);

        int circleSize = THEME_HEIGHT - 5;
        int padding = 5;
        int totalThemes = Manager.STYLE_MANAGER.getStyles().size();

        float maxScroll = Math.max(0, (totalThemes - VISIBLE_THEMES) * (circleSize + padding));
        themeScrollTarget = MathHelper.clamp(themeScrollTarget, 0, maxScroll);
        themeScrollOffset = MathHelper.clamp(themeScrollOffset, 0, maxScroll);

        // intentionally no arrow glyphs here (keeps UI minimal and avoids recognizable patterns)

        ctx.getMatrices().push();
        Scissor.push();
        Scissor.setFromComponentCoordinates(themeX + 1, themeY + offsetY, themeWidth - 2, THEME_HEIGHT);

        float startX = themeX + padding - themeScrollOffset;
        int centerY = (int) (themeY + (THEME_HEIGHT - circleSize) / 2 + 0.9f + offsetY);
        String hoveredTheme = null;
        int x;
        int y;

        for (Style style : Manager.STYLE_MANAGER.getStyles()) {
            int[] colors = style.colors;
            int c1 = colors[0];
            int c2 = colors.length > 1 ? colors[1] : colors[0];
            final Vector4i vec = new Vector4i(
                    ColorUtil.gradient(5, 0, c1, c2),
                    ColorUtil.gradient(5, 180, c1, c2),
                    ColorUtil.gradient(5, 90, c1, c2),
                    ColorUtil.gradient(5, 360, c1, c2)
            );
            x = (int) startX;
            y = centerY;

            RenderUtil.rectRGB(ctx.getMatrices(), x, y + 0.5f, circleSize, circleSize, 5,
                    ColorUtil.applyAlpha(vec.w, themeAlphaAnim),
                    ColorUtil.applyAlpha(vec.x, themeAlphaAnim),
                    ColorUtil.applyAlpha(vec.y, themeAlphaAnim),
                    ColorUtil.applyAlpha(vec.z, themeAlphaAnim)
            );

            if (RenderUtil.isHovered(mouseX, mouseY, x, y, circleSize, circleSize)) {
                hoveredTheme = style.name;
            }
            startX += circleSize + padding;
        }

        Scissor.pop();
        ctx.getMatrices().pop();

        if (hoveredTheme != null) {
            themeNameAnim += (1f - themeNameAnim) * 0.2f;
        } else {
            themeNameAnim += (0f - themeNameAnim) * 0.2f;
        }

        if (themeNameAnim > 0.01f && hoveredTheme != null) {
            int screenWidth = ctx.getScaledWindowWidth();
            int textWidth = (int) FontUtils.sf_medium[18].getWidth(hoveredTheme);
            int textX = (screenWidth - textWidth) / 2;

            int textColor = ColorUtil.applyAlpha(Color.WHITE.getRGB(), themeNameAnim * themeAlphaAnim);
            FontUtils.sf_medium[18].drawLeftAligned(ctx.getMatrices(), hoveredTheme, textX, centerY + 18, textColor);

            if (hoveredTheme.toLowerCase().contains("custom")) {
                int tipColor = ColorUtil.applyAlpha(Color.WHITE.getRGB(), themeNameAnim * themeAlphaAnim * 0.7f);
                String tipText = "ПКМ — удалить";
                int tipWidth = (int) FontUtils.sf_medium[14].getWidth(tipText);
                int tipX = (screenWidth - tipWidth) / 2;
                FontUtils.sf_medium[14].drawLeftAligned(ctx.getMatrices(), tipText, tipX, centerY + 32, tipColor);
            }
        }
    }

    private void renderColorPickers(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        int panelWidth = 85;
        int panelHeight = 51;
        float animOffsetX = (1f - colorPickerAnim) * 30f;
        float animScale = 0.95f + 0.05f * colorPickerAnim;
        float alphaMult = colorPickerAnim;

        int panelX = (int) (x - panelWidth - 20 + animOffsetX);
        int panelY = y - panelHeight / 2 - 12;
        panelX = MathHelper.clamp(panelX, 4, ctx.getScaledWindowWidth() - panelWidth - 4);
        panelY = MathHelper.clamp(panelY, 4, ctx.getScaledWindowHeight() - panelHeight - 4);

        ctx.getMatrices().push();
        ctx.getMatrices().translate(panelX + panelWidth / 2f, panelY + panelHeight / 2f, 0);
        ctx.getMatrices().scale(animScale, animScale, 1f);
        ctx.getMatrices().translate(-panelWidth / 2f, -panelHeight / 2f, 0);

        int baseColor = GUI_COLOR.getRGB();
        if (Manager.FUNCTION_MANAGER.clickGUI.blur.get() && Manager.FUNCTION_MANAGER.clickGUI.blurSetting.get("Создание темы")) {
            RenderUtil.drawBlur(ctx.getMatrices(), 0, 0, panelWidth, panelHeight, 4,12,-1);
        }
        RenderUtil.drawRoundedRect(ctx.getMatrices(), 0, 0, panelWidth, panelHeight, 4, ColorUtil.applyAlpha(baseColor, alphaMult));

        int picker1Size = 30;
        int picker1X = 5;
        int picker1Y = 5;
        RenderUtil.drawRoundedRect(ctx.getMatrices(), picker1X, picker1Y, picker1Size, picker1Size, 6, ColorUtil.applyAlpha(new Color(0, 0, 0, 90).getRGB(), alphaMult));
        RenderUtil.drawRoundedBorder(ctx.getMatrices(), picker1X, picker1Y, picker1Size, picker1Size, 14, 0.1f, ColorUtil.applyAlpha(Color.WHITE.getRGB(), alphaMult));

        int dotX1 = (int) (picker1X + picker1CursorX * picker1Size);
        int dotY1 = (int) (picker1Y + picker1CursorY * picker1Size);
        RenderUtil.drawCircle(ctx.getMatrices(), dotX1, dotY1, 4f, ColorUtil.applyAlpha(Color.BLACK.getRGB(), alphaMult));

        int picker2Size = 30;
        int picker2X = 50;
        int picker2Y = 5;
        RenderUtil.drawRoundedRect(ctx.getMatrices(), picker2X, picker2Y, picker2Size, picker2Size, 6, ColorUtil.applyAlpha(new Color(0, 0, 0, 90).getRGB(), alphaMult));
        RenderUtil.drawRoundedBorder(ctx.getMatrices(), picker2X, picker2Y, picker2Size, picker2Size, 14, 0.1f, ColorUtil.applyAlpha(Color.WHITE.getRGB(), alphaMult));

        int dotX2 = (int) (picker2X + picker2CursorX * picker2Size);
        int dotY2 = (int) (picker2Y + picker2CursorY * picker2Size);
        RenderUtil.drawCircle(ctx.getMatrices(), dotX2, dotY2, 4f, ColorUtil.applyAlpha(Color.BLACK.getRGB(), alphaMult));

        int closeButtonSize = 10;
        int closeButtonX = panelWidth - closeButtonSize;
        int closeButtonY = 0;
        RenderUtil.drawRoundedRect(ctx.getMatrices(), closeButtonX, closeButtonY, closeButtonSize, closeButtonSize, new Vector4f(0, 4, 0, 4), ColorUtil.applyAlpha(Color.WHITE.getRGB(), alphaMult));
        FontUtils.sf_medium[20].drawLeftAligned(ctx.getMatrices(), "×", closeButtonX + 2, closeButtonY - 1.5f, ColorUtil.applyAlpha(Color.RED.getRGB(), alphaMult));

        RenderUtil.drawRoundedRect(ctx.getMatrices(), 14, 39, 56, 8, new Vector4f(1, 1, 1, 1), ColorUtil.applyAlpha(Color.WHITE.getRGB(), alphaMult));
        FontUtils.durman[12].drawLeftAligned(ctx.getMatrices(), "Добавить тему", 18, 39, ColorUtil.applyAlpha(Color.BLACK.getRGB(), alphaMult));

        ctx.getMatrices().pop();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingSlider != null) {
            // делегируем расчет значения слайдера в SliderSettingRenderer,
            // он использует barX/barWidth и сам считает percent без рандома
            sliderSettingRenderer.mouseDragged(draggingSlider, mouseX, draggingSliderX, draggingSliderWidth);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingPicker1 = false;
        draggingPicker2 = false;

        if (draggingSlider != null) {
            // останавливаем драганье через рендерер и сбрасываем текущее активное колесико
            sliderSettingRenderer.mouseReleased(draggingSlider);
            draggingSlider = null;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int searchWidth = getSearchWidth();
        int searchX = getSearchX(searchWidth);
        int searchY = getSearchY();

        int totalWidth = renderCategories.size() * (PANEL_WIDTH + PANEL_MARGIN) - PANEL_MARGIN;
        int startX = (width - totalWidth) / 2;
        int startY = (height - PANEL_HEIGHT) / 2;

        {
            int iconSize = getSettingsIconSize();
            int iconX = startX + totalWidth - iconSize;
            int iconY = startY - iconSize - 6;
            if (RenderUtil.isHovered((int) mouseX, (int) mouseY, iconX, iconY, iconSize, iconSize)) {
                mc.setScreen(new ClientSettingsScreen(this));
                return true;
            }
        }

        // theme button click (open/close theme strip)
        {
            int themeWidth = getThemeWidth();
            int themeX = getThemeX(themeWidth);
            int themeY = getThemeY();
            int buttonX = themeX + themeWidth + 6;
            int buttonY = themeY;
            int buttonWidth = 16;
            int buttonHeight = 16;
            boolean hovered = mouseX >= buttonX && mouseX <= buttonX + buttonWidth && mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
            if (hovered && button == 0) {
                boolean next = !(themeMenuTarget > 0.01f);
                themeMenu = next;
                themeMenuTarget = next ? 1f : 0f;
                if (!next) colorPickerOpen = false;
                return true;
            }
        }

        // theme strip click (select style / open picker)
        if (themeMenuTarget > 0.01f) {
            int themeWidth = getThemeWidth();
            int themeX = getThemeX(themeWidth);
            int themeY = getThemeY();
            if (mouseX >= themeX && mouseX <= themeX + themeWidth && mouseY >= themeY && mouseY <= themeY + THEME_HEIGHT) {
                int circleSize = THEME_HEIGHT - 5;
                int padding = 5;
                float start = themeX + padding - themeScrollOffset;
                int y = themeY + (THEME_HEIGHT - circleSize) / 2;
                int x;

                for (Style style : Manager.STYLE_MANAGER.getStyles()) {
                    x = (int) start;
                    if (RenderUtil.isHovered((int) mouseX, (int) mouseY, x, y, circleSize, circleSize)) {
                        if (button == 0) {
                            Manager.STYLE_MANAGER.setTheme(style);
                            colorPickerOpen = false;
                            return true;
                        }
                        if (button == 1) {
                            Manager.STYLE_MANAGER.removeStyle(style);
                            colorPickerOpen = false;
                            return true;
                        }
                    }
                    start += circleSize + padding;
                }
                return true;
            } else {
                // click outside theme strip closes it
                themeMenu = false;
                themeMenuTarget = 0f;
                colorPickerOpen = false;
            }
        }

        if (mouseX >= searchX && mouseX <= searchX + searchWidth && mouseY >= searchY && mouseY <= searchY + SEARCH_HEIGHT) {
            searchState.focused = true;
            searchState.cursorPosition = searchState.text.length();
            return true;
        } else {
            searchState.focused = false;
        }

        // bind menu
        if (bindMenuOpen && bindMenuFunction != null) {
            int menuW = 92;
            int menuH = 38;
            int x = MathHelper.clamp(bindMenuX, 4, width - menuW - 4);
            int y = MathHelper.clamp(bindMenuY, 4, height - menuH - 4);

            int buttonX = x + 6;
            int buttonY = y + 18;
            int buttonW = menuW - 12;
            int buttonH = 14;

            boolean overMenu = RenderUtil.isHovered((int) mouseX, (int) mouseY, x, y, menuW, menuH);
            boolean overButton = RenderUtil.isHovered((int) mouseX, (int) mouseY, buttonX, buttonY, buttonW, buttonH);

            if (bindMenuListening) {
                // Click outside cancels, left-click does not bind (so clicking the bind button doesn't accidentally set MOUSE1)
                if (!overMenu) {
                    bindMenuListening = false;
                    bindMenuOpen = false;
                    bindMenuFunction = null;
                    return true;
                }

                if (button != 0) {
                    int code = -button - 2;
                    bindMenuFunction.setBindCode(code);
                    bindMenuListening = false;
                    bindMenuOpen = false;
                    bindMenuFunction = null;
                    return true;
                }

                return true;
            }

            if (overButton && button == 0) {
                bindMenuListening = true;
                return true;
            }

            if (overMenu) {
                return true;
            }

            bindMenuListening = false;
            bindMenuOpen = false;
            bindMenuFunction = null;
        }

        // bind capture for settings
        for (Type cat : renderCategories) {
            for (Function fn : Manager.FUNCTION_MANAGER.getFunctions(cat)) {
                if (!fn.expanded) continue;
                for (Setting set : fn.getSettings()) {
                    if (!set.isVisible()) continue;
                    if (set instanceof BindBooleanSetting bbs && bbs.isListeningForBind()) {
                        int code = -button - 2;
                        bbs.setKey(code);
                        bbs.setListeningForBind(false);
                        return true;
                    }
                    if (set instanceof BindSetting bs && bs.isBinding()) {
                        int code = -button - 2;
                        bs.setKey(code);
                        bs.setBinding(false);
                        return true;
                    }
                }
            }
        }

        int idx = 0;
        for (Type category : renderCategories) {
            int panelX = startX + idx++ * (PANEL_WIDTH + PANEL_MARGIN);
            float offset = scrollOffsets.get(category);
            float currentY = startY + SCROLL_AREA_Y_OFFSET - offset;

            for (Function function : Manager.FUNCTION_MANAGER.getFunctions(category)) {
                if (!isFunctionVisible(function)) continue;

                int functionHeight = FUNCTION_HEIGHT;
                int fullSettingsHeight = computeSettingsHeight(function);
                float prog = expandProgress.getOrDefault(function, function.expanded ? 1f : 0f);
                float eased = easeInOut(prog);
                int animatedSettingsHeight = (int) Math.round(fullSettingsHeight * eased);
                int totalHeight = functionHeight + animatedSettingsHeight;

                if (currentY + totalHeight < startY + SCROLL_AREA_Y_OFFSET) {
                    currentY += totalHeight + FUNCTION_GAP;
                    continue;
                }
                if (currentY > startY + SCROLL_AREA_Y_OFFSET + SCROLL_AREA_HEIGHT) {
                    break;
                }

                if (mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH && mouseY >= currentY && mouseY <= currentY + functionHeight && mouseY >= startY + SCROLL_AREA_Y_OFFSET && mouseY <= startY + SCROLL_AREA_Y_OFFSET + SCROLL_AREA_HEIGHT) {
                    if (button == 0) {
                        function.toggle();
                        return true;
                    } else if (button == 1) {
                        function.expanded = !function.expanded;
                        clampScrollForCategory(category);
                        return true;
                    } else if (button == 2) {
                        bindMenuOpen = true;
                        bindMenuListening = false;
                        bindMenuFunction = function;
                        bindMenuX = (int) mouseX + 6;
                        bindMenuY = (int) mouseY + 6;
                        return true;
                    }
                }

                if (animatedSettingsHeight > 0) {
                    float settingY = currentY + functionHeight;
                    int remaining = animatedSettingsHeight;

                    for (Setting setting : function.getSettings()) {
                        if (!setting.isVisible()) continue;

                        int settingHeight = getSettingRendererHeight(setting, PANEL_WIDTH - 20);
                        if (settingHeight <= 0) continue;

                        int visible = Math.max(0, Math.min(settingHeight, remaining));
                        if (visible <= 0) break;

                        if (mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH && mouseY >= settingY && mouseY <= settingY + visible && mouseY >= startY + SCROLL_AREA_Y_OFFSET && mouseY <= startY + SCROLL_AREA_Y_OFFSET + SCROLL_AREA_HEIGHT) {
                            int settingX = panelX + 10;
                            int settingWidth = PANEL_WIDTH - 20;

                            if (setting instanceof BooleanSetting booleanSetting) {
                                if (booleanSettingRenderer.mouseClicked(booleanSetting, mouseX, mouseY, button, settingX, (int) settingY, settingWidth, visible)) {
                                    return true;
                                }
                            } else if (setting instanceof BindBooleanSetting bindBooleanSetting) {
                                if (bindbooleanSettingRenderer.mouseClicked(bindBooleanSetting, mouseX, mouseY, button, settingX, (int) settingY, settingWidth, visible)) {
                                    return true;
                                }
                            } else if (setting instanceof BindSetting bindSetting) {
                                if (bindSettingRenderer.mouseClicked(bindSetting, mouseX, mouseY, button, settingX, (int) settingY - 2, settingWidth, visible)) {
                                    return true;
                                }
                            } else if (setting instanceof ModeSetting modeSetting) {
                                if (modeSettingRenderer.mouseClicked(modeSetting, mouseX, mouseY, button, settingX, (int) settingY, settingWidth, visible)) {
                                    return true;
                                }
                            } else if (setting instanceof MultiSetting multiSetting) {
                                if (multiSettingRenderer.mouseClicked(multiSetting, mouseX, mouseY, button, settingX, (int) settingY, settingWidth, visible)) {
                                    return true;
                                }
                            } else if (setting instanceof SliderSetting sliderSetting) {
                                if (sliderSettingRenderer.mouseClicked(sliderSetting, mouseX, mouseY, button, settingX, (int) settingY, settingWidth, visible)) {
                                    draggingSlider = sliderSetting;
                                    draggingSliderX = settingX;
                                    draggingSliderWidth = settingWidth;
                                    return true;
                                }
                            } else if (setting instanceof TextSetting textSetting) {
                                if (textSettingRenderer.mouseClicked(textSetting, mouseX, mouseY, button, settingX, (int) settingY, settingWidth, visible)) {
                                    return true;
                                }
                            }
                        }

                        settingY += settingHeight;
                        remaining -= settingHeight;
                        if (remaining <= 0) break;
                    }
                }

                currentY += totalHeight + FUNCTION_GAP;
            }
        }

        // remove focus from all text settings if clicked outside
        for (Type cat : renderCategories) {
            for (Function fn : Manager.FUNCTION_MANAGER.getFunctions(cat)) {
                if (!fn.expanded) continue;
                for (Setting set : fn.getSettings()) {
                    if (set instanceof TextSetting ts) {
                        ts.setFocused(false);
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int getCfgManagerWidth() {
        return 220;
    }

    private int getCfgManagerHeight() {
        return 20;
    }

    private int getCfgManagerY(int startY) {
        return Math.max(6, startY - 52);
    }

    private int getCfgManagerRowHeight() {
        return 16;
    }

    private int getCfgManagerVisible() {
        return 7;
    }

    private int getCfgManagerListHeight() {
        return getCfgManagerVisible() * getCfgManagerRowHeight() + 6;
    }

    private void renderCfgManager(DrawContext ctx, int mouseX, int mouseY, int startY) {
        cfgScrollOffset = MathUtil.lerp(cfgScrollOffset, cfgScrollTarget, 12f);

        int boxW = getCfgManagerWidth();
        int boxH = getCfgManagerHeight();
        int boxX = (width - boxW) / 2;
        int boxY = getCfgManagerY(startY);

        int a = GUI_COLOR.getAlpha();
        int accent = Manager.STYLE_MANAGER.getFirstColor();
        int panelColor = ColorUtil.reAlphaInt(accent, a);
        RenderUtil.drawRoundedRect(ctx.getMatrices(), boxX, boxY, boxW, boxH, 6, panelColor);

        String current = Manager.CONFIG_MANAGER.getCurrentConfig();
        String title = "CFG Manager";
        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), title, boxX + 8, boxY + 5, Color.WHITE.getRGB());

        String rightText;
        if (cfgNameFocused) {
            int pos = Math.min(cfgNameCursor, cfgNameText.length());
            String text = cfgNameText;
            rightText = text.substring(0, pos) + "|" + text.substring(pos);
        } else {
            rightText = current;
        }

        float rightW = FontUtils.sf_medium[16].getWidth(rightText);
        float rightX = boxX + boxW - 8 - rightW;
        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), rightText, rightX, boxY + 5, new Color(220, 220, 220).getRGB());

        int btnSize = 14;
        int btnY = boxY + (boxH - btnSize) / 2;
        int plusX = boxX + boxW + 6;
        int minusX = plusX + btnSize + 4;

        RenderUtil.drawRoundedRect(ctx.getMatrices(), plusX, btnY, btnSize, btnSize, 4, panelColor);
        RenderUtil.drawRoundedRect(ctx.getMatrices(), minusX, btnY, btnSize, btnSize, 4, panelColor);
        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), "+", plusX + 4, btnY + 2, Color.WHITE.getRGB());
        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), "-", minusX + 5, btnY + 2, Color.WHITE.getRGB());

        if (!cfgMenuOpen) return;

        int listY = boxY + boxH + 4;
        int listW = boxW;
        int listH = getCfgManagerListHeight();
        RenderUtil.drawRoundedRect(ctx.getMatrices(), boxX, listY, listW, listH, 6, panelColor);

        java.util.List<String> cfgs = Manager.CONFIG_MANAGER.getAllConfigurations();
        cfgs.sort(String.CASE_INSENSITIVE_ORDER);

        ctx.getMatrices().push();
        Scissor.push();
        Scissor.setFromComponentCoordinates(boxX + 1, listY + 1, listW - 2, listH - 2);

        int rowH = getCfgManagerRowHeight();
        float y = listY + 4 - cfgScrollOffset;
        int idx = 0;
        for (String cfg : cfgs) {
            if (idx++ == 0) {
            }

            int textColor = cfg.equalsIgnoreCase(current) ? ColorUtil.reAlphaInt(accent, 255) : new Color(200, 200, 200).getRGB();
            if (RenderUtil.isHovered(mouseX, mouseY, boxX, y, listW, rowH)) {
                textColor = Color.WHITE.getRGB();
            }

            FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), cfg, boxX + 8, y + 3, textColor);
            y += rowH;
        }

        Scissor.pop();
        ctx.getMatrices().pop();
    }

    private boolean handleCfgManagerClick(double mouseX, double mouseY, int button, int startY) {
        int boxW = getCfgManagerWidth();
        int boxH = getCfgManagerHeight();
        int boxX = (width - boxW) / 2;
        int boxY = getCfgManagerY(startY);

        int btnSize = 14;
        int btnY = boxY + (boxH - btnSize) / 2;
        int plusX = boxX + boxW + 6;
        int minusX = plusX + btnSize + 4;

        if (RenderUtil.isHovered((int) mouseX, (int) mouseY, plusX, btnY, btnSize, btnSize) && button == 0) {
            cfgNameFocused = true;
            cfgNameText = "";
            cfgNameCursor = 0;
            cfgMenuOpen = false;
            searchState.focused = false;
            return true;
        }

        if (RenderUtil.isHovered((int) mouseX, (int) mouseY, minusX, btnY, btnSize, btnSize) && button == 0) {
            String cur = Manager.CONFIG_MANAGER.getCurrentConfig();
            if (!cur.equalsIgnoreCase("AUTOCFG")) {
                Manager.CONFIG_MANAGER.deleteConfig(cur);
            }
            cfgMenuOpen = false;
            cfgNameFocused = false;
            return true;
        }

        if (RenderUtil.isHovered((int) mouseX, (int) mouseY, boxX, boxY, boxW, boxH) && button == 0) {
            if (!cfgNameFocused) {
                cfgMenuOpen = !cfgMenuOpen;
                cfgScrollTarget = 0f;
                cfgScrollOffset = 0f;
            }
            return true;
        }

        if (cfgMenuOpen) {
            int listY = boxY + boxH + 4;
            int listW = boxW;
            int listH = getCfgManagerListHeight();
            if (mouseX >= boxX && mouseX <= boxX + listW && mouseY >= listY && mouseY <= listY + listH && button == 0) {
                java.util.List<String> cfgs = Manager.CONFIG_MANAGER.getAllConfigurations();
                cfgs.sort(String.CASE_INSENSITIVE_ORDER);
                int rowH = getCfgManagerRowHeight();
                int idx = (int) Math.floor(((mouseY - (listY + 4)) + cfgScrollOffset) / rowH);
                if (idx >= 0 && idx < cfgs.size()) {
                    Manager.CONFIG_MANAGER.loadConfiguration(cfgs.get(idx), false);
                }
                cfgMenuOpen = false;
                return true;
            }

            cfgMenuOpen = false;
        }

        cfgNameFocused = false;
        return false;
    }
    private boolean isFunctionVisible(Function function) {
        String searchTextLower = searchState.text.toLowerCase();
        return searchTextLower.isEmpty() || function.name.toLowerCase().contains(searchTextLower) || function.keywords.toLowerCase().contains(searchTextLower);
    }

    @Override
    public void renderBackground(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        // без дополнительного фонового эффекта; весь blur контролируется через отдельные элементы (панели, описания и т.п.)
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private int computeSettingsHeight(Function f) {
        int settingsHeight = 0;
        for (Setting setting : f.getSettings()) {
            if (!setting.isVisible()) continue;
            if (setting instanceof BooleanSetting) {
                settingsHeight += booleanSettingRenderer.getHeight();
            } else if (setting instanceof BindBooleanSetting) {
                settingsHeight += bindbooleanSettingRenderer.getHeight();
            } else if (setting instanceof BindSetting) {
                settingsHeight += bindSettingRenderer.getHeight();
            } else if (setting instanceof ModeSetting) {
                settingsHeight += modeSettingRenderer.getHeight((ModeSetting) setting, PANEL_WIDTH - 20);
            } else if (setting instanceof MultiSetting) {
                settingsHeight += multiSettingRenderer.getHeight((MultiSetting) setting, PANEL_WIDTH - 20);
            } else if (setting instanceof SliderSetting) {
                settingsHeight += sliderSettingRenderer.getHeight();
            } else if (setting instanceof TextSetting) {
                settingsHeight += textSettingRenderer.getHeight();
            }
        }
        return Math.max(0, settingsHeight);
    }

    private int getSettingRendererHeight(Setting setting, int width) {
        if (!setting.isVisible()) return 0;
        if (setting instanceof BooleanSetting) {
            return booleanSettingRenderer.getHeight();
        } else if (setting instanceof BindBooleanSetting) {
            return bindbooleanSettingRenderer.getHeight();
        } else if (setting instanceof BindSetting) {
            return bindSettingRenderer.getHeight();
        } else if (setting instanceof ModeSetting modeSetting) {
            return modeSettingRenderer.getHeight(modeSetting, width);
        } else if (setting instanceof MultiSetting multiSetting) {
            return multiSettingRenderer.getHeight(multiSetting, width);
        } else if (setting instanceof SliderSetting) {
            return sliderSettingRenderer.getHeight();
        } else if (setting instanceof TextSetting) {
            return textSettingRenderer.getHeight();
        }
        return 0;
    }

    private void clampScrollForCategory(Type category) {
        int maxScroll = calculateMaxScroll(category);
        float clampedTarget = MathHelper.clamp(scrollTargets.get(category), 0f, (float) maxScroll);
        float clampedOffset = MathHelper.clamp(scrollOffsets.get(category), 0f, (float) maxScroll);
        scrollTargets.put(category, clampedTarget);
        scrollOffsets.put(category, clampedOffset);
    }
}