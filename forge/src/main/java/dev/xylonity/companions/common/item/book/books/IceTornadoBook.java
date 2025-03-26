package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.TornadoProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class IceTornadoBook extends AbstractMagicBook {

    public IceTornadoBook(Properties properties) {
        super(properties);
    }

    @Override
    public String getName() {
        return "ice_tornado";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {

        TornadoProjectile tornadoProjectile = CompanionsEntities.TORNADO_PROJECTILE.get().create(pLevel);

        if (tornadoProjectile != null) {
            tornadoProjectile.moveTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
            tornadoProjectile.setOwner(pPlayer);
            pLevel.addFreshEntity(tornadoProjectile);
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

}