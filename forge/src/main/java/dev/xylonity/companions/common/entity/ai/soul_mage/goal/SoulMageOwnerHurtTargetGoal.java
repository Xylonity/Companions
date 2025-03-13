package dev.xylonity.companions.common.entity.ai.soul_mage.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class SoulMageOwnerHurtTargetGoal extends TargetGoal {
    private final TamableAnimal tameAnimal;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public SoulMageOwnerHurtTargetGoal(TamableAnimal pTameAnimal) {
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

            if (this.ownerLastHurt != null && sharesOwnerChain(this.tameAnimal, this.ownerLastHurt)) {
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

    private boolean sharesOwnerChain(LivingEntity entity1, LivingEntity entity2) {
        LivingEntity ultimateOwner1 = getUltimateOwner(entity1);
        LivingEntity ultimateOwner2 = getUltimateOwner(entity2);
        return ultimateOwner1 != null && ultimateOwner1.equals(ultimateOwner2);
    }

    private LivingEntity getUltimateOwner(LivingEntity entity) {
        if (entity instanceof TamableAnimal) {
            TamableAnimal tame = (TamableAnimal) entity;
            LivingEntity owner = tame.getOwner();
            if (owner != null) {
                return getUltimateOwner(owner);
            }
        }

        return entity;
    }

}
