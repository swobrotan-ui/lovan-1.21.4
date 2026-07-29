import gui.Component;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import setting.ActionKeySetting;
import setting.KeyBindSetting;

public class pl extends Component {
   private static final float y = 30.0F;
   private static final float lG = 21.0F;
   private static final float qX = 8.0F;
   private static final float gQ = 11.0F;
   private static final float axW = 4.0F;
   private static final float ahB = 100.0F;
   private final ActionKeySetting Vq;
   private final qm WE;
   private final float fw;
   private final Matrix4f aar = new Matrix4f();
   private BuiltRectangle acC;
   private boolean apm = false;
   private float Kn = 1.0F;
   private float bm = 1.0F;
   private String Eq;
   private float Gy;
   private int adI = 0;
   private boolean dp = false;

   public pl(float f, float f1, ActionKeySetting actionkeysetting) {
      super(f + 20.0F, f1, 30.0F, 21.0F);
      this.Vq = actionkeysetting;
      this.WE = new qm();
      this.WE.a();
      this.fw = f + 20.0F + 30.0F;
      this.a();
   }

   private void a() {
      String s = this.b();
      if (!s.equals(this.Eq)) {
         this.Eq = s;
         float f = this.aeN.c(s, 11.0F);
         this.Gy = Math.max(30.0F, f + 16.0F);
         this.qd = this.Gy;
         this.aP = this.fw - this.Gy;
         this.acC = new br().a(this.Gy, 21.0F).b(4.0F).a();
      }
   }

   private String b() {
      if (this.dp && this.adI != 0) {
         String s3 = this.c(this.adI);
         if (s3.length() > 10) {
            String s1 = s3.substring(0, 9);
            return s1 + "..";
         } else {
            return s3;
         }
      } else {
         String s = this.Vq.getFullKeyName();
         if (s.length() > 10) {
            String s2 = s.substring(0, 9);
            return s2 + "..";
         } else {
            return s;
         }
      }
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.a();
      this.WE.a(this.cH);
      this.WE.f();
      if (!this.apm || this.dp) {
         this.bm = 1.0F;
      } else if (this.Kn <= 0.05F) {
         this.bm = 1.0F;
      } else if (this.Kn >= 0.95F) {
         this.bm = 0.0F;
      }

      if (this.Kn < this.bm) {
         this.Kn = Math.min(1.0F, this.Kn + f2 * 100.0F);
      } else if (this.Kn > this.bm) {
         this.Kn = Math.max(0.0F, this.Kn - f2 * 100.0F);
      }

      Matrix4f matrix4f1 = this.d(matrix4f, f, f1, this.WE.d());
      this.acC.a(matrix4f1, f, f1, f3);
      BuiltText builttext = this.k(this.aeN, this.Eq, 11.0F, Bz);
      float f4 = this.aeN.c(this.Eq, 11.0F);
      float f5 = this.aeN.e().d() * 11.0F;
      float f6 = f + (this.Gy - f4) / 2.0F;
      float f7 = f1 + (21.0F - f5) / 2.0F;
      builttext.a(matrix4f1, f6, f7 - 1.0F, f3 * this.Kn);
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (!this.apm) {
         if (i == 0) {
            this.apm = true;
            this.dp = false;
            this.adI = 0;
            this.bm = 0.0F;
            this.WE.b(true);
            return true;
         } else {
            return false;
         }
      } else {
         if (this.dp && this.adI != 0) {
            this.Vq.setKeyAndModifiers(i, this.adI);
         } else {
            this.Vq.setKeyCode(i);
         }

         this.apm = false;
         this.dp = false;
         this.adI = 0;
         this.bm = 1.0F;
         this.a();
         return true;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0) {
         this.WE.b(false);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean f(int i, int j, int k) {
      if (!this.apm) {
         return false;
      } else if (i == 256) {
         this.apm = false;
         this.dp = false;
         this.adI = 0;
         this.bm = 1.0F;
         this.a();
         return true;
      } else if (i == 261) {
         this.Vq.setKeyCode(-1);
         this.apm = false;
         this.dp = false;
         this.adI = 0;
         this.bm = 1.0F;
         this.a();
         return true;
      } else if (KeyBindSetting.isModifierKey(i)) {
         this.adI = this.adI | KeyBindSetting.getModifierFlag(i);
         this.dp = true;
         this.a();
         return true;
      } else {
         if (this.dp && this.adI != 0) {
            this.Vq.setKeyAndModifiers(i, this.adI);
         } else {
            this.Vq.setKeyCode(i);
         }

         this.apm = false;
         this.dp = false;
         this.adI = 0;
         this.bm = 1.0F;
         this.a();
         return true;
      }
   }

   @Override
   protected boolean g(int i, int l, int i1) {
      if (!this.apm || !this.dp) {
         return false;
      } else if (KeyBindSetting.isModifierKey(i)) {
         int j = KeyBindSetting.getModifierFlag(i);
         int k = this.adI & ~j;
         if (k == 0) {
            this.Vq.setKeyCode(i);
            this.apm = false;
            this.dp = false;
            this.adI = 0;
            this.bm = 1.0F;
            this.a();
         } else {
            this.adI = k;
            this.a();
         }

         return true;
      } else {
         return false;
      }
   }

   private String c(int i) {
      StringBuilder stringbuilder = new StringBuilder();
      if ((i & 2) != 0) {
         stringbuilder.append("CTRL + ");
      }

      if ((i & 1) != 0) {
         stringbuilder.append("SHIFT + ");
      }

      if ((i & 4) != 0) {
         stringbuilder.append("ALT + ");
      }

      if (!stringbuilder.isEmpty()) {
         stringbuilder.setLength(stringbuilder.length() - 3);
      }

      return stringbuilder.toString();
   }

   private Matrix4f d(Matrix4f matrix4f, float f, float f1, float f2) {
      if (f2 == 1.0F) {
         return matrix4f;
      } else {
         float f3 = f + this.qd / 2.0F;
         float f4 = f1 + this.aem / 2.0F;
         this.aar.set(matrix4f);
         this.aar.translate(f3, f4, 0.0F);
         this.aar.scale(f2, f2, 1.0F);
         this.aar.translate(-f3, -f4, 0.0F);
         return this.aar;
      }
   }

   public boolean e() {
      return this.apm;
   }
}
