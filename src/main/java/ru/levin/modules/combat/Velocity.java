package ru.levin.modules.combat;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import ru.levin.events.Event;
import ru.levin.events.impl.EventPacket;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;

import java.util.Random;

@SuppressWarnings("All")
@FunctionAnnotation(name = "Velocity", desc = "Управление отбрасыванием", type = Type.Combat)
public class Velocity extends Function {

    private final ModeSetting mode = new ModeSetting("Режим", "Снизить", "Отмена", "Изменить", "Снизить");
    private final SliderSetting x = new SliderSetting("X%", 10, 0, 100, 1, () -> mode.is("Изменить") || mode.is("Снизить"));
    private final SliderSetting y = new SliderSetting("Y%", 10, 0, 100, 1, () -> mode.is("Изменить") || mode.is("Снизить"));
    private final SliderSetting z = new SliderSetting("Z%", 10, 0, 100, 1, () -> mode.is("Изменить") || mode.is("Снизить"));
    private final Random random = new Random();

    public Velocity() {
        addSettings(mode, x, y, z);
    }

    @Override
    public void onEvent(Event event) {
        if (mc.player == null || mc.world == null) return;
        if (!(event instanceof EventPacket e)) return;
        if (!e.isReceivePacket()) return;

        if (!(e.getPacket() instanceof EntityVelocityUpdateS2CPacket p)) return;
        if (p.getEntityId() != mc.player.getId()) return;

        if (mode.is("Отмена")) {
            e.setCancel(true);
            return;
        }

        // масштабируем отскок и слегка джиттерим, чтобы не выглядело как идеальный 0/100%
        double sx = (mode.is("Снизить") ? x.get().doubleValue() : x.get().doubleValue()) / 100.0;
        double sy = (mode.is("Снизить") ? y.get().doubleValue() : y.get().doubleValue()) / 100.0;
        double sz = (mode.is("Снизить") ? z.get().doubleValue() : z.get().doubleValue()) / 100.0;

        double jitter = 1.0 + (random.nextDouble() - 0.5) * 0.06;
        sx *= jitter;
        sz *= jitter;

        double vx = (p.getVelocityX() / 8000.0) * sx;
        double vy = (p.getVelocityY() / 8000.0) * sy;
        double vz = (p.getVelocityZ() / 8000.0) * sz;

        e.setCancel(true);
        mc.player.setVelocity(vx, vy, vz);
    }
}
