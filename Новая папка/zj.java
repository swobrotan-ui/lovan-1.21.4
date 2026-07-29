import com.google.common.base.Suppliers;
import config.Config;
import core.ClientMain;
import core.ConfigManager;
import core.FriendManager;
import core.SoundManager;
import enum.SoundType;
import font.MSDFFont;
import gui.InteractiveComponent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import module.ClickGuiModule;
import module.Module;
import module.PanicModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import render.BuiltBlur;
import render.BuiltLine2d;
import render.BuiltLiquidGlass;
import render.BuiltRectangle;
import render.BuiltText;
import render.Size;
import render.TextCache;

public class zj extends Screen {
   private int WW = 1100;
   private int pe = 600;
   private float Hu = 211.0F;
   private float SF = 70.0F;
   private float dM = 200.0F;
   private static final Supplier<MSDFFont> cv = Suppliers.memoize(() -> {
      return MSDFFont.g().b("b").c("b").e();
   });
   private static final Supplier<MSDFFont> atv = Suppliers.memoize(() -> {
      return MSDFFont.g().b("a").c("a").e();
   });
   private final ConfigManager dh;
   private final v od;
   private final srr ahP;
   private final jh Tm;
   private final bze axm;
   private final mu arG;
   private final im amo;
   private final jsj ahX;
   private final List<InteractiveComponent> u = new ArrayList<InteractiveComponent>();
   private gt bN;
   private mi Xw;
   private BuiltRectangle Sg;
   private BuiltLine2d auW;
   private BuiltLine2d Ei;
   private BuiltRectangle ra;
   private BuiltBlur yk;
   private int CC = -1;
   private int vv = -1;
   private boolean Wr = true;
   private float Vd;
   private float azW;
   private float cA;
   private float ayw = 0.0F;
   private long iR = 0L;
   private boolean Xp = false;
   private boolean ams = false;
   private int Wz = 0;
   private boolean ua = false;
   private final double[] PV = new double[2];
   private int rk = -1;
   private int wj = -1;

   public zj() {
      super(Text.literal("GUI Screen"));
      this.dh = ClientMain.getInstance().getConfigManager();
      this.od = new v(atv.get());
      this.ahP = new srr();
      this.Tm = new jh();
      this.axm = new bze();
      this.arG = new mu();
      this.ahX = new jsj(cv);
      float f = this.WW - this.Hu;
      float f1 = this.pe - this.SF + 6.0F;
      oh oh = new oh(this.Hu, this.SF, f, f1);
      yx yx = new yx(this.Hu, this.SF, f, f1);
      yd yd = new yd(this.Hu, this.SF, f, f1);
      this.amo = new im(oh, yx, yd);
      this.initializeUI();
      this.restoreGuiState();
      this.setupCallbacks();
   }

   private void setupCallbacks() {
      this.ahP.m(s -> {
         this.amo.a(s);
         this.updateModuleGrid();
         this.saveGuiState();
         if (this.bN != null) {
            this.bN.s();
         }

         if (this.bN != null) {
            boolean flag = "Friends".equals(s) || "Configs".equals(s);
            this.bN.a(flag);
         }

         if (this.Xw != null) {
            if ("Friends".equals(s)) {
               this.Xw.a(ws.tg);
               return;
            }

            if ("Configs".equals(s)) {
               this.Xw.a(ws.ahz);
            }
         }
      });
      this.ahP.n(() -> {
         this.amo.p().O();
      });
      this.Tm.g(() -> {
         if (this.ahP.l().equals("Favourites")) {
            this.updateModuleGrid();
         }
      });
      this.axm.c(() -> {
         if (this.ahP.l().equals("Friends")) {
            this.amo.q().a();
         }
      });
      this.arG.d(() -> {
         if (this.ahP.l().equals("Configs")) {
            this.amo.r().k();
         }
      });
      if (this.Xw != null) {
         this.Xw.s(s -> {
            String s1 = this.ahP.l();
            if ("Friends".equals(s1)) {
               this.handleAddFriend(s);
            } else {
               if ("Configs".equals(s1)) {
                  this.handleAddConfig(s);
               }
            }
         });
      }
   }

