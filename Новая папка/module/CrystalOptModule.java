package module;

import enum.Category;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import setting.BooleanSetting;
import setting.GroupSetting;
import setting.SliderSetting;

public class CrystalOptModule extends Module {
   private GroupSetting mainSettingsGroup = new GroupSetting("Основные настройки", "Главные параметры модуля", true);
   private SliderSetting breakDelaySetting = new SliderSetting("Задержка ломания", "Задержка между действиями ломания (мс)", 50.0, 0.0, 200.0, 10.0);
   private SliderSetting placeDelaySetting = new SliderSetting("Задержка постановки", "Задержка между действиями постановки (мс)", 60.0, 10.0, 200.0, 10.0);
   private BooleanSetting instantBreakSetting = new BooleanSetting("Мгновенное ломание", "Мгновенное ломание на клиенте", true);
   private GroupSetting packetsGroup = new GroupSetting("Пакеты", "Настройки отправки пакетов", false);
   private SliderSetting packetLimitSetting = new SliderSetting("Лимит пакетов", "Максимум пакетов за тик", 2.0, 1.0, 5.0, 1.0);
   private BooleanSetting adaptivePacketLimitSetting = new BooleanSetting("Адаптивный лимит пакетов", "Регулировать лимит пакетов в зависимости от пинга", true);
   private SliderSetting maxPingSetting = new SliderSetting("Макс. пинг", "Максимальный пинг для оптимизации", 100.0, 20.0, 300.0, 10.0);
   private GroupSetting targetsGroup = new GroupSetting("Цели", "Настройки целей для атаки", false);
   private BooleanSetting breakCrystalsSetting = new BooleanSetting("Ломать кристаллы", "Ломать энд кристаллы", true);
   private BooleanSetting placeCrystalsSetting = new BooleanSetting("Ставить кристаллы", "Оптимизация постановки кристаллов", true);
   private ScheduledExecutorService NI = Executors.newSingleThreadScheduledExecutor();
   private int akX = 0;
   private int VW = 0;
   private long RR = 0L;
   private long aoI = 0L;
   private boolean E = false;

   public CrystalOptModule() {
      super("СрйзталОпт", "Оптимизация установки и уничтожения кристаллов", Category.PLAYER);
      this.mainSettingsGroup.addSettings(this.breakDelaySetting, this.placeDelaySetting, this.instantBreakSetting);
      this.packetsGroup.addSettings(this.packetLimitSetting, this.adaptivePacketLimitSetting, this.maxPingSetting);
      this.targetsGroup.addSettings(this.breakCrystalsSetting, this.placeCrystalsSetting);
      this.addSettings(this.mainSettingsGroup, this.packetsGroup, this.targetsGroup);
      this.U(this.adaptivePacketLimitSetting.getName(), this.maxPingSetting.getName(), true);
   }

   @Override
   public void onEnable() {
      if (this.hasPlayerAndWorld()) {
         this.akX = 0;
         this.VW = 0;
         this.E = false;
      }
   }

   @Override
   public void onDisable() {
      this.E = false;
   }

   @Override
   public void onEndTick() {
      if (this.isEnabled() && this.a()) {
         this.b();
      }
   }

   @Override
   public ActionResult onUseBlock(PlayerEntity playerentity, World world, Hand hand, BlockHitResult blockhitresult) {
      if (this.isEnabled() && this.placeCrystalsSetting.getValue() && playerentity == this.getPlayer()) {
         ItemStack itemstack = playerentity.getStackInHand(hand);
         if (!itemstack.isOf(Items.END_CRYSTAL)) {
            return ActionResult.PASS;
         } else if (this.akX >= this.k()) {
            return ActionResult.FAIL;
         } else {
            BlockPos blockpos = blockhitresult.getBlockPos();
            return (ActionResult)(this.h(blockpos) && this.m(blockpos) ? ActionResult.PASS : ActionResult.FAIL);
         }
      } else {
         return ActionResult.PASS;
      }
   }

   @Override
   public ActionResult onUseEntity(PlayerEntity playerentity, World world, Hand hand, Entity entity, EntityHitResult entityhitresult) {
      if (!this.isEnabled() || playerentity != this.getPlayer()) {
         return ActionResult.PASS;
      } else if (!this.g(entity)) {
         return ActionResult.PASS;
      } else if (this.akX >= this.k()) {
         return ActionResult.FAIL;
      } else {
         long i = System.currentTimeMillis();
         if (i - this.RR < this.breakDelaySetting.getLongValue()) {
            return ActionResult.FAIL;
         } else {
            if (this.instantBreakSetting.getValue() && this.akX >= 1) {
               entity.kill(this.getServerWorld());
               entity.setRemoved(RemovalReason.KILLED);
            }

            this.akX++;
            this.RR = i;
            return ActionResult.SUCCESS;
         }
      }
   }

   private boolean a() {
      return this.hasPlayerAndWorld() && !this.E;
   }

