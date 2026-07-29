import core.Localization;
import gui.Component;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;

public class mi extends Component {
   private static final float Ai = 16.0F;
   private static final float acO = 384.0F;
   private static final float dU = 30.0F;
   private static final float Xl = 7.0F;
   private static final float ru = 10.0F;
   private static final long adn = 500L;
   private static final float ed = 8.0F;
   private static final float kY = 3.0F;
   private static final float JL = 3.0F;
   private static final float azJ = 0.3F;
   private static final float ph = 14.0F;
   private static final float Cw = 20.0F;
   private static final float Gp = 7.0F;
   private static final float bf = 5.0F;
   private ws es = ws.tg;
   private ws ayu = ws.tg;
   private float Bc = 1.0F;
   private String Jq = "";
   private boolean oL = false;
   private int Ds = 0;
   private int BJ = 0;
   private int gn = 0;
   private float mG = 1.0F;
   private float en = 1.0F;
   private float bi = 0.0F;
   private float hv = 0.0F;
   private long WD = System.currentTimeMillis();
   private Consumer<String> dd;

   public mi(float f, float f1) {
      super(f, f1, 384.0F, 30.0F);
   }

   public void a(ws ws) {
      this.b(ws, false);
   }

   public void b(ws ws, boolean flag) {
      if (this.es != ws) {
         this.ayu = ws;
         if (flag) {
            this.es = ws;
            this.Bc = 1.0F;
            return;
         }

         this.Bc = 0.0F;
      }
   }

   public void c(boolean flag) {
      this.anm = flag;
      if (flag) {
         this.en = 1.0F;
      }
   }

   public void d() {
      this.en = 0.0F;
   }

   public void e() {
      this.mG = 0.0F;
      this.en = 1.0F;
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f15, float f2) {
      this.f();
      float f3 = f2 * this.mG;
      BuiltRectangle builtrectangle = RectangleCache.b(384.0F, 30.0F, 8.0F);
      builtrectangle.a(matrix4f, f, f1, f3);
      if (this.bi > 0.01F) {
         RectangleCache.b(384.0F, 30.0F, 8.0F).a(matrix4f, f, f1, this.bi * this.mG);
      }

      float f4 = f + 7.0F;
      float f5 = f1 + 6.5F;
      String s = this.es.b();
      float f6;
      if (this.Bc < 0.5F) {
         f6 = 1.0F - this.Bc * 2.0F;
      } else {
         f6 = (this.Bc - 0.5F) * 2.0F;
      }

      BuiltText builttext = TextCache.a(this.aiL, s, 16.0F, Bz);
      builttext.a(matrix4f, f4, f5, f3 * f6);
      float f7 = f1 + (30.0F - this.ayW.e().d() * 16.0F) / 2.0F;
      float f8 = f + 7.0F + 16.0F + 10.0F;
      if (this.Jq.isEmpty() && !this.oL) {
         String s1 = Localization.a().c(this.es.c());
         float f9;
         if (this.Bc < 0.5F) {
            f9 = 1.0F - this.Bc * 2.0F;
         } else {
            f9 = (this.Bc - 0.5F) * 2.0F;
         }

         BuiltText builttext2 = TextCache.a(this.ayW, s1, 16.0F, hp);
         builttext2.a(matrix4f, f8, f7, f3 * f9);
      } else if (!this.Jq.isEmpty()) {
         if (this.i()) {
            this.g(matrix4f, f8, f1, f3);
         }

         BuiltText builttext1 = TextCache.a(this.ayW, this.Jq, 16.0F, Bz);
         builttext1.a(matrix4f, f8, f7, f3);
      }

      if (this.oL && System.currentTimeMillis() / 500L % 2L == 0L) {
         float f12 = f8 + this.h();
         TextCache.a(this.ayW, "|", 16.0F, Bz).a(matrix4f, f12, f7, f3);
      }

      float f13 = f + 384.0F - 20.0F - 7.0F;
      float f14 = f1 + 5.0F;
      BuiltRectangle builtrectangle1 = RectangleCache.b(20.0F, 20.0F, 4.0F);
      builtrectangle1.a(matrix4f, f13, f14, f3);
      float f10 = f13 + 3.0F - 0.5F;
      float f11 = f14 + 3.0F - 0.5F;
      BuiltText builttext3 = TextCache.a(this.aiL, "R", 14.0F, Bz);
      builttext3.a(matrix4f, f10, f11, f3);
   }