   private void restoreGuiState() {
      Config config = this.dh.x();
      if (config != null) {
         String s = config.f();
         if (s != null && !s.isEmpty()) {
            this.ahP.i(s);
            this.amo.a(s);
            if (this.bN != null) {
               boolean flag = "Friends".equals(s) || "Configs".equals(s);
               this.bN.b(flag, true);
            }

            if (this.Xw != null) {
               if ("Friends".equals(s)) {
                  this.Xw.b(ws.tg, true);
                  this.Xw.c(true);
               } else if ("Configs".equals(s)) {
                  this.Xw.b(ws.ahz, true);
                  this.Xw.c(true);
               } else {
                  this.Xw.c(false);
               }
            }
         }

         Map map2 = config.g();
         if (map2 != null) {
            this.amo.p().L(map2);
         }

         Map map = config.h();
         if (map != null && !map.isEmpty()) {
            this.amo.p().e(map);
         }

         Map map1 = config.i();
         if (map1 != null && !map1.isEmpty()) {
            this.amo.p().N(map1);
         }
      }
   }

   private void saveGuiState() {
      Config config = this.dh.x();
      PanicModule panicmodule = ClientMain.getInstance().getModuleManager().<PanicModule>getModule(PanicModule.class);
      if (config != null) {
         if (panicmodule != null) {
            if (!panicmodule.c()) {
               if (!ClientMain.getInstance().getConfigSyncManager().k()) {
                  this.updateConfigWithGuiState(config);
                  this.dh.w(config);
                  ClientMain.getInstance().getConfigSyncManager().d(config);
               }
            }
         }
      }
   }

   public void updateConfigWithGuiState(Config config) {
      if (config != null) {
         config.s(this.ahP.l());
         config.t(this.amo.p().K());
         config.u(this.amo.p().c());
         config.v(this.amo.p().M());
      }
   }

   private void initializeUI() {
      this.buildUIElements();
      this.buildStaticComponents();
      this.buildCategoryComponents();
      this.setupSearchCallback();
   }

   private void buildUIElements() {
      this.Sg = new br().a(this.WW, this.pe).a();
      this.auW = new otj().a(this.WW).b(1.0F).d().j(0.2F).a();
      this.Ei = new otj().a(this.pe).b(1.0F).c().j(0.2F).a();
   }

   private void buildStaticComponents() {
      float f = 600.0F;
      float f1 = 15.0F;
      this.Xw = new mi(f, f1);
      this.Xw.c(false);
      this.u.add(this.Xw);
      List list = this.ahX.b();
      this.u.addAll(list);
      list.stream().filter(interactivecomponent -> {
         return interactivecomponent instanceof gt;
      }).findFirst().ifPresent(interactivecomponent -> {
         this.bN = (gt)interactivecomponent;
      });
      this.u.addAll(this.ahX.a(() -> {
         this.ahP.c("Favourites");
      }, () -> {
         this.ahP.c("Friends");
      }, () -> {
         this.ahP.c("Configs");
      }));
      float f2 = 47.0F;
      float f3 = 30.0F;
      float f4 = 15.0F;
      float f5 = this.pe - f3 - 15.0F;
      this.u.add(new jec(f4, f5, f2, f3));
   }

   private void buildCategoryComponents() {
      for (q q : this.ahX.c(this.ahP::c)) {
         this.ahP.b(q);
         this.u.add(q);
      }
   }

   private void setupSearchCallback() {
      this.u.stream().filter(interactivecomponent -> {
         return interactivecomponent instanceof gt;
      }).findFirst().ifPresent(interactivecomponent -> {
         gt gt = (gt)interactivecomponent;
         gt.u((s, s1) -> {
            String s2 = this.ahP.l();
            if ("Friends".equals(s2)) {
               this.amo.q().i(s);
            } else if ("Configs".equals(s2)) {
               this.amo.r().m(s);
            } else {
               this.amo.p().b(s1);
            }
         });
      });
   }

   private void updateModuleGrid() {
      List list = this.getModulesForCurrentCategory();
      this.amo.p().a(list, this.ahP.l());
   }

   private List<Module> getModulesForCurrentCategory() {
      String s = this.ahP.l();
      if ("Favourites".equals(s)) {
         return this.Tm.f(this.ahP.h());
      } else if ("Friends".equals(s)) {
         return Collections.<Module>emptyList();
      } else {
         return "Configs".equals(s) ? Collections.<Module>emptyList() : this.ahP.g(s);
      }
   }

   public List<Module> getAllModules() {
      return this.ahP.h();
   }

   public void toggleModuleFavourite(String s) {
      this.Tm.c(s);
   }

   public boolean isModuleFavourite(String s) {
      return this.Tm.d(s);
   }

   private void handleAddFriend(String s) {
      FriendManager.getInstance().addFriend(s);
      this.amo.q().a();
   }

