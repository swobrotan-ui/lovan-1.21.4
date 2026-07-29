import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import render.ColorCache;

public final class ajs {
   private static final MinecraftClient of = MinecraftClient.getInstance();

   private ajs() {
   }

   public static void a(Matrix4f matrix4f, ItemStack itemstack, float f, float f1, float f2, float f3) {
      if (!(f3 < 0.001F)) {
         DrawContext drawcontext = new DrawContext(of, of.getBufferBuilders().getEntityVertexConsumers());
         drawcontext.getMatrices().peek().getPositionMatrix().set(matrix4f);
         float f4 = f2 / 16.0F;
         drawcontext.getMatrices().push();
         drawcontext.getMatrices().translate(f, f1, 0.0F);
         drawcontext.getMatrices().scale(f4, f4, 1.0F);
         if (f3 < 1.0F) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f3);
         }

         drawcontext.drawItem(itemstack, 0, 0);
         drawcontext.draw();
         if (f3 < 1.0F) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }

         drawcontext.getMatrices().pop();
      }
   }

   public static void b(Matrix4f matrix4f, float f, float f1, ItemStack itemstack, float f2, float f3, float f4) {
      int i = itemstack.getMaxDamage();
      int j = itemstack.getDamage();
      if (i != 0) {
         float f5 = (float)(i - j) / i;
         float f6 = f5 * f2;
         int k = ColorCache.d(0, 0, 0, f4);
         int l = c(f5, f4);
         Immediate immediate = of.getBufferBuilders().getEffectVertexConsumers();
         VertexConsumer vertexconsumer = immediate.getBuffer(RenderLayer.getGui());
         e(vertexconsumer, matrix4f, f, f1, f2, f3, k);
         if (f6 > 0.0F) {
            e(vertexconsumer, matrix4f, f, f1, f6, f3, l);
         }

         immediate.draw();
      }
   }

   public static int c(float f, float f1) {
      int i;
      int j;
      if (f > 0.5F) {
         float f2 = (f - 0.5F) * 2.0F;
         i = (int)((1.0F - f2) * 255.0F);
         j = 255;
      } else {
         float f3 = f * 2.0F;
         i = 255;
         j = (int)(f3 * 255.0F);
      }

      return ColorCache.d(i, j, 0, f1);
   }

   public static Color d(float f, float f1) {
      int i;
      int j;
      if (f > 0.5F) {
         float f2 = (f - 0.5F) * 2.0F;
         i = (int)((1.0F - f2) * 255.0F);
         j = 255;
      } else {
         float f3 = f * 2.0F;
         i = 255;
         j = (int)(f3 * 255.0F);
      }

      return ColorCache.c(i, j, 0, f1);
   }

   public static void e(VertexConsumer vertexconsumer, Matrix4f matrix4f, float f, float f1, float f2, float f3, int i) {
      float f4 = (i >> 24 & 0xFF) / 255.0F;
      float f5 = (i >> 16 & 0xFF) / 255.0F;
      float f6 = (i >> 8 & 0xFF) / 255.0F;
      float f7 = (i & 0xFF) / 255.0F;
      vertexconsumer.vertex(matrix4f, f, f1 + f3, 0.0F).color(f5, f6, f7, f4);
      vertexconsumer.vertex(matrix4f, f + f2, f1 + f3, 0.0F).color(f5, f6, f7, f4);
      vertexconsumer.vertex(matrix4f, f + f2, f1, 0.0F).color(f5, f6, f7, f4);
      vertexconsumer.vertex(matrix4f, f, f1, 0.0F).color(f5, f6, f7, f4);
   }
}
