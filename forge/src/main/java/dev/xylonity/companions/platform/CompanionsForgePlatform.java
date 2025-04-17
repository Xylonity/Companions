package dev.xylonity.companions.platform;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.block.*;
import dev.xylonity.companions.common.item.HourglassItem;
import dev.xylonity.companions.common.item.armor.GenericArmorItem;
import dev.xylonity.companions.common.item.blockitem.GenericBlockItem;
import dev.xylonity.companions.common.item.book.books.*;
import dev.xylonity.companions.common.item.WrenchItem;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class CompanionsForgePlatform implements CompanionsPlatform {

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return Companions.ITEMS.register(id, item);
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String id, BlockBehaviour.Properties properties, CompanionsBlocks.BlockType blockType) {
        RegistryObject<T> tr = switch (blockType) {
            case SOUL_FURNACE -> (RegistryObject<T>) Companions.BLOCKS.register(id, () -> new SoulFurnaceBlock(properties.lightLevel((v) -> v.getValue(SoulFurnaceBlock.LIT) ? 13 : 0)));
            case CROISSANT_EGG -> (RegistryObject<T>) Companions.BLOCKS.register(id, () -> new CroissantEggBlock(properties));
            case PLASMA_LAMP -> (RegistryObject<T>) Companions.BLOCKS.register(id, () -> new PlasmaLampBlock(properties.lightLevel((v) -> v.getValue(PlasmaLampBlock.LIT) ? 15 : 0)));
            case VOLTAIC_PILLAR -> (RegistryObject<T>) Companions.BLOCKS.register(id, () -> new VoltaicPillarBlock(properties));
            case EMPTY_PUPPET -> (RegistryObject<T>) Companions.BLOCKS.register(id, () -> new EmptyPuppetBlock(properties));
            case RESPAWN_TOTEM -> (RegistryObject<T>) Companions.BLOCKS.register(id, () -> new RespawnTotemBlock(properties));
            default -> // TESLA_RECEIVER
                    (RegistryObject<T>) Companions.BLOCKS.register(id, () -> new TeslaCoilBlock(properties));
        };

        registerItem(id, () -> new GenericBlockItem(tr.get(), new Item.Properties(), id));
        return tr;
    }

    @Override
    public <T extends Item> Supplier<T> registerArmorItem(String id, ArmorMaterial armorMaterial, ArmorItem.Type armorType, boolean isGeckoArmor) {
        if (isGeckoArmor) {
            return (Supplier<T>) registerItem(id, () -> new GenericArmorItem(armorMaterial, armorType, new Item.Properties(), id));
        } else {
            return (Supplier<T>) registerItem(id, () -> new ArmorItem(armorMaterial, armorType, new Item.Properties()));
        }
    }

    @Override
    public <T extends Item> Supplier<T> registerWrenchItem(String id, Item.Properties properties) {
        return (Supplier<T>) registerItem(id, () -> new WrenchItem(properties) {
            @Override
            public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
                pTooltipComponents.add(Component.translatable("tooltip.companions.wrench"));
                super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
            }
        });
    }

    @Override
    public <T extends Item> Supplier<T> registerHourglassItem(String id, Item.Properties properties) {
        return (Supplier<T>) registerItem(id, () -> new HourglassItem(properties));
    }

    @Override
    public <T extends Item> Supplier<T> registerMagicBook(String id, Item.Properties properties, CompanionsItems.MagicType magicType) {
        switch (magicType) {
            case ICE_TORNADO -> {
                return (Supplier<T>) registerItem(id, () -> new IceTornadoBook(properties));
            }
            case FIRE_MARK -> {
                return (Supplier<T>) registerItem(id, () -> new FireMarkBook(properties));
            }
            case STONE_SPIKES -> {
                return (Supplier<T>) registerItem(id, () -> new StoneSpikesBook(properties));
            }
            case HEAL_RING -> {
                return (Supplier<T>) registerItem(id, () -> new HealRingBook(properties));
            }
            case BRACE -> {
                return (Supplier<T>) registerItem(id, () -> new BraceBook(properties));
            }
            case MAGIC_RAY -> {
                return (Supplier<T>) registerItem(id, () -> new MagicRayBook(properties));
            }
            case BLACK_HOLE -> {
                return (Supplier<T>) registerItem(id, () -> new BlackHoleBook(properties));
            }
            default -> { // ICE_SHARDS
                return (Supplier<T>) registerItem(id, () -> new IceShardBook(properties));
            }
        }
    }

    @Override
    public <T extends MobEffect> Supplier<T> registerEffect(String id, Supplier<T> effect) {
        return Companions.MOB_EFFECTS.register(id, effect);
    }

    @Override
    public <T extends ParticleType<?>> Supplier<T> registerParticle(String id, boolean overrideLimiter) {
        return Companions.PARTICLES.register(id, () -> (T) new SimpleParticleType(overrideLimiter));
    }

    @Override
    public <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String id, Supplier<T> tab) {
        return Companions.CREATIVE_TABS.register(id, tab);
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return CreativeModeTab.builder();
    }

    @Override
    public <T extends Raider> EntityType<T> getIllagerGolemEntity() {
        return (EntityType<T>) CompanionsEntities.ILLAGER_GOLEM.get();
    }

    @Override
    public <T extends Entity> EntityType<T> getFireMarkProjectile() {
        return (EntityType<T>) CompanionsEntities.FIRE_MARK_PROJECTILE.get();
    }

}
