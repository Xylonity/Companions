package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.BlackHoleProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BlackHoleBook extends AbstractMagicBook {

    public BlackHoleBook(Properties properties) {
        super(properties);
    }

    @Override
    protected String tooltipName() {
        return "black_hole_book";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide) {
            BlackHoleProjectile blackHole = CompanionsEntities.BLACK_HOLE_PROJECTILE.create(level);
            if (blackHole != null) {
                Vec3 spawnPos = player.getEyePosition(1.0F).add(player.getLookAngle().scale(0.5D)).add(0, 0.0D, 0);
                blackHole.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                blackHole.setOwner(player);
                blackHole.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.8f, 0.0F);
                level.addFreshEntity(blackHole);
            }

            player.getCooldowns().addCooldown(this, (int)(CompanionsConfig.BLACK_HOLE_COOLDOWN * (1 - (Util.hasFullSetOn(player, ArmorMaterials.MAGE) * CompanionsConfig.MAGE_SET_COOLDOWN_REDUCTION))));
        }

        return super.use(level, player, hand);
    }

    @Override
    protected void playSound(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), CompanionsSounds.SPELL_RELEASE_DARK_HOLE.get(), player.getSoundSource(), 1F, 1.0F);
    }

}
