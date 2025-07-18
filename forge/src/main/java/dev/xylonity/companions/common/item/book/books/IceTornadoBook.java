package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.TornadoProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class IceTornadoBook extends AbstractMagicBook {

    public IceTornadoBook(Properties properties) {
        super(properties);
    }

    @Override
    protected String tooltipName() {
        return "ice_tornado_book";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {

        if (!pLevel.isClientSide) {
            TornadoProjectile tornadoProjectile = CompanionsEntities.TORNADO_PROJECTILE.get().create(pLevel);
            if (tornadoProjectile != null) {
                Vec3 look = pPlayer.getLookAngle();
                Vec3 hLook = new Vec3(look.x, 0, look.z).normalize();
                tornadoProjectile.moveTo(pPlayer.getX() + hLook.x, pPlayer.getY(), pPlayer.getZ() + hLook.z);
                tornadoProjectile.setOwner(pPlayer);
                pLevel.addFreshEntity(tornadoProjectile);
            }

            pPlayer.getCooldowns().addCooldown(this, (int)(CompanionsConfig.ICE_TORNADO_COOLDOWN * (1 - (Util.hasFullSetOn(pPlayer, ArmorMaterials.MAGE) * CompanionsConfig.MAGE_SET_COOLDOWN_REDUCTION))));
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    protected void playSound(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), CompanionsSounds.SPELL_RELEASE_TORNADO.get(), player.getSoundSource(), 1.8F, 1.0F);
    }

}