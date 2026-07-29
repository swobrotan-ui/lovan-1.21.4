import core.ClientMain;
import gui.Component;
import module.ProtestModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;

public class m extends Component {
   private static final float lr = 180.0F;
   private static final float ahf = 40.0F;
   private static final float adg = 30.0F;
   private static final float Sp = 5.0F;
   private static final float apd = 5.0F;
   private static final String anx = "...";
   private final MinecraftClient Sm = MinecraftClient.getInstance();
   private final BuiltRectangle eo;
   private String H;
   private final String ayI;

   public m(float f, float f1) {
      super(f, f1, 180.0F, 40.0F);
      if (this.Sm.isIntegratedServerRunning()) {
         this.ayI = "local";
      } else {
         ServerInfo serverinfo = this.Sm.getCurrentServerEntry();
         this.ayI = serverinfo != null ? serverinfo.address : "Unknown";
      }

      this.eo = RectangleCache.b(180.0F, 40.0F, 8.0F);
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f7, float f2) {
      this.eo.a(matrix4f, f, f1, f2);
      ProtestModule protestmodule = ClientMain.getInstance().getModuleManager().<ProtestModule>getModule(ProtestModule.class);
      if (protestmodule != null && protestmodule.isEnabled()) {
         this.H = "SйstemPlayer";
      } else {
         this.H = this.Sm.getGameProfile().getName();
      }

      BuiltText builttext = TextCache.a(this.aiL, "Q", 27.0F, Bz);
      builttext.a(matrix4f, f + 5.0F, f1 + 5.0F, f2);
      float f3 = f + 5.0F + 30.0F + 5.0F;
      float f4 = 135.0F;
      String s = this.a(this.H, f4);
      BuiltText builttext1 = TextCache.a(this.aeN, s, 13.0F, Bz);
      builttext1.a(matrix4f, f3, f1 + 5.0F, f2);
      float f5 = this.nO.e().d() * 13.0F;
      float f6 = f1 + 40.0F - 5.0F - f5;
      BuiltText builttext2 = TextCache.a(this.nO, this.ayI, 13.0F, hp);
      builttext2.a(matrix4f, f3, f6 - 3.0F, f2);
   }

   private String a(String s, float f) {
      if (s != null && !s.isEmpty()) {
         if (this.aeN.c(s, 13.0F) <= f) {
            return s;
         } else {
            float f1 = this.aeN.c("...", 13.0F);
            if (f1 > f) {
               return "";
            } else {
               for (int i = s.length(); i > 0; i--) {
                  String s1 = s.substring(0, i);
                  float f2 = this.aeN.c(s1, 13.0F) + f1;
                  if (f2 <= f) {
                     return s1 + "...";
                  }
               }

               return "";
            }
         }
      } else {
         return "";
      }
   }
}
