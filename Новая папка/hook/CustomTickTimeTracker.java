package hook;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ReadableProfiler;
import net.minecraft.util.profiler.TickTimeTracker;

public class CustomTickTimeTracker extends TickTimeTracker {
   private final LongSupplier realTime;
   private final IntSupplier tickCount;
   private ReadableProfiler profiler = core.ProfilerHook.INSTANCE;

   public CustomTickTimeTracker(LongSupplier longsupplier, IntSupplier intsupplier) {
      super(longsupplier, intsupplier);
      this.realTime = longsupplier;
      this.tickCount = intsupplier;
   }

   public boolean isEnabled() {
      return this.profiler != core.ProfilerHook.INSTANCE;
   }

   public void disable() {
      this.profiler = DummyProfiler.INSTANCE;
   }

   public void enable() {
      this.profiler = core.ProfilerHook.INSTANCE;
   }

   public Profiler getProfiler() {
      return this.profiler;
   }

   public ProfileResult getResult() {
      return this.profiler.getResult();
   }
}
