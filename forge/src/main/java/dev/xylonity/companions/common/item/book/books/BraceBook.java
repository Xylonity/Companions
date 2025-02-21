package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.BraceProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BraceBook extends AbstractMagicBook {

    public BraceBook(Properties properties) {
        super(properties);
    }

    @Override
    public String getName() {
        return "fire_mark";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        BraceProjectile projectile = CompanionsEntities.BRACE_PROJECTILE.get().create(level);
        if (projectile != null) {
            Vec3 look = player.getLookAngle().normalize();
            Vec3 eyePos = player.getEyePosition();
            Vec3 spawnPos = eyePos.add(look.scale(0.3));
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

            projectile.setOwner(player);

            double speed = 0.7;
            Vec3 initialVelocity = look.scale(speed).add(player.getDeltaMovement());
            projectile.setDeltaMovement(initialVelocity);

            level.addFreshEntity(projectile);
        }

        return super.use(level, player, hand);
    }
}
