import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.SIZE_T;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

interface su extends StdCallLibrary {
   su INSTANCE = (su)Native.loadLibrary("kernel32.dll", su.class, W32APIOptions.ASCII_OPTIONS);

   HANDLE OpenProcess(int i, boolean flag, int j);

   Pointer VirtualAllocEx(HANDLE handle, Pointer pointer, SIZE_T size_t, int i, int j);

   boolean WriteProcessMemory(HANDLE handle, Pointer pointer, Pointer pointer1, int i, IntByReference intbyreference);

   boolean ReadProcessMemory(HANDLE handle, Pointer pointer, Pointer pointer1, int i, IntByReference intbyreference);

   boolean VirtualFreeEx(HANDLE handle, Pointer pointer, SIZE_T size_t, int i);

   HANDLE GetModuleHandle(String s);

   Pointer GetProcAddress(HANDLE handle, String s);

   HANDLE LoadLibraryA(String s);

   HANDLE CreateRemoteThread(HANDLE handle, int i, int j, Pointer pointer, Pointer pointer1, int k, int l);

   int GetLastError();
}
