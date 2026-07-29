import core.ClientMain;
import core.ConfigManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import render.BuiltLine2d;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;

public class sdc extends ct {
   private static final Map<String, List<String>> awc = new ub(64, 0.75F, true);
   private static final List<String> ahM = new ArrayList<String>(4);
   private static final ExecutorService ur = Executors.newSingleThreadExecutor(runnable -> {
      Thread thread = new Thread(runnable, "Config-IO");
      thread.setDaemon(true);
      return thread;
   });
   private static final float aih = 286.0F;
   private static final float jf = 130.0F;
   private static final float oB = 16.0F;
   private static final float fn = 13.0F;
   private static final float ul = 10.0F;
   private static final float YU = 10.0F;
   private static final float Jr = 13.0F;
   private static final float ag = 10.0F;
   private static final float acw = 24.0F;
   private static final float aab = 10.0F;
   private static final float Gv = 5.0F;
   private static final float alP = 5.0F;
   private static final float rx = 10.0F;
   private static final float Jf = 30.0F;
   private static final float ask = 18.0F;
   private static final float hV = 0.6F;
   private static final float akt = 1.0F;
   private static final float aeE = 10.0F;
   private static final long WN = 1000000000L;
   private static final int EZ = 48;
   private static final long aaF = 500L;
   private static final long rL = 2000L;
   private static final float aen = 8.0F;
   private String RX;
   private String agW;
   private final so LE;
   private final ConfigManager atX;
   private Runnable axz;
   private float amw = 1.0F;
   private final BuiltRectangle amr;
   private final BuiltLine2d api;
   private final wy aB;
   private final wy aev;
   private final wy ws;
   private final wy Zl;
   private final va zM;
   private boolean afV = false;
   private boolean anP = false;
   private boolean zN = false;
   private boolean Fn = false;
   private boolean zm = false;
   private String UW = "";
   private String Nj = null;
   private float auG = 0.0F;
   private float Ys = 1.0F;
   private long abF = 0L;
   private long v = System.currentTimeMillis();
   private long acA = System.currentTimeMillis();
   private int Px = 0;
   private int amP = 0;
   private static final float bS = 200.0F;
   private final mm aac;

   public sdc(float f, float f1, so so, boolean flag) {
      super(f, f1, flag);
      this.LE = so;
      this.RX = so.a();
      this.agW = so.b() != null ? so.b() : "Конфигурация";
      this.atX = ClientMain.getInstance().getConfigManager();
      this.qd = 286.0F;
      this.aem = 130.0F;
      this.amr = RectangleCache.b(286.0F, 130.0F, 8.0F);
      this.aac = new mm();
      this.aac.a(this.a());
      this.api = new otj().a(266.0F).b(1.0F).d().a();
      float f2 = 246.0F;
      float f3 = 5.0F;
      this.aB = new wy(f2, f3 - 1.0F, "E", this::p);
      float f4 = 95.0F;
      float f5 = 246.0F;
      this.aev = new wy(f5, f4, "b", this::r);
      this.aev.d(true);
      this.aev.e(false);
      f5 -= 28.0F;
      this.ws = new wy(f5, f4, "h", this::s);
      this.ws.e(false);
      f5 -= 28.0F;
      this.Zl = new wy(f5, f4, "a", this::t);
      this.Zl.e(false);
      this.zM = new va(246.0F, 5.0F, this::o);
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.azS.f();
      this.aac.b(this.a());
      this.aac.f();
      this.i();
      float f4 = this.azS.j();
      float f5 = f3 * this.azS.k() * this.aac.c() * this.amw;
      Matrix4f matrix4f1 = upq.b(matrix4f, f, f1, f4);
      if (this.azS.g()) {
         this.azS.g(matrix4f, f, f1);
      }

      this.amr.a(matrix4f1, f, f1, f5);
      this.b(matrix4f1, f, f1, i, j, f2, f5);
      this.c(matrix4f1, f, f1, i, j, f2, f5);
   }

