enum uvf {
   @Override
   public float b(float f, float f1, float f2, float f3) {
      f /= f3 / 2.0F;
      if (f < 1.0F) {
         return f2 / 2.0F * f * f * f + f1;
      } else {
         f -= 2.0F;
         return f2 / 2.0F * (f * f * f + 2.0F) + f1;
      }
   }
}
