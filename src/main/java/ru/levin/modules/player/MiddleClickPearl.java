package ru.levin.modules.player;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import ru.levin.events.Event;
import ru.levin.events.impl.input.EventMouse;
import ru.levin.manager.Manager;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BindSetting;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.util.player.InventoryUtil;

@FunctionAnnotation(name = "MiddleClickPearl",keywords = {"MCP"}, desc = "Откидывание пёрла по колёсику мыши", type = Type.Player)
public class MiddleClickPearl extends Function {
    private final ModeSetting mode = new ModeSetting("Тип","Обычный","Обычный","По бинду");
    private final BindSetting bind = new BindSetting("Кнопка",0,() -> mode.is("По бинду"));
    private final BooleanSetting inventoryUse = new BooleanSetting("Использовать из инвентаря",true,"Не используйте на HollyWorld (баниться)");

    public MiddleClickPearl() {
        addSettings(mode,bind,inventoryUse);
    }

    @Override
    public void onEvent(Event event) {
        if (mode.is("Обычный")) {
            if (event instanceof EventMouse mouseTick) {
                if (mouseTick.getButton() == 2) {
                    handleMouseTickEvent();
                }
            }
        }
        if (mode.is("По бинду")) {
            if (event instanceof EventMouse e) {
                int code = -e.getButton() - 2;
                if (code == bind.getKey()) {
                    handleMouseTickEvent();
                }
            }
        }
    }

    private void handleMouseTickEvent() {
        if (!mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL.getDefaultStack())) {
            if (Manager.FUNCTION_MANAGER.attackAura.target != null) {
                // Calculate yaw and pitch to look at target
                var target = Manager.FUNCTION_MANAGER.attackAura.target;
                double dx = target.getX() - mc.player.getX();
                double dy = target.getY() + target.getEyeHeight(target.getPose()) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
                double dz = target.getZ() - mc.player.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);
                float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
                float pitch = (float) -Math.toDegrees(Math.atan2(dy, distance));
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc.player.isOnGround(), mc.player.horizontalCollision));
            }
            InventoryUtil.inventorySwapClick2(Items.ENDER_PEARL, inventoryUse.get(), true);
        }
    }
}
