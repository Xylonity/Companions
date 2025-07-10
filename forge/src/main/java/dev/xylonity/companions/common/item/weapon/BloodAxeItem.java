package dev.xylonity.companions.common.item.weapon;

import dev.xylonity.companions.common.entity.projectile.BloodTornadoProjectile;
import dev.xylonity.companions.common.item.generic.GenericGeckoAxeItem;
import dev.xylonity.companions.common.material.ItemMaterials;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BloodAxeItem extends GenericGeckoAxeItem {

    public BloodAxeItem(Properties properties, String resourceKey, ItemMaterials material, float extraDamage, float extraSpeed) {
        super(properties, resourceKey, material, extraDamage, extraSpeed);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!pLevel.isClientSide) {
            for (int i = 0; i < 4; i++) {
                double angleRad = Math.toRadians(player.getYRot() + i * 90);

                BloodTornadoProjectile tornado = CompanionsEntities.BLOOD_TORNADO_PROJECTILE.get().create(pLevel);
                if (tornado != null) {
                    tornado.setPos(player.getX(), player.getY() + player.getBbHeight() * 0.4f, player.getZ());

                    tornado.setOwner(player);
                    tornado.setAlpha((float) angleRad);

                    tornado.shoot(-Math.sin(angleRad), 0.0, Math.cos(angleRad), 1.2525f, 0.0f);

                    pLevel.addFreshEntity(tornado);
                }
            }

            player.getCooldowns().addCooldown(this, 200);
        }

        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }

}
