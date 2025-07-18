package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.entity.projectile.RespawnTotemRingProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RespawnTotemBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public final Map<UUID, CompoundTag> savedEntities = new ConcurrentHashMap<>();
    private final List<RespawnData> pending = new ArrayList<>();

    private int tickCount;
    private boolean isCapturing;
    private int charges;
    private int captureCooldown;

    public RespawnTotemBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.RESPAWN_TOTEM.get(), pos, state);
        this.isCapturing = false;
        this.charges = 0;
        this.captureCooldown = 0;
        this.tickCount = 0;
    }

    public void queueRespawn(CompoundTag nbt, int delay) {
        if (!(level instanceof ServerLevel sv)) return;

        pending.add(new RespawnData(nbt, sv.getGameTime() + delay));
        setChanged();
    }

    public void setCharges(int charges) {
        this.charges = charges;
        this.sync();
    }

    public int getCharges() {
        return this.charges;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void captureNearby() {
        if (level == null) return;
        if (level.isClientSide) return;

        for (Entity e : level.getEntitiesOfClass(TamableAnimal.class, new AABB(worldPosition).inflate(5))) {
            if (e.isRemoved() || e instanceof Player) continue;

            CompoundTag data = new CompoundTag();
            e.save(data);
            savedEntities.put(e.getUUID(), data);

            e.getPersistentData().putLong("RespawnTotemPos", worldPosition.asLong());
            e.getPersistentData().putString("RespawnTotemDim", level.dimension().location().toString());
        }

        setChanged();
    }

    public int getCaptureCooldown() {
        return this.captureCooldown;
    }

    public void setCaptureCooldown(int captureCooldown) {
        this.captureCooldown = captureCooldown;
    }

    public void setCapturing(boolean capturing) {
        if (capturing && this.getCaptureCooldown() > 0) {
            return;
        }

        this.isCapturing = capturing;
    }

    public boolean isCapturing() {
        return this.isCapturing;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Charges", getCharges());

        ListTag list = new ListTag();
        savedEntities.forEach((uuid, data) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Uuid", uuid);
            entry.put("Data", data);
            list.add(entry);
        });

        tag.put("RespawnList", list);

        ListTag list2 = new ListTag();
        for (RespawnData p : pending) {
            CompoundTag e = new CompoundTag();
            e.putLong("When", p.time);
            e.put("Nbt", p.nbt);
            list2.add(e);
        }
        tag.put("RespawnPending", list2);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        savedEntities.clear();
        this.charges = tag.getInt("Charges");

        ListTag list = tag.getList("RespawnList", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag entry = (CompoundTag) t;
            savedEntities.put(entry.getUUID("Uuid"), entry.getCompound("Data"));
        }

        pending.clear();
        ListTag list2 = tag.getList("RespawnPending", Tag.TAG_COMPOUND);
        for (Tag t : list2) {
            CompoundTag e = (CompoundTag) t;
            pending.add(new RespawnData(e.getCompound("Nbt"), e.getLong("When")));
        }

    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.charges = tag.getInt("Charges");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("Charges", getCharges());
        return tag;
    }

    public void sync() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Packet<ClientGamePacketListener> pkt = ClientboundBlockEntityDataPacket.create(this);
        serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(worldPosition), false).forEach(p -> p.connection.send(pkt));
    }

    public static <X extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, X blockEntity) {
        if (!(blockEntity instanceof RespawnTotemBlockEntity t)) return;

        if (level instanceof ServerLevel sv) {
            long now = sv.getGameTime();

            Iterator<RespawnData> it = t.pending.iterator();
            while (it.hasNext()) {
                RespawnData p = it.next();
                if (now >= p.time) {
                    Entity spawned = EntityType.loadEntityRecursive(
                            p.nbt, sv,
                            e -> {
                                BlockPos sPos = findSpawn(sv, pos, 5);
                                double x = (sPos != null ? sPos.getX() + 0.5 : pos.getX() + 0.5);
                                double y = (sPos != null ? sPos.getY() : pos.getY() + 1);
                                double z = (sPos != null ? sPos.getZ() + 0.5 : pos.getZ() + 0.5);
                                e.setPos(x, y, z);
                                return e;
                            });

                    if (spawned != null) {
                        for (int i = 0; i < 20; i++) {
                            double dx = (sv.random.nextDouble() - 0.5) * 2.0;
                            double dy = (sv.random.nextDouble() - 0.5) * 2.0;
                            double dz = (sv.random.nextDouble() - 0.5) * 2.0;
                            sv.sendParticles(ParticleTypes.POOF, spawned.getX(), spawned.getY() + spawned.getBbHeight() * 0.5, spawned.getZ(), 1, dx, dy, dz, 0.1);
                        }

                        t.setCharges(t.getCharges() - 1);
                        sv.addFreshEntity(spawned);
                    }

                    it.remove();
                }
            }
        }

        if (t.isCapturing()) {
            if (level instanceof ServerLevel sv) {

                for (int i = 0; i < 20; i++) {
                    double dx = (sv.random.nextDouble() - 0.5) * 2.0;
                    double dy = (sv.random.nextDouble() - 0.5) * 2.0;
                    double dz = (sv.random.nextDouble() - 0.5) * 2.0;
                    sv.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 1, dx, dy, dz, 0.1);
                }

                RespawnTotemRingProjectile ring = CompanionsEntities.RESPAWN_TOTEM_RING_PROJECTILE.get().create(level);
                if (ring != null) {
                    ring.setPos(pos.getX() + 0.5, t.getRingSpawnY(level, pos) + 0.015, pos.getZ() + 0.5);
                    level.addFreshEntity(ring);
                }
            }

            t.setCharges(CompanionsConfig.RESPAWN_TOTEM_CHARGES);
            t.captureNearby();
            t.setCapturing(false);
            t.setCaptureCooldown(40);
            t.setChanged();
        }

        if (t.getCaptureCooldown() > 0) {
            t.setCaptureCooldown(t.getCaptureCooldown() - 1);
        }

        if (t.tickCount % 10 == 0 && t.getCharges() > 0) {
            double dx = (new Random().nextDouble() - 0.5) * 0.5;
            double dy = (new Random().nextDouble() - 0.5) * 0.5;
            double dz = (new Random().nextDouble() - 0.5) * 0.5;
            if (level instanceof ServerLevel sv) {
                sv.sendParticles(CompanionsParticles.RESPAWN_TOTEM.get(), t.getBlockPos().getX() + 0.5, t.getBlockPos().getY() + 1.5 * Math.random(), t.getBlockPos().getZ() + 0.5, 1, dx, dy, dz, 0.02);
            }
        }

        t.tickCount++;
    }

    private static BlockPos findSpawn(ServerLevel level, BlockPos center, int radius) {
        for (int i = 0; i < 80; i++) {
            int dx = new Random().nextInt(radius * 2 + 1) - radius;
            int dz = new Random().nextInt(radius * 2 + 1) - radius;
            int x = center.getX() + dx;
            int z = center.getZ() + dz;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            BlockPos ret = new BlockPos(x, y, z);
            if (level.getBlockState(ret).isAir() && level.getBlockState(ret.above()).isAir()) {
                return ret;
            }
        }

        return null;
    }

    private double getRingSpawnY(Level level, BlockPos basePos) {
        BlockPos below1 = basePos.below();
        if (!level.getBlockState(below1).isAir()) {

            BlockPos below2 = below1.below();
            if (!level.getBlockState(below2).isAir()) {
                int airCount = 0;
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    if (level.getBlockState(below2.relative(dir)).isAir()) {
                        airCount++;
                    }
                }

                if (airCount >= 3) return below2.getY();
            }

            int airCount = 0;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (level.getBlockState(below1.relative(dir)).isAir()) {
                    airCount++;
                }
            }

            if (airCount >= 3) return below1.getY();
        }

        return basePos.getY();
    }


    public int getTickCount() {
        return this.tickCount;
    }

    @Override
    public double getTick(Object o) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private static class RespawnData {
        CompoundTag nbt;
        long time;

        RespawnData(CompoundTag nbt, long time) {
            this.nbt = nbt;
            this.time = time;
        }
    }

}