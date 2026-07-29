package data;

import java.awt.Color;

record ColoredText(String text, Color color, String text, Color color, String text, Color color, String text, Color color) {
   private ColoredText(String s, Color color) {
      this.text = s;
      this.color = colorx;
   }

   public String getText() {
      return this.text;
   }

   public Color getColor() {
      return this.color;
   }
}
