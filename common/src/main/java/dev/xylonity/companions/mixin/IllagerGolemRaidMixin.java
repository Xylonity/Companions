package dev.xylonity.companions.mixin;

import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class IllagerGolemRaidMixin {

    @Shadow
    public abstract int getGroupsSpawned();

    @Shadow
    public abstract Level getLevel();

    @Shadow
    public abstract void joinRaid(int wave, Raider raider, @Nullable BlockPos pos, boolean isRecruited);

    @Inject(method = "spawnGroup", at = @At("HEAD"))
    private void companions$addCustomEntity(BlockPos pos, CallbackInfo ci) {
        int wave = this.getGroupsSpawned() + 1;

        if ((wave & 1) == 0) {
            return;
        }

        int illagerGolemAmount = 1;
        for (int i = 0; i < illagerGolemAmount; i++) {
            Raider raider = CompanionsEntities.ILLAGER_GOLEM.get().create(this.getLevel());
            if (raider == null) {
                continue;
            }

            raider.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);

            this.joinRaid(wave, raider, pos, false);
        }

    }

}
