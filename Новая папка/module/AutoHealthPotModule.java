package module;

import enum.Category;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import setting.ActionKeySetting;
import setting.SliderSetting;

public class AutoHealthPotModule extends Module {
   private ActionKeySetting potionKeySetting = new ActionKeySetting("Кнопка зелья здоровья", "Кнопка для использования лечебного зелья", 72, this::b);
   private SliderSetting throwDelaySetting = new SliderSetting("Задержка броска", "Задержка перед броском зелья (в мс)", 50.0, 0.0, 300.0, 10.0);
   private SliderSetting returnDelaySetting = new SliderSetting("Задержка возврата", "Задержка перед возвратом камеры (в мс)", 100.0, 50.0, 500.0, 25.0);
   private ScheduledExecutorService Pu;
   private boolean acU = false;
   private float alf = 0.0F;
   private float gp = 0.0F;
   private int avh = -1;
   private ItemStack wO = ItemStack.EMPTY;
   private boolean agh = false;
   private float qi = 85.0F;
   private long mR = 0L;
   private int wN = 8;
   private boolean afv = true;

   public AutoHealthPotModule() {
      super("АутоХеалтхПот", "Автоматическое использование зелий исцеления", Category.PLAYER);
      this.addSettings(this.throwDelaySetting, this.returnDelaySetting, this.potionKeySetting);
      this.Pu = Executors.newSingleThreadScheduledExecutor();
   }

   @Override
   public void onEnable() {
      this.avh = this.hasPlayer() ? this.getInventory().selectedSlot : -1;
      if (this.Pu == null || this.Pu.isShutdown()) {
         this.Pu = Executors.newSingleThreadScheduledExecutor();
      }
   }

   @Override
   public void onDisable() {
      this.acU = false;
      this.agh = false;
      if (this.Pu != null && !this.Pu.isShutdown()) {
         this.Pu.shutdownNow();
      }
   }

   @Override
   public void onEndTick() {
      if (this.agh) {
         this.f();
      }
   }

   private ScheduledExecutorService a() {
      if (this.Pu == null || this.Pu.isShutdown()) {
         this.Pu = Executors.newSingleThreadScheduledExecutor();
      }

      return this.Pu;
   }

   private void b() {
      if (!this.acU && !this.isNotInWorld()) {
         int i = this.c();
         if (i != -1) {
            PlayerEntity playerentity = this.getPlayer();
            this.acU = true;
            this.alf = playerentity.getPitch();
            this.gp = playerentity.getYaw();
            this.avh = this.getInventory().selectedSlot;
            if (this.afv) {
               this.wO = this.getInventory().getStack(this.wN).copy();
            }

            this.e();
         }
      }
   }

   private int c() {
      int i = -1;
      int j = 0;

      for (int k = 0; k < this.getInventory().size(); k++) {
         ItemStack itemstack = this.getInventory().getStack(k);
         if (itemstack.getItem() == Items.SPLASH_POTION) {
            int l = this.d(itemstack);
            if (l > 0) {
               if (l == 2) {
                  return k;
               }

               if (l > j) {
                  j = l;
                  i = k;
               }
            }
         }
      }

      return i;
   }

   private int d(ItemStack itemstack) {
      String s = itemstack.getName().getString().toLowerCase();
      if (!s.contains("healing") && !s.contains("исцеления") && !s.contains("лечения")) {
         return 0;
      } else {
         return !s.contains("ii") && !s.contains("2") && !s.contains("ии") ? 1 : 2;
      }
   }

   private void e() {
      this.getPlayer().setPitch(this.qi);
      this.h();
   }

   private void f() {
      if (this.agh) {
         PlayerEntity playerentity = this.getPlayer();
         float f = this.qi - this.alf;
         long i = System.currentTimeMillis() - this.mR;
         float f1 = Math.min(1.0F, (float)i / 200.0F);
         float f2 = (float)(0.5 - 0.5 * Math.cos(f1 * Math.PI));
         float f3 = this.alf + f * f2;
         playerentity.setPitch(f3);
         if (f1 >= 1.0F) {
            this.agh = false;
            this.a().schedule(() -> {
               this.getClient().execute(this::h);
            }, this.throwDelaySetting.getLongValue(), TimeUnit.MILLISECONDS);
         }
      }
   }

   private void h() {
      int i = this.c();
      if (i == -1) {
         this.o();
      } else if (i >= 9) {
         this.swapSlots(i, this.wN);
         this.a().schedule(() -> {
            this.getClient().execute(() -> {
               this.getInventory().selectedSlot = this.wN;
               this.i();
            });
         }, 50L, TimeUnit.MILLISECONDS);
      } else if (i < 9) {
         this.getInventory().selectedSlot = i;
         this.i();
      } else {
         this.o();
      }
   }

   private void i() {
      this.interactItem(this.getMainHand());
      this.a().schedule(() -> {
         this.getClient().execute(this::o);
      }, this.returnDelaySetting.getLongValue(), TimeUnit.MILLISECONDS);
   }

   private void o() {
      this.a().schedule(() -> {
         this.getClient().execute(() -> {
            this.getPlayer().setPitch(this.alf);
            this.getPlayer().setYaw(this.gp);
            this.p();
         });
      }, 50L, TimeUnit.MILLISECONDS);
   }

   private void p() {
      this.getInventory().selectedSlot = this.avh;
      if (this.afv && !this.wO.isEmpty()) {
         this.a().schedule(() -> {
            this.getClient().execute(() -> {
               this.getInventory().setStack(this.wN, this.wO);
               this.wO = ItemStack.EMPTY;
            });
         }, 100L, TimeUnit.MILLISECONDS);
      }

      this.acU = false;
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      super.onPlayerDeath(playerentity);
      if (this.enabledSetting.getValue()) {
         this.setEnabled(false);
      }

      this.acU = false;
      this.agh = false;
   }
}
