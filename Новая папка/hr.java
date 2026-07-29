import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;

public class hr {
   private static final MinecraftClient atd = MinecraftClient.getInstance();
   private int Xi = -1;
   private boolean amZ = false;
   private long hy = 0L;
   private LivingEntity ajE = null;

   public void a(LivingEntity livingentity) {
      if (atd.player != null) {
         if (this.Xi == -1) {
            this.Xi = atd.player.getInventory().selectedSlot;
         }

         int i = bp.j();
         if (i != -1) {
            atd.player.getInventory().selectedSlot = i;
            this.amZ = true;
            this.hy = System.currentTimeMillis();
            this.ajE = livingentity;
         }
      }
   }

   public void b() {
      if (this.amZ && this.Xi != -1 && atd.player != null) {
         long i = System.currentTimeMillis() - this.hy;
         boolean flag = i >= 200L;
         if ((this.ajE == null || !bp.i(this.ajE)) && i >= 100L) {
            flag = true;
         }

         if (flag) {
            this.c();
         }
      }
   }

   public void c() {
      if (atd.player != null) {
         if (this.Xi != -1 && this.Xi < 9) {
            atd.player.getInventory().selectedSlot = this.Xi;
         }

         this.d();
      }
   }

   public void d() {
      this.Xi = -1;
      this.amZ = false;
      this.hy = 0L;
      this.ajE = null;
   }

   public boolean e() {
      return this.amZ;
   }
}
