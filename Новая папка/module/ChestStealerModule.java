package module;

import enum.Category;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import setting.BooleanSetting;
import setting.SliderSetting;

public class ChestStealerModule extends Module {
   private SliderSetting delaySetting = new SliderSetting("Задержка", "Задержка между перемещением предметов (в мс)", 100.0, 0.0, 500.0, 10.0);
   private BooleanSetting autoCloseSetting = new BooleanSetting("Авто-закрытие", "Закрывать контейнер после стила", true);
   private long Hy = 0L;

   public ChestStealerModule() {
      super("СхезтЗтеалер", "Автоматически забирает предметы из контейнеров", Category.PLAYER);
      this.addSettings(this.delaySetting, this.autoCloseSetting);
   }

   @Override
   public void onEnable() {
      this.Hy = 0L;
   }

   @Override
   public void onDisable() {
      this.Hy = 0L;
   }

   @Override
   public void onEndTick() {
      if (!this.isNotInWorld() && this.getInteractionManager() != null && this.getPlayer() != null) {
         if (this.getScreen() instanceof HandledScreen handledscreen) {
            if (!(handledscreen instanceof InventoryScreen) && !(handledscreen instanceof CreativeInventoryScreen)) {
               ScreenHandler screenhandler = handledscreen.getScreenHandler();
               if (this.b(screenhandler)) {
                  long i = System.currentTimeMillis();
                  if (i - this.Hy >= this.delaySetting.getLongValue()) {
                     Slot slot = this.c(screenhandler);
                     if (slot == null) {
                        if (this.autoCloseSetting.getValue() && this.a()) {
                           handledscreen.close();
                           this.getClient().setScreen(null);
                        }
                     } else {
                        this.getInteractionManager().clickSlot(screenhandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, this.getPlayer());
                        this.Hy = i;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean a() {
      Screen screen = this.getScreen();
      if (!(screen instanceof HandledScreen)) {
         return false;
      } else {
         return screen instanceof InventoryScreen ? false : !(screen instanceof CreativeInventoryScreen);
      }
   }

   private boolean b(ScreenHandler screenhandler) {
      PlayerInventory playerinventory = this.getPlayer().getInventory();

      for (Slot slot : screenhandler.slots) {
         if (slot != null && slot.inventory != playerinventory) {
            return true;
         }
      }

      return false;
   }

   private Slot c(ScreenHandler screenhandler) {
      PlayerInventory playerinventory = this.getPlayer().getInventory();

      for (Slot slot : screenhandler.slots) {
         if (slot != null && slot.hasStack() && slot.inventory != playerinventory && slot.canTakeItems(this.getPlayer())) {
            return slot;
         }
      }

      return null;
   }
}
