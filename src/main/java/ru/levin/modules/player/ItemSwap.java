package ru.levin.modules.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import ru.levin.events.Event;
import ru.levin.events.impl.input.EventKey;
import ru.levin.events.impl.input.EventMouse;
import ru.levin.manager.ClientManager;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.fontManager.FontUtils;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BindSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.util.color.ColorUtil;
import ru.levin.util.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.glfw.GLFW.*;

@SuppressWarnings("All")
@FunctionAnnotation(name = "ItemSwap", desc = "Быстрый свап предметов между hotbar и левой рукой", type = Type.Player, key = 0)
public class ItemSwap extends Function implements IMinecraft {

    private final BindSetting bind = new BindSetting("Кнопка открытия", 0);

    private final ItemSlot[] slots = new ItemSlot[] {
            new ItemSlot("Пусто"),
            new ItemSlot("Пусто"),
            new ItemSlot("Пусто"),
            new ItemSlot("Пусто"),
            new ItemSlot("Пусто"),
            new ItemSlot("Пусто"),
            new ItemSlot("Пусто"),
            new ItemSlot("Пусто"),
            new ItemSlot("Пусто")
    };

    private boolean menuOpen = false;
    private int selectedSlot = -1;
    private float menuAlpha = 0f;
    private float menuScale = 0.5f;
    private boolean closing = false;

    private static class ItemSlot {
        String itemName;
        String hand = "Левая";

        ItemSlot(String name) {
            this.itemName = name;
        }
    }

    public ItemSwap() {
        addSettings(bind);
    }

    @Override
    public void onEvent(Event event) {
        if (mc.player == null || mc.interactionManager == null) return;

        if (event instanceof EventKey eventKey) {
            if (!menuOpen && !closing && eventKey.key == bind.getKey() && bind.getKey() != 0) {
                openMenu();
            }
            if (menuOpen && eventKey.key == GLFW_KEY_ESCAPE) {
                closeMenu();
            }
            if (menuOpen && eventKey.key >= GLFW_KEY_1 && eventKey.key <= GLFW_KEY_9) {
                int idx = eventKey.key - GLFW_KEY_1;
                quickSwapSlot(idx);
            }
        }

        if (menuOpen && event instanceof EventMouse eventMouse) {
            int btn = eventMouse.getButton();
            if (btn == 0 || btn == 1) {
                handleMenuClick(btn);
            }
        }
    }

    private void openMenu() {
        if (mc.currentScreen != null) return;
        menuOpen = true;
        closing = false;
        menuAlpha = 0f;
        menuScale = 0.5f;
        selectedSlot = -1;
        mc.setScreen(new SwapMenuScreen());
    }

    private void closeMenu() {
        if (!menuOpen) return;
        closing = true;
    }

    private void finishClose() {
        menuOpen = false;
        closing = false;
        selectedSlot = -1;
        mc.setScreen(null);
    }

