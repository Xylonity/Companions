package dev.xylonity.companions.common.entity.ai.puppet.goal;

import dev.xylonity.companions.common.entity.ai.puppet.AbstractPuppetLeftAttackGoal;
import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import dev.xylonity.companions.common.entity.projectile.StakeProjectile;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class PuppetLeftCannonAttackGoal extends AbstractPuppetLeftAttackGoal {

    public PuppetLeftCannonAttackGoal(PuppetEntity puppet, int minCd, int maxCd) {
        super(puppet, minCd, maxCd, "CANNON");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        Vec3 lookVec = puppet.getLookAngle();
        Vec3 vec3 = new Vec3(-lookVec.z, 0, lookVec.x).normalize();
        Vec3 offset = vec3.scale(-0.5).add(0, -0.2, 0);

        Vec3 startPos =  new Vec3(puppet.getX(), puppet.getY(), puppet.getZ()).add(offset); // puppet.getEyePosition(1.0F).subtract().add(offset);
        Vec3 targetPos = target.getEyePosition(1.0F);
        Vec3 direction = targetPos.subtract(startPos).normalize();

        StakeProjectile stake = new StakeProjectile(puppet.level(), puppet);
        stake.setPos(startPos.x, startPos.y, startPos.z);
        stake.shoot(direction.x, direction.y, direction.z, 1.6F, 0);
        puppet.level().addFreshEntity(stake);
    }

    @Override
    public void start() {
        super.start();
        puppet.level().playSound(null, puppet.blockPosition(), CompanionsSounds.PUPPET_ATTACK_CANON.get(), puppet.getSoundSource(), 4, 1);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.puppet.getOwner() != null;
    }

    @Override
    protected boolean hasRequiredArm() {
        return puppet.inventory.getItem(1).is(CompanionsItems.CANNON_ARM.get());
    }

}