import com.google.common.base.Suppliers;
import core.ClientMain;
import font.MSDFFont;
import gui.GuiConstants;
import java.util.function.Supplier;
import module.AutoSwapModule;
import module.ClickGuiModule;
import module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.client.gui.screen.recipebook.AbstractCraftingRecipeBookWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import render.RectangleCache;
import render.Size;
import render.TextCache;

public class oy extends RecipeBookScreen<PlayerScreenHandler> {
   private float mouseX;
   private float mouseY;
   private boolean mouseDown;
   private final StatusEffectsDisplay statusEffectsDisplay;
   private static final float BUTTON_HEIGHT = 25.0F;
   private static final float BUTTON_Y_OFFSET = 30.0F;
   private static final Supplier<MSDFFont> UNBOUNDED_FONT = Suppliers.memoize(() -> {
      return MSDFFont.g().b("bb").c("bb").e();
   });
   protected final MSDFFont unboundedFont = UNBOUNDED_FONT.get();

   public oy(PlayerEntity playerentity) {
      super(
         playerentity.playerScreenHandler,
         new AbstractCraftingRecipeBookWidget(playerentity.playerScreenHandler),
         playerentity.getInventory(),
         Text.translatable("container.crafting")
      );
      this.titleX = 97;
      this.statusEffectsDisplay = new StatusEffectsDisplay(this);
   }

   public void handledScreenTick() {
      super.handledScreenTick();
      if (this.client.interactionManager.hasCreativeInventory()) {
         this.client
            .setScreen(
               new CreativeInventoryScreen(
                  this.client.player, this.client.player.networkHandler.getEnabledFeatures(), (Boolean)this.client.options.getOperatorItemsTab().getValue()
               )
            );
      }
   }

   protected void init() {
      if (this.client.interactionManager.hasCreativeInventory()) {
         this.client
            .setScreen(
               new CreativeInventoryScreen(
                  this.client.player, this.client.player.networkHandler.getEnabledFeatures(), (Boolean)this.client.options.getOperatorItemsTab().getValue()
               )
            );
      } else {
         super.init();
      }
   }

   private void dropAllItems() {
      if (this.client != null && this.client.interactionManager != null && this.handler != null) {
         for (int i = 0; i < ((PlayerScreenHandler)this.handler).slots.size(); i++) {
            if (((PlayerScreenHandler)this.handler).getSlot(i).inventory == this.client.player.getInventory()
               && !((PlayerScreenHandler)this.handler).getSlot(i).getStack().isEmpty()) {
               this.client.interactionManager.clickSlot(((PlayerScreenHandler)this.handler).syncId, i, 1, SlotActionType.THROW, this.client.player);
            }
         }
      }
   }

   protected ScreenPos getRecipeBookButtonPos() {
      return new ScreenPos(this.x + 104, this.height / 2 - 22);
   }

   protected void onRecipeBookToggled() {
      this.mouseDown = true;
   }

   protected void drawForeground(DrawContext drawcontext, int i, int j) {
      drawcontext.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
   }

   public void render(DrawContext drawcontext, int i, int j, float f) {
      super.render(drawcontext, i, j, f);
      this.mouseX = i;
      this.mouseY = j;
      HudModule hudmodule = ClientMain.getInstance().getModuleManager().<HudModule>getModule(HudModule.class);
      if (hudmodule != null && hudmodule.isEnabled() && hudmodule.o().getValue()) {
         this.renderDropAllButton(drawcontext, i, j);
      }
   }

   private void renderDropAllButton(DrawContext drawcontext, int i, int j) {
      Matrix4f matrix4f = drawcontext.getMatrices().peek().getPositionMatrix();
      AutoSwapModule autoswapmodule = ClientMain.getInstance().getModuleManager().<AutoSwapModule>getModule(AutoSwapModule.class);
      String s = autoswapmodule != null && autoswapmodule.T() ? "Выберите предмет для колеса" : "Выкинуть все предметы";
      float f = 6.5F;
      float f1 = 16.0F;
      float f2 = this.unboundedFont.c(s, f);
      float f3 = f2 + f1;
      float f4 = this.x + this.backgroundWidth / 2.0F;
      float f5 = f4 - f3 / 2.0F;
      float f6 = this.y - 30.0F;
      RectangleCache.b(f3, 25.0F, 4.0F).render(matrix4f, f5, f6);
      float f7 = f5 + f1 / 2.0F + 4.0F;
      float f8 = f6 + (25.0F - f) / 2.0F;
      TextCache.a(this.unboundedFont, s, f, GuiConstants.Bz).render(matrix4f, f7, f8);
   }

