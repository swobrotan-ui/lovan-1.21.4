import core.ClientMain;
import hud.HudComponent;
import hud.HudEntry;
import hud.HudManager;
import java.util.ArrayList;
import java.util.List;
import module.ChatTweaksModule;
import module.PanicModule;
import module.ProtestModule;
import module.TranslatorModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;
import render.CustomChatHud;
import util.ChatUtil;

public class si extends ChatScreen {
   private static final float afI = 5.0F;
   private static boolean aal = false;
   private float gY;
   private float agn;
   private float bE;
   private float eR = 0.0F;
   private final cjf k;
   private HudComponent ack = null;
   private dt Dy;
   private volatile boolean ajB = false;

   public si(String s) {
      super(s);
      this.k = new cjf();
   }

   protected void init() {
      super.init();
      this.eR = 0.0F;
      aal = true;
      HudManager.b().a(true);
      if (this.chatField != null) {
         this.Dy = new dt(this.client, this.chatField);
         this.applyPasswordMasking();
      }
   }

   public void close() {
      HudManager.b().m();
      aal = false;
      HudManager.b().a(false);
      super.close();
   }

   public void removed() {
      if (aal) {
         aal = false;
         HudManager.b().a(false);
      }

      super.removed();
   }

   public void render(DrawContext drawcontext, int i, int j, float f) {
      super.render(drawcontext, i, j, f);
      PanicModule panicmodule = ClientMain.getInstance().getModuleManager().<PanicModule>getModule(PanicModule.class);
      this.calculateScaling();
      this.updateGuiAlpha(f);
      this.k.e();
      drawcontext.getMatrices().push();
      this.applyGuiTransform(drawcontext);
      Matrix4f matrix4f = drawcontext.getMatrices().peek().getPositionMatrix();
      if (this.ack != null) {
         this.k.f(matrix4f);
      }

      for (HudEntry hudentry : HudManager.b().n()) {
         if (hudentry.isVisible()) {
            HudComponent hudcomponent = hudentry.getComponent();
            if (panicmodule != null && !panicmodule.c()) {
               hudcomponent.render(matrix4f, this.eR);
            }
         }
      }

      drawcontext.getMatrices().pop();
      if (this.Dy != null && panicmodule != null && !panicmodule.c()) {
         this.Dy.b(drawcontext, i, j);
      }
   }

   private void updateGuiAlpha(float f) {
      this.eR = this.lerp(this.eR, 1.0F, f * 5.0F);
   }

   private void applyGuiTransform(DrawContext drawcontext) {
      drawcontext.getMatrices().translate(this.gY, this.agn, 0.0F);
      drawcontext.getMatrices().scale(this.bE, this.bE, 1.0F);
   }

