package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FireworkToadGoal extends Goal {
    private final FireworkToadEntity entity;
    private final int delayTicks;
    private final int hangTicks;
    private int tickCount;
    private Vec3 launchVector;
    private LivingEntity target;

    public FireworkToadGoal(FireworkToadEntity entity, int delayTicks, int hangTicks) {
        this.entity = entity;
        this.delayTicks = delayTicks;
        this.hangTicks = hangTicks;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!entity.isTame() || entity.getOwnerUUID() == null) return false;
        if (this.entity.getTarget() == null) return false;
        return entity.onGround();
    }

    @Override
    public void start() {
        this.tickCount = 0;
        this.target = this.entity.getTarget();
        entity.setNoGravity(false);
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && tickCount <= delayTicks + hangTicks + 100;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void stop() {
        entity.setNoGravity(false);
        this.target = null;
    }

    @Override
    public void tick() {
        tickCount++;

        if (tickCount == delayTicks) {
            entity.setDeltaMovement(entity.getDeltaMovement().x, 0.2, entity.getDeltaMovement().z);
            if (entity.level() instanceof ServerLevel world) {
                for (int i = 0; i < 20; i++) {
                    world.sendParticles(
                            ParticleTypes.CLOUD,
                            entity.getX(), entity.getY() + entity.getBbHeight()/2, entity.getZ(),
                            1,
                            0.2 * (Math.random()-0.5),
                            0.2 * (Math.random()-0.5),
                            0.2 * (Math.random()-0.5),
                            0.01
                    );
                }
            }

            entity.setNoGravity(true);
        } else if (tickCount > delayTicks && tickCount <= delayTicks + hangTicks) {
            ;;
        } else if (tickCount == delayTicks + hangTicks + 1 && target != null) {
            entity.setNoGravity(false);

            Vec3 dir = target.position().add(0, target.getBbHeight(), 0).subtract(entity.position()).normalize();
            launchVector = dir.scale(1.2);
            entity.setDeltaMovement(launchVector);
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.AMBIENT, 3.0F, 1.0F);
            entity.setCanExplode(true);
        } else if (tickCount > delayTicks + hangTicks + 1 && launchVector != null) {
            Vec3 current = entity.getDeltaMovement();
            Vec3 blended = current.lerp(launchVector, 0.1);
            entity.setDeltaMovement(blended);
        }
    }
}