package module;

import com.mojang.blaze3d.systems.RenderSystem;
import enum.Category;
import java.awt.Color;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import setting.ColorSetting;

public class WorldTintModule extends Module {
   private final ColorSetting colorSetting = new ColorSetting("Цвет", "цвет мира", Color.WHITE);

   public WorldTintModule() {
      super("ШорлдТинт", "Меняет цвет мира", Category.VISUAL);
      this.addSetting(this.colorSetting);
   }

   @Override
   public void onRenderAfterSetup(WorldRenderContext worldrendercontext) {
      if (this.isEnabled()) {
         this.a();
      }
   }

   @Override
   public void onRenderEnd(WorldRenderContext worldrendercontext) {
      if (this.isEnabled()) {
         this.b();
      }
   }

   private void a() {
      Color color = this.colorSetting.getColor();
      float f = color.getRed() / 255.0F;
      float f1 = color.getGreen() / 255.0F;
      float f2 = color.getBlue() / 255.0F;
      float f3 = Math.max(f, Math.max(f1, f2));
      float f4 = Math.min(f, Math.min(f1, f2));
      float f5 = f3 == 0.0F ? 0.0F : (f3 - f4) / f3;
      float f6 = 1.0F - f5 + f5 * f;
      float f7 = 1.0F - f5 + f5 * f1;
      float f8 = 1.0F - f5 + f5 * f2;
      RenderSystem.setShaderColor(f6, f7, f8, 1.0F);
   }

   private void b() {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }
}
