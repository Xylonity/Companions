package dev.xylonity.companions;

import dev.xylonity.companions.common.event.ClientBossMusicHandler;
import dev.xylonity.companions.common.tick.TickScheduler;
import dev.xylonity.companions.config.BuildSidedConfig;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CompanionsCommon.MOD_ID)
public class Companions {

    public static final String MOD_ID = CompanionsCommon.MOD_ID;
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CompanionsCommon.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Companions.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Companions.MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Companions.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Companions.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Companions.MOD_ID);

    public Companions() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CompanionsEntities.ENTITY.register(modEventBus);
        CompanionsBlockEntities.BLOCK_ENTITY.register(modEventBus);
        CompanionsMenuTypes.MENU_TYPES.register(modEventBus);

        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        PARTICLES.register(modEventBus);
        SOUNDS.register(modEventBus);

        BuildSidedConfig.of(modEventBus, CompanionsConfig.class);
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
    public static class CompanionsTickScheduler {

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            TickScheduler.cleanMarkedLevels();

            for (ServerLevel level : event.getServer().getAllLevels()) {
                TickScheduler.incrementTick(level);
                TickScheduler.processServerTasks(level);
                TickScheduler.processCommonTasks(level);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            if (level == null) return;

            TickScheduler.cleanMarkedLevels();
            TickScheduler.incrementTick(level);
            TickScheduler.processClientTasks(level);
            TickScheduler.processCommonTasks(level);
        }

        @SubscribeEvent
        public static void onLevelUnload(LevelEvent.Unload event) {
            LevelAccessor acc = event.getLevel();
            if (acc instanceof Level level) {
                TickScheduler.markLevelForCleanup(level);
            }
        }

    }

}