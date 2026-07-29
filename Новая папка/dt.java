import command.CommandRegistry;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class dt {
   private final MinecraftClient aR;
   private final TextFieldWidget aaY;
   private List<String> kq;
   private int Sh = 0;
   private boolean n = false;
   private int Bx;
   private int lc;
   private int ZE;
   private int ZI;

   public dt(MinecraftClient minecraftclient, TextFieldWidget textfieldwidget) {
      this.aR = minecraftclient;
      this.aaY = textfieldwidget;
   }

   public void a() {
      String s = this.aaY.getText();
      if (s.startsWith(".")) {
         this.kq = CommandRegistry.getInstance().getCompletions(s);
         this.n = !this.kq.isEmpty();
         this.Sh = 0;
      } else {
         this.n = false;
         this.kq = null;
      }
   }

   public void b(DrawContext drawcontext, int i, int j) {
      if (this.n && this.kq != null && !this.kq.isEmpty()) {
         drawcontext.getMatrices().push();
         drawcontext.getMatrices().translate(0.0F, 0.0F, 500.0F);
         TextRenderer textrenderer = this.aR.textRenderer;
         int k = this.aaY.getX();
         int l = Math.min(this.kq.size(), 10);
         int i1 = l * 12;
         int j1 = this.aaY.getY() - i1 - 4;
         int k1 = 0;

         for (int l1 = 0; l1 < l; l1++) {
            String s = this.kq.get(l1);
            int i2 = textrenderer.getWidth(s) + 8;
            String s1 = CommandRegistry.getInstance().getDescription(s.split(" ")[0]);
            if (s1 != null && !s1.isEmpty()) {
               i2 += textrenderer.getWidth(" - " + s1);
            }

            k1 = Math.max(k1, i2);
         }

         this.Bx = k - 2;
         this.lc = j1 - 2;
         this.ZE = k1 + 4;
         this.ZI = l * 12 + 4;
         drawcontext.fill(this.Bx, this.lc, k + k1 + 2, j1 + l * 12 + 2, -872415232);
         int k2 = -1;
         if (i >= this.Bx && i <= this.Bx + this.ZE && j >= this.lc && j <= this.lc + this.ZI) {
            int l2 = j - this.lc;
            k2 = l2 / 12;
            if (k2 >= l) {
               k2 = -1;
            }
         }

         for (int i3 = 0; i3 < l; i3++) {
            String s4 = this.kq.get(i3);
            int j3 = j1 + i3 * 12;
            if (i3 == this.Sh) {
               drawcontext.fill(k, j3, k + k1, j3 + 10, -2130706433);
            } else if (i3 == k2) {
               drawcontext.fill(k, j3, k + k1, j3 + 10, 1090519039);
            }

            String[] astring = s4.split(" ");
            String s2 = astring[0];
            drawcontext.drawText(textrenderer, s2, k + 2, j3 + 1, 16777215, true);
            if (astring.length > 1) {
               int j2 = textrenderer.getWidth(s2);
               String s3 = astring[1];
               drawcontext.drawText(textrenderer, " " + s3, k + 2 + j2, j3 + 1, 11184895, true);
            }

            String s5 = CommandRegistry.getInstance().getDescription(s2);
            if (s5 != null && !s5.isEmpty()) {
               int k3 = textrenderer.getWidth(s4);
               drawcontext.drawText(textrenderer, " - ", k + 2 + k3, j3 + 1, 8947848, true);
               drawcontext.drawText(textrenderer, s5, k + 2 + k3 + textrenderer.getWidth(" - "), j3 + 1, 8947848, true);
            }
         }

         drawcontext.getMatrices().pop();
      }
   }

   public void c() {
      if (this.n && this.kq != null && !this.kq.isEmpty()) {
         this.Sh = (this.Sh - 1 + this.kq.size()) % this.kq.size();
      }
   }

   public void d() {
      if (this.n && this.kq != null && !this.kq.isEmpty()) {
         this.Sh = (this.Sh + 1) % this.kq.size();
      }
   }

   public boolean e() {
      if (this.n && this.kq != null && !this.kq.isEmpty() && this.Sh < this.kq.size()) {
         String s = this.kq.get(this.Sh);
         this.aaY.setText(s + " ");
         this.aaY.setCursor(this.aaY.getText().length(), false);
         this.a();
         return true;
      } else {
         return false;
      }
   }

   public void f() {
      this.n = false;
      this.kq = null;
      this.Sh = 0;
   }

   public boolean g(double d0, double d1, int i) {
      if (!this.n || this.kq == null || this.kq.isEmpty() || i != 0) {
         return false;
      } else if (!(d0 < this.Bx) && !(d0 > this.Bx + this.ZE) && !(d1 < this.lc) && !(d1 > this.lc + this.ZI)) {
         int j = Math.min(this.kq.size(), 10);
         int k = (int)(d1 - this.lc);
         int l = k / 12;
         if (l >= 0 && l < j) {
            this.Sh = l;
            this.e();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean h() {
      return this.n;
   }
}
