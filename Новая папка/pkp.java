import gui.Component;
import org.joml.Matrix4f;
import render.BuiltBlur;
import render.BuiltRectangle;
import render.BuiltText;
import render.RadiusConfig;
import render.RectangleCache;
import render.Size;
import setting.ActionKeySetting;

public class pkp extends Component {
   private static final float IJ = 213.0F;
   private static final float aAa = 20.0F;
   private static final float Nf = 91.0F;
   private static final float auI = 37.0F;
   private static final float afU = 13.0F;
   private static final float TY = 4.0F;
   private static final float xi = 4.0F;
   private static final float oe = 12.0F;
   private static final float zv = 1.0F;
   private static final float ia = 0.001F;
   private static final float axE = 0.2F;
   private final float apj;
   private final float Xf;
   private final ActionKeySetting azc;
   private final fp avu;
   private BuiltBlur IX;
   private final BuiltRectangle Bu;
   private boolean acE = false;
   private boolean vn = true;
   private boolean ky = false;
   private float p = 0.0F;
   private float Er = 0.0F;
   private String aok = "";

   public pkp(float f, float f1, float f2, float f3, ActionKeySetting actionkeysetting) {
      super(f, f1, f2, f3);
      this.apj = f2;
      this.Xf = f3;
      this.azc = actionkeysetting;
      this.avu = new fp();
      this.avu.a();
      this.Bu = new br().a(213.0F, 20.0F).b(4.0F).a();
   }

   private BuiltBlur a(float f) {
      return f < 0.001F ? null : new hvg().a(new Size(this.apj + 2.0F, this.Xf + 2.0F)).b(new RadiusConfig(12.0F)).d(1.0F).e(f).a();
   }

   public void b() {
      this.avu.c(() -> {
         this.acE = false;
         this.vn = true;
         this.ky = false;
         this.p = 0.0F;
         this.Er = 0.0F;
         this.IX = null;
      });
   }

   public boolean c() {
      return this.acE || this.avu.g();
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      if (this.acE || this.avu.g()) {
         this.avu.f();
         float f4 = this.avu.e();
         float f5 = this.avu.f();
         if (!(f4 < 0.01F)) {
            if (f5 > 0.001F) {
               this.IX = this.a(f5);
            }

            if (this.IX != null && f5 > 0.001F) {
               this.IX.render(matrix4f, f, f1, 0.0F);
            }

            float f6 = f + 37.0F;
            float f7 = f1 + 91.0F;
            if (this.vn && !this.ky) {
               this.Bu.a(matrix4f, f6, f7, f4 * f3);
               String s = "Press any button to bind...";
               BuiltText builttext1 = this.k(this.aeN, s, 13.0F, Bz);
               this.Er += f2 * 0.2F;
               float f15 = (float)((Math.sin(this.Er * Math.PI) + 1.0) / 2.0);
               float f16 = this.aeN.c(s, 13.0F);
               float f17 = f6 + (213.0F - f16) / 2.0F;
               float f18 = f7 + 3.5F + 2.0F;
               builttext1.a(matrix4f, f17, f18 - 2.0F, f4 * f3 * f15);
            } else {
               if (this.ky) {
                  BuiltText builttext = this.k(this.aeN, this.aok, 13.0F, Bz);
                  float f8 = this.aeN.c(this.aok, 13.0F);
                  float f9 = f8 + 8.0F;
                  float f10 = 21.0F;
                  float f11 = f + (this.apj - f9) / 2.0F;
                  float f12 = f1 + (this.Xf - f10) / 2.0F;
                  RectangleCache.b(f9, f10, 4.0F).a(matrix4f, f11, f12, f4 * f3);
                  float f13 = f11 + 4.0F;
                  float f14 = f12 + 4.0F;
                  builttext.a(matrix4f, f13, f14, f4 * f3);
               }
            }
         }
      }
   }

   @Override
   protected void i(float f) {
      if (this.ky && this.acE && !this.avu.g()) {
         this.p += f;
         if (this.p >= 1.0F) {
            this.b();
         }
      }
   }

   @Override
   protected boolean f(int i, int j, int k) {
      if (!this.acE || !this.vn) {
         return false;
      } else if (i == 256) {
         this.b();
         return true;
      } else if (i == 261) {
         this.azc.setKeyCode(-1);
         this.aok = amu[1];
         this.ky = true;
         this.vn = false;
         this.p = 0.0F;
         return true;
      } else {
         this.azc.setKeyCode(i);
         this.aok = this.azc.formatKey(i);
         this.ky = true;
         this.vn = false;
         this.p = 0.0F;
         return true;
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (!this.acE) {
         return false;
      } else if (this.vn && !this.ky) {
         this.azc.setKeyCode(i);
         this.aok = this.azc.formatKey(i);
         this.ky = true;
         this.vn = false;
         this.p = 0.0F;
         return true;
      } else {
         return true;
      }
   }
}
