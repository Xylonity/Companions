package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionRandomStrollGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsLookAtPlayerGoal;
import dev.xylonity.companions.common.entity.ai.teddy.control.TeddyMoveControl;
import dev.xylonity.companions.common.entity.ai.teddy.goal.*;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;

import java.util.Map;
import java.util.Random;

public class TeddyEntity extends CompanionEntity implements TraceableEntity {

    private final RawAnimation LAY = RawAnimation.begin().thenPlay("lay");
    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation SLEEP = RawAnimation.begin().thenPlay("sleep");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation STAB = RawAnimation.begin().thenPlay("stab");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation AUTO_STAB = RawAnimation.begin().thenPlay("auto_stab");
    private final RawAnimation TRANSFORM = RawAnimation.begin().thenPlay("transform");
    private final RawAnimation TRANSFORM_ANGEL = RawAnimation.begin().thenPlay("transform_angel");

    private final RawAnimation MUTATED_FLY = RawAnimation.begin().thenPlay("flying");
    private final RawAnimation MUTATED_ATTACK1 = RawAnimation.begin().thenPlay("attack");
    private final RawAnimation MUTATED_ATTACK2 = RawAnimation.begin().thenPlay("stab");
    private final RawAnimation MUTATED_SIT1 = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation MUTATED_SIT2 = RawAnimation.begin().thenPlay("laying");
    private final RawAnimation MUTATED_SIT3 = RawAnimation.begin().thenPlay("flying_sit");
    private final RawAnimation MUTATED_DEATH = RawAnimation.begin().thenPlay("death");

    private final RawAnimation HOLY_SIT1 = RawAnimation.begin().thenPlay("lay");
    private final RawAnimation HOLY_SIT2 = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation HOLY_SIT3 = RawAnimation.begin().thenPlay("sit2");
    private final RawAnimation HOLY_ATTACK1 = RawAnimation.begin().thenPlay("stab");
    private final RawAnimation HOLY_ATTACK2 = RawAnimation.begin().thenPlay("stab2");
    private final RawAnimation HOLY_APPLY_EFFECTS = RawAnimation.begin().thenPlay("apply_effects");
    private final RawAnimation HOLY_RITUAL = RawAnimation.begin().thenPlay("apply_effects");
    private final RawAnimation HOLY_SUMMON_BALLS = RawAnimation.begin().thenPlay("summon_balls");
    private final RawAnimation HOLY_FLY = RawAnimation.begin().thenPlay("fly");
    private final RawAnimation HOLY_FLY_IDLE = RawAnimation.begin().thenPlay("fly_idle");

