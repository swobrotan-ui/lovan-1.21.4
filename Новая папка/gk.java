import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class gk implements WorldRenderContext {
   private WorldRenderer worldRenderer;
   private RenderTickCounter tickCounter;
   private boolean blockOutlines;
   private Camera camera;
   private GameRenderer gameRenderer;
   private Matrix4f positionMatrix;
   private Matrix4f projectionMatrix;
   private ClientWorld world;
   private boolean advancedTranslucency;
   private VertexConsumerProvider consumers;
   private Frustum frustum;
   private MatrixStack matrixStack;

   public void prepare(
      WorldRenderer worldrenderer,
      RenderTickCounter rendertickcounter,
      boolean flag,
      Camera camera,
      GameRenderer gamerenderer,
      Matrix4f matrix4f,
      Matrix4f matrix4f1,
      ClientWorld clientworld,
      boolean flag1,
      Frustum frustum
   ) {
      this.worldRenderer = worldrenderer;
      this.tickCounter = rendertickcounter;
      this.blockOutlines = flag;
      this.camera = camerax;
      this.gameRenderer = gamerenderer;
      this.positionMatrix = matrix4f;
      this.projectionMatrix = matrix4f1;
      this.world = clientworld;
      this.advancedTranslucency = flag1;
      this.frustum = frustumx;
      this.matrixStack = new MatrixStack();
      this.consumers = null;
   }

   public void setConsumers(VertexConsumerProvider vertexconsumerprovider) {
      this.consumers = vertexconsumerprovider;
   }

   public void setMatrixStack(MatrixStack matrixstack) {
      this.matrixStack = matrixstack;
   }

   public WorldRenderer worldRenderer() {
      return this.worldRenderer;
   }

   public RenderTickCounter tickCounter() {
      return this.tickCounter;
   }

   public boolean blockOutlines() {
      return this.blockOutlines;
   }

   public Camera camera() {
      return this.camera;
   }

   public GameRenderer gameRenderer() {
      return this.gameRenderer;
   }

   public Matrix4f positionMatrix() {
      return this.positionMatrix;
   }

   public Matrix4f projectionMatrix() {
      return this.projectionMatrix;
   }

   public ClientWorld world() {
      return this.world;
   }

   public boolean advancedTranslucency() {
      return this.advancedTranslucency;
   }

   @Nullable
   public VertexConsumerProvider consumers() {
      return this.consumers;
   }

   @Nullable
   public Frustum frustum() {
      return this.frustum;
   }

   @Nullable
   public MatrixStack matrixStack() {
      return this.matrixStack;
   }

   public boolean translucentBlockOutline() {
      return false;
   }
}
