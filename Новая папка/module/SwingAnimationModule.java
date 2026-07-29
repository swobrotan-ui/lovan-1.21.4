package module;

import core.ClientMain;
import enum.Category;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import setting.BooleanSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class SwingAnimationModule extends Module {
   public ListSetting modeSetting = new ListSetting(
      "Режим", "Mode selection", Arrays.<tv>stream(tv.values()).<String>map(tv::c).toList(), Collections.<String>singletonList(tv.Ew.c()), false
   );
   public SliderSetting strengthSetting = new SliderSetting("Сила", "сила взмаха", 1.0, 0.0, 15.0, 1.0, "", 1);
   public BooleanSetting onlyMainHandSetting = new BooleanSetting("Только в главной руке", "только в главной руке", true);
   public SliderSetting smoothStrengthSetting = new SliderSetting("Сила плавности", "скорость плавности", 2.0, 1.0, 2.5, 1.0);
   public BooleanSetting onlyWithAuraSetting = new BooleanSetting("Только с Аura", "работает только когда Аura включена", false);
   public float wd = 0.0F;

   public SwingAnimationModule() {
      super("ЗшингАниматион", "Меняет анимацию удара", Category.VISUAL);
      this.addSettings(this.modeSetting, this.strengthSetting, this.onlyMainHandSetting, this.smoothStrengthSetting, this.onlyWithAuraSetting);
   }

   public boolean a() {
      if (!this.isEnabled()) {
         return false;
      } else if (this.onlyWithAuraSetting.getValue()) {
         AuraModule auramodule = ClientMain.getInstance().getModuleManager().<AuraModule>getModule(AuraModule.class);
         return auramodule != null && auramodule.isEnabled();
      } else {
         return true;
      }
   }

   private tv b() {
      return tv.b(this.modeSetting.getFirst());
   }

   public void c(MatrixStack matrixstack, float f, Arm arm) {
      float f1 = this.smoothStrengthSetting.getFloatValue() / 100.0F;
      if (f < this.wd) {
         f1 = Math.min(f1 * 3.0F, 0.5F);
      }

      f = MathHelper.lerp(f1, this.wd, f);
      this.wd = f;
      int i = arm == Arm.RIGHT ? 1 : -1;
      float f2 = this.strengthSetting.getFloatValue() / 20.0F;
      tv tv = this.b();
      switch (tv) {
         case Ew:
            matrixstack.translate(0.56F * i, -0.52F, -0.72F);
            this.e(matrixstack, arm, f, f2);
            return;
         case gg:
            float f6 = (float)Math.sin(f * (Math.PI / 2) * 2.0);
            matrixstack.translate(0.55F * i, -0.5, -(0.7F + f6 * 0.002 * f2));
            matrixstack.scale(1.0F, 1.0F, f6 * f2 * 0.5F + 1.0F);
            matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F * f6 * f2));
            return;
         case ln:
            float f5 = (float)Math.sin(f * (Math.PI / 2) * 2.0);
            matrixstack.translate(i * 0.75F, -0.35F, -1.0F);
            matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 90));
            matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * -60));
            matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F - f2 * 50.0F * f5));
            return;
         case aqp:
            float f4 = MathHelper.sin(f * (float) Math.PI);
            matrixstack.translate(i * 0.56F, -0.32F, -0.72F);
            matrixstack.translate(0.0F, 0.0F, -1.5F * f4 / 5.0F * f2);
            matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(80.0F));
            matrixstack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(45.0F));
            matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-10.0F));
            matrixstack.translate(0.0F, 0.0F, -0.4F * f4 * f2);
            matrixstack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(f4 * -100.0F * f2));
            matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f4 * -180.0F * f2));
            matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-70.0F));
         default:
            return;
         case ahc:
            float f3 = (float)Math.sin(f * (Math.PI / 2) * 2.0);
            this.d(matrixstack, arm, 0.0F);
            matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 * i));
            matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-70 * i));
            matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((-100.0F - 60.0F * f3) * f2));
      }
   }

   private void d(MatrixStack matrixstack, Arm arm, float f) {
      int i = arm == Arm.RIGHT ? 1 : -1;
      matrixstack.translate(i * 0.56F, -0.52F + f * -0.6F, -0.72F);
   }

   private void e(MatrixStack matrixstack, Arm arm, float f, float f1) {
      int i = arm == Arm.RIGHT ? 1 : -1;
      float f2 = MathHelper.sin(f * f * (float) Math.PI);
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * (45.0F + f2 * -20.0F * f1)));
      float f3 = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
      matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * f3 * -20.0F * f1));
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f3 * -80.0F * f1));
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * -45.0F));
   }

   public float f(float f) {
      return !this.isEnabled() ? f : f * 0.5F;
   }

   @Override
   public void onEnable() {
      this.wd = 0.0F;
   }

   @Override
   public void onDisable() {
      this.wd = 0.0F;
   }
}
