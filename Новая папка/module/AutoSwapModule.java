package module;

import enum.Category;
import event.MouseMoveEvent;
import font.MSDFFont;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult.Fail;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import render.BuiltRectangle;
import render.BuiltText;
import render.TextCache;
import setting.ActionKeySetting;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.SliderSetting;

public class AutoSwapModule extends Module {
   private static int hr = 8;
   private static int UN = 3;
   private static float aeT = 30.0F;
   private static float ic = 400.0F;
   private static float sw = 92.0F;
   private static float MW = 54.0F;
   private static long Lm = 300L;
   private ActionKeySetting offhandSwapBindSetting = new ActionKeySetting("Бинд свапа в левую руку", "Кнопка для открытия колеса свапа (offhand)", -1);
   private ActionKeySetting useBindSetting = new ActionKeySetting("Бинд на юз", "Кнопка для открытия колеса активации", -1);
   private ActionKeySetting mainHandSwapBindSetting = new ActionKeySetting("Бинд свапа в основную руку", "Кнопка для свапа в руку (main hand)", -1);
   private SliderSetting slotsSetting = new SliderSetting("Ячейки", "Количество ячеек в колесе", 3.0, UN, hr, 1.0, "", 0);
   private BooleanSetting openInventorySetting = new BooleanSetting("Открывать инвентарь", "Открывать инвентарь во время свапа", true);
   private SliderSetting closeInventoryDelaySetting = new SliderSetting(
      "Делей закрытия инв.", "Задержка перед закрытием инвентаря (в тиках)", 1.0, 0.0, 5.0, 1.0
   );
   private SliderSetting swapDelaySetting = new SliderSetting("Делей свапа", "Задержка между действиями (в тиках)", 1.0, 0.0, 5.0, 1.0);
   private ColorSetting slotColorSetting = new ColorSetting("Цвет ячейки", "Цвет сегмента колеса", new Color(204, 204, 204, 94), true);
   private ColorSetting hoverColorSetting = new ColorSetting("Цвет при наводке", "Цвет подсветки ячейки (hover)", new Color(255, 255, 255, 255), true);
   private List<ItemStack> lI = new ArrayList<ItemStack>(hr);
   private List<ItemStack> afE = new ArrayList<ItemStack>(hr);
   private List<ItemStack> aaz = new ArrayList<ItemStack>(hr);
   private final MSDFFont BO = MSDFFont.g().b("bb").c("bb").e();
   private je UH = je.apC;
   private int JD = -1;
   private je Lc = je.apC;
   private int HQ = -1;
   private long KH = 0L;
   private boolean sK = false;
   private boolean atm = false;
   private boolean Mu = false;
   private boolean gW = false;
   private int eS = 0;
   private float Nw = 0.0F;
   private float Oh = 0.0F;
   private float gA = 0.0F;
   private float YO = 0.0F;
   private long azX = 0L;
   private float aeg = 0.0F;
   private float arE = 0.0F;
   private float ie = 0.0F;
   private lg SW = lg.tw;
   private int Ja = 0;
   private int aka = 0;
   private int lk = -1;
   private int Me = -1;
   private int arp = -1;
   private ItemStack Ql = ItemStack.EMPTY;
   private boolean auj = false;

   public AutoSwapModule() {
      super("АутоЗшап", "Автоматическая смена предметов.", Category.PLAYER);
      this.a();
      this.addSettings(
         this.offhandSwapBindSetting,
         this.useBindSetting,
         this.mainHandSwapBindSetting,
         this.slotsSetting,
         this.openInventorySetting,
         this.closeInventoryDelaySetting,
         this.swapDelaySetting,
         this.slotColorSetting,
         this.hoverColorSetting
      );
   }

   private void a() {
      for (int i = 0; i < hr; i++) {
         this.lI.add(ItemStack.EMPTY);
         this.afE.add(ItemStack.EMPTY);
         this.aaz.add(ItemStack.EMPTY);
      }
   }

   @Override
   public void onEnable() {
      this.b();
   }

   @Override
   public void onDisable() {
      this.b();
   }

