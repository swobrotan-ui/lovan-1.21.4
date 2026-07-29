package module;

import core.ClientMain;
import enum.Category;
import event.RotationEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import setting.BooleanSetting;
import setting.ListSetting;
import setting.SliderSetting;
import util.TPSTracker;

public class TriggerBotModule extends Module {
   private static String jF = "Криты";
   private static String Ux = "Умный";
   private static String TA = "1.8";
   private static String agT = "1.9+";
   private static String CJ = "Все";
   private static String Tw = "Только игроки";
   private static String py = "Только мобы";
   private static final Set<Block> ajj = new HashSet<Block>(
      Arrays.asList(
         Blocks.TALL_GRASS,
         Blocks.LARGE_FERN,
         Blocks.SUNFLOWER,
         Blocks.LILAC,
         Blocks.ROSE_BUSH,
         Blocks.PEONY,
         Blocks.TALL_SEAGRASS,
         Blocks.SUGAR_CANE,
         Blocks.KELP,
         Blocks.KELP_PLANT,
         Blocks.CRIMSON_FUNGUS,
         Blocks.WARPED_FUNGUS,
         Blocks.CRIMSON_ROOTS,
         Blocks.WARPED_ROOTS,
         Blocks.NETHER_SPROUTS,
         Blocks.TWISTING_VINES,
         Blocks.TWISTING_VINES_PLANT,
         Blocks.WEEPING_VINES,
         Blocks.WEEPING_VINES_PLANT
      )
   );
   private ListSetting targetsSetting = new ListSetting("Цели", "Выбор типа целей для атаки", Arrays.<String>asList(CJ, Tw, py), List.<String>of(CJ), false);
   private ListSetting attackModeSetting = new ListSetting("Режим атаки", "Выбор режима атаки", Arrays.<String>asList(jF, Ux), List.<String>of(Ux), false);
   private ListSetting rotationModeSetting = new ListSetting(
      "Режим PvP", "1.8 - мгновенные атаки без кулдауна, 1.9+ - ждёт перезарядку атаки", Arrays.<String>asList(TA, agT), List.<String>of(agT), false
   );
   private BooleanSetting sprintControlSetting = new BooleanSetting("Управление спринтом", "Автоматически управлять спринтом при атаке", true);
   private BooleanSetting tpsSyncSetting = new BooleanSetting("ТПС Синк", "Синхронизация с ТПС сервера", true);
   private BooleanSetting customDelaySetting = new BooleanSetting("Кастомная задержка", "Включить ручную настройку задержки перед ударом", false);
   private SliderSetting extraDelaySetting = new SliderSetting(
      "Доп. Задержка", "Задержка перед ударом после остановки спринта (0 = мгновенно)", 0.0, 0.0, 1000.0, 1.0
   );
   private BooleanSetting onlyWithWeaponSetting = new BooleanSetting("Только с оружием", "бить только с оружием", true);
   private BooleanSetting hitboxSyncSetting = new BooleanSetting("Синхронизация с ХитБокс", "Учитывать настройки модуля Хитбокс при атаке", false);
   private SliderSetting attackDistanceSetting = new SliderSetting("Дистанция атаки", "Максимальная дистанция атаки", 3.0, 2.0, 10.0, 0.1);
   private BooleanSetting attackInvisibleSetting = new BooleanSetting("Атаковать невидимых", "Атаковать невидимые цели", false);
   private BooleanSetting invisibleWithArmorSetting = new BooleanSetting("Невидимых с бронёй", "Атаковать невидимых только если на них броня", true);
   private BooleanSetting onlyArmoredSetting = new BooleanSetting("Только в броне", "Атаковать игроков только если на них броня", false);
   private BooleanSetting lineOfSightCheckSetting = new BooleanSetting("Проверка прямой видимости", "Атаковать только видимые цели", true);
   private BooleanSetting blockCheckSetting = new BooleanSetting("Проверка блокирующих блоков", "Не атаковать, если цель в блокирующих блоках", true);
   private BooleanSetting noiseSetting = new BooleanSetting("Шум", "Добавляет случайный разброс к таймингу удара", false);
   private SliderSetting noiseRangeSetting = new SliderSetting("Диапазон шума", "Максимальный разброс задержки удара (в мс)", 50.0, 1.0, 200.0, 1.0);
   private BooleanSetting missesSetting = new BooleanSetting("Промахи", "Разрешить случайные промахи", false);
   private SliderSetting missChanceSetting = new SliderSetting("Шанс промаха", "Вероятность промаха (в %)", 5.0, 1.0, 100.0, 1.0);
   private static final long Dd = 300L;
   private long AF = -1L;
   private LivingEntity awb = null;
   private LivingEntity GI = null;
   private long ZB = 0L;
   private boolean afX = false;
   private long aY = -1L;
   private boolean abC;
   private boolean adL;
   private boolean axv;
   private long qv = -1L;
   private final TPSTracker aoa = TPSTracker.getInstance();

