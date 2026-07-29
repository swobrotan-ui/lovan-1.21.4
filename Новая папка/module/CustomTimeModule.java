package module;

import enum.Category;
import net.minecraft.client.world.ClientWorld;
import setting.SliderSetting;
import util.UnsafeFieldAccessor;

public class CustomTimeModule extends Module {
   public final SliderSetting worldTimeSetting = new SliderSetting("Время в мире", "", 0.0, 0.0, 24000.0, 1.0);
   private UnsafeFieldAccessor<Boolean> axy;

   public CustomTimeModule() {
      super("СузтомТиме", "Меняет время в мире на ваше", Category.VISUAL);
      this.addSetting(this.worldTimeSetting);
      this.axy = new UnsafeFieldAccessor<Boolean>(this.getClient(), ClientWorld.class, 24);
   }

   @Override
   public void onEnable() {
      this.a(this.getClientWorld());
   }

   @Override
   public void onDisable() {
      this.b(this.getClientWorld(), true);
   }

   @Override
   public void onEndTick() {
      this.a(this.getClientWorld());
   }

   private void a(ClientWorld clientworld) {
      if (!this.isNotInWorld()) {
         long i = this.worldTimeSetting.getLongValue();
         clientworld.setTime(clientworld.getTime(), i, false);
         this.b(clientworld, false);
      }
   }

   private void b(ClientWorld clientworld, boolean flag) {
      if (!this.isNotInWorld()) {
         try {
            this.axy.setBoolean(clientworld, flag);
         } catch (Exception exception) {
         }
      }
   }
}
