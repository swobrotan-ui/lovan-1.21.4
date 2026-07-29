package setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GroupSetting extends Setting {
   private final List<Setting> settings;
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
   private boolean expanded;

   public GroupSetting(String s, String s1) {
      super(s, s1);
      this.settings = new ArrayList<Setting>();
      this.expanded = false;
   }

   public GroupSetting(String s, String s1, boolean flag) {
      super(s, s1);
      this.settings = new ArrayList<Setting>();
      this.expanded = flag;
   }

   public void addSetting(Setting setting) {
      if (setting != null) {
         this.lock.writeLock().lock();

         try {
            if (!this.settings.stream().anyMatch(setting2 -> {
               return setting2.getName().equals(setting.getName());
            })) {
               this.settings.add(setting);
               return;
            }
         } finally {
            this.lock.writeLock().unlock();
         }
      }
   }

   public void addSettings(Setting... asetting) {
      if (asetting != null && asetting.length != 0) {
         this.lock.writeLock().lock();

         try {
            for (Setting setting : asetting) {
               if (setting != null && !this.settings.stream().anyMatch(setting2 -> {
                  return setting2.getName().equals(setting.getName());
               })) {
                  this.settings.add(setting);
               }
            }
         } finally {
            this.lock.writeLock().unlock();
         }
      }
   }

   public void removeSetting(String s) {
      if (s != null && !s.trim().isEmpty()) {
         this.lock.writeLock().lock();

         try {
            this.settings.removeIf(setting -> {
               return setting.getName().equals(s);
            });
         } finally {
            this.lock.writeLock().unlock();
         }
      }
   }

   public Setting getSetting(String s) {
      if (s != null && !s.trim().isEmpty()) {
         this.lock.readLock().lock();

         Setting setting;
         try {
            setting = this.settings.stream().filter(setting1 -> {
               return setting1.getName().equals(s);
            }).findFirst().orElse(null);
         } finally {
            this.lock.readLock().unlock();
         }

         return setting;
      } else {
         return null;
      }
   }

   public boolean hasSetting(String s) {
      return this.getSetting(s) != null;
   }

   public int getSettingCount() {
      this.lock.readLock().lock();

      int i;
      try {
         i = this.settings.size();
      } finally {
         this.lock.readLock().unlock();
      }

      return i;
   }

   public List<Setting> getSettings() {
      this.lock.readLock().lock();

      ArrayList arraylist;
      try {
         arraylist = new ArrayList<Setting>(this.settings);
      } finally {
         this.lock.readLock().unlock();
      }

      return arraylist;
   }

   public List<Setting> getVisibleSettings() {
      this.lock.readLock().lock();

      List list;
      try {
         list = this.settings.stream().filter(Setting::isVisible).toList();
      } finally {
         this.lock.readLock().unlock();
      }

      return list;
   }

   @Override
   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.name);
      jsonobject.addProperty("description", this.description);
      jsonobject.addProperty("type", this.getType());
      jsonobject.addProperty("visible", this.visible);
      jsonobject.addProperty("expanded", this.expanded);
      jsonobject.addProperty("settingCount", this.getSettingCount());
      JsonArray jsonarray = new JsonArray();
      this.lock.readLock().lock();

      try {
         for (Setting setting : this.settings) {
            if (setting.isVisible()) {
               jsonarray.add(setting.toJson());
            }
         }
      } finally {
         this.lock.readLock().unlock();
      }

      jsonobject.add("settings", jsonarray);
      return jsonobject;
   }

   @Override
   public void fromJson(JsonObject jsonobject) {
      if (jsonobject.has("expanded")) {
         this.expanded = jsonobject.get("expanded").getAsBoolean();
      }

      if (jsonobject.has("visible")) {
         this.visible = jsonobject.get("visible").getAsBoolean();
      }

      if (jsonobject.has("settings") && jsonobject.get("settings").isJsonArray()) {
         JsonArray jsonarray = jsonobject.get("settings").getAsJsonArray();

         for (int i = 0; i < jsonarray.size(); i++) {
            JsonObject jsonobject1 = jsonarray.get(i).getAsJsonObject();
            if (jsonobject1.has("name")) {
               String s = jsonobject1.get("name").getAsString();
               Setting setting = this.getSetting(s);
               if (setting != null) {
                  setting.fromJson(jsonobject1);
               }
            }
         }
      }
   }

   @Override
   public void reset() {
      this.lock.readLock().lock();

      try {
         for (Setting setting : this.settings) {
            setting.reset();
         }
      } finally {
         this.lock.readLock().unlock();
      }
   }

   @Override
   public String getType() {
      return "group";
   }

   public ReentrantReadWriteLock getLock() {
      return this.lock;
   }

   public boolean isExpanded() {
      return this.expanded;
   }

   public void setExpanded(boolean flag) {
      this.expanded = flag;
   }
}
