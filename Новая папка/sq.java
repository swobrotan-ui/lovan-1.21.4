import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Vec3d;

public class sq {
   public double Hg;
   public double lW;
   public double ath;
   public double KO;
   public double Wp;
   public double MM;
   public double atT;
   public double QV;
   public double hS;
   public double Ss;
   public double qM;
   public double Lj;
   public final long Yx;
   public final long Vm;
   public float afi;
   public String adU;
   public List<Vec3d> asV = new ArrayList<Vec3d>();
   public int Qs = 55;

   public sq(Vec3d vec3d, Vec3d vec3d1, Vec3d vec3d2, Vec3d vec3d3, long i, float f) {
      this(vec3d, vec3d1, vec3d2, vec3d3, i, f, "default");
   }

   public sq(Vec3d vec3d, Vec3d vec3d1, Vec3d vec3d2, Vec3d vec3d3, long i, float f, String s) {
      this.Hg = vec3d.x;
      this.lW = vec3d.y;
      this.ath = vec3d.z;
      this.KO = vec3d2.x;
      this.Wp = vec3d2.y;
      this.MM = vec3d2.z;
      this.atT = vec3d1.x;
      this.QV = vec3d1.y;
      this.hS = vec3d1.z;
      this.Ss = vec3d3.x;
      this.qM = vec3d3.y;
      this.Lj = vec3d3.z;
      this.Vm = i;
      this.afi = f;
      this.adU = s;
      this.Yx = System.currentTimeMillis();
   }

   public void a(float f) {
      if (this.adU.equals("comet")) {
         this.asV.add(new Vec3d(this.Hg, this.lW, this.ath));
         if (this.asV.size() > this.Qs) {
            this.asV.remove(0);
         }

         this.Wp -= 0.4 * f;
      } else if (!this.adU.equals("lightning")) {
         this.KO *= 0.98;
         this.Wp *= 0.98;
         this.MM *= 0.98;
         this.Ss *= 0.98;
         this.qM *= 0.98;
         this.Lj *= 0.98;
      }

      if (!this.adU.equals("lightning")) {
         this.Hg = this.Hg + this.KO * f;
         this.lW = this.lW + this.Wp * f;
         this.ath = this.ath + this.MM * f;
      }

      this.atT = this.atT + this.Ss * f;
      this.QV = this.QV + this.qM * f;
      this.hS = this.hS + this.Lj * f;
   }
}
