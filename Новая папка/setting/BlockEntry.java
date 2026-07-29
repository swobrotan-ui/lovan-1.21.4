package setting;

import java.awt.Color;

public class BlockEntry {
   private boolean enabled;
   private Color color;

   public BlockEntry(boolean flag, Color color) {
      this.enabled = flag;
      this.color = colorx;
   }

   public void setEnabled(boolean flag) {
      this.enabled = flag;
   }

   public void setColor(Color color) {
      this.color = colorx;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public Color getColor() {
      return this.color;
   }
}