   private void b() {
      this.UH = je.apC;
      this.JD = -1;
      this.Lc = je.apC;
      this.HQ = -1;
      this.KH = 0L;
      this.sK = false;
      this.atm = false;
      this.Mu = false;
      this.gW = false;
      this.eS = 0;
      this.Nw = 0.0F;
      this.Oh = 0.0F;
      this.gA = 0.0F;
      this.YO = 0.0F;
      this.aeg = 0.0F;
      this.arE = 0.0F;
      this.ie = 0.0F;
      this.azX = System.nanoTime();
      this.c();
   }

   private void c() {
      this.SW = lg.tw;
      this.Ja = 0;
      this.aka = 0;
      this.lk = -1;
      this.Me = -1;
      this.arp = -1;
      this.Ql = ItemStack.EMPTY;
      this.auj = false;
   }

   private boolean d() {
      return this.SW != lg.tw;
   }

   @Override
   public void onMouseScroll(MouseMoveEvent mousemoveevent) {
      if (this.A()) {
         float f = 0.15F;
         this.YO = this.YO - (float)mousemoveevent.getYOffset() * f;
         mousemoveevent.setCancelled(true);
      }
   }

   @Override
   public void onStartTick() {
      if (!this.isNotInWorld()) {
         if (this.SW != lg.tw) {
            if (this.aka > 0) {
               this.aka--;
            } else {
               switch (this.SW) {
                  case KK:
                     this.e();
                     return;
                  case Km:
                     this.f();
                     return;
                  case hU:
                     this.h();
                     return;
                  case ZG:
                     this.i();
                     return;
                  case uB:
                     this.o();
               }
            }
         }
      }
   }

   @Override
   public void onEndTick() {
      if (this.getClient().currentScreen == null) {
         float f = this.getClient().getWindow().getScaledWidth() / 2.0F;
         float f1 = this.getClient().getWindow().getScaledHeight() / 2.0F;
         this.w(f, f1);
         this.t(f, f1);
      }
   }

   private void e() {
      switch (this.Ja) {
         case 1:
            if (this.openInventorySetting.getValue() && !this.auj) {
               this.getClient().setScreen(new InventoryScreen(this.getPlayer()));
            }

            this.p(this.swapDelaySetting.getIntValue());
            return;
         case 2:
            this.D(this.lk);
            if (this.arp != -1) {
               if (!this.Ql.isEmpty() && this.Ql.getItem() != Items.AIR) {
                  this.Z(this.lI, this.arp, this.Ql);
               } else {
                  this.Z(this.lI, this.arp, ItemStack.EMPTY);
               }
            }

            this.p(this.closeInventoryDelaySetting.getIntValue());
            return;
         case 3:
            if (this.openInventorySetting.getValue() && !this.auj && this.getScreen() instanceof InventoryScreen) {
               this.getScreen().close();
            }

            this.c();
      }
   }

   private void f() {
      switch (this.Ja) {
         case 1:
            int i = this.lk - 36;
            this.getInventory().selectedSlot = i;
            if (this.arp != -1 && !this.Ql.isEmpty() && this.Ql.getItem() != Items.AIR) {
               this.Z(this.aaz, this.arp, this.Ql);
            }

            this.c();
      }
   }

   private void h() {
      switch (this.Ja) {
         case 1:
            if (this.openInventorySetting.getValue() && !this.auj) {
               this.getClient().setScreen(new InventoryScreen(this.getPlayer()));
            }

            this.p(this.swapDelaySetting.getIntValue());
            return;
         case 2:
            this.E(this.lk, this.Me);
            if (this.arp != -1 && !this.Ql.isEmpty() && this.Ql.getItem() != Items.AIR) {
               this.Z(this.aaz, this.arp, this.Ql);
            }

            this.p(this.closeInventoryDelaySetting.getIntValue());
            return;
         case 3:
            if (this.openInventorySetting.getValue() && !this.auj && this.getScreen() instanceof InventoryScreen) {
               this.getScreen().close();
            }

            this.c();
      }
   }

   private void i() {
      switch (this.Ja) {
         case 1:
            this.getInventory().selectedSlot = this.lk - 36;
            this.p(this.swapDelaySetting.getIntValue());
            return;
         case 2:
            this.K();
            this.p(this.swapDelaySetting.getIntValue());
            return;
         case 3:
            this.getInventory().selectedSlot = this.Me;
            this.c();
      }
   }

