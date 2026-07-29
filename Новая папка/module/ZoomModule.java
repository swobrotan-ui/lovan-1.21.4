package module;

import enum.Category;
import event.MouseMoveEvent;
import setting.ActionKeySetting;
import setting.BooleanSetting;
import setting.Setting;
import setting.SliderSetting;

public class ZoomModule extends Module {
   private static float MIN_FOV = 0.0F;
   private static float MAX_FOV = 120.0F;
   private ActionKeySetting zoomKeySetting = new ActionKeySetting("Клавиша зума", "Зажмите для приближения", 67);
   private SliderSetting zoomFovSetting = new SliderSetting("Зум FOV", "Уровень приближения (меньше = ближе)", 30.0, MIN_FOV, MAX_FOV, 1.0);
   private SliderSetting speedSetting = new SliderSetting("Скорость", "Скорость анимации", 4.0, 1.0, 15.0, 0.5);
   private BooleanSetting scrollControlSetting = new BooleanSetting("Скролл управление", "Колёсико мыши меняет уровень зума", true);
   private SliderSetting scrollStepSetting = new SliderSetting("Шаг скролла", "Насколько меняется FOV за скролл", 5.0, 1.0, 20.0, 1.0);
   private long lastZoomTime = 0L;
   private float currentFov = 0.0F;
   private volatile float targetFov;
   private float originalFov;
   private static ZoomModule instance;

   public ZoomModule() {
      super("Zоом", "Плавное приближение камеры (зажатие клавиши)", Category.VISUAL);
      this.addSettings(this.zoomKeySetting, this.zoomFovSetting, this.speedSetting, this.scrollControlSetting, this.scrollStepSetting);
      instance = this;
   }

   @Override
   public void onEnable() {
      this.lastZoomTime = System.nanoTime();
      this.currentFov = 0.0F;
      float f = Math.max(MIN_FOV, this.zoomFovSetting.getFloatValue());
      this.targetFov = f;
      this.originalFov = f;
   }

   @Override
   public void onDisable() {
      this.currentFov = 0.0F;
   }

   @Override
   public void onMouseScroll(MouseMoveEvent mousemoveevent) {
      if (this.scrollControlSetting.getValue() && this.a()) {
         float f = this.scrollStepSetting.getFloatValue();
         float f1 = this.targetFov - (float)mousemoveevent.getYOffset() * f;
         this.targetFov = Math.max(MIN_FOV, Math.min(MAX_FOV, f1));
         mousemoveevent.setCancelled(true);
      }
   }

   private boolean a() {
      return this.getClient() != null && this.getClient().currentScreen == null && this.zoomKeySetting.isPressed();
   }

   public float b() {
      if (!this.isEnabled()) {
         return 1.0F;
      } else {
         this.c();
         if (this.currentFov <= 0.0F) {
            return 1.0F;
         } else {
            float f = Math.max(MIN_FOV, this.originalFov);
            float f1 = f / 70.0F;
            if (this.currentFov >= 1.0F) {
               return f1;
            } else {
               float f2 = this.d(this.currentFov);
               return 1.0F + (f1 - 1.0F) * f2;
            }
         }
      }
   }

   private void c() {
      long i = System.nanoTime();
      if (this.lastZoomTime != 0L && i - this.lastZoomTime <= NANOS_PER_SECOND) {
         float f = (float)(i - this.lastZoomTime) / (float)NANOS_PER_SECOND;
         this.lastZoomTime = i;
         float f1 = this.speedSetting.getFloatValue();
         boolean flag = this.a();
         if (flag) {
            this.currentFov = Math.min(1.0F, this.currentFov + f * f1);
         } else {
            this.currentFov = Math.max(0.0F, this.currentFov - f * f1);
         }

         float f2 = this.scrollControlSetting.getValue() ? this.targetFov : this.zoomFovSetting.getFloatValue();
         float f3 = f2 - this.originalFov;
         if (Math.abs(f3) > 0.01F) {
            this.originalFov += f3 * f * f1 * 2.0F;
         } else {
            this.originalFov = f2;
         }

         this.originalFov = Math.max(MIN_FOV, Math.min(MAX_FOV, this.originalFov));
      } else {
         this.lastZoomTime = i;
      }
   }

   private float d(float f) {
      return f < 0.5F ? 4.0F * f * f * f : 1.0F - (float)Math.pow(-2.0F * f + 2.0F, 3.0) / 2.0F;
   }

   public boolean e() {
      return this.isEnabled() && this.currentFov > 0.001F;
   }

   @Override
   protected void onSettingChanged(Setting setting) {
      if (setting == this.zoomFovSetting && !this.scrollControlSetting.getValue()) {
         float f = Math.max(MIN_FOV, this.zoomFovSetting.getFloatValue());
         this.targetFov = f;
         this.originalFov = f;
      }
   }

   public ActionKeySetting f() {
      return this.zoomKeySetting;
   }

   public SliderSetting g() {
      return this.zoomFovSetting;
   }

   public SliderSetting h() {
      return this.speedSetting;
   }

   public BooleanSetting i() {
      return this.scrollControlSetting;
   }

   public SliderSetting j() {
      return this.scrollStepSetting;
   }

   public long k() {
      return this.lastZoomTime;
   }

   public float l() {
      return this.currentFov;
   }

   public float m() {
      return this.targetFov;
   }

   public float n() {
      return this.originalFov;
   }

   public static ZoomModule o() {
      return instance;
   }
}
