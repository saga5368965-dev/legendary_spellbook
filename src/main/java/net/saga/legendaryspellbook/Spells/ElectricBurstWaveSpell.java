package net.saga.legendaryspellbook.Spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.ElectricityEntity;
import net.miauczel.legendary_monsters.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class ElectricBurstWaveSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("legendary_spellbook", "electric_burst_wave");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(15)
            .build();

    public ElectricBurstWaveSpell() {
        super();
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 15;
        this.spellPowerPerLevel = 5;
        this.castTime = 0;
        this.baseManaCost = 120;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        float maxSpreadAngle = 40.0f + (spellLevel * 16.0f);
        int waveCount = 8 + (spellLevel * 2);
        float stepDistance = 1.3f;
        float playerYaw = entity.getYRot();

        final int startTick = serverLevel.getServer().getTickCount();

        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.LIGHTNING_BOLT_IMPACT, net.minecraft.sounds.SoundSource.PLAYERS, 1.5f, 0.7f);

        for (int l = 0; l < waveCount; l++) {
            final int waveIndex = l;
            int delayTicks = waveIndex * 10;

            serverLevel.getServer().tell(new TickTask(startTick + delayTicks, () -> {
                // 実行された瞬間にコンソールにメッセージを出す（動作確認用）
                System.out.println("Electric Wave [" + waveIndex + "] executed at Tick: " + serverLevel.getServer().getTickCount());

                if (entity == null || !entity.isAlive()) return;

                float currentDistance = (waveIndex + 1) * stepDistance;
                float currentSpread = ((float) waveIndex / (waveCount - 1)) * maxSpreadAngle;
                int burstsInThisWave = Math.max(1, (int) (currentDistance * 1.8f));

                for (int i = 0; i < burstsInThisWave; i++) {
                    float angleOffset = (burstsInThisWave <= 1) ? 0 : ((float) i / (burstsInThisWave - 1) - 0.5f) * currentSpread;
                    float finalAngle = playerYaw + angleOffset;
                    float rad = (float) Math.toRadians(finalAngle);

                    double dx = -Mth.sin(rad) * currentDistance;
                    double dz = Mth.cos(rad) * currentDistance;
                    double spawnX = entity.getX() + dx;
                    double spawnZ = entity.getZ() + dz;

                    spawnElectricityBurst(serverLevel, spawnX, entity.getY() + 1.5, spawnZ, finalAngle, entity);
                }

                if (waveIndex % 2 == 0) {
                    serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            SoundEvents.LIGHTNING_BOLT_THUNDER, net.minecraft.sounds.SoundSource.PLAYERS, 0.4f, 1.1f + (waveIndex * 0.05f));
                }
            }));
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private void spawnElectricityBurst(ServerLevel world, double x, double y, double z, float yRot, LivingEntity caster) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        for (int i = 0; i < 6; i++) {
            BlockPos below = blockpos.below();
            if (world.getBlockState(below).isFaceSturdy(world, below, Direction.UP)) {
                ElectricityEntity burst = ModEntities.ELectr.get().create(world);
                if (burst != null) {
                    burst.moveTo(x, blockpos.getY(), z, yRot, 0.0F);
                    burst.setOwner(caster);
                    world.addFreshEntity(burst);
                }
                break;
            }
            blockpos = blockpos.below();
        }
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1)),
                Component.literal("§7Waves: ").append(Component.literal(String.valueOf(8 + (spellLevel * 2))))
        );
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ANIMATION_INSTANT_CAST;
    }

    @Override
    public CastType getCastType() { return CastType.INSTANT; }
    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }
    @Override
    public ResourceLocation getSpellResource() { return spellId; }
    @Override
    public Optional<SoundEvent> getCastFinishSound() { return Optional.of(SoundEvents.WITHER_SHOOT); }
}

