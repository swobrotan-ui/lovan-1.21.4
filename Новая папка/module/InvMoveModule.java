package module;

import enum.Category;
import enum.PacketDirection;
import event.PacketEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import org.lwjgl.glfw.GLFW;
import setting.BooleanSetting;

public class InvMoveModule extends Module {
   private final BooleanSetting moveInInventorySetting = new BooleanSetting("Бегать в инвентаре", "Позволяет двигаться с открытым инвентарём", true);
   private final BooleanSetting moveInGuiSetting = new BooleanSetting("Бегать в гуи", "Позволяет двигаться с открытым гуи клиента", true);
   private final Queue<ClickSlotC2SPacket> ER = new ConcurrentLinkedQueue<ClickSlotC2SPacket>();
   private boolean apU = false;
   private int axt = 0;
   private int acP = 0;
   private KeyBinding[] rV;

   public InvMoveModule() {
      super("ИнвМове", "Позволяет ходить с открытым инвентарём", Category.MOVEMENT);
      this.addSettings(this.moveInInventorySetting, this.moveInGuiSetting);
   }

   @Override
   public void onEnable() {
      this.f();
   }

   @Override
   public void onDisable() {
      this.f();
   }

   @Override
   public void onEndTick() {
      if (this.getPlayer() == null) {
         this.f();
      } else {
         if (this.axt > 0) {
            this.axt--;
            if (this.axt == 0) {
               while (!this.ER.isEmpty()) {
                  ClickSlotC2SPacket clickslotc2spacket = this.ER.poll();
                  if (clickslotc2spacket != null) {
                     PacketEvent.sendSilent(clickslotc2spacket);
                  }
               }
            }
         }

         if (this.acP > 0) {
            this.acP--;
            if (this.acP == 0) {
               this.b();
            }
         }

         if (this.getScreen() != null
            && !(this.getScreen() instanceof ChatScreen)
            && !(this.getScreen() instanceof SignEditScreen)
            && !(this.getScreen() instanceof AnvilScreen)) {
            if (!this.apU) {
               this.e();
               boolean flag1 = (this.getScreen() instanceof InventoryScreen || this.getScreen() instanceof oy)
                  && !(this.getScreen() instanceof CreativeInventoryScreen);
               boolean flag = this.getScreen() instanceof zj;
               if ((flag1 && this.moveInInventorySetting.getValue() || flag && this.moveInGuiSetting.getValue()) && this.rV != null) {
                  long i = this.getClient().getWindow().getHandle();

                  for (KeyBinding keybinding : this.rV) {
                     if (GLFW.glfwGetKey(i, keybinding.getDefaultKey().getCode()) == 1) {
                        keybinding.setPressed(true);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void onPacket(PacketEvent packetevent) {
      if (this.getPlayer() != null) {
         if (packetevent.getType() == PacketDirection.SEND && packetevent.getPacket() instanceof ClickSlotC2SPacket clickslotc2spacket && this.c() && this.d()) {
            this.ER.add(clickslotc2spacket);
            packetevent.setCancelled(true);
            this.a();
            this.axt = 2;
            this.acP = 4;
         }
      }
   }

   private void a() {
      this.apU = true;
      if (this.rV != null) {
         for (KeyBinding keybinding : this.rV) {
            keybinding.setPressed(false);
         }
      }
   }

   private void b() {
      this.apU = false;
   }

   private boolean c() {
      return (this.getScreen() instanceof InventoryScreen || this.getScreen() instanceof oy) && !(this.getScreen() instanceof CreativeInventoryScreen);
   }

   private boolean d() {
      return this.getPlayer() != null
         && this.getClientPlayer().input != null
         && (this.getClientPlayer().input.movementForward != 0.0F || this.getClientPlayer().input.movementSideways != 0.0F);
   }

   private void e() {
      if (this.rV == null && this.getOptions() != null) {
         this.rV = new KeyBinding[]{
            this.getOptions().forwardKey,
            this.getOptions().backKey,
            this.getOptions().leftKey,
            this.getOptions().rightKey,
            this.getOptions().jumpKey,
            this.getOptions().sprintKey
         };
      }
   }

   private void f() {
      while (!this.ER.isEmpty()) {
         ClickSlotC2SPacket clickslotc2spacket = this.ER.poll();
         if (clickslotc2spacket != null) {
            PacketEvent.sendSilent(clickslotc2spacket);
         }
      }

      this.apU = false;
      this.axt = 0;
      this.acP = 0;
      if (this.rV != null) {
         for (KeyBinding keybinding : this.rV) {
            keybinding.setPressed(false);
         }
      }
   }
}