   public TriggerBotModule() {
      super("ТриггерБот", "Автоматически бьёт игрока, который попадает под прицел", Category.COMBAT);
      this.extraDelaySetting.setVisibilitySupplier(this.customDelaySetting::getValue);
      this.attackDistanceSetting.setVisibilitySupplier(() -> {
         return !this.hitboxSyncSetting.getValue();
      });
      this.noiseRangeSetting.setVisibilitySupplier(this.noiseSetting::getValue);
      this.missChanceSetting.setVisibilitySupplier(this.missesSetting::getValue);
      this.invisibleWithArmorSetting.setVisibilitySupplier(this.attackInvisibleSetting::getValue);
      this.addSettings(
         this.targetsSetting,
         this.attackModeSetting,
         this.rotationModeSetting,
         this.sprintControlSetting,
         this.tpsSyncSetting,
         this.customDelaySetting,
         this.extraDelaySetting,
         this.onlyWithWeaponSetting,
         this.hitboxSyncSetting,
         this.attackDistanceSetting,
         this.attackInvisibleSetting,
         this.invisibleWithArmorSetting,
         this.onlyArmoredSetting,
         this.lineOfSightCheckSetting,
         this.blockCheckSetting,
         this.noiseSetting,
         this.noiseRangeSetting,
         this.missesSetting,
         this.missChanceSetting
      );
   }

   @Override
   public void onEnable() {
      this.a();
   }

   @Override
   public void onDisable() {
      if (this.abC) {
         this.f();
      }

      this.a();
   }

   private void a() {
      this.AF = -1L;
      this.awb = null;
      this.GI = null;
      this.ZB = 0L;
      this.afX = false;
      this.aY = -1L;
      this.abC = false;
      this.adL = false;
      this.axv = false;
      this.qv = -1L;
   }

   @Override
   public void onRotation(RotationEvent rotationevent) {
      if (this.isEnabled() && this.g()) {
         if (this.abC && this.qv != -1L && System.currentTimeMillis() - this.qv > 300L) {
            this.f();
            this.AF = -1L;
            this.awb = null;
         }

         if (this.awb != null && this.AF != -1L) {
            if (System.currentTimeMillis() >= this.AF) {
               boolean flag = !this.awb.isAlive() || !this.g();
               if (!flag) {
                  double d0 = this.hitboxSyncSetting.getValue() ? this.w() : this.attackDistanceSetting.getValue();
                  double d1 = this.x(this.getPlayer().getEyePos(), this.v(this.awb));
                  if (d1 > d0) {
                     flag = true;
                  } else if (this.o(this.awb, this.s())) {
                     try {
                        this.c(this.awb);
                     } finally {
                        this.f();
                        this.AF = -1L;
                        this.awb = null;
                     }
                  }
               }

               if (flag) {
                  this.f();
                  this.AF = -1L;
                  this.awb = null;
               }
            }
         } else {
            if (this.awb != null && (!this.awb.isAlive() || this.awb.isDead())) {
               this.f();
               this.AF = -1L;
               this.awb = null;
            }

            this.b();
         }
      } else {
         if (this.abC) {
            this.f();
         }
      }
   }

   private void b() {
      LivingEntity livingentity = this.i();
      if (livingentity == null) {
         if (this.abC) {
            this.f();
         }

         this.d();
      } else {
         boolean flag = this.s();
         boolean flag1 = this.sprintControlSetting.getValue();
         if (this.o(livingentity, flag)) {
            boolean flag2 = false;
            if (flag1 && !this.abC) {
               this.e();
               flag2 = true;
            }

            long i = this.customDelaySetting.getValue() ? (long)this.extraDelaySetting.getValue() : 0L;
            if (this.noiseSetting.getValue()) {
               i += (long)(Math.random() * this.noiseRangeSetting.getValue());
            }

            if (this.tpsSyncSetting.getValue()) {
               float f = this.aoa.getTickMultiplier();
               if (f > 1.0F) {
                  long j = (long)((f - 1.0F) * 500.0F);
                  i = Math.max(i, j);
               }

               if (i > 0L) {
                  i = this.aoa.adjustForTps(i);
               }
            }

            if (i == 0L) {
               if (flag2) {
                  this.awb = livingentity;
                  this.AF = System.currentTimeMillis();
                  return;
               }

               this.c(livingentity);
               if (flag1) {
                  this.f();
               }
            } else {
               this.awb = livingentity;
               this.AF = System.currentTimeMillis() + i;
            }
         } else {
            if (this.abC) {
               this.f();
            }
         }
      }
   }

