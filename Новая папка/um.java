import com.google.gson.annotations.SerializedName;

public final class um {
   @SerializedName("unicode1")
   private int leftChar;
   @SerializedName("unicode2")
   private int rightChar;
   private float advance;

   public int a() {
      return this.leftChar;
   }

   public int b() {
      return this.rightChar;
   }

   public float c() {
      return this.advance;
   }
}
