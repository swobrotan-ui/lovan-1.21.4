package hook;

import com.mojang.logging.LogUtils;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.Entry;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.LanServerEntry;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.ScanningEntry;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.ServerEntry;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.AxisGridWidget.DisplayAxis;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.LanServerQueryManager.LanServerDetector;
import net.minecraft.client.network.LanServerQueryManager.LanServerEntryList;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CustomMultiplayerScreen extends MultiplayerScreen {
   public static final int field_41849 = 308;
   public static final int field_41850 = 100;
   public static final int field_41851 = 74;
   public static final int field_41852 = 64;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final MultiplayerServerListPinger serverListPinger = new MultiplayerServerListPinger();
   private final Screen parent;
   private ServerList serverList;
   private ButtonWidget buttonEdit;
   private ButtonWidget buttonJoin;
   private ButtonWidget buttonDelete;
   private ServerInfo selectedEntry;
   private LanServerEntryList lanServers;
   @Nullable
   private LanServerDetector lanServerDetector;
   private boolean initialized;

   public CustomMultiplayerScreen(Screen screen) {
      super(screen);
      this.parent = screen;
   }

   protected void init() {
      if (this.initialized) {
         this.serverListWidget.setDimensionsAndPosition(this.width, this.height - 64 - 32, 0, 32);
      } else {
         this.initialized = true;
         this.serverList = new ServerList(this.client);
         this.serverList.loadFile();
         if (this.lanServers == null) {
            this.lanServers = new LanServerEntryList();
         }

         try {
            this.lanServerDetector = new LanServerDetector(this.lanServers);
            this.lanServerDetector.start();
         } catch (Exception exception) {
            LOGGER.warn("Unable to start LAN server detection: {}", exception.getMessage());
         }

         this.serverListWidget = new MultiplayerServerListWidget(this, this.client, this.width, this.height - 64 - 32, 32, 36);
         this.serverListWidget.setServers(this.serverList);
      }

      this.addDrawableChild(this.serverListWidget);
      this.buttonJoin = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.select"), buttonwidget4 -> {
         this.connect();
      }).width(100).build());
      ButtonWidget buttonwidget = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.direct"), buttonwidget4 -> {
         this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), "", ServerType.OTHER);
         this.client.setScreen(new DirectConnectScreen(this, this::directConnect, this.selectedEntry));
      }).width(100).build());
      ButtonWidget buttonwidget1 = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.add"), buttonwidget4 -> {
         this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), "", ServerType.OTHER);
         this.client.setScreen(new AddServerScreen(this, this::addEntry, this.selectedEntry));
      }).width(100).build());
      this.buttonEdit = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.edit"), buttonwidget4 -> {
         Entry entry = (Entry)this.serverListWidget.getSelectedOrNull();
         if (entry instanceof ServerEntry) {
            ServerInfo serverinfo = ((ServerEntry)entry).getServer();
            this.selectedEntry = new ServerInfo(serverinfo.name, serverinfo.address, ServerType.OTHER);
            this.selectedEntry.copyWithSettingsFrom(serverinfo);
            this.client.setScreen(new AddServerScreen(this, this::editEntry, this.selectedEntry));
         }
      }).width(74).build());
      this.buttonDelete = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.delete"), buttonwidget4 -> {
         Entry entry = (Entry)this.serverListWidget.getSelectedOrNull();
         if (entry instanceof ServerEntry) {
            String s = ((ServerEntry)entry).getServer().name;
            if (s != null) {
               MutableText mutabletext = Text.translatable("selectServer.deleteQuestion");
               MutableText mutabletext1 = Text.translatable("selectServer.deleteWarning", new Object[]{s});
               MutableText mutabletext2 = Text.translatable("selectServer.deleteButton");
               Text text = ScreenTexts.CANCEL;
               this.client.setScreen(new ConfirmScreen(this::removeEntry, mutabletext, mutabletext1, mutabletext2, text));
            }
         }
      }).width(74).build());
      ButtonWidget buttonwidget2 = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.refresh"), buttonwidget4 -> {
         this.refresh();
      }).width(74).build());
      ButtonWidget buttonwidget3 = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, buttonwidget4 -> {
         this.close();
      }).width(74).build());
      DirectionalLayoutWidget directionallayoutwidget = DirectionalLayoutWidget.vertical();
      AxisGridWidget axisgridwidget = (AxisGridWidget)directionallayoutwidget.add(new AxisGridWidget(308, 20, DisplayAxis.HORIZONTAL));
      axisgridwidget.add(this.buttonJoin);
      axisgridwidget.add(buttonwidget);
      axisgridwidget.add(buttonwidget1);
      directionallayoutwidget.add(EmptyWidget.ofHeight(4));
      AxisGridWidget axisgridwidget1 = (AxisGridWidget)directionallayoutwidget.add(new AxisGridWidget(308, 20, DisplayAxis.HORIZONTAL));
      axisgridwidget1.add(this.buttonEdit);
      axisgridwidget1.add(this.buttonDelete);
      axisgridwidget1.add(buttonwidget2);
      axisgridwidget1.add(buttonwidget3);
      directionallayoutwidget.refreshPositions();
      SimplePositioningWidget.setPos(directionallayoutwidget, 0, this.height - 64, this.width, 64);
      this.updateButtonActivationStates();
   }

   public void close() {
      this.client.setScreen(this.parent);
   }

   public void tick() {
      if (this.lanServers != null) {
         List list = this.lanServers.getEntriesIfUpdated();
         if (list != null) {
            this.serverListWidget.setLanServers(list);
         }
      }

      this.serverListPinger.tick();
   }

   public void removed() {
      if (this.lanServerDetector != null) {
         this.lanServerDetector.interrupt();
         this.lanServerDetector = null;
      }

      this.serverListPinger.cancel();
      this.serverListWidget.onRemoved();
   }

   private void refresh() {
      this.client.setScreen(new CustomMultiplayerScreen(this.parent));
   }

   private void removeEntry(boolean flag) {
      Entry entry = (Entry)this.serverListWidget.getSelectedOrNull();
      if (flag && entry instanceof ServerEntry) {
         this.serverList.remove(((ServerEntry)entry).getServer());
         this.serverList.saveFile();
         this.serverListWidget.setSelected(null);
         this.serverListWidget.setServers(this.serverList);
      }

      this.client.setScreen(this);
   }

   private void editEntry(boolean flag) {
      Entry entry = (Entry)this.serverListWidget.getSelectedOrNull();
      if (flag && entry instanceof ServerEntry) {
         ServerInfo serverinfo = ((ServerEntry)entry).getServer();
         serverinfo.name = this.selectedEntry.name;
         serverinfo.address = this.selectedEntry.address;
         serverinfo.copyWithSettingsFrom(this.selectedEntry);
         this.serverList.saveFile();
         this.serverListWidget.setServers(this.serverList);
      }

      this.client.setScreen(this);
   }

   private void addEntry(boolean flag) {
      if (flag) {
         ServerInfo serverinfo = this.serverList.tryUnhide(this.selectedEntry.address);
         if (serverinfo != null) {
            serverinfo.copyFrom(this.selectedEntry);
            this.serverList.saveFile();
         } else {
            this.serverList.add(this.selectedEntry, false);
            this.serverList.saveFile();
         }

         this.serverListWidget.setSelected(null);
         this.serverListWidget.setServers(this.serverList);
      }

      this.client.setScreen(this);
   }

   private void directConnect(boolean flag) {
      if (flag) {
         ServerInfo serverinfo = this.serverList.get(this.selectedEntry.address);
         if (serverinfo == null) {
            this.serverList.add(this.selectedEntry, true);
            this.serverList.saveFile();
            this.connect(this.selectedEntry);
         } else {
            this.connect(serverinfo);
         }
      } else {
         this.client.setScreen(this);
      }
   }

   public boolean keyPressed(int i, int j, int k) {
      if (this.shouldCloseOnEsc() && i == 256) {
         this.close();
         return true;
      } else if (i == 294) {
         this.refresh();
         return true;
      } else if (this.serverListWidget == null || this.serverListWidget.getSelectedOrNull() == null) {
         return false;
      } else if (KeyCodes.isToggle(i)) {
         this.connect();
         return true;
      } else {
         return this.serverListWidget.keyPressed(i, j, k);
      }
   }

   public void render(DrawContext drawcontext, int i, int j, float f) {
      super.render(drawcontext, i, j, f);
      drawcontext.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
   }

   public void connect() {
      Entry entry = (Entry)this.serverListWidget.getSelectedOrNull();
      if (entry instanceof ServerEntry) {
         this.connect(((ServerEntry)entry).getServer());
      } else {
         if (entry instanceof LanServerEntry) {
            LanServerInfo lanserverinfo = ((LanServerEntry)entry).getLanServerEntry();
            this.connect(new ServerInfo(lanserverinfo.getMotd(), lanserverinfo.getAddressPort(), ServerType.LAN));
         }
      }
   }

   private void connect(ServerInfo serverinfo) {
      aeu.connect(this, this.client, ServerAddress.parse(serverinfo.address), serverinfo, false, null);
   }

   public void select(Entry entry) {
      this.serverListWidget.setSelected(entry);
      this.updateButtonActivationStates();
   }

   protected void updateButtonActivationStates() {
      this.buttonJoin.active = false;
      this.buttonEdit.active = false;
      this.buttonDelete.active = false;
      Entry entry = (Entry)this.serverListWidget.getSelectedOrNull();
      if (entry != null && !(entry instanceof ScanningEntry)) {
         this.buttonJoin.active = true;
         if (entry instanceof ServerEntry) {
            this.buttonEdit.active = true;
            this.buttonDelete.active = true;
         }
      }
   }

   public MultiplayerServerListPinger getServerListPinger() {
      return this.serverListPinger;
   }

   public ServerList getServerList() {
      return this.serverList;
   }
}