    private void quickSwapSlot(int slotIndex) {
        ItemSlot slot = slots[slotIndex];
        if (slot == null || slot.itemName.equals("Пусто")) return;

        int invSlot = findItemInInventory(slot.itemName);
        if (invSlot == -1) return;

        int targetSlot = slot.hand.equals("Левая") ? 40 : mc.player.getInventory().selectedSlot;
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, invSlot, targetSlot, SlotActionType.SWAP, mc.player);
        ClientManager.message("§a[ItemSwap] §fСлот " + (slotIndex + 1) + " (" + slot.itemName + ") -> " + slot.hand);
    }

    private void handleMenuClick(int button) {
        if (selectedSlot < 0) return;

        ItemSlot slot = slots[selectedSlot];

        if (slot.itemName.equals("Пусто")) {
            if (button == 1) {
                captureCurrentItem(selectedSlot);
            }
            return;
        }

        if (button == 0) {
            quickSwapSlot(selectedSlot);
        } else if (button == 1) {
            captureCurrentItem(selectedSlot);
        }
    }

    private void captureCurrentItem(int slotIndex) {
        ItemStack mainHand = mc.player.getMainHandStack();
        if (mainHand.isEmpty()) return;
        slots[slotIndex].itemName = mainHand.getItem().getName().getString();
        ClientManager.message("§a[ItemSwap] §fСлот " + (slotIndex + 1) + " = §f" + mainHand.getItem().getName().getString());
    }

    private int findItemInInventory(String itemName) {
        PlayerEntity player = mc.player;
        if (player == null) return -1;

        String target = itemName.toLowerCase();
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getName().getString().toLowerCase().contains(target)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDisable() {
        menuOpen = false;
        closing = false;
        if (mc.currentScreen instanceof SwapMenuScreen) {
            mc.setScreen(null);
        }
    }

    private class SwapMenuScreen extends Screen {
        public SwapMenuScreen() {
            super(Text.literal("ItemSwap"));
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            if (!menuOpen && !closing) {
                close();
                return;
            }

            if (closing) {
                menuAlpha = Math.max(0f, menuAlpha - 0.14f);
                menuScale = Math.max(0.5f, menuScale - 0.08f);
                if (menuAlpha <= 0.01f) {
                    finishClose();
                    return;
                }
            } else {
                menuAlpha = Math.min(1f, menuAlpha + 0.12f);
                menuScale = Math.min(1f, menuScale + 0.1f);
            }

            int centerX = width / 2;
            int centerY = height / 2;
            int slotSize = 50;
            int gap = 6;
            int slotsCount = 9;
            int totalWidth = slotsCount * slotSize + (slotsCount - 1) * gap;
            int startX = centerX - totalWidth / 2;
            int startY = centerY - slotSize / 2 - 30;

            selectedSlot = getSlotAt(mouseX, mouseY, startX, startY, slotSize, gap);

            for (int i = 0; i < slotsCount; i++) {
                ItemSlot slotData = slots[i];
                float x = startX + i * (slotSize + gap);
                float y = startY;
                boolean hovered = (i == selectedSlot);

                int alpha = (int) (menuAlpha * 255);
                int bgColor = ColorUtil.withAlpha(
                        hovered ? new Color(55, 55, 75).getRGB() : new Color(28, 28, 38).getRGB(),
                        alpha);

                if (hovered) {
                    RenderUtil.drawRoundedRect(context.getMatrices(), x, y, slotSize, slotSize, 8f,
                            ColorUtil.withAlpha(Color.WHITE.getRGB(), (int) (menuAlpha * 60)));
                }

                RenderUtil.drawRoundedRect(context.getMatrices(), x + 1, y + 1, slotSize - 2, slotSize - 2, 8f, bgColor);

                if (!slotData.itemName.equals("Пусто")) {
                    PlayerEntity player = mc.player;
                    if (player != null) {
                        for (int j = 0; j < player.getInventory().size(); j++) {
                            ItemStack stack = player.getInventory().getStack(j);
                            if (!stack.isEmpty() && stack.getName().getString().equals(slotData.itemName)) {
                                context.drawItem(stack, (int) x + slotSize / 2 - 9, (int) y + slotSize / 2 - 9);
                                break;
                            }
                        }
                    }
                }

                String handLabel = "➜ " + slotData.hand;
                FontUtils.sfns_display_bold[11].centeredDraw(context.getMatrices(), handLabel,
                        (int) x + slotSize / 2, (int) y + slotSize + 2, ColorUtil.withAlpha(Color.WHITE.getRGB(), (int) (menuAlpha * 220)));

                String hint = slotData.itemName.equals("Пусто") ? "ПКМ=назначить" : "ЛКМ=свап | ПКМ=назначить";
                FontUtils.sfns_display_bold[10].centeredDraw(context.getMatrices(), hint,
                        (int) x + slotSize / 2, (int) y + slotSize + 13, ColorUtil.withAlpha(new Color(160, 160, 160).getRGB(), (int) (menuAlpha * 180)));
            }

            String title = "ItemSwap — ЛКМ=свап | ПКМ=назначить предмет | 1-9=быстрый свап | ESC=выход";
            FontUtils.sfns_display_bold[13].centeredDraw(context.getMatrices(), title,
                    width / 2, startY - 20, ColorUtil.withAlpha(Color.WHITE.getRGB(), (int) (menuAlpha * 220)));
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (selectedSlot >= 0) {
                if (button == 2 && !slots[selectedSlot].itemName.equals("Пусто")) {
                    toggleHand(selectedSlot);
                    return true;
                }
                handleMenuClick(button);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean shouldCloseOnEsc() {
            if (menuOpen || closing) {
                closeMenu();
                return true;
            }
            return super.shouldCloseOnEsc();
        }

        private void toggleHand(int slotIndex) {
            ItemSlot slot = slots[slotIndex];
            if (slot == null) return;
            slot.hand = slot.hand.equals("Левая") ? "Правая" : "Левая";
            ClientManager.message("§a[ItemSwap] §fСлот " + (slotIndex + 1) + " теперь свапает в " + slot.hand + " руку");
        }

        private int getSlotAt(double mx, double my, int startX, int startY, int slotSize, int gap) {
            for (int i = 0; i < 9; i++) {
                float x = startX + i * (slotSize + gap);
                float y = startY;
                if (mx >= x && mx <= x + slotSize && my >= y && my <= y + slotSize) {
                    return i;
                }
            }
            return -1;
        }
    }
}
