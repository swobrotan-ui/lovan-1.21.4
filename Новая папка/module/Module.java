package module;

import config.Config;
import core.ClientMain;
import core.ConfigManager;
import core.Localization;
import core.SoundManager;
import enum.Category;
import enum.SoundType;
import event.ChatMessageEvent;
import event.MouseMoveEvent;
import event.MovementEvent;
import event.PacketEvent;
import event.RenderHudEvent;
import event.RotationEvent;
import event.SpeedEvent;
import event.UseItemEvent;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import setting.BooleanSetting;
import setting.GroupSetting;
import setting.KeyBindSetting;
import setting.ListSetting;
import setting.RangeSetting;
import setting.Setting;
import setting.SliderSetting;
import setting.TextSetting;

public abstract class Module extends ModuleBase {
   private final String name;
   private final String description;
   private final Category category;
   private volatile boolean enabled = false;
   private final List<Setting> settings = new ArrayList<Setting>();
   private final ReentrantReadWriteLock settingsLock = new ReentrantReadWriteLock();
   private final Map<String, List<String>> visibilityDependencies = new ConcurrentHashMap<String, List<String>>();
   private final Map<String, Map<String, Boolean>> visibilityConditions = new ConcurrentHashMap<String, Map<String, Boolean>>();
   protected static final MinecraftClient mc = MinecraftClient.getInstance();
   protected BooleanSetting enabledSetting;
   protected KeyBindSetting keyBindSetting;
   protected GroupSetting groupSetting;

   public Module(String s, String s1, Category category) {
      this.name = s;
      this.description = s1;
      this.category = categoryx;
      if (this.keyBindSetting == null) {
         this.keyBindSetting = new KeyBindSetting("Клавиша включения", "", -1, this::toggle);
         this.addSetting(this.keyBindSetting);
      }
   }

   public String getDisplayName() {
      return Localization.a().c(this.name);
   }

   public String getDisplayDescription() {
      return Localization.a().c(this.description);
   }

   public void toggle() {
      this.setEnabled(!this.enabled);
   }

   public synchronized void setEnabled(boolean flag) {
      this.setEnabledWithRotation(flag, null);
   }

   public synchronized void setEnabledWithRotation(boolean flag, RotationEvent rotationevent) {
      if (this.enabled != flag) {
         boolean flag1 = this.enabled;
         this.enabled = flag;

         try {
            if (flag) {
               this.onEnable();
               SoundManager.getInstance().c(SoundType.MODULE_ENABLE);
            } else {
               this.onDisable();
               SoundManager.getInstance().c(SoundType.MODULE_DISABLE);
            }
         } catch (Exception exception) {
            PrintStream printstream = System.err;
            String s2 = this.name;
            String s = exception.getMessage();
            String s1 = s2;
            printstream.println(s1 + s);
            this.enabled = flag1;
         }
      }
   }

   public abstract void onEnable();

   public abstract void onDisable();

   public void onEndTick() {
   }

   public void onStartTick() {
   }

   public void onRenderStart() {
   }

   public void onPlayerDeath(PlayerEntity playerentity) {
   }

   public void onServerJoin() {
   }

   public void onServerDisconnect() {
   }

   public void onUseItem(UseItemEvent useitemevent) {
   }

   public void onAttackEntity(PlayerEntity playerentity, World world, Hand hand, Entity entity, EntityHitResult entityhitresult) {
   }

   public void onChatSend(String s) {
   }

   public void onChatMessage(ChatMessageEvent chatmessageevent) {
   }

   public void onMouseScroll(MouseMoveEvent mousemoveevent) {
   }

   public void onRenderAfterEntities(WorldRenderContext worldrendercontext) {
   }

   public void onRenderBeforeEntities(WorldRenderContext worldrendercontext) {
   }

   public void onRenderEnd(WorldRenderContext worldrendercontext) {
   }

   public void onRenderAfterSetup(WorldRenderContext worldrendercontext) {
   }

   public void onRenderAfterTranslucent(WorldRenderContext worldrendercontext) {
   }

   public void onRenderLate(WorldRenderContext worldrendercontext) {
   }

   public void onRotation(RotationEvent rotationevent) {
   }

   public void onRenderHud(RenderHudEvent renderhudevent) {
   }

   public void onPush() {
   }

   public void onCameraUpdate() {
   }

   public void onRenderHand() {
   }

   public void onSpeed(SpeedEvent speedevent) {
   }

   public void onMovement(MovementEvent movementevent) {
   }

   public void onPacket(PacketEvent packetevent) {
   }

   public ActionResult onUseBlock(PlayerEntity playerentity, World world, Hand hand, BlockHitResult blockhitresult) {
      return ActionResult.PASS;
   }

   public ActionResult onUseEntity(PlayerEntity playerentity, World world, Hand hand, Entity entity, EntityHitResult entityhitresult) {
      return ActionResult.PASS;
   }

   public void addSetting(Setting setting) {
      if (setting != null) {
         this.settingsLock.writeLock().lock();

         try {
            if (this.settings.stream().noneMatch(setting2 -> {
               return setting2.getName().equals(setting.getName());
            })) {
               this.settings.add(setting);
               this.R(setting);
            }
         } finally {
            this.settingsLock.writeLock().unlock();
         }
      }
   }

