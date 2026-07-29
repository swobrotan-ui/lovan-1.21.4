package module;

import enum.Category;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import setting.ActionKeySetting;
import setting.SliderSetting;

public class KeyPearlModule extends Module {
   private ActionKeySetting throwKeySetting = new ActionKeySetting("Кнопка броска", "Кнопка для использования эндер-жемчуга", 2, this::a);
   private SliderSetting delaySetting = new SliderSetting("Задержка", "Задержка между действиями (в тиках)", 1.0, 0.0, 5.0, 1.0);
   private int aaw = 0;
   private int TX = 0;
   private int awi = -1;
   private int DE = -1;
   private boolean avU = false;
   private boolean aeO = false;

   public KeyPearlModule() {
      super("КейПеарл", "Использование эндер жемчугов по клавише", Category.MOVEMENT);
      this.addSettings(this.throwKeySetting, this.delaySetting);
   }

   @Override
   public void onEnable() {
      this.e();
   }

   @Override
   public void onDisable() {
      this.e();
   }

   private void a() {
      if (!this.isNotInWorld() && this.aaw == 0) {
         int i = this.f();
         if (i != -1) {
            this.DE = i;
            this.awi = this.getInventory().selectedSlot;
            this.avU = this.DE >= 36 && this.DE <= 44;
            this.aeO = this.getScreen() instanceof InventoryScreen;
            this.aaw = 1;
            this.TX = 0;
         }
      }
   }

   @Override
   public void onStartTick() {
      if (this.hasPlayerAndWorld() && this.aaw != 0) {
         if (this.TX > 0) {
            this.TX--;
         } else if (this.avU) {
            this.b();
         } else {
            this.c();
         }
      }
   }

   private void b() {
      switch (this.aaw) {
         case 1:
            this.getInventory().selectedSlot = this.DE - 36;
            this.d();
            return;
         case 2:
            this.interactItem(this.getMainHand());
            this.d();
            return;
         case 3:
            this.getInventory().selectedSlot = this.awi;
            this.e();
      }
   }

   private void c() {
      switch (this.aaw) {
         case 1:
            if (!(this.getScreen() instanceof InventoryScreen)) {
               this.getClient().setScreen(new InventoryScreen(this.getPlayer()));
            }

            this.d();
            return;
         case 2:
            this.g(this.DE, this.awi);
            this.d();
            return;
         case 3:
            if (this.getScreen() instanceof InventoryScreen) {
               this.getScreen().close();
            }

            this.d();
            return;
         case 4:
            this.interactItem(this.getMainHand());
            this.d();
            return;
         case 5:
            this.getClient().setScreen(new InventoryScreen(this.getPlayer()));
            this.d();
            return;
         case 6:
            this.g(this.DE, this.awi);
            this.d();
            return;
         case 7:
            if (!this.aeO && this.getScreen() instanceof InventoryScreen) {
               this.getScreen().close();
            }

            this.e();
      }
   }

   private void d() {
      this.aaw++;
      this.TX = this.delaySetting.getIntValue();
   }

   private void e() {
      this.aaw = 0;
      this.TX = 0;
      this.awi = -1;
      this.DE = -1;
      this.avU = false;
      this.aeO = false;
   }

   private int f() {
      if (this.getPlayer() == null) {
         return -1;
      } else {
         DefaultedList defaultedlist = this.getPlayer().currentScreenHandler.slots;

         for (Slot slot : defaultedlist) {
            if (slot.id >= 36 && slot.id <= 44 && slot.getStack().getItem() == Items.ENDER_PEARL) {
               return slot.id;
            }
         }

         for (Slot slot1 : defaultedlist) {
            if (slot1.id >= 9 && slot1.id <= 35 && slot1.getStack().getItem() == Items.ENDER_PEARL) {
               return slot1.id;
            }
         }

         return -1;
      }
   }

   private void g(int i, int j) {
      if (this.getInteractionManager() != null && this.getPlayer() != null) {
         if (j >= 0 && j <= 8) {
            int k = this.getPlayer().currentScreenHandler.syncId;
            this.getInteractionManager().clickSlot(k, i, j, SlotActionType.SWAP, this.getPlayer());
         }
      }
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      super.onPlayerDeath(playerentity);
      if (this.enabledSetting.getValue()) {
         this.setEnabled(false);
      }
   }
}
