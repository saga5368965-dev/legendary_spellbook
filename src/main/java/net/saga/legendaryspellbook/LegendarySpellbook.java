package net.saga.legendaryspellbook;

import com.mojang.logging.LogUtils;
import net.saga.legendaryspellbook.registries.ModMobEffects;
import net.saga.legendaryspellbook.registries.ModSpells;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.saga.legendaryspellbook.registries.ModEntities;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LegendarySpellbook.MODID)
public class LegendarySpellbook {
    public static final String MODID = "legendary_spellbook";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LegendarySpellbook() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        ModEntities.ENTITIES.register(modEventBus);
        ModMobEffects.MOB_EFFECTS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        ModSpells.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
