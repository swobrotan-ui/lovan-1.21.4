package render;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public final class ShaderUtil {
   private static final ResourceManager apH = MinecraftClient.getInstance().getResourceManager();

   public static ShaderProgramKey a(String s, VertexFormat vertexformat, Defines defines) {
      return new ShaderProgramKey(Identifier.ofVanilla("core/" + s), vertexformat, defines);
   }

   public static <T> T b(String s, Class<T> oclass) {
      try {
         Gson gson = new Gson();
         return (T)gson.fromJson(s, oclass);
      } catch (Exception exception) {
         return null;
      }
   }

   public static String c(Identifier identifier) {
      return d(identifier, "\n");
   }

   public static String d(Identifier identifier, String s) {
      try {
         String s1;
         try (
            InputStream inputstream = apH.open(identifier);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
         ) {
            s1 = bufferedreader.lines().collect(Collectors.joining(s));
         }

         return s1;
      } catch (IOException ioexception) {
         throw new RuntimeException(ioexception);
      }
   }
}
