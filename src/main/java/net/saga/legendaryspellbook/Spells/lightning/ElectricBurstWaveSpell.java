package net.saga.legendaryspellbook.Spells.lightning;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.ElectricityEntity;
import net.minecraft.sounds.SoundEvents;
import java.util.Optional;


 public class ElectricBurstWaveSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("legendary_spellbook", "electric_burst_wave");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(20)
            .build();
    public ElectricBurstWaveSpell() {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 15;
        this.spellPowerPerLevel = 5;
        this.castTime =20;
        this.baseManaCost = 120;
    }
    @Override
    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }
    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }
    @Override
    public Optional<SoundEvent> getCastStartSound() {
        // 溜め時間はそのまま（またはお好みで変更）
        return Optional.of(SoundRegistry.DIVINE_SMITE_WINDUP.get());
    }
    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        // ウィザーの弾の発射音に変更
        return Optional.of(SoundEvents.WITHER_SHOOT);
    }
     @Override
     public CastType getCastType() {
         return CastType.LONG;
     }
    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        int boltsCount = 3 + (spellLevel * 1);
        float spreadAngle = 30.0F + (spellLevel * 10.0F);
        float angleStep = spreadAngle / Math.max(1, boltsCount - 1);

        // 修正1: playerMagicDataをRandomSourceにキャストせず、entityから取得
        float baseDamage = this.baseSpellPower + (spellLevel - 1) * this.spellPowerPerLevel;
        float lifeTime = 30.0F + (spellLevel * 5.0F);
        float playerRot = entity.getYRot();

        for (int i = 0; i < boltsCount; i++) {
            float currentAngle = playerRot - (spreadAngle / 2) + (angleStep * i);
            float rad = (float) Math.toRadians(currentAngle);

            double dx = -Math.sin(rad);
            double dz = Math.cos(rad);

            ElectricityEntity bolt = new ElectricityEntity(
                    entity,
                    dx,
                    0.0,
                    dz,
                    world,
                    baseDamage,
                    currentAngle,
                    lifeTime
            );

            // 出現位置をプレイヤーの少し前に設定
            double spawnX = entity.getX() + dx * 1.5;
            double spawnY = entity.getY() + 0.5;
            double spawnZ = entity.getZ() + dz * 1.5;
            bolt.moveTo(spawnX, spawnY, spawnZ);

            world.addFreshEntity(bolt);
        }

        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.OVERHEAD_MELEE_SWING_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return AnimationHolder.pass();
    }
}
