import com.mojang.blaze3d.systems.RenderSystem;
import gui.GuiConstants;
import java.awt.Color;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import render.BuiltRectangle;
import render.BuiltText;
import setting.BlockEntry;

class dg {
   private final Block Ty;
   private final BlockEntry rv;
   private final float azQ;
   private final em ayD;
   private final zl HY;
   private final BuiltRectangle If;
   // $VF: synthetic field
   final hc sG;

   public dg(hc hc, Block block, BlockEntry blockentry, float f) {
      this.sG = hc;
      this.Ty = block;
      this.rv = blockentry;
      this.azQ = f;
      this.ayD = new em(0.0F, 0.0F);
      this.ayD.a(blockentry.isEnabled());
      this.ayD.c(() -> {
         this.sG.jH.setEnabled(block, this.ayD.d());
      });
      this.HY = new zl(0.0F, 0.0F, blockentry.getColor());
      this.HY.a(() -> {
         this.e(block);
      });
      this.If = new br().a(20.0F, 20.0F).b(4.0F).a();
   }

   public void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      ItemStack itemstack = new ItemStack(this.Ty.asItem());
      if (!itemstack.isEmpty()) {
         this.b(matrix4f, itemstack, f + 5.0F, f1 + 3.0F, f3);
      }

      Identifier identifier = Registries.BLOCK.getId(this.Ty);
      String s = identifier.getPath().replace("_", " ");
      BuiltText builttext = hc.x(this.sG, hc.w(this.sG), s, 13.0F, GuiConstants.Bz);
      builttext.a(matrix4f, f + 30.0F, f1 + 4.0F, f3);
      float f4 = f + 286.0F - 40.0F;
      float f5 = f4 - 20.0F - 5.0F;
      float f6 = f5 - 30.0F - 5.0F;
      this.ayD.render(matrix4f, f6 - 10.0F, f1, i, j, f2, f3);
      this.HY.render(matrix4f, f5, f1, i, j, f2, f3);
      this.If.a(matrix4f, f4, f1, f3);
      BuiltText builttext1 = hc.z(this.sG, hc.y(this.sG), "F", 12.0F, GuiConstants.Bz);
      builttext1.a(matrix4f, f4 + 3.5F, f1 + 3.5F, f3);
   }

   private void b(Matrix4f matrix4f, ItemStack itemstack, float f, float f1, float f2) {
      if (!(f2 < 0.001F)) {
         MatrixStack matrixstack = new MatrixStack();
         matrixstack.peek().getPositionMatrix().set(matrix4f);
         matrixstack.translate(f + 10.0F, f1 + 9.0F, 0.0F);
         float f3 = 20.0F;
         matrixstack.scale(f3, -f3, 0.01F);
         GL11.glClear(256);
         if (f2 < 1.0F) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f2);
         }

         MinecraftClient minecraftclient = MinecraftClient.getInstance();
         Immediate immediate = minecraftclient.getBufferBuilders().getEntityVertexConsumers();
         minecraftclient.getItemRenderer()
            .renderItem(itemstack, ModelTransformationMode.GUI, 15728880, OverlayTexture.DEFAULT_UV, matrixstack, immediate, minecraftclient.world, 0);
         immediate.draw();
         if (f2 < 1.0F) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   public boolean c(double d0, double d1, int i, float f, float f1) {
      float f2 = f + 286.0F - 40.0F;
      float f3 = f2 - 20.0F - 5.0F;
      float f4 = f3 - 30.0F - 5.0F;
      float f5 = f4 - 10.0F;
      if (d0 >= f5 && d0 <= f5 + this.ayD.getWidth() && d1 >= f1 && d1 <= f1 + this.ayD.getHeight()) {
         this.ayD.it = f5;
         this.ayD.atW = f1;
         return this.ayD.mouseClicked(d0, d1, i);
      } else if (d0 >= f3 && d0 <= f3 + this.HY.getWidth() && d1 >= f1 && d1 <= f1 + this.HY.getHeight()) {
         this.HY.it = f3;
         this.HY.atW = f1;
         return this.HY.mouseClicked(d0, d1, i);
      } else if (d0 >= f2 && d0 <= f2 + 20.0F && d1 >= f1 && d1 <= f1 + 20.0F) {
         this.sG.jH.removeBlock(this.Ty);
         this.sG.b();
         return true;
      } else {
         return false;
      }
   }

   public void d(double d0, double d1, int i) {
      this.ayD.mouseReleased(d0, d1, i);
      this.HY.mouseReleased(d0, d1, i);
   }

   private void e(Block block) {
      if (this.sG.Bl == null) {
         float f = this.sG.it + 143.0F - 143.0F;
         float f1 = this.sG.atW + 100.5F - 100.0F;
         this.sG.Bl = new oip(f, f1, "Цвет блока", this.rv.getColor().getAlpha() < 255, this.sG::u);
         this.sG.Bl.c(this.rv.getColor());
         this.sG.Bl.p(() -> {
            Color color = this.sG.Bl.d();
            this.sG.jH.setColor(block, color);
            this.HY.a(color);
         });
      }
   }
}