   private void o() {
      switch (this.Ja) {
         case 1:
            if (!(this.getScreen() instanceof InventoryScreen)) {
               this.getClient().setScreen(new InventoryScreen(this.getPlayer()));
            }

            this.p(this.swapDelaySetting.getIntValue());
            return;
         case 2:
            this.E(this.lk, this.Me);
            this.p(this.closeInventoryDelaySetting.getIntValue());
            return;
         case 3:
            if (this.getScreen() instanceof InventoryScreen) {
               this.getScreen().close();
            }

            this.p(this.swapDelaySetting.getIntValue());
            return;
         case 4:
            this.K();
            this.p(this.swapDelaySetting.getIntValue());
            return;
         case 5:
            if (!this.auj) {
               this.getClient().setScreen(new InventoryScreen(this.getPlayer()));
            }

            this.p(this.swapDelaySetting.getIntValue());
            return;
         case 6:
            this.E(this.lk, this.Me);
            this.p(this.closeInventoryDelaySetting.getIntValue());
            return;
         case 7:
            if (!this.auj && this.getScreen() instanceof InventoryScreen) {
               this.getScreen().close();
            }

            this.c();
      }
   }

   private void p(int i) {
      this.Ja++;
      this.aka = i;
   }

   private void q(Slot slot) {
      if (!this.d() && slot != null && this.getInteractionManager() != null && this.getPlayer() != null) {
         this.SW = lg.KK;
         this.Ja = 1;
         this.aka = 0;
         this.lk = slot.id;
         this.arp = this.J(this.lI, slot.getStack());
         this.Ql = this.getPlayer().getOffHandStack().copy();
         this.auj = this.getScreen() instanceof InventoryScreen;
      }
   }

   private void r(Slot slot) {
      if (!this.d() && slot != null && this.getInteractionManager() != null && this.getPlayer() != null) {
         this.arp = this.J(this.aaz, slot.getStack());
         this.Ql = this.getPlayer().getMainHandStack().copy();
         this.auj = this.getScreen() instanceof InventoryScreen;
         this.lk = slot.id;
         this.Me = this.getInventory().selectedSlot;
         this.Ja = 1;
         this.aka = 0;
         if (this.ah(slot.id)) {
            this.SW = lg.Km;
         } else {
            this.SW = lg.hU;
         }
      }
   }

   private void s(ItemStack itemstack) {
      if (!this.d() && this.getInteractionManager() != null && this.getPlayer() != null) {
         Slot slot = this.F(itemstack);
         if (slot != null) {
            this.lk = slot.id;
            this.Me = this.getInventory().selectedSlot;
            this.auj = this.getScreen() instanceof InventoryScreen;
            this.Ja = 1;
            this.aka = 0;
            if (this.ah(slot.id)) {
               this.SW = lg.ZG;
            } else {
               this.SW = lg.uB;
            }
         }
      }
   }

   private void t(float f, float f1) {
      boolean flag = this.offhandSwapBindSetting.isPressed();
      boolean flag1 = this.useBindSetting.isPressed();
      boolean flag2 = this.mainHandSwapBindSetting.isPressed();
      boolean flag3 = this.ag();
      boolean flag4 = this.sK;
      boolean flag5 = this.atm;
      boolean flag6 = this.Mu;
      boolean flag7 = this.gW;
      this.sK = flag;
      this.atm = flag1;
      this.Mu = flag2;
      this.gW = flag3;
      if (this.A()) {
         this.eS++;
         this.ai();
      }

      if (this.A() && this.eS > 2 && flag3 && !flag7) {
         this.x(f, f1);
      } else {
         this.u(flag, flag4, je.wx, this.lI);
         this.u(flag1, flag5, je.Gt, this.afE);
         this.u(flag2, flag6, je.nx, this.aaz);
      }
   }

