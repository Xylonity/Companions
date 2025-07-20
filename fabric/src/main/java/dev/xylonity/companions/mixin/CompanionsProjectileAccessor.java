package dev.xylonity.companions.mixin;

import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Projectile.class)
public interface CompanionsProjectileAccessor {
    @Accessor("leftOwner")
    boolean getLeftOwner();

    @Accessor("leftOwner")
    void setLeftOwner(boolean leftOwner);
}
