package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.BlackHoleProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BlackHoleBook extends AbstractMagicBook {

    public BlackHoleBook(Properties properties) {
        super(properties);
    }

    @Override
    public String getName() {
        return "black_hole";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide) {
            BlackHoleProjectile blackHole = CompanionsEntities.BLACK_HOLE_PROJECTILE.get().create(level);
            if (blackHole != null) {
                Vec3 look = player.getLookAngle();
                Vec3 spawnAt = player.getEyePosition(1.0F)
                        .add(look.scale(0.5D))
                        .add(0, 0.0D, 0);

                blackHole.setPos(spawnAt.x, spawnAt.y, spawnAt.z);

                blackHole.setOwner(player);

                float velocity = 0.80F;
                blackHole.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 0.0F);

                level.addFreshEntity(blackHole);
            }
        }

        return super.use(level, player, hand);
    }



}
