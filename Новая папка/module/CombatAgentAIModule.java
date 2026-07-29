package module;

import combatagentai.BezierRotationStrategy;
import combatagentai.GaussianRotationStrategy;
import combatagentai.InteractionRateController;
import combatagentai.RaycastValidator;
import combatagentai.RotationStrategy;
import data.Angle;
import enum.Category;
import event.RotationEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import setting.BooleanSetting;
import setting.ListSetting;
import setting.SliderSetting;
import util.RotationUtil;
import core.ClientMain;

public class CombatAgentAIModule extends Module {
   private static final String ROTATION_BEZIER = "Bezier";
   private static final String ROTATION_GAUSSIAN = "Gaussian";
   private static final String TIMING_SKEWED = "Skewed";
   private static final String TIMING_MARKOV = "Markov";

   private final ListSetting rotationModeSetting = new ListSetting("Поворот", "Алгоритм поворота", Arrays.asList(ROTATION_BEZIER, ROTATION_GAUSSIAN), List.of(ROTATION_BEZIER), false);
   private final SliderSetting rotationSpeedSetting = new SliderSetting("Скорость", "Скорость поворота", 1.0, 0.1, 3.0, 0.05);
   private final SliderSetting yawJitterSetting = new SliderSetting("Шум Yaw", "Амплитуда шума (град)", 0.8, 0.1, 3.0, 0.1);
   private final SliderSetting pitchJitterSetting = new SliderSetting("Шум Pitch", "Амплитуда шума (град)", 0.4, 0.1, 2.0, 0.1);
   private final ListSetting timingModeSetting = new ListSetting("Тайминг", "Алгоритм интервалов", Arrays.asList(TIMING_SKEWED, TIMING_MARKOV), List.of(TIMING_SKEWED), false);
   private final SliderSetting minClickDelaySetting = new SliderSetting("Мин. задержка", "Минимальная задержка (мс)", 250.0, 50.0, 1000.0, 10.0);
   private final SliderSetting maxClickDelaySetting = new SliderSetting("Макс. задержка", "Максимальная задержка (мс)", 800.0, 100.0, 2000.0, 10.0);
   private final BooleanSetting raycastValidationSetting = new BooleanSetting("Raycast", "Проверка видимости", true);
   private final BooleanSetting entityValidationSetting = new BooleanSetting("Валидация сущ.", "Фильтр некорректных сущностей", true);
   private final SliderSetting attackRangeSetting = new SliderSetting("Дистанция", "Радиус атаки", 4.0, 2.0, 10.0, 0.1);
   private final BooleanSetting onlyWithWeaponSetting = new BooleanSetting("Только оружие", "Требовать оружие", true);
   private final BooleanSetting antiCheatBypassSetting = new BooleanSetting("Обход античита", "Добавлять случайный шум", true);

   private final Random random = new Random();
   private final RotationStrategy bezierStrategy = new BezierRotationStrategy();
   private final RotationStrategy gaussianStrategy = new GaussianRotationStrategy();
   private final RaycastValidator raycastValidator = new RaycastValidator();
   private InteractionRateController rateController;

   private LivingEntity targetEntity;
   private long nextAttackTime = 0L;

   public CombatAgentAIModule() {
      super("CombatAgentAI", "AI-агент для стресс-тестирования сервера", Category.COMBAT);
      this.rateController = new InteractionRateController(250L, 800L);
      this.addSettings(
         this.rotationModeSetting,
         this.rotationSpeedSetting,
         this.yawJitterSetting,
         this.pitchJitterSetting,
         this.timingModeSetting,
         this.minClickDelaySetting,
         this.maxClickDelaySetting,
         this.raycastValidationSetting,
         this.entityValidationSetting,
         this.attackRangeSetting,
         this.onlyWithWeaponSetting,
         this.antiCheatBypassSetting
      );
   }

   @Override
   public void onEnable() {
      this.resetState();
      this.rateController = new InteractionRateController(
         (long) this.minClickDelaySetting.getValue(),
         (long) this.maxClickDelaySetting.getValue()
      );
   }

   @Override
   public void onDisable() {
      this.resetState();
   }

   private void resetState() {
      this.targetEntity = null;
      this.nextAttackTime = 0L;
      this.bezierStrategy.reset();
      this.gaussianStrategy.reset();
   }

   @Override
   public void onRotation(RotationEvent rotationEvent) {
      if (!this.isEnabled() || !this.hasPlayerAndWorld()) {
         return;
      }

      PlayerEntity player = this.getPlayer();
      Vec3d eyePos = player.getEyePos();

      LivingEntity target = this.findTarget(player, eyePos);
      if (target != null) {
         this.targetEntity = target;
         this.processRotation(player, target, rotationEvent);

         if (this.shouldAttack()) {
            this.performAttack(target);
         }
      } else {
         this.targetEntity = null;
      }
   }

