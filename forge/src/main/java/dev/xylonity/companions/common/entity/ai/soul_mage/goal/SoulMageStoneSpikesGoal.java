package dev.xylonity.companions.common.entity.ai.soul_mage.goal;

import dev.xylonity.companions.common.entity.ai.soul_mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.custom.SoulMageEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.knightlib.common.api.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class SoulMageStoneSpikesGoal extends AbstractSoulMageAttackGoal {

    public SoulMageStoneSpikesGoal(SoulMageEntity soulMage, int minCd, int maxCd) {
        super(soulMage, minCd, maxCd, "STONE_SPIKES");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (!soulMage.level().isClientSide) {
            final double BASE_DISTANCE = 1.5;
            final double DISTANCE_INCREMENT = 1.5;
            final double HORIZONTAL_OFFSET_DEG = 0.0;
            final double VERTICAL_OFFSET_DEG = 0.0;
            final int NUM_SPIKES = 10;
            final int MAX_DOWN = 6;
            final int MAX_UP = 3;
            final int DELAY_INCREMENT = 2;

            Vec3 directionToTarget = new Vec3(target.getX() - soulMage.getX(), target.getY() - soulMage.getY(), target.getZ() - soulMage.getZ()).normalize();

            double offsetRad = Math.toRadians(HORIZONTAL_OFFSET_DEG);
            Vec3 horizontalDir = new Vec3(directionToTarget.x, 0, directionToTarget.z).normalize();
            double modX = horizontalDir.x * Math.cos(offsetRad) - horizontalDir.z * Math.sin(offsetRad);
            double modZ = horizontalDir.x * Math.sin(offsetRad) + horizontalDir.z * Math.cos(offsetRad);
            Vec3 adjustedHorizontal = new Vec3(modX, 0, modZ).normalize();

            double verticalRad = Math.toRadians(VERTICAL_OFFSET_DEG);
            double finalXComp = adjustedHorizontal.x * Math.cos(verticalRad);
            double finalYComp = Math.sin(verticalRad);
            double finalZComp = adjustedHorizontal.z * Math.cos(verticalRad);
            Vec3 finalDir = new Vec3(finalXComp, finalYComp, finalZComp).normalize();

            for (int i = 0; i < NUM_SPIKES; i++) {
                double distance = BASE_DISTANCE + i * DISTANCE_INCREMENT;
                double desiredX = soulMage.getX() + finalDir.x * distance;
                double desiredZ = soulMage.getZ() + finalDir.z * distance;
                int hmX = (int) Math.floor(desiredX);
                int hmZ = (int) Math.floor(desiredZ);
                int terrainY = soulMage.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(hmX, 0, hmZ)).getY();

                BlockPos basePos = new BlockPos(hmX, terrainY, hmZ);
                BlockPos spawnPos = findSpawnPos(basePos, soulMage.level(), MAX_DOWN, MAX_UP);
                if (spawnPos == null) {
                    spawnPos = basePos;
                }

                double finalX = desiredX;
                double finalY = spawnPos.getY();
                double finalZ = desiredZ;

                Projectile spike = CompanionsEntities.STONE_SPIKE_PROJECTILE.get().create(soulMage.level());
                if (spike != null) {
                    spike.moveTo(finalX, finalY, finalZ, soulMage.getYRot(), 0.0F);
                    spike.setOwner(soulMage);

                    int delay = i * DELAY_INCREMENT;
                    if (i != 0) {
                        TickScheduler.scheduleBoth(soulMage.level(), () -> soulMage.level().addFreshEntity(spike), delay);
                    } else {
                        soulMage.level().addFreshEntity(spike);
                    }
                }
            }
        }
    }

    @Override
    protected boolean hasRequiredBook() {
        for (int i = 0; i < soulMage.inventory.getContainerSize(); i++) {
            ItemStack stack = soulMage.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.BOOK_STONE_SPIKES.get()) {
                return true;
            }
        }

        return false;
    }

    private BlockPos findSpawnPos(BlockPos pos, Level level, int maxDown, int maxUp) {
        if (isValidSpawnPos(pos, level)) return pos;

        for (int d = 1; d <= maxDown; d++) {
            BlockPos cand = pos.below(d);
            if (isValidSpawnPos(cand, level)) return cand;
        }

        for (int u = 1; u <= maxUp; u++) {
            BlockPos cand = pos.above(u);
            if (isValidSpawnPos(cand, level)) return cand;
        }

        return null;
    }

    private boolean isValidSpawnPos(BlockPos pos, Level level) {
        if (!level.isInWorldBounds(pos)) return false;
        if (!level.getBlockState(pos).isAir()) return false;
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }
}