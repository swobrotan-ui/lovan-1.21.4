import core.Localization;
import gui.Component;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;

public class gt extends Component {
   private static final float ub = 16.0F;
   private static final float Df = 775.0F;
   private static final float Yb = 385.0F;
   private static final float axL = 30.0F;
   private static final float bu = 7.0F;
   private static final float Pn = 10.0F;
   private static final String aiB = "Z";
   private static final long agQ = 500L;
   private static final float Mb = 8.0F;
   private static final float arx = 10.0F;
   private static final float Ac = 0.3F;
   private static final Map<Character, Character> hJ = Map.<Character, Character>ofEntries(
      Map.<Character, Character>entry('A', 'А'),
      Map.<Character, Character>entry('B', 'Б'),
      Map.<Character, Character>entry('C', 'С'),
      Map.<Character, Character>entry('D', 'Д'),
      Map.<Character, Character>entry('E', 'Е'),
      Map.<Character, Character>entry('F', 'Ф'),
      Map.<Character, Character>entry('G', 'Г'),
      Map.<Character, Character>entry('H', 'Х'),
      Map.<Character, Character>entry('I', 'И'),
      Map.<Character, Character>entry('J', 'Ж'),
      Map.<Character, Character>entry('K', 'К'),
      Map.<Character, Character>entry('L', 'Л'),
      Map.<Character, Character>entry('M', 'М'),
      Map.<Character, Character>entry('N', 'Н'),
      Map.<Character, Character>entry('O', 'О'),
      Map.<Character, Character>entry('P', 'П'),
      Map.<Character, Character>entry('Q', 'К'),
      Map.<Character, Character>entry('R', 'Р'),
      Map.<Character, Character>entry('S', 'З'),
      Map.<Character, Character>entry('T', 'Т'),
      Map.<Character, Character>entry('U', 'У'),
      Map.<Character, Character>entry('V', 'В'),
      Map.<Character, Character>entry('W', 'Ш'),
      Map.<Character, Character>entry('X', '%'),
      Map.<Character, Character>entry('Y', 'Й'),
      Map.<Character, Character>entry('a', 'а'),
      Map.<Character, Character>entry('b', 'б'),
      Map.<Character, Character>entry('c', 'с'),
      Map.<Character, Character>entry('d', 'д'),
      Map.<Character, Character>entry('e', 'е'),
      Map.<Character, Character>entry('f', 'ф'),
      Map.<Character, Character>entry('g', 'г'),
      Map.<Character, Character>entry('h', 'х'),
      Map.<Character, Character>entry('i', 'и'),
      Map.<Character, Character>entry('j', 'ж'),
      Map.<Character, Character>entry('k', 'к'),
      Map.<Character, Character>entry('l', 'л'),
      Map.<Character, Character>entry('m', 'м'),
      Map.<Character, Character>entry('n', 'н'),
      Map.<Character, Character>entry('o', 'о'),
      Map.<Character, Character>entry('p', 'п'),
      Map.<Character, Character>entry('q', 'к'),
      Map.<Character, Character>entry('r', 'р'),
      Map.<Character, Character>entry('s', 'з'),
      Map.<Character, Character>entry('t', 'т'),
      Map.<Character, Character>entry('u', 'у'),
      Map.<Character, Character>entry('v', 'в'),
      Map.<Character, Character>entry('w', 'ш'),
      Map.<Character, Character>entry('x', '$'),
      Map.<Character, Character>entry('y', 'й')
   );
   private String aaa = "";
   private String bV = "";
   private boolean A = false;
   private int eF = 0;
   private int iL = 0;
   private int zX = 0;
   private float fK = 775.0F;
   private float mC = 775.0F;
   private float ayZ = 0.0F;
   private float awP = 0.0F;
   private long acz = System.currentTimeMillis();
   private BiConsumer<String, String> lt;

   public gt(float f, float f1) {
      super(f, f1, 775.0F, 30.0F);
   }

   public void a(boolean flag) {
      this.b(flag, false);
   }

