import com.mojang.logging.LogUtils;
import core.ClientMain;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Stream;
import module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.slf4j.Logger;
import shader.ShaderResource;
import util.UnsafeFieldAccessor;

public class we extends ReloadableResourceManagerImpl {
   private static final Logger LOGGER = LogUtils.getLogger();
   private LifecycledResourceManager activeManager;
   private final List<ResourceReloader> reloaders;
   private final ResourceType type;
   private final HashMap<String, ShaderResource> resourceCache = new HashMap<String, ShaderResource>();
   MinecraftClient mc = MinecraftClient.getInstance();

   public we() {
      super(ResourceType.CLIENT_RESOURCES);
      HudModule hudmodule = ClientMain.getInstance().getModuleManager().<HudModule>getModule(HudModule.class);
      this.type = ResourceType.CLIENT_RESOURCES;
      this.activeManager = new LifecycledResourceManagerImpl(this.type, List.of());
      UnsafeFieldAccessor unsafefieldaccessor = new UnsafeFieldAccessor(this.mc.getResourceManager(), ReloadableResourceManagerImpl.class, 2).removeModifier();
      this.reloaders = (List<ResourceReloader>)unsafefieldaccessor.getValue();
      UnsafeFieldAccessor unsafefieldaccessor1 = new UnsafeFieldAccessor(this.mc, MinecraftClient.class, ReloadableResourceManagerImpl.class).removeModifier();
      new ltw(this);
      unsafefieldaccessor1.setValue(this);
      this.mc.reloadResources();
   }

   public void close() {
      this.activeManager.close();
   }

   public void registerReloader(ResourceReloader resourcereloader) {
      this.reloaders.add(resourcereloader);
   }

   public ResourceReload reload(Executor executor, Executor executor1, CompletableFuture<Unit> completablefuture, List<ResourcePack> list) {
      this.activeManager.close();
      this.activeManager = new LifecycledResourceManagerImpl(this.type, list);
      return SimpleResourceReload.start(this, this.reloaders, executor, executor1, completablefuture, LOGGER.isDebugEnabled());
   }

   public Optional<Resource> getResource(Identifier identifier) {
      return this.resourceCache.containsKey(identifier.getPath())
         ? Optional.<Resource>of(this.resourceCache.get(identifier.getPath()).toResource())
         : this.activeManager.getResource(identifier);
   }

   public Set<String> getAllNamespaces() {
      return this.activeManager.getAllNamespaces();
   }

   public List<Resource> getAllResources(Identifier identifier) {
      return this.activeManager.getAllResources(identifier);
   }

   public Map<Identifier, Resource> findResources(String s, Predicate<Identifier> predicate) {
      LinkedHashMap linkedhashmap = new LinkedHashMap(this.activeManager.findResources(s, predicate));
      this.resourceCache.keySet().forEach(s2 -> {
         if (s2.startsWith(s)) {
            linkedhashmap.put(Identifier.ofVanilla(s2), this.resourceCache.get(s2).toResource());
         }
      });
      return linkedhashmap;
   }

   public Map<Identifier, List<Resource>> findAllResources(String s, Predicate<Identifier> predicate) {
      return this.activeManager.findAllResources(s, predicate);
   }

   public Stream<ResourcePack> streamResourcePacks() {
      return this.activeManager.streamResourcePacks();
   }

   public void addResource(ShaderResource shaderresource) {
      this.resourceCache.put(shaderresource.getLocation().getPath(), shaderresource);
   }
}
