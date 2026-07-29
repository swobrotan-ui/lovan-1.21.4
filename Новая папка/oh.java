import config.GroupScrollState;
import gui.Component;
import gui.InteractiveComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import module.Module;
import org.joml.Matrix4f;
import setting.ColorSetting;
import setting.GroupSetting;
import setting.Setting;

public class oh extends Component {
   private static final float AG = 201.0F;
   private static final float amx = 296.0F;
   private static final float acx = 11.0F;
   private static final int axq = 3;
   private static final float SP = 150.0F;
   private static final float agD = -7.7F;
   private static final float bO = 15.0F;
   private final List<ar> dr = new ArrayList<ar>();
   private final List<Module> hh = new ArrayList<Module>();
   private final Map<String, Float> ZN = new HashMap<String, Float>();
   private final Map<String, Float> AS = new HashMap<String, Float>();
   private final Map<String, Map<String, GroupScrollState>> Dk = new HashMap<String, Map<String, GroupScrollState>>();
   private final cd EQ = new cd();
   private zj Ok;
   private String CH = "";
   private String Wo = "";
   private float akf = 0.0F;
   private float uT = 0.0F;
   public float mn = 1.0F;
   public float Xj = 0.0F;
   public float PW = 0.0F;
   public float apG = 0.0F;
   public float atW = 0.0F;
   private final Matrix4f anD = new Matrix4f();

   public oh(float f, float f1, float f2, float f3) {
      super(f, f1, f2, f3);
      this.EQ.f(0.0F);
   }

   public void a(List<Module> list, String s) {
      this.j();
      this.hh.clear();
      this.hh.addAll(list);
      this.k(s);
      this.b(this.Wo);
      this.f(s);
   }

   public void b(String s) {
      this.Wo = s == null ? "" : s.toLowerCase().trim();
      this.g();
   }

   public Map<String, GroupScrollState> c() {
      HashMap hashmap = new HashMap();

      for (ar ar : this.dr) {
         if (ar.G() && ar.E() != null) {
            mld mld = ar.E();
            String s8 = this.CH;
            String s1 = ar.aqa.getName();
            String s2 = s8;
            String s = s2 + ":" + s1;
            GroupScrollState groupscrollstate = new GroupScrollState(ar.aqa.getName(), mld.acH, mld.aoj);
            hashmap.put(s, groupscrollstate);
         }

         if (ar.K() && ar.I() != null) {
            String s7 = this.CH;
            String s3 = ar.aqa.getName();
            String s4 = s7;
            String s5 = s4 + ":" + s3 + ":colorpicker";
            String s6 = this.d(ar, ar.I());
            if (s6 != null) {
               GroupScrollState groupscrollstate1 = new GroupScrollState(ar.aqa.getName(), s6, 0.0F);
               hashmap.put(s5, groupscrollstate1);
            }
         }
      }

      return hashmap;
   }

   private String d(ar ar, oip oip) {
      for (Setting setting : ar.aqa.getVisibleSettings()) {
         if (setting instanceof ColorSetting && setting.getName().equals(oip.o())) {
            return setting.getName();
         }
      }

      return null;
   }

   public void e(Map<String, GroupScrollState> map) {
      this.Dk.clear();

      for (Entry entry : map.entrySet()) {
         String s = (String)entry.getKey();
         if (s.contains(":")) {
            String s1 = s.substring(0, s.indexOf(":"));
            this.Dk.computeIfAbsent(s1, s2 -> {
               return new HashMap<String, GroupScrollState>();
            }).put(s, (GroupScrollState)entry.getValue());
         }
      }
   }

   private void f(String s) {
      Map map = this.Dk.get(s);
      if (map != null && !map.isEmpty()) {
         for (ar ar : this.dr) {
            String s2 = ar.aqa.getName();
            String s1 = s + ":" + s2;
            GroupScrollState groupscrollstate = (GroupScrollState)map.get(s1);
            if (groupscrollstate != null) {
               for (Setting setting : ar.aqa.getVisibleSettings()) {
                  if (setting instanceof GroupSetting groupsetting && groupsetting.getName().equals(groupscrollstate.b())) {
                     ar.q(groupsetting, groupscrollstate.c());
                     break;
                  }
               }
            }

            String s3 = ar.aqa.getName();
            String s4 = s + ":" + s3 + ":colorpicker";
            GroupScrollState groupscrollstate1 = (GroupScrollState)map.get(s4);
            if (groupscrollstate1 != null) {
               for (Setting setting1 : ar.aqa.getVisibleSettings()) {
                  if (setting1 instanceof ColorSetting colorsetting && colorsetting.getName().equals(groupscrollstate1.b())) {
                     ar.h(colorsetting);
                     break;
                  }
               }
            }
         }
      }
   }

