package module;

import enum.Category;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import setting.BooleanSetting;
import setting.SliderSetting;

public class AutoTotemModule extends Module {
   private SliderSetting minHealthSetting = new SliderSetting("Минимальное HP", "HP при котором возьмется тотем", 10.0, 1.0, 20.0, 0.5);
   private BooleanSetting priorityNormalSetting = new BooleanSetting(
      "Приоритет обычных", "Сначала брать незачарённые тотемы, зачарённые — в последнюю очередь", false
   );
   private BooleanSetting crystalsSetting = new BooleanSetting("Кристаллы", "Брать тотем при кристалле рядом", false);
   private SliderSetting crystalDistanceSetting = new SliderSetting(
      "Дист. кристаллов", "Максимальная дистанция до кристалла", 6.0, 1.0, 12.0, 0.5, " блоков", 1
   );
   private BooleanSetting tntSetting = new BooleanSetting("ТНТ", "Брать тотем при активированном ТНТ рядом", false);
   private SliderSetting tntDistanceSetting = new SliderSetting("Дист. ТНТ", "Максимальная дистанция до ТНТ", 8.0, 1.0, 20.0, 0.5, " блоков", 1);
   private SliderSetting tntFuseSetting = new SliderSetting("Фьюз ТНТ", "Максимальный оставшийся фьюз для срабатывания", 40.0, 1.0, 80.0, 1.0, " тиков", 0);
   private int Kf = 0;
   private int ayj = 0;
   private int Rr = -1;
   private boolean aez = false;
   private et apt = et.LL;
   private boolean Jk = false;
   private int VK = -1;
   private ItemStack GU = ItemStack.EMPTY;

   public AutoTotemModule() {
      super("АутоТотем", "Автоматически устанавливает тотем бессмертия в руку", Category.COMBAT);
      this.crystalDistanceSetting.setVisibilitySupplier(this.crystalsSetting::getValue);
      this.tntDistanceSetting.setVisibilitySupplier(this.tntSetting::getValue);
      this.tntFuseSetting.setVisibilitySupplier(this.tntSetting::getValue);
      this.addSettings(
         this.minHealthSetting,
         this.priorityNormalSetting,
         this.crystalsSetting,
         this.crystalDistanceSetting,
         this.tntSetting,
         this.tntDistanceSetting,
         this.tntFuseSetting
      );
   }

   @Override
   public void onEnable() {
      this.d();
   }

   @Override
   public void onDisable() {
      this.d();
      if (this.getScreen() instanceof InventoryScreen) {
         this.getScreen().close();
      }
   }

   @Override
   public void onEndTick() {
      if (this.isNotInWorld()) {
         this.d();
      } else if (this.Kf > 0) {
         this.ayj++;
         this.a();
      } else {
         ItemStack itemstack = this.getPlayer().getEquippedStack(EquipmentSlot.OFFHAND);
         boolean flag = itemstack.getItem() == Items.TOTEM_OF_UNDYING;
         float f = this.getPlayer().getHealth();
         if (this.h(f, flag)) {
            this.c();
         } else {
            boolean flag1 = f <= this.minHealthSetting.getValue();
            if (!flag1 && this.crystalsSetting.getValue()) {
               flag1 = dz.a(this.getWorld(), this.getPlayer(), this.crystalDistanceSetting.getValue());
            }

            if (!flag1 && this.tntSetting.getValue()) {
               flag1 = dz.b(this.getWorld(), this.getPlayer(), this.tntDistanceSetting.getValue(), (int)this.tntFuseSetting.getValue());
            }

            if (flag1 && !flag) {
               int i = this.f();
               if (i == -1) {
                  return;
               }

               this.b(i);
            }
         }
      }
   }

