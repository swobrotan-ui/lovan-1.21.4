package module;

import enum.Category;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class NoArmorStandModule extends Module {
   public NoArmorStandModule() {
      super("НоАрморЗтанд", "Модуль для отключения взаимодействия со стойками", Category.PLAYER);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   @Override
   public void onRenderBeforeEntities(WorldRenderContext worldrendercontext) {
      for (Entity entity : worldrendercontext.world().getEntities()) {
         if (entity instanceof ArmorStandEntity) {
            entity.setBoundingBox(
               new Box(
                  entity.getX() - 0.25,
                  entity.getBoundingBox().minY,
                  entity.getZ() - 0.25,
                  entity.getX() + 0.25,
                  entity.getBoundingBox().maxY,
                  entity.getZ() + 0.25
               )
            );
         }
      }
   }

   @Override
   public void onRenderAfterEntities(WorldRenderContext worldrendercontext) {
      for (Entity entity : worldrendercontext.world().getEntities()) {
         if (entity instanceof ArmorStandEntity) {
            entity.setBoundingBox(
               new Box(
                  entity.getX() - 0.0,
                  entity.getBoundingBox().minY,
                  entity.getZ() - 0.0,
                  entity.getX() + 0.0,
                  entity.getBoundingBox().maxY,
                  entity.getZ() + 0.0
               )
            );
         }
      }
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      super.onPlayerDeath(playerentity);
      if (this.enabledSetting.getValue()) {
         this.setEnabled(false);
      }
   }
}