   private void f() {
      long i = System.currentTimeMillis();
      float f = (float)(i - this.WD) / 1000.0F;
      this.WD = i;
      float f1 = this.en - this.mG;
      if (Math.abs(f1) > 0.001F) {
         this.mG += f1 * 3.0F * f;
         this.mG = Math.max(0.0F, Math.min(1.0F, this.mG));
      } else {
         this.mG = this.en;
      }

      float f2 = this.hv - this.bi;
      if (Math.abs(f2) > 0.001F) {
         this.bi += f2 * 8.0F * f;
         this.bi = Math.max(0.0F, Math.min(1.0F, this.bi));
      } else {
         this.bi = this.hv;
      }

      if (this.Bc < 1.0F) {
         this.Bc += 3.0F * f;
         if (this.Bc >= 0.5F && this.es != this.ayu) {
            this.es = this.ayu;
         }

         if (this.Bc >= 1.0F) {
            this.Bc = 1.0F;
         }
      }

      if (this.mG <= 0.001F && !this.anm) {
         this.anm = false;
      }
   }

   private void g(Matrix4f matrix4f, float f, float f1, float f2) {
      int i = Math.min(this.BJ, this.gn);
      int j = Math.max(this.BJ, this.gn);
      String s = this.Jq.substring(0, i);
      String s1 = this.Jq.substring(i, j);
      float f3 = f + this.ayW.c(s, 16.0F);
      float f4 = this.ayW.c(s1, 16.0F);
      BuiltRectangle builtrectangle = RectangleCache.b(f4, 26.0F, 2.0F);
      builtrectangle.a(matrix4f, f3, f1 + 2.0F, 0.3F * f2);
   }

   private float h() {
      if (!this.Jq.isEmpty() && this.Ds > 0) {
         String s = this.Jq.substring(0, Math.min(this.Ds, this.Jq.length()));
         return this.ayW.c(s, 16.0F);
      } else {
         return 0.0F;
      }
   }

   private boolean i() {
      return this.BJ != this.gn;
   }

   @Override
   public boolean mouseClicked(double d0, double d1, int i) {
      if (!this.wA || i != 0) {
         return false;
      } else if (this.r(d0, d1, this.it, this.atW)) {
         if (!this.oL) {
            this.oL = true;
            this.hv = 1.0F;
            this.Ds = this.Jq.length();
         } else {
            float f = this.it + 7.0F + 16.0F + 10.0F;
            float f1 = (float)d0 - f;
            this.Ds = this.j(f1);
         }

         this.k();
         return true;
      } else {
         if (this.oL) {
            this.oL = false;
            this.hv = 0.0F;
            this.k();
         }

         return false;
      }
   }

   private int j(float f) {
      if (!this.Jq.isEmpty() && !(f <= 0.0F)) {
         float f1 = 0.0F;

         for (int i = 0; i < this.Jq.length(); i++) {
            String s = this.Jq.substring(0, i + 1);
            float f2 = this.ayW.c(s, 16.0F);
            if (f2 > f) {
               float f3 = (f1 + f2) / 2.0F;
               if (f < f3) {
                  return i;
               }

               return i + 1;
            }

            f1 = f2;
         }

         return this.Jq.length();
      } else {
         return 0;
      }
   }

   private void k() {
      this.BJ = this.Ds;
      this.gn = this.Ds;
   }

   @Override
   public boolean charTyped(char c0, int i) {
      if (this.wA && this.oL) {
         if (this.i()) {
            this.l();
         }

         String s = this.Jq.substring(0, this.Ds);
         String s1 = this.Jq.substring(this.Ds);
         this.Jq = s + c0 + s1;
         this.Ds++;
         this.k();
         this.m();
         return true;
      } else {
         return false;
      }
   }

   private void l() {
      int i = Math.min(this.BJ, this.gn);
      int j = Math.max(this.BJ, this.gn);
      String s = this.Jq.substring(0, i);
      String s1 = this.Jq.substring(j);
      this.Jq = s + s1;
      this.Ds = i;
      this.k();
   }

   private void m() {
      this.hv = 1.0F;
   }

