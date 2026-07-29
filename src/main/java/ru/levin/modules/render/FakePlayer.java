package ru.levin.modules.render;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;

import java.util.UUID;

@SuppressWarnings("All")
@FunctionAnnotation(name = "FakePlayer", desc = "Создаёт фейк игрока", type = Type.Render)
public class FakePlayer extends Function {

    private final ModeSetting mod = new ModeSetting("Действие", "Спавн", "Спавн");

    private boolean spawned = false;
    private int fakeEntityId = -1;
    private ClientWorld lastWorld = null;

    public FakePlayer() {
        addSettings(mod);
    }

    @Override
    public void onEvent(Event event) {
        if (mc.player == null || mc.world == null) return;

        if (event instanceof EventUpdate) {
            if (spawned && mc.world != lastWorld) {
                removeFakePlayer();
                spawned = false;
            }

            if (mod.is("Спавн")) {
                if (!spawned) {
                    spawnFakePlayer();
                    spawned = true;
                }
            }
        }
    }

    private void spawnFakePlayer() {
        ClientWorld world = mc.world;
        if (world == null || mc.player == null) return;

        UUID uuid = UUID.nameUUIDFromBytes("fake-player".getBytes());
        GameProfile profile = new GameProfile(uuid, "FakePlayer");

        OtherClientPlayerEntity fake = new OtherClientPlayerEntity(world, profile);
        fake.refreshPositionAndAngles(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch());
        fake.setYaw(mc.player.getYaw());
        fake.setPitch(mc.player.getPitch());
        fake.setHeadYaw(mc.player.getHeadYaw());
        fake.setHealth(MathHelper.clamp(mc.player.getHealth(), 1.0f, fake.getMaxHealth()));

        world.addEntity(fake);

        fakeEntityId = fake.getId();
        lastWorld = world;
    }

    private void removeFakePlayer() {
        if (mc.world == null) return;
        if (fakeEntityId == -1) return;

        mc.world.removeEntity(fakeEntityId, Entity.RemovalReason.DISCARDED);
        fakeEntityId = -1;
        lastWorld = mc.world;
    }

    @Override
    protected void onDisable() {
        removeFakePlayer();
        spawned = false;
        super.onDisable();
    }
}
