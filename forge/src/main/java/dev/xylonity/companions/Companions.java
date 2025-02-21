package dev.xylonity.companions;

import dev.xylonity.companions.common.item.WrenchItem;
import dev.xylonity.companions.common.tick.TickScheduler;
import dev.xylonity.companions.config.CompanionsForgeConfig;
import dev.xylonity.companions.registry.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Iterator;

import static dev.xylonity.companions.common.tick.TickScheduler.TASKS;

@Mod(CompanionsCommon.MOD_ID)
public class Companions {

    public static final String MOD_ID = CompanionsCommon.MOD_ID;
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CompanionsCommon.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Companions.MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Companions.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Companions.MOD_ID);

    public Companions() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CompanionsEntities.ENTITY.register(modEventBus);
        CompanionsBlocks.BLOCK.register(modEventBus);
        CompanionsBlockEntities.BLOCK_ENTITY.register(modEventBus);

        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        PARTICLES.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CompanionsForgeConfig.SPEC, "kqcompanions.toml");

        CompanionsCommon.init();

        MinecraftForge.EVENT_BUS.register(new Testing());
    }

    static class Testing {
        @SubscribeEvent
        public void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
            Entity cameraEntity = event.getCamera().getEntity();

            if (cameraEntity instanceof Player player) {
                if (player.hasEffect(CompanionsEffects.ELECTROSHOCK.get())) {
                    float intensity = 0.2F;

                    double offsetX = (Math.random() - 0.5) * intensity;
                    double offsetY = (Math.random() - 0.5) * intensity;
                    double offsetZ = (Math.random() - 0.5) * intensity;

                    event.getCamera().move(offsetX, offsetY, offsetZ);
                }
            }
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