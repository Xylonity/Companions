package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.item.blockitem.CoinItem;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class FrogBonanzaBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final RawAnimation COIN = RawAnimation.begin().thenPlay("coin");
    public static final RawAnimation LEVER = RawAnimation.begin().thenPlay("lever");

    private boolean coinInserted = false;

    private static final int DELAY_BETWEEN_WHEELS = 4;
    private static final int GAME_TICKS = 100;
    private static final float EXTRA_SPINS = 720f; // meant for recalculating the final pos

    private final float[] rotation = new float[3];
    private final float[] startRot = new float[3];
    private final float[] totalRot = new float[3];
    private final int[] faceDeg = new int[3];
    private int spinStartTick;
    private int tickCount;

    private static final String TAG_ROT = "Rot";
    private static final String TAG_FACE = "Face";
    private static final String TAG_START = "Start";
    private static final String TAG_TOTAL = "Total";

    public FrogBonanzaBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.FROG_BONANZA.get(), pos, state);
        this.tickCount = 0;
        this.spinStartTick = -1;
    }

    public void startSpinning(int[] finalFacesDeg) {
        if (this.level == null || this.level.isClientSide) return;

        for (int i = 0; i < 3; i++) {
            faceDeg[i] = finalFacesDeg[i] % 360;
            startRot[i] = rotation[i];

            float delta = faceDeg[i] - Util.normalizeDeg(startRot[i]);
            if (delta < 0) {
                delta += 360f;
            }

            totalRot[i] = delta + EXTRA_SPINS;
        }

        spinStartTick = tickCount;
        sync();
    }

    public void startGame() {
        startSpinning(new int[]{new Random().nextInt(4) * 90, new Random().nextInt(4) * 90, new Random().nextInt(4) * 90});
    }

    public float getWheelRotation(int idx) {
        return rotation[idx];
    }

    public static <T extends BlockEntity> void tick(Level lvl, BlockPos pos, BlockState st, T be) {
        if (!(be instanceof FrogBonanzaBlockEntity b)) return;

        if (b.spinStartTick < 0) return;

        boolean hasFinished = true;
        for (int i = 0; i < 3; i++) {
            int local = b.tickCount - (b.spinStartTick + i * DELAY_BETWEEN_WHEELS);
            if (local < 0) {
                hasFinished = false;
                continue;
            }

            if (local >= GAME_TICKS) {
                b.rotation[i] = b.faceDeg[i];
            } else {
                hasFinished = false;
                // eased rotation
                b.rotation[i] = Util.normalizeDeg(b.startRot[i] + b.totalRot[i] * (1f - (float) Math.pow(1f - (local / (float) GAME_TICKS), 3)));
            }

        }

        if (hasFinished) {
            b.spinStartTick = -1;
            b.getFroggyReward();
            b.sync();
        }

        b.tickCount++;
    }

    private void getFroggyReward() {
        Map<Integer, Integer> counts = new ConcurrentHashMap<>();
        for (int f : faceDeg) {
            counts.put(f, counts.getOrDefault(f, 0) + 1);
        }

        if (counts.size() == 1) {
            switch (faceDeg[0]) {
                case 0 -> tripleCreeper();
                case 90 -> tripleCoin();
                case 180 -> tripleTeddy();
                case 270 -> tripleSkull();
            }

            return;
        }

        for (var entry : counts.entrySet()) {
            if (entry.getValue() == 2) {
                switch (entry.getKey()) {
                    case 0 -> doubleCreeper();
                    case 90 -> doubleCoin();
                    case 180 -> doubleTeddy();
                    case 270 -> doubleSkull();
                }

                return;
            }
        }

    }

    private void doubleCreeper()   {

    }

    private void doubleCoin()  {

    }

    private void doubleTeddy() {

    }

    private void doubleSkull() {

    }

    private void tripleCreeper()   {

    }

    private void tripleCoin()  {

    }

    private void tripleTeddy() {

    }

    private void tripleSkull() {

    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("SpinStart", this.spinStartTick);
        tag.putInt("TickCount", this.tickCount);
        tag.put(TAG_ROT, Util.floatsToList(rotation));
        tag.put(TAG_START, Util.floatsToList(startRot));
        tag.put(TAG_TOTAL, Util.floatsToList(totalRot));
        tag.put(TAG_FACE, Util.intsToList(faceDeg));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.spinStartTick = tag.getInt("SpinStart");
        this.tickCount = tag.getInt("TickCount");
        Util.listToFloats(tag.getList(TAG_ROT, Tag.TAG_FLOAT), this.rotation);
        Util.listToFloats(tag.getList(TAG_START, Tag.TAG_FLOAT), this.startRot);
        Util.listToFloats(tag.getList(TAG_TOTAL, Tag.TAG_FLOAT), this.totalRot);
        Util.listToInts(tag.getList(TAG_FACE, Tag.TAG_INT), this.faceDeg);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    private void sync() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Packet<ClientGamePacketListener> pkt = ClientboundBlockEntityDataPacket.create(this);
        serverLevel.getChunkSource().chunkMap.getPlayers(serverLevel.getChunkAt(worldPosition).getPos(), false).forEach(p -> p.connection.send(pkt));
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (level == null) return InteractionResult.PASS;

        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (spinStartTick >= 0) return InteractionResult.PASS;

        if (!coinInserted) {
            if (player.getItemInHand(hand).getItem() instanceof CoinItem) {

                if (!player.getAbilities().instabuild) player.getItemInHand(hand).shrink(1);

                coinInserted = true;

                triggerAnim("coin_controller", "coin");
                sync();

                level.playSound(null, getBlockPos(), CompanionsSounds.COIN_CLATTER.get(), SoundSource.BLOCKS, 1f, 1f);

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }

        coinInserted = false;

        triggerAnim("lever_controller", "lever");
        startGame();
        sync();

        level.playSound(null, getBlockPos(), CompanionsSounds.BONANZA.get(), SoundSource.BLOCKS, 0.65f, 1f);

        return InteractionResult.SUCCESS;
    }

    @Override
    public double getTick(Object o) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "coin_controller", state -> PlayState.STOP).triggerableAnim("coin", COIN));
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "lever_controller", state -> PlayState.STOP).triggerableAnim("lever", LEVER));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}