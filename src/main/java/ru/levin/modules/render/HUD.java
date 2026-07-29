package ru.levin.modules.render;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.gl.ShaderProgramKeys;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import ru.levin.ExosWare;
import ru.levin.mixin.iface.ItemCooldownEntryAccessor;
import ru.levin.mixin.iface.ItemCooldownManagerAccessor;
import ru.levin.mixin.iface.BossBarHudAccessor;
import ru.levin.modules.setting.*;
import ru.levin.events.Event;
import ru.levin.events.impl.input.EventMouse;
import ru.levin.events.impl.EventUpdate;
import ru.levin.events.impl.player.EventPlayerHurt;
import ru.levin.events.impl.render.EventRender2D;
import ru.levin.events.impl.EventPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import ru.levin.manager.ClientManager;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.manager.dragManager.Dragging;
import ru.levin.manager.notificationManager.Notification;
import ru.levin.manager.notificationManager.NotificationType;
import ru.levin.manager.notificationManager.NotificationManager;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.misc.FTHelper;
import ru.levin.modules.misc.HWHelper;
import ru.levin.util.animations.Animation;
import ru.levin.util.animations.impl.EaseBackIn;
import ru.levin.util.animations.impl.DecelerateAnimation;
import ru.levin.util.color.ColorUtil;
import ru.levin.manager.fontManager.FontUtils;
import ru.levin.manager.fontManager.RenderFonts;
import ru.levin.util.math.MathUtil;
import ru.levin.util.player.AudioUtil;
import ru.levin.util.render.RenderAddon;
import ru.levin.util.render.RenderUtil;
import ru.levin.util.render.Scissor;

import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import dev.redstones.mediaplayerinfo.impl.win.WindowsMediaPlayerInfo;

import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import static ru.levin.util.color.ColorUtil.hud_color;
import static ru.levin.util.render.RenderUtil.*;

@SuppressWarnings("All")
@FunctionAnnotation(name = "HUD", desc = "Интерфейс клиента", type = Type.Render)
public class HUD extends Function {

    private static final Identifier HUD_KEYBINDS_ICON = Identifier.of("sodiumextra", "images/hud/keybinds.png");
    private boolean hudKeybindsIconFiltered = false;

    private final java.util.ArrayList<Function> enabledModsTmp = new java.util.ArrayList<>(64);
    private final java.util.Map<String, DecelerateAnimation> activeModAnims = new java.util.HashMap<>();
    private final java.util.ArrayList<StatusEffectInstance> potionEffectsTmp = new java.util.ArrayList<>(32);
    public final MultiSetting setting = new MultiSetting(
            "Элементы",
            Arrays.asList("WaterMark", "HUDNew", "TargetHUD", "KeyBinds", "StaffList", "PotionHUD", "ItemCoolDownHUD", "Coordinates / TPS","ArmorHUD", "Notifications", "Медиаплеер (мощные пк)", "SwapHelpers", "ActiveMods", "Sensitivity"),
            new String[]{"WaterMark", "HUDNew", "TargetHUD", "KeyBinds", "StaffList", "PotionHUD", "ItemCoolDownHUD", "Coordinates / TPS","ArmorHUD", "Notifications", "Медиаплеер (мощные пк)", "SwapHelpers", "ActiveMods", "Sensitivity"});

    private final SliderSetting mouseSensDpi = new SliderSetting("Мышь DPI", 800, 100, 16000, 50, () -> setting.get("Sensitivity"));

    private final MultiSetting watermarkParts = new MultiSetting(
            () -> setting.get("WaterMark"),
            "WaterMark",
            Arrays.asList("Логотип", "Ник", "FPS", "Координаты", "Пинг"),
            new String[]{"Логотип", "Ник", "FPS", "Координаты", "Пинг"}
    );

    private final ModeSetting watermarkPosition = new ModeSetting(
            () -> setting.get("WaterMark"),
            "Позиция WaterMark",
            "Слева",
            "Слева",
            "По центру"
    );


    private final ModeSetting hudStyle = new ModeSetting(
            () -> setting.get("WaterMark") || setting.get("HUDNew"),
            "Стиль HUD",
            "Водяной",
            "Водяной",
            "Инфобар"
    );

    private final ModeSetting hudMode = new ModeSetting(
            () -> setting.get("HUDNew"),
            "Режим HUD",
            "HUD1",
            "HUD1",
            "HUD2"
    );

    public final ModeSetting hudColor = new ModeSetting("Цвет худа","Обычный","Обычный","Зависит от темы","Тема HUD");
    private final ModeSetting gradientType = new ModeSetting(() -> hudColor.is("Зависит от темы") || hudColor.is("Тема HUD"),"Тип градиента", "Слева направо", "Слева направо", "Справа налево");
    private final ModeSetting hudTheme = new ModeSetting(() -> hudColor.is("Тема HUD"), "Тема HUD",
            "Аметист",
            "Аметист",
            "Океан",
            "Кислотный",
            "Закат",
            "Розовый",
            "Лайм",
            "Кровавый"
    );

    final SliderSetting customAlpha = new SliderSetting("Прозрачность", 120, 120, 255, 5);
    public final BooleanSetting visibleCrosshair = new BooleanSetting("Показывать TargetHUD при навидении", false, "показывает таргетхуд при навидении на игрока", () -> setting.get("TargetHUD"));
    public final BooleanSetting blur = new BooleanSetting("Размытие", false, "Рендерит размытие на все элементы худа");
    private final SliderSetting roundingSilaSanya = new SliderSetting("Закругление головы", 2f, 0f, 12f, 1f);
    private static final Pattern NAME_PATTERN = Pattern.compile("^\\w{3,16}$");
    private static final Pattern PREFIX_MATCHES = Pattern.compile(".*(mod|мод|adm|адм|help|хелп|curat|курат|own|овн|dev|supp|сапп|yt|ют|сотруд).*", Pattern.CASE_INSENSITIVE);

    private static final Item[] TRACKED_ITEMS = {
            Items.ENDER_PEARL, Items.CHORUS_FRUIT, Items.FIREWORK_ROCKET, Items.SHIELD,
            Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.TOTEM_OF_UNDYING,
            Items.SNOWBALL, Items.DRIED_KELP, Items.ENDER_EYE, Items.NETHERITE_SCRAP,
            Items.EXPERIENCE_BOTTLE, Items.PHANTOM_MEMBRANE
    };

    private static final Map<Item, String> ITEM_NAMES;
    static {
        Map<Item, String> tmp = new HashMap<>(16);
        tmp.put(Items.ENDER_PEARL, "Эндер-жемчюг");
        tmp.put(Items.CHORUS_FRUIT, "Хорус");
        tmp.put(Items.FIREWORK_ROCKET, "Фейрверк");
        tmp.put(Items.SHIELD, "Щит");
        tmp.put(Items.GOLDEN_APPLE, "Золотое яблоко");
        tmp.put(Items.ENCHANTED_GOLDEN_APPLE, "Чарка");
        tmp.put(Items.TOTEM_OF_UNDYING, "Тотем");
        tmp.put(Items.SNOWBALL, "Снежок");
        tmp.put(Items.DRIED_KELP, "Пласт");
        tmp.put(Items.ENDER_EYE, "Дезориентация");
        tmp.put(Items.NETHERITE_SCRAP, "Трапка");
        tmp.put(Items.EXPERIENCE_BOTTLE, "Пузырёк опыта");
        tmp.put(Items.PHANTOM_MEMBRANE, "Аура");
        ITEM_NAMES = Collections.unmodifiableMap(tmp);
    }

    public HUD() {
        addSettings(setting, hudMode, hudColor, gradientType, hudTheme, customAlpha, visibleCrosshair, blur, roundingSilaSanya, mouseSensDpi);
    }

    private int[] getHudThemeColors() {
        return switch (hudTheme.get()) {
            case "Океан" -> new int[]{0xFF0077BE, 0xFF00B4D8};
            case "Кислотный" -> new int[]{0xFFCCFF00, 0xFF00FF00};
            case "Закат" -> new int[]{0xFFFF7D00, 0xFFFFD700};
            case "Розовый" -> new int[]{0xFFFF2DAA, 0xFFFF6BD6};
            case "Лайм" -> new int[]{0xFF6AFF00, 0xFF00FFA8};
            case "Кровавый" -> new int[]{0xFF8B0000, 0xFFFF3344};
            default -> new int[]{0xFF9B6EFF, 0xFF5433FF};
        };
    }

    private int hudAccent(float index, int alpha) {
        if (isMonoTheme() && !hudColor.is("Тема HUD")) {
            return hudAccentStatic(alpha);
        }
        if (hudColor.is("Тема HUD")) {
            int[] c = getHudThemeColors();
            int c1 = c[0];
            int c2 = c[1];
            if (gradientType.is("Справа налево")) {
                int tmp = c1;
                c1 = c2;
                c2 = tmp;
            }
            int rgb = ColorUtil.gradient(5, (int) index, c1, c2);
            return ColorUtil.withAlpha(rgb, alpha / 255f);
        }
        if (hudColor.is("Зависит от темы") || isClientTheme()) {
            int c1 = Manager.STYLE_MANAGER.getFirstColor();
            int c2 = Manager.STYLE_MANAGER.getSecondColor();
            if (gradientType.is("Справа налево")) {
                int tmp = c1;
                c1 = c2;
                c2 = tmp;
            }
            int rgb = ColorUtil.gradient(5, (int) index, c1, c2);
            return ColorUtil.withAlpha(rgb, alpha / 255f);
        }
        return ColorUtil.withAlpha(Manager.STYLE_MANAGER.getFirstColor(), alpha / 255f);
    }

    private boolean useHudTheme() {
        return hudColor.is("Зависит от темы") || hudColor.is("Тема HUD") || isMonoTheme() || isClientTheme();
    }

    private boolean isMonoTheme() {
        return false;
    }

    private boolean isClientTheme() {
        if (Manager.STYLE_MANAGER.getTheme() == null) {
            return false;
        }
        return "Клиентский".equalsIgnoreCase(Manager.STYLE_MANAGER.getTheme().name);
    }

    private boolean isTurquoiseTheme() {
        if (Manager.STYLE_MANAGER.getTheme() == null) {
            return false;
        }
        String name = Manager.STYLE_MANAGER.getTheme().name;
        if (name == null) return false;
        name = name.trim().replace(" ", "");
        return "Бирюзовый".equalsIgnoreCase(name);
    }

    private boolean isDarkColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        // perceived luminance
        int lum = (int) (0.2126f * r + 0.7152f * g + 0.0722f * b);
        return lum < 130;
    }

    private int hudAccentStatic(int alpha) {
        if (isMonoTheme() && !hudColor.is("Тема HUD")) {
            return new Color(190, 190, 190, alpha).getRGB();
        }
        int c1;
        int c2;
        if (hudColor.is("Тема HUD")) {
            int[] c = getHudThemeColors();
            c1 = c[0];
            c2 = c[1];
        } else {
            c1 = Manager.STYLE_MANAGER.getFirstColor();
            c2 = Manager.STYLE_MANAGER.getSecondColor();
        }
        if (gradientType.is("Справа налево")) {
            int tmp = c1;
            c1 = c2;
            c2 = tmp;
        }
        int rgb = ColorUtil.interpolateColor(c1, c2, 0.5f);
        return ColorUtil.withAlpha(rgb, alpha / 255f);
    }

    private int hudSurface(int r, int g, int b, int alpha, float mix) {
        // для "Обычный" делаем HUD монохромным, независимо от темы
        if (hudColor.is("Обычный")) {
            return monoSurface(alpha, mix);
        }
        if (!useHudTheme()) {
            return new Color(r, g, b, alpha).getRGB();
        }
        int base = new Color(r, g, b, alpha).getRGB();
        if (isMonoTheme() && !hudColor.is("Тема HUD")) {
            return monoSurface(alpha, mix);
        }
        int accent = hudAccent(90, alpha);
        return ColorUtil.interpolateColor(base, accent, mix);
    }

    private int monoSurface(int alpha, float mix) {
        float t = MathHelper.clamp(mix, 0f, 1f);
        int dark = 0xFF4B4B4B;
        int light = 0xFF7F7F7F;
        int rgb = ColorUtil.interpolateColor(dark, light, t);
        return ColorUtil.withAlpha(rgb, alpha / 255f);
    }

    private int hudSeparator(int alpha) {
        if (!useHudTheme()) {
            return new Color(255, 255, 255, alpha).getRGB();
        }
        if (isMonoTheme() && !hudColor.is("Тема HUD")) {
            return hudAccentStatic(alpha);
        }
        return hudAccent(90, alpha);
    }

    private int hudPill(int alpha) {
        if (!useHudTheme()) {
            return hud_color;
        }
        return hudAccent(90, alpha);
    }

    public final Dragging watermarkDrag = ExosWare.getInstance().createDrag(this, "WaterMark", 10, 10);
    public final Dragging targethudDrag = ExosWare.getInstance().createDrag(this, "TargetHUD", 10, 45);
    public final Dragging keybindsDrag = ExosWare.getInstance().createDrag(this, "KeyBindsHUD", 10, 95);
    public final Dragging stafflistDrag = ExosWare.getInstance().createDrag(this, "StaffListHUD", 10, 128);
    public final Dragging helperHudDrag = ExosWare.getInstance().createDrag(this, "HelperHUD", 10, 158);
    public final Dragging itemcooldownDrag = ExosWare.getInstance().createDrag(this, "CoolDownHUD", 10, 165);
    public final Dragging potionhudDrag = ExosWare.getInstance().createDrag(this, "PotionHUD", 10, 198);
    public final Dragging coordinateshudDrag = ExosWare.getInstance().createDrag(this, "CoordinatesHUD", 10, 198);
    public final Dragging armorDrag = ExosWare.getInstance().createDrag(this, "ArmorHUD", 478, 468);
    public final Dragging notificationsDrag = ExosWare.getInstance().createDrag(this, "NotificationsHUD", 10, 260);
    public final Dragging mediaPlayerDrag = ExosWare.getInstance().createDrag(this, "MediaPlayerHUD", 10, 290);
    public final Dragging swapHelpersDrag = ExosWare.getInstance().createDrag(this, "SwapHelpersHUD", 10, 320);
    public final Dragging mouseSensDrag = ExosWare.getInstance().createDrag(this, "MouseSensHUD", 10, 380);

    Animation tHudAnimation = new EaseBackIn(300, 1, 1.0f);
    private final Vector4f corner = new Vector4f(3, 0, 0, 3);
    LivingEntity target = null;
    float health = 0f;
    float health2 = 0f;
    int activeModules = 0;
    private float heightDynamic = 0f;
    private double scale = 0.0D;

    private final ExecutorService mediaExecutor = Executors.newSingleThreadExecutor();

    private float smoothFps = 0f;
    private float smoothPing = 0f;
    private float smoothTps = 0f;
    private float smoothCoordX = 0f;
    private float smoothCoordY = 0f;
    private float smoothCoordZ = 0f;
    private long lastNumberUpdateMs = 0L;
    private boolean lastNotificationsState = false;



    // поля для медиаплеера в стиле MediaplayerRenderer
    private String mediaTitle = "No track";
    private String mediaArtist = "Unknown artist";
    private int mediaDuration = 0;
    private int mediaPosition = 0;
    private float mediaAnimationStep = 0f;
    private float mediaAppearAnim = 0f;
    private float mediaWidthAnim = 0f;
    private float mediaPlayerHoverAnim = 0f;

    private Identifier mediaArtworkId = null;
    private int mediaArtworkHash = 0;
    private long mediaArtworkUpdatedAt = 0L;

    private IMediaSession cachedMediaSession = null;
    private MediaInfo cachedMediaInfo = null;
    private long cachedMediaUpdatedAt = 0L;
    private boolean mediaSessionErrorNotified = false;

    private boolean mediaNativeInitTried = false;
    private boolean mediaNativeReady = false;
    private String mediaInitError = "";

    private volatile IMediaSession asyncMediaSession = null;
    private volatile String asyncMediaDebugLine1 = "";
    private volatile String asyncMediaDebugLine2 = "";
    private volatile String asyncMediaError = "";
    private volatile long asyncMediaUpdatedAt = 0L;
    private volatile Future<?> asyncMediaTask = null;

    private boolean mediaPlayerExpanded = false;
    private float mediaPlayerExpandAnim = 0f;
