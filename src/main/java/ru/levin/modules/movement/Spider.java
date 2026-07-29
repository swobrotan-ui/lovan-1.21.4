package ru.levin.modules.movement;

import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

@FunctionAnnotation(name = "Spider", desc = "Лазание по стенам как паук", type = Type.Move)
public class Spider extends Function {

    private final SliderSetting jumpHeight = new SliderSetting("Высота прыжка", 0.30f, 0.1f, 1.0f, 0.01f);

    private final ModeSetting mode = new ModeSetting("Тип", "Water", "Water", "FunTime", "Rockstar");

    public Spider() {
        addSettings(mode, jumpHeight);
    }

    @Override
    public void onEvent(Event event) {
        if (mc.player == null || mc.world == null) return;

        if (mode.is("Rockstar")) {
            if (!(event instanceof EventUpdate)) return;
            if (mc.player.horizontalCollision) {
                mc.player.setOnGround(mc.player.age % 3 == 0);
                mc.player.prevY -= 2.0E-232;
                if (mc.player.isOnGround()) {
                    mc.player.setVelocity(mc.player.getVelocity().getX(), 0.42, mc.player.getVelocity().getZ());
                }
            }
            return;
        }

        if (!(event instanceof EventMotion)) return;

        if (mode.is("Water")) {
            int bucketSlot = findWaterBucketSlot();
            if (bucketSlot == -1) return;

            if (mc.player.getMainHandStack().getItem() != Items.WATER_BUCKET) {
                mc.player.getInventory().selectedSlot = bucketSlot;
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(bucketSlot));
            }

            if (mc.player.horizontalCollision) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.player.setVelocity(mc.player.getVelocity().x, jumpHeight.get().floatValue(), mc.player.getVelocity().z);
            }
        } else if (mode.is("FunTime")) {
            if (mc.player.horizontalCollision) {
                float moveSpeed = 1.1f;
                if (mc.player.isOnGround()) {
                    // карабкаемся рывком вверх только на земле — выглядит как серия прыжков
                    mc.player.setVelocity(
                            mc.player.getVelocity().x * moveSpeed,
                            0.45D,
                            mc.player.getVelocity().z * moveSpeed
                    );
                } else {
                    mc.player.setVelocity(
                            mc.player.getVelocity().x * moveSpeed,
                            mc.player.getVelocity().y,
                            mc.player.getVelocity().z * moveSpeed
                    );
                }
            }
        }
    }

    private int findWaterBucketSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.WATER_BUCKET) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
