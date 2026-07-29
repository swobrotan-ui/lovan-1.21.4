package module;

import enum.Category;
import setting.BooleanSetting;
import setting.SliderSetting;

public class CamTweaksModule extends Module {
   private BooleanSetting smoothTransitionSetting = new BooleanSetting("Плавный переход", "Включает плавную анимацию при смене камеры", true);
   private SliderSetting transitionSpeedSetting = new SliderSetting("Скорость перехода", "Скорость анимации перехода камеры", 0.1F, 0.1F, 3.0, 0.05F);
   private SliderSetting cameraDistanceSetting = new SliderSetting(
      "Дистанция камеры", "Максимальное отдаление камеры от персонажа", 4.0, -1.0, 10.0, 0.5, "", 1
   );
   private SliderSetting xOffsetSetting = new SliderSetting("X Смещение", "Смещение камеры по оси X (влево/вправо)", 0.0, -5.0, 5.0, 0.1F, "", 1);
   private SliderSetting yOffsetSetting = new SliderSetting("Y Смещение", "Смещение камеры по оси Y (вверх/вниз)", 0.0, -5.0, 5.0, 0.1F, "", 1);
   private BooleanSetting onlyF5Setting = new BooleanSetting("Только F5", "Применять задержку камеры только в режиме третьего лица", true);
   private SliderSetting xDelaySetting = new SliderSetting("X Задержка", "Задержка позиции по оси X (1.0 = без задержки)", 0.04F, 0.0, 1.0, 0.01F, "", 2);
   private SliderSetting yDelaySetting = new SliderSetting("Y Задержка", "Задержка позиции по оси Y (1.0 = без задержки)", 0.6F, 0.0, 1.0, 0.01F, "", 2);
   private SliderSetting zDelaySetting = new SliderSetting("Z Задержка", "Задержка позиции по оси Z (1.0 = без задержки)", 0.04F, 0.0, 1.0, 0.01F, "", 2);
   private SliderSetting rotationDelaySetting = new SliderSetting(
      "Задержка вращения", "Задержка вращения камеры (1.0 = без задержки)", 1.0, 0.0, 1.0, 0.01F, "", 2
   );
   private BooleanSetting throughWallsSetting = new BooleanSetting("Сквозь стены", "Позволяет камере проходить сквозь стены", true);
   private float aeQ = 0.0F;
   private float Hs = 0.0F;
   private float MB = 0.0F;
   private boolean eI = false;
   private double afs = 0.0;

   public CamTweaksModule() {
      super("СамТшеакз", "Позволяет смотреть от ф5 через стены и менять анимацию", Category.VISUAL);
      this.addSettings(
         this.smoothTransitionSetting,
         this.transitionSpeedSetting,
         this.cameraDistanceSetting,
         this.xOffsetSetting,
         this.yOffsetSetting,
         this.throughWallsSetting,
         this.onlyF5Setting,
         this.xDelaySetting,
         this.yDelaySetting,
         this.zDelaySetting,
         this.rotationDelaySetting
      );
   }

   @Override
   public void onEnable() {
      this.aeQ = 0.0F;
      this.Hs = 0.0F;
      this.MB = 0.0F;
      this.afs = 0.0;
      this.eI = false;
   }

   @Override
   public void onDisable() {
      this.aeQ = 0.0F;
      this.Hs = 0.0F;
      this.MB = 0.0F;
      this.afs = 0.0;
   }

   @Override
   public void onEndTick() {
      if (this.getOptions() != null) {
         boolean flag = !this.getOptions().getPerspective().isFirstPerson();
         this.Hs = this.aeQ;
         if (flag) {
            this.MB = this.cameraDistanceSetting.getFloatValue();
         } else {
            this.MB = 0.0F;
         }

         if (this.smoothTransitionSetting.getValue()) {
            float f = this.MB - this.aeQ;
            double d0 = f / (this.transitionSpeedSetting.getFloatValue() * 10.0F);
            this.afs = this.afs + (d0 - this.afs) * 0.2;
            if (Math.abs(f) < 0.5F) {
               this.afs *= 0.7;
            }

            double d1 = (Math.random() - 0.5) * 0.002;
            this.aeQ = this.aeQ + (float)(this.afs + d1);
            this.afs *= 0.92;
            float f1 = -1.0F;
            float f2 = 10.0F;
            if (this.aeQ < f1) {
               this.aeQ = f1;
            }

            if (this.aeQ > f2) {
               this.aeQ = f2;
            }

            if (flag != this.eI) {
               this.eI = flag;
            }
         } else {
            this.aeQ = this.MB;
            this.Hs = this.MB;
            this.afs = 0.0;
            this.eI = flag;
         }
      }
   }

   public float a(float f) {
      return !this.smoothTransitionSetting.getValue() ? this.aeQ : this.Hs + (this.aeQ - this.Hs) * f;
   }

   public SliderSetting b() {
      return this.xOffsetSetting;
   }

   public SliderSetting c() {
      return this.yOffsetSetting;
   }

   public BooleanSetting d() {
      return this.onlyF5Setting;
   }

   public SliderSetting e() {
      return this.xDelaySetting;
   }

   public SliderSetting f() {
      return this.yDelaySetting;
   }

   public SliderSetting g() {
      return this.zDelaySetting;
   }

   public SliderSetting h() {
      return this.rotationDelaySetting;
   }

   public BooleanSetting i() {
      return this.throughWallsSetting;
   }

   public float j() {
      return this.aeQ;
   }
}