   private void c(LivingEntity livingentity) {
      if (this.sprintControlSetting.getValue()) {
         this.getOptions().sprintKey.setPressed(false);
         this.getPlayer().setSprinting(false);
      }

      this.getInteractionManager().attackEntity(this.getPlayer(), livingentity);
      this.getPlayer().swingHand(this.getMainHand());
      this.GI = livingentity;
      this.ZB = System.currentTimeMillis();
      this.afX = true;
      this.aY = -1L;
   }

   private void d() {
      if (this.missesSetting.getValue() && this.GI != null && this.GI.isAlive()) {
         if (System.currentTimeMillis() - this.ZB > 2000L) {
            this.GI = null;
         } else if (!(this.getPlayer().distanceTo(this.GI) > this.attackDistanceSetting.getValue() + 1.0)) {
            if (this.afX) {
               this.afX = false;
               float f = this.missChanceSetting.getFloatValue();
               if (f > 0.0F && this.getPlayer().getRandom().nextFloat() * 100.0F < f) {
                  this.aY = System.currentTimeMillis();
               }
            }

            if (this.aY != -1L && System.currentTimeMillis() >= this.aY && this.getPlayer().getAttackCooldownProgress(0.0F) >= 0.8F) {
               this.getPlayer().swingHand(this.getMainHand());
               this.getPlayer().resetLastAttackedTicks();
               this.aY = -1L;
            }
         }
      }
   }

   private void e() {
      SprintModule sprintmodule = ClientMain.getInstance().getModuleManager().<SprintModule>getModule(SprintModule.class);
      this.adL = this.getOptions().sprintKey.isPressed();
      this.axv = sprintmodule != null && sprintmodule.isEnabled();
      this.getPlayer().setSprinting(false);
      this.getOptions().sprintKey.setPressed(false);
      if (sprintmodule != null && this.axv) {
         sprintmodule.setSprinting(false);
      }

      this.abC = true;
      this.qv = System.currentTimeMillis();
   }

   private void f() {
      if (this.abC) {
         try {
            SprintModule sprintmodule = ClientMain.getInstance().getModuleManager().<SprintModule>getModule(SprintModule.class);
            if (sprintmodule != null && this.axv) {
               sprintmodule.setSprinting(true);
               this.getOptions().sprintKey.setPressed(true);
               this.getPlayer().setSprinting(true);
            } else if (this.adL && this.getPlayer() != null) {
               this.getOptions().sprintKey.setPressed(true);
               this.getPlayer().setSprinting(true);
               return;
            }

            return;
         } catch (Exception exception) {
         } finally {
            this.abC = false;
            this.adL = false;
            this.axv = false;
            this.qv = -1L;
         }
      }
   }

   private boolean g() {
      return this.hasPlayerAndWorld() && !this.getPlayer().isUsingItem() && (!this.onlyWithWeaponSetting.getValue() || this.h());
   }

   private boolean h() {
      ItemStack itemstack = this.getClientPlayer().getMainHandStack();
      if (itemstack.isEmpty()) {
         return false;
      } else {
         Item item = itemstack.getItem();
         return item instanceof SwordItem || item instanceof AxeItem || item instanceof MaceItem;
      }
   }

