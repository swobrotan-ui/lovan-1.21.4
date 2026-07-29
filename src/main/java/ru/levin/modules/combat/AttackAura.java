package ru.levin.modules.combat;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.events.impl.input.EventMouse;
import ru.levin.events.impl.render.EventRender3D;
import ru.levin.events.impl.input.EventKeyBoard;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.events.impl.player.EventSprint;
import ru.levin.manager.Manager;
import ru.levin.mixin.iface.ClientPlayerEntityAccessor;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.movement.ElytraTarget;
import ru.levin.modules.render.littlePet.GhostWolfEntity;
import ru.levin.modules.setting.BindBooleanSetting;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.MultiSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.math.RayTraceUtil;
import ru.levin.util.move.MoveUtil;
import ru.levin.util.player.AuraUtil;
import ru.levin.util.math.PerlinNoise;
import ru.levin.util.player.GCDUtil;
import ru.levin.util.render.Render3DUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import static net.minecraft.util.Hand.MAIN_HAND;

@SuppressWarnings("All")
@FunctionAnnotation(name = "AttackAura", keywords = {"Пиздить","Хуячить","KillAura"}, desc = "Ебашит бошки всем вокруг", type = Type.Combat)
public class AttackAura extends Function {

    private static final long MIN_HIT_INTERVAL_MS = 40L;

    private final ModeSetting mode = new ModeSetting("Мод",
            "HolyWorld",
            "HolyWorld",
            "AI",
            "SpookyTime",
            "FunTime snap",
            "FunTime",
            "ReallyWorld",
            "1.8.9"
    );

    private final MultiSetting targets = new MultiSetting(
            "Кого атаковать",
            Collections.emptyList(),
            new String[]{"Игроков", "Мобов", "Жителей", "Невидимых", "Голых"}
    );

    private final ModeSetting sort = new ModeSetting("Приоритет по",
            "Дистанции",
            "Дистанции",
            "Здоровью",
            "Броне",
            "Поле зрения",
            "Всему сразу"
    );

    private final MultiSetting setting = new MultiSetting(
            "Настройки",
            Arrays.asList("Только критами", "Ломать щит", "Отжим щита"),
            new String[]{"Только критами", "Ломать щит", "Отжим щита"}
    );

    private final SliderSetting distance = new SliderSetting("Радиус атаки", 3.0f, 1.8f, 6f, 0.1f);
    private final SliderSetting rotateDistance = new SliderSetting("Радиус обнаружения", 5f, 0.0f, 10f, 0.1f);

    private final SliderSetting elytraDistance = new SliderSetting("Радиус на элитрах", 40f, 0f, 80f, 1f);

    private final BindBooleanSetting onlySpaceCritical = new BindBooleanSetting("Только с пробелом", false, () -> setting.get("Только критами"));
    private final ModeSetting critMode = new ModeSetting(() -> setting.get("Только критами"), "Режим критов",
            "Только криты",
            "Только криты",
            "Только криты умные"
    );
    private final BooleanSetting noAttackIfEat = new BooleanSetting("Не бить если ешь", false);
    private final BooleanSetting raycast = new BooleanSetting("Проверять наведение", false);
    private final BooleanSetting wallAttack = new BooleanSetting("Бить через стены", false);

    private final SliderSetting aiReactionMin = new SliderSetting(
            "AI реакция мин.",
            55f,
            0f,
            350f,
            5f,
            () -> mode.is("AI")
    );
    private final SliderSetting aiReactionMax = new SliderSetting(
            "AI реакция макс.",
            120f,
            0f,
            600f,
            5f,
            () -> mode.is("AI")
    );
    private final SliderSetting aiPredict = new SliderSetting(
            "AI предикт",
            2.2f,
            0f,
            6f,
            0.1f,
            () -> mode.is("AI")
    );

    public final BooleanSetting correction = new BooleanSetting("Коррекция", true);
    public final ModeSetting correctionType = new ModeSetting(() -> correction.get(), "Коррекция движения", "Свободная", "Свободная", "Преследование", "Таргетный");
    private final ModeSetting sprintreset = new ModeSetting("Тип спринта", "FunTime", "FunTime", "Legit", "None");

