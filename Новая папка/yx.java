import core.FriendManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;

public class yx extends jb<ps> {
   private boolean yv = true;
   private final Map<ps, Float> asa = new HashMap<ps, Float>();
   private final Map<ps, Float> FM = new HashMap<ps, Float>();
   private final Map<ps, Float> wQ = new HashMap<ps, Float>();
   private final Map<ps, Float> le = new HashMap<ps, Float>();
   private static final long vV = 1000000000L;
   private long rH = System.nanoTime();
   private static final float zl = 6.0F;

   public yx(float f, float f1, float f2, float f3) {
      super(f, f1, f2, f3);
      this.a();
   }

   public void a() {
      this.b(this.yv);
      this.yv = false;
   }

   private void b(boolean flag) {
      Map map = this.c();
      Set set = FriendManager.getInstance().getFriends();
      List list = this.d(set, map);
      this.zy.removeIf(ps -> {
         boolean flag1 = psxxxx.b();
         if (flag1) {
            this.asa.remove(psxxxx);
            this.FM.remove(psxxxx);
            this.wQ.remove(psxxxx);
            this.le.remove(psxxxx);
         }

         return flag1;
      });
      HashMap hashmap = new HashMap();
      HashMap hashmap1 = new HashMap();
      ArrayList arraylist = new ArrayList();

      for (ps psxxx : this.zy) {
         if (psxxx.k()) {
            arraylist.add(psxxx);
            hashmap1.put(psxxx.h(), psxxx);
         } else {
            hashmap.put(psxxx.h(), psxxx);
         }
      }

      ArrayList arraylist1 = new ArrayList();
      int i = 0;

      for (String s : list) {
         if (!hashmap1.containsKey(s)) {
            float[] afloat = il.a(i++, this.fh, this.pZ, this.Ip, this.apX, this.HC);
            ps psx = (ps)hashmap.get(s);
            if (psx != null) {
               arraylist1.add(psx);
               this.asa.put(psx, afloat[0]);
               this.FM.put(psx, afloat[1]);
               if (!this.wQ.containsKey(psx)) {
                  this.wQ.put(psx, psx.getX());
                  this.le.put(psx, psx.getY());
               }
            } else {
               ps psxx = new ps(afloat[0], afloat[1], s, this::e, flag);
               arraylist1.add(psxx);
               this.asa.put(psxx, afloat[0]);
               this.FM.put(psxx, afloat[1]);
               this.wQ.put(psxx, afloat[0]);
               this.le.put(psxx, afloat[1]);
            }
         }
      }

      for (ps psx : arraylist) {
         arraylist1.add(psx);
         if (!this.asa.containsKey(psx)) {
            this.asa.put(psx, psx.getX());
            this.FM.put(psx, psx.getY());
         }

         if (!this.wQ.containsKey(psx)) {
            this.wQ.put(psx, psx.getX());
            this.le.put(psx, psx.getY());
         }
      }

      this.zy.clear();
      this.zy.addAll(arraylist1);
      this.g();
   }

   private Map<String, Integer> c() {
      HashMap hashmap = new HashMap();
      int i = 0;

      for (ps ps : this.zy) {
         if (!ps.k()) {
            hashmap.put(ps.h(), i++);
         }
      }

      return hashmap;
   }

   private List<String> d(Set<String> set, Map<String, Integer> map) {
      ArrayList arraylist = new ArrayList(set);
      arraylist.sort((s, s1) -> {
         Integer integer = (Integer)map.get(s);
         Integer integer1 = (Integer)map.get(s1);
         if (integer != null && integer1 != null) {
            return Integer.compare(integer, integer1);
         } else if (integer != null) {
            return -1;
         } else {
            return integer1 != null ? 1 : s.compareTo(s1);
         }
      });
      return arraylist;
   }

   public void e() {
      this.b(true);
   }

   public void f(String s) {
      if (s != null && !s.trim().isEmpty()) {
         new Thread(() -> {
            FriendManager.getInstance().addFriend(s.trim());
            MinecraftClient.getInstance().execute(this::e);
         }, "Friend-Add-Thread").start();
      }
   }

   @Override
   protected void b(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.g(f2);
      if (this.NQ != null) {
         this.NQ.onAfterCardsRender(matrix4f, f, f1, i, j, f2, f3);
      }

      efj.a(0.0F, 0.0F, this.qd, this.aem, this.KL, this.aiE + f * this.KL, this.RT + f1 * this.KL);

      for (ps ps : new ArrayList<ps>(this.zy)) {
         Float f4 = this.wQ.get(ps);
         Float f5 = this.le.get(ps);
         float f6 = f4 != null ? f4 : ps.getX();
         float f7 = (f5 != null ? f5 : ps.getY()) - this.UA;
         if (il.c(f1 + f7, this.Ip, f1, this.aem)) {
            ps.it = f + f6;
            ps.atW = f1 + f7;
            float f8 = f3;
            if (ps.k()) {
               f8 = f3 * ps.l();
            }

            if (this.fk != null) {
               this.fk.render(ps, matrix4f, f + f6, f1 + f7, i, j, f2, f8);
            } else {
               ps.render(matrix4f, f + f6, f1 + f7, i, j, f2, f8);
            }
         }
      }

      efj.b();
      if (this.rZ != null) {
         this.rZ.onAfterCardsRender(matrix4f, f, f1, i, j, f2, f3);
      }
   }

   private void g(float f7) {
      long i = System.nanoTime();
      float f = (float)(i - this.rH) / 1.0E9F;
      this.rH = i;
      f = Math.min(f, 0.05F);

      for (ps ps : new ArrayList<ps>(this.zy)) {
         Float f1 = this.asa.get(ps);
         Float f2 = this.FM.get(ps);
         if (f1 != null && f2 != null) {
            float f3 = this.wQ.getOrDefault(ps, ps.getX());
            float f4 = this.le.getOrDefault(ps, ps.getY());
            float f5 = f1 - f3;
            float f6 = f2 - f4;
            if (!(Math.abs(f5) > 0.5F) && !(Math.abs(f6) > 0.5F)) {
               ps.aP = f1;
               ps.hn = f2;
               this.wQ.put(ps, f1);
               this.le.put(ps, f2);
            } else {
               f3 += f5 * f * 6.0F;
               f4 += f6 * f * 6.0F;
               this.wQ.put(ps, f3);
               this.le.put(ps, f4);
            }
         }
      }
   }

   @Override
   public boolean f(int i, int j, int k) {
      if (i == 259) {
         for (ps psx : this.zy) {
            if (psx.f()) {
               return true;
            }
         }
      }

      for (ps psx : this.zy) {
         if (psx.f(i, j, k)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean h(char c0, int i) {
      for (ps ps : this.zy) {
         if (ps.h(c0, i)) {
            return true;
         }
      }

      return false;
   }

   public boolean h() {
      for (ps ps : this.zy) {
         if (!ps.k()) {
            return false;
         }
      }

      return true;
   }

   public void i(String s) {
      if (s != null && !s.trim().isEmpty()) {
         String s2 = s.toLowerCase();

         for (ps ps : this.zy) {
            String s1 = ps.h().toLowerCase();
            boolean flag = s1.contains(s2);
            ps.setVisible(flag);
         }
      } else {
         for (ps psx : this.zy) {
            psx.setVisible(true);
         }
      }
   }
}
