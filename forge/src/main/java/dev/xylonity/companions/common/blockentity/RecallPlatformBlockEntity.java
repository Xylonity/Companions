package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.tesla.behaviour.platform.RecallPlatformPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
        if (!(t instanceof RecallPlatformBlockEntity platform)) return;

        platform.pulseBehaviour.process(platform, level, blockPos, blockState);

        if (platform.cooldown > 0) platform.cooldown--;
        platform.tickCount++;

        platform.sync();
    }

    public void onStepped(ServerPlayer player) {
        if (cooldown > 0) return;

        if (getLevel() == null) return;

        List<BlockPos> shuffled = new ArrayList<>(partnerPositions);
        Collections.shuffle(shuffled, new Random());

        for (BlockPos target : shuffled) {
            if (!(getLevel().getBlockEntity(target) instanceof RecallPlatformBlockEntity otherPlatform)) {
                partnerPositions.remove(target);
                setChanged();
                continue;
            }

            TeslaConnectionManager manager = TeslaConnectionManager.getInstance();
            if (!manager.getConnectedComponent(this.asConnectionNode()).contains(otherPlatform.asConnectionNode())) {
                partnerPositions.remove(target);
                setChanged();
                continue;
            }

            player.teleportTo(target.getX() + .5, target.getY() + 1, target.getZ() + .5);

            this.cooldown = COOLDOWN_TICKS;
            otherPlatform.cooldown = COOLDOWN_TICKS;
            return;
        }
    }

    public void updatePartners(Set<BlockPos> newPartners) {
        partnerPositions.clear();
        partnerPositions.addAll(newPartners);
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ListTag list = new ListTag();
        for (BlockPos p : partnerPositions) {
            CompoundTag t = new CompoundTag();
            t.putInt("X", p.getX());
            t.putInt("Y", p.getY());
            t.putInt("Z", p.getZ());
            list.add(t);
        }

        tag.put("Partners", list);
        tag.putInt("Cooldown", cooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        partnerPositions.clear();
        if (tag.contains("Partners", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Partners", Tag.TAG_COMPOUND);
            for (Tag t : list) {
                CompoundTag c = (CompoundTag) t;
                partnerPositions.add(new BlockPos(c.getInt("X"),
                        c.getInt("Y"),
                        c.getInt("Z")));
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
