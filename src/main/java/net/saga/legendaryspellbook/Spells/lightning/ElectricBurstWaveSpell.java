package net.saga.legendaryspellbook.Spells.lightning;

import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.ElectricityEntity;
import net.saga.legendaryspellbook.LegendarySpellbook;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class ElectricBurstWaveSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(LegendarySpellbook.MODID, "electric_burst_wave");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
            .setMaxLevel(4)
            .setCooldownSeconds(10)
            .build();

    public ElectricBurstWaveSpell() {
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 25;
        this.spellPowerPerLevel = 7;
        this.castTime = 20;
        this.baseManaCost = 120;
    }

    // --- 【共通計算メソッド】 ---
    private int getBoltsCount(int spellLevel) {
        return 3 + (spellLevel * 4);
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster);
    }

    private float getLifeTime(int spellLevel) {
        return 35.0F + (spellLevel * 15.0F);
    }

    // --- 【スペック情報の表示】 ---
    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                // ダメージ
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)),
                // 波の数（独自の翻訳キー）
                Component.translatable("ui.legendary_spellbook.bolts_count", getBoltsCount(spellLevel)),
                // 持続時間（独自の翻訳キー）
                Component.translatable("ui.legendary_spellbook.lifetime", Utils.stringTruncation(getLifeTime(spellLevel) / 20.0F, 1))
        );
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
        return Optional.of(SoundRegistry.LIGHTNING_LANCE_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.WITHER_SHOOT);
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        // 共通メソッドから数値を流用
        int boltsCount = getBoltsCount(spellLevel);
        float baseDamage = getDamage(spellLevel, entity);
        float lifeTime = getLifeTime(spellLevel);

        float spreadAngle = 30.0F + (spellLevel * 10.0F);
        float angleStep = spreadAngle / Math.max(1, boltsCount - 1);
        float playerRot = entity.getYRot();

        for (int i = 0; i < boltsCount; i++) {
            float currentAngle = playerRot - (spreadAngle / 2) + (angleStep * i);
            float rad = (float) Math.toRadians(currentAngle);

            double dx = -Math.sin(rad);
            double dz = Math.cos(rad);

            ElectricityEntity bolt = new ElectricityEntity(
                    entity, dx, 0.0, dz, world,
                    baseDamage, currentAngle, lifeTime
            );

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