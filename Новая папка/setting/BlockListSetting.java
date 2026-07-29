package setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class BlockListSetting extends Setting {
   private final Map<Block, BlockEntry> blocks = new ConcurrentHashMap<Block, BlockEntry>();
   private final Map<Block, BlockEntry> defaultBlocks = new HashMap<Block, BlockEntry>();

   public BlockListSetting(String s, String s1) {
      super(s, s1);
   }

   public void addDefault(Block block, boolean flag, Color color) {
      BlockEntry blockentry = new BlockEntry(flag, color);
      this.defaultBlocks.put(block, blockentry);
      this.blocks.put(block, new BlockEntry(flag, color));
   }

   public void setBlock(Block block, boolean flag, Color color) {
      this.blocks.put(block, new BlockEntry(flag, color));
      this.notifyChange();
   }

   public void removeBlock(Block block) {
      this.blocks.remove(block);
      this.notifyChange();
   }

   public boolean isEnabled(Block block) {
      BlockEntry blockentry = this.blocks.get(block);
      return blockentry != null && blockentry.isEnabled();
   }

   public Color getColor(Block block) {
      BlockEntry blockentry = this.blocks.get(block);
      return blockentry != null ? blockentry.getColor() : Color.WHITE;
   }

   public void setEnabled(Block block, boolean flag) {
      BlockEntry blockentry = this.blocks.get(block);
      if (blockentry != null) {
         blockentry.setEnabled(flag);
         this.notifyChange();
      }
   }

   public void setColor(Block block, Color color) {
      BlockEntry blockentry = this.blocks.get(block);
      if (blockentry != null) {
         blockentry.setColor(color);
         this.notifyChange();
      }
   }

   @Override
   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty(aiK[6], this.getType());
      JsonArray jsonarray = new JsonArray();

      for (Entry entry : this.blocks.entrySet()) {
         JsonObject jsonobject1 = new JsonObject();
         Identifier identifier = Registries.BLOCK.getId((Block)entry.getKey());
         jsonobject1.addProperty(aiK[4], identifier.toString());
         jsonobject1.addProperty(aiK[1], ((BlockEntry)entry.getValue()).isEnabled());
         Color color = ((BlockEntry)entry.getValue()).getColor();
         jsonobject1.addProperty(aiK[3], color.getRed());
         jsonobject1.addProperty(aiK[8], color.getGreen());
         jsonobject1.addProperty(aiK[2], color.getBlue());
         jsonobject1.addProperty(aiK[5], color.getAlpha());
         jsonarray.add(jsonobject1);
      }

      jsonobject.add(aiK[7], jsonarray);
      return jsonobject;
   }

   @Override
   public void fromJson(JsonObject jsonobject) {
      if (jsonobject.has(aiK[7])) {
         this.blocks.clear();
         JsonArray jsonarray = jsonobject.getAsJsonArray(aiK[7]);

         for (int i = 0; i < jsonarray.size(); i++) {
            JsonObject jsonobject1 = jsonarray.get(i).getAsJsonObject();
            String s = jsonobject1.get(aiK[4]).getAsString();
            Identifier identifier = Identifier.of(s);
            Block block = (Block)Registries.BLOCK.get(identifier);
            if (block != null) {
               boolean flag = jsonobject1.get(aiK[1]).getAsBoolean();
               int j = jsonobject1.get(aiK[3]).getAsInt();
               int k = jsonobject1.get(aiK[8]).getAsInt();
               int l = jsonobject1.get(aiK[2]).getAsInt();
               int i1 = jsonobject1.get(aiK[5]).getAsInt();
               Color color = new Color(j, k, l, i1);
               this.blocks.put(block, new BlockEntry(flag, color));
            }
         }

         this.notifyChange();
      }
   }

   @Override
   public String getType() {
      return "blocklist";
   }

   @Override
   public void reset() {
      this.blocks.clear();

      for (Entry entry : this.defaultBlocks.entrySet()) {
         this.blocks.put((Block)entry.getKey(), new BlockEntry(((BlockEntry)entry.getValue()).isEnabled(), ((BlockEntry)entry.getValue()).getColor()));
      }

      this.notifyChange();
   }

   public Map<Block, BlockEntry> getBlocks() {
      return this.blocks;
   }
}
