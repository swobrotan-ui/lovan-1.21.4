package gui;

import org.joml.Matrix4f;

public interface InteractiveComponent {
   void render(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3);

   boolean mouseClicked(double d0, double d1, int i);

   boolean mouseReleased(double d0, double d1, int i);

   boolean mouseDragged(double d0, double d1, int i, double d2, double d3);

   boolean mouseScrolled(double d0, double d1, double d2, double d3);

   boolean keyPressed(int i, int j, int k);

   boolean keyReleased(int i, int j, int k);

   boolean charTyped(char c0, int i);

   void tick(float f);

   float getWidth();

   float getHeight();

   float getX();

   float getY();

   void setPosition(float f, float f1);

   boolean isHovered(double d0, double d1);

   boolean isVisible();

   void setVisible(boolean flag);

   boolean isEnabled();

   void setEnabled(boolean flag);
}
