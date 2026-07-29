package module;

import aim.AimMode;
import aim.CakeWorldAimMode;
import aim.FTAimMode;
import aim.HVHAimMode;
import data.Angle;
import enum.Category;
import event.PacketEvent;
import event.RotationEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import setting.BooleanSetting;
import setting.ListSetting;
import setting.SliderSetting;
import util.RotationUtil;
import util.TPSTracker;

public class AuraModule extends Module {
   private static final int rD = 5;
   private static final int aaW = 10;
   private static final long fR = 50L;
   private static final long aS = 25L;
   private final fj aet = new fj();
   private final xx gB = xx.INSTANCE;
   private final ls dB = new ls();
   private final hr PZ = new hr();
   private final TPSTracker aas = TPSTracker.getInstance();
   private final HVHAimMode hQ = new HVHAimMode();
   private final CakeWorldAimMode wJ = new CakeWorldAimMode();
   private final FTAimMode Od = new FTAimMode();
   private AimMode Ic;
   private LivingEntity rp;
   private LivingEntity uW;
   private int akm;
   private int avQ;
   private Vec3d cJ;
   private Vec3d Qg;
   private int JV;
   private Angle gw;
   private Angle Gi;
   private int im;
   private boolean KP;
   private LivingEntity aqh;
   private FTAimMode gK;
   private long Yh = -1L;
   public ListSetting rotationModeSetting = new ListSetting(
      "Режим ротации", "Выбор алгоритма поворота камеры", List.<String>of("Basic", "SlothAC", "FT"), List.<String>of("Basic"), false
   );
   public ListSetting attackModeSetting = new ListSetting(
      "Режим атаки", "Выбор режима нанесения ударов", List.<String>of("Только криты", "Смарт"), List.<String>of("Смарт"), false
   );
   public ListSetting targetSortSetting = new ListSetting(
      "Приоритет цели", "По какому критерию выбирать цель", List.<String>of("Ближайший", "Меньше HP", "Текущая цель"), List.<String>of("Ближайший"), false
   );
   public SliderSetting targetSwitchDelaySetting = new SliderSetting("Задержка смены цели", "Время удержания текущей цели в тиках", 20.0, 0.0, 100.0, 5.0);
   public SliderSetting rangeSetting = new SliderSetting("Дальность", "Радиус атаки в блоках", 2.9F, 2.0, 7.0, 0.1F);
   public BooleanSetting customYDistanceSetting = new BooleanSetting("Своя дистанция Y", "Использовать отдельную дистанцию для вертикальной оси", false);
   public SliderSetting yDistanceSetting = new SliderSetting("Дистанция Y", "Вертикальная дистанция атаки в блоках", 5.4F, 0.5, 7.0, 0.5);
   public SliderSetting preDistanceSetting = new SliderSetting("Пре-дистанция", "Дополнительная дистанция для раннего обнаружения целей", 1.0, 0.0, 3.0, 0.1F);
   public SliderSetting fovAngleSetting = new SliderSetting("Угол FOV", "Угол обзора для поиска целей", 180.0, 30.0, 180.0, 5.0);
   public BooleanSetting onlyWithWeaponSetting = new BooleanSetting("Только с оружием", "Наводить и атаковать только при оружии в руках", true);
   public BooleanSetting breakShieldSetting = new BooleanSetting("Ломать щит", "Автоматически ломать щит противника топором", true);
   public BooleanSetting onlyPlayersSetting = new BooleanSetting("Только игроки", "Атаковать только игроков, игнорируя мобов", true);
   public BooleanSetting onlyArmoredSetting = new BooleanSetting("Только в броне", "Атаковать только цели с надетой бронёй", false);
   public BooleanSetting screenCheckSetting = new BooleanSetting("Проверка экрана", "Отключать при открытом интерфейсе", true);
   public BooleanSetting attackInvisibleSetting = new BooleanSetting("Атака невидимых", "Атаковать невидимых игроков", true);
   public BooleanSetting ignoreOffhandSetting = new BooleanSetting(
      "Игнорировать правую руку", "Игнорировать использование предметов игроком и целью (щит, еда)", false
   );
   public BooleanSetting throughWallsSetting = new BooleanSetting("Через стены", "Атаковать и наводиться на цели за блоками", false);
   public BooleanSetting multipointSetting = new BooleanSetting("Мультипоинт", "Наводиться на разные точки хитбокса", false);
   public SliderSetting multipointIntervalSetting = new SliderSetting(
      "Интервал смены точки", "Частота переключения точек мультипоинта в тиках", 5.0, 0.0, 15.0, 1.0
   );
   public SliderSetting fireworkBoostSetting = new SliderSetting("Буст фейерверка", "Множитель буста фейерверка (0 = без буста)", 0.0, 0.0, 4.0, 0.05F);
   public BooleanSetting tpsSyncSetting = new BooleanSetting("Синхронизация с TPS", "Synchronize attack timing with server TPS", true);

