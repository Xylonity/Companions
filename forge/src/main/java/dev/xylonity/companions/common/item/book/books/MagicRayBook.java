package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.MagicRayCircleProjectile;
import dev.xylonity.companions.common.entity.projectile.MagicRayPieceProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.common.tick.TickScheduler;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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
            double maxDistance = 30.0D;
            Vec3 eyePos = pPlayer.getEyePosition(1.0F);
            Vec3 lookVec = pPlayer.getLookAngle();

            HitResult hitResult = pPlayer.pick(maxDistance, 1.0F, false);
            Vec3 targetPos = (hitResult.getType() == HitResult.Type.BLOCK)
                    ? hitResult.getLocation()
                    : eyePos.add(lookVec.scale(maxDistance));

            double offset = 1D;
            Vec3 spawnPos = eyePos.add(lookVec.scale(offset));

            double finalDistance = maxDistance;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                finalDistance = hitResult.getLocation().distanceTo(spawnPos);
            }

            double separation = 1.0D;

            int totalPieces = (int) (finalDistance / separation);

            double traveled = 0.0;
            double increment   = 1.0D;
            for (int i = 0; i < (int)(maxDistance / increment); i++) {
                Vec3 piecePos = spawnPos.add(lookVec.scale(traveled));
                traveled += increment;

                BlockPos blockPos = BlockPos.containing(piecePos);
                if (!isPassableBlock(pLevel, blockPos)) {
                    spawnRayPiece(pLevel, pPlayer, piecePos, lookVec, (i == 0));
                    break;
                }

                spawnRayPiece(pLevel, pPlayer, piecePos, lookVec, (i == 0));
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
                setProjectileRotation(circle, lookVec);
                pLevel.addFreshEntity(circle);
            }
        } else {
            MagicRayPieceProjectile rayPiece = CompanionsEntities.MAGIC_RAY_PIECE_PROJECTILE.get().create(pLevel);
            if (rayPiece != null) {
                rayPiece.setPos(piecePos.x, piecePos.y, piecePos.z);
                setProjectileRotation(rayPiece, lookVec);
                if (pLevel instanceof ServerLevel serverLevel) {
                    TickScheduler.scheduleServer(serverLevel, () -> pLevel.addFreshEntity(rayPiece), 3);
                }
            }
        }
    }

    private void setProjectileRotation(MagicRayPieceProjectile projectile, Vec3 direction) {
        float yaw = (float) (Math.atan2(direction.z, direction.x) * (180.0F / Math.PI)) - 90.0F;
        float pitch = (float) (-(Math.atan2(direction.y,
                Math.sqrt(direction.x * direction.x + direction.z * direction.z)))
                * (180.0F / Math.PI));

        projectile.setPitch(pitch);
        projectile.setYaw(yaw);
    }

}
