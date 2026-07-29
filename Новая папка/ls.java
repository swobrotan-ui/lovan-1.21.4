import core.ClientMain;
import module.SprintModule;
import net.minecraft.client.MinecraftClient;

public class ls {
   private static final MinecraftClient Mq = MinecraftClient.getInstance();
   private static final long vu = 300L;
   private boolean RK = false;
   private boolean Je = false;
   private boolean zF = false;
   private boolean awd = false;
   private long Xb = -1L;

   public void a() {
      if (!this.RK && Mq.player != null && Mq.options != null) {
         SprintModule sprintmodule = ClientMain.getInstance().getModuleManager().<SprintModule>getModule(SprintModule.class);
         this.Je = Mq.options.sprintKey.isPressed();
         this.zF = sprintmodule != null && sprintmodule.isEnabled();
         Mq.player.setSprinting(false);
         Mq.options.sprintKey.setPressed(false);
         if (sprintmodule != null && this.zF) {
            sprintmodule.setSprinting(false);
         }

         this.RK = true;
         this.awd = true;
         this.Xb = System.currentTimeMillis();
      }
   }

   public void b() {
      this.awd = false;
   }

   public boolean c() {
      return this.RK && this.Xb != -1L && System.currentTimeMillis() - this.Xb > 300L;
   }

   public void d() {
      if (Mq.player != null && Mq.options != null) {
         Mq.options.sprintKey.setPressed(false);
         Mq.player.setSprinting(false);
      }
   }

   public void e() {
      if (this.RK && Mq.player != null && Mq.options != null) {
         try {
            SprintModule sprintmodule = ClientMain.getInstance().getModuleManager().<SprintModule>getModule(SprintModule.class);
            if (sprintmodule != null && this.zF) {
               sprintmodule.setSprinting(true);
               Mq.options.sprintKey.setPressed(true);
               Mq.player.setSprinting(true);
            } else if (this.Je) {
               Mq.options.sprintKey.setPressed(true);
               Mq.player.setSprinting(true);
               return;
            }

            return;
         } catch (Exception exception) {
         } finally {
            this.f();
         }
      }
   }

   public void f() {
      this.RK = false;
      this.Je = false;
      this.zF = false;
      this.awd = false;
      this.Xb = -1L;
   }

   public boolean g() {
      return this.RK;
   }

   public boolean h() {
      return this.awd;
   }
}