   private void g() {
      for (ar arxx : this.dr) {
         if (arxx.aoj > 0.0F) {
            this.AS.put(arxx.aqa.getName(), arxx.aoj);
         }
      }

      int l = 0;

      for (int i1 = this.dr.size(); l < i1; l++) {
         this.dr.get(l).B();
      }

      this.dr.clear();
      ArrayList arraylist;
      if (this.Wo.isEmpty()) {
         arraylist = new ArrayList<Module>(this.hh);
      } else {
         List list = this.h();
         arraylist = new ArrayList();
         int i = 0;

         for (int j = list.size(); i < j; i++) {
            Module module = (Module)list.get(i);
            if (this.i(module)) {
               arraylist.add(module);
            }
         }
      }

      float[] afloat = new float[3];

      for (Module module1 : arraylist) {
         int j1 = 0;
         float f = afloat[0];

         for (int k = 1; k < 3; k++) {
            if (afloat[k] < f) {
               f = afloat[k];
               j1 = k;
            }
         }

         float f4 = j1 * 296.0F;
         float f1 = afloat[j1];
         ar arx = new ar(f4, f1, module1);
         arx.y(this.Ok);
         this.dr.add(arx);
         afloat[j1] += 212.0F;
      }

      this.l();

      for (ar arx : this.dr) {
         Float f3 = this.AS.get(arx.aqa.getName());
         if (f3 != null && f3 > 0.0F) {
            arx.a(f3);
         }
      }

      float f2 = this.ZN.getOrDefault(this.CH, 0.0F);
      f2 = Math.max(0.0F, Math.min(f2, this.uT));
      this.EQ.f(f2);
      this.EQ.a(f2);
      this.akf = f2;
   }

   private List<Module> h() {
      return (List<Module>)(this.Ok != null ? this.Ok.getAllModules() : new ArrayList<Module>());
   }

