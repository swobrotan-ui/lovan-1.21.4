package ru.levin.playertracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerMovementTracker {

    private static final PlayerMovementTracker INSTANCE = new PlayerMovementTracker();
    private static final int MAX_ENTRIES = 300;

    public static PlayerMovementTracker get() {
        return INSTANCE;
    }

    private final Map<UUID, Vec3d> positions = new HashMap<>();

    private PlayerMovementTracker() {}

    public static void update(UUID uuid, Vec3d pos) {
        get().positions.put(uuid, pos);
    }

    public static void update(UUID uuid, double x, double y, double z) {
        update(uuid, new Vec3d(x, y, z));
    }

    public Vec3d get(UUID uuid) {
        return positions.get(uuid);
    }

    public Vec3d getByName(String name) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getNetworkHandler() == null) {
            return null;
        }
        for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
            if (entry.getProfile().getName().equalsIgnoreCase(name)) {
                return positions.get(entry.getProfile().getId());
            }
        }
        return null;
    }
}
