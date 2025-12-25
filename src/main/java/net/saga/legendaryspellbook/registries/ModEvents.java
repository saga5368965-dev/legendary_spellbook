package net.saga.legendaryspellbook.registries;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.IceSpikeEntity;
import net.miauczel.legendary_monsters.entity.client.HauntedGuardRenderer;
import net.miauczel.legendary_monsters.entity.client.LivingArmorRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.saga.legendaryspellbook.LegendarySpellbook;
import net.saga.legendaryspellbook.entity.mobs.SummonedHauntedGuard;
import net.saga.legendaryspellbook.entity.mobs.SummonedHauntedKnight;

@Mod.EventBusSubscriber(modid = LegendarySpellbook.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void onAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SUMMONED_HAUNTED_KNIGHT.get(), SummonedHauntedKnight.createAttributes().build());
        event.put(ModEntities.SUMMONED_HAUNTED_GUARD.get(), SummonedHauntedGuard.createAttributes().build());
    }

    @Mod.EventBusSubscriber(modid = LegendarySpellbook.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.SUMMONED_HAUNTED_GUARD.get(), HauntedGuardRenderer::new);
            event.registerEntityRenderer(ModEntities.SUMMONED_HAUNTED_KNIGHT.get(), LivingArmorRenderer::new);
        }
    }
}

@Mod.EventBusSubscriber(modid = LegendarySpellbook.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
class GameplayEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        boolean isSpike = event.getSource().getDirectEntity() instanceof IceSpikeEntity ||
                event.getSource().getMsgId().equals("freeze");

        if (isSpike) {
            IceSpikeEntity spike = null;
            if (event.getSource().getDirectEntity() instanceof IceSpikeEntity) {
                spike = (IceSpikeEntity) event.getSource().getDirectEntity();
            }

            if (spike != null) {
                LivingEntity caster = spike.getCaster();
                if (caster != null) {
                    float spellPower = (float) caster.getAttributeValue(AttributeRegistry.SPELL_POWER.get());
                    float enhancedDamage = 6.0f + (spellPower * 1.5f);
                    event.setAmount(enhancedDamage);
                }
            }
        }

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            MobEffectInstance blessingInstance = attacker.getEffect(ModMobEffects.LEGENDARY_PALADIN_BLESSING.get());

            if (blessingInstance != null) {
                LivingEntity victim = event.getEntity();
                int amp = blessingInstance.getAmplifier();

                MobEffect brokenArmor = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("legendary_monsters", "broken_armor"));
                MobEffect bleeding = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("legendary_monsters", "bleeding"));
                int duration = 100 + (amp * 40);
                int debuffLevel = amp;

                if (brokenArmor != null) {
                    victim.addEffect(new MobEffectInstance(brokenArmor, duration, debuffLevel));
                }

                if (bleeding != null) {
                    victim.addEffect(new MobEffectInstance(bleeding, duration, debuffLevel));
                }
            }
        }
    }
}