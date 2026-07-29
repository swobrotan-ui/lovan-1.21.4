package ru.levin.synclag.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.levin.synclag.SyncLagMod;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntityDrop {

    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "synclag-drop-scheduler");
                    t.setDaemon(true);
                    return t;
                }
            });

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

        if (!SyncLagMod.enabled) {
            return;
        }

        ItemStack mainHand = self.getMainHandStack();
        if (mainHand.isEmpty()) {
            return;
        }

        ItemStack copy = mainHand.copy();
        double x = self.getX();
        double y = self.getEyeY() - 0.3;
        double z = self.getZ();
        World world = self.getWorld();
        MinecraftServer server = self.getServer();

        if (server == null || world == null) {
            return;
        }

        int delayMs = 50 + self.getRandom().nextInt(51);

        SCHEDULER.schedule(() -> {
            server.execute(() -> {
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnEntity(new ItemEntity(serverWorld, x, y, z, copy));
                }
            });
        }, delayMs, TimeUnit.MILLISECONDS);

        cir.setReturnValue(true);
        cir.cancel();
    }
}