   private void a() {
      switch (this.Kf) {
         case 1:
            if (this.ayj >= 2) {
               this.Kf = 2;
               this.ayj = 0;
            }
            break;
         case 2:
            try {
               this.e(this.Rr);
               this.Kf = 3;
               this.ayj = 0;
            } catch (Exception exception) {
               this.d();
            }
            break;
         case 3:
            if (this.ayj >= 1) {
               if (!this.aez && this.getScreen() instanceof InventoryScreen) {
                  this.getScreen().close();
               }

               this.d();
            }
      }

      if (this.ayj > 20) {
         if (!this.aez && this.getScreen() instanceof InventoryScreen) {
            this.getScreen().close();
         }

         this.d();
      }
   }

   private void b(int i) {
      this.aez = this.getScreen() instanceof InventoryScreen;
      this.Rr = i;
      this.Kf = 1;
      this.ayj = 0;
      this.apt = et.LL;
      this.GU = this.getPlayer().getEquippedStack(EquipmentSlot.OFFHAND).copy();
      if (!this.aez) {
         this.getClient().setScreen(new InventoryScreen(this.getPlayer()));
      } else {
         this.Kf = 2;
      }
   }

   private void c() {
      if (this.VK < 0) {
         this.Jk = false;
      } else {
         this.aez = this.getScreen() instanceof InventoryScreen;
         this.Rr = this.VK;
         this.Kf = 1;
         this.ayj = 0;
         this.apt = et.pk;
         if (!this.aez) {
            this.getClient().setScreen(new InventoryScreen(this.getPlayer()));
         } else {
            this.Kf = 2;
         }
      }
   }

   private void d() {
      this.Kf = 0;
      this.ayj = 0;
      this.Rr = -1;
      this.aez = false;
      this.apt = et.LL;
   }

   private void e(int i) {
      if (this.getInteractionManager() != null && this.getPlayer() != null) {
         int j = i < 9 ? i + 36 : i;
         int k = this.getPlayer().currentScreenHandler.syncId;
         this.getInteractionManager().clickSlot(k, j, 40, SlotActionType.SWAP, this.getPlayer());
         if (this.apt == et.LL) {
            this.Jk = true;
            this.VK = i;
         } else {
            this.Jk = false;
            this.VK = -1;
            this.GU = ItemStack.EMPTY;
         }
      }
   }

   private int f() {
      if (this.priorityNormalSetting.getValue()) {
         int i = this.g(true);
         if (i != -1) {
            return i;
         }
      }

      return this.g(false);
   }

   private int g(boolean flag) {
      for (int i = 0; i < 9; i++) {
         ItemStack itemstack = this.getPlayer().getInventory().getStack(i);
         if (itemstack.getItem() == Items.TOTEM_OF_UNDYING && (!flag || itemstack.getEnchantments().isEmpty())) {
            return i;
         }
      }

      for (int j = 9; j < 36; j++) {
         ItemStack itemstack1 = this.getPlayer().getInventory().getStack(j);
         if (itemstack1.getItem() == Items.TOTEM_OF_UNDYING && (!flag || itemstack1.getEnchantments().isEmpty())) {
            return j;
         }
      }

      return -1;
   }

   private boolean h(float f, boolean flag) {
      if (!this.Jk) {
         return false;
      } else if (this.Kf > 0) {
         return false;
      } else if (f <= this.minHealthSetting.getValue()) {
         return false;
      } else if (!flag) {
         return false;
      } else if (this.crystalsSetting.getValue() && dz.a(this.getWorld(), this.getPlayer(), this.crystalDistanceSetting.getValue())) {
         return false;
      } else if (this.tntSetting.getValue() && dz.b(this.getWorld(), this.getPlayer(), this.tntDistanceSetting.getValue(), (int)this.tntFuseSetting.getValue())
         )
       {
         return false;
      } else if (this.VK < 0 || this.VK > 35) {
         return false;
      } else if (this.GU.isEmpty()) {
         return false;
      } else {
         ItemStack itemstack = this.getPlayer().getInventory().getStack(this.VK);
         return ItemStack.areItemsAndComponentsEqual(itemstack, this.GU);
      }
   }
}
