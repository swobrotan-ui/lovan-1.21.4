import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.EmptyBakedGlyph;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.BakedGlyph.DrawnGlyph;
import net.minecraft.client.font.BakedGlyph.Rectangle;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import render.CustomTextRenderer;

@Environment(EnvType.CLIENT)
class fz implements CharacterVisitor {
   final VertexConsumerProvider vertexConsumers;
   private final boolean shadow;
   private final int color;
   private final int backgroundColor;
   private final Matrix4f matrix;
   private final TextLayerType layerType;
   private final int light;
   private final boolean swapZIndex;
   float x;
   float y;
   private final List<DrawnGlyph> glyphs;
   @Nullable
   private List<Rectangle> rectangles;
   // $VF: synthetic field
   final CustomTextRenderer this$0;

   private void addRectangle(Rectangle rectangle) {
      if (this.rectangles == null) {
         this.rectangles = Lists.newArrayList();
      }

      this.rectangles.add(rectangle);
   }

   public fz(
      CustomTextRenderer customtextrenderer,
      VertexConsumerProvider vertexconsumerprovider,
      float f,
      float f1,
      int i,
      boolean flag,
      Matrix4f matrix4f,
      TextLayerType textlayertype,
      int j
   ) {
      this(customtextrenderer, vertexconsumerprovider, f, f1, i, 0, flag, matrix4f, textlayertype, j, true);
   }

   public fz(
      CustomTextRenderer customtextrenderer,
      VertexConsumerProvider vertexconsumerprovider,
      float f,
      float f1,
      int i,
      int j,
      boolean flag,
      Matrix4f matrix4f,
      TextLayerType textlayertype,
      int k,
      boolean flag1
   ) {
      this.this$0 = customtextrenderer;
      this.glyphs = new ArrayList<DrawnGlyph>();
      this.vertexConsumers = vertexconsumerprovider;
      this.x = f;
      this.y = f1;
      this.shadow = flag;
      this.color = i;
      this.backgroundColor = j;
      this.matrix = matrix4f;
      this.layerType = textlayertype;
      this.light = k;
      this.swapZIndex = flag1;
   }

   public boolean accept(int i, Style style, int j) {
      FontStorage fontstorage = this.this$0.getFontStorage(style.getFont());
      Glyph glyph = fontstorage.getGlyph(j, this.this$0.validateAdvance);
      BakedGlyph bakedglyph = style.isObfuscated() && j != 32 ? fontstorage.getObfuscatedBakedGlyph(glyph) : fontstorage.getBaked(j);
      boolean flag = style.isBold();
      TextColor textcolor = style.getColor();
      int k = this.getRenderColor(textcolor);
      int l = this.getShadowColor(style, k);
      float f = glyph.getAdvance(flag);
      float f1 = i == 0 ? this.x - 1.0F : this.x;
      float f2 = glyph.getShadowOffset();
      if (!(bakedglyph instanceof EmptyBakedGlyph)) {
         float f3 = flag ? glyph.getBoldOffset() : 0.0F;
         this.glyphs.add(new DrawnGlyph(this.x, this.y, k, l, bakedglyph, style, f3, f2));
      }

      if (style.isStrikethrough()) {
         this.addRectangle(new Rectangle(f1, this.y + 4.5F, this.x + f, this.y + 4.5F - 1.0F, this.getForegroundZIndex(), k, l, f2));
      }

      if (style.isUnderlined()) {
         this.addRectangle(new Rectangle(f1, this.y + 9.0F, this.x + f, this.y + 9.0F - 1.0F, this.getForegroundZIndex(), k, l, f2));
      }

      this.x += f;
      return true;
   }

   float drawLayer(float f) {
      BakedGlyph bakedglyph = null;
      if (this.backgroundColor != 0) {
         Rectangle rectangle = new Rectangle(f - 1.0F, this.y + 9.0F, this.x, this.y - 1.0F, this.getBackgroundZIndex(), this.backgroundColor);
         bakedglyph = this.this$0.getFontStorage(Style.DEFAULT_FONT_ID).getRectangleBakedGlyph();
         VertexConsumer vertexconsumer = this.vertexConsumers.getBuffer(bakedglyph.getLayer(this.layerType));
         bakedglyph.drawRectangle(rectangle, this.matrix, vertexconsumer, this.light);
      }

      this.drawGlyphs();
      if (this.rectangles != null) {
         if (bakedglyph == null) {
            bakedglyph = this.this$0.getFontStorage(Style.DEFAULT_FONT_ID).getRectangleBakedGlyph();
         }

         VertexConsumer vertexconsumer1 = this.vertexConsumers.getBuffer(bakedglyph.getLayer(this.layerType));

         for (Rectangle rectangle1 : this.rectangles) {
            bakedglyph.drawRectangle(rectangle1, this.matrix, vertexconsumer1, this.light);
         }
      }

      return this.x;
   }

   private int getRenderColor(@Nullable TextColor textcolor) {
      if (textcolor != null) {
         int i = ColorHelper.getAlpha(this.color);
         int j = textcolor.getRgb();
         return ColorHelper.withAlpha(i, j);
      } else {
         return this.color;
      }
   }

   private int getShadowColor(Style style, int i) {
      Integer integer = style.getShadowColor();
      if (integer != null) {
         float f = ColorHelper.getAlphaFloat(i);
         float f1 = ColorHelper.getAlphaFloat(integer);
         return f != 1.0F ? ColorHelper.withAlpha(ColorHelper.channelFromFloat(f * f1), integer) : integer;
      } else {
         return this.shadow ? ColorHelper.scaleRgb(i, 0.25F) : 0;
      }
   }

   void drawGlyphs() {
      for (DrawnGlyph drawnglyph : this.glyphs) {
         BakedGlyph bakedglyph = drawnglyph.glyph();
         VertexConsumer vertexconsumer = this.vertexConsumers.getBuffer(bakedglyph.getLayer(this.layerType));
         bakedglyph.draw(drawnglyph, this.matrix, vertexconsumer, this.light);
      }
   }

   private float getForegroundZIndex() {
      return this.swapZIndex ? 0.01F : -0.01F;
   }

   private float getBackgroundZIndex() {
      return this.swapZIndex ? -0.01F : 0.01F;
   }
}
