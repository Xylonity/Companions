package dev.xylonity.companions.common.entity.hostile;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.HostileEntity;
import dev.xylonity.companions.common.entity.ai.antlion.wild.goal.WildAntlionBaseAttackGoal;
import dev.xylonity.companions.common.entity.ai.antlion.wild.goal.WildAntlionBaseLongAttackGoal;
import dev.xylonity.companions.common.entity.ai.antlion.wild.goal.WildAntlionNearestAttackableTarget;
import dev.xylonity.companions.common.entity.companion.AntlionEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsSounds;
import dev.xylonity.knightlib.api.TickScheduler;
import dev.xylonity.knightlib.registry.KnightLibParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class WildAntlionEntity extends HostileEntity implements PlayerRideable {

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation DIG_IN = RawAnimation.begin().thenPlay("dig");
    private final RawAnimation DIG_OUT = RawAnimation.begin().thenPlay("dig_out");
    private final RawAnimation DIG_IDLE = RawAnimation.begin().thenPlay("dig_idle");
    private final RawAnimation DIG_ATTACK = RawAnimation.begin().thenPlay("dig_attack");
    private final RawAnimation DIG_SHOOT = RawAnimation.begin().thenPlay("dig_attack2");

    // base: 0 none, 1 attack, 2 shoot
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(WildAntlionEntity.class, EntityDataSerializers.INT);
    // 0 walk (none), 1 dig_in, 2 underground, 3 dig_out
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(WildAntlionEntity.class, EntityDataSerializers.INT);

    private static final int ANIMATION_BASE_DIG_IN_TICKS = 55;
    private static final int ANIMATION_BASE_DIG_OUT_TICKS = 15;
    private static final int NO_TARGET_MAX_TICKS = 20;

    private int diginCounter;
    private int digoutCounter;
    private int noTargetCounter;

    public WildAntlionEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.diginCounter = -1;
        this.digoutCounter = -1;
        this.noTargetCounter = -1;
    }

    @Override
    public void tick() {

        super.tick();

        if (!level().isClientSide) {

            setNoMovement(isUnderground() || getState() == 1);

            // digIn counter
            if (this.getState() == 1 && diginCounter == -1) {
                this.diginCounter++;
            }

            // digOut counter
            if (this.getState() == 2 && noTargetCounter == -1 && getTarget() == null) {
                this.noTargetCounter++;
            } else if (this.getState() == 2 && noTargetCounter == -1 && getTarget() == null) {
                this.noTargetCounter = -1;
            }

            if (this.diginCounter != -1) this.diginCounter++;
            if (this.digoutCounter != -1) this.digoutCounter++;
            if (this.noTargetCounter != -1) this.noTargetCounter++;

            if (diginCounter == ANIMATION_BASE_DIG_IN_TICKS) {
                cycleState();
                diginCounter = -1;
            }

            // if there is no target for a while, cycling dig out
            if (noTargetCounter == NO_TARGET_MAX_TICKS) {
                cycleState();
                noTargetCounter = -1;
                digoutCounter++;
            }

            if (digoutCounter == ANIMATION_BASE_DIG_OUT_TICKS) {
                cycleState();
                digoutCounter = -1;
            }

            // earthquake effects
            if (diginCounter == 20 || diginCounter == 35 || digoutCounter == 5) {
                earthquake();
            }

        }

    }

    private void earthquake() {
        int radius = 2;
        // manhattan distance
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                int dist = Math.abs(x) + Math.abs(z);
                if (dist >= 1 && dist <= radius) {
                    BlockPos blockPos = blockPosition().offset(x, -1, z);
                    BlockState state = level().getBlockState(blockPos);

                    if (!state.isAir() && state.getBlock() != Blocks.BEDROCK) {
                        if (dist == 1) {
                            spawnFallingBlock(blockPos, state, 0.18);
                        } else {
                            TickScheduler.scheduleServer(level(), () -> spawnFallingBlock(blockPos, state, 0.2), 5);
                        }
                    }
                }

            }

        }

    }

    private void spawnFallingBlock(BlockPos pos, BlockState state, double yDelay) {
        FallingBlockEntity block = new FallingBlockEntity(level(), pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state);

        block.setDeltaMovement(0.0, yDelay, 0.0);

        level().addFreshEntity(block);
        level().removeBlock(pos,false);
    }

    public void cycleState() {
        switch (getState()) {
            case 0 -> setState(1);
            case 1 -> setState(2);
            case 2 -> setState(3);
            case 3 -> setState(0);
        }
    }

    public void setState(int state) {
        this.entityData.set(STATE, state);
    }

    public int getState() {
        return this.entityData.get(STATE);
    }

    public boolean isUnderground() {
        return this.getState() == 2;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new WildAntlionBaseAttackGoal(this, 20, 60));
        this.goalSelector.addGoal(1, new WildAntlionBaseLongAttackGoal(this, 20, 60));

        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.43));

        this.targetSelector.addGoal(1, new WildAntlionNearestAttackableTarget<>(this, Player.class, true));
    }

    public static AttributeSupplier setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.ATTACK_DAMAGE, 7f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int type) {
        this.entityData.set(ATTACK_TYPE, type);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(STATE, 0);
    }

    @Override
    public void move(@NotNull MoverType pType, @NotNull Vec3 pPos) {
        super.move(pType, pPos);
        this.checkInsideBlocks();
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {

        if (!isEntityInFront(player) && player.getItemInHand(hand).getItem() == CompanionsItems.CRYSTALLIZED_BLOOD.get()) {
            if (level().isClientSide) return InteractionResult.SUCCESS;

            if (tameAntlion(player)) {
                tameParticles();
                this.discard();
            } else {
                failureParticles();
            }

        }

        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return CompanionsSounds.ANTLION_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return CompanionsSounds.ANTLION_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CompanionsSounds.ANTLION_IDLE.get();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        playSound(CompanionsSounds.ANTLION_STEPS.get());
    }

    protected boolean isEntityInFront(Entity target) {
        Vec3 toTarget = new Vec3(target.getX(), this.getY(), target.getZ()).subtract(this.position()).normalize();
        double angle = Math.acos(this.getLookAngle().normalize().dot(toTarget)) * (180.0 / Math.PI);
        return angle < (200 / 2f);
    }

    private boolean tameAntlion(Player player) {
        if (level().random.nextFloat() < 0.55f) {

            AntlionEntity antlion = CompanionsEntities.ANTLION.get().create(level());
            if (antlion != null) {
                antlion.moveTo(position());
                antlion.tameInteraction(player);

                level().addFreshEntity(antlion);
            }

            return true;
        }

        return false;
    }

    private void tameParticles() {
        for (int i = 0; i < 20; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 2.0;
            double dy = (this.random.nextDouble() - 0.5) * 2.0;
            double dz = (this.random.nextDouble() - 0.5) * 2.0;
            if (this.level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < 0.65f) level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.1);
                if (level.random.nextFloat() < 0.25f) level.sendParticles(KnightLibParticles.STARSET.get(), this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.2);
            }
        }

    }

    private void failureParticles() {
        for (int i = 0; i < 20; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 2.0;
            double dy = (this.random.nextDouble() - 0.5) * 2.0;
            double dz = (this.random.nextDouble() - 0.5) * 2.0;
            if (this.level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < 0.65f) level.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.025);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

       if (getAttackType() == 1) {
           event.getController().setAnimation(DIG_ATTACK);
       } else if (getAttackType() == 2) {
           event.getController().setAnimation(DIG_SHOOT);
       } else if (getState() == 1) {
           event.getController().setAnimation(DIG_IN);
       } else if (getState() == 2) {
           event.getController().setAnimation(DIG_IDLE);
       } else if (getState() == 3) {
           event.getController().setAnimation(DIG_OUT);
       }  else if (event.isMoving()) {
           event.getController().setAnimation(WALK);
       } else {
           event.getController().setAnimation(IDLE);
       }

        return PlayState.CONTINUE;
    }

}