   @Override
   protected void a(float f) {
      this.anC = 1.0F;
      this.ku = 1.0F;
   }

   private boolean a() {
      String s = this.atX.z();
      return s != null ? s.equals(this.RX) : this.LE != null && this.LE.d();
   }

   public boolean b() {
      return this.zN && this.amw <= 0.01F;
   }

   @Override
   protected void b(Matrix4f matrix4f, float f, float f1, int i, int j, float f7, float f2) {
      this.h();
      float f3 = f + 10.0F;
      String s = this.afV ? this.g() : this.RX;
      BuiltText builttext = TextCache.a(this.ayW, s, 16.0F, Bz);
      builttext.a(matrix4f, f3, f1 + 10.0F, f2);
      float f4 = f1 + 10.0F + 16.0F + 13.0F;
      this.api.a(matrix4f, f + 10.0F, f4, f2 * 0.2F);
      float f5 = f4 + 10.0F;
      String s1 = this.anP ? this.g() : this.agW;
      this.d(matrix4f, f + 10.0F, f5, s1, f2 * this.Ys);
      if (this.Nj != null && this.auG > 0.01F) {
         float f6 = f4 + 24.0F;
         this.e(matrix4f, f, f6, f2);
      }
   }

   @Override
   protected void c(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.Px = i;
      this.amP = j;
      if (!this.afV && !this.anP) {
         this.c(this.aB, matrix4f, f, f1, i, j, f2, f3);
         this.c(this.aev, matrix4f, f, f1, i, j, f2, f3);
         this.c(this.ws, matrix4f, f, f1, i, j, f2, f3);
         this.c(this.Zl, matrix4f, f, f1, i, j, f2, f3);
      } else {
         this.zM.it = f + this.zM.getX();
         this.zM.atW = f1 + this.zM.getY();
         this.zM.render(matrix4f, f + this.zM.getX(), f1 + this.zM.getY(), i, j, f2, f3);
      }
   }

   private void c(wy wy, Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      wy.it = f + wy.getX();
      wy.atW = f1 + wy.getY();
      wy.render(matrix4f, f + wy.getX(), f1 + wy.getY(), i, j, f2, f3);
   }

   private void d(Matrix4f matrix4f, float f, float f1, String s, float f2) {
      List list = this.f(s);
      float f3 = f1;

      for (String s1 : list) {
         BuiltText builttext = TextCache.a(this.nO, s1, 13.0F, hp);
         builttext.a(matrix4f, f, f3, f2);
         f3 += 15.0F;
      }
   }

   private void e(Matrix4f matrix4f, float f, float f1, float f2) {
      String[] astring = this.Nj.split("\n");
      float f3 = astring.length * 15.0F;
      float f4 = f1 + (81.0F - f3) / 2.0F;

      for (String s : astring) {
         float f5 = this.nO.c(s, 13.0F);
         float f6 = f + (286.0F - f5) / 2.0F;
         BuiltText builttext = TextCache.a(this.nO, s, 13.0F, Bz);
         builttext.a(matrix4f, f6, f4 - 27.0F, f2 * this.auG);
         f4 += 15.0F;
      }
   }

   private List<String> f(String s) {
      if (s != null && !s.isEmpty()) {
         List list = awc.get(s);
         if (list != null) {
            return list;
         } else {
            ArrayList arraylist = new ArrayList(4);
            String[] astring = s.split(" ");
            StringBuilder stringbuilder = new StringBuilder();

            for (String s1 : astring) {
               if (stringbuilder.length() + s1.length() + 1 > 48) {
                  if (!stringbuilder.isEmpty()) {
                     arraylist.add(stringbuilder.toString());
                     stringbuilder.setLength(0);
                     stringbuilder.append(s1);
                  } else {
                     arraylist.add(s1);
                  }
               } else {
                  if (!stringbuilder.isEmpty()) {
                     stringbuilder.append(" ");
                  }

                  stringbuilder.append(s1);
               }
            }

            if (!stringbuilder.isEmpty()) {
               arraylist.add(stringbuilder.toString());
            }

            awc.put(s, arraylist);
            return arraylist;
         }
      } else {
         ahM.clear();
         ahM.add("");
         return ahM;
      }
   }

