package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.MagicRayCircleProjectile;
import dev.xylonity.companions.common.entity.projectile.MagicRayPieceProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.knightlib.common.api.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MagicRayBook extends AbstractMagicBook {

    public MagicRayBook(Properties properties) {
        super(properties);
    }

    @Override
    public String getName() {
        return "magic_ray";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            Vec3 dir = pPlayer.getLookAngle();

            double traveled = 0d;
            for (int i = 0; i < 30; i++) {
                Vec3 piecePos = pPlayer.getEyePosition(1f).add(dir).add(dir.scale(traveled));
                traveled += 1;

                BlockPos blockPos = BlockPos.containing(piecePos);
                if (!isPassableBlock(pLevel, blockPos)) {
                    spawnRayPiece(pLevel, pPlayer, piecePos, dir, (i == 0));
                    break;
                }

                spawnRayPiece(pLevel, pPlayer, piecePos, dir, (i == 0));
            }
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private boolean isPassableBlock(Level level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

    private void spawnRayPiece(Level pLevel, Player pPlayer, Vec3 piecePos, Vec3 lookVec, boolean isFirstPiece) {
        if (isFirstPiece) {
            MagicRayCircleProjectile circle = CompanionsEntities.MAGIC_RAY_PIECE_CIRCLE_PROJECTILE.get().create(pLevel);
            if (circle != null) {
                circle.setPos(piecePos.x, piecePos.y, piecePos.z);
                circle.setOwner(pPlayer);
                rotateProjectile(circle, lookVec);
                pLevel.addFreshEntity(circle);
            }
        } else {
            MagicRayPieceProjectile ray = CompanionsEntities.MAGIC_RAY_PIECE_PROJECTILE.get().create(pLevel);
            if (ray != null) {
                ray.setPos(piecePos.x, piecePos.y, piecePos.z);
                rotateProjectile(ray, lookVec);
                ray.setOwner(pPlayer);
                if (pLevel instanceof ServerLevel level) {
                    TickScheduler.scheduleServer(level, () -> pLevel.addFreshEntity(ray), 3);
                }
            }
        }

    }

    private void rotateProjectile(MagicRayPieceProjectile projectile, Vec3 direction) {
        projectile.setPitch((float) (-(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z))) * (180.0F / Math.PI)));
        projectile.setYaw((float) (Math.atan2(direction.z, direction.x) * (180.0F / Math.PI)) - 90.0F);
    }

}