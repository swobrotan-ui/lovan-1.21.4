package module;

import core.SoundManager;
import enum.Category;
import enum.SoundType;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import setting.BooleanSetting;
import setting.Setting;
import setting.SliderSetting;

public class SoundsModule extends Module {
   private final BooleanSetting enableSoundsSetting = new BooleanSetting("Включить звуки", "Включить систему звуков", true);
   private final SliderSetting volumeSetting = new SliderSetting("Громкость", "Общая громкость звуков", 20.0, 0.0, 100.0, 1.0, "%", 0);
   private final BooleanSetting guiOpenSoundSetting = new BooleanSetting("Открытие GUI", "Звук открытия интерфейса", true);
   private final BooleanSetting guiCloseSoundSetting = new BooleanSetting("Закрытие GUI", "Звук закрытия интерфейса", true);
   private final BooleanSetting moduleEnableSoundSetting = new BooleanSetting("Включение модуля", "Звук включения модуля", true);
   private final BooleanSetting moduleDisableSoundSetting = new BooleanSetting("Выключение модуля", "Звук выключения модуля", true);
   private final BooleanSetting friendAddSoundSetting = new BooleanSetting("Добавление друга", "Звук добавления в друзья", true);
   private final BooleanSetting friendRemoveSoundSetting = new BooleanSetting("Удаление друга", "Звук удаления из друзей", true);
   private final BooleanSetting groupOpenSoundSetting = new BooleanSetting("Открытие группы", "Звук открытия группы настроек", true);
   private final BooleanSetting favouriteAddSoundSetting = new BooleanSetting("Добавление в избранное", "Звук добавления в избранное", true);
   private final BooleanSetting categorySwitchSoundSetting = new BooleanSetting("Переключение категории", "Звук переключения категории", true);
   private final BooleanSetting toggleOnSoundSetting = new BooleanSetting("Включение переключателя", "Звук включения переключателя", true);
   private final BooleanSetting toggleOffSoundSetting = new BooleanSetting("Выключение переключателя", "Звук выключения переключателя", true);
   private final Map<SoundType, BooleanSetting> Rb = new HashMap<SoundType, BooleanSetting>();

   public SoundsModule() {
      super("Зоундз", "Управление звуками интерфейса", Category.CLIENT);
      this.Rb.put(SoundType.GUI_OPEN, this.guiOpenSoundSetting);
      this.Rb.put(SoundType.GUI_CLOSE, this.guiCloseSoundSetting);
      this.Rb.put(SoundType.MODULE_ENABLE, this.moduleEnableSoundSetting);
      this.Rb.put(SoundType.MODULE_DISABLE, this.moduleDisableSoundSetting);
      this.Rb.put(SoundType.FRIEND_ADD, this.friendAddSoundSetting);
      this.Rb.put(SoundType.FRIEND_REMOVE, this.friendRemoveSoundSetting);
      this.Rb.put(SoundType.GROUP_OPEN, this.groupOpenSoundSetting);
      this.Rb.put(SoundType.FAVOURITE_ADD, this.favouriteAddSoundSetting);
      this.Rb.put(SoundType.CATEGORY_SWITCH, this.categorySwitchSoundSetting);
      this.Rb.put(SoundType.TOGGLE_ON, this.toggleOnSoundSetting);
      this.Rb.put(SoundType.TOGGLE_OFF, this.toggleOffSoundSetting);
      this.addSettings(
         this.enableSoundsSetting,
         this.volumeSetting,
         this.guiOpenSoundSetting,
         this.guiCloseSoundSetting,
         this.moduleEnableSoundSetting,
         this.moduleDisableSoundSetting,
         this.friendAddSoundSetting,
         this.friendRemoveSoundSetting,
         this.groupOpenSoundSetting,
         this.favouriteAddSoundSetting,
         this.categorySwitchSoundSetting,
         this.toggleOnSoundSetting,
         this.toggleOffSoundSetting
      );
      this.a();
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   @Override
   protected void onSettingChanged(Setting setting) {
      if (setting == this.enableSoundsSetting) {
         SoundManager.getInstance().setEnabled(this.enableSoundsSetting.getValue());
      } else if (setting == this.volumeSetting) {
         float f = (float)(this.volumeSetting.getValue() / 100.0);
         SoundManager.getInstance().setVolume(f);
      } else {
         for (Entry entry : this.Rb.entrySet()) {
            if (entry.getValue() == setting) {
               SoundManager.getInstance().f((SoundType)entry.getKey(), ((BooleanSetting)entry.getValue()).getValue());
               return;
            }
         }
      }
   }

   private void a() {
      SoundManager soundmanager = SoundManager.getInstance();
      soundmanager.setEnabled(this.enableSoundsSetting.getValue());
      soundmanager.setVolume((float)(this.volumeSetting.getValue() / 100.0));

      for (Entry entry : this.Rb.entrySet()) {
         soundmanager.f((SoundType)entry.getKey(), ((BooleanSetting)entry.getValue()).getValue());
      }
   }

   public BooleanSetting b() {
      return this.enableSoundsSetting;
   }

   public SliderSetting c() {
      return this.volumeSetting;
   }

   public BooleanSetting d() {
      return this.guiOpenSoundSetting;
   }

   public BooleanSetting e() {
      return this.guiCloseSoundSetting;
   }

   public BooleanSetting f() {
      return this.moduleEnableSoundSetting;
   }

   public BooleanSetting g() {
      return this.moduleDisableSoundSetting;
   }

   public BooleanSetting h() {
      return this.friendAddSoundSetting;
   }

   public BooleanSetting i() {
      return this.friendRemoveSoundSetting;
   }

   public BooleanSetting j() {
      return this.groupOpenSoundSetting;
   }

   public BooleanSetting k() {
      return this.favouriteAddSoundSetting;
   }

   public BooleanSetting l() {
      return this.categorySwitchSoundSetting;
   }

   public BooleanSetting m() {
      return this.toggleOnSoundSetting;
   }

   public BooleanSetting n() {
      return this.toggleOffSoundSetting;
   }

   public Map<SoundType, BooleanSetting> o() {
      return this.Rb;
   }
}