   private void u(boolean flag, boolean flag1, je je, List<ItemStack> list) {
      if (flag && !flag1 && this.UH == je.apC && this.JD == -1 && !this.d()) {
         this.y(je);
      } else {
         if (!flag && flag1 && this.UH == je) {
            int i = this.C();
            if (i == -1) {
               this.z();
               return;
            }

            ItemStack itemstack = this.Y(list, i);
            if (itemstack.isEmpty() || itemstack.getItem() == Items.AIR) {
               this.B(i, je);
               return;
            }

            this.v(je, i, itemstack);
            this.z();
         }
      }
   }

   private void v(je je, int i, ItemStack itemstack) {
      Slot slot = this.F(itemstack);
      if (slot != null) {
         List list = this.W(je);
         if (i >= 0 && i < list.size()) {
            this.Z(list, i, slot.getStack());
         }

         switch (je) {
            case wx:
               this.q(slot);
               return;
            case Gt:
               this.s(itemstack);
               return;
            case nx:
               this.r(slot);
         }
      }
   }

   private void w(float f, float f1) {
      float f2 = this.ae();
      float f3 = this.af();
      float f4 = this.ad(f2, f3, f, f1);
      if (f4 > 5.0F) {
         this.Nw = f2;
         this.Oh = f3;
      }
   }

   private void x(float f, float f1) {
      int i = this.ab(this.Nw, this.Oh, f, f1, this.aa(), this.gA);
      if (i != -1) {
         List list = this.W(this.UH);
         ItemStack itemstack = this.Y(list, i);
         if (!itemstack.isEmpty() && itemstack.getItem() != Items.AIR) {
            this.Z(list, i, ItemStack.EMPTY);
            this.ak(i);
         }
      }
   }

   private void y(je je) {
      this.UH = je;
      this.eS = 0;

      float f = switch (je) {
         case wx -> this.aeg;
         case Gt -> this.arE;
         case nx -> this.ie;
         default -> 0.0F;
      };
      this.gA = f;
      this.YO = f;
      this.azX = System.nanoTime();
      this.ai();
   }

   private void z() {
      switch (this.UH) {
         case wx:
            this.aeg = this.gA;
            break;
         case Gt:
            this.arE = this.gA;
            break;
         case nx:
            this.ie = this.gA;
      }

      this.UH = je.apC;
      this.gA = 0.0F;
      this.YO = 0.0F;
      this.aj();
   }

   private boolean A() {
      return this.UH != je.apC;
   }

   private void B(int i, je je) {
      this.JD = i;
      this.Lc = je;
      this.UH = je.apC;
      this.getClient().execute(() -> {
         try {
            this.getClient().setScreen(new oy(Objects.<PlayerEntity>requireNonNull(this.getPlayer())));
         } catch (Exception exception) {
            this.JD = -1;
            this.Lc = je.apC;
         }
      });
   }

   private int C() {
      if (!this.A()) {
         return -1;
      } else {
         float f = this.getClient().getWindow().getScaledWidth() / 2.0F;
         float f1 = this.getClient().getWindow().getScaledHeight() / 2.0F;
         return this.ab(this.ae(), this.af(), f, f1, this.aa(), this.gA);
      }
   }

   private void D(int i) {
      if (this.getInteractionManager() != null && this.getPlayer() != null) {
         int j = this.getPlayer().currentScreenHandler.syncId;
         this.getInteractionManager().clickSlot(j, i, 40, SlotActionType.SWAP, this.getPlayer());
      }
   }

   private void E(int i, int j) {
      if (this.getInteractionManager() != null && this.getPlayer() != null) {
         if (j >= 0 && j <= 8) {
            int k = this.getPlayer().currentScreenHandler.syncId;
            this.getInteractionManager().clickSlot(k, i, j, SlotActionType.SWAP, this.getPlayer());
         }
      }
   }

   private Slot F(ItemStack itemstack) {
      if (itemstack != null && !itemstack.isEmpty() && this.getPlayer() != null) {
         DefaultedList defaultedlist = this.getPlayer().currentScreenHandler.slots;
         Slot slot = this.G(defaultedlist, itemstack, 36, 44, true);
         if (slot != null) {
            return slot;
         } else {
            slot = this.G(defaultedlist, itemstack, 9, 35, true);
            if (slot != null) {
               return slot;
            } else {
               slot = this.G(defaultedlist, itemstack, 36, 44, false);
               return slot != null ? slot : this.G(defaultedlist, itemstack, 9, 35, false);
            }
         }
      } else {
         return null;
      }
   }

