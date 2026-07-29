package module;

import enum.Category;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import setting.ActionKeySetting;
import setting.BooleanSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class AutoCartModule extends Module {
   private static final String jY = "Сначала ставить";
   private static final String Of = "Сначала стрелять";
   private ListSetting modeSetting = new ListSetting(
      "Режим",
      "Порядок действий: сначала ставить ТНТ или сначала стрелять",
      Arrays.<String>asList("Сначала ставить", "Сначала стрелять"),
      Collections.<String>singletonList("Сначала ставить"),
      false
   );
   private ActionKeySetting activationBindSetting = new ActionKeySetting("Бинд активации", "Кнопка для активации AutoCart", 86, this::a);
   private SliderSetting bowChargeTicksSetting = new SliderSetting("Тики зарядки лука", "Сколько тиков заряжать лук", 7.0, 5.0, 20.0, 1.0);
   private SliderSetting placeDelaySetting = new SliderSetting("Делей установки", "Задержка между действиями (тики)", 2.0, 1.0, 10.0, 1.0);
   private SliderSetting aimSpeedSetting = new SliderSetting("Скорость наводки", "Скорость поворота камеры", 5.0, 1.0, 10.0, 0.5);
   private BooleanSetting autoDisableSetting = new BooleanSetting("Авто-выкл", "Выключить модуль после выстрела", false);
   private aw vx = aw.ate;
   private int um = 0;
   private BlockPos f = null;
   private Direction Rc = Direction.UP;
   private Vec3d kj = null;
   private float aqX = 0.0F;
   private float ade = 0.0F;
   private int hk = -1;
   private int atZ = -1;
   private int Dr = -1;
   private int adq = -1;
   private int amg = -1;
   private int bL = 0;
   private long atk = 0L;
   private static final double ahN = 3.0;
   private static final double zn = 0.05;
   private static final double Yz = 0.99;
   private static final double adD = 1.5;

   public AutoCartModule() {
      super("АутоСарт", "Автоматически ставит рельсы, вагонетку и стреляет из лука", Category.COMBAT);
      this.addSettings(
         this.modeSetting, this.activationBindSetting, this.bowChargeTicksSetting, this.placeDelaySetting, this.aimSpeedSetting, this.autoDisableSetting
      );
   }

   @Override
   public void onEnable() {
      this.T();
   }

   @Override
   public void onDisable() {
      this.T();
   }

   private void a() {
      if (this.isEnabled() && !this.isNotInWorld()) {
         if (this.vx == aw.ate) {
            PlayerEntity playerentity = this.getPlayer();
            if (playerentity != null) {
               BlockHitResult blockhitresult = this.Q(5.0);
               if (blockhitresult != null && blockhitresult.getType() == Type.BLOCK) {
                  this.f = blockhitresult.getBlockPos();
                  this.Rc = blockhitresult.getSide();
                  BlockPos blockpos = this.f.offset(this.Rc);
                  this.kj = Vec3d.ofBottomCenter(blockpos);
                  this.atZ = this.R(Items.RAIL);
                  if (this.atZ != -1) {
                     this.Dr = this.R(Items.TNT_MINECART);
                     if (this.Dr != -1) {
                        this.adq = this.R(Items.BOW);
                        if (this.adq != -1) {
                           this.amg = this.R(Items.ARROW);
                           if (this.amg != -1) {
                              this.hk = playerentity.getInventory().selectedSlot;
                              this.atk = System.nanoTime();
                              boolean flag = "Сначала стрелять".equals(this.modeSetting.getFirst());
                              if (flag) {
                                 this.e();
                                 this.vx = aw.uz;
                              } else {
                                 this.d();
                                 this.vx = aw.uZ;
                              }

                              this.um = 0;
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
   public void onRenderStart() {
      if (this.isEnabled() && !this.isNotInWorld() && this.vx != aw.ate && this.vx != aw.awW) {
         if (this.getPlayer() != null) {
            long i = System.nanoTime();
            double d0 = this.atk > 0L ? (i - this.atk) / 1.66666667E7 : 1.0;
            d0 = MathHelper.clamp(d0, 0.05, 3.0);
            this.atk = i;
            switch (this.vx) {
               case uZ:
               case atO:
               case zR:
               case hM:
               case zV:
                  this.d();
                  break;
               case AY:
               case ads:
               case auN:
               case UB:
               case ark:
                  this.e();
                  break;
               case uz:
               case ajZ:
               case yV:
               case ld:
               case Pf:
                  this.e();
               case akN:
               default:
                  break;
               case aj:
               case anX:
               case dV:
               case jo:
                  this.d();
            }

            this.b(d0);
         }
      }
   }

   private void b(double d0) {
      float fx = this.getPlayer().getYaw();
      float f1 = this.getPlayer().getPitch();
      double d1 = xw.o(this.aqX - fx);
      double d2 = this.ade - f1;
      double d3 = Math.sqrt(d1 * d1 + d2 * d2);
      if (!(d3 < 0.05)) {
         double d4 = this.aimSpeedSetting.getValue();
         double d5 = MathHelper.clamp(d4 * 0.04 * d0, 0.01, 0.95);
         double d6 = d1 * d5;
         double d7 = d2 * d5;
         double d8 = xw.m();
         d6 = xw.n(d6, d8);
         d7 = xw.n(d7, d8);
         if (d6 == 0.0 && Math.abs(d1) > d8) {
            d6 = Math.signum(d1) * d8;
         }

         if (d7 == 0.0 && Math.abs(d2) > d8) {
            d7 = Math.signum(d2) * d8;
         }

         float f2 = fx + (float)d6;
         float f3 = MathHelper.clamp(f1 + (float)d7, -90.0F, 90.0F);
         this.getPlayer().setYaw(f2);
         this.getPlayer().setPitch(f3);
      }
   }

   private boolean c() {
      float fx = this.getPlayer().getYaw();
      float f1 = this.getPlayer().getPitch();
      double d0 = xw.o(this.aqX - fx);
      double d1 = this.ade - f1;
      return Math.sqrt(d0 * d0 + d1 * d1) < 1.5;
   }

   private void d() {
      float[] afloat = this.M(this.kj);
      this.aqX = afloat[0];
      this.ade = afloat[1];
   }

   private void e() {
      float[] afloat = this.N(this.kj);
      this.aqX = afloat[0];
      this.ade = afloat[1];
   }

   @Override
   public void onStartTick() {
      if (this.isEnabled() && !this.isNotInWorld()) {
         if (this.vx != aw.auN && this.vx != aw.UB && this.vx != aw.yV && this.vx != aw.ld) {
            if (this.vx == aw.ark || this.vx == aw.Pf) {
               this.getOptions().useKey.setPressed(false);
            }
         } else {
            this.getOptions().useKey.setPressed(true);
         }
      }
   }

   @Override
   public void onEndTick() {
      if (this.isEnabled() && !this.isNotInWorld() && this.vx != aw.ate) {
         this.um++;
         switch (this.vx) {
            case uZ:
               this.f();
               break;
            case atO:
               this.h();
               break;
            case zR:
               this.i();
               break;
            case hM:
               this.o();
               break;
            case zV:
               this.p();
               break;
            case AY:
               this.q();
               break;
            case ads:
               this.r();
               break;
            case auN:
               this.u();
               break;
            case UB:
               this.v();
               break;
            case ark:
               this.w();
               break;
            case uz:
               this.x();
               break;
            case ajZ:
               this.y();
               break;
            case yV:
               this.z();
               break;
            case ld:
               this.A();
               break;
            case Pf:
               this.B();
               break;
            case akN:
               this.C();
               break;
            case aj:
               this.D();
               break;
            case anX:
               this.E();
               break;
            case dV:
               this.F();
               break;
            case jo:
               this.G();
               break;
            case arK:
               this.K();
               break;
            case awW:
               this.L();
         }

         if (this.um > 200) {
            this.T();
         }
      }
   }

   private void f() {
      if (this.c() && this.um >= 2) {
         this.vx = aw.atO;
         this.um = 0;
      }
   }

   private void h() {
      this.S(this.atZ);
      BlockHitResult blockhitresult = new BlockHitResult(Vec3d.ofCenter(this.f).add(Vec3d.of(this.Rc.getVector()).multiply(0.5)), this.Rc, this.f, false);
      ClientPlayerInteractionManager clientplayerinteractionmanager = this.getInteractionManager();
      if (clientplayerinteractionmanager != null) {
         clientplayerinteractionmanager.interactBlock(this.getClientPlayer(), Hand.MAIN_HAND, blockhitresult);
         this.getPlayer().swingHand(Hand.MAIN_HAND);
      }

      this.vx = aw.zR;
      this.um = 0;
   }

   private void i() {
      if (this.um >= (int)this.placeDelaySetting.getValue()) {
         this.vx = aw.hM;
         this.um = 0;
      }
   }

   private void o() {
      this.S(this.Dr);
      BlockPos blockpos = this.f.offset(this.Rc);
      BlockHitResult blockhitresult = new BlockHitResult(Vec3d.ofBottomCenter(blockpos).add(0.0, 0.0625, 0.0), Direction.UP, blockpos, false);
      ClientPlayerInteractionManager clientplayerinteractionmanager = this.getInteractionManager();
      if (clientplayerinteractionmanager != null) {
         clientplayerinteractionmanager.interactBlock(this.getClientPlayer(), Hand.MAIN_HAND, blockhitresult);
         this.getPlayer().swingHand(Hand.MAIN_HAND);
      }

      this.vx = aw.zV;
      this.um = 0;
   }

   private void p() {
      if (this.um >= (int)this.placeDelaySetting.getValue()) {
         this.vx = aw.AY;
         this.um = 0;
      }
   }

   private void q() {
      this.S(this.adq);
      this.vx = aw.ads;
      this.um = 0;
   }

   private void r() {
      if (this.c() && this.um >= 2) {
         this.vx = aw.auN;
         this.um = 0;
      }
   }

   private void u() {
      ClientPlayerInteractionManager clientplayerinteractionmanager = this.getInteractionManager();
      if (clientplayerinteractionmanager != null) {
         clientplayerinteractionmanager.interactItem(this.getPlayer(), Hand.MAIN_HAND);
      }

      this.bL = 0;
      this.vx = aw.UB;
      this.um = 0;
   }

   private void v() {
      this.bL++;
      if (this.bL >= (int)this.bowChargeTicksSetting.getValue()) {
         this.vx = aw.ark;
         this.um = 0;
      }
   }

   private void w() {
      this.getOptions().useKey.setPressed(false);
      if (this.getNetworkHandler() != null) {
         this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
      }

      if (this.getPlayer() != null) {
         this.getPlayer().stopUsingItem();
      }

      this.vx = aw.arK;
      this.um = 0;
   }

   private void x() {
      this.S(this.adq);
      this.vx = aw.ajZ;
      this.um = 0;
   }

   private void y() {
      if (this.c() && this.um >= 2) {
         this.vx = aw.yV;
         this.um = 0;
      }
   }

   private void z() {
      ClientPlayerInteractionManager clientplayerinteractionmanager = this.getInteractionManager();
      if (clientplayerinteractionmanager != null) {
         clientplayerinteractionmanager.interactItem(this.getPlayer(), Hand.MAIN_HAND);
      }

      this.bL = 0;
      this.vx = aw.ld;
      this.um = 0;
   }

   private void A() {
      this.bL++;
      if (this.bL >= (int)this.bowChargeTicksSetting.getValue()) {
         this.vx = aw.Pf;
         this.um = 0;
      }
   }

   private void B() {
      this.getOptions().useKey.setPressed(false);
      if (this.getNetworkHandler() != null) {
         this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
      }

      if (this.getPlayer() != null) {
         this.getPlayer().stopUsingItem();
      }

      this.vx = aw.akN;
      this.um = 0;
   }

   private void C() {
      if (this.um >= 1) {
         this.d();
         this.vx = aw.aj;
         this.um = 0;
      }
   }

   private void D() {
      if (this.c() && this.um >= 2) {
         this.vx = aw.anX;
         this.um = 0;
      }
   }

   private void E() {
      this.S(this.atZ);
      BlockHitResult blockhitresult = new BlockHitResult(Vec3d.ofCenter(this.f).add(Vec3d.of(this.Rc.getVector()).multiply(0.5)), this.Rc, this.f, false);
      ClientPlayerInteractionManager clientplayerinteractionmanager = this.getInteractionManager();
      if (clientplayerinteractionmanager != null) {
         clientplayerinteractionmanager.interactBlock(this.getClientPlayer(), Hand.MAIN_HAND, blockhitresult);
         this.getPlayer().swingHand(Hand.MAIN_HAND);
      }

      this.vx = aw.dV;
      this.um = 0;
   }

   private void F() {
      if (this.um >= (int)this.placeDelaySetting.getValue()) {
         this.vx = aw.jo;
         this.um = 0;
      }
   }

   private void G() {
      this.S(this.Dr);
      BlockPos blockpos = this.f.offset(this.Rc);
      BlockHitResult blockhitresult = new BlockHitResult(Vec3d.ofBottomCenter(blockpos).add(0.0, 0.0625, 0.0), Direction.UP, blockpos, false);
      ClientPlayerInteractionManager clientplayerinteractionmanager = this.getInteractionManager();
      if (clientplayerinteractionmanager != null) {
         clientplayerinteractionmanager.interactBlock(this.getClientPlayer(), Hand.MAIN_HAND, blockhitresult);
         this.getPlayer().swingHand(Hand.MAIN_HAND);
      }

      this.vx = aw.arK;
      this.um = 0;
   }

   private void K() {
      if (this.um >= 1) {
         if (this.hk >= 0) {
            this.getPlayer().getInventory().selectedSlot = this.hk;
         }

         this.vx = aw.awW;
         this.um = 0;
      }
   }

   private void L() {
      this.T();
      if (this.autoDisableSetting.getValue()) {
         this.setEnabled(false);
      }
   }

   private float[] M(Vec3d vec3d) {
      Vec3d vec3d1 = this.getPlayer().getEyePos();
      double d0 = vec3d.x - vec3d1.x;
      double d1 = vec3d.y - vec3d1.y;
      double d2 = vec3d.z - vec3d1.z;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      float fx = (float)(Math.atan2(d2, d0) * 180.0 / Math.PI) - 90.0F;
      float f1 = (float)(-(Math.atan2(d1, d3) * 180.0 / Math.PI));
      return new float[]{fx, f1};
   }

   private float[] N(Vec3d vec3d) {
      Vec3d vec3d1 = this.getPlayer().getEyePos();
      double d0 = vec3d.x - vec3d1.x;
      double d1 = vec3d.y + 0.5 - vec3d1.y;
      double d2 = vec3d.z - vec3d1.z;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      int i = (int)this.bowChargeTicksSetting.getValue();
      float fx = BowItem.getPullProgress(i);
      double d4 = fx * 3.0;
      if (d4 < 0.1) {
         d4 = 0.1;
      }

      float f1 = this.O(d3, d1, d4);
      float f2 = (float)(Math.atan2(d2, d0) * 180.0 / Math.PI) - 90.0F;
      return new float[]{f2, f1};
   }

   private float O(double d0, double d1, double d2) {
      float fx = 0.0F;
      double d3 = Double.MAX_VALUE;

      for (float f1 = -90.0F; f1 <= 90.0F; f1 += 0.25F) {
         double d4 = this.P(f1, d0, d1, d2);
         if (d4 < d3) {
            d3 = d4;
            fx = f1;
         }
      }

      for (float f2 = fx - 0.5F; f2 <= fx + 0.5F; f2 += 0.02F) {
         double d5 = this.P(f2, d0, d1, d2);
         if (d5 < d3) {
            d3 = d5;
            fx = f2;
         }
      }

      return fx;
   }

   private double P(float f, double d0, double d1, double d2) {
      double d3 = Math.toRadians(fx);
      double d4 = Math.cos(d3);
      double d5 = -Math.sin(d3);
      double d6 = d4 * d2;
      double d7 = d5 * d2;
      double d8 = 0.0;
      double d9 = 0.0;
      double d10 = 0.0;
      double d11 = 0.0;

      for (int i = 0; i < 300; i++) {
         d10 = d8;
         d11 = d9;
         d8 += d6;
         d9 += d7;
         d6 *= 0.99;
         d7 = d7 * 0.99 - 0.05;
         if (d8 >= d0) {
            double d12 = (d0 - d10) / (d8 - d10);
            double d13 = d11 + (d9 - d11) * d12;
            return Math.abs(d13 - d1);
         }
      }

      return Math.abs(d9 - d1) + Math.abs(d8 - d0) * 10.0;
   }

   private BlockHitResult Q(double d0) {
      PlayerEntity playerentity = this.getPlayer();
      if (playerentity != null && this.getWorld() != null) {
         Vec3d vec3d = playerentity.getEyePos();
         Vec3d vec3d1 = playerentity.getRotationVec(1.0F);
         Vec3d vec3d2 = vec3d.add(vec3d1.multiply(d0));
         RaycastContext raycastcontext = new RaycastContext(vec3d, vec3d2, ShapeType.OUTLINE, FluidHandling.NONE, playerentity);
         return this.getWorld().raycast(raycastcontext);
      } else {
         return null;
      }
   }

   private int R(Item item) {
      PlayerInventory playerinventory = this.getInventory();
      if (playerinventory == null) {
         return -1;
      } else {
         for (int i = 0; i < 9; i++) {
            if (playerinventory.getStack(i).isOf(item)) {
               return i;
            }
         }

         for (int j = 9; j < 36; j++) {
            if (playerinventory.getStack(j).isOf(item)) {
               return j;
            }
         }

         return -1;
      }
   }

   private void S(int i) {
      if (i >= 0 && this.getPlayer() != null) {
         if (i < 9) {
            this.getPlayer().getInventory().selectedSlot = i;
         } else {
            int j = Math.max(this.hk, 0);
            ClientPlayerInteractionManager clientplayerinteractionmanager = this.getInteractionManager();
            if (clientplayerinteractionmanager != null) {
               int k = this.getPlayer().currentScreenHandler.syncId;
               clientplayerinteractionmanager.clickSlot(k, i, j, SlotActionType.SWAP, this.getPlayer());
            }

            this.getPlayer().getInventory().selectedSlot = j;
         }
      }
   }

   private void T() {
      if (this.getOptions() != null) {
         this.getOptions().useKey.setPressed(false);
      }

      this.vx = aw.ate;
      this.um = 0;
      this.f = null;
      this.Rc = Direction.UP;
      this.kj = null;
      this.aqX = 0.0F;
      this.ade = 0.0F;
      this.hk = -1;
      this.atZ = -1;
      this.Dr = -1;
      this.adq = -1;
      this.amg = -1;
      this.bL = 0;
      this.atk = 0L;
   }
}
