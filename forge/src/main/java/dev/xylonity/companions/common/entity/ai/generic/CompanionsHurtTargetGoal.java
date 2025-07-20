package dev.xylonity.companions.common.entity.ai.generic;

import java.util.EnumSet;

import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class CompanionsHurtTargetGoal extends TargetGoal {
    private final CompanionEntity tameAnimal;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public CompanionsHurtTargetGoal(CompanionEntity pTameAnimal) {
        super(pTameAnimal, false);
        this.tameAnimal = pTameAnimal;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit()) {
            LivingEntity owner = this.tameAnimal.getOwner();
            if (owner == null) {
                return false;
            }

            this.ownerLastHurt = owner.getLastHurtMob();
            int currentTimestamp = owner.getLastHurtMobTimestamp();
            if (currentTimestamp == this.timestamp) {
                return false;
            }

            if (this.ownerLastHurt != null && Util.areEntitiesLinked(this.tameAnimal, this.ownerLastHurt)) {
                return false;
            }

            return this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) &&
                    this.tameAnimal.wantsToAttack(this.ownerLastHurt, owner);
        }

        return false;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity owner = this.tameAnimal.getOwner();

        if (owner != null) {
            this.timestamp = owner.getLastHurtMobTimestamp();
        }

        super.start();
    }

    public LivingEntity getOwnerLastHurt() {
        return this.ownerLastHurt;
    }

}
