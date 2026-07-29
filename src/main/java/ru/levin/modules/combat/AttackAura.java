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