   private void handleAddConfig(String s) {
      this.amo.r().n(s);
   }

   protected void init() {
      super.init();
      SoundManager.getInstance().setMuted(true);
      this.iR = 0L;
      this.ayw = 0.0F;
      this.Xp = false;
      this.ams = true;
      this.Wz = 2;
      SoundManager.getInstance().setMuted(false);
      SoundManager.getInstance().c(SoundType.GUI_OPEN);
      this.amo.p().R(this);
      this.updateModuleGrid();
   }

   public void render(DrawContext drawcontext, int i, int j, float f) {
      if (this.Wz > 0) {
         this.Wz--;
         if (this.Wz == 0) {
            this.iR = System.currentTimeMillis();
         }
      } else {
         BuiltLiquidGlass.c();
         this.calculateScaling();
         this.updateGuiAlpha();
         if (!(this.ayw < 0.01F)) {
            MinecraftClient minecraftclient = MinecraftClient.getInstance();
            int k = minecraftclient.getWindow().getScaledWidth();
            int l = minecraftclient.getWindow().getScaledHeight();
            Matrix4f matrix4f = drawcontext.getMatrices().peek().getPositionMatrix();
            this.renderBackgroundEffects(matrix4f, k, l);
            this.enableScissor(minecraftclient);
            drawcontext.getMatrices().push();
            this.applyGuiTransform(drawcontext);
            Matrix4f matrix4f1 = drawcontext.getMatrices().peek().getPositionMatrix();
            double[] adouble = this.scaleMousePosition(i, j);
            this.amo.h(this.cA, this.Vd, this.azW);
            this.renderBackground(matrix4f1);
            this.renderComponents(matrix4f1, adouble[0], adouble[1], f);
            this.updateTooltipFromHover(adouble[0], adouble[1]);
            this.od.c();
            drawcontext.getMatrices().pop();
            GL11.glDisable(3089);
            this.renderTooltip(drawcontext.getMatrices().peek().getPositionMatrix());
            super.render(drawcontext, i, j, f);
         }
      }
   }

   private void renderBackgroundEffects(Matrix4f matrix4f, int i, int j) {
      if (!(this.ayw < 0.01F)) {
         ClickGuiModule clickguimodule = ClientMain.getInstance().getModuleManager().<ClickGuiModule>getModule(ClickGuiModule.class);
         boolean flag = clickguimodule == null || clickguimodule.b().getValue();
         if (i != this.CC || j != this.vv || flag != this.Wr) {
            this.CC = i;
            this.vv = j;
            this.Wr = flag;
            this.ra = new br().a(i + 50.0F, j + 50.0F).a();
            this.yk = flag ? new hvg().a(new Size((float)i, (float)j)).e(10.0F).a() : null;
         }

         float f = 0.3F * this.ayw;
         this.ra.a(matrix4f, -10.0F, -10.0F, f);
         float f1 = flag ? 10.0F * this.ayw : 0.0F;
         if (this.yk != null && f1 > 0.5F) {
            this.yk.a(matrix4f, 0.0F, 0.0F, 0.0F, f1);
         }
      }
   }

   private void renderTooltip(Matrix4f matrix4f) {
      float f = this.Vd + this.WW / 2.0F * this.cA;
      float f1 = this.azW;
      this.od.f(matrix4f, f, f1, this.ayw, this.cA);
   }

   private void updateTooltipFromHover(double d0, double d1) {
      String s = this.findHoveredTooltip(d0, d1);
      if (s != null && !s.isEmpty()) {
         this.od.a(s);
      } else {
         this.od.b();
      }
   }

   private String findHoveredTooltip(double d0, double d1) {
      for (InteractiveComponent interactivecomponent : this.u) {
         if (interactivecomponent.isVisible() && interactivecomponent.isHovered(d0, d1)) {
            String s = this.getComponentDescription();
            if (s != null && !s.isEmpty()) {
               return s;
            }
         }
      }

      if (this.isMouseInModuleArea(d0, d1)) {
         String s1 = this.amo.o(d0, d1);
         if (s1 != null && !s1.isEmpty()) {
            return s1;
         }
      }

      return null;
   }

   private String getComponentDescription() {
      return null;
   }

   private void updateGuiAlpha() {
      if (this.ams && this.iR != 0L) {
         long i = System.currentTimeMillis() - this.iR;
         if (i < 0L) {
            i = 0L;
         }

         float f = Math.min(1.0F, (float)i / this.dM);
         float f1 = xsx.a(f);
         if (this.Xp) {
            this.ayw = 1.0F - f1;
            if (f >= 1.0F) {
               this.Xp = false;
               this.ams = false;
               super.close();
               return;
            }
         } else {
            this.ayw = f1;
         }
      } else {
         this.ayw = 0.0F;
      }
   }

