import font.MSDFFont;
import java.util.Objects;

class zb {
   private int UI;
   private String C;
   private float RB;
   private int CR;

   zb() {
   }

   zb(MSDFFont msdffont, String s, float f, int i) {
      this.UI = msdffont.hashCode();
      this.C = s;
      this.RB = f;
      this.CR = i;
   }

   void a(MSDFFont msdffont, String s, float f, int i) {
      this.UI = msdffont.hashCode();
      this.C = s;
      this.RB = f;
      this.CR = i;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return object instanceof zb zb ? this.UI == zb.UI && Float.compare(zb.RB, this.RB) == 0 && this.CR == zb.CR && Objects.equals(this.C, zb.C) : false;
      }
   }

   @Override
   public int hashCode() {
      int i = this.UI;
      i = 31 * i + (this.C != null ? this.C.hashCode() : 0);
      i = 31 * i + Float.floatToIntBits(this.RB);
      return 31 * i + this.CR;
   }
}
