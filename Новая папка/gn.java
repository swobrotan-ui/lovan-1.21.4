import gui.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;
import setting.FilePickerSetting;

public class gn extends Component {
   private static final float Bg = 100.0F;
   private static final float GP = 20.0F;
   private static final float arB = 5.0F;
   private static final float M = 90.0F;
   private static final String ajL = "...";
   private static final String amK = "Обзор";
   private final BuiltRectangle agj;
   private final qm sB;
   private final Matrix4f lJ = new Matrix4f();
   private final FilePickerSetting acg;

   public gn(float f, float f1, FilePickerSetting filepickersetting) {
      super(f, f1, 100.0F, 20.0F);
      this.acg = filepickersetting;
      this.agj = RectangleCache.b(100.0F, 20.0F, 4.0F);
      this.sB = new qm();
      this.sB.a();
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f8, float f2) {
      this.sB.a(this.cH);
      this.sB.f();
      float f3 = this.sB.d();
      Matrix4f matrix4f1 = f3 != 1.0F ? this.b(matrix4f, f, f1, f3) : matrix4f;
      this.agj.a(matrix4f1, f, f1, f2);
      String s = this.a();
      BuiltText builttext = TextCache.a(this.ayW, s, 13.0F, Bz);
      float f4 = this.ayW.c(s, 13.0F);
      float f5 = f + (100.0F - f4) / 2.0F;
      float f6 = this.ayW.e().d() * 13.0F;
      float f7 = f1 + (20.0F - f6) / 2.0F - 1.0F;
      builttext.a(matrix4f1, f5, f7, f2);
   }

   private String a() {
      if (!this.acg.hasValue()) {
         return "Обзор";
      } else {
         String s = this.acg.getFileName();
         if (this.ayW.c(s, 13.0F) <= 90.0F) {
            return s;
         } else {
            float f = this.ayW.c("...", 13.0F);
            if (f > 90.0F) {
               return "";
            } else {
               for (int i = s.length(); i > 0; i--) {
                  String s1 = s.substring(0, i);
                  float f1 = this.ayW.c(s1, 13.0F) + f;
                  if (f1 <= 90.0F) {
                     return s1 + "...";
                  }
               }

               return "";
            }
         }
      }
   }

   private Matrix4f b(Matrix4f matrix4f, float f, float f1, float f2) {
      float f3 = f + this.qd / 2.0F;
      float f4 = f1 + this.aem / 2.0F;
      this.lJ.set(matrix4f);
      this.lJ.translate(f3, f4, 0.0F);
      this.lJ.scale(f2, f2, 1.0F);
      this.lJ.translate(-f3, -f4, 0.0F);
      return this.lJ;
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0) {
         this.sB.b(true);
         this.c();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0) {
         this.sB.b(false);
         return true;
      } else {
         return false;
      }
   }

   private void c() {
      new Thread(
            () -> {
               try {
                  String s = "Add-Type -AssemblyName System.Windows.Forms; $d = New-Object System.Windows.Forms.OpenFileDialog; $d.Title = 'Выберите лоадер'; $d.Filter = 'All files (*.*)|*.*'; ";
                  if (this.acg.hasValue()) {
                     File file1 = new File(this.acg.getValue());
                     if (file1.getParentFile() != null && file1.getParentFile().exists()) {
                        String s1 = file1.getParentFile().getAbsolutePath().replace("'", "''");
                        s = s + "$d.InitialDirectory = '" + s1 + "'; ";
                     }
                  }

                  s = s + "if ($d.ShowDialog() -eq 'OK') { Write-Output $d.FileName }";
                  ProcessBuilder processbuilder = new ProcessBuilder("powershell", "-NoProfile", "-Command", s);
                  processbuilder.redirectErrorStream(true);
                  Process process = processbuilder.start();
                  StringBuilder stringbuilder = new StringBuilder();
                  BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                  String s2;
                  try {
                     while ((s2 = bufferedreader.readLine()) != null) {
                        stringbuilder.append(s2);
                     }
                  } catch (Throwable throwable1) {
                     try {
                        bufferedreader.close();
                     } catch (Throwable throwable) {
                        throwable1.addSuppressed(throwable);
                     }

                     throw throwable1;
                  }

                  bufferedreader.close();
                  process.waitFor();
                  String s3 = stringbuilder.toString().trim();
                  if (!s3.isEmpty()) {
                     this.acg.setValue(s3);
                  }
               } catch (Exception exception) {
               }
            },
            "FilePickerThread"
         )
         .start();
   }
}
