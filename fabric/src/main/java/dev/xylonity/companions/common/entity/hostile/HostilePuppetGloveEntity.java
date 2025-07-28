package dev.xylonity.companions.common.entity.hostile;

import dev.xylonity.companions.common.entity.companion.PuppetGloveEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import dev.xylonity.knightlib.api.TickScheduler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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

import java.util.List;
import java.util.UUID;

public class HostilePuppetGloveEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation BROOM_IDLE = RawAnimation.begin().thenPlay("broom_idle");
    private final RawAnimation BROOM_DROP = RawAnimation.begin().thenPlay("broom_drop");
    private final RawAnimation BROOM_PICKUP = RawAnimation.begin().thenPlay("broom_pickup");

    private final RawAnimation GAME_IDLE = RawAnimation.begin().thenPlay("game_idle");
    private final RawAnimation GAME_SCISSORS = RawAnimation.begin().thenPlay("game_scissors");
    private final RawAnimation GAME_ROCK = RawAnimation.begin().thenPlay("game_rock");
    private final RawAnimation GAME_PAPER = RawAnimation.begin().thenPlay("game_paper");
    private final RawAnimation GAME_ATTACK = RawAnimation.begin().thenPlay("game_attack");
    private final RawAnimation GAME_LOOSE = RawAnimation.begin().thenPlay("game_loose");
    private final RawAnimation GAME_TAME = RawAnimation.begin().thenPlay("game_tame");

    private static final EntityDataAccessor<Boolean> IS_PLAYING = SynchedEntityData.defineId(HostilePuppetGloveEntity.class, EntityDataSerializers.BOOLEAN);
    // 0 idle, 1 stone, 2 paper, 3 scissors, 4 attack, 5 round loose, 6 tame
    private static final EntityDataAccessor<Integer> GLOVE_MOVE = SynchedEntityData.defineId(HostilePuppetGloveEntity.class, EntityDataSerializers.INT);
    // 0 idle, 1 drop, 2 pickup
    private static final EntityDataAccessor<Integer> BROOM_PHASE = SynchedEntityData.defineId(HostilePuppetGloveEntity.class, EntityDataSerializers.INT);

    public UUID playingPlayerUUID = null;
    private long gameAutoStop = 0;
    private long roundsLost = 0;

    public HostilePuppetGloveEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 160)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 0.01F) {
            @Override
            public boolean canUse() {
                if (mob instanceof HostilePuppetGloveEntity glove) {
                    return glove.playingPlayerUUID != null && super.canUse();
                }

                return super.canUse();
            }
        });
    }

    @Override
    public void tick() {
        double lastX = this.getX();
        double lastZ = this.getZ();

        super.tick();

        if (tickCount > gameAutoStop && !(isPlaying() && getGloveMove() != 0 || !isPlaying() && getBroomPhase() != 0) && playingPlayerUUID != null) {
            setIsPlaying(false);
            setBroomPhase(2);
            TickScheduler.scheduleServer(level(), () -> setBroomPhase(0), 20);
            playingPlayerUUID = null;
        }

        if (tickCount % 15 == 0 && !level().isClientSide && getBroomPhase() == 0 && !isPlaying()) {
            playSound(CompanionsSounds.HOSTILE_PUPPET_GLOVE_CLEAN.get(), 0.5f, 1f);
        }

        this.setPos(lastX, getY(), lastZ);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (isPlaying() && getGloveMove() != 0 || !isPlaying() && getBroomPhase() != 0) return InteractionResult.FAIL;

        Vec3 entityEyePos = this.position().add(0, this.getEyeHeight(), 0);
        Vec3 playerEyePos = pPlayer.position().add(0, pPlayer.getEyeHeight(), 0);
        if (this.getLookAngle().dot(playerEyePos.subtract(entityEyePos).normalize()) < Math.cos(Math.toRadians(45.0))) {
            return InteractionResult.FAIL;
        }

        if (isPlaying()) {
            // If the player who tries to interact is different from the original one
            if (!pPlayer.getUUID().equals(playingPlayerUUID)) {
                return InteractionResult.FAIL;
            }

            ItemStack stack = pPlayer.getItemInHand(pHand);
            if (!isValidItem(stack)) {
                pPlayer.displayClientMessage(Component.translatable("hostile_puppet_glove.companions.client_message.wrong_item"), true);
                return InteractionResult.FAIL;
            }

            // We set a pseudorandom move if the interaction is correct
            if (level() instanceof ServerLevel)
                setGloveMove(random.nextInt(1, 4));

            // 20 ticks delay from now on because of the glove move above, which is 20 ticks long, so the consecutive tasks
            // are delayed

            // Checks if the player wins. 0 draw, 1 glove, 2 player.
            int res = doesThePlayerWin(getPlayerMoveFromItem(stack), getGloveMove());
            if (res == 0) {
                // Triggers playing idle animation
                TickScheduler.scheduleServer(level(), () -> setGloveMove(0), 20);

                pPlayer.displayClientMessage(Component.translatable("hostile_puppet_glove.companions.client_message.draw"), true);
            } else if (res == 1) {
                // Punches the player and goes back to idle
                TickScheduler.scheduleServer(level(), () -> setGloveMove(4), 20);
                TickScheduler.scheduleServer(level(), () -> playSound(CompanionsSounds.HOSTILE_PUPPET_GLOVE_ATTACK.get()), 20);

                // Delayed hurt
                List<Player> list = this.level().getEntitiesOfClass(Player.class, new AABB(this.blockPosition()).inflate(4));
                if (list.contains(pPlayer) && isEntityInFront(this, pPlayer, 120)) {
                    TickScheduler.scheduleServer(level(), () -> doHurtTarget(pPlayer), 25);
                }

                TickScheduler.scheduleServer(level(), () -> setGloveMove(0), 30);

                pPlayer.displayClientMessage(Component.translatable("hostile_puppet_glove.companions.client_message.glove_wins"), true);
            } else {
                if (roundsLost != 3) {
                    TickScheduler.scheduleServer(level(), () -> setGloveMove(5), 20);
                    TickScheduler.scheduleServer(level(), () -> playSound(CompanionsSounds.HOSTILE_PUPPET_GLOVE_LOOSE.get()), 20);
                    TickScheduler.scheduleServer(level(), () -> setGloveMove(0), 20 + 33);

                    pPlayer.displayClientMessage(Component.translatable("hostile_puppet_glove.companions.client_message.player_wins_round"), true);

                    roundsLost++;
                } else {
                    TickScheduler.scheduleServer(level(), () -> setGloveMove(6), 20);

                    TickScheduler.scheduleServer(level(), () -> {

                        PuppetGloveEntity glove = CompanionsEntities.PUPPET_GLOVE.create(level());
                        if (glove != null) {
                            for (int i = 0; i < 50; i++) {
                                double dx = (this.random.nextDouble() - 0.5) * 2.0;
                                double dy = (this.random.nextDouble() - 0.5) * 2.0;
                                double dz = (this.random.nextDouble() - 0.5) * 2.0;
                                if (this.level() instanceof ServerLevel level) {
                                    level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + getBbHeight() * 0.5, this.getZ(), 1, dx, dy, dz, 0.1);
                                }
                            }

                            glove.tame(pPlayer);
                            glove.moveTo(this.blockPosition().getCenter());

                            glove.setXRot(this.getXRot());
                            glove.setYRot(this.getYRot());
                            level().addFreshEntity(glove);
                        }

                        this.remove(RemovalReason.DISCARDED);
                    }, 64);

                    pPlayer.displayClientMessage(Component.translatable("hostile_puppet_glove.companions.client_message.player_wins"), true);

                    playingPlayerUUID = null;
                }
            }

            // Reset the counter
            gameAutoStop = tickCount + 600;

        } else {
            // If it's not playing we trigger the broom_drop animation and schedule the start of the game after the animation's end
            setBroomPhase(1);
            TickScheduler.scheduleServer(level(), () -> setIsPlaying(true), 20);
            // Idle state while playing
            TickScheduler.scheduleServer(level(), () -> setGloveMove(0), 20);

            // The player that started the game is the only one that can play
            playingPlayerUUID = pPlayer.getUUID();
            // We will tick this counter to auto-stop the game after 30 seconds
            gameAutoStop = tickCount + 600;

            pPlayer.displayClientMessage(Component.translatable("hostile_puppet_glove.companions.client_message.game_start"), true);

        }

        return InteractionResult.SUCCESS;
    }

    public static boolean isEntityInFront(LivingEntity viewer, Entity target, double fov) {
        Vec3 view = viewer.getLookAngle().normalize();
        Vec3 toTarget = new Vec3(target.getX(), viewer.getY(), target.getZ()).subtract(viewer.position()).normalize();
        double angle = Math.acos(view.dot(toTarget)) * (180.0 / Math.PI);
        return angle < (fov / 2);
    }

    // Handles every move and checks if the players wins
    private int doesThePlayerWin(int playerMove, int gloveMove) {
        if (playerMove == 0) {
            if (gloveMove == 1) return 0;
            else if (gloveMove == 2) return 1;
            else if (gloveMove == 3) return 2;
        } else if (playerMove == 1) {
            if (gloveMove == 1) return 2;
            else if (gloveMove == 2) return 0;
            else if (gloveMove == 3) return 1;
        } else if (playerMove == 2) {
            if (gloveMove == 1) return 1;
            else if (gloveMove == 2) return 2;
            else if (gloveMove == 3) return 0;
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
            } else if (getGloveMove() == 5) {
                event.setAnimation(GAME_LOOSE);
            } else if (getGloveMove() == 6) {
                event.setAnimation(GAME_TAME);
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
