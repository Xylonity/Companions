package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.BigIceShardProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class IceShardBook extends AbstractMagicBook {

    public IceShardBook(Properties properties) {
        super(properties);
    }

    @Override
    public String getName() {
        return "ice_shard";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {

        BigIceShardProjectile bigCrystalProjectile = CompanionsEntities.BIG_ICE_SHARD_PROJECTILE.get().create(pLevel);

        if (bigCrystalProjectile != null) {
            bigCrystalProjectile.moveTo(pPlayer.getX(), pPlayer.getY() + 6, pPlayer.getZ());
            bigCrystalProjectile.setOwner(pPlayer);
            pLevel.addFreshEntity(bigCrystalProjectile);
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}