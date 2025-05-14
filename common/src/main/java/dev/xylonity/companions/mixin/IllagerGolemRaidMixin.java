package dev.xylonity.companions.mixin;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.accessor.RaidAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class IllagerGolemRaidMixin implements RaidAccessor {

    @Shadow public abstract int getGroupsSpawned();
    @Shadow public abstract Level getLevel();
    @Shadow public abstract void joinRaid(int wave, Raider raider, @Nullable BlockPos pos, boolean isRecruited);
    @Shadow @Final private int numGroups;
    @Shadow protected abstract boolean shouldSpawnBonusGroup();

    @Shadow @Final private RandomSource random;
    @Unique private static final int[] COMPANIONS_ILLAGER_GOLEM_SPAWNWAVE;

    @Inject(method = "spawnGroup", at = @At("HEAD"))
    private void companions$addCustomEntity(BlockPos pos, CallbackInfo ci) {
        int wave = this.getGroupsSpawned() + 1;

        DifficultyInstance difficultyInstance = this.getLevel().getCurrentDifficultyAt(pos);

        int numIllagerGolems = this.companions$getDefaultNumSpawns(COMPANIONS_ILLAGER_GOLEM_SPAWNWAVE, wave, this.shouldSpawnBonusGroup()) + this.companions$getCustomBonusSpawns(this.random, wave, difficultyInstance, this.shouldSpawnBonusGroup());

        for (int i = 0; i < numIllagerGolems; i++) {
            EntityType<? extends Raider> companions$illager_golem_entityType = CompanionsCommon.COMMON_PLATFORM.getIllagerGolemEntity();
            Raider companions$illager_golem = companions$illager_golem_entityType.create(this.getLevel());

            if (companions$illager_golem != null) {
                companions$illager_golem.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                this.joinRaid(wave, companions$illager_golem, pos, false);
            }
        }

    }

    @Unique
    private int companions$getDefaultNumSpawns(int[] waves, int pWave, boolean pShouldSpawnBonusGroup) {
        return pShouldSpawnBonusGroup ? waves[this.numGroups] : waves[pWave];
    }

    @Unique
    private int companions$getCustomBonusSpawns(RandomSource random, int wave, DifficultyInstance difficultyInstance, boolean shouldSpawnBonusGroup) {
        Difficulty difficulty = difficultyInstance.getDifficulty();
        boolean isEasy = difficulty == Difficulty.EASY;
        boolean isNormal = difficulty == Difficulty.NORMAL;
        int potentialSpawns;

        if (isEasy) {
            potentialSpawns = wave > 2 ? random.nextInt(2) : 0;
        } else if (isNormal) {
            potentialSpawns = wave > 3 ? random.nextInt(2) + 1 : 1;
        } else {
            potentialSpawns = wave > 4 ? 2 : 1;
            if (shouldSpawnBonusGroup) {
                potentialSpawns += 1;
            }
        }

        return Math.max(0, potentialSpawns > 0 ? random.nextInt(potentialSpawns + 1) : 0);
    }

    static {
        COMPANIONS_ILLAGER_GOLEM_SPAWNWAVE = new int[]{0, 1, 2, 1, 2, 1, 2, 3};
    }

}