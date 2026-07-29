package render;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import core.ClientMain;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import module.ChatTweaksModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import net.minecraft.client.gui.hud.MessageIndicator.Icon;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nullables;
import net.minecraft.util.collection.ArrayListDeque;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CustomChatHud extends ChatHud {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_MESSAGES = 100;
   private static final int MISSING_MESSAGE_INDEX = -1;
   private static final int field_39772 = 4;
   private static final int field_39773 = 4;
   private static final int OFFSET_FROM_BOTTOM = 40;
   private static final int REMOVAL_QUEUE_TICKS = 60;
   private static final Text DELETED_MARKER_TEXT = Text.translatable("chat.deleted_marker").formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC});
   private final MinecraftClient client;
   private final ArrayListDeque<String> messageHistory = new ArrayListDeque(100);
   private final List<ChatHudLine> messages = Lists.newArrayList();
   private final List<Visible> visibleMessages = Lists.newArrayList();
   private int scrolledLines;
   private boolean hasUnreadNewMessages;
   private final List<io> removalQueue = new ArrayList<io>();
   private final List<ChatMessageEntry> duplicateGroups = new ArrayList<ChatMessageEntry>();

   public CustomChatHud(MinecraftClient minecraftclient) {
      super(minecraftclient);
      this.client = minecraftclient;
      this.messageHistory.addAll(minecraftclient.getCommandHistoryManager().getHistory());
   }

   public void tickRemovalQueueIfExists() {
      if (!this.removalQueue.isEmpty()) {
         this.tickRemovalQueue();
      }
   }

   public void render(DrawContext drawcontext, int i, int j, int k, boolean flag) {
      if (!this.isChatHidden()) {
         int l = this.getVisibleLineCount();
         int i1 = this.visibleMessages.size();
         if (i1 > 0) {
            Profiler profiler = Profilers.get();
            profiler.push("chat");
            float f = (float)this.getChatScale();
            int j1 = MathHelper.ceil(this.getWidth() / f);
            int k1 = drawcontext.getScaledWindowHeight();
            drawcontext.getMatrices().push();
            drawcontext.getMatrices().scale(f, f, 1.0F);
            drawcontext.getMatrices().translate(4.0F, 0.0F, 0.0F);
            int l1 = MathHelper.floor((k1 - 40) / f);
            int i2 = this.getMessageIndex(this.toChatLineX(j), this.toChatLineY(k));
            double d0 = (Double)this.client.options.getChatOpacity().getValue() * 0.9 + 0.1;
            double d1 = (Double)this.client.options.getTextBackgroundOpacity().getValue();
            double d2 = (Double)this.client.options.getChatLineSpacing().getValue();
            int j2 = this.getLineHeight();
            int k2 = (int)Math.round(-8.0 * (d2 + 1.0) + 4.0 * d2);
            int l2 = 0;

            for (int i4 = 0; i4 + this.scrolledLines < this.visibleMessages.size() && i4 < l; i4++) {
               int j4 = i4 + this.scrolledLines;
               Visible visible = this.visibleMessages.get(j4);
               if (visible != null) {
                  int i3 = i - visible.addedTime();
                  if (i3 < 200 || flag) {
                     double d3 = flag ? 1.0 : getMessageOpacityMultiplier(i3);
                     int j3 = (int)(255.0 * d3 * d0);
                     int k3 = (int)(255.0 * d3 * d1);
                     l2++;
                     if (j3 > 3) {
                        int l3 = l1 - i4 * j2;
                        int l4 = l3 + k2;
                        drawcontext.fill(-4, l3 - j2, 0 + j1 + 4 + 4, l3, k3 << 24);
                        MessageIndicator messageindicator = visible.indicator();
                        if (messageindicator != null) {
                           int i5 = messageindicator.indicatorColor() | j3 << 24;
                           drawcontext.fill(-4, l3 - j2, -2, l3, i5);
                           if (j4 == i2 && messageindicator.icon() != null) {
                              int j5 = this.getIndicatorX(visible);
                              Objects.<TextRenderer>requireNonNull(this.client.textRenderer);
                              int k5 = l4 + 9;
                              this.drawIndicatorIcon(drawcontext, j5, k5, messageindicator.icon());
                           }
                        }

                        drawcontext.getMatrices().push();
                        drawcontext.getMatrices().translate(0.0F, 0.0F, 50.0F);
                        drawcontext.drawTextWithShadow(this.client.textRenderer, visible.content(), 0, l4, ColorHelper.withAlpha(j3, -1));
                        drawcontext.getMatrices().pop();
                     }
                  }
               }
            }

            long i7 = this.client.getMessageHandler().getUnprocessedMessageCount();
            if (i7 > 0L) {
               int j7 = (int)(128.0 * d0);
               int l5 = (int)(255.0 * d1);
               drawcontext.getMatrices().push();
               drawcontext.getMatrices().translate(0.0F, l1, 0.0F);
               drawcontext.fill(-2, 0, j1 + 4, 9, l5 << 24);
               drawcontext.getMatrices().translate(0.0F, 0.0F, 50.0F);
               drawcontext.drawTextWithShadow(this.client.textRenderer, Text.translatable("chat.queue", new Object[]{i7}), 0, 1, 16777215 + (j7 << 24));
               drawcontext.getMatrices().pop();
            }

            if (flag) {
               int k7 = this.getLineHeight();
               int i6 = i1 * k7;
               int l7 = l2 * k7;
               int k4 = this.scrolledLines * l7 / i1 - l1;
               int j6 = l7 * l7 / i6;
               if (i6 != l7) {
                  int k6 = k4 > 0 ? 170 : 96;
                  int i8 = this.hasUnreadNewMessages ? 13382451 : 3355562;
                  int l6 = j1 + 4;
                  drawcontext.fill(l6, -k4, l6 + 2, -k4 - j6, 100, i8 + (k6 << 24));
                  drawcontext.fill(l6 + 2, -k4, l6 + 1, -k4 - j6, 100, 13421772 + (k6 << 24));
               }
            }

            drawcontext.getMatrices().pop();
            profiler.pop();
         }
      }
   }

   private void drawIndicatorIcon(DrawContext drawcontext, int i, int j, Icon icon) {
      int k = j - icon.height - 1;
      icon.draw(drawcontext, i, k);
   }

   private int getIndicatorX(Visible visible) {
      return this.client.textRenderer.getWidth(visible.content()) + 4;
   }

   private boolean isChatHidden() {
      return this.client.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN;
   }

   private static double getMessageOpacityMultiplier(int i) {
      double d0 = i / 200.0;
      d0 = 1.0 - d0;
      d0 *= 10.0;
      d0 = MathHelper.clamp(d0, 0.0, 1.0);
      return d0 * d0;
   }

   public void clear(boolean flag) {
      ChatTweaksModule chattweaksmodule = ClientMain.getInstance().getModuleManager().<ChatTweaksModule>getModule(ChatTweaksModule.class);
      if (chattweaksmodule != null && chattweaksmodule.isEnabled() && chattweaksmodule.b().getValue()) {
         this.client.getMessageHandler().processAll();
      } else {
         this.client.getMessageHandler().processAll();
         this.removalQueue.clear();
         this.visibleMessages.clear();
         this.messages.clear();
         this.duplicateGroups.clear();
         if (flag) {
            this.messageHistory.clear();
            this.messageHistory.addAll(this.client.getCommandHistoryManager().getHistory());
         }
      }
   }

   public void addMessage(Text text) {
      this.addMessage(text, (MessageSignatureData)null, this.client.isConnectedToLocalServer() ? MessageIndicator.singlePlayer() : MessageIndicator.system());
   }

   public void addMessage(Text text, @Nullable MessageSignatureData messagesignaturedata, @Nullable MessageIndicator messageindicator) {
      ChatHudLine chathudline = new ChatHudLine(this.client.inGameHud.getTicks(), text, messagesignaturedata, messageindicator);
      this.logChatMessage(chathudline);
      this.addVisibleMessage(chathudline);
      this.addMessage(chathudline);
   }

   private void logChatMessage(ChatHudLine chathudline) {
      MessageIndicator messageindicator = chathudline.indicator();
      if (messageindicator == null || messageindicator != MessageIndicator.system()) {
         String s = chathudline.content().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
         String s1 = (String)Nullables.map(messageindicator, MessageIndicator::loggedName);
         if (s1 != null) {
            LOGGER.info("[{}] [CHAT] {}", s1, s);
         } else {
            LOGGER.info("[CHAT] {}", s);
         }
      }
   }

   private void addVisibleMessage(ChatHudLine chathudline) {
      ChatTweaksModule chattweaksmodule = ClientMain.getInstance().getModuleManager().<ChatTweaksModule>getModule(ChatTweaksModule.class);
      boolean flag = chattweaksmodule != null && chattweaksmodule.isEnabled() && chattweaksmodule.a().getValue();
      int i = MathHelper.floor(this.getWidth() / this.getChatScale());
      Icon icon = chathudline.getIcon();
      if (icon != null) {
         i -= icon.width + 4 + 2;
      }

      List list = ChatMessages.breakRenderedChatMessageLines(chathudline.content(), i, this.client.textRenderer);
      boolean flag1 = this.isChatFocused();

      for (int j = 0; j < list.size(); j++) {
         OrderedText orderedtext = (OrderedText)list.get(j);
         if (flag1 && this.scrolledLines > 0) {
            this.hasUnreadNewMessages = true;
            this.scroll(1);
         }

         boolean flag2 = j == list.size() - 1;
         this.visibleMessages.add(0, new Visible(chathudline.creationTick(), orderedtext, chathudline.indicator(), flag2));
      }

      if (!flag) {
         while (this.visibleMessages.size() > 100) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
         }
      }
   }

   private void addMessage(ChatHudLine chathudline) {
      ChatTweaksModule chattweaksmodule = ClientMain.getInstance().getModuleManager().<ChatTweaksModule>getModule(ChatTweaksModule.class);
      boolean flag = chattweaksmodule != null && chattweaksmodule.isEnabled() && chattweaksmodule.a().getValue();
      boolean flag1 = chattweaksmodule != null && chattweaksmodule.isEnabled() && chattweaksmodule.d().getValue();
      if (flag1) {
         String s = chathudline.content().getString();

         for (int i = 0; i < this.duplicateGroups.size(); i++) {
            ChatMessageEntry chatmessageentry = this.duplicateGroups.get(i);
            if (chatmessageentry.getMessageContent().equals(s) && i < 5) {
               this.messages.removeIf(chathudline2 -> {
                  return chathudline2.creationTick() == chatmessageentry.getCreationTick();
               });
               chatmessageentry.increment();
               chatmessageentry.updateTick(chathudline.creationTick());
               int j = chatmessageentry.getCount();
               MutableText mutabletext = Text.literal(s + " §7(x" + j + ")");
               ChatHudLine chathudline1 = new ChatHudLine(chathudline.creationTick(), mutabletext, chathudline.signature(), chathudline.indicator());
               this.messages.add(0, chathudline1);
               this.refresh();
               return;
            }
         }

         this.duplicateGroups.add(0, new ChatMessageEntry(s, chathudline.creationTick(), chathudline.signature(), chathudline.indicator()));

         while (this.duplicateGroups.size() > 100) {
            this.duplicateGroups.remove(this.duplicateGroups.size() - 1);
         }
      }

      this.messages.add(0, chathudline);
      if (!flag) {
         while (this.messages.size() > 100) {
            this.messages.remove(this.messages.size() - 1);
         }
      }
   }

   private void tickRemovalQueue() {
      int i = this.client.inGameHud.getTicks();
      this.removalQueue.removeIf(io -> {
         return i >= io.deletableAfter() ? this.queueForRemoval(io.signature()) == null : false;
      });
   }

   public void removeMessage(MessageSignatureData messagesignaturedata) {
      io io = this.queueForRemoval(messagesignaturedata);
      if (io != null) {
         this.removalQueue.add(io);
      }
   }

   @Nullable
   private io queueForRemoval(MessageSignatureData messagesignaturedata) {
      int i = this.client.inGameHud.getTicks();
      ListIterator listiterator = this.messages.listIterator();

      while (listiterator.hasNext()) {
         ChatHudLine chathudline = (ChatHudLine)listiterator.next();
         if (messagesignaturedata.equals(chathudline.signature())) {
            int j = chathudline.creationTick() + 60;
            if (i >= j) {
               listiterator.set(this.createRemovalMarker(chathudline));
               this.refresh();
               return null;
            }

            return new io(messagesignaturedata, j);
         }
      }

      return null;
   }

   private ChatHudLine createRemovalMarker(ChatHudLine chathudline) {
      return new ChatHudLine(chathudline.creationTick(), DELETED_MARKER_TEXT, (MessageSignatureData)null, MessageIndicator.system());
   }

   public void reset() {
      this.resetScroll();
      this.refresh();
   }

   private void refresh() {
      this.visibleMessages.clear();

      for (ChatHudLine chathudline : Lists.reverse(this.messages)) {
         this.addVisibleMessage(chathudline);
      }
   }

   public ArrayListDeque<String> getMessageHistory() {
      return this.messageHistory;
   }

   public void addToMessageHistory(String s) {
      ChatTweaksModule chattweaksmodule = ClientMain.getInstance().getModuleManager().<ChatTweaksModule>getModule(ChatTweaksModule.class);
      boolean flag = chattweaksmodule != null && chattweaksmodule.isEnabled() && chattweaksmodule.a().getValue();
      if (!s.equals(this.messageHistory.peekLast())) {
         if (!flag && this.messageHistory.size() >= 100) {
            this.messageHistory.removeFirst();
         }

         this.messageHistory.addLast(s);
      }

      if (s.startsWith("/")) {
         this.client.getCommandHistoryManager().add(s);
      }
   }

   public void resetScroll() {
      this.scrolledLines = 0;
      this.hasUnreadNewMessages = false;
   }

   public void scroll(int i) {
      this.scrolledLines += i;
      int j = this.visibleMessages.size();
      if (this.scrolledLines > j - this.getVisibleLineCount()) {
         this.scrolledLines = j - this.getVisibleLineCount();
      }

      if (this.scrolledLines <= 0) {
         this.scrolledLines = 0;
         this.hasUnreadNewMessages = false;
      }
   }

   public boolean mouseClicked(double d0, double d1) {
      if (this.isChatFocused() && !this.client.options.hudHidden && !this.isChatHidden()) {
         MessageHandler messagehandler = this.client.getMessageHandler();
         if (messagehandler.getUnprocessedMessageCount() == 0L) {
            return false;
         } else {
            double d2 = d0 - 2.0;
            double d3 = this.client.getWindow().getScaledHeight() - d1 - 40.0;
            if (d2 <= MathHelper.floor(this.getWidth() / this.getChatScale()) && d3 < 0.0 && d3 > MathHelper.floor(-9.0 * this.getChatScale())) {
               messagehandler.process();
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   @Nullable
   public Text getFullMessageAt(double d0, double d1) {
      double d2 = this.toChatLineX(d0);
      double d3 = this.toChatLineY(d1);
      int i = this.getMessageIndex(d2, d3);
      if (i >= 0 && i < this.visibleMessages.size()) {
         Visible visible = this.visibleMessages.get(i);
         int j = visible.addedTime();

         for (ChatHudLine chathudline : this.messages) {
            if (chathudline.creationTick() == j) {
               return chathudline.content();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @Nullable
   public Style getTextStyleAt(double d0, double d1) {
      double d2 = this.toChatLineX(d0);
      double d3 = this.toChatLineY(d1);
      int i = this.getMessageLineIndex(d2, d3);
      if (i >= 0 && i < this.visibleMessages.size()) {
         Visible visible = this.visibleMessages.get(i);
         return this.client.textRenderer.getTextHandler().getStyleAt(visible.content(), MathHelper.floor(d2));
      } else {
         return null;
      }
   }

   @Nullable
   public MessageIndicator getIndicatorAt(double d0, double d1) {
      double d2 = this.toChatLineX(d0);
      double d3 = this.toChatLineY(d1);
      int i = this.getMessageIndex(d2, d3);
      if (i >= 0 && i < this.visibleMessages.size()) {
         Visible visible = this.visibleMessages.get(i);
         MessageIndicator messageindicator = visible.indicator();
         if (messageindicator != null && this.isXInsideIndicatorIcon(d2, visible, messageindicator)) {
            return messageindicator;
         }
      }

      return null;
   }

   private boolean isXInsideIndicatorIcon(double d0, Visible visible, MessageIndicator messageindicator) {
      if (d0 < 0.0) {
         return true;
      } else {
         Icon icon = messageindicator.icon();
         if (icon == null) {
            return false;
         } else {
            int i = this.getIndicatorX(visible);
            int j = i + icon.width;
            return d0 >= i && d0 <= j;
         }
      }
   }

   private double toChatLineX(double d0) {
      return d0 / this.getChatScale() - 4.0;
   }

   private double toChatLineY(double d0) {
      double d1 = this.client.getWindow().getScaledHeight() - d0 - 40.0;
      return d1 / (this.getChatScale() * this.getLineHeight());
   }

   private int getMessageIndex(double d0, double d1) {
      int i = this.getMessageLineIndex(d0, d1);
      if (i == -1) {
         return -1;
      } else {
         while (i >= 0) {
            if (this.visibleMessages.get(i).endOfEntry()) {
               return i;
            }

            i--;
         }

         return i;
      }
   }

   private int getMessageLineIndex(double d0, double d1) {
      if (this.isChatFocused() && !this.isChatHidden()) {
         if (!(d0 < -4.0) && !(d0 > MathHelper.floor(this.getWidth() / this.getChatScale()))) {
            int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
            if (d1 >= 0.0 && d1 < i) {
               int j = MathHelper.floor(d1 + this.scrolledLines);
               if (j >= 0 && j < this.visibleMessages.size()) {
                  return j;
               }
            }

            return -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public boolean isChatFocused() {
      return this.client.currentScreen instanceof ChatScreen;
   }

   public int getWidth() {
      return getWidth((Double)this.client.options.getChatWidth().getValue());
   }

   public int getHeight() {
      return getHeight(
         this.isChatFocused() ? (Double)this.client.options.getChatHeightFocused().getValue() : (Double)this.client.options.getChatHeightUnfocused().getValue()
      );
   }

   public double getChatScale() {
      return (Double)this.client.options.getChatScale().getValue();
   }

   public static int getWidth(double d0) {
      return MathHelper.floor(d0 * 280.0 + 40.0);
   }

   public static int getHeight(double d0) {
      return MathHelper.floor(d0 * 160.0 + 20.0);
   }

   public static double getDefaultUnfocusedHeight() {
      return 70.0 / (getHeight(1.0) - 20);
   }

   public int getVisibleLineCount() {
      return this.getHeight() / this.getLineHeight();
   }

   private int getLineHeight() {
      return (int)(9.0 * ((Double)this.client.options.getChatLineSpacing().getValue() + 1.0));
   }

   public void restoreChatState(ll ll) {
      this.messageHistory.clear();
      this.messageHistory.addAll(ll.messageHistory);
      this.removalQueue.clear();
      this.removalQueue.addAll(ll.removalQueue);
      this.messages.clear();
      this.messages.addAll(ll.messages);
      this.refresh();
   }
}
