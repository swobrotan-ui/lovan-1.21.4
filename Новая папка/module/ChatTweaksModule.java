package module;

import enum.Category;
import setting.BooleanSetting;

public class ChatTweaksModule extends Module {
   private BooleanSetting infiniteChatSetting = new BooleanSetting("Бесконечный чат", "Увеличивает лимит истории чата", true);
   private BooleanSetting noClearChatSetting = new BooleanSetting("Не очищать чат", "Предотвращает автоматическую очистку чата", true);
   private BooleanSetting copyRightClickSetting = new BooleanSetting("Копировать ПКМ", "Позволяет копировать сообщения правой кнопкой мыши", true);
   private BooleanSetting groupDuplicatesSetting = new BooleanSetting("Группировать дубликаты", "Объединяет одинаковые сообщения в одно", true);

   public ChatTweaksModule() {
      super("СхатТшеакз", "Утилита для чата", Category.VISUAL);
      this.addSettings(this.noClearChatSetting, this.infiniteChatSetting, this.copyRightClickSetting, this.groupDuplicatesSetting);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public BooleanSetting a() {
      return this.infiniteChatSetting;
   }

   public BooleanSetting b() {
      return this.noClearChatSetting;
   }

   public BooleanSetting c() {
      return this.copyRightClickSetting;
   }

   public BooleanSetting d() {
      return this.groupDuplicatesSetting;
   }
}
