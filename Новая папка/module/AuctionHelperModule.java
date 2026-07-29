package module;

import enum.Category;
import enum.PacketDirection;
import event.PacketEvent;
import event.RenderHudEvent;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import setting.ColorSetting;
import util.UnsafeFieldAccessor;

public class AuctionHelperModule extends Module {
   private static long aaN = 900L;
   private static float ce = 0.35F;
   private static float ahK = 1.0F;
   private static long aAi = 90L;
   private static long LM = 650L;
   private static int Hb = 104;
   private static Pattern bD = Pattern.compile("(\\d{1,3}(?:[\\s,._]\\d{3})+|\\d+)");
   private ColorSetting cheapestItemColorSetting = new ColorSetting("Самый дешевый предмет", "this", new Color(75, 255, 75, 255));
   private ColorSetting economicItemColorSetting = new ColorSetting("Экономичный предмет", "this", new Color(255, 75, 75, 255));
   private Slot arW;
   private Slot alU;
   private int RE = -1;
   private long mm = 0L;
   private boolean tq = false;
   private static boolean DS = false;
   private static Method auK;
   private static ne[] aay;
   private static boolean nB = false;
   private static UnsafeFieldAccessor<Integer> Em;
   private static UnsafeFieldAccessor<Integer> WX;
   private static final int BA = 23;
   private static final int acR = 24;

   public AuctionHelperModule() {
      super("АустионХелпер", "Подсветка лучших предметов на аукционе.", Category.PLAYER);
      this.addSettings(this.cheapestItemColorSetting, this.economicItemColorSetting);
   }

