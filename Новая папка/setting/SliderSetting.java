package setting;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public class SliderSetting extends Setting {
   @Expose
   private double value;
   @Expose
   private double defaultValue;
   @Expose
   private double min;
   @Expose
   private double max;
   @Expose
   private double step;
   @Expose
   private String unit;
   @Expose
   private int decimalPlaces;

   public SliderSetting(String s, String s1, double d0, double d1, double d2, double d3) {
      this(s, s1, d0, d1, d2, d3, "", 1);
   }

   public SliderSetting(String s, String s1, double d0, double d1, double d2, double d3, String s2, int i) {
      super(s, s1);
      this.defaultValue = d0;
      this.value = d0;
      this.min = d1;
      this.max = d2;
      this.step = d3;
      this.unit = s2 != null ? s2 : "";
      this.decimalPlaces = Math.max(0, i);
   }

   public void setValue(double d0) {
      double d1 = Math.round(d0 * Math.pow(10.0, this.decimalPlaces)) / Math.pow(10.0, this.decimalPlaces);
      double d2 = Math.max(this.min, Math.min(this.max, d1));
      if (this.value != d2) {
         this.value = d2;
         this.notifyChange();
      }
   }

   public void setValueRaw(double d0) {
      this.value = Math.max(this.min, Math.min(this.max, d0));
   }

   public int getIntValue() {
      return (int)Math.round(this.value);
   }

   public float getFloatValue() {
      return (float)this.value;
   }

   public double getDoubleValue() {
      return this.value;
   }

   public long getLongValue() {
      return (long)this.value;
   }

   @Override
   public void reset() {
      this.setValue(this.defaultValue);
   }

   public void increment() {
      this.setValue(this.value + this.step);
   }

   public void decrement() {
      this.setValue(this.value - this.step);
   }

   public double getPercentage() {
      return (this.value - this.min) / (this.max - this.min) * 100.0;
   }

   public void setPercentage(double d0) {
      d0 = Math.max(0.0, Math.min(100.0, d0));
      this.setValue(this.min + (this.max - this.min) * (d0 / 100.0));
   }

   public boolean isAtMin() {
      return Math.abs(this.value - this.min) < this.step / 2.0;
   }

   public boolean isAtMax() {
      return Math.abs(this.value - this.max) < this.step / 2.0;
   }

   public boolean isAtDefault() {
      return Math.abs(this.value - this.defaultValue) < this.step / 2.0;
   }

   public String getFormattedValue() {
      String s;
      if (this.decimalPlaces == 0) {
         s = "%.0f%s";
      } else {
         int i = this.decimalPlaces;
         s = "%." + i + "f%s";
      }

      return String.format(s, this.value, this.unit);
   }

   public String getDisplayText() {
      String s2 = this.name;
      String s = this.getFormattedValue();
      String s1 = s2;
      return s1 + ": " + s;
   }

   public double getRange() {
      return this.max - this.min;
   }

   public int getStepCount() {
      return (int)Math.round(this.getRange() / this.step);
   }

   @Override
   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.name);
      jsonobject.addProperty("description", this.description);
      jsonobject.addProperty("type", this.getType());
      jsonobject.addProperty("value", this.value);
      jsonobject.addProperty("defaultValue", this.defaultValue);
      jsonobject.addProperty("min", this.min);
      jsonobject.addProperty("max", this.max);
      jsonobject.addProperty("step", this.step);
      jsonobject.addProperty("unit", this.unit);
      jsonobject.addProperty("decimalPlaces", this.decimalPlaces);
      jsonobject.addProperty("formattedValue", this.getFormattedValue());
      jsonobject.addProperty("displayText", this.getDisplayText());
      jsonobject.addProperty("percentage", this.getPercentage());
      jsonobject.addProperty("isAtMin", this.isAtMin());
      jsonobject.addProperty("isAtMax", this.isAtMax());
      jsonobject.addProperty("isAtDefault", this.isAtDefault());
      jsonobject.addProperty("range", this.getRange());
      jsonobject.addProperty("stepCount", this.getStepCount());
      jsonobject.addProperty("visible", this.visible);
      return jsonobject;
   }

   @Override
   public void fromJson(JsonObject jsonobject) {
      if (jsonobject.has("value")) {
         this.setValue(jsonobject.get("value").getAsDouble());
      }

      if (jsonobject.has("percentage")) {
         this.setPercentage(jsonobject.get("percentage").getAsDouble());
      }

      if (jsonobject.has("visible")) {
         this.visible = jsonobject.get("visible").getAsBoolean();
      }
   }

   @Override
   public String getType() {
      return "slider";
   }

   public double getValue() {
      return this.value;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public double getStep() {
      return this.step;
   }

   public String getUnit() {
      return this.unit;
   }

   public int getDecimalPlaces() {
      return this.decimalPlaces;
   }

   public void setDefaultValue(double d0) {
      this.defaultValue = d0;
   }

   public void setMin(double d0) {
      this.min = d0;
   }

   public void setMax(double d0) {
      this.max = d0;
   }

   public void setStep(double d0) {
      this.step = d0;
   }

   public void setUnit(String s) {
      this.unit = s;
   }

   public void setDecimalPlaces(int i) {
      this.decimalPlaces = i;
   }
}