   private Slot G(List<Slot> list, ItemStack itemstack, int i, int j, boolean flag) {
      for (Slot slot : list) {
         if (slot.id >= i && slot.id <= j) {
            boolean flag1 = flag ? this.H(itemstack, slot.getStack()) : this.I(itemstack, slot.getStack());
            if (flag1) {
               return slot;
            }
         }
      }

      return null;
   }

   private boolean H(ItemStack itemstack, ItemStack itemstack1) {
      return itemstack != null && itemstack1 != null && !itemstack.isEmpty() && !itemstack1.isEmpty()
         ? ItemStack.areItemsAndComponentsEqual(itemstack, itemstack1)
         : false;
   }

   private boolean I(ItemStack itemstack, ItemStack itemstack1) {
      if (itemstack != null && itemstack1 != null && !itemstack.isEmpty() && !itemstack1.isEmpty()) {
         if (itemstack.getItem() != itemstack1.getItem()) {
            return false;
         } else {
            return ItemStack.areItemsAndComponentsEqual(itemstack, itemstack1)
               ? true
               : itemstack.getName().getString().equals(itemstack1.getName().getString());
         }
      } else {
         return false;
      }
   }

   private int J(List<ItemStack> list, ItemStack itemstack) {
      for (int i = 0; i < list.size(); i++) {
         if (this.H((ItemStack)list.get(i), itemstack)) {
            return i;
         }
      }

      return -1;
   }

