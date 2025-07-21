package dev.xylonity.companions.common.entity.projectile.trigger;

import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.common.entity.projectile.FireRayPieceProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.UUID;

public class FireRayBeamEntity extends Entity implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // NO I WON'T MAKE A GETTER/SETTER FOR EACH SINGLE ATT
    private static final EntityDataAccessor<Float> ORIGIN_X = SynchedEntityData.defineId(FireRayBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ORIGIN_Y = SynchedEntityData.defineId(FireRayBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ORIGIN_Z = SynchedEntityData.defineId(FireRayBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_YAW = SynchedEntityData.defineId(FireRayBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ROT_TICKS = SynchedEntityData.defineId(FireRayBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_PIECES = SynchedEntityData.defineId(FireRayBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SEPARATION = SynchedEntityData.defineId(FireRayBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_PITCH = SynchedEntityData.defineId(FireRayBeamEntity.class, EntityDataSerializers.FLOAT);

    private int age;
    private UUID[] pieceUUIDs;

    public FireRayBeamEntity(EntityType<? extends FireRayBeamEntity> type, Level level) {
        super(type, level);
        this.noPhysics  = true;
        this.pieceUUIDs = new UUID[0];
    }

    public FireRayBeamEntity(ServerLevel lvl, Vec3 origin, float yaw0, float pitch0, int rotTicks, int maxPieces, float sep, SacredPontiffEntity entity) {
        this(CompanionsEntities.FIRE_RAY_BEAM_ENTITY, lvl);
        setPos(origin.x, origin.y, origin.z);
        entityData.set(ORIGIN_X, (float) origin.x);
        entityData.set(ORIGIN_Y, (float) origin.y);
        entityData.set(ORIGIN_Z, (float) origin.z);

        entityData.set(START_YAW,   yaw0);
        entityData.set(START_PITCH, pitch0);
        entityData.set(ROT_TICKS,   rotTicks);
        entityData.set(MAX_PIECES,  maxPieces);
        entityData.set(SEPARATION,  sep);

        this.pieceUUIDs = new UUID[maxPieces];
        spawnPieces(lvl, entity);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ORIGIN_X, 0.0F);
        builder.define(ORIGIN_Y, 0.0F);
        builder.define(ORIGIN_Z, 0.0F);
        builder.define(START_YAW, 0.0F);
        builder.define(ROT_TICKS,  0);
        builder.define(MAX_PIECES, 0);
        builder.define(SEPARATION, 0.0F);
        builder.define(START_PITCH, 0f);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        ;;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag)  {
        ;;
    }

    private void spawnPieces(ServerLevel level, SacredPontiffEntity entity) {
        int maxPieces = entityData.get(MAX_PIECES);
        for (int i = 0; i < maxPieces; i++) {
            FireRayPieceProjectile piece = CompanionsEntities.FIRE_RAY_PIECE_PROJECTILE.create(level);
            if (piece != null) {
                piece.initAsChild(this, i);
                piece.setOwner(entity);
                level.addFreshEntity(piece);
                this.pieceUUIDs[i] = piece.getUUID();
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        if (tickCount % 5 == 0 && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLASH, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
        }

        // Attempt to ease in lol, but there is some visual lag anyway
        float eased = 0.5f * (1f - Mth.cos(((float) age / entityData.get(ROT_TICKS)) * Mth.PI));
        double yaw = Math.toRadians((entityData.get(START_YAW) + eased * 720f) % 360f);
        double pitch = Math.toRadians(entityData.get(START_PITCH));

        // The sequences of pieces (that I assume as full rays) rotate from an initial point to a specified yaw/pitch, and on a
        // hypothetical sphere, the pitch's horizontal is cut, and the entire yaw is rotated x turns (specified on the yaw above)
        Vec3 direction = new Vec3(Math.cos(pitch) * Math.cos(yaw), Math.sin(pitch), Math.cos(pitch) * Math.sin(yaw));
        Vec3 origin = new Vec3(entityData.get(ORIGIN_X), entityData.get(ORIGIN_Y), entityData.get(ORIGIN_Z));

        boolean blocked = false;
        for (int i = 0; i < pieceUUIDs.length; i++) {
            Entity e = ((ServerLevel) level()).getEntity(pieceUUIDs[i]);
            if (!(e instanceof FireRayPieceProjectile piece)) continue;

            Vec3 pos = origin.add(direction.scale(i * entityData.get(SEPARATION)));
            piece.syncPosition(pos);

            boolean solid = !level().getBlockState(BlockPos.containing(pos))
                    .getCollisionShape(level(), BlockPos.containing(pos))
                    .isEmpty();

            if (!blocked && solid) {
                blocked = true;
                piece.setInvisible(false);
            } else if (blocked) {
                piece.setInvisible(true);
            } else {
                piece.setInvisible(false);
            }

            if (i == 0) piece.setInvisible(true);
        }

        if (++age >= 135) discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        ;;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object o) {
        return RenderUtil.getCurrentTick();
    }

}