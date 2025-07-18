package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HealRingBook extends AbstractMagicBook {

    public HealRingBook(Properties properties) {
        super(properties);
    }

    @Override
    protected String tooltipName() {
        return "heal_ring_book";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {

        if (!pLevel.isClientSide) {
            Projectile healRing = CompanionsEntities.HEAL_RING_PROJECTILE.get().create(pLevel);
            if (healRing != null) {
                healRing.moveTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
                healRing.setOwner(pPlayer);
                pLevel.addFreshEntity(healRing);
            }

            pPlayer.getCooldowns().addCooldown(this, (int)(CompanionsConfig.HEAL_RING_COOLDOWN * (1 - (Util.hasFullSetOn(pPlayer, ArmorMaterials.MAGE) * CompanionsConfig.MAGE_SET_COOLDOWN_REDUCTION))));
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    protected void playSound(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), CompanionsSounds.SPELL_RELEASE_HEAL.get(), player.getSoundSource(), 1F, 1.0F);
    }

}