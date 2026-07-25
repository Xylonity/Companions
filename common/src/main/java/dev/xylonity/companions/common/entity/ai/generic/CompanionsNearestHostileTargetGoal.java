package dev.xylonity.companions.common.entity.ai.generic;

import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;

public class CompanionsNearestHostileTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

    private final CompanionEntity companion;

    public CompanionsNearestHostileTargetGoal(CompanionEntity pCompanion) {
        super(pCompanion, LivingEntity.class, 10, true, false, livingEntity -> livingEntity instanceof Enemy
                && !Util.areEntitiesLinked(pCompanion, livingEntity)
                && !Util.areTeammates(pCompanion.getOwner(), livingEntity));
        this.companion = pCompanion;
    }

    @Override
    public boolean canUse() {
        return CompanionsConfig.COMPANIONS_ATTACK_HOSTILES_PROACTIVELY && this.companion.isTame() && !this.companion.isOrderedToSit() && super.canUse();
    }

}