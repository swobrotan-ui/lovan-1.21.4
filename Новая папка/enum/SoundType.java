package enum;

public enum SoundType {
   GUI_OPEN("GUI Open", "4.wav"),
   GUI_CLOSE("GUI Close", "4F.wav"),
   MODULE_ENABLE("Module Enable", "3.wav"),
   MODULE_DISABLE("Module Disable", "3F.wav"),
   FRIEND_ADD("Friend Add", "3.wav"),
   FRIEND_REMOVE("Friend Remove", "3F.wav"),
   GROUP_OPEN("Group Open", "5.wav"),
   FAVOURITE_ADD("Favourite Add", "6.wav"),
   CATEGORY_SWITCH("Category Switch", "1.wav"),
   TOGGLE_ON("Toggle On", "2.wav"),
   TOGGLE_OFF("Toggle Off", "2F.wav");

   private final String displayName;
   private final String fileName;

   private SoundType(String s1, String s2) {
      this.displayName = s1;
      this.fileName = s2;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getFileName() {
      return this.fileName;
   }

   // $VF: synthetic method
   private static SoundType[] d() {
      return new SoundType[]{
         GUI_OPEN, GUI_CLOSE, MODULE_ENABLE, MODULE_DISABLE, FRIEND_ADD, FRIEND_REMOVE, GROUP_OPEN, FAVOURITE_ADD, CATEGORY_SWITCH, TOGGLE_ON, TOGGLE_OFF
      };
   }
}
