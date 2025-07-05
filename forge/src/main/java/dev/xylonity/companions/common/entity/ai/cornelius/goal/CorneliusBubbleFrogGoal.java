package dev.xylonity.companions.common.entity.ai.cornelius.goal;

import dev.xylonity.companions.common.entity.ai.cornelius.AbstractCorneliusAttackGoal;
import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import dev.xylonity.companions.common.entity.projectile.ScrollProjectile;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class CorneliusBubbleFrogGoal extends AbstractCorneliusAttackGoal {

    public CorneliusBubbleFrogGoal(CorneliusEntity cornelius, int minCd, int maxCd) {
        super(cornelius, minCd, maxCd);
    }

    @Override
    protected void performAttack(LivingEntity target) {
        BlockPos spawnPos = findPosAroundCornelius();
        ScrollProjectile scroll = CompanionsEntities.SCROLL.get().create(cornelius.level());
        if (scroll != null) {
            scroll.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

            scroll.setOwnerUUID(cornelius.getUUID());
            scroll.setSecondOwnerUUID(cornelius.getOwnerUUID());
            scroll.setEntityToSpawn(5);

            cornelius.level().addFreshEntity(scroll);
        }
    }

    @Override
    protected Item coin() {
        return CompanionsBlocks.COPPER_COIN.get().asItem();
    }

    @Override
    protected int coinsToConsume() {
        return 1;
    }

}