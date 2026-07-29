package module;

import enum.Category;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.MaceItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import setting.BooleanSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class AimAssistModule extends Module {
   private static final String CK = "Дистанция";
   private static final String atY = "Здоровье";
   private SliderSetting distanceSetting = new SliderSetting("Дистанция", "Максимальная дистанция до цели", 4.0, 1.0, 6.0, 0.1);
   private SliderSetting fovSetting = new SliderSetting("FOV", "Угол обзора для захвата цели", 90.0, 20.0, 360.0, 1.0);
   private SliderSetting speedSetting = new SliderSetting("Скорость", "Базовая скорость наведения", 4.0, 0.5, 10.0, 0.1);
   private BooleanSetting drawFovRadiusSetting = new BooleanSetting("Рисовать радиус FOV", "Рисует круг радиуса FOV на экране", false);
   private SliderSetting aimHeightSetting = new SliderSetting(
      "Высота прицела", "Точка прицеливания по высоте цели (0 = ноги, 1 = макушка)", 0.85, 0.0, 1.0, 0.05
   );
   private ListSetting prioritySetting = new ListSetting(
      "Приоритет", "По какому параметру выбирать цель", Arrays.<String>asList("Дистанция", "Здоровье"), List.<String>of("Дистанция"), false
   );
   private BooleanSetting onlyWithWeaponSetting = new BooleanSetting("Только с оружием", "Работать только с оружием в руке", true);
   private BooleanSetting onlyPlayersSetting = new BooleanSetting("Только игроки", "Целиться только на игроков", true);
   private BooleanSetting noAimInInventorySetting = new BooleanSetting("Не целить в инвентаре", "Не наводиться когда открыт инвентарь", true);
   private BooleanSetting hitInvisibleSetting = new BooleanSetting("Бить невидимых", "Целиться на невидимых игроков", false);
   private BooleanSetting onlyArmoredSetting = new BooleanSetting("Только в броне", "Целиться только на игроков в броне", false);
   private BooleanSetting ignoreNakedSetting = new BooleanSetting("Не бить голых", "Игнорировать игроков без брони", false);
   private BooleanSetting onlyXSetting = new BooleanSetting("Только по X", "Менять только X", false);
   private BooleanSetting multipointSetting = new BooleanSetting("Мультипоинт", "Рандомные точки прицеливания на хитбоксе", true);
   private BooleanSetting wallCheckSetting = new BooleanSetting("Проверка стен", "Не наводиться на цели за блоками", true);
   private BooleanSetting disableOnWorldChangeSetting = new BooleanSetting("Выкл. при смене мира", "Выключить модуль при телепорте в другой мир", false);
   private BooleanSetting microMovementsSetting = new BooleanSetting("Микродвижения", "Рандомный шум и вариация для обхода античита", true);
   private Entity FD;
   private long Dp;
   private long aeD;
   private double aeX;
   private double nX;
   private double RM;
   private double ayG;
   private double SL;
   private double Rt;
   private double alh;
   private double yY;
   private double akx;
   private double Nk;
   private long aho;
   private int adW;
   private double all;
   private double ef;
   private double aox;
   private double vK;
   private double aiH;
   private double PK;
   private double asf;
   private long KJ;
   private RegistryKey<World> ant;

   public AimAssistModule() {
      super("АимАззизт", "Плавно наводится на противника", Category.COMBAT);
      this.ignoreNakedSetting.setVisibilitySupplier(() -> {
         return !this.onlyArmoredSetting.getValue();
      });
      this.addSettings(
         this.distanceSetting,
         this.fovSetting,
         this.drawFovRadiusSetting,
         this.speedSetting,
         this.aimHeightSetting,
         this.prioritySetting,
         this.onlyWithWeaponSetting,
         this.onlyPlayersSetting,
         this.noAimInInventorySetting,
         this.hitInvisibleSetting,
         this.onlyArmoredSetting,
         this.ignoreNakedSetting,
         this.onlyXSetting,
         this.multipointSetting,
         this.wallCheckSetting,
         this.disableOnWorldChangeSetting,
         this.microMovementsSetting
      );
   }

   @Override
   public void onEnable() {
      this.a();
   }

   @Override
   public void onDisable() {
      this.a();
   }

   private void a() {
      this.FD = null;
      this.Dp = 0L;
      this.aeD = 0L;
      this.aeX = 0.0;
      this.nX = 0.0;
      this.ant = null;
      this.all = 0.0;
      this.ef = 0.0;
      this.aox = 0.0;
      this.vK = 0.0;
      this.aiH = 0.0;
      this.PK = 0.0;
      this.KJ = 0L;
      this.b();
   }

   private void b() {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      this.RM = threadlocalrandom.nextDouble(Math.PI * 2);
      this.ayG = threadlocalrandom.nextDouble(Math.PI * 2);
      this.SL = threadlocalrandom.nextDouble(0.08, 0.25);
      this.Rt = threadlocalrandom.nextDouble(0.06, 0.2);
      this.alh = threadlocalrandom.nextDouble(1.8, 3.5);
      this.yY = threadlocalrandom.nextDouble(0.75, 1.25);
      this.akx = threadlocalrandom.nextDouble(-0.1, 0.2);
      this.adW = threadlocalrandom.nextInt(3);
      this.Nk = threadlocalrandom.nextDouble(-0.15, 0.1);
      this.aho = System.currentTimeMillis();
      this.asf = threadlocalrandom.nextDouble(0.025, 0.065);
      this.c(threadlocalrandom);
   }

   private void c(ThreadLocalRandom threadlocalrandom) {
      this.vK = threadlocalrandom.nextDouble(-0.35, 0.35);
      this.aiH = threadlocalrandom.nextDouble(-0.12, 0.12);
      this.PK = threadlocalrandom.nextDouble(-0.35, 0.35);
      this.KJ = System.currentTimeMillis() + threadlocalrandom.nextLong(300L, 900L);
      this.asf = threadlocalrandom.nextDouble(0.02, 0.07);
   }

   @Override
   public void onRenderStart() {
      if (!this.isNotInWorld() && this.getPlayer() != null) {
         RegistryKey registrykey = this.getWorld().getRegistryKey();
         if (this.ant == null) {
            this.ant = registrykey;
         } else if (!this.ant.equals(registrykey)) {
            if (this.disableOnWorldChangeSetting.getValue()) {
               this.setEnabled(false);
               return;
            }

            this.ant = registrykey;
            this.a();
            this.ant = registrykey;
            return;
         }

         if (!this.onlyWithWeaponSetting.getValue() || this.j()) {
            if (!this.noAimInInventorySetting.getValue() || !(this.getScreen() instanceof HandledScreen)) {
               long i = System.nanoTime();
               double d0 = this.aeD > 0L ? (i - this.aeD) / 1.66666667E7 : 1.0;
               d0 = MathHelper.clamp(d0, 0.05, 3.0);
               this.aeD = i;
               Entity entity = this.g();
               if (entity == null) {
                  if (this.FD != null) {
                     this.a();
                  }
               } else {
                  if (entity != this.FD) {
                     this.FD = entity;
                     this.Dp = System.currentTimeMillis();
                     this.b();
                  }

                  ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
                  boolean flag = this.microMovementsSetting.getValue();
                  long j = System.currentTimeMillis();
                  if (flag && j - this.aho > threadlocalrandom.nextLong(180L, 450L)) {
                     this.Nk = this.Nk + threadlocalrandom.nextDouble(-0.06, 0.06);
                     this.Nk = MathHelper.clamp(this.Nk, -0.2, 0.15);
                     this.aho = j;
                  }

                  if (this.multipointSetting.getValue() && j >= this.KJ) {
                     this.c(threadlocalrandom);
                  }

                  if (this.multipointSetting.getValue()) {
                     double d1 = this.asf + (flag ? threadlocalrandom.nextDouble(-0.008, 0.008) : 0.0);
                     this.all = this.all + (this.vK - this.all) * d1;
                     this.ef = this.ef + (this.aiH - this.ef) * d1;
                     this.aox = this.aox + (this.PK - this.aox) * d1;
                  }

                  double[] adouble = this.i(entity);
                  float f = this.getPlayer().getYaw();
                  float f1 = this.getPlayer().getPitch();
                  boolean flag1 = this.onlyXSetting.getValue();
                  double d2 = m(adouble[0] - f);
                  double d3 = adouble[1] - f1;
                  double d4;
                  if (flag1) {
                     double d5 = flag ? 8.0 + threadlocalrandom.nextDouble(4.0) : 10.0;
                     double d6 = Math.abs(d3);
                     if (d6 > d5) {
                        double d7 = flag ? 0.06 + threadlocalrandom.nextDouble(0.06) : 0.08;
                        d4 = (d3 - Math.signum(d3) * d5) * d7;
                     } else {
                        d4 = 0.0;
                     }
                  } else {
                     d4 = d3;
                  }

                  double d23 = flag1 ? Math.abs(d2) : Math.sqrt(d2 * d2 + d4 * d4);
                  if (!(d23 < 0.05)) {
                     double d24 = MathHelper.clamp((j - this.Dp) / (flag ? 400.0 + threadlocalrandom.nextDouble(200.0) : 350.0), 0.0, 1.0);
                     double d25 = this.f(d24, flag);
                     double d8 = this.speedSetting.getValue() * (flag ? this.yY : 1.0);
                     if (flag && threadlocalrandom.nextDouble() < 0.015) {
                        this.yY = threadlocalrandom.nextDouble(0.7, 1.3);
                     }

                     double d9;
                     if (flag) {
                        if (d23 < 3.0) {
                           d9 = 0.35 + threadlocalrandom.nextDouble(0.25);
                        } else if (d23 < 10.0) {
                           d9 = 0.7 + threadlocalrandom.nextDouble(0.3);
                        } else if (d23 < 25.0) {
                           d9 = 0.85 + threadlocalrandom.nextDouble(0.3);
                        } else if (d23 < 50.0) {
                           d9 = 1.15 + threadlocalrandom.nextDouble(0.35);
                        } else {
                           d9 = 1.4 + threadlocalrandom.nextDouble(0.4);
                        }
                     } else if (d23 < 3.0) {
                        d9 = 0.45;
                     } else if (d23 < 10.0) {
                        d9 = 0.85;
                     } else if (d23 < 25.0) {
                        d9 = 1.0;
                     } else if (d23 < 50.0) {
                        d9 = 1.3;
                     } else {
                        d9 = 1.6;
                     }

                     double d10 = 1.0;
                     if (d23 > 15.0) {
                        double d11 = MathHelper.clamp((d23 - 15.0) / 60.0, 0.0, 1.0);
                        d10 = 1.0 + d11 * (flag ? 1.2 + threadlocalrandom.nextDouble(0.4) : 1.4);
                     }

                     double d26 = d8 * d25 * d9 * d10 * d0;
                     if (this.getPlayer().fallDistance > 0.0F) {
                        double d12 = flag ? 0.12 + threadlocalrandom.nextDouble(0.08) : 0.15;
                        double d13 = 1.0 + MathHelper.clamp(this.getPlayer().fallDistance * d12, 0.0, 0.7);
                        d26 *= d13;
                     }

                     if (!this.getPlayer().isOnGround() && this.getPlayer().getVelocity().y > 0.1) {
                        d26 *= flag ? 0.8 + threadlocalrandom.nextDouble(0.15) : 0.85;
                     }

                     double d27 = flag ? threadlocalrandom.nextDouble(-0.003, 0.003) : 0.0;
                     double d28 = d23 > 30.0 ? 0.96 : 0.92;
                     double d14 = MathHelper.clamp(d26 * 0.028 + d27, 0.005, d28);
                     double d15 = d2 * d14;
                     double d16 = flag ? 0.85 + threadlocalrandom.nextDouble(0.3) : 1.0;
                     double d17 = d4 * d14 * d16;
                     if (flag && d23 < 2.5 && this.akx > 0.01) {
                        double d18 = this.akx * threadlocalrandom.nextDouble(0.4, 1.0);
                        d15 *= 1.0 + d18;
                        d17 *= 1.0 + d18 * 0.7;
                        this.akx = this.akx * (0.9 + threadlocalrandom.nextDouble(0.07));
                     }

                     if (flag) {
                        double d29 = 0.06 + threadlocalrandom.nextDouble(0.1);
                        this.RM = this.RM + (this.SL + threadlocalrandom.nextDouble(-0.03, 0.03));
                        this.ayG = this.ayG + (this.Rt + threadlocalrandom.nextDouble(-0.025, 0.025));
                        d15 += Math.sin(this.RM) * d29 * (0.5 + threadlocalrandom.nextDouble(0.8));
                        d17 += Math.cos(this.ayG) * d29 * 0.55 * (0.4 + threadlocalrandom.nextDouble(0.7));
                     }

                     double d30 = flag ? 2.0 + threadlocalrandom.nextDouble(1.5) : 3.0;
                     double d19 = flag ? 1.5 + threadlocalrandom.nextDouble(1.0) : 2.5;
                     double d20 = d15 - this.aeX;
                     double d21 = d17 - this.nX;
                     if (Math.abs(d20) > d30) {
                        d15 = this.aeX + Math.signum(d20) * d30;
                     }

                     if (Math.abs(d21) > d19) {
                        d17 = this.nX + Math.signum(d21) * d19;
                     }

                     this.aeX = d15;
                     this.nX = d17;
                     if (flag) {
                        double d22 = this.k();
                        d15 = this.l(d15, d22);
                        d17 = this.l(d17, d22);
                     }

                     if (!(Math.abs(d15) < 0.003) || !(Math.abs(d17) < 0.003)) {
                        float f3 = f + (float)d15;
                        float f2 = MathHelper.clamp(f1 + (float)d17, -90.0F, 90.0F);
                        this.getPlayer().setYaw(f3);
                        this.getPlayer().setPitch(f2);
                     }
                  }
               }
            }
         }
      }
   }

   public void d(DrawContext drawcontext) {
      if (this.isEnabled() && !this.isNotInWorld() && this.drawFovRadiusSetting.getValue()) {
         if (this.getScreen() == null) {
            int i = drawcontext.getScaledWindowWidth();
            int j = drawcontext.getScaledWindowHeight();
            float f = this.e(i, j);
            if (!(f < 1.0F)) {
               float f1 = i * 0.5F;
               float f2 = j * 0.5F;
               int k = this.FD != null ? -866779307 : -855638017;
               dr.i(drawcontext.getMatrices(), f1, f2, f, 1.5F, k);
            }
         }
      }
   }

   private float e(int i, int j) {
      float f = Math.max(0.0F, Math.min(i, j) * 0.5F - 2.0F);
      if (f <= 0.0F) {
         return 0.0F;
      } else {
         double d0 = this.fovSetting.getValue();
         double d1 = MathHelper.clamp(((Integer)this.getClient().options.getFov().getValue()).intValue(), 30.0, 170.0);
         if (d0 >= d1) {
            return f;
         } else {
            double d2 = MathHelper.clamp(d0 * 0.5, 0.1, 89.9);
            double d3 = MathHelper.clamp(d1 * 0.5, 1.0, 89.9);
            double d4 = Math.tan(Math.toRadians(d2)) / Math.tan(Math.toRadians(d3));
            double d5 = d4 * (Math.min(i, j) * 0.5);
            return (float)MathHelper.clamp(d5, 0.0, f);
         }
      }
   }

   private double f(double d0, boolean flag) {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      if (flag) {
         double d2 = switch (this.adW) {
            case 0 -> {
               double d5 = MathHelper.clamp(d0, 0.0, 1.0);
               yield 1.0 - Math.pow(1.0 - d5, this.alh);
            }
            case 1 -> {
               double d4 = MathHelper.clamp(d0, 0.0, 1.0);
               if (d4 < 0.5) {
                  yield Math.pow(2.0 * d4, this.alh) / 2.0;
               } else {
                  yield 1.0 - Math.pow(2.0 * (1.0 - d4), this.alh) / 2.0;
               }
            }
            default -> {
               double d3 = MathHelper.clamp(d0, 0.0, 1.0);
               yield d3 * d3 * (3.0 - 2.0 * d3);
            }
         } * (0.85 + threadlocalrandom.nextDouble(0.3));
         return MathHelper.clamp(d2, 0.05, 1.8);
      } else {
         double d1 = MathHelper.clamp(d0, 0.0, 1.0);
         return 1.0 - Math.pow(1.0 - d1, 2.5);
      }
   }

   private Entity g() {
      if (this.getClientWorld() != null && this.getPlayer() != null) {
         double d0 = this.distanceSetting.getValue();
         double d1 = this.fovSetting.getValue() / 2.0;
         boolean flag = "Здоровье".equals(this.prioritySetting.getFirst());
         Entity entity = null;
         double d2 = Double.MAX_VALUE;
         Vec3d vec3d = this.getPlayer().getEyePos();
         Vec3d vec3d1 = this.getPlayer().getRotationVec(1.0F);

         for (Entity entity1 : this.getClientWorld().getEntities()) {
            if (entity1 != this.getPlayer()
               && entity1 instanceof LivingEntity livingentity
               && !livingentity.isDead()
               && !(livingentity.getHealth() <= 0.0F)
               && (!this.onlyPlayersSetting.getValue() || entity1 instanceof PlayerEntity)
               && !(entity1 instanceof PlayerEntity playerentity && this.isFriendPlayer(playerentity))
               && (this.hitInvisibleSetting.getValue() || !livingentity.isInvisible())) {
               if (this.onlyArmoredSetting.getValue()) {
                  if (!xk.d(livingentity)) {
                     continue;
                  }
               } else if (this.ignoreNakedSetting.getValue() && !xk.d(livingentity)) {
                  continue;
               }

               double d4 = this.getPlayer().distanceTo(entity1);
               if (!(d4 > d0) && !(d4 < 0.5)) {
                  if (this.wallCheckSetting.getValue()) {
                     Vec3d vec3d2 = entity1.getPos().add(0.0, entity1.getHeight() * this.aimHeightSetting.getValue(), 0.0);
                     if (lz.b(vec3d, vec3d2)) {
                        continue;
                     }
                  }

                  double d5 = this.h(vec3d, vec3d1, entity1);
                  if (!(d5 > d1)) {
                     double d3;
                     if (flag) {
                        d3 = livingentity.getHealth();
                     } else {
                        d3 = d4;
                     }

                     if (d3 < d2) {
                        d2 = d3;
                        entity = entity1;
                     }
                  }
               }
            }
         }

         return entity;
      } else {
         return null;
      }
   }

   private double h(Vec3d vec3d, Vec3d vec3d1, Entity entity) {
      Vec3d vec3d2 = entity.getPos().add(0.0, entity.getHeight() * this.aimHeightSetting.getValue(), 0.0);
      Vec3d vec3d3 = vec3d2.subtract(vec3d);
      double d0 = vec3d3.length();
      if (d0 < 0.001) {
         return 0.0;
      } else {
         vec3d3 = vec3d3.multiply(1.0 / d0);
         double d1 = vec3d1.x * vec3d3.x + vec3d1.y * vec3d3.y + vec3d1.z * vec3d3.z;
         d1 = MathHelper.clamp(d1, -1.0, 1.0);
         return Math.toDegrees(Math.acos(d1));
      }
   }

   private double[] i(Entity entity) {
      Vec3d vec3d = this.getPlayer().getEyePos();
      boolean flag = this.microMovementsSetting.getValue();
      double d0 = this.aimHeightSetting.getValue();
      double d1 = flag ? d0 + this.Nk : d0;
      double d2 = 0.0;
      double d3 = 0.0;
      double d4 = 0.0;
      if (this.multipointSetting.getValue()) {
         double d5 = entity.getWidth() * 0.5;
         d2 = this.all * d5;
         d4 = this.aox * d5;
         d3 = this.ef;
      }

      double d13 = MathHelper.clamp(d1 + d3, 0.05, 0.95);
      Vec3d vec3d1 = entity.getPos().add(d2, entity.getHeight() * d13, d4);
      double d6 = vec3d1.x - vec3d.x;
      double d7 = vec3d1.y - vec3d.y;
      double d8 = vec3d1.z - vec3d.z;
      double d9 = Math.sqrt(d6 * d6 + d8 * d8);
      double d10;
      if (d9 < 0.001) {
         d10 = this.getPlayer().getYaw();
      } else {
         d10 = Math.toDegrees(Math.atan2(-d6, d8));
      }

      double d11 = Math.sqrt(d6 * d6 + d7 * d7 + d8 * d8);
      double d12;
      if (d11 < 0.001) {
         d12 = this.getPlayer().getPitch();
      } else {
         d12 = -Math.toDegrees(Math.asin(MathHelper.clamp(d7 / d11, -1.0, 1.0)));
      }

      return new double[]{d10, d12};
   }

   private boolean j() {
      if (this.getPlayer() == null) {
         return false;
      } else {
         Item item = this.getPlayer().getMainHandStack().getItem();
         return item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem || item instanceof MaceItem;
      }
   }

   private double k() {
      return xw.m();
   }

   private double l(double d0, double d1) {
      return xw.n(d0, d1);
   }

   private static double m(double d0) {
      return xw.o(d0);
   }
}
