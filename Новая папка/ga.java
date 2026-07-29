import module.ModuleBase;

class ga {
   float art = 0.0F;
   long QK = System.nanoTime();

   private ga() {
   }

   float a() {
      return (float)(System.nanoTime() - this.QK) / (float)ModuleBase.NANOS_PER_SECOND;
   }
}
