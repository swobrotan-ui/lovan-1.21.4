package data;

import com.google.gson.annotations.SerializedName;

public class ABItem {
   @SerializedName("название")
   private String name;
   @SerializedName("максимальная_цена")
   private double maxPrice;
   @SerializedName("частичное_совпадение")
   private boolean partial = false;

   public ABItem() {
   }

   public ABItem(String s, double d0) {
      this.name = s;
      this.maxPrice = d0;
   }

   public ABItem(String s, double d0, boolean flag) {
      this.name = s;
      this.maxPrice = d0;
      this.partial = flag;
   }

   @Override
   public String toString() {
      boolean flag = this.partial;
      double d0 = this.maxPrice;
      String s = this.name;
      return "ABItem{name='" + s + "', maxPrice=" + d0 + ", partial=" + flag + "}";
   }

   public void setName(String s) {
      this.name = s;
   }

   public void setMaxPrice(double d0) {
      this.maxPrice = d0;
   }

   public void setPartial(boolean flag) {
      this.partial = flag;
   }

   public String getName() {
      return this.name;
   }

   public double getMaxPrice() {
      return this.maxPrice;
   }

   public boolean isPartial() {
      return this.partial;
   }
}
