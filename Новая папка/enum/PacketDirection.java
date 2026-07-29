package enum;

public enum PacketDirection {
   SEND,
   RECEIVE;

   public static PacketDirection a(String s) {
      return Enum.<PacketDirection>valueOf(PacketDirection.class, s);
   }

   // $VF: synthetic method
   private static PacketDirection[] b() {
      return new PacketDirection[]{SEND, RECEIVE};
   }
}
