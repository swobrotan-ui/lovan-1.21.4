import lombok.Generated;
import org.lwjgl.glfw.GLFW;

public enum vk {
   ZU(GLFW.glfwCreateStandardCursor(221185)),
   eZ(GLFW.glfwCreateStandardCursor(221188)),
   G(GLFW.glfwCreateStandardCursor(221189)),
   arr(GLFW.glfwCreateStandardCursor(221190)),
   aah(GLFW.glfwCreateStandardCursor(221186)),
   Uy(GLFW.glfwCreateStandardCursor(221187)),
   FG(GLFW.glfwCreateStandardCursor(221194)),
   mq(GLFW.glfwCreateStandardCursor(221193));

   private final long ayg;

   public static vk a(String s) {
      return Enum.<vk>valueOf(vk.class, s);
   }

   @Generated
   public long b() {
      return this.ayg;
   }

   @Generated
   private vk(long j) {
      this.ayg = j;
   }

   // $VF: synthetic method
   private static vk[] c() {
      return new vk[]{ZU, eZ, G, arr, aah, Uy, FG, mq};
   }
}
