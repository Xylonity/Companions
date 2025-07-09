package dev.xylonity.companions.common.entity.ai.teddy.goal;

import dev.xylonity.companions.common.entity.ai.teddy.AbstractTeddyAttackGoal;
import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import dev.xylonity.companions.common.entity.projectile.NeedleProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsEffects;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class TeddyVoodooAttackGoal extends AbstractTeddyAttackGoal {

    private List<LivingEntity> list;

    public TeddyVoodooAttackGoal(TeddyEntity teddy, int minCd, int maxCd) {
        super(teddy, 21, minCd, maxCd);
        this.list = new ArrayList<>();
    }

    @Override
    public boolean canUse() {
        this.list = teddy.level().getEntitiesOfClass(LivingEntity.class, new AABB(teddy.blockPosition()).inflate(15), e -> e.hasEffect(CompanionsEffects.VOODOO.get()));
        if (list.isEmpty()) return false;
        if (teddy.getPhase() == 2) return false;
        if (this.phase() != teddy.getPhase()) return false;
        if (teddy.getAttackType() != 0) return false;
        if (teddy.getTarget() == null) return false;
        if (teddy.getMainAction() != 1) return false;

        if (nextUseTick < 0) {
            nextUseTick = teddy.tickCount + minCooldown + teddy.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return teddy.tickCount >= nextUseTick;
    }

    @Override
    public void start() {
        super.start();
        teddy.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.list.clear();
        teddy.setNoMovement(false);
    }

    @Override
    public void tick() {
        LivingEntity target = teddy.getTarget();
        if (target != null) {
            teddy.getLookControl().setLookAt(target, 30F, 30F);
        }

        if (attackTicks == 16) {
            performAttack(target);
        }

        if (attackTicks == attackDelay() && target != null && target.isAlive()) {
            performAttack(target);
        }

        attackTicks++;
    }

    @Override
    protected void performAttack(LivingEntity unused) {
        for (LivingEntity e : list) {
            if (!Util.areEntitiesLinked(e, teddy)) {
                e.removeEffect(CompanionsEffects.VOODOO.get());

                if (teddy.level().isClientSide) continue;

                for (int i = 0; i < 2; i++) {
                    double radius = 2.0 + teddy.getRandom().nextDouble();
                    double angle = teddy.getRandom().nextDouble() * Math.PI * 2;

                    double x = e.getX() + Math.cos(angle) * radius;
                    double y = (e.getBoundingBox().minY + e.getBbHeight() * 0.75) + teddy.getRandom().nextDouble() * e.getBbHeight() * 0.5 + 1.0;
                    double z = e.getZ() + Math.sin(angle) * radius;

                    NeedleProjectile needle = CompanionsEntities.NEEDLE_PROJECTILE.get().create(teddy.level());
                    if (needle != null) {
                        needle.setOwner(teddy);
                        needle.setPos(x, y, z);
                        needle.setNoGravity(true);

                        Vec3 dir = e.getEyePosition().subtract(new Vec3(x, y, z)).normalize();
                        float yaw = (float) Math.atan2(dir.x, dir.z) * Mth.RAD_TO_DEG;
                        float pitch = (float) Math.atan2(dir.y, Math.hypot(dir.x, dir.z)) * Mth.RAD_TO_DEG;
                        needle.setYRot(yaw);
                        needle.setXRot(pitch);
                        needle.yRotO = yaw;
                        needle.xRotO = pitch;

                        needle.setTargetEntity(e);

                        needle.setInvisible(true);
                        teddy.level().addFreshEntity(needle);
                    }
                }
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 7;
    }

    @Override
    protected int phase() {
        return 1;
    }

    @Override
    protected int getAttackType() {
        return 2;
    }

}