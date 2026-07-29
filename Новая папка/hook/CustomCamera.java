package hook;

import core.ClientMain;
import java.lang.reflect.Method;
import module.CamTweaksModule;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.BlockView;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import util.UnsafeFieldAccessor;

public class CustomCamera extends Camera {
   private UnsafeFieldAccessor<Boolean> readyField;
   private UnsafeFieldAccessor<BlockView> areaField;
   private UnsafeFieldAccessor<Entity> focusedEntityField;
   private UnsafeFieldAccessor<Boolean> thirdPersonField;
   private UnsafeFieldAccessor<Float> lastTickDeltaField;
   private UnsafeFieldAccessor<Float> lastCameraYField;
   private UnsafeFieldAccessor<Float> cameraYField;
   private UnsafeFieldAccessor<Vec3d> posField;
   private UnsafeFieldAccessor<Mutable> blockPosField;
   private UnsafeFieldAccessor<Float> pitchField;
   private UnsafeFieldAccessor<Float> yawField;
   private UnsafeFieldAccessor<Quaternionf> rotationField;
   private UnsafeFieldAccessor<Vector3f> horizontalPlaneField;
   private UnsafeFieldAccessor<Vector3f> verticalPlaneField;
   private UnsafeFieldAccessor<Vector3f> diagonalPlaneField;
   private Method clipToSpaceMethod;
   private Vec3d targetPos = Vec3d.ZERO;
   private Vec3d currentPos = Vec3d.ZERO;
   private float targetYaw = 0.0F;
   private float targetPitch = 0.0F;
   private float currentYaw = 0.0F;
   private float currentPitch = 0.0F;

