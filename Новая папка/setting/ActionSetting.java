package setting;

import com.google.gson.JsonObject;

public class ActionSetting extends Setting {
   private transient Runnable callback;

   public ActionSetting(String s, String s1) {
      super(s, s1);
   }

   public void invoke() {
      if (this.callback != null) {
         try {
            this.callback.run();
         } catch (Exception exception) {
         }
      }

      this.notifyChange();
   }

   @Override
   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.name);
      jsonobject.addProperty("description", this.description);
      jsonobject.addProperty("type", this.getType());
      jsonobject.addProperty("visible", this.visible);
      return jsonobject;
   }

   @Override
   public void fromJson(JsonObject jsonobject) {
      if (jsonobject.has("visible")) {
         this.visible = jsonobject.get("visible").getAsBoolean();
      }
   }

   @Override
   public void reset() {
   }

   @Override
   public String getType() {
      return "action";
   }

   public Runnable getCallback() {
      return this.callback;
   }

   public void setCallback(Runnable runnable) {
      this.callback = runnable;
   }
}