    public LivingEntity target = null;
    private long cpsLimit = 0L;
    private long lastHitMs = 0L;
    private long nextClickTime = 0L;
    private boolean skipNextClick = false;
    private int preSprintTicks = 0;

    private int sprintStartDelayTicks = 0;
    private boolean sprintStartPlanned = false;
    private boolean sprintWasActiveBeforeHit = false;

    private boolean critLocked = false;

    private int aiLastTargetId = -1;
    private long aiReactUntilMs = 0L;
    private boolean aiSideRight = true;
    private long aiLastSideSwitchMs = 0L;

    private boolean spookySideRight = true;
    private long spookyLastSideSwitchMs = 0L;

    private boolean aiLearning = false;
    private final AimStats aiStats = new AimStats();

    private static final File AI_DIR = new File(mc.runDirectory, "files\\ai");
    private final Gson aiGson = new GsonBuilder().setPrettyPrinting().create();
    private String aiProfileName = null;
    private AimProfile aiProfile = null;

    private static final int AI_TRAIN_MAX_HITS = 200;
    private static final double AI_TRAIN_RADIUS = 3.0;
    private static final int AI_TRAIN_TARGETS = 3;
    private final List<Box> aiTrainBoxes = new ArrayList<>();
    private int aiTrainHits = 0;
    private boolean aiAutoEnabled = false;

    private float aiLastPlayerYaw = 0f;
    private float aiLastPlayerPitch = 0f;
    private boolean aiHasLastPlayerRot = false;

    private float funtimeSwingPhase = 0f;
    private int funtimeAimTicks = 0;

    private float funtimeLastPlayerYaw = 0f;
    private float funtimeLastPlayerPitch = 0f;

    private int funtimeLastSwingSign = 0;
    private float funtimePitchDrop = 0f;

    private boolean funtimeRestorePending = false;
    private float funtimeReturnYaw = 0f;
    private float funtimeReturnPitch = 0f;

    private int funtimePrevTargetId = -1;
    private int funtimeSwitchTicks = 0;
    private long funtimeReactionDelayMs = 0L;
    private long funtimeAwarenessDelayMs = 0L;

    private int funtimeConsecutiveHits = 0;
    private long funtimePostMaxCooldownMs = 0L;
    private long funtimeLastHitTimeMs = 0L;
    private static final int FUNTIME_MAX_CONSECUTIVE_HITS = 8;
    private static final long FUNTIME_POST_MAX_COOLDOWN_MS = 20L;
    private static final float FUNTIME_CRIT_BASE_INTERVAL_MS = 90f;
    private static final float FUNTIME_CRIT_MIN_INTERVAL_MS = 35f;
    private static final float FUNTIME_CRIT_INTERVAL_DECAY = 8f;

    private float funtimeAimSpeedYaw = 0f;
    private float funtimeAimSpeedPitch = 0f;
    private float funtimeLastTargetYaw = 0f;
    private float funtimeLastTargetPitch = 0f;

    private float funtimeSmoothYaw = 0.40f;
    private float funtimeSmoothPitch = 0.30f;
    private long funtimeLastSmoothUpdate = 0L;
    private boolean funtimeHasGroundRef = false;
    private double funtimeGroundBodyY = 0.0d;
    private int funtimeBodyPointIndex = 0;
    private int funtimeBodyPointTicks = 0;
    private int funtimeScanTicks = 0;
    private int funtimeSnapTicks = 0;
    private int funtimeAfkTicks = 0;
    private float funtimePostHitDriftYaw = 0f;
    private float funtimePostHitDriftPitch = 0f;
    private float funtimeBreathPhase = 0f;
    private double funtimeLastDist = 0d;

    // Neuro fields for SpookyTime
    private PerlinNoise neuroNoise;
    private long neuroStartTime;
    private float neuroFactor;

