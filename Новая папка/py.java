import a.Loader;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class py {
   private static final int TS = 2035711;
   private static final int LJ = 4096;
   private static final int Wg = 8192;
   private static final int Wl = 64;
   private static String[] gR = new String[3];

   public static native boolean a(int i, String s);

   private static native boolean b(su su, HANDLE handle, byte[] abyte, Pointer pointer);

   private static native void c(byte[] abyte, int i, int j, long k, boolean flag);

   private static native boolean d(byte[] abyte, int i, su su);

   private static native long e(int i, String s);

   private static native long f(su su, HANDLE handle, long i, String s);

   private static native byte[] g(su su, HANDLE handle, long i, int j);

   private static native byte[] h(long i, long j);

   private static native byte[] i();

   private static native String j(byte[] abyte, int i);

   private static native void k(byte[] abyte, int i, long j);

   static {
      Loader.init(py.class);
      m();
   }

   private static native String l(char[] achar, long i, int j);

   private static native void m();

   public static native void guard();
}
