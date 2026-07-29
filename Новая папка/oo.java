enum oo {
   @Override
   public float b(float f, float f1, float f2, float f3) {
      if ((f = f / f3) < 0.36363637F) {
         return f2 * (7.5625F * f * f) + f1;
      } else if (f < 0.72727275F) {
         float f6;
         return f2 * (7.5625F * (f6 = f - 0.54545456F) * f6 + 0.75F) + f1;
      } else {
         float f4;
         float f5;
         return f < 0.9090909090909091
            ? f2 * (7.5625F * (f4 = f - 0.8181818F) * f4 + 0.9375F) + f1
            : f2 * (7.5625F * (f5 = f - 0.95454544F) * f5 + 0.984375F) + f1;
      }
   }
}
