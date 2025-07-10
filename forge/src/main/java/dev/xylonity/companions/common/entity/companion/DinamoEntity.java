package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionRandomStrollGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.tesla.behaviour.dinamo.DinamoAttackBehaviour;
import dev.xylonity.companions.common.tesla.behaviour.dinamo.DinamoPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.*;

public class DinamoEntity extends CompanionEntity implements GeoEntity {
    private final TeslaConnectionManager connectionManager;
    public List<LivingEntity> entitiesToAttack = new ArrayList<>();

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("roll");
    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");

    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CYCLE_COUNTER = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANIMATION_START_TICK = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ATTACK_ACTIVE = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_CYCLE_COUNTER = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> TARGET_IDS = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Boolean> SHOULD_ATTACK = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.BOOLEAN);

    private final ITeslaGeneratorBehaviour pulseBehavior;
    private final ITeslaGeneratorBehaviour attackBehavior;

    public DinamoEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.connectionManager = TeslaConnectionManager.getInstance();
        this.pulseBehavior = new DinamoPulseBehaviour();
        this.attackBehavior = new DinamoAttackBehaviour();
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(2, new CompanionFollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false));
        this.goalSelector.addGoal(2, new CompanionRandomStrollGoal(this, 0.43));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this) {
            @Override
            public void start() {
                super.start();
                DinamoEntity.this.setTargetIds(getTargetIds() + getOwnerLastHurt().getId() + ";");
            }
        });
    }

    public TeslaConnectionManager.ConnectionNode asConnectionNode() {
        return TeslaConnectionManager.ConnectionNode.forEntity(getUUID(), level().dimension().location());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag outgoing = new ListTag();

        for (TeslaConnectionManager.ConnectionNode node : TeslaConnectionManager.getInstance().getOutgoing(asConnectionNode())) {
            outgoing.add(node.serialize());
        }

        tag.put("OutgoingConnections", outgoing);
        tag.putInt("AnimationStartTick", getAnimationStartTick());
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
        return CompanionsConfig.DINAMO_KEEP_CHUNK_LOADED;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        TeslaConnectionManager.ConnectionNode me = asConnectionNode();
        connectionManager.getOutgoing(me).clear();
        connectionManager.getIncoming(me).clear();

        if (tag.contains("OutgoingConnections")) {
            ListTag outgoingList = tag.getList("OutgoingConnections", 10);
            for (Tag t : outgoingList) {
                TeslaConnectionManager.ConnectionNode node = TeslaConnectionManager.ConnectionNode.deserialize((CompoundTag) t);
                connectionManager.addConnection(me, node, true);
            }
        }

        connectionManager.recalculateDistances();
    }

    @Override
    public void die(@NotNull DamageSource pCause) {
        super.die(pCause);

        Set<TeslaConnectionManager.ConnectionNode> outNodes = new HashSet<>(connectionManager.getOutgoing(asConnectionNode()));
        for (TeslaConnectionManager.ConnectionNode target : outNodes) {
            connectionManager.removeConnection(asConnectionNode(), target);
        }

        Set<TeslaConnectionManager.ConnectionNode> inNodes = new HashSet<>(connectionManager.getIncoming(asConnectionNode()));
        for (TeslaConnectionManager.ConnectionNode source : inNodes) {
            connectionManager.removeConnection(source, asConnectionNode());
        }

        connectionManager.removeConnectionNode(asConnectionNode());
        connectionManager.recalculateDistances();

    }

    public static AttributeSupplier setAttributes() {
        return Raider.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 65.0D)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (isTame() && !this.level().isClientSide && hand == InteractionHand.MAIN_HAND && getOwner() == player && player.getMainHandItem().getItem() != CompanionsItems.WRENCH.get()) {
            if (player.isShiftKeyDown() && getMainAction() != 0) {
                setShouldAttack(!shouldAttack());

                if (shouldAttack()) {
                    player.displayClientMessage(Component.translatable("dinamo.companions.client_message.attack").withStyle(ChatFormatting.GREEN), true);
                } else {
                    player.displayClientMessage(Component.translatable("dinamo.companions.client_message.no_attack").withStyle(ChatFormatting.GREEN), true);
                }

            } else {
                if (level().isClientSide) return InteractionResult.SUCCESS;

                handleDefaultMainActionAndHeal(player, hand);
            }

            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.isAlive()) {
            setActive(false);
        }

        // clears the target list on the client upon the cycle end of life
        if (level().isClientSide) {
            if (this.getAttackCycleCounter() >= ITeslaUtil.DINAMO_ATTACK_DELAY) {
                this.entitiesToAttack.clear();
            }
        }

        if (getMainAction() == 0) {
            pulseBehavior.tick(this);
        } else {
            attackBehavior.tick(this);
        }

        // populating the server side cached target entities to the client list (to make the ray visible)
        if (level().isClientSide) {
            if (!getTargetIds().isEmpty() && !getTargetIds().isBlank()) {
                for (String s : getTargetIds().split(";")) {
                    if (level().getEntity(Integer.parseInt(s)) instanceof LivingEntity e) {
                        this.entitiesToAttack.add(e);
                    }
                }
            }

        }

    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    public void setActive(boolean active) {
        this.entityData.set(ACTIVE, active);
    }

    public String getTargetIds() {
        return this.entityData.get(TARGET_IDS);
    }

    public void setTargetIds(String uuid) {
        this.entityData.set(TARGET_IDS, uuid);
    }


    public boolean isActiveForAttack() {
        return this.entityData.get(ATTACK_ACTIVE);
    }

    public void setActiveForAttack(boolean active) {
        this.entityData.set(ATTACK_ACTIVE, active);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public int getAnimationStartTick() {
        return this.entityData.get(ANIMATION_START_TICK);
    }

    public void setAnimationStartTick(int tick) {
        this.entityData.set(ANIMATION_START_TICK, tick);
    }

    public int getCycleCounter() {
        return this.entityData.get(CYCLE_COUNTER);
    }

    public void setCycleCounter(int tick) {
        this.entityData.set(CYCLE_COUNTER, tick);
    }

    public int getAttackCycleCounter() {
        return this.entityData.get(ATTACK_CYCLE_COUNTER);
    }

    public void setAttackCycleCounter(int tick) {
        this.entityData.set(ATTACK_CYCLE_COUNTER, tick);
    }

    public boolean isActive() {
        return this.entityData.get(ACTIVE);
    }

    public boolean shouldAttack() {
        return this.entityData.get(SHOULD_ATTACK);
    }

    public void setShouldAttack(boolean shouldAttack) {
        this.entityData.set(SHOULD_ATTACK, shouldAttack);
    }

    public void handleNodeSelection(TeslaConnectionManager.ConnectionNode thisNode, TeslaConnectionManager.ConnectionNode nodeToConnect) {
        connectionManager.addConnection(thisNode, nodeToConnect);
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        super.playStepSound(pPos, pState);
        this.playSound(CompanionsSounds.DINAMO_STEP.get(), 0.07875f, 1f);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CompanionsSounds.DINAMO_IDLE.get();
    }

    @Override
    protected void playHurtSound(@NotNull DamageSource pSource) {
        playSound(CompanionsSounds.DINAMO_HURT.get(), 0.25f, 1f);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return CompanionsSounds.DINAMO_DEATH.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 400;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTIVE, false);
        this.entityData.define(CYCLE_COUNTER, 0);
        this.entityData.define(ANIMATION_START_TICK, 0);
        this.entityData.define(ATTACK_ACTIVE, true);
        this.entityData.define(ATTACK_CYCLE_COUNTER, 0);
        this.entityData.define(SHOULD_ATTACK, true);
        this.entityData.define(TARGET_IDS, "");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackcontroller", 1, this::attackPredicate));
    }

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> event) {
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (getMainAction() == 0) {
            event.getController().setAnimation(SIT);
        } else if (event.isMoving()) {
            event.getController().setAnimation(WALK);
        } else {
            event.getController().setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}