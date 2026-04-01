package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.TeslaNetwork;
import dev.xylonity.companions.common.tesla.behaviour.platform.RecallPlatformPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RecallPlatformBlockEntity extends AbstractTeslaBlockEntity {

    private static final int COOLDOWN_TICKS = 20;
    private final Set<BlockPos> partnerPositions = new HashSet<>();
    private int cooldown = 0;

    private final ITeslaNodeBehaviour pulseBehaviour;

    public RecallPlatformBlockEntity(BlockPos pos, BlockState st) {
        super(CompanionsBlockEntities.RECALL_PLATFORM.get(), pos, st);
        this.pulseBehaviour = new RecallPlatformPulseBehaviour();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (!(t instanceof RecallPlatformBlockEntity platform)) {
            return;
        }

        if (level.getEntitiesOfClass(Player.class, new AABB(platform.getBlockPos()).move(0, 1, 0).inflate(0.5)).isEmpty()) {
            platform.cooldown = COOLDOWN_TICKS;
        }
        else {
            platform.cooldown--;
        }

        platform.pulseBehaviour.process(platform, level, blockPos, blockState);
        platform.tickCount++;
        platform.sync();
    }

    public void onStepped(ServerPlayer player) {
        if (cooldown > 0 || !isActive()) {
            return;
        }
        if (getLevel() == null) {
            return;
        }

        final List<BlockPos> shuffled = new ArrayList<>(partnerPositions);
        Collections.shuffle(shuffled, new Random());

        final TeslaNetwork network = TeslaNetwork.get(getLevel());
        for (final BlockPos target : shuffled) {
            if (!(getLevel().getBlockEntity(target) instanceof RecallPlatformBlockEntity otherPlatform)) {
                partnerPositions.remove(target);
                setChanged();
                continue;
            }

            if (!network.getConnectedComponent(this.asConnectionTarget()).contains(otherPlatform.asConnectionTarget())) {
                partnerPositions.remove(target);
                setChanged();
                continue;
            }

            if (level != null) {
                level.playSound(null, getBlockPos(), CompanionsSounds.TEDDY_TRANSFORMATION.get(), SoundSource.BLOCKS, 1, 1);
            }

            player.teleportTo(target.getX() + .5, target.getY() + 1, target.getZ() + .5);

            if (level != null) {
                level.playSound(null, target, CompanionsSounds.TEDDY_TRANSFORMATION.get(), SoundSource.BLOCKS, 1, 1);
            }

            this.cooldown = COOLDOWN_TICKS;
            otherPlatform.cooldown = COOLDOWN_TICKS;

            return;
        }
    }

    public void updatePartners(Set<BlockPos> newPartners) {
        if (partnerPositions.equals(newPartners)) {
            return;
        }

        partnerPositions.clear();
        partnerPositions.addAll(newPartners);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        final ListTag list = new ListTag();
        for (final BlockPos blockPos : partnerPositions) {
            final CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("X", blockPos.getX());
            compoundTag.putInt("Y", blockPos.getY());
            compoundTag.putInt("Z", blockPos.getZ());
            list.add(compoundTag);
        }

        tag.put("Partners", list);
        tag.putInt("Cooldown", cooldown);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        partnerPositions.clear();
        if (tag.contains("Partners", Tag.TAG_LIST)) {
            final ListTag list = tag.getList("Partners", Tag.TAG_COMPOUND);
            for (final Tag tagg : list) {
                final CompoundTag compoundTag = (CompoundTag) tagg;
                partnerPositions.add(new BlockPos(compoundTag.getInt("X"), compoundTag.getInt("Y"), compoundTag.getInt("Z")));
            }

        }

        cooldown = tag.getInt("Cooldown");
    }

    @Override
    public @NotNull Vec3 electricalChargeOriginOffset() {
        return Vec3.ZERO;
    }

    @Override
    public @NotNull Vec3 electricalChargeEndOffset() {
        return new Vec3(0, .5, 0);
    }

}