package net.saga.legendaryspellbook.registries;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.saga.legendaryspellbook.LegendarySpellbook;
import net.saga.legendaryspellbook.Spells.evocation.LegendaryPaladinBlessingSpell;
import net.saga.legendaryspellbook.Spells.evocation.SoulEndSpell;
import net.saga.legendaryspellbook.Spells.evocation.SummonHauntedArmySpell;
import net.saga.legendaryspellbook.Spells.lightning.ElectricBurstWaveSpell;

public class ModSpells {
    public static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, LegendarySpellbook.MODID);

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }
    public static final RegistryObject<AbstractSpell> ELECTRIC_BURST_WAVE = SPELLS.register("electric_burst_wave", () -> new ElectricBurstWaveSpell());
    public static final RegistryObject<AbstractSpell> SUMMON_HAUNTED_ARMY = SPELLS.register("summon_haunted_army", () -> new SummonHauntedArmySpell());
    public static final RegistryObject<AbstractSpell> SOUL_end = SPELLS.register("soul_end", () ->  new SoulEndSpell());
    public static final RegistryObject<AbstractSpell> LEGENDARY_PALADIN_BLESSING_SPELL = SPELLS.register("legendary_paladin_blessing", () ->  new LegendaryPaladinBlessingSpell());

}