   public CustomCamera() {
      this.readyField = new UnsafeFieldAccessor<Boolean>(this, Camera.class, 4);
      this.areaField = new UnsafeFieldAccessor<BlockView>(this, Camera.class, 5);
      this.focusedEntityField = new UnsafeFieldAccessor<Entity>(this, Camera.class, 6);
      this.thirdPersonField = new UnsafeFieldAccessor<Boolean>(this, Camera.class, 15);
      this.lastTickDeltaField = new UnsafeFieldAccessor<Float>(this, Camera.class, 18);
      this.lastCameraYField = new UnsafeFieldAccessor<Float>(this, Camera.class, 17);
      this.cameraYField = new UnsafeFieldAccessor<Float>(this, Camera.class, 16);
      this.posField = new UnsafeFieldAccessor<Vec3d>(this, Camera.class, 7);
      this.blockPosField = new UnsafeFieldAccessor<Mutable>(this, Camera.class, 8);
      this.pitchField = new UnsafeFieldAccessor<Float>(this, Camera.class, 12);
      this.yawField = new UnsafeFieldAccessor<Float>(this, Camera.class, 13);
      this.rotationField = new UnsafeFieldAccessor<Quaternionf>(this, Camera.class, 14);
      this.horizontalPlaneField = new UnsafeFieldAccessor<Vector3f>(this, Camera.class, 9);
      this.verticalPlaneField = new UnsafeFieldAccessor<Vector3f>(this, Camera.class, 10);
      this.diagonalPlaneField = new UnsafeFieldAccessor<Vector3f>(this, Camera.class, 11);
      String s = ClientMain.getInstance().isDev() ? "clipToSpace" : "method_19318";

      try {
         this.clipToSpaceMethod = Camera.class.getDeclaredMethod(s, float.class);
         this.clipToSpaceMethod.setAccessible(true);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void update(BlockView blockview, Entity entity, boolean flag, boolean flag1, float f) {
      CamTweaksModule camtweaksmodule = ClientMain.getInstance().getModuleManager().<CamTweaksModule>getModule(CamTweaksModule.class);
      boolean flag2 = camtweaksmodule != null && camtweaksmodule.isEnabled();

      try {
         this.readyField.setBoolean(true);
         this.areaField.setValue(blockview);
         this.focusedEntityField.setValue(entity);
         this.thirdPersonField.setBoolean(flag);
         this.lastTickDeltaField.setFloat(f);
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      float f1 = this.getCameraY(f);
      float f2 = entity.getYaw(f);
      float f3 = entity.getPitch(f);
      if (entity.hasVehicle()
         && entity.getVehicle() instanceof MinecartEntity minecartentity
         && minecartentity.getController() instanceof ExperimentalMinecartController experimentalminecartcontroller
         && experimentalminecartcontroller.hasCurrentLerpSteps()) {
         Vec3d vec3d = minecartentity.getPassengerRidingPos(entity)
            .subtract(minecartentity.getPos())
            .subtract(entity.getVehicleAttachmentPos(minecartentity))
            .add(new Vec3d(0.0, f1, 0.0));
         this.targetPos = experimentalminecartcontroller.getLerpedPosition(f).add(vec3d);
      } else {
         this.targetPos = new Vec3d(
            MathHelper.lerp(f, entity.prevX, entity.getX()),
            MathHelper.lerp(f, entity.prevY, entity.getY()) + f1,
            MathHelper.lerp(f, entity.prevZ, entity.getZ())
         );
      }

      this.targetYaw = f2;
      this.targetPitch = f3;
      boolean flag3 = true;
      if (flag2 && camtweaksmodule.d().getValue()) {
         flag3 = flag;
      }

      float f4;
      float f5;
      float f8;
      float f9;
      if (flag2 && flag3) {
         f8 = camtweaksmodule.e().getFloatValue();
         f9 = camtweaksmodule.f().getFloatValue();
         f4 = camtweaksmodule.g().getFloatValue();
         f5 = camtweaksmodule.h().getFloatValue();
      } else {
         f8 = 1.0F;
         f9 = 1.0F;
         f4 = 1.0F;
         f5 = 1.0F;
      }

      this.currentPos = new Vec3d(
         MathHelper.lerp(f8, this.currentPos.x, this.targetPos.x),
         MathHelper.lerp(f9, this.currentPos.y, this.targetPos.y),
         MathHelper.lerp(f4, this.currentPos.z, this.targetPos.z)
      );
      this.currentYaw = this.lerpAngle(this.currentYaw, this.targetYaw, f5);
      this.currentPitch = MathHelper.lerp(f5, this.currentPitch, this.targetPitch);
      this.setRotation(this.currentYaw, this.currentPitch);
      this.setPos(this.currentPos);
      if (flag) {
         if (flag1) {
            this.setRotation(this.getYaw() + 180.0F, -this.getPitch());
         }

         float f10 = entity instanceof LivingEntity livingentity ? livingentity.getScale() : 1.0F;
         if (flag2) {
            float f11 = camtweaksmodule.a(f) * f10;
            this.moveByCustom(-f11, 0.0F, 0.0F);
            float f6 = camtweaksmodule.b().getFloatValue();
            float f7 = camtweaksmodule.c().getFloatValue();
            this.moveByCustom(0.0F, f7, f6);
         } else {
            this.moveBy(-this.clipToSpace(4.0F * f10), 0.0F, 0.0F);
         }
      } else {
         if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
            Direction direction = ((LivingEntity)entity).getSleepingDirection();
            this.setRotation(direction != null ? direction.getPositiveHorizontalDegrees() - 180.0F : 0.0F, 0.0F);
            this.moveBy(0.0F, 0.3F, 0.0F);
         }
      }
   }

   private float clipToSpace(float f) {
      try {
         return (Float)this.clipToSpaceMethod.invoke(this, f);
      } catch (Exception exception) {
         exception.printStackTrace();
         return f;
      }
   }

   private float lerpAngle(float f, float f1, float f2) {
      float f3 = (f1 - f + 180.0F) % 360.0F - 180.0F;
      if (f3 < -180.0F) {
         f3 += 360.0F;
      }

      return f + f3 * f2;
   }

   private float getCameraY(float f) {
      try {
         float f1 = this.lastCameraYField.getFloat();
         float f2 = this.cameraYField.getFloat();
         return MathHelper.lerp(f, f1, f2);
      } catch (Exception exception) {
         return 0.0F;
      }
   }

   private void moveByCustom(float f, float f1, float f2) {
      Vector3f vector3f = new Vector3f(f2, f1, -f).rotate(this.getRotation());
      Vec3d vec3d = this.getPos();
      Vec3d vec3d1 = new Vec3d(vec3d.x + vector3f.x, vec3d.y + vector3f.y, vec3d.z + vector3f.z);

      try {
         this.posField.setValue(vec3d1);
         Mutable mutable = this.blockPosField.getValue();
         mutable.set(vec3d1.x, vec3d1.y, vec3d1.z);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void setRotation(float f, float f1) {
      try {
         this.pitchField.setFloat(f1);
         this.yawField.setFloat(f);
         Quaternionf quaternionf = new Quaternionf();
         quaternionf.rotationYXZ((float) Math.PI - f * (float) (Math.PI / 180.0), -f1 * (float) (Math.PI / 180.0), 0.0F);
         this.rotationField.setValue(quaternionf);
         Vector3f vector3f = new Vector3f(0.0F, 0.0F, -1.0F);
         Vector3f vector3f1 = new Vector3f(0.0F, 1.0F, 0.0F);
         Vector3f vector3f2 = new Vector3f(-1.0F, 0.0F, 0.0F);
         Vector3f vector3f3 = this.horizontalPlaneField.getValue();
         vector3f.rotate(quaternionf, vector3f3);
         Vector3f vector3f4 = this.verticalPlaneField.getValue();
         vector3f1.rotate(quaternionf, vector3f4);
         Vector3f vector3f5 = this.diagonalPlaneField.getValue();
         vector3f2.rotate(quaternionf, vector3f5);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void setPos(double d0, double d1, double d2) {
      this.setPos(new Vec3d(d0, d1, d2));
   }

   public void setPos(Vec3d vec3d) {
      try {
         this.posField.setValue(vec3d);
         Mutable mutable = this.blockPosField.getValue();
         mutable.set(vec3d.x, vec3d.y, vec3d.z);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
}
