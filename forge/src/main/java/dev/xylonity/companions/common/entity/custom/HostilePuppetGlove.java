package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.tick.TickScheduler;
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
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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

import java.util.UUID;

public class HostilePuppetGlove extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    private final RawAnimation BROOM_IDLE = RawAnimation.begin().thenPlay("broom_idle");
    private final RawAnimation BROOM_DROP = RawAnimation.begin().thenPlay("broom_drop");
    private final RawAnimation BROOM_PICKUP = RawAnimation.begin().thenPlay("broom_pickup");

    private final RawAnimation GAME_IDLE = RawAnimation.begin().thenPlay("game_idle");
    private final RawAnimation GAME_SCISSORS = RawAnimation.begin().thenPlay("game_scissors");
    private final RawAnimation GAME_ROCK = RawAnimation.begin().thenPlay("game_rock");
    private final RawAnimation GAME_PAPER = RawAnimation.begin().thenPlay("game_paper");
    private final RawAnimation GAME_ATTACK = RawAnimation.begin().thenPlay("game_attack");

    private static final EntityDataAccessor<Boolean> IS_PLAYING = SynchedEntityData.defineId(HostilePuppetGlove.class, EntityDataSerializers.BOOLEAN);
    // 0 idle, 1 stone, 2 paper, 3 scissors, 4 attack
    private static final EntityDataAccessor<Integer> GLOVE_MOVE = SynchedEntityData.defineId(HostilePuppetGlove.class, EntityDataSerializers.INT);
    // 0 idle, 1 drop, 2 pickup
    private static final EntityDataAccessor<Integer> BROOM_PHASE = SynchedEntityData.defineId(HostilePuppetGlove.class, EntityDataSerializers.INT);

    private UUID playingPlayerUUID = null;
    private long gameAutoStop = 0;

    public HostilePuppetGlove(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new MoveControl(this);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .build();
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    public void tick() {
        double lastX = this.getX();
        double lastZ = this.getZ();

        super.tick();

        this.setPos(lastX, getY(), lastZ);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (isPlaying()) {
            if (!pPlayer.getUUID().equals(playingPlayerUUID)) {
                return InteractionResult.FAIL;
            }

            if (tickCount > gameAutoStop) {
                setIsPlaying(false);
                setBroomPhase(2);
                TickScheduler.scheduleServer(level(), () -> setBroomPhase(0), 20);
                playingPlayerUUID = null;
                return InteractionResult.SUCCESS;
            }

            ItemStack stack = pPlayer.getItemInHand(pHand);
            if (!isValidItem(stack)) {
                pPlayer.displayClientMessage(Component.literal("Necesitas piedra, papel o tijera"), true);
                return InteractionResult.FAIL;
            }

            if (level() instanceof ServerLevel)
                setGloveMove(random.nextInt(1, 4));

            int res = doesThePlayerWin(getPlayerMoveFromItem(stack), getGloveMove() - 1);
            if (res == 0) {
                //TickScheduler.scheduleServer(level(), () -> setGloveMove(4), 20);
                TickScheduler.scheduleServer(level(), () -> setGloveMove(0), 20);
                pPlayer.displayClientMessage(Component.literal("=="), true);
                //playAnimation(GAME_ATTACK);
            } else if (res == 1) {
                TickScheduler.scheduleServer(level(), () -> setGloveMove(4), 20);
                TickScheduler.scheduleServer(level(), () -> setGloveMove(0), 30);
                pPlayer.displayClientMessage(Component.literal("Gana guante"), true);
            } else {
                pPlayer.displayClientMessage(Component.literal("Gana jugador"), true);

                TickScheduler.scheduleServer(level(), () -> setBroomPhase(2), 20);
                TickScheduler.scheduleServer(level(), () -> setIsPlaying(false), 20);
                TickScheduler.scheduleServer(level(), () -> setBroomPhase(0), 40);

                playingPlayerUUID = null;
            }

            pPlayer.displayClientMessage(Component.literal("" + (res == 1 ? "Has perdido" : "Has ganado o empatado")), true);
            return InteractionResult.SUCCESS;
        } else {
            setBroomPhase(1);
            TickScheduler.scheduleServer(level(), () -> setIsPlaying(true), 20);
            TickScheduler.scheduleServer(level(), () -> setGloveMove(0), 20);

            playingPlayerUUID = pPlayer.getUUID();
            gameAutoStop = tickCount + 600;

            pPlayer.displayClientMessage(Component.literal("Vas a jugar al piedra, papel o tijera"), true);

            return InteractionResult.SUCCESS;
        }
    }

    private int doesThePlayerWin(int playerMove, int gloveMove) {
        if (playerMove == 0) {
            if (gloveMove == 0) return 0;
            else if (gloveMove == 1) return 1;
            else if (gloveMove == 2) return 2;
        } else if (playerMove == 1) {
            if (gloveMove == 0) return 2;
            else if (gloveMove == 1) return 0;
            else if (gloveMove == 2) return 1;
        } else if (playerMove == 2) {
            if (gloveMove == 0) return 1;
            else if (gloveMove == 1) return 2;
            else if (gloveMove == 2) return 0;
        }

        return -1;
    }

    private boolean isValidItem(ItemStack stack) {
        return stack.getItem() == Items.PAPER ||
                stack.getItem() == Items.COBBLESTONE ||
                stack.getItem() == Items.SHEARS;
    }

    private int getPlayerMoveFromItem(ItemStack stack) {
        if (stack.getItem() == Items.COBBLESTONE) {
            return 0;
        } else if (stack.getItem() == Items.PAPER) {
            return 1;
        } else if (stack.getItem() == Items.SHEARS) {
            return 2;
        }

        return -1;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_PLAYING, false);
        this.entityData.define(GLOVE_MOVE, -1);
        this.entityData.define(BROOM_PHASE, 0);
    }

    private void setIsPlaying(boolean playing) {
        this.entityData.set(IS_PLAYING, playing);
    }

    private boolean isPlaying() {
        return this.entityData.get(IS_PLAYING);
    }

    private void setBroomPhase(int phase) {
        this.entityData.set(BROOM_PHASE, phase);
    }

    private int getBroomPhase() {
        return this.entityData.get(BROOM_PHASE);
    }

    private void setGloveMove(int phase) {
        this.entityData.set(GLOVE_MOVE, phase);
    }

    private int getGloveMove() {
        return this.entityData.get(GLOVE_MOVE);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (isPlaying()) {
            if (getGloveMove() == 1) {
                event.setAnimation(GAME_ROCK);
            } else if (getGloveMove() == 2) {
                event.setAnimation(GAME_PAPER);
            } else if (getGloveMove() == 3) {
                event.setAnimation(GAME_SCISSORS);
            } else if (getGloveMove() == 4) {
                event.setAnimation(GAME_ATTACK);
            } else {
                event.setAnimation(GAME_IDLE);
            }
        } else {
            if (getBroomPhase() == 1) {
                event.setAnimation(BROOM_DROP);
            } else if (getBroomPhase() == 2) {
                event.setAnimation(BROOM_PICKUP);
            } else {
                event.setAnimation(BROOM_IDLE);
            }
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
