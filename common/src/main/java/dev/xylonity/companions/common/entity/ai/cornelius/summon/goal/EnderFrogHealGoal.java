package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.AbstractCorneliusSummonAttackGoal;
import dev.xylonity.companions.common.entity.projectile.FrogHealProjectile;
import dev.xylonity.companions.common.entity.summon.EnderFrogEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EnderFrogHealGoal extends AbstractCorneliusSummonAttackGoal {

    public EnderFrogHealGoal(CompanionSummonEntity summon, int minCd, int maxCd) {
        super(summon, 30, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        summon.setNoMovement(true);
        summon.playSound(CompanionsSounds.SPELL_RELEASE_HEAL.get());
    }

    @Override
    public boolean canUse() {
        if (summon.getClass() != summonType()) return false;
        if (summon.getAttackType() != 0) return false;
        if (summon.getSecondOwner() != null) {
            if (summon.getOwner() != null) {
                if ((summon.getOwner().getHealth() >= this.summon.getOwner().getMaxHealth() * 0.5f)
                        && (summon.getSecondOwner().getHealth() >= this.summon.getSecondOwner().getMaxHealth() * 0.5f)) return false;
            }
        } else if (summon.getOwner() != null) {
            if ((summon.getOwner().getHealth() >= this.summon.getOwner().getMaxHealth() * 0.5f)) return false;
        }

        if (nextUseTick < 0) {
            nextUseTick = summon.tickCount + minCooldown + summon.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return summon.tickCount >= nextUseTick;
    }

    @Override
    public void stop() {
        super.stop();
        summon.setNoMovement(false);
    }

    @Override
    public void tick() {
        LivingEntity owner = null;
        if (summon.getOwner() != null) {
            if (summon.getOwner().getHealth() < summon.getOwner().getMaxHealth() * 0.5f) {
                owner = summon.getOwner();
            }
        }

        if (summon.getSecondOwner() != null) {
            if (summon.getSecondOwner().getHealth() < summon.getSecondOwner().getMaxHealth() * 0.5f) {
                owner = summon.getSecondOwner();
            }
        }

        if (owner != null) {
            summon.getLookControl().setLookAt(owner, 30F, 30F);
        }

        if (attackTicks == attackDelay() && owner != null && owner.isAlive()) {
            performAttack(owner);
        }

        attackTicks++;
    }

    @Override
    protected int getAttackType() {
        return 1;
    }

    @Override
    protected void performAttack(LivingEntity owner) {
        FrogHealProjectile projectile = CompanionsEntities.FROG_HEAL_PROJECTILE.create(summon.level());
        if (projectile != null) {
            Vec3 targetPos = owner.position().add(0, owner.getEyeHeight(), 0);
            Vec3 spawnPos = summon.position().add(0, summon.getBbHeight() * 1.25, 0);
            Vec3 vel = targetPos.subtract(spawnPos).normalize().scale(projectile.getDefaultSpeed());

            projectile.setOwner(summon);
            projectile.setTarget(owner);
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            projectile.setDeltaMovement(vel);
            projectile.setNoGravity(true);

            summon.level().addFreshEntity(projectile);
        }

    }

    @Override
    protected int attackDelay() {
        return 17;
    }

    @Override
    protected Class<? extends CompanionSummonEntity> summonType() {
        return EnderFrogEntity.class;
    }

}