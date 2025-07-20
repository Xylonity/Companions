package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class ScrollProjectile extends BaseProjectile {

    private final RawAnimation SPAWN = RawAnimation.begin().thenPlay("spawn");

    // 0 none, 1 firework toad, 2 nether bullfrog, 3 ender frog, 4 ember pole, 5 bubble frog
    private static final EntityDataAccessor<Integer> ENTITY_TO_SPAWN = SynchedEntityData.defineId(ScrollProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(ScrollProjectile.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> SECOND_OWNER_UUID = SynchedEntityData.defineId(ScrollProjectile.class, EntityDataSerializers.OPTIONAL_UUID);

    public static final int WHEN_TO_SUMMON = 30;

    public ScrollProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount == WHEN_TO_SUMMON && !level().isClientSide) {
            spawnParticles();
            CompanionSummonEntity e = getEntityToSpawn();
            if (e != null && getOwnerUUID() != null) {
                e.setOwnerUUID(getOwnerUUID());
                e.setSecondOwnerUUID(getSecondOwnerUUID());
                e.setTame(true, false);
                e.moveTo(position());
                level().addFreshEntity(e);
            }

            playSound(CompanionsSounds.SCROLL_SOUND.get(), 0.75f, 1f);
        }

    }

    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(pUuid));
    }

    public UUID getSecondOwnerUUID() {
        return this.entityData.get(SECOND_OWNER_UUID).orElse(null);
    }

    public void setSecondOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(SECOND_OWNER_UUID, Optional.ofNullable(pUuid));
    }

    private CompanionSummonEntity getEntityToSpawn() {
        return switch (this.entityData.get(ENTITY_TO_SPAWN)) {
            case 1 -> CompanionsEntities.FIREWORK_TOAD.get().create(level());
            case 2 -> CompanionsEntities.NETHER_BULLFROG.get().create(level());
            case 3 -> CompanionsEntities.ENDER_FROG.get().create(level());
            case 4 -> CompanionsEntities.EMBER_POLE.get().create(level());
            case 5 -> CompanionsEntities.BUBBLE_FROG.get().create(level());
            default -> null;
        };
    }

    private void spawnParticles() {
        for (int i = 0; i < 10; i++) {
            double dx = (this.level().random.nextDouble() - 0.5) * 0.75;
            double dy = (this.level().random.nextDouble() - 0.5) * 0.75;
            double dz = (this.level().random.nextDouble() - 0.5) * 0.75;
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, position().x, position().y + getBbHeight(), position().z, 1, dx, dy, dz, 0.1);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ENTITY_TO_SPAWN, 0);
        builder.define(OWNER_UUID, Optional.empty());
        builder.define(SECOND_OWNER_UUID, Optional.empty());
    }

    public void setEntityToSpawn(int id) {
        this.entityData.set(ENTITY_TO_SPAWN, id);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.setAnimation(SPAWN);
        return PlayState.CONTINUE;
    }

    @Override
    protected int baseLifetime() {
        return 47;
    }

}
