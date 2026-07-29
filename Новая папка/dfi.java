import core.ClientMain;
import module.TargetESPModule;

public class dfi {
   private long ahk;
   private float abE;
   private boolean axM;

   public void a() {
      if (this.axM) {
         this.abE = this.c();
      } else {
         this.abE = 0.0F;
      }

      this.ahk = System.currentTimeMillis();
      this.axM = true;
   }

   public void b() {
      if (this.axM) {
         TargetESPModule targetespmodule = ClientMain.getInstance().getModuleManager().<TargetESPModule>getModule(TargetESPModule.class);
         if (targetespmodule != null) {
            long i = System.currentTimeMillis() - this.ahk;
            long j = (long)targetespmodule.l().getValue();
            if (i >= j) {
               this.axM = false;
            }
         }
      }
   }

   public float c() {
      TargetESPModule targetespmodule = ClientMain.getInstance().getModuleManager().<TargetESPModule>getModule(TargetESPModule.class);
      if (targetespmodule == null) {
         return 0.0F;
      } else {
         long i = System.currentTimeMillis() - this.ahk;
         long j = (long)targetespmodule.l().getValue();
         float f = (float)targetespmodule.m().getValue() / 100.0F;
         long k = (long)((float)j * f);
         float f1;
         if (i < k) {
            float f2 = (float)i / (float)k;
            f1 = this.e(f2);
         } else {
            float f3 = (float)(i - k) / (float)(j - k);
            f1 = 1.0F - this.f(f3);
         }

         return this.d(this.abE, f1, Math.min(1.0F, (float)i / 100.0F));
      }
   }

   private float d(float f, float f1, float f2) {
      return f + (f1 - f) * f2;
   }

   private float e(float f) {
      return 1.0F - (float)Math.pow(1.0F - f, 3.0);
   }

   private float f(float f) {
      return (float)Math.pow(f, 3.0);
   }

   public long g() {
      return this.ahk;
   }

   public float h() {
      return this.abE;
   }

   public boolean i() {
      return this.axM;
   }
}
