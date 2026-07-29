package combatagentai;

import data.Angle;
import java.util.Random;

public interface RotationStrategy {
   Angle calculateRotation(Angle current, Angle target, Random random, float speed);
   void reset();
}