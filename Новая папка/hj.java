import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class hj {
   private double ahw = 0.0;
   private double ali = 0.0;
   private boolean akC = true;
   MinecraftClient hD = MinecraftClient.getInstance();

   public void a(dp dp, float f) {
      if (this.hD.mouse != null && dp != null && this.hD.currentScreen == null) {
         long i = this.hD.getWindow().getHandle();
         double[] adouble = new double[1];
         double[] adouble1 = new double[1];
         GLFW.glfwGetCursorPos(i, adouble, adouble1);
         if (this.akC) {
            this.ahw = adouble[0];
            this.ali = adouble1[0];
            this.akC = false;
         }

         double d0 = adouble[0] - this.ahw;
         double d1 = adouble1[0] - this.ali;
         this.ahw = adouble[0];
         this.ali = adouble1[0];
         double d2 = (Double)this.hD.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
         double d3 = d2 * d2 * d2 * 8.0 * f;
         d0 *= d3;
         d1 *= d3;
         float f1 = dp.getYaw() + (float)d0 * 0.15F;
         float f2 = dp.getPitch() + (float)d1 * 0.15F;
         f2 = MathHelper.clamp(f2, -90.0F, 90.0F);
         dp.setYaw(f1);
         dp.setPitch(f2);
      }
   }

   public void b() {
      this.akC = true;
   }
}
