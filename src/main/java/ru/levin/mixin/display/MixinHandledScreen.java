package ru.levin.mixin.display;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.InputUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.levin.manager.ClientManager;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.util.player.TimerUtil;
import ru.levin.x2demo.X2DemoMod;
import ru.levin.x2demo.network.X2RequestC2SPacket;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T>,IMinecraft{

    @Unique
    private final TimerUtil timerUtil = new TimerUtil();

    @Shadow
    @Nullable
    protected Slot focusedSlot;

    protected MixinHandledScreen(Text title) {
        super(title);
    }

    @Shadow
    protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    @Unique
    private boolean sodiumextra$isAuctionScreen() {
        String title = this.getTitle() == null ? "" : this.getTitle().getString();
        String lower = title.toLowerCase();
        return lower.contains("аук") || lower.contains("auction") || lower.contains("ah") || lower.contains("аукцион");
    }

    @Unique
    private int sodiumextra$btnX() {
        return (this.width / 2) - 50;
    }

    @Unique
    private int sodiumextra$btnY() {
        return 6;
    }

    @Unique
    private boolean sodiumextra$inButton(double mouseX, double mouseY) {
        int x = sodiumextra$btnX();
        int y = sodiumextra$btnY();
        return mouseX >= x && mouseX <= x + 100 && mouseY >= y && mouseY <= y + 14;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void sodiumextra$renderAutoBuyButton(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!sodiumextra$isAuctionScreen()) return;
        if (Manager.FUNCTION_MANAGER == null || ru.levin.modules.FunctionManager.get("AutoBuy") == null) return;

        int x = sodiumextra$btnX();
        int y = sodiumextra$btnY();
        boolean hover = sodiumextra$inButton(mouseX, mouseY);
        int bg = hover ? 0xAA00AA00 : 0xAA000000;
        context.fill(x, y, x + 100, y + 14, bg);
        context.drawCenteredTextWithShadow(this.textRenderer, "AutoBuy", x + 50, y + 3, 0xFFFFFFFF);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void sodiumextra$clickAutoBuyButton(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button != 0) return;
        if (!sodiumextra$isAuctionScreen()) return;
        if (!sodiumextra$inButton(mouseX, mouseY)) return;

        ru.levin.modules.Function f = ru.levin.modules.FunctionManager.get("AutoBuy");
        if (f instanceof ru.levin.modules.misc.AutoBuy autoBuy) {
            autoBuy.startBuying();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"))
    private void onDrawMouseoverTooltip(DrawContext context, int x, int y, CallbackInfo ci) {
        if (this.focusedSlot == null || !this.focusedSlot.hasStack()) return;

        long windowHandle = mc.getWindow().getHandle();

        boolean leftMousePressed = GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean shiftPressed = InputUtil.isKeyPressed(windowHandle, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT);
        if (Manager.FUNCTION_MANAGER.itemScroller != null && Manager.FUNCTION_MANAGER.itemScroller.state && leftMousePressed && shiftPressed && client.currentScreen != null) {
            if (timerUtil.hasTimeElapsed(Manager.FUNCTION_MANAGER.itemScroller.scroll.get().longValue()) && this.focusedSlot.hasStack()) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.QUICK_MOVE);
                timerUtil.reset();
            }
        }
    }

    @Unique
    private boolean x2demo$isX2Enabled() {
        if (Manager.FUNCTION_MANAGER == null) return false;
        var x2Module = ru.levin.modules.FunctionManager.get("X2Duplicator");
        return x2Module != null && x2Module.isState();
    }

    @Unique
    private boolean x2demo$inX2Button(double mouseX, double mouseY) {
        int x = (this.width - 176) / 2;
        int y = (this.height - 133) / 2;
        int buttonX = x + 176 - 26;
        int buttonY = y + 6;
        return mouseX >= buttonX && mouseX <= buttonX + 20 && mouseY >= buttonY && mouseY <= buttonY + 16;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void x2demo$renderX2Button(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!x2demo$isX2Enabled()) return;

        int x = (this.width - 176) / 2;
        int y = (this.height - 133) / 2;
        int buttonX = x + 176 - 26;
        int buttonY = y + 6;

        boolean hover = x2demo$inX2Button(mouseX, mouseY);
        int bgColor = hover ? 0xFF00CC00 : 0xFF00FF00;
        context.fill(buttonX, buttonY, buttonX + 20, buttonY + 16, bgColor);
        context.drawCenteredTextWithShadow(this.textRenderer, "x2", buttonX + 10, buttonY + 4, 0xFFFFFFFF);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void x2demo$clickX2Button(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!x2demo$isX2Enabled() || button != 0) return;
        if (!x2demo$inX2Button(mouseX, mouseY)) return;

        mc.player.playSound(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
        if (mc.player.getMainHandStack().isEmpty()) return;

        ItemStack held = mc.player.getMainHandStack();
        int slot = mc.player.getInventory().selectedSlot;
        int count = held.getCount();

        // УЧЕБНАЯ отправка кастомного C2S-пакета. Реального изменения предметов не происходит —
        // сервер лишь залогирует запрос и вернёт подтверждение (см. X2DemoMod).
        if (ClientPlayNetworking.canSend(X2RequestC2SPacket.ID)) {
            ClientPlayNetworking.send(new X2RequestC2SPacket(slot, count));
            X2DemoMod.LOGGER.info("[x2] CLIENT: отправлен запрос 'x2' на сервер: slot={}, count={}",
                    slot, count);
        } else {
            ClientManager.message("[x2demo] Сервер не поддерживает канал x2 (мод не установлен на сервере)");
        }
        cir.setReturnValue(true);
    }
}