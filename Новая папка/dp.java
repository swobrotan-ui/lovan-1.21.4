import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class dp extends Entity {
   private double Bj;
   private double zq;
   private GameMode gL;
   MinecraftClient pa = MinecraftClient.getInstance();

   public dp(World world, Vec3d vec3d, float f, float f1, float f2, float f3) {
      super(EntityType.MARKER, world);
      this.setPosition(vec3d);
      this.setYaw(f);
      this.setPitch(f1);
      this.Bj = f2;
      this.zq = f3;
      this.noClip = true;
      this.setInvisible(true);
      if (this.pa.player != null && this.pa.interactionManager != null) {
         this.gL = this.pa.interactionManager.getCurrentGameMode();
         this.pa.interactionManager.setGameMode(GameMode.SPECTATOR);
      }
   }

   protected void initDataTracker(Builder builder) {
   }

   public void tick() {
      super.tick();
      if (this.pa.player != null) {
         this.handleMovement();
      }
   }

   public boolean damage(ServerWorld serverworld, DamageSource damagesource, float f) {
      return false;
   }

   private void handleMovement() {
      if (this.pa.options != null) {
         Vec3d vec3d = Vec3d.ZERO;
         double d0 = this.pa.options.sprintKey.isPressed() ? this.zq : this.Bj;
         float f = this.getYaw();
         float f1 = this.getPitch();
         double d1 = Math.toRadians(f);
         double d2 = Math.toRadians(f1);
         Vec3d vec3d1 = new Vec3d(-Math.sin(d1) * Math.cos(d2), -Math.sin(d2), Math.cos(d1) * Math.cos(d2));
         Vec3d vec3d2 = new Vec3d(Math.cos(d1), 0.0, Math.sin(d1));
         Vec3d vec3d3 = new Vec3d(0.0, 1.0, 0.0);
         if (this.pa.options.forwardKey.isPressed()) {
            vec3d = vec3d.add(vec3d1.multiply(d0));
         }

         if (this.pa.options.backKey.isPressed()) {
            vec3d = vec3d.add(vec3d1.multiply(-d0));
         }

         if (this.pa.options.leftKey.isPressed()) {
            vec3d = vec3d.add(vec3d2.multiply(d0));
         }

         if (this.pa.options.rightKey.isPressed()) {
            vec3d = vec3d.add(vec3d2.multiply(-d0));
         }

         if (this.pa.options.jumpKey.isPressed()) {
            vec3d = vec3d.add(vec3d3.multiply(d0));
         }

         if (this.pa.options.sneakKey.isPressed()) {
            vec3d = vec3d.add(vec3d3.multiply(-d0));
         }

         if (!vec3d.equals(Vec3d.ZERO)) {
            Vec3d vec3d4 = this.getPos().add(vec3d);
            this.setPosition(vec3d4);
         }
      }
   }

   public void updateSpeeds(float f, float f1) {
      this.Bj = f;
      this.zq = f1;
   }

   public void restoreGameMode() {
      if (this.pa.interactionManager != null && this.gL != null) {
         this.pa.interactionManager.setGameMode(this.gL);
      }
   }

   public boolean shouldRender(double d0) {
      return false;
   }

   protected void readCustomDataFromNbt(NbtCompound nbtcompound) {
   }

   protected void writeCustomDataToNbt(NbtCompound nbtcompound) {
   }

   public boolean canMoveVoluntarily() {
      return false;
   }

   public boolean isInvisible() {
      return true;
   }
}
