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
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Mobs.AbandonedCrypt.HauntedGuardEntity;
import net.saga.legendaryspellbook.registries.ModEntities;
import net.saga.legendaryspellbook.registries.ModMobEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SummonedHauntedGuard extends HauntedGuardEntity implements MagicSummon {
    protected LivingEntity cachedSummoner;
    protected UUID summonerUUID;

    public SummonedHauntedGuard(EntityType<?> pEntityType, Level pLevel) {
        super((EntityType<? extends HauntedGuardEntity>) pEntityType, pLevel);
        this.xpReward = 0;
    }

    public SummonedHauntedGuard(Level pLevel, LivingEntity owner) {
        this(ModEntities.SUMMONED_HAUNTED_GUARD.get(), pLevel);
        setSummoner(owner);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof HurtByTargetGoal ||
        goal.getGoal() instanceof NearestAttackableTargetGoal);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new GenericFollowOwnerGoal(this, this::getSummoner, 1.0f, 10, 2, true, 15));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F, 1.0F));
        this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
        this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
        this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
        this.targetSelector.addGoal(4, (new GenericHurtByTargetGoal(this, (entity) -> entity == getSummoner())).setAlertOthers());
        super.registerGoals();
    }
    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if (shouldIgnoreDamage(pSource)) {
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity entityIn) {
        return entityIn == this || this.isAlliedHelper(entityIn) || super.isAlliedTo(entityIn);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            // タイマーが切れたら消滅
            if (!this.hasEffect(ModMobEffects.HAUNTED_ARMY_TIMER.get())) {
                this.onUnSummon();
            }
        }
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        this.onDeathHelper();
        super.die(pDamageSource);
    }

    @Override
    public void onUnSummon() {
        if (!level().isClientSide) {
            MagicManager.spawnParticles(level(), ParticleTypes.POOF, getX(), getY(), getZ(), 25, .4, .8, .4, .03, false);
            discard();
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
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.summonerUUID = OwnerHelper.deserializeOwner(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        OwnerHelper.serializeOwner(compoundTag, summonerUUID);
    }
}