package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.BigIceShardProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class IceShardBook extends AbstractMagicBook {

    public IceShardBook(Properties properties) {
        super(properties);
    }

    @Override
    public String getName() {
        return "ice_shard";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player player, @NotNull InteractionHand pUsedHand) {

        BigIceShardProjectile projectile = CompanionsEntities.BIG_ICE_SHARD_PROJECTILE.get().create(pLevel);

        if (projectile != null) {
            Vec3 forward = player.getLookAngle().normalize();

            Vec3 up = new Vec3(0, 1, 0);

            if (Math.abs(forward.dot(up)) > 0.99) {
                up = new Vec3(1, 0, 0);
            }

            Vec3 right = forward.cross(up).normalize();
            double spawnX = player.getX() + right.x * 0.5 + up.x * 0.5;
            double spawnY = player.getY() + player.getBbHeight() + right.y * 0.5 + up.y * 0.5;
            double spawnZ = player.getZ() + right.z * 0.5 + up.z * 0.5;
            projectile.moveTo(spawnX, spawnY, spawnZ);
            projectile.setOwner(player);

            double maxAngle = Math.toRadians(30);
            Random random = new Random();
            double cosTheta = random.nextDouble() * (1 - Math.cos(maxAngle)) + Math.cos(maxAngle);
            double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
            double phi = random.nextDouble() * 2 * Math.PI;

            Vec3 randomDir = forward.scale(cosTheta).add(right.scale(sinTheta * Math.cos(phi))).add(up.scale(sinTheta * Math.sin(phi))).normalize();
            projectile.setDeltaMovement(randomDir.scale(0.2));

            LivingEntity tByMob = player.getLastHurtByMob();
            projectile.setTarget(tByMob == null ? player.getLastHurtMob() : tByMob);

            pLevel.addFreshEntity(projectile);
        }

        return super.use(pLevel, player, pUsedHand);
    }

}