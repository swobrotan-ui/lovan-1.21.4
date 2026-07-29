package module;

import com.google.common.collect.Lists;
import enum.Category;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.player.PlayerEntity;
import setting.BooleanSetting;

public class AntiBotModule extends Module {
   public static final CopyOnWriteArrayList<PlayerEntity> ait = Lists.newCopyOnWriteArrayList();
   public final BooleanSetting removeFromWorldSetting = new BooleanSetting("Удалять из мира", "Полностью скрывает обнаруженных ботов.", false);

   public AntiBotModule() {
      super("АнтиБот", "Игнорирует ботов от анти-читов", Category.COMBAT);
   }

   @Override
   public void onEndTick() {
      ClientPlayNetworkHandler clientplaynetworkhandler = this.getNetworkHandler();
      if (clientplaynetworkhandler != null) {
         ait.removeIf(playerentity5 -> {
            return !this.getWorld().getPlayers().contains(playerentity5);
         });
         HashSet hashset = new HashSet();

         for (PlayerListEntry playerlistentry : clientplaynetworkhandler.getPlayerList()) {
            hashset.add(playerlistentry.getProfile().getId());
         }

         HashMap hashmap = new HashMap();

         for (PlayerEntity playerentity : this.getWorld().getPlayers()) {
            if (playerentity != this.getPlayer()) {
               hashmap.computeIfAbsent(playerentity.getName().getString(), s -> {
                  return new ArrayList<PlayerEntity>();
               }).add(playerentity);
            }
         }

         for (PlayerEntity playerentity2 : this.getWorld().getPlayers()) {
            if (playerentity2 != this.getPlayer() && !ait.contains(playerentity2)) {
               boolean flag = false;
               if (!hashset.contains(playerentity2.getUuid())) {
                  flag = true;
               }

               if (!flag) {
                  List list = (List)hashmap.get(playerentity2.getName().getString());
                  if (list != null && list.size() > 1) {
                     PlayerEntity playerentity1 = list.stream().min(Comparator.comparingInt(Entity::getId)).orElse(null);
                     if (playerentity2 != playerentity1) {
                        flag = true;
                     }
                  }
               }

               if (!flag) {
                  for (PlayerEntity playerentity4 : this.getWorld().getPlayers()) {
                     if (playerentity4 != playerentity2
                        && playerentity4 != this.getPlayer()
                        && playerentity4.getUuid().equals(playerentity2.getUuid())
                        && !ait.contains(playerentity4)) {
                        if (playerentity2.getId() > playerentity4.getId()) {
                           flag = true;
                        }
                        break;
                     }
                  }
               }

               if (flag) {
                  ait.add(playerentity2);
               }
            }
         }

         if (this.removeFromWorldSetting.getValue()) {
            for (PlayerEntity playerentity3 : ait) {
               this.getClientWorld().removeEntity(playerentity3.getId(), RemovalReason.DISCARDED);
            }
         }
      }
   }

   public static boolean a(LivingEntity livingentity) {
      return livingentity instanceof PlayerEntity ? ait.contains(livingentity) : false;
   }

   @Override
   public void onEnable() {
      ait.clear();
   }

   @Override
   public void onDisable() {
      ait.clear();
   }
}
