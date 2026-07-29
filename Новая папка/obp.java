import module.HitboxTweaksModule;

public class obp {
   private final HitboxTweaksModule abm;
   private long qT;
   private float dv;
   private boolean Mz;

   public obp(HitboxTweaksModule hitboxtweaksmodule) {
      this.abm = hitboxtweaksmodule;
   }

   public void a() {
      if (this.Mz) {
         this.dv = this.c();
      } else {
         this.dv = 0.0F;
      }

      this.qT = System.currentTimeMillis();
      this.Mz = true;
   }

   public void b() {
      if (this.Mz) {
         long i = System.currentTimeMillis() - this.qT;
         long j = (long)this.abm.timeSetting.getValue();
         if (i >= j) {
            this.Mz = false;
         }
      }
   }

   public float c() {
      long i = System.currentTimeMillis() - this.qT;
      long j = (long)this.abm.timeSetting.getValue();
      float f = (float)this.abm.fadeStartSetting.getValue() / 100.0F;
      long k = (long)((float)j * f);
      float f1;
      if (i < k) {
         float f2 = (float)i / (float)k;
         f1 = this.f(f2);
      } else {
         float f3 = (float)(i - k) / (float)(j - k);
         f1 = 1.0F - this.g(f3);
      }

      return this.e(this.dv, f1, Math.min(1.0F, (float)i / 100.0F));
   }

   public float d() {
      long i = System.currentTimeMillis() - this.qT;
      long j = (long)this.abm.timeSetting.getValue();
      float f = this.abm.f();
      long k = j / 4L;
      float f1;
      if (i < k) {
         float f2 = (float)i / (float)k;
         f1 = this.f(f2) * f;
      } else {
         float f3 = (float)(i - k) / (float)(j - k);
         f1 = (1.0F - this.g(f3)) * f;
      }

      float f4 = this.dv * f;
      return this.e(f4, f1, Math.min(1.0F, (float)i / 100.0F));
   }

   private float e(float f, float f1, float f2) {
      return f + (f1 - f) * f2;
   }

   private float f(float f) {
      return 1.0F - (float)Math.pow(1.0F - f, 3.0);
   }

   private float g(float f) {
      return (float)Math.pow(f, 3.0);
   }

   public boolean h() {
      return this.Mz;
   }
}
