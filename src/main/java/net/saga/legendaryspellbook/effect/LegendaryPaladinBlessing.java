package net.saga.legendaryspellbook.effect;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class LegendaryPaladinBlessing extends MobEffect {
    public LegendaryPaladinBlessing(MobEffectCategory beneficial, int i) {
        super(MobEffectCategory.BENEFICIAL, 0x312E2B);

        // 各レベルごとに「どれくらい加算するか」を 0.1D (=10%) などで設定します。
        // 計算式: 最終値 = 元の値 * (1 + (設定値 * エフェクトレベル))
        // ※エフェクトレベル(amplifier)は 0 から始まるので、レベル1で 10%、レベル5で 50% 増しになります。

        // 1. 物理攻撃力: レベルごとに +10%
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "C5D766D0-5A3A-4F7D-B6E3-2C4B7D9B4E21", 0.1D, AttributeModifier.Operation.MULTIPLY_BASE);

        // 2. 魔法攻撃力: レベルごとに +10%
        this.addAttributeModifier(AttributeRegistry.SPELL_POWER.get(), "E2B7A9D1-4F2C-3B1A-9D8E-7C6B5A4D3F2E", 0.1D, AttributeModifier.Operation.MULTIPLY_BASE);

        // 3. 攻撃速度: レベルごとに +10%
        this.addAttributeModifier(Attributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.1D, AttributeModifier.Operation.MULTIPLY_BASE);

        // 4. 詠唱速度減少: レベルごとに +5%
        this.addAttributeModifier(AttributeRegistry.CAST_TIME_REDUCTION.get(), "6E3F3328-4C0A-AA36-5BA2-BB9DBEF3AF8B", 0.05D, AttributeModifier.Operation.MULTIPLY_BASE);
    }
}
