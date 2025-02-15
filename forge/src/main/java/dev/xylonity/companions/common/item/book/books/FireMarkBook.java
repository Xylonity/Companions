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

public class FireMarkBook extends AbstractMagicBook {

    public FireMarkBook(Properties properties) {
        super(properties);
    }

    @Override
    public String getName() {
        return "fire_mark";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {

        Projectile fireMarkRing = CompanionsEntities.FIRE_MARK_RING_PROJECTILE.get().create(pLevel);
        if (fireMarkRing != null) {
            fireMarkRing.moveTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
            fireMarkRing.setOwner(pPlayer);
            pLevel.addFreshEntity(fireMarkRing);
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}