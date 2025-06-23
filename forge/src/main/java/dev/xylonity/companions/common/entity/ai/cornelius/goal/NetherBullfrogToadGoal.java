package dev.xylonity.companions.common.entity.ai.cornelius.goal;

import dev.xylonity.companions.common.entity.ai.cornelius.AbstractCorneliusAttackGoal;
import dev.xylonity.companions.common.entity.custom.CorneliusEntity;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import dev.xylonity.companions.common.entity.summon.NetherBullfrogEntity;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class NetherBullfrogToadGoal extends AbstractCorneliusAttackGoal {

    public NetherBullfrogToadGoal(CorneliusEntity cornelius, int minCd, int maxCd) {
        super(cornelius, minCd, maxCd);
    }

    @Override
    protected void performAttack(LivingEntity target) {
        BlockPos spawnPos = findPosAroundCornelius();
        NetherBullfrogEntity bullfrog = CompanionsEntities.NETHER_BULLFROG.get().create(cornelius.level());
        if (bullfrog != null) {
            bullfrog.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

            if (target != null) bullfrog.setTarget(target);

            bullfrog.setOwnerUUID(cornelius.getUUID());

            cornelius.level().addFreshEntity(bullfrog);
        }
    }

    @Override
    protected Item coin() {
        return CompanionsBlocks.NETHER_COIN.get().asItem();
    }

    @Override
    protected int coinsToConsume() {
        return 1;
    }

}