    private float spookyAimY = 0.55f;
    private float spookyAimYTarget = 0.55f;
    private long spookyAimNextSwitchMs = 0L;

    private float spookySwingPhase = 0f;
    private int spookyLastSwingSign = 0;
    private float spookyPitchDrop = 0f;

    private float spookySunriseYawStatic = 0f;
    private float spookySunrisePitchStatic = 0f;

    // Perlin noise for FunTime
    private PerlinNoise funtimeNoise;
    private double funtimeNoiseTime = 0.0;

    public AttackAura() {
        addSettings(
                mode,
                targets,
                sort,
                setting,
                distance,
                rotateDistance,
                elytraDistance,
                correction,
                correctionType,
                sprintreset,
                onlySpaceCritical,
                critMode,
                noAttackIfEat,
                raycast,
                wallAttack,
                aiReactionMin,
                aiReactionMax,
                aiPredict
        );
    }

    @Override
    public void onEvent(Event event) {
        ClientPlayerEntity player = mc.player;
        if (player == null || player.isDead()) {
            target = null;
            preSprintTicks = 0;
            sprintStartDelayTicks = 0;
            sprintStartPlanned = false;
            sprintWasActiveBeforeHit = false;
            funtimePrevTargetId = -1;
            funtimeSwitchTicks = 0;
            funtimeReactionDelayMs = 0L;
            funtimeAwarenessDelayMs = 0L;
            funtimeConsecutiveHits = 0;
            funtimeAimSpeedYaw = 0f;
            funtimeAimSpeedPitch = 0f;
            funtimeLastTargetYaw = 0f;
            funtimeLastTargetPitch = 0f;
            funtimeSmoothYaw = 0.40f;
            funtimeSmoothPitch = 0.30f;
            funtimeLastSmoothUpdate = 0L;
            funtimeHasGroundRef = false;
            funtimeBodyPointIndex = 0;
            funtimeBodyPointTicks = 0;
            funtimeBreathPhase = 0f;
            funtimeLastDist = 0d;
            funtimeNoiseTime = 0.0;
            return;
        }

        if (event instanceof EventRender3D render3D) {
            renderAITargets(render3D);
        }

        if (event instanceof EventMouse mouse) {
            onMouseAITarget(mouse);
        }

        if (event instanceof EventKeyBoard e) {
            if (correction.get()) {
                float basisYaw = Manager.ROTATION.getYaw();

                if (correctionType.is("Свободная")) {
                    MoveUtil.fixMovement(e, basisYaw);
                } else if (correctionType.is("Преследование")) {
                    if (target != null && isValidTarget(target)) {
                        float desiredYaw = getYawToTarget(target);
                        applyMovementTowardsYaw(e, basisYaw, desiredYaw, true);
                    }
                } else if (correctionType.is("Таргетный")) {
                    boolean pressingW = e.getMovementForward() > 0;
                    if (pressingW && target != null && isValidTarget(target)) {
                        float desiredYaw = getYawToTarget(target);
                        applyMovementTowardsYaw(e, basisYaw, desiredYaw, false);
                    }
                }
            }
        }

        if (event instanceof EventSprint sprint) {
            if (sprintreset.is("Legit")) {
                if (target != null && shouldAttack(target) && player.isSprinting()) {
                    sprint.setSprinting(false);
                }
            }
        }

        if (event instanceof EventUpdate) {
            trackAIMimicLearning();

            if (sprintStartPlanned) {
                if (sprintStartDelayTicks > 0) {
                    sprintStartDelayTicks--;
                }

                if (sprintStartDelayTicks <= 0) {
                    sprintStartPlanned = false;
                    if (sprintWasActiveBeforeHit) {
                        boolean canStartSprint = mc.player.input.movementForward > 0
                                && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                                && !mc.player.isGliding()
                                && !mc.player.isUsingItem()
                                && !mc.player.horizontalCollision
                                && mc.player.getHungerManager().getFoodLevel() > 6
                                && !mc.player.isSneaking();

                        if (canStartSprint) {
                            mc.player.networkHandler.send