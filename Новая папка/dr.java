import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Set;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class dr {
   private static final Set<Item> agX = Set.<Item>of(
      Items.DIAMOND,
      Items.EMERALD,
      Items.GOLD_INGOT,
      Items.IRON_INGOT,
      Items.NETHERITE_INGOT,
      Items.DIAMOND_SWORD,
      Items.DIAMOND_PICKAXE,
      Items.DIAMOND_AXE,
      Items.DIAMOND_SHOVEL,
      Items.DIAMOND_HOE,
      Items.DIAMOND_HELMET,
      Items.DIAMOND_CHESTPLATE,
      Items.DIAMOND_LEGGINGS,
      Items.DIAMOND_BOOTS,
      Items.NETHERITE_SWORD,
      Items.NETHERITE_PICKAXE,
      Items.NETHERITE_AXE,
      Items.NETHERITE_SHOVEL,
      Items.NETHERITE_HOE,
      Items.NETHERITE_HELMET,
      Items.NETHERITE_CHESTPLATE,
      Items.NETHERITE_LEGGINGS,
      Items.NETHERITE_BOOTS,
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.GOLDEN_APPLE,
      Items.TOTEM_OF_UNDYING,
      Items.ELYTRA,
      Items.SHULKER_BOX,
      Items.WHITE_SHULKER_BOX,
      Items.ORANGE_SHULKER_BOX,
      Items.MAGENTA_SHULKER_BOX,
      Items.LIGHT_BLUE_SHULKER_BOX,
      Items.YELLOW_SHULKER_BOX,
      Items.LIME_SHULKER_BOX,
      Items.PINK_SHULKER_BOX,
      Items.GRAY_SHULKER_BOX,
      Items.LIGHT_GRAY_SHULKER_BOX,
      Items.CYAN_SHULKER_BOX,
      Items.PURPLE_SHULKER_BOX,
      Items.BLUE_SHULKER_BOX,
      Items.BROWN_SHULKER_BOX,
      Items.GREEN_SHULKER_BOX,
      Items.RED_SHULKER_BOX,
      Items.BLACK_SHULKER_BOX
   );
   private static final Set<Item> zu = Set.<Item>of(
      Items.BREAD,
      Items.COOKED_BEEF,
      Items.COOKED_PORKCHOP,
      Items.COOKED_CHICKEN,
      Items.COOKED_MUTTON,
      Items.COOKED_SALMON,
      Items.COOKED_COD,
      Items.BAKED_POTATO,
      Items.CARROT,
      Items.POTATO,
      Items.BEETROOT,
      Items.APPLE
   );

   public static void a(boolean flag) {
      if (!flag) {
         RenderSystem.disableDepthTest();
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void b() {
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
      RenderSystem.enableCull();
   }

   public static Vec3d c(Entity entity, float f, Vec3d vec3d) {
      double d0 = MathHelper.lerp(f, entity.prevX, entity.getX()) - vec3d.x;
      double d1 = MathHelper.lerp(f, entity.prevY, entity.getY()) - vec3d.y;
      double d2 = MathHelper.lerp(f, entity.prevZ, entity.getZ()) - vec3d.z;
      return new Vec3d(d0, d1, d2);
   }

   public static void d(BufferBuilder bufferbuilder, Matrix4f matrix4f, Vec3d vec3d, float f, float f1, float f2, float f3) {
      bufferbuilder.vertex(matrix4f, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z).color(f, f1, f2, f3);
   }

   public static void e(BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float[] afloat, float f6) {
      bufferbuilder.vertex(matrix4f, f, f1, f2).color(afloat[0], afloat[1], afloat[2], f6);
      bufferbuilder.vertex(matrix4f, f3, f4, f5).color(afloat[0], afloat[1], afloat[2], f6);
   }

   public static void f(BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float[] afloat, float f2) {
      float f3 = f / 2.0F;
      e(bufferbuilder, matrix4f, -f3, 0.0F, -f3, -f3, 0.0F, f3, afloat, f2);
      e(bufferbuilder, matrix4f, -f3, 0.0F, f3, f3, 0.0F, f3, afloat, f2);
      e(bufferbuilder, matrix4f, f3, 0.0F, f3, f3, 0.0F, -f3, afloat, f2);
      e(bufferbuilder, matrix4f, f3, 0.0F, -f3, -f3, 0.0F, -f3, afloat, f2);
      e(bufferbuilder, matrix4f, -f3, f1, -f3, -f3, f1, f3, afloat, f2);
      e(bufferbuilder, matrix4f, -f3, f1, f3, f3, f1, f3, afloat, f2);
      e(bufferbuilder, matrix4f, f3, f1, f3, f3, f1, -f3, afloat, f2);
      e(bufferbuilder, matrix4f, f3, f1, -f3, -f3, f1, -f3, afloat, f2);
      e(bufferbuilder, matrix4f, -f3, 0.0F, -f3, -f3, f1, -f3, afloat, f2);
      e(bufferbuilder, matrix4f, -f3, 0.0F, f3, -f3, f1, f3, afloat, f2);
      e(bufferbuilder, matrix4f, f3, 0.0F, f3, f3, f1, f3, afloat, f2);
      e(bufferbuilder, matrix4f, f3, 0.0F, -f3, f3, f1, -f3, afloat, f2);
   }

   public static void g(BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float[] afloat, float f3) {
      float f4 = f / 2.0F;
      float f5 = f / 2.0F;
      float f6 = Math.min(f2, Math.min(f, f1) * 0.4F);
      float[][] afloat1 = new float[][]{
         {-f4, 0.0F, -f5}, {f4, 0.0F, -f5}, {-f4, 0.0F, f5}, {f4, 0.0F, f5}, {-f4, f1, -f5}, {f4, f1, -f5}, {-f4, f1, f5}, {f4, f1, f5}
      };
      float[][][] afloat2 = new float[][][]{
         {{f6, 0.0F, 0.0F}, {0.0F, f6, 0.0F}, {0.0F, 0.0F, f6}},
         {{-f6, 0.0F, 0.0F}, {0.0F, f6, 0.0F}, {0.0F, 0.0F, f6}},
         {{f6, 0.0F, 0.0F}, {0.0F, f6, 0.0F}, {0.0F, 0.0F, -f6}},
         {{-f6, 0.0F, 0.0F}, {0.0F, f6, 0.0F}, {0.0F, 0.0F, -f6}},
         {{f6, 0.0F, 0.0F}, {0.0F, -f6, 0.0F}, {0.0F, 0.0F, f6}},
         {{-f6, 0.0F, 0.0F}, {0.0F, -f6, 0.0F}, {0.0F, 0.0F, f6}},
         {{f6, 0.0F, 0.0F}, {0.0F, -f6, 0.0F}, {0.0F, 0.0F, -f6}},
         {{-f6, 0.0F, 0.0F}, {0.0F, -f6, 0.0F}, {0.0F, 0.0F, -f6}}
      };

      for (int i = 0; i < afloat1.length; i++) {
         float[] afloat3 = afloat1[i];
         float[][] afloat4 = afloat2[i];

         for (float[] afloat5 : afloat4) {
            e(
               bufferbuilder,
               matrix4f,
               afloat3[0],
               afloat3[1],
               afloat3[2],
               afloat3[0] + afloat5[0],
               afloat3[1] + afloat5[1],
               afloat3[2] + afloat5[2],
               afloat,
               f3
            );
         }
      }
   }

   public static void h(MatrixStack matrixstack, int i, int j, int k, int l, float f, int i1) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      bufferbuilder.vertex(matrix4f, i - i1, j - i1, 0.0F).color(0.0F, 0.0F, 0.0F, f);
      bufferbuilder.vertex(matrix4f, i - i1, j + l + i1, 0.0F).color(0.0F, 0.0F, 0.0F, f);
      bufferbuilder.vertex(matrix4f, i + k + i1, j + l + i1, 0.0F).color(0.0F, 0.0F, 0.0F, f);
      bufferbuilder.vertex(matrix4f, i + k + i1, j - i1, 0.0F).color(0.0F, 0.0F, 0.0F, f);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
   }

   public static void i(MatrixStack matrixstack, float f, float f1, float f2, float f3, int i) {
      if (!(f2 <= 0.0F)) {
         float[] afloat = y(i);
         int j = MathHelper.clamp((int)(f2 * 1.8F), 48, 220);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.disableCull();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         RenderSystem.lineWidth(Math.max(1.0F, f3));
         Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

         for (int k = 0; k <= j; k++) {
            float f4 = (float)((Math.PI * 2) * k / j);
            float f5 = f + MathHelper.cos(f4) * f2;
            float f6 = f1 + MathHelper.sin(f4) * f2;
            bufferbuilder.vertex(matrix4f, f5, f6, 0.0F).color(afloat[0], afloat[1], afloat[2], afloat[3]);
         }

         BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
         RenderSystem.lineWidth(1.0F);
         RenderSystem.enableDepthTest();
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static float[] j(Formatting formatting) {
      switch (formatting) {
         case BLACK:
            return new float[]{0.0F, 0.0F, 0.0F};
         case DARK_BLUE:
            return new float[]{0.0F, 0.0F, 0.5F};
         case DARK_GREEN:
            return new float[]{0.0F, 0.5F, 0.0F};
         case DARK_AQUA:
            return new float[]{0.0F, 0.5F, 0.5F};
         case DARK_RED:
            return new float[]{0.5F, 0.0F, 0.0F};
         case DARK_PURPLE:
            return new float[]{0.5F, 0.0F, 0.5F};
         case GOLD:
            return new float[]{1.0F, 0.5F, 0.0F};
         case GRAY:
            return new float[]{0.5F, 0.5F, 0.5F};
         case DARK_GRAY:
            return new float[]{0.25F, 0.25F, 0.25F};
         case BLUE:
            return new float[]{0.3F, 0.3F, 1.0F};
         case GREEN:
            return new float[]{0.3F, 1.0F, 0.3F};
         case AQUA:
            return new float[]{0.3F, 1.0F, 1.0F};
         case RED:
            return new float[]{1.0F, 0.3F, 0.3F};
         case LIGHT_PURPLE:
            return new float[]{1.0F, 0.3F, 1.0F};
         case YELLOW:
            return new float[]{1.0F, 1.0F, 0.3F};
         default:
            return new float[]{1.0F, 1.0F, 1.0F};
      }
   }

   public static float[] k(String s) {
      for (Formatting formatting : Formatting.values()) {
         if (s.contains(formatting.toString())) {
            return j(formatting);
         }
      }

      return new float[]{1.0F, 1.0F, 1.0F};
   }

   public static float[] l(Item item) {
      String s = item.toString();
      if (item == Items.DIAMOND || s.contains("diamond")) {
         return new float[]{0.3F, 1.0F, 1.0F};
      } else if (item == Items.EMERALD || s.contains("emerald")) {
         return new float[]{0.3F, 1.0F, 0.3F};
      } else if (item == Items.GOLD_INGOT || s.contains("gold")) {
         return new float[]{1.0F, 1.0F, 0.3F};
      } else if (item == Items.IRON_INGOT || s.contains("iron")) {
         return new float[]{0.8F, 0.8F, 0.8F};
      } else if (item == Items.NETHERITE_INGOT || s.contains("netherite")) {
         return new float[]{0.3F, 0.1F, 0.3F};
      } else if (item == Items.ENCHANTED_GOLDEN_APPLE) {
         return new float[]{1.0F, 0.3F, 1.0F};
      } else if (item == Items.GOLDEN_APPLE) {
         return new float[]{1.0F, 0.8F, 0.3F};
      } else if (item == Items.TOTEM_OF_UNDYING) {
         return new float[]{1.0F, 1.0F, 0.0F};
      } else if (item == Items.ELYTRA) {
         return new float[]{0.5F, 0.3F, 0.8F};
      } else if (s.contains("shulker")) {
         return new float[]{0.8F, 0.3F, 0.8F};
      } else if (s.contains("sword")) {
         return new float[]{1.0F, 0.3F, 0.3F};
      } else if (s.contains("pickaxe") || s.contains("axe") || s.contains("shovel") || s.contains("hoe")) {
         return new float[]{0.3F, 0.8F, 1.0F};
      } else if (s.contains("helmet") || s.contains("chestplate") || s.contains("leggings") || s.contains("boots")) {
         return new float[]{0.6F, 0.6F, 1.0F};
      } else {
         return zu.contains(item) ? new float[]{1.0F, 0.6F, 0.3F} : new float[]{1.0F, 1.0F, 1.0F};
      }
   }

   public static boolean m(Item item) {
      return agX.contains(item);
   }

   public static int n(PlayerEntity playerentity) {
      float f = playerentity.getHealth();
      float f1 = playerentity.getMaxHealth();
      float f2 = f / f1;
      if (f2 > 0.6F) {
         return 5635925;
      } else {
         return f2 > 0.3F ? 16777045 : 16733525;
      }
   }

   public static Vec3d o(float f, float f1) {
      float f2 = f * (float) (Math.PI / 180.0);
      float f3 = -f1 * (float) (Math.PI / 180.0);
      float f4 = (float)Math.cos(f3);
      float f5 = (float)Math.sin(f3);
      float f6 = (float)Math.cos(f2);
      float f7 = (float)Math.sin(f2);
      return new Vec3d(f5 * f6, -f7, f4 * f6);
   }

   public static boolean p(Vec3d vec3d, Vec3d vec3d1, double d0) {
      double d1 = d0 * d0;
      return !(vec3d.squaredDistanceTo(vec3d1) <= d1);
   }

   public static void q(MatrixStack matrixstack, Vec3d vec3d, Vec3d vec3d1, float f, float f1, float f2, float f3, boolean flag) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      if (flag) {
         RenderSystem.disableDepthTest();
      }

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      u(bufferbuilder, matrix4f, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z, (float)vec3d1.x, (float)vec3d1.y, (float)vec3d1.z, f, f1, f2, f3);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.enableDepthTest();
   }

   public static void r(
      BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9
   ) {
      bufferbuilder.vertex(matrix4f, f, f1, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f4, f5).color(f6, f7, f8, f9);
   }

   public static void s(
      BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9
   ) {
      r(bufferbuilder, matrix4f, f, f1, f2, f3, f1, f2, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f3, f1, f2, f3, f1, f5, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f3, f1, f5, f, f1, f5, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f, f1, f5, f, f1, f2, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f, f4, f2, f3, f4, f2, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f3, f4, f2, f3, f4, f5, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f3, f4, f5, f, f4, f5, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f, f4, f5, f, f4, f2, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f, f1, f2, f, f4, f2, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f3, f1, f2, f3, f4, f2, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f3, f1, f5, f3, f4, f5, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f, f1, f5, f, f4, f5, f6, f7, f8, f9);
   }

   public static void t(
      BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9
   ) {
      r(bufferbuilder, matrix4f, f, f1, f2, f3, f4, f5, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f3, f1, f2, f, f4, f5, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f, f1, f5, f3, f4, f2, f6, f7, f8, f9);
      r(bufferbuilder, matrix4f, f3, f1, f5, f, f4, f2, f6, f7, f8, f9);
   }

   public static void u(
      BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9
   ) {
      bufferbuilder.vertex(matrix4f, f, f1, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f1, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f1, f5).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f, f1, f5).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f, f4, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f, f4, f5).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f4, f5).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f4, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f, f1, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f, f4, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f4, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f1, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f, f1, f5).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f1, f5).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f4, f5).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f, f4, f5).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f, f1, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f, f1, f5).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f, f4, f5).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f, f4, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f1, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f4, f2).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f4, f5).color(f6, f7, f8, f9);
      bufferbuilder.vertex(matrix4f, f3, f1, f5).color(f6, f7, f8, f9);
   }

   public static void v(BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7) {
      bufferbuilder.vertex(matrix4f, f, f1 + f3, 0.0F).texture(0.0F, 1.0F).color(f4, f5, f6, f7);
      bufferbuilder.vertex(matrix4f, f + f2, f1 + f3, 0.0F).texture(1.0F, 1.0F).color(f4, f5, f6, f7);
      bufferbuilder.vertex(matrix4f, f + f2, f1, 0.0F).texture(1.0F, 0.0F).color(f4, f5, f6, f7);
      bufferbuilder.vertex(matrix4f, f, f1, 0.0F).texture(0.0F, 0.0F).color(f4, f5, f6, f7);
   }

   public static void w(BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      float f4 = 0.5F;
      float f5 = 0.7F;
      float f6 = 0.0F;
      float f7 = -f5 / 2.0F;
      float f8 = -f4 * 0.866F;
      float f9 = -f5 / 2.0F;
      float f10 = -f4 / 2.0F;
      float f11 = f4 * 0.866F;
      float f12 = -f5 / 2.0F;
      float f13 = -f4 / 2.0F;
      float f14 = 0.0F;
      float f15 = f5 / 2.0F;
      float f16 = 0.0F;
      r(bufferbuilder, matrix4f, f6, f7, f4, f8, f9, f10, f, f1, f2, f3);
      r(bufferbuilder, matrix4f, f8, f9, f10, f11, f12, f13, f, f1, f2, f3);
      r(bufferbuilder, matrix4f, f11, f12, f13, f6, f7, f4, f, f1, f2, f3);
      r(bufferbuilder, matrix4f, f6, f7, f4, f14, f15, f16, f, f1, f2, f3);
      r(bufferbuilder, matrix4f, f8, f9, f10, f14, f15, f16, f, f1, f2, f3);
      r(bufferbuilder, matrix4f, f11, f12, f13, f14, f15, f16, f, f1, f2, f3);
   }

   public static int x(Color color, float f) {
      int i = Math.max(0, Math.min(255, (int)(f * 255.0F)));
      return i << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
   }

   public static float[] y(int i) {
      return new float[]{(i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, (i >> 24 & 0xFF) / 255.0F};
   }
}
