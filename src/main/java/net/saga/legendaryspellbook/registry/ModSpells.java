package net.saga.legendaryspellbook.registry;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.saga.legendaryspellbook.LegendarySpellbook;
import net.saga.legendaryspellbook.Spells.lightning.ElectricBurstWaveSpell;

public class ModSpells {
    public static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, LegendarySpellbook.MODID);

    // メインクラスから呼ばれる「提出」用メソッド
    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }
    // 呪文の登録
    public static final RegistryObject<AbstractSpell> ELECTRIC_BURST_WAVE = SPELLS.register("electric_burst_wave", () -> new ElectricBurstWaveSpell());
}
