package shader;

import java.util.ArrayList;
import java.util.List;

public class ShaderResourceProvider {
   private final List<ShaderResource> resources = new ArrayList<ShaderResource>();

   public ShaderResourceProvider(String s) {
      ShaderData shaderdata = ShaderCache.getShader(s);
      if (shaderdata == null) {
         System.err.println("Shader not found in cache: " + s);
      } else {
         String s2 = shaderdata.getName();
         String s1 = "shaders/core/" + s2;
         this.resources.add(ShaderResource.b(s1 + ".json", shaderdata.getJsonBytes()));
         this.resources.add(ShaderResource.b(s1 + ".fsh", shaderdata.getFshBytes()));
         this.resources.add(ShaderResource.b(s1 + ".vsh", shaderdata.getVshBytes()));
      }
   }

   public List<ShaderResource> getResources() {
      return this.resources;
   }
}
