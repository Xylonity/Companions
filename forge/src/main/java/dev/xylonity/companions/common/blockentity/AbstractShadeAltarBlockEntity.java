package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.entity.projectile.ShadeAltarUpgradeHaloProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractShadeAltarBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int MAX_CHARGES = CompanionsConfig.SHADOW_ALTAR_MAX_CHARGES;
    private static final int MAX_BLOOD_CHARGES = CompanionsConfig.SHADOW_ALTAR_BLOOD_CHARGES_AMOUNT;

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
                    halo.setPos(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.05, this.getBlockPos().getZ() + 0.5);
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
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        charges = tag.getInt("Charges");
        prevCharges = tag.getInt("PrevCharges");
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.charges = tag.getInt("Charges");
        this.prevCharges = tag.getInt("PrevCharges");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("Charges", getCharges());
        tag.putInt("PrevCharges", prevCharges);
        return tag;
    }

    public void sync() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Packet<ClientGamePacketListener> pkt = ClientboundBlockEntityDataPacket.create(this);
        ChunkPos chunkPos = new ChunkPos(worldPosition);

        serverLevel.getChunkSource().chunkMap.getPlayers(chunkPos, false).forEach(p -> p.connection.send(pkt));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { ;; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}