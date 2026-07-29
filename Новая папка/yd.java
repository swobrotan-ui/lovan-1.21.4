import core.ClientMain;
import core.ConfigManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;

public class yd extends jb<ct> {
   private final ConfigManager LP;
   private boolean auM = true;
   private boolean amS = false;
   private hck b;
   private final Map<ct, Float> ahV = new HashMap<ct, Float>();
   private final Map<ct, Float> ON = new HashMap<ct, Float>();
   private final Map<ct, Float> Wy = new HashMap<ct, Float>();
   private final Map<ct, Float> aag = new HashMap<ct, Float>();
   private static final long ya = 1000000000L;
   private long wD = System.nanoTime();
   private static final float RI = 6.0F;

   public yd(float f, float f1, float f2, float f3) {
      super(f, f1, f2, f3);
      this.LP = ClientMain.getInstance().getConfigManager();
      this.pZ = 286.0F;
      this.Ip = 130.0F;
      this.fh = 3;
      this.apX = 10.0F;
      this.HC = 11.0F;
      this.a();
   }

   public void a() {
      this.b(this.auM);
   }

   private void b(boolean flag) {
      Map map = this.d();
      if (!this.amS) {
         this.amS = true;
         if (this.LP.q()) {
            this.c(true, map);
         }

         new Thread(() -> {
            try {
               this.LP.p();
               MinecraftClient.getInstance().execute(() -> {
                  Map map1 = this.d();
                  this.c(flag, map1);
               });
            } catch (Exception exception) {
            }
         }, "L").start();
      } else {
         this.c(flag, map);
      }
   }

   private void c(boolean flag, Map<String, Integer> map) {
      List list = this.LP.y();
      List list1 = this.e(list, map);
      this.zy.removeIf(ct -> {
         if (ctx instanceof sdc sdcxxxx) {
            boolean flag1 = sdcxxxx.b();
            if (flag1) {
               this.ahV.remove(ctx);
               this.ON.remove(ctx);
               this.Wy.remove(ctx);
               this.aag.remove(ctx);
            }

            return flag1;
         } else {
            return false;
         }
      });
      HashMap hashmap = new HashMap();
      HashMap hashmap1 = new HashMap();
      ArrayList arraylist = new ArrayList();

      for (ct ct : this.zy) {
         if (ct instanceof sdc sdcxxx) {
            if (sdcxxx.A()) {
               arraylist.add(sdcxxx);
               hashmap1.put(sdcxxx.u(), sdcxxx);
            } else {
               hashmap.put(sdcxxx.u(), sdcxxx);
            }
         }
      }

      ArrayList arraylist1 = new ArrayList();
      int i = 0;

      for (so so : list1) {
         if (!hashmap1.containsKey(so.a())) {
            float[] afloat = il.a(i++, this.fh, this.pZ, this.Ip, this.apX, this.HC);
            sdc sdcx = (sdc)hashmap.get(so.a());
            if (sdcx != null) {
               arraylist1.add(sdcx);
               this.ahV.put(sdcx, afloat[0]);
               this.ON.put(sdcx, afloat[1]);
               if (!this.Wy.containsKey(sdcx)) {
                  this.Wy.put(sdcx, sdcx.getX());
                  this.aag.put(sdcx, sdcx.getY());
               }
            } else {
               sdc sdcxx = new sdc(afloat[0], afloat[1], so, flag);
               sdcxx.w(this::k);
               arraylist1.add(sdcxx);
               this.ahV.put(sdcxx, afloat[0]);
               this.ON.put(sdcxx, afloat[1]);
               this.Wy.put(sdcxx, afloat[0]);
               this.aag.put(sdcxx, afloat[1]);
            }
         }
      }

      for (sdc sdcx : arraylist) {
         arraylist1.add(sdcx);
         if (!this.ahV.containsKey(sdcx)) {
            this.ahV.put(sdcx, sdcx.getX());
            this.ON.put(sdcx, sdcx.getY());
         }

         if (!this.Wy.containsKey(sdcx)) {
            this.Wy.put(sdcx, sdcx.getX());
            this.aag.put(sdcx, sdcx.getY());
         }
      }

      float[] afloat1 = il.a(i, this.fh, this.pZ, this.Ip, this.apX, this.HC);
      if (this.b == null || !this.zy.contains(this.b)) {
         this.b = new hck(afloat1[0], afloat1[1], this::f, flag);
         this.Wy.put(this.b, afloat1[0]);
         this.aag.put(this.b, afloat1[1]);
      }

      arraylist1.add(this.b);
      this.ahV.put(this.b, afloat1[0]);
      this.ON.put(this.b, afloat1[1]);
      if (!this.Wy.containsKey(this.b)) {
         this.Wy.put(this.b, this.b.getX());
         this.aag.put(this.b, this.b.getY());
      }

      this.zy.clear();
      this.zy.addAll(arraylist1);
      this.auM = false;
      this.g();
   }

   private Map<String, Integer> d() {
      HashMap hashmap = new HashMap();
      int i = 0;

      for (ct ct : this.zy) {
         if (ct instanceof sdc sdc) {
            hashmap.put(sdc.u(), i++);
         }
      }

      return hashmap;
   }

   private List<so> e(List<so> list, Map<String, Integer> map) {
      ArrayList arraylist = new ArrayList(list);
      arraylist.sort((so, so) -> {
         Integer integer = (Integer)map.get(sox.a());
         Integer integer1 = (Integer)map.get(so.a());
         if (integer != null && integer1 != null) {
            return Integer.compare(integer, integer1);
         } else if (integer != null) {
            return -1;
         } else {
            return integer1 != null ? 1 : sox.a().compareTo(so.a());
         }
      });
      return arraylist;
   }

