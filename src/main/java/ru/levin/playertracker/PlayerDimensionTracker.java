package ru.levin.playertracker;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerDimensionTracker {

    private static final PlayerDimensionTracker INSTANCE = new PlayerDimensionTracker();

    public static PlayerDimensionTracker get() {
        return INSTANCE;
    }

    private final Map<UUID, DimensionSnapshot> snapshots = new ConcurrentHashMap<>();

    private PlayerDimensionTracker() {}

    public void setDimension(UUID uuid, RegistryKey<World> dimension) {
        snapshots.put(uuid, new DimensionSnapshot(dimension, System.currentTimeMillis()));
    }

    public RegistryKey<World> getDimension(UUID uuid) {
        DimensionSnapshot snap = snapshots.get(uuid);
        if (snap == null) return World.OVERWORLD;
        return snap.dimension;
    }

    public long getLastSeen(UUID uuid) {
        DimensionSnapshot snap = snapshots.get(uuid);
        if (snap == null) return 0L;
        return snap.timestamp;
    }

    public boolean isAlive(UUID uuid, long ttlMs) {
        DimensionSnapshot snap = snapshots.get(uuid);
        if (snap == null) return false;
        return System.currentTimeMillis() - snap.timestamp < ttlMs;
    }

    private record DimensionSnapshot(RegistryKey<World> dimension, long timestamp) {}
}