private boolean mediaPlayerDraggingProgress = false;
    private int mediaDragButton = -1;
    private float mediaThumbHover = 0f;
    private float mediaProgressAlpha = 0f;
    private float mediaPlayerHoverX = 0f;
    private float mediaPlayerHoverY = 0f;
    private boolean mediaAltNext = true;
    private float mediaTopbarX = 0f;
    private float mediaTopbarY = 0f;
    private float mediaTopbarW = 0f;
    private float mediaTopbarH = 0f;
    private float mediaBtnPanelAnim = 0f;
    private float mediaBtnPanelX = 0f;
    private float mediaBtnPanelY = 0f;
    private float mediaBtnPanelW = 0f;
    private float mediaBtnPanelH = 0f;

    private final Animation mediaExpandAnim = new EaseBackIn(220, 1, 1.0f, Direction.AxisDirection.NEGATIVE);

    private String mediaDebugLine1 = "";
    private String mediaDebugLine2 = "";
    private boolean mediaNoSessionsNotified = false;
    private String mediaLastError = "";

    private float mediaBlockX = 0f;
    private float mediaBlockY = 0f;
    private float mediaBlockW = 0f;
    private float mediaBlockH = 0f;

    private final List<StaffPlayer> staffPlayers = new ArrayList<>(32);
    private final Set<String> addedPlayers = new HashSet<>(64);

    private String serverAddressCache = "";
    private boolean isLocalServerCache = false;

    // ховер кнопок медиаплеера (назад / пауза / далее)
    private final float[] mediaBtnHover = new float[3];

    private transient Robot mediaRobot;

    private void requestMediaSessionAsync() {
        long now = System.currentTimeMillis();
        if (asyncMediaTask != null && !asyncMediaTask.isDone()) return;
        if ((now - asyncMediaUpdatedAt) < 500L) return;

        asyncMediaTask = mediaExecutor.submit(() -> {
            try {
                ensureMediaNativeLoaded();
                if (!mediaNativeReady) {
                    asyncMediaSession = null;
                    asyncMediaDebugLine1 = "Sessions: error";
                    asyncMediaDebugLine2 = "";
                    asyncMediaError = (mediaInitError != null && !mediaInitError.isEmpty()) ? mediaInitError : "init failed";
                    asyncMediaUpdatedAt = System.currentTimeMillis();
                    return;
                }

                List<IMediaSession> sessions = WindowsMediaPlayerInfo.INSTANCE.getMediaSessions();
                if (sessions == null || sessions.isEmpty()) {
                    asyncMediaSession = null;
                    asyncMediaDebugLine1 = "Sessions: 0";
                    asyncMediaDebugLine2 = "";
                    asyncMediaError = "";
                    asyncMediaUpdatedAt = System.currentTimeMillis();
                    return;
                }

                StringBuilder owners = new StringBuilder();
                int shown = 0;
                for (IMediaSession s : sessions) {
                    if (s == null) continue;
                    String o = s.getOwner();
                    if (o == null) o = "?";
                    if (owners.length() > 0) owners.append(", ");
                    owners.append(o);
                    shown++;
                    if (shown >= 3) break;
                }

                IMediaSession best = null;
                for (IMediaSession s : sessions) {
                    if (s == null) continue;
                    MediaInfo m = s.getMedia();
                    if (m == null) continue;
                    if (m.getPlaying()) {
                        best = s;
                        break;
                    }
                    if (best == null && m.getDuration() > 0) {
                        best = s;
                    }
                }

                asyncMediaSession = best != null ? best : sessions.get(0);
                asyncMediaDebugLine1 = "Sessions: " + sessions.size();
                asyncMediaDebugLine2 = "Owners: " + owners;
                asyncMediaError = "";
                asyncMediaUpdatedAt = System.currentTimeMillis();
            } catch (Throwable t) {
                Throwable root = t;
                if (t instanceof ExceptionInInitializerError ei && ei.getCause() != null) root = ei.getCause();
                asyncMediaSession = null;
                asyncMediaDebugLine1 = "Sessions: error";
                asyncMediaDebugLine2 = "";
                asyncMediaError = root.getClass().getSimpleName() + (root.getMessage() != null ? (": " + root.getMessage()) : "");
                asyncMediaUpdatedAt = System.currentTimeMillis();
            }
        });
    }

    private void ensureMediaNativeLoaded() {
        if (mediaNativeInitTried) return;
        mediaNativeInitTried = true;
        try {
            // dll is loaded inside WindowsMediaPlayerInfo init. Before loading the class, ensure resource is present.
            try {
                if (HUD.class.getResourceAsStream("/mediaplayerinfo/natives/win/MediaPlayerInfo.dll") == null) {
                    mediaInitError = "Media DLL resource not found";
                    if (!mediaSessionErrorNotified && Manager.NOTIFICATION_MANAGER != null) {
                        mediaSessionErrorNotified = true;
                        Manager.NOTIFICATION_MANAGER.add(NotificationType.INFO, "MediaPlayer", mediaInitError, 5);
                    }
                    return;
                }
            } catch (Throwable ignored) {
                // ignore resource probing errors
            }

            // Force class init and capture the real cause if init fails
            Class<?> cls;
            try {
                cls = Class.forName("dev.redstones.mediaplayerinfo.impl.win.WindowsMediaPlayerInfo");
            } catch (ExceptionInInitializerError ei) {
                Throwable c = (ei.getCause() != null) ? ei.getCause() : ei;
                mediaInitError = c.getClass().getSimpleName() + (c.getMessage() != null ? (": " + c.getMessage()) : "");
                throw ei;
            }

            Object instObj = cls.getField("INSTANCE").get(null);
            if (instObj instanceof MediaPlayerInfo mpi) {
                mpi.getMediaSessions();
            }

            mediaNativeReady = true;
        } catch (Throwable t) {
            Throwable root = t;
            if (t instanceof ExceptionInInitializerError ei && ei.getCause() != null) root = ei.getCause();
            if (mediaInitError == null || mediaInitError.isEmpty()) {
                mediaInitError = root.getClass().getSimpleName() + (root.getMessage() != null ? (": " + root.getMessage()) : "");
            }
            if (!mediaSessionErrorNotified && Manager.NOTIFICATION_MANAGER != null) {
                mediaSessionErrorNotified = true;
                Manager.NOTIFICATION_MANAGER.add(NotificationType.INFO, "MediaPlayer", "Media init error: " + root.getClass().getSimpleName() + (root.getMessage() != null ? (" (" + root.getMessage() + ")") : ""), 6);
            }
        }
    }

    private IMediaSession getActiveMediaSession() {
        requestMediaSessionAsync();

        mediaDebugLine1 = asyncMediaDebugLine1;
        mediaDebugLine2 = asyncMediaDebugLine2;
        mediaLastError = asyncMediaError;
        return asyncMediaSession;
    }

    private Identifier getOrUpdateArtworkId(MediaInfo media) {
        if (media == null) return null;
        byte[] bytes = media.getArtworkPng();
        if (bytes == null || bytes.length == 0) return null;

        int h = Arrays.hashCode(bytes);
        long now = System.currentTimeMillis();
        if (mediaArtworkId != null && mediaArtworkHash == h && (now - mediaArtworkUpdatedAt) < 30_000L) {
            return mediaArtworkId;
        }

        try {
            BufferedImage img = media.getArtwork();
            if (img == null) return null;

            int w = img.getWidth();
            int he = img.getHeight();
            if (w <= 0 || he <= 0) return null;

            NativeImage ni = new NativeImage(w, he, true);
            for (int yy = 0; yy < he; yy++) {
                for (int xx = 0; xx < w; xx++) {
                    int argb = img.getRGB(xx, yy);
                    int a = (argb >>> 24) & 0xFF;
                    int r = (argb >>> 16) & 0xFF;
                    int g = (argb >>> 8) & 0xFF;
                    int b = (argb) & 0xFF;
                    int abgr = (a << 24) | (b << 16) | (g << 8) | r;
                    ni.setColorArgb(xx, yy, abgr);
                }
            }

            NativeImageBackedTexture tex = new NativeImageBackedTexture(ni);
            tex.upload();
            Identifier id = Identifier.of("sodiumextra", "dynamic/media_artwork_" + Integer.toHexString(h));
            mc.getTextureManager().registerTexture(id, tex);
            mediaArtworkId = id;
            mediaArtworkHash = h;
            mediaArtworkUpdatedAt = now;
            return id;
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (mc == null || mc.player == null || mc.world == null) return;

 if (event instanceof EventMouse mouse) {
              if (setting.get("Медиаплеер (мощные пк)") && (mc.currentScreen instanceof ChatScreen || mc.currentScreen == null)) {
                  handleMediaPlayerClick(mouse.getButton());
              }
              if (mediaPlayerDraggingProgress && mouse.getButton() == mediaDragButton) {
                  mediaPlayerDraggingProgress = false;
                  mediaDragButton = -1;
              }
              if (setting.get("SwapHelpers")) {
                  handleSwapHelpersClick(mouse.getButton());
              }
          }

         if (event instanceof EventPacket packetEvent) {
            if (packetEvent.isReceivePacket() && packetEvent.getPacket() instanceof EntityDamageS2CPacket pkt) {
                Entity entity = mc.world.getEntityById(pkt.entityId());
                if (entity == null) return;
                DamageSource source = pkt.createDamageSource(mc.world);
                Entity attacker = source.getAttacker();
            }
        }

  if (event instanceof EventUpdate) {
              if (setting.get("StaffList")) {
                  updateStaffPlayers(mc);
              }
              if (mediaPlayerDraggingProgress) {
                  double scale = mc.getWindow().getScaleFactor();
                  double mx = mc.mouse.getX() / scale;
                  double my = mc.mouse.getY() / scale;
                  float px = mediaPlayerDrag.getX();
                  float py = mediaPlayerDrag.getY();
                  float pw = mediaPlayerDrag.getWidth();
                  float pbX = px + 8f;
                  float pbY = py + 63f;
                  float pbW = pw - 18f;
                  float pbH = 3f;
                  long windowHandle = mc.getWindow().getHandle();
                  boolean buttonReleased = mediaDragButton >= 0 && GLFW.glfwGetMouseButton(windowHandle, mediaDragButton) != GLFW.GLFW_PRESS;
                  if (buttonReleased) {
                      mediaPlayerDraggingProgress = false;
                      mediaDragButton = -1;
                  } else {
                      float progress = MathHelper.clamp((float) ((mx - pbX) / pbW), 0f, 1f);
                      mediaAnimationStep = progress;
                      IMediaSession session = getActiveMediaSession();
                      if (session != null && session.getMedia() != null) {
                          MediaInfo m = session.getMedia();
                          long dur = m.getDuration();
                          if (dur > 0) {
                           long newPos = (long) (progress * dur);
                          }
                      }
                  }
              }
          }
        if (event instanceof EventRender2D eventRender2D) {
            boolean sNotifications = setting.get("Notifications");
            if (lastNotificationsState && !sNotifications) {
                Manager.NOTIFICATION_MANAGER.getNotifications().clear();
            }
            lastNotificationsState = sNotifications;

            boolean sWaterMark = setting.get("WaterMark");
            boolean sTargetHUD = setting.get("TargetHUD");
            boolean sStaffList = setting.get("StaffList");
            boolean sKeyBinds = setting.get("KeyBinds");
            boolean sItemCooldown = setting.get("ItemCoolDownHUD");
            boolean sPotion = setting.get("PotionHUD");
            boolean sCoordinates = setting.get("Coordinates / TPS");
            boolean sArmorHUD = setting.get("ArmorHUD");
            boolean sMediaPlayer = setting.get("Медиаплеер (мощные пк)");
            boolean sSwapHelpers = setting.get("SwapHelpers");
            boolean sActiveMods = setting.get("ActiveMods");

            if (sWaterMark && hudStyle.is("Водяной")) waterMark(eventRender2D);
            if (sTargetHUD) targethud(eventRender2D);
            if (sStaffList) staffList(eventRender2D);
            if (sKeyBinds) keybindHud(eventRender2D);
            if (sItemCooldown) cooldown(eventRender2D);
            if (sPotion) potion(eventRender2D);
            if (sCoordinates) сoordinates(eventRender2D);
            if (sArmorHUD) armor(eventRender2D);
            if (sMediaPlayer) mediaPlayer(eventRender2D);
            if (sSwapHelpers) swapHelpersHud(eventRender2D);
            if (sActiveMods) activeMods(eventRender2D);
            if (hudMode.is("HUD2")) hudNewOnEvent(event);
            if (setting.get("Sensitivity")) mouseSensHud(eventRender2D);
        }
    }

    private final HUDNew hudNew = new HUDNew();

    private void hudNewOnEvent(Event event) {
        if (mc == null || mc.player == null || mc.world == null) return;
        if (event instanceof EventRender2D eventRender2D) {
            boolean sWaterMark = setting.get("WaterMark");
            boolean sMediaPlayer = setting.get("Медиаплеер (мощные пк)");
            if (sWaterMark) hudNew.hudNewWaterMark(eventRender2D);
            if (sMediaPlayer) hudNew.hudNewMediaPlayer(eventRender2D);
        }
    }

    private float swapBlockX;
    private float swapBlockY;
    private float swapBlockW;
    private float swapBlockH;

    private void handleSwapHelpersClick(int button) {
        if (button != 0) return;

        double scale = mc.getWindow().getScaleFactor();
        double mouseX = mc.mouse.getX() / scale;
        double mouseY = mc.mouse.getY() / scale;

        if (!RenderUtil.isInRegion((int) mouseX, (int) mouseY, swapBlockX, swapBlockY, swapBlockW, swapBlockH)) return;

        float x = swapHelpersDrag.getX();
        float y = swapHelpersDrag.getY();

        float pillH = 18f;
        float pillGapX = 6f;

        java.util.ArrayList<HelperRow> rows = new java.util.ArrayList<>(16);

        Function ftFunc = ru.levin.modules.FunctionManager.get("FTHelper");
        if (ftFunc instanceof FTHelper ft && ft.state) {
            for (var e : ft.getBinds().entrySet()) {
                rows.add(new HelperRow(e.getKey().getName(), e.getValue(), e.getKey().getKey()));
            }
        }

        Function hwFunc = ru.levin.modules.FunctionManager.get("HWHelper");
        if (hwFunc instanceof HWHelper hw && hw.state) {
            for (var e : hw.getBinds().entrySet()) {
                rows.add(new HelperRow(e.getKey().getName(), e.getValue(), e.getKey().getKey()));
            }
        }

        if (rows.isEmpty()) return;

        float curX = x;
        for (HelperRow r : rows) {
            String keyName = getShortKey(ClientManager.getKey(r.keyCode));
            if (keyName == null) keyName = "";
            float pillW = 6f + 9f + 6f + FontUtils.sfns_display_bold[14].getWidth(keyName) + 6f;

            if (RenderUtil.isInRegion((int) mouseX, (int) mouseY, curX, y, pillW, pillH)) {
                int[] slots = findItemSlots(r.item);
                ru.levin.util.player.InventoryUtil.use(slots[0], slots[1], true);
                return;
            }

            curX += pillW + pillGapX;
        }
    }

    private int[] findItemSlots(Item item) {
        if (mc.player == null) return new int[]{-1, -1};

        int hotbarSlot = -1;
        int inventorySlot = -1;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || stack.getItem() != item) continue;

            if (i < 9) {
                if (hotbarSlot == -1) hotbarSlot = i;
                if (inventorySlot == -1) inventorySlot = i + 36;
            } else {
                if (inventorySlot == -1) inventorySlot = i;
            }
            if (hotbarSlot != -1 && inventorySlot != -1) break;
        }
        return new int[]{hotbarSlot, inventorySlot};
    }

    private void swapHelpersHud(EventRender2D render2D) {
        float x = swapHelpersDrag.getX();
        float y = swapHelpersDrag.getY();

        var ctx = render2D.getDrawContext();
        var matrices = ctx.getMatrices();
        var font = FontUtils.sfns_display_bold[14];

        int alpha = Math.min(255, Math.round(customAlpha.get().intValue() * 0.7f));
        boolean plainHud = hudColor.is("Обычный");

        float padX = 6f;
        float pillH = 18f;
        float pillGapX = 6f;
        float pillR = 7f;
        float iconScale = 0.7f;
        float iconW = 9f;
        float textGap = 2f;

        java.util.ArrayList<HelperRow> rows = new java.util.ArrayList<>(16);

        Function ftFunc = ru.levin.modules.FunctionManager.get("FTHelper");
        if (ftFunc instanceof FTHelper ft && ft.state) {
            for (var e : ft.getBinds().entrySet()) {
                rows.add(new HelperRow(e.getKey().getName(), e.getValue(), e.getKey().getKey()));
            }
        }

        Function hwFunc = ru.levin.modules.FunctionManager.get("HWHelper");
        if (hwFunc instanceof HWHelper hw && hw.state) {
            for (var e : hw.getBinds().entrySet()) {
                rows.add(new HelperRow(e.getKey().getName(), e.getValue(), e.getKey().getKey()));
            }
        }

        if (rows.isEmpty()) {
            float w = padX * 2f + Math.max(18f, font.getWidth("Swap"));
            float h = pillH;

            int pillColor = plainHud
                    ? new Color(0, 0, 0, (int) (alpha * 0.92f)).getRGB()
                    : hudSurface(18, 16, 30, (int) (alpha * 0.92f), 0.12f);

            if (alpha <= 240 && blur.get()) {
                drawBlur(matrices, x, y, w, h, new Vector4f(pillR, pillR, pillR, pillR), 12, Color.white.getRGB());
            }
            drawRoundedRect(matrices, x, y, w, h, pillR, pillColor);

            float titleW = font.getWidth("Swap");
            font.drawLeftAligned(matrices, "Swap", x + w / 2f - titleW / 2f, y + (h - font.getHeight()) / 2f, new Color(215, 215, 215).getRGB());

            swapHelpersDrag.setWidth(w);
            swapHelpersDrag.setHeight(h);
            swapBlockX = x;
            swapBlockY = y;
            swapBlockW = w;
            swapBlockH = h;
            return;
        }

        float totalW = 0f;
        for (int i = 0; i < rows.size(); i++) {
            HelperRow r = rows.get(i);
            String keyName = getShortKey(ClientManager.getKey(r.keyCode));
            if (keyName == null) keyName = "";
            float w = padX + iconW + textGap + font.getWidth(keyName) + padX;
            totalW += w;
            if (i + 1 < rows.size()) totalW += pillGapX;
        }

        float totalH = pillH;

        int pillColor = plainHud
                ? new Color(0, 0, 0, (int) (alpha * 0.92f)).getRGB()
                : hudSurface(18, 16, 30, (int) (alpha * 0.92f), 0.12f);

        if (alpha <= 240 && blur.get()) {
            drawBlur(matrices, x, y, totalW, totalH, new Vector4f(pillR, pillR, pillR, pillR), 12, Color.white.getRGB());
        }

        float curX = x;
        for (HelperRow r : rows) {
            String keyName = getShortKey(ClientManager.getKey(r.keyCode));
            if (keyName == null) keyName = "";
            float pillW = padX + iconW + textGap + font.getWidth(keyName) + padX;

            drawRoundedRect(matrices, curX, y, pillW, pillH, pillR, pillColor);

            ItemStack stack = new ItemStack(r.item);
            float iconY = y + (pillH - 9f) / 2f - 0.5f;
            RenderAddon.renderItem(ctx, stack, curX + padX - 1.5f, iconY, iconScale, false);

            font.drawLeftAligned(matrices, keyName, curX + padX + iconW + textGap, y + (pillH - font.getHeight()) / 2f, new Color(215, 215, 215).getRGB());

            curX += pillW + pillGapX;
        }

        swapHelpersDrag.setWidth(totalW);
        swapHelpersDrag.setHeight(totalH);

        swapBlockX = x;
        swapBlockY = y;
        swapBlockW = totalW;
        swapBlockH = totalH;
    }

    private void helperHud(EventRender2D render2D) {
        float x = helperHudDrag.getX();
        float y = helperHudDrag.getY();

        var ctx = render2D.getDrawContext();
        var matrices = ctx.getMatrices();
        var font = FontUtils.sfns_display_bold[14];

        int alpha = Math.min(255, Math.round(customAlpha.get().intValue() * 0.7f));
        boolean plainHud = hudColor.is("Обычный");

        float padX = 6f;
        float pillH = 18f;
        float pillGapY = 4f;
        float pillR = 7f;
        float iconScale = 0.7f;
        float iconW = 9f;
        float textGap = 6f;

        java.util.ArrayList<HelperRow> rows = new java.util.ArrayList<>(12);

        Function ftFunc = ru.levin.modules.FunctionManager.get("FTHelper");
        if (ftFunc instanceof FTHelper ft && ft.state) {
            for (var e : ft.getBinds().entrySet()) {
                rows.add(new HelperRow(e.getKey().getName(), e.getValue(), e.getKey().getKey()));
            }
        }

        Function hwFunc = ru.levin.modules.FunctionManager.get("HWHelper");
        if (hwFunc instanceof HWHelper hw && hw.state) {
            for (var e : hw.getBinds().entrySet()) {
                rows.add(new HelperRow(e.getKey().getName(), e.getValue(), e.getKey().getKey()));
            }
        }

        if (rows.isEmpty()) {
            helperHudDrag.setWidth(1);
            helperHudDrag.setHeight(1);
            return;
        }

        float maxWidth = 40f;
        for (HelperRow r : rows) {
            String keyName = getShortKey(ClientManager.getKey(r.keyCode));
            if (keyName == null) keyName = "";
            float w = padX + iconW + textGap + font.getWidth(keyName) + padX;
            if (w > maxWidth) maxWidth = w;
        }

        float totalH = rows.size() * pillH + (rows.size() - 1) * pillGapY;

        int pillColor = plainHud
                ? new Color(0, 0, 0, (int) (alpha * 0.92f)).getRGB()
                : hudSurface(18, 16, 30, (int) (alpha * 0.92f), 0.12f);

        if (alpha <= 240 && blur.get()) {
            drawBlur(matrices, x, y, maxWidth, totalH, new Vector4f(pillR, pillR, pillR, pillR), 12, Color.white.getRGB());
        }

        float rowY = y;
        for (HelperRow r : rows) {
            drawRoundedRect(matrices, x, rowY, maxWidth, pillH, pillR, pillColor);

            ItemStack stack = new ItemStack(r.item);
            float iconY = rowY + (pillH - 9f) / 2f;
            RenderAddon.renderItem(ctx, stack, x + padX - 1.5f, iconY, iconScale, false);

            String keyName = getShortKey(ClientManager.getKey(r.keyCode));
            if (keyName == null) keyName = "";
            float bindW = font.getWidth(keyName);
            font.drawLeftAligned(matrices, keyName, x + maxWidth - bindW - padX, rowY + (pillH - font.getHeight()) / 2f, new Color(215, 215, 215).getRGB());

            rowY += pillH + pillGapY;
        }

        helperHudDrag.setWidth(maxWidth);
        helperHudDrag.setHeight(totalH);
    }

    private static final class HelperRow {
        final String label;
        final Item item;
        final int keyCode;

        private HelperRow(String label, Item item, int keyCode) {
            this.label = label;
            this.item = item;
            this.keyCode = keyCode;
        }
    }

    private void activeMods(EventRender2D render2D) {
        var matrices = render2D.getDrawContext().getMatrices();
        var font = FontUtils.sfns_display_bold[14];

        int screenW = mc.getWindow().getScaledWidth();
        float xRight = screenW - 6f;
        float y = 6f;

        enabledModsTmp.clear();
        for (Function f : Manager.FUNCTION_MANAGER.getFunctions()) {
            if (f == null) continue;
            if (!f.state) continue;
            if (f == this) continue;
            if ("ClickGUI".equalsIgnoreCase(f.name)) continue;
            enabledModsTmp.add(f);
        }

        java.util.Set<String> present = new java.util.HashSet<>();
        for (Function f : enabledModsTmp) present.add(f.name);

        // направление анимации: включён -> появление, выключен -> исчезновение
        for (java.util.Map.Entry<String, DecelerateAnimation> e : activeModAnims.entrySet()) {
            e.getValue().setDirection(present.contains(e.getKey())
                    ? net.minecraft.util.math.Direction.AxisDirection.POSITIVE
                    : net.minecraft.util.math.Direction.AxisDirection.NEGATIVE);
        }
        activeModAnims.entrySet().removeIf(e -> !present.contains(e.getKey()) && e.getValue().getOutput() <= 0.01);

        if (enabledModsTmp.isEmpty() && activeModAnims.isEmpty()) {
            return;
        }

        // имена для отрисовки: активные модули + затухающие при выключении
        java.util.List<String> names = new java.util.ArrayList<>();
        for (Function f : enabledModsTmp) names.add(f.name);
        for (String n : activeModAnims.keySet()) {
            if (!present.contains(n)) names.add(n);
        }
        names.sort((a, b) -> Float.compare(font.getWidth(b), font.getWidth(a)));

        float dot = 3.5f;
        float dotGap = 5f;
        float padX = 7f;
        float padY = 5f;
        float lineH = 12f;

        float maxW = 0f;
        for (String n : names) maxW = Math.max(maxW, font.getWidth(n));

        float innerW = dot + dotGap + maxW;
        float bgW = innerW + padX * 2f;
        float bgH = names.size() * lineH + padY * 2f;
        float bgX = xRight - bgW;
        float bgY = y - padY;

        drawRoundedRect(matrices, bgX, bgY, bgW, bgH, 6f, injectAlpha(Color.black.getRGB(), 130));
        drawRoundedRect(matrices, bgX, bgY, 2.5f, bgH, 1.25f, hudAccent(0, 200));

        float rowY = y;
        int idx = 0;
        for (String name : names) {
            DecelerateAnimation anim = activeModAnims.computeIfAbsent(name, k -> new DecelerateAnimation(260, 1));
            float prog = (float) MathHelper.clamp(anim.getOutput(), 0f, 1f);

            float alpha = prog;
            float slide = (1f - prog) * 14f;

            float nameW = font.getWidth(name);
            float rowRight = xRight - slide;
            float dotX = rowRight - nameW - dotGap - dot;
            float dotY = rowY + (lineH - dot) / 2f - 1f;

            int accent = hudAccent(idx * 22f, Math.round(alpha * 255));
            drawRoundedRect(matrices, dotX, dotY, dot, dot, dot / 2f, accent);
            font.drawLeftAligned(matrices, name, rowRight - nameW, rowY, ColorUtil.withAlpha(Color.white.getRGB(), alpha));

            rowY += lineH;
            idx++;
        }
    }

    private boolean isLikelyGoldenApple(LivingEntity entity) {
        if (entity == null) return false;

        StatusEffectInstance absorption = entity.getStatusEffect(StatusEffects.ABSORPTION);
        StatusEffectInstance regen = entity.getStatusEffect(StatusEffects.REGENERATION);
        if (absorption == null || regen == null) return false;

        int absAmp = absorption.getAmplifier();
        int regenAmp = regen.getAmplifier();
        int absTicks = absorption.getDuration();
        int regenTicks = regen.getDuration();

        // Normal golden apple: Regeneration II (amp=1) ~5s (100t), Absorption I (amp=0) ~2m (2400t)
        boolean normal = regenAmp >= 1 && regenTicks >= 60 && regenTicks <= 180
                && absAmp == 0 && absTicks >= 1200 && absTicks <= 2600;

        // Enchanted golden apple: Regeneration II (amp=1) ~20s (400t), Absorption V? (amp>=3) ~2m
        boolean enchanted = regenAmp >= 1 && regenTicks >= 240 && regenTicks <= 520
                && absAmp >= 3 && absTicks >= 1200 && absTicks <= 2600;

        return normal || enchanted;
    }

    private void handleMediaPlayerClick(int button) {
        if (button != 0) return;

        IMediaSession session = getActiveMediaSession();
        if (session == null) return;
        MediaInfo media = session.getMedia();
        if (media == null) return;

        double scale = mc.getWindow().getScaleFactor();
        double mouseX = mc.mouse.getX() / scale;
        double mouseY = mc.mouse.getY() / scale;

        float x = mediaPlayerDrag.getX();
        float y = mediaPlayerDrag.getY();
        float w = mediaPlayerDrag.getWidth();
        float h = mediaPlayerDrag.getHeight();

        final float PANEL_HEIGHT = 36f;
        final float CONTROLS_GAP = 3f;
        final float CONTROLS_HEIGHT = 24f;
        boolean hitMain = isInRegion((int) mouseX, (int) mouseY, x, y, w, PANEL_HEIGHT);
        boolean hitControls = isInRegion((int) mouseX, (int) mouseY, x, y + PANEL_HEIGHT, w, CONTROLS_GAP + CONTROLS_HEIGHT);
        if (!hitMain && !hitControls) return;

        final float BTN_SIZE = 18f;
        final float BTN_GAP = 5f;
        float totalBtnsW = BTN_SIZE * 3 + BTN_GAP * 2;
        float btnsStartX = x + (w - totalBtnsW) / 2f;
        float btnsY = y + PANEL_HEIGHT + CONTROLS_GAP + (CONTROLS_HEIGHT - BTN_SIZE) / 2f;
        for (int i = 0; i < 3; i++) {
            float bx = btnsStartX + i * (BTN_SIZE + BTN_GAP);
            float by = btnsY;
            if (isInRegion((int) mouseX, (int) mouseY, bx, by, BTN_SIZE, BTN_SIZE)) {
                try {
                    if (i == 0) {
                        session.previous();
                    } else if (i == 1) {
                        session.playPause();
                    } else {
                        session.next();
                    }
                } catch (Throwable ignored) {
                }
                return;
            }
        }

        float pbX = x + 8f;
        float pbY = y + 63f;
        float pbW = w - 18f;
        float pbH = 3f;
        float fillWidth = pbW * mediaAnimationStep;
        float thumbX = pbX + fillWidth;
        float thumbY = pbY + pbH / 2f;
        boolean hitThumb = isInRegion((int) mouseX, (int) mouseY, thumbX - 6f, thumbY - 6f, 12f, 12f);
        boolean hitBar = isInRegion((int) mouseX, (int) mouseY, pbX - 2f, pbY - 2f, pbW + 4f, pbH + 4f);
        if (hitThumb || hitBar) {
            mediaPlayerDraggingProgress = true;
            mediaDragButton = button;
        }
    }

    public boolean isMediaPlayerControlHit(double mouseX, double mouseY) {
        final float PANEL_HEIGHT = 36f;
        final float CONTROLS_GAP = 3f;
        final float CONTROLS_HEIGHT = 30f;
        boolean mainHit = RenderUtil.isInRegion((int) mouseX, (int) mouseY, mediaBlockX, mediaBlockY, mediaBlockW, mediaBlockH);
        boolean controlsHit = isInRegion((int) mouseX, (int) mouseY, mediaBlockX, mediaBlockY + PANEL_HEIGHT, mediaBlockW, CONTROLS_GAP + CONTROLS_HEIGHT);
        boolean progressHit = false;
        if (mediaBlockW > 0 && mediaBlockH > 0) {
            float pbX = mediaBlockX + 8f;
            float pbY = mediaBlockY + 63f;
            float pbW = mediaBlockW - 18f;
            float pbH = 3f;
            progressHit = isInRegion((int) mouseX, (int) mouseY, pbX - 2f, pbY - 2f, pbW + 4f, pbH + 4f);
        }
        return mainHit || controlsHit || progressHit;
    }

    private void mediaPlayer(EventRender2D eventRender2D) {
        MatrixStack matrices = eventRender2D.getMatrixStack();
        IMediaSession session = getActiveMediaSession();
        MediaInfo media = session != null ? session.getMedia() : null;
        boolean hasTrack = media != null && media.getTitle() != null && !media.getTitle().isEmpty();
        float targetAppear = hasTrack ? 1f : 0f;
        mediaAppearAnim = MathUtil.fast(mediaAppearAnim, targetAppear, 10);
        if (mediaAppearAnim <= 0.01f && !hasTrack) {
            mediaPlayerDrag.setWidth(1);
            mediaPlayerDrag.setHeight(1);
            mediaBlockX = 0f;
            mediaBlockY = 0f;
            mediaBlockW = 0f;
            mediaBlockH = 0f;
            return;
        }
        final float PANEL_RADIUS = 7f;
        final float PANEL_HEIGHT = 36f;
        final float COVER_SIZE = 24f;
        final float LEFT_PADDING = 10f;
        final float TEXT_GAP = 9f;
        final float RIGHT_PADDING = 10f;
        final float PROGRESS_HEIGHT = 3f;
        final float PROGRESS_TOP_OFFSET = 1f;
        final float CONTROLS_GAP = 3f;
        final float CONTROLS_HEIGHT = 30f;
        final float BTN_SIZE = 22f;
        final float BTN_GAP = 7f;
        var titleFont = FontUtils.sfns_display_bold[13];
        var artistFont = FontUtils.sfns_display_bold[11];
        String title = hasTrack ? media.getTitle() : mediaTitle;
        String artist = (media != null && media.getArtist() != null && !media.getArtist().isEmpty()) ? media.getArtist() : "";
        if (title == null) title = "";
        if (artist == null) artist = "";
        float titleWidth = titleFont.getWidth(title);
        float artistWidth = artistFont.getWidth(artist);
        float contentWidth = LEFT_PADDING + COVER_SIZE + TEXT_GAP + Math.min(Math.max(titleWidth, artistWidth), 170f) + RIGHT_PADDING;
        float targetWidth = Math.max(contentWidth, 110f);
        if (mediaWidthAnim <= 1f) mediaWidthAnim = targetWidth;
        mediaWidthAnim = MathUtil.fast(mediaWidthAnim, targetWidth, 12);
        float width = mediaWidthAnim;
        float sw = mc.getWindow().getScaledWidth();
        float x = sw / 2f - width / 2f;
        float baseY = 6f;
        float bossBarOffset = hasBossBarAbove() ? 22f : 0f;
        float targetY = baseY + bossBarOffset;
        float y = targetY - (1f - mediaAppearAnim) * 20f;
        mediaPlayerDrag.setX(x);
        mediaPlayerDrag.setY(y);
        mediaPlayerDrag.setWidth(width);
        double scale = mc.getWindow().getScaleFactor();
        double mouseX = mc.mouse.getX() / scale;
        double mouseY = mc.mouse.getY() / scale;

        final float controlsAreaY = y + PANEL_HEIGHT;
        final float controlsAreaH = CONTROLS_GAP + CONTROLS_HEIGHT;
        boolean isHoveringMain = isInRegion((int) mouseX, (int) mouseY, x, y, width, PANEL_HEIGHT);
        boolean isHoveringControls = controlsAreaH > 0 && isInRegion((int) mouseX, (int) mouseY, x, controlsAreaY, width, controlsAreaH);
        boolean isHovering = isHoveringMain || isHoveringControls;
        float targetHover = isHovering ? 1f : 0f;
        mediaPlayerHoverAnim = MathUtil.fast(mediaPlayerHoverAnim, targetHover, 14);
        float controlsAlpha = mediaPlayerHoverAnim;
        float targetProgressAlpha = controlsAlpha;
        mediaProgressAlpha = MathUtil.fast(mediaProgressAlpha, targetProgressAlpha, 12);
        float totalHeight = PANEL_HEIGHT + controlsAlpha * (CONTROLS_GAP + CONTROLS_HEIGHT);
        mediaPlayerDrag.setHeight(totalHeight);
        mediaBlockX = x;
        mediaBlockY = y;
        mediaBlockW = width;
        mediaBlockH = totalHeight;
        float hoverBrightness = 0.85f + mediaPlayerHoverAnim * 0.15f;
        float currentAlphaNormalized = mediaAppearAnim;
        int panelBgColor = ColorUtil.withAlpha(new Color(11, 10, 18).getRGB(), 0.95f);
        int panelAlphaInt = Math.round(currentAlphaNormalized * 255f);
        if (panelAlphaInt > 50 && blur.get()) {
            int glowColor = ColorUtil.withAlpha(new Color(110, 85, 230).getRGB(), currentAlphaNormalized * 0.2f);
            drawRoundedRect(matrices, x - 2f, y - 2f, width + 4f, totalHeight + 4f, PANEL_RADIUS + 1f, glowColor);
        }
        if (panelAlphaInt > 150 && blur.get()) {
            drawBlur(matrices, x, y, width, totalHeight, new Vector4f(PANEL_RADIUS, PANEL_RADIUS, PANEL_RADIUS, PANEL_RADIUS), 12, Color.white.getRGB());
        }
        drawRoundedRect(matrices, x, y, width, totalHeight, PANEL_RADIUS, panelBgColor);
        int borderHighlight = ColorUtil.withAlpha(new Color(80, 70, 140).getRGB(), currentAlphaNormalized * 0.25f);
        drawRoundedRect(matrices, x, y, width, 0.5f, PANEL_RADIUS, borderHighlight);
        float coverX = x + LEFT_PADDING;

        boolean mediaPlaying = media != null && media.getPlaying();
        if (controlsAlpha > 0.01f) {
            float totalBtnsW = BTN_SIZE * 3 + BTN_GAP * 2;
            float btnsStartX = x + (width - totalBtnsW) / 2f;
            float btnsY = y + PANEL_HEIGHT + CONTROLS_GAP + (CONTROLS_HEIGHT - BTN_SIZE) / 2f;
            for (int i = 0; i < 3; i++) {
                float bx = btnsStartX + i * (BTN_SIZE + BTN_GAP);
                boolean btnHover = isInRegion((int) mouseX, (int) mouseY, bx, btnsY, BTN_SIZE, BTN_SIZE);
                mediaBtnHover[i] = MathUtil.fast(mediaBtnHover[i], btnHover ? 1f : 0f, 20);
                float iconAlpha = controlsAlpha * (0.85f + mediaBtnHover[i] * 0.15f);
                float iconCX = bx + BTN_SIZE / 2f;
                float iconCY = btnsY + BTN_SIZE / 2f;
                int iconColor = ColorUtil.withAlpha(Color.white.getRGB(), iconAlpha);
                float hoverScale = 1f + mediaBtnHover[i] * 0.1f;
                float iconW = BTN_SIZE * 0.6f * hoverScale;
                float iconH = BTN_SIZE * 0.6f * hoverScale;
                float iconX = iconCX - iconW / 2f;
                float iconY = iconCY - iconH / 2f;
                if (i == 0) {
                    float slide = mediaBtnHover[i] * 2f;
                    drawSkipIcon(matrices, iconX - slide, iconY, iconW, iconH, true, iconColor);
                } else if (i == 2) {
                    float slide = mediaBtnHover[i] * 2f;
                    drawSkipIcon(matrices, iconX + slide, iconY, iconW, iconH, false, iconColor);
                } else {
                    if (mediaPlaying) {
                        float barW = iconW * 0.22f;
                        float baseGap = iconW * 0.26f;
                        float animatedGap = baseGap + mediaBtnHover[i] * 1.5f;
                        float barH = iconH * 0.9f;
                        float startX = iconCX - animatedGap - barW;
                        float startY = iconCY - barH / 2f;
                        drawRoundedRect(matrices, startX, startY, barW, barH, 0.5f, iconColor);
                        drawRoundedRect(matrices, startX + animatedGap + barW, startY, barW, barH, 0.5f, iconColor);
                    } else {
                        float triW = iconW * 0.65f;
                        float triH = iconH * 0.9f;
                        float triX = iconCX - triW / 2f + mediaBtnHover[i] * 1.2f;
                        drawTriangle(matrices, triX, iconCY - triH / 2f, triW, triH, false, iconColor);
                    }
                }
            }

            float progressY = y + PANEL_HEIGHT + CONTROLS_GAP + CONTROLS_HEIGHT - 6f;
            float progressX = x + LEFT_PADDING;
            float progressWidth = width - LEFT_PADDING - RIGHT_PADDING;
            long dur = media != null ? media.getDuration() : 0L;
            long pos = media != null ? media.getPosition() : 0L;
            float progress = dur > 0L ? MathHelper.clamp((float) pos / (float) dur, 0f, 1f) : 0f;
            mediaAnimationStep = MathUtil.fast(mediaAnimationStep, progress, 8);
            if (mediaProgressAlpha > 0.01f) {
                int progressBgColor = ColorUtil.withAlpha(new Color(40, 35, 60).getRGB(), currentAlphaNormalized * 0.25f * mediaProgressAlpha);
                drawRoundedRect(matrices, progressX, progressY, progressWidth, PROGRESS_HEIGHT, 1f, progressBgColor);
                float fillWidth = progressWidth * mediaAnimationStep;
                if (fillWidth > 0.5f) {
                    int fillColor = ColorUtil.withAlpha(Color.white.getRGB(), currentAlphaNormalized * 0.9f * mediaProgressAlpha);
                    drawRoundedRect(matrices, progressX, progressY, fillWidth, PROGRESS_HEIGHT, 1f, fillColor);
                }
                float thumbX = progressX + fillWidth;
                float thumbY = progressY + PROGRESS_HEIGHT / 2f;
                boolean thumbHover = isInRegion((int) mouseX, (int) mouseY, thumbX - 5f, thumbY - 5f, 10f, 10f);
                mediaThumbHover = MathUtil.fast(mediaThumbHover, thumbHover ? 1f : 0f, 18);
                float thumbScale = 1f + mediaThumbHover * 0.25f;
                float thumbRadius = 3.5f * thumbScale;
                int thumbGlow = ColorUtil.withAlpha(Color.white.getRGB(), (int)(currentAlphaNormalized * 180f * mediaProgressAlpha * mediaThumbHover));
                drawRoundedRect(matrices, thumbX - thumbRadius - 1.5f, thumbY - thumbRadius - 1.5f, thumbRadius * 2 + 3f, thumbRadius * 2 + 3f, thumbRadius + 1.5f, thumbGlow);
                int thumbColor = ColorUtil.withAlpha(Color.white.getRGB(), (int)(currentAlphaNormalized * 255f * mediaProgressAlpha));
                drawRoundedRect(matrices, thumbX - thumbRadius, thumbY - thumbRadius, thumbRadius * 2, thumbRadius * 2, thumbRadius, thumbColor);
            }
        }

        mediaBtnPanelX = x;
        mediaBtnPanelY = y + PANEL_HEIGHT;
        mediaBtnPanelW = width;
        mediaBtnPanelH = controlsAlpha * (CONTROLS_GAP + CONTROLS_HEIGHT);

        renderMediaContent(matrices, media, title, artist, titleFont, artistFont, x, y, width, PANEL_HEIGHT, PANEL_RADIUS, COVER_SIZE, coverX, LEFT_PADDING, TEXT_GAP, RIGHT_PADDING, PROGRESS_HEIGHT, PROGRESS_TOP_OFFSET, currentAlphaNormalized, hoverBrightness);
    }

    private void renderMediaContent(MatrixStack matrices, MediaInfo media, String title, String artist, RenderFonts titleFont, RenderFonts artistFont, float x, float y, float width, float PANEL_HEIGHT, float PANEL_RADIUS, float COVER_SIZE, float coverX, float LEFT_PADDING, float TEXT_GAP, float RIGHT_PADDING, float PROGRESS_HEIGHT, float PROGRESS_TOP_OFFSET, float currentAlphaNormalized, float hoverBrightness) {
        float contentAlpha = currentAlphaNormalized * hoverBrightness;
        float coverY = y + (PANEL_HEIGHT - COVER_SIZE) / 2f;
        Identifier artwork = media != null ? getOrUpdateArtworkId(media) : null;
        if (artwork != null) {
            int coverBgColor = ColorUtil.withAlpha(new Color(22, 19, 36).getRGB(), currentAlphaNormalized);
            drawRoundedRect(matrices, coverX, coverY, COVER_SIZE, COVER_SIZE, PANEL_RADIUS - 2f, coverBgColor);
            RenderUtil.drawTexture(matrices, artwork, coverX, coverY, COVER_SIZE, COVER_SIZE, PANEL_RADIUS - 2f, ColorUtil.withAlpha(Color.white.getRGB(), contentAlpha));
            int coverBorderColor = ColorUtil.withAlpha(new Color(130, 100, 230).getRGB(), currentAlphaNormalized * 0.55f);
            drawRoundedRect(matrices, coverX, coverY, COVER_SIZE, COVER_SIZE, PANEL_RADIUS - 2f, coverBorderColor);
            int highlightColor = ColorUtil.withAlpha(new Color(130, 110, 250).getRGB(), currentAlphaNormalized * 0.4f);
            drawRoundedRect(matrices, coverX, coverY, COVER_SIZE, 0.5f, PANEL_RADIUS - 2f, highlightColor);
        } else {
            int iconBgColor = ColorUtil.withAlpha(new Color(22, 19, 36).getRGB(), currentAlphaNormalized);
            drawRoundedRect(matrices, coverX, coverY, COVER_SIZE, COVER_SIZE, PANEL_RADIUS - 2f, iconBgColor);
            int innerBgColor = ColorUtil.withAlpha(new Color(110, 90, 220).getRGB(), currentAlphaNormalized * 0.15f);
            drawRoundedRect(matrices, coverX + 1f, coverY + 1f, COVER_SIZE - 2f, COVER_SIZE - 2f, PANEL_RADIUS - 4f, innerBgColor);
            int noteColor = ColorUtil.withAlpha(new Color(190, 175, 240).getRGB(), contentAlpha * 0.9f);
            float noteCX = coverX + COVER_SIZE / 2f;
            float noteCY = coverY + COVER_SIZE / 2f;
            float noteScale = COVER_SIZE * 0.22f;
            float headW = noteScale * 0.7f;
            float headH = noteScale * 0.55f;
            float headX = noteCX - headW / 2f;
            float headY = noteCY - headH / 2f + noteScale * 0.15f;
            drawRoundedRect(matrices, headX, headY, headW, headH, headH / 2f, noteColor);
            float stemX = headX + headW - 0.5f;
            float stemY = headY - noteScale * 0.75f;
            float stemW = noteScale * 0.13f;
            float stemH = noteScale * 0.85f;
            drawRoundedRect(matrices, stemX, stemY, stemW, stemH, 0.5f, noteColor);
        }
        float textX = coverX + COVER_SIZE + TEXT_GAP;
        float textYCenter = y + PANEL_HEIGHT / 2f;
        float maxTextWidth = width - textX - RIGHT_PADDING;
        String displayTitle = truncateText(title, titleFont, maxTextWidth);
        int titleColor = ColorUtil.withAlpha(new Color(255, 255, 255).getRGB(), contentAlpha);
        titleFont.drawLeftAligned(matrices, displayTitle, textX, textYCenter - 6f, titleColor);
        if (!artist.isEmpty()) {
            String displayArtist = truncateText(artist, artistFont, maxTextWidth);
            int artistColor = ColorUtil.withAlpha(new Color(190, 185, 220).getRGB(), contentAlpha * 0.95f);
             artistFont.drawLeftAligned(matrices, displayArtist, textX, textYCenter + 5f, artistColor);
        }
    }
    private String truncateText(String text, RenderFonts font, float maxWidth) {
        if (text == null || text.isEmpty() || maxWidth <= 0) return text;
        
        float textWidth = font.getWidth(text);
        if (textWidth <= maxWidth) return text;
        
        String ellipsis = "...";
        float ellipsisWidth = font.getWidth(ellipsis);
        if (ellipsisWidth >= maxWidth) return ellipsis;
        
        int charIndex = text.length() - 1;
        while (charIndex > 0 && font.getWidth(text.substring(0, charIndex) + ellipsis) > maxWidth) {
            charIndex--;
        }
        return text.substring(0, charIndex) + ellipsis;
    }

    private boolean hasBossBarAbove() {
        try {
            if (mc == null || mc.world == null || mc.inGameHud == null) return false;
            BossBarHud bossOverlayGui = mc.inGameHud.getBossBarHud();
            if (bossOverlayGui == null) return false;
            Map<UUID, ClientBossBar> bossBars = ((BossBarHudAccessor) bossOverlayGui).getBossBars();
            return bossBars != null && !bossBars.isEmpty();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static String formatTime(long totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format(Locale.ENGLISH, "%d:%02d", minutes, seconds);
    }

    private void drawTriangle(MatrixStack matrices, float x, float y, float w, float h, boolean left, int color) {
        float x1 = left ? (x + w) : x;
        float x2 = left ? x : (x + w);
        float y1 = y;
        float y2 = y + h / 2f;
        float y3 = y + h;
        drawFilledTriangle(matrices, x1, y1, x1, y3, x2, y2, color);
    }

    private void drawFilledTriangle(MatrixStack matrices, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        RenderUtil.enableRender();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        var mat = matrices.peek().getPositionMatrix();
        float a = ((color >>> 24) & 0xFF) / 255f;
        float r = ((color >>> 16) & 0xFF) / 255f;
        float g = ((color >>> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        BufferBuilder buffer = IMinecraft.tessellator().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        buffer.vertex(mat, x1, y1, 0).color(r, g, b, a);
        buffer.vertex(mat, x2, y2, 0).color(r, g, b, a);
        buffer.vertex(mat, x3, y3, 0).color(r, g, b, a);
        RenderUtil.render3D.endBuilding(buffer);
        RenderUtil.disableRender();
    }

    private void drawSkipArrow(MatrixStack matrices, float x, float y, float w, float h, boolean left, int color) {
        float barW = Math.max(2f, w * 0.28f);
        float triW = w - barW;
        float pad = h * 0.18f;
        if (left) {
            drawRoundedRect(matrices, x, y + pad, barW, h - pad * 2, 0.5f, color);
            drawTriangle(matrices, x + barW, y + pad, triW, h - pad * 2, true, color);
        } else {
            float barX = x + w - barW;
            drawRoundedRect(matrices, barX, y + pad, barW, h - pad * 2, 0.5f, color);
            drawTriangle(matrices, x, y + pad, triW, h - pad * 2, false, color);
        }
    }

    private void drawSkipIcon(MatrixStack matrices, float x, float y, float w, float h, boolean left, int color) {
        float barW = w * 0.24f;
        float triW = w * 0.42f;
        float triH = h * 0.9f;
        float cy = y + h / 2f - triH / 2f;
        if (left) {
            float barX = x;
            drawRoundedRect(matrices, barX, cy, barW, triH, 0.5f, color);
            drawTriangle(matrices, barX + barW, cy, triW, triH, true, color);
        } else {
            float barX = x + w - barW;
            drawRoundedRect(matrices, barX, cy, barW, triH, 0.5f, color);
            drawTriangle(matrices, barX - triW, cy, triW, triH, false, color);
        }
    }

    private void drawNotificationsInsideMediaPlayer(EventRender2D event, float anchorX, float anchorY) {
        java.util.List<Notification> notifications = Manager.NOTIFICATION_MANAGER.getNotifications();
        if (notifications.isEmpty()) return;

        var ctx = event.getDrawContext();
        var matrices = ctx.getMatrices();
        var font = FontUtils.durman[13];

        float gap = 3f;
        float yOffset = 0f;

        for (java.util.Iterator<Notification> it = notifications.iterator(); it.hasNext();) {
            Notification n = it.next();
            long timePassed = System.currentTimeMillis() - n.getTime();
            long totalDuration = n.getDuration() * 1000L;

            if (timePassed > totalDuration - 222) {
                n.animation.setDirection(Direction.AxisDirection.NEGATIVE);
            }
            float alpha = (float) n.animation.getOutput();

            if (timePassed > totalDuration) {
                n.animationy.setDirection(Direction.AxisDirection.NEGATIVE);
            }
            if (n.animationy.finished(Direction.AxisDirection.NEGATIVE)) {
                it.remove();
                continue;
            }

            String title = n.getName();
            String statusText = n.getType() == NotificationType.SUCCESS ? "Включен" : n.getType() == NotificationType.REMOVED ? "Выключен" : (n.getDesc() != null ? n.getDesc() : "");
            float titleWidth = font.getWidth(title);
            float statusWidth = FontUtils.durman[12].getWidth(statusText);
            float textWidth = Math.max(titleWidth, statusWidth);

            float paddingX = 8f;
            float paddingY = 4f;
            float width = Math.max(paddingX * 2 + textWidth, 90f);
            float height = 26f;
            float barHeight = 2.5f;

            float animX = (float) n.animation.getOutput();
            float animY = (float) n.animationy.getOutput();

            float baseX = anchorX - width - 4f;
            float baseY = anchorY + yOffset;
            float x = baseX;
            float y = baseY;

            if (n.animation.getDirection() == Direction.AxisDirection.NEGATIVE) {
                x = baseX + width * (1f - animX);
            } else {
                float slideIn = (1f - animX) * 18f;
                x = baseX + slideIn;
            }

            if (n.animationy.getDirection() == Direction.AxisDirection.NEGATIVE) {
                y = baseY - height * animY;
            } else {
                y = baseY - (1f - animY) * 14f;
            }

            n.setX(x);
            n.setY(y);

            int bgColor = ColorUtil.rgba(18, 12, 35, (int)(190 * alpha));
            int enabledColor = ColorUtil.rgba(90, 200, 120, (int)(220 * alpha));
            int disabledColor = ColorUtil.rgba(220, 90, 90, (int)(220 * alpha));
            int statusColor = n.getType() == NotificationType.SUCCESS ? enabledColor : n.getType() == NotificationType.REMOVED ? disabledColor : ColorUtil.rgba(210, 210, 210, (int)(220 * alpha));

            RenderUtil.drawRoundedRect(matrices, x, y, width, height, 6f, bgColor);

            float textX = x + paddingX;
            float titleY = y + paddingY;
            float statusY = y + height - paddingY - 7f;

            FontUtils.durman[13].drawLeftAligned(matrices, title, textX, titleY, ColorUtil.rgba(255, 255, 255, (int)(220 * alpha)));
            FontUtils.durman[12].drawLeftAligned(matrices, statusText, textX, statusY, statusColor);

            long timePassedN = System.currentTimeMillis() - n.getTime();
            long totalDurationN = n.getDuration() * 1000L;
            float progressN = totalDurationN > 0 ? Math.max(0f, 1f - (float) timePassedN / (float) totalDurationN) : 0f;
            float barWidthN = width * progressN;
            if (barWidthN > 0.5f) {
                int barColorN = ColorUtil.rgba(255, 255, 255, (int)(80 * alpha));
                RenderUtil.drawRoundedRect(matrices, x + 1f, y + height - barHeight - 1f, barWidthN, barHeight, 1.25f, barColorN);
            }

            yOffset += height + barHeight + gap;
        }
    }
    private void armor(EventRender2D eventRender2D) {
        float x = armorDrag.getX();
        float y = armorDrag.getY();
        int armorCount = 0;
        for (int i = 0; i < 4; i++) {
            if (!mc.player.getInventory().armor.get(i).isEmpty()) armorCount++;
        }

        int width = armorCount > 0 ? 20 * armorCount : 35;
        armorDrag.setWidth(width);
        armorDrag.setHeight(18);

        float startX = x + width - 20;
        for (int i = 0; i < 4; i++) {
            ItemStack itemStack = mc.player.getInventory().armor.get(i);
            if (!itemStack.isEmpty()) {
                eventRender2D.getDrawContext().getMatrices().push();
                eventRender2D.getDrawContext().getMatrices().translate(startX, y + 0.2f, 0);
                eventRender2D.getDrawContext().getMatrices().scale(1, 1, 1);
                eventRender2D.getDrawContext().drawItem(itemStack, 0, 0, 0);
                eventRender2D.getDrawContext().drawStackOverlay(mc.textRenderer, itemStack, 0, 0);
                eventRender2D.getDrawContext().getMatrices().pop();
                startX -= 20;
            }
        }
    }

    private void updateStaffPlayers(MinecraftClient mc) {
        staffPlayers.clear();
        addedPlayers.clear();

        Map<String, PlayerListEntry> nameToEntry = new HashMap<>(mc.player.networkHandler.getPlayerList().size() + 4);
        for (PlayerListEntry e : mc.player.networkHandler.getPlayerList()) {
            if (e.getProfile() != null && e.getProfile().getName() != null) {
                nameToEntry.put(e.getProfile().getName().toLowerCase(Locale.ROOT), e);
            }
        }

        String ourName = mc.player.getName().getString();
        Scoreboard scoreboard = mc.world.getScoreboard();

        for (Team team : scoreboard.getTeams()) {
            Text prefixComponent = team.getPrefix();
            String prefix = prefixComponent.getString();
            String cleanPrefixLower = repairString(prefix).toLowerCase(Locale.ROOT);

            for (String member : team.getPlayerList()) {
                if (member == null || member.equals(ourName) || addedPlayers.contains(member)) continue;
                if (!NAME_PATTERN.matcher(member).matches()) continue;

                PlayerListEntry entry = nameToEntry.get(member.toLowerCase(Locale.ROOT));
                boolean isVanished = (entry == null);

                if (!isVanished) {
                    if (PREFIX_MATCHES.matcher(cleanPrefixLower).matches() || Manager.STAFF_MANAGER.isStaff(member)) {
                        java.util.UUID uuid = entry.getProfile().getId();
                        staffPlayers.add(new StaffPlayer(member, prefixComponent, uuid));
                        addedPlayers.add(member);
                    }
                } else {
                    if (!prefix.isEmpty()) {
                        staffPlayers.add(new StaffPlayer(member, prefixComponent, null));
                        addedPlayers.add(member);
                    }
                }
            }
        }

        if (!staffPlayers.isEmpty()) {
            staffPlayers.sort(Comparator.comparing(StaffPlayer::getName));
        }
    }




    private float potionListHeightDynamic = 0;
    private float potionSmoothWidth = -1f;
    private float potionSmoothHeight = -1f;
    private long potionLastTimeNs = 0L;
    private final float[] potionRowSlideOffset = new float[64];
    private final java.util.LinkedHashMap<String, Float> potionRowAnim = new java.util.LinkedHashMap<>();
    private final java.util.LinkedHashMap<String, StatusEffectInstance> potionRowCache = new java.util.LinkedHashMap<>();

    private float potionComputeDtSeconds() {
        long now = System.nanoTime();
        if (potionLastTimeNs == 0L) {
            potionLastTimeNs = now;
            return 1f / 60f;
        }
        long d = now - potionLastTimeNs;
        potionLastTimeNs = now;
        double dt = Math.min(Math.max(d / 1_000_000_000.0, 0.0), 0.1);
        return (float) dt;
    }

    private void potion(EventRender2D eventRender2D) {
        float posX = potionhudDrag.getX();
        float posY = potionhudDrag.getY();
        float time = (System.currentTimeMillis() % 2000L) / 2000f;
        float pulse = (float)(Math.sin(time * Math.PI * 2) * 0.1f + 0.9f);

        float dt = potionComputeDtSeconds();

        var matrices = eventRender2D.getDrawContext().getMatrices();

        int headerHeight = 16;
        int rowHeight = headerHeight - 4;
        int rowGap = 1;
        int padding = 4;

        potionEffectsTmp.clear();
        potionEffectsTmp.addAll(mc.player.getStatusEffects());
        List<StatusEffectInstance> activeEffects = potionEffectsTmp;
        float maxWidth = 100;
        List<Runnable> list = Lists.newArrayListWithCapacity(activeEffects.size());

        float maxDurationWidth = 0;
        java.util.LinkedHashSet<String> current = new java.util.LinkedHashSet<>();
        int idx = 0;
        for (StatusEffectInstance eff : activeEffects) {
            String name = I18n.translate(eff.getEffectType().value().getTranslationKey());
            int level = eff.getAmplifier() + 1;
            String levelStr = (level > 1) ? " " + level : "";
            String displayName = name + levelStr;
            float nameWidth = FontUtils.sfns_display_bold[13].getWidth(displayName);

            String duration = formatDuration(eff);
            float durationWidth = FontUtils.sfns_display_bold[13].getWidth(duration);
            maxDurationWidth = Math.max(maxDurationWidth, durationWidth);

            float totalWidth = padding * 2 + 25 + nameWidth + padding + durationWidth;
            if (totalWidth > maxWidth) maxWidth = totalWidth;

            String key = eff.getEffectType().value().getTranslationKey() + ":" + eff.getAmplifier();
            current.add(key);
            potionRowCache.put(key, eff);
            potionRowAnim.putIfAbsent(key, 0f);

            if (idx < potionRowSlideOffset.length && potionRowAnim.getOrDefault(key, 0f) <= 0f) {
                potionRowSlideOffset[idx] = 0f;
            }
            idx++;
        }

        java.util.ArrayList<String> potKeys = new java.util.ArrayList<>(potionRowAnim.keySet());
        for (String k : potKeys) {
            float target = current.contains(k) ? 1f : 0f;
            float cur = potionRowAnim.getOrDefault(k, 0f);
            float next = MathUtil.fast(cur, target, 15);
            if (next <= 0.01f && target == 0f) {
                potionRowAnim.remove(k);
                potionRowCache.remove(k);
            } else {
                potionRowAnim.put(k, next);
            }
        }

        float listHeightTarget = 0f;
        int visibleCount = 0;
        for (float p : potionRowAnim.values()) {
            if (p <= 0.01f) continue;
            listHeightTarget += p * (rowHeight + rowGap);
            visibleCount++;
        }
        if (visibleCount > 0) listHeightTarget -= rowGap;
        potionListHeightDynamic = MathUtil.fast(potionListHeightDynamic, listHeightTarget, 15);
        float totalHeightTarget = headerHeight + potionListHeightDynamic + 3f;
        int alpha = Math.min(255, Math.round(customAlpha.get().intValue() * 0.7f));

        boolean plainHud = hudColor.is("Обычный");
        boolean mono = hudColor.is("Обычный") || (isMonoTheme() && !hudColor.is("Тема HUD"));

        boolean hasEffects = !potionRowAnim.isEmpty();
        float fullHeightPot = headerHeight + (hasEffects ? potionListHeightDynamic + rowGap : 0);

        if (potionSmoothWidth < 0f) potionSmoothWidth = maxWidth;
        if (potionSmoothHeight < 0f) potionSmoothHeight = Math.max(30f, totalHeightTarget);
        potionSmoothWidth = smoothTowards(potionSmoothWidth, maxWidth, dt, 12f);
        potionSmoothHeight = smoothTowards(potionSmoothHeight, Math.max(30f, totalHeightTarget), dt, 12f);

        float widthDynamic = MathHelper.floor(potionSmoothWidth * 2.0f) / 2.0f;
        float heightDynamic = MathHelper.floor(potionSmoothHeight * 2.0f) / 2.0f;
        widthDynamic = Math.max(90f, widthDynamic);

        if (alpha <= 240 && blur.get()) {
            // blur под всей карточкой Active potions
            drawBlur(matrices, posX, posY, widthDynamic, fullHeightPot, new Vector4f(4, 4, 4, 4), 12, Color.white.getRGB());
        }

        int headerColorPot = plainHud
                ? new Color(0, 0, 0, Math.min(255, (int) (alpha * 0.85f))).getRGB()
                : hudSurface(24, 20, 55, Math.min(255, (int) (alpha * 0.80f)), 0.3f);
        int headerColorPotTop = headerColorPot;

        // шапка поверх
        drawRoundedRect(matrices, posX, posY, widthDynamic, headerHeight, new Vector4f(4.0f, 4.0f, 4.0f, 4.0f), headerColorPotTop);

        int headerIconSize = 10;
        float headerIconY = posY + (headerHeight - headerIconSize) / 2f;
        RenderUtil.drawTexture(matrices, "images/hud/potion.png", posX + 8, headerIconY, headerIconSize, headerIconSize, 0, Color.white.getRGB());

        var headerFont = FontUtils.sfns_display_bold[14];
        float headerTextY = posY + (headerHeight - headerFont.getHeight()) / 2f + 0.6f;
        float potionTextX = posX + 8 + headerIconSize + 3;
        float sepX = posX + 8 + headerIconSize + 1.8f;
        float sepY = posY + (headerHeight - 8f) / 2f;
        RenderUtil.drawRoundedRect(matrices, sepX, sepY, 0.5f, 8f, 0f,
                ColorUtil.applyAlpha(Color.white.getRGB(), plainHud ? 0.10f : 0.08f));
        headerFont.drawLeftAligned(matrices, "Active potions", potionTextX, headerTextY, Color.white.getRGB());

        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, widthDynamic, headerHeight + potionListHeightDynamic + rowGap);

        var rowFont = FontUtils.sfns_display_bold[13];
        float yOffset = posY + headerHeight + rowGap;
        float xShift = 1.0f;
        StatusEffectSpriteManager spriteManager = mc.getStatusEffectSpriteManager();

        // render active first then fading
        java.util.ArrayList<String> order = new java.util.ArrayList<>();
        order.addAll(current);
        for (String k : potionRowAnim.keySet()) {
            if (!current.contains(k)) order.add(k);
        }

        int rowIndex = 0;
        for (String key : order) {
            float itemProgress = potionRowAnim.getOrDefault(key, 0f);
            if (itemProgress <= 0.01f) continue;

            StatusEffectInstance effect = potionRowCache.get(key);
            if (effect == null) continue;
            RegistryEntry<StatusEffect> holder = effect.getEffectType();
            Sprite sprite = spriteManager.getSprite(holder);
            String name = I18n.translate(effect.getTranslationKey());
            int level = effect.getAmplifier() + 1;
            String levelStr = (level > 1) ? " " + level : "";
            String displayName = name + levelStr;
            String duration = formatDuration(effect);
            if (rowIndex >= potionRowSlideOffset.length) break;

            float targetSlide = current.contains(key) ? 1f : 0f;
            potionRowSlideOffset[rowIndex] = smoothTowards(potionRowSlideOffset[rowIndex], targetSlide, dt, 8f);
            float slidePC = easeOutCubic(potionRowSlideOffset[rowIndex]);

            float rowX = posX - widthDynamic + widthDynamic * slidePC;
            float rowY = yOffset;

            int blinkAlpha = 255;

            float textPC = slidePC;
            int nameColor = ColorUtil.applyAlpha(new Color(240, 240, 240).getRGB(), textPC);
            int timeColor = ColorUtil.applyAlpha(new Color(245, 245, 245).getRGB(), textPC);
            int iconColor = ColorUtil.applyAlpha(Color.white.getRGB(), textPC);

            int rowAlpha = Math.min(255, (int) (alpha * 0.75f * slidePC * (blinkAlpha / 255f)));
            int rowColor = plainHud
                    ? new Color(0, 0, 0, rowAlpha).getRGB()
                    : (mono
                        ? new Color(45, 45, 45, rowAlpha).getRGB()
                        : hudSurface(24, 20, 55, rowAlpha, 0.28f));
            drawRoundedRect(matrices, rowX, rowY, widthDynamic, rowHeight, 4.0f, rowColor);

            RenderUtil.drawRoundedRect(matrices, rowX + widthDynamic - 11f, rowY + 3f, 0.5f, 6f, 0f,
                    ColorUtil.applyAlpha(Color.white.getRGB(), plainHud ? 0.10f * slidePC : 0.08f * slidePC));

            float contentY = rowY + (rowHeight - rowFont.getHeight()) / 2f;
            float durationX = rowX + 8f + xShift;
            float durationW = rowFont.getWidth(duration);
            rowFont.drawLeftAligned(matrices, duration, durationX, contentY, timeColor);

            float iconX = rowX + widthDynamic - 8f - 12f + xShift;
            float iconY = rowY + (rowHeight - 12f) / 2f;
            drawSprite(matrices, sprite, iconX, iconY, 12f, 12f, iconColor);

            float nameX = durationX + durationW + 6f;
            float maxNameW = (iconX - 4f) - nameX;
            String nameToDraw = displayName;
            if (maxNameW > 0f && rowFont.getWidth(nameToDraw) > maxNameW) {
                String suffix = "...";
                float suffixW = rowFont.getWidth(suffix);
                if (suffixW >= maxNameW) {
                    nameToDraw = suffix;
                } else {
                    int len = nameToDraw.length();
                    while (len > 0) {
                        String candidate = nameToDraw.substring(0, len) + suffix;
                        if (rowFont.getWidth(candidate) <= maxNameW) {
                            nameToDraw = candidate;
                            break;
                        }
                        len--;
                    }
                }
            }
            rowFont.drawLeftAligned(matrices, nameToDraw, nameX, contentY, nameColor);

            yOffset += (rowHeight + rowGap) * itemProgress;
            rowIndex++;
        }

        Scissor.unset();
        Scissor.pop();
        list.forEach(Runnable::run);

        potionhudDrag.setWidth(widthDynamic);
        potionhudDrag.setHeight(heightDynamic);
    }

    private void drawSprite(MatrixStack matrices, Sprite sprite, float x, float y, float w, float h, int color) {
        RenderUtil.enableRender();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, sprite.getAtlasId());

        float a = ((color >>> 24) & 0xFF) / 255f;
        float r = ((color >>> 16) & 0xFF) / 255f;
        float g = ((color >>> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();

        var mat = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(mat, x, y + h, 0).texture(u0, v1).color(r, g, b, a);
        buffer.vertex(mat, x + w, y + h, 0).texture(u1, v1).color(r, g, b, a);
        buffer.vertex(mat, x + w, y, 0).texture(u1, v0).color(r, g, b, a);
        buffer.vertex(mat, x, y, 0).texture(u0, v0).color(r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderUtil.disableRender();
    }



    private String formatDuration(StatusEffectInstance eff) {
        if (eff.isInfinite() || eff.getDuration() > 18000) {
            return "**:**";
        }
        String raw = StatusEffectUtil.getDurationText(eff, 1.0F, 20.0f).getString();
        return raw.replace("{", "").replace("}", "");
    }

    private float cooldownListHeightDynamic = 0;

    private void cooldown(EventRender2D eventRender2D) {
        float posX = itemcooldownDrag.getX();
        float posY = itemcooldownDrag.getY();
        int headerHeight = 18;
        int padding = 5;
        int lineHeight = 10;
        var matrices = eventRender2D.getDrawContext().getMatrices();

        List<Item> activeItems = new ArrayList<>();
        float maxWidth = 100;

        ItemCooldownManager manager = mc.player.getItemCooldownManager();
        ItemCooldownManagerAccessor accessor = (ItemCooldownManagerAccessor) manager;

        for (Item item : TRACKED_ITEMS) {
            ItemStack stack = new ItemStack(item);
            if (manager.isCoolingDown(stack)) {
                activeItems.add(item);

                String itemName = ITEM_NAMES.getOrDefault(item, stack.getName().getString());

                Identifier id = manager.getGroup(stack);
                Object rawEntry = accessor.getEntries().get(id);

                float remainingSeconds = 0f;
                if (rawEntry instanceof ItemCooldownEntryAccessor entry) {
                    int end = entry.getEndTick();
                    int current = accessor.getTick();
                    float remainingTicks = end - (current + mc.getRenderTickCounter().getTickDelta(true));
                    remainingSeconds = Math.max(0f, remainingTicks / 20.0f);
                }

                String timeLeft = formatCooldownTime(remainingSeconds);

                float nameWidth = FontUtils.sfns_display_bold[13].getWidth(itemName);
                float timeWidth = FontUtils.sfns_display_bold[13].getWidth(timeLeft);
                float totalWidth = padding * 2 + 25 + nameWidth + padding + timeWidth;

                if (totalWidth > maxWidth) maxWidth = totalWidth;
            }
        }

        float listHeightTarget = activeItems.size() * lineHeight;
        cooldownListHeightDynamic = MathUtil.fast(cooldownListHeightDynamic, listHeightTarget, 15);
        float totalHeight = headerHeight + cooldownListHeightDynamic;

        int alpha = Math.min(255, Math.round(customAlpha.get().intValue() * 0.7f));
        boolean plainHud = hudColor.is("Обычный");

        boolean hasCooldowns = !activeItems.isEmpty();
        float fullHeightCd = headerHeight + (hasCooldowns ? cooldownListHeightDynamic + 6 : 0);

        if (alpha <= 240 && blur.get()) {
            // blur под карточкой cooldowns
            drawBlur(matrices, posX, posY, maxWidth, fullHeightCd, new Vector4f(4, 4, 4, 4), 12, Color.white.getRGB());
        }

        int headerColorCd = plainHud
                ? new Color(0, 0, 0, Math.min(255, (int) (alpha * 0.85f))).getRGB()
                : hudSurface(24, 20, 55, Math.min(255, (int) (alpha * 0.80f)), 0.3f);
        int listBgColorCd = plainHud
                ? new Color(0, 0, 0, Math.min(255, (int) (alpha * 0.70f))).getRGB()
                : hudSurface(30, 26, 70, Math.min(255, (int) (alpha * 0.70f)), 0.22f);
        int pillColorCd = plainHud
                ? new Color(0, 0, 0, Math.min(255, (int) (alpha * 0.85f))).getRGB()
                : hudSurface(40, 36, 90, Math.min(255, (int) (alpha * 0.85f)), 0.38f);

        // при "Обычный" — solid black for list
        drawRoundedRect(matrices, posX, posY, maxWidth, fullHeightCd, 4.0f, listBgColorCd);

        int headerColorCdTop = headerColorCd;

        // шапка поверх
        drawRoundedRect(matrices, posX, posY, maxWidth, headerHeight, new Vector4f(4.0f, 4.0f, 4.0f, 4.0f), headerColorCdTop);

        int headerIconSize = 11;
        float headerIconY = posY + (headerHeight - headerIconSize) / 2f;
        RenderUtil.drawTexture(matrices, "images/hud/cooldown.png", posX + 8, headerIconY, headerIconSize, headerIconSize, 0, Color.white.getRGB());

        var headerFont = FontUtils.sfns_display_bold[14];
        float headerTextY = posY + (headerHeight - headerFont.getHeight()) / 2f;
        float cooldownCenterX = posX + maxWidth / 2f;
        float cooldownTextWidth = headerFont.getWidth("Cooldowns");
        headerFont.drawLeftAligned(matrices, "Cooldowns", cooldownCenterX - cooldownTextWidth / 2f, headerTextY, Color.white.getRGB());

        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, maxWidth, (headerHeight + cooldownListHeightDynamic + padding / 2.0F + 5));

        var rowFont = FontUtils.sfns_display_bold[13];
        float yOffset = posY + headerHeight + padding;
        for (Item item : activeItems) {
            ItemStack stack = item.getDefaultStack();
            String itemName = ITEM_NAMES.getOrDefault(item, stack.getName().getString());

            Identifier id = manager.getGroup(stack);
            Object rawEntry = accessor.getEntries().get(id);

            float remainingSeconds = 0f;
            if (rawEntry instanceof ItemCooldownEntryAccessor entry) {
                int end = entry.getEndTick();
                int current = accessor.getTick();
                float remainingTicks = end - (current + mc.getRenderTickCounter().getTickDelta(true));
                remainingSeconds = Math.max(0f, remainingTicks / 20.0f);
            }

            String timeLeft = formatCooldownTime(remainingSeconds);

            float rowIconY = yOffset + (lineHeight - 9f) / 2f;
            RenderAddon.renderItem(eventRender2D.getDrawContext(), stack, posX + padding - 1.5f, rowIconY, 0.6f,false);

            // вертикальная полоска предмета в виде маленькой "таблетки"
            RenderUtil.drawRoundedRect(matrices, posX + padding + 10, rowIconY, 1.2f, 9, 4.5f, hudSeparator(120));

            float rowTextY = yOffset + (lineHeight - rowFont.getHeight()) / 2f;
            rowFont.drawLeftAligned(matrices, itemName, posX + padding + 14f, rowTextY, -1);

            float timeWidth = rowFont.getWidth(timeLeft);
            // фон времени кд — таблетка
            float pillY = yOffset + (lineHeight - 10f) / 2f;
            RenderUtil.drawRoundedRect(matrices, posX + maxWidth - timeWidth - padding - 5, pillY, 6 + timeWidth, 10, 5f, pillColorCd);
            rowFont.drawLeftAligned(matrices, timeLeft, posX + maxWidth - timeWidth - padding - 2, rowTextY, -1);

            yOffset += lineHeight;
        }

        Scissor.unset();
        Scissor.pop();

        itemcooldownDrag.setWidth(maxWidth);
        itemcooldownDrag.setHeight(totalHeight + 5);
    }



    private int activeStaff = 0;
    private float hDynam = 0;
    private float widthDynamic = 0;
    private float nameWidth = 0;

    private void staffList(EventRender2D render2D) {
        float posX = stafflistDrag.getX();
        float posY = stafflistDrag.getY();

        var fontBig = FontUtils.sfns_display_bold[14];
        var fontSmall = FontUtils.sfns_display_bold[13];

        int headerHeight = 17;
        int rowHeight = headerHeight - 4;
        int rowGap = 1;
        int padding = 3;
        int offset = rowHeight + rowGap;
        float width = Math.max(nameWidth + 60, 100);
        int index = 0;
        nameWidth = 0;

        hDynam = MathUtil.fast(this.hDynam, activeStaff > 0 ? (activeStaff * rowHeight + (activeStaff - 1) * rowGap) : 0, 15);
        widthDynamic = MathUtil.fast(this.widthDynamic, width, 8);

        int alpha = Math.min(255, Math.round(customAlpha.get().intValue() * 0.7f));
        boolean plainHud = hudColor.is("Обычный");

        int headerColorStaff = plainHud
                ? new Color(0, 0, 0, Math.min(255, (int) (alpha * 0.85f))).getRGB()
                : hudSurface(24, 20, 55, Math.min(255, (int) (alpha * 0.80f)), 0.3f);
        boolean hasStaff = !staffPlayers.isEmpty();
        float fullHeightStaff = headerHeight + (hasStaff ? hDynam + rowGap : 0);

        // шапка поверх
        drawRoundedRect(render2D.getDrawContext().getMatrices(), posX, posY, widthDynamic, headerHeight, new Vector4f(4.0f, 4.0f, 4.0f, 4.0f), headerColorStaff);

        int headerIconSize = 11;
        float headerIconY = posY + (headerHeight - headerIconSize) / 2f;
        RenderUtil.drawTexture(render2D.getDrawContext().getMatrices(), "images/hud/staff.png", posX + 8, headerIconY, headerIconSize, headerIconSize, 0, Color.white.getRGB());

        float headerTextY = posY + (headerHeight - fontBig.getHeight()) / 2f;
        float staffTextX = posX + 8 + headerIconSize + 3;
        float sepX = posX + 8 + headerIconSize + 1.8f;
        float sepY = posY + (headerHeight - 8f) / 2f;
        RenderUtil.drawRoundedRect(render2D.getDrawContext().getMatrices(), sepX, sepY, 0.5f, 8f, 0f,
                ColorUtil.applyAlpha(Color.white.getRGB(), plainHud ? 0.10f : 0.08f));
        fontBig.drawLeftAligned(render2D.getDrawContext().getMatrices(), "StaffList", staffTextX, headerTextY, Color.white.getRGB());

        if (hasStaff) {
            Scissor.push();
            Scissor.setFromComponentCoordinates(posX, posY, widthDynamic, headerHeight + hDynam + rowGap);

            Map<String, PlayerListEntry> playerInfoMap = new HashMap<>();
            for (PlayerListEntry info : mc.getNetworkHandler().getPlayerList()) {
                playerInfoMap.put(info.getProfile().getName(), info);
            }

            float rowDenom = Math.max(1f, (rowHeight + rowGap));
            float visibleRows = hDynam / rowDenom;
            for (StaffPlayer staff : staffPlayers) {
                String staffname = staff.getName();
                String status = staff.getStatus().getString();
                float statusWidth = fontSmall.getWidth(status);
                float currentWidth = fontSmall.getWidth(staffname);
                if (currentWidth > nameWidth) nameWidth = currentWidth;

                float baseY = posY + headerHeight + rowGap + (index * (rowHeight + rowGap));
                float itemProgress = MathHelper.clamp(visibleRows - index, 0f, 1f);
                float animOffsetY = (1f - itemProgress) * (rowHeight + rowGap);
                float rowY = baseY - animOffsetY;

                int pillAlpha = Math.min(255, (int) (alpha * 0.85f * itemProgress));
                int pillColor = new Color(0, 0, 0, pillAlpha).getRGB();
                drawRoundedRect(render2D.getDrawContext().getMatrices(), posX, rowY, widthDynamic, rowHeight, 3.5f, pillColor);

                PlayerListEntry playerInfo = playerInfoMap.get(staffname);
                if (playerInfo != null) {
                    if (!(staff.getStatus() == StaffPlayer.Status.VANISHED || staff.getStatus() == StaffPlayer.Status.SPEC)) {
                        RenderAddon.drawStaffHead(render2D.getDrawContext().getMatrices(), playerInfo.getSkinTextures().texture(), posX + 6f, rowY + (rowHeight - 9f) / 2f, 9, 3);
                    }
                } else {
                    int iconColor = ColorUtil.applyAlpha(Color.white.getRGB(), itemProgress);
                    RenderUtil.drawTexture(render2D.getDrawContext().getMatrices(), "images/hud/staffvanish.png", posX + 6f, rowY + (rowHeight - 9f) / 2f, 9, 9, 3, iconColor);
                }

                int nameColor = ColorUtil.applyAlpha(Color.WHITE.getRGB(), itemProgress);
                int statusColor = ColorUtil.applyAlpha(getStatusColor(staff.getStatus()), itemProgress);
                float textY = rowY + (rowHeight - fontSmall.getHeight()) / 2f;
                fontSmall.drawLeftAligned(render2D.getDrawContext().getMatrices(), staffname, posX + 6f + 12f, textY, nameColor);
                fontSmall.drawLeftAligned(render2D.getDrawContext().getMatrices(), status, posX + widthDynamic - statusWidth - 6f, textY, statusColor);

                index++;
            }

            Scissor.unset();
            Scissor.pop();
        }

        activeStaff = index;
        stafflistDrag.setWidth(widthDynamic);
        stafflistDrag.setHeight(hDynam + headerHeight + padding + 1);
    }

    private float lastHealth = 0.0f;
    private float lastAbsorption = 0.0f;
    private float goldenAppleFlash = 0.0f;
    private boolean lastHadGoldenApple = false;
    private long goldenAppleUntilMs = 0L;
    private int lastTargetEntityId = -1;

    private float stableHpValue = -1.0f;
    private float stableMaxHpValue = 20.0f;
    private long stableHpLastUpdateMs = 0L;

    private float lastBarFill = 1.0f;
    private float lastAbsBarFill = 0.0f;
    private float barFillAnim = 1.0f;
    private float absBarFillAnim = 0.0f;

    private int darkenRgb(int color, float mul) {
        mul = MathHelper.clamp(mul, 0.0f, 1.0f);
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        r = MathHelper.clamp((int) (r * mul), 0, 255);
        g = MathHelper.clamp((int) (g * mul), 0, 255);
        b = MathHelper.clamp((int) (b * mul), 0, 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private void drawGradientText(RenderFonts font, MatrixStack matrices, String text, float x, float y, int c1, int c2) {
        if (text == null || text.isEmpty()) return;
        float cx = x;
        int len = text.length();
        for (int i = 0; i < len; i++) {
            String s = String.valueOf(text.charAt(i));
            float t = len <= 1 ? 0.0f : (i / (float) (len - 1));
            int col = ColorUtil.interpolateColor(c1, c2, t);
            font.drawLeftAligned(matrices, s, cx, y, col);
            cx += font.getWidth(s);
        }
    }

    private void targethud(EventRender2D render2D) {
        float x = targethudDrag.getX();
        float y = targethudDrag.getY();

        target = getTarget(target);
        scale = tHudAnimation.getOutput();
        if (scale == 0.0 || target == null) return;

        float[] serverHealth = getServerHealthForTarget(target);
        float currentHealth = serverHealth[0];
        float maxHealth = serverHealth[1];

        int curTargetId = target.getId();
        if (lastTargetEntityId != curTargetId) {
            lastTargetEntityId = curTargetId;
            stableHpValue = currentHealth;
            stableMaxHpValue = Math.max(1.0f, maxHealth);
            stableHpLastUpdateMs = System.currentTimeMillis();
        }

        float currentAbsorptionRaw = Math.max(0.0f, target.getAbsorptionAmount());
        float rawHealth = MathHelper.clamp(currentHealth / Math.max(1.0f, maxHealth), 0.0F, 1.0F);
        float rawAbsorption = MathHelper.clamp(currentAbsorptionRaw, 0.0F, 20.0F);

        lastHealth = MathUtil.fast(lastHealth, rawHealth, 8);
        lastAbsorption = (currentAbsorptionRaw > 0.0f) ? MathUtil.fast(lastAbsorption, rawAbsorption, 8) : 0.0f;

        float displayHp = currentHealth;
        float displayAbs = currentAbsorptionRaw;
        String hpText;
        if (displayAbs > 0.05f) {
            hpText = String.format(Locale.ENGLISH, "HP: %.1f (+%.1f)", displayHp + displayAbs, displayAbs);
        } else {
            hpText = String.format(Locale.ENGLISH, "HP: %.1f", displayHp);
        }

        int alpha = Math.min(255, Math.round(customAlpha.get().intValue() * 0.7f));
        float animAlpha = (float) MathHelper.clamp(scale, 0.0, 1.0);
        int alphaAnim = MathHelper.clamp(Math.round(alpha * animAlpha), 0, 255);
        boolean plainHud = hudColor.is("Обычный");

        float width = 106f;
        float height = 38f;
        float radius = 7f;
        float margin = 4f;
        float headSize = 33f;

        float appendY = y + margin;

        render2D.getMatrixStack().push();
        RenderAddon.sizeAnimation(render2D.getMatrixStack(), x + width / 2.0F, y + height / 2.0F, scale);

        MatrixStack matrices = render2D.getDrawContext().getMatrices();
        Vector4f r = new Vector4f(radius, radius, radius, radius);

        if (blur.get() && alpha <= 240) {
            drawBlur(matrices, x, y, width, height, r, 12, Color.white.getRGB());
        }

        int baseCard = plainHud
                ? new Color(0, 0, 0, alphaAnim).getRGB()
                : hudSurface(30, 26, 70, alphaAnim, 0.22f);

        drawRoundedRect(matrices, x, y, width, height, r, baseCard);

        float headX = x + 2.0f;
        float headY = y + (height - headSize) * 0.5f;
        RenderAddon.drawHead(matrices, target, headX, headY, headSize, 5.0f);

        float textX = x + 37.5f;
        float nameY = appendY + 0.5f;
        float hpY = appendY + 10.7f;

        RenderFonts nameFont = FontUtils.sf_bold[16];
        RenderFonts smallFont = FontUtils.sf_medium[14];

        String displayName = Manager.FUNCTION_MANAGER.nameProtect.getProtectedName(target.getName().getString());
        if (displayName.length() > 16) displayName = displayName.substring(0, 16) + "...";
        drawTextWithShadow(nameFont, matrices, displayName, textX, nameY, Color.white.getRGB());
        drawTextWithShadow(smallFont, matrices, hpText, textX, hpY, ColorUtil.withAlpha(Color.white.getRGB(), 0.90f));

        float barX = textX - 0.2f;
        float barY = appendY + 29.0f - 3.0f - 6.0f + 0.15f - 0.5f;
        float barW = (x + width) - barX - 3.0f;
        float barH = 10.0f;

        float barR = 2.0f;
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int barBgGray = ColorUtil.withAlpha(new Color(15, 15, 15).getRGB(), Math.min(1f, alphaAnim / 255f));
        drawRoundedRect(matrices, barX, barY, barW, barH, barR, barBgGray);

        float absW = barW * MathHelper.clamp(lastAbsorption / maxHealth, 0.0F, 0.5F);
        float fillW = barW * lastHealth;
        if (fillW > 0.0f) {
            float fillR = Math.min(barR, fillW / 2f);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            String themeName = "";
            if (Manager.STYLE_MANAGER.getTheme() != null && Manager.STYLE_MANAGER.getTheme().name != null) {
                themeName = Manager.STYLE_MANAGER.getTheme().name.trim();
            }
            boolean isClientTheme = themeName.equalsIgnoreCase("Клиентский");

            int themeC1 = Manager.STYLE_MANAGER.getFirstColor();
            int themeC2 = Manager.STYLE_MANAGER.getSecondColor();
            int themeBase = ColorUtil.interpolateColor(themeC1, themeC2, 0.5f);

            int hpColor1;
            int hpColor2;
            if (isClientTheme) {
                hpColor1 = ColorUtil.withAlpha(new Color(0, 0, 0, 255).getRGB(), alphaAnim / 255f);
                hpColor2 = new Color(255, 255, 255, 255).getRGB();
            } else {
                int leftThemed = ColorUtil.interpolateColor(themeBase, new Color(0, 0, 0, 255).getRGB(), 0.55f);
                int rightThemed = ColorUtil.interpolateColor(themeBase, new Color(255, 255, 255, 255).getRGB(), 0.55f);
                hpColor1 = ColorUtil.withAlpha(leftThemed, alphaAnim / 255f);
                hpColor2 = ColorUtil.withAlpha(rightThemed, alphaAnim / 255f);
            }
            rectRGB(matrices, barX, barY, fillW, barH, fillR, hpColor1, hpColor1, hpColor2, hpColor2);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        if (absW > 0.0f) {
            float absR = Math.min(barR, absW / 2f);
            int aAbs = Math.min(255, alphaAnim);
            int absorbStart = new Color(35, 28, 0, aAbs).getRGB();
            int absorbEnd = new Color(255, 245, 160, 255).getRGB();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            rectRGB(matrices, barX, barY, absW, barH, absR,
                    absorbStart, absorbStart, absorbEnd, absorbEnd);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        render2D.getMatrixStack().pop();

        targethudDrag.setWidth(width);
        targethudDrag.setHeight(height);
    }

    private float[] getServerHealthForTarget(LivingEntity target) {
        float currentHealth = target.getHealth();
        float maxHealth = Math.max(1.0F, target.getMaxHealth());

        if (!(target instanceof net.minecraft.entity.player.PlayerEntity)) {
            return new float[]{currentHealth, maxHealth, 0f};
        }

        if (isFunTimeServer()) {
            return new float[]{currentHealth, 20.0f, 0f};
        }

        Float tabHealth = readHealthFromTab(target);
        if (tabHealth != null && isPlausibleServerHp(tabHealth, currentHealth)) {
            return new float[]{tabHealth, 20.0f, 1f};
        }

        Scoreboard scoreboard = target.getWorld().getScoreboard();

        // 0) Любой display slot: многие сервера кладут HP не только в BELOW_NAME
        for (ScoreboardDisplaySlot slot : ScoreboardDisplaySlot.values()) {
            Float slotHp = readHealthFromObjective(scoreboard, slot, target, 20.0f);
            if (slotHp != null && isPlausibleServerHp(slotHp, currentHealth)) {
                return new float[]{slotHp, 20.0f, 1f};
            }
        }

        // 1) fallback: ищем objective по имени/дисплею (HP/health/hearts/❤)
        ScoreboardObjective found = null;
        for (ScoreboardObjective objective : scoreboard.getObjectives()) {
            String display = objective.getDisplayName().getString().toLowerCase(Locale.ROOT);
            String name = objective.getName() != null ? objective.getName().toLowerCase(Locale.ROOT) : "";
            if (display.contains("hp") || display.contains("здоров") || display.contains("health") || display.contains("heart") || display.contains("❤")
                    || name.contains("hp") || name.contains("здоров") || name.contains("health") || name.contains("heart") || name.contains("showhealth")) {
                found = objective;
                break;
            }
        }
        if (found != null) {
            Float any = readHealthFromObjective(scoreboard, found, target, 20.0f);
            if (any != null && isPlausibleServerHp(any, currentHealth)) {
                return new float[]{any, 20.0f, 1f};
            }
        }

        // клиентское как последний вариант
        return new float[]{currentHealth, maxHealth, 0f};
    }

    private boolean isFunTimeServer() {
        if (mc.getCurrentServerEntry() == null) return false;
        String address = mc.getCurrentServerEntry().address;
        if (address == null) return false;
        address = address.toLowerCase(Locale.ROOT);
        return address.contains("funtime");
    }

    private boolean isPlausibleServerHp(float serverHp, float clientHp) {
        if (Float.isNaN(serverHp) || Float.isInfinite(serverHp)) return false;
        if (serverHp < 0.0f || serverHp > 20.0f) return false;
        // некоторые сервера/объективы могут отдавать 0 как плейсхолдер
        if (serverHp <= 0.001f && clientHp > 1.0f) return false;
        return true;
    }

    private Float readHealthFromTab(LivingEntity target) {
        if (mc.getNetworkHandler() == null) return null;
        if (!(target instanceof net.minecraft.entity.player.PlayerEntity player)) return null;

        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (entry == null) return null;
        if (entry.getDisplayName() == null) return null;

        String s = entry.getDisplayName().getString();
        return parseHealthFromText(s, 20.0f);
    }

    private Float parseHealthFromText(String text, float max) {
        if (text == null || text.isEmpty()) return null;
        String s = text.replace(',', '.');

        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("(\\d+(?:\\.\\d+)?)\\s*(?:❤|♥|hp|HP)")
                .matcher(s);
        if (!m.find()) {
            m = java.util.regex.Pattern
                    .compile("(?:❤|♥)\\s*(\\d+(?:\\.\\d+)?)")
                    .matcher(s);
        }

        // если в TAB есть сердечко, но формат нестандартный (число не рядом с ❤)
        if (!m.find() && (s.contains("❤") || s.contains("♥"))) {
            java.util.regex.Matcher num = java.util.regex.Pattern
                    .compile("(\\d+(?:\\.\\d+)?)")
                    .matcher(s);
            String last = null;
            while (num.find()) {
                last = num.group(1);
            }
            if (last == null) return null;
            try {
                float v = Float.parseFloat(last);
                if (v > max + 1.0f && v <= max * 2.0f + 2.0f) {
                    v = v / 2.0f;
                }
                if (v < 0.0f || v > max * 2.0f) return null;
                return MathHelper.clamp(v, 0.0f, max);
            } catch (Exception ignored) {
                return null;
            }
        }

        if (!m.find()) return null;

        try {
            float value = Float.parseFloat(m.group(1));
            if (value > max + 1.0f && value <= max * 2.0f + 2.0f) {
                value = value / 2.0f;
            }
            if (value < 0.0f || value > max * 2.0f) return null;
            return MathHelper.clamp(value, 0.0f, max);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Float readHealthFromObjective(Scoreboard scoreboard, ScoreboardDisplaySlot slot, LivingEntity target, float max) {
        ScoreboardObjective obj = scoreboard.getObjectiveForSlot(slot);
        if (obj == null) return null;
        return readHealthFromObjective(scoreboard, obj, target, max);
    }

    private Float readHealthFromObjective(Scoreboard scoreboard, ScoreboardObjective obj, LivingEntity target, float max) {
        try {
            int score = scoreboard.getOrCreateScore(ScoreHolder.fromName(target.getNameForScoreboard()), obj).getScore();
            if (score < 0) return null;

            // некоторые сервера считают в half-hearts: 40 вместо 20
            float value = score;
            if (value > max + 1.0f && value <= max * 2.0f + 2.0f) {
                value = value / 2.0f;
            }

            if (value < 0 || value > max * 2.0f) return null;
            return MathHelper.clamp(value, 0.0f, max);
        } catch (Exception ignored) {
            return null;
        }
    }




    private void waterMark(EventRender2D render2D) {
        float x = watermarkDrag.getX();
        float y = watermarkDrag.getY();

        updateSmoothNumbers();

        String clientText = "L";
        String playerText = Manager.FUNCTION_MANAGER.nameProtect.state
                ? Manager.FUNCTION_MANAGER.nameProtect.getCustomName()
                : mc.player.getName().getString();
        String fpsText = String.format("%.0f Fps", smoothFps);
        String coordsText = String.format("%.0f %.0f %.0f", smoothCoordX, smoothCoordY, smoothCoordZ);
        String pingText = String.format("%.0f P", smoothPing);

        var matrices = render2D.getDrawContext().getMatrices();
        var font = FontUtils.sfns_display_bold[15];

        long now = System.currentTimeMillis();
        int alpha = Math.min(255, Math.round(customAlpha.get().intValue() * 0.7f));
        float pulse = (float) ((Math.sin(now * 0.05f) + 1f) * 0.5f);
        boolean isOrdinary = hudColor.is("Обычный");
        boolean isMono = isMonoTheme();
        boolean mono = isMono || isOrdinary;
        boolean clientTheme = isClientTheme();
        boolean turquoiseTheme = isTurquoiseTheme();
        boolean turquoisePalette = turquoiseTheme || ((Manager.STYLE_MANAGER.getSecondColor() & 0x00FFFFFF) == 0x002EC4B6);

        float accentIndex = (now / 8L) % 360L;
        int wmAccent;
        int wmAccent2;
        if (isMonoTheme()) {
            wmAccent = Color.white.getRGB();
            wmAccent2 = Color.white.getRGB();
        } else {
            int c1;
            int c2;
            if (hudColor.is("Тема HUD")) {
                int[] c = getHudThemeColors();
                c1 = c[0];
                c2 = c[1];
            } else {
                c1 = Manager.STYLE_MANAGER.getFirstColor();
                c2 = Manager.STYLE_MANAGER.getSecondColor();
            }
            if (gradientType.is("Справа налево")) {
                int tmp = c1;
                c1 = c2;
                c2 = tmp;
            }
            wmAccent = ColorUtil.gradient(5, (int) accentIndex, c1, c2);
            wmAccent2 = ColorUtil.gradient(5, (int) ((accentIndex + 90f) % 360f), c1, c2);
        }
        int baseCardColor = isOrdinary
                ? new Color(0, 0, 0, alpha).getRGB() // black
                : isMono
                ? new Color(90, 90, 90, alpha).getRGB() // lighter gray like KeyBinds
                : hudSurface(30, 26, 70, alpha, 0.22f);

        int pulseTint;
        if (mono) {
            // при обычном/чёрно-белом HUD делаем лёгкую серую пульсацию без цвета
            int midGray = new Color(120, 120, 120, alpha).getRGB();
            int lightGray = new Color(200, 200, 200, alpha).getRGB();
            pulseTint = ColorUtil.interpolateColor(midGray, lightGray, pulse);
        } else {
            pulseTint = useHudTheme()
                    ? hudAccent(90, Math.min(255, alpha + 20))
                    : new Color(150, 150, 150, Math.min(255, alpha + 20)).getRGB();
        }

        int cardColor;
        if (turquoisePalette && hudColor.is("Зависит от темы")) {
            cardColor = ColorUtil.withAlpha(Manager.STYLE_MANAGER.getSecondColor(), alpha / 255f);
        } else {
            cardColor = ColorUtil.interpolateColor(baseCardColor, pulseTint, mono ? 0.18f * pulse : 0.12f * pulse);
        }

        float shimmerT = (float) ((System.currentTimeMillis() % 1800L) / 1800.0);
        shimmerT = 1.0f - shimmerT;
        float sigma = 0.14f;

        float cardHeight = 18f;
        float paddingX = 8f;
        float gapX = 4f;
        float gapY = 3f;
        float textGapLogo = 4f;
        float textGapName = 6f;

        float humanIconWidth = 10f;
        float humanIconGap = 3f;
        float humanIconGapFromLogo = 2f;

        boolean showLogo = watermarkParts.get("Логотип");
        boolean showNick = watermarkParts.get("Ник");
        boolean showRecode = showLogo;
        boolean showMan = showNick;
        boolean showFps = watermarkParts.get("FPS");
        boolean showCoords = watermarkParts.get("Координаты");
        boolean showPing = watermarkParts.get("Пинг");

        if (watermarkPosition.is("По центру")) {
            float pillH = 18f;
            float pillPadX = 10f;

            float sepScaleX = 0.8f;
            float spaceW = font.getWidth(" ");
            float sepBarW = font.getWidth("|") * sepScaleX;
            int sepColor = new Color(170, 170, 170, Math.min(255, (int) (alpha * 0.65f))).getRGB();

            java.util.List<String> parts = new java.util.ArrayList<>();
            if (showLogo) parts.add("<LOGO>");
            if (showNick) parts.add(playerText);
            if (showFps) parts.add(fpsText);
            if (showCoords) parts.add(coordsText);
            if (showPing) parts.add(pingText);

            float lineW = 0f;
            for (int i = 0; i < parts.size(); i++) {
                if (i > 0) lineW += spaceW + sepBarW + spaceW;
                String p = parts.get(i);
                if ("<LOGO>".equals(p)) {
                    lineW += 10f;
                } else {
                    lineW += font.getWidth(p);
                }
            }

            float pillW = Math.max(1f, pillPadX * 2 + lineW);
            float cx = mc.getWindow().getScaledWidth() / 2f - pillW / 2f;

            float radiusPill = 9f;
            drawRoundedRect(matrices, cx, y, pillW, pillH, new Vector4f(radiusPill, radiusPill, radiusPill, radiusPill), cardColor);

            float px = cx + pillPadX;
            float ty = y + 4.5f;
            for (int i = 0; i < parts.size(); i++) {
                if (i > 0) {
                    px += spaceW;
                    matrices.push();
                    matrices.translate(px, 0, 0);
                    matrices.scale(sepScaleX, 1f, 1f);
                    font.drawLeftAligned(matrices, "|", 0, ty, sepColor);
                    matrices.pop();
                    px += sepBarW + spaceW;
                }
                String p = parts.get(i);
                if ("<LOGO>".equals(p)) {
                    float s = 10f;
                    float iy = y + (pillH - s) / 2.0f;
                    RenderUtil.drawTexture(matrices, "images/logo/lovan.png", px, iy, s, s, 0, Color.white.getRGB());
                    px += s;
                } else {
                    font.drawLeftAligned(matrices, p, px, ty, Color.white.getRGB());
                    px += font.getWidth(p);
                }
            }

            watermarkDrag.setHeight(pillH);
            watermarkDrag.setWidth(pillW);
            return;
        }

        boolean packSecondRowToTop = !showFps;

        float iconSize = cardHeight - 5f;
        float statIconSize = 10f;
        float statIconGap = 3f;

        int wmIconBase = isMono
                ? new Color(190, 190, 190, 255).getRGB()
                : (clientTheme
                ? Color.white.getRGB()
                : (turquoisePalette ? Manager.STYLE_MANAGER.getSecondColor() : wmAccent2));

        float logoLabelGap = 4f;
        String logoSeparator = "|";
        float logoSeparatorGap = 3f;
        float logoSeparatorScaleX = 0.8f;
        String logoLabel = "Release";
        float logoCardWidth = 0f;
        if (showLogo) {
            float w = paddingX * 2;
            if (showLogo) {
                w += iconSize;
            }
            if (showLogo && showRecode) {
                w += logoLabelGap;
                w += font.getWidth(logoSeparator) * logoSeparatorScaleX + logoSeparatorGap;
            }
            if (showRecode) {
                w += font.getWidth(logoLabel);
            }
            logoCardWidth = Math.max(1f, w);
        }

        float nameCardWidth = 0f;
        if (showNick) {
            float w = paddingX * 2;
            if (showMan) {
                w += humanIconWidth;
            }
            if (showMan && showNick) {
                w += humanIconGap;
            }
            if (showNick) {
                w += font.getWidth(playerText);
            }
            nameCardWidth = Math.max(1f, w);
        }

        float fpsCardWidth = showFps ? (paddingX * 2 + statIconSize + statIconGap + font.getWidth(fpsText)) : 0f;
        float coordsWidth = showCoords ? (paddingX * 2 + font.getWidth(coordsText)) : 0f;
        float pingWidth = showPing ? (paddingX * 2 + statIconSize + statIconGap + font.getWidth(pingText)) : 0f;

        boolean showStar = true;
        float starIconSize = statIconSize + 2f;
        float starCardWidth = showStar ? (paddingX * 2 + starIconSize) : 0f;

        float row1Width = 0f;
        if (logoCardWidth > 0f) row1Width += logoCardWidth;
        if (logoCardWidth > 0f && nameCardWidth > 0f) row1Width += gapX;
        if (nameCardWidth > 0f) row1Width += nameCardWidth;
        if (row1Width > 0f && fpsCardWidth > 0f) row1Width += gapX;
        if (fpsCardWidth > 0f) row1Width += fpsCardWidth;

        if (packSecondRowToTop) {
            if (row1Width > 0f && coordsWidth > 0f) row1Width += gapX;
            if (coordsWidth > 0f) row1Width += coordsWidth;
            if (row1Width > 0f && pingWidth > 0f) row1Width += gapX;
            if (pingWidth > 0f) row1Width += pingWidth;
        }

        float row2Width = 0f;
        if (starCardWidth > 0f) row2Width += starCardWidth;
        if (!packSecondRowToTop) {
            if (row2Width > 0f && coordsWidth > 0f) row2Width += gapX;
            if (coordsWidth > 0f) row2Width += coordsWidth;
            if (row2Width > 0f && pingWidth > 0f) row2Width += gapX;
            if (pingWidth > 0f) row2Width += pingWidth;
        }

        float totalWidth = Math.max(row1Width, row2Width);
        float totalHeight = (row1Width > 0f ? cardHeight : 0f) + (row2Width > 0f ? (cardHeight + (row1Width > 0f ? gapY : 0f)) : 0f);

        Vector4f radius = new Vector4f(4.0f, 4.0f, 4.0f, 4.0f);

        float topY = y;
        float bottomY = y + (row1Width > 0f ? (cardHeight + gapY) : 0f);

        float cursorX = x;
        float textY = topY + 4.5f;

        if (row1Width > 0f) {
            if (alpha <= 240 && blur.get()) {
                if (logoCardWidth > 0f) {
                    drawBlur(matrices, cursorX, topY, logoCardWidth, cardHeight, radius, 12, Color.white.getRGB());
                    cursorX += logoCardWidth;
                    if (nameCardWidth > 0f) cursorX += gapX;
                }
                if (nameCardWidth > 0f) {
                    float nx = x + (logoCardWidth > 0f ? (logoCardWidth + gapX) : 0f);
                    drawBlur(matrices, nx, topY, nameCardWidth, cardHeight, radius, 12, Color.white.getRGB());
                }
                if (fpsCardWidth > 0f) {
                    float fx = x + (logoCardWidth > 0f ? logoCardWidth : 0f) + (nameCardWidth > 0f ? (gapX + nameCardWidth) : 0f);
                    if (logoCardWidth > 0f && nameCardWidth == 0f) fx = x + logoCardWidth;
                    if (logoCardWidth > 0f && nameCardWidth > 0f) fx = x + logoCardWidth + gapX + nameCardWidth;
                    if (fx > x) fx += gapX;
                    if (logoCardWidth == 0f && nameCardWidth > 0f) fx = x + nameCardWidth + gapX;
                    if (logoCardWidth == 0f && nameCardWidth == 0f) fx = x;
                    drawBlur(matrices, fx, topY, fpsCardWidth, cardHeight, radius, 12, Color.white.getRGB());
                }
            }

            float topCardX = x;

            if (logoCardWidth > 0f) {
                drawRoundedRect(matrices, topCardX, topY, logoCardWidth, cardHeight, radius, cardColor);
                float lx = topCardX + paddingX;
                float ly = topY + (cardHeight - iconSize) / 2.0f;

                // unified shimmer (right -> left) across logo + "|Recode"
                int shimmerBase = (clientTheme || mono) ? Color.white.getRGB() : wmAccent2;
                int shimmerA = Math.min(255, (int) (alpha * 0.95f));
                int shimmerHighlight = isDarkColor(shimmerBase)
                        ? new Color(255, 255, 255, shimmerA).getRGB()
                        : new Color(0, 0, 0, shimmerA).getRGB();

                float globalStartX = lx;
                float globalEndX = lx + (showLogo ? iconSize : 0f)
                        + (showLogo && showRecode ? logoLabelGap : 0f)
                        + (showLogo && showRecode ? (font.getWidth(logoSeparator) * logoSeparatorScaleX + logoSeparatorGap) : 0f)
                        + (showRecode ? font.getWidth(logoLabel) : 0f);
                float totalW = Math.max(1.0f, globalEndX - globalStartX);

                float labelX = lx;
                if (showLogo) {
                    float logoCenter = lx + iconSize * 0.55f;
                    float tLogo = MathHelper.clamp((logoCenter - globalStartX) / totalW, 0.0f, 1.0f);
                    float dLogo = Math.abs(tLogo - shimmerT);
                    dLogo = Math.min(dLogo, 1.0f - dLogo);
                    float aLogo = (float) Math.exp(-Math.pow(dLogo / sigma, 2.0));

                    int logoBase = (clientTheme || turquoisePalette) ? Color.white.getRGB() : wmAccent2;
                    int logoColor = ColorUtil.interpolateColor(logoBase, shimmerHighlight, MathHelper.clamp(aLogo, 0.0f, 0.75f));
                    RenderUtil.drawTexture(matrices, "images/logo/lovan.png", lx, ly, iconSize, iconSize, 0, logoColor);

                    labelX = lx + iconSize + (showRecode ? logoLabelGap : 0f);
                }

                if (showLogo && showRecode) {
                    int sepColor = new Color(170, 170, 170, Math.min(255, (int) (alpha * 0.65f))).getRGB();
                    matrices.push();
                    matrices.translate(labelX, 0, 0);
                    matrices.scale(logoSeparatorScaleX, 1f, 1f);
                    font.drawLeftAligned(matrices, logoSeparator, 0, textY, sepColor);
                    matrices.pop();
                    labelX += font.getWidth(logoSeparator) * logoSeparatorScaleX + logoSeparatorGap;

                    // draw "Release" per-char
                    for (int i = 0; i < logoLabel.length(); i++) {
                        String ch = String.valueOf(logoLabel.charAt(i));
                        float t = MathHelper.clamp((labelX - globalStartX) / totalW, 0.0f, 1.0f);
                        float d = Math.abs(t - shimmerT);
                        d = Math.min(d, 1.0f - d);
                        float a = (float) Math.exp(-Math.pow(d / sigma, 2.0));

                        int base = (clientTheme || turquoisePalette) ? Color.white.getRGB() : wmAccent;
                        int c = ColorUtil.interpolateColor(base, shimmerHighlight, MathHelper.clamp(a, 0.0f, 0.75f));
                        font.drawLeftAligned(matrices, ch, labelX, textY, c);
                        labelX += font.getWidth(ch);
                    }
                } else if (showRecode) {
                    font.drawLeftAligned(matrices, logoLabel, labelX, textY, (clientTheme || turquoisePalette) ? Color.white.getRGB() : wmAccent);
                }

                topCardX += logoCardWidth + (nameCardWidth > 0f ? gapX : 0f);
            }

            if (nameCardWidth > 0f) {
                drawRoundedRect(matrices, topCardX, topY, nameCardWidth, cardHeight, radius, cardColor);
                float nx = topCardX + paddingX;
                if (showMan) {
                    float ny = textY + (font.getHeight() - humanIconWidth) / 2.0f;
                    RenderUtil.drawTexture(matrices, "images/logo/man.png", nx, ny, humanIconWidth, humanIconWidth, 0, wmIconBase);
                    nx += humanIconWidth + humanIconGap;
                }
                drawTextWithShadow(font, matrices, playerText, nx, textY, Color.white.getRGB());
                topCardX += nameCardWidth + (fpsCardWidth > 0f ? gapX : 0f);
            }

            if (fpsCardWidth > 0f) {
                drawRoundedRect(matrices, topCardX, topY, fpsCardWidth, cardHeight, radius, cardColor);
                float fx = topCardX + paddingX;
                float fy = topY + (cardHeight - statIconSize) / 2.0f;
                RenderUtil.drawTexture(matrices, "images/logo/fps.png", fx, fy, statIconSize, statIconSize, 0, wmIconBase);
                font.drawLeftAligned(matrices, fpsText, fx + statIconSize + statIconGap, textY, Color.white.getRGB());
            }

            if (packSecondRowToTop) {
                if (coordsWidth > 0f) {
                    drawRoundedRect(matrices, topCardX, topY, coordsWidth, cardHeight, radius, cardColor);
                    font.drawLeftAligned(matrices, coordsText, topCardX + paddingX, textY, Color.white.getRGB());
                    topCardX += coordsWidth + (pingWidth > 0f ? gapX : 0f);
                }

                if (pingWidth > 0f) {
                    drawRoundedRect(matrices, topCardX, topY, pingWidth, cardHeight, radius, cardColor);
                    float px = topCardX + paddingX;
                    float py = topY + (cardHeight - statIconSize) / 2.0f;
                    RenderUtil.drawTexture(matrices, "images/logo/ping.png", px, py, statIconSize, statIconSize, 0, wmIconBase);
                    font.drawLeftAligned(matrices, pingText, px + statIconSize + statIconGap, textY, Color.white.getRGB());
                }
            }
        }

        if (row2Width > 0f) {
            float bottomCardX = x;
            if (starCardWidth > 0f) {
                drawRoundedRect(matrices, bottomCardX, bottomY, starCardWidth, cardHeight, radius, cardColor);
                float sx = bottomCardX + paddingX;
                float sy = bottomY + (cardHeight - starIconSize) / 2.0f;
                float centerX = sx + starIconSize * 0.5f;
                float globalStartX = x;
                float globalEndX = x + totalWidth;
                float totalW = Math.max(1.0f, globalEndX - globalStartX);
                float t = MathHelper.clamp((centerX - globalStartX) / totalW, 0.0f, 1.0f);
                float d = Math.abs(t - shimmerT);
                d = Math.min(d, 1.0f - d);
                float a = (float) Math.exp(-Math.pow(d / sigma, 2.0));

                int starBaseForHighlight = (clientTheme || mono) ? Color.white.getRGB() : wmAccent2;
                int starA = Math.min(255, (int) (alpha * 0.95f));
                int starHighlight = isDarkColor(starBaseForHighlight)
                        ? new Color(255, 255, 255, starA).getRGB()
                        : new Color(0, 0, 0, starA).getRGB();
                int starBase = (clientTheme || turquoisePalette) ? Color.white.getRGB() : wmAccent2;
                int starColor = ColorUtil.interpolateColor(starBase, starHighlight, MathHelper.clamp(a, 0.0f, 0.75f));
                RenderUtil.drawTexture(matrices, "images/logo/star.png", sx, sy, starIconSize, starIconSize, 0, starColor);
                bottomCardX += starCardWidth + (coordsWidth > 0f ? gapX : 0f);
            }

            if (coordsWidth > 0f) {
                drawRoundedRect(matrices, bottomCardX, bottomY, coordsWidth, cardHeight, radius, cardColor);
                font.drawLeftAligned(matrices, coordsText, bottomCardX + paddingX, bottomY + 3.5f, Color.white.getRGB());
                bottomCardX += coordsWidth + (pingWidth > 0f ? gapX : 0f);
            }

            if (pingWidth > 0f) {
                drawRoundedRect(matrices, bottomCardX, bottomY, pingWidth, cardHeight, radius, cardColor);
                float px = bottomCardX + paddingX;
                float py = bottomY + (cardHeight - statIconSize) / 2.0f;
                RenderUtil.drawTexture(matrices, "images/logo/ping.png", px, py, statIconSize, statIconSize, 0, wmIconBase);
                if (!isMono && !clientTheme && !turquoisePalette) {
                    Scissor.push();
                    Scissor.setFromComponentCoordinates(px, py, statIconSize * 0.5f, statIconSize);
                    RenderUtil.drawTexture(matrices, "images/logo/ping.png", px, py, statIconSize, statIconSize, 0, wmAccent2);
                    Scissor.pop();
                }
                font.drawLeftAligned(matrices, pingText, px + statIconSize + statIconGap, bottomY + 3.5f, Color.white.getRGB());
            }
        }

        watermarkDrag.setHeight(totalHeight);
        watermarkDrag.setWidth(totalWidth);
    }

    private void сoordinates(EventRender2D render2D) {
        float x = coordinateshudDrag.getX();
        float y = coordinateshudDrag.getY();

        var matrices = render2D.getDrawContext().getMatrices();
        var font = FontUtils.sfns_display_bold[15];

        String coords = String.format("%.0f %.0f %.0f", smoothCoordX, smoothCoordY, smoothCoordZ);
        String ticks = String.format("%.1f TPS", smoothTps);

        int alpha = Math.min(255, Math.round(customAlpha.get().intValue() * 0.7f));
        boolean plainHud = hudColor.is("Обычный");
        int cardColor = plainHud
                ? new Color(0, 0, 0, alpha).getRGB()
                : hudSurface(30, 26, 70, alpha, 0.22f);

        float paddingX = 10f;
        float height = 18f;
        float gap = 4f;

        float coordsWidth = paddingX * 2 + font.getWidth(coords);
        float ticksWidth = paddingX * 2 + font.getWidth(ticks);

        if (alpha <= 240) {
            if (blur.get()) {
                drawBlur(matrices, x, y, coordsWidth + gap + ticksWidth, height, new Vector4f(4, 4, 4, 4), 12, Color.white.getRGB());
            }
        }

        Vector4f radius = new Vector4f(3.5f, 3.5f, 3.5f, 3.5f);

        drawRoundedRect(matrices, x, y, coordsWidth, height, radius, cardColor);
        font.drawLeftAligned(matrices, coords, x + paddingX, y + 4.5f, Color.white.getRGB());

        float secondX = x + coordsWidth + gap;
        drawRoundedRect(matrices, secondX, y, ticksWidth, height, radius, cardColor);
        font.drawLeftAligned(matrices, ticks, secondX + paddingX, y + 4.5f, Color.white.getRGB());

        coordinateshudDrag.setWidth(coordsWidth + gap + ticksWidth);
        coordinateshudDrag.setHeight(height);
    }




    private float keybindsHeightDynamic = 0;
    private float keybindsSmoothWidth = -1f;
    private float keybindsSmoothHeight = -1f;
    private float keybindsSmoothMaxKeyWidth = -1f;
    private long keybindsLastTimeNs = 0L;
    private final java.util.LinkedHashMap<String, Float> hotkeysRowAnim = new java.util.LinkedHashMap<>();
    private final java.util.LinkedHashMap<String, String> hotkeysRowBind = new java.util.LinkedHashMap<>();

    private float keybindsComputeDtSeconds() {
        long now = System.nanoTime();
        if (keybindsLastTimeNs == 0L) {
            keybindsLastTimeNs = now;
            return 1f / 60f;
        }
        long d = now - keybindsLastTimeNs;
        keybindsLastTimeNs = now;
        double dt = Math.min(Math.max(d / 1_000_000_000.0, 0.0), 0.1);
        return (float) dt;
    }

    private float smoothTowards(float current, float target, float dt, float speedPerSec) {
        if (!Float.isFinite(dt) || dt <= 0f) return target;
        float k = 1f - (float) Math.exp(-speedPerSec * dt);
        return current + (target - current) * k;
    }

    private float easeOutCubic(float t) {
        if (t < 0f) t = 0f;
        if (t > 1f) t = 1f;
        return 1.0f - (float) Math.pow(1.0f - t, 3.0);
    }

    private void updateSmoothNumbers() {
        long now = System.currentTimeMillis();
        if (now - lastNumberUpdateMs < 80L) return;
        lastNumberUpdateMs = now;

        float targetFps = ClientManager.getFps();
        float targetPing = 0f;
        try { targetPing = Float.parseFloat(ClientManager.getPing()); } catch (Exception ignored) {}
        float targetTps = ClientManager.getTPS();
        double px = mc.player.getX();
        double py = mc.player.getY();
        double pz = mc.player.getZ();

        smoothFps = MathUtil.fast(smoothFps, targetFps, 10);
        smoothPing = MathUtil.fast(smoothPing, targetPing, 10);
        smoothTps = MathUtil.fast(smoothTps, targetTps, 10);
        smoothCoordX = MathUtil.fast(smoothCoordX, (float) px, 8);
        smoothCoordY = MathUtil.fast(smoothCoordY, (float) py, 8);
        smoothCoordZ = MathUtil.fast(smoothCoordZ, (float) pz, 8);
    }

    private void keybindHud(EventRender2D render2D) {
        float posX = keybindsDrag.getX();
        float posY = keybindsDrag.getY();
        int headerHeight = 16;
        int rowHeight = headerHeight - 4;
        int rowGap = 1;
        int index = 0;

        float dt = keybindsComputeDtSeconds();

        var matrices = render2D.getDrawContext().getMatrices();
        var font = FontUtils.sfns_display_bold[14];

        int alpha = Math.min(255, Math.round(customAlpha.get().intValue() * 0.7f));

        float minWidth = 90f;
        float minHeight = 30f;
        float maxWidth = minWidth;

        java.util.LinkedHashSet<String> currentRows = new java.util.LinkedHashSet<>();
        for (Function f : Manager.FUNCTION_MANAGER.getFunctions()) {
            if ("MiddleClickPearl".equalsIgnoreCase(f.name)) continue;
            if (f.bind != 0 && f.state) {
                String bindKey = getShortKey(ClientManager.getKey(f.bind));
                currentRows.add(f.name);
                hotkeysRowBind.put(f.name, bindKey);
                hotkeysRowAnim.putIfAbsent(f.name, 0f);

                float nameWidth = font.getWidth(f.name);
                float bindWidth = font.getWidth(bindKey);
                float totalWidth = 14f + nameWidth + 8f + bindWidth;
                if (totalWidth > maxWidth) maxWidth = totalWidth;
            }
        }

        // animate targets (present -> 1, missing -> 0)
        java.util.ArrayList<String> keysSnapshot = new java.util.ArrayList<>(hotkeysRowAnim.keySet());
        for (String key : keysSnapshot) {
            float target = currentRows.contains(key) ? 1f : 0f;
            float cur = hotkeysRowAnim.getOrDefault(key, 0f);
            float next = MathUtil.fast(cur, target, 15);
            if (next <= 0.01f && target == 0f) {
                hotkeysRowAnim.remove(key);
                hotkeysRowBind.remove(key);
            } else {
                hotkeysRowAnim.put(key, next);
            }
        }

        float listHeightTarget = 0f;
        int visibleCount = 0;
        for (float p : hotkeysRowAnim.values()) {
            if (p <= 0.01f) continue;
            listHeightTarget += p * (rowHeight + rowGap);
            visibleCount++;
        }
        if (visibleCount > 0) listHeightTarget -= rowGap;
        keybindsHeightDynamic = MathUtil.fast(keybindsHeightDynamic, listHeightTarget, 15);
        float totalHeightTarget = Math.max(minHeight, headerHeight + keybindsHeightDynamic + 5f);

        maxWidth = Math.max(minWidth, maxWidth);
        if (keybindsSmoothWidth < 0f) keybindsSmoothWidth = maxWidth;
        if (keybindsSmoothHeight < 0f) keybindsSmoothHeight = totalHeightTarget;
        keybindsSmoothWidth = smoothTowards(keybindsSmoothWidth, maxWidth, dt, 12f);
        keybindsSmoothHeight = smoothTowards(keybindsSmoothHeight, totalHeightTarget, dt, 12f);

        float animatedWidth = MathHelper.floor(keybindsSmoothWidth * 2.0f) / 2.0f;
        float totalHeight = MathHelper.floor(keybindsSmoothHeight * 2.0f) / 2.0f;

        animatedWidth = Math.max(minWidth, animatedWidth);
        totalHeight = Math.max(minHeight, totalHeight);

        // max width keys (for centering/separator if needed)
        float targetMaxKeyWidth = 0f;
        for (String name : currentRows) {
            String k = hotkeysRowBind.getOrDefault(name, "");
            targetMaxKeyWidth = Math.max(targetMaxKeyWidth, font.getWidth(k));
        }
        if (keybindsSmoothMaxKeyWidth < 0f) keybindsSmoothMaxKeyWidth = targetMaxKeyWidth;
        keybindsSmoothMaxKeyWidth = smoothTowards(keybindsSmoothMaxKeyWidth, targetMaxKeyWidth, dt, 12f);

        boolean hasHotkeys = !hotkeysRowAnim.isEmpty();
        float fullHeightHotkeys = headerHeight + (hasHotkeys ? keybindsHeightDynamic + rowGap : 0);

        if (alpha <= 240 && blur.get()) {
            // общий blur под блоком hotkeys
            drawBlur(matrices, posX, posY, animatedWidth, fullHeightHotkeys, new Vector4f(4, 4, 4, 4), 12, Color.white.getRGB());
        }

        boolean mono = hudColor.is("Обычный") || (isMonoTheme() && !hudColor.is("Тема HUD"));
        boolean plainHud = hudColor.is("Обычный");

        int headerColor = mono
                ? new Color(55, 55, 55, Math.min(255, (int) (alpha * 0.80f))).getRGB()
                : hudSurface(24, 20, 55, Math.min(255, (int) (alpha * 0.80f)), 0.3f);
        int headerSeparator = mono
                ? hudSeparator(120)
                : ColorUtil.applyAlpha(Color.BLACK.getRGB(), 0.2f);

        int headerColorTop = plainHud
                ? new Color(0, 0, 0, Math.min(255, (int) (alpha * 0.85f))).getRGB()
                : headerColor;
        int headerSeparatorTop = plainHud
                ? ColorUtil.applyAlpha(Color.WHITE.getRGB(), 0.12f)
                : headerSeparator;

        // шапка поверх
        drawRoundedRect(matrices, posX, posY, animatedWidth, headerHeight, new Vector4f(4.0f, 4.0f, 4.0f, 4.0f), headerColorTop);
        RenderUtil.drawRoundedRect(matrices, posX, posY + headerHeight - 1f, animatedWidth, 1f, 0f, headerSeparatorTop);

        // icon + title group left (vertically centered)
        float iconSize = 11f;
        float iconY = posY + (headerHeight - iconSize) / 2f + 1f;
        float headerTextY = posY + (headerHeight - FontUtils.sfns_display_bold[14].getHeight()) / 2f;

        String headerLabel = "HotKeys";
        float iconX = posX + 6f;
        float headerTextX = iconX + iconSize + 6f;
        
        var tex = mc.getTextureManager().getTexture(HUD_KEYBINDS_ICON);
        if (!hudKeybindsIconFiltered && tex instanceof net.minecraft.client.texture.AbstractTexture abstractTexture) {
            hudKeybindsIconFiltered = true;
            abstractTexture.setFilter(true, true);
        }
        RenderUtil.drawTexture(matrices, HUD_KEYBINDS_ICON, iconX, iconY, iconSize, iconSize, 0f, Color.WHITE.getRGB());

        float sepX = iconX + iconSize + 2.8f;
        float sepY = posY + (headerHeight - 8f) / 2f;
        RenderUtil.drawRoundedRect(matrices, sepX, sepY, 0.5f, 8f, 0f,
                ColorUtil.applyAlpha(Color.white.getRGB(), plainHud ? 0.10f : 0.08f));

        // text and close glyph
        FontUtils.sfns_display_bold[14].drawLeftAligned(matrices, headerLabel, headerTextX, headerTextY, Color.white.getRGB());
        FontUtils.sfns_display_bold[14].drawRightAligned(matrices, "x", posX + animatedWidth - 8, headerTextY,
                ColorUtil.applyAlpha(Color.white.getRGB(), mono ? 0.45f : 0.8f));

        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, animatedWidth, headerHeight + keybindsHeightDynamic + rowGap);

        float listStartY = posY + headerHeight + rowGap;
        float yOffset = listStartY;
        // render active rows first, then fading out rows
        java.util.ArrayList<String> renderOrder = new java.util.ArrayList<>();
        renderOrder.addAll(currentRows);
        for (String k : hotkeysRowAnim.keySet()) {
            if (!currentRows.contains(k)) renderOrder.add(k);
        }

        for (String name : renderOrder) {
            float itemProgress = hotkeysRowAnim.getOrDefault(name, 0f);
            if (itemProgress <= 0.01f) continue;
            String bindKey = hotkeysRowBind.getOrDefault(name, "");
            float bindWidth = font.getWidth(bindKey);

            float slidePC = easeOutCubic(itemProgress);

            float pillW = animatedWidth;
            float pillH = rowHeight + 1.0f;

            float rowX = posX;
            float pillY = yOffset - (1f - slidePC) * (rowHeight + rowGap) - 0.5f;

            int rowAlpha = Math.min(255, (int) (alpha * 0.75f * slidePC));
            int rowColor = plainHud
                    ? new Color(0, 0, 0, rowAlpha).getRGB()
                    : (mono
                        ? new Color(45, 45, 45, rowAlpha).getRGB()
                        : hudSurface(24, 20, 55, rowAlpha, 0.28f));
            drawRoundedRect(matrices, rowX, pillY, pillW, pillH, 4.0f, rowColor);

            float nameX = rowX + 8f;
            float bindX = rowX + pillW - bindWidth - 8f;
            float textY = pillY + (pillH - font.getHeight()) / 2f;

            int nameColor = ColorUtil.applyAlpha(Color.white.getRGB(), slidePC);
            int bindColor = ColorUtil.applyAlpha(new Color(245, 245, 245).getRGB(), slidePC);
            font.drawLeftAligned(matrices, name, nameX, textY, nameColor);
            font.drawLeftAligned(matrices, bindKey, bindX, textY, bindColor);

            yOffset += (rowHeight + rowGap) * itemProgress;
            index++;
        }

        Scissor.unset();
        Scissor.pop();

        activeModules = index;
        keybindsDrag.setWidth(animatedWidth);
        keybindsDrag.setHeight(totalHeight);
    }


    private String getShortKey(String key) {
        if (key == null) return "";
        String bindText = key.toUpperCase();
        return bindText.length() > 6 ? bindText.substring(0, 6) + "…" : bindText;
    }

    private void drawBindIcon(MatrixStack matrices, float x, float y, boolean mono, boolean attackAura) {
        float size = 10f;
        if (!attackAura) {
            int bg = ColorUtil.applyAlpha(Color.BLACK.getRGB(), mono ? 0.28f : 0.35f);
            RenderUtil.drawRoundedRect(matrices, x, y, size, size, 2.5f, bg);
        }
        if (attackAura) {
            int tint = mono ? new Color(235, 235, 235).getRGB() : Color.white.getRGB();
            RenderUtil.drawTexture(matrices, "images/hud/swords.png", x + 2f, y + 2f, 6, 6, 0, tint);
            return;
        }
        if (mono) {
            int iconColor = new Color(235, 235, 235).getRGB();
            float inset = 2.2f;
            float thickness = 1.1f;
            RenderUtil.drawRoundedRect(matrices, x + inset, y + inset, thickness, size - inset * 2, 0.5f, iconColor);
            RenderUtil.drawRoundedRect(matrices, x + size - inset - thickness, y + inset, thickness, size - inset * 2, 0.5f, iconColor);
            RenderUtil.drawRoundedRect(matrices, x + inset, y + size / 2f - thickness / 2f, size - inset * 2, thickness, 0.5f, iconColor);
        } else {
            RenderUtil.drawTexture(matrices, "images/hud/crosshair.png", x + 2f, y + 2f, 6, 6, 0, Color.white.getRGB());
        }
    }

    private void drawKeyboardIcon(MatrixStack matrices, float x, float y, float size, boolean mono) {
        // SVG 1-в-1 (viewBox 24x24)
        float s = size / 24f;
        int c = Color.WHITE.getRGB();

        // Корпус: <rect x="2" y="5" width="20" height="14" rx="3" stroke-width="2"/>
        float bodyX = x + 2f * s;
        float bodyY = y + 5f * s;
        float bodyW = 20f * s;
        float bodyH = 14f * s;
        float bodyR = 3f * s;
        float strokePx = 2f * s;
        float denom = Math.max(1f, Math.min(bodyW, bodyH));
        float stroke = MathHelper.clamp(strokePx / denom, 0.02f, 0.25f);
        RenderUtil.drawRoundedBorder(matrices, bodyX, bodyY, bodyW, bodyH, bodyR, stroke, c);

        float inset = Math.max(0.7f, strokePx * 0.9f);
        float keyOffsetX = size <= 16f ? 0.35f * s : 0f;
        float keyOffsetY = size <= 16f ? 0.55f * s : 0f;

        // Клавиши: width/height=2, rx=0.5
        float keyScale = size <= 16f ? 1.85f : 1.0f;
        float keyW = Math.max(2.2f, (2f * s) * keyScale);
        float keyH = Math.max(2.2f, (2f * s) * keyScale);
        float keyR = Math.min(keyW * 0.5f, 0.6f * s);
        float[] keyXs = new float[]{5f, 8f, 11f, 14f, 17f};
        float[] keyYs = new float[]{8f, 11f};
        for (float kyBase : keyYs) {
            for (float kxBase : keyXs) {
                float cx = (kxBase + 1f) * s + keyOffsetX;
                float cy = (kyBase + 1f) * s + keyOffsetY;
                float px = MathHelper.clamp(x + cx - keyW / 2f, bodyX + inset, bodyX + bodyW - inset - keyW);
                float py = MathHelper.clamp(y + cy - keyH / 2f, bodyY + inset, bodyY + bodyH - inset - keyH);
                RenderUtil.drawRoundedRect(matrices, px, py, keyW, keyH, keyR, c);
            }
        }

        // Пробел: <rect x="6.5" y="14.5" width="11" height="2.2" rx="1.1" fill="white"/>
        float spaceW = Math.max(7.5f, 11f * s);
        float spaceH = Math.max(2.0f, (2.2f * s) * (size <= 16f ? 1.25f : 1.0f));
        float spaceR = Math.min(spaceH * 0.5f, 1.1f * s);
        float spaceCx = (6.5f + 11f / 2f) * s + keyOffsetX;
        float spaceCy = (14.5f + 2.2f / 2f) * s + keyOffsetY;
        float spaceX = MathHelper.clamp(x + spaceCx - spaceW / 2f, bodyX + inset, bodyX + bodyW - inset - spaceW);
        float spaceY = MathHelper.clamp(y + spaceCy - spaceH / 2f, bodyY + inset, bodyY + bodyH - inset - spaceH);
        RenderUtil.drawRoundedRect(matrices, spaceX, spaceY, spaceW, spaceH, spaceR, c);
    }


    public LivingEntity getTarget(LivingEntity nullTarget) {
        LivingEntity target = nullTarget;

        if (Manager.FUNCTION_MANAGER.attackAura.target instanceof LivingEntity) {
            target = (LivingEntity) Manager.FUNCTION_MANAGER.attackAura.target;
            tHudAnimation.setDirection(Direction.AxisDirection.POSITIVE);
        }
        else if (visibleCrosshair.get() && mc.crosshairTarget instanceof EntityHitResult) {
            Entity aimed = ((EntityHitResult) mc.crosshairTarget).getEntity();
            if (aimed instanceof LivingEntity) {
                target = (LivingEntity) aimed;
                tHudAnimation.setDirection(Direction.AxisDirection.POSITIVE);
            } else {
                tHudAnimation.setDirection(Direction.AxisDirection.NEGATIVE);
            }
        }
        else if (mc.currentScreen instanceof ChatScreen) {
            target = mc.player;
            tHudAnimation.setDirection(Direction.AxisDirection.POSITIVE);
        }
        else {
            tHudAnimation.setDirection(Direction.AxisDirection.NEGATIVE);
        }

        return target;
    }

    private void drawTextWithShadow(RenderFonts font, MatrixStack matrices, String text, float x, float y, int color) {
        font.drawLeftAligned(matrices, text, x + 0.5f, y + 0.5f, ColorUtil.withAlpha(Color.black.getRGB(), 90));
        font.drawLeftAligned(matrices, text, x, y, color);
    }

    private String repairString(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (c >= 65281 && c <= 65374) {
                sb.append((char) (c - 65248));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private void mouseSensHud(EventRender2D render2D) {
        float x = mouseSensDrag.getX();
        float y = mouseSensDrag.getY();

        var matrices = render2D.getDrawContext().getMatrices();
        var font = FontUtils.sfns_display_bold[14];

        double sens = mc.options.getMouseSensitivity().getValue();
        double sensMid = sens * 0.6 + 0.2;
        double multiplier = sensMid * sensMid * sensMid * 8.0;
        int inGamePercent = (int) Math.round(sens * 200);
        int dpi = (int) Math.round(mouseSensDpi.get().doubleValue());
        double edpi = dpi * multiplier;
        double gcdVal = multiplier * 0.15;

        var lines = new java.util.ArrayList<String>();
        lines.add("§7Сенсы:");
        lines.add("§aВ игре: §f" + inGamePercent + "%");
        lines.add("§aGCD: §f" + String.format(java.util.Locale.ROOT, "%.4f", gcdVal));
        lines.add("§aeDPI: §f" + (int) Math.round(edpi));
        if (edpi < 50) lines.add("§eНизкая, флекс");
        else if (edpi < 100) lines.add("§aСтабильная ПвП");
        else if (edpi < 160) lines.add("§aСредне-высокая");
        else if (edpi < 250) lines.add("§eВысокая");
        else lines.add("§cОчень высокая");

        float padX = 8f;
        float padY = 6f;
        float lineGap = 1.5f;
        float lineH = font.getHeight() + lineGap;

        float maxW = 0;
        for (String line : lines) maxW = Math.max(maxW, font.getWidth(line));
        float totalW = padX * 2 + maxW;
        float topY = y;
        float totalH = lines.size() * lineH + padY * 2 - lineGap;

        int alpha = Math.min(255, Math.round(customAlpha.get().floatValue() * 0.7f));
        boolean plainHud = hudColor.is("Обычный");

        int baseBg = plainHud
                ? new Color(0, 0, 0, alpha).getRGB()
                : hudSurface(18, 16, 30, alpha, 0.12f);

        if (alpha <= 240 && blur.get()) {
            drawBlur(matrices, x, topY, totalW, totalH, new Vector4f(7f, 7f, 7f, 7f), 12, Color.white.getRGB());
        }

        drawRoundedRect(matrices, x, topY, totalW, totalH, 7f, baseBg);

        float lx = x + padX;
        float ly = topY + padY;
        for (String line : lines) {
            drawTextWithShadow(font, matrices, line, lx, ly, Color.white.getRGB());
            ly += lineH;
        }

        mouseSensDrag.setWidth(totalW);
        mouseSensDrag.setHeight(totalH);
    }

    @Override
    public void onDisable() {
        staffPlayers.clear();
        addedPlayers.clear();
    }
    public class StaffPlayer {
        @Getter
        private final String name;
        @Getter
        private final Text prefix;
        @Getter
        private Status status;
        @Getter
        private final long joinTime;
        @Getter
        private GameMode gameMode;
        @Getter
        private boolean isOnPlayerList;
        @Getter
        private final java.util.UUID uuid;

        public StaffPlayer(String name, Text prefix, java.util.UUID uuid) {
            this.name = name;
            this.prefix = prefix;
            this.uuid = uuid;
            this.joinTime = System.currentTimeMillis();
            updateStatus();
        }

        public void updateStatus() {
            this.status = Status.NONE;
            this.isOnPlayerList = false;
            this.gameMode = null;
        }

        public enum Status {
            NONE("§2[ON]"),
            NEAR("§6[N]"),
            SPEC("§e[GM3]"),
            VANISHED("§c[V]");

            @Getter
            final String string;

            Status(String string) {
                this.string = string;
            }
        }
    }

    private String processName(String original) {
        if (original.length() > 12 || original.matches(".*\\d.*")) {
            return original.substring(0, Math.min(9, original.length())) + "...";
        }
        return original;
    }

    private int getStatusColor(StaffPlayer.Status status) {
        switch(status) {
            case NEAR: return Color.ORANGE.getRGB();
            case SPEC: return Color.YELLOW.getRGB();
            case VANISHED: return Color.RED.getRGB();
            default: return Color.GREEN.getRGB();
        }
    }
    private String formatCooldownTime(float seconds) {
        int totalSeconds = (int) Math.floor(seconds);
        int minutes = totalSeconds / 60;
        int secs = totalSeconds % 60;

        if (minutes > 0) {
            if (secs > 0) {
                return String.format("%dм %02dс", minutes, secs);
            } else {
                return String.format("%dм", minutes);
            }
        } else {
            return String.format("%dс", secs);
        }
    }

}