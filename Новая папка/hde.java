import net.minecraft.client.render.FrameGraphBuilder.Profiler;
import render.CustomWorldRenderer;

class hde implements Profiler {
   // $VF: synthetic field
   final net.minecraft.util.profiler.Profiler val$profiler;

   hde(CustomWorldRenderer customworldrenderer, net.minecraft.util.profiler.Profiler profiler) {
      this.val$profiler = profiler;
   }

   public void push(String s) {
      this.val$profiler.push(s);
   }

   public void pop(String s) {
      this.val$profiler.pop();
   }
}
