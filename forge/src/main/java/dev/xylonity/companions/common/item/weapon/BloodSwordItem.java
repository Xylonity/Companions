package dev.xylonity.companions.common.item.weapon;

import dev.xylonity.companions.common.entity.projectile.BloodSlashProjectile;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import dev.xylonity.companions.common.item.generic.GenericGeckoSwordItem;
import dev.xylonity.companions.common.material.ItemMaterials;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BloodSwordItem extends GenericGeckoSwordItem {

    public BloodSwordItem(Properties properties, String resourceKey, ItemMaterials material, float extraDamage, float extraSpeed) {
        super(properties, resourceKey, material, extraDamage, extraSpeed);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!pLevel.isClientSide) {
            BloodSlashProjectile slash = CompanionsEntities.BLOOD_SLASH_PROJECTILE.get().create(pLevel);
            if (slash != null) {
                Vec3 spawnPos = player.position().add(0, player.getEyeHeight(), 0);
                slash.setOwner(player);
                slash.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                slash.setDeltaMovement(player.getLookAngle().scale(HolinessStartProjectile.SPEED));
                slash.setNoGravity(true);
                pLevel.addFreshEntity(slash);
            }

            player.getCooldowns().addCooldown(this, 200);
        }

        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }

}
