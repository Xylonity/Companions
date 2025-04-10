package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class PlasmaLampBlockEntity extends AbstractTeslaBlockEntity implements GeoBlockEntity, ITeslaUtil {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final TeslaConnectionManager connectionManager;

    public int tickCount;
    public boolean hasSignal;

    private int ANIMATION_START_TICK;
    private static final int TIME_PER_ACTIVATION = 14;
    public static final int ELECTRICAL_CHARGE_DURATION = 8;

    public PlasmaLampBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.PLASMA_LAMP.get(), pos, state);
        this.connectionManager = TeslaConnectionManager.getInstance();
        tickCount = 0;
        ANIMATION_START_TICK = 0;
        this.hasSignal = false;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).inflate(10.0);
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

    @Override
    public void setActive(boolean isActive) {
        if (isActive && !this.isActive()) {
            ANIMATION_START_TICK = this.tickCount;
        }

        this.isActive = isActive;
    }

    @Override
    public boolean hasConcurrentPower() {
        return false;
    }

    @Override
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
        tag.putInt("Distance", this.getDistance());
        tag.putBoolean("IsActive", this.isActive());
        tag.putBoolean("HasSignal", this.hasSignal);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        tag.getList("OutgoingConnections", 10).forEach(t ->
                TeslaConnectionManager.getInstance().addConnection(asConnectionNode(), TeslaConnectionManager.ConnectionNode.deserialize((CompoundTag) t), true)
        );

        this.tickCount = tag.getInt("TickCount");
        this.setDistance(tag.getInt("Distance"));
        this.isActive = tag.getBoolean("IsActive");
        this.hasSignal = tag.getBoolean("HasSignal");
    }

    @Override
    public void onLoad() {
        super.onLoad();
        //TeslaConnectionManager.getInstance().registerBlockEntity(this);
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
        tag.putBoolean("IsActive", this.isActive());
        tag.putBoolean("HasSignal", this.hasSignal);
        tag.putInt("Distance", this.getDistance());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.tickCount = tag.getInt("TickCount");
        this.isActive = tag.getBoolean("IsActive");
        this.hasSignal = tag.getBoolean("HasSignal");
        this.setDistance(tag.getInt("Distance"));
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
        //if (t instanceof PlasmaLampBlockEntity receiver) {
        //    receiver.tickCount++;
//
        //    TeslaConnectionManager.ConnectionNode firstIncoming = null;
        //    if (!receiver.connectionManager.getIncoming(receiver.asConnectionNode()).isEmpty()) {
        //        firstIncoming = receiver.connectionManager.getIncoming(receiver.asConnectionNode()).iterator().next();
        //    }
//
        //    if (firstIncoming != null) {
        //        if (firstIncoming.isEntity()) {
        //            if (receiver.tickCount % TIME_PER_ACTIVATION == 0) {
        //                receiver.setActive(true);
        //            }
        //            if (receiver.isActive() && receiver.tickCount % TIME_PER_ACTIVATION >= ELECTRICAL_CHARGE_DURATION) {
        //                receiver.setActive(false);
        //            }
        //        } else if (firstIncoming.isBlock()) {
        //            AbstractTeslaBlockEntity incomingReceiver = receiver.connectionManager.getBlockEntity(firstIncoming);
        //            if (incomingReceiver != null && incomingReceiver.isActive()) {
        //                TickScheduler.scheduleBoth(level, () -> receiver.setActive(true), 6);
        //            } else {
        //                TickScheduler.scheduleBoth(level, () -> receiver.setActive(false), 6);
        //            }
        //        }
        //    } else {
        //        if (receiver.tickCount % TIME_PER_ACTIVATION == 0) {
        //            receiver.setActive(true);
        //        }
        //        if (receiver.isActive() && receiver.tickCount % TIME_PER_ACTIVATION >= ELECTRICAL_CHARGE_DURATION) {
        //            receiver.setActive(false);
        //        }
        //    }
//
        //    if (CompanionsConfig.DINAMO_RECEIVER_REDSTONE_MODE.get() && !receiver.connectionManager.getIncoming(receiver.asConnectionNode()).isEmpty()) {
        //        TeslaConnectionManager.ConnectionNode firstIncomingForSignal = receiver.connectionManager.getIncoming(receiver.asConnectionNode()).iterator().next();
        //        if (firstIncomingForSignal.isBlock()) {
        //            AbstractTeslaBlockEntity incomingReceiver = receiver.connectionManager.getBlockEntity(firstIncomingForSignal);
        //            receiver.setSignal(incomingReceiver != null && incomingReceiver.isActive());
        //        } else if (firstIncomingForSignal.isEntity()) {
        //            receiver.setSignal(true);
        //        }
        //    } else {
        //        receiver.setSignal(receiver.getDistance() > 0);
        //    }
//
        //    if (receiver.isActive() && receiver.level instanceof ServerLevel serverLevel) {
        //        for (TeslaConnectionManager.ConnectionNode connectionNode : TeslaConnectionManager.getInstance().getOutgoing(receiver.asConnectionNode())) {
        //            if (connectionNode.isEntity()) {
        //                Entity connectedEntity = serverLevel.getEntity(connectionNode.entityId());
        //                if (connectedEntity != null) {
        //                    Vec3 start = receiver.getBlockPos().getCenter().add(0.0, 0.3D, 0.0);
        //                    Vec3 end = connectedEntity.position().add(0.0, connectedEntity.getBbHeight() * 0.5D, 0.0);
//
        //                    AABB segmentAABB = new AABB(start, end).inflate(1.0D);
//
        //                    List<LivingEntity> nearbyEntities = receiver.level.getEntitiesOfClass(
        //                            LivingEntity.class,
        //                            segmentAABB,
        //                            e -> e != connectedEntity && e.isAlive()
        //                    );
//
        //                    for (LivingEntity victim : nearbyEntities) {
        //                        if (ITeslaUtil.isEntityNearLine(start, end, victim, 0.75D)) {
        //                            victim.hurt(victim.level().damageSources().lightningBolt(), 5f);
        //                            if (new Random().nextFloat() <= 0.2) {
        //                                victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0));
        //                            }
        //                        }
        //                    }
        //                }
        //            } else if (connectionNode.isBlock()) {
        //                Vec3 start = receiver.getBlockPos().getCenter().add(0.0, 0.0D, 0.0);
        //                Vec3 end = connectionNode.blockPos().getCenter().add(0.0, 0.0D, 0.0);
//
        //                AABB segmentAABB = new AABB(start, end).inflate(1.0D);
//
        //                List<LivingEntity> nearbyEntities = receiver.level.getEntitiesOfClass(LivingEntity.class, segmentAABB);
//
        //                for (LivingEntity victim : nearbyEntities) {
        //                    if (ITeslaUtil.isEntityNearLine(start, end, victim, 0.75D)) {
        //                        victim.hurt(victim.level().damageSources().lightningBolt(), 5f);
        //                        if (new Random().nextFloat() <= 0.2) {
        //                            victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0));
        //                        }
        //                    }
        //                }
        //            }
        //        }
        //    }
        //}
    }

}
