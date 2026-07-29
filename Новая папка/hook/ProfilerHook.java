package hook;

import com.mojang.blaze3d.systems.RenderSystem;
import core.ClientMain;
import enum.HookType;
import java.util.Queue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.TickTimeTracker;
import util.UnsafeFieldAccessor;

public class ProfilerHook extends Hook {
   private final ThreadLocal<Profiler> ACTIVE = new ThreadLocal<Profiler>();
   private UnsafeFieldAccessor<TickTimeTracker> continuousProfiler;
   public UnsafeFieldAccessor<Queue<Runnable>> queue;
   private TickTimeTracker cProfiler;
   private UnsafeFieldAccessor<ThreadLocal<Profiler>> reflectField;
   public static ProfilerHook INSTANCE = new ProfilerHook();

   @Override
   public void hook() {
      this.cProfiler = new CustomTickTimeTracker(System::nanoTime, () -> {
         return 0;
      });
      this.continuousProfiler = new UnsafeFieldAccessor<TickTimeTracker>(MinecraftClient.getInstance(), MinecraftClient.class, TickTimeTracker.class)
         .removeModifier();
      this.continuousProfiler.setValue(this.cProfiler);
      RenderSystem.recordRenderCall(ClientMain::new);
      this.setQueue();
   }

   public void setQueue() {
      if (this.queue == null) {
         this.queue = new UnsafeFieldAccessor<Queue<Runnable>>(MinecraftClient.getInstance(), MinecraftClient.class, Queue.class).removeModifier();
         this.reflectField = new UnsafeFieldAccessor<ThreadLocal<Profiler>>(null, Profilers.class, 1);
      }

      this.queue.getValue().add(() -> {
         this.reflectField.getValue().set(core.ProfilerHook.INSTANCE);
      });
   }

   public ProfilerHook() {
      super(HookType.Init);
   }

   @Override
   public void unHook() {
   }
}
