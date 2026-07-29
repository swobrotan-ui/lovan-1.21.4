package module;

import com.mojang.blaze3d.systems.RenderSystem;
import core.ClientMain;
import data.ColoredText;
import enum.Category;
import font.MSDFFont;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import render.BuiltRectangle;
import render.BuiltText;
import render.ColorCache;
import render.RectangleCache;
import render.TextCache;
import setting.BooleanSetting;
import setting.GroupSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class NameTagsModule extends Module {
   private String ass = "FT";
   private String KU = "MB";
   private SliderSetting sizeSetting = new SliderSetting("Размер", "Масштаб имени игрока", 0.35, 0.01, 1.0, 0.05);
   private SliderSetting alphaSetting = new SliderSetting("Альфа", "Прозрачность фона", 1.0, 0.0, 1.0, 0.01);
   private BooleanSetting showArmorSetting = new BooleanSetting("Показывать броню", "Отображать броню игрока", true);
   private BooleanSetting showHandItemsSetting = new BooleanSetting("Показывать предметы в руках", "Отображать предметы в руках игрока", true);
   private ListSetting displayModeSetting = new ListSetting(
      "Режим HP", "Способ отображения здоровья", Arrays.<String>asList("Очки здоровья", "Сердца"), Collections.<String>singletonList("Очки здоровья"), false
   );
   private ListSetting healthBypassSetting = new ListSetting(
      "Обход HP", "Режим получения реального HP", Arrays.<String>asList("Выкл", this.KU, this.ass), List.<String>of("Выкл"), false
   );
   private BooleanSetting showDonateSetting = new BooleanSetting("Отображать донат", "Показывать ранг/донат перед ником", false);
   private GroupSetting displayGroup = new GroupSetting("Отображение", "Настройки рендера", false);
   private BooleanSetting staticSizeSetting = new BooleanSetting("Статичный размер", "Размер не зависит от дистанции", false);
   private BooleanSetting showFriendsSetting = new BooleanSetting("Показывать друзей", "Отображать друзей", true);
   private BooleanSetting showSelfSetting = new BooleanSetting("Показывать себя", "Отображать себя", false);
   private static float auJ = 16.0F;
   private static float adP = 5.0F;
   private static float aaG = 10.0F;
   private static float kD = 8.0F;
   private static float Gf = 4.0F;
   private static float Ff = 20.0F;
   private static float eY = 3.0F;
   private static float zs = 2.0F;
   private static float ayY = 2.0F;
   private static float JS = 20.0F;
   private static float xk = 5.0F;
   private MSDFFont zD;
   private final Map<UUID, b> mv = new HashMap<UUID, b>();
   private final Map<UUID, lji> ajM = new HashMap<UUID, lji>();
   private long Cz = 0L;
   private static long asH = 100L;
   private static final int Yk = 16755200;
   private static final int abM = 16733525;
   private static final Color uO = new Color(65280);
   private static final Color qz = new Color(16776960);
   private static final Color ajl = new Color(16755200);
   private static final Color Ph = new Color(16733525);

   public NameTagsModule() {
      super("НамеТагз", "Показывает имена игроков и их здоровье", Category.RENDER);
      this.displayGroup.addSettings(this.staticSizeSetting, this.showFriendsSetting, this.showSelfSetting);
      this.addSettings(
         this.healthBypassSetting,
         this.displayModeSetting,
         this.showDonateSetting,
         this.sizeSetting,
         this.alphaSetting,
         this.showArmorSetting,
         this.showHandItemsSetting,
         this.displayGroup
      );
      this.zD = MSDFFont.g().b("bb").c("bb").e();
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
      this.mv.clear();
      this.ajM.clear();
   }

   @Override
   public void onRenderLate(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld()) {
         long i = System.currentTimeMillis();
         if (i - this.Cz > asH) {
            this.a();
            this.Cz = i;
         }

         this.c(worldrendercontext);
      }
   }

   private void a() {
      String s = this.healthBypassSetting.getFirst();
      if (s != null && !s.equals("Выкл") && !s.equals(this.ass)) {
         try {
            if (s.equals(this.KU)) {
               this.b();
            }
         } catch (Exception exception) {
         }
      }
   }

   private void b() {
      Scoreboard scoreboard = this.getWorld().getScoreboard();
      if (scoreboard != null) {
         ScoreboardObjective scoreboardobjective = null;

         for (ScoreboardObjective scoreboardobjective1 : scoreboard.getObjectives()) {
            if (scoreboardobjective1 != null) {
               String s = scoreboardobjective1.getName().toLowerCase();
               String s1 = scoreboardobjective1.getDisplayName().getString().toLowerCase();
               if (s.contains("health") || s.contains("hp") || s.contains("heart") || s1.contains("♥") || s1.contains("❤") || s1.contains("health")) {
                  scoreboardobjective = scoreboardobjective1;
                  break;
               }
            }
         }

         if (scoreboardobjective == null) {
            scoreboardobjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
         }

         if (scoreboardobjective != null) {
            for (PlayerEntity playerentity : this.getWorld().getPlayers()) {
               ReadableScoreboardScore readablescoreboardscore = scoreboard.getScore(playerentity, scoreboardobjective);
               if (readablescoreboardscore != null) {
                  this.ajM.put(playerentity.getUuid(), new lji((float)readablescoreboardscore.getScore()));
               }
            }
         }
      }
   }

   private void c(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld()) {
         MatrixStack matrixstack = worldrendercontext.matrixStack();
         Camera camera = worldrendercontext.camera();
         Vec3d vec3d = camera.getPos();
         float f = worldrendercontext.tickCounter().getTickDelta(true);
         PlayerEntity playerentity = this.getPlayer();
         Vec3d vec3d1 = playerentity.getPos();
         double d0 = 64.0;
         float f1 = (float)((Integer)this.getClient().options.getFov().getValue()).intValue();
         ArrayList arraylist = new ArrayList();

         for (PlayerEntity playerentity1 : this.getWorld().getPlayers()) {
            if (wu.c(playerentity1, playerentity, vec3d1, d0, this.showSelfSetting.getValue(), this.showFriendsSetting.getValue(), this, camera, f1)) {
               double d1 = playerentity.distanceTo(playerentity1);
               arraylist.add(new SimpleEntry<PlayerEntity, Double>(playerentity1, d1));
            }
         }

         if (!arraylist.isEmpty()) {
            arraylist.sort((entry2, entry3) -> {
               return Double.compare((Double)entry3.getValue(), (Double)entry2.getValue());
            });
            this.getClient().getBufferBuilders().getEntityVertexConsumers().draw();
            this.getClient().getBufferBuilders().getEffectVertexConsumers().draw();
            GL11.glDepthFunc(519);
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();

            try {
               for (Entry entry : arraylist) {
                  PlayerEntity playerentity2 = (PlayerEntity)entry.getKey();
                  Vec3d vec3d2 = dr.c(playerentity2, f, vec3d);
                  matrixstack.push();

                  try {
                     matrixstack.translate(vec3d2.x, vec3d2.y, vec3d2.z);
                     this.d(matrixstack, playerentity2, worldrendercontext.camera().getYaw(), worldrendercontext.camera().getPitch());
                  } finally {
                     matrixstack.pop();
                  }
               }
            } finally {
               try {
                  this.getClient().getBufferBuilders().getEntityVertexConsumers().draw();
                  this.getClient().getBufferBuilders().getEffectVertexConsumers().draw();
               } catch (Exception exception) {
               }

               RenderSystem.depthMask(true);
               GL11.glDepthFunc(515);
               RenderSystem.disableBlend();
               RenderSystem.enableCull();
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            HashSet hashset = new HashSet();

            for (Entry entry1 : arraylist) {
               hashset.add(((PlayerEntity)entry1.getKey()).getUuid());
            }

            this.mv.keySet().retainAll(hashset);
            this.ajM.keySet().retainAll(hashset);
         }
      }
   }

   private void d(MatrixStack matrixstack, PlayerEntity playerentity, float f, float f1) {
      matrixstack.push();

      try {
         matrixstack.translate(0.0, playerentity.getHeight() + 0.5, 0.0);
         matrixstack.multiply(new Quaternionf().rotationY(-f * (float) (Math.PI / 180.0)));
         matrixstack.multiply(new Quaternionf().rotationX(f1 * (float) (Math.PI / 180.0)));
         double d0 = this.getPlayer().distanceTo(playerentity);
         float f2 = this.staticSizeSetting.getValue() ? 0.025F : (float)Math.max(0.015, Math.min(d0 * 0.0015, 0.08));
         float f3 = f2 * this.sizeSetting.getFloatValue();
         matrixstack.scale(-f3, -f3, f3);
         String s = playerentity.getName().getString();
         ProtestModule protestmodule = ClientMain.getInstance().getModuleManager().<ProtestModule>getModule(ProtestModule.class);
         boolean flag = protestmodule != null && protestmodule.isEnabled();
         if (flag) {
            s = protestmodule.a(s);
         }

         Color color = this.isFriendPlayer(playerentity) ? Color.GREEN : Color.WHITE;
         ArrayList arraylist = new ArrayList();
         if (this.showDonateSetting.getValue()) {
            String s1 = this.p(playerentity);
            if (!s1.isEmpty()) {
               arraylist.add(new ColoredText(s1 + " ", Color.WHITE));
            }
         }

         arraylist.add(new ColoredText(s, color));
         String s2 = this.j(playerentity);
         float f4 = 0.0F;

         for (ColoredText coloredtext : arraylist) {
            f4 += this.zD.c(coloredtext.text, auJ);
         }

         float f21 = this.zD.c(s2, auJ);
         float f22 = f4 + Gf + f21;
         b b = this.mv.computeIfAbsent(playerentity.getUuid(), uuid -> {
            return new b();
         });
         ItemStack[] aitemstack = new ItemStack[4];
         boolean flag1 = false;

         for (int i = 0; i < 4; i++) {
            if (this.showArmorSetting.getValue()) {
               aitemstack[i] = playerentity.getInventory().getStack(36 + i);
               if (!aitemstack[i].isEmpty()) {
                  flag1 = true;
               }
            } else {
               aitemstack[i] = ItemStack.EMPTY;
            }
         }

         ItemStack itemstack2 = playerentity.getMainHandStack();
         ItemStack itemstack = playerentity.getOffHandStack();
         b.a(aitemstack, flag1);
         float f5 = f22 + aaG * 2.0F;
         float f6 = auJ + adP * 2.0F;
         float f7 = -f5 / 2.0F;
         float f8 = -f6 / 2.0F;
         Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
         float f10 = 0.0F;
         float f11 = 0.0F;
         float f12 = 0.0F;
         if (this.showArmorSetting.getValue() && b.abN.h() > 0.001F) {
            float f13 = b.abN.h();
            float f14 = 0.0F;

            for (int j = 0; j < 4; j++) {
               float f15 = b.anr[j].g();
               if (f15 > 0.001F) {
                  f14 += (Ff + eY) * f15;
               }
            }

            f14 -= eY;
            if (f14 > 0.0F) {
               f11 = f14 + aaG * 2.0F;
               f10 = Ff + ayY + zs + adP * 2.0F;
               f12 = -f11 / 2.0F;
               float f9 = f8 - f10 - 5.0F;
               float f24 = this.alphaSetting.getFloatValue() * f13;
               BuiltRectangle builtrectangle1 = RectangleCache.b(f11, f10, kD);
               builtrectangle1.a(matrix4f, f12, f9, f24);
               float f16 = f12 + aaG;
               float f17 = f9 + adP;

               for (int k = 3; k >= 0; k--) {
                  ItemStack itemstack1 = aitemstack[k];
                  float f18 = b.anr[k].g();
                  if (f18 > 0.001F) {
                     float f19;
                     if (b.abN.g()) {
                        float f20 = Math.max(0.0F, (f13 - 0.3F) / 0.7F);
                        f19 = f18 * f20;
                     } else if (!flag1) {
                        float f31 = Math.min(1.0F, f13 / 0.7F);
                        f19 = f18 * f31;
                     } else {
                        f19 = f18;
                     }

                     if (!itemstack1.isEmpty() && f19 > 0.001F) {
                        this.f(matrix4f, itemstack1, f16, f17, f19);
                        float f32 = f17 + Ff + ayY;
                        this.g(matrix4f, f16, f32, itemstack1, f19);
                     }

                     f16 += flag1 ? (Ff + eY) * f18 : Ff + eY;
                  }
               }
            }
         }

         float f23 = this.alphaSetting.getFloatValue();
         BuiltRectangle builtrectangle = RectangleCache.b(f5, f6, kD);
         builtrectangle.a(matrix4f, f7, f8, f23);
         float f25 = f7 + aaG;
         float f26 = f8 + adP;
         float f27 = f25;

         for (ColoredText coloredtext1 : arraylist) {
            BuiltText builttext1 = TextCache.a(this.zD, coloredtext1.text, auJ, coloredtext1.color);
            builttext1.render(matrix4f, f27, f26 - 1.5F);
            f27 += this.zD.c(coloredtext1.text, auJ);
         }

         Color color1 = this.k(playerentity);
         BuiltText builttext = TextCache.a(this.zD, s2, auJ, color1);
         builttext.render(matrix4f, f25 + f4 + Gf, f26 - 1.5F);
         float f28 = JS + ayY + zs + adP * 2.0F;
         float f29 = f8 - f28 - 5.0F;
         float f30 = f29 + adP;
         if (this.showHandItemsSetting.getValue()) {
            if (!itemstack2.isEmpty()) {
               float f33 = f10 > 0.0F ? f12 + f11 + xk + 2.0F : xk + 2.0F;
               this.e(matrixstack, itemstack2, f33, f29, f30);
            }

            if (!itemstack.isEmpty()) {
               float f34 = f10 > 0.0F ? f12 - JS - xk - 15.0F : -JS - xk - 15.0F;
               this.e(matrixstack, itemstack, f34, f29, f30);
            }
         }
      } finally {
         matrixstack.pop();
      }
   }

   private void e(MatrixStack matrixstack, ItemStack itemstack, float f, float f1, float f2) {
      float f3 = JS + aaG * 2.0F - 4.0F;
      float f4 = JS + ayY + zs + adP * 2.0F;
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      BuiltRectangle builtrectangle = RectangleCache.b(f3, f4, kD);
      builtrectangle.a(matrix4f, f, f1, this.alphaSetting.getFloatValue());
      float f5 = f + (f3 - JS) / 2.0F;
      float f6 = f2;
      if (!itemstack.isDamageable()) {
         f6 = f1 + (f4 - JS) / 2.0F;
      }

      this.f(matrix4f, itemstack, f5, f6, 1.0F);
      if (itemstack.isDamageable()) {
         float f7 = f2 + JS + ayY;
         this.g(matrix4f, f5, f7, itemstack, 1.0F);
      }

      this.getClient().getBufferBuilders().getEntityVertexConsumers().draw();
      GL11.glDepthFunc(519);
      if (itemstack.isStackable() && itemstack.getCount() > 1) {
         String s = String.valueOf(itemstack.getCount());
         float f8 = auJ * 0.7F;
         float f9 = this.zD.c(s, f8);
         float f10 = f5 + JS - f9 - 2.0F;
         float f11 = f6 + JS - f8 + 2.0F;
         BuiltText builttext = TextCache.a(this.zD, s, f8, Color.WHITE);
         builttext.render(matrix4f, f10, f11);
      }
   }

   private void f(Matrix4f matrix4f, ItemStack itemstack, float f, float f1, float f2) {
      if (!(f2 < 0.001F)) {
         MatrixStack matrixstack = new MatrixStack();
         matrixstack.peek().getPositionMatrix().set(matrix4f);
         matrixstack.translate(f + 10.0F, f1 + 9.0F, 0.0F);
         float f3 = Ff + 4.0F;
         matrixstack.scale(f3, -f3, f3);
         Immediate immediate = this.getClient().getBufferBuilders().getEntityVertexConsumers();
         if (f2 < 1.0F) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f2);
         }

         this.getClient()
            .getItemRenderer()
            .renderItem(itemstack, ModelTransformationMode.GUI, 15728880, OverlayTexture.DEFAULT_UV, matrixstack, immediate, this.getClient().world, 0);
         immediate.draw();
         GL11.glDepthFunc(519);
         if (f2 < 1.0F) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   private void g(Matrix4f matrix4f, float f, float f1, ItemStack itemstack, float f2) {
      int i = itemstack.getMaxDamage();
      int j = itemstack.getDamage();
      if (i != 0) {
         float f3 = (float)(i - j) / i;
         float f4 = f3 * Ff;
         int k = ColorCache.d(0, 0, 0, f2 * 0.8F);
         int l = this.i(f3, f2);
         Immediate immediate = this.getClient().getBufferBuilders().getEntityVertexConsumers();
         VertexConsumer vertexconsumer = immediate.getBuffer(RenderLayer.getGui());
         this.h(vertexconsumer, matrix4f, f, f1, Ff, zs, k);
         if (f4 > 0.0F) {
            this.h(vertexconsumer, matrix4f, f, f1, f4, zs, l);
         }
      }
   }

   private void h(VertexConsumer vertexconsumer, Matrix4f matrix4f, float f, float f1, float f2, float f3, int i) {
      ajs.e(vertexconsumer, matrix4f, f, f1, f2, f3, i);
   }

   private int i(float f, float f1) {
      return ajs.c(f, f1);
   }

   private String j(PlayerEntity playerentity) {
      float f = this.l(playerentity);
      float f1 = this.n(playerentity);
      String s = this.displayModeSetting.getFirst();
      switch (s) {
         case "Процент":
            return String.format("%.0f%%", f / f1 * 100.0F);
         case "Сердца":
            int i = Math.round(f / 2.0F);
            int j = Math.round(f1 / 2.0F);
            return String.format("%d/%d", i, j);
         default:
            return String.format("%.1f", f);
      }
   }

   private Color k(PlayerEntity playerentity) {
      float f = this.l(playerentity);
      float f1 = this.n(playerentity);
      float f2 = f / f1;
      if (f2 > 0.75F) {
         return uO;
      } else if (f2 > 0.5F) {
         return qz;
      } else {
         return f2 > 0.25F ? ajl : Ph;
      }
   }

   private float l(PlayerEntity playerentity) {
      float f = playerentity.getHealth();
      float f1 = playerentity.getAbsorptionAmount();
      if (!this.o()) {
         return f + f1;
      } else {
         String s = this.healthBypassSetting.getFirst();
         if (this.ass.equals(s)) {
            float f2 = this.m(playerentity);
            if (f2 > 0.0F) {
               return f2;
            }
         } else {
            lji lji = this.ajM.get(playerentity.getUuid());
            if (lji != null && lji.Fu > 0.0F) {
               return lji.Fu + f1;
            }
         }

         return f + f1;
      }
   }

   private float m(PlayerEntity playerentity) {
      try {
         Scoreboard scoreboard = this.getWorld().getScoreboard();
         if (scoreboard == null) {
            return -1.0F;
         }

         ScoreboardObjective scoreboardobjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
         if (scoreboardobjective == null) {
            return -1.0F;
         }

         ReadableScoreboardScore readablescoreboardscore = scoreboard.getScore(playerentity, scoreboardobjective);
         if (readablescoreboardscore != null) {
            return readablescoreboardscore.getScore();
         }
      } catch (Exception exception) {
      }

      return -1.0F;
   }

   private float n(PlayerEntity playerentity) {
      float f = playerentity.getMaxHealth();
      float f1 = playerentity.getAbsorptionAmount();
      if (!this.o()) {
         return f + f1;
      } else {
         String s = this.healthBypassSetting.getFirst();
         if (this.ass.equals(s)) {
            return Math.max(f, 20.0F);
         } else {
            float f2 = Math.max(f, 20.0F);
            return f2 + f1;
         }
      }
   }

   public boolean o() {
      String s = this.healthBypassSetting.getFirst();
      return s != null && !s.equals("Выкл");
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      super.onPlayerDeath(playerentity);
      if (this.enabledSetting.getValue() && playerentity == this.getPlayer()) {
         this.setEnabled(false);
      }
   }

   private String p(PlayerEntity playerentity) {
      try {
         Team team = playerentity.getScoreboardTeam();
         if (team == null) {
            return "";
         } else {
            String s = team.getPrefix().getString();
            return s.replaceAll("[§&][0-9a-fk-orA-FK-OR]", "").trim();
         }
      } catch (Exception exception) {
         return "";
      }
   }

   public String q() {
      return this.ass;
   }

   public String r() {
      return this.KU;
   }

   public ListSetting s() {
      return this.displayModeSetting;
   }

   public ListSetting t() {
      return this.healthBypassSetting;
   }
}
