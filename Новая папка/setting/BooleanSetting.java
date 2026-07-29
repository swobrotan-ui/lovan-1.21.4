package setting;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public class BooleanSetting extends Setting {
   @Expose
   private boolean value;
   @Expose
   private boolean defaultValue;

   public BooleanSetting(String s, String s1, boolean flag) {
      super(s, s1);
      this.defaultValue = flag;
      this.value = flag;
   }

   public boolean getValue() {
      return this.value;
   }

   public void setValue(boolean flag) {
      if (this.value != flag) {
         this.value = flag;
         this.notifyChange();
      }
   }

   public void toggle() {
      this.value = !this.value;
      this.notifyChange();
   }

   @Override
   public void reset() {
      this.setValue(this.defaultValue);
   }

   @Override
   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.name);
      jsonobject.addProperty("description", this.description);
      jsonobject.addProperty("type", this.getType());
      jsonobject.addProperty("value", this.value);
      jsonobject.addProperty("defaultValue", this.defaultValue);
      jsonobject.addProperty("visible", this.visible);
      return jsonobject;
   }

   @Override
   public void fromJson(JsonObject jsonobject) {
      if (jsonobject.has("value")) {
         this.value = jsonobject.get("value").getAsBoolean();
      }

      if (jsonobject.has("visible")) {
         this.visible = jsonobject.get("visible").getAsBoolean();
      }
   }

   @Override
   public String getType() {
      return "boolean";
   }

   public void setDefaultValue(boolean flag) {
      this.defaultValue = flag;
   }

   public boolean getDefaultValue() {
      return this.defaultValue;
   }
}
