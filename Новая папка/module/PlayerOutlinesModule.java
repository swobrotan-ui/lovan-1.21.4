package module;

import core.ClientMain;
import enum.Category;
import java.awt.Color;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.SliderSetting;

public class PlayerOutlinesModule extends Module {
   private ColorSetting outlineColorSetting = new ColorSetting("Цвет контура", "Цвет контура", new Color(255, 255, 255), true);
   private BooleanSetting showFriendsSetting = new BooleanSetting("Показывать друзей", "Отображать друзей", true);
   private SliderSetting glowRadiusSetting = new SliderSetting("Радиус свечения", "Размер glow эффекта", 1.0, 0.1, 5.0, 0.1, "x", 1);
   private SliderSetting glowIntensitySetting = new SliderSetting("Сила свечения", "Интенсивность glow", 2.2, 0.0, 10.0, 0.1, "", 1);
   private SliderSetting outlineSharpnessSetting = new SliderSetting("Чёткость контура", "Яркость оригинального контура", 1.1, 0.0, 5.0, 0.1, "", 1);
   private final Map<Integer, Boolean> Zv = new HashMap<Integer, Boolean>();
   private volatile Set<Integer> cO = new HashSet<Integer>();
   private final Set<Integer> aut = new HashSet<Integer>();
   private Method zc;
   private Method oW;
   private static final int kR = 6;
   private float cX = 1.0F;
   private float uM = 0.0F;
   private boolean aoy = false;
   private long Ib = System.currentTimeMillis();
   private int[] aN = new int[4];
   private Color gJ = null;
   private float Xd = -1.0F;
   private Vec3d oh = null;
   private long aso = 0L;
   private static final long hj = 16L;
   String VV = ClientMain.getInstance().isDev() ? "setFlag" : "method_5729";
   String ang = ClientMain.getInstance().isDev() ? "getFlag" : "method_5795";
   private static float sE = 0.2F;
   private static float ago = 0.18F;
   private static float ama = 0.12F;
   private static float sd = 0.05F;
   private static float fO = 2.0F;
   private static float Pz = 4.0F;
   private static float aww = 6.0F;
   private static float apD = 1.1F;

   public PlayerOutlinesModule() {
      super("ПлайерОутлинез", "Обводка контура игрока через ванильный эффект свечения", Category.RENDER);
      this.addSettings(this.outlineColorSetting, this.showFriendsSetting, this.glowRadiusSetting, this.glowIntensitySetting, this.outlineSharpnessSetting);
      this.a();
   }

   private void a() {
      try {
         this.zc = Entity.class.getDeclaredMethod(this.VV, int.class, boolean.class);
         this.zc.setAccessible(true);
         this.oW = Entity.class.getDeclaredMethod(this.ang, int.class);
         this.oW.setAccessible(true);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Override
   public void onEnable() {
      this.Ib = System.currentTimeMillis();
      this.cX = 1.0F;
      this.uM = 0.0F;
      this.aoy = false;
      this.Zv.clear();
      this.cO = new HashSet<Integer>();
      this.aut.clear();
      this.oh = null;
      this.aso = 0L;
      this.gJ = null;
      this.Xd = -1.0F;
   }

   @Override
   public void onDisable() {
      if (!this.isNotInWorld()) {
         HashSet hashset = new HashSet<Integer>(this.cO);
         hashset.addAll(this.aut);

         for (PlayerEntity playerentity : this.getWorld().getPlayers()) {
            if (hashset.contains(playerentity.getId())) {
               Boolean obool = this.Zv.get(playerentity.getId());
               this.c(playerentity, obool != null ? obool : false);
            }
         }
      }

      this.Zv.clear();
      this.cO = new HashSet<Integer>();
      this.aut.clear();
      this.aoy = true;
      this.Ib = System.currentTimeMillis();
   }

   @Override
   public void onRenderAfterSetup(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld() && this.isEnabled()) {
         this.e();
         this.cO = new HashSet<Integer>(this.aut);
         this.aut.clear();
         PlayerEntity playerentity = this.getPlayer();
         long i = System.currentTimeMillis();
         if (this.oh == null || i - this.aso > 16L) {
            this.oh = playerentity.getPos();
            this.aso = i;
         }

         boolean flag = this.getClient().options.getPerspective().isFirstPerson();
         ClientPlayerEntity clientplayerentity = this.getClient().player;
         float f = (float)((Integer)this.getClient().options.getFov().getValue()).intValue();
         Camera camera = this.getClient().gameRenderer.getCamera();

         for (PlayerEntity playerentity1 : this.getWorld().getPlayers()) {
            if (playerentity1.isAlive()
               && (!flag || playerentity1 != clientplayerentity)
               && wu.b(playerentity1, playerentity, this.oh, 128.0, false, this.showFriendsSetting.getValue(), this)) {
               Vec3d vec3d = playerentity1.getPos().add(0.0, playerentity1.getHeight() * 0.5, 0.0);
               if (wu.a(vec3d, camera, f)) {
                  int j = playerentity1.getId();
                  boolean flag1 = this.d(playerentity1);
                  this.Zv.putIfAbsent(j, flag1);
                  this.aut.add(j);
                  this.cO.add(j);
                  if (!flag1) {
                     this.c(playerentity1, true);
                  }
               }
            }
         }

         this.b();
      }
   }

   private void b() {
      Iterator iterator = this.Zv.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         int i = (Integer)entry.getKey();
         if (!this.aut.contains(i)) {
            Entity entity = this.getWorld().getEntityById(i);
            if (entity != null) {
               this.c(entity, (Boolean)entry.getValue());
            }

            iterator.remove();
         }
      }
   }

   private void c(Entity entity, boolean flag) {
      if (this.zc != null) {
         try {
            this.zc.invoke(entity, 6, flag);
         } catch (Exception exception) {
         }
      }
   }

   private boolean d(Entity entity) {
      if (this.oW == null) {
         return false;
      } else {
         try {
            return (Boolean)this.oW.invoke(entity, 6);
         } catch (Exception exception) {
            return false;
         }
      }
   }

   private void e() {
      long i = System.currentTimeMillis();
      float f = (float)(i - this.Ib) / 1000.0F;
      this.Ib = i;
      if (this.aoy) {
         this.uM = Math.max(0.0F, this.uM - f * 5.0F);
      } else {
         this.uM = Math.min(1.0F, this.uM + f * 5.0F);
      }

      this.cX = this.uM;
   }

   public boolean f(Entity entity) {
      return this.cO.contains(entity.getId());
   }

   public int[] g() {
      Color color = this.outlineColorSetting.getColor();
      if (this.gJ != color || this.Xd != this.cX) {
         this.gJ = color;
         this.Xd = this.cX;
         int i = (int)(color.getAlpha() / 255.0F * this.cX * 255.0F);
         this.aN[0] = color.getRed();
         this.aN[1] = color.getGreen();
         this.aN[2] = color.getBlue();
         this.aN[3] = i;
      }

      return this.aN;
   }

   public static PlayerOutlinesModule h() {
      return ClientMain.getInstance().getModuleManager().<PlayerOutlinesModule>getModule(PlayerOutlinesModule.class);
   }

   public float[] i() {
      return new float[]{sE, ago, ama, sd};
   }

   public float[] j() {
      float f = this.glowRadiusSetting.getFloatValue();
      return new float[]{fO * f, Pz * f, aww * f};
   }

   public float k() {
      return apD;
   }

   public float l() {
      return this.glowIntensitySetting.getFloatValue();
   }

   public float m() {
      return this.outlineSharpnessSetting.getFloatValue();
   }
}
