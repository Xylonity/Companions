package dev.xylonity.companions.platform;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.block.*;
import dev.xylonity.companions.common.item.*;
import dev.xylonity.companions.common.item.armor.BloodArmorItem;
import dev.xylonity.companions.common.item.armor.GeckoBloodArmorItem;
import dev.xylonity.companions.common.item.armor.GeckoHolyRobeArmorItem;
import dev.xylonity.companions.common.item.armor.GeckoMageArmorItem;
import dev.xylonity.companions.common.item.blockitem.CoinItem;
import dev.xylonity.companions.common.item.blockitem.GenericBlockItem;
import dev.xylonity.companions.common.item.book.books.*;
import dev.xylonity.companions.common.item.generic.GenericGeckoItem;
import dev.xylonity.companions.common.item.weapon.BloodAxeItem;
import dev.xylonity.companions.common.item.weapon.BloodScytheItem;
import dev.xylonity.companions.common.item.weapon.BloodSwordItem;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.material.ItemMaterials;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class CompanionsFabricPlatform implements CompanionsPlatform {

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return registerSupplier(BuiltInRegistries.ITEM, id, item);
    }

    @Override
    public <T extends Item> Supplier<T> registerSpecificItem(String id, Item.Properties properties, CompanionsItems.ItemType itemType) {
        switch (itemType) {
            case HOURGLASS -> {
                return (Supplier<T>) registerItem(id, () -> new HourglassItem(properties));
            }
            case WRENCH -> {
                return (Supplier<T>) registerItem(id, () -> new WrenchItem(properties));
            }
            case CRYSTALLIZED_BLOOD -> {
                return (Supplier<T>) registerItem(id, () -> new CrystallizedBloodItem(properties));
            }
            case NEEDLE -> {
                return (Supplier<T>) registerItem(id, () -> new NeedleItem(properties));
            }
            case GENERIC -> {
                return (Supplier<T>) registerItem(id, () -> new GenericGeckoItem(properties, id));
            }
            default -> // SHADOW_BELL
            {
                return (Supplier<T>) registerItem(id, () -> new ShadowBellItem(properties));
            }
        }
    }

    @Override
    public <T extends Item> Supplier<T> registerSpecificItem(String id, Item.Properties properties, CompanionsItems.ItemType itemType, ItemMaterials material, float extraDamage, float extraSpeed) {
        switch (itemType) {
            case BLOOD_SWORD -> {
                return (Supplier<T>) registerItem(id, () -> new BloodSwordItem(properties, id, material, extra(8, extraDamage), extra(11, extraSpeed)));
            }
            case BLOOD_AXE -> {
                return (Supplier<T>) registerItem(id, () -> new BloodAxeItem(properties, id, material, extra(6, extraDamage), extra(9, extraSpeed)));
            }
            case BLOOD_PICKAXE -> {
                return (Supplier<T>) registerItem(id, () -> new BloodScytheItem(properties, id, material, extra(7, extraDamage), extra(10, extraSpeed)));
            }
        }

        return registerSpecificItem(id, properties, itemType);
    }

    private float extra(int idx, float fallback) {
        String[] parts = CompanionsConfig.CRYSTALLIZED_BLOOD_WEAPON_STATS.trim().split("\\s*,\\s*");
        float ret = fallback;
        if (parts.length >= idx) {
            ret = Float.parseFloat(parts[idx - 1]);
        }

        return ret;
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String id, BlockBehaviour.Properties properties, CompanionsBlocks.BlockType blockType, CompanionsBlocks.BlockItem blockItem) {
        Supplier<T> tr = switch (blockType) {
            case COIN_BLOCK -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new CoinBlock(properties));
            case SOUL_FURNACE -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new SoulFurnaceBlock(properties.lightLevel((v) -> v.getValue(SoulFurnaceBlock.LIT) ? 13 : 0)));
            case CROISSANT_EGG -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new CroissantEggBlock(properties));
            case PLASMA_LAMP -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new PlasmaLampBlock(properties.lightLevel((v) -> v.getValue(PlasmaLampBlock.LIT) ? 15 : 0)));
            case VOLTAIC_PILLAR -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new VoltaicPillarBlock(properties));
            case EMPTY_PUPPET -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new EmptyPuppetBlock(properties));
            case RESPAWN_TOTEM -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new RespawnTotemBlock(properties.lightLevel((v) -> v.getValue(RespawnTotemBlock.LIT) ? 13 : 0)));
            case SHADE_SWORD_ALTAR -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new ShadeSwordAltarBlock(properties));
            case SHADE_MAW_ALTAR -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new ShadeMawAltarBlock(properties));
            case RECALL_PLATFORM -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new RecallPlatformBlock(properties));
            case VOLTAIC_RELAY -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new VoltaicRelayBlock(properties));
            case FROG_BONANZA -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new FrogBonanzaBlock(properties));
            case ETERNAL_FIRE -> (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new EternalFireBlock(properties));
            default -> // TESLA_RECEIVER
                    (Supplier<T>) registerSupplier(BuiltInRegistries.BLOCK, id, () -> new TeslaCoilBlock(properties));
        };

        Supplier<Item> item = switch (blockItem) {
            case COIN -> () -> new CoinItem(tr.get(), new Item.Properties(), id);
            default -> // GENERIC
                    () -> new GenericBlockItem(tr.get(), new Item.Properties(), id);
        };

        registerItem(id, item);

        return tr;
    }

    @Override
    public <T extends Item> Supplier<T> registerArmorItem(String id, ArmorMaterials armorMaterial, ArmorItem.Type armorType, boolean isGeckoArmor) {
        if (isGeckoArmor) {
            return switch (armorMaterial) {
                case CRYSTALLIZED_BLOOD -> (Supplier<T>) registerItem(id, () -> new GeckoBloodArmorItem(armorMaterial, armorType, new Item.Properties().fireResistant(), id));
                case MAGE -> (Supplier<T>) registerItem(id, () -> new GeckoMageArmorItem(armorMaterial, armorType, new Item.Properties(), id));
                case HOLY_ROBE -> (Supplier<T>) registerItem(id, () -> new GeckoHolyRobeArmorItem(armorMaterial, armorType, new Item.Properties().fireResistant(), id));
            };
        } else {
            return switch (armorMaterial) {
                case CRYSTALLIZED_BLOOD -> (Supplier<T>) registerItem(id, () -> new BloodArmorItem(armorMaterial, armorType, new Item.Properties().fireResistant()));
                default -> (Supplier<T>) registerItem(id, () -> new ArmorItem(armorMaterial, armorType, new Item.Properties()));
            };
        }
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
            case NAGINATA -> {
                return (Supplier<T>) registerItem(id, () -> new NaginataBook(properties));
            }
            default -> { // ICE_SHARDS
                return (Supplier<T>) registerItem(id, () -> new IceShardBook(properties));
            }
        }
    }

    @Override
    public <T extends MobEffect> Supplier<T> registerEffect(String id, Supplier<T> item) {
        return registerSupplier(BuiltInRegistries.MOB_EFFECT, id, item);
    }

    @Override
    public <T extends ParticleType<?>> Supplier<T> registerParticle(String id, boolean overrideLimiter) {
        return registerSupplier(BuiltInRegistries.PARTICLE_TYPE, id, () -> (T) FabricParticleTypes.simple());
    }

    @Override
    public <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound) {
        return registerSupplier(BuiltInRegistries.SOUND_EVENT, id, sound);
    }

    @Override
    public <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String id, Supplier<T> tab) {
        return registerSupplier(BuiltInRegistries.CREATIVE_MODE_TAB, id, tab);
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return FabricItemGroup.builder();
    }

    @Override
    public <T extends Raider> EntityType<T> getIllagerGolemEntity() {
        return (EntityType<T>) CompanionsEntities.ILLAGER_GOLEM;
    }

    @Override
    public <T extends Entity> EntityType<T> getFireMarkProjectile() {
        return (EntityType<T>) CompanionsEntities.FIRE_MARK_PROJECTILE;
    }

    private static <T, R extends Registry<? super T>> Supplier<T> registerSupplier(R registry, String id, Supplier<T> factory) {
        T value = factory.get();
        Registry.register((Registry<T>) registry, new ResourceLocation(Companions.MOD_ID, id), value);
        return () -> value;
    }

}
