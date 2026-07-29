package ru.levin.screens.mainmenu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ru.levin.manager.fontManager.FontUtils;
import ru.levin.util.render.RenderUtil;

import java.awt.Color;

@SuppressWarnings("All")
public class MainMenu extends Screen {

    private static final Identifier LOGO_TEX = Identifier.of("sodiumextra", "images/logo/lovan.png");
    private static final int BG_COLOR = new Color(0x0D0D11).getRGB();
    private static final int BUTTON_BASE = new Color(20, 20, 25, 150).getRGB();
    private static final int BUTTON_HOVER = new Color(40, 40, 50, 200).getRGB();
    private static final int BUTTON_OUTLINE = new Color(255, 255, 255, 40).getRGB();
    private static final int BUTTON_OUTLINE_HOVER = new Color(255, 255, 255, 100).getRGB();
    private static final int TEXT_COLOR = new Color(0xEEEEEE).getRGB();

    private Button singleplayerButton;
    private Button multiplayerButton;
    private Button altmanagerButton;
    private Button optionsButton;
    private Button quitButton;

    private float introProgress = 0f;
    private float logoTimer = 0f;

    public MainMenu() {
        super(Text.literal("Main Menu"));
    }

    @Override
    protected void init() {
        introProgress = 0f;
        logoTimer = 0f;

        int buttonW = 220;
        int buttonH = 28;
        singleplayerButton = new Button("Singleplayer", buttonW, buttonH);
        multiplayerButton = new Button("Multiplayer", buttonW, buttonH);
        altmanagerButton = new Button("Account Manager", buttonW, buttonH);
        optionsButton = new Button("Settings", buttonW, buttonH);
        quitButton = new Button("Quit", buttonW, buttonH);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float dt = MathHelper.clamp(delta, 0f, 1f);

        introProgress = Math.min(1f, introProgress + dt * 0.018f);
        float t1 = MathHelper.clamp((introProgress - 0f) / (1f - 0f), 0f, 1f);
        float intro = t1 * t1 * (3f - 2f * t1);

        logoTimer = Math.min(1f, logoTimer + dt * 0.022f);
        float logoAlpha = MathHelper.clamp((logoTimer - 0f) / (0.75f - 0f), 0f, 1f);
        logoAlpha = logoAlpha * logoAlpha * (3f - 2f * logoAlpha);
        float logoScale = 0.85f + 0.15f * logoAlpha;

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 0);

        RenderUtil.drawRoundedRect(context.getMatrices(), 0, 0, this.width, this.height, 0f, BG_COLOR);

        float centerX = this.width / 2f;
        float centerY = this.height / 2f;

        int logoSize = 56;
        float logoX = centerX - logoSize / 2f;
        float logoY = centerY - 110f;

        context.getMatrices().push();
        context.getMatrices().translate(centerX, logoY + logoSize / 2f, 0);
        context.getMatrices().scale(logoScale, logoScale, 1);
        context.getMatrices().translate(-centerX, -(logoY + logoSize / 2f), 0);
        RenderUtil.drawTexture(context.getMatrices(), LOGO_TEX, logoX, logoY, logoSize, logoSize, 0, Color.WHITE.getRGB());
        context.getMatrices().pop();

        float titleY = logoY + logoSize + 10f;
        float titleAlpha = intro;
        FontUtils.sf_medium[18].centeredDraw(context.getMatrices(), "Lovan Client", centerX, titleY,
                ru.levin.util.color.ColorUtil.withAlpha(TEXT_COLOR, (int) (255 * titleAlpha)));

        float buttonGap = 10f;
        float totalHeight = 5 * 28f + 4 * buttonGap;
        float startY = titleY + 32f + (1f - intro) * 12f;

        singleplayerButton.setPosition(centerX, startY);
        multiplayerButton.setPosition(centerX, startY + 28f + buttonGap);
        altmanagerButton.setPosition(centerX, startY + 2f * (28f + buttonGap));
        optionsButton.setPosition(centerX, startY + 3f * (28f + buttonGap));
        quitButton.setPosition(centerX, startY + 4f * (28f + buttonGap) + 4f);

        singleplayerButton.render(context, mouseX, mouseY, dt);
        multiplayerButton.render(context, mouseX, mouseY, dt);
        altmanagerButton.render(context, mouseX, mouseY, dt);
        optionsButton.render(context, mouseX, mouseY, dt);
        quitButton.render(context, mouseX, mouseY, dt);

        context.getMatrices().pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (singleplayerButton.isHovered(mouseX, mouseY)) {
            this.client.setScreen(new SelectWorldScreen(this));
            return true;
        }
        if (multiplayerButton.isHovered(mouseX, mouseY)) {
            this.client.setScreen(new MultiplayerScreen(this));
            return true;
        }
        if (altmanagerButton.isHovered(mouseX, mouseY)) {
            this.client.setScreen(new ru.levin.screens.altmanager.AltManager(this));
            return true;
        }
        if (optionsButton.isHovered(mouseX, mouseY)) {
            this.client.setScreen(new OptionsScreen(this, client.options));
            return true;
        }
        if (quitButton.isHovered(mouseX, mouseY)) {
            this.client.scheduleStop();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private class Button {
        private final String name;
        private final int width;
        private final int height;
        private float x;
        private float y;
        private float hoverAnim = 0f;

        Button(String name, int width, int height) {
            this.name = name;
            this.width = width;
            this.height = height;
        }

        void setPosition(float centerX, float y) {
            this.x = centerX - width / 2f;
            this.y = y;
        }

        void render(DrawContext context, int mouseX, int mouseY, float delta) {
            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;

            float speed = 0.09f;
            if (hovered) {
                hoverAnim = Math.min(1f, hoverAnim + speed * delta);
            } else {
                hoverAnim = Math.max(0f, hoverAnim - speed * delta);
            }

            int bg = ru.levin.util.color.ColorUtil.blendColorsInt(BUTTON_BASE, BUTTON_HOVER, hoverAnim);
            int outline = ru.levin.util.color.ColorUtil.withAlpha(BUTTON_OUTLINE_HOVER,
                    MathHelper.lerp(0.35f, 1f, hoverAnim));

            float radius = 5f;

            RenderUtil.drawRoundedRect(context.getMatrices(), x, y, width, height, radius, bg);
            RenderUtil.drawRoundedBorder(context.getMatrices(), x, y, width, height, radius, 1f, outline);

            float textY = y + (height - FontUtils.sf_medium[14].getHeight()) / 2f;
            FontUtils.sf_medium[14].centeredDraw(context.getMatrices(), name, x + width / 2f, textY,
                    ru.levin.util.color.ColorUtil.withAlpha(TEXT_COLOR, 255));
        }

        boolean isHovered(double mouseX, double mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}