   public void b(boolean flag, boolean flag1) {
      this.mC = flag ? 385.0F : 775.0F;
      if (flag1) {
         this.fK = this.mC;
      }
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f8, float f2) {
      this.c();
      BuiltRectangle builtrectangle = RectangleCache.b(this.fK, 30.0F, 8.0F);
      builtrectangle.a(matrix4f, f, f1, f2);
      if (this.ayZ > 0.01F) {
         RectangleCache.b(this.fK, 30.0F, 8.0F).a(matrix4f, f, f1, this.ayZ);
      }

      float f3 = f + 7.0F;
      float f4 = f1 + 6.5F;
      BuiltText builttext = TextCache.a(this.aiL, "Z", 16.0F, Bz);
      builttext.a(matrix4f, f3, f4, f2);
      float f5 = f1 + (30.0F - this.ayW.e().d() * 16.0F) / 2.0F;
      float f6 = f + 7.0F + 16.0F + 10.0F;
      if (this.aaa.isEmpty() && !this.A) {
         String s = Localization.a().c("Введите ваш поисковой запрос...");
         BuiltText builttext2 = TextCache.a(this.ayW, s, 16.0F, hp);
         builttext2.a(matrix4f, f6, f5, f2);
      } else if (!this.aaa.isEmpty()) {
         if (this.f()) {
            this.d(matrix4f, f6, f1);
         }

         BuiltText builttext1 = TextCache.a(this.ayW, this.aaa, 16.0F, Bz);
         builttext1.a(matrix4f, f6, f5, f2);
      }

      if (this.A && System.currentTimeMillis() / 500L % 2L == 0L) {
         float f7 = f6 + this.e();
         TextCache.a(this.ayW, "|", 16.0F, Bz).a(matrix4f, f7, f5, f2);
      }
   }

   private void c() {
      long i = System.currentTimeMillis();
      float f = (float)(i - this.acz) / 1000.0F;
      this.acz = i;
      float f1 = this.awP - this.ayZ;
      if (Math.abs(f1) > 0.001F) {
         this.ayZ += f1 * 8.0F * f;
         this.ayZ = Math.max(0.0F, Math.min(1.0F, this.ayZ));
      } else {
         this.ayZ = this.awP;
      }

      float f2 = this.mC - this.fK;
      if (Math.abs(f2) > 0.1F) {
         this.fK += f2 * 10.0F * f;
      } else {
         this.fK = this.mC;
      }
   }

   private void d(Matrix4f matrix4f, float f, float f1) {
      int i = Math.min(this.iL, this.zX);
      int j = Math.max(this.iL, this.zX);
      String s = this.aaa.substring(0, i);
      String s1 = this.aaa.substring(i, j);
      float f2 = f + this.ayW.c(s, 16.0F);
      float f3 = this.ayW.c(s1, 16.0F);
      BuiltRectangle builtrectangle = RectangleCache.b(f3, 26.0F, 2.0F);
      builtrectangle.a(matrix4f, f2, f1 + 2.0F, 0.3F);
   }

   private float e() {
      if (!this.aaa.isEmpty() && this.eF > 0) {
         String s = this.aaa.substring(0, Math.min(this.eF, this.aaa.length()));
         return this.ayW.c(s, 16.0F);
      } else {
         return 0.0F;
      }
   }

   private boolean f() {
      return this.iL != this.zX;
   }

