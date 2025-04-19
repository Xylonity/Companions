package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.registry.*;
import dev.xylonity.knightlib.compat.registry.KnightLibItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class BrokenDinamoEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation IDLE1 = RawAnimation.begin().thenPlay("broken1");
    private final RawAnimation IDLE2 = RawAnimation.begin().thenPlay("broken2");
    private final RawAnimation FIX1 = RawAnimation.begin().thenPlay("fixing1");
    private final RawAnimation FIX2 = RawAnimation.begin().thenPlay("fixing2");

    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(BrokenDinamoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> REPAIR_STEP = SynchedEntityData.defineId(BrokenDinamoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOULD_FIX = SynchedEntityData.defineId(BrokenDinamoEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int ANIMATION_FIX_MAX_TICKS = 40;
    private int animationCounter;
    private UUID ownerUUID = null;
    private boolean ironOrPlanksConfirmation;
    private final int requiredIron;
    private final int requiredWood;

    public BrokenDinamoEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.animationCounter = 0;
        this.ironOrPlanksConfirmation = false;
        this.requiredIron = 10 + this.random.nextInt(31);
        this.requiredWood = 10 + this.random.nextInt(31);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void registerGoals() { ;; }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    public int getRepairStep() {
        return this.entityData.get(REPAIR_STEP);
    }

    public void setRepairStep(int step) {
        this.entityData.set(REPAIR_STEP, step);
    }

    @Override
    public void tick() {
        double lastX = this.getX();
        double lastZ = this.getZ();

        super.tick();

        if (getPhase() == 4) {
            DinamoEntity dinamo = CompanionsEntities.DINAMO.get().create(this.level());

            if (dinamo != null) {
                dinamo.moveTo(getX(), getY(), getZ());

                if (this.ownerUUID != null) {
                    dinamo.setTame(true);
                    dinamo.setOwnerUUID(this.ownerUUID);
                }

                this.level().addFreshEntity(dinamo);
            }

            for (int i = 0; i < 50; i++) {
                double dx = (this.random.nextDouble() - 0.5) * 2.0;
                double dy = (this.random.nextDouble() - 0.5) * 2.0;
                double dz = (this.random.nextDouble() - 0.5) * 2.0;
                if (this.level() instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.1);
                }
            }

            this.remove(RemovalReason.DISCARDED);
        }

        if (shouldFix()) {
            this.animationCounter++;
            if (this.animationCounter == ANIMATION_FIX_MAX_TICKS) {

                if (getPhase() == 1) {
                    for (int i = 0; i < 50; i++) {
                        double dx = (this.random.nextDouble() - 0.5) * 2.5;
                        double dy = (this.random.nextDouble() - 0.5) * 1.5;
                        double dz = (this.random.nextDouble() - 0.5) * 2.5;
                        if (this.level() instanceof ServerLevel level) {
                            level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.1);
                        }
                    }
                }

                setPhase(getPhase() + 1);
                setShouldFix(false);
                this.animationCounter = 0;
                this.refreshDimensions();
            }
        }

        this.setPos(lastX, getY(), lastZ);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (this.level().isClientSide() || getPhase() == 1 || getPhase() == 3) return InteractionResult.SUCCESS;

        int repairStep = getRepairStep();
        ItemStack heldItem = player.getItemInHand(hand);

        switch (repairStep) {
            case 0 -> {
                int requiredAmount = getPhase() == 0 ? requiredWood : requiredIron;
                MutableComponent transArgsParam0 = Component.literal(String.valueOf(requiredAmount)).withStyle(ChatFormatting.ITALIC);
                Item requiredItem = getPhase() == 0 ? Items.OAK_PLANKS : Items.IRON_INGOT;
                String type = getPhase() == 0 ? "oak" : "iron";

                if (heldItem.getItem() != requiredItem) {
                    player.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message.requires_" + type, transArgsParam0), true);
                    player.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    return InteractionResult.SUCCESS;
                }

                if (!ironOrPlanksConfirmation) {
                    player.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message." + type + "_will_get_consumed", transArgsParam0), true);
                    ironOrPlanksConfirmation = true;
                } else {

                    if (heldItem.getCount() < requiredAmount) {
                        player.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message.not_enough_" + type, transArgsParam0),true);
                        ironOrPlanksConfirmation = false;

                        return InteractionResult.SUCCESS;
                    }

                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(requiredAmount);
                    }

                    player.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message." + type + "_consumed", transArgsParam0), true);

                    if (level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.WAX_OFF, this.getX(), this.getY() + 0.5, this.getZ(), 8, 0.5, 0.5, 0.5, 0.05);
                    }

                    player.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    setRepairStep(1);
                    ironOrPlanksConfirmation = false;
                }

                return InteractionResult.SUCCESS;
            }
            case 1 -> {
                Item requiredItem = getPhase() == 0 ? KnightLibItems.GREAT_ESSENCE.get() : CompanionsBlocks.TESLA_COIL.get().asItem();
                String type = getPhase() == 0 ? "great_essence" : "tesla_coil";

                if (heldItem.getItem() != requiredItem) {
                    player.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message.requires_" + type), true);
                    player.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    return InteractionResult.SUCCESS;
                }

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                player.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message."+ type + "_consumed"), true);

                if (level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.WAX_OFF, this.getX(), this.getY() + 0.5, this.getZ(), 8, 0.5, 0.5, 0.5, 0.05);
                }

                player.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
                setRepairStep(2);

                return InteractionResult.SUCCESS;
            }
            case 2 -> {
                if (heldItem.getItem() != CompanionsItems.WRENCH.get()) {
                    player.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message.requires_wrench"), true);
                    player.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    return InteractionResult.SUCCESS;
                }

                this.setPhase(this.getPhase() + 1);
                this.setShouldFix(true);
                setRepairStep(0);
                this.ownerUUID = player.getUUID();
                player.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
                heldItem.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(hand));

                return InteractionResult.SUCCESS;
            }
            default -> {
                return InteractionResult.SUCCESS;
            }
        }

    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return getPhase() != 2 ? super.getDimensions(pPose) : EntityDimensions.scalable(1F, 1F);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public boolean shouldFix() {
        return this.entityData.get(SHOULD_FIX);
    }

    public void setShouldFix(boolean shouldFix) {
        this.entityData.set(SHOULD_FIX, shouldFix);
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 0);
        this.entityData.define(REPAIR_STEP, 0);
        this.entityData.define(SHOULD_FIX, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (getPhase() == 3) {
            event.getController().setAnimation(FIX2);
        } else if (getPhase() == 2) {
            event.getController().setAnimation(IDLE2);
        } else if (getPhase() == 1) {
            event.getController().setAnimation(FIX1);
        } else if (getPhase() == 0) {
            event.getController().setAnimation(IDLE1);
        }

        return PlayState.CONTINUE;
    }

}