    // Phase 1: 1 stab, 2 auto-stab
    // Phase 2: 1 attack
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.INT);
    // 1 base, 2 mutated, 3 holy1, 4 holy2
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SECOND_PHASE_COUNTER = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANGEL_PHASE_COUNTER = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ON_AIR = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TELEPORTED = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> RITUAL_TICKS = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> RITUAL_ITEM = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.ITEM_STACK);

    private static final int ANIMATION_TRANSFORM_MAX_TICKS = 200;
    private static final int ANIMATION_TRANSFORM_ANGEL_MAX_TICKS = 80;
    private static final int ANIMATION_DEAD_MAX_TICKS = 64;
    public static final int RITUAL_MAX_TICKS = 33;

    private int ritualRenderStartTick = -1;

    public TeddyEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    public boolean getTeleported() {
        return this.entityData.get(TELEPORTED);
    }

    public void setTeleported(boolean teleported) {
        this.entityData.set(TELEPORTED, teleported);
    }

    public int getRitualTicks() {
        return this.entityData.get(RITUAL_TICKS);
    }

    public void setRitualTicks(int ticks) {
        this.entityData.set(RITUAL_TICKS, ticks);
    }

    public ItemStack getRitualItem() {
        return this.entityData.get(RITUAL_ITEM);
    }

    public void setRitualItem(ItemStack stack) {
        this.entityData.set(RITUAL_ITEM, stack);
    }

    public float getRitualRenderAge(float partialTick) {
        if (getRitualTicks() <= 0) {
            ritualRenderStartTick = -1;
            return 0f;
        }

        if (ritualRenderStartTick < 0) {
            ritualRenderStartTick = tickCount - getRitualTicks();
        }

        return tickCount - ritualRenderStartTick + partialTick;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this) {
            @Override
            public boolean canUse() {
                if (!isTame()) return false;
                if (isInWaterOrBubble()) return false;
                if (!onGround() && getPhase() == 1) return false;
                if (getOwner() == null) return true;
                return (!(distanceToSqr(getOwner()) < 144.0) || getOwner().getLastHurtByMob() == null) && isOrderedToSit();
            }

            @Override
            public void start() {
                super.start();
                if (getPhase() == 2 || (getPhase() > 2 && getSitVariation() != 2)) {
                    double currentX = getX();
                    double currentZ = getZ();

                    BlockPos groundPos = Util.findClosestGroundBelow(TeddyEntity.this, 3.0f);
                    if (groundPos != null) {
                        double y = groundPos.getY() + 1.0;
                        teleportTo(currentX, y, currentZ);
                    }
                    else if (getPhase() > 2) {
                        setSitVariation(2);
                    }

                }

            }

        });

        this.goalSelector.addGoal(2, new TeddyAttackGoal(this, 10, 30));
        this.goalSelector.addGoal(2, new TeddyVoodooAttackGoal(this, 60, 200));
        this.goalSelector.addGoal(3, new TeddyApproachTargetGoal(this, 0.45, 0.4f, 1.25f));

        this.goalSelector.addGoal(2, new HolyTeddySummonBallsGoal(this, 140, 260));
        this.goalSelector.addGoal(2, new HolyTeddyBlessingGoal(this, 300, 500));
        this.goalSelector.addGoal(3, new HolyTeddyAttackGoal(this, 15, 40));

        this.goalSelector.addGoal(3, new MutatedTeddyAttackGoal(this, 0, 15));
        this.goalSelector.addGoal(4, new MutatedTeddyFollowTargetGoal(this));

        this.goalSelector.addGoal(5, new MutatedTeddyRandomStrollGoal(this, 0.43f));
        this.goalSelector.addGoal(5, new MutatedTeddyFollowOwnerGoal(this, 0.6D, 3.0F, 7.0F, 0.18f));
        this.goalSelector.addGoal(5, new CompanionFollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && getPhase() == 1;
            }
        });
        this.goalSelector.addGoal(5, new CompanionRandomStrollGoal(this, 0.43) {
            @Override
            public boolean canUse() {
                return super.canUse() && getPhase() == 1;
            }
        });

        this.goalSelector.addGoal(6, new CompanionsLookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && getMainAction() != 0 && getPhase() == 1;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && getMainAction() != 0 && getPhase() == 1;
            }
        });

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        if (getPhase() != 1 && source.is(DamageTypes.IN_WALL)) return false;

        boolean ret = super.hurt(source, amount);

        if (!level().isClientSide && amount > 4.0f && getPhase() == 2 && getTarget() != null) {
            spawnMutantParticles(25);
            tpOppositeSide();
            spawnMutantParticles(25);
        }

        return ret;
    }

    private void tpOppositeSide() {
        LivingEntity target = getTarget();
        if (target == null) return;

        Vec3 pos = target.position();
        Vec3 newPos = pos.subtract(position().x - pos.x, 0, position().z - pos.z);

        setPos(newPos.x, position().y, newPos.z);
        setTeleported(true);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return CompanionEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.TEDDY_MAX_LIFE)
                .add(Attributes.ATTACK_DAMAGE, CompanionsConfig.TEDDY_DAMAGE)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    private void updateStats() {
        AttributeInstance maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(CompanionsConfig.TEDDY_MUTANT_MAX_LIFE);
        }

        AttributeInstance damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.setBaseValue(CompanionsConfig.TEDDY_MUTANT_DAMAGE);
        }

    }

    private void updateHolyStats() {
        final AttributeInstance maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(CompanionsConfig.TEDDY_HOLY_MAX_LIFE);
        }

        final AttributeInstance damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.setBaseValue(CompanionsConfig.TEDDY_HOLY_DAMAGE);
        }

    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public int getSecondPhaseCounter() {
        return this.entityData.get(SECOND_PHASE_COUNTER);
    }

    public void setSecondPhaseCounter(int t) {
        this.entityData.set(SECOND_PHASE_COUNTER, t);
    }

    public int getAngelPhaseCounter() {
        return this.entityData.get(ANGEL_PHASE_COUNTER);
    }

    public void setAngelPhaseCounter(int t) {
        this.entityData.set(ANGEL_PHASE_COUNTER, t);
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int type) {
        this.entityData.set(ATTACK_TYPE, type);
    }

    public boolean getIsOnAir() {
        return this.entityData.get(IS_ON_AIR);
    }

    public void setIsOnAir(boolean isOnAir) {
        this.entityData.set(IS_ON_AIR, isOnAir);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return getPhase() == 1 && super.causeFallDamage(pFallDistance, pMultiplier, pSource);
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(PHASE, 1);
        pBuilder.define(SECOND_PHASE_COUNTER, 0);
        pBuilder.define(ANGEL_PHASE_COUNTER, 0);
        pBuilder.define(ATTACK_TYPE, 0);
        pBuilder.define(IS_ON_AIR, false);
        pBuilder.define(TELEPORTED, false);
        pBuilder.define(RITUAL_TICKS, 0);
        pBuilder.define(RITUAL_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        super.tick();

        if (getPhase() != 1) {
            this.setNoGravity(true);
        }

        if (getPhase() == 2 && !level().isClientSide && this.getMainAction() == 0) {
            setIsOnAir(level().getBlockState(blockPosition().below()).isAir());
        }

        if (getSecondPhaseCounter() != 0 && getSecondPhaseCounter() <= ANIMATION_TRANSFORM_MAX_TICKS) {
            setTarget(null);
            setNoMovement(true);

            if (getSecondPhaseCounter() == ANIMATION_TRANSFORM_MAX_TICKS) {
                setNoMovement(false);

                level().addParticle(CompanionsParticles.TEDDY_TRANSFORMATION_CLOUD.get(), true, getX(), getY() + 0.15F, getZ(), 0, 0, 0);

                spawnMutantParticles(50);

                setPhase(2);
                this.refreshDimensions();

                updateStats();
                playSound(CompanionsSounds.TEDDY_TRANSFORMATION.get());

                this.moveControl = new TeddyMoveControl(this);
            }

            setSecondPhaseCounter(getSecondPhaseCounter() + 1);
        }

        if (getAngelPhaseCounter() != 0 && getAngelPhaseCounter() <= ANIMATION_TRANSFORM_ANGEL_MAX_TICKS) {
            setTarget(null);
            setNoMovement(true);

            if (getAngelPhaseCounter() == ANIMATION_TRANSFORM_ANGEL_MAX_TICKS) {
                setNoMovement(false);

                spawnAngelParticles(50);

                setPhase(random.nextInt(2) + 3);
                this.refreshDimensions();

                updateHolyStats();
                playSound(CompanionsSounds.TEDDY_TRANSFORMATION.get());

                this.moveControl = new TeddyMoveControl(this);
            }

            setAngelPhaseCounter(getAngelPhaseCounter() + 1);
        }

        if (getRitualTicks() > 0 && !level().isClientSide) {
            setTarget(null);
            setNoMovement(true);

            if (level() instanceof ServerLevel level && tickCount % 3 == 0) {
                Vec3 pos = ritualItemPos();
                level.sendParticles(ParticleTypes.ENCHANT, pos.x, pos.y + 0.4, pos.z, 2, 0.2, 0.25, 0.2, 0.1);
            }

            if (getRitualTicks() >= RITUAL_MAX_TICKS) {
                finishRitual();
            }
            else {
                setRitualTicks(getRitualTicks() + 1);
            }

        }

        if (getPhase() == 2) {
            if (getOwner() != null && !getOwner().hasEffect(MobEffects.REGENERATION)) {
                if (this.distanceToSqr(getOwner()) < 16 * 16)  {
                    getOwner().addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, true, true, true));
                }
            }
        }

        if (getPhase() == 2 && !level().isClientSide && CompanionsConfig.TEDDY_MUTANT_HEALS_OVER_TIME) {
            if (tickCount % 200 == 0) this.heal(1f);
            if (tickCount % 15 == 0 && ((getMainAction() == 0 && getIsOnAir()) || (getMainAction() != 0))) playSound(CompanionsSounds.MUTANT_TEDDY_FLAP_WINGS.get(), 0.4f, 1f);
        }

    }

    public Vec3 ritualItemPos() {
        return position().add(Vec3.directionFromRotation(0, yBodyRot).scale(0.9)).add(0, 1.05, 0);
    }

    private void finishRitual() {
        final ItemStack itemToFix = getRitualItem().copy();
        EnchantmentHelper.updateEnchantments(itemToFix, mutable -> mutable.removeIf(h -> h.is(Enchantments.VANISHING_CURSE)));

        final Vec3 pos = ritualItemPos();
        final ItemEntity item = new ItemEntity(level(), pos.x, pos.y, pos.z, itemToFix);
        item.setDeltaMovement(0, 0.15, 0);
        level().addFreshEntity(item);

        spawnAngelParticles(30);
        playSound(SoundEvents.PLAYER_LEVELUP, 0.8f, 1.5f);

        setRitualItem(ItemStack.EMPTY);
        setRitualTicks(0);
        setNoMovement(false);
    }

    private void spawnAngelParticles(int amount) {
        for (int i = 0; i < amount; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 2.0;
            double dy = (this.random.nextDouble() - 0.5) * 2.0;
            double dz = (this.random.nextDouble() - 0.5) * 2.0;
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.1);
                if (i % 5 == 0) {
                    level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.1);
                }

            }

        }

    }

    private void spawnMutantParticles(int amount) {
        for (int i = 0; i < amount; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 2.0;
            double dy = (this.random.nextDouble() - 0.5) * 2.0;
            double dz = (this.random.nextDouble() - 0.5) * 2.0;
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.1);
                if (i % 5 == 0) level.sendParticles(CompanionsParticles.TEDDY_TRANSFORMATION.get(), this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.2);
            }
        }
    }

    @Override
    protected void playHurtSound(@NotNull DamageSource pSource) {
        if (getPhase() == 2) {
            playSound(CompanionsSounds.MUTANT_TEDDY_HURT.get(), 0.55f, 1f);
        }

        super.playHurtSound(pSource);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return getPhase() == 2 ? CompanionsSounds.MUTANT_TEDDY_IDLE.get() : null;
    }

    @Override
    public void move(@NotNull MoverType pType, @NotNull Vec3 pPos) {
        super.move(pType, pPos);
        this.checkInsideBlocks();
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    protected @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose pPose) {
        return getPhase() == 1 ? super.getDefaultDimensions(pPose) : getPhase() == 2 ? EntityDimensions.scalable(1F, 2F) : EntityDimensions.scalable(0.9F, 1.4F);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if ((getSecondPhaseCounter() != 0 || getAngelPhaseCounter() != 0) && getPhase() == 1) {
            return InteractionResult.PASS;
        }

        // Tame
        final ItemStack stack = player.getItemInHand(hand);
        if (!isTame() && stack.getItem() == CompanionsItems.NEEDLE.get()) {
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            this.tameInteraction(player);

            return InteractionResult.SUCCESS;
        }

        // Mutant phase
        if (isTame() && player == getOwner() && stack.getItem() == CompanionsItems.ETERNAL_LIGHTER.get() && getPhase() == 1) {
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }

            setSecondPhaseCounter(getSecondPhaseCounter() + 1);

            return InteractionResult.SUCCESS;
        }

        // Holy phase
        if (isTame() && player == getOwner() && stack.getItem() == CompanionsBlocks.PORCELAIN_POTTERY.get().asItem() && getPhase() == 1) {
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            setAngelPhaseCounter(getAngelPhaseCounter() + 1);

            return InteractionResult.SUCCESS;
        }

        // Mutant flesh per lava bucket
        if (isTame() && player == getOwner() && stack.getItem() == Items.LAVA_BUCKET && getPhase() == 2) {
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            player.setItemInHand(hand, new ItemStack(Items.BUCKET));

            level().addFreshEntity(new ItemEntity(level(), position().x, position().y, position().z, new ItemStack(CompanionsItems.MUTANT_FLESH.get(), new Random().nextInt(1, 3))));

            playSound(SoundEvents.GENERIC_DRINK);

            return InteractionResult.SUCCESS;
        }

        // Vanishing curse cleansing ritual
        if (isTame() && player == getOwner() && getPhase() > 2 && getRitualTicks() == 0 && getTarget() == null
                && EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet().stream().anyMatch(h -> h.is(Enchantments.VANISHING_CURSE))) {
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }

            setRitualItem(stack.copyWithCount(1));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            setRitualTicks(1);
            playSound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1f, 0.8f);

            return InteractionResult.SUCCESS;
        }

        // Healing
        if (handleDefaultMainActionAndHeal(player, hand)) {
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (getPhase() == 2) {
            if (this.deathTime >= ANIMATION_DEAD_MAX_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
                this.level().broadcastEntityEvent(this, (byte) 60);
                this.remove(RemovalReason.KILLED);
            }
            
        }
        else {
            if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
                this.level().broadcastEntityEvent(this, (byte) 60);
                this.remove(RemovalReason.KILLED);
            }

        }

    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        if (getPhase() == 1) {
            playSound(CompanionsSounds.TEDDY_STEP.get());
        } else {
            super.playStepSound(pPos, pState);
        }
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (pKey.equals(PHASE)) {
            this.refreshDimensions();
        }
        if (pKey.equals(RITUAL_TICKS) && getRitualTicks() == 0) {
            ritualRenderStartTick = -1;
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Phase")) {
            this.setPhase(pCompound.getInt("Phase"));
            if (getPhase() != 1) {
                this.moveControl = new TeddyMoveControl(this);
            }
        }
        if (pCompound.contains("RitualTicks")) {
            this.setRitualTicks(pCompound.getInt("RitualTicks"));
        }
        if (pCompound.contains("RitualItem")) {
            this.setRitualItem(ItemStack.parseOptional(this.registryAccess(), pCompound.getCompound("RitualItem")));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("RitualTicks", getRitualTicks());
        if (!getRitualItem().isEmpty()) {
            pCompound.put("RitualItem", getRitualItem().save(this.registryAccess()));
        }
        pCompound.putInt("Phase", getPhase());
        if (this.getSecondPhaseCounter() != 0) {
            pCompound.putInt("Phase", 2);
        }
        else if (this.getAngelPhaseCounter() != 0 && getPhase() == 1) {
            pCompound.putInt("Phase", random.nextInt(2) + 3);
        }
    }

    @Override
    protected boolean canThisCompanionWork() {
        return false;
    }

    @Override
    protected int sitAnimationsAmount() {
        return getPhase() == 2 ? 2 : 3;
    }

    @Override
    protected boolean shouldKeepChunkLoaded() {
        return CompanionsConfig.TEDDY_KEEP_CHUNK_LOADED;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (getPhase() == 1) {
            if (this.getSecondPhaseCounter() != 0 && this.getSecondPhaseCounter() <= ANIMATION_TRANSFORM_MAX_TICKS) {
                event.getController().setAnimation(TRANSFORM);
            }
            else if (this.getAngelPhaseCounter() != 0 && this.getAngelPhaseCounter() <= ANIMATION_TRANSFORM_ANGEL_MAX_TICKS) {
                event.getController().setAnimation(TRANSFORM_ANGEL);
            }
            else if (this.getMainAction() == 0) {
                final RawAnimation sit = getSitVariation() == 0 ? LAY : getSitVariation() == 1 ? SIT : SLEEP;
                event.getController().setAnimation(sit);
            }
            else if (getAttackType() == 1) {
                event.setAnimation(STAB);
            }
            else if (getAttackType() == 2) {
                event.setAnimation(AUTO_STAB);
            }
            else if (event.isMoving()) {
                event.getController().setAnimation(WALK);
            }
            else {
                event.getController().setAnimation(IDLE);
            }

        }
        else if (getPhase() == 2) {
            if (isDeadOrDying()) {
                event.getController().setAnimation(MUTATED_DEATH);
            }
            else if (this.getMainAction() == 0) {
                final RawAnimation sit = getIsOnAir() ? MUTATED_SIT3 : getSitVariation() == 0 ? MUTATED_SIT1 : MUTATED_SIT2;
                event.getController().setAnimation(sit);
            }
            else if (getAttackType() == 1) {
                event.getController().setAnimation(MUTATED_ATTACK1);
            }
            else if (getAttackType() == 2) {
                event.getController().setAnimation(MUTATED_ATTACK2);
            }
            else {
                event.getController().setAnimation(MUTATED_FLY);
            }

        }
        else {
            if (getRitualTicks() > 0) {
                event.getController().setAnimation(HOLY_RITUAL);
            }
            else if (this.getMainAction() == 0) {
                final RawAnimation sit = getSitVariation() == 0 ? HOLY_SIT1 : getSitVariation() == 1 ? HOLY_SIT2 : HOLY_SIT3;
                event.getController().setAnimation(sit);
            }
            else if (getAttackType() == 1) {
                event.setAnimation(HOLY_ATTACK1);
            }
            else if (getAttackType() == 2) {
                event.setAnimation(HOLY_ATTACK2);
            }
            else if (getAttackType() == 3) {
                event.setAnimation(HOLY_APPLY_EFFECTS);
            }
            else if (getAttackType() == 4) {
                event.setAnimation(HOLY_SUMMON_BALLS);
            }
            else if (event.isMoving() && event.getLimbSwingAmount() > 0.35f) {
                event.getController().setAnimation(HOLY_FLY);
            }
            else {
                event.getController().setAnimation(HOLY_FLY_IDLE);
            }

        }

        return PlayState.CONTINUE;
    }

}
