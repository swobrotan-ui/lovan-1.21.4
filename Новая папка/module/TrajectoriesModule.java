package module;

import data.ProjectileParams;
import enum.Category;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import render.BuiltLine3d;
import setting.BooleanSetting;
import setting.ColorSetting;

public class TrajectoriesModule extends Module {
   private static final long GY = 1000000000L;
   private BooleanSetting showLandingPointSetting = new BooleanSetting("Показывать точку приземления", "Отображать крестик в точке приземления", true);
   private ColorSetting landingColorSetting = new ColorSetting("Цвет приземления", "Цвет крестика приземления", new Color(131, 197, 255), true);
   private final rpy Rl = new rpy();
   private final List<Vec3d> PG = new ArrayList<Vec3d>();
   private Vec3d UL = null;
   private Vec3d zO = null;
   private Direction ha = Direction.UP;
   private float Ho = 0.0F;
   private long uD = 0L;

   public TrajectoriesModule() {
      super("Тражесториез", "Показывает предполагаемую траекторию полёта снарядов", Category.RENDER);
      this.addSettings(this.showLandingPointSetting, this.landingColorSetting);
      this.U(this.showLandingPointSetting.getName(), this.landingColorSetting.getName(), true);
   }

   @Override
   public void onEnable() {
      this.uD = System.nanoTime();
      this.Ho = 0.0F;
      this.zO = null;
   }

   @Override
   public void onDisable() {
      this.PG.clear();
      this.UL = null;
      this.zO = null;
      this.Ho = 0.0F;
   }

   @Override
   public void onRenderStart() {
      if (this.isEnabled() && !this.isNotInWorld()) {
         this.PG.clear();
         this.UL = null;
         PlayerEntity playerentity = this.getPlayer();
         if (playerentity != null) {
            this.zO = playerentity.getPos();
            ItemStack itemstack = playerentity.getMainHandStack();
            ItemStack itemstack1 = playerentity.getOffHandStack();
            ItemStack itemstack2 = this.b(itemstack, itemstack1);
            if (itemstack2 != null) {
               ProjectileParams projectileparams = this.d(itemstack2, playerentity);
               if (projectileparams != null) {
                  this.e(playerentity, projectileparams);
               }
            }
         }
      }
   }

