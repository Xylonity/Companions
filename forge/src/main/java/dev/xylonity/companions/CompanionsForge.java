package dev.xylonity.companions;

import dev.xylonity.companions.common.biome.CompanionsSpawnBiomeModifier;
import dev.xylonity.companions.client.ClientProxy;
import dev.xylonity.companions.common.CommonProxy;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.proxy.IProxy;
import dev.xylonity.companions.registry.*;
import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.api.config.ConfigComposer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Companions.MOD_ID)
public class CompanionsForge {

    public static final String MOD_ID = Companions.MOD_ID;

    public static final IProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Companions.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CompanionsForge.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CompanionsForge.MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CompanionsForge.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CompanionsForge.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CompanionsForge.MOD_ID);

    public CompanionsForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        KnightLib.initialize();

        CompanionsLootModifier.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        CompanionsBlockEntities.BLOCK_ENTITY.register(modEventBus);
        CompanionsMenuTypes.MENU_TYPES.register(modEventBus);
        CompanionsRecipeTypes.TYPES.register(modEventBus);
        CompanionsRecipeTypes.SERIALIZERS.register(modEventBus);
        CompanionsEntities.ENTITY.register(modEventBus);
        CompanionsSpawnBiomeModifier.BIOME_MODIFIER.register(modEventBus);

        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        PARTICLES.register(modEventBus);
        SOUNDS.register(modEventBus);

        CompanionsSpawnBiomeModifier.BIOME_MODIFIER.register("companions_mob_spawns", CompanionsSpawnBiomeModifier::makeCodec);

        ConfigComposer.registerConfig(CompanionsForge.MOD_ID, CompanionsConfig.class);
        Companions.init();
    }

}