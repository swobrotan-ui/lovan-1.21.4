import java.util.ArrayList;
import shader.BlurShaderResource;
import shader.BorderShaderResource;
import shader.ColorPickerShaderResource;
import shader.CornerBracketShaderResource;
import shader.EntityOutlineResources;
import shader.Line3dShaderResource;
import shader.LineShaderResource;
import shader.LiquidGlassShaderResource;
import shader.MSDFFontShaderResource;
import shader.OutlineShaderResource;
import shader.RectangleShaderResource;
import shader.ShaderCache;
import shader.TextureShaderResource;
import shader.WhiteRectangleShaderResource;

public class ltw {
   public ltw(we we) {
      ShaderCache.a();
      ArrayList arraylist = new ArrayList();
      arraylist.addAll(new BorderShaderResource().getResources());
      arraylist.addAll(new RectangleShaderResource().getResources());
      arraylist.addAll(new WhiteRectangleShaderResource().getResources());
      arraylist.addAll(new OutlineShaderResource().getResources());
      arraylist.addAll(new LineShaderResource().getResources());
      arraylist.addAll(new Line3dShaderResource().getResources());
      arraylist.addAll(new TextureShaderResource().getResources());
      arraylist.addAll(new BlurShaderResource().getResources());
      arraylist.addAll(new MSDFFontShaderResource().getResources());
      arraylist.addAll(new ColorPickerShaderResource().getResources());
      arraylist.addAll(new EntityOutlineResources().getResources());
      arraylist.addAll(new CornerBracketShaderResource().getResources());
      arraylist.addAll(new LiquidGlassShaderResource().getResources());
      arraylist.addAll(new uz().b());
      arraylist.forEach(we::addResource);
   }
}
