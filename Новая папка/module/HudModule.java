package module;

import config.Config;
import core.ClientMain;
import data.Position;
import enum.Category;
import hud.HudManager;
import java.util.HashMap;
import setting.ActionSetting;
import setting.BooleanSetting;
import setting.GroupSetting;

public class HudModule extends Module {
   private BooleanSetting watermarkSetting = new BooleanSetting("Ватермарка", "Рендерить ватермарку?", true);
   private GroupSetting watermarkSettingsGroup = new GroupSetting("Настройки ватермарки", "Элементы ватермарки", false);
   private BooleanSetting fpsSetting = new BooleanSetting("FPS", "Показывать FPS", true);
   private BooleanSetting pingSetting = new BooleanSetting("Пинг", "Показывать пинг", true);
   private BooleanSetting tpsSetting = new BooleanSetting("TPS", "Показывать TPS сервера", true);
   private BooleanSetting timeSetting = new BooleanSetting("Время", "Показывать текущее время", true);
   private BooleanSetting keybindsSetting = new BooleanSetting("Кейбинды", "Рендерить кейбинды?", true);
   private BooleanSetting visualsInBindsSetting = new BooleanSetting("Визуалы в биндах", "Показывать бинды на визуал-модули", true);
   private BooleanSetting staffListSetting = new BooleanSetting("Список стафа", "Рендерить список включенных модулей?", true);
   private BooleanSetting potionEffectsSetting = new BooleanSetting("Эффекты зелий", "Рендерить список активных зелий?", true);
   private BooleanSetting armorSetting = new BooleanSetting("Броня", "Рендерить список брони?", true);
   private BooleanSetting targetHudSetting = new BooleanSetting("ТаргетХуд", "Рендерить список броню?", true);
   private final BooleanSetting customHotbarSetting = new BooleanSetting("Кастом хотбар", "z", true);
   private final BooleanSetting customInventorySetting = new BooleanSetting("Кастомный инв", "z", true);
   private final BooleanSetting saturationSetting = new BooleanSetting("Насыщение", "z", true);
   private ActionSetting apl = new ActionSetting("Сбросить позиции HUD", "Сбросить все позиции HUD элементов на дефолтные");

   public HudModule() {
      super("Худ", "Рендер определенных элементов на экране", Category.CLIENT);
      this.apl.setCallback(this::a);
      this.watermarkSettingsGroup.addSettings(this.fpsSetting, this.pingSetting, this.tpsSetting, this.timeSetting);
      this.visualsInBindsSetting.setVisibilitySupplier(this.keybindsSetting::getValue);
      this.addSettings(
         this.watermarkSetting,
         this.watermarkSettingsGroup,
         this.keybindsSetting,
         this.visualsInBindsSetting,
         this.staffListSetting,
         this.potionEffectsSetting,
         this.armorSetting,
         this.targetHudSetting,
         this.customHotbarSetting,
         this.customInventorySetting,
         this.saturationSetting,
         this.apl
      );
   }

   private void a() {
      try {
         Config config = ClientMain.getInstance().getConfigManager().x();
         if (config != null) {
            if (config.d() != null) {
               config.d().clear();
            } else {
               config.q(new HashMap<String, Position>());
            }

            HudManager.b().l();
            if (!ClientMain.getInstance().getConfigSyncManager().k()) {
               ClientMain.getInstance().getConfigSyncManager().d(config);
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public BooleanSetting b() {
      return this.watermarkSetting;
   }

   public GroupSetting c() {
      return this.watermarkSettingsGroup;
   }

   public BooleanSetting d() {
      return this.fpsSetting;
   }

   public BooleanSetting e() {
      return this.pingSetting;
   }

   public BooleanSetting f() {
      return this.tpsSetting;
   }

   public BooleanSetting g() {
      return this.timeSetting;
   }

   public BooleanSetting h() {
      return this.keybindsSetting;
   }

   public BooleanSetting i() {
      return this.visualsInBindsSetting;
   }

   public BooleanSetting j() {
      return this.staffListSetting;
   }

   public BooleanSetting k() {
      return this.potionEffectsSetting;
   }

   public BooleanSetting l() {
      return this.armorSetting;
   }

   public BooleanSetting m() {
      return this.targetHudSetting;
   }

   public BooleanSetting n() {
      return this.customHotbarSetting;
   }

   public BooleanSetting o() {
      return this.customInventorySetting;
   }

   public BooleanSetting p() {
      return this.saturationSetting;
   }
}
