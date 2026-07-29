package render;

import font.MSDFFont;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class TextCache {
   private static final Map<zb, BuiltText> gb = new HashMap<zb, BuiltText>();
   private static final int di = 1000;
   private static final zb as = new zb();

   public static BuiltText a(MSDFFont msdffont, String s, float f, Color color) {
      as.a(msdffont, s, f, color.getRGB());
      BuiltText builttext = gb.get(as);
      if (builttext != null) {
         return builttext;
      } else {
         if (gb.size() > 1000) {
            gb.clear();
         }

         zb zb = new zb(msdffont, s, f, color.getRGB());
         BuiltText builttext1 = new qt().a(msdffont).b(s).c(f).d(0.0F).e(color).g(0.4F).h(-0.3F).a();
         gb.put(zb, builttext1);
         return builttext1;
      }
   }
}
