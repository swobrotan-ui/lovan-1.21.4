import net.minecraft.util.math.Vec3d;

public class g {
   private String IF;
   private Vec3d ajc;
   private String Ox;
   private boolean aT;
   private long Cy;

   public g(String s, Vec3d vec3d, String s1) {
      this.IF = s;
      this.ajc = vec3d;
      this.Ox = s1;
      this.aT = true;
      this.Cy = System.currentTimeMillis();
   }

   public g(String s, double d0, double d1, double d2, String s1) {
      this(s, new Vec3d(d0, d1, d2), s1);
   }

   public double a() {
      return this.ajc.x;
   }

   public double b() {
      return this.ajc.y;
   }

   public double c() {
      return this.ajc.z;
   }

   public double d(Vec3d vec3d) {
      return this.ajc.distanceTo(vec3d);
   }

   @Override
   public String toString() {
      return String.format("%s [%.1f, %.1f, %.1f] (%s)", this.IF, this.a(), this.b(), this.c(), this.Ox);
   }

   public String e() {
      return this.IF;
   }

   public Vec3d f() {
      return this.ajc;
   }

   public String g() {
      return this.Ox;
   }

   public boolean h() {
      return this.aT;
   }

   public long i() {
      return this.Cy;
   }

   public void j(String s) {
      this.IF = s;
   }

   public void k(Vec3d vec3d) {
      this.ajc = vec3d;
   }

   public void l(String s) {
      this.Ox = s;
   }

   public void m(boolean flag) {
      this.aT = flag;
   }

   public void n(long i) {
      this.Cy = i;
   }
}
