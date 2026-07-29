import data.Angle;
import event.RotationEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import util.RotationUtil;

public class xx {
   public static final xx INSTANCE = new xx();
   private final MinecraftClient ajD = MinecraftClient.getInstance();
   private Angle BU = null;
   private Angle adY = null;
   private Angle Si = Angle.ZERO;
   private boolean LH = false;
   private boolean kX = false;
   private int aaM = 0;
   private static final int BE = 5;

   private xx() {
   }

   public void a(Angle angle) {
      if (angle == null) {
         if (this.BU != null) {
            this.kX = true;
            this.aaM = 0;
         }

         this.adY = this.BU != null ? this.BU : RotationUtil.getPlayerRotation();
      } else {
         this.adY = this.BU;
         this.LH = true;
         this.kX = false;
      }

      this.BU = angle;
   }

   public Angle b() {
      return this.BU != null ? this.BU : RotationUtil.getPlayerRotation();
   }

   public boolean c() {
      return this.LH || this.kX;
   }

   public void d(RotationEvent rotationevent) {
      if (this.kX) {
         if (this.ajD.player == null) {
            this.i();
         } else {
            this.aaM++;
            if (this.aaM >= 5) {
               this.BU = null;
               this.LH = false;
               this.kX = false;
               this.aaM = 0;
            } else {
               Angle angle = new Angle(this.ajD.player.getYaw(), this.ajD.player.getPitch());
               float f = this.aaM / 5.0F;
               float f1 = MathHelper.lerp(f, this.BU.getYaw(), angle.getYaw());
               float f2 = MathHelper.lerp(f, this.BU.getPitch(), angle.getPitch());
               rotationevent.setPitch(f2);
               rotationevent.setYaw(f1);
               rotationevent.setStrictMoveCorrection(true);
            }
         }
      } else if (this.LH && this.BU != null) {
         rotationevent.setPitch(this.BU.getPitch());
         rotationevent.setYaw(this.BU.getYaw());
         rotationevent.setStrictMoveCorrection(true);
      }
   }

   public void e(RotationEvent rotationevent, String s) {
      if (!s.equals("Нет") && this.LH) {
         if (this.ajD.player != null) {
            float f = rotationevent.getYaw();
            float f1 = rotationevent.getStaticYaw();
            float f2 = this.f(f, f1);
            float f3 = this.ajD.player.input.movementForward;
            float f4 = this.ajD.player.input.movementSideways;
            if (f3 != 0.0F || f4 != 0.0F) {
               double d0 = Math.toRadians(f2);
               double d1 = Math.cos(d0);
               double d2 = Math.sin(d0);
               float f5 = (float)(f3 * d1 - f4 * d2);
               float f6 = (float)(f3 * d2 + f4 * d1);
               this.ajD.player.input.movementForward = f5;
               this.ajD.player.input.movementSideways = f6;
            }
         }
      }
   }

   private float f(float f, float f1) {
      float f2 = f - f1;

      while (f2 > 180.0F) {
         f2 -= 360.0F;
      }

      while (f2 < -180.0F) {
         f2 += 360.0F;
      }

      return f2;
   }

   public static double g(Angle angle, Angle angle1) {
      return Math.hypot(Math.abs(h(angle.getYaw(), angle1.getYaw())), Math.abs(angle.getPitch() - angle1.getPitch()));
   }

   public static float h(float f, float f1) {
      return MathHelper.wrapDegrees(f - f1);
   }

   public void i() {
      this.BU = null;
      this.adY = null;
      this.LH = false;
      this.kX = false;
      this.aaM = 0;
   }

   public Angle j() {
      return this.Si;
   }

   public void k(Angle angle) {
      this.Si = angle;
   }
}
