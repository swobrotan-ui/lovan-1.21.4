package module;

import enum.Category;
import java.awt.Color;
import java.util.List;
import setting.ActionSetting;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.GroupSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class CustomThemeModule extends Module {
   private ListSetting themeSetting = new ListSetting("Тема", "1", List.<String>of("Стандарт", "Сильвер", "Кастом"), List.<String>of("Стандарт"), false);
   private ListSetting renderSetting = new ListSetting("Рендер", "1", List.<String>of("Обычный", "LiquidGlass"), List.<String>of("Обычный"), false);
   private ColorSetting color1Setting = new ColorSetting("Первый цвет", "1", new Color(43, 46, 50, 255), true);
   private ColorSetting color2Setting = new ColorSetting("Второй цвет", "1", new Color(25, 26, 29, 255), true);
   private ColorSetting color3Setting = new ColorSetting("Третий цвет", "1", new Color(15, 15, 17, 255), true);
   private ColorSetting color4Setting = new ColorSetting("Четвертый цвет", "1", new Color(4, 4, 4, 255), true);
   private ColorSetting borderColor1Setting = new ColorSetting("Первый цвет границы", "1", new Color(72, 76, 83, 255), true);
   private ColorSetting borderColor2Setting = new ColorSetting("Второй цвет границы", "1", new Color(38, 38, 38, 255), true);
   private ColorSetting borderColor3Setting = new ColorSetting("Третий цвет границы", "1", new Color(16, 16, 16, 0), true);
   private ColorSetting borderColor4Setting = new ColorSetting("Четвертый цвет границы", "1", new Color(0, 0, 0, 0), true);
   private GroupSetting gradientPositionsGroup = new GroupSetting("Позиции градиента", "1");
   private SliderSetting scaleXSetting = new SliderSetting("Масштаб X", "1", 0.7, -10.0, 10.0, 0.01);
   private SliderSetting scaleYSetting = new SliderSetting("Масштаб Y", "1", -0.4, -10.0, 10.0, 0.01);
   private SliderSetting radiusSetting = new SliderSetting("Радиус", "1", 1.9, 0.0, 10.0, 0.01);
   private SliderSetting centerXSetting = new SliderSetting("Центр X", "1", 1.0, 0.0, 1.0, 0.01);
   private SliderSetting centerYSetting = new SliderSetting("Центр Y", "1", 0.0, -10.0, 10.0, 0.01);
   private SliderSetting noiseSetting = new SliderSetting("Шум", "1", 1.0, 0.0, 10.0, 0.01);
   private BooleanSetting textShadowSetting = new BooleanSetting("Тень текста", "Включить/выключить тень для текста", true);
   private SliderSetting fresnelSetting = new SliderSetting("Френель", "1", 4.5, 1.0, 100.0, 0.5);
   private SliderSetting brightnessSetting = new SliderSetting("Яркость", "1", 0.8, 0.1, 3.0, 0.01);
   private ActionSetting kM = new ActionSetting("Сброс позиций", "Вернуть позиции градиента по умолчанию");
   private ActionSetting abz = new ActionSetting("Сброс градиента", "Вернуть цвета градиента по умолчанию");
   private ActionSetting adH = new ActionSetting("Сброс обводки", "Вернуть цвета обводки по умолчанию");
   private ActionSetting atn = new ActionSetting("Сброс LiquidGlass", "Вернуть настройки LiquidGlass по умолчанию");

   public CustomThemeModule() {
      super("CustomTheme", "Позволяет изменить тему", Category.CLIENT);
      this.kM.setCallback(this::a);
      this.abz.setCallback(this::b);
      this.adH.setCallback(this::c);
      this.atn.setCallback(this::d);
      this.gradientPositionsGroup.addSettings(this.scaleXSetting, this.scaleYSetting, this.radiusSetting, this.centerXSetting, this.centerYSetting);
      this.themeSetting.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.color1Setting.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.color2Setting.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.color3Setting.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.color4Setting.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.borderColor1Setting.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.borderColor2Setting.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.borderColor3Setting.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.borderColor4Setting.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.noiseSetting.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.gradientPositionsGroup.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.kM.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.abz.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.adH.setVisibilitySupplier(() -> {
         return !this.e();
      });
      this.fresnelSetting.setVisibilitySupplier(this::e);
      this.brightnessSetting.setVisibilitySupplier(this::e);
      this.atn.setVisibilitySupplier(this::e);
      this.addSettings(
         this.themeSetting,
         this.renderSetting,
         this.color1Setting,
         this.color2Setting,
         this.color3Setting,
         this.color4Setting,
         this.borderColor1Setting,
         this.borderColor2Setting,
         this.borderColor3Setting,
         this.borderColor4Setting,
         this.noiseSetting,
         this.textShadowSetting,
         this.gradientPositionsGroup,
         this.kM,
         this.abz,
         this.adH,
         this.fresnelSetting,
         this.brightnessSetting,
         this.atn
      );
   }

   private void a() {
      this.scaleXSetting.setValue(0.7);
      this.scaleYSetting.setValue(-0.4);
      this.radiusSetting.setValue(1.9);
      this.centerXSetting.setValue(1.0);
      this.centerYSetting.setValue(0.0);
      this.noiseSetting.setValue(1.0);
   }

   private void b() {
      this.color1Setting.setColor(new Color(43, 46, 50, 255));
      this.color2Setting.setColor(new Color(25, 26, 29, 255));
      this.color3Setting.setColor(new Color(15, 15, 17, 255));
      this.color4Setting.setColor(new Color(4, 4, 4, 255));
   }

   private void c() {
      this.borderColor1Setting.setColor(new Color(72, 76, 83, 255));
      this.borderColor2Setting.setColor(new Color(38, 38, 38, 255));
      this.borderColor3Setting.setColor(new Color(16, 16, 16, 0));
      this.borderColor4Setting.setColor(new Color(0, 0, 0, 0));
   }

   private void d() {
      this.fresnelSetting.setValue(4.5);
      this.brightnessSetting.setValue(0.7);
   }

   public boolean e() {
      return this.renderSetting.getFirst().contains("LiquidGlass");
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public ListSetting f() {
      return this.themeSetting;
   }

   public ListSetting g() {
      return this.renderSetting;
   }

   public ColorSetting h() {
      return this.color1Setting;
   }

   public ColorSetting i() {
      return this.color2Setting;
   }

   public ColorSetting j() {
      return this.color3Setting;
   }

   public ColorSetting k() {
      return this.color4Setting;
   }

   public ColorSetting l() {
      return this.borderColor1Setting;
   }

   public ColorSetting m() {
      return this.borderColor2Setting;
   }

   public ColorSetting n() {
      return this.borderColor3Setting;
   }

   public ColorSetting o() {
      return this.borderColor4Setting;
   }

   public SliderSetting p() {
      return this.scaleXSetting;
   }

   public SliderSetting q() {
      return this.scaleYSetting;
   }

   public SliderSetting r() {
      return this.radiusSetting;
   }

   public SliderSetting s() {
      return this.centerXSetting;
   }

   public SliderSetting t() {
      return this.centerYSetting;
   }

   public SliderSetting u() {
      return this.noiseSetting;
   }

   public BooleanSetting v() {
      return this.textShadowSetting;
   }

   public SliderSetting w() {
      return this.fresnelSetting;
   }

   public SliderSetting x() {
      return this.brightnessSetting;
   }

   public ActionSetting y() {
      return this.kM;
   }

   public ActionSetting z() {
      return this.abz;
   }

   public ActionSetting A() {
      return this.adH;
   }

   public ActionSetting B() {
      return this.atn;
   }
}
