package module;

import core.FriendManager;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModuleBase {
   public static long NANOS_PER_SECOND = 1000000000L;
   public Identifier glowTexture = Identifier.of("minecraft", "textures/glow.png");
   public Identifier glowPolygonTexture = Identifier.of("minecraft", "textures/glow_polygon.png");
   public Identifier glowEllipseTexture = Identifier.of("minecraft", "textures/glow_ellipse.png");
   public Identifier glowSquareTexture = Identifier.of("minecraft", "textures/glow_square.png");
   public Identifier glowStarTexture = Identifier.of("minecraft", "textures/glow_star.png");

   public boolean isFriend(String s) {
      return FriendManager.getInstance().isFriend(s);
   }

   public boolean isFriendPlayer(PlayerEntity playerentity) {
      return FriendManager.getInstance().isFriendPlayer(playerentity);
   }

   public boolean isFriendLiving(LivingEntity livingentity) {
      return FriendManager.getInstance().isFriendLiving(livingentity);
   }

   public MinecraftClient getClient() {
      return MinecraftClient.getInstance();
   }

   public PlayerEntity getPlayer() {
      return this.getClient().player;
   }

   protected PlayerInventory getInventory() {
      PlayerEntity playerentity = this.getPlayer();
      return playerentity != null ? playerentity.getInventory() : null;
   }

   protected ClientPlayNetworkHandler getNetworkHandler() {
      return this.getClient().getNetworkHandler();
   }

   protected Screen getScreen() {
      return this.getClient().currentScreen;
   }

   protected GameOptions getOptions() {
      return this.getClient().options;
   }

   protected ClientPlayerInteractionManager getInteractionManager() {
      return this.getClient().interactionManager;
   }

   protected ClientPlayerEntity getClientPlayer() {
      return this.getClient().player;
   }

   protected GameRenderer getGameRenderer() {
      return this.getClient().gameRenderer;
   }

   public World getWorld() {
      return this.getClient().world;
   }

   protected ClientWorld getClientWorld() {
      return this.getClient().world;
   }

   protected ServerWorld getServerWorld() {
      return this.getClient().getServer() != null
         ? this.getClient().getServer().getWorld(Objects.<World>requireNonNull(this.getWorld()).getRegistryKey())
         : null;
   }

   protected boolean hasPlayer() {
      return this.getPlayer() != null;
   }

   protected boolean hasPlayerAndWorld() {
      return this.getPlayer() != null && this.getWorld() != null;
   }

   protected boolean isNotInWorld() {
      return !this.hasPlayerAndWorld();
   }

   protected void clickSlot(int i) {
      ClientPlayerInteractionManager clientplayerinteractionmanager = this.getInteractionManager();
      PlayerEntity playerentity = this.getPlayer();
      if (clientplayerinteractionmanager != null && playerentity != null) {
         clientplayerinteractionmanager.clickSlot(playerentity.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, playerentity);
      }
   }

   protected void interactItem(Hand hand) {
      ClientPlayerInteractionManager clientplayerinteractionmanager = this.getInteractionManager();
      PlayerEntity playerentity = this.getPlayer();
      if (clientplayerinteractionmanager != null && playerentity != null) {
         clientplayerinteractionmanager.interactItem(playerentity, hand);
      }
   }

   protected void swapSlots(int i, int j) {
      this.clickSlot(i < 9 ? i + 36 : i);
      this.clickSlot(j < 9 ? j + 36 : j);
      this.clickSlot(i < 9 ? i + 36 : i);
   }

   protected void equipArmor(int i) {
      this.clickSlot(i < 9 ? i + 36 : i);
      this.clickSlot(6);
      this.clickSlot(i < 9 ? i + 36 : i);
   }

   protected Hand getMainHand() {
      return Hand.MAIN_HAND;
   }

   public void sendChatMessage(String s) {
      if (this.hasPlayerAndWorld()) {
         this.getPlayer().sendMessage(Text.of(s), false);
      }
   }
}