   private LivingEntity findTarget(PlayerEntity player, Vec3d eyePos) {
      double range = this.attackRangeSetting.getValue();
      List<LivingEntity> candidates = new ArrayList<>();

      for (Entity entity : this.getClientWorld().getEntities()) {
         if (!(entity instanceof LivingEntity)) continue;
         if (entity == player) continue;

         LivingEntity living = (LivingEntity) entity;
         if (!this.isValidTarget(living, player)) continue;

         double distance = player.distanceTo(living);
         if (distance > range) continue;

         if (this.entityValidationSetting.getValue() && !this.isValidEntity(living)) {
            continue;
         }

         if (this.raycastValidationSetting.getValue()) {
            Vec3d targetPos = this.getTargetPosition(living);
            if (!this.raycastValidator.isVisible(eyePos, player.getRotationVec(1.0F), targetPos, this.getClientWorld())) {
               continue;
            }
         }

         candidates.add(living);
      }

      if (candidates.isEmpty()) return null;

      return candidates.stream()
         .min((a, b) -> Double.compare(player.distanceTo(a), player.distanceTo(b)))
         .orElse(null);
   }

   private boolean isValidTarget(LivingEntity entity, PlayerEntity player) {
      if (!entity.isAlive() || entity.isDead()) return false;
      if (entity.getHealth() <= 0.0F) return false;
      if (entity instanceof PlayerEntity) {
         if (this.isFriend(entity)) return false;
      }
      return true;
   }

   private boolean isValidEntity(LivingEntity entity) {
      if (entity.getVehicle() != null) return true;
      return entity.getHealth() > 0.0F;
   }

   private boolean isFriend(Entity entity) {
      try {
         return ClientMain.getInstance().getFriendManager().isFriend(entity);
      } catch (Exception e) {
         return false;
      }
   }

   private Vec3d getTargetPosition(LivingEntity entity) {
      return entity.getPos().add(0.0, entity.getHeight() * 0.7, 0.0);
   }

   private void processRotation(PlayerEntity player, LivingEntity target, RotationEvent rotationEvent) {
      Vec3d eyePos = player.getEyePos();
      Vec3d targetPos = this.getTargetPosition(target);
      Vec3d direction = targetPos.subtract(eyePos);

      Angle targetRotation = RotationUtil.getRotation(direction);
      Angle currentRotation = new Angle(player.getYaw(), player.getPitch());

      RotationStrategy strategy = ROTATION_BEZIER.equals(this.rotationModeSetting.getSelectedValues().getFirst())
         ? this.bezierStrategy : this.gaussianStrategy;

      Angle finalRotation = strategy.calculateRotation(currentRotation, targetRotation, this.random, this.rotationSpeedSetting.getValue());

      if (this.antiCheatBypassSetting.getValue()) {
         this.applyHumanJitter(finalRotation);
      }

      rotationEvent.setYaw(finalRotation.getYaw());
      rotationEvent.setPitch(finalRotation.getPitch());
   }

   private void applyHumanJitter(Angle rotation) {
      float yawJitter = (float) (this.random.nextGaussian() * this.yawJitterSetting.getValue());
      float pitchJitter = (float) (this.random.nextGaussian() * this.pitchJitterSetting.getValue());
      rotation.setYaw(rotation.getYaw() + yawJitter);
      rotation.setPitch(MathHelper.clamp(rotation.getPitch() + pitchJitter, -90.0F, 90.0F));
   }

   private boolean shouldAttack() {
      long now = System.currentTimeMillis();
      if (this.targetEntity == null) return false;
      if (this.onlyWithWeaponSetting.getValue() && !this.hasValidWeapon()) return false;
      return now >= this.nextAttackTime;
   }

   private void performAttack(LivingEntity target) {
      if (!this.hasValidWeapon()) return;

      this.getInteractionManager().attackEntity(this.getPlayer(), target);
      this.getPlayer().swingHand(Hand.MAIN_HAND);

      long delay = this.rateController.calculateRequiredDelay();
      this.nextAttackTime = System.currentTimeMillis() + delay;
   }

   private boolean hasValidWeapon() {
      ItemStack stack = this.getPlayer().getMainHandStack();
      if (stack.isEmpty()) return false;
      Item item = stack.getItem();
      return item instanceof SwordItem;
   }

   @Override
   public void onEndTick() {
      if (this.isEnabled() && this.targetEntity != null) {
         if (!this.targetEntity.isAlive() || this.targetEntity.isDead()) {
            this.targetEntity = null;
         }
      }
   }
}