   private String g() {
      String s = this.UW.isEmpty() ? " " : this.UW;
      long i = System.currentTimeMillis();
      boolean flag = i / 500L % 2L == 0L;
      return flag ? s + "|" : s;
   }

   private void h() {
      long i = System.currentTimeMillis();
      float f = Math.min((float)(i - this.v) / 1000.0F, 0.1F);
      this.v = i;
      if (this.Nj != null) {
         long j = i - this.abF;
         float f1 = j > 2000L ? 0.0F : 1.0F;
         float f2 = j > 2000L ? 1.0F : 0.0F;
         this.auG = this.auG + (f1 - this.auG) * f * 8.0F;
         this.Ys = this.Ys + (f2 - this.Ys) * f * 8.0F;
         if (this.auG < 0.01F && j > 2000L) {
            this.Nj = null;
            this.auG = 0.0F;
         }
      } else {
         this.Ys = this.Ys + (1.0F - this.Ys) * f * 8.0F;
      }
   }

   private void i() {
      if (this.zN) {
         long i = System.currentTimeMillis();
         float f = (float)(i - this.acA);
         float f1 = Math.min(f / 200.0F, 1.0F);
         float f2 = 1.0F - (1.0F - f1) * (1.0F - f1) * (1.0F - f1);
         this.amw = 1.0F - f2;
         if (f1 >= 1.0F && !this.zm) {
            this.zm = true;
            this.amw = 0.0F;
         }
      }
   }

