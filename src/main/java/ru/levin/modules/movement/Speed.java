package ru.levin.modules.movement;

import ru.levin.modules.combat.TargetStrafe;
import ru.levin.events.Event;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.manager.ClientManager;
import ru.levin.manager.Manager;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.move.MoveUtil;

@FunctionAnnotation(name = "Speed", desc = "Ускорение передвижения", type = Type.Move)
public class Speed extends Function {
    private final ModeSetting mode = new ModeSetting("Режим", "Strafe", "Strafe", "LowHop", "Vanilla");
    private final SliderSetting speed = new SliderSetting("Скорость", 0.55f, 0.1f, 2.5f, 0.01f);

    public Speed() {
        addSettings(mode, speed);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventMotion)) return;
        if (mc.player == null || mc.world == null) return;
        if (!MoveUtil.isMoving() || mc.player.isGliding()) return;

        switch (mode.get()) {
            case "Vanilla" -> vanilla();
            case "LowHop" -> lowHop();
            default -> strafe();
        }
    }

    // обычный сет скорости (заметен античитам)
    private void vanilla() {
        MoveUtil.setSpeed(speed.get().floatValue());
    }

    // bunnyhop: держим скорость и подпрыгиваем на земле, выглядит как легитный стрейф
    private void strafe() {
        MoveUtil.setMotion(speed.get().floatValue());
        if (mc.player.isOnGround()) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0.42, mc.player.getVelocity().z);
        }
    }

    // маленький хоп — минимум вертикали, сложнее заметить по полёту
    private void lowHop() {
        MoveUtil.setMotion(speed.get().floatValue());
        if (mc.player.isOnGround()) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0.12, mc.player.getVelocity().z);
        }
    }

    @Override
    protected void onEnable() {
        TargetStrafe targetStrafe = Manager.FUNCTION_MANAGER.targetStrafe;
        if (targetStrafe.state) {
            targetStrafe.setState(false);
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        ClientManager.TICK_TIMER = 1.0f;
        super.onDisable();
    }
}
