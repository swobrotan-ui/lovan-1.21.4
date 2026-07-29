package setting;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import java.awt.Color;

public class ColorSetting extends Setting {
   @Expose
   private int red;
   @Expose
   private int green;
   @Expose
   private int blue;
   @Expose
   private int alpha;
   @Expose
   private int defaultRed;
   @Expose
   private int defaultGreen;
   @Expose
   private int defaultBlue;
   @Expose
   private int defaultAlpha;
   @Expose
   private boolean hasAlpha;

   public ColorSetting(String s, String s1, Color color) {
      this(s, s1, color, false);
   }

   public ColorSetting(String s, String s1, Color color, boolean flag) {
      super(s, s1);
      this.defaultRed = color.getRed();
      this.defaultGreen = color.getGreen();
      this.defaultBlue = color.getBlue();
      this.defaultAlpha = flag ? color.getAlpha() : 255;
      this.red = this.defaultRed;
      this.green = this.defaultGreen;
      this.blue = this.defaultBlue;
      this.alpha = this.defaultAlpha;
      this.hasAlpha = flag;
   }

   public void setColor(Color color) {
      int i = this.red;
      int j = this.green;
      int k = this.blue;
      int l = this.alpha;
      this.red = color.getRed();
      this.green = color.getGreen();
      this.blue = color.getBlue();
      if (this.hasAlpha) {
         this.alpha = color.getAlpha();
      }

      if (i != this.red || j != this.green || k != this.blue || l != this.alpha) {
         this.notifyChange();
      }
   }

   public void setRGB(int i, int j, int k) {
      this.setColor(new Color(Math.max(0, Math.min(255, i)), Math.max(0, Math.min(255, j)), Math.max(0, Math.min(255, k)), this.alpha));
   }

   public void setRGBA(int i, int j, int k, int l) {
      this.setColor(
         new Color(
            Math.max(0, Math.min(255, i)), Math.max(0, Math.min(255, j)), Math.max(0, Math.min(255, k)), this.hasAlpha ? Math.max(0, Math.min(255, l)) : 255
         )
      );
   }

   public Color getColor() {
      return new Color(this.red, this.green, this.blue, this.alpha);
   }

   public int getRGB() {
      return new Color(this.red, this.green, this.blue).getRGB();
   }

   public int getRGBA() {
      return new Color(this.red, this.green, this.blue, this.alpha).getRGB();
   }

   @Override
   public void reset() {
      this.setRGBA(this.defaultRed, this.defaultGreen, this.defaultBlue, this.defaultAlpha);
   }

   public boolean isDefault() {
      return this.red == this.defaultRed && this.green == this.defaultGreen && this.blue == this.defaultBlue && this.alpha == this.defaultAlpha;
   }

   public String getHex() {
      return this.hasAlpha
         ? String.format("#%02X%02X%02X%02X", this.red, this.green, this.blue, this.alpha)
         : String.format("#%02X%02X%02X", this.red, this.green, this.blue);
   }

   @Override
   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.name);
      jsonobject.addProperty("description", this.description);
      jsonobject.addProperty("type", this.getType());
      jsonobject.addProperty("red", this.red);
      jsonobject.addProperty("green", this.green);
      jsonobject.addProperty("blue", this.blue);
      jsonobject.addProperty("alpha", this.alpha);
      jsonobject.addProperty("hasAlpha", this.hasAlpha);
      jsonobject.addProperty("hex", this.getHex());
      jsonobject.addProperty("visible", this.visible);
      return jsonobject;
   }

   @Override
   public void fromJson(JsonObject jsonobject) {
      if (jsonobject.has("red") && jsonobject.has("green") && jsonobject.has("blue")) {
         int i = jsonobject.get("red").getAsInt();
         int j = jsonobject.get("green").getAsInt();
         int k = jsonobject.get("blue").getAsInt();
         int l = jsonobject.has("alpha") ? jsonobject.get("alpha").getAsInt() : 255;
         this.setRGBA(i, j, k, l);
      }

      if (jsonobject.has("visible")) {
         this.visible = jsonobject.get("visible").getAsBoolean();
      }
   }

   @Override
   public String getType() {
      return "color";
   }

   public int getRed() {
      return this.red;
   }

   public int getGreen() {
      return this.green;
   }

   public int getBlue() {
      return this.blue;
   }

   public int getAlpha() {
      return this.alpha;
   }

   public int getDefaultRed() {
      return this.defaultRed;
   }

   public int getDefaultGreen() {
      return this.defaultGreen;
   }

   public int getDefaultBlue() {
      return this.defaultBlue;
   }

   public int getDefaultAlpha() {
      return this.defaultAlpha;
   }

   public boolean hasAlpha() {
      return this.hasAlpha;
   }

   public void setRed(int i) {
      this.red = i;
   }

   public void setGreen(int i) {
      this.green = i;
   }

   public void setBlue(int i) {
      this.blue = i;
   }

   public void setAlpha(int i) {
      this.alpha = i;
   }

   public void setDefaultRed(int i) {
      this.defaultRed = i;
   }

   public void setDefaultGreen(int i) {
      this.defaultGreen = i;
   }

   public void setDefaultBlue(int i) {
      this.defaultBlue = i;
   }

   public void setDefaultAlpha(int i) {
      this.defaultAlpha = i;
   }

   public void setHasAlpha(boolean flag) {
      this.hasAlpha = flag;
   }
}
