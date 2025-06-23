package dev.xylonity.companions.common.entity.ai.cornelius.goal;

import dev.xylonity.companions.common.entity.ai.cornelius.AbstractCorneliusAttackGoal;
import dev.xylonity.companions.common.entity.custom.CorneliusEntity;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class CorneliusFireworkToadGoal extends AbstractCorneliusAttackGoal {

    public CorneliusFireworkToadGoal(CorneliusEntity cornelius, int minCd, int maxCd) {
        super(cornelius, minCd, maxCd);
    }

    @Override
    protected void performAttack(LivingEntity target) {
        BlockPos spawnPos = findPosAroundCornelius();
        FireworkToadEntity toad = CompanionsEntities.FIREWORK_TOAD.get().create(cornelius.level());
        if (toad != null) {
            toad.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

            if (target != null) toad.setTarget(target);
            toad.setOwnerUUID(cornelius.getOwnerUUID());

            cornelius.level().addFreshEntity(toad);
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