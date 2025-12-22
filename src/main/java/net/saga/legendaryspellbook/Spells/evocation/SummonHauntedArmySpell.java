package net.saga.legendaryspellbook.Spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.saga.legendaryspellbook.LegendarySpellbook;
import net.saga.legendaryspellbook.entity.mobs.SummonedHauntedGuard;
import net.saga.legendaryspellbook.entity.mobs.SummonedHauntedKnight;
import net.saga.legendaryspellbook.registries.ModMobEffects;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class SummonHauntedArmySpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(LegendarySpellbook.MODID, "summon_haunted_army");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(90)
            .build();

    public SummonHauntedArmySpell() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 30;
        this.baseManaCost = 100;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.EVOKER_PREPARE_SUMMON);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.BEACON_POWER_SELECT);
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (world instanceof ServerLevel serverLevel) {
            // タイマーの時間設定（10分）
            int summonTime = 20 * 60 * 10;
            float radius = 2.0f + 0.2f * spellLevel;

            // 1. プレイヤー（自分）にタイマーを表示させる
            entity.addEffect(new MobEffectInstance(
                    ModMobEffects.HAUNTED_ARMY_TIMER.get(),
                    summonTime,
                    0,
                    false, // ambient
                    false, // visible
                    true   // showIcon: これで画面右上にアイコンと残り時間が出る
            ));
            // 召喚数の計算: 基本6体 + 呪文レベル
            int summonCount = 6 + spellLevel;

            for (int i = 0; i < summonCount; i++) {
                boolean isKnight = Utils.random.nextDouble() < 0.5;
                Monster haunted = isKnight ? new SummonedHauntedKnight(world, entity) : new SummonedHauntedGuard(world, entity);

                haunted.finalizeSpawn(serverLevel, world.getCurrentDifficultyAt(haunted.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);

                // 2. モンスター側にもエフェクトを付与（消滅判定用）
                haunted.addEffect(new MobEffectInstance(ModMobEffects.HAUNTED_ARMY_TIMER.get(), summonTime, 0, false, false, false));

                // 円形に配置するための計算（分母を summonCount に合わせることで均等に並ぶ）
                Vec3 spawnPos = entity.position().add(new Vec3(radius, 0, 0).yRot((float) Math.toRadians(360f / summonCount * i)));
                haunted.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

                world.addFreshEntity(haunted);
            }
        }
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    public List<MutableComponent> getUniqueSpellsText(int spellLevel) {
        // 実際の召喚数（6 + レベル）を計算してツールチップに表示
        int summonCount = 6 + spellLevel;

        return List.of(
                Component.translatable("ui.irons_spellbooks.summon_count", summonCount),
                Component.translatable(String.format("spell.%s.%s.desc", LegendarySpellbook.MODID, "summon_haunted_army"))
        );
    }
}
