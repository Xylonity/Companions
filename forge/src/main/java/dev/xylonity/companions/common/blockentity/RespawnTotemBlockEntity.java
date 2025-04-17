package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.RespawnTotemBlock;
import dev.xylonity.companions.common.entity.custom.CroissantDragonEntity;
import dev.xylonity.companions.common.entity.custom.SoulMageEntity;
import dev.xylonity.companions.common.tick.TickScheduler;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RespawnTotemBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int tickCounter = 0;
    private int sonarCounter = 0;
    private boolean captured = false;

    public RespawnTotemBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.RESPAWN_TOTEM.get(), pos, state);
    }

    private final Map<UUID, CompoundTag> savedEntities = new HashMap<>();

    public void captureNearby() {
        if (level == null) return;
        if (level.isClientSide) return;

        for (Entity e : level.getEntitiesOfClass(TamableAnimal.class, new AABB(worldPosition).inflate(10))) {
            if (e.isRemoved() || e instanceof Player) continue;

            CompoundTag data = new CompoundTag();
            e.save(data);
            savedEntities.put(e.getUUID(), data);

            e.getPersistentData().putLong("RespawnTotemPos", worldPosition.asLong());
            e.getPersistentData().putString("RespawnTotemDim", level.dimension().location().toString());
        }

        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        ListTag list = new ListTag();
        savedEntities.forEach((uuid, data) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Uuid", uuid);
            entry.put("Data", data);
            list.add(entry);
        });

        tag.put("RespawnList", list);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        savedEntities.clear();

        ListTag list = tag.getList("RespawnList", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag entry = (CompoundTag) t;
            savedEntities.put(entry.getUUID("Uuid"), entry.getCompound("Data"));
        }

    }

    public static <X extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, X blockEntity) {
        if (!(blockEntity instanceof RespawnTotemBlockEntity t)) return;

        if (state.getValue(RespawnTotemBlock.LIT)) {

            if (t.sonarCounter % 20 == 0) {
                if (level instanceof ServerLevel sv) {
                    for (int i = 0; i < 50; i++) {
                        double dx = (level.random.nextDouble() - 0.5) * 2.0;
                        double dy = (level.random.nextDouble() - 0.5) * 2.0;
                        double dz = (level.random.nextDouble() - 0.5) * 2.0;
                        sv.sendParticles(ParticleTypes.POOF, t.getBlockPos().getX(), t.getBlockPos().getY() + 0.5, t.getBlockPos().getZ(), 1, dx, dy, dz, 0.1);
                        if (i % 5 == 0)
                            sv.sendParticles(CompanionsParticles.TEDDY_TRANSFORMATION.get(), t.getBlockPos().getX(), t.getBlockPos().getY() + 0.5, t.getBlockPos().getZ(), 1, dx, dy, dz, 0.2);
                    }
                }

            }

            if (state.getValue(RespawnTotemBlock.LIT) && !t.captured) {
                t.captureNearby();
                t.captured = true;
            }

            if (t.sonarCounter == 60) {
                level.setBlockAndUpdate(pos, state.setValue(RespawnTotemBlock.LIT, false));
                t.sonarCounter = 0;
                t.captured = false;
            }

            t.sonarCounter++;
        }

        t.tickCounter++;
    }

    public int getTickCounter() {
        return this.tickCounter;
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

}