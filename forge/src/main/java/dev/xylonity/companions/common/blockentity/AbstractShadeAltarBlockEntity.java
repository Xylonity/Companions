package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.common.entity.projectile.ShadeAltarUpgradeHaloProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public abstract class AbstractShadeAltarBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int MAX_CHARGES = CompanionsConfig.SHADOW_ALTAR_MAX_CHARGES;
    private static final int MAX_BLOOD_CHARGES = CompanionsConfig.SHADOW_ALTAR_BLOOD_CHARGES_AMOUNT;

    public UUID activeShadeUUID = null;
    private int charges = 0;
    private int prevCharges = 0;

    public AbstractShadeAltarBlockEntity(BlockEntityType<?> pType, BlockPos pos, BlockState state) {
        super(pType, pos, state);
    }

    public int getCharges() {
        return charges;
    }

    public int getMaxCharges() {
        return MAX_CHARGES;
    }

    public int getBloodCharges() {
        return MAX_BLOOD_CHARGES;
    }

    public boolean isBloodUpgradeActive() {
        return this.getCharges() >= MAX_CHARGES - MAX_BLOOD_CHARGES;
    }

    public boolean addCharge() {
        if (charges >= MAX_CHARGES) return false;
        charges++;
        setChanged();
        return true;
    }

    public boolean consumeCharge() {
        if (charges <= 0) return false;
        charges--;
        setChanged();
        return true;
    }

    protected boolean isImportantChargeDiff() {
        return prevCharges == 0 && charges == 1 || prevCharges == MAX_CHARGES - MAX_BLOOD_CHARGES - 1 && charges == MAX_CHARGES - MAX_BLOOD_CHARGES;
    }

    protected boolean hasIncreasedFromPrevInteraction() {
        return charges > prevCharges;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            if (hasIncreasedFromPrevInteraction()) {
                ShadeAltarUpgradeHaloProjectile halo = CompanionsEntities.SHADE_ALTAR_UPGRADE_HALO.get().create(level);
                if (halo != null) {
                    halo.setPos(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.015, this.getBlockPos().getZ() + 0.5);
                    level.addFreshEntity(halo);
                }
            }
        }

        this.prevCharges = this.charges;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Charges", charges);
        tag.putInt("PrevCharges", prevCharges);

        if (activeShadeUUID != null) {
            tag.putUUID("ActiveShadeUUID", activeShadeUUID);
        } else {
            tag.remove("ActiveShadeUUID");
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        charges = tag.getInt("Charges");
        prevCharges = tag.getInt("PrevCharges");
        if (tag.hasUUID("ActiveShadeUUID")) {
            activeShadeUUID = tag.getUUID("ActiveShadeUUID");
        } else {
            activeShadeUUID = null;
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.charges = tag.getInt("Charges");
        this.prevCharges = tag.getInt("PrevCharges");
        if (tag.hasUUID("ActiveShadeUUID")) {
            activeShadeUUID = tag.getUUID("ActiveShadeUUID");
        } else {
            activeShadeUUID = null;
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("Charges", getCharges());
        tag.putInt("PrevCharges", prevCharges);
        if (activeShadeUUID != null) {
            tag.putUUID("ActiveShadeUUID", activeShadeUUID);
        }
        return tag;
    }

    public void sync() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Packet<ClientGamePacketListener> pkt = ClientboundBlockEntityDataPacket.create(this);
        serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(worldPosition), false).forEach(p -> p.connection.send(pkt));
    }

    public abstract @Nullable ShadeEntity spawnShade(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand);

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public double findPlaceToSpawn(@NotNull Player pPlayer, @NotNull Level pLevel, int blockX, int blockZ) {
        int baseY = Mth.floor(pPlayer.getY());
        int minY = pLevel.getMinBuildHeight();
        int maxY = pLevel.getMaxBuildHeight() - 1;
        int spawnY = baseY;
        boolean found = false;

        for (int offset = 0; offset <= Math.max(baseY - minY, maxY - baseY) && !found; offset++) {
            int yDown = baseY - offset;
            if (yDown >= minY) {
                BlockPos below = new BlockPos(blockX, yDown - 1, blockZ);
                BlockPos curr = new BlockPos(blockX, yDown, blockZ);

                if ((yDown - 1 >= minY) && !pLevel.getBlockState(below).isAir() && pLevel.getBlockState(curr).isAir()) {
                    spawnY = yDown;
                    found = true;
                }
            }

            if (!found && offset > 0) {
                int yUp = baseY + offset;
                if (yUp <= maxY) {
                    BlockPos below = new BlockPos(blockX, yUp - 1, blockZ);
                    BlockPos curr = new BlockPos(blockX, yUp, blockZ);

                    if ((yUp - 1 >= minY) && !pLevel.getBlockState(below).isAir() && pLevel.getBlockState(curr).isAir()) {
                        spawnY = yUp;
                        found = true;
                    }
                }
            }

        }

        if (!found) {
            return Math.max(minY + 1, Math.min(maxY, baseY));
        }

        return spawnY;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { ;; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}