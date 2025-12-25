package net.saga.legendaryspellbook.Spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.TargetedTargetAreaCastData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.saga.legendaryspellbook.LegendarySpellbook;
import net.saga.legendaryspellbook.registries.ModMobEffects;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class LegendaryPaladinBlessingSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(LegendarySpellbook.MODID, "legendary_paladin_blessing");
    private final float RADIUS = 20.0f;

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(70)
            .build();

    public LegendaryPaladinBlessingSpell() {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 20;
        this.spellPowerPerLevel = 5;
        this.castTime = 30;
        this.baseManaCost = 100;
    }

    @Override
    public ResourceLocation getSpellResource() { return spellId; }

    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }

    @Override
    public CastType getCastType() { return CastType.LONG; }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        double seconds = getDuration(spellLevel, caster) / 20.0;
        String timeString = String.format("%.1fs", seconds);
        int percentIncrease = 10 + (spellLevel - 1) * 10;
        int castReduction = 5 + (spellLevel - 1) * 5;
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", timeString).withStyle(ChatFormatting.BLUE),
                Component.literal("  +").append(Component.literal(percentIncrease + "% ")).append(Component.translatable("attribute.name.generic.attack_damage")).withStyle(ChatFormatting.GOLD),
                Component.literal("  +").append(Component.literal(percentIncrease + "% ")).append(Component.translatable("attribute.irons_spellbooks.spell_power")).withStyle(ChatFormatting.AQUA),
                Component.literal("  +").append(Component.literal(percentIncrease + "% ")).append(Component.translatable("attribute.name.generic.attack_speed")).withStyle(ChatFormatting.YELLOW),
                Component.literal("  +").append(Component.literal(castReduction + "% ")).append(Component.translatable("attribute.irons_spellbooks.cast_time_reduction")).withStyle(ChatFormatting.LIGHT_PURPLE),
                Component.literal("  ").append(Component.translatable("ui.irons_spellbooks.radius", RADIUS)).withStyle(ChatFormatting.GRAY)
        );
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (level instanceof ServerLevel serverLevel) {
            if (!Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, .35f, false)) {
                playerMagicData.setAdditionalCastData(new TargetEntityCastData(entity));
            }
            if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
                var target = targetData.getTarget(serverLevel);
                if (target != null) {
                    var area = TargetedAreaEntity.createTargetAreaEntity(level, target.position(), RADIUS, 0x312E2B, target);
                    playerMagicData.setAdditionalCastData(new TargetedTargetAreaCastData(target, area));
                }
            }
        }
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof TargetedTargetAreaCastData targetData) {
            if (level instanceof ServerLevel serverLevel) {
                var targetEntity = targetData.getTarget(serverLevel);
                if (targetEntity != null) {
                    int duration = getDuration(spellLevel, entity);
                    int amplifier = spellLevel - 1;
                    level.getEntitiesOfClass(LivingEntity.class, targetEntity.getBoundingBox().inflate(RADIUS)).forEach((victim) -> {
                        if (victim.distanceToSqr(targetEntity) < RADIUS * RADIUS && Utils.shouldHealEntity(entity, victim)) {
                            if (ModMobEffects.LEGENDARY_PALADIN_BLESSING.isPresent()) {
                                victim.addEffect(new MobEffectInstance(ModMobEffects.LEGENDARY_PALADIN_BLESSING.get(), duration, amplifier));
                            }
                        }
                    });
                }
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private int getDuration(int spellLevel, LivingEntity caster) {
        return (int) (getSpellPower(spellLevel, caster) * 20);
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() { return Optional.of(SoundRegistry.CLOUD_OF_REGEN_LOOP.get()); }

    @Override
    public Optional<SoundEvent> getCastFinishSound() { return Optional.of(net.minecraft.sounds.SoundEvents.WITHER_SPAWN); }

    @Override
    public AnimationHolder getCastStartAnimation() { return SpellAnimations.SELF_CAST_ANIMATION; }
}