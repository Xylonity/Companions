package dev.xylonity.companions.common.entity.ai.cornelius;

import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public abstract class AbstractCorneliusAttackGoal extends Goal {
    protected final CorneliusEntity cornelius;
    private final int minCooldown, maxCooldown;
    protected int attackTicks;
    protected int nextUseTick;
    protected boolean started;

    private int currentAttackDelay;
    private int currentAttackDuration;

    private static final int SPAWN_RADIUS = 3;
    private static final int MAX_SPAWN_ATTEMPTS = 20;

    public AbstractCorneliusAttackGoal(CorneliusEntity cornelius, int minCd, int maxCd) {
        this.cornelius = cornelius;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.nextUseTick = -1;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!cornelius.canAttack()) return false;
        if (cornelius.getAttackType() != 0) return false;
        if (cornelius.getTarget() == null) return false;

        if (!hasSufficientCoins()) {
            nextUseTick = -1;
            return false;
        }

        if (nextUseTick < 0) {
            int cd = minCooldown + cornelius.getRandom().nextInt(maxCooldown - minCooldown + 1);
            nextUseTick = cornelius.tickCount + cd;
            return false;
        }

        return cornelius.tickCount >= nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return started && attackTicks < currentAttackDuration;
    }

    @Override
    public void start() {
        int currentAttackType = cornelius.getRandom().nextInt(3) + 1;
        switch (currentAttackType) {
            case 1 -> {
                currentAttackDelay = 15;
                currentAttackDuration = 30;
            }
            case 2 -> {
                currentAttackDelay = 17;
                currentAttackDuration = 25;
            }
            default -> {
                currentAttackDelay = 18;
                currentAttackDuration = 26;
            }
        }

        attackTicks = 0;
        started = true;
        cornelius.setAttackType(currentAttackType);
        cornelius.setSummonedCount(cornelius.getSummonedCount() + 1);
        consumeCoins();
    }

    @Override
    public void stop() {
        started = false;
        cornelius.setAttackType(0);
        nextUseTick = -1;
    }

    @Override
    public void tick() {
        LivingEntity target = cornelius.getTarget();
        if (target != null) {
            cornelius.getLookControl().setLookAt(target, 30F, 30F);
        }

        if (attackTicks == currentAttackDelay && target != null && target.isAlive()) {
            performAttack(target);
        }

        attackTicks++;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected boolean hasSufficientCoins() {
        int found = 0;
        for (int i = 0; i <= 2; i++) {
            ItemStack stack = cornelius.inventory.getItem(i);
            if (stack.getItem() == coin()) {
                found += stack.getCount();
                if (found >= coinsToConsume()) return true;
            }
        }

        return false;
    }

    protected void consumeCoins() {
        int toConsume = coinsToConsume();
        for (int i = 0; i <= 2 && toConsume > 0; i++) {
            ItemStack stack = cornelius.inventory.getItem(i);
            if (stack.getItem() == coin()) {
                int remove = Math.min(toConsume, stack.getCount());
                stack.shrink(remove);
                toConsume -= remove;
                cornelius.inventory.setItem(i, stack);
            }
        }

    }

    protected boolean isValidSpawnPosition(Level level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        return !level.getBlockState(pos).isSolidRender(level, pos)
                && !level.getBlockState(abovePos).isSolidRender(level, abovePos)
                && level.getBlockState(pos.below()).isSolidRender(level, pos.below());
    }

    protected BlockPos findPosAroundCornelius() {
        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {
            int x = cornelius.blockPosition().getX() + cornelius.getRandom().nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS;
            int z = cornelius.blockPosition().getZ() + cornelius.getRandom().nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS;

            BlockPos surf = findSurfacePosition(cornelius.level(), new BlockPos(x, cornelius.blockPosition().getY(), z));
            if (surf != null && isValidSpawnPosition(cornelius.level(), surf)) {
                return surf;
            }
        }

        return cornelius.blockPosition();
    }

    protected BlockPos findPosAroundTarget() {
        if (cornelius.getTarget() != null) {
            for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS * 2; attempt++) {
                int x = cornelius.getTarget().blockPosition().getX() + cornelius.getTarget().getRandom().nextInt((SPAWN_RADIUS + 4) * 2 + 1) - (SPAWN_RADIUS + 4);
                int z = cornelius.getTarget().blockPosition().getZ() + cornelius.getTarget().getRandom().nextInt((SPAWN_RADIUS + 4) * 2 + 1) - (SPAWN_RADIUS + 4);

                BlockPos surf = findSurfacePosition(cornelius.getTarget().level(), new BlockPos(x, cornelius.getTarget().blockPosition().getY(), z));
                if (surf != null && isValidSpawnPosition(cornelius.getTarget().level(), surf)) {
                    return surf;
                }
            }

            return cornelius.getTarget().blockPosition();
        }

        return cornelius.blockPosition();
    }

    private BlockPos findSurfacePosition(Level level, BlockPos startPos) {
        for (int y = startPos.getY(); y > level.getMinBuildHeight(); y--) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            BlockPos abovePos = checkPos.above();

            if (level.getBlockState(checkPos).isSolidRender(level, checkPos) && !level.getBlockState(abovePos).isSolidRender(level, abovePos)) {
                return abovePos;
            }
        }

        for (int y = startPos.getY(); y < level.getMaxBuildHeight(); y++) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            BlockPos abovePos = checkPos.above();

            if (level.getBlockState(checkPos).isSolidRender(level, checkPos) &&
                    !level.getBlockState(abovePos).isSolidRender(level, abovePos)) {
                return abovePos;
            }
        }

        return null;
    }

    protected abstract void performAttack(LivingEntity target);
    protected abstract Item coin();
    protected abstract int coinsToConsume();

}