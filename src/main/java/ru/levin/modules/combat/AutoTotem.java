package ru.levin.modules.combat;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PlayerHeadItem;
import ru.levin.events.impl.move.EventEntitySpawn;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.mixin.player.MixinEntity;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.MultiSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.manager.Manager;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.util.player.InventoryUtil;

import java.util.Arrays;

@SuppressWarnings("all")
@FunctionAnnotation(name = "AutoTotem", desc = "Берёт в руки тотем при определённом здоровье", type = Type.Combat)
public class AutoTotem extends Function {

    private int swapStage = 0;
    private long nextSwapActionMs = 0L;
    private boolean openedInventoryByMe = false;
    private boolean auraWasDisabledByMe = false;
    private boolean auraWasEnabled = false;

    private final MultiSetting mode = new MultiSetting(
            "Брать если",
            Arrays.asList("Кристалл", "Игрок с булавой"),
            new String[]{"Кристалл", "Игрок с булавой", "Рядом крипер", "Обсидиан", "Якорь", "Падение", "Вагонетка"}
    );

    public final SliderSetting hp = new SliderSetting("Здоровье", 4.5f, 2.0f, 20.0f, 0.1f);

    private final SliderSetting crystalDistance = new SliderSetting("До кристалла", 4, 2, 6, 1, () -> mode.get("Кристалл"));
    private final SliderSetting anchorDistance = new SliderSetting("До якоря", 4, 2, 6, 1, () -> mode.get("Якорь"));
    private final SliderSetting minecartDistance = new SliderSetting("До Вагонетки", 4, 2, 8, 1, () -> mode.get("Вагонетка"));
    private final SliderSetting obsidianDistance = new SliderSetting("До Обсидиана", 4, 2, 8, 1, () -> mode.get("Обсидиан"));

    public AutoTotem() {
        addSettings(mode, hp, crystalDistance, anchorDistance, minecartDistance, obsidianDistance);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventEntitySpawn spawnEvent) {
            Entity e = spawnEvent.getEntity();
            if (mode.get("Кристалл") && e instanceof EndCrystalEntity) {
                if (mc.player != null && e.distanceTo(mc.player) <= crystalDistance.get().floatValue()) {
                    forceTotem();
                }
            }

            if (mode.get("Вагонетка") && e instanceof TntMinecartEntity) {
                if (mc.player != null && e.distanceTo(mc.player) <= minecartDistance.get().floatValue()) {
                    forceTotem();
                }
            }
        }
        if (event instanceof EventUpdate) {
            if (mc.player == null || mc.interactionManager == null) return;

            if (swapStage != 0) {
                tickSwapStateMachine();
                return;
            }

            int slot = getTotemSlot();
            ItemStack offhand = mc.player.getOffHandStack();

            if (mc.player.getHealth() <= hp.get().floatValue()) {
                if (slot < 0) return;
                if (offhand.getItem() != Items.TOTEM_OF_UNDYING) {
                    startSwap(slot);
                }
            }
        }
    }

    private void startSwap(int totemSlot) {
        if (mc.player == null || mc.interactionManager == null) return;

        auraWasDisabledByMe = false;
        auraWasEnabled = false;
        openedInventoryByMe = false;

        AttackAura aura = Manager.FUNCTION_MANAGER.attackAura;
        if (aura != null && aura.state && aura.target != null) {
            auraWasEnabled = true;
            aura.setState(false);
            auraWasDisabledByMe = true;
        }

        if (!(mc.currentScreen instanceof InventoryScreen)) {
            mc.setScreen(new InventoryScreen(mc.player));
            openedInventoryByMe = true;
        }

        swapStage = 1;
        nextSwapActionMs = System.currentTimeMillis() + 80L;
    }

    private void tickSwapStateMachine() {
        if (mc.player == null || mc.interactionManager == null) {
            resetSwapState();
            return;
        }

        long now = System.currentTimeMillis();
        if (now < nextSwapActionMs) return;

        if (swapStage == 1) {
            int slot = getTotemSlot();
            if (slot >= 0 && mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
                InventoryUtil.swapSlotsUniversal(slot, 40, false, true);
            }
            swapStage = 2;
            nextSwapActionMs = now + 80L;
            return;
        }

        if (swapStage == 2) {
            if (openedInventoryByMe && mc.currentScreen instanceof InventoryScreen) {
                mc.setScreen(null);
            }

            if (auraWasDisabledByMe && auraWasEnabled) {
                AttackAura aura = Manager.FUNCTION_MANAGER.attackAura;
                if (aura != null) {
                    aura.setState(true);
                }
            }
            resetSwapState();
        }
    }

    private void resetSwapState() {
        swapStage = 0;
        nextSwapActionMs = 0L;
        openedInventoryByMe = false;
        auraWasDisabledByMe = false;
        auraWasEnabled = false;
    }

    private void forceTotem() {
        if (mc.player == null || mc.interactionManager == null) return;
        int slot = getTotemSlot();
        if (slot < 0) return;

        ItemStack offhand = mc.player.getOffHandStack();
        if (offhand.getItem() != Items.TOTEM_OF_UNDYING) {
            startSwap(slot);
        }
    }


    private int getTotemSlot() {
        return findTotem(false);
    }

    private int findTotem(boolean enchanted) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                boolean hasEnchant = stack.hasEnchantments();
                if (enchanted == hasEnchant) return i;
            }
        }
        return -1;
    }
}
