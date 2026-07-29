package module;

import enum.Category;
import java.util.Arrays;
import java.util.Collections;
import setting.ListSetting;

public class ParrotModule extends Module {
   private ListSetting sideSetting = new ListSetting(
      "Сторона", "Выбор плеча для птички-скуфа", Arrays.<String>asList("Справа", "Слева"), Collections.<String>singletonList("Справа"), false
   );
   private ListSetting typeSetting = new ListSetting(
      "Цвет птички",
      "Цвет птички-скуфа",
      Arrays.<String>asList("Синий", "Зеленый", "Красно-синий", "Желто-синий"),
      Collections.<String>singletonList("Желто-синий"),
      false
   );

   public ParrotModule() {
      super("Паррот", "Рендер птички-скуфа на плече", Category.VISUAL);
      this.addSettings(this.sideSetting, this.typeSetting);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public ListSetting a() {
      return this.sideSetting;
   }

   public ListSetting b() {
      return this.typeSetting;
   }
}