   private LivingEntity i() {
      ClientPlayerEntity clientplayerentity = this.getClientPlayer();
      Vec3d vec3d = clientplayerentity.getEyePos();
      Vec3d vec3d1 = clientplayerentity.getRotationVec(1.0F);
      double d0 = this.attackDistanceSetting.getValue();
      Vec3d vec3d2 = vec3d.add(vec3d1.multiply(d0));
      Box box = clientplayerentity.getBoundingBox().stretch(vec3d1.multiply(d0)).expand(1.0);
      Entity entity = null;
      double d1 = Double.MAX_VALUE;

      for (Entity entity1 : this.getWorld().getOtherEntities(clientplayerentity, box, entity2 -> {
         return entity2.canHit() && entity2 instanceof LivingEntity;
      })) {
         if (entity1 != clientplayerentity && this.k((LivingEntity)entity1)) {
            Box box1 = this.j(entity1);
            Optional optional = box1.raycast(vec3d, vec3d2);
            if (optional.isPresent()) {
               double d2 = vec3d.distanceTo((Vec3d)optional.get());
               double d3 = this.x(vec3d, this.v(entity1));
               if (d3 <= d0 && d2 < d1) {
                  entity = entity1;
                  d1 = d2;
               }
            } else if (box1.contains(vec3d)) {
               entity = entity1;
               d1 = 0.0;
            }
         }
      }

      if (entity instanceof LivingEntity livingentity) {
         if (this.hitboxSyncSetting.getValue()) {
            HitboxModule hitboxmodule = ClientMain.getInstance().getModuleManager().<HitboxModule>getModule(HitboxModule.class);
            if (hitboxmodule != null && hitboxmodule.isEnabled() && hitboxmodule.am().getValue()) {
               double d4 = this.x(vec3d, this.v(livingentity));
               if (d4 > hitboxmodule.an().getValue()) {
                  return null;
               }
            }
         }

         return livingentity;
      } else {
         return null;
      }
   }

   private Box j(Entity entity) {
      Box box = this.hitboxSyncSetting.getValue() ? entity.getBoundingBox() : this.v(entity);
      return box.expand(entity.getTargetingMargin());
   }

   private boolean k(LivingEntity livingentity) {
      if (this.isFriendLiving(livingentity)) {
         return false;
      } else {
         String s = this.targetsSetting.getSelectedValues().getFirst();
         if (Tw.equals(s) && !(livingentity instanceof PlayerEntity)) {
            return false;
         } else if (py.equals(s) && !(livingentity instanceof MobEntity)) {
            return false;
         } else if (this.onlyArmoredSetting.getValue() && livingentity instanceof PlayerEntity && !this.m(livingentity)) {
            return false;
         } else {
            return !this.l(livingentity) ? false : !this.lineOfSightCheckSetting.getValue() || this.n(this.getPlayer().getEyePos(), livingentity);
         }
      }
   }

   private boolean l(LivingEntity livingentity) {
      if (!livingentity.isInvisible()) {
         return true;
      } else {
         return !this.attackInvisibleSetting.getValue() ? false : !this.invisibleWithArmorSetting.getValue() || this.m(livingentity);
      }
   }

   private boolean m(LivingEntity livingentity) {
      return xk.d(livingentity);
   }

   private boolean n(Vec3d vec3d, Entity entity) {
      RaycastContext raycastcontext = new RaycastContext(vec3d, entity.getBoundingBox().getCenter(), ShapeType.COLLIDER, FluidHandling.NONE, this.getPlayer());
      return this.getWorld().raycast(raycastcontext).getType() == Type.MISS;
   }

   private boolean o(LivingEntity livingentity, boolean flag) {
      if (this.blockCheckSetting.getValue() && this.p()) {
         return false;
      } else if (this.hitboxSyncSetting.getValue() && !this.u(livingentity)) {
         return false;
      } else {
         boolean flag1 = TA.equals(this.rotationModeSetting.getSelectedValues().getFirst());
         if (!flag1) {
            float f = this.tpsSyncSetting.getValue() ? Math.min(1.0F, 0.8F * this.aoa.getTickMultiplier()) : 0.8F;
            if (this.getPlayer().getAttackCooldownProgress(0.0F) < f) {
               return false;
            }
         }

         String s = this.attackModeSetting.getSelectedValues().getFirst();
         if (jF.equals(s)) {
            return this.r() || flag;
         } else if (flag1) {
            return true;
         } else {
            return !this.r() && this.getPlayer().getVelocity().y > 0.0 ? false : this.getPlayer().isOnGround() || this.r() || flag;
         }
      }
   }

   private boolean p() {
      BlockPos blockpos = this.getPlayer().getBlockPos();
      BlockPos blockpos1 = blockpos.up();
      BlockPos blockpos2 = BlockPos.ofFloored(this.getPlayer().getEyePos());
      return this.q(blockpos) || this.q(blockpos1) || this.q(blockpos2);
   }

