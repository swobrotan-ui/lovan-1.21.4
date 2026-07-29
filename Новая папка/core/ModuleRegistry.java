package core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import module.ABRaidmineModule;
import module.AimAssistModule;
import module.AirStuckModule;
import module.AnchorTapModule;
import module.AntiBotModule;
import module.AspectRatioModule;
import module.AuctionHelperModule;
import module.AuraModule;
import module.AutoAcceptModule;
import module.AutoCartModule;
import module.AutoHealthPotModule;
import module.AutoLeaveModule;
import module.AutoRespawnModule;
import module.AutoSwapModule;
import module.AutoToolModule;
import module.AutoTotemModule;
import module.BlinkModule;
import module.BlockESPModule;
import module.BlockOutlineModule;
import module.BoatFlyModule;
import module.CamTweaksModule;
import module.CapeModule;
import module.ChatTweaksModule;
import module.ChestStealerModule;
import module.CleanHudModule;
import module.ClickGuiModule;
import module.CombatAgentAIModule;
import module.CrystalOptModule;
import module.CrystalTapModule;
import module.CustomFogModule;
import module.CustomThemeModule;
import module.CustomTimeModule;
import module.ElytraSwapModule;
import module.EmergencyStopModule;
import module.FakeLagModule;
import module.FakePlayerModule;
import module.FastUseModule;
import module.FreeCamModule;
import module.FullBrightModule;
import module.HandTweaksModule;
import module.HitParticlesModule;
import module.HitboxModule;
import module.HitboxTweaksModule;
import module.HudModule;
import module.InvMoveModule;
import module.ItemESPModule;
import module.ItemRadiusModule;
import module.ItemScrollerModule;
import module.JumpParticlesModule;
import module.JumpResetModule;
import module.KeyPearlModule;
import module.LagRangeModule;
import module.MCFModule;
import module.MaceSwapModule;
import module.Module;
import module.NameTagsModule;
import module.NoArmorStandModule;
import module.NoJumpDelayModule;
import module.NoOverlayModule;
import module.NoPushModule;
import module.NoServerRotateModule;
import module.NoSlowModule;
import module.NoWebModule;
import module.PanicModule;
import module.ParrotModule;
import module.ParticlesModule;
import module.PearlParticlesModule;
import module.PearlTracerModule;
import module.PlayerChamsModule;
import module.PlayerESPModule;
import module.PlayerOutlinesModule;
import module.PlayerScalerModule;
import module.ProtestModule;
import module.ShieldBreakerModule;
import module.ShiftTapModule;
import module.ShowInvisibleModule;
import module.SoundsModule;
import module.SpeedModule;
import module.SpiderModule;
import module.SprintModule;
import module.SwingAnimationModule;
import module.TabTweaksModule;
import module.TargetESPModule;
import module.TracersModule;
import module.TrailsModule;
import module.TrajectoriesModule;
import module.TranslatorModule;
import module.TriggerBotModule;
import module.ViewModelModule;
import module.WaypointsModule;
import module.WindChargeModule;
import module.WorldTintModule;
import module.ZoomModule;

public class ModuleRegistry {
   private static final Map<Integer, Supplier<Module>> registry = new LinkedHashMap<Integer, Supplier<Module>>();

   private static void register(int i, Supplier<Module> supplier) {
      registry.put(i, supplier);
   }

   public static Module createModule(int i) {
      Supplier supplier = registry.get(i);
      return supplier != null ? (Module)supplier.get() : null;
   }

