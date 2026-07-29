import gui.Component;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;

public abstract class jb<T extends Component> extends Component {
   protected final List<T> zy = new ArrayList<T>();
   protected final cd uR;
   protected float UA = 0.0F;
   protected float BB = 0.0F;
   protected nku Gq = nku.sZ;
   protected float[] T = new float[0];
   protected yi<T> fk;
   protected ai NQ;
   protected ai rZ;
   protected float KL = 1.0F;
   protected float aiE = 0.0F;
   protected float RT = 0.0F;
   protected float pZ = 286.0F;
   protected float Ip = 40.0F;
   protected int fh = 3;
   protected float apX = 11.0F;
   protected float HC = 11.0F;
   protected float LX = 20.0F;

   public jb(float f, float f1, float f2, float f3) {
      super(f, f1, f2, f3);
      this.uR = new cd();
      this.uR.f(0.0F);
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.uR.c();
      this.UA = this.uR.d();
      this.b(matrix4f, f, f1, i, j, f2, f3);
   }

   public void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3, it it) {
      this.uR.c();
      this.UA = this.uR.d();
      float f4 = it.m();
      float f5 = it.n();
      if (f4 == 1.0F && f5 == 1.0F) {
         this.render(matrix4f, f, f1, i, j, f2, f3);
      } else {
         this.d(matrix4f, f, f1, it);
         this.c(matrix4f, f, f1, i, j, f2, f3 * f5, f4);
      }
   }

   protected void b(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      if (this.NQ != null) {
         this.NQ.onAfterCardsRender(matrix4f, f, f1, i, j, f2, f3);
      }

      efj.a(0.0F, 0.0F, this.qd, this.aem, this.KL, this.aiE + f * this.KL, this.RT + f1 * this.KL);

      for (Component component : this.zy) {
         float f4 = f1 + component.getY() - this.UA;
         if (il.c(f4, this.Ip, f1, this.aem)) {
            component.it = f + component.getX();
            component.atW = f4;
            if (this.fk != null) {
               this.fk.render((T)component, matrix4f, f + component.getX(), f4, i, j, f2, f3);
            } else {
               component.render(matrix4f, f + component.getX(), f4, i, j, f2, f3);
            }
         }
      }

      efj.b();
      if (this.rZ != null) {
         this.rZ.onAfterCardsRender(matrix4f, f, f1, i, j, f2, f3);
      }
   }

   protected void c(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3, float f4) {
      efj.a(0.0F, 0.0F, this.qd, this.aem, this.KL, this.aiE + f * this.KL, this.RT + f1 * this.KL);

      for (Component component : this.zy) {
         float f5 = f1 + component.getY() - this.UA;
         if (il.c(f5, this.Ip, f1, this.aem)) {
            Matrix4f matrix4f1 = upq.b(matrix4f, f + component.getX(), f5, f4);
            component.it = f + component.getX();
            component.atW = f5;
            component.render(matrix4f1, f + component.getX(), f5, i, j, f2, f3);
         }
      }

      efj.b();
   }

   protected void d(Matrix4f matrix4f, float f, float f1, it it) {
      efj.a(0.0F, 0.0F, this.qd, this.aem, this.KL, this.aiE + f * this.KL, this.RT + f1 * this.KL);

      for (Component component : this.zy) {
         float f2 = f1 + component.getY() - this.UA;
         if (il.c(f2, this.Ip, f1, this.aem)) {
            it.j(matrix4f, f + component.getX(), f2);
         }
      }

      efj.b();
   }

   protected void g() {
      this.BB = il.b(this.zy.size(), this.fh, this.Ip, this.HC, this.aem);
   }

   protected void h(double d0) {
      float f = this.uR.d() + (float)(-d0 * this.LX);
      f = Math.max(0.0F, Math.min(f, this.BB));
      this.uR.a(f);
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      for (Component component : this.zy) {
         float f = this.atW + component.getY() - this.UA;
         component.it = this.it + component.getX();
         component.atW = f;
         if (component.mouseClicked(d0, d1, i)) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i != 0) {
         for (Component component1 : this.zy) {
            if (component1.mouseReleased(d0, d1, i)) {
               return true;
            }
         }

         return false;
      } else {
         for (Component component : this.zy) {
            component.mouseReleased(d0, d1, i);
         }

         return true;
      }
   }

   @Override
   protected boolean e(double d1, double d2, double d3, double d0) {
      this.h(d0);
      return true;
   }

   @Override
   protected void i(float f) {
      this.uR.c();
      this.UA = this.uR.d();

      for (Component component : this.zy) {
         component.tick(f);
      }
   }

   public void i(float f, float f1, float f2) {
      this.KL = f;
      this.aiE = f1;
      this.RT = f2;
   }

   public void k(float f) {
      float f1 = Math.max(0.0F, Math.min(f, this.BB));
      this.uR.f(f1);
      this.uR.a(f1);
      this.UA = f1;
   }

   protected void l(int i) {
      this.Gq = nku.Ij;
      this.T = new float[i];
   }

   protected void m(T component, float f, float f1) {
      int i = this.n();
      float f2 = i * this.apX;
      float f3 = this.T[i];
      component.aP = f2;
      component.hn = f3;
      this.zy.add((T)component);
      this.T[i] = this.T[i] + (f + f1);
   }

   private int n() {
      int i = 0;
      float f = this.T[0];

      for (int j = 1; j < this.T.length; j++) {
         if (this.T[j] < f) {
            f = this.T[j];
            i = j;
         }
      }

      return i;
   }

   protected void o(float f) {
      if (this.zy.isEmpty()) {
         this.BB = 0.0F;
      } else {
         float f1 = 0.0F;

         for (Component component : this.zy) {
            float f2 = component.getY() + component.getHeight();
            if (f2 > f1) {
               f1 = f2;
            }
         }

         this.BB = Math.max(0.0F, f1 + f - this.aem);
      }
   }

   public List<T> p() {
      return this.zy;
   }

   public cd q() {
      return this.uR;
   }

   public float r() {
      return this.UA;
   }

   public float s() {
      return this.BB;
   }

   public nku t() {
      return this.Gq;
   }

   public float[] u() {
      return this.T;
   }

   public yi<T> v() {
      return this.fk;
   }

   public ai w() {
      return this.NQ;
   }

   public ai x() {
      return this.rZ;
   }

   public float y() {
      return this.KL;
   }

   public float z() {
      return this.aiE;
   }

   public float A() {
      return this.RT;
   }

   public float B() {
      return this.pZ;
   }

   public float C() {
      return this.Ip;
   }

   public int D() {
      return this.fh;
   }

   public float E() {
      return this.apX;
   }

   public float F() {
      return this.HC;
   }

   public float G() {
      return this.LX;
   }

   public void H(float f) {
      this.UA = f;
   }

   public void I(float f) {
      this.BB = f;
   }

   public void J(nku nku) {
      this.Gq = nku;
   }

   public void K(float[] afloat) {
      this.T = afloat;
   }

   public void L(yi<T> yi) {
      this.fk = yi;
   }

   public void M(ai ai) {
      this.NQ = ai;
   }

   public void N(ai ai) {
      this.rZ = ai;
   }

   public void O(float f) {
      this.KL = f;
   }

   public void P(float f) {
      this.aiE = f;
   }

   public void Q(float f) {
      this.RT = f;
   }

   public void R(float f) {
      this.pZ = f;
   }

   public void S(float f) {
      this.Ip = f;
   }

   public void T(int i) {
      this.fh = i;
   }

   public void U(float f) {
      this.apX = f;
   }

   public void V(float f) {
      this.HC = f;
   }

   public void W(float f) {
      this.LX = f;
   }
}
