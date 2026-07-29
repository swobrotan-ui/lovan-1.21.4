import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;

public final class ml {
   public static boolean a(rg rg, World world, PlayerEntity playerentity, double d0, double d1, double d2) {
      double d3 = d0 - rg.VS.x;
      double d4 = d1 - rg.VS.y;
      double d5 = d2 - rg.VS.z;
      double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
      if (d6 > 0.0) {
         d3 /= d6;
         d4 /= d6;
         d5 /= d6;
      }

      if (d3 * rg.avF + d4 * rg.ajW + d5 * rg.auw < rg.ayT) {
         return false;
      } else {
         Vec3d vec3d = new Vec3d(d0, d1, d2);
         BlockHitResult blockhitresult = world.raycast(new RaycastContext(rg.VS, vec3d, ShapeType.COLLIDER, FluidHandling.NONE, playerentity));
         if (blockhitresult.getType() == Type.BLOCK) {
            double d7 = rg.VS.squaredDistanceTo(blockhitresult.getPos());
            double d8 = (d0 - rg.VS.x) * (d0 - rg.VS.x) + (d1 - rg.VS.y) * (d1 - rg.VS.y) + (d2 - rg.VS.z) * (d2 - rg.VS.z);
            return d7 >= d8;
         } else {
            return true;
         }
      }
   }

   public static void b(MatrixStack matrixstack, rg rg, double d0, double d1, double d2) {
      double d3 = d0 - rg.VS.x;
      double d4 = d1 - rg.VS.y;
      double d5 = d2 - rg.VS.z;
      matrixstack.push();
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rg.AZ));
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rg.Sd + 180.0F));
      matrixstack.translate(d3, d4, d5);
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rg.Sd));
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rg.AZ));
   }

   public static void c(MatrixStack matrixstack, rg rg, double d0, double d1, double d2, double d3, double d4, double d5, float f) {
      b(matrixstack, rg, d0, d1, d2);
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotation((float)d3));
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotation((float)d4));
      matrixstack.multiply(RotationAxis.POSITIVE_Z.rotation((float)d5));
      matrixstack.scale(f, f, f);
   }

   public static void d(Identifier identifier, MinecraftClient minecraftclient) {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      RenderSystem.enableDepthTest();
      RenderSystem.disableCull();
      RenderSystem.depthMask(false);
      AbstractTexture abstracttexture = minecraftclient.getTextureManager().getTexture(identifier);
      RenderSystem.setShaderTexture(0, abstracttexture.getGlId());
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
   }

   public static void e() {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      RenderSystem.enableDepthTest();
      RenderSystem.disableCull();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
   }

   public static void f() {
      RenderSystem.depthMask(true);
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
   }

   public static void g(BufferBuilder bufferbuilder, MatrixStack matrixstack, float f, float f1, float f2, float f3, float f4) {
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      float f5 = f / 2.0F;
      bufferbuilder.vertex(matrix4f, -f5, f5, 0.0F).texture(0.0F, 1.0F).color(f1, f2, f3, f4);
      bufferbuilder.vertex(matrix4f, f5, f5, 0.0F).texture(1.0F, 1.0F).color(f1, f2, f3, f4);
      bufferbuilder.vertex(matrix4f, f5, -f5, 0.0F).texture(1.0F, 0.0F).color(f1, f2, f3, f4);
      bufferbuilder.vertex(matrix4f, -f5, -f5, 0.0F).texture(0.0F, 0.0F).color(f1, f2, f3, f4);
   }

   public static int h(Color color, float f) {
      return dr.x(color, f);
   }

   public static float[] i(int i) {
      return new float[]{(i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, (i >> 24 & 0xFF) / 255.0F};
   }

   public static float[] j(Color color, float f) {
      return new float[]{color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, Math.max(0.0F, Math.min(1.0F, f))};
   }

   public static int k(int i, float f) {
      int j = i >> 24 & 0xFF;
      return i & 16777215 | Math.round(j * f) << 24;
   }

   public static int l(float f, long i) {
      float f1 = ((float)(System.currentTimeMillis() % i) / (float)i + f) % 1.0F;
      return Color.HSBtoRGB(f1, 0.8F, 1.0F) | 0xFF000000;
   }

   public static float m(float f, float f1, float f2) {
      if (f < f1) {
         return f / f1;
      } else {
         return f > f2 ? 1.0F - (f - f2) / (1.0F - f2) : 1.0F;
      }
   }

   public static float n(float f) {
      float f1 = f - 1.0F;
      return f1 * f1 * f1 + 1.0F;
   }

   public static float o(float f) {
      return f < 0.5F ? 4.0F * f * f * f : 1.0F - (float)Math.pow(-2.0F * f + 2.0F, 3.0) / 2.0F;
   }
}
