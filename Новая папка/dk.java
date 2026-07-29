public class dk extends vjv {
   private static final float hR = 0.3F;
   private static final float apQ = 0.15F;
   private static final float awp = 8.0F;
   private static final float auy = 1.07F;
   private static final float ql = 0.9F;
   private static final float awJ = 1.0F;
   private static final float Tu = 1.0F;
   private static final float Az = 0.3F;
   private static final float TF = 0.7F;
   private r awv = r.fy;
   private float cd = 0.0F;
   private float gf = 1.0F;
   private float akn = 1.0F;
   private float Ru = 0.3F;
   private float br = 0.3F;
   private boolean xU = false;
   private boolean Is = false;
   private boolean Dc = false;

   public void a(boolean flag) {
      if (this.xU != flag && !this.Is) {
         this.xU = flag;
         this.d();
         this.e(flag ? r.fQ : r.fy);
      }
   }

   public void b(boolean flag) {
      if (this.Is != flag) {
         this.Is = flag;
         this.e(flag ? r.oJ : r.Q);
      }
   }

   public void c(boolean flag) {
      if (this.Dc != flag) {
         this.Dc = flag;
         this.d();
      }
   }

   private void d() {
      this.br = this.Dc ? 1.0F : (this.xU ? 0.7F : 0.3F);
   }

   private void e(r r) {
      this.awv = r;
      this.gf = this.akn;
      this.cd = 0.0F;
      this.wI = System.nanoTime();
   }

   @Override
   public void f() {
      float f = this.b();
      this.cd += f;
      this.g();
      this.h(f);
   }

   private void g() {
      float f;
      float f1;
      switch (this.awv) {
         case fy:
            f = 1.0F;
            f1 = 0.3F;
            break;
         case fQ:
            f = 1.07F;
            f1 = 0.3F;
            break;
         case oJ:
            f = 0.9F;
            f1 = 0.15F;
            break;
         case Q:
            f = this.xU ? 1.07F : 1.0F;
            f1 = 0.15F;
            break;
         default:
            return;
      }

      float f2 = Math.min(this.cd / f1, 1.0F);
      float f3 = this.c(f2);
      this.akn = this.gf + (f - this.gf) * f3;
      if (f2 >= 1.0F) {
         this.akn = f;
         if (this.awv == r.Q) {
            this.awv = this.xU ? r.fQ : r.fy;
         }
      }
   }

   private void h(float f) {
      float f1 = this.br - this.Ru;
      if (Math.abs(f1) > 0.001F) {
         this.Ru += f1 * 8.0F * f;
         if (Math.abs(this.br - this.Ru) < 0.01F) {
            this.Ru = this.br;
         }

         this.Ru = Math.max(0.0F, Math.min(1.0F, this.Ru));
      } else {
         this.Ru = this.br;
      }
   }

   public float i() {
      return (this.Ru - 0.3F) / 0.7F;
   }

   @Override
   public boolean g() {
      return this.awv != r.fy || Math.abs(this.akn - 1.0F) > 0.001F || Math.abs(this.Ru - this.br) > 0.001F;
   }

   @Override
   public void h() {
      this.awv = r.fy;
      this.cd = 0.0F;
      this.akn = 1.0F;
      this.gf = 1.0F;
      this.Ru = 0.3F;
      this.br = 0.3F;
      this.xU = false;
      this.Is = false;
      this.Dc = false;
      this.wI = System.nanoTime();
   }

   public float j() {
      return this.akn;
   }

   public float k() {
      return this.Ru;
   }
}
