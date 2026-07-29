package module;

import enum.Category;
import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.SliderSetting;

public class HitParticlesModule extends ParticleModule {
   private ColorSetting colorSetting = new ColorSetting("Цвет", "Цвет частиц", new Color(255, 100, 100));
   private SliderSetting countSetting = new SliderSetting("Количество", "Количество партиклов", 30.0, 3.0, 30.0, 1.0);
   private SliderSetting sizeSetting = new SliderSetting("Размер", "Размер партиклов", 0.25, 0.05F, 0.5, 0.01F);
   private SliderSetting lifetimeSetting = new SliderSetting("Время жизни", "Время жизни партиклов", 0.5, 0.5, 3.0, 0.1F);
   private SliderSetting spreadSetting = new SliderSetting("Разброс", "Чем больше, тем сильнее разброс", 0.15F, 0.02F, 0.15F, 0.01F);
   private SliderSetting gravitySetting = new SliderSetting("Гравитация", "Сила гравитации", 0.012F, 0.005F, 0.03F, 0.001F);
   private SliderSetting fadeSpeedSetting = new SliderSetting("Скорость исчезновения", "Контролирует скорость исчезновения частиц", 1.0, 0.3F, 2.0, 0.1F);
   private BooleanSetting colorAnimationSetting = new BooleanSetting("Анимация Цветов", "Радужная анимация", false);

   public HitParticlesModule() {
      super("ХитПартислез", "Создает эффектные частицы при ударе", Category.PARTICLES);
      this.addSettings(
         this.colorSetting,
         this.countSetting,
         this.sizeSetting,
         this.lifetimeSetting,
         this.spreadSetting,
         this.gravitySetting,
         this.fadeSpeedSetting,
         this.colorAnimationSetting
      );
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
   public void onAttackEntity(PlayerEntity playerentity, World world, Hand hand, Entity entity, EntityHitResult entityhitresult) {
      if (entity != null && !this.isNotInWorld() && playerentity == this.getPlayer() && entity.isAlive()) {
         this.a(entity.getPos().add(0.0, entity.getHeight() / 2.0, 0.0));
      }
   }

   @Override
   protected void a() {
      if (this.getPlayer().hurtTime == 10) {
         this.a(this.getPlayer().getPos().add(0.0, this.getPlayer().getHeight() / 2.0, 0.0));
      }
   }

   private void a(Vec3d vec3d) {
      mvy.a(this, vec3d, (int)this.countSetting.getValue(), this.spreadSetting.getFloatValue(), this.gravitySetting.getFloatValue());
   }
}
