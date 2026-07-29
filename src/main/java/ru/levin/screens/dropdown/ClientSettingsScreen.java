package ru.levin.screens.dropdown;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import ru.levin.manager.ClientManager;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.manager.themeManager.Style;
import ru.levin.modules.Function;
import ru.levin.manager.fontManager.FontUtils;
import ru.levin.util.color.ColorUtil;
import ru.levin.util.math.MathUtil;
import ru.levin.util.render.RenderAddon;
import ru.levin.util.render.RenderUtil;
import ru.levin.util.render.Scissor;
import ru.levin.util.animations.impl.EaseInOutQuad;

import java.awt.*;
import java.util.List;

import static ru.levin.util.render.RenderUtil.drawBlur;

public class ClientSettingsScreen extends Screen implements IMinecraft {

    private enum Tab {
        THEME("Theme"),
        BINDS("Binds"),
        CONFIGS("Configs"),
        ABOUT("About");

        final String title;

        Tab(String title) {
            this.title = title;
        }
    }

    private final Screen parent;

    private boolean isClose;
    private double animation;
    private final EaseInOutQuad animationOpen = new EaseInOutQuad(220, 1);

    private Tab tab = Tab.THEME;

    private Tab prevTab = Tab.THEME;
    private float tabSwitchAnim = 1.0f;
    private final float[] tabHoverAnim = new float[Tab.values().length];
    private float closeHoverAnim = 0.0f;

    private float scrollOffset = 0f;
    private float scrollTarget = 0f;
    private float scrollVelocity = 0f;

    private Function bindListening = null;
    private boolean bindListeningActive = false;

    private String bindsSearchText = "";
    private int bindsSearchCursor = 0;
    private boolean bindsSearchFocused = false;
    private boolean bindsOnlyBound = false;

    private String cfgNameText = "";
    private int cfgNameCursor = 0;
    private boolean cfgNameFocused = false;

    public ClientSettingsScreen(Screen parent) {
        super(Text.literal("Client Settings"));
        this.parent = parent;
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
            if (parent != null) mc.setScreen(parent);
            else super.close();
            return;
        }
        if (animation <= 0.01) return;

        super.render(ctx, mouseX, mouseY, delta);
        ctx.getMatrices().push();

        int w = 320;
        int h = 220;
        int x = (ctx.getScaledWindowWidth() - w) / 2;
        int y = (ctx.getScaledWindowHeight() - h) / 2;

        RenderAddon.sizeAnimation(ctx.getMatrices(), ctx.getScaledWindowWidth() / 2f, ctx.getScaledWindowHeight() / 2f, animation);

        int themeBase = Manager.STYLE_MANAGER.getFirstColor();
        int bg = new Color(18, 18, 24, 210).getRGB();

        if (Manager.FUNCTION_MANAGER != null && Manager.FUNCTION_MANAGER.clickGUI != null
                && Manager.FUNCTION_MANAGER.clickGUI.blur.get() && Manager.FUNCTION_MANAGER.clickGUI.blurSetting.get("Панели")) {
            drawBlur(ctx.getMatrices(), x, y, w, h, 10, 8, -1);
        }

