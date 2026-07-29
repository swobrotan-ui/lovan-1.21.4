import gui.Component;
import java.util.ArrayList;
import java.util.List;
import setting.ActionKeySetting;
import setting.ActionSetting;
import setting.BlockListSetting;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.FilePickerSetting;
import setting.GroupSetting;
import setting.ListSetting;
import setting.RangeSetting;
import setting.Setting;
import setting.SliderSetting;

public class kk {
   private static final float Mp = 286.0F;
   private static final float LN = 10.0F;

   public static List<kjx> a(List<Setting> list, float f, float f1, Runnable runnable) {
      ArrayList arraylist = new ArrayList();
      float f2 = f;

      for (Setting setting : list) {
         Component component = b(setting, f1, runnable);
         if (component != null) {
            component.setPosition(component.getX(), f2);
            arraylist.add(new kjx(setting.getDisplayName(), component, f2, setting));
            if (setting.isVisible()) {
               f2 += component.getHeight() + 10.0F;
            }
         }
      }

      return arraylist;
   }

   private static Component b(Setting setting, float f, Runnable runnable) {
      if (setting instanceof BooleanSetting booleansetting) {
         return c(booleansetting, f);
      } else if (setting instanceof RangeSetting rangesetting) {
         return d(rangesetting, f);
      } else if (setting instanceof SliderSetting slidersetting) {
         return e(slidersetting, f);
      } else if (setting instanceof ListSetting listsetting) {
         return f(listsetting, f);
      } else if (setting instanceof GroupSetting groupsetting) {
         return g(groupsetting, f, runnable);
      } else if (setting instanceof ActionKeySetting actionkeysetting) {
         return i(actionkeysetting, f, runnable);
      } else if (setting instanceof ColorSetting colorsetting) {
         return h(colorsetting, f, runnable);
      } else if (setting instanceof FilePickerSetting filepickersetting) {
         return j(filepickersetting, f);
      } else if (setting instanceof ActionSetting actionsetting) {
         return k(actionsetting, f);
      } else {
         return setting instanceof BlockListSetting blocklistsetting ? l(blocklistsetting, f, runnable) : null;
      }
   }

   private static em c(BooleanSetting booleansetting, float f) {
      float f1 = 236.0F - f;
      em em = new em(f1, 0.0F);
      em.a(booleansetting.getValue());
      em.c(() -> {
         booleansetting.setValue(em.d());
      });
      return em;
   }

   private static pw d(RangeSetting rangesetting, float f) {
      float f1 = 276.0F - f;
      pw pw = new pw(
         f1,
         0.0F,
         (float)rangesetting.getMin(),
         (float)rangesetting.getMax(),
         (float)rangesetting.getValueLow(),
         (float)rangesetting.getValueHigh(),
         (float)rangesetting.getStep(),
         rangesetting.getDecimalPlaces()
      );
      pw.l(() -> {
         rangesetting.setRange(pw.j(), pw.k());
      });
      return pw;
   }

   private static cm e(SliderSetting slidersetting, float f) {
      float f1 = 276.0F - f;
      cm cm = new cm(
         f1,
         0.0F,
         (float)slidersetting.getMin(),
         (float)slidersetting.getMax(),
         (float)slidersetting.getValue(),
         (float)slidersetting.getStep(),
         slidersetting.getDecimalPlaces()
      );
      cm.h(() -> {
         slidersetting.setValue(cm.g());
      });
      return cm;
   }

   private static x f(ListSetting listsetting, float f) {
      float f1 = 276.0F - f;
      x x = new x(f1, 0.0F, listsetting.getAvailableValues(), listsetting.getFirst());
      x.s(s -> {
         listsetting.setSelectedValues(List.<String>of(s));
      });
      return x;
   }

   private static gl g(GroupSetting groupsetting, float f, Runnable runnable) {
      float f1 = 256.0F - f;
      return new gl(f1, 0.0F, runnable);
   }

   private static zl h(ColorSetting colorsetting, float f, Runnable runnable) {
      float f1 = 256.0F - f;
      zl zl = new zl(f1, 0.0F, colorsetting.getColor());
      zl.a(runnable);
      return zl;
   }

   private static pl i(ActionKeySetting actionkeysetting, float f, Runnable runnable) {
      float f1 = 276.0F - f;
      float f2 = f1 - 50.0F;
      return new pl(f2, 0.0F, actionkeysetting);
   }

   private static gn j(FilePickerSetting filepickersetting, float f) {
      float f1 = 176.0F - f;
      return new gn(f1, 0.0F, filepickersetting);
   }

   private static au k(ActionSetting actionsetting, float f) {
      float f1 = 256.0F - f;
      au au = new au(f1, 0.0F);
      au.b(actionsetting::invoke);
      return au;
   }

   private static nw l(BlockListSetting blocklistsetting, float f, Runnable runnable) {
      float f1 = 256.0F - f;
      nw nw = new nw(f1, 0.0F);
      nw.a(runnable);
      return nw;
   }
}
