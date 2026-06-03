package dev.xylonity.companions;

import dev.xylonity.companions.client.ClientProxy;
import dev.xylonity.companions.common.CommonProxy;
import dev.xylonity.companions.common.biome.CompanionsSpawnBiomeModifier;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.proxy.IProxy;
import dev.xylonity.companions.registry.*;
import dev.xylonity.knightlib.api.config.ConfigComposer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CompanionsCommon.MOD_ID)
public class Companions {

    public static final String MOD_ID = CompanionsCommon.MOD_ID;

    public static IProxy PROXY = FMLLoader.getDist() == Dist.CLIENT ? new ClientProxy() : new CommonProxy();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, CompanionsCommon.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Companions.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Companions.MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Companions.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Companions.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Companions.MOD_ID);
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, Companions.MOD_ID);

    public Companions(IEventBus modEventBus, ModContainer modContainer) {

        CompanionsLootModifier.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        CompanionsBlockEntities.BLOCK_ENTITY.register(modEventBus);
        CompanionsMenuTypes.MENU_TYPES.register(modEventBus);
        CompanionsRecipes.TYPES.register(modEventBus);
        CompanionsRecipes.SERIALIZERS.register(modEventBus);
        CompanionsEntities.ENTITY.register(modEventBus);
        CompanionsSpawnBiomeModifier.BIOME_MODIFIER.register(modEventBus);

        ARMOR_MATERIALS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        PARTICLES.register(modEventBus);
        SOUNDS.register(modEventBus);

        Companions.PROXY.registerClientEvents();

        //CompanionsSpawnBiomeModifier.BIOME_MODIFIER.register("companions_mob_spawns", CompanionsSpawnBiomeModifier::makeCodec);

        ConfigComposer.registerConfig(Companions.MOD_ID, CompanionsConfig.class);

        CompanionsCommon.init();
    }

}