package module;

import enum.Category;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import setting.BooleanSetting;
import setting.SliderSetting;

public class ShieldBreakerModule extends Module implements StartTick {
   private BooleanSetting onlyPlayersSetting = new BooleanSetting("Только игроки", "Реагировать только на игроков, а не на мобов", true);
   private SliderSetting holdTimeSetting = new SliderSetting("Время удержания", "Время удержания топора после атаки (в мс)", 200.0, 200.0, 1000.0, 10.0);
   private SliderSetting detectionRangeSetting = new SliderSetting("Дальность обнаружения", "Максимальное расстояние для обнаружения щита", 3.4, 2.0, 6.0, 0.1);
   private LivingEntity auA;
   private int fa = -1;
   private boolean auS = false;
   private long akl;
   private boolean MY = false;

   public ShieldBreakerModule() {
      super("ЗхиелдБреакер", "Автоматически ломает щит противника", Category.COMBAT);
      this.addSettings(this.onlyPlayersSetting, this.holdTimeSetting, this.detectionRangeSetting);
      this.U("Автоматический возврат оружия", "Время удержания топора", true);
   }

   @Override
   public void onEnable() {
      ClientTickEvents.START_CLIENT_TICK.register(this);
      this.resetState();
   }

   @Override
   public void onDisable() {
      this.resetState();
   }

   public void onStartTick(MinecraftClient minecraftclient) {
      if (this.isEnabled() && this.isValidEnvironment()) {
         this.detectAttackAndSwap();
         this.handleAxeState();
      } else {
         this.resetState();
      }
   }

   private boolean isValidEnvironment() {
      return this.hasPlayerAndWorld();
   }

   private void detectAttackAndSwap() {
      boolean flag = this.getOptions().attackKey.isPressed();
      if (flag && !this.MY) {
         LivingEntity livingentity = this.findNearestBlockingTarget();
         if (livingentity != null && this.isTargetBlockingWithShield(livingentity)) {
            this.performAxeSwap(livingentity);
         }
      }

      this.MY = flag;
   }

   private LivingEntity findNearestBlockingTarget() {
      ClientPlayerEntity clientplayerentity = this.getClientPlayer();
      return clientplayerentity.getWorld()
         .getEntitiesByClass(LivingEntity.class, clientplayerentity.getBoundingBox().expand(this.detectionRangeSetting.getValue()), this::isValidTarget)
         .stream()
         .filter(livingentity -> {
            return clientplayerentity.distanceTo(livingentity) <= this.detectionRangeSetting.getValue();
         })
         .filter(this::isTargetBlockingWithShield)
         .min((livingentity, livingentity1) -> {
            return Float.compare(clientplayerentity.distanceTo(livingentity), clientplayerentity.distanceTo(livingentity1));
         })
         .orElse(null);
   }

   private boolean isValidTarget(LivingEntity livingentity) {
      return livingentity == this.getClientPlayer() || !livingentity.canHit() || livingentity.isInvisible()
         ? false
         : !this.onlyPlayersSetting.getValue() || livingentity instanceof PlayerEntity;
   }

   private boolean isTargetBlockingWithShield(LivingEntity livingentity) {
      return !livingentity.isBlocking() ? false : livingentity.getActiveItem().getItem() == Items.SHIELD;
   }

   private void performAxeSwap(LivingEntity livingentity) {
      if (this.fa == -1) {
         this.fa = this.getInventory().selectedSlot;
      }

      int i = this.findAxe();
      if (i != -1) {
         this.getInventory().selectedSlot = i;
         this.auS = true;
         this.akl = System.currentTimeMillis();
         this.auA = livingentity;
      }
   }

   private int findAxe() {
      if (this.getClientPlayer() == null) {
         return -1;
      } else {
         for (int i = 0; i < 9; i++) {
            ItemStack itemstack = this.getClientPlayer().getInventory().getStack(i);
            if (!itemstack.isEmpty() && itemstack.getItem() instanceof AxeItem) {
               return i;
            }
         }

         return -1;
      }
   }

   private void handleAxeState() {
      if (this.auS && this.fa != -1) {
         long i = System.currentTimeMillis() - this.akl;
         boolean flag = i >= this.holdTimeSetting.getValue();
         if ((this.auA == null || !this.isTargetBlockingWithShield(this.auA)) && i >= 100L) {
            flag = true;
         }

         if (flag) {
            this.returnToOriginalWeapon();
         }
      }
   }

   private void returnToOriginalWeapon() {
      if (this.fa != -1 && this.fa < 9) {
         this.getInventory().selectedSlot = this.fa;
      }

      this.fa = -1;
      this.auS = false;
      this.akl = 0L;
      this.auA = null;
   }

   private void resetState() {
      if (this.fa != -1) {
         this.returnToOriginalWeapon();
      }

      this.auA = null;
      this.MY = false;
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      super.onPlayerDeath(playerentity);
      if (this.enabledSetting.getValue()) {
         this.setEnabled(false);
      }

      this.resetState();
   }
}
