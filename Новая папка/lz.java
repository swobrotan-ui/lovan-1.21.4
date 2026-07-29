import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class lz {
   private static final MinecraftClient aiY = MinecraftClient.getInstance();

   public static boolean a(LivingEntity livingentity, Vec3d vec3d, double d0) {
      if (aiY.player == null) {
         return false;
      } else {
         Vec3d vec3d1 = aiY.player.getEyePos();
         Box box = livingentity.getBoundingBox();
         Vec3d vec3d2 = vec3d1.add(vec3d.subtract(vec3d1).normalize().multiply(d0));
         return box.contains(vec3d2) || box.raycast(vec3d1, vec3d2).isPresent();
      }
   }

   public static boolean b(Vec3d vec3d, Vec3d vec3d1) {
      if (aiY.world == null) {
         return false;
      } else {
         RaycastContext raycastcontext = new RaycastContext(vec3d, vec3d1, ShapeType.COLLIDER, FluidHandling.NONE, aiY.player);
         BlockHitResult blockhitresult = aiY.world.raycast(raycastcontext);
         if (blockhitresult.getType() == Type.MISS) {
            return false;
         } else {
            BlockState blockstate = aiY.world.getBlockState(blockhitresult.getBlockPos());
            return !c(blockstate);
         }
      }
   }

   public static boolean c(BlockState blockstate) {
      Block block = blockstate.getBlock();
      return block instanceof DoorBlock
         || block instanceof TrapdoorBlock
         || block instanceof SlabBlock
         || block instanceof StairsBlock
         || block instanceof FenceBlock
         || block instanceof FenceGateBlock
         || block instanceof WallBlock
         || block instanceof PaneBlock
         || block instanceof SkullBlock
         || block instanceof BannerBlock
         || block instanceof SignBlock
         || block instanceof ButtonBlock
         || block instanceof AbstractPressurePlateBlock
         || block instanceof CarpetBlock
         || block instanceof FlowerBlock
         || block instanceof TorchBlock
         || block instanceof LanternBlock
         || block instanceof AbstractCandleBlock
         || !blockstate.isFullCube(aiY.world, BlockPos.ORIGIN)
         || !blockstate.isOpaque();
   }
}