   public AuraModule() {
      super("Аура", "Автоматически бьёт игроков", Category.COMBAT);
      this.Ic = this.hQ;
      this.addSettings(
         this.rotationModeSetting,
         this.attackModeSetting,
         this.targetSortSetting,
         this.targetSwitchDelaySetting,
         this.rangeSetting,
         this.customYDistanceSetting,
         this.yDistanceSetting,
         this.preDistanceSetting,
         this.fovAngleSetting,
         this.onlyWithWeaponSetting,
         this.breakShieldSetting,
         this.onlyPlayersSetting,
         this.onlyArmoredSetting,
         this.screenCheckSetting,
         this.attackInvisibleSetting,
         this.ignoreOffhandSetting,
         this.throughWallsSetting,
         this.multipointSetting,
         this.multipointIntervalSetting,
         this.fireworkBoostSetting,
         this.tpsSyncSetting
      );
   }

   @Override
   public void onEnable() {
      this.A();
      this.gB.i();
   }

   @Override
   public void onDisable() {
      if (this.dB.g()) {
         this.dB.e();
      }

      if (this.gB.c()) {
         this.u();
      } else {
         this.A();
      }
   }

   @Override
   public void onRotation(RotationEvent rotationevent) {
      if (!this.isEnabled()) {
         this.a(rotationevent);
      } else if (!this.w()) {
         this.a(rotationevent);
      } else {
         this.dB.b();
         if (this.dB.c()) {
            this.e(true);
         }

         if (this.dB.g() && this.dB.h()) {
            this.getOptions().sprintKey.setPressed(false);
            this.getPlayer().setSprinting(false);
         }

         this.b();
         if (this.akm > 0) {
            this.akm--;
         }

         this.avQ++;
         LivingEntity livingentity = this.m(this.rangeSetting.getValue() + this.preDistanceSetting.getValue());
         this.q(livingentity);
         if (livingentity == null) {
            this.a(rotationevent);
            if (this.aqh == null && this.dB.g()) {
               this.dB.e();
            }
         } else {
            this.f(livingentity, rotationevent);
         }

         if (this.breakShieldSetting.getValue()) {
            this.PZ.b();
         }
      }
   }

   private void a(RotationEvent rotationevent) {
      if (this.dB.g()) {
         this.dB.e();
      }

      if (this.KP && this.gw != null && this.Gi != null) {
         if (this.getPlayer() != null) {
            this.Gi = new Angle(this.getPlayer().getYaw(), this.getPlayer().getPitch());
         }

         this.r(this.Gi);
         this.gB.d(rotationevent);
         if (this.im >= 5) {
            this.v();
            return;
         }
      } else if (!this.isEnabled() && this.gB.c()) {
         this.v();
      }
   }

   private void b() {
      if (this.aqh != null && this.Yh != -1L) {
         if (!this.aqh.isAlive() || this.aqh.isDead()) {
            this.e(true);
         } else if (!this.dB.h()) {
            if (System.currentTimeMillis() >= this.Yh) {
               Vec3d vec3d = this.n(this.aqh);
               if (!this.c(this.aqh, vec3d)) {
                  this.e(true);
               } else if (!this.d()) {
                  this.Yh = System.currentTimeMillis() + 25L;
               } else {
                  try {
                     this.z(this.aqh);
                     if (this.gK != null) {
                        this.gK.onAttack();
                     }
                  } finally {
                     this.e(true);
                  }
               }
            }
         }
      }
   }

   private boolean c(LivingEntity livingentity, Vec3d vec3d) {
      if (!this.w()) {
         return false;
      } else if (this.getPlayer().isBlocking() && !this.ignoreOffhandSetting.getValue()) {
         return false;
      } else {
         boolean flag = xk.b(livingentity, this.rangeSetting.getValue(), this.customYDistanceSetting.getValue(), this.yDistanceSetting.getValue());
         boolean flag1 = this.throughWallsSetting.getValue() || lz.a(livingentity, vec3d, this.rangeSetting.getValue());
         return flag && flag1 && !this.getPlayer().isBlocking();
      }
   }

   private boolean d() {
      return bp.c(this.attackModeSetting.getFirst(), true);
   }