        RenderUtil.drawRoundedRect(ctx.getMatrices(), x, y, w, h, 10, bg);
        RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, w, h, 10, 0.9f, ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.08f));

        int topTint1 = ColorUtil.applyAlpha(themeBase, 0.10f);
        int topTint2 = ColorUtil.applyAlpha(themeBase, 0.00f);
        RenderUtil.rectRGB(ctx.getMatrices(), x + 1, y + 1, w - 2, 44, 10,
                topTint1, topTint1, topTint2, topTint2);
        RenderUtil.drawRoundedRect(ctx.getMatrices(), x + 12, y + 30, w - 24, 1, 1, ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f));

        FontUtils.sf_bold[18].drawLeftAligned(ctx.getMatrices(), "Settings", x + 12, y + 10, Color.WHITE.getRGB());

        int closeW = 18;
        int closeH = 14;
        int closeX = x + w - 12 - closeW;
        int closeY = y + 10;
        boolean closeHovered = RenderUtil.isHovered(mouseX, mouseY, closeX, closeY, closeW, closeH);
        closeHoverAnim = MathUtil.lerp(closeHoverAnim, closeHovered ? 1.0f : 0.0f, 14f);
        int closeBg = closeHovered ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.09f) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.05f);
        ctx.getMatrices().push();
        float cx = closeX + closeW / 2f;
        float cyClose = closeY + closeH / 2f;
        float closeScale = 1.0f + 0.08f * closeHoverAnim;
        ctx.getMatrices().translate(cx, cyClose, 0.0);
        ctx.getMatrices().scale(closeScale, closeScale, 1.0f);
        ctx.getMatrices().translate(-cx, -cyClose, 0.0);
        RenderUtil.drawRoundedRect(ctx.getMatrices(), closeX, closeY, closeW, closeH, 5, closeBg);
        RenderUtil.drawRoundedBorder(ctx.getMatrices(), closeX, closeY, closeW, closeH, 5, 0.8f,
                closeHovered ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.14f) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.08f));
        FontUtils.sf_medium[16].centeredDraw(ctx.getMatrices(), "X", closeX + closeW / 2f, closeY + 2,
                ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.85f + 0.15f * closeHoverAnim));
        ctx.getMatrices().pop();

        int headerH = 34;
        int sidebarW = 86;
        int sidebarX = x + 10;
        int sidebarY = y + headerH + 10;
        int sidebarH = h - headerH - 20;

        int sideTop = ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.045f);
        int sideBot = ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.018f);
        RenderUtil.rectRGB(ctx.getMatrices(), sidebarX, sidebarY, sidebarW, sidebarH, 8, sideTop, sideTop, sideBot, sideBot);
        RenderUtil.drawRoundedBorder(ctx.getMatrices(), sidebarX, sidebarY, sidebarW, sidebarH, 8, 0.9f,
                ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f));

        int tabX = sidebarX + 6;
        int tabY = sidebarY + 8;
        int tabW = sidebarW - 12;
        int tabH = 20;
        int tabGap = 6;

        int i = 0;
        for (Tab t : Tab.values()) {
            int ty = tabY + i * (tabH + tabGap);
            boolean selected = t == tab;
            boolean hovered = RenderUtil.isHovered(mouseX, mouseY, tabX, ty, tabW, tabH);

            tabHoverAnim[i] = MathUtil.lerp(tabHoverAnim[i], hovered ? 1.0f : 0.0f, 16f);
            float ha = tabHoverAnim[i];

            ctx.getMatrices().push();
            float txc = tabX + tabW / 2f;
            float tyc = ty + tabH / 2f;
            float tScale = 1.0f + 0.05f * ha;
            ctx.getMatrices().translate(txc, tyc, 0.0);
            ctx.getMatrices().scale(tScale, tScale, 1.0f);
            ctx.getMatrices().translate(-txc, -tyc, 0.0);

            int b1 = new Color(0, 0, 0, selected ? 92 : (int) (62 + ha * 14)).getRGB();
            int b2 = new Color(0, 0, 0, selected ? 66 : (int) (50 + ha * 10)).getRGB();
            RenderUtil.rectRGB(ctx.getMatrices(), tabX, ty, tabW, tabH, 7, b1, b1, b2, b2);
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), tabX, ty, tabW, tabH, 7, 0.9f,
                    selected ? ColorUtil.reAlphaInt(themeBase, 155)
                            : hovered
                                    ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f + 0.05f * ha)
                                    : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f));

            if (selected) {
                RenderUtil.drawRoundedRect(ctx.getMatrices(), tabX + 4, ty + 4, 2, tabH - 8, 2, ColorUtil.reAlphaInt(themeBase, 180));
            }

            int tc = selected ? Color.WHITE.getRGB() : new Color(190, 190, 205).getRGB();
            FontUtils.sf_medium[16].centeredDraw(ctx.getMatrices(), t.title, tabX + tabW / 2f, ty + 5, tc);

            ctx.getMatrices().pop();
            i++;
        }

        int contentX = sidebarX + sidebarW + 10;
        int contentY = sidebarY;
        int contentW = (x + w - 10) - contentX;
        int contentH = sidebarH;

        int panelTop = ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.04f);
        int panelBot = ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.015f);
        RenderUtil.rectRGB(ctx.getMatrices(), contentX, contentY, contentW, contentH, 8,
                panelTop, panelTop, panelBot, panelBot);
        RenderUtil.drawRoundedBorder(ctx.getMatrices(), contentX, contentY, contentW, contentH, 8, 0.9f,
                ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f));

        int maxScroll = getMaxScroll(contentW, contentH);

        // inertial scrolling
        if (Math.abs(scrollVelocity) > 0.01f) {
            scrollTarget += scrollVelocity;
            scrollVelocity *= 0.82f;
        } else {
            scrollVelocity = 0f;
        }

        scrollTarget = MathHelper.clamp(scrollTarget, 0f, (float) maxScroll);
        if (scrollTarget <= 0f || scrollTarget >= (float) maxScroll) {
            scrollVelocity = 0f;
        }
        scrollOffset = MathUtil.lerp(scrollOffset, scrollTarget, 16f);
        scrollOffset = MathHelper.clamp(scrollOffset, 0f, (float) maxScroll);

        ctx.getMatrices().push();
        Scissor.push();
        Scissor.setFromComponentCoordinates(contentX, contentY, contentW, contentH);

        tabSwitchAnim = MathUtil.lerp(tabSwitchAnim, 1.0f, 12f);
        float slide = (1.0f - tabSwitchAnim) * 8.0f;
        ctx.getMatrices().translate(slide, 0.0, 0.0);

        float cy = contentY - scrollOffset;
        if (tab == Tab.THEME) {
            cy = renderThemes(ctx, mouseX, mouseY, contentX, (int) cy, contentW);
        } else if (tab == Tab.BINDS) {
            cy = renderBinds(ctx, mouseX, mouseY, contentX, (int) cy, contentW);
        } else if (tab == Tab.CONFIGS) {
            cy = renderConfigs(ctx, mouseX, mouseY, contentX, (int) cy, contentW);
        } else {
            cy = renderAbout(ctx, mouseX, mouseY, contentX, (int) cy, contentW);
        }

        Scissor.pop();
        ctx.getMatrices().pop();

        drawScrollBar(ctx, mouseX, mouseY, contentX, contentY, contentW, contentH, maxScroll);

        ctx.getMatrices().pop();
    }

    private void drawScrollBar(DrawContext ctx, int mouseX, int mouseY, int x, int y, int w, int h, int maxScroll) {
        if (maxScroll <= 1) return;
        int trackW = 3;
        int trackX = x + w - trackW;
        int trackY = y;
        int trackH = h;

        int track = ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f);
        RenderUtil.drawRoundedRect(ctx.getMatrices(), trackX, trackY, trackW, trackH, 2, track);

        float visible = h;
        float total = h + maxScroll;
        float ratio = MathHelper.clamp(visible / total, 0.10f, 1.0f);
        int thumbH = Math.max(10, (int) (trackH * ratio));
        float p = MathHelper.clamp(scrollOffset / (float) maxScroll, 0.0f, 1.0f);
        int thumbY = trackY + (int) ((trackH - thumbH) * p);

        boolean hov = RenderUtil.isHovered(mouseX, mouseY, trackX - 2, thumbY - 2, trackW + 4, thumbH + 4);
        int thumb = hov ? ColorUtil.applyAlpha(Manager.STYLE_MANAGER.getFirstColor(), 0.60f) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.18f);
        RenderUtil.drawRoundedRect(ctx.getMatrices(), trackX, thumbY, trackW, thumbH, 2, thumb);
    }

    private int getMaxScroll(int contentW, int contentH) {
        if (tab == Tab.THEME) {
            int count = Manager.STYLE_MANAGER.getStyles().size();
            int itemH = 18;
            int gap = 4;
            int total = count * (itemH + gap);
            return Math.max(0, total - contentH);
        }
        if (tab == Tab.BINDS) {
            int count = 0;
            for (Function f : Manager.FUNCTION_MANAGER.getFunctions()) {
                if (isBindVisibleBySearch(f)) count++;
            }
            int itemH = 18;
            int gap = 4;
            int header = 18 + 10; // search row + gap
            int total = header + count * (itemH + gap);
            return Math.max(0, total - contentH);
        }
        if (tab == Tab.ABOUT) {
            return 0;
        }
        // configs
        int count = Manager.CONFIG_MANAGER.getAllConfigurations().size();
        int itemH = 18;
        int gap = 4;
        int total = 60 + count * (itemH + gap);
        return Math.max(0, total - contentH);
    }

    private float renderAbout(DrawContext ctx, int mouseX, int mouseY, int x, int y, int w) {
        int themeBase = Manager.STYLE_MANAGER.getFirstColor();

        int cardH = 18;
        int gap = 6;
        int bg = new Color(0, 0, 0, 55).getRGB();

        String themeName = Manager.STYLE_MANAGER.getTheme() != null ? Manager.STYLE_MANAGER.getTheme().name : "None";
        String currentCfg = Manager.CONFIG_MANAGER.getCurrentConfig();
        if (currentCfg == null) currentCfg = "None";

        int modules = Manager.FUNCTION_MANAGER != null ? Manager.FUNCTION_MANAGER.getFunctions().size() : 0;
        int cfgs = Manager.CONFIG_MANAGER != null ? Manager.CONFIG_MANAGER.getAllConfigurations().size() : 0;

        y = drawAboutRow(ctx, x, y, w, cardH, bg, themeBase, "Theme", themeName);
        y += gap;
        y = drawAboutRow(ctx, x, y, w, cardH, bg, themeBase, "Modules", String.valueOf(modules));
        y += gap;
        y = drawAboutRow(ctx, x, y, w, cardH, bg, themeBase, "Configs", String.valueOf(cfgs));
        y += gap;
        y = drawAboutRow(ctx, x, y, w, cardH, bg, themeBase, "Current", currentCfg);

        return y;
    }

    private int drawAboutRow(DrawContext ctx, int x, int y, int w, int h, int bg, int themeBase, String k, String v) {
        int c1 = new Color(0, 0, 0, 72).getRGB();
        int c2 = new Color(0, 0, 0, 50).getRGB();
        RenderUtil.rectRGB(ctx.getMatrices(), x, y, w, h, 7, c1, c1, c2, c2);
        RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, w, h, 7, 0.9f, ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.07f));

        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), k, x + 10, y + 4, new Color(190, 190, 205).getRGB());

        int valueC = Color.WHITE.getRGB();
        float valueW = FontUtils.sf_medium[16].getWidth(v);
        float valueX = x + w - 10 - valueW;
        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), v, valueX, y + 4, valueC);

        RenderUtil.drawRoundedRect(ctx.getMatrices(), x + 2, y + 5, 2, h - 10, 2, ColorUtil.reAlphaInt(themeBase, 170));
        return y + h;
    }

    private float renderThemes(DrawContext ctx, int mouseX, int mouseY, int x, int y, int w) {
        int themeBase = Manager.STYLE_MANAGER.getFirstColor();
        int itemH = 18;
        int gap = 4;

        List<Style> styles = Manager.STYLE_MANAGER.getStyles();
        for (Style s : styles) {
            boolean selected = Manager.STYLE_MANAGER.getTheme() == s;
            boolean hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, w, itemH);

            int baseTop = new Color(0, 0, 0, selected ? 92 : (hovered ? 76 : 66)).getRGB();
            int baseBot = new Color(0, 0, 0, selected ? 66 : (hovered ? 58 : 50)).getRGB();
            RenderUtil.rectRGB(ctx.getMatrices(), x, y, w, itemH, 7, baseTop, baseTop, baseBot, baseBot);

            int br;
            if (selected) {
                br = ColorUtil.reAlphaInt(themeBase, 160);
            } else if (hovered) {
                br = ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.11f);
            } else {
                br = ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f);
            }
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, w, itemH, 7, 0.9f, br);

            int nameC = selected ? Color.WHITE.getRGB() : new Color(190, 190, 205).getRGB();
            FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), s.name, x + 10, y + 4, nameC);

            int c1 = s.colors.length > 0 ? s.colors[0] : Color.WHITE.getRGB();
            int c2 = s.colors.length > 1 ? s.colors[1] : c1;
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x + w - 38, y + 5, 14, 8, 3, ColorUtil.reAlphaInt(c1, 255));
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x + w - 20, y + 5, 14, 8, 3, ColorUtil.reAlphaInt(c2, 255));

            if (selected) {
                RenderUtil.drawRoundedRect(ctx.getMatrices(), x + 2, y + 5, 2, itemH - 10, 2, ColorUtil.reAlphaInt(themeBase, 180));
            }

            y += itemH + gap;
        }

        return y;
    }

    private boolean isBindVisibleBySearch(Function f) {
        if (f == null || f.name == null) return false;
        if (bindsOnlyBound && f.getBindCode() == 0) return false;
        if (bindsSearchText == null || bindsSearchText.isEmpty()) return true;
        return f.name.toLowerCase().contains(bindsSearchText.toLowerCase());
    }

    private float renderBinds(DrawContext ctx, int mouseX, int mouseY, int x, int y, int w) {
        int themeBase = Manager.STYLE_MANAGER.getFirstColor();
        int itemH = 18;
        int gap = 4;

        // search row
        {
            int fieldH = 18;
            int btnW = 44;
            int gapBtn = 6;
            int fieldW = w - (btnW + gapBtn + btnW + 8);
            int bgTop = new Color(0, 0, 0, 72).getRGB();
            int bgBot = new Color(0, 0, 0, 50).getRGB();
            RenderUtil.rectRGB(ctx.getMatrices(), x, y, fieldW, fieldH, 7, bgTop, bgTop, bgBot, bgBot);

            boolean hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, fieldW, fieldH);
            int border = bindsSearchFocused
                    ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.16f)
                    : hovered ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.10f) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f);
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, fieldW, fieldH, 7, 0.8f, border);

            String text = bindsSearchText;
            if (bindsSearchFocused) {
                int pos = Math.min(bindsSearchCursor, text.length());
                text = text.substring(0, pos) + (System.currentTimeMillis() / 450L % 2L == 0 ? "|" : "") + text.substring(pos);
            }
            RenderUtil.drawTexture(ctx.getMatrices(), "images/gui/search.png", x + 8, y + 4, 10, 10, 1, ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.9f));
            FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), text.isEmpty() && !bindsSearchFocused ? "Search" : text, x + 22, y + 4,
                    text.isEmpty() && !bindsSearchFocused ? new Color(255, 255, 255, 140).getRGB() : Color.WHITE.getRGB());

            int btnX = x + fieldW + 8;

            RenderUtil.rectRGB(ctx.getMatrices(), btnX, y, btnW, fieldH, 7, bgTop, bgTop, bgBot, bgBot);
            boolean fh = RenderUtil.isHovered(mouseX, mouseY, btnX, y, btnW, fieldH);
            int fcol;
            if (bindsOnlyBound) {
                fcol = ColorUtil.reAlphaInt(themeBase, fh ? 180 : 150);
            } else {
                fcol = fh ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.12f) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f);
            }
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), btnX, y, btnW, fieldH, 7, 0.8f, fcol);
            FontUtils.sf_medium[16].centeredDraw(ctx.getMatrices(), "Bound", btnX + btnW / 2f, y + 4,
                    bindsOnlyBound ? Color.WHITE.getRGB() : new Color(210, 210, 225).getRGB());

            int clearX = btnX + btnW + gapBtn;
            RenderUtil.rectRGB(ctx.getMatrices(), clearX, y, btnW, fieldH, 7, bgTop, bgTop, bgBot, bgBot);
            boolean bh = RenderUtil.isHovered(mouseX, mouseY, clearX, y, btnW, fieldH);
            int bcol = bh ? ColorUtil.reAlphaInt(themeBase, 120) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f);
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), clearX, y, btnW, fieldH, 7, 0.8f, bcol);
            FontUtils.sf_medium[16].centeredDraw(ctx.getMatrices(), "Clear", clearX + btnW / 2f, y + 4, Color.WHITE.getRGB());

            y += fieldH + 10;
        }

        for (Function f : Manager.FUNCTION_MANAGER.getFunctions()) {
            if (!isBindVisibleBySearch(f)) continue;
            boolean hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, w, itemH);

            int rowTop = new Color(0, 0, 0, hovered ? 76 : 66).getRGB();
            int rowBot = new Color(0, 0, 0, hovered ? 58 : 50).getRGB();
            RenderUtil.rectRGB(ctx.getMatrices(), x, y, w, itemH, 7, rowTop, rowTop, rowBot, rowBot);
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, w, itemH, 7, 0.9f,
                    hovered ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.11f) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f));

            FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), f.name, x + 10, y + 4, new Color(210, 210, 225).getRGB());

            String keyText;
            if (bindListeningActive && bindListening == f) {
                keyText = "...";
            } else {
                int code = f.getBindCode();
                keyText = code == 0 ? "None" : ClientManager.getKey(code);
            }

            int keyW = (int) FontUtils.sf_medium[16].getWidth(keyText) + 14;
            int bx = x + w - keyW - 8;
            int by = y + 3;
            int bh = itemH - 6;
            int pillTop = ColorUtil.applyAlpha(ColorUtil.reAlphaInt(themeBase, 220), (bindListeningActive && bindListening == f) ? 0.28f : 0.20f);
            int pillBot = ColorUtil.applyAlpha(ColorUtil.reAlphaInt(themeBase, 220), (bindListeningActive && bindListening == f) ? 0.18f : 0.14f);
            RenderUtil.rectRGB(ctx.getMatrices(), bx, by, keyW, bh, 6, pillTop, pillTop, pillBot, pillBot);
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), bx, by, keyW, bh, 6, 0.9f, ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.08f));

            int fontH = (int) FontUtils.sf_medium[16].getHeight();
            int textY = by + Math.max(0, (bh - fontH) / 2);
            FontUtils.sf_medium[16].centeredDraw(ctx.getMatrices(), keyText, bx + keyW / 2f, textY, Color.WHITE.getRGB());

            y += itemH + gap;
        }

        return y;
    }

    private float renderConfigs(DrawContext ctx, int mouseX, int mouseY, int x, int y, int w) {
        int themeBase = Manager.STYLE_MANAGER.getFirstColor();

        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), "Name", x + 10, y + 4, new Color(190, 190, 205).getRGB());
        y += 18;

        // name field
        {
            int fieldH = 18;
            int bgTop = new Color(0, 0, 0, 72).getRGB();
            int bgBot = new Color(0, 0, 0, 50).getRGB();
            RenderUtil.rectRGB(ctx.getMatrices(), x, y, w, fieldH, 7, bgTop, bgTop, bgBot, bgBot);

            boolean hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, w, fieldH);
            int border = cfgNameFocused
                    ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.16f)
                    : hovered ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.10f) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f);
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, w, fieldH, 7, 0.8f, border);

            String text = cfgNameText;
            if (cfgNameFocused) {
                int pos = Math.min(cfgNameCursor, text.length());
                text = text.substring(0, pos) + (System.currentTimeMillis() / 450L % 2L == 0 ? "|" : "") + text.substring(pos);
            }
            FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), text.isEmpty() && !cfgNameFocused ? "config" : text, x + 10, y + 4,
                    text.isEmpty() && !cfgNameFocused ? new Color(255, 255, 255, 140).getRGB() : Color.WHITE.getRGB());

            y += fieldH + 10;
        }

        int btnH = 18;
        int btnW = (w - 10) / 2;

        int saveX = x;
        int loadX = x + btnW + 10;
        int btnY = y;

        renderButton(ctx, mouseX, mouseY, saveX, btnY, btnW, btnH, "Save", () -> {
            String name = cfgNameText.trim();
            if (name.isEmpty()) return;
            Manager.CONFIG_MANAGER.saveConfiguration(name);
        });

        renderButton(ctx, mouseX, mouseY, loadX, btnY, btnW, btnH, "Load", () -> {
            String name = cfgNameText.trim();
            if (name.isEmpty()) return;
            Manager.CONFIG_MANAGER.loadConfiguration(name, false);
        });

        y += btnH + 10;

        List<String> cfgs = Manager.CONFIG_MANAGER.getAllConfigurations();
        int itemH = 18;
        int gap = 4;

        for (String cfg : cfgs) {
            boolean hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, w, itemH);
            int rowTop = new Color(0, 0, 0, hovered ? 76 : 66).getRGB();
            int rowBot = new Color(0, 0, 0, hovered ? 58 : 50).getRGB();
            RenderUtil.rectRGB(ctx.getMatrices(), x, y, w, itemH, 7, rowTop, rowTop, rowBot, rowBot);
            RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, w, itemH, 7, 0.9f,
                    hovered ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.11f) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.06f));

            boolean current = cfg.equalsIgnoreCase(Manager.CONFIG_MANAGER.getCurrentConfig());
            int nameC = current ? Color.WHITE.getRGB() : new Color(210, 210, 225).getRGB();
            FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), cfg, x + 10, y + 4, nameC);

            int delSize = 10;
            int delX = x + w - delSize - 8;
            int delY = y + 4;
            RenderUtil.drawTexture(ctx.getMatrices(), "images/gui/trash.png", delX, delY, delSize, delSize, 1, ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.85f));

            int ind = current ? ColorUtil.reAlphaInt(themeBase, 160) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.05f);
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x + 2, y + 5, 2, itemH - 10, 2, ind);

            y += itemH + gap;
        }

        return y;
    }

    private void renderButton(DrawContext ctx, int mouseX, int mouseY, int x, int y, int w, int h, String title, Runnable onClick) {
        int themeBase = Manager.STYLE_MANAGER.getFirstColor();
        boolean hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, w, h);
        int bgTop = new Color(0, 0, 0, hovered ? 82 : 72).getRGB();
        int bgBot = new Color(0, 0, 0, hovered ? 62 : 50).getRGB();
        RenderUtil.rectRGB(ctx.getMatrices(), x, y, w, h, 7, bgTop, bgTop, bgBot, bgBot);
        int border = hovered ? ColorUtil.reAlphaInt(themeBase, 140) : ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.07f);
        RenderUtil.drawRoundedBorder(ctx.getMatrices(), x, y, w, h, 7, 0.9f, border);
        FontUtils.sf_medium[16].centeredDraw(ctx.getMatrices(), title, x + w / 2f, y + 4, Color.WHITE.getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int w = 320;
        int h = 220;
        int x = (width - w) / 2;
        int y = (height - h) / 2;

        int closeW = 18;
        int closeH = 14;
        int closeX = x + w - 12 - closeW;
        int closeY = y + 10;
        if (RenderUtil.isHovered((int) mouseX, (int) mouseY, closeX, closeY, closeW, closeH)) {
            close();
            return true;
        }

        int headerH = 34;
        int sidebarW = 86;
        int sidebarX = x + 10;
        int sidebarY = y + headerH + 10;

        int tabX = sidebarX + 6;
        int tabY = sidebarY + 8;
        int tabW = sidebarW - 12;
        int tabH = 20;
        int tabGap = 6;

        int i = 0;
        for (Tab t : Tab.values()) {
            int ty = tabY + i * (tabH + tabGap);
            if (RenderUtil.isHovered((int) mouseX, (int) mouseY, tabX, ty, tabW, tabH)) {
                if (tab != t) {
                    prevTab = tab;
                    tab = t;
                    tabSwitchAnim = 0.0f;
                }
                scrollOffset = 0f;
                scrollTarget = 0f;
                bindListening = null;
                bindListeningActive = false;
                cfgNameFocused = false;
                bindsSearchFocused = false;
                return true;
            }
            i++;
        }

        int contentX = sidebarX + sidebarW + 10;
        int contentY = sidebarY;
        int contentW = (x + w - 10) - contentX;

        int localX = (int) mouseX;
        int localY = (int) mouseY;

        if (tab == Tab.THEME) {
            int itemH = 18;
            int gap = 4;
            int cy = (int) (contentY - scrollOffset);
            List<Style> styles = Manager.STYLE_MANAGER.getStyles();
            for (Style s : styles) {
                if (RenderUtil.isHovered(localX, localY, contentX, cy, contentW, itemH)) {
                    Manager.STYLE_MANAGER.setTheme(s);
                    return true;
                }
                cy += itemH + gap;
            }
        } else if (tab == Tab.BINDS) {
            int itemH = 18;
            int gap = 4;
            int cy = (int) (contentY - scrollOffset);

            int fieldH = 18;
            int btnW = 44;
            int gapBtn = 6;
            int fieldW = contentW - (btnW + gapBtn + btnW + 8);
            if (RenderUtil.isHovered(localX, localY, contentX, cy, fieldW, fieldH)) {
                bindsSearchFocused = true;
                cfgNameFocused = false;
                bindsSearchCursor = bindsSearchText.length();
                return true;
            }

            int btnX = contentX + fieldW + 8;
            if (RenderUtil.isHovered(localX, localY, btnX, cy, btnW, fieldH)) {
                bindsOnlyBound = !bindsOnlyBound;
                return true;
            }

            int clearX = btnX + btnW + gapBtn;
            if (RenderUtil.isHovered(localX, localY, clearX, cy, btnW, fieldH)) {
                for (Function f : Manager.FUNCTION_MANAGER.getFunctions()) {
                    if (f != null) f.setBindCode(0);
                }
                bindListening = null;
                bindListeningActive = false;
                return true;
            }

            cy += fieldH + 10;

            for (Function f : Manager.FUNCTION_MANAGER.getFunctions()) {
                if (!isBindVisibleBySearch(f)) continue;
                if (RenderUtil.isHovered(localX, localY, contentX, cy, contentW, itemH)) {
                    // click on key pill area to listen
                    String keyText = f.getBindCode() == 0 ? "None" : ClientManager.getKey(f.getBindCode());
                    if (bindListeningActive && bindListening == f) keyText = "...";

                    int keyW = (int) FontUtils.sf_medium[16].getWidth(keyText) + 14;
                    int bx = contentX + contentW - keyW - 8;
                    int by = cy + 3;
                    int bh = itemH - 6;
                    if (RenderUtil.isHovered(localX, localY, bx, by, keyW, bh)) {
                        bindListening = f;
                        bindListeningActive = true;
                    }
                    return true;
                }
                cy += itemH + gap;
            }
        } else {
            int labelH = 18;
            int fieldY = contentY + labelH;
            int fieldH = 18;
            if (RenderUtil.isHovered(localX, localY, contentX, fieldY, contentW, fieldH)) {
                cfgNameFocused = true;
                cfgNameCursor = cfgNameText.length();
                return true;
            }

            int btnY = fieldY + fieldH + 10;
            int btnH = 18;
            int btnW = (contentW - 10) / 2;
            if (RenderUtil.isHovered(localX, localY, contentX, btnY, btnW, btnH)) {
                String name = cfgNameText.trim();
                if (!name.isEmpty()) Manager.CONFIG_MANAGER.saveConfiguration(name);
                return true;
            }
            if (RenderUtil.isHovered(localX, localY, contentX + btnW + 10, btnY, btnW, btnH)) {
                String name = cfgNameText.trim();
                if (!name.isEmpty()) Manager.CONFIG_MANAGER.loadConfiguration(name, false);
                return true;
            }

            int listY = btnY + btnH + 10;
            int itemH = 18;
            int gap = 4;
            int cy = (int) (listY - scrollOffset);
            List<String> cfgs = Manager.CONFIG_MANAGER.getAllConfigurations();
            for (String cfg : cfgs) {
                if (RenderUtil.isHovered(localX, localY, contentX, cy, contentW, itemH)) {
                    int delSize = 10;
                    int delX = contentX + contentW - delSize - 8;
                    int delY = cy + 4;
                    if (RenderUtil.isHovered(localX, localY, delX, delY, delSize, delSize)) {
                        Manager.CONFIG_MANAGER.deleteConfig(cfg);
                        return true;
                    }
                    cfgNameText = cfg;
                    cfgNameCursor = cfgNameText.length();
                    return true;
                }
                cy += itemH + gap;
            }

            cfgNameFocused = false;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scrollVelocity += (float) (-scrollY) * 10.5f;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }

        if (bindListeningActive && bindListening != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                bindListening.setBindCode(0);
            } else {
                bindListening.setBindCode(keyCode);
            }
            bindListening = null;
            bindListeningActive = false;
            return true;
        }

        if (bindsSearchFocused) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE -> {
                    if (bindsSearchCursor > 0 && !bindsSearchText.isEmpty()) {
                        bindsSearchText = bindsSearchText.substring(0, bindsSearchCursor - 1) + bindsSearchText.substring(bindsSearchCursor);
                        bindsSearchCursor--;
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_LEFT -> {
                    if (bindsSearchCursor > 0) bindsSearchCursor--;
                    return true;
                }
                case GLFW.GLFW_KEY_RIGHT -> {
                    if (bindsSearchCursor < bindsSearchText.length()) bindsSearchCursor++;
                    return true;
                }
                case GLFW.GLFW_KEY_ENTER -> {
                    bindsSearchFocused = false;
                    return true;
                }
            }
        }

        if (cfgNameFocused) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE -> {
                    if (cfgNameCursor > 0 && !cfgNameText.isEmpty()) {
                        cfgNameText = cfgNameText.substring(0, cfgNameCursor - 1) + cfgNameText.substring(cfgNameCursor);
                        cfgNameCursor--;
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_LEFT -> {
                    if (cfgNameCursor > 0) cfgNameCursor--;
                    return true;
                }
                case GLFW.GLFW_KEY_RIGHT -> {
                    if (cfgNameCursor < cfgNameText.length()) cfgNameCursor++;
                    return true;
                }
                case GLFW.GLFW_KEY_ENTER -> {
                    cfgNameFocused = false;
                    return true;
                }
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int keyCode) {
        if (bindsSearchFocused) {
            if (bindsSearchText.length() < 24 && c >= 32 && c != 127) {
                String before = bindsSearchText.substring(0, bindsSearchCursor);
                String after = bindsSearchText.substring(bindsSearchCursor);
                bindsSearchText = before + c + after;
                bindsSearchCursor++;
            }
            return true;
        }
        if (cfgNameFocused) {
            if (cfgNameText.length() < 24 && c >= 32 && c != 127) {
                String before = cfgNameText.substring(0, cfgNameCursor);
                String after = cfgNameText.substring(cfgNameCursor);
                cfgNameText = before + c + after;
                cfgNameCursor++;
            }
            return true;
        }
        return super.charTyped(c, keyCode);
    }

    @Override
    public void close() {
        if (isClose) return;
        isClose = true;
        animationOpen.setDirection(Direction.AxisDirection.NEGATIVE);
        animationOpen.reset();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    }
}
