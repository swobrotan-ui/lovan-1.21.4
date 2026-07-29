class ne {
   private final int QQ;
   private final Object aii;

   private ne(int i, Object object) {
      this.QQ = i;
      this.aii = object;
   }

   static ne a(Object object) {
      return new ne(0, object);
   }

   static ne b() {
      return new ne(1, null);
   }

   static ne c() {
      return new ne(2, null);
   }

   static ne d() {
      return new ne(3, null);
   }

   Object e(Object object) {
      if (this.QQ == 1) {
         return object;
      } else if (this.QQ == 2) {
         return false;
      } else {
         return this.QQ == 3 ? 0 : this.aii;
      }
   }
}
