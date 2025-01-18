package dev.xylonity.companions;

import dev.xylonity.companions.common.tick.TickScheduler;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.util.Iterator;

import static dev.xylonity.companions.common.tick.TickScheduler.TASKS;

@Mod(CompanionsCommon.MOD_ID)
public class Companions {

    public static final String MOD_ID = CompanionsCommon.MOD_ID;
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, CompanionsCommon.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Companions.MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Companions.MOD_ID);

    public Companions() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CompanionsEntities.ENTITY.register(modEventBus);

        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);

        CompanionsCommon.init();
    }

    @Mod.EventBusSubscriber(modid = CompanionsCommon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class KnightLibClientEvents {

        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {


        }

    }

    @Mod.EventBusSubscriber(modid = CompanionsCommon.MOD_ID)
    public static class TestTickScheduler {

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                if (event.side.isServer() && event.type == TickEvent.Type.SERVER) {
                    TASKS.forEach((level, tasksList) -> {
                        long currentTime = level.getGameTime();
                        Iterator<TickScheduler.ScheduledTask> it = tasksList.iterator();
                        while (it.hasNext()) {
                            TickScheduler.ScheduledTask task = it.next();
                            if (task.runAt <= currentTime) {
                                task.runnable.run();
                                it.remove();
                            }
                        }
                    });
                }

            }

        }

    }

}