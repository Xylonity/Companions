package dev.xylonity.companions.common.entity.hostile;

import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.knightlib.registry.KnightLibItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class BrokenDinamoEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation IDLE1 = RawAnimation.begin().thenPlay("broken1");
    private final RawAnimation IDLE2 = RawAnimation.begin().thenPlay("broken2");
    private final RawAnimation FIX1 = RawAnimation.begin().thenPlay("fixing1");
    private final RawAnimation FIX2 = RawAnimation.begin().thenPlay("fixing2");

    // 0 first item, 1 second item, 2 wrench, 3 fix1, 4 third item, 5 fourth item, 6 wrench, 7 fix2
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(BrokenDinamoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(BrokenDinamoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(BrokenDinamoEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final int ANIMATION_FIX_TICKS = 40;

    private int animationCounter;
    private int woodAmount;
    private int ironAmount;
    private boolean confirmationCheck;

    public BrokenDinamoEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.animationCounter = -1;
        this.woodAmount = 10 + getRandom().nextInt(10, 30);
        this.ironAmount = 10 + getRandom().nextInt(10, 30);
        this.confirmationCheck = false;
    }

    @Override
    protected void registerGoals() { ;; }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(pUuid));
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    public void tick() {
        double prevX = this.getX();
        double prevZ = this.getZ();

        super.tick();

        if (!level().isClientSide) {

            if ((getState() == 3 || getState() == 7) && animationCounter == -1) {
                animationCounter++;
            }

            if (animationCounter >= ANIMATION_FIX_TICKS) {
                if (getState() == 3) {
                    animationCounter = -1;
                    cycleState();
                } else {
                    if (level() instanceof ServerLevel serverLevel && getOwnerUUID() != null) {
                        tameDinamo(serverLevel.getEntity(getOwnerUUID()));
                    }

                    this.discard();
                }

                level().playSound(null, this.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                generatePoofParticles();
            }

            if (animationCounter != -1) animationCounter++;

            if (tickCount >= getLifetime() && getLifetime() != -1) {
                generatePoofParticles();
                this.discard();
            }

        }

        this.setPos(prevX, getY(), prevZ);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {

        if (getState() == 3 || getState() == 7) return InteractionResult.PASS;

        if (pPlayer.getUUID().equals(getOwnerUUID()) || getOwnerUUID() == null) {

            ItemStack stack = pPlayer.getItemInHand(pHand);
            return switch (getState()) {
                case 0 -> giveFirstItem(pPlayer, pHand, stack);
                case 1 -> giveSecondItem(pPlayer, pHand, stack);
                case 4 -> giveThirdItem(pPlayer, pHand, stack);
                case 5 -> giveFourthItem(pPlayer, pHand, stack);
                default -> wrenchInteract(pPlayer, pHand, stack);
            };

        }

        return super.mobInteract(pPlayer, pHand);
    }

    private void tameDinamo(Entity e) {
        if (e instanceof Player player) {
            DinamoEntity dinamo = CompanionsEntities.DINAMO.get().create(level());
            if (dinamo != null) {
                dinamo.moveTo(position());
                dinamo.tameInteraction(player);
                level().addFreshEntity(dinamo);
            }
        }

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

    private void generateWaxParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.WAX_OFF, position().x, this.getY() + getBbHeight() * 0.5f, position().z, 8, 0.75, 0.75, 0.75, 0.065);
        }
    }

    private InteractionResult giveFirstItem(Player pPlayer, InteractionHand pHand, ItemStack stack) {
        if (stack.getItem() == Items.OAK_PLANKS && stack.getCount() >= woodAmount) {
            if (confirmationCheck) {
                if (level().isClientSide) return InteractionResult.SUCCESS;

                if (!pPlayer.getAbilities().instabuild) stack.shrink(woodAmount);

                cycleState();
                confirmationCheck = false;

                if (getOwnerUUID() == null) setOwnerUUID(pPlayer.getUUID());

                pPlayer.displayClientMessage(
                        Component.translatable("broken_dinamo.companions.client_message.wood_consumed",
                                Component.literal(String.valueOf(woodAmount)).withStyle(ChatFormatting.ITALIC)), true);

                pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
                generateWaxParticles();

                return InteractionResult.SUCCESS;
            } else {
                pPlayer.displayClientMessage(
                        Component.translatable("broken_dinamo.companions.client_message.wood_will_get_consumed",
                                Component.literal(String.valueOf(woodAmount)).withStyle(ChatFormatting.ITALIC)), true);

                confirmationCheck = true;
            }
        } else {
            pPlayer.displayClientMessage(
                    Component.translatable("broken_dinamo.companions.client_message.requires_wood",
                            Component.literal(String.valueOf(woodAmount)).withStyle(ChatFormatting.ITALIC)), true);

            pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);

            return InteractionResult.PASS;
        }

        return InteractionResult.SUCCESS;
    }

    private InteractionResult giveSecondItem(Player pPlayer, InteractionHand pHand, ItemStack stack) {
        if (stack.getItem() == KnightLibItems.HOMUNCULUS.get()) {
            if (level().isClientSide) return InteractionResult.SUCCESS;

            if (!pPlayer.getAbilities().instabuild) stack.shrink(1);

            cycleState();

            pPlayer.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message.homunculus_consumed"), true);

            pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
            generateWaxParticles();

            return InteractionResult.SUCCESS;
        } else {
            pPlayer.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message.requires_homunculus"), true);

            pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);

            return InteractionResult.PASS;
        }
    }

    private InteractionResult giveThirdItem(Player pPlayer, InteractionHand pHand, ItemStack stack) {
        if (stack.getItem() == Items.IRON_INGOT && stack.getCount() >= ironAmount) {
            if (confirmationCheck) {
                if (level().isClientSide) return InteractionResult.SUCCESS;

                if (!pPlayer.getAbilities().instabuild) stack.shrink(ironAmount);

                cycleState();
                confirmationCheck = false;

                if (getOwnerUUID() == null) setOwnerUUID(pPlayer.getUUID());

                pPlayer.displayClientMessage(
                        Component.translatable("broken_dinamo.companions.client_message.iron_consumed",
                                Component.literal(String.valueOf(ironAmount)).withStyle(ChatFormatting.ITALIC)), true);

                pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
                generateWaxParticles();

                return InteractionResult.SUCCESS;
            } else {
                pPlayer.displayClientMessage(
                        Component.translatable("broken_dinamo.companions.client_message.iron_will_get_consumed",
                                Component.literal(String.valueOf(ironAmount)).withStyle(ChatFormatting.ITALIC)), true);

                pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);

                confirmationCheck = true;
            }
        } else {
            pPlayer.displayClientMessage(
                    Component.translatable("broken_dinamo.companions.client_message.requires_iron",
                            Component.literal(String.valueOf(ironAmount)).withStyle(ChatFormatting.ITALIC)), true);

            pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);

            return InteractionResult.PASS;
        }

        return InteractionResult.SUCCESS;
    }

    private InteractionResult giveFourthItem(Player pPlayer, InteractionHand pHand, ItemStack stack) {
        if (stack.getItem() == CompanionsBlocks.TESLA_COIL.get().asItem()) {
            if (level().isClientSide) return InteractionResult.SUCCESS;

            if (!pPlayer.getAbilities().instabuild) stack.shrink(1);

            cycleState();

            pPlayer.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message.tesla_coil_consumed"), true);

            pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
            generateWaxParticles();

            return InteractionResult.SUCCESS;
        } else {
            pPlayer.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message.requires_tesla_coil"), true);

            pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);

            return InteractionResult.PASS;
        }
    }

    private InteractionResult wrenchInteract(Player pPlayer, InteractionHand pHand, ItemStack stack) {
        if (stack.getItem() == CompanionsItems.WRENCH.get()) {
            if (level().isClientSide) return InteractionResult.SUCCESS;

            if (!pPlayer.getAbilities().instabuild) stack.hurtAndBreak(1, pPlayer, LivingEntity.getSlotForHand(pHand));

            cycleState();

            pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.0F);

            return InteractionResult.SUCCESS;
        } else {
            pPlayer.displayClientMessage(Component.translatable("broken_dinamo.companions.client_message.requires_wrench"), true);

            pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);

            return InteractionResult.PASS;
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pPose) {
        return getState() != 2 ? super.getDefaultDimensions(pPose) : EntityDimensions.scalable(1F, 1F);
    }

    public int getState() {
        return this.entityData.get(STATE);
    }

    public void cycleState() {
        this.entityData.set(STATE, getState() + 1);
    }

    public void setState(int state) {
        this.entityData.set(STATE, state);
    }

    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    public void setLifetime(int lifetime) {
        this.entityData.set(LIFETIME, lifetime);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STATE, 0);
        builder.define(LIFETIME, -1);
        builder.define(OWNER_UUID, Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Lifetime")) {
            this.setLifetime(pCompound.getInt("Lifetime"));
        }
        if (pCompound.contains("State")) {
            this.setState(pCompound.getInt("State"));
        }
        if (pCompound.contains("WoodAmount")) {
            this.woodAmount = pCompound.getInt("WoodAmount");
        }
        if (pCompound.contains("IronAmount")) {
            this.ironAmount = pCompound.getInt("IronAmount");
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Lifetime", getLifetime());
        pCompound.putInt("State", getState());
        pCompound.putInt("WoodAmount", this.woodAmount);
        pCompound.putInt("IronAmount", this.ironAmount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (getState() == 7) {
            event.getController().setAnimation(FIX2);
        } else if (getState() == 4 || getState() == 5 || getState() == 6) {
            event.getController().setAnimation(IDLE2);
        } else if (getState() == 3) {
            event.getController().setAnimation(FIX1);
        } else if (getState() == 0 || getState() == 1 || getState() == 2) {
            event.getController().setAnimation(IDLE1);
        }

        return PlayState.CONTINUE;
    }

}