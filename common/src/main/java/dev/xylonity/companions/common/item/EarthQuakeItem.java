package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.tick.TickScheduler;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class EarthQuakeItem extends Item {

    public EarthQuakeItem(Properties $$0) {
        super($$0);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide()) doEarthquake((ServerLevel) level, player.blockPosition());

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    // Using Manhattan Distance to calculate the closest path in the grid to avoid square-like shapes
    private void doEarthquake(ServerLevel serverLevel, BlockPos center) {
        int radius = 2;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int dist = Math.abs(x) + Math.abs(z);

                if (dist >= 1 && dist <= radius) {
                    BlockPos blockPos = center.offset(x, -1, z);
                    BlockState state = serverLevel.getBlockState(blockPos);

                    if (!state.isAir() && state.getBlock() != Blocks.BEDROCK) {
                        if (dist == 1) {
                            spawnFallingBlock(serverLevel, blockPos, state, 0.23);
                        } else {
                            TickScheduler.schedule(serverLevel, () -> spawnFallingBlock(serverLevel, blockPos, state, 0.18), 4);
                        }
                    }
                }
            }
        }

    }

    private void spawnFallingBlock(ServerLevel level, BlockPos pos, BlockState state, double yDelay) {
        FallingBlockEntity fallingBlock = new FallingBlockEntity(
                level,
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D,
                state
        );

        fallingBlock.setDeltaMovement(0.0, yDelay, 0.0);

        level.addFreshEntity(fallingBlock);
        level.removeBlock(pos, false);
    }

}