   private void j(String s) {
      this.Nj = s;
      this.auG = 0.0F;
      this.Ys = 1.0F;
      this.abF = System.currentTimeMillis();
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (!this.zN && !this.Fn) {
         if (i == 0 && (this.afV || this.anP) && this.zM.mouseClicked(d0, d1, i)) {
            return true;
         } else if (i != 0
            || this.afV
            || this.anP
            || !this.aB.mouseClicked(d0, d1, i) && !this.aev.mouseClicked(d0, d1, i) && !this.ws.mouseClicked(d0, d1, i) && !this.Zl.mouseClicked(d0, d1, i)) {
            if (i == 0) {
               return this.k(d0, d1);
            } else {
               return i == 1 ? this.l(d0, d1) : false;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      this.aB.mouseReleased(d0, d1, i);
      this.aev.mouseReleased(d0, d1, i);
      this.ws.mouseReleased(d0, d1, i);
      this.Zl.mouseReleased(d0, d1, i);
      this.zM.mouseReleased(d0, d1, i);
      return false;
   }

   private boolean k(double d0, double d1) {
      if (d0 >= this.it && d0 <= this.it + this.qd && d1 >= this.atW && d1 <= this.atW + this.aem) {
         this.m();
         return true;
      } else {
         return false;
      }
   }

   private boolean l(double d0, double d1) {
      if (d0 >= this.it && d0 <= this.it + this.qd && d1 >= this.atW && d1 <= this.atW + this.aem) {
         this.q(d1);
         return true;
      } else {
         return false;
      }
   }

   private void m() {
      ur.submit(() -> {
         try {
            this.atX.j(this.RX);
            this.atX.m(this.RX);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      });
   }

   @Override
   public boolean f(int i, int j, int k) {
      if (!this.afV && !this.anP) {
         return false;
      } else if (i == 257) {
         this.o();
         return true;
      } else if (i == 256) {
         this.afV = false;
         this.anP = false;
         this.UW = "";
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean h(char c0, int i) {
      if (!this.afV && !this.anP) {
         return false;
      } else if (c0 >= ' ' && c0 != 127) {
         String s = this.UW;
         this.UW = s + c0;
         return true;
      } else {
         return false;
      }
   }

   public boolean n() {
      if (!this.afV && !this.anP) {
         return false;
      } else {
         if (!this.UW.isEmpty()) {
            this.UW = this.UW.substring(0, this.UW.length() - 1);
         }

         return true;
      }
   }

   private void o() {
      String s = this.UW.trim();
      if (s.isEmpty()) {
         this.afV = false;
         this.anP = false;
      } else if (this.afV && !s.equals(this.RX)) {
         String s1 = this.RX;
         boolean flag = this.a();
         this.RX = s;
         this.LE.g(this.RX);
         this.afV = false;
         this.anP = false;
         ur.submit(() -> {
            try {
               this.atX.i(this.RX, this.agW);
               if (flag) {
                  this.atX.m(this.RX);
               }

               this.atX.k(s1);
               MinecraftClient.getInstance().execute(() -> {
                  if (this.axz != null) {
                     this.axz.run();
                  }
               });
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         });
      } else if (this.anP && !s.equals(this.agW)) {
         this.agW = s;
         this.LE.h(this.agW);
         this.afV = false;
         this.anP = false;
         ur.submit(() -> {
            try {
               this.atX.i(this.RX, this.agW);
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         });
      } else {
         this.afV = false;
         this.anP = false;
      }
   }

   private void p() {
      if (!this.afV && !this.anP) {
         this.q(this.amP);
      }
   }

   private void q(double d0) {
      float f = this.atW + 10.0F + 16.0F;
      if (d0 <= f) {
         this.afV = true;
         this.anP = false;
         this.UW = this.RX;
      } else {
         this.anP = true;
         this.afV = false;
         this.UW = this.agW;
      }
   }

   private void r() {
      this.zN = true;
      this.acA = System.currentTimeMillis();
      boolean flag = this.a();
      if (this.axz != null) {
         this.axz.run();
      }

      ur.submit(() -> {
         try {
            this.atX.k(this.RX);
            if (flag) {
               MinecraftClient.getInstance().execute(() -> {
                  this.atX.t();
                  this.atX.A(null);
               });
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      });
   }

   private void s() {
      ur.submit(() -> {
         try {
            String s = this.atX.n(this.RX);
            MinecraftClient.getInstance().execute(() -> {
               if (s != null) {
                  try {
                     GLFW.glfwSetClipboardString(MinecraftClient.getInstance().getWindow().getHandle(), s);
                     this.j(s + "\nСкопирован!");
                  } catch (Exception exception1) {
                     this.j("Ошибка при экспорте");
                  }
               } else {
                  this.j("Ошибка при экспорте");
               }
            });
         } catch (Exception exception) {
            MinecraftClient.getInstance().execute(() -> {
               this.j("Ошибка при экспорте");
            });
         }
      });
   }

   private void t() {
      ur.submit(() -> {
         try {
            boolean flag = this.atX.i(this.RX, this.agW);
            MinecraftClient.getInstance().execute(() -> {
               if (flag) {
                  this.j("Сохранено");
               } else {
                  this.j("Ошибка при сохранении");
               }
            });
         } catch (Exception exception) {
            MinecraftClient.getInstance().execute(() -> {
               this.j("Ошибка при сохранении");
            });
         }
      });
   }

   public String u() {
      return this.RX;
   }

   public String v() {
      return this.agW;
   }

   public void w(Runnable runnable) {
      this.axz = runnable;
   }

   public float x() {
      return this.amw;
   }

   public boolean y() {
      return this.afV;
   }

   public boolean z() {
      return this.anP;
   }

   public boolean A() {
      return this.zN;
   }

   public boolean B() {
      return this.Fn;
   }

   public void C(boolean flag) {
      this.Fn = flag;
   }
}
