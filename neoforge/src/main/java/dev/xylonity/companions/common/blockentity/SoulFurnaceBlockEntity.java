package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.SoulFurnaceBlock;
import dev.xylonity.companions.common.container.SoulFurnaceContainerMenu;
import dev.xylonity.companions.registry.*;
import dev.xylonity.knightlib.common.blockentity.GreatChaliceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;
import java.util.Random;

public class SoulFurnaceBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider, Container {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private static final int MAX_CHARGES = 9;

    public int charges = 0;
    private int currentProgress = 0;
    private int processingTime = 0;

    private SoulFurnaceRecipe currentRecipe = null;

    public boolean isLit;

    public static final List<SoulFurnaceRecipe> RECIPES = List.of(
            new SoulFurnaceRecipe(Items.CANDLE, null, 1, 200, CompanionsEntities.LIVING_CANDLE.get(), null),
            new SoulFurnaceRecipe(Items.DIAMOND, CompanionsItems.SOUL_GEM.get(), 3, 400, null, null),
            new SoulFurnaceRecipe(Items.ROTTEN_FLESH, CompanionsItems.CRYSTALLIZED_BLOOD.get(), 2, 100, null, null),
            new SoulFurnaceRecipe(CompanionsItems.BIG_BREAD.get(), null, 5, 2400, null, CompanionsBlocks.CROISSANT_EGG.get())
    );

