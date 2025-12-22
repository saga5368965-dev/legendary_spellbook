package net.saga.legendaryspellbook.registries;

import net.miauczel.legendary_monsters.entity.client.HauntedGuardRenderer;
import net.miauczel.legendary_monsters.entity.client.LivingArmorRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.miauczel.legendary_monsters.entity.client.FHauntedGuardRenderer;
import net.saga.legendaryspellbook.LegendarySpellbook;
import net.saga.legendaryspellbook.entity.mobs.SummonedHauntedGuard;
import net.saga.legendaryspellbook.entity.mobs.SummonedHauntedKnight;

@Mod.EventBusSubscriber(modid = LegendarySpellbook.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    // --- サーバー・クライアント共通：属性の登録 ---
    @SubscribeEvent
    public static void onAttributeCreation(EntityAttributeCreationEvent event) {
        // 騎士とガードのステータスを登録
        event.put(ModEntities.SUMMONED_HAUNTED_KNIGHT.get(), SummonedHauntedKnight.createAttributes().build());
        event.put(ModEntities.SUMMONED_HAUNTED_GUARD.get(), SummonedHauntedGuard.createAttributes().build());
    }
    // --- クライアント専用：見た目（レンダラー）の登録 ---
    @Mod.EventBusSubscriber(modid = LegendarySpellbook.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.SUMMONED_HAUNTED_GUARD.get(),
                    (context) -> new HauntedGuardRenderer(context));

            // 騎士の登録 (明示的なキャスト版)
            event.registerEntityRenderer(ModEntities.SUMMONED_HAUNTED_KNIGHT.get(),
                    (context) -> new LivingArmorRenderer(context));
    }
}
}
