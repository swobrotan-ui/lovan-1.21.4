package setting;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public class FilePickerSetting extends Setting {
   @Expose
   private String value;
   @Expose
   private final String defaultValue;

   public FilePickerSetting(String s, String s1) {
      this(s, s1, "");
   }

   public FilePickerSetting(String s, String s1, String s2) {
      super(s, s1);
      this.defaultValue = s2 != null ? s2 : "";
      this.value = this.defaultValue;
   }

   public void setValue(String s) {
      this.value = s != null ? s : "";
      this.notifyChange();
   }

   public boolean hasValue() {
      return this.value != null && !this.value.isEmpty();
   }

   public String getFileName() {
      if (!this.hasValue()) {
         return "";
      } else {
         int i = Math.max(this.value.lastIndexOf(47), this.value.lastIndexOf(92));
         return i >= 0 ? this.value.substring(i + 1) : this.value;
      }
   }

   @Override
   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.name);
      jsonobject.addProperty("description", this.description);
      jsonobject.addProperty("type", this.getType());
      jsonobject.addProperty("value", this.value);
      jsonobject.addProperty("visible", this.visible);
      return jsonobject;
   }

   @Override
   public void fromJson(JsonObject jsonobject) {
      if (jsonobject.has("value")) {
         this.value = jsonobject.get("value").getAsString();
      }

      if (jsonobject.has("visible")) {
         this.visible = jsonobject.get("visible").getAsBoolean();
      }
   }

   @Override
   public void reset() {
      this.value = this.defaultValue;
   }

   @Override
   public String getType() {
      return "file_picker";
   }

   public String getValue() {
      return this.value;
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }
}