   private boolean i(Module module) {
      String s = module.getName().toLowerCase();
      if (s.contains(this.Wo)) {
         return true;
      } else {
         String s1 = module.getDisplayName().toLowerCase();
         if (s1.contains(this.Wo)) {
            return true;
         } else {
            String s2 = module.getDescription().toLowerCase();
            if (s2.contains(this.Wo)) {
               return true;
            } else {
               String s3 = module.getDisplayDescription().toLowerCase();
               if (s3.contains(this.Wo)) {
                  return true;
               } else {
                  List list = module.getVisibleSettings();
                  int i = 0;

                  for (int j = list.size(); i < j; i++) {
                     Setting setting = (Setting)list.get(i);
                     if (setting.getName().toLowerCase().contains(this.Wo) || setting.getDisplayName().toLowerCase().contains(this.Wo)) {
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      }
   }

   private void j() {
      if (!this.CH.isEmpty()) {
         this.ZN.put(this.CH, this.EQ.d());
      }
   }

   private void k(String s) {
      this.CH = s;
      this.l();
   }

   private void l() {
      if (this.dr.isEmpty()) {
         this.uT = 0.0F;
      } else {
         float f = 0.0F;

         for (ar ar : this.dr) {
            float f1 = ar.getY() + ar.getHeight();
            if (f1 > f) {
               f = f1;
            }
         }

         this.uT = Math.max(0.0F, f + 15.0F - this.aem);
      }
   }

   public void m(float f, float f1, float f2) {
      this.mn = f;
      this.Xj = f1;
      this.PW = f2;
   }

   public void n(float f, float f1) {
      this.apG = f;
      this.atW = f1;
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.EQ.c();
      this.akf = this.EQ.d();
      this.o(matrix4f, f, f1, i, j, f2, f3);
      this.p(matrix4f, f, f1, i, j, f2, f3);
      this.x(matrix4f, f, f1, i, j, f2, f3);
      this.z(matrix4f, f, f1, i, j, f2, f3);
   }

   private void o(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      efj.a(0.0F, -7.7F, this.qd, this.aem, this.mn, this.Xj + f * this.mn, this.PW + f1 * this.mn);
      float f4 = this.akf - 201.0F;
      float f5 = this.akf + this.aem + 201.0F;

      for (ar ar : this.dr) {
         if (!ar.G()) {
            float f6 = ar.getY();
            float f7 = f6 + ar.getHeight();
            if (f7 >= f4 && f6 <= f5) {
               this.r(matrix4f, f, f1, ar, i, j, f2, f3);
            }
         }
      }

      efj.b();
   }

   private void p(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      for (ar ar : this.dr) {
         if (ar.G() && ar.E() != null) {
            this.q(matrix4f, f, f1, ar.E(), i, j, f2, f3);
         }
      }
   }

   private void q(Matrix4f matrix4f, float f, float f1, mld mld, int i, int j, float f2, float f3) {
      efj.a(0.0F, 0.0F, this.qd, this.aem, this.mn, this.Xj + f * this.mn, this.PW + f1 * this.mn);
      this.r(matrix4f, f, f1, mld, i, j, f2, f3);
      efj.b();
   }

   private void r(Matrix4f matrix4f, float f, float f1, InteractiveComponent interactivecomponent, int i, int j, float f2, float f3) {
      this.D(interactivecomponent);
      float f4 = this.C(f1, interactivecomponent.getY());
      this.E(interactivecomponent, f, f4);
      interactivecomponent.render(matrix4f, f + interactivecomponent.getX(), f4, i, j, f2, f3);
   }

   public void s(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3, it it) {
      this.EQ.c();
      this.akf = this.EQ.d();
      float f4 = it.m();
      float f5 = it.n();
      if (f4 == 1.0F && f5 == 1.0F) {
         this.render(matrix4f, f, f1, i, j, f2, f3);
      } else {
         this.t(matrix4f, f, f1, i, j, f2, f3, f4, f5);
         this.v(matrix4f, f, f1, it);
         this.u(matrix4f, f, f1, i, j, f2, f3, f4, f5);
      }
   }

   private void t(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3, float f4, float f5) {
      efj.a(0.0F, -7.7F, this.qd, this.aem, this.mn, this.Xj + f * this.mn, this.PW + f1 * this.mn);
      float f6 = this.akf - 201.0F;
      float f7 = this.akf + this.aem + 201.0F;

      for (ar ar : this.dr) {
         if (!ar.G()) {
            float f8 = ar.getY();
            float f9 = f8 + ar.getHeight();
            if (f9 >= f6 && f8 <= f7) {
               this.w(matrix4f, f, f1, ar, i, j, f2, f3 * f5, f4);
            }
         }
      }

      efj.b();
   }

   private void u(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3, float f4, float f5) {
      for (ar ar : this.dr) {
         if (ar.G() && ar.E() != null) {
            efj.a(0.0F, 0.0F, this.qd, this.aem, this.mn, this.Xj + f * this.mn, this.PW + f1 * this.mn);
            mld mld = ar.E();
            this.w(matrix4f, f, f1, mld, i, j, f2, f3 * f5, f4);
            efj.b();
         }
      }
   }

   public void v(Matrix4f matrix4f, float f, float f1, it it) {
      efj.a(0.0F, -7.7F, this.qd, this.aem, this.mn, this.Xj + f * this.mn, this.PW + f1 * this.mn);
      float f2 = this.akf - 201.0F;
      float f3 = this.akf + this.aem + 201.0F;

      for (ar ar : this.dr) {
         if (!ar.G()) {
            float f4 = ar.getY();
            float f5 = f4 + ar.getHeight();
            if (f5 >= f2 && f4 <= f3) {
               float f6 = this.C(f1, ar.getY());
               it.j(matrix4f, f + ar.getX(), f6);
            }
         }
      }

      efj.b();
   }

   private void w(Matrix4f matrix4f, float f, float f1, InteractiveComponent interactivecomponent, int i, int j, float f2, float f3, float f4) {
      this.D(interactivecomponent);
      float f5 = this.C(f1, interactivecomponent.getY());
      Matrix4f matrix4f1 = this.B(matrix4f, f, f5, interactivecomponent, f4);
      this.E(interactivecomponent, f, f5);
      interactivecomponent.render(matrix4f1, f + interactivecomponent.getX(), f5, i, j, f2, f3);
   }

   private void x(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      for (ar ar : this.dr) {
         if (ar.K() && ar.I() != null) {
            this.y(matrix4f, f, f1, ar, ar.I(), i, j, f2, f3);
         }
      }
   }

   private void y(Matrix4f matrix4f, float f, float f1, ar ar, oip oip, int i, int j, float f2, float f3) {
      efj.a(0.0F, 0.0F, this.qd, this.aem, this.mn, this.Xj + f * this.mn, this.PW + f1 * this.mn);
      float f4 = f1 + ar.getY() - this.akf;
      oip.it = f + ar.getX();
      oip.atW = f4;
      oip.render(matrix4f, f + ar.getX(), f4, i, j, f2, f3);
      efj.b();
   }

   private void z(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      for (ar ar : this.dr) {
         if (ar.O() && ar.M() != null) {
            this.A(matrix4f, f, f1, ar, ar.M(), i, j, f2, f3);
         }
      }
   }

   private void A(Matrix4f matrix4f, float f, float f1, ar ar, hc hc, int i, int j, float f2, float f3) {
      efj.a(0.0F, 0.0F, this.qd, this.aem, this.mn, this.Xj + f * this.mn, this.PW + f1 * this.mn);
      float f4 = f1 + ar.getY() - this.akf;
      hc.it = f + ar.getX();
      hc.atW = f4;
      hc.render(matrix4f, f + ar.getX(), f4, i, j, f2, f3);
      efj.b();
   }

   private Matrix4f B(Matrix4f matrix4f, float f, float f1, InteractiveComponent interactivecomponent, float f2) {
      float f3 = f + interactivecomponent.getX() + interactivecomponent.getWidth() / 2.0F;
      float f4 = f1 + interactivecomponent.getHeight() / 2.0F;
      this.anD.set(matrix4f);
      this.anD.translate(f3, f4, 0.0F);
      this.anD.scale(f2, f2, 1.0F);
      this.anD.translate(-f3, -f4, 0.0F);
      return this.anD;
   }

   private float C(float f, float f1) {
      return f + f1 - this.akf;
   }

   private void D(InteractiveComponent interactivecomponent) {
      if (interactivecomponent instanceof ar ar) {
         ar.uQ = this.mn;
         ar.uv = this.Xj;
         ar.Cc = this.PW;
      } else if (interactivecomponent instanceof mld mld) {
         mld.uQ = this.mn;
         mld.uv = this.Xj;
         mld.Cc = this.PW;
      } else {
         if (interactivecomponent instanceof oip oip) {
            ;
         }
      }
   }

   private void E(InteractiveComponent interactivecomponent, float f, float f1) {
      if (interactivecomponent instanceof ar ar) {
         ar.it = f + ar.getX();
         ar.atW = f1;
      } else if (interactivecomponent instanceof mld mld) {
         mld.it = f + mld.getX();
         mld.atW = f1;
      } else {
         if (interactivecomponent instanceof oip oip) {
            oip.it = f + oip.getX();
            oip.atW = f1;
         }
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      for (ar ar : this.dr) {
         if (ar.K() && ar.I() != null) {
            oip oip = ar.I();
            float f = this.atW + ar.getY() - this.akf;
            oip.it = this.apG + ar.getX();
            oip.atW = f;
            if (oip.mouseClicked(d0, d1, i)) {
               return true;
            }
         }

         if (ar.O() && ar.M() != null) {
            hc hc = ar.M();
            float f1 = this.atW + ar.getY() - this.akf;
            hc.it = this.apG + ar.getX();
            hc.atW = f1;
            if (hc.mouseClicked(d0, d1, i)) {
               return true;
            }
         }
      }

      return this.H(ar -> {
         InteractiveComponent interactivecomponent = this.I(arx);
         this.J(interactivecomponent);
         return interactivecomponent.mouseClicked(d0, d1, i);
      });
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0) {
         for (ar ar : this.dr) {
            if (ar.K() && ar.I() != null) {
               ar.I().mouseReleased(d0, d1, i);
            }

            if (ar.O() && ar.M() != null) {
               ar.M().mouseReleased(d0, d1, i);
            }

            InteractiveComponent interactivecomponent = this.I(ar);
            interactivecomponent.mouseReleased(d0, d1, i);
         }

         return true;
      } else {
         return this.H(ar -> {
            return this.I(arx).mouseReleased(d0, d1, i);
         });
      }
   }

   @Override
   protected boolean d(double d0, double d1, int i, double d2, double d3) {
      for (ar ar : this.dr) {
         if (ar.K() && ar.I() != null) {
            oip oip = ar.I();
            float f = this.atW + ar.getY() - this.akf;
            oip.it = this.apG + ar.getX();
            oip.atW = f;
            if (oip.mouseDragged(d0, d1, i, d2, d3)) {
               return true;
            }
         }

         if (ar.O() && ar.M() != null) {
            hc hc = ar.M();
            float f1 = this.atW + ar.getY() - this.akf;
            hc.it = this.apG + ar.getX();
            hc.atW = f1;
            if (hc.mouseDragged(d0, d1, i, d2, d3)) {
               return true;
            }
         }
      }

      return this.H(ar -> {
         return this.I(arx).mouseDragged(d0, d1, i, d2, d3);
      });
   }

   @Override
   protected boolean e(double d0, double d1, double d2, double d3) {
      for (ar ar : this.dr) {
         if (ar.O() && ar.M() != null) {
            hc hc = ar.M();
            if (hc.mouseScrolled(d0, d1, d2, d3)) {
               return true;
            }
         }
      }

      boolean flag = this.H(ar -> {
         InteractiveComponent interactivecomponent = this.I(arx);
         this.J(interactivecomponent);
         return interactivecomponent.mouseScrolled(d0, d1, d2, d3);
      });
      if (!flag) {
         this.F(d3);
         return true;
      } else {
         return true;
      }
   }

   private void F(double d0) {
      float f = this.EQ.d() + (float)(-d0 * 150.0);
      f = Math.max(0.0F, Math.min(f, this.uT));
      this.EQ.a(f);
      this.ZN.put(this.CH, f);
   }

   public void G(double d0) {
      float f = this.EQ.d() + (float)d0;
      f = Math.max(0.0F, Math.min(f, this.uT));
      this.EQ.a(f);
      this.ZN.put(this.CH, f);
   }

   @Override
   protected boolean f(int i, int j, int k) {
      for (ar ar : this.dr) {
         if (ar.O() && ar.M() != null && ar.M().keyPressed(i, j, k)) {
            return true;
         }
      }

      return this.H(ar -> {
         return this.I(arx).keyPressed(i, j, k);
      });
   }

   @Override
   protected boolean g(int i, int j, int k) {
      return this.H(ar -> {
         return this.I(ar).keyReleased(i, j, k);
      });
   }

   @Override
   protected boolean h(char c0, int i) {
      for (ar ar : this.dr) {
         if (ar.O() && ar.M() != null && ar.M().charTyped(c0, i)) {
            return true;
         }
      }

      return this.H(ar -> {
         return this.I(arx).charTyped(c0, i);
      });
   }

   @Override
   protected void i(float f) {
      this.EQ.c();
      this.akf = this.EQ.d();
      float f1 = this.akf - 201.0F;
      float f2 = this.akf + this.aem + 201.0F;

      for (ar ar : this.dr) {
         float f3 = ar.getY();
         float f4 = f3 + ar.getHeight();
         if (ar.G() || ar.K()) {
            this.I(ar).tick(f);
         } else if (f4 >= f1 && f3 <= f2) {
            this.I(ar).tick(f);
         }
      }
   }

   private boolean H(ru ru) {
      for (ar ar : this.dr) {
         if (ru.process(ar)) {
            return true;
         }
      }

      return false;
   }

   private InteractiveComponent I(ar ar) {
      if (ar.K() && ar.I() != null) {
         return ar.I();
      } else {
         return (InteractiveComponent)(ar.G() && ar.E() != null ? ar.E() : ar);
      }
   }

   private void J(InteractiveComponent interactivecomponent) {
      if (interactivecomponent instanceof ar ar) {
         ar.atW = this.atW + ar.getY() - this.akf;
      } else {
         if (interactivecomponent instanceof mld mld) {
            mld.atW = this.atW + mld.getY() - this.akf;
         }
      }
   }

   public Map<String, Float> K() {
      this.j();
      return new HashMap<String, Float>(this.ZN);
   }

   public void L(Map<String, Float> map) {
      if (map != null && !map.isEmpty()) {
         this.ZN.clear();
         this.ZN.putAll(map);
      }
   }

   public Map<String, Float> M() {
      for (ar ar : this.dr) {
         if (ar.aoj > 0.0F) {
            this.AS.put(ar.aqa.getName(), ar.aoj);
         } else {
            this.AS.remove(ar.aqa.getName());
         }
      }

      return new HashMap<String, Float>(this.AS);
   }

   public void N(Map<String, Float> map) {
      if (map != null && !map.isEmpty()) {
         this.AS.clear();
         this.AS.putAll(map);
      }
   }

   public void O() {
      this.EQ.a(0.0F);
      this.ZN.put(this.CH, 0.0F);
   }

   public String P(double d0, double d1) {
      for (ar arxx : this.dr) {
         InteractiveComponent interactivecomponent = this.I(arxx);
         if (interactivecomponent instanceof ar arx) {
            arx.atW = this.atW + arx.getY() - this.akf;
         } else if (interactivecomponent instanceof mld mldx) {
            mldx.atW = this.atW + mldx.getY() - this.akf;
         }

         if (interactivecomponent.isHovered(d0, d1)) {
            if (interactivecomponent instanceof ar arx) {
               return arx.A(d0, d1);
            }

            if (interactivecomponent instanceof mld mld) {
               return mld.i(d0, d1);
            }
         }
      }

      return null;
   }

   public List<ar> Q() {
      return this.dr;
   }

   public void R(zj zj) {
      this.Ok = zj;
   }
}
