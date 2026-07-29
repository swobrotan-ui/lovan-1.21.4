package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import module.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import setting.ActionKeySetting;
import setting.GroupSetting;
import setting.KeyBindSetting;
import setting.Setting;

public class KeyBindManager {
   private static final Map<Integer, Boolean> keyStates = new HashMap<Integer, Boolean>();
   private static KeyBindManager instance;
   private final List<KeyBindSetting> keyBinds = new ArrayList<KeyBindSetting>();
   private final List<ActionKeySetting> actionKeys = new ArrayList<ActionKeySetting>();

   public static KeyBindManager getInstance() {
      if (instance == null) {
         instance = new KeyBindManager();
      }

      return instance;
   }

   public void registerKeyBind(KeyBindSetting keybindsetting) {
      if (!this.keyBinds.contains(keybindsetting)) {
         this.keyBinds.add(keybindsetting);
      }
   }

   public void unregisterKeyBind(KeyBindSetting keybindsetting) {
      this.keyBinds.remove(keybindsetting);
   }

   public void d(ActionKeySetting actionkeysetting) {
      if (!this.actionKeys.contains(actionkeysetting)) {
         this.actionKeys.add(actionkeysetting);
      }
   }

   public void e(ActionKeySetting actionkeysetting) {
      this.actionKeys.remove(actionkeysetting);
   }

   public void clearKeyStates() {
      try {
         keyStates.clear();
      } catch (Exception exception) {
         System.err.println(exception.getMessage());
      }
   }

   public void registerTickListener() {
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)minecraftclient -> {
         if (minecraftclient.player != null && minecraftclient.currentScreen == null) {
            long i = minecraftclient.getWindow().getHandle();
            this.h(i);
         }
      });
   }

   private void h(long i) {
      if (MinecraftClient.getInstance().currentScreen == null) {
         ArrayList arraylist = new ArrayList<KeyBindSetting>(this.keyBinds);
         ArrayList arraylist1 = new ArrayList<ActionKeySetting>(this.actionKeys);

         for (Module module : ClientMain.getInstance().getModuleManager().getAllModules()) {
            this.k(module, module.getSettings(), arraylist, arraylist1);
         }

         HashSet hashset = new HashSet();
         HashMap hashmap = new HashMap();

         for (KeyBindSetting keybindsetting : arraylist) {
            int j = keybindsetting.getKeyCode();
            if (j != -1 && j != -1) {
               hashmap.computeIfAbsent(j, integer -> {
                  return new ArrayList<KeyBindSetting>();
               }).add(keybindsetting);
               hashset.add(j);
            }
         }

         HashMap hashmap1 = new HashMap();

         for (ActionKeySetting actionkeysetting1 : arraylist1) {
            int k = actionkeysetting1.getKeyCode();
            if (k != -1 && k != -1) {
               hashmap1.computeIfAbsent(k, integer -> {
                  return new ArrayList<ActionKeySetting>();
               }).add(actionkeysetting1);
               hashset.add(k);
            }
         }

         for (int l : hashset) {
            try {
               boolean flag1 = this.j(i, l);
               Boolean obool = keyStates.get(l);
               boolean flag = obool != null && obool;
               if (flag1 && !flag) {
                  List list = (List)hashmap.get(l);
                  if (list != null) {
                     for (KeyBindSetting keybindsetting1 : list) {
                        if (this.i(i, keybindsetting1.getModifierFlags())) {
                           keybindsetting1.invoke();
                        }
                     }
                  }

                  List list1 = (List)hashmap1.get(l);
                  if (list1 != null) {
                     for (ActionKeySetting actionkeysetting : list1) {
                        if (this.i(i, actionkeysetting.getModifierFlags())) {
                           actionkeysetting.invoke();
                        }
                     }
                  }
               }

               keyStates.put(l, flag1);
            } catch (Exception exception) {
            }
         }
      }
   }

   private boolean i(long i, int j) {
      if (j == 0) {
         return true;
      } else {
         boolean flag = (j & 2) != 0;
         boolean flag1 = (j & 1) != 0;
         boolean flag2 = (j & 4) != 0;
         boolean flag3 = GLFW.glfwGetKey(i, 341) == 1 || GLFW.glfwGetKey(i, 345) == 1;
         boolean flag4 = GLFW.glfwGetKey(i, 340) == 1 || GLFW.glfwGetKey(i, 344) == 1;
         boolean flag5 = GLFW.glfwGetKey(i, 342) == 1 || GLFW.glfwGetKey(i, 346) == 1;
         return flag == flag3 && flag1 == flag4 && flag2 == flag5;
      }
   }

   private boolean j(long i, int j) {
      return j >= 0 && j <= 7 ? GLFW.glfwGetMouseButton(i, j) == 1 : GLFW.glfwGetKey(i, j) == 1;
   }

   private void k(Module module, List<Setting> list, List<KeyBindSetting> list1, List<ActionKeySetting> list2) {
      for (Setting setting : list) {
         if (setting instanceof KeyBindSetting keybindsetting) {
            list1.add(keybindsetting);
         } else if (setting instanceof ActionKeySetting actionkeysetting) {
            if (module.isEnabled()) {
               list2.add(actionkeysetting);
            }
         } else if (setting instanceof GroupSetting groupsetting) {
            this.k(module, groupsetting.getSettings(), list1, list2);
         }
      }
   }

   public void l() {
      keyStates.clear();
   }
}
