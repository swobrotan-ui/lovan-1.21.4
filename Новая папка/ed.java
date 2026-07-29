import hud.ListEntry;
import setting.KeyBindSetting;

class ed extends ListEntry<KeyBindSetting> {
   final String auh;
   final KeyBindSetting Wf;
   final String adf;

   ed(String s, KeyBindSetting keybindsetting, String s1) {
      super(keybindsetting, s1);
      this.auh = s;
      this.Wf = keybindsetting;
      this.adf = null;
   }

   ed(String s, KeyBindSetting keybindsetting, String s1, String s2) {
      super(keybindsetting, s2);
      this.auh = s;
      this.Wf = keybindsetting;
      this.adf = s1;
   }

   @Override
   public void a() {
   }
}
