import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelFuture;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlay;
import net.minecraft.client.QuickPlayLogger.WorldType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.network.ClientConnection;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class aeu extends Screen {
   private static final AtomicInteger CONNECTOR_THREADS_COUNT = new AtomicInteger(0);
   static final Logger LOGGER = LogUtils.getLogger();
   private static final long NARRATOR_INTERVAL = 2000L;
   public static final Text ABORTED_TEXT = Text.translatable("connect.aborted");
   public static final Text UNKNOWN_HOST_TEXT = Text.translatable("disconnect.genericReason", new Object[]{Text.translatable("disconnect.unknownHost")});
   @Nullable
   volatile ClientConnection connection;
   @Nullable
   ChannelFuture future;
   volatile boolean connectingCancelled;
   final Screen parent;
   private Text status = Text.translatable("connect.connecting");
   private long lastNarrationTime = -1L;
   final Text failureErrorMessage;
   private final vf backgroundRenderer = new vf();

   public aeu(Screen screen, Text text) {
      super(text);
      this.parent = screen;
      this.failureErrorMessage = text;
   }

   public static void connect(
      Screen screen, MinecraftClient minecraftclient, ServerAddress serveraddress, ServerInfo serverinfo, boolean flag, @Nullable CookieStorage cookiestorage
   ) {
      if (minecraftclient.currentScreen instanceof ConnectScreen) {
         LOGGER.error("Attempt to connect while already connecting");
      } else {
         Text text;
         if (cookiestorage != null) {
            text = ScreenTexts.CONNECT_FAILED_TRANSFER;
         } else if (flag) {
            text = QuickPlay.ERROR_TITLE;
         } else {
            text = ScreenTexts.CONNECT_FAILED;
         }

         aeu aeu = new aeu(screen, text);
         if (cookiestorage != null) {
            aeu.setStatus(Text.translatable("connect.transferring"));
         }

         minecraftclient.disconnect();
         minecraftclient.loadBlockList();
         minecraftclient.ensureAbuseReportContext(ReporterEnvironment.ofThirdPartyServer(serverinfo.address));
         minecraftclient.getQuickPlayLogger().setWorld(WorldType.MULTIPLAYER, serverinfo.address, serverinfo.name);
         minecraftclient.setScreen(aeu);
         aeu.connect(minecraftclient, serveraddress, serverinfo, cookiestorage);
      }
   }

   private void connect(MinecraftClient minecraftclient, ServerAddress serveraddress, ServerInfo serverinfo, @Nullable CookieStorage cookiestorage) {
      LOGGER.info("Connecting to {}, {}", serveraddress.getAddress(), serveraddress.getPort());
      int i = CONNECTOR_THREADS_COUNT.incrementAndGet();
      fs fs = new fs(this, "Server Connector #" + i, serveraddress, minecraftclient, serverinfo, cookiestorage);
      fs.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
      fs.start();
   }

   private void setStatus(Text text) {
      this.status = text;
   }

   public void tick() {
      if (this.connection != null) {
         if (this.connection.isOpen()) {
            this.connection.tick();
            return;
         }

         this.connection.handleDisconnection();
      }
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, buttonwidget -> {
         synchronized (this) {
            this.connectingCancelled = true;
            if (this.future != null) {
               this.future.cancel(true);
               this.future = null;
            }

            if (this.connection != null) {
               this.connection.disconnect(ABORTED_TEXT);
            }
         }

         this.client.setScreen(this.parent);
      }).dimensions(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
   }

   public void render(DrawContext drawcontext, int i, int j, float f) {
      this.backgroundRenderer.render(drawcontext, this.width, this.height, f);
      super.render(drawcontext, i, j, f);
      long k = Util.getMeasuringTimeMs();
      if (k - this.lastNarrationTime > 2000L) {
         this.lastNarrationTime = k;
         this.client.getNarratorManager().narrate(Text.translatable("narrator.joining"));
      }

      drawcontext.drawCenteredTextWithShadow(this.textRenderer, this.status, this.width / 2, this.height / 2 - 50, 16777215);
   }

   public void renderBackground(DrawContext drawcontext, int i, int j, float f) {
   }
}
