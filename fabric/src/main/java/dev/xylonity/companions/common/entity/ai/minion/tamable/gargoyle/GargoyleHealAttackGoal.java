package dev.xylonity.companions.common.entity.ai.minion.tamable.gargoyle;

import dev.xylonity.companions.common.entity.ai.minion.tamable.AbstractMinionAttackGoal;
import dev.xylonity.companions.common.entity.companion.MinionEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

public class GargoyleHealAttackGoal extends AbstractMinionAttackGoal {

    public GargoyleHealAttackGoal(MinionEntity minion, int minCd, int maxCd) {
        super(minion, 20, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        minion.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        minion.setNoMovement(false);
    }

    @Override
    public boolean canUse() {
        if (!minion.getVariant().equals(variant())) return false;
        if (minion.getAttackType() != 0) return false;
        if (minion.getOwner() == null) return false;
        if (minion.getMainAction() != 1) return false;
        if (this.minion.getOwner().getHealth() >= this.minion.getOwner().getMaxHealth() * 0.5f) return false;

        if (nextUseTick < 0) {
            nextUseTick = minion.tickCount + minCooldown + minion.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return minion.tickCount >= nextUseTick;
    }

    @Override
    public void tick() {
        LivingEntity owner = minion.getOwner();
        if (owner != null) {
            minion.getLookControl().setLookAt(owner, 30F, 30F);
        }

        if (attackTicks == attackDelay() && owner != null && owner.isAlive()) {
            performAttack(owner);
        }

        attackTicks++;
    }

    @Override
    protected void performAttack(LivingEntity owner) {
        if (minion.getOwner() != null) {
            Projectile healRing = CompanionsEntities.HEAL_RING_PROJECTILE.create(minion.getOwner().level());
            if (healRing != null) {
                healRing.moveTo(owner.getX(), owner.getY(), owner.getZ());
                healRing.setOwner(owner);
                minion.getOwner().level().addFreshEntity(healRing);
            }
        }
    }

    @Override
    protected int attackDelay() {
        return 8;
    }

    @Override
    protected String variant() {
        return MinionEntity.Variant.END.getName();
    }

    @Override
    protected int attackType() {
        return 2;
    }

}