   public boolean mouseClicked(double d0, double d1, int i) {
      AutoSwapModule autoswapmodule = ClientMain.getInstance().getModuleManager().<AutoSwapModule>getModule(AutoSwapModule.class);
      if (autoswapmodule != null && autoswapmodule.isEnabled() && autoswapmodule.T() && this.focusedSlot != null && i == 0) {
         autoswapmodule.S(this.focusedSlot.id, SlotActionType.PICKUP);
         return true;
      } else {
         if (i == 0) {
            HudModule hudmodule = ClientMain.getInstance().getModuleManager().<HudModule>getModule(HudModule.class);
            if (hudmodule != null && hudmodule.isEnabled() && hudmodule.o().getValue()) {
               String s = autoswapmodule != null && autoswapmodule.T() ? "Выберите предмет для колеса" : "Выкинуть все предметы";
               float f = this.unboundedFont.c(s, 6.5F);
               float f1 = f + 16.0F;
               float f2 = this.x + this.backgroundWidth / 2.0F;
               float f3 = f2 - f1 / 2.0F;
               float f4 = this.y - 30.0F;
               if (d0 >= f3 && d0 <= f3 + f1 && d1 >= f4 && d1 <= f4 + 25.0F) {
                  if (autoswapmodule == null || !autoswapmodule.T()) {
                     this.dropAllItems();
                  }

                  return true;
               }
            }
         }

         return super.mouseClicked(d0, d1, i);
      }
   }

   public void close() {
      AutoSwapModule autoswapmodule = ClientMain.getInstance().getModuleManager().<AutoSwapModule>getModule(AutoSwapModule.class);
      if (autoswapmodule != null && autoswapmodule.T()) {
         autoswapmodule.U();
      }

      super.close();
   }

   public boolean shouldHideStatusEffectHud() {
      return this.statusEffectsDisplay.shouldHideStatusEffectHud();
   }

   protected boolean shouldAddPaddingToGhostResult() {
      return false;
   }

   protected void drawBackground(DrawContext drawcontext, float f, int k, int l) {
      int i = this.x;
      int j = this.y;
      HudModule hudmodule = ClientMain.getInstance().getModuleManager().<HudModule>getModule(HudModule.class);
      if (hudmodule != null && hudmodule.isEnabled() && hudmodule.o().getValue()) {
         drawcontext.getMatrices().push();
         Matrix4f matrix4f = new Matrix4f();
         RectangleCache.b(176.0F, 166.0F, 8.0F).a(matrix4f, this.x, this.y, 0.95F);
         drawcontext.getMatrices().pop();
      } else {
         drawcontext.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
      }

      this.drawEntity(drawcontext, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F, this.mouseX, this.mouseY, this.client.player);
   }

   public void renderBackground(DrawContext drawcontext, int i, int j, float f) {
      HudModule hudmodule = ClientMain.getInstance().getModuleManager().<HudModule>getModule(HudModule.class);
      ClickGuiModule clickguimodule = ClientMain.getInstance().getModuleManager().<ClickGuiModule>getModule(ClickGuiModule.class);
      boolean flag = hudmodule != null && hudmodule.isEnabled() && hudmodule.o().getValue();
      boolean flag1 = !flag || clickguimodule == null || clickguimodule.c().getValue();
      if (flag1) {
         int k = this.client.getWindow().getScaledWidth();
         int l = this.client.getWindow().getScaledHeight();
         Matrix4f matrix4f = drawcontext.getMatrices().peek().getPositionMatrix();
         new hvg().a(new Size((float)k, (float)l)).e(10.0F).a().render(matrix4f, 0.0F, 0.0F);
      }

      this.drawBackground(drawcontext, f, i, j);
   }

