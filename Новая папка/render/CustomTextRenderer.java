package render;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import core.ClientMain;
import core.FriendManager;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import module.ProtestModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class CustomTextRenderer extends TextRenderer {
   private final Function<Identifier, FontStorage> fontStorageAccessor;
   final boolean validateAdvance;
   private final TextHandler handler;

   public CustomTextRenderer(Function<Identifier, FontStorage> function, boolean flag) {
      super(function, flag);
      this.fontStorageAccessor = function;
      this.validateAdvance = flag;
      this.handler = new TextHandler((i, style) -> {
         return this.getFontStorage(style.getFont()).getGlyph(i, this.validateAdvance).getAdvance(style.isBold());
      });
   }

   FontStorage getFontStorage(Identifier identifier) {
      return this.fontStorageAccessor.apply(identifier);
   }

   public String mirror(String s) {
      try {
         Bidi bidi = new Bidi(new ArabicShaping(8).shape(s), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException arabicshapingexception) {
         return s;
      }
   }

   private String replacePlayerName(String s) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient.player == null) {
         return s;
      } else {
         ProtestModule protestmodule = ClientMain.getInstance().getModuleManager().<ProtestModule>getModule(ProtestModule.class);
         if (protestmodule != null && protestmodule.isEnabled()) {
            String s1 = minecraftclient.player.getGameProfile().getName();
            if (s.contains(s1)) {
               s = s.replace(s1, "SуstemPlayer");
            }

            if (protestmodule.e().getValue()) {
               for (String s2 : FriendManager.getInstance().getFriends()) {
                  String s3 = Pattern.quote(s2);
                  s = s.replaceAll("(?i)" + s3, "SуstemFriend");
               }
            }

            return s;
         } else {
            return s;
         }
      }
   }

   private OrderedText replacePlayerNameInOrderedText(OrderedText orderedtext) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient.player == null) {
         return orderedtext;
      } else {
         ProtestModule protestmodule = ClientMain.getInstance().getModuleManager().<ProtestModule>getModule(ProtestModule.class);
         if (protestmodule != null && protestmodule.isEnabled()) {
            StringBuilder stringbuilder = new StringBuilder();
            orderedtext.accept((j, style, i) -> {
               stringbuilder.appendCodePoint(i);
               return true;
            });
            String s = stringbuilder.toString();
            String s1 = minecraftclient.player.getGameProfile().getName();
            if (s.contains(s1)) {
               s = s.replace(s1, "SуstemPlayer");
            }

            if (protestmodule.e().getValue()) {
               for (String s2 : FriendManager.getInstance().getFriends()) {
                  String s3 = Pattern.quote(s2);
                  s = s.replaceAll("(?i)" + s3, "SуstemFriend");
               }
            }

            return !s.equals(stringbuilder.toString()) ? OrderedText.styledForwardsVisitedString(s, Style.EMPTY) : orderedtext;
         } else {
            return orderedtext;
         }
      }
   }

   public int draw(
      String s,
      float f,
      float f1,
      int i,
      boolean flag,
      Matrix4f matrix4f,
      VertexConsumerProvider vertexconsumerprovider,
      TextLayerType textlayertype,
      int j,
      int k
   ) {
      s = this.replacePlayerName(s);
      if (this.isRightToLeft()) {
         s = this.mirror(s);
      }

      return this.drawInternal(s, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, true);
   }

   public int draw(
      Text text,
      float f,
      float f1,
      int i,
      boolean flag,
      Matrix4f matrix4f,
      VertexConsumerProvider vertexconsumerprovider,
      TextLayerType textlayertype,
      int j,
      int k
   ) {
      return this.draw(text, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, true);
   }

   public int draw(
      Text text,
      float f,
      float f1,
      int i,
      boolean flag,
      Matrix4f matrix4f,
      VertexConsumerProvider vertexconsumerprovider,
      TextLayerType textlayertype,
      int j,
      int k,
      boolean flag1
   ) {
      OrderedText orderedtext = this.replacePlayerNameInOrderedText(text.asOrderedText());
      return this.drawInternal(orderedtext, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, flag1);
   }

   public int draw(
      OrderedText orderedtext,
      float f,
      float f1,
      int i,
      boolean flag,
      Matrix4f matrix4f,
      VertexConsumerProvider vertexconsumerprovider,
      TextLayerType textlayertype,
      int j,
      int k
   ) {
      orderedtext = this.replacePlayerNameInOrderedText(orderedtext);
      return this.drawInternal(orderedtext, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, true);
   }

   public void drawWithOutline(
      OrderedText orderedtext, float f, float f1, int i, int j, Matrix4f matrix4f, VertexConsumerProvider vertexconsumerprovider, int k
   ) {
      orderedtext = this.replacePlayerNameInOrderedText(orderedtext);
      int l = tweakTransparency(j);
      fz fzx = new fz(this, vertexconsumerprovider, 0.0F, 0.0F, l, false, matrix4f, TextLayerType.NORMAL, k);

      for (int i1 = -1; i1 <= 1; i1++) {
         for (int j1 = -1; j1 <= 1; j1++) {
            if (i1 != 0 || j1 != 0) {
               float[] afloat = new float[]{f};
               int k1 = i1;
               int l1 = j1;
               orderedtext.accept((l2, style, i3) -> {
                  boolean flag = style.isBold();
                  FontStorage fontstorage = this.getFontStorage(style.getFont());
                  Glyph glyph = fontstorage.getGlyph(i3, this.validateAdvance);
                  fzx.x = afloat[0] + k1 * glyph.getShadowOffset();
                  fzx.y = f1 + l1 * glyph.getShadowOffset();
                  afloat[0] += glyph.getAdvance(flag);
                  return fzx.accept(l2, style.withColor(l), i3);
               });
            }
         }
      }

      fzx.drawGlyphs();
      fz fzx = new fz(this, vertexconsumerprovider, f, f1, tweakTransparency(i), false, matrix4f, TextLayerType.POLYGON_OFFSET, k);
      orderedtext.accept(fzx);
      fzx.drawLayer(f);
   }

   private static int tweakTransparency(int i) {
      return (i & -67108864) == 0 ? ColorHelper.fullAlpha(i) : i;
   }

   private int drawInternal(
      String s,
      float f,
      float f1,
      int i,
      boolean flag,
      Matrix4f matrix4f,
      VertexConsumerProvider vertexconsumerprovider,
      TextLayerType textlayertype,
      int j,
      int k,
      boolean flag1
   ) {
      i = tweakTransparency(i);
      f = this.drawLayer(s, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, flag1);
      return (int)f + (flag ? 1 : 0);
   }

   private int drawInternal(
      OrderedText orderedtext,
      float f,
      float f1,
      int i,
      boolean flag,
      Matrix4f matrix4f,
      VertexConsumerProvider vertexconsumerprovider,
      TextLayerType textlayertype,
      int j,
      int k,
      boolean flag1
   ) {
      i = tweakTransparency(i);
      f = this.drawLayer(orderedtext, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, flag1);
      return (int)f + (flag ? 1 : 0);
   }

   private float drawLayer(
      String s,
      float f,
      float f1,
      int i,
      boolean flag,
      Matrix4f matrix4f,
      VertexConsumerProvider vertexconsumerprovider,
      TextLayerType textlayertype,
      int j,
      int k,
      boolean flag1
   ) {
      fz fz = new fz(this, vertexconsumerprovider, f, f1, i, j, flag, matrix4f, textlayertype, k, flag1);
      TextVisitFactory.visitFormatted(s, Style.EMPTY, fz);
      return fz.drawLayer(f);
   }

   private float drawLayer(
      OrderedText orderedtext,
      float f,
      float f1,
      int i,
      boolean flag,
      Matrix4f matrix4f,
      VertexConsumerProvider vertexconsumerprovider,
      TextLayerType textlayertype,
      int j,
      int k,
      boolean flag1
   ) {
      fz fz = new fz(this, vertexconsumerprovider, f, f1, i, j, flag, matrix4f, textlayertype, k, flag1);
      orderedtext.accept(fz);
      return fz.drawLayer(f);
   }

   public int getWidth(String s) {
      return MathHelper.ceil(this.handler.getWidth(s));
   }

   public int getWidth(StringVisitable stringvisitable) {
      return MathHelper.ceil(this.handler.getWidth(stringvisitable));
   }

   public int getWidth(OrderedText orderedtext) {
      return MathHelper.ceil(this.handler.getWidth(orderedtext));
   }

   public String trimToWidth(String s, int i, boolean flag) {
      return flag ? this.handler.trimToWidthBackwards(s, i, Style.EMPTY) : this.handler.trimToWidth(s, i, Style.EMPTY);
   }

   public String trimToWidth(String s, int i) {
      return this.handler.trimToWidth(s, i, Style.EMPTY);
   }

   public StringVisitable trimToWidth(StringVisitable stringvisitable, int i) {
      return this.handler.trimToWidth(stringvisitable, i, Style.EMPTY);
   }

   public int getWrappedLinesHeight(String s, int i) {
      return 9 * this.handler.wrapLines(s, i, Style.EMPTY).size();
   }

   public int getWrappedLinesHeight(StringVisitable stringvisitable, int i) {
      return 9 * this.handler.wrapLines(stringvisitable, i, Style.EMPTY).size();
   }

   public List<OrderedText> wrapLines(StringVisitable stringvisitable, int i) {
      return Language.getInstance().reorder(this.handler.wrapLines(stringvisitable, i, Style.EMPTY));
   }

   public boolean isRightToLeft() {
      return Language.getInstance().isRightToLeft();
   }

   public TextHandler getTextHandler() {
      return this.handler;
   }
}
