package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.tesla.behaviour.TeslaCoilBehaviour;
import dev.xylonity.companions.common.tick.TickScheduler;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
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

public class TeslaCoilBlockEntity extends AbstractTeslaBlockEntity implements GeoBlockEntity, ITeslaUtil {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final TeslaConnectionManager connectionManager;

    public static final int ELECTRICAL_CHARGE_DURATION = 8;

    public int tickCount;
    public int activationTick;

    public TeslaCoilBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.TESLA_COIL.get(), pos, state);
        this.connectionManager = TeslaConnectionManager.getInstance();
        this.tickCount = 0;
        this.activationTick = 0;
        this.teslaBehavior = new TeslaCoilBehaviour();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).inflate(10.0);
    }

    public int getAnimationStartTick() {
        return activationTick;
    }

    public void setActive(boolean isActive) {
        if (isActive && !this.isActive()) {
            this.activationTick = this.tickCount;
        }

        this.isActive = isActive;
    }

    @Override
    public boolean hasConcurrentPower() {
        return this.isActive;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag outgoing = new ListTag();
        connectionManager.getOutgoing(asConnectionNode()).forEach(node -> outgoing.add(node.serialize()));
        tag.put("OutgoingConnections", outgoing);
        tag.putInt("TickCount", this.tickCount);
        tag.putBoolean("IsActive", this.isActive);
        tag.putInt("ActivationTick", this.activationTick);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        tag.getList("OutgoingConnections", 10).forEach(t ->
                TeslaConnectionManager.getInstance().addConnection(asConnectionNode(), TeslaConnectionManager.ConnectionNode.deserialize((CompoundTag) t), true)
        );
        this.tickCount = tag.getInt("TickCount");
        this.isActive = tag.getBoolean("IsActive");
        this.activationTick = tag.contains("ActivationTick") ? tag.getInt("ActivationTick") : 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("TickCount", this.tickCount);
        tag.putBoolean("IsActive", this.isActive);
        tag.putInt("Distance", this.distance);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.tickCount = tag.getInt("TickCount");
        this.isActive = tag.getBoolean("IsActive");
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
        if (t instanceof TeslaCoilBlockEntity coil) {
            coil.tickCount++;

            coil.teslaBehavior.process(coil);

            //if (coil.isActive() && coil.level instanceof ServerLevel serverLevel) {
            //    for (TeslaConnectionManager.ConnectionNode connectionNode : TeslaConnectionManager.getInstance().getOutgoing(coil.asConnectionNode())) {
            //        if (connectionNode.isEntity()) {
            //            Entity connectedEntity = serverLevel.getEntity(connectionNode.entityId());
            //            if (connectedEntity != null) {
            //                Vec3 start = coil.getBlockPos().getCenter().add(0.0, 0.3D, 0.0);
            //                Vec3 end = connectedEntity.position().add(0.0, connectedEntity.getBbHeight() * 0.5D, 0.0);

            //                AABB segmentAABB = new AABB(start, end).inflate(1.0D);

            //                List<LivingEntity> nearbyEntities = coil.level.getEntitiesOfClass(
            //                        LivingEntity.class,
            //                        segmentAABB,
            //                        e -> e != connectedEntity && e.isAlive()
            //                );

            //                for (LivingEntity victim : nearbyEntities) {
            //                    if (ITeslaUtil.isEntityNearLine(start, end, victim, 0.75D)) {
            //                        victim.hurt(victim.level().damageSources().lightningBolt(), 5f);
            //                        if (new Random().nextFloat() <= 0.2) {
            //                            victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0));
            //                        }
            //                    }
            //                }
            //            }
            //        } else if (connectionNode.isBlock()) {
            //            Vec3 start = coil.getBlockPos().getCenter().add(0.0, 0.0D, 0.0);
            //            Vec3 end = connectionNode.blockPos().getCenter().add(0.0, 0.0D, 0.0);

            //            AABB segmentAABB = new AABB(start, end).inflate(1.0D);

            //            List<LivingEntity> nearbyEntities = coil.level.getEntitiesOfClass(
            //                    LivingEntity.class,
            //                    segmentAABB
            //            );

            //            for (LivingEntity victim : nearbyEntities) {
            //                if (ITeslaUtil.isEntityNearLine(start, end, victim, 0.75D)) {
            //                    victim.hurt(victim.level().damageSources().lightningBolt(), 5f);
            //                    if (new Random().nextFloat() <= 0.2) {
            //                        victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0));
            //                    }
            //                }
            //            }
            //        }
            //    }
            //}
        }
    }

}