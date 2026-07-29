import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

public class rg {
   public final Vec3d VS;
   public final float AZ;
   public final float Sd;
   public final double avF;
   public final double ajW;
   public final double auw;
   public final double ayT;

   public rg(Camera camera, float f) {
      this.VS = camera.getPos();
      this.AZ = camera.getPitch();
      this.Sd = camera.getYaw();
      float f1 = (float)Math.cos(Math.toRadians(this.AZ));
      float f2 = (float)Math.sin(Math.toRadians(this.AZ));
      float f3 = (float)Math.cos(Math.toRadians(this.Sd + 90.0F));
      float f4 = (float)Math.sin(Math.toRadians(this.Sd + 90.0F));
      this.avF = f1 * f3;
      this.ajW = -f2;
      this.auw = f1 * f4;
      this.ayT = Math.cos(Math.toRadians(f / 2.0 + 20.0));
   }

   public static rg a(MinecraftClient minecraftclient) {
      Camera camera = minecraftclient.getEntityRenderDispatcher().camera;
      float f = (float)((Integer)minecraftclient.options.getFov().getValue()).intValue();
      return new rg(camera, f);
   }
}
