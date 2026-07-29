import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.RectangleCache;

public class wa {
   public final qh dc = new qh();
   private Float nv = null;
   private long ajG = System.nanoTime();
   private MinecraftClient FS = MinecraftClient.getInstance();

   public void a(DrawContext drawcontext, RenderTickCounter rendertickcounter, PlayerEntity playerentity, ItemStack itemstack, Arm arm, int i, int j) {
      Matrix4f matrix4f = drawcontext.getMatrices().peek().getPositionMatrix();
      int k = playerentity.getInventory().selectedSlot;
      this.dc.b(k);
      this.dc.c();
      float f = 19.0F;
      float f1 = 2.0F;
      float f2 = 6.0F;
      float f3 = j - 23;
      float f4 = 2.0F;
      float f5 = 9.0F * f + 8.0F * f1;
      float f6 = f5 + f4 * 2.0F;
      float f7 = f + f4 * 2.0F;
      float f8 = i - f6 / 2.0F;
      BuiltRectangle builtrectangle = RectangleCache.b(f6, f7, f2);
      builtrectangle.a(matrix4f, f8, f3 - f4, 0.9F);
      float f9 = this.dc.d();
      float f10 = i - f5 / 2.0F + f9 * (f + f1);
      BuiltRectangle builtrectangle1 = RectangleCache.b(f, f, f2 - 1.0F);
      builtrectangle1.a(matrix4f, f10, f3, 0.95F);

      for (int l = 0; l < 9; l++) {
         float f11 = i - f5 / 2.0F + l * (f + f1);
         float f12 = Math.abs(l - f9);
         float f13 = 0.3F;
         if (f12 < 1.5F) {
            f13 = 0.3F + (1.5F - f12) * 0.15F;
         }

         BuiltRectangle builtrectangle2 = RectangleCache.b(f, f, f2 - 2.0F);
         builtrectangle2.a(matrix4f, f11, f3, f13);
         ItemStack itemstack1 = (ItemStack)playerentity.getInventory().main.get(l);
         if (!itemstack1.isEmpty()) {
            float f14 = 0.8F;
            float f15 = 16.0F * f14;
            float f16 = f11 + (f - f15) / 2.0F - 0.5F;
            float f17 = f3 + (f - f15) / 2.0F;
            drawcontext.getMatrices().push();
            drawcontext.getMatrices().translate(f16, f17, 0.0F);
            drawcontext.getMatrices().scale(f14, f14, 1.0F);
            drawcontext.drawItem(playerentity, itemstack1, 0, 0, l + 1);
            drawcontext.drawStackOverlay(this.FS.textRenderer, itemstack1, 0, 0);
            drawcontext.getMatrices().pop();
         }
      }

      boolean flag = !itemstack.isEmpty();
      if (this.nv == null) {
         this.nv = flag ? 1.0F : 0.0F;
      }

      float f18 = flag ? 1.0F : 0.0F;
      float f19 = 20.0F;
      float f20 = (float)(System.nanoTime() - this.ajG) / 1.0E9F;
      this.ajG = System.nanoTime();
      if (Math.abs(f18 - this.nv) > 0.01F) {
         this.nv = this.nv + (f18 - this.nv) * f20 * f19;
         this.nv = Math.max(0.0F, Math.min(1.0F, this.nv));
      }

      if (this.nv > 0.01F) {
         float f21;
         if (arm == Arm.LEFT) {
            f21 = f8 - f - 5.0F;
         } else {
            f21 = f8 + f6 + 5.0F;
         }

         BuiltRectangle builtrectangle3 = RectangleCache.b(f, f, f2 - 2.0F);
         builtrectangle3.a(matrix4f, f21, f3, 0.8F * this.nv);
         if (flag) {
            float f22 = 0.8F;
            float f23 = 16.0F * f22;
            float f24 = f21 + (f - f23) / 2.0F - 0.5F;
            float f25 = f3 + (f - f23) / 2.0F;
            drawcontext.getMatrices().push();
            drawcontext.getMatrices().translate(f24, f25, 0.0F);
            drawcontext.getMatrices().scale(f22, f22, 1.0F);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.nv);
            drawcontext.drawItem(playerentity, itemstack, 0, 0, 10);
            drawcontext.drawStackOverlay(this.FS.textRenderer, itemstack, 0, 0);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawcontext.getMatrices().pop();
         }
      }
   }
}