   private void e(boolean flag) {
      if (flag) {
         this.dB.e();
      }

      this.Yh = -1L;
      this.aqh = null;
      this.gK = null;
   }

   private void f(LivingEntity livingentity, RotationEvent rotationevent) {
      this.E();
      Vec3d vec3d = this.n(livingentity);
      Angle angle = RotationUtil.getRotationTo(vec3d);
      Angle angle1 = this.gB.b();
      if (angle1 == null) {
         angle1 = new Angle(this.getPlayer().getYaw(), this.getPlayer().getPitch());
      }

      if (this.Ic instanceof FTAimMode ftaimmode) {
         this.g(livingentity, vec3d, angle, angle1, rotationevent, ftaimmode);
      } else {
         if (!this.KP && this.gw != null && this.Gi != null && this.im < 5) {
            this.r(angle);
         } else {
            if (this.im >= 5) {
               this.gw = null;
               this.Gi = null;
               this.im = 0;
            }

            Angle angle2 = this.Ic.calculateRotation(angle1, angle, vec3d, livingentity);
            this.gB.a(angle2);
         }

         this.gB.d(rotationevent);
         this.gB.e(rotationevent, "Y");
         if (this.aqh == null && this.k(livingentity, vec3d)) {
            this.j(livingentity, vec3d);
         }
      }
   }

   private void g(LivingEntity livingentity, Vec3d vec3d, Angle angle, Angle angle1, RotationEvent rotationevent, FTAimMode ftaimmode) {
      if (this.aqh != null) {
         this.a(rotationevent);
      } else if (this.KP && this.gw != null && this.Gi != null && this.im < 5) {
         this.a(rotationevent);
      } else {
         boolean flag = this.l(livingentity, vec3d);
         ftaimmode.setAggressive(flag);
         if (this.h(livingentity, vec3d)) {
            Angle angle3 = ftaimmode.calculateRotation(angle1, angle, vec3d, livingentity);
            this.gB.a(angle3);
            this.gB.d(rotationevent);
            this.gB.e(rotationevent, "Y");
            if (flag) {
               this.i(livingentity, vec3d, ftaimmode);
               this.u();
            }
         } else {
            Angle angle2 = ftaimmode.calculateRotation(angle1, angle, vec3d, null);
            this.gB.a(angle2);
            this.gB.d(rotationevent);
            this.gB.e(rotationevent, "Y");
         }
      }
   }

   private boolean h(LivingEntity livingentity, Vec3d vec3d) {
      if (this.getPlayer().isBlocking() && !this.ignoreOffhandSetting.getValue()) {
         return false;
      } else if (this.akm > 5) {
         return false;
      } else {
         boolean flag = xk.b(livingentity, this.rangeSetting.getValue(), this.customYDistanceSetting.getValue(), this.yDistanceSetting.getValue());
         boolean flag1 = this.throughWallsSetting.getValue() || lz.a(livingentity, vec3d, this.rangeSetting.getValue());
         return flag && flag1 && !this.getPlayer().isBlocking() ? bp.d(this.attackModeSetting.getFirst(), true, 5.0F) : false;
      }
   }

   private void i(LivingEntity livingentity, Vec3d vec3d, FTAimMode ftaimmode) {
      this.dB.a();
      if (this.breakShieldSetting.getValue() && bp.i(livingentity)) {
         boolean flag = this.throughWallsSetting.getValue() || lz.a(livingentity, vec3d, this.rangeSetting.getValue());
         if (flag) {
            this.PZ.a(livingentity);
         }
      }

      this.aqh = livingentity;
      this.gK = ftaimmode;
      this.Yh = System.currentTimeMillis();
      this.akm = this.tpsSyncSetting.getValue() ? this.aas.adjustForTps(10) : 10;
      this.uW = livingentity;
      this.avQ = 0;
   }

   private void j(LivingEntity livingentity, Vec3d vec3d) {
      this.dB.a();
      this.aqh = livingentity;
      this.gK = null;
      if (this.breakShieldSetting.getValue() && bp.i(livingentity)) {
         boolean flag = this.throughWallsSetting.getValue() || lz.a(livingentity, vec3d, this.rangeSetting.getValue());
         if (flag) {
            this.PZ.a(livingentity);
         }
      }

      if (this.dB.h()) {
         this.Yh = System.currentTimeMillis();
      } else {
         long i = this.tpsSyncSetting.getValue() ? this.aas.adjustForTps(50L) : 50L;
         this.Yh = System.currentTimeMillis() + i;
      }

      this.akm = this.tpsSyncSetting.getValue() ? this.aas.adjustForTps(10) : 10;
      this.uW = livingentity;
      this.avQ = 0;
   }

