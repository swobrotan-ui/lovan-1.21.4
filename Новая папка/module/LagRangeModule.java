package module;

import enum.Category;
import net.minecraft.entity.player.PlayerEntity;
import setting.SliderSetting;

public class LagRangeModule extends PacketModule {
   private final SliderSetting distanceSetting = new SliderSetting("Дистанция", "Дистанция до ближайшего игрока для активации", 10.0, 1.0, 30.0, 0.5, "м", 1);
   private boolean GS = false;
   private long aaZ = 0L;
   private static final long oH = 150L;

   public LagRangeModule() {
      super("ЛагРанге", "Задерживает отправку пакетов рядом с игроками", Category.MOVEMENT);
      this.addSettings(this.eu, this.distanceSetting, this.showServerPosSetting);
   }

   @Override
   protected boolean shouldDelay() {
      if (this.isNotInWorld()) {
         return false;
      } else {
         double d0 = this.distanceSetting.getValue() * this.distanceSetting.getValue();
         boolean flag = false;

         for (PlayerEntity playerentity : this.getClientWorld().getPlayers()) {
            if (playerentity != this.getPlayer() && this.getPlayer().squaredDistanceTo(playerentity) <= d0) {
               flag = true;
               break;
            }
         }

         long i = System.currentTimeMillis();
         if (flag != this.GS) {
            if (this.aaZ == 0L) {
               this.aaZ = i;
            }

            if (i - this.aaZ >= 150L) {
               this.GS = flag;
               this.aaZ = 0L;
               if (!flag) {
                  return false;
               }
            }

            return this.GS;
         } else {
            this.aaZ = 0L;
            return flag;
         }
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.GS = false;
      this.aaZ = 0L;
   }
}
