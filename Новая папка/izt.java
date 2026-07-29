import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class izt extends Entity {
   private final float gd;
   private final float GV;
   private final ItemStack[] tE;
   private final ItemStack[] Wq;
   private final String Cv;

   public izt(World world, Vec3d vec3d, float f, float f1, ClientPlayerEntity clientplayerentity) {
      super(EntityType.PLAYER, world);
      this.setPosition(vec3d);
      this.setYaw(f);
      this.setPitch(f1);
      this.gd = f;
      this.GV = f1;
      this.Cv = clientplayerentity.getName().getString();
      this.tE = new ItemStack[36];

      for (int i = 0; i < 36; i++) {
         ItemStack itemstack = clientplayerentity.getInventory().getStack(i);
         this.tE[i] = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
      }

      this.Wq = new ItemStack[4];

      for (int j = 0; j < 4; j++) {
         ItemStack itemstack1 = clientplayerentity.getInventory().getArmorStack(j);
         this.Wq[j] = itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1.copy();
      }

      this.noClip = true;
      this.setInvisible(false);
   }

   protected void initDataTracker(Builder builder) {
   }

   public void tick() {
      super.tick();
      this.setVelocity(Vec3d.ZERO);
      this.setYaw(this.gd);
      this.setPitch(this.GV);
   }

   public boolean damage(ServerWorld serverworld, DamageSource damagesource, float f) {
      return false;
   }

   public boolean shouldRender(double d0) {
      return d0 < 128.0;
   }

   protected void readCustomDataFromNbt(NbtCompound nbtcompound) {
   }

   protected void writeCustomDataToNbt(NbtCompound nbtcompound) {
   }

   public boolean canMoveVoluntarily() {
      return false;
   }

   public boolean isInvisible() {
      return false;
   }

   public boolean canHit() {
      return false;
   }

   public boolean canBeHitByProjectile() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }
}
