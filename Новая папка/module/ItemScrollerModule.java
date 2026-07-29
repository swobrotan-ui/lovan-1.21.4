package module;

import enum.Category;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class ItemScrollerModule extends Module {
   private static List<Field> ne;
   private static Method Am;
   private static Class<?> ayH;
   private long ahy = 0L;
   private int gC = -1;
   private final Set<Integer> akH = new HashSet<Integer>();

   public ItemScrollerModule() {
      super("ИтемЗсроллер", "Перемещайте предметы, удерживая клавишу Shift и наводя курсор на ячейки.", Category.PLAYER);
   }

   @Override
   public void onEnable() {
      this.i();
   }

   @Override
   public void onDisable() {
      this.i();
   }

   @Override
   public void onEndTick() {
      if (!this.isNotInWorld() && this.getInteractionManager() != null && this.getPlayer() != null) {
         boolean flag = this.getOptions().sneakKey.isPressed() || this.q();
         boolean flag1 = this.p();
         if (flag && flag1) {
            if (this.getScreen() instanceof HandledScreen handledscreen) {
               int j = this.getPlayer().currentScreenHandler.syncId;
               if (j != this.gC) {
                  this.akH.clear();
                  this.gC = j;
               }

               Slot slot = this.a(handledscreen);
               if (slot != null && slot.hasStack()) {
                  if (this.akH.add(slot.id)) {
                     long i = System.currentTimeMillis();
                     if (i - this.ahy < 0L) {
                        this.akH.remove(slot.id);
                     } else {
                        this.getInteractionManager()
                           .clickSlot(this.getPlayer().currentScreenHandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, this.getPlayer());
                        this.ahy = i;
                     }
                  }
               }
            } else {
               this.o();
            }
         } else {
            this.o();
         }
      } else {
         this.o();
      }
   }

   private Slot a(HandledScreen<?> handledscreen) {
      Slot slot = this.c(handledscreen);
      return slot != null ? slot : this.b(handledscreen);
   }

   private Slot b(HandledScreen<?> handledscreen) {
      try {
         if (ayH != handledscreen.getClass() || ne == null) {
            ayH = handledscreen.getClass();
            ne = this.d(ayH);
            Am = null;
         }

         if (ne.isEmpty()) {
            return null;
         }

         for (Field field : ne) {
            if (field.get(handledscreen) instanceof Slot slot && slot.id >= 0 && slot.id < this.getPlayer().currentScreenHandler.slots.size()) {
               return slot;
            }
         }
      } catch (Exception exception) {
      }

      return null;
   }

   private Slot c(HandledScreen<?> handledscreen) {
      try {
         if (ayH != handledscreen.getClass() || Am == null) {
            ayH = handledscreen.getClass();
            Am = this.e(ayH);
            ne = null;
         }

         if (Am == null) {
            return null;
         }

         double d0 = this.r();
         double d1 = this.s();

         for (Slot slot : this.getPlayer().currentScreenHandler.slots) {
            if (slot != null && slot.isEnabled() && this.f(handledscreen, Am, slot, d0, d1) instanceof Boolean obool && obool) {
               return slot;
            }
         }
      } catch (Exception exception) {
      }

      return null;
   }

   private List<Field> d(Class<?> oclass) {
      ArrayList arraylist = new ArrayList();

      for (Class oclass1 = oclass; oclass1 != null && oclass1 != Object.class; oclass1 = oclass1.getSuperclass()) {
         for (Field field : oclass1.getDeclaredFields()) {
            if (Slot.class.isAssignableFrom(field.getType())) {
               field.setAccessible(true);
               arraylist.add(field);
            }
         }
      }

      return arraylist;
   }

   private Method e(Class<?> oclass) {
      for (Class oclass1 = oclass; oclass1 != null && oclass1 != Object.class; oclass1 = oclass1.getSuperclass()) {
         for (Method method : oclass1.getDeclaredMethods()) {
            Class[] aclass = method.getParameterTypes();
            if (method.getReturnType() == boolean.class
               && aclass.length == 3
               && Slot.class.isAssignableFrom(aclass[0])
               && this.g(aclass[1])
               && this.g(aclass[2])) {
               method.setAccessible(true);
               return method;
            }
         }
      }

      return null;
   }

   private Object f(HandledScreen<?> handledscreen, Method method, Slot slot, double d0, double d1) throws Exception {
      Class[] aclass = method.getParameterTypes();
      Object object = this.h(aclass[1], d0);
      Object object1 = this.h(aclass[2], d1);
      return method.invoke(handledscreen, slot, object, object1);
   }

   private boolean g(Class<?> oclass) {
      return oclass == int.class || oclass == double.class || oclass == float.class;
   }

   private Object h(Class<?> oclass, double d0) {
      if (oclass == int.class) {
         return (int)Math.floor(d0);
      } else {
         return oclass == float.class ? (float)d0 : d0;
      }
   }

   private void i() {
      this.o();
      ayH = null;
      ne = null;
      Am = null;
   }

   private void o() {
      this.ahy = 0L;
      this.gC = -1;
      this.akH.clear();
   }

   private boolean p() {
      return GLFW.glfwGetMouseButton(this.getClient().getWindow().getHandle(), 0) == 1;
   }

   private boolean q() {
      long i = this.getClient().getWindow().getHandle();
      return GLFW.glfwGetKey(i, 340) == 1 || GLFW.glfwGetKey(i, 344) == 1;
   }

   private double r() {
      return this.getClient().mouse.getX() * this.getClient().getWindow().getScaledWidth() / this.getClient().getWindow().getWidth();
   }

   private double s() {
      return this.getClient().mouse.getY() * this.getClient().getWindow().getScaledHeight() / this.getClient().getWindow().getHeight();
   }
}
