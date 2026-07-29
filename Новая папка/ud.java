import hud.ListEntry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

class ud extends ListEntry<RegistryEntry<StatusEffect>> {
   final String TK;
   String BP;
   final RegistryEntry<StatusEffect> zk;
   final StatusEffectInstance ug;

   ud(String s, String s1, RegistryEntry<StatusEffect> registryentry, StatusEffectInstance statuseffectinstance, String s2) {
      super(registryentry, s2);
      this.TK = s;
      this.BP = s1;
      this.zk = registryentry;
      this.ug = statuseffectinstance;
   }

   public void a(String s) {
      this.BP = s;
   }

   @Override
   public void a() {
   }
}
