package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionRandomStrollGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.entity.ai.puppet.glove.goal.PuppetGloveAttackGoal;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class PuppetGloveEntity extends CompanionEntity {

    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation RECEIVE_PUPPET = RawAnimation.begin().thenPlay("recieve_puppet");

    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(PuppetGloveEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TRANSFORMING = SynchedEntityData.defineId(PuppetGloveEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int TRANSFORMATION_TICKS = 25;

    private int transformationCounter;

    public PuppetGloveEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.transformationCounter = -1;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (transformationCounter != -1) transformationCounter++;
            if (transformationCounter == TRANSFORMATION_TICKS) tameGlove(getOwner());
        }
    }

    @Override
    protected boolean canThisCompanionWork() {
        return false;
    }

    @Override
    protected int sitAnimationsAmount() {
        return 1;
    }

    @Override
    protected boolean shouldKeepChunkLoaded() {
        return CompanionsConfig.PUPPET_GLOVE_KEEP_CHUNK_LOADED;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        playSound(CompanionsSounds.PUPPET_GLOVE_STEP.get());
    }

    public static AttributeSupplier.Builder setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.PUPPET_GLOVE_MAX_LIFE)
                .add(Attributes.ATTACK_DAMAGE, CompanionsConfig.PUPPET_GLOVE_DAMAGE)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new PuppetGloveAttackGoal(this, 10, 40));

        this.goalSelector.addGoal(3, new CompanionFollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new CompanionRandomStrollGoal(this, 0.43));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {

        if (isTransforming()) return InteractionResult.PASS;

        Item item = player.getItemInHand(hand).getItem();
        if (item == CompanionsBlocks.EMPTY_PUPPET.get().asItem() && isTame() && getOwner() != null && getOwner() == player) {
            if (level().isClientSide) return InteractionResult.SUCCESS;

            setOwnerUUID(player.getUUID());
            setTransforming(true);
            // start transformation counter
            this.transformationCounter++;

            return InteractionResult.SUCCESS;
        }

        if (level().isClientSide) return InteractionResult.SUCCESS;

        if (handleDefaultMainActionAndHeal(player, hand)) {
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private void tameGlove(LivingEntity player) {
        PuppetEntity puppet = CompanionsEntities.PUPPET.create(level());
        if (puppet != null) {
            puppet.moveTo(position());

            if (player instanceof Player p) {
                puppet.tameInteraction(p);
            } else if (getOwner() == null && getOwnerUUID() != null) {
                puppet.setOwnerUUID(getOwnerUUID());
            }

            level().addFreshEntity(puppet);
        }

        generatePoofParticles();
        this.discard();
    }

    private void generatePoofParticles() {
        for (int i = 0; i < 30; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 1.25;
            double dy = (this.random.nextDouble() - 0.5) * 1.25;
            double dz = (this.random.nextDouble() - 0.5) * 1.25;
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, position().x, this.getY() + getBbHeight() * Math.random(), position().z, 1, dx, dy, dz, 0.1);
            }
        }

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_ATTACKING, false);
        builder.define(TRANSFORMING, false);
    }

    public boolean isAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(IS_ATTACKING, attacking);
    }

    public boolean isTransforming() {
        return this.entityData.get(TRANSFORMING);
    }

    public void setTransforming(boolean transforming) {
        this.entityData.set(TRANSFORMING, transforming);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (isTransforming()) {
            event.getController().setAnimation(RECEIVE_PUPPET);
        } else if (isAttacking()) {
          event.getController().setAnimation(ATTACK);
        } else if (this.getMainAction() == 0) {
            event.getController().setAnimation(SIT);
        } else if (event.isMoving()) {
            event.getController().setAnimation(WALK);
        } else {
            event.getController().setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}