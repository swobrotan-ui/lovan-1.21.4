package enum;

import core.Localization;

public enum Category {
   COMBAT("Сомбат", "C"),
   MOVEMENT("Мовемент", "U"),
   PLAYER("Плайер", "M"),
   RENDER("Рендер", "N"),
   VISUAL("Визуал", "O"),
   PARTICLES("Партислез", "", VISUAL),
   CLIENT("Слиент", "P");

   private final String displayName;
   private final String icon;
   private final Category parent;

   private Category(String s1, String s2) {
      this(s1, s2, null);
   }

   private Category(String s1, String s2, Category category1) {
      this.displayName = s1;
      this.icon = s2;
      this.parent = category1;
   }

   public String getLocalizedName() {
      return Localization.a().c(this.displayName);
   }

   public boolean isSubCategory() {
      return this.parent != null;
   }

   @Override
   public String toString() {
      return this.displayName;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getIcon() {
      return this.icon;
   }

   public Category getParent() {
      return this.parent;
   }

   // $VF: synthetic method
   private static Category[] g() {
      return new Category[]{COMBAT, MOVEMENT, PLAYER, RENDER, VISUAL, PARTICLES, CLIENT};
   }
}
