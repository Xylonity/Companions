package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.FireMarkRingProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FireMarkBook extends AbstractMagicBook {

    public FireMarkBook(Properties properties) {
        super(properties);
    }

    @Override
    public String getName() {
        return "fire_mark";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        int dist = 50;
        boolean spawnFlag = false;

        Vec3 eyePos = pPlayer.getEyePosition();
        Vec3 lookVec = pPlayer.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(dist));

        BlockHitResult hit = pLevel.clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, pPlayer));
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos targetPos = hit.getBlockPos();
            double distance = eyePos.distanceTo(Vec3.atCenterOf(targetPos));

            if (distance <= dist) {
                FireMarkRingProjectile fireMarkRing = CompanionsEntities.FIRE_MARK_RING_PROJECTILE.get().create(pLevel);
                if (fireMarkRing != null) {
                    fireMarkRing.moveTo(targetPos.getX() + 0.5, targetPos.getY() + 1.0, targetPos.getZ() + 0.5);
                    fireMarkRing.setOwner(pPlayer);
                    pLevel.addFreshEntity(fireMarkRing);
                    spawnFlag = true;
                }
            }
        }

        if (!spawnFlag) {
            FireMarkRingProjectile fireMarkRing = CompanionsEntities.FIRE_MARK_RING_PROJECTILE.get().create(pLevel);
            if (fireMarkRing != null) {
                fireMarkRing.moveTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
                fireMarkRing.setOwner(pPlayer);
                pLevel.addFreshEntity(fireMarkRing);
            }
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

}