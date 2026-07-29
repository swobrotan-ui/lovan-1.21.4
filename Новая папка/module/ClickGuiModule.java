package module;

import core.ClientMain;
import enum.Category;
import net.minecraft.client.MinecraftClient;
import setting.BooleanSetting;

public class ClickGuiModule extends Module {
   private final BooleanSetting blurGuiSetting = new BooleanSetting("Блюр GUI", "Размытие фона при открытом ClickGUI", true);
   private final BooleanSetting blurCustomInventorySetting = new BooleanSetting("Блюр кастом инв", "Размытие фона в кастомном инвентаре", true);

   public ClickGuiModule() {
      super("СлискГуи", "Открыть/закрыть меню", Category.CLIENT);
      this.keyBindSetting.setDefaultKeyCode(344);
      this.keyBindSetting.setKeyCode(344);
      this.addSettings(this.blurGuiSetting, this.blurCustomInventorySetting);
   }

   public void a() {
      if (this.keyBindSetting.getKeyCode() == -1) {
         this.keyBindSetting.setKeyCode(344);
      }
   }

   @Override
   public void toggle() {
      if (!ClientMain.getInstance().getModuleManager().<PanicModule>getModule(PanicModule.class).c()) {
         MinecraftClient minecraftclient = MinecraftClient.getInstance();
         if (minecraftclient.currentScreen == null) {
            minecraftclient.setScreen(new zj());
         } else {
            if (minecraftclient.currentScreen instanceof zj) {
               minecraftclient.setScreen(null);
            }
         }
      }
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public BooleanSetting b() {
      return this.blurGuiSetting;
   }

   public BooleanSetting c() {
      return this.blurCustomInventorySetting;
   }
}
