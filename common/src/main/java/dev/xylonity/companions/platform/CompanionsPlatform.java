package dev.xylonity.companions.platform;

import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.material.ItemMaterials;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public interface CompanionsPlatform {

    <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item);
    <T extends Item> Supplier<T> registerSpecificItem(String id, Item.Properties properties, CompanionsItems.ItemType itemType);
    <T extends Item> Supplier<T> registerSpecificItem(String id, Item.Properties properties, CompanionsItems.ItemType itemType, ItemMaterials material, float extraDamage, float extraSpeed);
    <T extends Block> Supplier<T> registerBlock(String id, BlockBehaviour.Properties properties, CompanionsBlocks.BlockType blockType, CompanionsBlocks.BlockItem blockItem);
    <T extends Item> Supplier<T> registerArmorItem(String id, ArmorMaterials armorMaterial, ArmorItem.Type armorType, boolean isGeckoArmor);
    <T extends Item> Supplier<T> registerMagicBook(String id, Item.Properties properties, CompanionsItems.MagicType magicType);
    <T extends MobEffect> Supplier<T> registerEffect(String id, Supplier<T> item);
    <T extends ParticleType<?>> Supplier<T> registerParticle(String id, boolean overrideLimiter);
    <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound);
    <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String id, Supplier<T> tab);

    CreativeModeTab.Builder creativeTabBuilder();
    <T extends Raider> EntityType<T> getIllagerGolemEntity();
    <T extends Entity> EntityType<T> getFireMarkProjectile();

    //void makeWrenchInteraction(ItemStack stack, Player player, LivingEntity target, InteractionHand hand, AtomicReference<UUID> uuid);

}
