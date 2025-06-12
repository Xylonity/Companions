package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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

    public RecallPlatformBlockEntity(BlockPos pos, BlockState st) {
        super(CompanionsBlockEntities.RECALL_PLATFORM.get(), pos, st);
    }

    public static <T extends BlockEntity>
    void tick(Level lvl, BlockPos pos, BlockState st, T t) {
        if (!(t instanceof RecallPlatformBlockEntity platform)) return;

        if (platform.cooldown > 0) platform.cooldown--;
        platform.tickCount++;
    }

    public void onStepped(ServerPlayer player) {
        if (cooldown > 0) return;

        Level lvl = getLevel();
        if (lvl == null) return;

        List<BlockPos> shuffled = new ArrayList<>(partnerPositions);
        Collections.shuffle(shuffled, new Random());

        for (BlockPos target : shuffled) {
            if (!(lvl.getBlockEntity(target) instanceof RecallPlatformBlockEntity otherPlatform)) {
                partnerPositions.remove(target);
                setChanged();
                continue;
            }

            TeslaConnectionManager mgr = TeslaConnectionManager.getInstance();
            if (!mgr.getConnectedComponent(this.asConnectionNode()).contains(otherPlatform.asConnectionNode())) {
                partnerPositions.remove(target);
                setChanged();
                continue;
            }

            player.teleportTo(target.getX() + .5,
                    target.getY() + 1,
                    target.getZ() + .5);

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
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

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
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

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

    @Override public @NotNull Vec3 electricalChargeOriginOffset() {
        return Vec3.ZERO;
    }

    @Override public @NotNull Vec3 electricalChargeEndOffset() {
        return new Vec3(0, .5, 0);
    }

}
