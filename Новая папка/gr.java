import gui.Component;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import render.BuiltLine2d;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;

public class gr extends Component {
   private static final float Eg = 400.0F;
   private static final float bW = 500.0F;
   private static final float Re = 12.0F;
   private static final float Jc = 40.0F;
   private static final float Ww = 0.01F;
   private static final float awH = 25.0F;
   private static final float aAe = 20.0F;
   private static final float aeY = 30.0F;
   private static final Map<Character, Character> TW = Map.<Character, Character>ofEntries(
      Map.<Character, Character>entry('А', 'A'),
      Map.<Character, Character>entry('Б', 'B'),
      Map.<Character, Character>entry('С', 'C'),
      Map.<Character, Character>entry('Д', 'D'),
      Map.<Character, Character>entry('Е', 'E'),
      Map.<Character, Character>entry('Ф', 'F'),
      Map.<Character, Character>entry('Г', 'G'),
      Map.<Character, Character>entry('Х', 'H'),
      Map.<Character, Character>entry('И', 'I'),
      Map.<Character, Character>entry('Ж', 'J'),
      Map.<Character, Character>entry('К', 'K'),
      Map.<Character, Character>entry('Л', 'L'),
      Map.<Character, Character>entry('М', 'M'),
      Map.<Character, Character>entry('Н', 'N'),
      Map.<Character, Character>entry('О', 'O'),
      Map.<Character, Character>entry('П', 'P'),
      Map.<Character, Character>entry('Р', 'R'),
      Map.<Character, Character>entry('З', 'S'),
      Map.<Character, Character>entry('Т', 'T'),
      Map.<Character, Character>entry('У', 'U'),
      Map.<Character, Character>entry('В', 'V'),
      Map.<Character, Character>entry('Ш', 'W'),
      Map.<Character, Character>entry('Й', 'Y'),
      Map.<Character, Character>entry('а', 'a'),
      Map.<Character, Character>entry('б', 'b'),
      Map.<Character, Character>entry('с', 'c'),
      Map.<Character, Character>entry('д', 'd'),
      Map.<Character, Character>entry('е', 'e'),
      Map.<Character, Character>entry('ф', 'f'),
      Map.<Character, Character>entry('г', 'g'),
      Map.<Character, Character>entry('х', 'h'),
      Map.<Character, Character>entry('и', 'i'),
      Map.<Character, Character>entry('ж', 'j'),
      Map.<Character, Character>entry('к', 'k'),
      Map.<Character, Character>entry('л', 'l'),
      Map.<Character, Character>entry('м', 'm'),
      Map.<Character, Character>entry('н', 'n'),
      Map.<Character, Character>entry('о', 'o'),
      Map.<Character, Character>entry('п', 'p'),
      Map.<Character, Character>entry('р', 'r'),
      Map.<Character, Character>entry('з', 's'),
      Map.<Character, Character>entry('т', 't'),
      Map.<Character, Character>entry('у', 'u'),
      Map.<Character, Character>entry('в', 'v'),
      Map.<Character, Character>entry('ш', 'w'),
      Map.<Character, Character>entry('й', 'y')
   );
   public final vp ae;
   private final Runnable XV;
   private final Consumer<Block> gq;
   private final lh ahg;
   private final BuiltRectangle IG;
   private final BuiltLine2d IZ;
   private final BuiltRectangle AX;
   private final String Nt;
   private boolean Vf = false;
   private final List<Block> amB = new ArrayList<Block>();
   private final List<Block> aqo = new ArrayList<Block>();
   private String dA = "";
   private boolean ayz = false;
   private float adk = 0.0F;
   private float Yv = 0.0F;
   private final Matrix4f azO = new Matrix4f();

   public gr(float f, float f1, String s, Consumer<Block> consumer, Runnable runnable) {
      super(f, f1, 400.0F, 500.0F);
      this.Nt = s;
      this.gq = consumer;
      this.XV = runnable;
      this.ae = new vp();
      this.ae.a();
      this.ae.b();
      this.ahg = new lh(360.0F, 5.0F, this::e);
      this.IG = new br().a(400.0F, 500.0F).b(8.0F).a();
      this.IZ = new otj().a(400.0F).b(1.0F).d().a();
      this.AX = new br().a(380.0F, 30.0F).b(4.0F).a();
      this.a();
      this.b();
   }