   private void K() {
      if (this.getInteractionManager() != null && this.getPlayer() != null && this.getClient() != null) {
         for (Hand hand : Hand.values()) {
            ItemStack itemstack = this.getPlayer().getStackInHand(hand);
            HitResult hitresult = this.getClient().crosshairTarget;
            if (hitresult != null && this.L(hitresult, hand)) {
               return;
            }

            if (!itemstack.isEmpty() && this.getInteractionManager().interactItem(this.getPlayer(), hand) instanceof Success success) {
               if (success.swingSource() == SwingSource.CLIENT) {
                  this.getPlayer().swingHand(hand);
               }

               return;
            }
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean L(HitResult hitresult, Hand hand) {
      switch (hitresult.getType()) {
         case ENTITY:
            EntityHitResult entityhitresult = (EntityHitResult)hitresult;
            ActionResult actionresult1 = this.getInteractionManager()
               .interactEntityAtLocation(this.getPlayer(), entityhitresult.getEntity(), entityhitresult, hand);
            if (!actionresult1.isAccepted()) {
               actionresult1 = this.getInteractionManager().interactEntity(this.getPlayer(), entityhitresult.getEntity(), hand);
            }

            if (actionresult1 instanceof Success success1) {
               if (success1.swingSource() == SwingSource.CLIENT) {
                  this.getPlayer().swingHand(hand);
               }

               return true;
            }
            break;
         case BLOCK:
            BlockHitResult blockhitresult = (BlockHitResult)hitresult;
            ActionResult actionresult = this.getInteractionManager().interactBlock(this.getClientPlayer(), hand, blockhitresult);
            if (actionresult instanceof Success success) {
               if (success.swingSource() == SwingSource.CLIENT) {
                  this.getPlayer().swingHand(hand);
               }

               return true;
            }

            if (actionresult instanceof Fail) {
               return true;
            }
      }

      return false;
   }

   public void M(DrawContext drawcontext) {
      if (!this.isNotInWorld() && (this.A() || this.JD != -1)) {
         if (this.A()) {
            this.R();
         }

         int i = this.aa();
         float f = drawcontext.getScaledWindowWidth() / 2.0F;
         float f1 = drawcontext.getScaledWindowHeight() / 2.0F;
         float f2 = this.ae();
         float f3 = this.af();
         int j = this.ab(f2, f3, f, f1, i, this.gA);
         List list = this.X();
         Matrix4f matrix4f = drawcontext.getMatrices().peek().getPositionMatrix();
         pel pel = this.N(j);
         BuiltRectangle.k(
            matrix4f,
            f,
            f1,
            MW,
            sw,
            i,
            pel.axf,
            this.JD,
            pel.PS,
            pel.Oi,
            pel.Tf,
            this.slotColorSetting != null ? this.slotColorSetting.getColor() : null,
            this.hoverColorSetting != null ? this.hoverColorSetting.getColor() : null,
            1.0F,
            this.gA
         );
         ItemStack itemstack = this.getPlayer().getOffHandStack();
         this.O(drawcontext, list, i, f, f1, itemstack, matrix4f);
         if (j != -1 && j < i && this.A()) {
            ItemStack itemstack1 = this.Y(list, j);
            if (!itemstack1.isEmpty() && itemstack1.getItem() != Items.AIR && !this.H(itemstack1, itemstack)) {
               this.Q(drawcontext, itemstack1, (int)f2, (int)f3);
            }
         }

         if (this.A()) {
            this.P(matrix4f, f, f1);
         }
      }
   }

   private pel N(int i) {
      long j = System.currentTimeMillis();
      float f = 0.0F;
      int k = -1;
      boolean flag = false;
      if (this.HQ != -1 && j <= this.KH) {
         k = this.HQ;
         f = (float)(j - (this.KH - Lm)) / (float)Lm;
      }

      if (i != -1 && this.ag()) {
         List list = this.X();
         ItemStack itemstack = this.Y(list, i);
         if (!itemstack.isEmpty() && itemstack.getItem() != Items.AIR) {
            k = i;
            f = 0.5F;
            flag = true;
         }
      }

      return new pel(i, k, f, flag);
   }

   private void O(DrawContext drawcontext, List<ItemStack> list, int i, float f, float f1, ItemStack itemstack, Matrix4f matrix4f) {
      for (int j = 0; j < i; j++) {
         ItemStack itemstack1 = this.Y(list, j);
         if (!itemstack1.isEmpty() && itemstack1.getItem() != Items.AIR && (this.UH != je.wx || !this.H(itemstack1, itemstack))) {
            float f2 = this.ac(j, i);
            float f3 = (MW + sw) / 2.0F;
            float f4 = f + (float)Math.cos(f2) * f3;
            float f5 = f1 + (float)Math.sin(f2) * f3;
            drawcontext.drawItem(itemstack1, (int)(f4 - 8.0F), (int)(f5 - 8.0F));
            String s = itemstack1.getName().getString();
            float f6 = 5.5F;
            BuiltText builttext = TextCache.a(this.BO, s, f6, Color.WHITE);
            float f7 = this.BO.c(s, f6);
            builttext.render(matrix4f, f4 - f7 / 2.0F, f5 + 12.0F);
         }
      }
   }

   private void P(Matrix4f matrix4f, float f, float f1) {
      String s = "ПКМ по ячейке - удалить предмет";
      BuiltText builttext = TextCache.a(this.BO, s, 8.0F, new Color(200, 200, 200, 255));
      float f2 = this.BO.c(s, 8.0F);
      builttext.render(matrix4f, f - f2 / 2.0F + 4.0F, f1 - 110.0F);
   }

   private void Q(DrawContext drawcontext, ItemStack itemstack, int i, int j) {
      try {
         List list = itemstack.getTooltip(
            TooltipContext.DEFAULT, this.getClient().player, this.getClient().options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC
         );
         if (list != null && !list.isEmpty()) {
            drawcontext.drawTooltip(this.getClient().textRenderer, list, i, j);
         }
      } catch (Exception exception) {
      }
   }

   private void R() {
      long i = System.nanoTime();
      if (this.azX == 0L) {
         this.azX = i;
      } else {
         float f = (float)(i - this.azX) / (float)NANOS_PER_SECOND;
         this.azX = i;
         if (f > 0.1F) {
            f = 0.1F;
         }

         float f1 = this.YO - this.gA;
         if (Math.abs(f1) > 0.001F) {
            this.gA += f1 * f * 12.0F;
         } else {
            this.gA = this.YO;
         }
      }
   }

   public void S(int i, SlotActionType slotactiontype) {
      if (!this.isNotInWorld() && this.JD != -1 && this.getClient().currentScreen instanceof oy && slotactiontype == SlotActionType.PICKUP) {
         DefaultedList defaultedlist = this.getPlayer().currentScreenHandler.slots;
         if (i >= 0 && i < defaultedlist.size()) {
            Slot slot = (Slot)defaultedlist.get(i);
            ItemStack itemstack = slot.getStack();
            if (itemstack != null && !itemstack.isEmpty() && itemstack.getItem() != Items.AIR) {
               List list = this.W(this.Lc);
               this.Z(list, this.JD, itemstack.copy());
               this.JD = -1;
               this.Lc = je.apC;
               this.getClient().setScreen(null);
               this.getClient().mouse.lockCursor();
            }
         }
      }
   }

   public boolean T() {
      return this.JD != -1;
   }

   public void U() {
      this.JD = -1;
   }

   public void V() {
      Collections.fill(this.lI, ItemStack.EMPTY);
   }

   private List<ItemStack> W(je je) {
      switch (je) {
         case Gt:
            return this.afE;
         case nx:
            return this.aaz;
         default:
            return this.lI;
      }
   }

   private List<ItemStack> X() {
      return this.JD != -1 ? this.W(this.Lc) : this.W(this.UH);
   }

   private ItemStack Y(List<ItemStack> list, int i) {
      if (i >= 0 && i < list.size()) {
         ItemStack itemstack = (ItemStack)list.get(i);
         return itemstack == null ? ItemStack.EMPTY : itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   private void Z(List<ItemStack> list, int i, ItemStack itemstack) {
      if (i >= 0 && i < list.size()) {
         list.set(i, itemstack.copy());
      }
   }

   private int aa() {
      return MathHelper.clamp(Math.round(this.slotsSetting.getFloatValue()), UN, hr);
   }

   private int ab(float f, float f1, float f2, float f3, int i, float f4) {
      float f5 = this.ad(f, f1, f2, f3);
      if (!(f5 < aeT) && !(f5 > ic)) {
         double d0 = Math.atan2(f1 - f3, f - f2) + (Math.PI / 2) - f4;

         while (d0 < 0.0) {
            d0 += Math.PI * 2;
         }

         while (d0 >= Math.PI * 2) {
            d0 -= Math.PI * 2;
         }

         int j = (int)Math.floor(d0 / (Math.PI * 2) * i);
         return j >= 0 && j < i ? j : -1;
      } else {
         return -1;
      }
   }

   private float ac(int i, int j) {
      float f = (float)((-Math.PI / 2) + (Math.PI * 2) * ((double)i / j)) + this.gA;
      float f1 = (float)((-Math.PI / 2) + (Math.PI * 2) * ((i + 1.0) / j)) + this.gA;
      return (f + f1) / 2.0F;
   }

   private float ad(float f, float f1, float f2, float f3) {
      float f4 = f - f2;
      float f5 = f1 - f3;
      return (float)Math.sqrt(f4 * f4 + f5 * f5);
   }

   private float ae() {
      return (float)(this.getClient().mouse.getX() * this.getClient().getWindow().getScaledWidth() / this.getClient().getWindow().getWidth());
   }

   private float af() {
      return (float)(this.getClient().mouse.getY() * this.getClient().getWindow().getScaledHeight() / this.getClient().getWindow().getHeight());
   }

   private boolean ag() {
      return GLFW.glfwGetMouseButton(this.getClient().getWindow().getHandle(), 1) == 1;
   }

   private boolean ah(int i) {
      return i >= 36 && i <= 44;
   }

   private void ai() {
      if (this.getClient().mouse.isCursorLocked()) {
         this.getClient().mouse.unlockCursor();
      }
   }

   private void aj() {
      if (!this.getClient().mouse.isCursorLocked()) {
         this.getClient().mouse.lockCursor();
      }
   }

   private void ak(int i) {
      this.HQ = i;
      this.KH = System.currentTimeMillis() + Lm;
   }

   public List<ItemStack> al() {
      return this.lI;
   }

   public List<ItemStack> am() {
      return this.afE;
   }

   public List<ItemStack> an() {
      return this.aaz;
   }
}
