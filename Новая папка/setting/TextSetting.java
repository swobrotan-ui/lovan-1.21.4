package setting;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public class TextSetting extends Setting {
   @Expose
   private String value;
   @Expose
   private final String defaultValue;
   @Expose
   private final String placeholder;
   @Expose
   private final boolean isNumber;
   @Expose
   private final double minValue;
   @Expose
   private final double maxValue;

   public TextSetting(String s, String s1, String s2, String s3) {
      this(s, s1, s2, s3, false, 0.0, 0.0);
   }

   public TextSetting(String s, String s1, String s2, String s3, boolean flag) {
      this(s, s1, s2, s3, flag, 0.0, 100.0);
   }

   public TextSetting(String s, String s1, String s2, String s3, boolean flag, double d0, double d1) {
      super(s, s1);
      this.defaultValue = s2 != null ? s2 : "";
      this.value = this.defaultValue;
      this.placeholder = s3 != null ? s3 : "";
      this.isNumber = flag;
      this.minValue = d0;
      this.maxValue = d1;
   }

   public void setValue(String s) {
      if (s == null) {
         this.value = "";
      } else if (this.isNumber) {
         try {
            double d0 = Double.parseDouble(s);
            if (this.minValue != this.maxValue) {
               d0 = Math.max(this.minValue, Math.min(this.maxValue, d0));
            }

            this.value = String.valueOf(d0);
         } catch (NumberFormatException numberformatexception) {
         }
      } else {
         this.value = s;
      }
   }

   public void setNumericValue(double d0) {
      if (this.isNumber) {
         if (this.minValue != this.maxValue) {
            d0 = Math.max(this.minValue, Math.min(this.maxValue, d0));
         }

         this.value = String.valueOf(d0);
      }
   }

   public double getDoubleValue() {
      if (this.isNumber) {
         try {
            return Double.parseDouble(this.value);
         } catch (NumberFormatException numberformatexception) {
            return this.minValue;
         }
      } else {
         return 0.0;
      }
   }

   public int getIntValue() {
      return (int)this.getDoubleValue();
   }

   public float getFloatValue() {
      return (float)this.getDoubleValue();
   }

   public long getLongValue() {
      return (long)this.getDoubleValue();
   }

   public boolean isEmpty() {
      return this.value == null || this.value.trim().isEmpty();
   }

   public boolean isValidNumber() {
      if (!this.isNumber) {
         return false;
      } else {
         try {
            Double.parseDouble(this.value);
            return true;
         } catch (NumberFormatException numberformatexception) {
            return false;
         }
      }
   }

   public void increment() {
      if (this.isNumber && this.minValue != this.maxValue) {
         double d0 = this.getDoubleValue();
         double d1 = this.k();
         this.setNumericValue(d0 + d1);
      }
   }

   public void decrement() {
      if (this.isNumber && this.minValue != this.maxValue) {
         double d0 = this.getDoubleValue();
         double d1 = this.k();
         this.setNumericValue(d0 - d1);
      }
   }

   private double k() {
      double d0 = this.maxValue - this.minValue;
      if (d0 >= 100.0) {
         return 1.0;
      } else if (d0 >= 10.0) {
         return 0.1;
      } else {
         return d0 >= 1.0 ? 0.01 : 0.001;
      }
   }

   @Override
   public void reset() {
      this.value = this.defaultValue;
   }

   public boolean isAtDefault() {
      return this.value.equals(this.defaultValue);
   }

   public String getDisplayText() {
      if (this.isEmpty()) {
         String s = this.placeholder;
         String s1 = this.name;
         return s1 + ": " + s;
      } else {
         String s2 = this.value;
         String s3 = this.name;
         return s3 + ": " + s2;
      }
   }

   public boolean hasRange() {
      return this.isNumber && this.minValue != this.maxValue;
   }

   @Override
   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.name);
      jsonobject.addProperty("description", this.description);
      jsonobject.addProperty("type", this.getType());
      jsonobject.addProperty("value", this.value);
      jsonobject.addProperty("defaultValue", this.defaultValue);
      jsonobject.addProperty("placeholder", this.placeholder);
      jsonobject.addProperty("isNumber", this.isNumber);
      jsonobject.addProperty("minValue", this.minValue);
      jsonobject.addProperty("maxValue", this.maxValue);
      jsonobject.addProperty("isEmpty", this.isEmpty());
      jsonobject.addProperty("isValidNumber", this.isValidNumber());
      jsonobject.addProperty("isAtDefault", this.isAtDefault());
      jsonobject.addProperty("displayText", this.getDisplayText());
      jsonobject.addProperty("visible", this.visible);
      return jsonobject;
   }

   @Override
   public void fromJson(JsonObject jsonobject) {
      if (jsonobject.has("value")) {
         this.setValue(jsonobject.get("value").getAsString());
      }

      if (jsonobject.has("visible")) {
         this.visible = jsonobject.get("visible").getAsBoolean();
      }
   }

   @Override
   public String getType() {
      return "input";
   }

   public String getValue() {
      return this.value;
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }

   public String getPlaceholder() {
      return this.placeholder;
   }

   public boolean isNumber() {
      return this.isNumber;
   }

   public double getMinValue() {
      return this.minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }
}
