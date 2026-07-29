import core.FriendManager;
import org.joml.Matrix4f;
import render.BuiltText;
import render.TextCache;

public class ps extends ct {
   private static final long Un = 300L;
   private static final long akQ = 500L;
   private static final float axX = 200.0F;
   private static final float ayV = 16.0F;
   private static final float XB = 10.0F;
   private static final float rc = 11.0F;
   private static final float HV = 5.0F;
   private String Su;
   private final rku ze;
   private final va adN;
   private Runnable yw;
   private boolean axh = false;
   private String bp;
   private long jV = 0L;
   private boolean RD = false;
   private float jR = 1.0F;
   private long pp = System.currentTimeMillis();

   public ps(float f, float f1, String s, Runnable runnable, boolean flag) {
      super(f, f1, flag);
      this.Su = s;
      this.bp = s;
      this.yw = runnable;
      float f2 = 251.0F;
      float f3 = 5.0F;
      this.ze = new rku(f2, f3, this::a);
      this.adN = new va(f2, f3, this::g);
   }

   public void a() {
      if (!this.RD) {
         this.RD = true;
         this.pp = System.currentTimeMillis();
         if (this.yw != null) {
            this.yw.run();
         }

         new Thread(() -> {
            FriendManager.getInstance().removeFriendSilent(this.Su);
         }, "Friend-Remove-Thread").start();
      }
   }

   public boolean b() {
      return this.RD && this.jR <= 0.01F;
   }

   private void c() {
      if (this.RD) {
         long i = System.currentTimeMillis();
         float f = (float)(i - this.pp);
         float f1 = Math.min(f / 200.0F, 1.0F);
         float f2 = 1.0F - (1.0F - f1) * (1.0F - f1) * (1.0F - f1);
         this.jR = 1.0F - f2;
         if (f1 >= 1.0F) {
            this.jR = 0.0F;
         }
      }
   }

   @Override
   protected void b(Matrix4f matrix4f, float f, float f1, int i, int j, float f3, float f2) {
      this.c();
      String s = this.axh ? this.d() : this.Su;
      BuiltText builttext = TextCache.a(this.ayW, s, 16.0F, Bz);
      builttext.a(matrix4f, f + 10.0F, f1 + 11.0F, f2);
   }

   @Override
   protected void c(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      if (!this.axh) {
         this.ze.it = f + this.ze.getX();
         this.ze.atW = f1 + this.ze.getY();
         this.ze.render(matrix4f, f + this.ze.getX(), f1 + this.ze.getY(), i, j, f2, f3);
      } else {
         this.adN.it = f + this.adN.getX();
         this.adN.atW = f1 + this.adN.getY();
         this.adN.render(matrix4f, f + this.adN.getX(), f1 + this.adN.getY(), i, j, f2, f3);
      }
   }

   private String d() {
      String s = this.bp.isEmpty() ? " " : this.bp;
      long i = System.currentTimeMillis();
      boolean flag = i / 500L % 2L == 0L;
      return flag ? s + "|" : s;
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i != 0 || this.RD) {
         return false;
      } else if (this.axh && this.adN.mouseClicked(d0, d1, i)) {
         return true;
      } else {
         return !this.axh && this.ze.mouseClicked(d0, d1, i) ? true : this.e(d0, d1);
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      return this.ze.mouseReleased(d0, d1, i);
   }

   private boolean e(double d0, double d1) {
      if (d0 >= this.it && d0 <= this.it + this.qd && d1 >= this.atW && d1 <= this.atW + this.aem) {
         long i = System.currentTimeMillis();
         if (i - this.jV < 300L) {
            this.axh = true;
            this.bp = this.Su;
            return true;
         }

         this.jV = i;
      }

      return false;
   }

   @Override
   public boolean f(int i, int j, int k) {
      if (!this.axh) {
         return false;
      } else if (i == 257) {
         this.g();
         return true;
      } else if (i == 256) {
         this.axh = false;
         this.bp = this.Su;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean h(char c0, int i) {
      if (!this.axh) {
         return false;
      } else if (c0 >= ' ' && c0 != 127) {
         String s = this.bp;
         this.bp = s + c0;
         return true;
      } else {
         return false;
      }
   }

   public boolean f() {
      if (!this.axh) {
         return false;
      } else {
         if (!this.bp.isEmpty()) {
            this.bp = this.bp.substring(0, this.bp.length() - 1);
         }

         return true;
      }
   }

   private void g() {
      if (!this.bp.trim().isEmpty() && !this.bp.trim().equals(this.Su)) {
         FriendManager.getInstance().removeFriend(this.Su);
         FriendManager.getInstance().addFriend(this.bp.trim());
         this.Su = this.bp.trim();
      }

      this.axh = false;
   }

   public String h() {
      return this.Su;
   }

   public void i(Runnable runnable) {
      this.yw = runnable;
   }

   public boolean j() {
      return this.axh;
   }

   public boolean k() {
      return this.RD;
   }

   public float l() {
      return this.jR;
   }
}
