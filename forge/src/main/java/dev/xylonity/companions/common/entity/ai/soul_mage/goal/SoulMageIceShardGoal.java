package dev.xylonity.companions.common.entity.ai.soul_mage.goal;

import dev.xylonity.companions.common.entity.ai.soul_mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.custom.SoulMageEntity;
import dev.xylonity.companions.common.entity.projectile.BigIceShardProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class SoulMageIceShardGoal extends AbstractSoulMageAttackGoal {

    public SoulMageIceShardGoal(SoulMageEntity soulMage, int minCd, int maxCd) {
        super(soulMage, minCd, maxCd, "ICE_SHARD");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (!soulMage.level().isClientSide && soulMage.getOwner() != null) {
            BigIceShardProjectile projectile = CompanionsEntities.BIG_ICE_SHARD_PROJECTILE.get().create(soulMage.level());

            if (projectile != null) {
                Vec3 forward = soulMage.getLookAngle().normalize();

                Vec3 up = new Vec3(0, 1, 0);

                if (Math.abs(forward.dot(up)) > 0.99) {
                    up = new Vec3(1, 0, 0);
                }

                Vec3 right = forward.cross(up).normalize();

                double spawnX = soulMage.getX() + right.x * 0.5 + up.x * 0.5;
                double spawnY = soulMage.getY() + soulMage.getBbHeight() + right.y * 0.5 + up.y * 0.5;
                double spawnZ = soulMage.getZ() + right.z * 0.5 + up.z * 0.5;
                projectile.moveTo(spawnX, spawnY, spawnZ);
                projectile.setOwner(soulMage);

                double maxAngle = Math.toRadians(30);
                Random random = new Random();

                double u = random.nextDouble();
                double cosTheta = u * (1 - Math.cos(maxAngle)) + Math.cos(maxAngle);
                double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
                double phi = random.nextDouble() * 2 * Math.PI;

                Vec3 randomDir = new Vec3(sinTheta * Math.cos(phi), cosTheta, sinTheta * Math.sin(phi)).normalize();

                double initialSpeed = 0.2;
                projectile.setDeltaMovement(randomDir.scale(initialSpeed));
                projectile.setTarget(target);

                soulMage.level().addFreshEntity(projectile);
            }
        }
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.soulMage.getOwner() != null;
    }

    @Override
    protected boolean hasRequiredBook() {
        for (int i = 0; i < soulMage.inventory.getContainerSize(); i++) {
            ItemStack stack = soulMage.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.BOOK_ICE_SHARD.get()) {
                return true;
            }
        }

        return false;
    }

}