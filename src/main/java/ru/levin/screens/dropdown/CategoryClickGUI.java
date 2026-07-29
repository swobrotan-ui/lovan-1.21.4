package ru.levin.screens.dropdown;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
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
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.manager.fontManager.FontUtils;
import ru.levin.modules.Function;
import ru.levin.modules.Type;
import ru.levin.modules.setting.*;
import ru.levin.screens.dropdown.impl.*;
import ru.levin.screens.dropdown.search.SearchState;
import ru.levin.util.animations.impl.EaseInOutQuad;
import ru.levin.util.color.ColorUtil;
import ru.levin.util.math.MathUtil;
import ru.levin.util.render.RenderAddon;
import ru.levin.util.render.RenderUtil;
import ru.levin.util.render.Scissor;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.levin.util.render.RenderUtil.drawBlur;

public class CategoryClickGUI extends Screen implements IMinecraft {
    private boolean isClose;
    private double animation;
    private final EaseInOutQuad animationOpen = new EaseInOutQuad(250, 1);

    private final int LEFT_WIDTH = 100;
    private final int RIGHT_WIDTH = 236;
    private final int PANEL_HEIGHT = 240;
    private final int PANEL_GAP = 6;

    private final int TITLE_HEIGHT = 18;
    private final int SEARCH_HEIGHT = 20;
    private final int CATEGORY_HEIGHT = 20;
    private final int CATEGORY_GAP = 6;

    private final int TAB_HEIGHT = 16;
    private final int TAB_GAP = 5;
    private final int TAB_PAD_X = 12;

    private final int LEFT_HEADER_HEIGHT = 42;

    private final int FUNCTION_HEIGHT = 20;
    private final int FUNCTION_GAP = 3;

    private final float SCROLL_SPEED = 12f;
    private final float SCROLL_LERP = 18f;

    private enum RightTab {
        MODULES("Modules"),
        SETTINGS("Settings");

        final String title;
        RightTab(String title) { this.title = title; }
    }

    private RightTab rightTab = RightTab.MODULES;
    private float rightTabSwitchAnim = 0.0f;
    private RightTab rightPrevTab = RightTab.MODULES;
    private final float[] rightTabHover = new float[RightTab.values().length];

    private final SearchState searchState;
    private float scrollOffset = 0f;
    private float scrollTarget = 0f;

    private Type selectedCategory = Type.Combat;
    private Function selectedFunction = null;
    private final EnumSet<Type> renderCategories = EnumSet.of(Type.Combat, Type.Move, Type.Render, Type.Player, Type.Misc);

    private String getCategoryName(Type category) {
        return switch (category) {
            case Combat -> "Combat";
            case Move -> "Movement";
            case Render -> "Visuals";
            case Player -> "Player";
            case Misc -> "Misc";
        };
    }

    private final Map<Function, Float> expandProgress = new HashMap<>();
    private final Map<Function, Float> hoverProgress = new HashMap<>();
    private final Map<Function, Float> toggleProgress = new HashMap<>();

    private final Map<Type, Float> categorySelectProgress = new HashMap<>();
    private final Map<Type, Float> categoryHoverProgress = new HashMap<>();

    private final BooleanSettingRenderer booleanSettingRenderer = new BooleanSettingRenderer();
    private final BindBooleanSettingRenderer bindbooleanSettingRenderer = new BindBooleanSettingRenderer();
    private final BindSettingRenderer bindSettingRenderer = new BindSettingRenderer();
    private final ModeSettingRenderer modeSettingRenderer = new ModeSettingRenderer();
    private final MultiSettingRenderer multiSettingRenderer = new MultiSettingRenderer();
    private final SliderSettingRenderer sliderSettingRenderer = new SliderSettingRenderer();
    private final TextSettingRenderer textSettingRenderer = new TextSettingRenderer();

    private SliderSetting draggingSlider = null;
    private int draggingSliderX = 0;
    private int draggingSliderWidth = 0;

    private Function previewFunction = null;

    public CategoryClickGUI() {
        super(Text.literal("ClickGUI"));
        this.searchState = new SearchState();
    }

    private static final int PREVIEW_OVERLAY_W = 220;
    private static final int PREVIEW_OVERLAY_H = 160;

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
                int size = 58;
                InventoryScreen.drawEntity(ctx, cx, cy, size, 0, 0, 0f, 0f, 0f, le);
                renderEffectIconsAround(ctx, le, x + w / 2f, y + h * 0.32f, 34f);
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
            float sc = 28f;
            ctx.getMatrices().scale(sc, -sc, sc);

