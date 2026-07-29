package setting;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import org.lwjgl.glfw.GLFW;

public class ActionKeySetting extends Setting {
   @Expose
   private int keyCode;
   @Expose
   private int modifierFlags;
   @Expose
   private int defaultKeyCode;
   @Expose
   private String keyName;
   private Runnable callback;

   public ActionKeySetting(String s, String s1, int i) {
      super(s, s1);
      this.defaultKeyCode = i;
      this.keyCode = i;
      this.modifierFlags = 0;
      this.keyName = KeyBindSetting.formatKeyNameStatic(i, 0);
   }

   public ActionKeySetting(String s, String s1, int i, Runnable runnable) {
      this(s, s1, i);
      this.callback = runnable;
   }

   public void setKeyCode(int i) {
      this.keyCode = i;
      this.modifierFlags = 0;
      this.keyName = this.formatKey(i);
   }

   public void setKeyAndModifiers(int i, int j) {
      this.keyCode = i;
      this.modifierFlags = j;
      this.keyName = KeyBindSetting.formatKeyNameStatic(i, j);
   }

   public void invoke() {
      if (this.callback != null) {
         this.callback.run();
      }
   }

   public String getFullKeyName() {
      return this.keyName;
   }

   public String formatKey(int i) {
      if (i == -1) {
         return "None";
      } else {
         switch (i) {
            case 0:
               return "M1";
            case 1:
               return "M2";
            case 2:
               return "M3";
            case 3:
               return "M4";
            case 4:
               return "M5";
            case 5:
               return "M6";
            case 6:
               return "M7";
            case 7:
               return "M8";
            default:
               switch (i) {
                  case 32:
                     return "SPACE";
                  case 39:
                     return "'";
                  case 44:
                     return ",";
                  case 45:
                     return "-";
                  case 46:
                     return ".";
                  case 47:
                     return "/";
                  case 59:
                     return ";";
                  case 61:
                     return "=";
                  case 91:
                     return "[";
                  case 92:
                     return "\\";
                  case 93:
                     return "]";
                  case 96:
                     return "`";
                  case 256:
                     return "ESC";
                  case 257:
                     return "ENTER";
                  case 258:
                     return "TAB";
                  case 259:
                     return "BACKSPACE";
                  case 260:
                     return "INSERT";
                  case 261:
                     return "DELETE";
                  case 262:
                     return "RIGHT";
                  case 263:
                     return "LEFT";
                  case 264:
                     return "DOWN";
                  case 265:
                     return "UP";
                  case 266:
                     return "PAGE_UP";
                  case 267:
                     return "PAGE_DOWN";
                  case 268:
                     return "HOME";
                  case 269:
                     return "END";
                  case 280:
                     return "CAPS";
                  case 281:
                     return "SCROLL_LOCK";
                  case 282:
                     return "NUM_LOCK";
                  case 340:
                     return "LSHIFT";
                  case 341:
                     return "LCTRL";
                  case 342:
                     return "LALT";
                  case 344:
                     return "RSHIFT";
                  case 345:
                     return "RCTRL";
                  case 346:
                     return "RALT";
                  default:
                     if (i >= 290 && i <= 314) {
                        int j = i - 290 + 1;
                        return "F" + j;
                     } else if (i >= 320 && i <= 329) {
                        int k = i - 320;
                        return "KP_" + k;
                     } else if (i >= 48 && i <= 57) {
                        return String.valueOf((char)i);
                     } else {
                        return i >= 65 && i <= 90 ? String.valueOf((char)i) : "KEY_" + i;
                     }
               }
         }
      }
   }

   public boolean isPressed() {
      if (this.keyCode != -1 && this.keyCode != -1) {
         long i = GLFW.glfwGetCurrentContext();
         if (i == 0L) {
            return false;
         } else {
            return this.keyCode >= 0 && this.keyCode <= 7 ? GLFW.glfwGetMouseButton(i, this.keyCode) == 1 : GLFW.glfwGetKey(i, this.keyCode) == 1;
         }
      } else {
         return false;
      }
   }

   @Override
   public void reset() {
      this.setKeyCode(this.defaultKeyCode);
   }

   @Override
   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.name);
      jsonobject.addProperty("description", this.description);
      jsonobject.addProperty("type", this.getType());
      jsonobject.addProperty("keyCode", this.keyCode);
      jsonobject.addProperty("modifierFlags", this.modifierFlags);
      jsonobject.addProperty("defaultKeyCode", this.defaultKeyCode);
      jsonobject.addProperty("keyName", this.keyName);
      jsonobject.addProperty("fullKeyName", this.getFullKeyName());
      return jsonobject;
   }

   @Override
   public void fromJson(JsonObject jsonobject) {
      if (jsonobject.has("keyCode")) {
         int i = jsonobject.get("keyCode").getAsInt();
         int j = jsonobject.has("modifierFlags") ? jsonobject.get("modifierFlags").getAsInt() : 0;
         this.setKeyAndModifiers(i, j);
      }
   }

   @Override
   public String getType() {
      return "hotkey";
   }

   public int getKeyCode() {
      return this.keyCode;
   }

   public int getModifierFlags() {
      return this.modifierFlags;
   }

   public int getDefaultKeyCode() {
      return this.defaultKeyCode;
   }

   public String getKeyName() {
      return this.keyName;
   }

   public Runnable getCallback() {
      return this.callback;
   }

   public void setModifierFlags(int i) {
      this.modifierFlags = i;
   }

   public void setDefaultKeyCode(int i) {
      this.defaultKeyCode = i;
   }

   public void setKeyName(String s) {
      this.keyName = s;
   }

   public void setCallback(Runnable runnable) {
      this.callback = runnable;
   }
}
