package event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;

public class UseItemEvent {
   private final PlayerEntity player;
   private final Hand hand;
   private final HitResult target;
   private boolean cancelled;

   public UseItemEvent(PlayerEntity playerentity, Hand hand, HitResult hitresult) {
      this.player = playerentity;
      this.hand = handx;
      this.target = hitresult;
      this.cancelled = false;
   }

   public PlayerEntity getPlayer() {
      return this.player;
   }

   public Hand getHand() {
      return this.hand;
   }

   public HitResult getTarget() {
      return this.target;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean flag) {
      this.cancelled = flag;
   }
}
