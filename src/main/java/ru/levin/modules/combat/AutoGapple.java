package ru.levin.modules.combat;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.player.InventoryUtil;

@SuppressWarnings("All")
@FunctionAnnotation(name = "AutoGapple", desc = "Авто яблоко (упрощённо)", type = Type.Combat)
public class AutoGapple extends Function {

    private final SliderSetting healthEat = new SliderSetting("ХП", 15, 1, 20, 1);
    private boolean eating;

    public AutoGapple() {
        addSettings(healthEat);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventUpdate)) return;
        if (mc.player == null || mc.world == null) return;

        float total = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        boolean shouldEat = total <= healthEat.get().floatValue();

        if (!shouldEat) {
            stopEating();
            return;
        }

        ItemStack offhand = mc.player.getOffHandStack();
        boolean offhandGapple = offhand.isOf(Items.GOLDEN_APPLE) || offhand.isOf(Items.ENCHANTED_GOLDEN_APPLE);

        if (!offhandGapple) {
            int slot = InventoryUtil.getItemSlot(Items.GOLDEN_APPLE);
            if (slot == -1) slot = InventoryUtil.getItemSlot(Items.ENCHANTED_GOLDEN_APPLE);
            if (slot == -1) {
                stopEating();
                return;
            }
            InventoryUtil.moveToOffhand(Items.GOLDEN_APPLE);
        }

        startEating();
    }

    private void startEating() {
        eating = true;
        mc.options.useKey.setPressed(true);
    }

    private void stopEating() {
        if (!eating) return;
        mc.options.useKey.setPressed(false);
        eating = false;
    }
}
