package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionRandomStrollGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.tesla.ConnectionTarget;
import dev.xylonity.companions.common.tesla.TeslaNetwork;
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
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
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
import java.util.concurrent.ConcurrentHashMap;

public class DinamoEntity extends CompanionEntity implements GeoEntity {

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

    private static final EntityDataAccessor<CompoundTag> OUTGOING_CONNECTIONS = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.COMPOUND_TAG);

    private final Set<ConnectionTarget> outgoing = ConcurrentHashMap.newKeySet();

    private final ITeslaGeneratorBehaviour pulseBehavior;
    private final ITeslaGeneratorBehaviour attackBehavior;

    private boolean teslaIndexed = false;

    public DinamoEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.pulseBehavior  = new DinamoPulseBehaviour();
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

    public ConnectionTarget asConnectionTarget() {
        return ConnectionTarget.forEntity(getUUID(), level().dimension().location());
    }

    public Set<ConnectionTarget> getOutgoing() {
        if (level().isClientSide) {
            return deserializeOutgoing(this.entityData.get(OUTGOING_CONNECTIONS));
        }

        return outgoing;
    }

    public void addOutgoingConnection(ConnectionTarget target) {
        outgoing.add(target);
        syncOutgoingToClient();
    }

    public void removeOutgoingConnection(ConnectionTarget target) {
        outgoing.remove(target);
        syncOutgoingToClient();
    }

    private void syncOutgoingToClient() {
        this.entityData.set(OUTGOING_CONNECTIONS, serializeOutgoing());
    }

    private CompoundTag serializeOutgoing() {
        final CompoundTag tag = new CompoundTag();
        final ListTag list = new ListTag();
        for (final ConnectionTarget connectionTarget : outgoing) {
            list.add(connectionTarget.serialize());
        }

        tag.put("Connections", list);
        return tag;
    }

    private static Set<ConnectionTarget> deserializeOutgoing(CompoundTag tag) {
        final Set<ConnectionTarget> result = ConcurrentHashMap.newKeySet();
        if (tag != null && tag.contains("Connections", Tag.TAG_LIST)) {
            final ListTag list = tag.getList("Connections", Tag.TAG_COMPOUND);
            for (final Tag tagg : list) {
                result.add(ConnectionTarget.deserialize((CompoundTag) tagg));
            }

        }

        return result;
    }

    public void handleNodeSelection(ConnectionTarget thisNode, ConnectionTarget nodeToConnect) {
        addOutgoingConnection(nodeToConnect);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        final ListTag list = new ListTag();
        for (ConnectionTarget t : outgoing) {
            list.add(t.serialize());
        }

        tag.put("OutgoingConnections", list);
        tag.putInt("AnimationStartTick", getAnimationStartTick());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        outgoing.clear();
        if (tag.contains("OutgoingConnections", Tag.TAG_LIST)) {
            final ListTag list = tag.getList("OutgoingConnections", Tag.TAG_COMPOUND);
            for (final Tag tagg : list) {
                outgoing.add(ConnectionTarget.deserialize((CompoundTag) tagg));
            }

        }

        syncOutgoingToClient();

        if (!level().isClientSide) {
            TeslaNetwork.get(level()).indexEntityOutgoing(asConnectionTarget(), outgoing);
            teslaIndexed = true;
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
        return CompanionsConfig.DINAMO_KEEP_CHUNK_LOADED;
    }

    @Override
    public void die(@NotNull DamageSource pCause) {
        super.die(pCause);

        if (!level().isClientSide) {
            final TeslaNetwork network = TeslaNetwork.get(level());
            final ConnectionTarget self = asConnectionTarget();

            for (final ConnectionTarget target : new HashSet<>(outgoing)) {
                network.onConnectionRemoved(self, target);
            }

            outgoing.clear();

            for (final ConnectionTarget source : network.getIncoming(self)) {
                if (source.isBlock()) {
                    final AbstractTeslaBlockEntity blockEntity = network.getBlockEntity(source.blockPos());
                    if (blockEntity != null) {
                        blockEntity.removeOutgoing(self);
                        blockEntity.sync();
                    }

                }

            }

            network.removeEntityNode(self);
        }

    }

    public static AttributeSupplier.Builder setAttributes() {
        return Raider.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.DINAMO_MAX_LIFE)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (isTame() && !this.level().isClientSide && hand == InteractionHand.MAIN_HAND && getOwner() == player
                && player.getMainHandItem().getItem() != CompanionsItems.WRENCH.get()) {

            if (player.isShiftKeyDown() && getMainAction() != 0) {
                setShouldAttack(!shouldAttack());
                if (shouldAttack()) {
                    player.displayClientMessage(Component.translatable("dinamo.companions.client_message.attack").withStyle(ChatFormatting.GREEN), true);
                }
                else {
                    player.displayClientMessage(Component.translatable("dinamo.companions.client_message.no_attack").withStyle(ChatFormatting.GREEN), true);
                }

            }
            else {
                if (level().isClientSide) {
                    return InteractionResult.SUCCESS;
                }

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

        if (level().isClientSide) {
            if (this.getAttackCycleCounter() >= scaleAttackCooldown(ITeslaUtil.DINAMO_ATTACK_DELAY)) {
                this.entitiesToAttack.clear();
            }

        }

        if (getMainAction() == 0) {
            pulseBehavior.tick(this);
        }
        else {
            attackBehavior.tick(this);
        }

        // Populates the client-side target list to render the outgoing arcs
        if (level().isClientSide) {
            if (!getTargetIds().isEmpty() && !getTargetIds().isBlank()) {
                for (final String string : getTargetIds().split(";")) {
                    try {
                        if (level().getEntity(Integer.parseInt(string)) instanceof LivingEntity e) {
                            this.entitiesToAttack.add(e);
                        }

                    }
                    catch (NumberFormatException ignored) {
                        ;;
                    }

                }

            }

        }

        // Lazy index with TeslaNetwork if not yet done
        if (!level().isClientSide && !teslaIndexed && !outgoing.isEmpty()) {
            TeslaNetwork.get(level()).indexEntityOutgoing(asConnectionTarget(), outgoing);
            teslaIndexed = true;
        }

    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    public void setActive(boolean active) {
        this.entityData.set(ACTIVE, active);
    }

    public boolean isActive() {
        return this.entityData.get(ACTIVE);
    }

    public String getTargetIds() {
        return this.entityData.get(TARGET_IDS);
    }

    public void setTargetIds(String ids) {
        this.entityData.set(TARGET_IDS, ids);
    }

    public boolean isActiveForAttack() {
        return this.entityData.get(ATTACK_ACTIVE);
    }

    public void setActiveForAttack(boolean active) {
        this.entityData.set(ATTACK_ACTIVE, active);
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

    public boolean shouldAttack() {
        return this.entityData.get(SHOULD_ATTACK);
    }

    public void setShouldAttack(boolean v) {
        this.entityData.set(SHOULD_ATTACK, v);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
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
        this.entityData.define(OUTGOING_CONNECTIONS, new CompoundTag());
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
        }
        else if (event.isMoving()) {
            event.getController().setAnimation(WALK);
        }
        else {
            event.getController().setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}
