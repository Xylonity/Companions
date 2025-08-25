package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.SoulFurnaceBlock;
import dev.xylonity.companions.common.container.SoulFurnaceContainerMenu;
import dev.xylonity.companions.common.recipe.SoulFurnaceRecipe;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsRecipes;
import dev.xylonity.knightlib.common.blockentity.GreatChaliceBlockEntity;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.List;
import java.util.Random;

public class SoulFurnaceBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider, Container {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private static final int MAX_CHARGES = 9;
    public int charges = 0;
    private int currentProgress = 0;
    private int processingTime = 0;

    @Nullable
    private SoulFurnaceRecipe currentRecipe = null;

    public boolean isLit;

    public SoulFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.SOUL_FURNACE, pos, state);
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

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        if (!(be instanceof SoulFurnaceBlockEntity furnace) || level.isClientSide()) return;

        furnace.handleGreatChaliceInteraction(level, pos);

        if (furnace.currentRecipe == null && !furnace.getItem(0).isEmpty()) {
            ItemStack inputStack = furnace.getItem(0);
            SoulFurnaceRecipe found = furnace.findRecipe(level, inputStack);
            if (found != null) {
                inputStack.shrink(1);
                furnace.currentRecipe = found;
                furnace.processingTime = found.processTime();
                furnace.currentProgress = 0;
                furnace.setChanged();
            }
        }

        if (furnace.currentRecipe != null) {
            furnace.handleCooking(pos, level, state);
        }

        boolean lit = furnace.currentRecipe != null;
        BlockState currentState = level.getBlockState(pos);
        if (currentState.hasProperty(SoulFurnaceBlock.LIT) && currentState.getValue(SoulFurnaceBlock.LIT) != lit) {
            level.setBlock(pos, currentState.setValue(SoulFurnaceBlock.LIT, lit), 3);
        }

        if (CompanionsConfig.SOUL_FURNACE_CONSTANT_MAX_CHARGES) furnace.charges = SoulFurnaceBlockEntity.MAX_CHARGES;

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
        if (this.currentRecipe == null) return;
        if (this.currentProgress < this.processingTime) return;

        SoulFurnaceRecipe recipe = this.currentRecipe;

        level.playSound(null, pos.above(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1f, 1f);

        if (recipe.outputsItem()) {
            ItemStack output = new ItemStack(recipe.resultItem(), Math.max(1, recipe.resultCount()));
            ItemStack current = this.getItem(1);
            if (current.isEmpty()) {
                this.setItem(1, output);
            } else if (ItemStack.isSameItemSameTags(current, output) || current.is(output.getItem())) {
                current.grow(output.getCount());
            } else {
                Containers.dropItemStack(level, pos.getX(), pos.getY() + 1, pos.getZ(), output);
            }

        } else if (recipe.outputsEntity()) {
            BlockPos spawnPos = pos.relative(state.getValue(SoulFurnaceBlock.FACING));
            Entity e = recipe.resultEntity().create(level);
            if (e != null) {
                e.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                level.addFreshEntity(e);
            }

        } else if (recipe.outputsBlock()) {
            Block target = recipe.resultBlock();
            BlockPos targetPos = pos.relative(state.getValue(SoulFurnaceBlock.FACING));
            if (level.isEmptyBlock(targetPos)) {
                BlockState recipeState = target.defaultBlockState();
                if (recipeState.hasProperty(SoulFurnaceBlock.FACING)) {
                    recipeState = recipeState.setValue(SoulFurnaceBlock.FACING, state.getValue(SoulFurnaceBlock.FACING));
                } else if (recipeState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    recipeState = recipeState.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(SoulFurnaceBlock.FACING));
                }

                level.setBlockAndUpdate(targetPos, recipeState);
                spawnPoofParticles(targetPos);
            } else {
                if (level.getBlockState(targetPos).getBlock() == target) {
                    BlockPos stackPos = targetPos;
                    while (!level.isEmptyBlock(stackPos) && stackPos.getY() < level.getMaxBuildHeight()) {
                        stackPos = stackPos.above();
                    }

                    if (level.isEmptyBlock(stackPos)) {
                        level.setBlockAndUpdate(stackPos, target.defaultBlockState());
                    } else {
                        Containers.dropItemStack(level, targetPos.getX(), targetPos.getY(), targetPos.getZ(), new ItemStack(target));
                    }

                } else {
                    Containers.dropItemStack(level, targetPos.getX(), targetPos.getY(), targetPos.getZ(), new ItemStack(target));
                }

            }

        }

        this.charges -= recipe.requiredCharges();
        this.currentRecipe = null;
        this.currentProgress = 0;
        this.processingTime = 0;
        this.setChanged();

        ItemStack stack = this.getItem(0);
        if (!stack.isEmpty()) {
            SoulFurnaceRecipe next = findRecipe(level, stack);
            if (next != null) {
                stack.shrink(1);
                this.currentRecipe = next;
                this.processingTime = next.processTime();
                this.currentProgress = 0;
                this.setChanged();
            }
        }

    }

    @Nullable
    private SoulFurnaceRecipe findRecipe(Level level, ItemStack input) {
        if (input.isEmpty()) return null;
        List<? extends Recipe<?>> list = level.getRecipeManager().getAllRecipesFor(CompanionsRecipes.SOUL_FURNACE_TYPE);
        for (Recipe<?> rec : list) {
            if (rec instanceof SoulFurnaceRecipe r) {
                if (r.input().test(input) && this.charges >= r.requiredCharges()) return r;
            }
        }

        return null;
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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.charges = tag.getInt("Charges");
        this.currentProgress = tag.getInt("Progress");
        this.processingTime = tag.getInt("ProcessingTime");
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Charges", this.charges);
        tag.putInt("Progress", this.currentProgress);
        tag.putInt("ProcessingTime", this.processingTime);
        ContainerHelper.saveAllItems(tag, this.items);
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
                if (index == 0) return charges;
                if (index == 1) return currentProgress;
                if (index == 2) return processingTime;
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) charges = value;
                else if (index == 1) currentProgress = value;
                else if (index == 2) processingTime = value;
            }

            @Override
            public int getCount() {
                return 3;
            }

        });

    }

    @Override
    public double getTick(Object o) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { }

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
        for (ItemStack stack : this.items) if (!stack.isEmpty()) return false;
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
        if (index == 0 && !stack.isEmpty() && this.currentRecipe == null && this.level != null && !this.level.isClientSide()) {
            SoulFurnaceRecipe found = findRecipe(this.level, stack);
            if (found != null) {
                stack.shrink(1);
                this.currentRecipe = found;
                this.processingTime = found.processTime();
                this.currentProgress = 0;
                this.setChanged();
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

}