   private void applyGuiTransform(DrawContext drawcontext) {
      drawcontext.getMatrices().translate(this.Vd, this.azW, 0.0F);
      drawcontext.getMatrices().scale(this.cA, this.cA, 1.0F);
   }

   private void renderBackground(Matrix4f matrix4f) {
      this.Sg.a(matrix4f, 0.0F, 0.0F, this.ayw);
      this.auW.a(matrix4f, 0.0F, 60.0F, this.ayw);
      this.Ei.a(matrix4f, 200.0F, 0.0F, this.ayw);
   }

   private void renderComponents(Matrix4f matrix4f, double d0, double d1, float f) {
      this.renderStaticComponents(matrix4f, (int)d0, (int)d1, f);
      String s = this.ahP.l();
      it it = this.ahP.j();
      it.f();
      if (this.amo.d()) {
         this.amo.e(matrix4f, this.Hu, this.SF, (int)d0, (int)d1, f, this.ayw, it);
      }

      if (this.amo.b()) {
         this.amo.f(matrix4f, this.Hu, this.SF, (int)d0, (int)d1, f, this.ayw, it);
         if (this.axm.a()) {
            this.renderEmptyText(matrix4f, "Здесь будет список друзей");
         }
      }

      if (this.amo.c()) {
         this.amo.g(matrix4f, this.Hu, this.SF, (int)d0, (int)d1, f, this.ayw, it);
      }

      if ("Favourites".equals(s) && this.Tm.e()) {
         this.renderEmptyText(matrix4f, "Здесь пока нет избранных модулей");
      }
   }

   private void renderEmptyText(Matrix4f matrix4f, String s) {
      MSDFFont msdffont = atv.get();
      float f = 15.0F;
      BuiltText builttext = TextCache.a(msdffont, s, f, new Color(255, 255, 255, 128));
      float f1 = msdffont.c(s, f);
      float f2 = this.WW - this.Hu;
      float f3 = this.pe - this.SF;
      float f4 = this.Hu + (f2 - f1) * 0.5F;
      float f5 = this.SF + (f3 - f) * 0.5F;
      builttext.a(matrix4f, f4 - 25.0F, f5 - 25.0F, this.ayw);
   }

   private void renderStaticComponents(Matrix4f matrix4f, int i, int j, float f) {
      for (InteractiveComponent interactivecomponent : this.u) {
         if (interactivecomponent.isVisible()) {
            interactivecomponent.render(matrix4f, interactivecomponent.getX(), interactivecomponent.getY(), i, j, f, this.ayw);
         }
      }
   }

   public void tick() {
      super.tick();
      float f = 0.05F;
      xr.h(this.u, f);
      this.amo.p().tick(f);
      if (this.bN != null && this.Xw != null) {
         String s = this.ahP.l();
         boolean flag = "Friends".equals(s) || "Configs".equals(s);
         if (flag) {
            if (!this.Xw.isVisible()) {
               this.Xw.c(true);
               this.Xw.e();
               return;
            }
         } else {
            float f1 = this.bN.t();
            if (f1 >= 730.0F) {
               this.Xw.d();
            }

            if (f1 >= 769.0F) {
               this.Xw.c(false);
            }
         }
      }
   }

   public boolean mouseClicked(double d0, double d1, int i) {
      if (!this.isWithinBounds(d0, d1)) {
         return super.mouseClicked(d0, d1, i);
      } else {
         double[] adouble = this.scaleMousePosition(d0, d1);
         if (xr.a(this.u, adouble[0], adouble[1], i)) {
            this.ua = false;
            return true;
         } else {
            if (this.isMouseInModuleArea(adouble[0], adouble[1])) {
               if (this.amo.i(adouble[0], adouble[1], i)) {
                  this.ua = false;
                  return true;
               }

               if (i == 0) {
                  this.ua = true;
                  return true;
               }
            }

            this.ua = false;
            return super.mouseClicked(d0, d1, i);
         }
      }
   }

