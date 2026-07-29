package core;

import event.ChatMessageEvent;
import event.MouseMoveEvent;
import event.MovementEvent;
import event.RenderHudEvent;
import event.RotationEvent;
import event.SpeedEvent;
import event.UseItemEvent;
import java.util.List;
import module.Module;
import module.PanicModule;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class EventManager {
   private volatile ModuleManager moduleManager;

   private ModuleManager getModuleManager() {
      ModuleManager modulemanager = this.moduleManager;
      if (modulemanager == null) {
         modulemanager = ClientMain.getInstance().getModuleManager();
         this.moduleManager = modulemanager;
      }

      return modulemanager;
   }

   public void onEndTick() {
      List list = this.getModuleManager().getEnabledModules();
      if (!list.isEmpty()) {
         for (Module module : list) {
            try {
               module.onEndTick();
            } catch (Exception exception) {
            }
         }
      }
   }

   public void onStartTick() {
      List list = this.getModuleManager().getEnabledModules();
      if (!list.isEmpty()) {
         for (Module module : list) {
            try {
               module.onStartTick();
            } catch (Exception exception) {
            }
         }
      }
   }

   public void onPlayerDeath(PlayerEntity playerentity) {
      if (playerentity != null) {
         ModuleManager modulemanager = this.getModuleManager();
         modulemanager.disableAll();
         List list = modulemanager.getAllModules();
         if (!list.isEmpty()) {
            for (Module module : list) {
               try {
                  module.onPlayerDeath(playerentity);
               } catch (Exception exception) {
               }
            }
         }
      }
   }

   public void onJoinServer() {
      List list = this.getModuleManager().getAllModules();
      if (!list.isEmpty()) {
         for (Module module : list) {
            try {
               module.onServerJoin();
            } catch (Exception exception) {
            }
         }
      }
   }

   public void onDisconnect() {
      List list = this.getModuleManager().getEnabledModules();
      if (!list.isEmpty()) {
         for (Module module : list) {
            try {
               module.onServerDisconnect();
            } catch (Exception exception) {
            }
         }
      }
   }

   public void onRenderHud(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      RenderHudEvent renderhudevent = new RenderHudEvent(drawcontext, rendertickcounter);
      List list = this.getModuleManager().getEnabledModules();
      if (!list.isEmpty()) {
         for (Module module : list) {
            try {
               module.onRenderHud(renderhudevent);
            } catch (Exception exception) {
            }
         }
      }
   }

   public UseItemEvent onUseEntity(PlayerEntity playerentity, Hand hand, Entity entity, EntityHitResult entityhitresult) {
      UseItemEvent useitemevent = new UseItemEvent(playerentity, hand, entityhitresult);
      List list = this.getModuleManager().getEnabledModules();
      if (list.isEmpty()) {
         return useitemevent;
      } else {
         for (Module module : list) {
            try {
               module.onUseItem(useitemevent);
            } catch (Exception exception) {
            }
         }

         return useitemevent;
      }
   }

   public ActionResult i(PlayerEntity playerentity, World world, Hand hand, Entity entity, EntityHitResult entityhitresult) {
      List list = this.getModuleManager().getEnabledModules();
      if (list.isEmpty()) {
         return ActionResult.PASS;
      } else {
         for (Module module : list) {
            try {
               module.onAttackEntity(playerentity, world, hand, entity, entityhitresult);
            } catch (Exception exception) {
            }
         }

         return ActionResult.PASS;
      }
   }

   public void onChatSent(String s) {
      if (s != null && !s.trim().isEmpty()) {
         List list = this.getModuleManager().getEnabledModules();
         if (!list.isEmpty()) {
            for (Module module : list) {
               try {
                  module.onChatSend(s);
               } catch (Exception exception) {
               }
            }
         }
      }
   }

   public ChatMessageEvent onChatMessage(String s) {
      ChatMessageEvent chatmessageevent = new ChatMessageEvent(s);
      ModuleManager modulemanager = this.getModuleManager();
      PanicModule panicmodule = modulemanager.<PanicModule>getModule(PanicModule.class);
      List list = modulemanager.getAllModules();
      if (list.isEmpty()) {
         return chatmessageevent;
      } else if (panicmodule != null && !panicmodule.c()) {
         for (Module module : list) {
            try {
               module.onChatMessage(chatmessageevent);
               if (chatmessageevent.isCancelled()) {
                  break;
               }
            } catch (Exception exception) {
            }
         }

         return chatmessageevent;
      } else {
         chatmessageevent.cancel();
         return chatmessageevent;
      }
   }

   public void onAfterEntities(WorldRenderContext worldrendercontext) {
      if (worldrendercontext != null) {
         List list = this.getModuleManager().getEnabledModules();
         if (!list.isEmpty()) {
            for (Module module : list) {
               try {
                  module.onRenderAfterEntities(worldrendercontext);
               } catch (Exception exception) {
               }
            }
         }
      }
   }

   public void onBeforeEntities(WorldRenderContext worldrendercontext) {
      if (worldrendercontext != null) {
         List list = this.getModuleManager().getEnabledModules();
         if (!list.isEmpty()) {
            for (Module module : list) {
               try {
                  module.onRenderBeforeEntities(worldrendercontext);
               } catch (Exception exception) {
               }
            }
         }
      }
   }

   public void onSpeedEvent(SpeedEvent speedevent) {
      List list = this.getModuleManager().getEnabledModules();
      if (!list.isEmpty()) {
         for (Module module : list) {
            try {
               module.onSpeed(speedevent);
            } catch (Exception exception) {
            }
         }
      }
   }

   public void onMovementEvent(MovementEvent movementevent) {
      if (movementevent != null) {
         List list = this.getModuleManager().getEnabledModules();
         if (!list.isEmpty()) {
            for (Module module : list) {
               try {
                  module.onMovement(movementevent);
               } catch (Exception exception) {
               }
            }
         }
      }
   }

   public void onWorldRenderStart() {
      List list = this.getModuleManager().getEnabledModules();
      if (!list.isEmpty()) {
         for (Module module : list) {
            try {
               module.onRenderStart();
            } catch (Exception exception) {
            }
         }
      }
   }

   public void q(WorldRenderContext worldrendercontext) {
      if (worldrendercontext != null) {
         List list = this.getModuleManager().getEnabledModules();
         if (!list.isEmpty()) {
            for (Module module : list) {
               try {
                  module.onRenderEnd(worldrendercontext);
               } catch (Exception exception) {
               }
            }
         }
      }
   }

   public void r(WorldRenderContext worldrendercontext) {
      if (worldrendercontext != null) {
         List list = this.getModuleManager().getEnabledModules();
         if (!list.isEmpty()) {
            for (Module module : list) {
               try {
                  module.onRenderAfterSetup(worldrendercontext);
               } catch (Exception exception) {
               }
            }
         }
      }
   }

   public void s(WorldRenderContext worldrendercontext) {
      if (worldrendercontext != null) {
         List list = this.getModuleManager().getEnabledModules();
         if (!list.isEmpty()) {
            for (Module module : list) {
               try {
                  module.onRenderAfterTranslucent(worldrendercontext);
               } catch (Exception exception) {
               }
            }
         }
      }
   }

   public void t(WorldRenderContext worldrendercontext) {
      if (worldrendercontext != null) {
         List list = this.getModuleManager().getEnabledModules();
         if (!list.isEmpty()) {
            for (Module module : list) {
               try {
                  module.onRenderLate(worldrendercontext);
               } catch (Exception exception) {
               }
            }
         }
      }
   }

   public ActionResult u(PlayerEntity playerentity, World world, Hand hand, BlockHitResult blockhitresult) {
      List list = this.getModuleManager().getEnabledModules();
      if (list.isEmpty()) {
         return ActionResult.PASS;
      } else {
         for (Module module : list) {
            try {
               ActionResult actionresult = module.onUseBlock(playerentity, world, hand, blockhitresult);
               if (actionresult != ActionResult.PASS) {
                  return actionresult;
               }
            } catch (Exception exception) {
            }
         }

         return ActionResult.PASS;
      }
   }

   public ActionResult v(PlayerEntity playerentity, World world, Hand hand, Entity entity, EntityHitResult entityhitresult) {
      List list = this.getModuleManager().getEnabledModules();
      if (list.isEmpty()) {
         return ActionResult.PASS;
      } else {
         for (Module module : list) {
            try {
               ActionResult actionresult = module.onUseEntity(playerentity, world, hand, entity, entityhitresult);
               if (actionresult != ActionResult.PASS) {
                  return actionresult;
               }
            } catch (Exception exception) {
            }
         }

         return ActionResult.PASS;
      }
   }

   public RotationEvent onRotation(float f, float f1) {
      List list = this.getModuleManager().getAllModules();
      if (list.isEmpty()) {
         return new RotationEvent(f, f1, true);
      } else {
         RotationEvent rotationevent = new RotationEvent(f, f1, true);

         for (Module module : list) {
            try {
               module.onRotation(rotationevent);
            } catch (Exception exception) {
            }
         }

         return rotationevent;
      }
   }

   public void x() {
      List list = this.getModuleManager().getEnabledModules();
      if (!list.isEmpty()) {
         for (Module module : list) {
            try {
               module.onPush();
            } catch (Exception exception) {
            }
         }
      }
   }

   public void y() {
      List list = this.getModuleManager().getEnabledModules();
      if (!list.isEmpty()) {
         for (Module module : list) {
            try {
               module.onCameraUpdate();
            } catch (Exception exception) {
            }
         }
      }
   }

   public void z() {
      List list = this.getModuleManager().getEnabledModules();
      if (!list.isEmpty()) {
         for (Module module : list) {
            try {
               module.onRenderHand();
            } catch (Exception exception) {
            }
         }
      }
   }

   public MouseMoveEvent onMouseScroll(double d0, double d1) {
      MouseMoveEvent mousemoveevent = new MouseMoveEvent(d0, d1);
      List list = this.getModuleManager().getEnabledModules();
      if (list.isEmpty()) {
         return mousemoveevent;
      } else {
         for (Module module : list) {
            try {
               module.onMouseScroll(mousemoveevent);
               if (mousemoveevent.isCancelled()) {
                  break;
               }
            } catch (Exception exception) {
            }
         }

         return mousemoveevent;
      }
   }
}
