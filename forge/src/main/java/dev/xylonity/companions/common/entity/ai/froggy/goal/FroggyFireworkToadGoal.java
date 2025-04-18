package dev.xylonity.companions.common.entity.ai.froggy.goal;

import dev.xylonity.companions.common.entity.ai.froggy.AbstractFroggyGoal;
import dev.xylonity.companions.common.entity.ai.soul_mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.custom.FroggyEntity;
import dev.xylonity.companions.common.entity.custom.SoulMageEntity;
import dev.xylonity.companions.common.entity.projectile.TornadoProjectile;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class FroggyFireworkToadGoal extends AbstractFroggyGoal {

    public FroggyFireworkToadGoal(FroggyEntity froggy, int minCd, int maxCd) {
        super(froggy, minCd, maxCd, "FIREWORK_TOAD");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (!froggy.level().isClientSide && froggy.getOwner() != null) {
            FireworkToadEntity fireworkToad = CompanionsEntities.FIREWORK_TOAD.get().create(this.froggy.level());
            if (fireworkToad != null) {

                Vec3 startPos = this.froggy.getEyePosition(1f);
                Vec3 targetPos = target.getEyePosition(1.0F);
                Vec3 direction = targetPos.subtract(startPos).normalize();

                Vec3 spawnPos = startPos.add(direction);

                fireworkToad.moveTo(spawnPos.x, spawnPos.y - 1, spawnPos.z);

                fireworkToad.setTame(true);
                fireworkToad.setOwnerUUID(this.froggy.getUUID());
                fireworkToad.setTarget(this.froggy.getTarget());
                this.froggy.level().addFreshEntity(fireworkToad);
            }
        }
    }

    @Override
    protected boolean hasRequiredCoin() {
        return true;
    }

    @Override
    protected boolean shouldPerformAttack(LivingEntity target) {
        return attackTicks == 6;
    }

}