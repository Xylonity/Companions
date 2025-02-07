package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.illagergolem.TeslaConnectionManager;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.*;

public class DinamoEntity extends CompanionEntity implements GeoEntity {
    public List<Entity> visibleEntities = new ArrayList<>();
    private final TeslaConnectionManager connectionManager;

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("roll");

    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> CONNECTED_ENTITIES = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<CompoundTag> CONNECTIONS = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public static final EntityDataAccessor<Integer> TEST_TIMER = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TICKCOUNT = SynchedEntityData.defineId(DinamoEntity.class, EntityDataSerializers.INT);

    private static final int TIME_PER_ACTIVATION = 14;
    public static final int ELECTRICAL_CHARGE_DURATION = 8;
    private static final int MAX_RADIUS = 10;

    public DinamoEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
        this.connectionManager = TeslaConnectionManager.getInstance();
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 0.5D, 5.0F, 2.0F, false));
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
        tag.putInt("TickCount", getTickCount());
        tag.putInt("AnimationStartTick", getAnimationStartTick());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        setAnimationStartTick(tag.getInt("TickCount"));
        setAnimationStartTick(tag.getInt("AnimationStartTick"));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("TickCount")) {
            setTickCount(tag.getInt("TickCount"));
        } else {
            setTickCount(0);
        }

        if (tag.contains("AnimationStartTick")) {
            setAnimationStartTick(tag.getInt("AnimationStartTick"));
        } else {
            setAnimationStartTick(0);
        }

        TeslaConnectionManager.getInstance().getOutgoing(asConnectionNode()).clear();
        TeslaConnectionManager.getInstance().getIncoming(asConnectionNode()).clear();

        if (tag.contains("OutgoingConnections")) {
            ListTag outgoingList = tag.getList("OutgoingConnections", 10);

            for (Tag t : outgoingList) {
                TeslaConnectionManager.ConnectionNode node = TeslaConnectionManager.ConnectionNode.deserialize((CompoundTag) t);
                TeslaConnectionManager.getInstance().addConnection(asConnectionNode(), node);
            }
        }

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
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) this.setTickCount(this.tickCount);

        if (getTickCount() % TIME_PER_ACTIVATION == 0) {
            setActive(true);
        }

        if (isActive() && getTickCount() % TIME_PER_ACTIVATION >= ELECTRICAL_CHARGE_DURATION) {
            setActive(false);
            visibleEntities.clear();
        }

        if (this.isActive() && this.level() instanceof ServerLevel level) {
            for (TeslaConnectionManager.ConnectionNode connectionNode : TeslaConnectionManager.getInstance().getOutgoing(this.asConnectionNode())) {
                if (connectionNode.isEntity()) {
                    Entity connectedEntity = level.getEntity(connectionNode.getEntityId());
                    Vec3 start = this.position().add(0.0, this.getBbHeight() * 0.5D, 0.0);

                    if (connectedEntity != null) {

                        Vec3 end = connectedEntity.position().add(0.0, connectedEntity.getBbHeight() * 0.5D, 0.0);

                        AABB segmentAABB = new AABB(start, end).inflate(1.0D);

                        List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(
                                LivingEntity.class,
                                segmentAABB,
                                e -> e != this && e != connectedEntity && e.isAlive()
                        );

                        for (LivingEntity victim : nearbyEntities) {
                            if (isEntityNearLine(start, end, victim, 0.75D)) {
                                this.doHurtTarget(victim);
                                if (random.nextFloat() <= 0.2) {
                                    victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0));
                                }
                            }
                        }
                    }
                } else if (connectionNode.isBlock()) {
                    Vec3 start = this.position().add(0.0, this.getBbHeight() * 0.5D, 0.0);
                    Vec3 end = connectionNode.getBlockPos().getCenter().add(0.0, 0.3D, 0.0);

                    AABB segmentAABB = new AABB(start, end).inflate(1.0D);

                    List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(
                            LivingEntity.class,
                            segmentAABB,
                            e -> e != this && e.isAlive()
                    );

                    for (LivingEntity victim : nearbyEntities) {
                        if (isEntityNearLine(start, end, victim, 0.75D)) {
                            this.doHurtTarget(victim);
                            if (random.nextFloat() <= 0.2) {
                                victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0));
                            }
                        }
                    }
                }
            }

        }

    }

    public static boolean isEntityNearLine(Vec3 start, Vec3 end, Entity entity, double threshold) {
        Vec3 entityPos = entity.position();

        Vec3 ab = end.subtract(start);
        Vec3 ac = entityPos.subtract(start);

        double abLengthSq = ab.lengthSqr();
        if (abLengthSq < 1e-8) {
            return entityPos.distanceTo(start) <= threshold;
        }

        double t = ac.dot(ab) / abLengthSq;
        t = Mth.clamp(t, 0.0D, 1.0D);

        Vec3 projection = start.add(ab.scale(t));

        double distance = entityPos.distanceTo(projection);

        return distance <= threshold;
    }

    public void setActive(boolean active) {
        if (active && !this.isActive()) {
            setAnimationStartTick(getTickCount());
        }

        this.entityData.set(ACTIVE, active);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public int getAnimationStartTick() {
        return this.entityData.get(TEST_TIMER);
    }

    public void setAnimationStartTick(int tick) {
        this.entityData.set(TEST_TIMER, tick);
    }

    public int getTickCount() {
        return this.entityData.get(TICKCOUNT);
    }

    public void setTickCount(int tick) {
        this.entityData.set(TICKCOUNT, tick);
    }

    public boolean isActive() {
        return this.entityData.get(ACTIVE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTIVE, false);
        this.entityData.define(CONNECTED_ENTITIES, "");
        this.entityData.define(TEST_TIMER, 0);
        this.entityData.define(CONNECTIONS, new CompoundTag());
        this.entityData.define(TICKCOUNT, 0);
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
        if (event.isMoving()) {
            event.getController().setAnimation(WALK);
        } else {
            event.getController().setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}