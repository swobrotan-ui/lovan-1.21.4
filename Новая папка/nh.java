import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class nh {
   private final MinecraftClient asU = MinecraftClient.getInstance();
   private long Xh = 100L;
   private final ScheduledExecutorService azB = Executors.newSingleThreadScheduledExecutor();
   private boolean uC = false;
   private LivingEntity DD = null;
   private long UE = 0L;
   private float yZ = 0.0F;

   public void a(LivingEntity livingentity) {
      if (!this.uC && livingentity != null && this.asU.player != null && this.asU.world != null) {
         if (this.asU.player.isGliding()) {
            this.DD = livingentity;
            this.uC = true;
            this.UE = System.currentTimeMillis();
         }
      }
   }

   public void b() {
      this.uC = false;
      this.DD = null;
   }

   public void c() {
      if (this.uC && this.asU.player != null && this.asU.world != null && this.DD != null) {
         if (!this.DD.isDead() && this.DD.isAlive()) {
            long i = System.currentTimeMillis();
            if (i - this.UE >= this.Xh) {
               this.d();
               this.UE = i;
            }
         } else {
            this.b();
         }
      }
   }

   private void d() {
      if (this.asU.player != null && this.asU.world != null && this.asU.player.isGliding()) {
         int i = this.e();
         if (i != -1) {
            double d0 = this.DD.getX() - this.asU.player.getX();
            double d1 = this.DD.getZ() - this.asU.player.getZ();
            double d2 = this.DD.getY() + this.DD.getHeight() * 0.5 - this.asU.player.getEyeY();
            double d3 = Math.sqrt(d0 * d0 + d1 * d1);
            float f = (float)(Math.toDegrees(Math.atan2(d1, d0)) - 90.0);
            float f1 = (float)(-Math.toDegrees(Math.atan2(d2, d3)));
            int j = this.asU.player.getInventory().selectedSlot;
            boolean flag = i < 9;
            if (!flag) {
               this.f(i, 8);
               i = 8;
            }

            this.asU.player.getInventory().selectedSlot = i;
            this.asU.interactionManager.interactItem(this.asU.player, Hand.MAIN_HAND);
            if (this.yZ > 0.0F) {
               this.g(f, f1);
            }

            this.asU.player.getInventory().selectedSlot = j;
            if (!flag) {
               this.f(8, i);
            }
         }
      }
   }

   private int e() {
      if (this.asU.player == null) {
         return -1;
      } else {
         for (int i = 0; i < this.asU.player.getInventory().size(); i++) {
            if (this.asU.player.getInventory().getStack(i).getItem() == Items.FIREWORK_ROCKET) {
               return i;
            }
         }

         return -1;
      }
   }

   private void f(int i, int j) {
      if (this.asU.interactionManager != null && this.asU.player != null) {
         int k = i < 9 ? i + 36 : i;
         int l = j < 9 ? j + 36 : j;
         this.asU.interactionManager.clickSlot(0, k, 0, SlotActionType.PICKUP, this.asU.player);
         this.asU.interactionManager.clickSlot(0, l, 0, SlotActionType.PICKUP, this.asU.player);
         this.asU.interactionManager.clickSlot(0, k, 0, SlotActionType.PICKUP, this.asU.player);
      }
   }

   private void g(float f, float f1) {
      if (this.asU.player != null && this.DD != null) {
         Vec3d vec3d = Vec3d.fromPolar(f1, f).normalize();
         Vec3d vec3d1 = this.asU.player.getVelocity();
         double d0 = 0.05 * this.yZ;
         Vec3d vec3d2 = new Vec3d(vec3d1.x + vec3d.x * d0, vec3d1.y + vec3d.y * d0, vec3d1.z + vec3d.z * d0);
         this.asU.player.setVelocity(vec3d2);
      }
   }

   public boolean h(LivingEntity livingentity) {
      if (livingentity == null) {
         return false;
      } else {
         ItemStack itemstack = livingentity.getEquippedStack(EquipmentSlot.CHEST);
         return !itemstack.isEmpty() && Registries.ITEM.getId(itemstack.getItem()).equals(Identifier.of("minecraft", "elytra"));
      }
   }

   public void i() {
      this.b();
      if (!this.azB.isShutdown()) {
         this.azB.shutdown();

         try {
            if (!this.azB.awaitTermination(100L, TimeUnit.MILLISECONDS)) {
               this.azB.shutdownNow();
            }

            return;
         } catch (InterruptedException interruptedexception) {
            this.azB.shutdownNow();
            Thread.currentThread().interrupt();
         }
      }
   }

   public boolean j() {
      return this.uC;
   }

   public LivingEntity k() {
      return this.DD;
   }

   public void l(float f) {
      this.yZ = f;
   }
}