   private boolean q(BlockPos blockpos) {
      BlockState blockstate = this.getWorld().getBlockState(blockpos);
      Block block = blockstate.getBlock();
      return !ajj.contains(block) && !(block instanceof TallPlantBlock)
         ? blockstate.isFullCube(this.getWorld(), blockpos) && !blockstate.isTransparent()
         : true;
   }

   private boolean r() {
      return this.getPlayer().isTouchingWater()
         || this.getPlayer().isSwimming()
         || this.getPlayer().isGliding()
         || this.getPlayer().isClimbing()
         || this.getPlayer().isInsideWall()
         || this.getPlayer().isInLava()
         || this.getPlayer().hasVehicle();
   }

   private boolean s() {
      ClientPlayerEntity clientplayerentity = this.getClientPlayer();
      if (!clientplayerentity.isOnGround() && !clientplayerentity.isTouchingWater() && !clientplayerentity.isInLava() && !clientplayerentity.hasVehicle()) {
         double d0 = clientplayerentity.getVelocity().y;
         if (this.t(clientplayerentity) && d0 < -0.01) {
            double d1 = clientplayerentity.getY() - (clientplayerentity.getBlockPos().down().getY() + 1.0);
            if (d1 > 0.4) {
               return true;
            }
         }

         return d0 < -0.15;
      } else {
         return false;
      }
   }

   private boolean t(ClientPlayerEntity clientplayerentity) {
      BlockPos blockpos = clientplayerentity.getBlockPos().up(2);
      BlockState blockstate = this.getWorld().getBlockState(blockpos);
      return !blockstate.isAir() && blockstate.isFullCube(this.getWorld(), blockpos);
   }

   private boolean u(LivingEntity livingentity) {
      HitboxModule hitboxmodule = ClientMain.getInstance().getModuleManager().<HitboxModule>getModule(HitboxModule.class);
      if (hitboxmodule == null || !hitboxmodule.isEnabled()) {
         return true;
      } else if (hitboxmodule.R() && hitboxmodule.al().getValue()) {
         if (hitboxmodule.ah().getFloatValue() <= 0.3F) {
            return true;
         } else {
            String s = hitboxmodule.ai().getFirst();
            if (!"Новая".equals(s)) {
               return true;
            } else {
               Vec3d vec3d = this.getPlayer().getEyePos();
               Box box = this.v(livingentity);
               if (box.contains(vec3d)) {
                  return true;
               } else {
                  double d0 = hitboxmodule.ap().getValue();
                  float f = hitboxmodule.aF() * (float) (Math.PI / 180.0);
                  float f1 = -hitboxmodule.aE() * (float) (Math.PI / 180.0);
                  float f2 = (float)Math.cos(f);
                  float f3 = (float)Math.sin(f);
                  float f4 = (float)Math.cos(f1);
                  float f5 = (float)Math.sin(f1);
                  Vec3d vec3d1 = new Vec3d(f5 * f2, -f3, f4 * f2);
                  Vec3d vec3d2 = vec3d.add(vec3d1.multiply(d0));
                  if (box.raycast(vec3d, vec3d2).isPresent()) {
                     return true;
                  } else {
                     Vec3d vec3d3 = this.getPlayer().getRotationVec(1.0F);
                     Vec3d vec3d4 = vec3d.add(vec3d3.multiply(d0));
                     return box.raycast(vec3d, vec3d4).isPresent();
                  }
               }
            }
         }
      } else {
         return true;
      }
   }

   private Box v(Entity entity) {
      return entity.getType().getDimensions().getBoxAt(entity.getPos());
   }

   private double w() {
      HitboxModule hitboxmodule = ClientMain.getInstance().getModuleManager().<HitboxModule>getModule(HitboxModule.class);
      return hitboxmodule != null && hitboxmodule.isEnabled() && hitboxmodule.am().getValue()
         ? hitboxmodule.an().getValue()
         : this.attackDistanceSetting.getValue();
   }

   private double x(Vec3d vec3d, Box box) {
      double d0 = Math.max(box.minX, Math.min(vec3d.x, box.maxX));
      double d1 = Math.max(box.minY, Math.min(vec3d.y, box.maxY));
      double d2 = Math.max(box.minZ, Math.min(vec3d.z, box.maxZ));
      return vec3d.distanceTo(new Vec3d(d0, d1, d2));
   }

   public boolean y() {
      return this.isEnabled() && this.sprintControlSetting.getValue() && this.abC;
   }
}
