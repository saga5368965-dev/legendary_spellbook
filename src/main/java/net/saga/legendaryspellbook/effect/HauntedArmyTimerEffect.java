package net.saga.legendaryspellbook.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;

public class HauntedArmyTimerEffect extends MobEffect {
    public HauntedArmyTimerEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    @SuppressWarnings("resource")
    public void removeAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap map, int amp) {
        // タイマー終了時にサーバー側で消滅させる
        if (!entity.level().isClientSide) {
            entity.discard();
        }
        super.removeAttributeModifiers(entity, map, amp);
    }

    // 1.20.1で必須のメソッド
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
