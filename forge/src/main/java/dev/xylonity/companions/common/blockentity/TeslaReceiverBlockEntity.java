package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.entity.ai.illagergolem.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.IActivable;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.*;

public class TeslaReceiverBlockEntity extends BlockEntity implements GeoBlockEntity, IActivable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final TeslaConnectionManager connectionManager;

    private static final int TIME_PER_ACTIVATION = 14;
    public static final int ELECTRICAL_CHARGE_DURATION = 8;

    public int distance;
    public int tickCount;
    public boolean hasSignal;
    private boolean isActive;
    private int ANIMATION_START_TICK;

    public TeslaReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.TESLA_RECEIVER.get(), pos, state);
        this.connectionManager = TeslaConnectionManager.getInstance();
        tickCount = 0;
        ANIMATION_START_TICK = 0;
        this.isActive = false;
        this.hasSignal = false;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).inflate(10.0);
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    public int getAnimationStartTick() {
        return ANIMATION_START_TICK;
    }

    public void setSignal(boolean hasSignal) {
        this.hasSignal = hasSignal;

        if (level != null) {
            level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
    }

    public boolean hasSignal() {
        return hasSignal;
    }

    public void setActive(boolean isActive) {
        if (isActive && !this.isActive()) {
            ANIMATION_START_TICK = this.tickCount;
        }

        this.isActive = isActive;
    }

    public TeslaConnectionManager.ConnectionNode asConnectionNode() {
        ResourceLocation dimension = getLevel() != null
                ? getLevel().dimension().location()
                : new ResourceLocation("overworld");

        return TeslaConnectionManager.ConnectionNode.forBlock(getBlockPos(), dimension);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag outgoing = new ListTag();
        connectionManager.getOutgoing(asConnectionNode()).forEach(node -> outgoing.add(node.serialize()));
        tag.put("OutgoingConnections", outgoing);
        tag.putInt("TickCount", this.tickCount);
        tag.putInt("Distance", this.distance);
        tag.putBoolean("IsActive", this.isActive);
        tag.putBoolean("HasSignal", this.hasSignal);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        tag.getList("OutgoingConnections", 10).forEach(t ->
                TeslaConnectionManager.getInstance().addConnection(asConnectionNode(), TeslaConnectionManager.ConnectionNode.deserialize((CompoundTag) t), true)
        );

        this.tickCount = tag.getInt("TickCount");
        this.distance = tag.getInt("Distance");
        this.isActive = tag.getBoolean("IsActive");
        this.hasSignal = tag.getBoolean("HasSignal");
    }

    @Override
    public void onLoad() {
        super.onLoad();
        TeslaConnectionManager.getInstance().registerBlockEntity(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { ;; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("TickCount", this.tickCount);
        tag.putBoolean("IsActive", this.isActive);
        tag.putBoolean("HasSignal", this.hasSignal);
        tag.putInt("Distance", this.distance);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.tickCount = tag.getInt("TickCount");
        this.isActive = tag.getBoolean("IsActive");
        this.hasSignal = tag.getBoolean("HasSignal");
        this.distance = tag.getInt("Distance");
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public double getTick(Object o) {
        return RenderUtils.getCurrentTick();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (t instanceof TeslaReceiverBlockEntity receiver) {
            receiver.tickCount++;

            if (receiver.tickCount % TIME_PER_ACTIVATION == 0) {
                receiver.setActive(true);
            }

            if (receiver.isActive() && receiver.tickCount % TIME_PER_ACTIVATION >= ELECTRICAL_CHARGE_DURATION) {
                receiver.setActive(false);
            }

            if (CompanionsConfig.DINAMO_RECEIVER_REDSTONE_MODE.get() && (!receiver.connectionManager.getIncoming(receiver.asConnectionNode()).isEmpty() || !receiver.connectionManager.getOutgoing(receiver.asConnectionNode()).isEmpty())) {
                receiver.setSignal(receiver.isActive());
            } else {
                receiver.setSignal(receiver.getDistance() > 0);
            }

            if (receiver.isActive() && receiver.level instanceof ServerLevel serverLevel) {
                for (TeslaConnectionManager.ConnectionNode connectionNode : TeslaConnectionManager.getInstance().getOutgoing(receiver.asConnectionNode())) {
                    if (connectionNode.isEntity()) {
                        Entity connectedEntity = serverLevel.getEntity(connectionNode.entityId());
                        if (connectedEntity != null) {
                            Vec3 start = receiver.getBlockPos().getCenter().add(0.0, 0.3D, 0.0);
                            Vec3 end = connectedEntity.position().add(0.0, connectedEntity.getBbHeight() * 0.5D, 0.0);

                            AABB segmentAABB = new AABB(start, end).inflate(1.0D);

                            List<LivingEntity> nearbyEntities = receiver.level.getEntitiesOfClass(
                                    LivingEntity.class,
                                    segmentAABB,
                                    e -> e != connectedEntity && e.isAlive()
                            );

                            for (LivingEntity victim : nearbyEntities) {
                                if (isEntityNearLine(start, end, victim, 0.75D)) {
                                    victim.hurt(victim.level().damageSources().lightningBolt(), 5f);
                                    if (new Random().nextFloat() <= 0.2) {
                                        victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0));
                                    }
                                }
                            }
                        }
                    } else if (connectionNode.isBlock()) {
                        Vec3 start = receiver.getBlockPos().getCenter().add(0.0, 0.0D, 0.0);
                        Vec3 end = connectionNode.blockPos().getCenter().add(0.0, 0.0D, 0.0);

                        AABB segmentAABB = new AABB(start, end).inflate(1.0D);

                        List<LivingEntity> nearbyEntities = receiver.level.getEntitiesOfClass(
                                LivingEntity.class,
                                segmentAABB
                        );

                        for (LivingEntity victim : nearbyEntities) {
                            if (isEntityNearLine(start, end, victim, 0.75D)) {
                                victim.hurt(victim.level().damageSources().lightningBolt(), 5f);
                                if (new Random().nextFloat() <= 0.2) {
                                    victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0));
                                }
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

}