    public SoulFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.SOUL_FURNACE.get(), pos, state);
        this.isLit = false;
    }

    @Override
    public boolean canPlaceItem(int pIndex, @NotNull ItemStack pStack) {
        return pIndex == 0;
    }

    @Override
    public boolean canTakeItem(@NotNull Container pTarget, int pIndex, @NotNull ItemStack pStack) {
        return pIndex == 1;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T F) {
        if (F instanceof SoulFurnaceBlockEntity furnace) {
            if (level.isClientSide()) return;

            furnace.handleGreatChaliceInteraction(level, pos);

            if (furnace.currentRecipe == null && !furnace.getItem(0).isEmpty()) {
                ItemStack inputStack = furnace.getItem(0);
                for (SoulFurnaceRecipe recipe : RECIPES) {
                    if (recipe.input == inputStack.getItem() && furnace.charges >= recipe.requiredCharges) {
                        inputStack.shrink(1);

                        furnace.currentRecipe = recipe;
                        furnace.processingTime = recipe.processTime;
                        furnace.currentProgress = 0;
                        furnace.setChanged();

                        break;
                    }
                }
            }

            if (furnace.currentRecipe != null) {
                furnace.handleCooking(pos, level, state);
            }

            // lits the block when the progress is not zero
            boolean lit = furnace.currentRecipe != null;
            BlockState currentState = level.getBlockState(pos);
            if (currentState.hasProperty(SoulFurnaceBlock.LIT) && currentState.getValue(SoulFurnaceBlock.LIT) != lit) {
                level.setBlock(pos, currentState.setValue(SoulFurnaceBlock.LIT, lit), 3);
            }

        }

    }

    private void handleGreatChaliceInteraction(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos.above()) instanceof GreatChaliceBlockEntity be && this.charges < MAX_CHARGES) {
            if (be.isFull()) {
                be.setCharges(0);

                level.playSound(null, pos.above(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1f, 1f);

                this.charges = Math.min(this.charges + 1, MAX_CHARGES);
                this.setChanged();
            }
        }

    }

    private void handleCooking(BlockPos pos, Level level, BlockState state) {
        this.currentProgress++;
        if (this.currentProgress >= this.processingTime) {

            level.playSound(null, pos.above(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1f, 1f);

            SoulFurnaceRecipe recipe = this.currentRecipe;

            if (recipe.output == null || recipe.output == Items.AIR) {
                // spawn entity
                if (recipe.entityType != null) {
                    BlockPos spawnPos = pos.relative(state.getValue(SoulFurnaceBlock.FACING));
                    Entity e = recipe.entityType.create(level);
                    if (e != null) {
                        e.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                        level.addFreshEntity(e);
                    }

                }
                else {
                    // spawn block
                    if (recipe.block() != null) {
                        BlockPos targetPos = pos.relative(state.getValue(SoulFurnaceBlock.FACING));
                        if (level.isEmptyBlock(targetPos)) {
                            BlockState recipeState = recipe.block().defaultBlockState();

                            // set block in front of the furnace
                            if (recipeState.hasProperty(SoulFurnaceBlock.FACING)) {
                                recipeState = recipeState.setValue(SoulFurnaceBlock.FACING, state.getValue(SoulFurnaceBlock.FACING));
                            } else if (recipeState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                                recipeState = recipeState.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(SoulFurnaceBlock.FACING));
                            }

                            level.setBlockAndUpdate(targetPos, recipeState);

                            spawnPoofParticles(targetPos);

                        }
                        // set output item
                        else {
                            if (level.getBlockState(targetPos).getBlock() == recipe.block()) {
                                BlockPos stackPos = targetPos;
                                while (!level.isEmptyBlock(stackPos) && stackPos.getY() < level.getMaxBuildHeight()) {
                                    stackPos = stackPos.above();
                                }

                                if (level.isEmptyBlock(stackPos)) {
                                    level.setBlockAndUpdate(stackPos, recipe.block().defaultBlockState());
                                } else {
                                    Containers.dropItemStack(level, targetPos.getX(), targetPos.getY(), targetPos.getZ(), new ItemStack(recipe.block()));
                                }

                            } else {
                                Containers.dropItemStack(level, targetPos.getX(), targetPos.getY(), targetPos.getZ(), new ItemStack(recipe.block()));
                            }

                        }

                        level.playSound(null, pos, CompanionsSounds.POP.get(), SoundSource.BLOCKS, 0.65f, 1f);
                    }

                }
            }
            else {
                if (this.getItem(1).isEmpty()) {
                    this.setItem(1, new ItemStack(recipe.output));
                } else if (this.getItem(1).getItem() == recipe.output) {
                    this.getItem(1).grow(1);
                }

            }

            this.charges -= recipe.requiredCharges;
            this.currentRecipe = null;
            this.currentProgress = 0;
            this.processingTime = 0;
            this.setChanged();

            // handles time progress
            ItemStack stack = this.getItem(0);
            if (!stack.isEmpty()) {
                for (SoulFurnaceRecipe r : RECIPES) {
                    if (r.input == stack.getItem() && this.charges >= r.requiredCharges) {
                        stack.shrink(1);

                        this.currentRecipe = r;
                        this.processingTime = r.processTime;
                        this.currentProgress = 0;

                        this.setChanged();

                        break;
                    }
                }
            }

        }

    }

    private void spawnPoofParticles(BlockPos targetPos) {
        for (int i = 0; i < 25; i++) {
            double dx = (new Random().nextDouble() - 0.5) * 2.5;
            double dy = (new Random().nextDouble() - 0.5) * 1.5;
            double dz = (new Random().nextDouble() - 0.5) * 2.5;
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.POOF, targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1, dx, dy, dz, 0.04);
            }
        }
    }

    public static boolean isValidInput(ItemStack stack) {
        for (SoulFurnaceRecipe recipe : RECIPES) {
            if (recipe.input == stack.getItem()) return true;
        }

        return false;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.charges = tag.getInt("Charges");
        this.currentProgress = tag.getInt("Progress");
        this.processingTime = tag.getInt("ProcessingTime");

        if (tag.contains("CurrentRecipeIndex")) {
            int index = tag.getInt("CurrentRecipeIndex");
            if (index >= 0 && index < RECIPES.size()) {
                this.currentRecipe = RECIPES.get(index);
            }
        }

        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("Charges", this.charges);
        tag.putInt("Progress", this.currentProgress);
        tag.putInt("ProcessingTime", this.processingTime);

        if (this.currentRecipe != null) {
            int index = RECIPES.indexOf(this.currentRecipe);
            tag.putInt("CurrentRecipeIndex", index);
        } else {
            tag.putInt("CurrentRecipeIndex", -1);
        }

        ContainerHelper.saveAllItems(tag, this.items, provider);
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Soul Furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new SoulFurnaceContainerMenu(id, inventory, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> charges;
                    case 1 -> currentProgress;
                    case 2 -> processingTime;
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        charges = value;
                        break;
                    case 1:
                        currentProgress = value;
                        break;
                    case 2:
                        processingTime = value;
                        break;
                }
            }
            @Override
            public int getCount() {
                return 3;
            }
        });
    }

    @Override
    public double getTick(Object o) {
        return RenderUtil.getCurrentTick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { ;; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(this.items, index, count);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        if (index == 0 && !stack.isEmpty() && this.currentRecipe == null) {
            for (SoulFurnaceRecipe recipe : RECIPES) {
                if (recipe.input == stack.getItem() && this.charges >= recipe.requiredCharges) {
                    stack.shrink(1);
                    this.currentRecipe = recipe;
                    this.processingTime = recipe.processTime;
                    this.currentProgress = 0;
                    this.setChanged();
                    break;
                }
            }

        }

        this.items.set(index, stack);
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public static int getMaxCharges() {
        return MAX_CHARGES;
    }

    public boolean isLit() {
        return isLit;
    }

    public record SoulFurnaceRecipe(Item input, Item output, int requiredCharges, int processTime, EntityType<?> entityType, Block block) { ;; }
}