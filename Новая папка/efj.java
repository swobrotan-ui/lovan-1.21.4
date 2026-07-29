import data.Rect;
import java.util.Stack;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11;

public class efj {
   private static final Stack<Rect> abc = new Stack<Rect>();

   public static void a(float f, float f1, float f2, float f3, float f4, float f5, float f6) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      double d0 = minecraftclient.getWindow().getScaleFactor();
      int i = minecraftclient.getWindow().getHeight();
      float f7 = (f5 + f * f4) * (float)d0;
      float f8 = (f6 + f1 * f4) * (float)d0;
      float f9 = f2 * f4 * (float)d0;
      float f10 = f3 * f4 * (float)d0;
      int j = (int)f7;
      int k = (int)(i - f8 - f10);
      int l = (int)f9;
      int i1 = (int)f10;
      if (!abc.isEmpty()) {
         Rect rect = abc.peek();
         int j1 = Math.max(j, rect.x);
         int k1 = Math.max(k, rect.y);
         int l1 = Math.min(j + l, rect.x + rect.width);
         int i2 = Math.min(k + i1, rect.y + rect.height);
         j = j1;
         k = k1;
         l = Math.max(0, l1 - j1);
         i1 = Math.max(0, i2 - k1);
      }

      abc.push(new Rect(j, k, l, i1));
      GL11.glEnable(3089);
      GL11.glScissor(j, k, l, i1);
   }

   public static void b() {
      if (abc.isEmpty()) {
         GL11.glDisable(3089);
      } else {
         abc.pop();
         if (abc.isEmpty()) {
            GL11.glDisable(3089);
         } else {
            Rect rect = abc.peek();
            GL11.glScissor(rect.x, rect.y, rect.width, rect.height);
         }
      }
   }
}
