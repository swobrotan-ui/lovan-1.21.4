package module;

import command.ABCommand;
import data.ABItem;
import enum.Category;
import event.ChatMessageEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import setting.ActionKeySetting;
import setting.SliderSetting;

public class ABRaidmineModule extends Module {
   private SliderSetting refreshDelaySetting = new SliderSetting("Задержка обновления", "Задержка между обновлениями (в мс)", 1000.0, 500.0, 5000.0, 100.0);
   private SliderSetting buyDelaySetting = new SliderSetting("Задержка покупки", "Задержка перед кликом на подтверждение (в мс)", 300.0, 100.0, 1000.0, 50.0);
   private SliderSetting scanDelaySetting = new SliderSetting(
      "Задержка сканирования", "Задержка после обновления перед сканированием (в мс)", 600.0, 300.0, 1500.0, 50.0
   );
   private ActionKeySetting viewKeySetting = new ActionKeySetting("Клавиша просмотра", "Наведи на предмет и нажми для просмотра названия", 66, () -> {
   });
   private ScheduledExecutorService Xe;
   private boolean Nv = false;
   private boolean KQ = false;
   private boolean YQ = false;
   private pm axj;
   private ox kZ;
   private ABCommand abO;
   private static final int fN = 0;
   private static final int axC = 44;

   public ABRaidmineModule() {
      super("АБ Raidmine", "Автоматическая покупка предметов на аукционе", Category.PLAYER);
      this.addSettings(this.refreshDelaySetting, this.scanDelaySetting, this.buyDelaySetting, this.viewKeySetting);
      this.axj = new pm();
      this.kZ = new ox(this);
      this.abO = new ABCommand(this);
   }

   @Override
   public void onEnable() {
      if (this.isNotInWorld()) {
         this.setEnabled(false);
      } else {
         this.Nv = true;
         this.KQ = false;
         if (this.axj == null || this.axj.c() == null) {
            this.axj = new pm();
         }

         int i = this.axj.c().size();
         this.sendChatMessage("§7Загружено предметов: §f" + i);
         this.sendChatMessage("§7Диапазон сканирования: слоты 0-44");
         this.Xe = Executors.newSingleThreadScheduledExecutor();
         this.d();
         this.Xe.scheduleAtFixedRate(() -> {
            if (this.Nv && !this.isNotInWorld()) {
               this.getClient().execute(this::f);
            }
         }, this.refreshDelaySetting.getLongValue(), this.refreshDelaySetting.getLongValue(), TimeUnit.MILLISECONDS);
      }
   }

   @Override
   public void onDisable() {
      this.Nv = false;
      this.KQ = false;
      this.YQ = false;
      if (this.Xe != null && !this.Xe.isShutdown()) {
         this.Xe.shutdown();

         try {
            if (!this.Xe.awaitTermination(1L, TimeUnit.SECONDS)) {
               this.Xe.shutdownNow();
            }

            return;
         } catch (InterruptedException interruptedexception) {
            this.Xe.shutdownNow();
            Thread.currentThread().interrupt();
         }
      }
   }

   @Override
   public void onChatMessage(ChatMessageEvent chatmessageevent) {
      this.abO.handleMessage(chatmessageevent);
   }

   @Override
   public void onEndTick() {
      if (!this.isNotInWorld()) {
         int i = this.viewKeySetting.getKeyCode();
         boolean flag = this.a(i);
         if (flag && !this.YQ) {
            this.b();
         }

         this.YQ = flag;
      }
   }

   private boolean a(int i) {
      long j = this.getClient().getWindow().getHandle();
      return GLFW.glfwGetKey(j, i) == 1;
   }

   private void b() {
      if (!this.isNotInWorld()) {
         if (this.getScreen() instanceof HandledScreen handledscreen) {
            try {
               Field field = HandledScreen.class.getDeclaredField("focusedSlot");
               field.setAccessible(true);
               Slot slot = (Slot)field.get(handledscreen);
               if (slot != null && !slot.getStack().isEmpty()) {
                  this.c(slot.getStack());
               } else {
                  this.sendChatMessage("§cНаведите курсор на предмет!");
               }
            } catch (Exception exception) {
               String s = exception.getMessage();
               this.sendChatMessage("§cОшибка: " + s);
            }
         } else {
            this.sendChatMessage("§cОткройте GUI с предметами!");
         }
      }
   }