   private void calculateScaling() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      int i = minecraftclient.getWindow().getWidth();
      int j = minecraftclient.getWindow().getHeight();
      double d0 = minecraftclient.getWindow().getScaleFactor();
      float f = i / 1920.0F;
      this.gY = 0.0F;
      this.agn = 0.0F;
      this.bE = (float)(f / d0);
      if (i > 0) {
         this.k.a(j * 1920.0F / i);
      }
   }

   private double[] scaleMousePosition(double d0, double d1) {
      return new double[]{(d0 - this.gY) / this.bE, (d1 - this.agn) / this.bE};
   }

   public boolean mouseClicked(double d0, double d1, int i) {
      if (i == 1 && this.copyClickedMessage(d0, d1)) {
         return true;
      } else if (i == 2 && this.translateClickedMessage(d0, d1)) {
         return true;
      } else if (!this.isPanic() && this.Dy != null && this.Dy.g(d0, d1, i)) {
         return true;
      } else {
         double[] adouble = this.scaleMousePosition(d0, d1);
         List list = HudManager.b().n();

         for (int j = list.size() - 1; j >= 0; j--) {
            HudEntry hudentry = (HudEntry)list.get(j);
            if (hudentry.isVisible()) {
               HudComponent hudcomponent = hudentry.getComponent();
               if (hudcomponent.mouseClicked(adouble[0], adouble[1], i)) {
                  this.ack = hudcomponent;
                  this.k.c();
                  return true;
               }
            }
         }

         return super.mouseClicked(d0, d1, i);
      }
   }

   private boolean copyClickedMessage(double d0, double d1) {
      ChatTweaksModule chattweaksmodule = ClientMain.getInstance().getModuleManager().<ChatTweaksModule>getModule(ChatTweaksModule.class);
      if (chattweaksmodule != null && chattweaksmodule.isEnabled() && chattweaksmodule.c().getValue()) {
         MinecraftClient minecraftclient = MinecraftClient.getInstance();
         if (minecraftclient.inGameHud == null) {
            return false;
         } else if (minecraftclient.inGameHud.getChatHud() instanceof CustomChatHud customchathud) {
            Text text = customchathud.getFullMessageAt(d0, d1);
            if (text == null) {
               return false;
            } else {
               String s = text.getString();
               if (s.isEmpty()) {
                  return false;
               } else {
                  minecraftclient.keyboard.setClipboard(s);
                  ChatUtil.sendSuccess("Сообщение скопировано в буфер обмена");
                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean mouseDragged(double d0, double d1, int i, double d2, double d3) {
      if (this.ack != null && this.ack.isDragging()) {
         double[] adouble = this.scaleMousePosition(d0, d1);
         double d4 = d2 / this.bE;
         double d5 = d3 / this.bE;
         ArrayList arraylist = new ArrayList();

         for (HudEntry hudentry : HudManager.b().n()) {
            arraylist.add(hudentry.getComponent());
         }

         boolean flag = hasControlDown();
         this.k.j(!flag);
         if (this.ack.mouseDragged(adouble[0], adouble[1], i, d4, d5)) {
            dm dm = this.k.g(this.ack.getX(), this.ack.getY(), this.ack.getWidth(), this.ack.getTotalHeight(), arraylist, this.ack);
            this.ack.setPosition(dm.a(), dm.b());
            return true;
         }
      }

      return super.mouseDragged(d0, d1, i, d2, d3);
   }

   public boolean mouseReleased(double d0, double d1, int i) {
      double[] adouble = this.scaleMousePosition(d0, d1);
      boolean flag = false;

      for (HudEntry hudentry : HudManager.b().n()) {
         HudComponent hudcomponent = hudentry.getComponent();
         if (hudcomponent.mouseReleased(adouble[0], adouble[1], i)) {
            flag = true;
         }
      }

      this.ack = null;
      this.k.d();
      return flag ? true : super.mouseReleased(d0, d1, i);
   }

   private void applyPasswordMasking() {
      if (this.chatField != null) {
         this.chatField.setRenderTextProvider((s, integer) -> {
            ProtestModule protestmodule = ClientMain.getInstance().getModuleManager().<ProtestModule>getModule(ProtestModule.class);
            if (protestmodule != null && protestmodule.b()) {
               String s1 = this.chatField.getText();
               String s2 = protestmodule.c(s1);
               int i = Math.min(integer + s.length(), s2.length());
               if (integer < s2.length()) {
                  s = s2.substring(integer, i);
               }
            }

            String s3 = this.chatField.getText();
            Style style = !s3.startsWith("/") && !s3.startsWith(".") ? Style.EMPTY : Style.EMPTY.withColor(Formatting.GRAY);
            return OrderedText.styledForwardsVisitedString(s, style);
         });
      }
   }

   private float lerp(float f, float f1, float f2) {
      return f + (f1 - f) * Math.min(f2, 1.0F);
   }

   public boolean shouldPause() {
      return false;
   }

   private boolean isPanic() {
      PanicModule panicmodule = ClientMain.getInstance().getModuleManager().<PanicModule>getModule(PanicModule.class);
      return panicmodule == null || panicmodule.c();
   }

   public boolean keyPressed(int i, int j, int k) {
      if (i == 84 && hasControlDown() && !hasShiftDown() && !hasAltDown()) {
         this.translateChatField();
         return true;
      } else {
         if (!this.isPanic() && this.Dy != null && this.Dy.h()) {
            if (i == 265) {
               this.Dy.c();
               return true;
            }

            if (i == 264) {
               this.Dy.d();
               return true;
            }

            if (i == 258 && this.Dy.e()) {
               return true;
            }

            if (i == 256) {
               this.Dy.f();
            }
         }

         boolean flag = super.keyPressed(i, j, kx);
         this.applyPasswordMasking();
         if (this.Dy != null && !this.isPanic()) {
            this.Dy.a();
         }

         return flag;
      }
   }

   public boolean charTyped(char c0, int i) {
      boolean flag = super.charTyped(c0, i);
      this.applyPasswordMasking();
      if (this.Dy != null && !this.isPanic()) {
         this.Dy.a();
      }

      return flag;
   }

   private TranslatorModule getTranslator() {
      return ClientMain.getInstance().getModuleManager().<TranslatorModule>getModule(TranslatorModule.class);
   }

   private boolean isTranslatorEnabled() {
      TranslatorModule translatormodule = this.getTranslator();
      return translatormodule != null && translatormodule.isEnabled();
   }

   private boolean translateClickedMessage(double d0, double d1) {
      if (this.isTranslatorEnabled() && !this.ajB) {
         MinecraftClient minecraftclient = MinecraftClient.getInstance();
         if (minecraftclient.inGameHud == null) {
            return false;
         } else if (minecraftclient.inGameHud.getChatHud() instanceof CustomChatHud customchathud) {
            Text text = customchathud.getFullMessageAt(d0, d1);
            if (text == null) {
               return false;
            } else {
               String s = TranslatorModule.d(text.getString());
               if (s.isEmpty()) {
                  return false;
               } else {
                  this.ajB = true;
                  TranslatorModule translatormodule = this.getTranslator();
                  translatormodule.g(s, new fq(this, minecraftclient));
                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void translateChatField() {
      if (this.isTranslatorEnabled() && this.chatField != null && !this.ajB) {
         String s = this.chatField.getText();
         if (!s.isEmpty() && !s.startsWith(".") && !s.startsWith("/")) {
            this.ajB = true;
            TranslatorModule translatormodule = this.getTranslator();
            translatormodule.f(s, new gd(this));
         }
      }
   }

   // $VF: synthetic method
   static TextFieldWidget access$000(si si) {
      return si.chatField;
   }

   // $VF: synthetic method
   static TextFieldWidget access$100(si si) {
      return si.chatField;
   }
}
