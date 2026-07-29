import dev.mark.system.modules.settings.ISetting;
import dev.mark.system.render.gui.common.base.Component;
import dev.mark.system.render.gui.components.settings.DropDownComp;
import setting.Setting;

public record kjx(
   String label,
   Component component,
   float yOffset,
   DropDownComp dropdown,
   ISetting setting,
   String label,
   gui.Component component,
   float yOffset,
   x dropdown,
   Setting setting,
   String label,
   Component component,
   float yOffset,
   DropDownComp dropdown,
   ISetting setting,
   String label,
   gui.Component component,
   float yOffset,
   x dropdown,
   Setting setting
) {
   private final String eO;
   private final gui.Component anL;
   private final float rl;
   private final x WK;
   private final Setting ara;

   public kjx(String s, gui.Component component, float f, Setting setting) {
      this(s, component, f, component instanceof x ? (x)component : null, setting);
   }

   public kjx(String s, gui.Component component, float f, x x, Setting setting) {
      this.eO = s;
      this.anL = component;
      this.rl = f;
      this.WK = x;
      this.ara = setting;
   }

   public String a() {
      return this.eO;
   }

   public gui.Component b() {
      return this.anL;
   }

   public float c() {
      return this.rl;
   }

   public x d() {
      return this.WK;
   }

   public Setting e() {
      return this.ara;
   }
}