   @Override
   public boolean keyPressed(int i, int k, int j) {
      if (this.wA && this.oL) {
         boolean flag = (j & 2) != 0;
         boolean flag1 = (j & 1) != 0;
         if (flag) {
            return this.n(i);
         } else {
            switch (i) {
               case 256:
                  this.Jq = "";
                  this.Ds = 0;
                  this.oL = false;
                  this.hv = 0.0F;
                  this.k();
                  return true;
               case 257:
                  this.o();
                  return true;
               case 258:
               case 260:
               case 264:
               case 265:
               case 266:
               case 267:
               default:
                  return false;
               case 259:
                  if (this.i()) {
                     this.l();
                  } else if (this.Ds > 0) {
                     String s2 = this.Jq.substring(0, this.Ds - 1);
                     String s3 = this.Jq.substring(this.Ds);
                     this.Jq = s2 + s3;
                     this.Ds--;
                  }

                  this.m();
                  return true;
               case 261:
                  if (this.i()) {
                     this.l();
                  } else if (this.Ds < this.Jq.length()) {
                     String s = this.Jq.substring(0, this.Ds);
                     String s1 = this.Jq.substring(this.Ds + 1);
                     this.Jq = s + s1;
                  }

                  this.m();
                  return true;
               case 262:
                  if (flag1) {
                     if (this.Ds < this.Jq.length()) {
                        this.Ds++;
                        this.gn = this.Ds;
                     }
                  } else if (this.i()) {
                     this.Ds = Math.max(this.BJ, this.gn);
                     this.k();
                  } else if (this.Ds < this.Jq.length()) {
                     this.Ds++;
                     this.k();
                  }

                  return true;
               case 263:
                  if (flag1) {
                     if (this.Ds > 0) {
                        this.Ds--;
                        this.gn = this.Ds;
                     }
                  } else if (this.i()) {
                     this.Ds = Math.min(this.BJ, this.gn);
                     this.k();
                  } else if (this.Ds > 0) {
                     this.Ds--;
                     this.k();
                  }

                  return true;
               case 268:
                  if (flag1) {
                     this.gn = 0;
                     this.Ds = 0;
                  } else {
                     this.Ds = 0;
                     this.k();
                  }

                  return true;
               case 269:
                  if (flag1) {
                     this.gn = this.Jq.length();
                     this.Ds = this.Jq.length();
                  } else {
                     this.Ds = this.Jq.length();
                     this.k();
                  }

                  return true;
            }
         }
      } else {
         return false;
      }
   }

   private boolean n(int i) {
      switch (i) {
         case 65:
            this.BJ = 0;
            this.gn = this.Jq.length();
            this.Ds = this.Jq.length();
            return true;
         case 67:
            if (this.i()) {
               this.p();
            }

            return true;
         case 86:
            String s = this.q();
            if (s != null && !s.isEmpty()) {
               if (this.i()) {
                  this.l();
               }

               String s1 = this.Jq.substring(0, this.Ds);
               String s2 = this.Jq.substring(this.Ds);
               this.Jq = s1 + s + s2;
               this.Ds = this.Ds + s.length();
               this.k();
               this.m();
            }

            return true;
         case 88:
            if (this.i()) {
               this.p();
               this.l();
            }

            return true;
         default:
            return false;
      }
   }

   private void o() {
      if (!this.Jq.isEmpty()) {
         if (this.dd != null) {
            this.dd.accept(this.Jq.trim());
         }

         this.Jq = "";
         this.Ds = 0;
         this.k();
      }
   }

   private void p() {
      if (this.i()) {
         int i = Math.min(this.BJ, this.gn);
         int j = Math.max(this.BJ, this.gn);
         String s = this.Jq.substring(i, j);

         try {
            GLFW.glfwSetClipboardString(MinecraftClient.getInstance().getWindow().getHandle(), s);
         } catch (Exception exception) {
         }
      }
   }

   private String q() {
      try {
         return GLFW.glfwGetClipboardString(MinecraftClient.getInstance().getWindow().getHandle());
      } catch (Exception exception) {
         return null;
      }
   }

   private boolean r(double d0, double d1, float f, float f1) {
      return d0 >= f && d0 <= f + 384.0F && d1 >= f1 && d1 <= f1 + 30.0F;
   }

   public void s(Consumer<String> consumer) {
      this.dd = consumer;
   }
}