   private void b() {
      if (this.getPlayer() != null && this.getWorld() != null) {
         if (this.getOptions().attackKey.isPressed()) {
            this.VW++;
         } else {
            this.VW = 0;
         }

         if (this.VW <= 2) {
            if (!this.getOptions().useKey.isPressed() && !this.getOptions().attackKey.isPressed()) {
               this.akX = 0;
            }

            if (this.akX < this.k()) {
               if (this.e() && this.getOptions().attackKey.isPressed()) {
                  this.c();
               }

               if (this.placeCrystalsSetting.getValue() && this.getPlayer().getMainHandStack().isOf(Items.END_CRYSTAL) && this.getOptions().useKey.isPressed()) {
                  this.d();
               }
            }
         }
      }
   }

   private void c() {
      long i = System.currentTimeMillis();
      if (i - this.RR >= this.breakDelaySetting.getLongValue()) {
         Entity entity = this.f();
         if (entity != null) {
            if (this.instantBreakSetting.getValue() && this.akX >= 1) {
               entity.kill(this.getServerWorld());
               entity.setRemoved(RemovalReason.KILLED);
            }

            this.getInteractionManager().attackEntity(this.getPlayer(), entity);
            this.getPlayer().swingHand(this.getMainHand());
            this.akX++;
            this.RR = i;
         }
      }
   }

   private void d() {
      long i = System.currentTimeMillis();
      if (i - this.aoI >= this.placeDelaySetting.getLongValue()) {
         BlockHitResult blockhitresult = this.i();
         if (blockhitresult != null) {
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (this.h(blockpos) && this.m(blockpos)) {
               this.E = true;
               this.NI.schedule(() -> {
                  this.getClient().execute(() -> {
                     try {
                        ActionResult actionresult = this.getInteractionManager().interactBlock(this.getClientPlayer(), this.getMainHand(), blockhitresult);
                        if (actionresult.isAccepted()) {
                           this.getPlayer().swingHand(this.getMainHand());
                           this.aoI = System.currentTimeMillis();
                        }
                     } finally {
                        this.E = false;
                     }
                  });
               }, 10L, TimeUnit.MILLISECONDS);
            }
         }
      }
   }

   private boolean e() {
      return this.getClient().crosshairTarget instanceof EntityHitResult entityhitresult && this.g(entityhitresult.getEntity());
   }

   private Entity f() {
      if (this.getClient().crosshairTarget instanceof EntityHitResult entityhitresult) {
         Entity entity = entityhitresult.getEntity();
         if (this.g(entity)) {
            return entity;
         }
      }

      return null;
   }

   private boolean g(Entity entity) {
      return entity instanceof EndCrystalEntity ? this.breakCrystalsSetting.getValue() : false;
   }

   private boolean h(BlockPos blockpos) {
      BlockState blockstate = this.getWorld().getBlockState(blockpos);
      return blockstate.isOf(Blocks.OBSIDIAN) || blockstate.isOf(Blocks.BEDROCK);
   }

   private BlockHitResult i() {
      if (this.isNotInWorld()) {
         return null;
      } else {
         Vec3d vec3d = this.getPlayer().getEyePos();
         Vec3d vec3d1 = this.j();
         return this.getWorld().raycast(new RaycastContext(vec3d, vec3d.add(vec3d1.multiply(4.5)), ShapeType.OUTLINE, FluidHandling.NONE, this.getPlayer()));
      }
   }

   private Vec3d j() {
      float f = (float) (Math.PI / 180.0);
      float f1 = (float) Math.PI;
      float f2 = MathHelper.cos(-this.getPlayer().getYaw() * f - f1);
      float f3 = MathHelper.sin(-this.getPlayer().getYaw() * f - f1);
      float f4 = -MathHelper.cos(-this.getPlayer().getPitch() * f);
      float f5 = MathHelper.sin(-this.getPlayer().getPitch() * f);
      return new Vec3d(f3 * f4, f5, f2 * f4).normalize();
   }

   private int k() {
      if (!this.adaptivePacketLimitSetting.getValue()) {
         return (int)this.packetLimitSetting.getValue();
      } else {
         int i = this.l();
         if (i > this.maxPingSetting.getValue()) {
            return Math.max(1, (int)this.packetLimitSetting.getValue() - 1);
         } else {
            return i < 50 ? 1 : (int)this.packetLimitSetting.getValue();
         }
      }
   }

   private int l() {
      if (this.getClient().getNetworkHandler() == null) {
         return 0;
      } else {
         PlayerListEntry playerlistentry = this.getClient().getNetworkHandler().getPlayerListEntry(this.getPlayer().getUuid());
         return playerlistentry == null ? 0 : playerlistentry.getLatency();
      }
   }

   private boolean m(BlockPos blockpos) {
      if (this.isNotInWorld()) {
         return false;
      } else {
         BlockState blockstate = this.getWorld().getBlockState(blockpos);
         if (!blockstate.isOf(Blocks.OBSIDIAN) && !blockstate.isOf(Blocks.BEDROCK)) {
            return false;
         } else {
            BlockPos blockpos1 = blockpos.up();
            if (!this.getWorld().isAir(blockpos1)) {
               return false;
            } else {
               Box box = new Box(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), blockpos1.getX() + 1.0, blockpos1.getY() + 2.0, blockpos1.getZ() + 1.0);
               List list = this.getWorld().getOtherEntities(null, box);
               return list.isEmpty();
            }
         }
      }
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      super.onPlayerDeath(playerentity);
      if (this.enabledSetting.getValue()) {
         this.setEnabled(false);
      }
   }
}
