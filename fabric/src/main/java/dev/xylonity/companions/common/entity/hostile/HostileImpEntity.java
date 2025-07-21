package dev.xylonity.companions.common.entity.hostile;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.HostileEntity;
import dev.xylonity.companions.common.entity.ai.minion.hostile.goal.HostileImpBraceAttackGoal;
import dev.xylonity.companions.common.entity.ai.minion.hostile.goal.HostileImpFireMarkAttackGoal;
import dev.xylonity.companions.common.entity.companion.MinionEntity;
import dev.xylonity.companions.common.entity.projectile.ShadeAltarUpgradeHaloProjectile;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class HostileImpEntity extends HostileEntity {

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation THROW = RawAnimation.begin().thenPlay("throw");
    private final RawAnimation RING = RawAnimation.begin().thenPlay("ring");

    // 0 none, 1 default, 2 ring
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(HostileImpEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(HostileImpEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int ANGRY_MAX_TICKS = 300;

    private int angryCounter;

    public HostileImpEntity(EntityType<? extends HostileEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.angryCounter = -1;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {

            if (isAngry() && angryCounter == -1) {
                angryCounter++;
            }

            if (angryCounter >= ANGRY_MAX_TICKS) {
                angryCounter = -1;
                setAngry(false);
                setTarget(null);
                failureParticles();
            }

            if (angryCounter != -1) {
                angryCounter++;
            }

        }

    }

    public static AttributeSupplier.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new HostileImpBraceAttackGoal(this, 20, 90));
        this.goalSelector.addGoal(1, new HostileImpFireMarkAttackGoal(this, 20, 160));

        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.43));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && isAngry();
            }
        });
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();

        InteractionResult interactionResult = super.mobInteract(player, hand);

        if (!isAngry()) {

            if (item == CompanionsBlocks.COPPER_COIN.get().asItem()) {
                interactionResult = handleCoinInteraction(level().random.nextFloat() < 0.25f, player);
            } else if (item == CompanionsBlocks.NETHER_COIN.get().asItem()) {
                interactionResult = handleCoinInteraction(level().random.nextFloat() < 0.5f, player);
            } else if (item == CompanionsBlocks.END_COIN.get().asItem()) {
                interactionResult = handleCoinInteraction(level().random.nextFloat() < 0.8f, player);
            } else {
                player.displayClientMessage(Component.translatable("hostile_imp.companions.client_message.requires_coin"), true);
            }

        }

        return interactionResult;
    }

    private InteractionResult handleCoinInteraction(boolean tame, Player player) {
        if (level().isClientSide) return InteractionResult.SUCCESS;

        if (tame) {
            tameImp(player);
            tameParticles();
            this.discard();
        } else {
            setAngry(true);
            failureParticles();

            ShadeAltarUpgradeHaloProjectile halo = CompanionsEntities.SHADE_ALTAR_UPGRADE_HALO.create(level());
            if (halo != null) {
                halo.moveTo(position());
                level().addFreshEntity(halo);
            }

            return InteractionResult.PASS;
        }

        return InteractionResult.SUCCESS;
    }

    private void tameParticles() {
        for (int i = 0; i < 20; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 2.0;
            double dy = (this.random.nextDouble() - 0.5) * 2.0;
            double dz = (this.random.nextDouble() - 0.5) * 2.0;
            if (this.level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < 0.65f) level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.1);
                if (level.random.nextFloat() < 0.25f) level.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.2);
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

    private void tameImp(Player player) {
        MinionEntity minion = CompanionsEntities.MINION.create(level());
        if (minion != null) {
            minion.moveTo(position());
            minion.tameInteraction(player);

            level().addFreshEntity(minion);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_TYPE, 0);
        builder.define(ANGRY, false);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() != null) setAngry(true);
        return super.hurt(pSource, pAmount);
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int attacking) {
        this.entityData.set(ATTACK_TYPE, attacking);
    }

    public boolean isAngry() {
        return this.entityData.get(ANGRY);
    }

    public void setAngry(boolean isAngry) {
        this.entityData.set(ANGRY, isAngry);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (this.getAttackType() == 1) {
            event.getController().setAnimation(THROW);
        } else if (this.getAttackType() == 2) {
            event.getController().setAnimation(RING);
        } else if (event.isMoving()) {
            event.getController().setAnimation(WALK);
        } else {
            event.getController().setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}