   public void drawEntity(DrawContext drawcontext, int i, int j, int k, int l, int i1, float f, float f1, float f2, LivingEntity livingentity) {
      HudModule hudmodule = ClientMain.getInstance().getModuleManager().<HudModule>getModule(HudModule.class);
      if (hudmodule != null && hudmodule.isEnabled() && hudmodule.o().getValue()) {
         drawcontext.getMatrices().push();
         Matrix4f matrix4f = new Matrix4f();
         RectangleCache.b(40.0F, 73.0F, 4.0F).render(matrix4f, this.x + 31, this.y + 6);
         RectangleCache.b(20.0F, 73.0F, 4.0F).render(matrix4f, this.x + 6, this.y + 6);
         RectangleCache.b(40.0F, 40.0F, 4.0F).render(matrix4f, this.x + 95, this.y + 15);
         RectangleCache.b(21.0F, 21.0F, 4.0F).render(matrix4f, this.x + 151.2, this.y + 25);
         RectangleCache.b(21.0F, 21.0F, 4.0F).render(matrix4f, this.x + 74.5, this.y + 59);
         drawcontext.getMatrices().pop();
      }

      float f13 = (i + k) / 2.0F;
      float f3 = (j + l) / 2.0F;
      drawcontext.enableScissor(i, j, k, l);
      float f4 = (float)Math.atan((f13 - f1) / 40.0F);
      float f5 = (float)Math.atan((f3 - f2) / 40.0F);
      Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
      Quaternionf quaternionf1 = new Quaternionf().rotateX(f5 * 20.0F * (float) (Math.PI / 180.0));
      quaternionf.mul(quaternionf1);
      float f6 = livingentity.bodyYaw;
      float f7 = livingentity.getYaw();
      float f8 = livingentity.getPitch();
      float f9 = livingentity.prevHeadYaw;
      float f10 = livingentity.headYaw;
      livingentity.bodyYaw = 180.0F + f4 * 20.0F;
      livingentity.setYaw(180.0F + f4 * 40.0F);
      livingentity.setPitch(-f5 * 20.0F);
      livingentity.headYaw = livingentity.getYaw();
      livingentity.prevHeadYaw = livingentity.getYaw();
      float f11 = livingentity.getScale();
      Vector3f vector3f = new Vector3f(0.0F, livingentity.getHeight() / 2.0F + f * f11, 0.0F);
      float f12 = i1 / f11;
      drawEntity(drawcontext, f13, f3, f12, vector3f, quaternionf, quaternionf1, livingentity);
      livingentity.bodyYaw = f6;
      livingentity.setYaw(f7);
      livingentity.setPitch(f8);
      livingentity.prevHeadYaw = f9;
      livingentity.headYaw = f10;
      drawcontext.disableScissor();
   }

   public static void drawEntity(
      DrawContext drawcontext,
      float f,
      float f1,
      float f2,
      Vector3f vector3f,
      Quaternionf quaternionf,
      @Nullable Quaternionf quaternionf1,
      LivingEntity livingentity
   ) {
      drawcontext.getMatrices().push();
      drawcontext.getMatrices().translate(f, f1, 50.0);
      drawcontext.getMatrices().scale(f2, f2, -f2);
      drawcontext.getMatrices().translate(vector3f.x, vector3f.y, vector3f.z);
      drawcontext.getMatrices().multiply(quaternionf);
      drawcontext.draw();
      DiffuseLighting.method_34742();
      EntityRenderDispatcher entityrenderdispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
      if (quaternionf1 != null) {
         entityrenderdispatcher.setRotation(quaternionf1.conjugate(new Quaternionf()).rotateY((float) Math.PI));
      }

      entityrenderdispatcher.setRenderShadows(false);
      drawcontext.draw(vertexconsumerprovider -> {
         entityrenderdispatcher.render(livingentity, 0.0, 0.0, 0.0, 1.0F, drawcontext.getMatrices(), vertexconsumerprovider, 15728880);
      });
      drawcontext.draw();
      entityrenderdispatcher.setRenderShadows(true);
      drawcontext.getMatrices().pop();
      DiffuseLighting.enableGuiDepthLighting();
   }

   public boolean mouseReleased(double d0, double d1, int i) {
      if (this.mouseDown) {
         this.mouseDown = false;
         return true;
      } else {
         return super.mouseReleased(d0, d1, i);
      }
   }
}