   public void addSettings(Setting... asetting) {
      if (asetting != null && asetting.length != 0) {
         this.settingsLock.writeLock().lock();

         try {
            for (Setting setting : asetting) {
               if (setting != null && this.settings.stream().noneMatch(setting2 -> {
                  return setting2.getName().equals(setting.getName());
               })) {
                  this.settings.add(setting);
                  this.R(setting);
               }
            }
         } finally {
            this.settingsLock.writeLock().unlock();
         }
      }
   }

   private void R(Setting setting) {
      setting.setOnChange(() -> {
         this.W(setting.getName());
         this.onSettingChanged(setting);

         try {
            ConfigManager configmanager = ClientMain.getInstance().getConfigManager();
            Config config = configmanager.x();
            if (!ClientMain.getInstance().getConfigSyncManager().k()) {
               ClientMain.getInstance().getConfigSyncManager().d(config);
            }
         } catch (Exception exception) {
         }
      });
      if (setting instanceof ListSetting listsetting) {
         listsetting.setOnSelectionChange(() -> {
            this.W(setting.getName());
         });
      }

      if (setting instanceof GroupSetting groupsetting) {
         for (Setting setting1 : groupsetting.getSettings()) {
            this.R(setting1);
         }
      }
   }

   protected void onSettingChanged(Setting setting) {
   }

   protected void T(String s, String s1, String s2, boolean flag) {
      this.visibilityDependencies.computeIfAbsent(s, s3 -> {
         return new ArrayList<String>();
      }).add(s1);
      this.visibilityConditions.computeIfAbsent(s, s3 -> {
         return new HashMap<String, Boolean>();
      }).put(s2, flag);
      this.W(s);
   }

   protected void U(String s, String s1, boolean flag) {
      this.T(s, s1, String.valueOf(flag), true);
   }

   protected void V(String s, String s1, String s2) {
      this.T(s, s1, s2, true);
   }

   private void W(String s) {
      List list = this.visibilityDependencies.get(s);
      if (list != null && !list.isEmpty()) {
         Setting setting = this.getSettingByName(s);
         if (setting != null) {
            String s1 = this.X(setting);
            Map map = this.visibilityConditions.get(s);
            if (map != null) {
               boolean flag = map.getOrDefault(s1, false);

               for (String s2 : list) {
                  Setting setting1 = this.getSettingByName(s2);
                  if (setting1 != null) {
                     setting1.setVisible(flag);
                  }
               }
            }
         }
      }
   }

   private String X(Setting setting) {
      if (setting instanceof BooleanSetting booleansetting) {
         return String.valueOf(booleansetting.getValue());
      } else if (setting instanceof ListSetting listsetting) {
         return listsetting.getSelectedValues().isEmpty() ? "none" : listsetting.getSelectedValues().getFirst();
      } else if (setting instanceof RangeSetting rangesetting) {
         double d0 = rangesetting.getValueHigh();
         double d1 = rangesetting.getValueLow();
         return d1 + "-" + d0;
      } else if (setting instanceof SliderSetting slidersetting) {
         return String.valueOf(slidersetting.getValue());
      } else {
         return setting instanceof TextSetting textsetting ? textsetting.getValue() : "";
      }
   }

   public Setting getSettingByName(String s) {
      if (s != null && !s.trim().isEmpty()) {
         this.settingsLock.readLock().lock();

         try {
            for (Setting setting : this.settings) {
               if (setting.getName().equals(s)) {
                  return setting;
               }
            }

            for (Setting setting2 : this.settings) {
               if (setting2 instanceof GroupSetting groupsetting) {
                  Setting setting1 = groupsetting.getSetting(s);
                  if (setting1 != null) {
                     return setting1;
                  }
               }
            }

            return null;
         } finally {
            this.settingsLock.readLock().unlock();
         }
      } else {
         return null;
      }
   }

   public List<Setting> getVisibleSettings() {
      this.settingsLock.readLock().lock();

      ArrayList arraylist1;
      try {
         ArrayList arraylist = new ArrayList();

         for (Setting setting : this.settings) {
            if (setting.isVisible()) {
               arraylist.add(setting);
            }
         }

         arraylist1 = arraylist;
      } finally {
         this.settingsLock.readLock().unlock();
      }

      return arraylist1;
   }

   public boolean hasSetting(String s) {
      return this.getSettingByName(s) != null;
   }

   public int getSettingCount() {
      this.settingsLock.readLock().lock();

      int i;
      try {
         i = this.settings.size();
      } finally {
         this.settingsLock.readLock().unlock();
      }

      return i;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public Category getCategory() {
      return this.category;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public List<Setting> getSettings() {
      return this.settings;
   }

   public ReentrantReadWriteLock getSettingsLock() {
      return this.settingsLock;
   }

   public Map<String, List<String>> ai() {
      return this.visibilityDependencies;
   }

   public Map<String, Map<String, Boolean>> aj() {
      return this.visibilityConditions;
   }

   public BooleanSetting getEnabledSetting() {
      return this.enabledSetting;
   }

   public KeyBindSetting getKeyBindSetting() {
      return this.keyBindSetting;
   }

   public GroupSetting getGroupSetting() {
      return this.groupSetting;
   }
}