            VertexConsumerProvider.Immediate consumers = mc.getBufferBuilders().getEntityVertexConsumers();
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) mc.player;

            if (f.name.equalsIgnoreCase("SwingAnimations")) {
                // preview the actual swing transform from your module
                Manager.FUNCTION_MANAGER.swingAnimations.renderFirstPersonItem(player, tickDelta, 0f, Hand.MAIN_HAND, 0.65f, item, 0f, ctx.getMatrices(), consumers, light);
            } else {
                // ViewModel: apply slider offsets only
                var vm = Manager.FUNCTION_MANAGER.viewModel;
                ctx.getMatrices().translate(vm.right_x.get().floatValue(), vm.right_y.get().floatValue(), vm.right_z.get().floatValue());
                mc.getItemRenderer().renderItem(player, item, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, false, ctx.getMatrices(), consumers, mc.world, light, 0, player.getId() + ModelTransformationMode.FIRST_PERSON_RIGHT_HAND.ordinal());
            }
            consumers.draw();
            ctx.getMatrices().pop();

            renderEffectIconsAround(ctx, mc.player, x + w / 2f, y + h * 0.28f, 30f);
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
        if (!Manager.FUNCTION_MANAGER.clickGUI.guiType.is("Категории")) {
            close();
        }
        if (animation <= 0.01) return;

        super.render(ctx, mouseX, mouseY, delta);
        ctx.getMatrices().push();

        int totalWidth = LEFT_WIDTH + PANEL_GAP + RIGHT_WIDTH;
        int startX = (width - totalWidth) / 2;
        int startY = (height - PANEL_HEIGHT) / 2;

        RenderAddon.sizeAnimation(ctx.getMatrices(), width / 2, height / 2, animation);

        renderCategoryPanel(ctx, startX, startY, mouseX, mouseY);
        renderRightPanel(ctx, startX + LEFT_WIDTH + PANEL_GAP, startY, mouseX, mouseY);

        ctx.getMatrices().pop();
    }

    private void renderCategoryPanel(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        ru.levin.modules.render.ClickGUI clickGUI = Manager.FUNCTION_MANAGER.clickGUI;

        // базовый цвет панели как в обычном ClickGUI
        Color guiColorObj = clickGUI.getGuiColor();
        int panelColor = guiColorObj.getRGB();
        int themeBase = Manager.STYLE_MANAGER.getFirstColor();
        int accent = themeBase;
        Color themeCol = new Color(themeBase, true);
        if (Math.abs(themeCol.getRed() - themeCol.getGreen()) < 15 &&
                Math.abs(themeCol.getGreen() - themeCol.getBlue()) < 15) {
            int a = guiColorObj.getAlpha();
            panelColor = new Color(18, 18, 24, a).getRGB();
        }

        if (clickGUI.blur.get() && clickGUI.blurSetting.get("Панели")) {
            drawBlur(ctx.getMatrices(), x, y, LEFT_WIDTH, PANEL_HEIGHT, 12, 8, -1);
        }

        boolean plain = clickGUI.interfaceStyle.is("Обычный");
        if (plain) {
            int shadow1 = new Color(0, 0, 0, 55).getRGB();
            int shadow2 = new Color(0, 0, 0, 28).getRGB();
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x - 2, y - 2, LEFT_WIDTH + 4, PANEL_HEIGHT + 4, 14, shadow2);
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x - 1, y - 1, LEFT_WIDTH + 2, PANEL_HEIGHT + 2, 13, shadow1);
        }
        RenderUtil.drawRoundedRect(ctx.getMatrices(), x, y, LEFT_WIDTH, PANEL_HEIGHT, 12, panelColor);

        if (plain) {
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, LEFT_WIDTH, PANEL_HEIGHT, 12, 0.8f,
                    ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.08f));

            int c1 = ColorUtil.reAlphaInt(themeBase, 100);
            int c2 = ColorUtil.reAlphaInt(themeBase, 0);
            RenderUtil.rectRGB(ctx.getMatrices(), x + 1, y + 1, LEFT_WIDTH - 2, 12, 12,
                    c1,
                    c1,
                    c2,
                    c2
            );
        }

        {
            int headerX = x + 6;
            int headerY = y + 6;
            int headerW = LEFT_WIDTH - 12;
            int headerH = LEFT_HEADER_HEIGHT;

            int logoSize = 22;
            int logoX = headerX + headerW / 2 - logoSize / 2;
            int logoY = headerY + 5;
            RenderUtil.drawTexture(ctx.getMatrices(), "images/logo/lovan.png", logoX, logoY, logoSize, logoSize, 2, Color.WHITE.getRGB());

            String title = "Lovan";
            float titleY = logoY + logoSize + 2;
            FontUtils.sf_bold[18].centeredDraw(ctx.getMatrices(), title, headerX + headerW / 2f, titleY,
                    ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.92f));
        }

        int searchX = x + 10;
        int searchW = LEFT_WIDTH - 20;
        int searchY = y + 6 + LEFT_HEADER_HEIGHT + 6;
        renderSearchField(ctx, searchX, searchY, searchW, SEARCH_HEIGHT);

        int listX = x + 10;
        int listW = LEFT_WIDTH - 20;
        int listY = searchY + SEARCH_HEIGHT + 6;

        int idx = 0;
        for (Type category : renderCategories) {
            int itemY = listY + idx++ * (CATEGORY_HEIGHT + CATEGORY_GAP);
            boolean hovered = RenderUtil.isHovered(mouseX, mouseY, listX, itemY, listW, CATEGORY_HEIGHT);
            boolean selected = category == selectedCategory;

            float selTarget = selected ? 1f : 0f;
            float selProg = categorySelectProgress.getOrDefault(category, selTarget);
            selProg = MathUtil.lerp(selProg, selTarget, 14f);
            if (Math.abs(selTarget - selProg) < 0.001f) selProg = selTarget;
            categorySelectProgress.put(category, selProg);

            float hovTarget = hovered ? 1f : 0f;
            float hovProg = categoryHoverProgress.getOrDefault(category, hovTarget);
            hovProg = MathUtil.lerp(hovProg, hovTarget, 16f);
            if (Math.abs(hovTarget - hovProg) < 0.001f) hovProg = hovTarget;
            categoryHoverProgress.put(category, hovProg);

            float indicatorW = 1.5f + 2.0f * selProg + 1.0f * hovProg;
            int indicatorAlpha = (int) MathHelper.clamp(40 + 140 * selProg + 35 * hovProg, 0, 255);
            int indicatorColor = ColorUtil.reAlphaInt(accent, indicatorAlpha);
            RenderUtil.drawRoundedRect(ctx.getMatrices(), listX, itemY + 4, indicatorW, CATEGORY_HEIGHT - 8, 2,
                    indicatorColor);
            String name = switch (category) {
                case Combat -> "Combat";
                case Move -> "Movement";
                case Render -> "Visuals";
                case Player -> "Player";
                case Misc -> "Misc";
            };

            int baseText = new Color(170, 170, 190).getRGB();
            int targetText = Color.WHITE.getRGB();
            float mix = MathHelper.clamp(0.55f * selProg + 0.35f * hovProg, 0f, 1f);
            int textColor = ColorUtil.interpolateColor(baseText, targetText, mix);
            int textY = (int) (itemY + (CATEGORY_HEIGHT - FontUtils.sf_medium[16].getHeight()) / 2f);
            float textX = listX + 8 + 6f * selProg + 2f * hovProg;
            FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), name, textX, textY, textColor);
        }
    }

    private void renderRightPanel(DrawContext ctx, int x, int y, int mouseX, int mouseY) {
        ru.levin.modules.render.ClickGUI clickGUI = Manager.FUNCTION_MANAGER.clickGUI;
        Color guiColorObj = clickGUI.getGuiColor();
        int panelColor = guiColorObj.getRGB();
        int themeBase = Manager.STYLE_MANAGER.getFirstColor();
        Color themeCol = new Color(themeBase, true);
        if (Math.abs(themeCol.getRed() - themeCol.getGreen()) < 15 &&
                Math.abs(themeCol.getGreen() - themeCol.getBlue()) < 15) {
            int a = guiColorObj.getAlpha();
            panelColor = new Color(18, 18, 24, a).getRGB();
        }
        if (clickGUI.blur.get() && clickGUI.blurSetting.get("Панели")) {
            drawBlur(ctx.getMatrices(), x, y, RIGHT_WIDTH, PANEL_HEIGHT, 12, 8, -1);
        }

        boolean plain = clickGUI.interfaceStyle.is("Обычный");
        if (plain) {
            int shadow1 = new Color(0, 0, 0, 55).getRGB();
            int shadow2 = new Color(0, 0, 0, 28).getRGB();
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x - 2, y - 2, RIGHT_WIDTH + 4, PANEL_HEIGHT + 4, 14, shadow2);
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x - 1, y - 1, RIGHT_WIDTH + 2, PANEL_HEIGHT + 2, 13, shadow1);
        }
        RenderUtil.drawRoundedRect(ctx.getMatrices(), x, y, RIGHT_WIDTH, PANEL_HEIGHT, 12, panelColor);

        if (plain) {
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, RIGHT_WIDTH, PANEL_HEIGHT, 12, 0.8f,
                    ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.08f));

            int c1 = ColorUtil.reAlphaInt(themeBase, 100);
            int c2 = ColorUtil.reAlphaInt(themeBase, 0);
            RenderUtil.rectRGB(ctx.getMatrices(), x + 1, y + 1, RIGHT_WIDTH - 2, 12, 12,
                    c1,
                    c1,
                    c2,
                    c2
            );
        }

        int tabY = y + 10;
        int tabX = x + 10;
        int tabW = RIGHT_WIDTH - 20;
        int tabH = 20;

        for (int i = 0; i < RightTab.values().length; i++) {
            RightTab t = RightTab.values()[i];
            int tx = tabX + i * (tabW / 2 + 6);
            int tw = tabW / 2;
            boolean selected = t == rightTab;
            boolean hovered = RenderUtil.isHovered(mouseX, mouseY, tx, tabY, tw, tabH);

            rightTabHover[i] = MathUtil.lerp(rightTabHover[i], hovered ? 1.0f : 0.0f, 16f);
            float ha = rightTabHover[i];

            ctx.getMatrices().push();
            float txc = tx + tw / 2f;
            float tyc = tabY + tabH / 2f;
            float tScale = 1.0f + 0.04f * ha;
            ctx.getMatrices().translate(txc, tyc, 0.0);
            ctx.getMatrices().scale(tScale, tScale, 1.0f);
            ctx.getMatrices().translate(-txc, -tyc, 0.0);

            int bg = new Color(0, 0, 0, selected ? 78 : (int) (54 + ha * 12)).getRGB();
            RenderUtil.drawRoundedRect(ctx.getMatrices(), tx, tabY, tw, tabH, 7, bg);
            int borderC = selected ? ColorUtil.reAlphaInt(themeBase, 140)
                    : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f + 0.04f * ha);
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), tx, tabY, tw, tabH, 7, 0.8f, borderC);

            if (selected) {
                RenderUtil.drawRoundedRect(ctx.getMatrices(), tx + 4, tabY + 4, 2, tabH - 8, 1, ColorUtil.reAlphaInt(themeBase, 190));
            }

            int tc = selected ? Color.WHITE.getRGB() : new Color(185, 185, 200).getRGB();
            FontUtils.sf_medium[14].centeredDraw(ctx.getMatrices(), t.title, txc, tabY + 5, tc);
            ctx.getMatrices().pop();
        }

        int contentY = tabY + tabH + 8;
        int contentH = PANEL_HEIGHT - (contentY - y) - 6;
        int contentX = x + 6;
        int contentW = RIGHT_WIDTH - 12;

        rightTabSwitchAnim = MathUtil.lerp(rightTabSwitchAnim, 1.0f, 14f);
        float slide = (1.0f - rightTabSwitchAnim) * 6.0f;
        ctx.getMatrices().translate(slide, 0.0, 0.0);

        ctx.getMatrices().push();
        Scissor.push();
        Scissor.setFromComponentCoordinates(contentX, contentY, contentW, contentH);

        if (rightTab == RightTab.MODULES) {
            renderModulePanel(ctx, x, contentY, mouseX, mouseY, contentW, contentH);
        } else {
            renderSettingsTab(ctx, x, contentY, mouseX, mouseY, contentW, contentH);
        }

        Scissor.pop();
        ctx.getMatrices().pop();

        int maxScroll = getMaxScroll(contentW, contentH);
        drawRightScrollBar(ctx, mouseX, mouseY, contentX, contentY, contentW, contentH, maxScroll);
        renderPreviewIfNeeded(ctx, x, y);
    }

    private void drawRightScrollBar(DrawContext ctx, int mouseX, int mouseY, int x, int y, int w, int h, int maxScroll) {
        if (maxScroll <= 1) return;
        int trackW = 3;
        int trackX = x + w - trackW;
        int trackY = y;
        int trackH = h;

        int track = ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.05f);
        RenderUtil.drawRoundedRect(ctx.getMatrices(), trackX, trackY, trackW, trackH, 2, track);

        float visible = h;
        float total = h + maxScroll;
        float ratio = MathHelper.clamp(visible / total, 0.10f, 1.0f);
        int thumbH = Math.max(10, (int) (trackH * ratio));
        float p = MathHelper.clamp(scrollOffset / (float) maxScroll, 0.0f, 1.0f);
        int thumbY = trackY + (int) ((trackH - thumbH) * p);

        boolean hov = RenderUtil.isHovered(mouseX, mouseY, trackX - 2, thumbY - 2, trackW + 4, thumbH + 4);
        int thumb = hov ? ColorUtil.reAlphaInt(Manager.STYLE_MANAGER.getFirstColor(), 55) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.14f);
        RenderUtil.drawRoundedRect(ctx.getMatrices(), trackX, thumbY, trackW, thumbH, 2, thumb);
    }

    private void renderPreviewIfNeeded(DrawContext ctx, int panelX, int panelY) {
        Function pf = previewFunction;
        if (pf != null && pf.expanded && hasPreviewPill(pf)) {
            int px = panelX + RIGHT_WIDTH + 10;
            int py = panelY + 14;
            renderPreviewOverlay(ctx, pf, px, py, PREVIEW_OVERLAY_W, PREVIEW_OVERLAY_H);
        }
    }

    private void renderModulePanel(DrawContext ctx, int baseX, int contentY, int mouseX, int mouseY, int contentW, int contentH) {
        ru.levin.modules.render.ClickGUI clickGUI = Manager.FUNCTION_MANAGER.clickGUI;
        int themeBase = Manager.STYLE_MANAGER.getFirstColor();

        int maxScroll = calculateMaxScroll(contentW, contentH);
        scrollTarget = MathHelper.clamp(scrollTarget, 0f, (float) maxScroll);
        scrollOffset = MathUtil.lerp(scrollOffset, scrollTarget, SCROLL_LERP);
        scrollOffset = MathHelper.clamp(scrollOffset, 0f, (float) maxScroll);

        int innerX = baseX + 6;
        int innerW = contentW;
        int listY = contentY;
        int listH = contentH;

        float cy = listY - scrollOffset;
        previewFunction = null;
        List<Function> functions = getVisibleFunctions();
        for (Function f : functions) {
            int functionHeight = 34;
            int settingsFull = computeSettingsHeight(f, innerW - 16);
            float prog = updateExpandAnimation(f);
            float eased = easeInOut(prog);
            int settingsVisible = (int) Math.round(settingsFull * eased);
            int totalHeight = functionHeight + settingsVisible;

            if (cy + totalHeight < listY || cy > listY + listH) {
                cy += totalHeight + FUNCTION_GAP;
                continue;
            }

            boolean hovered = mouseX >= innerX && mouseX <= innerX + innerW && mouseY >= cy && mouseY <= cy + functionHeight;
            float hoverAnim = updateHoverAnimation(f, hovered);

            int baseAlpha = Math.min(255, clickGUI.alphaModules.get().intValue() + Math.round(12f * hoverAnim));
            int themeColor = ColorUtil.reAlphaInt(themeBase, baseAlpha);
            int baseBg = f.state
                    ? themeColor
                    : ColorUtil.interpolateColor(new Color(26, 24, 40, baseAlpha).getRGB(), themeColor, 0.30f);
            int bg = ColorUtil.interpolateColor(baseBg, ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.16f), hoverAnim * 0.20f);

            float moduleRounding = 9f;
            RenderUtil.drawRoundedRect(ctx.getMatrices(), innerX, cy - 0.5f, innerW, functionHeight - 1, moduleRounding, bg);

            int nameColor = f.state ? Color.WHITE.getRGB() : new Color(210, 210, 220).getRGB();
            int descColor = new Color(150, 150, 165).getRGB();
            int nameX = innerX + 10;
            int nameY = (int) (cy + 7);
            int descY = (int) (cy + 19);

            int rightPad = 10;
            int switchW = 28;
            int switchH = 14;
            int switchX = innerX + innerW - rightPad - switchW;
            int switchY = (int) (cy + (functionHeight - switchH) / 2f);

            int maxNameW = switchX - 10 - nameX;
            String nameText = trimToWidth(f.name, Math.max(0, maxNameW));
            FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), nameText, nameX, nameY, nameColor);

            String desc = f.desc == null ? "" : f.desc;
            boolean globalSearch = !searchState.text.isEmpty();
            if (globalSearch) {
                desc = getCategoryName(f.getCategory());
            }
            String descText = trimToWidth(desc, Math.max(0, maxNameW));
            FontUtils.sf_medium[12].drawLeftAligned(ctx.getMatrices(), descText, nameX, descY, descColor);

            float stateProg = updateToggleAnimation(f);
            int offColor = new Color(55, 55, 55, 210).getRGB();
            int onColor = ColorUtil.reAlphaInt(themeBase, 170);
            int switchBg = ColorUtil.interpolateColor(offColor, onColor, stateProg);
            RenderUtil.drawRoundedRect(ctx.getMatrices(), switchX, switchY, switchW, switchH, 7, switchBg);
            float knob = 10f;
            float knobX = switchX + 2f + (switchW - knob - 4f) * stateProg;
            float knobY = switchY + (switchH - knob) / 2f;
            RenderUtil.drawCircle(ctx.getMatrices(), knobX + knob / 2f, knobY + knob / 2f, knob, Color.WHITE.getRGB());

            if (f.expanded && hasPreviewPill(f)) {
                previewFunction = f;
            }

            if (settingsVisible > 0) {
                int sx = innerX + 8;
                int sw = innerW - 16;
                float sy = cy + functionHeight;

                ctx.getMatrices().push();
                Scissor.push();
                Scissor.setFromComponentCoordinates(innerX, (int) sy, innerW, settingsVisible);

                int contentW2 = sw;

                for (Setting setting : f.getSettings()) {
                    if (!setting.isVisible()) continue;
                    int h = getSettingRendererHeight(setting, contentW2);
                    if (h <= 0) continue;
                    if (sy + h > cy + functionHeight + settingsVisible) break;

                    if (setting instanceof BooleanSetting booleanSetting) {
                        booleanSettingRenderer.render(ctx, booleanSetting, sx, (int) sy, contentW2, h);
                    } else if (setting instanceof BindBooleanSetting bindBooleanSetting) {
                        bindbooleanSettingRenderer.render(ctx, bindBooleanSetting, sx, (int) sy, contentW2, h);
                    } else if (setting instanceof BindSetting bindSetting) {
                        bindSettingRenderer.render(ctx, bindSetting, sx, (int) sy - 2, contentW2, h);
                    } else if (setting instanceof ModeSetting modeSetting) {
                        modeSettingRenderer.render(ctx, modeSetting, sx, (int) sy, contentW2, h);
                    } else if (setting instanceof MultiSetting multiSetting) {
                        multiSettingRenderer.render(ctx, multiSetting, sx, (int) sy, contentW2, h);
                    } else if (setting instanceof SliderSetting sliderSetting) {
                        sliderSettingRenderer.render(ctx, sliderSetting, sx, (int) sy - 2, contentW2, h);
                    } else if (setting instanceof TextSetting textSetting) {
                        textSettingRenderer.render(ctx, textSetting, sx, (int) sy, contentW2, h);
                    }

                    sy += h;
                }

                Scissor.pop();
                ctx.getMatrices().pop();
            }

            cy += totalHeight + FUNCTION_GAP;
        }
    }

    private void renderSettingsTab(DrawContext ctx, int baseX, int y, int mouseX, int mouseY, int contentW, int contentH) {
        ru.levin.modules.render.ClickGUI clickGUI = Manager.FUNCTION_MANAGER.clickGUI;
        int themeBase = Manager.STYLE_MANAGER.getFirstColor();
        int itemH = 22;
        int gap = 5;

        ctx.getMatrices().push();
        Scissor.push();
        Scissor.setFromComponentCoordinates(baseX + 6, y, contentW, contentH);

        float cy = y;

        String[] sections = {
                "Appearance",
                "Interface",
                "Modules",
                "System"
        };

        for (String section : sections) {
            int sectionW = contentW - 16;
            cy = renderSettingsSectionLabel(ctx, baseX + 8, cy, sectionW, section);
            cy += 2;

            int cardY = (int) cy;
            int cardW = sectionW;
            int cardH = itemH;

            if (section.equals("Appearance")) {
                cy = renderSettingsRow(ctx, baseX + 8, cardY, cardW, cardH, "Theme", clickGUI.colorGUI.get(), false, mouseX, mouseY, () -> {
                    List<String> modes = List.of("Светло-чёрная", "Тёмная");
                    int ci = modes.indexOf(clickGUI.colorGUI.get());
                    int ni = (ci + 1) % modes.size();
                    clickGUI.colorGUI.set(modes.get(ni));
                });
                cy += gap;
                cy = renderSettingsRow(ctx, baseX + 8, (int) cy, cardW, cardH, "GUI Type", clickGUI.guiType.get(), false, mouseX, mouseY, () -> {
                    List<String> modes = List.of("Колонки", "Категории");
                    int ci = modes.indexOf(clickGUI.guiType.get());
                    int ni = (ci + 1) % modes.size();
                    clickGUI.guiType.set(modes.get(ni));
                });
                cy += gap;
            } else if (section.equals("Interface")) {
                cy = renderSettingsRow(ctx, baseX + 8, cardY, cardW, cardH, "Blur", clickGUI.blur.get() ? "On" : "Off", clickGUI.blur.get(), mouseX, mouseY, () -> clickGUI.blur.set(!clickGUI.blur.get()));
                cy += gap;
                cy = renderSettingsRow(ctx, baseX + 8, (int) cy, cardW, cardH, "Style", clickGUI.interfaceStyle.get(), false, mouseX, mouseY, () -> {
                    List<String> modes = List.of("Клиентский", "Обычный");
                    int ci = modes.indexOf(clickGUI.interfaceStyle.get());
                    int ni = (ci + 1) % modes.size();
                    clickGUI.interfaceStyle.set(modes.get(ni));
                });
                cy += gap;
                cy = renderSettingsRow(ctx, baseX + 8, (int) cy, cardW, cardH, "Alpha", String.valueOf(clickGUI.alpha.get().intValue()), false, mouseX, mouseY, null);
                cy += gap;
            } else if (section.equals("Modules")) {
                cy = renderSettingsRow(ctx, baseX + 8, cardY, cardW, cardH, "Module Alpha", String.valueOf(clickGUI.alphaModules.get().intValue()), false, mouseX, mouseY, null);
                cy += gap;
                cy = renderSettingsRow(ctx, baseX + 8, (int) cy, cardW, cardH, "Rounding", String.valueOf(clickGUI.rounding.get().intValue()), false, mouseX, mouseY, null);
                cy += gap;
                cy = renderSettingsRow(ctx, baseX + 8, (int) cy, cardW, cardH, "Outline", clickGUI.strike.get() ? "On" : "Off", clickGUI.strike.get(), mouseX, mouseY, () -> clickGUI.strike.set(!clickGUI.strike.get()));
                cy += gap;
                cy = renderSettingsRow(ctx, baseX + 8, (int) cy, cardW, cardH, "Filling", clickGUI.filling.get() ? "On" : "Off", clickGUI.filling.get(), mouseX, mouseY, () -> clickGUI.filling.set(!clickGUI.filling.get()));
                cy += gap;
            } else {
                cy = renderSettingsRow(ctx, baseX + 8, cardY, cardW, cardH, "Client", "ExosWare", false, mouseX, mouseY, null);
                cy += gap;
                cy = renderSettingsRow(ctx, baseX + 8, (int) cy, cardW, cardH, "Version", "1.21.4", false, mouseX, mouseY, null);
            }

            cy += gap + 4;
        }

        Scissor.pop();
        ctx.getMatrices().pop();
    }

    private float renderSettingsSectionLabel(DrawContext ctx, int x, float y, int w, String title) {
        FontUtils.sf_medium[12].drawLeftAligned(ctx.getMatrices(), title.toUpperCase(), x, y + 3,
                ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.45f));
        return y + 12;
    }

    private float renderSettingsRow(DrawContext ctx, int x, int y, int w, int h, String title, String value, boolean toggle, int mouseX, int mouseY, Runnable onClick) {
        boolean hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, w, h);

        int bgTop = new Color(0, 0, 0, hovered ? 72 : 58).getRGB();
        int bgBot = new Color(0, 0, 0, hovered ? 52 : 42).getRGB();
        RenderUtil.rectRGB(ctx.getMatrices(), x, y, w, h, 7, bgTop, bgTop, bgBot, bgBot);

        int borderC = hovered ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.07f) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.04f);
        RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, w, h, 7, 0.8f, borderC);

        int titleC = hovered ? Color.WHITE.getRGB() : new Color(190, 190, 205).getRGB();
        FontUtils.sf_medium[14].drawLeftAligned(ctx.getMatrices(), title, x + 10, y + 5, titleC);

        if (toggle) {
            float tw = FontUtils.sf_medium[14].getWidth(value);
            float tx = x + w - 10 - tw;
            int valC = Boolean.parseBoolean(value) ? ColorUtil.reAlphaInt(Manager.STYLE_MANAGER.getFirstColor(), 220) : new Color(140, 140, 155).getRGB();
            FontUtils.sf_medium[14].drawLeftAligned(ctx.getMatrices(), value, tx, y + 5, valC);
        } else {
            int valC = new Color(160, 160, 175).getRGB();
            FontUtils.sf_medium[14].drawRightAligned(ctx.getMatrices(), value, x + w - 8, y + 5, valC);
        }

        if (hovered && onClick != null) {
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x + 1, y + 1, w - 2, h - 2, 6,
                    ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.04f));
        }

        return y + h + 5;
    }

    private void renderSearchField(DrawContext ctx, int searchX, int searchY, int searchW, int searchH) {
        int bg = new Color(0, 0, 0, 55).getRGB();
        RenderUtil.drawRoundedRect(ctx.getMatrices(), searchX, searchY, searchW, searchH, 9, bg);
        RenderUtil.drawRoundedBorder(ctx.getMatrices(), searchX, searchY, searchW, searchH, 9, 0.8f,
                ColorUtil.applyAlpha(Color.WHITE.getRGB(), searchState.focused ? 0.16f : 0.08f));

        String displayText;
        int searchTextColor;
        int searchTextX;
        if (searchState.text.isEmpty() && !searchState.focused) {
            displayText = "Поиск";
            searchTextColor = new Color(255, 255, 255, 140).getRGB();
            searchTextX = searchX + 24;
        } else {
            String text = searchState.text;
            if (searchState.focused && searchState.cursorVisible) {
                int pos = Math.min(searchState.cursorPosition, text.length());
                text = text.substring(0, pos) + "|" + text.substring(pos);
            }
            displayText = text;
            searchTextColor = Color.WHITE.getRGB();
            searchTextX = searchX + 24;
        }
        int searchTextY = (int) (searchY + (SEARCH_HEIGHT - FontUtils.sf_medium[16].getHeight()) / 2f);
        int iconSize = 10;
        int iconX = searchX + 10;
        int iconY = searchY + searchH / 2 - iconSize / 2;
        RenderUtil.drawTexture(ctx.getMatrices(), "images/gui/search.png", iconX, iconY, iconSize, iconSize, 1, Color.WHITE.getRGB());
        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), displayText, searchTextX, searchTextY, searchTextColor);
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int totalWidth = LEFT_WIDTH + PANEL_GAP + RIGHT_WIDTH;
        int startX = (width - totalWidth) / 2;
        int startY = (height - PANEL_HEIGHT) / 2;

        int leftX = startX;
        int leftY = startY;
        int rightX = startX + LEFT_WIDTH + PANEL_GAP;
        int rightY = startY;

        int leftSearchX = leftX + 10;
        int leftSearchW = LEFT_WIDTH - 20;
        int leftSearchY = leftY + 6 + LEFT_HEADER_HEIGHT + 6;
        if (mouseX >= leftSearchX && mouseX <= leftSearchX + leftSearchW && mouseY >= leftSearchY && mouseY <= leftSearchY + SEARCH_HEIGHT) {
            searchState.focused = true;
            searchState.cursorPosition = searchState.text.length();
            return true;
        }

        int listX = leftX + 10;
        int listW = LEFT_WIDTH - 20;
        int listY = leftSearchY + SEARCH_HEIGHT + 6;

        int idx = 0;
        for (Type category : renderCategories) {
            int itemY = listY + idx++ * (CATEGORY_HEIGHT + CATEGORY_GAP);
            if (RenderUtil.isHovered((int) mouseX, (int) mouseY, listX, itemY, listW, CATEGORY_HEIGHT)) {
                selectedCategory = category;
                scrollOffset = 0f;
                scrollTarget = 0f;
                return true;
            }
        }

        searchState.focused = false;

        int tabY = rightY + 10;
        int tabX = rightX + 10;
        int tabW = RIGHT_WIDTH - 20;
        int tabH = 20;

        for (int i = 0; i < RightTab.values().length; i++) {
            RightTab t = RightTab.values()[i];
            int tx = tabX + i * (tabW / 2 + 6);
            int tw = tabW / 2;
            if (RenderUtil.isHovered((int) mouseX, (int) mouseY, tx, tabY, tw, tabH)) {
                if (rightTab != t) {
                    rightPrevTab = rightTab;
                    rightTab = t;
                    rightTabSwitchAnim = 0.0f;
                }
                scrollOffset = 0f;
                scrollTarget = 0f;
                return true;
            }
        }

        int contentX = rightX + 6;
        int contentY = rightY + 10 + 20 + 8;
        int contentW = RIGHT_WIDTH - 12;
        int contentH = PANEL_HEIGHT - (contentY - rightY) - 6;

        if (rightTab == RightTab.MODULES) {
            handleModuleClicks(mouseX, mouseY, button, contentX, contentY, contentW, contentH);
        } else {
            handleSettingsClicks(mouseX, mouseY, button, contentX, contentY, contentW, contentH);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleModuleClicks(double mouseX, double mouseY, int button, int contentX, int contentY, int contentW, int contentH) {
        float currentY = contentY - scrollOffset;

        List<Function> functions = getVisibleFunctions();
        for (Function function : functions) {
            int functionHeight = 34;
            int settingsFull = computeSettingsHeight(function, contentW - 16);
            float prog = updateExpandAnimation(function);
            float eased = easeInOut(prog);
            int settingsVisible = (int) Math.round(settingsFull * eased);
            int totalHeight = functionHeight + settingsVisible;

            if (currentY + totalHeight < contentY) {
                currentY += totalHeight + FUNCTION_GAP;
                continue;
            }
            if (currentY > contentY + contentH) {
                break;
            }

            int rightPad = 10;
            int switchW = 28;
            int switchH = 14;
            int switchX = contentX + contentW - rightPad - switchW;
            int switchY = (int) (currentY + (functionHeight - switchH) / 2f);
            boolean onSwitch = mouseX >= switchX && mouseX <= switchX + switchW && mouseY >= switchY && mouseY <= switchY + switchH;

            if (mouseX >= contentX && mouseX <= contentX + contentW && mouseY >= currentY && mouseY <= currentY + functionHeight && mouseY >= contentY && mouseY <= contentY + contentH) {
                if (button == 0) {
                    function.toggle();
                    return;
                } else if (button == 1) {
                    function.expanded = !function.expanded;
                    return;
                }
            }

            if (function.expanded && settingsVisible > 0) {
                float sY = currentY + functionHeight;
                int sx = contentX + 8;
                int sw = contentW - 16;
                float bottom = sY + settingsVisible;
                if (mouseX >= contentX && mouseX <= contentX + contentW && mouseY >= sY && mouseY <= bottom && mouseY >= contentY && mouseY <= contentY + contentH) {
                    for (Setting setting : function.getSettings()) {
                        if (!setting.isVisible()) continue;
                        int h = getSettingRendererHeight(setting, sw);
                        if (h <= 0) continue;
                        if (mouseY >= sY && mouseY <= sY + h) {
                            if (setting instanceof BooleanSetting booleanSetting) {
                                if (booleanSettingRenderer.mouseClicked(booleanSetting, mouseX, mouseY, button, sx, (int) sY, sw, h)) return;
                            } else if (setting instanceof BindBooleanSetting bindBooleanSetting) {
                                if (bindbooleanSettingRenderer.mouseClicked(bindBooleanSetting, mouseX, mouseY, button, sx, (int) sY, sw, h)) return;
                            } else if (setting instanceof BindSetting bindSetting) {
                                if (bindSettingRenderer.mouseClicked(bindSetting, mouseX, mouseY, button, sx, (int) sY - 2, sw, h)) return;
                            } else if (setting instanceof ModeSetting modeSetting) {
                                if (modeSettingRenderer.mouseClicked(modeSetting, mouseX, mouseY, button, sx, (int) sY, sw, h)) return;
                            } else if (setting instanceof MultiSetting multiSetting) {
                                if (multiSettingRenderer.mouseClicked(multiSetting, mouseX, mouseY, button, sx, (int) sY, sw, h)) return;
                            } else if (setting instanceof SliderSetting sliderSetting) {
                                if (sliderSettingRenderer.mouseClicked(sliderSetting, mouseX, mouseY, button, sx, (int) sY - 2, sw, h)) {
                                    draggingSlider = sliderSetting;
                                    draggingSliderX = sx;
                                    draggingSliderWidth = sw;
                                    return;
                                }
                            } else if (setting instanceof TextSetting textSetting) {
                                if (textSettingRenderer.mouseClicked(textSetting, mouseX, mouseY, button, sx, (int) sY, sw, h)) return;
                            }
                            return;
                        }
                        sY += h;
                        if (sY > bottom) break;
                    }
                }
            }

            currentY += totalHeight + FUNCTION_GAP;
        }
    }

    private void handleSettingsClicks(double mouseX, double mouseY, int button, int contentX, int contentY, int contentW, int contentH) {
        ru.levin.modules.render.ClickGUI clickGUI = Manager.FUNCTION_MANAGER.clickGUI;
        int itemH = 22;
        int gap = 5;

        float cy = contentY - scrollOffset;

        String[] sections = {"Appearance", "Interface", "Modules", "System"};
        for (String section : sections) {
            int sectionW = contentW - 16;
            cy += 12 + 2;

            int cardY = (int) cy;
            int cardW = sectionW;
            int cardH = itemH;

            if (section.equals("Appearance")) {
                if (handleSettingsRowClick(mouseX, mouseY, button, contentX + 8, cardY, cardW, cardH, () -> {
                    List<String> modes = List.of("Светло-чёрная", "Тёмная");
                    int ci = modes.indexOf(clickGUI.colorGUI.get());
                    int ni = (ci + 1) % modes.size();
                    clickGUI.colorGUI.set(modes.get(ni));
                })) return;
                cy += gap;
                if (handleSettingsRowClick(mouseX, mouseY, button, contentX + 8, (int) cy, cardW, cardH, () -> {
                    List<String> modes = List.of("Колонки", "Категории");
                    int ci = modes.indexOf(clickGUI.guiType.get());
                    int ni = (ci + 1) % modes.size();
                    clickGUI.guiType.set(modes.get(ni));
                })) return;
                cy += gap;
            } else if (section.equals("Interface")) {
                if (handleSettingsRowClick(mouseX, mouseY, button, contentX + 8, cardY, cardW, cardH, () -> clickGUI.blur.set(!clickGUI.blur.get()))) return;
                cy += gap;
                if (handleSettingsRowClick(mouseX, mouseY, button, contentX + 8, (int) cy, cardW, cardH, () -> {
                    List<String> modes = List.of("Клиентский", "Обычный");
                    int ci = modes.indexOf(clickGUI.interfaceStyle.get());
                    int ni = (ci + 1) % modes.size();
                    clickGUI.interfaceStyle.set(modes.get(ni));
                })) return;
                cy += gap;
                cy += itemH + gap;
            } else if (section.equals("Modules")) {
                cy += itemH + gap;
                cy += itemH + gap;
                if (handleSettingsRowClick(mouseX, mouseY, button, contentX + 8, (int) cy, cardW, cardH, () -> clickGUI.strike.set(!clickGUI.strike.get()))) return;
                cy += gap;
                if (handleSettingsRowClick(mouseX, mouseY, button, contentX + 8, (int) cy, cardW, cardH, () -> clickGUI.filling.set(!clickGUI.filling.get()))) return;
                cy += gap;
            } else {
                cy += itemH + gap;
                cy += itemH + gap;
            }

            cy += gap + 4;
        }
    }

    private boolean handleSettingsRowClick(double mouseX, double mouseY, int button, int x, int y, int w, int h, Runnable onClick) {
        if (RenderUtil.isHovered((int) mouseX, (int) mouseY, x, y, w, h)) {
            if (button == 0 && onClick != null) {
                onClick.run();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingSlider != null && button == 0) {
            sliderSettingRenderer.mouseReleased(draggingSlider);
            draggingSlider = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingSlider != null) {
            sliderSettingRenderer.mouseDragged(draggingSlider, mouseX, draggingSliderX, draggingSliderWidth);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int totalWidth = LEFT_WIDTH + PANEL_GAP + RIGHT_WIDTH;
        int startX = (width - totalWidth) / 2;
        int startY = (height - PANEL_HEIGHT) / 2;
        int rightX = startX + LEFT_WIDTH + PANEL_GAP;
        int rightY = startY;

        int tabY = rightY + 10;
        int tabX = rightX + 10;
        int tabW = RIGHT_WIDTH - 20;
        int tabH = 20;

        int contentX = rightX + 6;
        int contentY = rightY + 10 + 20 + 8;
        int contentW = RIGHT_WIDTH - 12;
        int contentH = PANEL_HEIGHT - (contentY - rightY) - 6;

        if (mouseX >= contentX && mouseX <= contentX + contentW && mouseY >= contentY && mouseY <= contentY + contentH) {
            int maxScroll = getMaxScroll(contentW, contentH);
            if (maxScroll > 0) {
                scrollTarget = MathHelper.clamp(scrollTarget - (float) scrollY * SCROLL_SPEED, 0f, (float) maxScroll);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isClose) return true;

        if (keyCode == 256 || keyCode == 340) {
            close();
            return true;
        }

        if (searchState.focused) {
            switch (keyCode) {
                case 259 -> {
                    if (!searchState.text.isEmpty() && searchState.cursorPosition > 0) {
                        searchState.text = searchState.text.substring(0, searchState.cursorPosition - 1) + searchState.text.substring(searchState.cursorPosition);
                        searchState.cursorPosition--;
                    }
                    return true;
                }
                case 263 -> {
                    if (searchState.cursorPosition > 0) searchState.cursorPosition--;
                    return true;
                }
                case 262 -> {
                    if (searchState.cursorPosition < searchState.text.length()) searchState.cursorPosition++;
                    return true;
                }
                case 257, 256 -> {
                    searchState.focused = false;
                    return true;
                }
            }
        }

        for (Function function : Manager.FUNCTION_MANAGER.getFunctions(selectedCategory)) {
            if (!function.expanded) continue;
            for (Setting setting : function.getSettings()) {
                if (setting instanceof TextSetting textSetting && textSetting.isFocused()) {
                    if (textSettingRenderer.keyPressed(textSetting, keyCode, scanCode, modifiers)) {
                        return true;
                    }
                }
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int keyCode) {
        if (searchState.focused) {
            if (searchState.text.length() < 30) {
                String before = searchState.text.substring(0, searchState.cursorPosition);
                String after = searchState.text.substring(searchState.cursorPosition);
                searchState.text = before + c + after;
                searchState.cursorPosition++;
            }
            return true;
        }

        for (Function function : Manager.FUNCTION_MANAGER.getFunctions(selectedCategory)) {
            if (!function.expanded) continue;
            for (Setting setting : function.getSettings()) {
                if (setting instanceof TextSetting textSetting && textSetting.isFocused()) {
                    if (textSettingRenderer.charTyped(textSetting, c, keyCode)) {
                        return true;
                    }
                }
            }
        }

        return super.charTyped(c, keyCode);
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
        if (isClose) return;
        isClose = true;
        animationOpen.setDirection(Direction.AxisDirection.NEGATIVE);
        animationOpen.reset();
    }

    @Override
    public void renderBackground(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private int getMaxScroll(int contentW, int contentH) {
        if (rightTab == RightTab.MODULES) {
            return calculateMaxScroll(contentW, contentH);
        }
        return getSettingsTabMaxScroll(contentW, contentH);
    }

    private int getSettingsTabMaxScroll(int contentW, int contentH) {
        int total = 0;
        String[] sections = {"Appearance", "Interface", "Modules", "System"};
        int itemH = 22;
        int gap = 5;
        int sectionLabel = 12;
        int sectionPad = 6;
        for (String section : sections) {
            total += sectionLabel + sectionPad;
            int rows = switch (section) {
                case "Appearance" -> 2;
                case "Interface" -> 3;
                case "Modules" -> 4;
                default -> 2;
            };
            total += rows * (itemH + gap);
        }
        return Math.max(0, total - contentH);
    }

    private boolean isFunctionVisible(Function function) {
        String searchTextLower = searchState.text.toLowerCase();
        return searchTextLower.isEmpty() || function.name.toLowerCase().contains(searchTextLower) || function.keywords.toLowerCase().contains(searchTextLower);
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

    private float updateToggleAnimation(Function f) {
        float target = f.state ? 1f : 0f;
        float prog = toggleProgress.getOrDefault(f, target);
        prog = MathUtil.lerp(prog, target, 16f);
        if (Math.abs(target - prog) < 0.001f) prog = target;
        toggleProgress.put(f, prog);
        return prog;
    }

    private int calculateMaxScroll(int listWidth, int listHeight) {
        int totalHeight = 0;
        List<Function> functions = getVisibleFunctions();
        for (Function f : functions) {
            int functionHeight = 34;
            int settingsFull = computeSettingsHeight(f, listWidth - 16);
            float prog = expandProgress.getOrDefault(f, f.expanded ? 1f : 0f);
            float eased = easeInOut(prog);
            int settingsVisible = (int) Math.round(settingsFull * eased);
            totalHeight += functionHeight + settingsVisible + FUNCTION_GAP;
        }
        int maxScroll = totalHeight - listHeight;
        return Math.max(0, maxScroll);
    }

    private int calculateMaxScroll(int listHeight) {
        return calculateMaxScroll(RIGHT_WIDTH - 12, listHeight);
    }

    private List<Function> getVisibleFunctions() {
        List<Function> visible = new ArrayList<>();
        boolean globalSearch = !searchState.text.isEmpty();
        List<Function> source = globalSearch
                ? Manager.FUNCTION_MANAGER.getFunctions()
                : Manager.FUNCTION_MANAGER.getFunctions(selectedCategory);
        for (Function f : source) {
            if (isFunctionVisible(f)) visible.add(f);
        }
        return visible;
    }

    private int computeSettingsHeight(Function f, int width) {
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
                settingsHeight += modeSettingRenderer.getHeight((ModeSetting) setting, width);
            } else if (setting instanceof MultiSetting) {
                settingsHeight += multiSettingRenderer.getHeight((MultiSetting) setting, width);
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
}
