package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.AbstractCorneliusSummonAttackGoal;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class FireworkToadGoal extends AbstractCorneliusSummonAttackGoal {

    private Vec3 p0, p1, p2;
    private int totalTicks;
    private boolean flying;

    public FireworkToadGoal(CompanionSummonEntity summon, int minCd, int maxCd) {
        super(summon, 110, minCd, maxCd);
    }

    @Override
    public boolean canUse() {
        return super.canUse();
    }

    @Override
    public void start() {
        super.start();

        FireworkToadEntity toad = (FireworkToadEntity) summon;
        LivingEntity target = toad.getTarget();
        if (target == null || !target.isAlive()) return;

        p0 = toad.position();
        p2 = target.position();

        double horiz = new Vec3(p2.x - p0.x, 0, p2.z - p0.z).length();
        double y = Math.max(p0.y, p2.y) + Mth.clamp(horiz * 1.15, 1.0, 12.0);
        p1 = new Vec3((p0.x + p2.x) * 0.5, y, (p0.z + p2.z) * 0.5);

        totalTicks = new Random().nextInt(70, 110);
        flying = true;

        toad.setFlying(true);
        toad.setNoMovement(true);
        toad.setNoGravity(true);
        toad.noPhysics = true;
        toad.getNavigation().stop();
        toad.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void tick() {
        super.tick();

        if (!flying) return;

        Vec3 pos = Util.bezier(p0, p1, p2, attackTicks / (double) totalTicks);
        summon.setPos(pos.x, pos.y, pos.z);
        summon.setDeltaMovement(Vec3.ZERO);

        Vec3 dir = Util.bezier(p0, p1, p2, Math.min(attackTicks / (double) totalTicks + .01, 1.0)).subtract(pos).normalize();
        float yaw = (float)(Mth.atan2(dir.z, dir.x) * 180 / Math.PI) - 90F;

        summon.setYRot(yaw);
        summon.setYBodyRot(yaw);
        summon.setXRot((float)(-Mth.atan2(dir.y, Math.sqrt(dir.x*dir.x + dir.z*dir.z)) * 180 / Math.PI));

        attackTicks++;

        if (attackTicks >= totalTicks) {
            spawnRockets(pos);
            resetPhysics((FireworkToadEntity) summon);
            summon.remove(RemovalReason.DISCARDED);
            flying = false;
        }

        if (attackTicks == totalTicks / 2) {
            summon.triggerAnim("rot_controller", "rot");
        }
    }

    @Override
    public void stop() {
        super.stop();

        if (flying && summon.isAlive()) resetPhysics((FireworkToadEntity) summon);

        flying = false;
    }

    @Override
    protected void performAttack(LivingEntity owner) { ;; }

    private static void resetPhysics(FireworkToadEntity toad) {
        toad.setFlying(false);
        toad.setNoMovement(false);
        toad.setNoGravity(false);
        toad.noPhysics = false;
        toad.setDeltaMovement(Vec3.ZERO);
        toad.setParabolaCenter(null);
    }

    private void spawnRockets(Vec3 center) {

        if (summon.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.FLASH, center.x, center.y, center.z, 1, 0, 0, 0, 0);

            for (int i = 0; i < 6; i++) {
                double angle = summon.getRandom().nextDouble() * Math.PI * 2;
                double dist = summon.getRandom().nextDouble() * 8;
                double dx = Math.cos(angle) * dist;
                double dz = Math.sin(angle) * dist;
                double dy = 3.0 + summon.getRandom().nextDouble() * 7.0;

                rocket(level, new Vec3(center.x + dx, center.y + dy, center.z + dz), summon.getRandom());
            }

            rocket(level, new Vec3(center.x, center.y + 0.5, center.z), summon.getRandom());

            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, summon.getBoundingBox().inflate(3))) {
                if (!Util.areEntitiesLinked(e, summon)) {
                    summon.doHurtTarget(e);
                }
            }

        }

    }

    private static void rocket(ServerLevel world, Vec3 where, RandomSource r) {
        CompoundTag fwTag = new CompoundTag();
        fwTag.putByte("Flight", (byte)1);

        double rand = r.nextDouble();
        int shape;
        if (rand < 0.3) {
            // circle
            shape = r.nextBoolean() ? 0 : 1;
        } else if (rand < 0.6) {
            // star
            shape = 2;
        } else {
            // burst
            shape = 4;
        }

        CompoundTag exp = new CompoundTag();
        exp.putByte("Type", (byte) shape);
        exp.putBoolean("Trail", false);
        exp.putBoolean("Flicker", true);

        int cr = 128 + r.nextInt(128);
        int cg = 128 + r.nextInt(128);
        int cb = 128 + r.nextInt(128);
        int color = (cr << 16) | (cg << 8) | cb;

        exp.putIntArray("Colors", new int[]{ color });
        exp.putIntArray("FadeColors", new int[]{ color });

        ListTag expls = new ListTag();
        expls.add(exp);
        fwTag.put("Explosions", expls);

        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        //stack.addTagElement("Fireworks", fwTag);

        FireworkRocketEntity rocket = new FireworkRocketEntity(world, where.x, where.y, where.z, stack);

        world.addFreshEntity(rocket);
        world.broadcastEntityEvent(rocket, (byte) 17);

        rocket.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected int getAttackType() {
        return 1;
    }

    @Override
    protected int attackDelay() {
        return -1;
    }

    @Override
    protected Class<? extends CompanionSummonEntity> summonType() {
        return FireworkToadEntity.class;
    }

}