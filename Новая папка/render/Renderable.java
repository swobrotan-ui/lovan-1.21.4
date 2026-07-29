package render;

import org.joml.Matrix4f;

public interface Renderable {
   Matrix4f eb = new Matrix4f();

   default void render(double d0, double d1) {
      this.render((float)d0, (float)d1);
   }

   default void render(float f, float f1) {
      this.render(eb, f, f1);
   }

   default void render(Matrix4f matrix4f, double d0, double d1) {
      this.render(matrix4f, (float)d0, (float)d1);
   }

   default void render(Matrix4f matrix4f, float f, float f1) {
      this.render(matrix4f, f, f1, 0.0F);
   }

   default void render(double d0, double d1, double d2) {
      this.render((float)d0, (float)d1, (float)d2);
   }

   default void render(float f, float f1, float f2) {
      this.render(eb, f, f1, f2);
   }

   default void render(Matrix4f matrix4f, double d0, double d1, double d2) {
      this.render(matrix4f, (float)d0, (float)d1, (float)d2);
   }

   void render(Matrix4f matrix4f, float f, float f1, float f2);
}