   public static List<Module> createAllModules() {
      ArrayList arraylist = new ArrayList();

      for (Supplier supplier : registry.values()) {
         try {
            arraylist.add((Module)supplier.get());
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

      return arraylist;
   }

   public static Set<Integer> parseModuleIds(String s) {
      HashSet hashset = new HashSet();
      if (s != null && !s.isEmpty()) {
         String[] astring = s.split(",");

         for (String s1 : astring) {
            try {
               int i = Integer.parseInt(s1.trim());
               if (registry.containsKey(i)) {
                  hashset.add(i);
               }
            } catch (NumberFormatException numberformatexception) {
            }
         }

         return hashset;
      } else {
         return hashset;
      }
   }

   public static List<Module> createModules(Set<Integer> set) {
      ArrayList arraylist = new ArrayList();

      for (Integer integer : set) {
         Module module = createModule(integer);
         if (module != null) {
            arraylist.add(module);
         }
      }

      return arraylist;
   }

   static {
      register(1, HudModule::new);
      register(2, SoundsModule::new);
      register(3, MCFModule::new);
      register(4, PanicModule::new);
      register(5, CleanHudModule::new);
      register(69, CustomThemeModule::new);
      register(6, TranslatorModule::new);
      register(7, ClickGuiModule::new);
      register(10, AuraModule::new);
      register(11, AimAssistModule::new);
      register(12, TriggerBotModule::new);
      register(14, HitboxModule::new);
      register(13, AnchorTapModule::new);
      register(15, CrystalTapModule::new);
      register(16, ShieldBreakerModule::new);
      register(17, AutoTotemModule::new);
      register(18, MaceSwapModule::new);
      register(19, AntiBotModule::new);
      register(100, AutoCartModule::new);
      register(21, InvMoveModule::new);
      register(22, ElytraSwapModule::new);
      register(23, EmergencyStopModule::new);
      register(24, KeyPearlModule::new);
      register(25, FreeCamModule::new);
      register(26, NoJumpDelayModule::new);
      register(27, JumpResetModule::new);
      register(28, SprintModule::new);
      register(29, WindChargeModule::new);
      register(30, AirStuckModule::new);
      register(31, NoWebModule::new);
      register(32, SpiderModule::new);
      register(33, SpeedModule::new);
      register(34, BoatFlyModule::new);
      register(35, FakeLagModule::new);
      register(36, LagRangeModule::new);
      register(37, BlinkModule::new);
      register(38, NoSlowModule::new);
      register(85, ShiftTapModule::new);
      register(99, AuctionHelperModule::new);
      register(40, NoPushModule::new);
      register(41, FastUseModule::new);
      register(42, AutoHealthPotModule::new);
      register(43, CrystalOptModule::new);
      register(44, AutoRespawnModule::new);
      register(45, NoArmorStandModule::new);
      register(46, AutoToolModule::new);
      register(47, AutoSwapModule::new);
      register(48, ABRaidmineModule::new);
      register(49, AutoLeaveModule::new);
      register(83, ItemScrollerModule::new);
      register(95, AutoAcceptModule::new);
      register(97, NoServerRotateModule::new);
      register(98, ChestStealerModule::new);
      register(50, NoOverlayModule::new);
      register(51, WorldTintModule::new);
      register(52, CustomTimeModule::new);
      register(53, CustomFogModule::new);
      register(54, TargetESPModule::new);
      register(55, CamTweaksModule::new);
      register(56, HitboxTweaksModule::new);
      register(57, HandTweaksModule::new);
      register(58, ChatTweaksModule::new);
      register(59, TabTweaksModule::new);
      register(60, ViewModelModule::new);
      register(61, SwingAnimationModule::new);
      register(62, AspectRatioModule::new);
      register(63, CapeModule::new);
      register(64, ProtestModule::new);
      register(65, FakePlayerModule::new);
      register(66, ParrotModule::new);
      register(67, FullBrightModule::new);
      register(68, ZoomModule::new);
      register(94, BlockOutlineModule::new);
      register(70, NameTagsModule::new);
      register(71, PlayerESPModule::new);
      register(72, PlayerChamsModule::new);
      register(73, TracersModule::new);
      register(74, ItemESPModule::new);
      register(75, BlockESPModule::new);
      register(76, PearlTracerModule::new);
      register(77, ShowInvisibleModule::new);
      register(78, WaypointsModule::new);
      register(79, PlayerScalerModule::new);
      register(80, PlayerOutlinesModule::new);
      register(81, TrajectoriesModule::new);
      register(82, ItemRadiusModule::new);
      register(90, ParticlesModule::new);
      register(91, JumpParticlesModule::new);
      register(92, HitParticlesModule::new);
      register(93, PearlParticlesModule::new);
      register(96, TrailsModule::new);
      register(101, CombatAgentAIModule::new);
   }
}
