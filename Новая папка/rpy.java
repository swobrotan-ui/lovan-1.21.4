import java.awt.Color;
import render.BuiltLine3d;

public final class rpy extends se<BuiltLine3d> {
   private float zK = 2.0F;
   private float ZF = 1.0F;
   private float anS = 1.0F;
   private float awE = 1.0F;
   private float hO = 1.0F;
   private float xG = 0.3F;
   private boolean aqt = true;
   private boolean avy = false;
   private float agG = 1.0F;
   private float aoO = 1.0F;
   private float CF = 0.0F;
   private float vT = 0.0F;
   private float rb = 0.0F;
   private float zz = 0.5F;
   private int Xt = 4;
   private boolean iA = false;
   private boolean aef = false;
   private float akZ = 3.0F;
   private float bg = 0.5F;
   private int apg = 4;

   public rpy a(float f) {
      this.zK = f;
      return this;
   }

   public rpy b(float f, float f1, float f2, float f3) {
      this.ZF = f;
      this.anS = f1;
      this.awE = f2;
      this.hO = f3;
      return this;
   }

   public rpy c(float f, float f1, float f2) {
      return this.b(f, f1, f2, 1.0F);
   }

   public rpy d(Color color, float f) {
      return this.b(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, f);
   }

   public rpy e(Color color) {
      return this.b(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
   }

   public rpy f(float f) {
      this.xG = f;
      return this;
   }

   public rpy g(boolean flag) {
      this.aqt = flag;
      return this;
   }

   public rpy h(boolean flag) {
      this.avy = flag;
      return this;
   }

   public rpy i(float f, float f1) {
      this.agG = f;
      this.aoO = f1;
      return this;
   }

   public rpy j(float f, float f1, float f2, float f3) {
      this.CF = f;
      this.vT = f1;
      this.rb = f2;
      this.zz = f3;
      return this;
   }

   public rpy k(Color color, float f) {
      return this.j(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, f);
   }

   public rpy l(int i) {
      this.Xt = Math.max(3, Math.min(8, i));
      return this;
   }

   public rpy m(boolean flag) {
      this.iA = flag;
      return this;
   }

   public rpy n(boolean flag) {
      this.aef = flag;
      return this;
   }

   public rpy o(float f) {
      this.akZ = Math.max(1.0F, f);
      return this;
   }

   public rpy p(float f) {
      this.bg = Math.max(0.0F, Math.min(4.0F, f));
      return this;
   }

   public rpy q(int i) {
      this.apg = Math.max(2, Math.min(8, i));
      return this;
   }

   public rpy r(float f, float f1, float f2, float f3, float f4, int i, boolean flag, boolean flag1) {
      this.zK = f;
      this.ZF = f1;
      this.anS = f2;
      this.awE = f3;
      this.hO = f4;
      this.Xt = Math.max(3, Math.min(8, i));
      this.aqt = flag;
      this.iA = flag1;
      return this;
   }

   public rpy s(float f, Color color, float f1, int i, boolean flag, boolean flag1) {
      return this.r(f, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, f1, i, flag, flag1);
   }

   public rpy t(float f, float f1, float f2, float f3, float f4) {
      return this.r(f, f1, f2, f3, f4, 8, true, false);
   }

   public rpy u(float f, Color color, float f1) {
      return this.s(f, color, f1, 8, true, false);
   }

   public rpy v(boolean flag, float f, float f1, float f2, float f3, float f4, float f5, boolean flag1, float f6, float f7, int i) {
      this.avy = flag;
      this.agG = f;
      this.aoO = f1;
      this.CF = f2;
      this.vT = f3;
      this.rb = f4;
      this.zz = f5;
      this.aef = flag1;
      this.akZ = Math.max(1.0F, f6);
      this.bg = Math.max(0.0F, Math.min(4.0F, f7));
      this.apg = Math.max(2, Math.min(8, i));
      return this;
   }

   protected BuiltLine3d w() {
      return new BuiltLine3d(
         this.zK,
         this.ZF,
         this.anS,
         this.awE,
         this.hO,
         this.xG,
         this.aqt,
         this.avy,
         this.agG,
         this.aoO,
         this.CF,
         this.vT,
         this.rb,
         this.zz,
         this.Xt,
         this.iA,
         this.aef,
         this.akZ,
         this.bg,
         this.apg
      );
   }

   @Override
   protected void b() {
      this.zK = 2.0F;
      this.ZF = 1.0F;
      this.anS = 1.0F;
      this.awE = 1.0F;
      this.hO = 1.0F;
      this.xG = 0.3F;
      this.aqt = true;
      this.avy = false;
      this.agG = 1.0F;
      this.aoO = 1.0F;
      this.CF = 0.0F;
      this.vT = 0.0F;
      this.rb = 0.0F;
      this.zz = 0.5F;
      this.Xt = 4;
      this.iA = false;
      this.aef = false;
      this.akZ = 3.0F;
      this.bg = 0.5F;
      this.apg = 4;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected Object c() {
      return this.w();
   }
}
