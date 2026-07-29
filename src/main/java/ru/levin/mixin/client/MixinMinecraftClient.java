package ru.levin.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.levin.ExosWare;
import ru.levin.manager.*;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements IMinecraft {
    @Inject(method = "method_29043", at = @At("HEAD"), cancellable = true, remap = false)
    private void isMultiplayerEnabled(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "method_24287", at = @At("HEAD"), cancellable = true, remap = false)
    private void getWindowTitle(CallbackInfoReturnable<String> cir) {
        if (!ClientManager.legitMode) {
            boolean showBar = ((System.currentTimeMillis() / 450L) % 2L) == 0L;
            String sep = showBar ? " | " : "   ";
            String name = (Manager.USER_PROFILE != null && Manager.USER_PROFILE.getName() != null)
                    ? Manager.USER_PROFILE.getName()
                    : "";
            cir.setReturnValue("Lovan Client" + sep + name);
        }
    }
    @Inject(at = @At("HEAD"), method = "method_1490", remap = false)
    private void stop(CallbackInfo ci) {
        ExosWare.getInstance().shutDown();
    }
    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void init(CallbackInfo callbackInfo) {
        ExosWare.getInstance().init();
    }
}
