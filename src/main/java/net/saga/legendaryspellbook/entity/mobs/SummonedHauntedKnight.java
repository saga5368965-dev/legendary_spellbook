package net.saga.legendaryspellbook.entity.mobs;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.MagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.goals.*;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Mobs.AbandonedCrypt.HauntedKnightEntity;
import net.saga.legendaryspellbook.registries.ModEntities;
import net.saga.legendaryspellbook.registries.ModMobEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SummonedHauntedKnight extends HauntedKnightEntity implements MagicSummon {
    protected LivingEntity cachedSummoner;
    protected UUID summonerUUID;

    public SummonedHauntedKnight(EntityType<?> type, Level level) {
        super((EntityType<? extends HauntedKnightEntity>) type, level);
        this.xpReward = 0;
    }

    public SummonedHauntedKnight(Level level, LivingEntity owner) {
        this(ModEntities.SUMMONED_HAUNTED_KNIGHT.get(), level);
        this.setSummoner(owner);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.getAvailableGoals().removeIf(goal ->
                goal.getGoal() instanceof HurtByTargetGoal ||
                        goal.getGoal() instanceof NearestAttackableTargetGoal
        );

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new GenericFollowOwnerGoal(this, this::getSummoner, 1.0f, 10, 2, true, 50));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F, 1.0F));

        this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
        this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
        this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
        this.targetSelector.addGoal(4, (new GenericHurtByTargetGoal(this, (entity) -> entity == getSummoner())).setAlertOthers());

        super.registerGoals();
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity entityIn) {
        if (entityIn == this) {
            return true;
        } else if (entityIn == getSummoner() || this.isAlliedHelper(entityIn)) {
            return true;
        } else if (getSummoner() != null && !entityIn.isAlliedTo(getSummoner())) {
            return false;
        } else {
            return this.getTeam() == null && entityIn.getTeam() == null;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (!this.hasEffect(ModMobEffects.HAUNTED_ARMY_TIMER.get())) {
                this.onUnSummon();
            }
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        this.onDeathHelper();
        super.die(damageSource);
    }

    @Override
    public void onUnSummon() {
        if (!this.level().isClientSide) {
            MagicManager.spawnParticles(level(), ParticleTypes.POOF, getX(), getY(), getZ(), 25, .4, .8, .4, .03, false);
            this.discard();
        }
    }

    @Override
    public LivingEntity getSummoner() {
        return OwnerHelper.getAndCacheOwner(level(), cachedSummoner, summonerUUID);
    }

    public void setSummoner(@Nullable LivingEntity owner) {
        if (owner != null) {
            this.summonerUUID = owner.getUUID();
            this.cachedSummoner = owner;
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.summonerUUID = OwnerHelper.deserializeOwner(tag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        OwnerHelper.serializeOwner(tag, summonerUUID);
    }
}