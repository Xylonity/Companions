package dev.xylonity.companions.common.item;

import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEffects;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MutantFlesh extends TooltipItem {

    public MutantFlesh(Properties properties, String tooltipName) {
        super(properties, tooltipName);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        ItemStack ret = super.finishUsingItem(stack, level, entity);

        if (entity instanceof Player player && !level.isClientSide) {
            Vec3 eyePos = entity.getEyePosition();
            Vec3 endPos = eyePos.add(entity.getLookAngle().scale(20));

            AABB bb = new AABB(eyePos, endPos).inflate(2.0);

            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, player, eyePos, endPos, bb, Entity::isAlive);
            if (entityHit != null && CompanionsConfig.MUTANT_FLESH_SHOULD_TP) {
                spawnMutantParticles(10, level, player.position());

                Vec3 tpPos = calculateOppositeSide(player, entityHit.getEntity());

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.teleportTo(tpPos.x, tpPos.y, tpPos.z);
                } else {
                    player.setPos(tpPos.x, tpPos.y, tpPos.z);
                }

                spawnMutantParticles(10, level, tpPos);
            }
        }

        if (level.random.nextFloat() < 0.25f) {
            entity.addEffect(new MobEffectInstance(CompanionsEffects.VOODOO.get(), level.random.nextInt(100, 600), 0, true, true, true));
        }

        if (level.random.nextFloat() < 0.75 && CompanionsConfig.MUTANT_FLESH_SHOULD_HURT) {
            entity.hurt(entity.damageSources().magic(), 3);
        }

        return ret;
    }

    private void spawnMutantParticles(int amount, Level level, Vec3 position) {
        if (level instanceof ServerLevel sv) {
            for (int i = 0; i < amount; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 2.0;
                double dy = (level.random.nextDouble() - 0.5) * 2.0;
                double dz = (level.random.nextDouble() - 0.5) * 2.0;

                sv.sendParticles(ParticleTypes.POOF, position.x, position.y + 1, position.z, 1, dx, dy, dz, 0.1);
                if (i % 5 == 0) {
                    sv.sendParticles(CompanionsParticles.TEDDY_TRANSFORMATION.get(), position.x, position.y + 1, position.z, 1, dx, dy, dz, 0.2);
                }
            }
        }

    }

    private Vec3 calculateOppositeSide(LivingEntity player, Entity target) {
        Vec3 playerPos = player.position();
        Vec3 targetPos = target.position();
        Vec3 oppositePos = targetPos.add(targetPos.subtract(playerPos));
        return new Vec3(oppositePos.x, playerPos.y, oppositePos.z);
    }

}