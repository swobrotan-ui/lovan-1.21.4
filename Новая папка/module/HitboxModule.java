package module;

import core.ClientMain;
import core.LicenseManager;
import enum.Category;
import event.RotationEvent;
import event.UseItemEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import setting.ActionKeySetting;
import setting.BooleanSetting;
import setting.GroupSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class HitboxModule extends Module {
   boolean aaU = LicenseManager.f("Настройки обхода");
   private static String ajQ = "Обычная";
   private static String Uc = "Новая";
   private static String lh = "Игроки";
   private static String axc = "Мобы";
   private static String EG = "Все";
   private ListSetting targetsSetting = new ListSetting("Цели", "Какие сущности увеличивать", Arrays.<String>asList(axc, lh, EG), List.<String>of(lh), false);
   private BooleanSetting onlyWithWeaponSetting = new BooleanSetting("Только с оружием", "Работает только при оружии в руке", true);
   private BooleanSetting onlyArmoredSetting = new BooleanSetting("Только в броне", "Работает только на игроках в броне", false);
   private BooleanSetting onlyTargetSetting = new BooleanSetting("Только на противнике", "Увеличивать хитбокс только для последней атакованной цели", false);
   private SliderSetting resetTimeSetting = new SliderSetting("Время сброса", "Время в секундах до сброса цели", 3.0, 1.0, 7.0, 0.5);
   private BooleanSetting invisibleHitboxSetting = new BooleanSetting("Невидимый хитбокс", "Не показывать увеличенный хитбокс", false);
   private BooleanSetting hideIndicatorSetting = new BooleanSetting("Скрыть индикатор", "Скрыть индикатор атаки на увеличенном хитбоксе", true);
   private GroupSetting sizeSettingsGroup = new GroupSetting("Настройки размера", "Параметры хитбокса", true);
   private BooleanSetting fixedSizeSetting = new BooleanSetting("Фиксированный размер", "Использовать фиксированный размер хитбокса", true);
   private SliderSetting fixedSizeValueSetting = new SliderSetting("Фикс. размер", "Фиксированный размер хитбокса", 0.7, 0.3, 1.2, 0.05);
   private SliderSetting minSizeSetting = new SliderSetting("Мин. размер", "Минимальный размер хитбокса", 0.3, 0.2, 1.0, 0.05);
   private SliderSetting maxSizeSetting = new SliderSetting("Макс. размер", "Максимальный размер хитбокса", 2.0, 0.5, 5.0, 0.1);
   private SliderSetting stepSetting = new SliderSetting("Шаг", "Шаг изменения размера", 0.1, 0.05, 0.5, 0.05);
   private BooleanSetting animationSetting = new BooleanSetting("Анимация", "Плавное изменение размера хитбокса", true);
   private SliderSetting animationSpeedSetting = new SliderSetting("Скорость анимации", "Скорость плавного изменения размера хитбокса", 6.0, 1.0, 15.0, 0.5);
   private SliderSetting sizeSetting = new SliderSetting(
      "Размер", "Размер хитбокса", 0.3F, this.minSizeSetting.getValue(), this.maxSizeSetting.getValue(), this.stepSetting.getValue()
   );
   private ListSetting rotationModeSetting = new ListSetting("Режим ротации", "Тип ротации обхода", Arrays.<String>asList(ajQ, Uc), List.<String>of(ajQ), false);
   private GroupSetting bypassSettingsGroup = new GroupSetting("Настройки обхода", "Автоматическое наведение на цель", false);
   private BooleanSetting bypassSetting = new BooleanSetting("Обход", "Включить автоприцел на цели", this.aaU);
   private BooleanSetting antiReachSetting = new BooleanSetting("Анти-Reach", "Отменяет атаки дальше 3 блоков при увеличенном хитбоксе", this.aaU);
   private SliderSetting antiReachDistanceSetting = new SliderSetting(
      "Дистанция Анти-Reach", "Максимальная дистанция для атаки при увеличенном хитбоксе", 2.85, 1.0, 3.0, 0.01
   );
   private BooleanSetting fakeSwingSetting = new BooleanSetting("Фейк свинг", "Визуальный свинг при отмене удара", true);
   private SliderSetting searchDistanceSetting = new SliderSetting("Дистанция", "Дистанция поиска цели", 5.0, 3.0, 10.0, 0.5);
   private SliderSetting aimSpeedSetting = new SliderSetting("Скорость наводки", "Скорость наведения на оригинальный хитбокс и возврата", 0.85, 0.1, 2.0, 0.05);
   private SliderSetting trackingSpeedSetting = new SliderSetting("Скорость слежения", "Скорость поворота камеры при движении цели", 0.85, 0.1, 2.0, 0.05);
   private GroupSetting hotkeysGroup = new GroupSetting("Горячие клавиши", "Кнопки управления размером", false);
   private ActionKeySetting resetSizeKeySetting = new ActionKeySetting("Сбросить размер", "Сбросить размер хитбокса", 61, this::N);
   private ActionKeySetting increaseSizeKeySetting = new ActionKeySetting("Увеличить размер", "Увеличить хитбокс", 265, this::O);
   private ActionKeySetting decreaseSizeKeySetting = new ActionKeySetting("Уменьшить размер", "Уменьшить хитбокс", 264, this::P);
   private double Js = 0.3;
   private LivingEntity zU = null;
   private Vec3d WZ = null;
   private LivingEntity agO = null;
   private long oP = 0L;
   private boolean aul = false;
   private boolean abx = false;
   private long acL = 0L;
   private float AN = 0.0F;
   private float awL = 0.0F;
   private boolean dY = false;
   private boolean aiu = false;
   private boolean OZ = false;
   private boolean abl = false;
   private static double aer = 0.5;
   private float agF = 0.3F;
   private long gF = 0L;
   private boolean YS = false;
   private long bZ;
   private long em;
   private long abX;
   private LivingEntity ahp;
   private boolean amy;
   private GLFWMouseButtonCallback aeZ;

   public HitboxModule() {
      super("Хитбо$", "Увеличивает размер хитбокса игрока", Category.COMBAT);
      this.sizeSettingsGroup
         .addSettings(this.minSizeSetting, this.maxSizeSetting, this.stepSetting, this.sizeSetting, this.animationSetting, this.animationSpeedSetting);
      if (this.aaU) {
         this.bypassSettingsGroup
            .addSettings(
               this.rotationModeSetting,
               this.bypassSetting,
               this.antiReachSetting,
               this.antiReachDistanceSetting,
               this.fakeSwingSetting,
               this.searchDistanceSetting,
               this.aimSpeedSetting,
               this.trackingSpeedSetting
            );
         this.addSetting(this.bypassSettingsGroup);
      }

      this.hotkeysGroup.addSettings(this.resetSizeKeySetting, this.increaseSizeKeySetting, this.decreaseSizeKeySetting);
      this.addSettings(
         this.targetsSetting,
         this.onlyWithWeaponSetting,
         this.onlyArmoredSetting,
         this.fixedSizeSetting,
         this.fixedSizeValueSetting,
         this.onlyTargetSetting,
         this.resetTimeSetting,
         this.invisibleHitboxSetting,
         this.hideIndicatorSetting,
         this.hotkeysGroup,
         this.sizeSettingsGroup
      );
      this.fixedSizeValueSetting.setVisibilitySupplier(this.fixedSizeSetting::getValue);
      this.resetTimeSetting.setVisibilitySupplier(this.onlyTargetSetting::getValue);
      this.minSizeSetting.setVisibilitySupplier(() -> {
         return !this.fixedSizeSetting.getValue();
      });
      this.maxSizeSetting.setVisibilitySupplier(() -> {
         return !this.fixedSizeSetting.getValue();
      });
      this.stepSetting.setVisibilitySupplier(() -> {
         return !this.fixedSizeSetting.getValue();
      });
      this.sizeSetting.setVisibilitySupplier(() -> {
         return !this.fixedSizeSetting.getValue();
      });
      this.animationSpeedSetting.setVisibilitySupplier(this.animationSetting::getValue);
      if (this.aaU) {
         this.antiReachDistanceSetting.setVisibilitySupplier(this.antiReachSetting::getValue);
         this.searchDistanceSetting.setVisibilitySupplier(this.bypassSetting::getValue);
         this.aimSpeedSetting.setVisibilitySupplier(this.bypassSetting::getValue);
         this.trackingSpeedSetting.setVisibilitySupplier(this.bypassSetting::getValue);
         this.rotationModeSetting.setVisibilitySupplier(this.bypassSetting::getValue);
      }

      this.a();
      this.c();
   }

   private void a() {
      this.fixedSizeSetting.setOnChange(() -> {
         if (this.isEnabled()) {
            if (this.fixedSizeSetting.getValue()) {
               this.Js = this.sizeSetting.getValue();
               this.sizeSetting.setValue(this.fixedSizeValueSetting.getValue());
               return;
            }

            this.sizeSetting.setValue(this.Js);
         }
      });
      this.fixedSizeValueSetting.setOnChange(() -> {
         if (this.isEnabled() && this.fixedSizeSetting.getValue()) {
            this.sizeSetting.setValue(this.fixedSizeValueSetting.getValue());
         }
      });
      this.sizeSetting.setOnChange(() -> {
         if (this.isEnabled() && !this.fixedSizeSetting.getValue() && !this.abx && !this.YS) {
            this.Js = this.sizeSetting.getValue();
         }
      });
   }

   @Override
   public synchronized void setEnabled(boolean flag) {
      if (!flag && this.isEnabled() && !this.YS) {
         if (!this.fixedSizeSetting.getValue()) {
            this.Js = this.sizeSetting.getValue();
         }

         if (!this.animationSetting.getValue()) {
            this.sizeSetting.setValue(0.3F);
            this.agF = 0.3F;
            this.h();
            this.e();
            super.setEnabled(false);
         } else {
            this.YS = true;
            this.sizeSetting.setValue(0.3F);
            this.h();
            this.e();
         }
      } else if (flag && this.YS) {
         this.YS = false;
         double d0 = this.fixedSizeSetting.getValue() ? this.fixedSizeValueSetting.getValue() : this.Js;
         this.sizeSetting.setValue(d0);
      } else {
         this.YS = false;
         super.setEnabled(flag);
      }
   }

   @Override
   public void onEnable() {
      if (this.fixedSizeSetting.getValue()) {
         this.sizeSetting.setValue(this.fixedSizeValueSetting.getValue());
      } else {
         this.sizeSetting.setValue(this.Js);
      }

      this.agF = 0.3F;
      this.gF = System.nanoTime();
      this.h();
      this.e();
   }

   @Override
   public void onDisable() {
      this.sizeSetting.setValue(0.3F);
      this.agF = 0.3F;
      this.h();
      this.e();
      this.b();
   }

   private void b() {
      if (this.getWorld() != null) {
         for (Entity entity : this.getClientWorld().getEntities()) {
            if (entity instanceof LivingEntity livingentity && livingentity != this.getPlayer()) {
               entity.setBoundingBox(this.r(entity));
            }
         }
      }
   }

   private void c() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient != null && minecraftclient.getWindow() != null) {
         long i = minecraftclient.getWindow().getHandle();
         this.aeZ = GLFW.glfwSetMouseButtonCallback(i, null);
         GLFW.glfwSetMouseButtonCallback(i, (j, k, l, i1) -> {
            if (k == 1) {
               if (l == 1) {
                  if (!this.isEnabled() || !this.bypassSetting.getValue()) {
                     if (this.aeZ != null) {
                        this.aeZ.invoke(j, k, l, i1);
                     }

                     return;
                  }

                  if (!this.K()) {
                     if (this.aeZ != null) {
                        this.aeZ.invoke(j, k, l, i1);
                     }

                     return;
                  }

                  double d0 = this.fixedSizeSetting.getValue() ? this.fixedSizeValueSetting.getValue() : this.Js;
                  double d1 = this.sizeSetting.getValue();
                  if (d1 <= 0.3) {
                     this.sizeSetting.setValue(d0);
                  }

                  this.abx = true;
                  this.acL = System.currentTimeMillis();
                  this.aU(true);
                  this.h();
                  this.aW(0.0F);
                  this.sizeSetting.setValue(0.3F);
                  ClientMain.getInstance().getExecutor().schedule(() -> {
                     if (this.isEnabled()) {
                        this.sizeSetting.setValue(d0);
                        this.aU(false);
                        this.abx = false;
                        this.acL = 0L;
                     }
                  }, 500L, TimeUnit.MILLISECONDS);
               } else if (l == 0 && this.abx) {
                  this.abx = false;
                  this.acL = 0L;
                  this.aU(false);
                  double d2 = this.fixedSizeSetting.getValue() ? this.fixedSizeValueSetting.getValue() : this.Js;
                  ClientMain.getInstance().getExecutor().schedule(() -> {
                     if (this.isEnabled()) {
                        this.sizeSetting.setValue(d2);
                     }
                  }, 50L, TimeUnit.MILLISECONDS);
               }
            }

            if (this.aeZ != null) {
               this.aeZ.invoke(j, k, l, i1);
            }
         });
      }
   }

   @Override
   public void onAttackEntity(PlayerEntity playerentity, World world, Hand hand, Entity entity, EntityHitResult entityhitresult) {
      if (this.isEnabled() && this.onlyTargetSetting.getValue()) {
         if (entity instanceof LivingEntity livingentity && this.Q(livingentity)) {
            this.agO = livingentity;
            this.oP = System.currentTimeMillis();
         }
      }
   }

   @Override
   public void onUseItem(UseItemEvent useitemevent) {
      if (this.isEnabled()) {
         HitResult hitresult = useitemevent.getTarget();
         if (hitresult instanceof EntityHitResult entityhitresult
            && entityhitresult.getEntity() instanceof LivingEntity livingentity
            && this.onlyTargetSetting.getValue()
            && this.Q(livingentity)) {
            this.agO = livingentity;
            this.oP = System.currentTimeMillis();
         }

         if (this.aaU
            && this.bypassSetting.getValue()
            && Uc.equals(this.rotationModeSetting.getFirst())
            && this.sizeSetting.getFloatValue() > 0.3F
            && hitresult instanceof EntityHitResult entityhitresult1
            && entityhitresult1.getEntity() instanceof LivingEntity livingentity1) {
            if (!this.Q(livingentity1)) {
               return;
            }

            boolean flag = !this.onlyWithWeaponSetting.getValue() || this.D();
            if (this.u(livingentity1, flag) > this.s(livingentity1) && !this.H(livingentity1) && !this.J(livingentity1, false)) {
               useitemevent.setCancelled(true);
               if (this.fakeSwingSetting.getValue()) {
                  this.getClientPlayer().swingHand(this.getMainHand());
                  this.getClientPlayer().resetLastAttackedTicks();
               }

               return;
            }
         }

         if (this.antiReachSetting.getValue()) {
            float f = this.sizeSetting.getFloatValue();
            if (!(f <= 0.3F) && hitresult instanceof EntityHitResult entityhitresult2) {
               if (entityhitresult2.getEntity() instanceof LivingEntity livingentity2) {
                  if (this.Q(livingentity2)) {
                     if (!(livingentity2 instanceof PlayerEntity playerentity && this.onlyArmoredSetting.getValue() && !this.F(playerentity))) {
                        Box box = this.r(livingentity2);
                        Vec3d vec3d = this.getPlayer().getEyePos();
                        Vec3d vec3d1 = this.p(vec3d, box);
                        double d0 = vec3d.distanceTo(vec3d1);
                        if (d0 > this.antiReachDistanceSetting.getValue()) {
                           useitemevent.setCancelled(true);
                           if (this.fakeSwingSetting.getValue()) {
                              this.getClientPlayer().swingHand(this.getMainHand());
                              this.getClientPlayer().resetLastAttackedTicks();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void onRotation(RotationEvent rotationevent) {
      if (this.isEnabled()) {
         if (this.getPlayer() != null && this.getPlayer().isUsingItem() && !this.abx) {
            this.abx = true;
            this.acL = System.currentTimeMillis();
            if (!this.fixedSizeSetting.getValue()) {
               this.Js = this.sizeSetting.getValue();
            }

            this.sizeSetting.setValue(0.3F);
         } else if (this.getPlayer() != null && !this.getPlayer().isUsingItem() && this.abx && this.acL > 0L) {
            long i = System.currentTimeMillis() - this.acL;
            if (i > 50L) {
               this.abx = false;
               this.acL = 0L;
               double d0 = this.fixedSizeSetting.getValue() ? this.fixedSizeValueSetting.getValue() : this.Js;
               this.sizeSetting.setValue(d0);
            }
         }

         float f4 = this.sizeSetting.getFloatValue();
         if (f4 <= 0.3F && !this.aul) {
            if (this.aiu) {
               this.h();
            }
         } else {
            this.d();
            if (!this.bypassSetting.getValue() || this.getPlayer() == null || this.getWorld() == null) {
               this.g(rotationevent);
            } else if (this.onlyWithWeaponSetting.getValue() && !this.D()) {
               this.g(rotationevent);
            } else {
               LivingEntity livingentity = this.G();
               if (livingentity != null) {
                  boolean flag2 = this.J(livingentity, true);
                  if (!flag2 && Uc.equals(this.rotationModeSetting.getFirst())) {
                     flag2 = this.I(livingentity, aer);
                  }

                  boolean flag = this.J(livingentity, false);
                  if (!flag && flag2) {
                     boolean flag1 = this.z(livingentity);
                     this.zU = livingentity;
                     this.aiu = true;
                     if (!this.dY) {
                        this.AN = this.getPlayer().getYaw();
                        this.awL = this.getPlayer().getPitch();
                        this.dY = true;
                        this.OZ = true;
                     }

                     if (flag1) {
                        Vec3d vec3d = this.A(this.zU);
                        if (vec3d != null) {
                           this.WZ = vec3d;
                           this.abl = true;
                        } else {
                           this.WZ = this.o(this.zU);
                           this.abl = false;
                        }
                     } else {
                        this.WZ = this.o(this.zU);
                        this.abl = false;
                     }

                     if (Uc.equals(this.rotationModeSetting.getFirst())) {
                        this.j(rotationevent);
                     } else {
                        float f5 = this.x(this.WZ);
                        float f = Math.abs(this.M(f5 - this.AN));
                        if (f > 2.0F) {
                           this.OZ = true;
                           this.AN = this.C(this.AN, f5, this.aimSpeedSetting.getValue());
                        } else {
                           this.OZ = false;
                           this.AN = this.C(this.AN, f5, this.trackingSpeedSetting.getValue());
                        }

                        float f1;
                        if (this.abl) {
                           float f2 = this.y(this.WZ);
                           float f3 = Math.abs(f2 - this.awL);
                           if (f3 > 2.0F) {
                              this.awL = this.C(this.awL, f2, this.aimSpeedSetting.getValue());
                           } else {
                              this.awL = this.C(this.awL, f2, this.trackingSpeedSetting.getValue());
                           }

                           f1 = this.awL;
                        } else {
                           float f6 = this.getPlayer().getPitch();
                           float f7 = Math.abs(f6 - this.awL);
                           if (f7 > 1.0F) {
                              this.awL = this.C(this.awL, f6, this.aimSpeedSetting.getValue());
                              f1 = this.awL;
                           } else {
                              this.awL = f6;
                              f1 = f6;
                           }
                        }

                        rotationevent.setPitch(f1);
                        rotationevent.setYaw(this.AN);
                        rotationevent.setStrictMoveCorrection(true);
                     }
                  } else {
                     this.g(rotationevent);
                  }
               } else {
                  this.g(rotationevent);
               }
            }
         }
      }
   }

   private void d() {
      if (this.onlyTargetSetting.getValue() && this.agO != null) {
         long i = System.currentTimeMillis();
         long j = i - this.oP;
         long k = (long)(this.resetTimeSetting.getValue() * 1000.0);
         if (j >= k) {
            this.e();
         }
      }
   }

   private void e() {
      this.agO = null;
      this.oP = 0L;
   }

   private boolean f(LivingEntity livingentity) {
      return !this.onlyTargetSetting.getValue() || livingentity == this.agO;
   }

   private void g(RotationEvent rotationevent) {
      if (this.aiu) {
         float f = this.getPlayer().getYaw();
         float f1 = this.getPlayer().getPitch();
         float f2 = Math.abs(this.M(this.AN - f));
         float f3 = Math.abs(this.awL - f1);
         if (Uc.equals(this.rotationModeSetting.getFirst())) {
            if (!this.amy) {
               this.amy = true;
               this.abX = System.currentTimeMillis();
            }

            this.k(rotationevent, f, f1);
            if (f2 < 5.0F && f3 < 5.0F) {
               this.h();
            }
         } else {
            this.AN = this.C(this.AN, f, this.aimSpeedSetting.getValue());
            this.awL = this.C(this.awL, f1, this.aimSpeedSetting.getValue());
            rotationevent.setPitch(this.awL);
            rotationevent.setYaw(this.AN);
            rotationevent.setStrictMoveCorrection(true);
            if (f2 < 5.0F && f3 < 5.0F) {
               this.h();
            }
         }
      }
   }

   private void h() {
      this.zU = null;
      this.WZ = null;
      this.dY = false;
      this.aiu = false;
      this.OZ = false;
      this.abl = false;
      this.i();
   }

   private void i() {
      this.bZ = 0L;
      this.em = 0L;
      this.abX = 0L;
      this.ahp = null;
      this.amy = false;
   }

   private void j(RotationEvent rotationevent) {
      long i = System.nanoTime();
      double d0 = this.bZ > 0L ? (i - this.bZ) / 1.66666667E7 : 1.0;
      d0 = MathHelper.clamp(d0, 0.05, 3.0);
      this.bZ = i;
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      if (this.zU != this.ahp) {
         this.ahp = this.zU;
         this.em = System.currentTimeMillis();
         this.amy = false;
         this.abX = 0L;
      }

      float f = this.x(this.WZ);
      float f1 = this.abl ? this.y(this.WZ) : this.getPlayer().getPitch();
      double d1 = n(f - this.AN);
      double d2 = (double)f1 - this.awL;
      double d3 = Math.sqrt(d1 * d1 + d2 * d2);
      if (d3 < 0.05) {
         rotationevent.setPitch(this.awL);
         rotationevent.setYaw(this.AN);
         rotationevent.setStrictMoveCorrection(true);
      } else {
         double d4 = this.l();
         if (d3 > 2.0) {
            this.OZ = true;
         } else {
            this.OZ = false;
         }

         double d5 = this.OZ ? this.aimSpeedSetting.getValue() : this.trackingSpeedSetting.getValue();
         double d6 = MathHelper.clamp((System.currentTimeMillis() - this.em) / 200.0, 0.0, 1.0);
         double d7 = d6 * d6 * (3.0 - 2.0 * d6);
         double d8 = MathHelper.clamp(d3 / 15.0, 0.15, 1.0);
         double d9 = d5 * d7 * d8 * d0 * (0.9 + threadlocalrandom.nextDouble(0.2));
         double d10 = d1 * d9;
         double d11 = d2 * d9;
         d10 = this.m(d10, d4);
         d11 = this.m(d11, d4);
         if (d10 == 0.0 && Math.abs(d1) > d4) {
            d10 = Math.signum(d1) * d4;
         }

         if (d11 == 0.0 && Math.abs(d2) > d4) {
            d11 = Math.signum(d2) * d4;
         }

         if (Math.abs(d10) > Math.abs(d1)) {
            d10 = this.m(d1, d4);
         }

         if (Math.abs(d11) > Math.abs(d2)) {
            d11 = this.m(d2, d4);
         }

         this.AN += (float)d10;
         this.awL = MathHelper.clamp(this.awL + (float)d11, -90.0F, 90.0F);
         rotationevent.setPitch(this.awL);
         rotationevent.setYaw(this.AN);
         rotationevent.setStrictMoveCorrection(true);
      }
   }

   private void k(RotationEvent rotationevent, float f, float f1) {
      long i = System.nanoTime();
      double d0 = this.bZ > 0L ? (i - this.bZ) / 1.66666667E7 : 1.0;
      d0 = MathHelper.clamp(d0, 0.05, 3.0);
      this.bZ = i;
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      double d1 = n(f - this.AN);
      double d2 = (double)f1 - this.awL;
      double d3 = Math.sqrt(d1 * d1 + d2 * d2);
      if (d3 < 0.05) {
         rotationevent.setPitch(this.awL);
         rotationevent.setYaw(this.AN);
         rotationevent.setStrictMoveCorrection(true);
      } else {
         double d4 = this.l();
         if (d3 > 2.0) {
            this.OZ = true;
         } else {
            this.OZ = false;
         }

         double d5 = this.OZ ? this.aimSpeedSetting.getValue() : this.trackingSpeedSetting.getValue();
         double d6 = MathHelper.clamp((System.currentTimeMillis() - this.abX) / 200.0, 0.0, 1.0);
         double d7 = d6 * d6 * (3.0 - 2.0 * d6);
         double d8 = MathHelper.clamp(d3 / 15.0, 0.15, 1.0);
         double d9 = d5 * d7 * d8 * d0 * (0.9 + threadlocalrandom.nextDouble(0.2));
         double d10 = d1 * d9;
         double d11 = d2 * d9;
         d10 = this.m(d10, d4);
         d11 = this.m(d11, d4);
         if (d10 == 0.0 && Math.abs(d1) > d4) {
            d10 = Math.signum(d1) * d4;
         }

         if (d11 == 0.0 && Math.abs(d2) > d4) {
            d11 = Math.signum(d2) * d4;
         }

         if (Math.abs(d10) > Math.abs(d1)) {
            d10 = this.m(d1, d4);
         }

         if (Math.abs(d11) > Math.abs(d2)) {
            d11 = this.m(d2, d4);
         }

         this.AN += (float)d10;
         this.awL = MathHelper.clamp(this.awL + (float)d11, -90.0F, 90.0F);
         rotationevent.setPitch(this.awL);
         rotationevent.setYaw(this.AN);
         rotationevent.setStrictMoveCorrection(true);
      }
   }

   private double l() {
      double d0 = (Double)MinecraftClient.getInstance().options.getMouseSensitivity().getValue();
      double d1 = d0 * 0.6 + 0.2;
      return d1 * d1 * d1 * 1.2;
   }

   private double m(double d0, double d1) {
      return d1 <= 1.0E-4 ? d0 : d0 - d0 % d1;
   }

   private static double n(double d0) {
      d0 %= 360.0;
      if (d0 > 180.0) {
         d0 -= 360.0;
      }

      if (d0 < -180.0) {
         d0 += 360.0;
      }

      return d0;
   }

   private Vec3d o(LivingEntity livingentity) {
      Vec3d vec3d = this.getPlayer().getEyePos();
      Vec3d vec3d1 = this.getPlayer().getRotationVec(1.0F);
      Box box = this.r(livingentity);
      double d0 = 0.2F;
      Box box1 = new Box(box.minX + d0, box.minY, box.minZ + d0, box.maxX - d0, box.maxY, box.maxZ - d0);
      Box box2 = this.t(livingentity);
      double d1 = this.searchDistanceSetting.getValue();
      Vec3d vec3d2 = vec3d.add(vec3d1.multiply(d1));
      Vec3d vec3d3 = box2.raycast(vec3d, vec3d2).orElse(box2.getCenter());
      return this.p(vec3d3, box1);
   }

   private Vec3d p(Vec3d vec3d, Box box) {
      double d0 = Math.max(box.minX, Math.min(vec3d.x, box.maxX));
      double d1 = Math.max(box.minY, Math.min(vec3d.y, box.maxY));
      double d2 = Math.max(box.minZ, Math.min(vec3d.z, box.maxZ));
      return new Vec3d(d0, d1, d2);
   }

   private void q(LivingEntity livingentity, float f) {
      livingentity.setBoundingBox(
         new Box(
            livingentity.getX() - f,
            livingentity.getBoundingBox().minY,
            livingentity.getZ() - f,
            livingentity.getX() + f,
            livingentity.getBoundingBox().maxY,
            livingentity.getZ() + f
         )
      );
   }

   private Box r(Entity entity) {
      return entity.getType().getDimensions().getBoxAt(entity.getPos());
   }

   private float s(Entity entity) {
      Box box = this.r(entity);
      return (float)(box.getLengthX() / 2.0);
   }

   private Box t(Entity entity) {
      float f = this.s(entity) + (this.sizeSetting.getFloatValue() - 0.3F);
      return new Box(entity.getX() - f, entity.getBoundingBox().minY, entity.getZ() - f, entity.getX() + f, entity.getBoundingBox().maxY, entity.getZ() + f);
   }

   @Override
   public void onRenderBeforeEntities(WorldRenderContext worldrendercontext) {
      this.w();
      boolean flag = !this.onlyWithWeaponSetting.getValue() || this.D();

      for (Entity entity : worldrendercontext.world().getEntities()) {
         if (entity instanceof LivingEntity livingentity && livingentity != this.getPlayer() && this.Q(livingentity)) {
            if (livingentity instanceof PlayerEntity playerentity && this.isFriendPlayer(playerentity)) {
               this.q(playerentity, 0.3F);
            } else {
               float f = this.v(livingentity, flag);
               this.q(livingentity, f);
            }
         }
      }
   }

   @Override
   public void onRenderAfterEntities(WorldRenderContext worldrendercontext) {
      boolean flag = !this.onlyWithWeaponSetting.getValue() || this.D();

      for (Entity entity : worldrendercontext.world().getEntities()) {
         if (entity instanceof LivingEntity livingentity && livingentity != this.getPlayer() && this.Q(livingentity)) {
            if (livingentity instanceof PlayerEntity playerentity && this.isFriendPlayer(playerentity)) {
               this.q(playerentity, 0.0F);
            } else {
               float f = this.u(livingentity, flag);
               this.q(livingentity, f);
            }
         }
      }
   }

   private float u(LivingEntity livingentity, boolean flag) {
      float f = this.s(livingentity);
      if (!flag) {
         return f;
      } else if (livingentity instanceof PlayerEntity playerentity && this.onlyArmoredSetting.getValue() && !this.F(playerentity)) {
         return f;
      } else {
         return !this.f(livingentity) ? f : f + (this.sizeSetting.getFloatValue() - 0.3F);
      }
   }

   private float v(LivingEntity livingentity, boolean flag) {
      float f = this.s(livingentity);
      if (!flag) {
         return f;
      } else if (livingentity instanceof PlayerEntity playerentity && this.onlyArmoredSetting.getValue() && !this.F(playerentity)) {
         return f;
      } else if (!this.f(livingentity)) {
         return f;
      } else if (this.invisibleHitboxSetting.getValue()) {
         return f;
      } else {
         return !this.animationSetting.getValue() ? f + (this.sizeSetting.getFloatValue() - 0.3F) : f + (this.agF - 0.3F);
      }
   }

   private void w() {
      long i = System.nanoTime();
      if (this.gF != 0L && i - this.gF <= NANOS_PER_SECOND) {
         float f = (float)(i - this.gF) / (float)NANOS_PER_SECOND;
         this.gF = i;
         float f1 = this.invisibleHitboxSetting.getValue() ? 0.3F : this.sizeSetting.getFloatValue();
         float f2 = f1 - this.agF;
         float f3 = this.animationSpeedSetting.getFloatValue();
         if (Math.abs(f2) > 0.001F) {
            float f4 = 1.0F - (float)Math.exp(-f * f3);
            this.agF += f2 * f4;
         } else {
            this.agF = f1;
         }

         if (this.YS && Math.abs(this.agF - 0.3F) < 0.01F) {
            this.agF = 0.3F;
            this.YS = false;
            super.setEnabled(false);
         }
      } else {
         this.gF = i;
      }
   }

   private float x(Vec3d vec3d) {
      Vec3d vec3d1 = this.getPlayer().getEyePos();
      if (this.zU != null) {
         Vec3d vec3d2 = this.zU.getVelocity();
         vec3d = vec3d.add(vec3d2.multiply(0.1));
      }

      double d1 = vec3d.x - vec3d1.x;
      double d0 = vec3d.z - vec3d1.z;
      return (float)(Math.atan2(d0, d1) * 180.0 / Math.PI) - 90.0F;
   }

   private float y(Vec3d vec3d) {
      Vec3d vec3d1 = this.getPlayer().getEyePos();
      double d0 = vec3d.x - vec3d1.x;
      double d1 = vec3d.y - vec3d1.y;
      double d2 = vec3d.z - vec3d1.z;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      return (float)(-(Math.atan2(d1, d3) * 180.0 / Math.PI));
   }

   private boolean z(LivingEntity livingentity) {
      if (livingentity != null && this.getPlayer() != null) {
         Vec3d vec3d = this.getPlayer().getEyePos();
         Box box = this.r(livingentity);
         float f = this.getPlayer().getPitch();
         double d0 = this.B(livingentity, vec3d, f);
         return d0 < box.minY || d0 > box.maxY;
      } else {
         return false;
      }
   }

   private Vec3d A(LivingEntity livingentity) {
      if (livingentity != null && this.getPlayer() != null) {
         Vec3d vec3d = this.getPlayer().getEyePos();
         Box box = this.r(livingentity);
         float f = this.getPlayer().getPitch();
         double d0 = this.B(livingentity, vec3d, f);
         double d1;
         if (d0 < box.minY) {
            d1 = box.minY + 0.1;
         } else {
            if (!(d0 > box.maxY)) {
               return null;
            }

            d1 = box.maxY - 0.1;
         }

         double d2 = livingentity.getX();
         double d3 = livingentity.getZ();
         return new Vec3d(d2, d1, d3);
      } else {
         return null;
      }
   }

   private double B(LivingEntity livingentity, Vec3d vec3d, float f) {
      double d0 = livingentity.getX() - vec3d.x;
      double d1 = livingentity.getZ() - vec3d.z;
      double d2 = Math.sqrt(d0 * d0 + d1 * d1);
      double d3 = Math.toRadians(f);
      return d2 < 0.5 ? vec3d.y - Math.tan(d3) * 0.5 : vec3d.y - Math.tan(d3) * d2;
   }

   private float C(float f, float f1, double d0) {
      return f + this.M(f1 - f) * (float)d0;
   }

   private boolean D() {
      return this.E(this.getPlayer().getMainHandStack()) || this.E(this.getPlayer().getOffHandStack());
   }

   private boolean E(ItemStack itemstack) {
      return bp.b(itemstack);
   }

   private boolean F(PlayerEntity playerentity) {
      return !((ItemStack)playerentity.getInventory().armor.get(0)).isEmpty()
         || !((ItemStack)playerentity.getInventory().armor.get(1)).isEmpty()
         || !((ItemStack)playerentity.getInventory().armor.get(2)).isEmpty()
         || !((ItemStack)playerentity.getInventory().armor.get(3)).isEmpty();
   }

   private LivingEntity G() {
      LivingEntity livingentity = null;
      double d0 = this.searchDistanceSetting.getValue();

      for (Entity entity : this.getClientWorld().getEntities()) {
         if (entity instanceof LivingEntity livingentity1 && livingentity1 != this.getPlayer() && this.L(livingentity1) && this.f(livingentity1)) {
            double d1 = this.getPlayer().distanceTo(livingentity1);
            if (d1 < d0) {
               d0 = d1;
               livingentity = livingentity1;
            }
         }
      }

      return livingentity;
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      super.onPlayerDeath(playerentity);
      if (this.enabledSetting.getValue()) {
         this.setEnabled(false);
      }
   }

   private boolean H(LivingEntity livingentity) {
      Vec3d vec3d = this.getPlayer().getEyePos();
      float f = this.awL * (float) (Math.PI / 180.0);
      float f1 = -this.AN * (float) (Math.PI / 180.0);
      float f2 = MathHelper.cos(f);
      float f3 = MathHelper.sin(f);
      float f4 = MathHelper.cos(f1);
      float f5 = MathHelper.sin(f1);
      Vec3d vec3d1 = new Vec3d(f5 * f2, -f3, f4 * f2);
      double d0 = this.searchDistanceSetting.getValue();
      Vec3d vec3d2 = vec3d.add(vec3d1.multiply(d0));
      Box box = this.r(livingentity);
      Box box1 = Uc.equals(this.rotationModeSetting.getFirst()) ? box.expand(0.0, aer, 0.0) : box;
      return box1.contains(vec3d) ? true : box1.raycast(vec3d, vec3d2).isPresent();
   }

   private boolean I(Entity entity, double d0) {
      Vec3d vec3d = this.getPlayer().getEyePos();
      Vec3d vec3d1 = this.getPlayer().getRotationVec(1.0F);
      double d1 = this.searchDistanceSetting.getValue();
      Vec3d vec3d2 = vec3d.add(vec3d1.multiply(d1));
      Box box = this.t(entity).expand(0.0, d0, 0.0);
      if (box.contains(vec3d)) {
         double d2 = entity.getX() - vec3d.x;
         double d3 = entity.getZ() - vec3d.z;
         double d4 = vec3d1.x;
         double d5 = vec3d1.z;
         double d6 = Math.sqrt(d2 * d2 + d3 * d3);
         double d7 = Math.sqrt(d4 * d4 + d5 * d5);
         if (!(d6 < 0.01) && !(d7 < 0.01)) {
            double d8 = d2 / d6 * (d4 / d7) + d3 / d6 * (d5 / d7);
            return d8 > -0.5;
         } else {
            return true;
         }
      } else {
         return box.raycast(vec3d, vec3d2).isPresent();
      }
   }

   private boolean J(Entity entity, boolean flag) {
      Vec3d vec3d = this.getPlayer().getEyePos();
      Vec3d vec3d1 = this.getPlayer().getRotationVec(1.0F);
      double d0 = this.searchDistanceSetting.getValue();
      Vec3d vec3d2 = vec3d.add(vec3d1.multiply(d0));
      Box box = flag ? this.t(entity) : this.r(entity);
      if (box.contains(vec3d)) {
         double d1 = entity.getX() - vec3d.x;
         double d2 = entity.getZ() - vec3d.z;
         double d3 = vec3d1.x;
         double d4 = vec3d1.z;
         double d5 = Math.sqrt(d1 * d1 + d2 * d2);
         double d6 = Math.sqrt(d3 * d3 + d4 * d4);
         if (!(d5 < 0.01) && !(d6 < 0.01)) {
            double d7 = d1 / d5 * (d3 / d6) + d2 / d5 * (d4 / d6);
            return d7 > -0.5;
         } else {
            return true;
         }
      } else {
         return box.raycast(vec3d, vec3d2).isPresent();
      }
   }

   public boolean K() {
      if (this.getPlayer() != null && this.getWorld() != null) {
         for (Entity entity : this.getClientWorld().getEntities()) {
            if (entity instanceof LivingEntity livingentity
               && livingentity != this.getPlayer()
               && this.L(livingentity)
               && (!this.onlyTargetSetting.getValue() || livingentity == this.agO)) {
               double d0 = this.getPlayer().distanceTo(livingentity);
               if (!(d0 > this.searchDistanceSetting.getValue()) && this.J(livingentity, true)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean L(LivingEntity livingentity) {
      if (!this.Q(livingentity)) {
         return false;
      } else if (!livingentity.isAlive()) {
         return false;
      } else {
         if (livingentity instanceof PlayerEntity playerentity) {
            if (playerentity.isSpectator()) {
               return false;
            }

            if (this.isFriendPlayer(playerentity)) {
               return false;
            }

            if (this.onlyArmoredSetting.getValue() && !this.F(playerentity)) {
               return false;
            }
         }

         return true;
      }
   }

   private float M(float f) {
      return ((f + 180.0F) % 360.0F + 360.0F) % 360.0F - 180.0F;
   }

   private void N() {
      if (!this.fixedSizeSetting.getValue()) {
         this.sizeSetting.setValue(0.3);
         this.Js = 0.3;
      }
   }

   private void O() {
      if (!this.fixedSizeSetting.getValue()) {
         double d0 = Math.min(this.sizeSetting.getValue() + this.stepSetting.getValue(), this.maxSizeSetting.getValue());
         if (d0 != this.sizeSetting.getValue()) {
            this.sizeSetting.setValue(d0);
            this.Js = d0;
         }
      }
   }

   private void P() {
      if (!this.fixedSizeSetting.getValue()) {
         double d0 = Math.max(this.sizeSetting.getValue() - this.stepSetting.getValue(), this.minSizeSetting.getValue());
         if (d0 != this.sizeSetting.getValue()) {
            this.sizeSetting.setValue(d0);
            this.Js = d0;
         }
      }
   }

   private boolean Q(Entity entity) {
      String s = this.targetsSetting.getFirst();
      if (EG.equals(s)) {
         return entity instanceof LivingEntity;
      } else if (lh.equals(s)) {
         return entity instanceof PlayerEntity;
      } else {
         return axc.equals(s) ? entity instanceof MobEntity : false;
      }
   }

   public boolean R() {
      return this.aaU;
   }

   public ListSetting S() {
      return this.targetsSetting;
   }

   public BooleanSetting T() {
      return this.onlyWithWeaponSetting;
   }

   public BooleanSetting U() {
      return this.onlyArmoredSetting;
   }

   public BooleanSetting V() {
      return this.onlyTargetSetting;
   }

   public SliderSetting W() {
      return this.resetTimeSetting;
   }

   public BooleanSetting X() {
      return this.invisibleHitboxSetting;
   }

   public BooleanSetting Y() {
      return this.hideIndicatorSetting;
   }

   public GroupSetting Z() {
      return this.sizeSettingsGroup;
   }

   public BooleanSetting aa() {
      return this.fixedSizeSetting;
   }

   public SliderSetting ab() {
      return this.fixedSizeValueSetting;
   }

   public SliderSetting ac() {
      return this.minSizeSetting;
   }

   public SliderSetting ad() {
      return this.maxSizeSetting;
   }

   public SliderSetting ae() {
      return this.stepSetting;
   }

   public BooleanSetting af() {
      return this.animationSetting;
   }

   public SliderSetting ag() {
      return this.animationSpeedSetting;
   }

   public SliderSetting ah() {
      return this.sizeSetting;
   }

   public ListSetting ai() {
      return this.rotationModeSetting;
   }

   public GroupSetting aj() {
      return this.bypassSettingsGroup;
   }

   public BooleanSetting al() {
      return this.bypassSetting;
   }

   public BooleanSetting am() {
      return this.antiReachSetting;
   }

   public SliderSetting an() {
      return this.antiReachDistanceSetting;
   }

   public BooleanSetting ao() {
      return this.fakeSwingSetting;
   }

   public SliderSetting ap() {
      return this.searchDistanceSetting;
   }

   public SliderSetting aq() {
      return this.aimSpeedSetting;
   }

   public SliderSetting ar() {
      return this.trackingSpeedSetting;
   }

   public GroupSetting as() {
      return this.hotkeysGroup;
   }

   public ActionKeySetting at() {
      return this.resetSizeKeySetting;
   }

   public ActionKeySetting au() {
      return this.increaseSizeKeySetting;
   }

   public ActionKeySetting av() {
      return this.decreaseSizeKeySetting;
   }

   public double aw() {
      return this.Js;
   }

   public LivingEntity ax() {
      return this.zU;
   }

   public Vec3d ay() {
      return this.WZ;
   }

   public LivingEntity az() {
      return this.agO;
   }

   public long aA() {
      return this.oP;
   }

   public boolean aB() {
      return this.aul;
   }

   public boolean aC() {
      return this.abx;
   }

   public long aD() {
      return this.acL;
   }

   public float aE() {
      return this.AN;
   }

   public float aF() {
      return this.awL;
   }

   public boolean aG() {
      return this.dY;
   }

   public boolean aH() {
      return this.aiu;
   }

   public boolean aI() {
      return this.OZ;
   }

   public boolean aJ() {
      return this.abl;
   }

   public float aK() {
      return this.agF;
   }

   public long aL() {
      return this.gF;
   }

   public boolean aM() {
      return this.YS;
   }

   public long aN() {
      return this.bZ;
   }

   public long aO() {
      return this.em;
   }

   public long aP() {
      return this.abX;
   }

   public LivingEntity aQ() {
      return this.ahp;
   }

   public boolean aR() {
      return this.amy;
   }

   public GLFWMouseButtonCallback aS() {
      return this.aeZ;
   }

   public void aT(SliderSetting slidersetting) {
      this.sizeSetting = slidersetting;
   }

   public void aU(boolean flag) {
      this.aul = flag;
   }

   public void aV(boolean flag) {
      this.abx = flag;
   }

   public void aW(float f) {
      this.AN = f;
   }

   public void aX(float f) {
      this.awL = f;
   }
}
