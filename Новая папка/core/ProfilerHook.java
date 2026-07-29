package core;

import event.MovementEvent;
import event.RotationEvent;
import event.SpeedEvent;
import event.UseItemEvent;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ReadableProfiler;
import net.minecraft.util.profiler.SampleType;
import net.minecraft.util.profiler.ProfilerSystem.LocatedInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class ProfilerHook implements ReadableProfiler {
   public static final ProfilerHook INSTANCE = new ProfilerHook();
   private RotationEvent rotationData = null;
   private Vec2f cameraPos = new Vec2f(0.0F, 0.0F);
   private MinecraftClient mc = MinecraftClient.getInstance();
   private float savedPlayerYaw = 0.0F;
   private float savedPlayerPitch = 0.0F;
   private boolean shouldBlockBoatRotation = false;

   public void setBlockBoatRotation(boolean flag) {
      this.shouldBlockBoatRotation = flag;
   }

   public void startTick() {
   }

   public void endTick() {
   }

   public void push(String s) {
      ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
      boolean flag = clientplayerentity != null;
      EventManager eventmanager = ClientMain.getInstance().getEventManager();
      if (s.equals("newAi") && flag) {
         if (clientplayerentity.isUsingItem() && !clientplayerentity.hasVehicle()) {
            SpeedEvent speedevent = new SpeedEvent();
            eventmanager.onSpeedEvent(speedevent);
            if (speedevent.isCancelled() && clientplayerentity instanceof ClientPlayerEntity clientplayerentity1) {
               float f = speedevent.getSpeedMultiplier();
               float f1 = f / 0.2F;
               clientplayerentity1.input.movementSideways *= f1;
               clientplayerentity1.input.movementForward *= f1;
            }
         }

         if (clientplayerentity.isSwimming()) {
            MovementEvent movementevent = new MovementEvent(Vec3d.ZERO);
            eventmanager.onMovementEvent(movementevent);
            double d0 = movementevent.getMutableVector().y * 0.01;
            clientplayerentity.addVelocity(0.0, d0, 0.0);
         }
      }

      if (s.equals("entities")
         && flag
         && this.shouldBlockBoatRotation
         && clientplayerentity.hasVehicle()
         && clientplayerentity.getVehicle() instanceof BoatEntity) {
         this.savedPlayerYaw = clientplayerentity.getYaw();
         this.savedPlayerPitch = clientplayerentity.getPitch();
      }

      if (s.equals("entities") && flag) {
         this.rotationData = eventmanager.onRotation(clientplayerentity.getPitch(), clientplayerentity.getYaw());
         if (!clientplayerentity.hasVehicle()) {
            clientplayerentity.setPitch(this.rotationData.getPitch());
            clientplayerentity.setYaw(this.rotationData.getYaw());
         }
      }

      if (s.equals("blockEntities") && flag && this.rotationData != null && !clientplayerentity.hasVehicle()) {
         clientplayerentity.setPitch(this.rotationData.getStaticPitch());
         clientplayerentity.setYaw(this.rotationData.getStaticYaw());
      }

      if (this.rotationData != null && flag && !clientplayerentity.hasVehicle()) {
         boolean flag1 = this.rotationData.isStrictMoveCorrection() ? s.equals("ai") : s.equals("entityBaseTick");
         if (flag1) {
            clientplayerentity.setPitch(this.rotationData.getStaticPitch());
            clientplayerentity.setYaw(this.rotationData.getStaticYaw());
         }
      }

      if (this.rotationData != null && flag && !clientplayerentity.hasVehicle()) {
         boolean flag2 = this.rotationData.isStrictMoveCorrection() ? s.equals("jump") : s.equals("push");
         if (flag2) {
            clientplayerentity.setPitch(this.rotationData.getPitch());
            clientplayerentity.setYaw(this.rotationData.getYaw());
         }
      }

      if (s.equals("push") && flag) {
         eventmanager.x();
      }

      if (s.equals("camera") && flag) {
         eventmanager.y();
      }

      if (s.equals("sound")) {
         hook.ProfilerHook.INSTANCE.setQueue();
      }
   }

   public void push(Supplier<String> supplier) {
   }

   public void pop() {
   }

   public void swap(String s) {
      EventManager eventmanager = ClientMain.getInstance().getEventManager();
      if (eventmanager != null) {
         if (s.equals("hand")) {
            eventmanager.z();
         }

         if (s.equals("Keybindings")) {
            if (this.mc.player != null && this.mc.crosshairTarget instanceof EntityHitResult entityhitresult) {
               if (this.mc.options.attackKey.wasPressed()) {
                  UseItemEvent useitemevent = eventmanager.onUseEntity(this.mc.player, Hand.MAIN_HAND, entityhitresult.getEntity(), entityhitresult);
                  if (!useitemevent.isCancelled() && this.mc.interactionManager != null) {
                     this.mc.interactionManager.attackEntity(this.mc.player, entityhitresult.getEntity());
                     this.mc.player.swingHand(Hand.MAIN_HAND);
                  }
               }
            } else if (this.mc.options.attackKey.wasPressed() && this.mc.crosshairTarget != null) {
               KeyBinding.onKeyPressed(this.mc.options.attackKey.getDefaultKey());
            }

            while (this.mc.options.chatKey.wasPressed()) {
               this.mc.setScreen(new si(""));
            }

            if (this.mc.currentScreen == null && this.mc.getOverlay() == null && this.mc.options.commandKey.wasPressed()) {
               this.mc.setScreen(new si("/"));
            }

            while (this.mc.options.inventoryKey.wasPressed()) {
               if (this.mc.interactionManager.hasRidingInventory()) {
                  this.mc.player.openRidingInventory();
               } else {
                  this.mc.setScreen(new oy(this.mc.player));
               }
            }
         }

         if (s.equals("entities")) {
            ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
            if (clientplayerentity != null) {
               if (this.shouldBlockBoatRotation && clientplayerentity.hasVehicle() && clientplayerentity.getVehicle() instanceof BoatEntity) {
                  clientplayerentity.setYaw(this.savedPlayerYaw);
                  clientplayerentity.setPitch(this.savedPlayerPitch);
                  clientplayerentity.prevYaw = this.savedPlayerYaw;
                  clientplayerentity.prevPitch = this.savedPlayerPitch;
               }

               this.cameraPos = new Vec2f(clientplayerentity.getPitch(), clientplayerentity.getYaw());
               if (this.rotationData != null && this.rotationData.isModified() && !clientplayerentity.hasVehicle()) {
                  clientplayerentity.setPitch(this.rotationData.getPitch());
                  clientplayerentity.setYaw(this.rotationData.getYaw());
                  clientplayerentity.setHeadYaw(this.rotationData.getYaw());
                  clientplayerentity.updatePrevAngles();
               }
            }
         }

         if (s.equals("blockentities")) {
            ClientPlayerEntity clientplayerentity1 = MinecraftClient.getInstance().player;
            if (clientplayerentity1 != null && !clientplayerentity1.hasVehicle()) {
               clientplayerentity1.setPitch(this.cameraPos.x);
               clientplayerentity1.setYaw(this.cameraPos.y);
            }
         }
      }
   }

   public void swap(Supplier<String> supplier) {
   }

   public void markSampleType(SampleType sampletype) {
   }

   public void visit(String s, int i) {
   }

   public void visit(Supplier<String> supplier, int i) {
   }

   public ProfileResult getResult() {
      return null;
   }

   @Nullable
   public LocatedInfo getInfo(String s) {
      return null;
   }

   public Set<Pair<String, SampleType>> getSampleTargets() {
      return Set.<Pair<String, SampleType>>of();
   }
}
