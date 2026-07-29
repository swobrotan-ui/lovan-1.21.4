package data;

record Rect(int x, int y, int width, int height, int x, int y, int width, int height, int x, int y, int width, int height, int x, int y, int width, int height) {
   private Rect(int i, int j, int k, int l) {
      this.x = i;
      this.y = j;
      this.width = k;
      this.height = l;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }
}
