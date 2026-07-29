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