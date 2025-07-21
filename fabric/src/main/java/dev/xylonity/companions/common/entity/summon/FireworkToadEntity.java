package dev.xylonity.companions.common.entity.summon;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.SummonFrogEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.goal.FireworkToadGoal;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.goal.SummonHopToOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsSummonHurtTargetGoal;
import dev.xylonity.companions.common.util.interfaces.IFrogJumpUtil;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class FireworkToadEntity extends SummonFrogEntity implements IFrogJumpUtil {

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation FLY = RawAnimation.begin().thenPlay("fly");
    private final RawAnimation ROT = RawAnimation.begin().thenPlayAndHold("rot");

    private static final EntityDataAccessor<Vector3f> PARABOLA_CENTER = SynchedEntityData.defineId(FireworkToadEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Boolean> FLY_ENABLED = SynchedEntityData.defineId(FireworkToadEntity.class, EntityDataSerializers.BOOLEAN);

    public FireworkToadEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PARABOLA_CENTER, new Vector3f(0F, 0F, 0F));
        builder.define(FLY_ENABLED, false);
    }

    public void setParabolaCenter(Vec3 center) {
        if (center == null) {
            this.entityData.set(PARABOLA_CENTER, new Vector3f(0F, 0F, 0F));
        } else {
            this.entityData.set(PARABOLA_CENTER, new Vector3f((float) center.x, (float) center.y, (float) center.z));
        }
    }

    private boolean isFlying() {
        return this.entityData.get(FLY_ENABLED);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLY_ENABLED, flying);
    }

    @Override
    public void tick() {
        super.tick();

        // trail
        if (isFlying()) {
            if (tickCount % 3 == 0) {
                level().addParticle(ParticleTypes.POOF, getX(), getY() + getBbHeight() * 0.15, getZ(), 0, -0.05, 0);
            }
        }

    }

    public Vec3 getParabolaCenter() {
        Vector3f v = this.entityData.get(PARABOLA_CENTER);
        if (v.x() == 0F && v.y() == 0F && v.z() == 0F) {
            return null;
        }

        return new Vec3(v.x(), v.y(), v.z());
    }

    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        //this.goalSelector.addGoal(2, new FireworkToadBomberGoal(this));
        this.goalSelector.addGoal(2, new FireworkToadGoal(this, 20, 80));

        this.goalSelector.addGoal(4, new SummonHopToOwnerGoal<>(this, 0.725D, 6.0F, 2.0F, false));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsSummonHurtTargetGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "rot_controller", state -> PlayState.STOP).triggerableAnim("rot", ROT));
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return CompanionsSounds.SMALL_FROG_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return CompanionsSounds.SMALL_FROG_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CompanionsSounds.SMALL_FROG_IDLE.get();
    }

    @Override
    protected SoundEvent jumpSound() {
        return CompanionsSounds.SMALL_FROG_JUMP.get();
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (isFlying()) {
            event.setAnimation(FLY);
        } else if (getCycleCount() >= 0) {
            event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void setCanAttack(boolean canAttack) {

    }

    @Override
    public int getAttackType() {
        return 0;
    }

}