   @Override
   public void onPacket(PacketEvent packetevent) {
      if (packetevent.getType() == PacketDirection.RECEIVE) {
         if (packetevent.getPacket() instanceof ScreenHandlerSlotUpdateS2CPacket) {
            if (this.getClient().currentScreen instanceof GenericContainerScreen genericcontainerscreen) {
               if (this.s(genericcontainerscreen)) {
                  this.tq = true;
               }
            }
         }
      }
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   @Override
   public void onStartTick() {
      if (this.getScreen() instanceof GenericContainerScreen genericcontainerscreen && this.s(genericcontainerscreen)) {
         long i = System.currentTimeMillis();
         this.a(genericcontainerscreen, i);
         if (!this.tq && i - this.mm >= LM) {
            this.b(genericcontainerscreen);
         }
      } else {
         this.u();
      }
   }

   @Override
   public void onRenderHud(RenderHudEvent renderhudevent) {
      if (this.getScreen() instanceof GenericContainerScreen genericcontainerscreen && this.s(genericcontainerscreen)) {
         long i1 = System.currentTimeMillis();
         this.a(genericcontainerscreen, i1);
         int[] aint = this.v(genericcontainerscreen);
         int i = aint[0];
         int j = aint[1];
         int k = this.q(this.cheapestItemColorSetting.getColor());
         int l = this.q(this.economicItemColorSetting.getColor());
         if (this.arW != null) {
            this.r(renderhudevent.getContext(), i, j, this.arW, k);
         }

         if (this.alU != null) {
            this.r(renderhudevent.getContext(), i, j, this.alU, l);
         }
      } else {
         this.u();
      }
   }

   private void a(GenericContainerScreen genericcontainerscreen, long i) {
      int j = ((GenericContainerScreenHandler)genericcontainerscreen.getScreenHandler()).syncId;
      if (j != this.RE) {
         this.RE = j;
         this.tq = true;
         this.arW = null;
         this.alU = null;
      }

      if (this.tq && i - this.mm >= aAi) {
         this.b(genericcontainerscreen);
      }
   }

   private void b(GenericContainerScreen genericcontainerscreen) {
      if (this.getPlayer() == null) {
         this.u();
      } else {
         long i = System.currentTimeMillis();
         DefaultedList defaultedlist = ((GenericContainerScreenHandler)genericcontainerscreen.getScreenHandler()).slots;
         int j = defaultedlist.size();
         int[] aint = new int[j];
         int[] aint1 = new int[j];
         Slot slot = null;
         int k = Integer.MAX_VALUE;

         for (int l = 0; l < j; l++) {
            Slot slot1 = (Slot)defaultedlist.get(l);
            ItemStack itemstack = slot1.getStack();
            if (itemstack.isEmpty()) {
               aint[l] = -1;
               aint1[l] = 0;
            } else if (slot1.inventory == this.getInventory()) {
               aint[l] = -1;
               aint1[l] = 0;
            } else if (slot1.y >= Hb) {
               aint[l] = -1;
               aint1[l] = 0;
            } else {
               int i1 = this.c(itemstack);
               int j1 = Math.max(1, itemstack.getCount());
               aint[l] = i1;
               aint1[l] = j1;
               if (i1 >= 0 && i1 < k) {
                  k = i1;
                  slot = slot1;
               }
            }
         }

         Slot slot3 = null;
         double d1 = Double.POSITIVE_INFINITY;
         int i2 = Integer.MAX_VALUE;

         for (int j2 = 0; j2 < j; j2++) {
            int k1 = aint[j2];
            if (k1 >= 0) {
               Slot slot2 = (Slot)defaultedlist.get(j2);
               if (slot2 != slot) {
                  int l1 = Math.max(1, aint1[j2]);
                  double d0 = (double)k1 / l1;
                  boolean flag = d0 < d1 - 1.0E-9;
                  boolean flag1 = Math.abs(d0 - d1) <= 1.0E-9;
                  if (flag || flag1 && k1 < i2) {
                     d1 = d0;
                     i2 = k1;
                     slot3 = slot2;
                  }
               }
            }
         }

         this.arW = slot;
         this.alU = slot3;
         this.tq = false;
         this.mm = i;
      }
   }

   private int c(ItemStack itemstack) {
      return this.d(itemstack);
   }

   private int d(ItemStack itemstack) {
      try {
         int i = Math.max(1, itemstack.getCount());
         List list = this.i(itemstack);
         String s = String.join(" ", list);
         return this.e(s, i);
      } catch (Throwable throwable) {
         return -1;
      }
   }

   private int e(String s, int i) {
      if (s != null && !s.isEmpty()) {
         String s1 = s.toLowerCase();
         Matcher matcher = bD.matcher(s);
         int j = -1;
         long k = -1L;

         while (matcher.find()) {
            int l = matcher.start(1);
            int i1 = matcher.end(1);
            long j1 = this.h(matcher.group(1));
            if (j1 > 0L) {
               long k1 = this.g(s1, i1);
               if (k1 != 1L) {
                  j1 *= k1;
               }

               int l1 = Math.max(0, l - 52);
               int i2 = Math.min(s1.length(), i1 + 52);
               String s2 = s1.substring(l1, i2);
               if (this.f(s2)) {
                  boolean flag = s2.contains("за шт")
                     || s2.contains("/шт")
                     || s2.contains("шт.")
                     || s2.contains(" per ")
                     || s2.contains(" each ")
                     || s2.contains("за 1");
                  boolean flag1 = s2.contains("всего") || s2.contains("итого") || s2.contains("total") || s2.contains("сумм") || s2.contains("общ");
                  int j2 = 3;
                  if (flag1) {
                     j2 += 3;
                  }

                  if (flag) {
                     j2++;
                  }

                  long k2 = flag ? j1 * i : j1;
                  if (k2 > 0L && (j2 > j || j2 == j && k2 > k)) {
                     j = j2;
                     k = k2;
                  }
               }
            }
         }

         if (k <= 0L) {
            return -1;
         } else {
            return k > 2147483647L ? Integer.MAX_VALUE : (int)k;
         }
      } else {
         return -1;
      }
   }

   private boolean f(String s) {
      return s.contains("цена")
         || s.contains("price")
         || s.contains("стоим")
         || s.contains("руб")
         || s.contains("монет")
         || s.contains("coins")
         || s.contains("коин")
         || s.contains("buy")
         || s.contains("куп")
         || s.contains("$")
         || s.contains("₽");
   }

   private long g(String s, int i) {
      if (i >= s.length()) {
         return 1L;
      } else {
         char c0 = s.charAt(i);
         char c1 = i + 1 < s.length() ? s.charAt(i + 1) : 0;
         if (c0 != 'k' && c0 != 1082) {
            return c0 != 'm' && c0 != 1084 ? 1L : 1000000L;
         } else {
            return c1 != 'k' && c1 != 1082 ? 1000L : 1000000L;
         }
      }
   }

   private long h(String s) {
      long i = 0L;

      for (int j = 0; j < s.length(); j++) {
         char c0 = s.charAt(j);
         if (c0 >= '0' && c0 <= '9') {
            i = i * 10L + (c0 - '0');
         }
      }

      return i;
   }

   private List<String> i(ItemStack itemstack) {
      ArrayList arraylist = this.j(itemstack);
      if (arraylist != null && !arraylist.isEmpty()) {
         arraylist.replaceAll(this::p);
         return arraylist;
      } else {
         ArrayList arraylist1 = new ArrayList(2);
         arraylist1.add(this.o(itemstack));
         arraylist1.replaceAll(this::p);
         return arraylist1;
      }
   }

   private ArrayList<String> j(ItemStack itemstack) {
      try {
         List list = itemstack.getTooltip(TooltipContext.DEFAULT, this.getClient().player, TooltipType.BASIC);
         if (list != null && !list.isEmpty()) {
            ArrayList arraylist = new ArrayList(list.size());

            for (Text text : list) {
               if (text != null) {
                  arraylist.add(text.getString());
               }
            }

            if (!arraylist.isEmpty()) {
               return arraylist;
            }
         }
      } catch (Throwable throwable1) {
      }

      try {
         if (!DS) {
            this.k(itemstack);
         }

         if (auK != null && aay != null) {
            Object[] aobject = new Object[aay.length];

            for (int i = 0; i < aay.length; i++) {
               aobject[i] = aay[i].e(this.getClient().player);
            }

            if (auK.invoke(itemstack, aobject) instanceof List list1) {
               ArrayList arraylist1 = new ArrayList(list1.size());

               for (Object object : list1) {
                  if (object instanceof Text text1) {
                     arraylist1.add(text1.getString());
                  } else if (object != null) {
                     arraylist1.add(String.valueOf(object));
                  }
               }

               return arraylist1;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } catch (Throwable throwable) {
         return null;
      }
   }

   private void k(ItemStack itemstack) {
      DS = true;
      if (itemstack != null) {
         Method[] amethod = itemstack.getClass().getMethods();

         for (Method method : amethod) {
            if (List.class.isAssignableFrom(method.getReturnType())) {
               Class[] aclass = method.getParameterTypes();
               ne[] ane = this.l(aclass);
               if (ane != null) {
                  try {
                     Object[] aobject = new Object[ane.length];

                     for (int i = 0; i < ane.length; i++) {
                        aobject[i] = ane[i].e(this.getClient().player);
                     }

                     Object object = method.invoke(itemstack, aobject);
                     if (object instanceof List) {
                        auK = method;
                        aay = ane;
                        return;
                     }
                  } catch (Throwable throwable) {
                  }
               }
            }
         }

         auK = null;
         aay = null;
      }
   }

   private ne[] l(Class<?>[] aclass) {
      if (aclass == null) {
         return null;
      } else {
         ne[] ane = new ne[aclass.length];

         for (int i = 0; i < aclass.length; i++) {
            Class oclass = aclass[i];
            if (oclass == null) {
               return null;
            }

            if (this.getClient().player != null && oclass.isAssignableFrom(this.getClient().player.getClass())) {
               ane[i] = ne.b();
            } else if (oclass == boolean.class || oclass == Boolean.class) {
               ane[i] = ne.c();
            } else if (oclass == int.class || oclass == Integer.class) {
               ane[i] = ne.d();
            } else if (oclass.isEnum()) {
               Object object = this.m(oclass, "NORMAL", "DEFAULT", "BASIC", "REGULAR");
               ane[i] = ne.a(object);
            } else {
               Object object1 = this.n(oclass, "DEFAULT", "NORMAL", "BASIC", "REGULAR", "STANDARD");
               if (object1 != null) {
                  ane[i] = ne.a(object1);
               } else {
                  ane[i] = ne.a(null);
               }
            }
         }

         return ane;
      }
   }

   private Object m(Class<?> oclass, String... astring) {
      try {
         Object[] aobject = oclass.getEnumConstants();
         if (aobject != null && aobject.length != 0) {
            for (String s : astring) {
               if (s != null) {
                  for (Object object : aobject) {
                     if (object != null && s.equalsIgnoreCase(String.valueOf(object))) {
                        return object;
                     }
                  }
               }
            }

            return aobject[0];
         } else {
            return null;
         }
      } catch (Throwable throwable) {
         return null;
      }
   }

   private Object n(Class<?> oclass, String... astring) {
      try {
         for (String s : astring) {
            try {
               Field field = oclass.getField(s);
               if (oclass.isAssignableFrom(field.getType())) {
                  return field.get(null);
               }
            } catch (Throwable throwable) {
            }
         }
      } catch (Throwable throwable1) {
      }

      return null;
   }

   private String o(ItemStack itemstack) {
      try {
         return itemstack.getName().getString();
      } catch (Throwable throwable) {
         return "";
      }
   }

   private String p(String s) {
      if (s != null && !s.isEmpty()) {
         Formatting.strip(s);
         return Formatting.strip(s);
      } else {
         return "";
      }
   }

   private int q(Color color) {
      long i = System.currentTimeMillis();
      float f = (float)(i % aaN) / (float)aaN;
      float f1 = 0.5F - 0.5F * MathHelper.cos(f * (float) (Math.PI * 2));
      float f2 = MathHelper.clamp(ce + (ahK - ce) * f1, 0.0F, 1.0F);
      int j = MathHelper.clamp((int)(color.getAlpha() * f2), 0, 255);
      return j << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
   }

   private void r(DrawContext drawcontext, int i, int j, Slot slot, int k) {
      int l = i + slot.x;
      int i1 = j + slot.y;
      drawcontext.fill(l, i1, l + 16, i1 + 16, k);
   }

   private boolean s(GenericContainerScreen genericcontainerscreen) {
      String s = genericcontainerscreen.getTitle() == null ? "" : genericcontainerscreen.getTitle().getString();
      String s1 = s.toLowerCase();
      return s1.contains("аукцион") || s1.contains("поиск") || s1.contains("auction") || s1.contains("Маркет");
   }

   private void u() {
      this.arW = null;
      this.alU = null;
      this.RE = -1;
      this.mm = 0L;
      this.tq = false;
   }

   private int[] v(GenericContainerScreen genericcontainerscreen) {
      int i = (genericcontainerscreen.width - 176) / 2;
      int j = (genericcontainerscreen.height - 166) / 2;

      try {
         if (!nB) {
            this.w(genericcontainerscreen);
         }

         if (Em != null && WX != null) {
            int k = Em.getInt(genericcontainerscreen);
            int l = WX.getInt(genericcontainerscreen);
            if (!this.x(genericcontainerscreen, k, l)) {
               return new int[]{i, j};
            }

            return new int[]{k, l};
         }
      } catch (Throwable throwable) {
      }

      return new int[]{i, j};
   }

   private void w(GenericContainerScreen genericcontainerscreen) {
      nB = true;

      for (Class oclass = genericcontainerscreen.getClass(); oclass != null && oclass != Object.class; oclass = oclass.getSuperclass()) {
         try {
            UnsafeFieldAccessor unsafefieldaccessor = new UnsafeFieldAccessor(genericcontainerscreen, oclass, "x");
            UnsafeFieldAccessor unsafefieldaccessor1 = new UnsafeFieldAccessor(genericcontainerscreen, oclass, "y");
            int i = unsafefieldaccessor.getInt(genericcontainerscreen);
            int j = unsafefieldaccessor1.getInt(genericcontainerscreen);
            if (this.x(genericcontainerscreen, i, j)) {
               Em = unsafefieldaccessor;
               WX = unsafefieldaccessor1;
               return;
            }
         } catch (Throwable throwable2) {
         }

         try {
            UnsafeFieldAccessor unsafefieldaccessor2 = new UnsafeFieldAccessor(genericcontainerscreen, oclass, 23);
            UnsafeFieldAccessor unsafefieldaccessor4 = new UnsafeFieldAccessor(genericcontainerscreen, oclass, 24);
            int k = unsafefieldaccessor2.getInt(genericcontainerscreen);
            int i1 = unsafefieldaccessor4.getInt(genericcontainerscreen);
            if (this.x(genericcontainerscreen, k, i1)) {
               Em = unsafefieldaccessor2;
               WX = unsafefieldaccessor4;
               return;
            }
         } catch (Throwable throwable1) {
         }

         try {
            UnsafeFieldAccessor unsafefieldaccessor3 = new UnsafeFieldAccessor(genericcontainerscreen, oclass, 23);
            UnsafeFieldAccessor unsafefieldaccessor5 = new UnsafeFieldAccessor(genericcontainerscreen, oclass, 23);
            int l = unsafefieldaccessor3.getInt(genericcontainerscreen);
            int j1 = unsafefieldaccessor5.getInt(genericcontainerscreen);
            if (this.x(genericcontainerscreen, l, j1)) {
               Em = unsafefieldaccessor3;
               WX = unsafefieldaccessor5;
               return;
            }
         } catch (Throwable throwable) {
         }
      }
   }

   private boolean x(GenericContainerScreen genericcontainerscreen, int i, int j) {
      if (i < -64 || j < -64) {
         return false;
      } else {
         return i > genericcontainerscreen.width || j > genericcontainerscreen.height
            ? false
            : i != 0 || j != 0 || genericcontainerscreen.width <= 176 || genericcontainerscreen.height <= 166;
      }
   }
}
