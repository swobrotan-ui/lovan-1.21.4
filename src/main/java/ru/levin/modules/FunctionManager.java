package ru.levin.modules;

import ru.levin.modules.combat.*;
import ru.levin.modules.misc.*;
import ru.levin.modules.misc.ClientSounds;
import ru.levin.modules.misc.NoCommands;
import ru.levin.modules.misc.Optimizer;
import ru.levin.modules.movement.*;
import ru.levin.modules.player.*;
import ru.levin.modules.player.AutoJoin;
import ru.levin.modules.render.*;
import ru.levin.modules.impl.util.Scaffold;
import ru.levin.x2demo.X2Module;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FunctionManager {

    public static final List<Function> functions = new CopyOnWriteArrayList<>();
    public final ClickGUI clickGUI;
    public final Optimizer optimizer;
    public final ClientSounds clientSounds;
    public final ElytraTarget elytraTarget;
    public final SuperFirework superFirework;
    public final AttackAura attackAura;
    public final CrystalAura crystalAura;
    public final NoFriendDamage noFriendDamage;
    public final HitBoxSnap xbox;
    public final NoMineAnimation noMineAnimation;
    public final NoCommands noCommands;
    public final SwingAnimations swingAnimations;
    public final ViewModel viewModel;
    public final NoPush noPush;
    public final FreeCamera freeCamera;
    public final HUD hud;
    public final NoRender noRender;
    public final Invnew invnew;
    public final NameProtect nameProtect;
    public final NoInteract noInteract;
    public final ItemScroller itemScroller;
    public final NoSlow noSlow;
    public final LittleSnickers littleSnickers;
    public final UnHook unHook;
    public final FullBright fullBright;
    public final ItemPhysic itemPhysic;
    public final AutoPotion autoPotion;
    public final GuiWalk guiWalk;
    public final ExtraTab extraTab;
    public final FreeLook freeLook;
    public final AntiBot antiBot;
    public final AutoExplosion autoExplosion;
    public final Arrows arrows;
    public final CustomCoolDown customCoolDown;
    public final Blink blink;
    public final NameTags nameTags;
    public final BlockESP blockESP;
    public final ChestStealer chestStealer;
    public final AutoJoin autoJoin;
    public final Dupe dupe;
    public final AspectRatio aspectRatio;
    public final World customWorld;
    public final NoRayTrace noRayTrace;
    public final IRC irc;
    public final FTHelper ftHelper;
    public final HWHelper hwHelper;
    public final ClickAction clickAction;
    public final Phase phase;
    public final TargetStrafe targetStrafe;
    public final Globals globals;
    public final SensPick sensPick;
    public final AutoSprint autoSprint;
    public final Speed speed;
    public final CrossHair crossHair;
    public final SelfTrap selfTrap;
    public final AutoTotem autoTotem;
    public final BlockHighLight blockHighLight;
    public final SeeInvisible seeInvisible;
    public final HitColor hitColor;
    //public final KTLeave ktLeave;
    //public final MaceExploit maceExploit;

    public FunctionManager() {
        //Combat
        functions.add(new Criticals());
        functions.add(new TriggerBot());
        functions.add(targetStrafe = new TargetStrafe());
        functions.add(noFriendDamage = new NoFriendDamage());
        functions.add(autoExplosion = new AutoExplosion());
        functions.add(new AttackExtend());
        functions.add(attackAura = new AttackAura());
        functions.add(crystalAura = new CrystalAura());
        functions.add(selfTrap = new SelfTrap());
        functions.add(autoPotion = new AutoPotion());
        functions.add(autoTotem = new AutoTotem());
        functions.add(new AutoLeave());
        functions.add(new SuperBow());
        functions.add(xbox = new HitBoxSnap());
        functions.add(antiBot = new AntiBot());
        functions.add(new Velocity());
        functions.add(new AimAssist());
        functions.add(new AutoArmor());
        functions.add(new AutoGapple());

        //Misc
        functions.add(unHook = new UnHook());
        functions.add(optimizer = new Optimizer());
        functions.add(clientSounds = new ClientSounds());
        functions.add(new Theme());
        functions.add(new DeathCoords());
        functions.add(new ServerRPSpoff());
        functions.add(new Xray());
        functions.add(new ElytraHelper());
        functions.add(ftHelper = new FTHelper());
        functions.add(hwHelper = new HWHelper());
        functions.add(new RWHelper());
        functions.add(new AutoDuel());
        functions.add(new AutoDuelBot());
        functions.add(globals = new Globals());
        functions.add(new Scaffold());
        functions.add(irc = new IRC());
        functions.add(nameProtect = new NameProtect());
        functions.add(noCommands = new NoCommands());
        functions.add(new AutoBuy());
        functions.add(sensPick = new SensPick());
        functions.add(new X2Module());

        //Movement
        functions.add(blink = new Blink());
        functions.add(phase = new Phase());
        functions.add(autoSprint = new AutoSprint());
        functions.add(new HighJump());
        functions.add(new Flight());
        functions.add(elytraTarget = new ElytraTarget());
        functions.add(new ElytraRecast());
        functions.add(new ElytraMotion());
        functions.add(superFirework = new SuperFirework());
        functions.add(freeLook = new FreeLook());
        functions.add(speed = new Speed());
        functions.add(new Strafe());
        functions.add(new Spider());
        functions.add(new AirStuck());
        functions.add(noSlow = new NoSlow());
        functions.add(new NoInteract());
        functions.add(new NoMineAnimation());
        functions.add(noPush = new NoPush());
        functions.add(new Jesus());
        functions.add(new WaterSpeed());

        //Player
        functions.add(guiWalk = new GuiWalk());
        functions.add(new NoDelay());
        functions.add(new AutoTool());
        functions.add(new AutoRespawn());
        functions.add(new AutoEZ());
        functions.add(clickAction = new ClickAction());
        functions.add(itemScroller = new ItemScroller());
        functions.add(new ItemFixSwap());
        functions.add(new PerfectTime());
        functions.add(noRayTrace = new NoRayTrace());
        functions.add(noMineAnimation = new NoMineAnimation());
        functions.add(new AutoRespawn());
        functions.add(new AutoTool());
        functions.add(autoJoin = new AutoJoin());
        functions.add(freeCamera = new FreeCamera());
        functions.add(customCoolDown = new CustomCoolDown());
        functions.add(new MiddleClickFriend());
        functions.add(new MiddleClickPearl());
        functions.add(new TargetPearl());
        functions.add(noInteract = new NoInteract());
        functions.add(chestStealer = new ChestStealer());
        functions.add(new EnderChestExploit());
        functions.add(new InvseeExploit());
        functions.add(new RegionExploit());
        functions.add(new ItemSwap());
        functions.add(new AutoWardenLoot());
        functions.add(dupe = new Dupe());
        //     functions.add(maceExploit = new MaceExploit());
        //     functions.add(new GodMode());
        //Render
        functions.add(clickGUI = new ClickGUI());
        clickGUI.interfaceStyle.set("Клиентский");
        clickGUI.guiType.set("Категории");

        functions.add(hud = new HUD());
        functions.add(swingAnimations = new SwingAnimations());
        functions.add(viewModel = new ViewModel());
        functions.add(aspectRatio = new AspectRatio());
        functions.add(crossHair = new CrossHair());
        functions.add(fullBright = new FullBright());
        functions.add(seeInvisible = new SeeInvisible());
        functions.add(hitColor = new HitColor());
        //     functions.add(new ShulkerPreview());
        functions.add(customWorld = new World());
        functions.add(noRender = new NoRender());
        functions.add(invnew = new Invnew());
        functions.add(blockESP = new BlockESP());
        functions.add(itemPhysic = new ItemPhysic());
        functions.add(extraTab = new ExtraTab());
        functions.add(arrows = new Arrows());
        functions.add(new ESP());
        functions.add(nameTags = new NameTags());
        functions.add(new Prediction());
        functions.add(blockHighLight = new BlockHighLight());
        functions.add(new AutoAccept());
        functions.add(new JumpCircles());
        functions.add(new Breadcrumbs());
        functions.add(new Trails());
        functions.add(new Particles());
        functions.add(new Chinahat());
        functions.add(new FakePlayer());
        functions.add(new TargetESP());
        functions.add(new TPLoot());
        functions.add(new MyDick());
        functions.add(new BaseFinder());
        //     functions.add(ktLeave = new KTLeave());
        functions.add(littleSnickers = new LittleSnickers());
    }



    public List<Function> getFunctions() {
        return functions;
    }

    public List<Function> getFunctions(Type category) {
        List<Function> functions = new ArrayList<>();
        for (Function function : getFunctions()) {
            if (function.getCategory() == category) {
                functions.add(function);
            }

        }
        return functions;
    }

    public static Function get(String name) {
        for (Function function : functions) {
            if (function != null && function.name.equalsIgnoreCase(name)) {
                return function;
            }
        }
        return null;
    }
}
