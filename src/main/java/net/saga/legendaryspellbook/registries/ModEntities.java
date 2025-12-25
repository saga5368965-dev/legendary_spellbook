package net.saga.legendaryspellbook.registries;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.saga.legendaryspellbook.entity.mobs.SummonedHauntedGuard;
import net.saga.legendaryspellbook.entity.mobs.SummonedHauntedKnight;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "legendary_spellbook");

    public static final RegistryObject<EntityType<SummonedHauntedKnight>> SUMMONED_HAUNTED_KNIGHT =
            ENTITIES.register("summoned_haunted_knight", () ->
                    EntityType.Builder.<SummonedHauntedKnight>of(SummonedHauntedKnight::new, MobCategory.CREATURE)
                            .sized(0.7F, 2.0F)
                            .clientTrackingRange(64)
                            .build("summoned_haunted_knight"));

    public static final RegistryObject<EntityType<SummonedHauntedGuard>> SUMMONED_HAUNTED_GUARD =
            ENTITIES.register("summoned_haunted_guard", () ->
                    EntityType.Builder.<SummonedHauntedGuard>of(SummonedHauntedGuard::new, MobCategory.CREATURE)
                            .sized(0.7F, 2.0F)
                            .clientTrackingRange(64)
                            .build("summoned_haunted_guard"));
}