   private void c(ItemStack itemstack) {
      if (!itemstack.isEmpty()) {
         String s = this.l(itemstack);
         String s1 = itemstack.getItem().toString();
         this.sendChatMessage("§6§l=== ИНФОРМАЦИЯ О ПРЕДМЕТЕ ===");
         this.sendChatMessage("§7ID: §f" + s1);
         this.sendChatMessage("§7Название: §f" + s);
         int i = itemstack.getCount();
         this.sendChatMessage("§7Количество: §f" + i);
      }
   }

   private void d() {
      this.getClient().execute(() -> {
         if ((this.getScreen() == null || !this.e()) && this.getNetworkHandler() != null) {
            this.getNetworkHandler().sendCommand("ah");
            this.sendChatMessage("§7Открываем аукцион...");
         }
      });
   }

   private boolean e() {
      if (!(this.getScreen() instanceof HandledScreen handledscreen)) {
         return false;
      } else {
         ScreenHandler screenhandler = handledscreen.getScreenHandler();

         for (Slot slot : screenhandler.slots) {
            ItemStack itemstack = slot.getStack();
            if (itemstack.getItem() == Items.NETHER_STAR) {
               String s = this.l(itemstack);
               if (s.contains("Обновить") || s.contains("обновить")) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private void f() {
      if (!this.KQ) {
         if (this.getScreen() instanceof HandledScreen handledscreen) {
            ScreenHandler screenhandler = handledscreen.getScreenHandler();
            int i = this.g(screenhandler);
            if (i != -1) {
               this.clickSlot(i);
               this.Xe.schedule(() -> {
                  this.getClient().execute(this::h);
               }, this.scanDelaySetting.getLongValue(), TimeUnit.MILLISECONDS);
            }
         }
      }
   }

   private int g(ScreenHandler screenhandler) {
      for (int i = 0; i < screenhandler.slots.size(); i++) {
         Slot slot = (Slot)screenhandler.slots.get(i);
         ItemStack itemstack = slot.getStack();
         if (itemstack.getItem() == Items.NETHER_STAR) {
            String s = this.l(itemstack);
            if (s.contains("Обновить") || s.contains("обновить")) {
               return slot.id;
            }
         }
      }

      return -1;
   }

   private void h() {
      if (!this.KQ) {
         if (this.getScreen() instanceof HandledScreen handledscreen) {
            ScreenHandler screenhandler = handledscreen.getScreenHandler();
            if (!screenhandler.slots.isEmpty()) {
               ArrayList arraylist = new ArrayList();

               for (int i = 0; i <= Math.min(44, screenhandler.slots.size() - 1); i++) {
                  Slot slot = (Slot)screenhandler.slots.get(i);
                  ItemStack itemstack = slot.getStack();
                  if (!itemstack.isEmpty()
                     && itemstack.getItem() != Items.NETHER_STAR
                     && itemstack.getItem() != Items.BARRIER
                     && itemstack.getItem() != Items.GRAY_STAINED_GLASS_PANE
                     && itemstack.getItem() != Items.BLACK_STAINED_GLASS_PANE) {
                     if (itemstack.contains(DataComponentTypes.CUSTOM_NAME)) {
                        Text text = (Text)itemstack.get(DataComponentTypes.CUSTOM_NAME);
                        if (text != null && this.i(text)) {
                           continue;
                        }
                     }

                     String s4 = this.l(itemstack);
                     String s = this.k(s4);
                     Double d0 = this.m(itemstack);
                     if (d0 != null) {
                        ABItem abitem = this.axj.a(s);
                        if (abitem != null && d0 <= abitem.getMaxPrice()) {
                           arraylist.add(new vv(slot.id, s4, s, d0, abitem.getMaxPrice()));
                        }
                     }
                  }
               }

               if (!arraylist.isEmpty()) {
                  arraylist.sort(Comparator.comparingDouble(vv -> {
                     return vvx.auH / vvx.tp;
                  }));
                  vv vv = (vv)arraylist.get(0);
                  double d1 = (1.0 - vv.auH / vv.tp) * 100.0;
                  this.sendChatMessage("§6§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                  this.sendChatMessage("§a§l  НАЙДЕН ПОДХОДЯЩИЙ ПРЕДМЕТ!");
                  this.sendChatMessage("§6§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                  String s1 = vv.ast;
                  this.sendChatMessage("§7Название: §f" + s1);
                  int j = vv.mK;
                  this.sendChatMessage("§7Слот: §e#" + j);
                  String s2 = this.j(vv.auH);
                  this.sendChatMessage("§7Цена за штуку: §e" + s2);
                  String s3 = this.j(vv.tp);
                  this.sendChatMessage("§7Макс. цена: §e" + s3);
                  this.sendChatMessage(String.format("§7Выгодность: §a%.1f%%", d1));
                  this.sendChatMessage("§6§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                  this.KQ = true;
                  this.n(vv.mK);
               }
            }
         }
      }
   }

   private boolean i(Text text) {
      return text.getStyle().isItalic();
   }

   private String j(double d0) {
      if (d0 >= 1000000.0) {
         return String.format("%.1fM", d0 / 1000000.0);
      } else {
         return d0 >= 1000.0 ? String.format("%.1fK", d0 / 1000.0) : String.format("%.0f", d0);
      }
   }

   private String k(String s) {
      String s1 = s.replaceAll("§.", "");
      return s1.trim();
   }

   private String l(ItemStack itemstack) {
      if (itemstack.contains(DataComponentTypes.CUSTOM_NAME)) {
         Text text = (Text)itemstack.get(DataComponentTypes.CUSTOM_NAME);
         if (text != null) {
            return text.getString();
         }
      }

      return itemstack.getName().getString();
   }

   private Double m(ItemStack itemstack) {
      if (!itemstack.contains(DataComponentTypes.LORE)) {
         return null;
      } else {
         LoreComponent lorecomponent = (LoreComponent)itemstack.get(DataComponentTypes.LORE);
         if (lorecomponent != null && lorecomponent.lines() != null) {
            for (Text text : lorecomponent.lines()) {
               String s = text.getString();
               if (s.contains("Стоимость за штуку:") || s.contains("Стоимость:")) {
                  try {
                     String s1;
                     if (s.contains("Стоимость за штуку:")) {
                        s1 = s.substring(s.indexOf("Стоимость за штуку:") + "Стоимость за штуку:".length()).trim();
                     } else {
                        s1 = s.substring(s.indexOf("Стоимость:") + "Стоимость:".length()).trim();
                     }

                     s1 = s1.replaceAll("[^0-9]", "");
                     if (!s1.isEmpty()) {
                        return Double.parseDouble(s1);
                     }
                  } catch (Exception exception) {
                  }
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   private void n(int i) {
      this.sendChatMessage("§bНачинаем покупку...");
      this.clickSlot(i);
      this.Xe.schedule(() -> {
         this.getClient().execute(this::o);
      }, this.buyDelaySetting.getLongValue(), TimeUnit.MILLISECONDS);
   }

   private void o() {
      if (this.getScreen() instanceof HandledScreen handledscreen) {
         ScreenHandler screenhandler = handledscreen.getScreenHandler();
         int i = this.p(screenhandler);
         if (i != -1) {
            this.clickSlot(i);
            this.sendChatMessage("§a§lПОКУПКА ЗАВЕРШЕНА!");
            this.Xe.schedule(() -> {
               this.getClient().execute(() -> {
                  this.getClient().setScreen(null);
                  this.Xe.schedule(() -> {
                     this.getClient().execute(this::d);
                  }, 500L, TimeUnit.MILLISECONDS);
                  this.KQ = false;
               });
            }, 300L, TimeUnit.MILLISECONDS);
         } else {
            this.sendChatMessage("§cОшибка: кнопка подтверждения не найдена");
            this.getClient().setScreen(null);
            this.Xe.schedule(() -> {
               this.getClient().execute(this::d);
            }, 500L, TimeUnit.MILLISECONDS);
            this.KQ = false;
         }
      } else {
         this.sendChatMessage("§cОшибка: меню подтверждения не открыто");
         this.KQ = false;
      }
   }

   private int p(ScreenHandler screenhandler) {
      for (int i = 0; i < screenhandler.slots.size(); i++) {
         Slot slot = (Slot)screenhandler.slots.get(i);
         ItemStack itemstack = slot.getStack();
         if (itemstack.getItem() == Items.LIME_CONCRETE) {
            String s = this.l(itemstack);
            if (s.contains("Приобрести за") || s.contains("приобрести за")) {
               return slot.id;
            }
         }
      }

      return -1;
   }

   public pm q() {
      return this.axj;
   }

   public void r(pm pm) {
      this.axj = pm;
   }

   public ox s() {
      return this.kZ;
   }
}
