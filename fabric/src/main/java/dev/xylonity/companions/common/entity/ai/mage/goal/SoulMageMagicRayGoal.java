package dev.xylonity.companions.common.entity.ai.mage.goal;

import dev.xylonity.companions.common.entity.ai.mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.companions.common.entity.projectile.MagicRayCircleProjectile;
import dev.xylonity.companions.common.entity.projectile.MagicRayPieceProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.knightlib.api.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoulMageMagicRayGoal extends AbstractSoulMageAttackGoal {

    public SoulMageMagicRayGoal(SoulMageEntity soulMage, int minCd, int maxCd) {
        super(soulMage, minCd, maxCd, "MAGIC_RAY");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        Vec3 baseDir = target.position().subtract(soulMage.position()).normalize();

        double traveled = 0d;
        for (int i = 0; i < 30; i++) {
            Vec3 piecePos = soulMage.getEyePosition(1f).add(baseDir).add(baseDir.scale(traveled));
            traveled += 1;

            BlockPos blockPos = BlockPos.containing(piecePos);
            if (!isPassableBlock(soulMage.level(), blockPos)) {
                spawnRayPiece(soulMage.level(), soulMage, piecePos, baseDir, (i == 0));
                break;
            }

            spawnRayPiece(soulMage.level(), soulMage, piecePos, baseDir, (i == 0));
        }
    }

    private boolean isPassableBlock(Level level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

    private void spawnRayPiece(Level pLevel, SoulMageEntity pPlayer, Vec3 piecePos, Vec3 lookVec, boolean isFirstPiece) {
        if (isFirstPiece) {
            MagicRayCircleProjectile circle = CompanionsEntities.MAGIC_RAY_PIECE_CIRCLE_PROJECTILE.create(pLevel);
            if (circle != null) {
                circle.setPos(piecePos.x, piecePos.y, piecePos.z);
                circle.setOwner(pPlayer);
                rotateProjectile(circle, lookVec);
                pLevel.addFreshEntity(circle);
            }
        } else {
            MagicRayPieceProjectile ray = CompanionsEntities.MAGIC_RAY_PIECE_PROJECTILE.create(pLevel);
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

    @Override
    protected boolean hasRequiredBook() {
        for (int i = 0; i < soulMage.inventory.getContainerSize(); i++) {
            ItemStack stack = soulMage.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.BOOK_MAGIC_RAY.get()) {
                return true;
            }
        }

        return false;
    }

}