   @Override
   public void onRenderAfterTranslucent(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld() && !this.PG.isEmpty()) {
         this.a();

         try {
            MatrixStack matrixstack = worldrendercontext.matrixStack();
            Vec3d vec3d = worldrendercontext.camera().getPos();
            if (this.showLandingPointSetting.getValue() && this.UL != null && this.Ho > 0.001F) {
               Vec3d vec3d1 = this.UL;
               PlayerEntity playerentity = this.getPlayer();
               if (playerentity != null && this.zO != null) {
                  float f = worldrendercontext.tickCounter().getTickDelta(true);
                  double d0 = MathHelper.lerp(f, playerentity.prevX, playerentity.getX());
                  double d1 = MathHelper.lerp(f, playerentity.prevY, playerentity.getY());
                  double d2 = MathHelper.lerp(f, playerentity.prevZ, playerentity.getZ());
                  vec3d1 = this.UL.add(d0 - this.zO.x, d1 - this.zO.y, d2 - this.zO.z);
               }

               this.h(matrixstack, vec3d, vec3d1, 1.7F);
            }
         } catch (Exception exception) {
         }
      }
   }

   private void a() {
      long i = System.nanoTime();
      if (this.uD != 0L && i - this.uD <= 1000000000L) {
         float f = (float)(i - this.uD) / 1.0E9F;
         this.uD = i;
         float f1 = 20.0F;
         if (this.UL != null) {
            this.Ho = Math.min(1.0F, this.Ho + f * f1);
         } else {
            this.Ho = Math.max(0.0F, this.Ho - f * f1);
         }
      } else {
         this.uD = i;
      }
   }

   private ItemStack b(ItemStack itemstack, ItemStack itemstack1) {
      if (this.c(itemstack)) {
         return itemstack;
      } else {
         return this.c(itemstack1) ? itemstack1 : null;
      }
   }

   private boolean c(ItemStack itemstack) {
      return itemstack == null || itemstack.isEmpty()
         ? false
         : itemstack.isOf(Items.ENDER_PEARL)
            || itemstack.isOf(Items.SNOWBALL)
            || itemstack.isOf(Items.EGG)
            || itemstack.isOf(Items.SPLASH_POTION)
            || itemstack.isOf(Items.LINGERING_POTION)
            || itemstack.isOf(Items.BOW)
            || itemstack.isOf(Items.CROSSBOW)
            || itemstack.isOf(Items.TRIDENT)
            || itemstack.isOf(Items.WIND_CHARGE);
   }

   private ProjectileParams d(ItemStack itemstack, PlayerEntity playerentity) {
      if (itemstack.isOf(Items.ENDER_PEARL)) {
         return new ProjectileParams(1.5, 0.03, 0.99);
      } else if (itemstack.isOf(Items.SNOWBALL) || itemstack.isOf(Items.EGG)) {
         return new ProjectileParams(1.5, 0.03, 0.99);
      } else if (itemstack.isOf(Items.SPLASH_POTION) || itemstack.isOf(Items.LINGERING_POTION)) {
         return new ProjectileParams(0.5, 0.05, 0.99);
      } else if (itemstack.isOf(Items.BOW)) {
         if (!playerentity.isUsingItem()) {
            return null;
         } else {
            int j = playerentity.getItemUseTime();
            float f = BowItem.getPullProgress(j);
            double d0 = f * 3.0;
            return d0 < 0.1 ? null : new ProjectileParams(d0, 0.05, 0.99);
         }
      } else if (itemstack.isOf(Items.CROSSBOW)) {
         return !CrossbowItem.isCharged(itemstack) ? null : new ProjectileParams(3.15, 0.05, 0.99);
      } else if (itemstack.isOf(Items.TRIDENT)) {
         if (!playerentity.isUsingItem()) {
            return null;
         } else {
            int i = playerentity.getItemUseTime();
            return i < 10 ? null : new ProjectileParams(2.5, 0.05, 0.99);
         }
      } else {
         return itemstack.isOf(Items.WIND_CHARGE) ? new ProjectileParams(1.5, 0.0, 1.0) : null;
      }
   }

   private void e(PlayerEntity playerentity, ProjectileParams projectileparams) {
      float f = playerentity.getYaw();
      float f1 = playerentity.getPitch();
      double d0 = Math.toRadians(f);
      double d1 = Math.toRadians(f1);
      double d2 = -MathHelper.sin((float)d0) * MathHelper.cos((float)d1);
      double d3 = -MathHelper.sin((float)d1);
      double d4 = MathHelper.cos((float)d0) * MathHelper.cos((float)d1);
      double d5 = Math.sqrt(d2 * d2 + d3 * d3 + d4 * d4);
      d2 /= d5;
      d3 /= d5;
      d4 /= d5;
      Vec3d vec3d = new Vec3d(playerentity.getX(), playerentity.getEyeY() - 0.1, playerentity.getZ());
      Vec3d vec3d1 = new Vec3d(d2 * projectileparams.speed, d3 * projectileparams.speed, d4 * projectileparams.speed);
      short short1 = 300;

      for (int i = 0; i < short1; i++) {
         this.PG.add(vec3d);
         Vec3d vec3d2 = vec3d.add(vec3d1);
         RaycastContext raycastcontext = new RaycastContext(vec3d, vec3d2, ShapeType.COLLIDER, FluidHandling.NONE, ShapeContext.absent());
         BlockHitResult blockhitresult = this.getWorld().raycast(raycastcontext);
         EntityHitResult entityhitresult = this.f(vec3d, vec3d2, playerentity);
         if (blockhitresult.getType() == Type.BLOCK || entityhitresult != null) {
            Vec3d vec3d3 = blockhitresult.getType() == Type.BLOCK ? blockhitresult.getPos() : null;
            Vec3d vec3d4 = entityhitresult != null ? entityhitresult.getPos() : null;
            if (vec3d3 != null && vec3d4 != null) {
               double d6 = vec3d.squaredDistanceTo(vec3d3);
               double d7 = vec3d.squaredDistanceTo(vec3d4);
               if (d6 < d7) {
                  this.UL = vec3d3;
                  this.ha = blockhitresult.getSide();
               } else {
                  this.UL = vec3d4;
                  this.ha = this.g(entityhitresult.getEntity(), vec3d4);
               }
            } else if (vec3d3 != null) {
               this.UL = vec3d3;
               this.ha = blockhitresult.getSide();
            } else {
               this.UL = vec3d4;
               this.ha = this.g(entityhitresult.getEntity(), vec3d4);
            }

            this.PG.add(this.UL);
            return;
         }

         vec3d1 = vec3d1.multiply(projectileparams.drag);
         vec3d1 = new Vec3d(vec3d1.x, vec3d1.y - projectileparams.gravity, vec3d1.z);
         vec3d = vec3d2;
         if (vec3d2.y < -64.0) {
            return;
         }
      }
   }

   private EntityHitResult f(Vec3d vec3d, Vec3d vec3d1, PlayerEntity playerentity) {
      vec3d1.subtract(vec3d);
      Box box = new Box(vec3d, vec3d1).expand(1.0);
      Entity entity = null;
      double d0 = Double.MAX_VALUE;
      Vec3d vec3d2 = null;

      for (Entity entity1 : this.getWorld().getOtherEntities(playerentity, box, entity2 -> {
         return !entity2.isSpectator() && entity2.canHit();
      })) {
         Box box1 = entity1.getBoundingBox().expand(0.3);
         Optional optional = box1.raycast(vec3d, vec3d1);
         if (optional.isPresent()) {
            double d1 = vec3d.squaredDistanceTo((Vec3d)optional.get());
            if (d1 < d0) {
               d0 = d1;
               entity = entity1;
               vec3d2 = (Vec3d)optional.get();
            }
         }
      }

      return entity != null ? new EntityHitResult(entity, vec3d2) : null;
   }

   private Direction g(Entity entity, Vec3d vec3d) {
      Box box = entity.getBoundingBox().expand(0.3);
      double d0 = Math.abs(vec3d.y - box.maxY);
      double d1 = Math.abs(vec3d.y - box.minY);
      double d2 = Math.abs(vec3d.z - box.minZ);
      double d3 = Math.abs(vec3d.z - box.maxZ);
      double d4 = Math.abs(vec3d.x - box.minX);
      double d5 = Math.abs(vec3d.x - box.maxX);
      double d6 = d0;
      Direction direction = Direction.UP;
      if (d1 < d0) {
         d6 = d1;
         direction = Direction.DOWN;
      }

      if (d2 < d6) {
         d6 = d2;
         direction = Direction.NORTH;
      }

      if (d3 < d6) {
         d6 = d3;
         direction = Direction.SOUTH;
      }

      if (d4 < d6) {
         d6 = d4;
         direction = Direction.WEST;
      }

      if (d5 < d6) {
         direction = Direction.EAST;
      }

      return direction;
   }

   private void h(MatrixStack matrixstack, Vec3d vec3d, Vec3d vec3d1, float f) {
      Color color = this.landingColorSetting.getColor();
      int i = (int)(color.getAlpha() * this.Ho);
      Color color1 = new Color(color.getRed(), color.getGreen(), color.getBlue(), i);
      BuiltLine3d builtline3d = this.Rl.a(f * 0.8F).e(color1).l(6).a();
      xzs xzs = builtline3d.h();
      Vec3d vec3d2 = vec3d1.subtract(vec3d);
      float f1 = 0.5F;
      byte b0 = 32;
      double d0 = vec3d2.x;
      double d1 = vec3d2.y;
      double d2 = vec3d2.z;

      for (int j = 0; j < b0; j++) {
         double d3 = (Math.PI * 2) * j / b0;
         double d4 = (Math.PI * 2) * (j + 1) / b0;
         double d5 = Math.cos(d3) * f1;
         double d6 = Math.sin(d3) * f1;
         double d7 = Math.cos(d4) * f1;
         double d8 = Math.sin(d4) * f1;
         Axis axis = this.ha.getAxis();
         Vec3d vec3d3;
         Vec3d vec3d4;
         if (axis == Axis.Y) {
            vec3d3 = new Vec3d(d0 + d5, d1, d2 + d6);
            vec3d4 = new Vec3d(d0 + d7, d1, d2 + d8);
         } else if (axis == Axis.X) {
            vec3d3 = new Vec3d(d0, d1 + d5, d2 + d6);
            vec3d4 = new Vec3d(d0, d1 + d7, d2 + d8);
         } else {
            vec3d3 = new Vec3d(d0 + d5, d1 + d6, d2);
            vec3d4 = new Vec3d(d0 + d7, d1 + d8, d2);
         }

         xzs.b(vec3d3, vec3d4);
      }

      xzs.d(matrixstack, Vec3d.ZERO);
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      super.onPlayerDeath(playerentity);
      if (this.enabledSetting.getValue() && playerentity == this.getPlayer()) {
         this.setEnabled(false);
      }
   }
}
