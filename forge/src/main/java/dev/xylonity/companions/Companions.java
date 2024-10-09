package dev.xylonity.companions;

import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

@Mod(CompanionsCommon.MOD_ID)
public class Companions {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, CompanionsCommon.MOD_ID);

    public Companions() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CompanionsEntities.ENTITY.register(modEventBus);

        ITEMS.register(modEventBus);

        CompanionsCommon.init();
    }

    @Mod.EventBusSubscriber(modid = CompanionsCommon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class KnightLibClientEvents {

        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {


        }

    }

}