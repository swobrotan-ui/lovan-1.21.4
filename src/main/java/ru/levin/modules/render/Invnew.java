package ru.levin.modules.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import ru.levin.events.Event;
import ru.levin.events.impl.render.EventRender2D;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.util.math.MathUtil;
import ru.levin.util.render.RenderUtil;

import java.awt.*;

import static ru.levin.util.render.RenderUtil.*;

@FunctionAnnotation(name = "Invnew", type = Type.Render, desc = "Визуальный хотбар")
public class Invnew extends Function {
    private float selectedAnim = 0f;
    private float selectorScalePhase = 0f;
    private long lastRender = System.currentTimeMillis();

    public Invnew() {
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender2D eventRender2D && MinecraftClient.getInstance().player != null) {
            long now = System.currentTimeMillis();
            float delta = (now - lastRender) / 16.666f;
            lastRender = now;

            MinecraftClient mc = MinecraftClient.getInstance();
            int selectedSlot = mc.player.getInventory().selectedSlot;

            float diff = selectedSlot - selectedAnim;
            float absDiff = Math.abs(diff);

            if (absDiff < 0.02f) {
                selectedAnim = selectedSlot;
            } else {
                selectedAnim += diff * Math.min(1f, 0.15f * delta);
            }

            float isMoving = Math.abs(selectedSlot - selectedAnim);
            float juice = Math.min(1f, isMoving * 3.5f);

            float slotSize = 20.0F;
            int slots = 9;
            float gap = 1.0F;

            float totalWidth = slots * slotSize + (slots - 1) * gap;
            float paddingX = 4f;
            float paddingY = 2.0f;

            float containerW = totalWidth + paddingX * 2;
            float containerH = slotSize + paddingY * 2;

            float screenW = mc.getWindow().getScaledWidth();
            float screenH = mc.getWindow().getScaledHeight();

            float baseX = (screenW - containerW) / 2f;
            float baseY = screenH - containerH - 2;

            float slotBaseX = baseX + paddingX;
            float slotBaseY = baseY + paddingY;

            int outlineAlpha = (int) (120);
            int outlineColor = (outlineAlpha << 24) | 0x888888;
            drawRoundedBorder(eventRender2D.getMatrixStack(), baseX, baseY, containerW, containerH, 5f, 0.5f, outlineColor);

            int slotIndex = (int) Math.floor(selectedAnim);
            float lt = selectedAnim - slotIndex;
            if (slotIndex < 0) slotIndex = 0;
            if (slotIndex >= slots - 1) slotIndex = slots - 2;

            float fromX = slotBaseX + slotIndex * (slotSize + gap);
            float toX = fromX + slotSize + gap;
            float selRawX = MathHelper.lerp(lt, fromX, toX);
            float selX = selRawX - 0.2f;
            float selY = slotBaseY - 0.2f;
            float selSize = slotSize + 0.4f;

            selectorScalePhase += delta * 6f;
            float pulse = 1f + (float) Math.sin(selectorScalePhase) * 0.012f;
            float selectorScale = MathHelper.lerp(1f - juice * 0.15f, 1f + juice * 0.06f, pulse);
            float sx = selX + selSize / 2f;
            float sy = selY + selSize / 2f;

            int selectOutlineAlpha = (int) (160 + juice * 80);
            int selectOutlineColor = (selectOutlineAlpha << 24) | 0xAAAAAA;
            drawRoundedBorder(eventRender2D.getMatrixStack(), sx - selSize * selectorScale / 2f, sy - selSize * selectorScale / 2f, selSize * selectorScale, selSize * selectorScale, 3f, 0.5f, selectOutlineColor);

            float itemDrawSize = 13.0f;

            for (int col = 0; col < slots; col++) {
                float slotX = slotBaseX + col * (slotSize + gap);
                float slotY = slotBaseY;

                ItemStack stack = mc.player.getInventory().main.get(col);
                if (stack.isEmpty()) continue;

                float itemDrawX = slotX + (slotSize - itemDrawSize) / 2f;
                float itemDrawY = slotY + (slotSize - itemDrawSize) / 2f - 0.5f;

                eventRender2D.getDrawContext().drawItem(stack, (int) itemDrawX, (int) itemDrawY, 0);
                if (stack.getCount() > 1) {
                    eventRender2D.getDrawContext().drawStackOverlay(mc.textRenderer, stack, (int) itemDrawX, (int) itemDrawY);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        selectedAnim = 0f;
        selectorScalePhase = 0f;
    }
}
