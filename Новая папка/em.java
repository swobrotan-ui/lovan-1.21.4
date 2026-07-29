import core.SoundManager;
import enum.SoundType;
import gui.Component;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltWhiteRectangle;
import render.RectangleCache;

public class em extends Component {
   private static final float HJ = 40.0F;
   private static final float BX = 20.0F;
   private static final float ami = 16.0F;
   private static final float apL = 2.0F;
   private final BuiltRectangle RJ;
   private final BuiltRectangle fJ;
   private final BuiltWhiteRectangle acp;
   private final mw Ej;
   private Runnable alF;
   private boolean acB = false;

   public em(float f, float f1) {
      super(f, f1, 40.0F, 20.0F);
      this.RJ = RectangleCache.b(40.0F, 20.0F, 10.0F);
      this.fJ = RectangleCache.b(16.0F, 16.0F, 8.0F);
      this.acp = new ewt().a(16.0F, 16.0F).b(8.0F).a();
      this.Ej = new mw();
      this.Ej.a();
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f5, float f2) {
      this.Ej.f();
      this.RJ.a(matrix4f, f, f1, f2);
      float f3 = this.Ej.d();
      float f4 = this.Ej.e();
      if (f4 < 0.01F) {
         this.fJ.a(matrix4f, f + f3, f1 + 2.0F, f2);
      } else if (f4 > 0.99F) {
         this.acp.a(matrix4f, f + f3, f1 + 2.0F, f2);
      } else {
         this.fJ.a(matrix4f, f + f3, f1 + 2.0F, f2 * (1.0F - f4));
         this.acp.a(matrix4f, f + f3, f1 + 2.0F, f2 * f4);
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0) {
         this.b(!this.acB, true);
         if (this.alF != null) {
            this.alF.run();
         }

         return true;
      } else {
         return false;
      }
   }

   public void a(boolean flag) {
      this.b(flag, false);
   }

   public void b(boolean flag, boolean flag1) {
      this.acB = flag;
      this.Ej.b(flag);
      if (flag1) {
         if (flag) {
            SoundManager.getInstance().c(SoundType.TOGGLE_ON);
            return;
         }

         SoundManager.getInstance().c(SoundType.TOGGLE_OFF);
      }
   }

   public void c(Runnable runnable) {
      this.alF = runnable;
   }

   public boolean d() {
      return this.acB;
   }
}
