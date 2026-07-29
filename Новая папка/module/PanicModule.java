package module;

import core.ClientMain;
import core.KeyBindManager;
import enum.Category;
import setting.ActionKeySetting;
import setting.BooleanSetting;
import setting.FilePickerSetting;

public class PanicModule extends Module {
   private boolean anh = false;
   ClientMain NH = ClientMain.getInstance();
   private BooleanSetting clearLoaderSetting = new BooleanSetting("Очистить лоадер", "Очистить лоадер при панике", false);
   private FilePickerSetting Zd = new FilePickerSetting("Путь к лоадеру", "Путь к лоадеру");
   private BooleanSetting clearTracesSetting = new BooleanSetting("Очищать", "Очищать следы при панике", false);
   private BooleanSetting clearRecentSetting = new BooleanSetting("Очищать рецент", "Очищать недавние файлы", true);
   private BooleanSetting clearRegistrySetting = new BooleanSetting("Очищать реестр", "Очищать следы в реестре", true);
   private BooleanSetting clearTempSetting = new BooleanSetting("Очищать темп", "Очищать временные файлы", true);
   private ActionKeySetting restoreModulesKeySetting = new ActionKeySetting(
      "Восстановление модулей", "Бинд для восстановления модулей после паники", -1, this::b
   );
   private boolean P = false;

   public PanicModule() {
      super("Панис", "Отключает все функции до перезапуска", Category.CLIENT);
      this.addSettings(
         this.restoreModulesKeySetting,
         this.clearLoaderSetting,
         this.Zd,
         this.clearTracesSetting,
         this.clearRecentSetting,
         this.clearRegistrySetting,
         this.clearTempSetting
      );
      this.U(this.clearTracesSetting.getName(), this.clearRecentSetting.getName(), true);
      this.U(this.clearTracesSetting.getName(), this.clearRegistrySetting.getName(), true);
      this.U(this.clearTracesSetting.getName(), this.clearTempSetting.getName(), true);
      this.U(this.clearLoaderSetting.getName(), this.Zd.getName(), true);
      KeyBindManager.getInstance().d(this.restoreModulesKeySetting);
   }

   @Override
   public void onEnable() {
      this.anh = true;
      if (this.clearTracesSetting.getValue()) {
         if (this.clearRecentSetting.getValue()) {
            aa.a();
         }

         if (this.clearRegistrySetting.getValue()) {
            hq.a();
         }

         if (this.clearTempSetting.getValue()) {
            wc.a();
         }
      }

      if (this.clearLoaderSetting.getValue() && this.Zd.hasValue()) {
         String s = this.Zd.getValue();
         this.P = lrv.a(s);
         fa.a(s);
         if (this.P) {
            lrv.c(s);
         }
      }

      try {
         this.NH.getConfigSyncManager().g();
      } catch (Exception exception) {
      } finally {
         this.NH.getConfigSyncManager().a();
      }

      this.getClient().execute(() -> {
         this.getClient().setScreen(null);
      });
      new Thread(this::a, "L").start();
      System.gc();
   }

   @Override
   public void onDisable() {
   }

   private void a() {
      try {
         Thread.sleep(1000L);
         return;
      } catch (Exception exception) {
      } finally {
         this.NH.getModuleManager().clearModules();
      }
   }

   private void b() {
      if (this.anh) {
         new Thread(() -> {
            try {
               if (this.P) {
                  ago.a();
                  this.P = false;
               }

               this.NH.getConfigSyncManager().b();
               this.NH.modulesLoaded = false;
               this.NH.getModuleManager().init();
               this.anh = false;
            } catch (Exception exception) {
            }
         }, "OC").start();
      }
   }

   public boolean c() {
      return this.anh;
   }
}
