package dev.xylonity.companions.common.entity.ai.cloak.goal;

import dev.xylonity.companions.common.entity.ai.cloak.AbstractCloakAttackGoal;
import dev.xylonity.companions.common.entity.companion.CloakEntity;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class CloakInvisibilityGoal extends AbstractCloakAttackGoal {

    public CloakInvisibilityGoal(CloakEntity cloak, int minCd, int maxCd) {
        super(cloak, 25, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        cloak.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        cloak.setNoMovement(false);
    }

    @Override
    public boolean canUse() {
        if (cloak.getAttackType() != 0) return false;
        if (cloak.getMainAction() != 1) return false;
        if (cloak.getOwner() == null) return false;
        if (cloak.getOwner().hasEffect(CompanionsEffects.PHANTOM.get())) return false;

        if (nextUseTick < 0) {
            nextUseTick = cloak.tickCount + minCooldown + cloak.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return cloak.tickCount >= nextUseTick;
    }

    @Override
    public void tick() {
        LivingEntity target = cloak.getTarget();
        if (target != null) {
            cloak.getLookControl().setLookAt(target, 30F, 30F);
        }

        if (attackTicks == attackDelay() && cloak.getOwner() != null && cloak.getOwner().isAlive()) {
            performAttack(target);
        }

        attackTicks++;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (cloak.getOwner() != null) {
            cloak.getOwner().addEffect(new MobEffectInstance(CompanionsEffects.PHANTOM.get(), 600, 0, false, true, true));
            cloak.addEffect(new MobEffectInstance(CompanionsEffects.PHANTOM.get(), 600, 0, false, true, true));
            spawnParticles(cloak.getOwner().position(), cloak.getOwner().getBbHeight());
            spawnParticles(cloak.position(), cloak.getBbHeight());
        }
    }

    private void spawnParticles(Vec3 pos, float entityHeight) {
        for (int i = 0; i < 10; i++) {
            double dx = (cloak.level().random.nextDouble() - 0.5) * 0.75;
            double dy = (cloak.level().random.nextDouble() - 0.5) * 0.75;
            double dz = (cloak.level().random.nextDouble() - 0.5) * 0.75;
            if (cloak.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, pos.x, pos.y + entityHeight * 0.5f, pos.z, 1, dx, dy, dz, 0.1);
            }
        }
    }

    @Override
    protected int attackDelay() {
        return 20;
    }

    @Override
    protected int attackType() {
        return 3;
    }
}