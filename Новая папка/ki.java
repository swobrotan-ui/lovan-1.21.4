import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import util.UnsafeFieldAccessor;

public class ki {
   private OtherClientPlayerEntity nT;
   private Vec3d Mk;
   private float cx;
   private float ht;
   private float ahx;
   private float lZ;
   private boolean Y;
   private boolean afm;
   private float aan;
   private float wW;
   private float Rw;
   private float Zu;
   private boolean lx;
   private int Md;
   private Hand aV;
   private float Qu;
   private EntityPose mc;
   private int fP;
   private final MinecraftClient asE = MinecraftClient.getInstance();
   private UnsafeFieldAccessor<Float> EC;
   private UnsafeFieldAccessor<Float> ayq;
   private UnsafeFieldAccessor<Float> sX;
   private UnsafeFieldAccessor<Float> bk;
   private UnsafeFieldAccessor<Float> HP;
   private UnsafeFieldAccessor<Float> acq;

   private void a(LimbAnimator limbanimator) {
      this.EC = new UnsafeFieldAccessor<Float>(limbanimator, LimbAnimator.class, 0);
      this.ayq = new UnsafeFieldAccessor<Float>(limbanimator, LimbAnimator.class, 1);
      this.sX = new UnsafeFieldAccessor<Float>(limbanimator, LimbAnimator.class, 2);
      this.bk = new UnsafeFieldAccessor<Float>(limbanimator, LimbAnimator.class, 3);
   }

   private void b(LivingEntity livingentity) {
      this.HP = new UnsafeFieldAccessor<Float>(livingentity, LivingEntity.class, 103);
      this.acq = new UnsafeFieldAccessor<Float>(livingentity, LivingEntity.class, 104);
   }

   public void c(AbstractClientPlayerEntity abstractclientplayerentity) {
      if (abstractclientplayerentity != null && this.asE.world != null) {
         this.Mk = abstractclientplayerentity.getPos();
         this.cx = abstractclientplayerentity.getYaw();
         this.ht = abstractclientplayerentity.getPitch();
         this.ahx = abstractclientplayerentity.bodyYaw;
         this.lZ = abstractclientplayerentity.headYaw;
         this.Y = abstractclientplayerentity.isSneaking();
         this.afm = abstractclientplayerentity.isSprinting();
         this.a(abstractclientplayerentity.limbAnimator);
         this.b(abstractclientplayerentity);
         this.wW = abstractclientplayerentity.limbAnimator.getSpeed();

         try {
            this.aan = this.sX.getFloat();
            this.Rw = this.bk.getFloat();
         } catch (Exception exception1) {
            this.aan = 0.0F;
            this.Rw = 1.0F;
         }

         this.Zu = abstractclientplayerentity.handSwingProgress;
         this.lx = abstractclientplayerentity.handSwinging;
         this.Md = abstractclientplayerentity.handSwingTicks;
         this.aV = abstractclientplayerentity.getActiveHand();

         try {
            this.Qu = this.HP.getFloat();
         } catch (Exception exception) {
            this.Qu = 0.0F;
         }

         this.mc = abstractclientplayerentity.getPose();
         this.fP = abstractclientplayerentity.age;
         this.d(abstractclientplayerentity, this.asE.world);
      }
   }

   private void d(AbstractClientPlayerEntity abstractclientplayerentity, ClientWorld clientworld) {
      if (this.nT == null) {
         GameProfile gameprofile = abstractclientplayerentity.getGameProfile();
         this.nT = new OtherClientPlayerEntity(clientworld, gameprofile);
         this.e();
         this.f(abstractclientplayerentity);
      }
   }

   private void e() {
      if (this.nT != null) {
         this.nT.setPosition(this.Mk);
         this.nT.prevX = this.Mk.x;
         this.nT.prevY = this.Mk.y;
         this.nT.prevZ = this.Mk.z;
         this.nT.lastRenderX = this.Mk.x;
         this.nT.lastRenderY = this.Mk.y;
         this.nT.lastRenderZ = this.Mk.z;
         this.nT.setYaw(this.cx);
         this.nT.setPitch(this.ht);
         this.nT.prevYaw = this.cx;
         this.nT.prevPitch = this.ht;
         this.nT.bodyYaw = this.ahx;
         this.nT.prevBodyYaw = this.ahx;
         this.nT.headYaw = this.lZ;
         this.nT.prevHeadYaw = this.lZ;
         this.nT.setSneaking(this.Y);
         this.nT.setSprinting(this.afm);
         this.nT.setPose(this.mc);
         this.nT.age = this.fP;
         this.a(this.nT.limbAnimator);
         this.b(this.nT);

         try {
            float f = this.aan + this.wW;
            this.sX.setFloat(f);
            this.ayq.setFloat(this.wW);
            this.EC.setFloat(this.wW);
            this.bk.setFloat(this.Rw);
         } catch (Exception exception1) {
            exception1.printStackTrace();
         }

         this.nT.handSwingProgress = this.Zu;
         this.nT.lastHandSwingProgress = this.Zu;
         this.nT.handSwinging = this.lx;
         this.nT.handSwingTicks = this.Md;
         this.nT.preferredHand = this.aV;
         this.nT.clearActiveItem();

         try {
            this.HP.setFloat(this.Qu);
            this.acq.setFloat(this.Qu);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   private void f(AbstractClientPlayerEntity abstractclientplayerentity) {
      if (this.nT != null) {
         for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            this.nT.equipStack(equipmentslot, abstractclientplayerentity.getEquippedStack(equipmentslot).copy());
         }
      }
   }

   public void g(WorldRenderContext worldrendercontext) {
      if (this.nT != null && this.Mk != null && this.asE.world != null) {
         MatrixStack matrixstack = worldrendercontext.matrixStack();
         Vec3d vec3d = worldrendercontext.camera().getPos();
         float f = 0.0F;
         this.e();
         double d0 = this.Mk.x - vec3d.x;
         double d1 = this.Mk.y - vec3d.y;
         double d2 = this.Mk.z - vec3d.z;
         EntityRenderDispatcher entityrenderdispatcher = this.asE.getEntityRenderDispatcher();
         Immediate immediate = this.asE.getBufferBuilders().getEntityVertexConsumers();
         int i = entityrenderdispatcher.getLight(this.nT, f);
         entityrenderdispatcher.render(this.nT, d0, d1, d2, f, matrixstack, immediate, i);
         immediate.draw();
      }
   }

   public void h() {
      this.nT = null;
      this.Mk = null;
   }

   public boolean i() {
      return this.nT != null && this.Mk != null;
   }
}
