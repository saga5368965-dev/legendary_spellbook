package net.saga.legendaryspellbook.Spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.saga.legendaryspellbook.LegendarySpellbook;

import java.util.List;
import java.util.Objects;

@AutoSpellConfig
public class SoulEndSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(LegendarySpellbook.MODID, "soul_end");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(180)
            .build();

    public SoulEndSpell() {
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 14;
        this.spellPowerPerLevel = 5;
        this.castTime = 19;
        this.baseManaCost = 160;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        float rangeMultiplier = 1.0f + (spellLevel - 1) * 0.15f;
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)),
                Component.translatable("ui.irons_spellbooks.radius", Utils.stringTruncation(16.0f * rangeMultiplier, 1)).withStyle(ChatFormatting.YELLOW)
        );
    }

    @Override
    public CastType getCastType() { return CastType.LONG; }

    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }

    @Override
    public ResourceLocation getSpellResource() { return spellId; }

    @Override
    public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (playerMagicData != null && playerMagicData.getCastDurationRemaining() % 8 == 0) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.WITHER_AMBIENT, entity.getSoundSource(), 1.0f, 0.4f);
        }
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float totalDamage = getDamage(spellLevel, entity);
        float rangeMultiplier = 1.0f + (spellLevel - 1) * 0.15f;
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 2.0f, 0.7f);
        Vec3 smiteLocation = entity.position();
        performPaladinWave(level, entity, smiteLocation, 15.0f * rangeMultiplier, totalDamage * 0.5f);

        if (!level.isClientSide) {
            Objects.requireNonNull(level.getServer()).tell(new net.minecraft.server.TickTask(level.getServer().getTickCount() + 3, () -> {
                performPaladinWave(level, entity, smiteLocation, 13.0f * rangeMultiplier, totalDamage * 0.3f);
                level.playSound(null, smiteLocation.x, smiteLocation.y, smiteLocation.z, SoundEvents.WITHER_SHOOT, entity.getSoundSource(), 1.2f, 0.3f);
            }));
            level.getServer().tell(new net.minecraft.server.TickTask(level.getServer().getTickCount() + 6, () -> {
                performPaladinWave(level, entity, smiteLocation, 19.0f * rangeMultiplier, totalDamage * 0.2f);
                CameraShakeManager.addCameraShake(new CameraShakeData(40, smiteLocation, 35f));
                level.playSound(null, smiteLocation.x, smiteLocation.y, smiteLocation.z, SoundEvents.GENERIC_EXPLODE, entity.getSoundSource(), 1.5f, 0.3f);
            }));
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private void performPaladinWave(Level level, LivingEntity caster, Vec3 center, float radius, float damage) {
        level.playSound(null, center.x, center.y, center.z, SoundEvents.IRON_GOLEM_ATTACK, caster.getSoundSource(), 1.5f, 0.4f);
        float centerFillRadius = radius * 0.3f;
        int fillDensity = (int) (centerFillRadius * centerFillRadius * 6.0f);

        for (int i = 0; i < fillDensity; i++) {
            double r = Math.sqrt(level.random.nextDouble()) * centerFillRadius;
            double theta = level.random.nextDouble() * 2 * Math.PI;
            double px = center.x + r * Math.cos(theta);
            double pz = center.z + r * Math.sin(theta);
            MagicManager.spawnParticles(level, ParticleTypes.SOUL_FIRE_FLAME, px, center.y + 0.1, pz, 1, 0.05, 0.1, 0.05, 0.02, false);
            if (level.random.nextFloat() < 0.4f) {
                MagicManager.spawnParticles(level, ParticleTypes.SOUL, px, center.y + 0.2, pz, 1, 0.1, 0.3, 0.1, 0.03, false);
            }
        }

        int directions = 16;
        for (int i = 0; i < directions; i++) {
            double rad = Math.toRadians(i * (360.0 / directions));
            double vx = Math.cos(rad);
            double vz = Math.sin(rad);

            for (double dist = 0.5; dist <= radius; dist += 1.0) {
                double px = center.x + vx * dist;
                double pz = center.z + vz * dist;
                MagicManager.spawnParticles(level, ParticleTypes.SOUL, px, center.y + 0.1, pz, 3, 0.1, 0.1, 0.1, 0.05, false);
                if (dist > centerFillRadius && dist % 2 == 0) {
                    MagicManager.spawnParticles(level, ParticleTypes.SOUL_FIRE_FLAME, px, center.y + 0.1, pz, 2, 0.1, 0.2, 0.1, 0.03, false);
                }
            }
        }

        if (!level.isClientSide) {
            AABB hitBox = new AABB(center.x - radius, center.y - 1, center.z - radius, center.x + radius, center.y + 5, center.z + radius);
            for (Entity target : level.getEntities(caster, hitBox)) {
                if (target instanceof LivingEntity victim && victim != caster && victim.isAlive()) {
                    double dx = victim.getX() - center.x;
                    double dz = victim.getZ() - center.z;
                    double distance = Math.sqrt(dx * dx + dz * dz);

                    if (distance <= radius && DamageSources.applyDamage(victim, damage, getDamageSource(caster))) {
                        float knockup = 0.3f + (radius / 20f);
                        victim.setDeltaMovement(dx / distance * 0.4, knockup, dz / distance * 0.4);
                        victim.hurtMarked = true;
                        if (victim instanceof net.minecraft.world.entity.player.Player p) p.disableShield(true);
                        caster.heal(0.5f);
                    }
                }
            }
        }
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity) + (entity != null ? Utils.getWeaponDamage(entity, net.minecraft.world.entity.MobType.UNDEFINED) : 0);
    }

    private String getDamageText(int spellLevel, LivingEntity entity) {
        return Utils.stringTruncation(getDamage(spellLevel, entity), 1);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.OVERHEAD_MELEE_SWING_ANIMATION;
    }
}