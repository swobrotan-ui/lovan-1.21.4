import net.minecraft.item.ItemStack;

class b {
   private kq abN;
   private ve[] anr;
   private boolean oS = false;

   public b() {
      this.abN = new kq();
      this.anr = new ve[4];

      for (int i = 0; i < 4; i++) {
         this.anr[i] = new ve();
         this.anr[i].a();
      }
   }

   public void a(ItemStack[] aitemstack, boolean flag) {
      if (!this.oS) {
         this.abN.a();
         this.oS = true;
      }

      for (int i = 0; i < 4; i++) {
         this.anr[i].e(!aitemstack[i].isEmpty());
         this.anr[i].b();
      }

      this.abN.f(flag);
      this.abN.b();
   }
}
