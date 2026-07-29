package enum;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
enum HeartType {
   CONTAINER(
      Identifier.ofVanilla("hud/heart/container"),
      Identifier.ofVanilla("hud/heart/container_blinking"),
      Identifier.ofVanilla("hud/heart/container"),
      Identifier.ofVanilla("hud/heart/container_blinking"),
      Identifier.ofVanilla("hud/heart/container_hardcore"),
      Identifier.ofVanilla("hud/heart/container_hardcore_blinking"),
      Identifier.ofVanilla("hud/heart/container_hardcore"),
      Identifier.ofVanilla("hud/heart/container_hardcore_blinking")
   ),
   NORMAL(
      Identifier.ofVanilla("hud/heart/full"),
      Identifier.ofVanilla("hud/heart/full_blinking"),
      Identifier.ofVanilla("hud/heart/half"),
      Identifier.ofVanilla("hud/heart/half_blinking"),
      Identifier.ofVanilla("hud/heart/hardcore_full"),
      Identifier.ofVanilla("hud/heart/hardcore_full_blinking"),
      Identifier.ofVanilla("hud/heart/hardcore_half"),
      Identifier.ofVanilla("hud/heart/hardcore_half_blinking")
   ),
   POISONED(
      Identifier.ofVanilla("hud/heart/poisoned_full"),
      Identifier.ofVanilla("hud/heart/poisoned_full_blinking"),
      Identifier.ofVanilla("hud/heart/poisoned_half"),
      Identifier.ofVanilla("hud/heart/poisoned_half_blinking"),
      Identifier.ofVanilla("hud/heart/poisoned_hardcore_full"),
      Identifier.ofVanilla("hud/heart/poisoned_hardcore_full_blinking"),
      Identifier.ofVanilla("hud/heart/poisoned_hardcore_half"),
      Identifier.ofVanilla("hud/heart/poisoned_hardcore_half_blinking")
   ),
   WITHERED(
      Identifier.ofVanilla("hud/heart/withered_full"),
      Identifier.ofVanilla("hud/heart/withered_full_blinking"),
      Identifier.ofVanilla("hud/heart/withered_half"),
      Identifier.ofVanilla("hud/heart/withered_half_blinking"),
      Identifier.ofVanilla("hud/heart/withered_hardcore_full"),
      Identifier.ofVanilla("hud/heart/withered_hardcore_full_blinking"),
      Identifier.ofVanilla("hud/heart/withered_hardcore_half"),
      Identifier.ofVanilla("hud/heart/withered_hardcore_half_blinking")
   ),
   ABSORBING(
      Identifier.ofVanilla("hud/heart/absorbing_full"),
      Identifier.ofVanilla("hud/heart/absorbing_full_blinking"),
      Identifier.ofVanilla("hud/heart/absorbing_half"),
      Identifier.ofVanilla("hud/heart/absorbing_half_blinking"),
      Identifier.ofVanilla("hud/heart/absorbing_hardcore_full"),
      Identifier.ofVanilla("hud/heart/absorbing_hardcore_full_blinking"),
      Identifier.ofVanilla("hud/heart/absorbing_hardcore_half"),
      Identifier.ofVanilla("hud/heart/absorbing_hardcore_half_blinking")
   ),
   FROZEN(
      Identifier.ofVanilla("hud/heart/frozen_full"),
      Identifier.ofVanilla("hud/heart/frozen_full_blinking"),
      Identifier.ofVanilla("hud/heart/frozen_half"),
      Identifier.ofVanilla("hud/heart/frozen_half_blinking"),
      Identifier.ofVanilla("hud/heart/frozen_hardcore_full"),
      Identifier.ofVanilla("hud/heart/frozen_hardcore_full_blinking"),
      Identifier.ofVanilla("hud/heart/frozen_hardcore_half"),
      Identifier.ofVanilla("hud/heart/frozen_hardcore_half_blinking")
   );

   private final Identifier fullTexture;
   private final Identifier fullBlinkingTexture;
   private final Identifier halfTexture;
   private final Identifier halfBlinkingTexture;
   private final Identifier hardcoreFullTexture;
   private final Identifier hardcoreFullBlinkingTexture;
   private final Identifier hardcoreHalfTexture;
   private final Identifier hardcoreHalfBlinkingTexture;

   private HeartType(
      Identifier identifier,
      Identifier identifier1,
      Identifier identifier2,
      Identifier identifier3,
      Identifier identifier4,
      Identifier identifier5,
      Identifier identifier6,
      Identifier identifier7
   ) {
      this.fullTexture = identifier;
      this.fullBlinkingTexture = identifier1;
      this.halfTexture = identifier2;
      this.halfBlinkingTexture = identifier3;
      this.hardcoreFullTexture = identifier4;
      this.hardcoreFullBlinkingTexture = identifier5;
      this.hardcoreHalfTexture = identifier6;
      this.hardcoreHalfBlinkingTexture = identifier7;
   }

   public Identifier getTexture(boolean flag, boolean flag1, boolean flag2) {
      if (!flag) {
         if (flag1) {
            return flag2 ? this.halfBlinkingTexture : this.halfTexture;
         } else {
            return flag2 ? this.fullBlinkingTexture : this.fullTexture;
         }
      } else if (flag1) {
         return flag2 ? this.hardcoreHalfBlinkingTexture : this.hardcoreHalfTexture;
      } else {
         return flag2 ? this.hardcoreFullBlinkingTexture : this.hardcoreFullTexture;
      }
   }

   static HeartType fromPlayerState(PlayerEntity playerentity) {
      HeartType hearttype;
      if (playerentity.hasStatusEffect(StatusEffects.POISON)) {
         hearttype = POISONED;
      } else if (playerentity.hasStatusEffect(StatusEffects.WITHER)) {
         hearttype = WITHERED;
      } else if (playerentity.isFrozen()) {
         hearttype = FROZEN;
      } else {
         hearttype = NORMAL;
      }

      return hearttype;
   }

   // $VF: synthetic method
   private static HeartType[] $values() {
      return new HeartType[]{CONTAINER, NORMAL, POISONED, WITHERED, ABSORBING, FROZEN};
   }
}
