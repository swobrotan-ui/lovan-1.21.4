import net.minecraft.util.math.Direction;

// $VF: synthetic class
class qn {
   // $VF: synthetic field
   static final int[] To = new int[Direction.values().length];

   static {
      try {
         To[Direction.NORTH.ordinal()] = 1;
      } catch (NoSuchFieldError nosuchfielderror3) {
      }

      try {
         To[Direction.SOUTH.ordinal()] = 2;
      } catch (NoSuchFieldError nosuchfielderror2) {
      }

      try {
         To[Direction.WEST.ordinal()] = 3;
      } catch (NoSuchFieldError nosuchfielderror1) {
      }

      try {
         To[Direction.EAST.ordinal()] = 4;
      } catch (NoSuchFieldError nosuchfielderror) {
      }
   }
}
