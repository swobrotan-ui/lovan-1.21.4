package shader;

import java.io.ByteArrayInputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public class ShaderResource {
   private final Identifier location;
   private final byte[] buffer;

   public ShaderResource(String s, byte[] abyte) {
      this.location = Identifier.ofVanilla(s);
      this.buffer = abyte;
   }

   public Resource toResource() {
      return new Resource(MinecraftClient.getInstance().getDefaultResourcePack(), () -> {
         return new ByteArrayInputStream(this.buffer);
      });
   }

   public static ShaderResource b(String s, byte[] abyte) {
      return new ShaderResource(s, abyte);
   }

   public Identifier getLocation() {
      return this.location;
   }
}
