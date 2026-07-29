package module;

import com.mojang.blaze3d.systems.RenderSystem;
import enum.Category;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import setting.BlockListSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class BlockESPModule extends Module {
   private ListSetting espModeSetting = new ListSetting(
      "Режим ESP", "Режим отображения ESP", Arrays.<String>asList("Ящик", "Углы", "Заливка"), List.<String>of("Ящик"), false
   );
   private SliderSetting cornerSizeSetting = new SliderSetting("Размер углов", "Размер углов ESP", 0.3, 0.1, 1.0, 0.1);
   private SliderSetting rangeSetting = new SliderSetting("Дальность", "Дальность поиска блоков", 32.0, 16.0, 128.0, 8.0);
   private SliderSetting fillAlphaSetting = new SliderSetting("Прозрачность заливки", "Прозрачность заливки блоков", 0.3, 0.1, 1.0, 0.1);
   private BlockListSetting kA = new BlockListSetting("Список блоков", "Управление блоками для ESP");
   private final Map<ChunkPos, Map<BlockPos, Block>> Zf = new ConcurrentHashMap<ChunkPos, Map<BlockPos, Block>>();
   private final Set<ChunkPos> auO = ConcurrentHashMap.<ChunkPos>newKeySet();
   private BlockPos Dh = null;
   private int Rd = 0;
   private static final int vq = 5;
   private final Queue<BlockPos> ayn = new ArrayDeque<BlockPos>();
   private static final int ahE = 50;
   private int ame = 0;
   private static final int Kl = 20;
   private int abS = 0;

   public BlockESPModule() {
      super("БлоскЕЗП", "Показывать ценные блоки сквозь стены", Category.RENDER);
      this.a();
      this.addSettings(this.espModeSetting, this.cornerSizeSetting, this.rangeSetting, this.fillAlphaSetting, this.kA);
      this.b();
   }

   private void a() {
      this.cornerSizeSetting.setVisibilitySupplier(() -> {
         return this.espModeSetting.isSelected("Углы");
      });
      this.fillAlphaSetting.setVisibilitySupplier(() -> {
         return this.espModeSetting.isSelected("Заливка");
      });
   }

   private void b() {
      this.kA.addDefault(Blocks.DIAMOND_ORE, true, new Color(0, 255, 255));
      this.kA.addDefault(Blocks.DEEPSLATE_DIAMOND_ORE, true, new Color(0, 255, 255));
      this.kA.addDefault(Blocks.GOLD_ORE, true, new Color(255, 215, 0));
      this.kA.addDefault(Blocks.DEEPSLATE_GOLD_ORE, true, new Color(255, 215, 0));
      this.kA.addDefault(Blocks.NETHER_GOLD_ORE, true, new Color(255, 215, 0));
      this.kA.addDefault(Blocks.RAW_GOLD_BLOCK, true, new Color(255, 215, 0));
      this.kA.addDefault(Blocks.GOLD_BLOCK, true, new Color(255, 215, 0));
      this.kA.addDefault(Blocks.ANCIENT_DEBRIS, true, new Color(128, 0, 128));
      this.kA.addDefault(Blocks.NETHERITE_BLOCK, true, new Color(128, 0, 128));
      this.kA.addDefault(Blocks.EMERALD_ORE, true, new Color(0, 255, 0));
      this.kA.addDefault(Blocks.DEEPSLATE_EMERALD_ORE, true, new Color(0, 255, 0));
      this.kA.addDefault(Blocks.IRON_ORE, false, new Color(200, 200, 200));
      this.kA.addDefault(Blocks.DEEPSLATE_IRON_ORE, false, new Color(200, 200, 200));
      this.kA.addDefault(Blocks.COAL_ORE, false, new Color(50, 50, 50));
      this.kA.addDefault(Blocks.DEEPSLATE_COAL_ORE, false, new Color(50, 50, 50));
      this.kA.addDefault(Blocks.REDSTONE_ORE, false, new Color(255, 0, 0));
      this.kA.addDefault(Blocks.DEEPSLATE_REDSTONE_ORE, false, new Color(255, 0, 0));
      this.kA.addDefault(Blocks.LAPIS_ORE, false, new Color(0, 100, 255));
      this.kA.addDefault(Blocks.DEEPSLATE_LAPIS_ORE, false, new Color(0, 100, 255));
      this.kA.addDefault(Blocks.COPPER_ORE, false, new Color(255, 140, 0));
      this.kA.addDefault(Blocks.DEEPSLATE_COPPER_ORE, false, new Color(255, 140, 0));
      this.kA.addDefault(Blocks.CHEST, true, new Color(139, 69, 19));
      this.kA.addDefault(Blocks.TRAPPED_CHEST, true, new Color(139, 69, 19));
      this.kA.addDefault(Blocks.ENDER_CHEST, true, new Color(128, 0, 128));
      this.kA.addDefault(Blocks.SPAWNER, true, new Color(255, 100, 100));
   }

   @Override
   public void onEnable() {
      this.Zf.clear();
      this.auO.clear();
      this.ayn.clear();
      this.Dh = null;
      this.Rd = 0;
      this.ame = 0;
      this.abS = 0;
   }

   @Override
   public void onDisable() {
      this.Zf.clear();
      this.auO.clear();
      this.ayn.clear();
   }

   @Override
   public void onRenderAfterTranslucent(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld()) {
         try {
            this.e();
            this.c();
            this.h(worldrendercontext);
         } catch (Exception exception) {
         }
      }
   }

   private void c() {
      this.Rd++;
      if (this.Rd >= 5) {
         this.Rd = 0;
         if (this.ayn.isEmpty()) {
            for (Map map : this.Zf.values()) {
               this.ayn.addAll(map.keySet());
            }
         }

         for (int i = 0; !this.ayn.isEmpty() && i < 50; i++) {
            BlockPos blockpos = this.ayn.poll();
            if (blockpos == null) {
               return;
            }

            BlockState blockstate = this.getWorld().getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (!this.g(block)) {
               this.d(blockpos);
            }
         }
      }
   }

   private void d(BlockPos blockpos) {
      ChunkPos chunkpos = new ChunkPos(blockpos);
      Map map = this.Zf.get(chunkpos);
      if (map != null) {
         map.remove(blockpos);
         if (map.isEmpty()) {
            this.Zf.remove(chunkpos);
         }
      }
   }

   private void e() {
      if (this.getPlayer() != null && this.getWorld() != null) {
         BlockPos blockpos = this.getPlayer().getBlockPos();
         ChunkPos chunkpos = new ChunkPos(blockpos);
         int i = (int)this.rangeSetting.getValue();
         int j = i / 16 + 1;
         this.ame++;
         if (this.ame >= 20) {
            this.ame = 0;
            ArrayList arraylist = new ArrayList();

            for (int k = -j; k <= j; k++) {
               for (int l = -j; l <= j; l++) {
                  arraylist.add(new ChunkPos(chunkpos.x + k, chunkpos.z + l));
               }
            }

            if (!arraylist.isEmpty()) {
               this.abS = this.abS % arraylist.size();
               ChunkPos chunkpos2 = (ChunkPos)arraylist.get(this.abS);
               this.auO.remove(chunkpos2);
               this.abS++;
            }
         }

         boolean flag = this.Dh == null || !this.Dh.equals(new BlockPos(chunkpos.getStartX(), blockpos.getY(), chunkpos.getStartZ()));
         if (flag) {
            this.Dh = new BlockPos(chunkpos.getStartX(), blockpos.getY(), chunkpos.getStartZ());
            HashSet hashset = new HashSet();

            for (ChunkPos chunkpos1 : this.Zf.keySet()) {
               if (Math.abs(chunkpos1.x - chunkpos.x) > j + 1 || Math.abs(chunkpos1.z - chunkpos.z) > j + 1) {
                  hashset.add(chunkpos1);
               }
            }

            hashset.forEach(this.Zf::remove);
            hashset.forEach(this.auO::remove);
         }

         for (int i1 = -j; i1 <= j; i1++) {
            for (int j1 = -j; j1 <= j; j1++) {
               ChunkPos chunkpos3 = new ChunkPos(chunkpos.x + i1, chunkpos.z + j1);
               if (!this.auO.contains(chunkpos3)) {
                  this.f(chunkpos3);
                  this.auO.add(chunkpos3);
               }
            }
         }
      }
   }

   private void f(ChunkPos chunkpos) {
      WorldChunk worldchunk = this.getWorld().getChunk(chunkpos.x, chunkpos.z);
      if (worldchunk != null) {
         HashMap hashmap = new HashMap();
         int i = chunkpos.getStartX();
         int j = chunkpos.getStartZ();

         for (int k = 0; k < 16; k++) {
            for (int l = 0; l < 16; l++) {
               for (int i1 = worldchunk.getBottomY(); i1 < worldchunk.getTopYInclusive(); i1++) {
                  BlockPos blockpos = new BlockPos(i + k, i1, j + l);
                  BlockState blockstate = worldchunk.getBlockState(blockpos);
                  Block block = blockstate.getBlock();
                  if (this.g(block)) {
                     hashmap.put(blockpos.toImmutable(), block);
                  }
               }
            }
         }

         if (!hashmap.isEmpty()) {
            this.Zf.put(chunkpos, hashmap);
         }
      }
   }

   private boolean g(Block block) {
      return this.kA.isEnabled(block);
   }

   private void h(WorldRenderContext worldrendercontext) {
      if (!this.Zf.isEmpty()) {
         MatrixStack matrixstack = worldrendercontext.matrixStack();
         if (matrixstack != null) {
            Vec3d vec3d = worldrendercontext.camera().getPos();
            BlockPos blockpos = this.getPlayer().getBlockPos();
            int i = (int)this.rangeSetting.getValue();
            int j = i * i;
            boolean flag = this.espModeSetting.isSelected("Ящик");
            boolean flag1 = this.espModeSetting.isSelected("Углы");
            boolean flag2 = this.espModeSetting.isSelected("Заливка");
            HashMap hashmap = new HashMap();

            for (Map map : this.Zf.values()) {
               for (Entry entry : map.entrySet()) {
                  BlockPos blockpos1 = (BlockPos)entry.getKey();
                  if (blockpos1.getSquaredDistance(blockpos) <= j) {
                     hashmap.put(blockpos1, (Block)entry.getValue());
                  }
               }
            }

            if (!hashmap.isEmpty()) {
               if (flag2) {
                  this.i(matrixstack, vec3d, hashmap);
               }

               if (flag || flag1) {
                  dr.a(false);
                  Tessellator tessellator = Tessellator.getInstance();
                  BufferBuilder bufferbuilder = tessellator.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

                  for (Entry entry1 : hashmap.entrySet()) {
                     BlockPos blockpos2 = (BlockPos)entry1.getKey();
                     Block block = (Block)entry1.getValue();
                     if (this.kA.isEnabled(block)) {
                        Vec3d vec3d1 = new Vec3d(blockpos2.getX() - vec3d.x, blockpos2.getY() - vec3d.y, blockpos2.getZ() - vec3d.z);
                        matrixstack.push();
                        matrixstack.translate(vec3d1.x, vec3d1.y, vec3d1.z);
                        float[] afloat = this.n(block);
                        if (flag) {
                           this.k(matrixstack, bufferbuilder, afloat);
                        }

                        if (flag1) {
                           this.l(matrixstack, bufferbuilder, afloat);
                        }

                        matrixstack.pop();
                     }
                  }

                  BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
                  dr.b();
               }
            }
         }
      }
   }

   private void i(MatrixStack matrixstack, Vec3d vec3d, Map<BlockPos, Block> map) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.disableCull();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      float f = this.fillAlphaSetting.getFloatValue();

      for (Entry entry : map.entrySet()) {
         BlockPos blockpos = (BlockPos)entry.getKey();
         Block block = (Block)entry.getValue();
         if (this.kA.isEnabled(block)) {
            Vec3d vec3d1 = new Vec3d(blockpos.getX() - vec3d.x, blockpos.getY() - vec3d.y, blockpos.getZ() - vec3d.z);
            matrixstack.push();
            matrixstack.translate(vec3d1.x, vec3d1.y, vec3d1.z);
            float[] afloat = this.n(block);
            this.j(matrixstack, bufferbuilder, afloat, f);
            matrixstack.pop();
         }
      }

      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
      RenderSystem.enableCull();
   }

   private void j(MatrixStack matrixstack, BufferBuilder bufferbuilder, float[] afloat, float f) {
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      bufferbuilder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 0.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 0.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 0.0F, 0.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 0.0F, 1.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 0.0F, 1.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 0.0F, 1.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 0.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 0.0F, 0.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 0.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 0.0F, 1.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 0.0F, 0.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 0.0F, 1.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 0.0F, 1.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 0.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
      bufferbuilder.vertex(matrix4f, 1.0F, 0.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], f);
   }

   private void k(MatrixStack matrixstack, BufferBuilder bufferbuilder, float[] afloat) {
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      this.m(bufferbuilder, matrix4f, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, afloat);
      this.m(bufferbuilder, matrix4f, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, afloat);
      this.m(bufferbuilder, matrix4f, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, afloat);
      this.m(bufferbuilder, matrix4f, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, afloat);
      this.m(bufferbuilder, matrix4f, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, afloat);
      this.m(bufferbuilder, matrix4f, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, afloat);
      this.m(bufferbuilder, matrix4f, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, afloat);
      this.m(bufferbuilder, matrix4f, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, afloat);
      this.m(bufferbuilder, matrix4f, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, afloat);
      this.m(bufferbuilder, matrix4f, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, afloat);
      this.m(bufferbuilder, matrix4f, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, afloat);
      this.m(bufferbuilder, matrix4f, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, afloat);
   }

   private void l(MatrixStack matrixstack, BufferBuilder bufferbuilder, float[] afloat) {
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      float f = this.cornerSizeSetting.getFloatValue();
      float[][] afloat1 = new float[][]{
         {0.0F, 0.0F, 0.0F},
         {1.0F, 0.0F, 0.0F},
         {0.0F, 0.0F, 1.0F},
         {1.0F, 0.0F, 1.0F},
         {0.0F, 1.0F, 0.0F},
         {1.0F, 1.0F, 0.0F},
         {0.0F, 1.0F, 1.0F},
         {1.0F, 1.0F, 1.0F}
      };
      float[][][] afloat2 = new float[][][]{
         {{f, 0.0F, 0.0F}, {0.0F, f, 0.0F}, {0.0F, 0.0F, f}},
         {{-f, 0.0F, 0.0F}, {0.0F, f, 0.0F}, {0.0F, 0.0F, f}},
         {{f, 0.0F, 0.0F}, {0.0F, f, 0.0F}, {0.0F, 0.0F, -f}},
         {{-f, 0.0F, 0.0F}, {0.0F, f, 0.0F}, {0.0F, 0.0F, -f}},
         {{f, 0.0F, 0.0F}, {0.0F, -f, 0.0F}, {0.0F, 0.0F, f}},
         {{-f, 0.0F, 0.0F}, {0.0F, -f, 0.0F}, {0.0F, 0.0F, f}},
         {{f, 0.0F, 0.0F}, {0.0F, -f, 0.0F}, {0.0F, 0.0F, -f}},
         {{-f, 0.0F, 0.0F}, {0.0F, -f, 0.0F}, {0.0F, 0.0F, -f}}
      };

      for (int i = 0; i < afloat1.length; i++) {
         float[] afloat3 = afloat1[i];
         float[][] afloat4 = afloat2[i];

         for (float[] afloat5 : afloat4) {
            this.m(
               bufferbuilder, matrix4f, afloat3[0], afloat3[1], afloat3[2], afloat3[0] + afloat5[0], afloat3[1] + afloat5[1], afloat3[2] + afloat5[2], afloat
            );
         }
      }
   }

   private void m(BufferBuilder bufferbuilder, Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4, float f5, float[] afloat) {
      bufferbuilder.vertex(matrix4f, f, f1, f2).color(afloat[0], afloat[1], afloat[2], 1.0F);
      bufferbuilder.vertex(matrix4f, f3, f4, f5).color(afloat[0], afloat[1], afloat[2], 1.0F);
   }

   private float[] n(Block block) {
      Color color = this.kA.getColor(block);
      return new float[]{color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F};
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      super.onPlayerDeath(playerentity);
      if (this.enabledSetting.getValue() && playerentity == this.getPlayer()) {
         this.setEnabled(false);
      }
   }
}
