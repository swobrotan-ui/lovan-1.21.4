package aim;

import data.Angle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public abstract class AimMode {
   protected final String name;
   protected float speed = 3.0F;
   protected static final MinecraftClient mc = MinecraftClient.getInstance();

   public AimMode(String s) {
      this.name = s;
   }

   public abstract Angle calculateRotation(Angle angle, Angle angle1, Vec3d vec3d, Entity entity);

   public String getName() {
      return this.name;
   }

   public float getSpeed() {
      return this.speed;
   }
}
