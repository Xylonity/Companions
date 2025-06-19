package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.knightlib.common.api.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class StoneSpikesBook extends AbstractMagicBook {

    public StoneSpikesBook(Properties properties) {
        super(properties);
    }

    @Override
    public String getName() {
        return "stone_spikes";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        final double BASE_DISTANCE = 1.5;
        final double DISTANCE_INCREMENT = 1.5;
        final double HORIZONTAL_OFFSET_DEG = 0.0;
        final double VERTICAL_OFFSET_DEG = 0.0;
        final int NUM_SPIKES = 10;
        final int MAX_DOWN = 6;
        final int MAX_UP = 3;
        final int DELAY_INCREMENT = 2;

        Vec3 look = player.getLookAngle().normalize();
        Vec3 horizontalDir = new Vec3(look.x, 0, look.z).normalize();

        double offsetRad = Math.toRadians(HORIZONTAL_OFFSET_DEG);
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

            double desiredX = player.getX() + finalDir.x * distance;
            double desiredZ = player.getZ() + finalDir.z * distance;
            int hmX = (int) Math.floor(desiredX);
            int hmZ = (int) Math.floor(desiredZ);
            int terrainY = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(hmX, 0, hmZ)).getY();

            BlockPos basePos = new BlockPos(hmX, terrainY, hmZ);
            BlockPos spawnPos = findSpawnPos(basePos, level, MAX_DOWN, MAX_UP);

            if (spawnPos == null) spawnPos = basePos;

            double finalY = spawnPos.getY();

            Projectile spike = CompanionsEntities.STONE_SPIKE_PROJECTILE.get().create(level);
            if (spike != null) {
                spike.moveTo(desiredX, finalY, desiredZ, player.getYRot(), 0.0F);
                spike.setOwner(player);

                int delay = i * DELAY_INCREMENT;
                if ( i != 0) {
                    TickScheduler.scheduleBoth(level, () -> level.addFreshEntity(spike), delay);
                } else {
                    level.addFreshEntity(spike);
                }
            }
        }

        return super.use(level, player, usedHand);
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
