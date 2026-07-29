package setting;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import java.util.concurrent.ThreadLocalRandom;

public class RangeSetting extends Setting {
   @Expose
   private double valueLow;
   @Expose
   private double valueHigh;
   @Expose
   private double defaultLow;
   @Expose
   private double defaultHigh;
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

   public RangeSetting(String s, String s1, double d0, double d1, double d2, double d3, double d4) {
      this(s, s1, d0, d1, d2, d3, d4, "", 1);
   }

   public RangeSetting(String s, String s1, double d0, double d1, double d2, double d3, double d4, String s2, int i) {
      super(s, s1);
      this.defaultLow = d0;
      this.defaultHigh = d1;
      this.min = d2;
      this.max = d3;
      this.step = d4;
      this.unit = s2 != null ? s2 : "";
      this.decimalPlaces = Math.max(0, i);
      this.valueLow = this.f(d0);
      this.valueHigh = this.f(d1);
   }

   public void setRange(double d0, double d1) {
      double d2 = this.g(this.f(Math.min(d0, d1)));
      double d3 = this.g(this.f(Math.max(d0, d1)));
      if (this.valueLow != d2 || this.valueHigh != d3) {
         this.valueLow = d2;
         this.valueHigh = d3;
         this.notifyChange();
      }
   }

   public void setValueLow(double d0) {
      double d1 = this.g(this.f(Math.min(d0, this.valueHigh)));
      if (this.valueLow != d1) {
         this.valueLow = d1;
         this.notifyChange();
      }
   }

   public void setValueHigh(double d0) {
      double d1 = this.g(this.f(Math.max(d0, this.valueLow)));
      if (this.valueHigh != d1) {
         this.valueHigh = d1;
         this.notifyChange();
      }
   }

   public double getRandomInRange() {
      return Math.abs(this.valueLow - this.valueHigh) < this.step / 2.0 ? this.valueLow : ThreadLocalRandom.current().nextDouble(this.valueLow, this.valueHigh);
   }

   public long getRandomLong() {
      return Math.round(this.getRandomInRange());
   }

   private double f(double d0) {
      return Math.max(this.min, Math.min(this.max, d0));
   }

   private double g(double d0) {
      return Math.round(d0 * Math.pow(10.0, this.decimalPlaces)) / Math.pow(10.0, this.decimalPlaces);
   }

   @Override
   public void reset() {
      this.setRange(this.defaultLow, this.defaultHigh);
   }

   public String getFormattedLow() {
      return this.k(this.valueLow);
   }

   public String getFormattedHigh() {
      return this.k(this.valueHigh);
   }

   public String getDisplayText() {
      String s = this.getFormattedHigh();
      String s1 = this.getFormattedLow();
      return s1 + " - " + s;
   }

   private String k(double d0) {
      if (this.decimalPlaces == 0) {
         return String.format("%.0f%s", d0, this.unit);
      } else {
         int i = this.decimalPlaces;
         return String.format("%." + i + "f%s", d0, this.unit);
      }
   }

   @Override
   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.name);
      jsonobject.addProperty("description", this.description);
      jsonobject.addProperty("type", this.getType());
      jsonobject.addProperty("valueLow", this.valueLow);
      jsonobject.addProperty("valueHigh", this.valueHigh);
      jsonobject.addProperty("defaultLow", this.defaultLow);
      jsonobject.addProperty("defaultHigh", this.defaultHigh);
      jsonobject.addProperty("min", this.min);
      jsonobject.addProperty("max", this.max);
      jsonobject.addProperty("step", this.step);
      jsonobject.addProperty("unit", this.unit);
      jsonobject.addProperty("decimalPlaces", this.decimalPlaces);
      jsonobject.addProperty("visible", this.visible);
      return jsonobject;
   }

   @Override
   public void fromJson(JsonObject jsonobject) {
      double d0 = jsonobject.has("valueLow") ? jsonobject.get("valueLow").getAsDouble() : this.valueLow;
      double d1 = jsonobject.has("valueHigh") ? jsonobject.get("valueHigh").getAsDouble() : this.valueHigh;
      this.setRange(d0, d1);
      if (jsonobject.has("visible")) {
         this.visible = jsonobject.get("visible").getAsBoolean();
      }
   }

   @Override
   public String getType() {
      return "range_slider";
   }

   public double getValueLow() {
      return this.valueLow;
   }

   public double getValueHigh() {
      return this.valueHigh;
   }

   public double getDefaultLow() {
      return this.defaultLow;
   }

   public double getDefaultHigh() {
      return this.defaultHigh;
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

   public void setDefaultLow(double d0) {
      this.defaultLow = d0;
   }

   public void setDefaultHigh(double d0) {
      this.defaultHigh = d0;
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
