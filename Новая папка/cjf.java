import hud.HudComponent;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import render.BuiltRectangle;

public class cjf {
   private static final float ayQ = 1.0F;
   private static final float Cf = 8.0F;
   private static final float ZC = 5.0F;
   private static final float Xu = 10.0F;
   private static final float jO = 8.0F;
   private float atP = 0.0F;
   private float uF = 0.0F;
   private long nF = System.nanoTime();
   private boolean BR = true;
   private BuiltRectangle ase;
   private BuiltRectangle aqb;
   private final float qI = 1920.0F;
   private float hB = 1080.0F;
   private final List<oi> xn = new ArrayList<oi>();

   public cjf() {
      this.b();
   }

   public void a(float f) {
      float f1 = Math.max(100.0F, f);
      if (Math.abs(this.hB - f1) > 0.5F) {
         this.hB = f1;
         this.aqb = new br().a(1.0F, this.hB).a();
      }
   }

   private void b() {
      this.ase = new br().a(1920.0F, 1.0F).a();
      this.aqb = new br().a(1.0F, this.hB).a();
   }

   public void c() {
      this.uF = 1.0F;
   }

   public void d() {
      this.uF = 0.0F;
      this.xn.clear();
   }

   public void e() {
      long i = System.nanoTime();
      float f = (float)(i - this.nF) / 1.0E9F;
      this.nF = i;
      if (this.atP < this.uF) {
         this.atP += f * 10.0F;
         if (this.atP > this.uF) {
            this.atP = this.uF;
            return;
         }
      } else if (this.atP > this.uF) {
         this.atP -= f * 8.0F;
         if (this.atP < this.uF) {
            this.atP = this.uF;
         }
      }
   }

   public void f(Matrix4f matrix4f) {
      if (!(this.atP <= 0.001F)) {
         for (oi oi : this.xn) {
            if (oi.NX) {
               this.ase.a(matrix4f, 0.0F, oi.TP - 0.5F, this.atP * 0.7F);
            } else {
               this.aqb.a(matrix4f, oi.TP - 0.5F, 0.0F, this.atP * 0.7F);
            }
         }
      }
   }

   public dm g(float f, float f1, float f2, float f3, List<HudComponent> list, HudComponent hudcomponent) {
      this.xn.clear();
      if (!this.BR) {
         float f29 = Math.max(0.0F, Math.min(f, 1920.0F - f2));
         float f30 = Math.max(0.0F, Math.min(f1, this.hB - f3));
         return new dm(f29, f30, false, false);
      } else {
         float f4 = f;
         float f5 = f1;
         boolean flag = false;
         boolean flag1 = false;
         float f6 = f;
         float f7 = f + f2;
         float f8 = f + f2 / 2.0F;
         float f9 = f1;
         float f10 = f1 + f3;
         float f11 = f1 + f3 / 2.0F;
         float f12 = 960.0F;
         float f13 = this.hB / 2.0F;
         beb beb = null;
         beb bebx = null;
         float f14 = Math.abs(f8 - f12);
         if (f14 < 8.0F) {
            float f15 = f12 - f2 / 2.0F;
            beb = new beb(f15, f14, f12, false);
         }

         float f31 = Math.abs(f11 - f13);
         if (f31 < 8.0F) {
            float f16 = f13 - f3 / 2.0F;
            bebx = new beb(f16, f31, f13, true);
         }

         float f32 = Math.abs(f - 5.0F);
         if (f32 < 8.0F && (beb == null || f32 < beb.ca)) {
            beb = new beb(5.0F, f32, 5.0F, false);
         }

         float f17 = Math.abs(f7 - 1915.0F);
         if (f17 < 8.0F && (beb == null || f17 < beb.ca)) {
            beb = new beb(1915.0F - f2, f17, 1915.0F, false);
         }

         float f18 = Math.abs(f1 - 5.0F);
         if (f18 < 8.0F && (bebx == null || f18 < bebx.ca)) {
            bebx = new beb(5.0F, f18, 5.0F, true);
         }

         float f19 = Math.abs(f10 - (this.hB - 5.0F));
         if (f19 < 8.0F && (bebx == null || f19 < bebx.ca)) {
            bebx = new beb(this.hB - 5.0F - f3, f19, this.hB - 5.0F, true);
         }

         for (HudComponent hudcomponent1 : list) {
            if (hudcomponent1 != hudcomponent) {
               float f20 = hudcomponent1.getX();
               float f21 = hudcomponent1.getX() + hudcomponent1.getWidth();
               float f22 = hudcomponent1.getX() + hudcomponent1.getWidth() / 2.0F;
               float f23 = hudcomponent1.getY();
               float f24 = hudcomponent1.getY() + hudcomponent1.getTotalHeight();
               float f25 = hudcomponent1.getY() + hudcomponent1.getTotalHeight() / 2.0F;
               float[][] afloat = new float[][]{{f6, f20, f20}, {f6, f21, f21}, {f7, f20, f20}, {f7, f21, f21}, {f8, f22, f22}};

               for (float[] afloat1 : afloat) {
                  float f26 = Math.abs(afloat1[0] - afloat1[1]);
                  if (f26 < 8.0F && (beb == null || f26 < beb.ca)) {
                     float f27;
                     if (afloat1[0] == f6) {
                        f27 = afloat1[1];
                     } else if (afloat1[0] == f7) {
                        f27 = afloat1[1] - f2;
                     } else {
                        f27 = afloat1[1] - f2 / 2.0F;
                     }

                     beb = new beb(f27, f26, afloat1[2], false);
                  }
               }

               float[][] afloat2 = new float[][]{{f9, f23, f23}, {f9, f24, f24}, {f10, f23, f23}, {f10, f24, f24}, {f11, f25, f25}};

               for (float[] afloat3 : afloat2) {
                  float f33 = Math.abs(afloat3[0] - afloat3[1]);
                  if (f33 < 8.0F && (bebx == null || f33 < bebx.ca)) {
                     float f28;
                     if (afloat3[0] == f9) {
                        f28 = afloat3[1];
                     } else if (afloat3[0] == f10) {
                        f28 = afloat3[1] - f3;
                     } else {
                        f28 = afloat3[1] - f3 / 2.0F;
                     }

                     bebx = new beb(f28, f33, afloat3[2], true);
                  }
               }
            }
         }

         if (beb != null) {
            f4 = beb.gv;
            flag = true;
            this.xn.add(new oi(beb.Td, false));
         }

         if (bebx != null) {
            f5 = bebx.gv;
            flag1 = true;
            this.xn.add(new oi(bebx.Td, true));
         }

         f4 = Math.max(0.0F, Math.min(f4, 1920.0F - f2));
         f5 = Math.max(0.0F, Math.min(f5, this.hB - f3));
         return new dm(f4, f5, flag, flag1);
      }
   }

   public float h() {
      return this.atP;
   }

   public boolean i() {
      return this.BR;
   }

   public void j(boolean flag) {
      this.BR = flag;
   }
}