   private boolean k(LivingEntity livingentity, Vec3d vec3d) {
      if (this.getPlayer().isBlocking() && !this.ignoreOffhandSetting.getValue()) {
         return false;
      } else {
         boolean flag = xk.b(livingentity, this.rangeSetting.getValue(), this.customYDistanceSetting.getValue(), this.yDistanceSetting.getValue());
         boolean flag1 = this.throughWallsSetting.getValue() || lz.a(livingentity, vec3d, this.rangeSetting.getValue());
         return bp.c(this.attackModeSetting.getFirst(), this.y()) && flag && !this.getPlayer().isBlocking() && flag1;
      }
   }

   private boolean l(LivingEntity livingentity, Vec3d vec3d) {
      if (this.getPlayer().isBlocking() && !this.ignoreOffhandSetting.getValue()) {
         return false;
      } else if (this.akm > 0) {
         return false;
      } else {
         boolean flag = xk.b(livingentity, this.rangeSetting.getValue(), this.customYDistanceSetting.getValue(), this.yDistanceSetting.getValue());
         boolean flag1 = this.throughWallsSetting.getValue() || lz.a(livingentity, vec3d, this.rangeSetting.getValue());
         return bp.c(this.attackModeSetting.getFirst(), true) && flag && !this.getPlayer().isBlocking() && flag1;
      }
   }

   private LivingEntity m(double d0) {
      if (this.isNotInWorld()) {
         return null;
      } else {
         Stream stream = xk.a(d0, this.customYDistanceSetting.getValue(), this.yDistanceSetting.getValue())
            .filter(
               livingentity -> {
                  return xk.c(
                     livingentity,
                     this.attackInvisibleSetting.getValue(),
                     this.ignoreOffhandSetting.getValue(),
                     this.onlyArmoredSetting.getValue(),
                     this.onlyPlayersSetting.getValue()
                  );
               }
            )
            .filter(livingentity -> {
               return xk.e(livingentity, this.fovAngleSetting.getFloatValue());
            });
         if (!this.throughWallsSetting.getValue()) {
            stream = stream.filter(livingentity -> {
               return !lz.b(this.getPlayer().getEyePos(), xk.g(livingentity));
            });
         }

         List list = stream.toList();
         return list.isEmpty() ? null : xk.f(list, this.targetSortSetting.getFirst(), this.uW, this.avQ, this.targetSwitchDelaySetting.getFloatValue());
      }
   }

   private Vec3d n(LivingEntity livingentity) {
      if (!this.multipointSetting.getValue()) {
         return xk.g(livingentity);
      } else {
         ArrayList arraylist = this.aet.a(livingentity, 0, 0);
         if (arraylist.isEmpty()) {
            return xk.g(livingentity);
         } else {
            double d0 = this.rangeSetting.getValue() * this.rangeSetting.getValue();
            List list = arraylist.stream().filter(vec3d -> {
               return this.getPlayer().getEyePos().squaredDistanceTo(vec3d) <= d0;
            }).toList();
            if (list.isEmpty()) {
               return xk.g(livingentity);
            } else if (this.cJ == null) {
               this.cJ = this.o(list);
               this.Qg = this.cJ;
               this.JV = 0;
               return this.cJ;
            } else {
               this.JV++;
               int i = (int)this.multipointIntervalSetting.getValue();
               if (this.JV >= i) {
                  this.cJ = this.Qg;
                  this.Qg = this.o(list);
                  this.JV = 0;
               }

               float f = Math.min(1.0F, (float)this.JV / i);
               float f1 = this.D(f) * 0.2F;
               return this.p(this.cJ, this.Qg, f1);
            }
         }
      }
   }

   private Vec3d o(List<Vec3d> list) {
      return list.stream().min((vec3d, vec3d1) -> {
         return Double.compare(this.getPlayer().getEyePos().squaredDistanceTo(vec3d), this.getPlayer().getEyePos().squaredDistanceTo(vec3d1));
      }).orElse((Vec3d)list.getFirst());
   }

   private Vec3d p(Vec3d vec3d, Vec3d vec3d1, float f) {
      return new Vec3d(vec3d.x + (vec3d1.x - vec3d.x) * f, vec3d.y + (vec3d1.y - vec3d.y) * f, vec3d.z + (vec3d1.z - vec3d.z) * f);
   }

