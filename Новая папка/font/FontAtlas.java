package font;

import com.google.gson.annotations.SerializedName;

public final class FontAtlas {
   @SerializedName("distanceRange")
   private float range;
   private float width;
   private float height;

   public float a() {
      return this.range;
   }

   public float b() {
      return this.width;
   }

   public float c() {
      return this.height;
   }
}
