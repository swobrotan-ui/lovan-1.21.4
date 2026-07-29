package module;

import enum.Category;
import setting.ActionSetting;
import setting.BooleanSetting;
import setting.SliderSetting;

public class ViewModelModule extends Module {
   private SliderSetting xSetting = new SliderSetting("X", "Позиция руки по X", 0.0, -2.0, 2.0, 0.01);
   private SliderSetting ySetting = new SliderSetting("Y", "Позиция руки по Y", 0.0, -2.0, 2.0, 0.01);
   private SliderSetting zSetting = new SliderSetting("Z", "Позиция руки по Z", 0.0, -2.0, 2.0, 0.01);
   private SliderSetting sizeSetting = new SliderSetting("Размер", "Размер руки", 1.0, 0.1, 3.0, 0.01);
   private SliderSetting rotXSetting = new SliderSetting("Пов. X", "Поворот руки по X", 0.0, -180.0, 180.0, 1.0);
   private SliderSetting rotYSetting = new SliderSetting("Пов. Y", "Поворот руки по Y", 0.0, -180.0, 180.0, 1.0);
   private SliderSetting rotZSetting = new SliderSetting("Пов. Z", "Поворот руки по Z", 0.0, -180.0, 180.0, 1.0);
   private SliderSetting leftXSetting = new SliderSetting("Л. X", "Позиция левой руки по X", 0.0, -2.0, 2.0, 0.01);
   private SliderSetting leftYSetting = new SliderSetting("Л. Y", "Позиция левой руки по Y", 0.0, -2.0, 2.0, 0.01);
   private SliderSetting leftZSetting = new SliderSetting("Л. Z", "Позиция левой руки по Z", 0.0, -2.0, 2.0, 0.01);
   private SliderSetting leftSizeSetting = new SliderSetting("Л. Размер", "Размер левой руки", 1.0, 0.1, 3.0, 0.01);
   private SliderSetting leftRotXSetting = new SliderSetting("Л. Пов. X", "Поворот левой руки по X", 0.0, -180.0, 180.0, 1.0);
   private SliderSetting leftRotYSetting = new SliderSetting("Л. Пов. Y", "Поворот левой руки по Y", 0.0, -180.0, 180.0, 1.0);
   private SliderSetting leftRotZSetting = new SliderSetting("Л. Пов. Z", "Поворот левой руки по Z", 0.0, -180.0, 180.0, 1.0);
   private BooleanSetting mirrorSetting = new BooleanSetting("Зеркало", "Левая рука зеркалит правую", true);
   private ActionSetting ahT = new ActionSetting("Сброс", "Вернуть все настройки по умолчанию");

   public ViewModelModule() {
      super("ВиешМодел", "Позволяет изменять позицию, размер и поворот рук", Category.VISUAL);
      this.ahT.setCallback(this::a);
      this.addSettings(
         this.mirrorSetting,
         this.xSetting,
         this.ySetting,
         this.zSetting,
         this.sizeSetting,
         this.rotXSetting,
         this.rotYSetting,
         this.rotZSetting,
         this.leftXSetting,
         this.leftYSetting,
         this.leftZSetting,
         this.leftSizeSetting,
         this.leftRotXSetting,
         this.leftRotYSetting,
         this.leftRotZSetting,
         this.ahT
      );
      this.leftXSetting.setVisibilitySupplier(() -> {
         return !this.mirrorSetting.getValue();
      });
      this.leftYSetting.setVisibilitySupplier(() -> {
         return !this.mirrorSetting.getValue();
      });
      this.leftZSetting.setVisibilitySupplier(() -> {
         return !this.mirrorSetting.getValue();
      });
      this.leftSizeSetting.setVisibilitySupplier(() -> {
         return !this.mirrorSetting.getValue();
      });
      this.leftRotXSetting.setVisibilitySupplier(() -> {
         return !this.mirrorSetting.getValue();
      });
      this.leftRotYSetting.setVisibilitySupplier(() -> {
         return !this.mirrorSetting.getValue();
      });
      this.leftRotZSetting.setVisibilitySupplier(() -> {
         return !this.mirrorSetting.getValue();
      });
   }

   private void a() {
      this.xSetting.setValue(0.0);
      this.ySetting.setValue(0.0);
      this.zSetting.setValue(0.0);
      this.sizeSetting.setValue(1.0);
      this.rotXSetting.setValue(0.0);
      this.rotYSetting.setValue(0.0);
      this.rotZSetting.setValue(0.0);
      this.leftXSetting.setValue(0.0);
      this.leftYSetting.setValue(0.0);
      this.leftZSetting.setValue(0.0);
      this.leftSizeSetting.setValue(1.0);
      this.leftRotXSetting.setValue(0.0);
      this.leftRotYSetting.setValue(0.0);
      this.leftRotZSetting.setValue(0.0);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public SliderSetting b() {
      return this.xSetting;
   }

   public SliderSetting c() {
      return this.ySetting;
   }

   public SliderSetting d() {
      return this.zSetting;
   }

   public SliderSetting e() {
      return this.sizeSetting;
   }

   public SliderSetting f() {
      return this.rotXSetting;
   }

   public SliderSetting g() {
      return this.rotYSetting;
   }

   public SliderSetting h() {
      return this.rotZSetting;
   }

   public SliderSetting i() {
      return this.leftXSetting;
   }

   public SliderSetting j() {
      return this.leftYSetting;
   }

   public SliderSetting k() {
      return this.leftZSetting;
   }

   public SliderSetting l() {
      return this.leftSizeSetting;
   }

   public SliderSetting m() {
      return this.leftRotXSetting;
   }

   public SliderSetting n() {
      return this.leftRotYSetting;
   }

   public SliderSetting o() {
      return this.leftRotZSetting;
   }

   public BooleanSetting p() {
      return this.mirrorSetting;
   }

   public ActionSetting q() {
      return this.ahT;
   }
}
