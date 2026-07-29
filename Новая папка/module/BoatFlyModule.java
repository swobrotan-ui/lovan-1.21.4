package module;

import enum.Category;
import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import setting.BooleanSetting;
import setting.SliderSetting;

public class BoatFlyModule extends Module {
   public SliderSetting upSpeedSetting = new SliderSetting("Скорость вверх", "скорость вверх", 0.4F, 0.05F, 5.0, 0.05F);
   public SliderSetting downSpeedSetting = new SliderSetting("Скорость вниз", "скорость вниз", 0.4F, 0.05F, 5.0, 0.05F);
   public SliderSetting sideSpeedSetting = new SliderSetting("Скорость в стороны", "скорость в стороны", 0.6F, 0.05F, 5.0, 0.05F);
   public SliderSetting blockSpeedSetting = new SliderSetting("Скорость в блоках", "скорость в блоках", 0.25, 0.05F, 2.0, 0.05F);
   public SliderSetting checkDistanceSetting = new SliderSetting("Дистанция проверки", "дистанция проверки блоков", 0.5, 0.1F, 2.0, 0.1F);
   public BooleanSetting useItemsSetting = new BooleanSetting("Использование предметов", "Позволяет использовать предметы в лодке", true);
   private boolean JJ = false;

   public BoatFlyModule() {
      super("БоатФлй", "Позволяет двигаться на лодке сквозь блоки", Category.MOVEMENT);
      this.addSettings(
         this.upSpeedSetting, this.downSpeedSetting, this.sideSpeedSetting, this.blockSpeedSetting, this.checkDistanceSetting, this.useItemsSetting
      );
   }

   @Override
   public void onEndTick() {
      if (this.getPlayer() != null && this.getWorld() != null) {
         if (this.getPlayer().getVehicle() instanceof BoatEntity boatentity) {
            if (this.useItemsSetting.getValue()) {
               this.a();
            }

            boatentity.noClip = true;
            boatentity.setNoGravity(true);
            boolean flag1 = this.b();
            boolean flag = this.c(boatentity);
            double d0 = !flag1 && !flag ? this.sideSpeedSetting.getValue() : this.blockSpeedSetting.getValue();
            boatentity.setYaw(this.getPlayer().getYaw());
            boatentity.prevYaw = boatentity.getYaw();
            double d1 = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            float f = boatentity.getYaw();
            double d4 = Math.toRadians(f);
            if (this.getOptions().jumpKey.isPressed()) {
               d2 += this.upSpeedSetting.getValue();
            }

            if (GLFW.glfwGetKey(this.getClient().getWindow().getHandle(), 88) == 1) {
               d2 -= this.downSpeedSetting.getValue();
            }

            PlayerInput playerinput = this.getClientPlayer().input.playerInput;
            if (playerinput.forward()) {
               d1 -= MathHelper.sin((float)d4) * d0;
               d3 += MathHelper.cos((float)d4) * d0;
            }

            if (playerinput.backward()) {
               d1 += MathHelper.sin((float)d4) * d0;
               d3 -= MathHelper.cos((float)d4) * d0;
            }

            if (playerinput.left()) {
               d1 += MathHelper.cos((float)d4) * d0;
               d3 += MathHelper.sin((float)d4) * d0;
            }

            if (playerinput.right()) {
               d1 -= MathHelper.cos((float)d4) * d0;
               d3 -= MathHelper.sin((float)d4) * d0;
            }

            Vec3d vec3d = new Vec3d(d1, d2, d3);
            boatentity.setVelocity(vec3d);
            if (!this.getPlayer().hasVehicle()) {
               this.getPlayer().startRiding(boatentity, true);
            }
         } else {
            this.JJ = false;
         }
      }
   }

   private void a() {
      boolean flag = this.getOptions().useKey.isPressed();
      if (flag && !this.JJ) {
         if (!this.getPlayer().getMainHandStack().isEmpty()) {
            this.interactItem(Hand.MAIN_HAND);
         } else if (!this.getPlayer().getOffHandStack().isEmpty()) {
            this.interactItem(Hand.OFF_HAND);
         }
      }

      if (!flag && this.JJ && this.getPlayer().isUsingItem()) {
         this.getPlayer().stopUsingItem();
      }

      this.JJ = flag;
   }

   private boolean b() {
      if (this.isNotInWorld()) {
         return false;
      } else {
         Box box = this.getPlayer().getBoundingBox().expand(0.001);
         int i = MathHelper.floor(box.minX);
         int j = MathHelper.floor(box.minY);
         int k = MathHelper.floor(box.minZ);
         int l = MathHelper.floor(box.maxX);
         int i1 = MathHelper.floor(box.maxY);
         int j1 = MathHelper.floor(box.maxZ);

         for (int k1 = i; k1 <= l; k1++) {
            for (int l1 = j; l1 <= i1; l1++) {
               for (int i2 = k; i2 <= j1; i2++) {
                  BlockPos blockpos = BlockPos.ofFloored(k1, l1, i2);
                  BlockState blockstate = this.getWorld().getBlockState(blockpos);
                  if (blockstate.isSolid()) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private boolean c(BoatEntity boatentity) {
      if (this.isNotInWorld()) {
         return false;
      } else {
         Vec3d vec3d = boatentity.getPos();
         float f = boatentity.getYaw();
         double d0 = Math.toRadians(f);
         double d1 = this.checkDistanceSetting.getValue();
         PlayerInput playerinput = this.getClientPlayer().input.playerInput;
         double d2 = vec3d.x;
         double d3 = vec3d.y;
         double d4 = vec3d.z;
         if (playerinput.forward()) {
            d2 -= MathHelper.sin((float)d0) * d1;
            d4 += MathHelper.cos((float)d0) * d1;
         }

         if (playerinput.backward()) {
            d2 += MathHelper.sin((float)d0) * d1;
            d4 -= MathHelper.cos((float)d0) * d1;
         }

         if (playerinput.left()) {
            d2 += MathHelper.cos((float)d0) * d1;
            d4 += MathHelper.sin((float)d0) * d1;
         }

         if (playerinput.right()) {
            d2 -= MathHelper.cos((float)d0) * d1;
            d4 -= MathHelper.sin((float)d0) * d1;
         }

         if (this.getOptions().jumpKey.isPressed()) {
            d3 += d1;
         }

         if (GLFW.glfwGetKey(this.getClient().getWindow().getHandle(), 88) == 1) {
            d3 -= d1;
         }

         Box box = new Box(d2 - 0.5, d3 - 0.5, d4 - 0.5, d2 + 0.5, d3 + 0.5, d4 + 0.5);
         int i = MathHelper.floor(box.minX);
         int j = MathHelper.floor(box.minY);
         int k = MathHelper.floor(box.minZ);
         int l = MathHelper.floor(box.maxX);
         int i1 = MathHelper.floor(box.maxY);
         int j1 = MathHelper.floor(box.maxZ);

         for (int k1 = i; k1 <= l; k1++) {
            for (int l1 = j; l1 <= i1; l1++) {
               for (int i2 = k; i2 <= j1; i2++) {
                  BlockPos blockpos = BlockPos.ofFloored(k1, l1, i2);
                  BlockState blockstate = this.getWorld().getBlockState(blockpos);
                  if (blockstate.isSolid()) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   @Override
   public void onEnable() {
      this.JJ = false;
   }

   @Override
   public void onDisable() {
      if (this.getPlayer() != null && this.getPlayer().getVehicle() instanceof BoatEntity boatentity) {
         boatentity.noClip = false;
         boatentity.setNoGravity(false);
      }

      this.JJ = false;
   }
}