   private void q(LivingEntity livingentity) {
      if (livingentity == null && this.rp != null) {
         this.u();
         if (this.dB.g()) {
            this.dB.e();
         }
      }

      if (livingentity != null && livingentity != this.rp) {
         if (this.KP) {
            this.KP = false;
         }

         if (!this.F()) {
            this.gw = this.gB.b() != null ? this.gB.b() : new Angle(this.getPlayer().getYaw(), this.getPlayer().getPitch());
            Vec3d vec3d = this.n(livingentity);
            this.Gi = RotationUtil.getRotationTo(vec3d);
            this.im = 0;
         } else {
            this.gw = null;
            this.Gi = null;
            this.im = 0;
         }

         this.B();
      }

      this.rp = livingentity;
   }

   private void r(Angle angle) {
      this.im++;
      if (this.im < 5) {
         float f = this.im / 5.0F;
         float f1 = this.D(f);
         if (!this.KP) {
            this.Gi = angle;
         }

         Angle angle1 = this.s(this.gw, this.Gi, f1);
         this.gB.a(angle1);
      }
   }

   private Angle s(Angle angle, Angle angle1, float f) {
      float f1 = this.C(angle1.getYaw() - angle.getYaw());
      float f2 = angle1.getPitch() - angle.getPitch();
      float f3 = angle.getYaw() + f1 * f;
      float f4 = Math.max(-90.0F, Math.min(90.0F, angle.getPitch() + f2 * f));
      return new Angle(f3, f4);
   }

   private void u() {
      if (!this.KP && this.gB.c() && this.getPlayer() != null) {
         this.gw = this.gB.b();
         this.Gi = new Angle(this.getPlayer().getYaw(), this.getPlayer().getPitch());
         this.im = 0;
         this.KP = true;
      }
   }

   private void v() {
      this.gw = null;
      this.Gi = null;
      this.im = 0;
      this.KP = false;
      this.gB.a(null);
      if (!this.isEnabled()) {
         this.A();
      }
   }

   private boolean w() {
      if (this.isNotInWorld()) {
         this.x();
         return false;
      } else if (this.screenCheckSetting.getValue() && this.getScreen() != null) {
         this.x();
         return false;
      } else if (this.onlyWithWeaponSetting.getValue() && !bp.a()) {
         this.x();
         return false;
      } else if (!this.ignoreOffhandSetting.getValue() && this.getPlayer().isUsingItem()) {
         this.x();
         return false;
      } else {
         return true;
      }
   }

   private void x() {
      if (this.gB.c() && !this.KP) {
         this.u();
      } else if (this.gB.c() && this.KP) {
         this.im = 5;
      }

      if (this.dB.g()) {
         this.dB.e();
      }

      this.rp = null;
   }

   private boolean y() {
      return this.rp == null ? false : xx.g(this.gB.b(), RotationUtil.getRotationTo(this.n(this.rp))) < 10.0;
   }

   private void z(LivingEntity livingentity) {
      this.dB.d();
      this.getInteractionManager().attackEntity(this.getPlayer(), livingentity);
      this.getPlayer().swingHand(Hand.MAIN_HAND);
      this.getPlayer().resetLastAttackedTicks();
   }

   private void A() {
      this.rp = null;
      this.uW = null;
      this.akm = 0;
      this.avQ = 0;
      this.B();
      this.gw = null;
      this.Gi = null;
      this.im = 0;
      this.KP = false;
      this.aqh = null;
      this.gK = null;
      this.Yh = -1L;
      this.PZ.d();
      this.dB.f();
      this.Od.reset();
   }

   private void B() {
      this.cJ = null;
      this.Qg = null;
      this.JV = 0;
   }

   private float C(float f) {
      f %= 360.0F;
      if (f > 180.0F) {
         f -= 360.0F;
      }

      if (f < -180.0F) {
         f += 360.0F;
      }

      return f;
   }

   private float D(float f) {
      return f < 0.5F ? 4.0F * f * f * f : 1.0F + 0.5F * (2.0F * f - 2.0F) * (2.0F * f - 2.0F) * (2.0F * f - 2.0F);
   }

   private void E() {
      String s = this.rotationModeSetting.getFirst();
      if ("SlothAC".equals(s)) {
         this.Ic = this.wJ;
      } else if ("FT".equals(s)) {
         this.Ic = this.Od;
      } else {
         this.Ic = this.hQ;
      }
   }

   private boolean F() {
      return "FT".equals(this.rotationModeSetting.getFirst());
   }

   @Override
   public void onPacket(PacketEvent packetevent) {
      this.aas.onPacket(packetevent);
   }

   public boolean G() {
      return this.isEnabled() && this.dB.g();
   }
}
