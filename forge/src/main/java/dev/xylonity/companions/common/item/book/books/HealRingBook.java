package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.registry.CompanionsEntities;
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
    public String getName() {
        return "fire_mark";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {

        Projectile healRing = CompanionsEntities.HEAL_RING_PROJECTILE.get().create(pLevel);
        if (healRing != null) {
            healRing.moveTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
            healRing.setOwner(pPlayer);
            pLevel.addFreshEntity(healRing);
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}