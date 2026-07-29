import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class dz {
   public static boolean a(World world, PlayerEntity playerentity, double d0) {
      if (world != null && playerentity != null) {
         for (EndCrystalEntity endcrystalentity : world.getEntitiesByClass(
            EndCrystalEntity.class, playerentity.getBoundingBox().expand(d0), endcrystalentity1 -> {
               return true;
            }
         )) {
            if (playerentity.distanceTo(endcrystalentity) <= d0) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean b(World world, PlayerEntity playerentity, double d0, int i) {
      if (world != null && playerentity != null) {
         for (TntEntity tntentity : world.getEntitiesByClass(TntEntity.class, playerentity.getBoundingBox().expand(d0), tntentity1 -> {
            return tntentity1.getFuse() <= i;
         })) {
            if (playerentity.distanceTo(tntentity) <= d0) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