   @Override
   public boolean mouseClicked(double d0, double d1, int i) {
      if (this.anm && this.wA && i == 0) {
         float f = this.it + 7.0F;
         if (this.r(d0, d1, f, this.atW, 16.0F)) {
            this.q();
            return true;
         } else if (this.r(d0, d1, this.it, this.atW, this.fK)) {
            if (!this.A) {
               this.A = true;
               this.awP = 1.0F;
               this.eF = this.aaa.length();
            } else {
               float f1 = this.it + 7.0F + 16.0F + 10.0F;
               float f2 = (float)d0 - f1;
               this.eF = this.g(f2);
            }

            this.h();
            return true;
         } else {
            if (this.A) {
               this.A = false;
               this.awP = 0.0F;
               this.h();
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private int g(float f) {
      if (!this.aaa.isEmpty() && !(f <= 0.0F)) {
         float f1 = 0.0F;

         for (int i = 0; i < this.aaa.length(); i++) {
            String s = this.aaa.substring(0, i + 1);
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

         return this.aaa.length();
      } else {
         return 0;
      }
   }

   private void h() {
      this.iL = this.eF;
      this.zX = this.eF;
   }

   @Override
   public boolean charTyped(char c0, int i) {
      if (this.anm && this.wA && this.A) {
         if (this.f()) {
            this.i();
         }

         String s = this.aaa.substring(0, this.eF);
         String s1 = this.aaa.substring(this.eF);
         this.aaa = s + c0 + s1;
         this.eF++;
         this.h();
         this.bV = this.p(this.aaa);
         this.j();
         this.q();
         return true;
      } else {
         return false;
      }
   }

   private void i() {
      int i = Math.min(this.iL, this.zX);
      int j = Math.max(this.iL, this.zX);
      String s = this.aaa.substring(0, i);
      String s1 = this.aaa.substring(j);
      this.aaa = s + s1;
      this.eF = i;
      this.h();
   }

   private void j() {
      this.awP = 1.0F;
   }

   @Override
   public boolean keyPressed(int i, int k, int j) {
      if (this.anm && this.wA && this.A) {
         boolean flag = (j & 2) != 0;
         boolean flag1 = (j & 1) != 0;
         if (flag) {
            return this.k(i, flag1);
         } else {
            switch (i) {
               case 256:
                  this.aaa = "";
                  this.bV = "";
                  this.eF = 0;
                  this.A = false;
                  this.awP = 0.0F;
                  this.h();
                  this.q();
                  return true;
               case 257:
                  this.q();
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
                  if (this.f()) {
                     this.i();
                  } else if (this.eF > 0) {
                     String s2 = this.aaa.substring(0, this.eF - 1);
                     String s3 = this.aaa.substring(this.eF);
                     this.aaa = s2 + s3;
                     this.eF--;
                  }

                  this.bV = this.p(this.aaa);
                  this.j();
                  this.q();
                  return true;
               case 261:
                  if (this.f()) {
                     this.i();
                  } else if (this.eF < this.aaa.length()) {
                     String s = this.aaa.substring(0, this.eF);
                     String s1 = this.aaa.substring(this.eF + 1);
                     this.aaa = s + s1;
                  }

                  this.bV = this.p(this.aaa);
                  this.j();
                  this.q();
                  return true;
               case 262:
                  if (flag1) {
                     if (this.eF < this.aaa.length()) {
                        this.eF++;
                        this.zX = this.eF;
                     }
                  } else if (this.f()) {
                     this.eF = Math.max(this.iL, this.zX);
                     this.h();
                  } else if (this.eF < this.aaa.length()) {
                     this.eF++;
                     this.h();
                  }

                  return true;
               case 263:
                  if (flag1) {
                     if (this.eF > 0) {
                        this.eF--;
                        this.zX = this.eF;
                     }
                  } else if (this.f()) {
                     this.eF = Math.min(this.iL, this.zX);
                     this.h();
                  } else if (this.eF > 0) {
                     this.eF--;
                     this.h();
                  }

                  return true;
               case 268:
                  if (flag1) {
                     this.zX = 0;
                     this.eF = 0;
                  } else {
                     this.eF = 0;
                     this.h();
                  }

                  return true;
               case 269:
                  if (flag1) {
                     this.zX = this.aaa.length();
                     this.eF = this.aaa.length();
                  } else {
                     this.eF = this.aaa.length();
                     this.h();
                  }

                  return true;
            }
         }
      } else {
         return false;
      }
   }

   private boolean k(int i, boolean flag) {
      switch (i) {
         case 65:
            this.iL = 0;
            this.zX = this.aaa.length();
            this.eF = this.aaa.length();
            return true;
         case 67:
            if (this.f()) {
               this.n();
            }

            return true;
         case 86:
            String s2 = this.o();
            if (s2 != null && !s2.isEmpty()) {
               if (this.f()) {
                  this.i();
               }

               String s4 = this.aaa.substring(0, this.eF);
               String s6 = this.aaa.substring(this.eF);
               this.aaa = s4 + s2 + s6;
               this.eF = this.eF + s2.length();
               this.h();
               this.bV = this.p(this.aaa);
               this.j();
               this.q();
            }

            return true;
         case 88:
            if (this.f()) {
               this.n();
               this.i();
               this.bV = this.p(this.aaa);
               this.q();
            }

            return true;
         case 259:
            if (this.f()) {
               this.i();
            } else {
               int i1 = this.l();
               if (i1 < this.eF) {
                  String s3 = this.aaa.substring(0, i1);
                  String s5 = this.aaa.substring(this.eF);
                  this.aaa = s3 + s5;
                  this.eF = i1;
                  this.h();
               }
            }

            this.bV = this.p(this.aaa);
            this.j();
            this.q();
            return true;
         case 261:
            if (this.f()) {
               this.i();
            } else {
               int l = this.m();
               if (l > this.eF) {
                  String s = this.aaa.substring(0, this.eF);
                  String s1 = this.aaa.substring(l);
                  this.aaa = s + s1;
                  this.h();
               }
            }

            this.bV = this.p(this.aaa);
            this.j();
            this.q();
            return true;
         case 262:
            int k = this.m();
            if (flag) {
               this.eF = k;
               this.zX = this.eF;
            } else {
               this.eF = k;
               this.h();
            }

            return true;
         case 263:
            int j = this.l();
            if (flag) {
               this.eF = j;
               this.zX = this.eF;
            } else {
               this.eF = j;
               this.h();
            }

            return true;
         default:
            return false;
      }
   }

   private int l() {
      if (this.eF <= 0) {
         return 0;
      } else {
         int i = this.eF - 1;

         while (i > 0 && Character.isWhitespace(this.aaa.charAt(i))) {
            i--;
         }

         while (i > 0 && !Character.isWhitespace(this.aaa.charAt(i - 1))) {
            i--;
         }

         return i;
      }
   }

   private int m() {
      if (this.eF >= this.aaa.length()) {
         return this.aaa.length();
      } else {
         int i = this.eF;

         while (i < this.aaa.length() && !Character.isWhitespace(this.aaa.charAt(i))) {
            i++;
         }

         while (i < this.aaa.length() && Character.isWhitespace(this.aaa.charAt(i))) {
            i++;
         }

         return i;
      }
   }

   private void n() {
      if (this.f()) {
         int i = Math.min(this.iL, this.zX);
         int j = Math.max(this.iL, this.zX);
         String s = this.aaa.substring(i, j);

         try {
            GLFW.glfwSetClipboardString(MinecraftClient.getInstance().getWindow().getHandle(), s);
         } catch (Exception exception) {
         }
      }
   }

   private String o() {
      try {
         return GLFW.glfwGetClipboardString(MinecraftClient.getInstance().getWindow().getHandle());
      } catch (Exception exception) {
         return null;
      }
   }

   private String p(String s) {
      StringBuilder stringbuilder = new StringBuilder();

      for (char c0 : s.toCharArray()) {
         char c1 = hJ.getOrDefault(c0, c0);
         stringbuilder.append(c1);
      }

      return stringbuilder.toString();
   }

   private void q() {
      if (this.lt != null) {
         String s = this.aaa.toLowerCase().trim();
         String s1 = this.bV.toLowerCase().trim();
         this.lt.accept(s, s1);
      }
   }

   private boolean r(double d0, double d1, float f, float f1, float f2) {
      return d0 >= f && d0 <= f + f2 && d1 >= f1 && d1 <= f1 + 30.0F;
   }

   public void s() {
      this.aaa = "";
      this.bV = "";
      this.eF = 0;
      this.h();
      this.q();
   }

   public float t() {
      return this.fK;
   }

   public void u(BiConsumer<String, String> biconsumer) {
      this.lt = biconsumer;
   }
}
