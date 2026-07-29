package data;

import a.Loader;

public class Position {
   private float x;
   private float y;

   public Position(float f, float f1) {
      this.x = f;
      this.y = f1;
   }

   public native float getX();

   public native float getY();

   public native void setX(float f);

   public native void setY(float f);

   static {
      Loader.init(Position.class);
   }

   public static native void guard();
}
