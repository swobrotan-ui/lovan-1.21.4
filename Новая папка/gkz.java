enum gkz {
   @Override
   public float b(float f, float f1, float f2, float f3) {
      if (f == 0.0F) {
         return f1;
      } else if ((f = f / f3) == 1.0F) {
         return f1 + f2;
      } else {
         float f4 = f3 * 0.3F;
         float f5 = f4 / 4.0F;
         return -(f2 * (float)Math.pow(2.0, 10.0F * --f) * (float)Math.sin((f * f3 - f5) * (Math.PI * 2) / f4)) + f1;
      }
   }
}
