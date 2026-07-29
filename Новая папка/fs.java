import java.net.InetSocketAddress;
import java.util.Optional;
import net.CustomClientConnection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ResourcePackPolicy;
import net.minecraft.client.resource.server.ServerResourcePackManager.AcceptanceStatus;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.state.LoginStates;
import net.minecraft.text.Text;

class fs extends Thread {
   // $VF: synthetic field
   final ServerAddress val$address;
   // $VF: synthetic field
   final MinecraftClient val$client;
   // $VF: synthetic field
   final ServerInfo val$info;
   // $VF: synthetic field
   final CookieStorage val$cookieStorage;
   // $VF: synthetic field
   final aeu this$0;

   fs(aeu aeu, String s, ServerAddress serveraddress, MinecraftClient minecraftclient, ServerInfo serverinfo, CookieStorage cookiestorage) {
      super(s);
      this.this$0 = aeu;
      this.val$address = serveraddress;
      this.val$client = minecraftclient;
      this.val$info = serverinfo;
      this.val$cookieStorage = cookiestorage;
   }

   @Override
   public void run() {
      InetSocketAddress inetsocketaddress = null;

      try {
         if (!this.this$0.connectingCancelled) {
            Optional optional = AllowedAddressResolver.DEFAULT.resolve(this.val$address).map(Address::getInetSocketAddress);
            if (!this.this$0.connectingCancelled) {
               if (optional.isEmpty()) {
                  this.val$client.execute(() -> {
                     minecraftclient.setScreen(new DisconnectedScreen(this.this$0.parent, this.this$0.failureErrorMessage, aeu.UNKNOWN_HOST_TEXT));
                  });
               } else {
                  inetsocketaddress = (InetSocketAddress)optional.get();
                  CustomClientConnection customclientconnection;
                  synchronized (this.this$0) {
                     if (this.this$0.connectingCancelled) {
                        return;
                     }

                     customclientconnection = new CustomClientConnection(NetworkSide.CLIENTBOUND);
                     customclientconnection.resetPacketSizeLog(this.val$client.getDebugHud().getPacketSizeLog());
                     this.this$0.future = ClientConnection.connect(
                        inetsocketaddress, this.val$client.options.shouldUseNativeTransport(), customclientconnection
                     );
                  }

                  this.this$0.future.syncUninterruptibly();
                  synchronized (this.this$0) {
                     if (this.this$0.connectingCancelled) {
                        customclientconnection.disconnect(aeu.ABORTED_TEXT);
                        return;
                     }

                     this.this$0.connection = customclientconnection;
                     this.val$client.getServerResourcePackProvider().init(customclientconnection, toAcceptanceStatus(this.val$info.getResourcePackPolicy()));
                  }

                  this.this$0
                     .connection
                     .connect(
                        inetsocketaddress.getHostName(),
                        inetsocketaddress.getPort(),
                        LoginStates.C2S,
                        LoginStates.S2C,
                        new ClientLoginNetworkHandler(
                           this.this$0.connection,
                           this.val$client,
                           this.val$info,
                           this.this$0.parent,
                           false,
                           null,
                           this.this$0::setStatus,
                           this.val$cookieStorage
                        ),
                        this.val$cookieStorage != null
                     );
                  this.this$0
                     .connection
                     .send(new LoginHelloC2SPacket(this.val$client.getSession().getUsername(), this.val$client.getSession().getUuidOrNull()));
               }
            }
         }
      } catch (Exception exception2) {
         if (!this.this$0.connectingCancelled) {
            Exception exception;
            if (exception2.getCause() instanceof Exception exception1) {
               exception = exception1;
            } else {
               exception = exception2;
            }

            aeu.LOGGER.error("Couldn't connect to server", exception2);
            String s2;
            if (inetsocketaddress == null) {
               s2 = exception.getMessage();
            } else {
               s2 = exception.getMessage();
               int i = inetsocketaddress.getPort();
               String s = inetsocketaddress.getHostName();
               s2 = s2.replaceAll(s + ":" + i, "").replaceAll(inetsocketaddress.toString(), "");
            }

            String s1 = s2;
            this.val$client
               .execute(
                  () -> {
                     minecraftclient.setScreen(
                        new DisconnectedScreen(
                           this.this$0.parent, this.this$0.failureErrorMessage, Text.translatable("disconnect.genericReason", new Object[]{s1})
                        )
                     );
                  }
               );
         }
      }
   }

   private static AcceptanceStatus toAcceptanceStatus(ResourcePackPolicy resourcepackpolicy) {
      switch (resourcepackpolicy) {
         case ENABLED:
            return AcceptanceStatus.ALLOWED;
         case DISABLED:
            return AcceptanceStatus.DECLINED;
         case PROMPT:
            return AcceptanceStatus.PENDING;
         default:
            throw new MatchException(null, null);
      }
   }
}
