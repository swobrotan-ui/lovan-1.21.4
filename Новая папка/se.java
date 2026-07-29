public abstract class se<T> {
   public se() {
      this.b();
   }

   public final T a() {
      Object object = this.c();
      this.b();
      return (T)object;
   }

   protected abstract void b();

   protected abstract T c();
}