   private void a() {
      for (Block block : Registries.BLOCK) {
         if (block != Blocks.AIR && block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR) {
            this.amB.add(block);
         }
      }
   }

   private void b() {
      this.aqo.clear();
      if (this.dA.isEmpty()) {
         this.aqo.addAll(this.amB);
      } else {
         String s = this.c(this.dA).toLowerCase();
         String s1 = this.dA.toLowerCase();

         for (Block block : this.amB) {
            Identifier identifier = Registries.BLOCK.getId(block);
            String s2 = identifier.getPath().replace("_", " ");
            String s3 = s2.toLowerCase();
            if (s3.contains(s1) || s3.contains(s)) {
               this.aqo.add(block);
            }
         }
      }

      this.d();
      this.adk = 0.0F;
   }

   private String c(String s) {
      StringBuilder stringbuilder = new StringBuilder();

      for (char c0 : s.toCharArray()) {
         char c1 = TW.getOrDefault(c0, c0);
         stringbuilder.append(c1);
      }

      return stringbuilder.toString();
   }

   private void d() {
      float f = this.aqo.size() * 25.0F;
      float f1 = 400.0F;
      this.Yv = Math.max(0.0F, f - f1);
   }

   private void e() {
      if (!this.Vf && this.XV != null) {
         this.Vf = true;
         this.XV.run();
      }
   }

   public void f(Runnable runnable) {
      this.ae.c(runnable);
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.ae.f();
      float f4 = f3 * this.ae.k();
      if (!(f4 < 0.01F)) {
         Matrix4f matrix4f1 = this.ae.g() ? this.i(matrix4f, f, f1, this.ae.j(), this.aem) : matrix4f;
         if (this.ae.g()) {
            this.ae.g(matrix4f, f, f1);
         }

         this.IG.a(matrix4f1, f, f1, f4);
         BuiltText builttext = this.k(this.ayW, this.Nt, 15.0F, Bz);
         builttext.a(matrix4f1, f + 10.0F, f1 + 12.0F, f4);
         this.IZ.a(matrix4f1, f, f1 + 40.0F, f4);
         this.g(matrix4f1, f, f1, f4);
         this.h(matrix4f1, f, f1, i, j, f4);
         this.j(this.ahg, f, f1, i, j, f2, f4, matrix4f1);
      }
   }

   private void g(Matrix4f matrix4f, float f, float f1, float f2) {
      float f3 = f + 10.0F;
      float f4 = f1 + 40.0F + 10.0F;
      this.AX.a(matrix4f, f3, f4, f2);
      String s = this.dA.isEmpty() ? "Поиск..." : this.dA;
      Color color = this.dA.isEmpty() ? hp : Bz;
      BuiltText builttext = this.k(this.nO, s, 13.0F, color);
      builttext.a(matrix4f, f3 + 8.0F, f4 + 8.0F, f2);
      if (this.ayz && System.currentTimeMillis() % 1000L < 500L) {
         float f5 = f3 + 8.0F + this.nO.c(this.dA, 13.0F);
         new otj().a(13.0F).b(1.0F).c().a().a(matrix4f, f5, f4 + 7.0F, f2);
      }
   }

   private void h(Matrix4f matrix4f, float f, float f1, int i, int j, float f2) {
      float f3 = f + 10.0F;
      float f4 = f1 + 40.0F + 30.0F + 20.0F;
      float f5 = 380.0F;
      float f6 = 400.0F;

      for (int k = 0; k < this.aqo.size(); k++) {
         Block block = this.aqo.get(k);
         float f7 = f4 + k * 25.0F - this.adk;
         if (!(f7 + 25.0F < f4) && !(f7 > f4 + f6)) {
            boolean flag = i >= f3 && i <= f3 + f5 && j >= f7 && j <= f7 + 25.0F;
            if (flag) {
               RectangleCache.b(f5, 25.0F, 4.0F).a(matrix4f, f3, f7, f2 * 0.3F);
            }

            Identifier identifier = Registries.BLOCK.getId(block);
            String s = identifier.getPath().replace("_", " ");
            BuiltText builttext = this.k(this.nO, s, 13.0F, Bz);
            builttext.a(matrix4f, f3 + 8.0F, f7 + 5.0F, f2);
         }
      }
   }

