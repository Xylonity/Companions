package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class FrogBonanzaBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final RawAnimation COIN = RawAnimation.begin().thenPlay("coin");
    public static final RawAnimation LEVER = RawAnimation.begin().thenPlay("lever");

    private static final int DELAY_BETWEEN_WHEELS = 4;
    private static final int GAME_TICKS = 100;
    private static final float EXTRA_SPINS = 720f; // meant for recalculating the final pos

    private final float[] rotation = new float[3];
    private final float[] startRot = new float[3];
    private final float[] totalRot = new float[3];
    private final int[] faceDeg = new int[3];
    private int spinStartTick;
    private int tickCount;

    private int spinsRemaining = 0;

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

        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
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

    private void doubleCreeper() {
        if (getLevel() instanceof ServerLevel server) {
            BlockPos center = worldPosition.above();
            for (int i = 0; i < 2 + new Random().nextInt(3); i++) {
                PrimedTnt tnt = new PrimedTnt(server, center.getX() + 0.5, center.getY(), center.getZ() + 0.5, null);
                double angle = getLevel().random.nextDouble() * Math.PI * 2;
                double speed = 0.15 + getLevel().random.nextDouble() * 0.6;
                tnt.setDeltaMovement(Math.cos(angle) * speed, 0.5 + getLevel().random.nextDouble() * 0.3, Math.sin(angle) * speed);
                tnt.setFuse(40);
                server.addFreshEntity(tnt);
            }

            getLevel().playSound(null, getBlockPos(), CompanionsSounds.POP.get(), SoundSource.BLOCKS);
        }

    }

    private void doubleCoin()  {
        if (getLevel() != null) {
            popResource(getLevel(), getBlockPos(), new ItemStack(CompanionsBlocks.COPPER_COIN.get(), new Random().nextInt(2, 10)));
            popResource(getLevel(), getBlockPos(), new ItemStack(CompanionsBlocks.NETHER_COIN.get(), new Random().nextInt(1, 4)));
            popResource(getLevel(), getBlockPos(), new ItemStack(CompanionsBlocks.END_COIN.get()));

            getLevel().playSound(null, getBlockPos(), CompanionsSounds.POP.get(), SoundSource.BLOCKS);
        }

    }

    private void doubleTeddy() {
        if (getLevel() != null) {
            RandomSource rand = getLevel().getRandom();

            popResource(getLevel(), getBlockPos(), new ItemStack(Items.GOLDEN_APPLE, 1));
            popResource(getLevel(), getBlockPos(), new ItemStack(Items.FEATHER, rand.nextInt(1, 5)));

            if (rand.nextFloat() < 0.5f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.ENDER_PEARL, 1));
            }

            if (rand.nextFloat() < 0.25f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, rand.nextInt(1, 2)));
            }

            if (rand.nextFloat() < 0.05f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.NETHERITE_SCRAP, 1));
            }

            if (rand.nextFloat() < 0.10f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.WITHER_SKELETON_SKULL));
            }

            if (rand.nextFloat() < 0.10f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.AXOLOTL_BUCKET));
            }

            if (rand.nextFloat() < 0.05f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.MUSIC_DISC_PIGSTEP));
            }

            if (rand.nextFloat() < 0.15f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.DIAMOND, 1));
            }

            if (rand.nextFloat() < 0.20f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.EMERALD, rand.nextInt(1, 3)));
            }

            if (rand.nextFloat() < 0.10f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.EXPERIENCE_BOTTLE, rand.nextInt(1, 4)));
            }

            if (rand.nextFloat() < 0.005f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.NETHER_STAR, 1));
            }

            getLevel().playSound(null, getBlockPos(), CompanionsSounds.POP.get(), SoundSource.BLOCKS);
        }

    }

    private void doubleSkull() {
        if (getLevel() instanceof ServerLevel serverLevel) {
            Vec3 center = Vec3.atCenterOf(getBlockPos());
            Player player = serverLevel.getNearestPlayer(center.x, center.y, center.z, 15.0, false);
            if (player != null) {
                FallingBlockEntity anvil = new FallingBlockEntity(serverLevel, player.getX(), player.getY() + 15, player.getZ(), Blocks.ANVIL.defaultBlockState());

                anvil.time = 1;
                anvil.setHurtsEntities(2.0f, 40);
                serverLevel.addFreshEntity(anvil);
            }

            getLevel().playSound(null, getBlockPos(), CompanionsSounds.POP.get(), SoundSource.BLOCKS);
        }

    }

    private void tripleCreeper() {
        if (getLevel() instanceof ServerLevel server) {
            BlockPos center = worldPosition.above();

            for (int i = 0; i < 3; i++) {
                double angle = new Random().nextDouble() * Math.PI * 2;
                double dist = new Random().nextDouble() * 4;
                double x = center.getX() + 0.5 + Math.cos(angle) * dist;
                double y = center.getY();
                double z = center.getZ() + 0.5 + Math.sin(angle) * dist;

                Creeper creeper = EntityType.CREEPER.create(server);
                if (creeper != null) {
                    creeper.moveTo(x, y, z, new Random().nextFloat() * 360F, 0F);
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
                    if (bolt != null) {
                        bolt.moveTo(x, y, z);
                        server.addFreshEntity(bolt);
                        creeper.thunderHit(server, bolt);
                    }

                    server.addFreshEntity(creeper);
                }
            }

            getLevel().playSound(null, getBlockPos(), CompanionsSounds.POP.get(), SoundSource.BLOCKS);
        }

    }

    private void tripleCoin()  {
        if (getLevel() != null) {
            RandomSource rand = getLevel().getRandom();

            popResource(getLevel(), getBlockPos(), new ItemStack(CompanionsBlocks.COPPER_COIN.get(), rand.nextInt(1, 20)));

            if (rand.nextFloat() < 0.75f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(CompanionsBlocks.NETHER_COIN.get(), rand.nextInt(1, 5)));
            }

            if (rand.nextFloat() < 0.45f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(CompanionsBlocks.END_COIN.get(), 1));
            }

            getLevel().playSound(null, getBlockPos(), CompanionsSounds.POP.get(), SoundSource.BLOCKS);
        }

    }

    private void tripleTeddy() {
        if (getLevel() != null) {
            RandomSource rand = getLevel().getRandom();

            popResource(getLevel(), getBlockPos(), new ItemStack(Items.GOLDEN_APPLE, rand.nextInt(1, 4)));

            if (rand.nextFloat() < 0.8f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, rand.nextInt(1, 4)));
            }

            if (rand.nextFloat() < 0.75f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.ENDER_PEARL, rand.nextInt(1, 3)));
            }

            if (rand.nextFloat() < 0.75f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.ENDER_EYE, rand.nextInt(1, 3)));
            }

            if (rand.nextFloat() < 0.65f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.DIAMOND, rand.nextInt(1, 10)));
            }

            if (rand.nextFloat() < 0.85f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.GOLD_INGOT, rand.nextInt(1, 20)));
            }

            if (rand.nextFloat() < 0.55f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.CAKE, 1));
            }

            if (rand.nextFloat() < 0.3f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.NETHERITE_SCRAP, 1));
            } else if (rand.nextFloat() < 0.1f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.NETHERITE_INGOT, 1));
            }

            if (rand.nextFloat() < 0.05f) {
                popResource(getLevel(), getBlockPos(), new ItemStack(Items.NETHER_STAR, 1));
            }

           //if (rand.nextFloat() < 0.15f) {
           //    popResource(getLevel(), getBlockPos(), EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.FIRE_ASPECT, 2)));
           //}

           //if (rand.nextFloat() < 0.1f) {
           //    popResource(getLevel(), getBlockPos(), EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.PROTECTION.getOrThrow(level), 2)));
           //}

            getLevel().playSound(null, getBlockPos(), CompanionsSounds.POP.get(), SoundSource.BLOCKS);
        }

    }

    private void tripleSkull() {
        if (!(level instanceof ServerLevel server)) return;
        BlockPos center = worldPosition.above();

        Player player = server.getNearestPlayer(center.getX(), center.getY(), center.getZ(), 15.0,false);
        if (player == null) return;

        SpawnUtil.trySpawnMob(EntityType.WARDEN, MobSpawnType.TRIGGERED, server, center, 20, 5, 6, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER
        ).ifPresent(warden -> {
            warden.moveTo(warden.getX(), warden.getY(), warden.getZ(), player.getYRot(), 0);
            warden.setTarget(player);
            warden.getBrain().setMemory(MemoryModuleType.ANGRY_AT, player.getUUID());
        });

        level.playSound(null, center, CompanionsSounds.POP.get(), SoundSource.BLOCKS, 1f, 1f);
    }

    public static void popResource(Level pLevel, BlockPos pPos, ItemStack pStack) {
        double d0 = EntityType.ITEM.getHeight() / 2f;
        double d1 = pPos.getX() + 0.5;
        double d2 = pPos.getY() + 1.5 + Mth.nextDouble(pLevel.random, -0.25, 0.25) - d0;
        double d3 = pPos.getZ() + 0.5;
        popResource(pLevel, () -> new ItemEntity(pLevel, d1, d2, d3, pStack, -0.25 + Math.random() * 0.25f, -0.35 + Math.random() * 0.35f, -0.25 + Math.random() * 0.25f), pStack);
    }

    private static void popResource(Level pLevel, Supplier<ItemEntity> pItemEntitySupplier, ItemStack pStack) {
        if (!pLevel.isClientSide && !pStack.isEmpty()) {
            ItemEntity itementity = pItemEntitySupplier.get();
            itementity.setDefaultPickUpDelay();
            pLevel.addFreshEntity(itementity);
        }

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("SpinStart", this.spinStartTick);
        tag.putInt("TickCount", this.tickCount);
        tag.put(TAG_ROT, Util.floatsToList(rotation));
        tag.put(TAG_START, Util.floatsToList(startRot));
        tag.put(TAG_TOTAL, Util.floatsToList(totalRot));
        tag.put(TAG_FACE, Util.intsToList(faceDeg));
        tag.putInt("SpinsRemaining", this.spinsRemaining);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.spinStartTick = tag.getInt("SpinStart");
        this.tickCount = tag.getInt("TickCount");
        Util.listToFloats(tag.getList(TAG_ROT, Tag.TAG_FLOAT), this.rotation);
        Util.listToFloats(tag.getList(TAG_START, Tag.TAG_FLOAT), this.startRot);
        Util.listToFloats(tag.getList(TAG_TOTAL, Tag.TAG_FLOAT), this.totalRot);
        Util.listToInts(tag.getList(TAG_FACE, Tag.TAG_INT), this.faceDeg);
        this.spinsRemaining = tag.getInt("SpinsRemaining");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    private void sync() {
        if (!(level instanceof ServerLevel)) return;

        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public ItemInteractionResult interact(Player player, InteractionHand hand) {
        if (level == null) return ItemInteractionResult.FAIL;

        if (level.isClientSide) return ItemInteractionResult.SUCCESS;

        if (spinStartTick >= 0) return ItemInteractionResult.FAIL;

        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();

        if (spinsRemaining <= 0) {
            if (item == CompanionsBlocks.COPPER_COIN.get().asItem()) {
                spinsRemaining = 1;
            } else if (item == CompanionsBlocks.NETHER_COIN.get().asItem()) {
                spinsRemaining = 3;
            } else if (item == CompanionsBlocks.END_COIN.get().asItem()) {
                spinsRemaining = 5;
            } else {
                return ItemInteractionResult.FAIL;
            }

            if (!player.getAbilities().instabuild) stack.shrink(1);

            triggerAnim("coin_controller", "coin");
            sync();

            level.playSound(null, getBlockPos(), CompanionsSounds.COIN_CLATTER.get(), SoundSource.BLOCKS, 1f, 1f);

            return ItemInteractionResult.SUCCESS;
        }

        spinsRemaining--;
        triggerAnim("lever_controller", "lever");
        startGame();
        sync();

        level.playSound(null, getBlockPos(), CompanionsSounds.BONANZA.get(), SoundSource.BLOCKS, 0.35f, 1f);

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public double getTick(Object o) {
        return RenderUtil.getCurrentTick();
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