   private void f() {
      int i = this.g();
      int j = i + 1;
      String s = "Config " + j;
      String s1 = "Новая конфигурация";
      so so = new so();
      so.g(s);
      so.h(s1);
      so.j(false);
      sdc sdc = this.h(so);
      sdc.C(true);
      new Thread(() -> {
         try {
            if (i > 0) {
               this.LP.t();
            }

            boolean flag = this.LP.h(s, s1);
            if (flag) {
               this.LP.j(s);
               this.LP.m(s);
               MinecraftClient.getInstance().execute(() -> {
                  sdc.C(false);
               });
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }, "C").start();
   }

   private int g() {
      int i = 0;

      for (ct ct : this.zy) {
         if (ct instanceof sdc sdc && !sdc.A()) {
            i++;
         }
      }

      return i;
   }

   private sdc h(so so) {
      int i = this.g();
      float[] afloat = il.a(i, this.fh, this.pZ, this.Ip, this.apX, this.HC);
      sdc sdc = new sdc(afloat[0], afloat[1], so, false);
      sdc.w(this::k);
      int j = this.zy.size() - 1;
      if (j < 0) {
         j = 0;
      }

      this.zy.add(j, sdc);
      this.ahV.put(sdc, afloat[0]);
      this.ON.put(sdc, afloat[1]);
      this.Wy.put(sdc, afloat[0]);
      this.aag.put(sdc, afloat[1]);
      float[] afloat1 = il.a(i + 1, this.fh, this.pZ, this.Ip, this.apX, this.HC);
      this.ahV.put(this.b, afloat1[0]);
      this.ON.put(this.b, afloat1[1]);
      this.g();
      return sdc;
   }

   @Override
   protected void b(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.j(f2);
      if (this.NQ != null) {
         this.NQ.onAfterCardsRender(matrix4f, f, f1, i, j, f2, f3);
      }

      efj.a(0.0F, 0.0F, this.qd, this.aem, this.KL, this.aiE + f * this.KL, this.RT + f1 * this.KL);

      for (ct ct : new ArrayList<ct>(this.zy)) {
         Float f4 = this.Wy.get(ct);
         Float f5 = this.aag.get(ct);
         float f6 = f4 != null ? f4 : ct.getX();
         float f7 = (f5 != null ? f5 : ct.getY()) - this.UA;
         if (il.c(f1 + f7, this.Ip, f1, this.aem)) {
            ct.it = f + f6;
            ct.atW = f1 + f7;
            float f8 = f3;
            if (ct instanceof sdc sdc && sdc.A()) {
               f8 = f3 * sdc.x();
            }

            if (this.fk != null) {
               this.fk.render(ct, matrix4f, f + f6, f1 + f7, i, j, f2, f8);
            } else {
               ct.render(matrix4f, f + f6, f1 + f7, i, j, f2, f8);
            }
         }
      }

      efj.b();
      if (this.rZ != null) {
         this.rZ.onAfterCardsRender(matrix4f, f, f1, i, j, f2, f3);
      }
   }

   private void j(float f7) {
      long i = System.nanoTime();
      float f = (float)(i - this.wD) / 1.0E9F;
      this.wD = i;
      f = Math.min(f, 0.05F);

      for (ct ct : new ArrayList<ct>(this.zy)) {
         Float f1 = this.ahV.get(ct);
         Float f2 = this.ON.get(ct);
         if (f1 != null && f2 != null) {
            float f3 = this.Wy.getOrDefault(ct, ct.getX());
            float f4 = this.aag.getOrDefault(ct, ct.getY());
            float f5 = f1 - f3;
            float f6 = f2 - f4;
            if (!(Math.abs(f5) > 0.5F) && !(Math.abs(f6) > 0.5F)) {
               ct.aP = f1;
               ct.hn = f2;
               this.Wy.put(ct, f1);
               this.aag.put(ct, f2);
            } else {
               f3 += f5 * f * 6.0F;
               f4 += f6 * f * 6.0F;
               this.Wy.put(ct, f3);
               this.aag.put(ct, f4);
            }
         }
      }
   }

   public void k() {
      this.b(true);
   }

   @Override
   public boolean f(int i, int j, int k) {
      if (i == 259) {
         for (ct ctx : this.zy) {
            if (ctx instanceof sdc sdcx && sdcx.n()) {
               return true;
            }
         }
      }

      for (ct ctx : this.zy) {
         if (ctx instanceof sdc sdc && sdc.f(i, j, k)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean h(char c0, int i) {
      for (ct ct : this.zy) {
         if (ct instanceof sdc sdc && sdc.h(c0, i)) {
            return true;
         }
      }

      return false;
   }

   public boolean l() {
      return this.zy.size() <= 1;
   }

   public void m(String s) {
      if (s != null && !s.trim().isEmpty()) {
         String s3 = s.toLowerCase();

         for (ct ct : this.zy) {
            if (ct instanceof sdc sdc) {
               String s1 = sdc.u().toLowerCase();
               String s2 = sdc.v().toLowerCase();
               boolean flag = s1.contains(s3) || s2.contains(s3);
               ct.setVisible(flag);
            } else if (ct instanceof hck) {
               ct.setVisible(true);
            }
         }
      } else {
         for (ct ctx : this.zy) {
            ctx.setVisible(true);
         }
      }
   }

   public void n(String s) {
      if (s != null && !s.trim().isEmpty()) {
         new Thread(() -> {
            try {
               String s2 = this.LP.o(s.trim(), null);
               if (s2 != null && !s2.isEmpty()) {
                  this.LP.t();
                  this.LP.j(s2);
                  this.LP.m(s2);
                  MinecraftClient.getInstance().execute(this::k);
               }
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }, "I").start();
      }
   }
}
