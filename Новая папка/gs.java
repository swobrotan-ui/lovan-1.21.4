import gui.Component;
import org.joml.Matrix4f;
import render.BuiltLiquidGlass;
import render.BuiltText;
import render.RadiusConfig;
import render.RectangleCache;
import render.Size;
import setting.KeyBindSetting;

public class gs extends Component {
   private static final float ajV = 177.0F;
   private static final float arh = 20.0F;
   private static final float abW = 91.0F;
   private static final float PP = 37.0F;
   private static final float arT = 13.0F;
   private static final float oY = 4.0F;
   private static final float afh = 4.0F;
   private static final float pD = 12.0F;
   private static final float aaj = 1.0F;
   private static final float dN = 0.001F;
   private final float st;
   private final float NC;
   private final KeyBindSetting pv;
   private final fp aib;
   private BuiltLiquidGlass axs;
   private boolean aaC = false;
   private boolean ww = true;
   private boolean No = false;
   private float aqe = 0.0F;
   private String VA = "";
   private int Mh = 0;
   private boolean atl = false;

   public gs(float f, float f1, float f2, float f3, KeyBindSetting keybindsetting) {
      super(f, f1, f2, f3);
      this.st = f2;
      this.NC = f3;
      this.pv = keybindsetting;
      this.aib = new fp();
      this.aib.a();
   }

   private BuiltLiquidGlass a(float f) {
      return f < 0.001F ? null : new u().a(new Size(this.st, this.NC)).b(new RadiusConfig(12.0F)).d(1.0F).f(50.0F).g(1.0F).h(10.0F).a();
   }

   public void b() {
      this.aaC = true;
      this.ww = true;
      this.No = false;
      this.aqe = 0.0F;
      this.VA = "";
      this.Mh = 0;
      this.atl = false;
      this.aib.b();
   }

   public void c() {
      this.aib.c(() -> {
         this.aaC = false;
         this.ww = true;
         this.No = false;
         this.aqe = 0.0F;
         this.axs = null;
         this.Mh = 0;
         this.atl = false;
      });
   }

   public boolean d() {
      return this.aaC || this.aib.g();
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f18, float f2) {
      if (this.aaC || this.aib.g()) {
         this.aib.f();
         float f3 = this.aib.e();
         float f4 = this.aib.f();
         float f5 = f3 * f2;
         if (!(f5 < 0.01F)) {
            if (f4 > 0.001F) {
               this.axs = this.a(f5);
               if (this.axs != null) {
                  this.axs.a(matrix4f, f, f1, f5);
               }
            }

            float f6 = f + 37.0F;
            float f7 = f1 + 91.0F;
            if (this.ww && !this.No) {
               RectangleCache.b(177.0F, 20.0F, 4.0F).a(matrix4f, f6 + 15.0F, f7, f5);
               String s1;
               if (this.atl) {
                  String s = this.e(this.Mh);
                  s1 = s + "...";
               } else {
                  s1 = "Press any button to bind...";
               }

               BuiltText builttext1 = this.k(this.nO, s1, 13.0F, Bz);
               float f15 = this.nO.c(s1, 13.0F);
               float f16 = f6 + (177.0F - f15) / 2.0F;
               float f17 = f7 + 3.5F + 2.0F;
               builttext1.a(matrix4f, f16 + 20.0F, f17 - 2.0F, f5);
            } else {
               if (this.No) {
                  BuiltText builttext = this.k(this.aeN, this.VA, 13.0F, Bz);
                  float f8 = this.aeN.c(this.VA, 13.0F);
                  float f9 = f8 + 8.0F;
                  float f10 = 21.0F;
                  float f11 = f + (this.st - f9) / 2.0F;
                  float f12 = f1 + (this.NC - f10) / 2.0F;
                  RectangleCache.b(f9, f10, 4.0F).a(matrix4f, f11, f12, f5);
                  float f13 = f11 + 4.0F;
                  float f14 = f12 + 4.0F;
                  builttext.a(matrix4f, f13, f14, f5);
               }
            }
         }
      }
   }

   @Override
   protected void i(float f) {
      if (this.No && this.aaC && !this.aib.g()) {
         this.aqe += f;
         if (this.aqe >= 1.0F) {
            this.c();
         }
      }
   }

   @Override
   protected boolean f(int i, int j, int k) {
      if (!this.aaC || !this.ww) {
         return false;
      } else if (i == 256) {
         this.Mh = 0;
         this.atl = false;
         this.c();
         return true;
      } else if (i == 261) {
         this.pv.setKeyCode(-1);
         this.VA = "None";
         this.No = true;
         this.ww = false;
         this.aqe = 0.0F;
         this.Mh = 0;
         this.atl = false;
         return true;
      } else if (KeyBindSetting.isModifierKey(i)) {
         this.Mh = this.Mh | KeyBindSetting.getModifierFlag(i);
         this.atl = true;
         return true;
      } else {
         if (this.atl && this.Mh != 0) {
            this.pv.setKeyAndModifiers(i, this.Mh);
            this.VA = this.pv.formatKeyName(i, this.Mh);
         } else {
            this.pv.setKeyCode(i);
            this.VA = this.pv.formatKey(i);
         }

         this.No = true;
         this.ww = false;
         this.aqe = 0.0F;
         this.Mh = 0;
         this.atl = false;
         return true;
      }
   }

   @Override
   protected boolean g(int i, int l, int i1) {
      if (!this.aaC || !this.ww || !this.atl) {
         return false;
      } else if (KeyBindSetting.isModifierKey(i)) {
         int j = KeyBindSetting.getModifierFlag(i);
         int k = this.Mh & ~j;
         if (k == 0) {
            this.pv.setKeyCode(i);
            this.VA = this.pv.formatKey(i);
            this.No = true;
            this.ww = false;
            this.aqe = 0.0F;
            this.Mh = 0;
            this.atl = false;
         } else {
            this.Mh = k;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (!this.aaC) {
         return false;
      } else if (this.ww && !this.No) {
         if (this.atl && this.Mh != 0) {
            this.pv.setKeyAndModifiers(i, this.Mh);
            this.VA = this.pv.formatKeyName(i, this.Mh);
         } else {
            this.pv.setKeyCode(i);
            this.VA = this.pv.formatKey(i);
         }

         this.No = true;
         this.ww = false;
         this.aqe = 0.0F;
         this.Mh = 0;
         this.atl = false;
         return true;
      } else {
         return true;
      }
   }

   private String e(int i) {
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
}