   private Matrix4f i(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      if (f2 == 1.0F) {
         return matrix4f;
      } else {
         float f4 = f + 200.0F;
         float f5 = f1 + f3 * 0.5F;
         this.azO.set(matrix4f);
         this.azO.translate(f4, f5, 0.0F);
         this.azO.scale(f2, f2, 1.0F);
         this.azO.translate(-f4, -f5, 0.0F);
         return this.azO;
      }
   }

   private void j(lh lh, float f, float f1, int i, int j, float f2, float f3, Matrix4f matrix4f) {
      if (lh != null) {
         float f4 = f + lh.getX();
         float f5 = f1 + lh.getY();
         lh.it = f4;
         lh.atW = f5;
         lh.render(matrix4f, f4, f5, i, j, f2, f3);
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if ((!this.ae.g() || !(this.ae.k() < 0.5F)) && !this.Vf) {
         if (this.k(d0, d1, i)) {
            return true;
         } else {
            float f = this.it + 10.0F;
            float f1 = this.atW + 40.0F + 10.0F;
            if (d0 >= f && d0 <= f + 400.0F - 20.0F && d1 >= f1 && d1 <= f1 + 30.0F) {
               this.ayz = true;
               return true;
            } else {
               this.ayz = false;
               float f2 = this.it + 10.0F;
               float f3 = this.atW + 40.0F + 30.0F + 20.0F;
               float f4 = 380.0F;
               float f5 = 400.0F;
               if (d0 >= f2 && d0 <= f2 + f4 && d1 >= f3 && d1 <= f3 + f5) {
                  int j = (int)((d1 - f3 + this.adk) / 25.0);
                  if (j >= 0 && j < this.aqo.size()) {
                     Block block = this.aqo.get(j);
                     if (this.gq != null) {
                        this.gq.accept(block);
                     }

                     return true;
                  }
               }

               return false;
            }
         }
      } else {
         return false;
      }
   }

   private boolean k(double d0, double d1, int i) {
      float f = this.it + this.ahg.getX();
      float f1 = this.atW + this.ahg.getY();
      this.ahg.it = f;
      this.ahg.atW = f1;
      return d0 >= f && d0 <= f + this.ahg.getWidth() && d1 >= f1 && d1 <= f1 + this.ahg.getHeight() && this.ahg.mouseClicked(d0, d1, i);
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0 && this.ahg != null) {
         this.ahg.mouseReleased(d0, d1, i);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean e(double d0, double d1, double d3, double d2) {
      float f = this.it + 10.0F;
      float f1 = this.atW + 40.0F + 30.0F + 20.0F;
      float f2 = 380.0F;
      float f3 = 400.0F;
      if (d0 >= f && d0 <= f + f2 && d1 >= f1 && d1 <= f1 + f3) {
         this.adk = Math.max(0.0F, Math.min(this.Yv, (float)(this.adk - d2 * 20.0)));
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean f(int i, int j, int k) {
      if (!this.ayz) {
         return false;
      } else if (i == 259) {
         if (!this.dA.isEmpty()) {
            this.dA = this.dA.substring(0, this.dA.length() - 1);
            this.b();
         }

         return true;
      } else if (i != 257 && i != 335) {
         return false;
      } else {
         this.ayz = false;
         return true;
      }
   }

   @Override
   protected boolean h(char c0, int i) {
      if (!this.ayz) {
         return false;
      } else if (!Character.isLetterOrDigit(c0) && c0 != ' ' && c0 != '_') {
         return false;
      } else {
         String s = this.dA;
         this.dA = s + c0;
         this.b();
         return true;
      }
   }
}
