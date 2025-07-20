package dev.xylonity.companions;

import dev.xylonity.companions.common.biome.CompanionsSpawnBiomeModifier;
import dev.xylonity.companions.client.ClientProxy;
import dev.xylonity.companions.common.CommonProxy;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.proxy.IProxy;
import dev.xylonity.companions.registry.*;
import dev.xylonity.knightlib.config.ConfigComposer;
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

@Mod(CompanionsCommon.MOD_ID)
public class Companions {

    public static final String MOD_ID = CompanionsCommon.MOD_ID;

    public static final IProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CompanionsCommon.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Companions.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Companions.MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Companions.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Companions.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Companions.MOD_ID);

    public Companions() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CompanionsLootModifier.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        CompanionsBlockEntities.BLOCK_ENTITY.register(modEventBus);
        CompanionsMenuTypes.MENU_TYPES.register(modEventBus);
        CompanionsRecipes.TYPES.register(modEventBus);
        CompanionsRecipes.SERIALIZERS.register(modEventBus);
        CompanionsEntities.ENTITY.register(modEventBus);
        CompanionsSpawnBiomeModifier.BIOME_MODIFIER.register(modEventBus);

        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        PARTICLES.register(modEventBus);
        SOUNDS.register(modEventBus);

        CompanionsSpawnBiomeModifier.BIOME_MODIFIER.register("companions_mob_spawns", CompanionsSpawnBiomeModifier::makeCodec);

        ConfigComposer.registerConfig(CompanionsConfig.class, modEventBus);
        CompanionsCommon.init();
    }

}