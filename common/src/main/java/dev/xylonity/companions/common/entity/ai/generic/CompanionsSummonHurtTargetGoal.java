package dev.xylonity.companions.common.entity.ai.generic;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class CompanionsSummonHurtTargetGoal extends TargetGoal {
    private final CompanionSummonEntity tameSummon;
    private LivingEntity targetEntity;
    private int lastCompanionHurtTimestamp;

    public CompanionsSummonHurtTargetGoal(CompanionSummonEntity summon) {
        super(summon, false);
        this.tameSummon = summon;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!this.tameSummon.isTame() || this.tameSummon.isOrderedToSit()) {
            return false;
        }

        LivingEntity owner1 = tameSummon.getOwner();
        LivingEntity owner2 = tameSummon.getSecondOwner();
        LivingEntity companion = getCompanionOwner();

        LivingEntity newTarget = null;
        int bestTimestamp = -1;

        if (owner1 != null) {
            LivingEntity by = owner1.getLastHurtByMob();
            int tsBy = owner1.getLastHurtByMobTimestamp();
            if (by != null && tsBy > bestTimestamp) {
                newTarget = by;
                bestTimestamp = tsBy;
            }

            LivingEntity victim = owner1.getLastHurtMob();
            int v = owner1.getLastHurtMobTimestamp();
            if (victim != null && v > bestTimestamp) {
                newTarget = victim;
                bestTimestamp = v;
            }

        }

        if (owner2 != null) {
            LivingEntity by = owner2.getLastHurtByMob();
            int tsBy = owner2.getLastHurtByMobTimestamp();
            if (by != null && tsBy > bestTimestamp) {
                newTarget = by;
                bestTimestamp = tsBy;
            }

            LivingEntity victim = owner2.getLastHurtMob();
            if (victim != null && owner2.getLastHurtMobTimestamp() > bestTimestamp) {
                newTarget = victim;
            }

        }

        if (newTarget == null && companion != null) {
            int tsComp = companion.getLastHurtMobTimestamp();
            LivingEntity compVictim = companion.getLastHurtMob();
            if (compVictim != null && tsComp != this.lastCompanionHurtTimestamp) {
                newTarget = compVictim;
                this.lastCompanionHurtTimestamp = tsComp;
            }
        }

        if (newTarget == null) return false;

        if (Util.areEntitiesLinked(this.tameSummon, newTarget)) return false;

        if (!this.canAttack(newTarget, TargetingConditions.DEFAULT)) return false;

        Player owner = null;
        if (owner1 instanceof Player) {
            owner = (Player) owner1;
        } else if (owner2 instanceof Player) {
            owner = (Player) owner2;
        }

        if (owner == null) return false;

        if (!this.tameSummon.wantsToAttack(newTarget, owner)) return false;

        this.targetEntity = newTarget;

        return true;
    }


    @Override
    public void start() {
        this.mob.setTarget(this.targetEntity);
        super.start();
    }

    private LivingEntity getCompanionOwner() {
        LivingEntity owner1 = this.tameSummon.getOwner();
        LivingEntity owner2 = this.tameSummon.getSecondOwner();

        if (owner1 != null && !(owner1 instanceof Player)) {
            return owner1;
        }

        if (owner2 != null && !(owner2 instanceof Player)) {
            return owner2;
        }

        return null;
    }

}