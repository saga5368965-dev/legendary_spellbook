package net.saga.legendaryspellbook.registries;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.saga.legendaryspellbook.effect.HauntedArmyTimerEffect;
import net.saga.legendaryspellbook.effect.LegendaryPaladinBlessing;

public class ModMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "legendary_spellbook");

    public static final RegistryObject<MobEffect> HAUNTED_ARMY_TIMER = MOB_EFFECTS.register("haunted_army_timer",
            () -> new HauntedArmyTimerEffect(MobEffectCategory.NEUTRAL, 0x4d0000));
    public static final RegistryObject<MobEffect> LEGENDARY_PALADIN_BLESSING = MOB_EFFECTS.register("legendary_paladin_blessing",
            () -> new LegendaryPaladinBlessing(MobEffectCategory.BENEFICIAL, 0x312E2B));
    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