   public boolean mouseReleased(double d0, double d1, int i) {
      double[] adouble = this.scaleMousePosition(d0, d1);
      if (i == 0) {
         this.ua = false;
         this.u.forEach(interactivecomponent -> {
            interactivecomponent.mouseReleased(adouble[0], adouble[1], i);
         });
         this.amo.j(adouble[0], adouble[1], i);
         return true;
      } else {
         boolean flag = xr.b(this.u, adouble[0], adouble[1], i);
         flag |= this.amo.j(adouble[0], adouble[1], i);
         return flag || super.mouseReleased(d0, d1, i);
      }
   }

   public boolean mouseDragged(double d0, double d1, int i, double d2, double d3) {
      double[] adouble = this.scaleMousePosition(d0, d1);
      double d4 = d2 / this.cA;
      double d5 = d3 / this.cA;
      if (this.ua && i == 0) {
         this.amo.p().G(-d5 * 20.0);
         return true;
      } else {
         boolean flag = xr.c(this.u, adouble[0], adouble[1], i, d4, d5);
         flag |= this.amo.k(adouble[0], adouble[1], i, d4, d5);
         return flag || super.mouseDragged(d0, d1, i, d2, d3);
      }
   }

   public boolean mouseScrolled(double d0, double d1, double d2, double d3) {
      double[] adouble = this.scaleMousePosition(d0, d1);
      return this.isMouseInModuleArea(adouble[0], adouble[1]) && this.amo.l(adouble[0], adouble[1], d2, d3)
         ? true
         : xr.d(this.u, adouble[0], adouble[1], d2, d3) || super.mouseScrolled(d0, d1, d2, d3);
   }

   public boolean keyPressed(int i, int j, int k) {
      if (this.amo.m(i, j, k)) {
         return true;
      } else {
         boolean flag = xr.e(this.u, i, j, k);
         flag |= this.amo.p().keyPressed(i, j, k);
         return flag || super.keyPressed(i, j, k);
      }
   }

   public boolean keyReleased(int i, int j, int k) {
      boolean flag = xr.f(this.u, i, j, k);
      flag |= this.amo.p().keyReleased(i, j, k);
      return flag || super.keyReleased(i, j, k);
   }

   public boolean charTyped(char c0, int i) {
      if (this.amo.n(c0, i)) {
         return true;
      } else {
         boolean flag = xr.g(this.u, c0, i);
         flag |= this.amo.p().charTyped(c0, i);
         return flag || super.charTyped(c0, i);
      }
   }

   private boolean isMouseInModuleArea(double d0, double d1) {
      return d0 >= this.Hu && d0 <= this.WW && d1 >= this.SF && d1 <= this.pe;
   }

   private boolean isWithinBounds(double d0, double d1) {
      double[] adouble = this.scaleMousePosition(d0, d1);
      return adouble[0] >= 0.0 && adouble[0] <= this.WW && adouble[1] >= 0.0 && adouble[1] <= this.pe;
   }

   private double[] scaleMousePosition(double d0, double d1) {
      this.PV[0] = (d0 - this.Vd) / this.cA;
      this.PV[1] = (d1 - this.azW) / this.cA;
      return this.PV;
   }

   private void calculateScaling() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      int i = minecraftclient.getWindow().getWidth();
      int j = minecraftclient.getWindow().getHeight();
      if (i != this.rk || j != this.wj) {
         this.rk = i;
         this.wj = j;
         double d0 = minecraftclient.getWindow().getScaleFactor();
         float f = i / 1920.0F;
         float f1 = this.WW * f;
         float f2 = this.pe * f;
         this.Vd = (float)((i - f1) / 2.0 / d0);
         this.azW = (float)((j - f2) / 2.0 / d0);
         this.cA = (float)(f / d0);
      }
   }

   private void enableScissor(MinecraftClient minecraftclient) {
      double d0 = minecraftclient.getWindow().getScaleFactor();
      int i = (int)(this.Vd * d0);
      int j = (int)(minecraftclient.getWindow().getHeight() - (this.azW + this.pe * this.cA) * d0);
      int k = (int)(this.WW * this.cA * d0);
      int l = (int)(this.pe * this.cA * d0);
      GL11.glEnable(3089);
      GL11.glScissor(i, j, k, l);
   }

   public boolean shouldPause() {
      return false;
   }

   public void close() {
      if (!this.Xp) {
         this.Xp = true;
         this.iR = System.currentTimeMillis();
         SoundManager.getInstance().c(SoundType.GUI_CLOSE);
         this.saveGuiState();
      }
   }

   public void removed() {
      this.ams = false;
      this.ayw = 0.0F;
      super.removed();
   }

   public void renderBackground(DrawContext drawcontext, int i, int j, float f) {
   }

   public bze getFriends() {
      return this.axm;
   }
}
