package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.BigIceShardProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
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
    protected String tooltipName() {
        return "ice_shard_book";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player player, @NotNull InteractionHand pUsedHand) {

        if (!pLevel.isClientSide) {
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

                // conical distribution
                // https://gist.github.com/Piratkopia13/6db0896218e5d83479d38b0e43ab2b68
                double angle = Math.toRadians(30);
                double cos = new Random().nextDouble() * (1 - Math.cos(angle)) + Math.cos(angle);
                double sin = Math.sqrt(1 - cos * cos);
                double phi = new Random().nextDouble() * 2 * Math.PI;

                projectile.setDeltaMovement(forward.scale(cos).add(right.scale(sin * Math.cos(phi))).add(up.scale(sin * Math.sin(phi))).normalize().scale(0.2));

                LivingEntity e = player.getLastHurtByMob();
                projectile.setTarget(e == null ? player.getLastHurtMob() : e);

                pLevel.addFreshEntity(projectile);
            }

            player.getCooldowns().addCooldown(this, (int)(CompanionsConfig.ICE_SHARD_COOLDOWN * (1 - (Util.hasFullSetOn(player, ArmorMaterials.MAGE) * CompanionsConfig.MAGE_SET_COOLDOWN_REDUCTION))));
        }

        return super.use(pLevel, player, pUsedHand);
    }

    @Override
    protected void playSound(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), CompanionsSounds.SPELL_RELEASE_ICE.get(), player.getSoundSource(), 1F, 1.0F);
    }

}