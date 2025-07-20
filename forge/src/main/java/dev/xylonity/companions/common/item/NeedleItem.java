package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class NeedleItem extends TooltipItem {

    public NeedleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    protected String tooltipName() {
        return "needle";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide) {
            Vec3 eyePos = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            Vec3 endPos = eyePos.add(look.scale(80));

            BlockHitResult blockHit = level.clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, player, eyePos, endPos, player.getBoundingBox().expandTowards(look.scale(80)).inflate(1.0), e -> e != player && e.isPickable());

            HitResult hit = entityHit != null && eyePos.distanceTo(entityHit.getLocation()) < eyePos.distanceTo(blockHit.getLocation()) ? entityHit : blockHit;

            if (hit.getType() == HitResult.Type.ENTITY) {
                EntityHitResult eHit = (EntityHitResult) hit;
                Entity target = eHit.getEntity();
                if (!Util.areEntitiesLinked(target, player)) {
                    if (!player.getAbilities().instabuild) player.getItemInHand(hand).shrink(1);
                    if (target instanceof LivingEntity e) {
                        e.hurt(player.damageSources().magic(), (float) CompanionsConfig.NEEDLE_ITEM_DAMAGE);
                        player.hurt(player.damageSources().magic(), (float) CompanionsConfig.NEEDLE_ITEM_DAMAGE);
                    }
                }

            }

        }

        player.swing(hand, true);
        return super.use(level, player, hand);
    }

}