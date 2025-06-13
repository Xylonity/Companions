package dev.xylonity.companions.common.entity.summon;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.froggy.summon.goal.FireworkToadGoal;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class EnderFrogEntity extends CompanionSummonEntity {

    private static final EntityDataAccessor<Boolean> CAN_EXPLODE = SynchedEntityData.defineId(EnderFrogEntity.class, EntityDataSerializers.BOOLEAN);

    public EnderFrogEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    public void setCanExplode(boolean canExplode) {
        this.entityData.set(CAN_EXPLODE, canExplode);
    }

    public boolean canExplode() {
        return this.entityData.get(CAN_EXPLODE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CAN_EXPLODE, false);
    }

    @Override
    public void tick() {
        super.tick();

        //if (level() instanceof ClientLevel sv)
        //    for (int i = 0; i < 5; i++) {
        //        sv.addParticle(ParticleTypes.SMOKE, getX(), getY() + getBbHeight(), getZ(),
        //                0.05 * Math.random() - 0.025, -0.2, 0.05 * Math.random() - 0.025);
        //    }

        if (this.horizontalCollision || this.verticalCollision && canExplode()) {
            explode();
            this.discard();
        }

    }

    private void explode() {
        if (!(level() instanceof ServerLevel sv)) return;

        double x = getX();
        double y = getY() + getBbHeight() / 2;
        double z = getZ();

        sv.sendParticles(CompanionsParticles.FIREWORK_TOAD.get(), x, y, z, 1, 0,0,0, 0.1);
        sv.sendParticles(ParticleTypes.FIREWORK, x, y, z, 20, 0,0,0, 0.05);
        sv.sendParticles(ParticleTypes.SMOKE, x, y, z, 40, 0,0,0, 0.12);
        for (int i = 0; i < 40; i++) {
            sv.sendParticles(ParticleTypes.CLOUD, x, y, z,
                    1,
                    0.2 * (Math.random() - 0.5),
                    0.2 * (Math.random() - 0.5),
                    0.2 * (Math.random() - 0.5),
                    0.1
            );
        }

        sv.playSound(null, x, y, z, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.AMBIENT, 4.0F, 1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        return PlayState.CONTINUE;
    }

}