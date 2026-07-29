package gui;

import com.google.common.base.Suppliers;
import font.MSDFFont;
import java.awt.Color;
import java.util.function.Supplier;
import org.joml.Matrix4f;
import render.BuiltText;
import render.TextCache;

public abstract class Component implements InteractiveComponent, GuiConstants {
   private static final Supplier<MSDFFont> amO = Suppliers.memoize(() -> {
      return MSDFFont.g().b("b").c("b").e();
   });
   private static final Supplier<MSDFFont> pR = Suppliers.memoize(() -> {
      return MSDFFont.g().b("bb").c("bb").e();
   });
   private static final Supplier<MSDFFont> DB = Suppliers.memoize(() -> {
      return MSDFFont.g().b("a").c("a").e();
   });
   private static final Supplier<MSDFFont> lo = Suppliers.memoize(() -> {
      return MSDFFont.g().b("c").c("c").e();
   });
   protected final MSDFFont aeN = amO.get();
   protected final MSDFFont ayW = pR.get();
   protected final MSDFFont nO = DB.get();
   protected final MSDFFont aiL = lo.get();
   public float aP;
   public float hn;
   protected float qd;
   protected float aem;
   protected boolean anm = true;
   protected boolean wA = true;
   public boolean cH = false;
   protected float Ji = 1.0F;
   protected float bP = 1.0F;
   protected float axg = 5.0F;
   public float it;
   public float atW;

   public Component(float f, float f1, float f2, float f3) {
      this.aP = f;
      this.hn = f1;
      this.qd = f2;
      this.aem = f3;
   }

   @Override
   public void render(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      if (this.anm) {
         this.it = f;
         this.atW = f1;
         this.cH = this.isHovered(i, j);
         this.Ji = this.j(this.Ji, this.bP * f3, f2 * this.axg);
         this.a(matrix4f, f, f1, i, j, f2, this.Ji);
      }
   }

   protected abstract void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3);

   @Override
   public boolean mouseClicked(double d0, double d1, int i) {
      return this.anm && this.wA && this.isHovered(d0, d1) && this.b(d0, d1, i);
   }

   @Override
   public boolean mouseReleased(double d0, double d1, int i) {
      return this.anm && this.wA && this.c(d0, d1, i);
   }

   @Override
   public boolean mouseDragged(double d0, double d1, int i, double d2, double d3) {
      return this.anm && this.wA && this.d(d0, d1, i, d2, d3);
   }

   @Override
   public boolean mouseScrolled(double d0, double d1, double d2, double d3) {
      return this.anm && this.wA && this.isHovered(d0, d1) && this.e(d0, d1, d2, d3);
   }

   @Override
   public boolean keyPressed(int i, int j, int k) {
      return this.anm && this.wA && this.f(i, j, k);
   }

   @Override
   public boolean keyReleased(int i, int j, int k) {
      return this.anm && this.wA && this.g(i, j, k);
   }

   @Override
   public boolean charTyped(char c0, int i) {
      return this.anm && this.wA && this.h(c0, i);
   }

   @Override
   public void tick(float f) {
      if (this.anm) {
         this.i(f);
      }
   }

   protected boolean b(double d0, double d1, int i) {
      return false;
   }

   protected boolean c(double d0, double d1, int i) {
      return false;
   }

   protected boolean d(double d0, double d1, int i, double d2, double d3) {
      return false;
   }

   protected boolean e(double d0, double d1, double d2, double d3) {
      return false;
   }

   protected boolean f(int i, int j, int k) {
      return false;
   }

   protected boolean g(int i, int j, int k) {
      return false;
   }

   protected boolean h(char c0, int i) {
      return false;
   }

   protected void i(float f) {
   }

   @Override
   public float getWidth() {
      return this.qd;
   }

   @Override
   public float getHeight() {
      return this.aem;
   }

   @Override
   public float getX() {
      return this.aP;
   }

   @Override
   public float getY() {
      return this.hn;
   }

   @Override
   public void setPosition(float f, float f1) {
      this.aP = f;
      this.hn = f1;
   }

   @Override
   public boolean isHovered(double d0, double d1) {
      return d0 >= this.it && d0 <= this.it + this.qd && d1 >= this.atW && d1 <= this.atW + this.aem;
   }

   @Override
   public boolean isVisible() {
      return this.anm;
   }

   @Override
   public void setVisible(boolean flag) {
      this.anm = flag;
   }

   @Override
   public boolean isEnabled() {
      return this.wA;
   }

   @Override
   public void setEnabled(boolean flag) {
      this.wA = flag;
   }

   protected float j(float f, float f1, float f2) {
      return f + (f1 - f) * Math.min(f2, 1.0F);
   }

   protected BuiltText k(MSDFFont msdffont, String s, float f, Color color) {
      return TextCache.a(msdffont, s, f, color);
   }
}
