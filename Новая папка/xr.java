import gui.InteractiveComponent;
import java.util.List;

public class xr {
   public static boolean a(List<? extends InteractiveComponent> list, double d0, double d1, int i) {
      for (int j = list.size() - 1; j >= 0; j--) {
         InteractiveComponent interactivecomponent = (InteractiveComponent)list.get(j);
         if (interactivecomponent.mouseClicked(d0, d1, i)) {
            return true;
         }
      }

      return false;
   }

   public static boolean b(List<? extends InteractiveComponent> list, double d0, double d1, int i) {
      for (InteractiveComponent interactivecomponent : list) {
         if (interactivecomponent.mouseReleased(d0, d1, i)) {
            return true;
         }
      }

      return false;
   }

   public static boolean c(List<? extends InteractiveComponent> list, double d0, double d1, int i, double d2, double d3) {
      for (InteractiveComponent interactivecomponent : list) {
         if (interactivecomponent.mouseDragged(d0, d1, i, d2, d3)) {
            return true;
         }
      }

      return false;
   }

   public static boolean d(List<? extends InteractiveComponent> list, double d0, double d1, double d2, double d3) {
      for (InteractiveComponent interactivecomponent : list) {
         if (interactivecomponent.mouseScrolled(d0, d1, d2, d3)) {
            return true;
         }
      }

      return false;
   }

   public static boolean e(List<? extends InteractiveComponent> list, int i, int j, int k) {
      for (InteractiveComponent interactivecomponent : list) {
         if (interactivecomponent.keyPressed(i, j, k)) {
            return true;
         }
      }

      return false;
   }

   public static boolean f(List<? extends InteractiveComponent> list, int i, int j, int k) {
      for (InteractiveComponent interactivecomponent : list) {
         if (interactivecomponent.keyReleased(i, j, k)) {
            return true;
         }
      }

      return false;
   }

   public static boolean g(List<? extends InteractiveComponent> list, char c0, int i) {
      for (InteractiveComponent interactivecomponent : list) {
         if (interactivecomponent.charTyped(c0, i)) {
            return true;
         }
      }

      return false;
   }

   public static void h(List<? extends InteractiveComponent> list, float f) {
      for (InteractiveComponent interactivecomponent : list) {
         interactivecomponent.tick(f);
      }
   }
}
