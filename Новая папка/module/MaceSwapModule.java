package module;

import enum.Category;
import event.RotationEvent;
import event.UseItemEvent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class MaceSwapModule extends Module {
   private boolean dn = false;
   private int alB = -1;
   private int qO = 0;

   public MaceSwapModule() {
      super("МасеЗшап", "При ударе мечем, модуль свапнет на булаву, урон увеличится на 1-2 единицы. Рабоатет на серверах 1.21+", Category.COMBAT);
   }

   @Override
   public void onEnable() {
      this.dn = false;
      this.qO = 0;
      this.alB = -1;
   }

   @Override
   public void onDisable() {
      this.dn = false;
      this.qO = 0;
      this.alB = -1;
   }

   @Override
   public void onUseItem(UseItemEvent useitemevent) {
      if (mc.player != null && useitemevent.getHand() == Hand.MAIN_HAND && !this.dn) {
         ItemStack itemstack = mc.player.getMainHandStack();
         if (this.d(itemstack)) {
            int i = this.c();
            if (i != -1) {
               this.a(i);
            }
         }
      }
   }

   private void a(int i) {
      if (mc.player != null) {
         this.alB = mc.player.getInventory().selectedSlot;
         this.dn = true;
         this.qO = 0;
         this.b(i);
      }
   }

   private void b(int i) {
      if (mc.player != null) {
         mc.player.getInventory().selectedSlot = i;
      }
   }

   private int c() {
      if (mc.player == null) {
         return -1;
      } else {
         PlayerInventory playerinventory = mc.player.getInventory();

         for (int i = 0; i < 9; i++) {
            ItemStack itemstack = playerinventory.getStack(i);
            if (itemstack.getItem() == Items.MACE) {
               return i;
            }
         }

         return -1;
      }
   }

   private boolean d(ItemStack itemstack) {
      return itemstack != null && !itemstack.isEmpty()
         ? itemstack.getItem() == Items.WOODEN_SWORD
            || itemstack.getItem() == Items.STONE_SWORD
            || itemstack.getItem() == Items.IRON_SWORD
            || itemstack.getItem() == Items.GOLDEN_SWORD
            || itemstack.getItem() == Items.DIAMOND_SWORD
            || itemstack.getItem() == Items.NETHERITE_SWORD
         : false;
   }

   @Override
   public void onRotation(RotationEvent rotationevent) {
      if (this.dn && mc.player != null) {
         this.qO++;
         if (this.qO >= 2 && this.alB != -1) {
            this.b(this.alB);
            this.dn = false;
            this.qO = 0;
            this.alB = -1;
         }
      }
   }
}
