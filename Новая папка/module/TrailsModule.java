package module;

import enum.Category;
import java.awt.Color;
import net.minecraft.util.math.Vec3d;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.SliderSetting;

public class TrailsModule extends ParticleModule {
   private ColorSetting colorSetting = new ColorSetting("Цвет", "Цвет частиц следа", new Color(120, 179, 255));
   private SliderSetting spawnRateSetting = new SliderSetting("Частота спавна", "Сколько частиц спавнить за тик", 5.0, 1.0, 15.0, 1.0);
   private SliderSetting sizeSetting = new SliderSetting("Размер", "Размер партиклов", 0.25, 0.05F, 0.5, 0.01F);
   private SliderSetting lifetimeSetting = new SliderSetting("Время жизни", "Время жизни партиклов в секундах", 1.0, 0.5, 3.0, 0.1F);
   private SliderSetting spreadSetting = new SliderSetting("Разброс", "Разброс частиц в стороны", 0.15F, 0.01F, 0.3F, 0.01F);
   private SliderSetting gravitySetting = new SliderSetting("Гравитация", "Сила гравитации для частиц", 0.01F, 0.0, 0.03F, 0.001F);
   private SliderSetting fadeSpeedSetting = new SliderSetting("Скорость исчезновения", "Контролирует скорость исчезновения частиц", 1.5, 0.3F, 2.0, 0.1F);
   private SliderSetting minSpeedSetting = new SliderSetting("Мин. скорость", "Минимальная скорость игрока для спавна", 0.05F, 0.0, 0.3F, 0.01F);
   private SliderSetting spawnHeightSetting = new SliderSetting("Высота спавна", "Смещение высоты спавна от игрока", 0.3F, 0.0, 2.0, 0.1F);
   private BooleanSetting colorAnimationSetting = new BooleanSetting("Анимация Цветов", "Радужная анимация цвета", false);
   private Vec3d fU = null;

   public TrailsModule() {
      super("Траилз", "Создает след из частиц за игроком", Category.PARTICLES);
      this.addSettings(
         this.colorSetting,
         this.spawnRateSetting,
         this.sizeSetting,
         this.lifetimeSetting,
         this.spreadSetting,
         this.gravitySetting,
         this.fadeSpeedSetting,
         this.minSpeedSetting,
         this.spawnHeightSetting,
         this.colorAnimationSetting
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.fU = null;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.fU = null;
   }

   @Override
   protected int b() {
      return 1500;
   }

   @Override
   protected int c() {
      return 150;
   }

   @Override
   protected long e() {
      return (long)(this.lifetimeSetting.getValue() * 1000.0);
   }

   @Override
   protected float f() {
      return this.sizeSetting.getFloatValue();
   }

   @Override
   protected int g() {
      return 3;
   }

   @Override
   protected float i() {
      return this.fadeSpeedSetting.getFloatValue();
   }

   @Override
   protected boolean j() {
      return this.colorAnimationSetting.getValue();
   }

   @Override
   protected Color k() {
      return this.colorSetting.getColor();
   }

   @Override
   protected void a() {
      if (this.getPlayer() != null) {
         Vec3d vec3d = this.getPlayer().getPos();
         if (this.fU != null) {
            double d0 = vec3d.x - this.fU.x;
            double d1 = vec3d.z - this.fU.z;
            if (Math.sqrt(d0 * d0 + d1 * d1) >= this.minSpeedSetting.getValue()) {
               mvy.b(
                  this,
                  vec3d,
                  (int)this.spawnRateSetting.getValue(),
                  this.spreadSetting.getFloatValue(),
                  this.gravitySetting.getFloatValue(),
                  this.spawnHeightSetting.getFloatValue()
               );
            }
         }

         this.fU = vec3d;
      }
   }
}
