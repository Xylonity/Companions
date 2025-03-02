package dev.xylonity.companions.platform;

import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public interface CompanionsPlatform {

    <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item);
    <T extends Item> Supplier<T> registerArmorItem(String id, ArmorMaterial armorMaterial, ArmorItem.Type armorType, boolean isGeckoArmor);
    <T extends Item> Supplier<T> registerWrenchItem(String id, Item.Properties properties);
    <T extends Item> Supplier<T> registerMagicBook(String id, Item.Properties properties, CompanionsItems.MagicType magicType);
    <T extends MobEffect> Supplier<T> registerEffect(String id, Supplier<T> item);
    <T extends ParticleType<?>> Supplier<T> registerParticle(String id, boolean overrideLimiter);
    <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String id, Supplier<T> tab);

    CreativeModeTab.Builder creativeTabBuilder();
    <T extends Raider> EntityType<T> getIllagerGolemEntity();
    <T extends Entity> EntityType<T> getFireMarkProjectile();

    //void makeWrenchInteraction(ItemStack stack, Player player, LivingEntity target, InteractionHand hand, AtomicReference<UUID> uuid);

}
