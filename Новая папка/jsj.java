import enum.Category;
import font.MSDFFont;
import gui.InteractiveComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class jsj {
   private static final float XZ = 30.0F;
   private static final float RV = 1100.0F;
   private static final float D = 15.0F;
   private static final float eP = 71.0F;
   private static final float qe = 122.0F;
   private static final float akd = 38.0F;
   private final Supplier<MSDFFont> ks;

   public jsj(Supplier<MSDFFont> supplier) {
      this.ks = supplier;
   }

   public List<InteractiveComponent> a(Runnable runnable, Runnable runnable1, Runnable runnable2) {
      ArrayList arraylist = new ArrayList();
      float f = 1060.0F;
      float f1 = 1025.0F;
      float f2 = 990.0F;
      float f3 = 15.0F;
      arraylist.add(new fty(f, f3, runnable));
      arraylist.add(new mh(f1, f3, runnable1));
      arraylist.add(new a(f2, f3, runnable2));
      return arraylist;
   }

   public List<InteractiveComponent> b() {
      ArrayList arraylist = new ArrayList();
      arraylist.add(new gt(210.0F, 15.0F));
      arraylist.add(new m(10.0F, 10.0F));
      return arraylist;
   }

   public List<q> c(Consumer<String> consumer) {
      ArrayList arraylist = new ArrayList();
      q qx = this.d(71.0F, "All", "V", true, false, () -> {
         consumer.accept("All");
      });
      arraylist.add(qx);
      float f = 122.0F;
      float f1 = 122.0F;

      for (Category category : Category.values()) {
         boolean flag = category.isSubCategory();
         if (flag) {
            f = f1 + 30.0F + 5.0F;
         }

         String s = category.getDisplayName();
         q qx = this.d(f, s, category.getIcon(), false, flag, () -> {
            consumer.accept(s);
         });
         arraylist.add(qx);
         f1 = f;
         f += 38.0F;
      }

      float f2 = f1 + 30.0F + 20.0F;
      arraylist.add(this.d(f2, "Friends", "S", false, false, () -> {
         consumer.accept("Friends");
      }));
      f2 += 38.0F;
      arraylist.add(this.d(f2, "Configs", "T", false, false, () -> {
         consumer.accept("Configs");
      }));
      return arraylist;
   }

   private q d(float f, String s, String s1, boolean flag, boolean flag1, Runnable runnable) {
      float f1 = flag1 ? 48.0F : 15.0F;
      q q = new q(f1, f, this.ks, s, s1, runnable, flag1);
      q.b(flag);
      return q;
   }
}
