import net.minecraft.util.hit.HitResult.Type;

// $VF: synthetic class
class wl {
   // $VF: synthetic field
   static final int[] KZ = new int[Type.values().length];

   static {
      try {
         KZ[Type.ENTITY.ordinal()] = 1;
      } catch (NoSuchFieldError nosuchfielderror1) {
      }

      try {
         KZ[Type.BLOCK.ordinal()] = 2;
      } catch (NoSuchFieldError nosuchfielderror) {
      }
   }
}
