package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.SoulFurnaceBlock;
import dev.xylonity.companions.common.container.SoulFurnaceContainerMenu;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
    private int charge = 0;
    private static final int MAX_CHARGE = 9;
    private int currentProgress = 0;
    private int processingTime = 0;
    private SoulFurnaceRecipe currentRecipe = null;

    public static final List<SoulFurnaceRecipe> RECIPES = List.of(
            new SoulFurnaceRecipe(Items.CANDLE, null, 1, 100, CompanionsEntities.LIVING_CANDLE.get(), null),
            new SoulFurnaceRecipe(Items.DIAMOND, CompanionsItems.SOUL_GEM.get(), 1, 160, null, null),
            new SoulFurnaceRecipe(CompanionsItems.BIG_BREAD.get(), null, 3, 50, null, CompanionsBlocks.CROISSANT_EGG.get())
    );

    public SoulFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.SOUL_FURNACE.get(), pos, state);
    }

    @Override
    public boolean canPlaceItem(int pIndex, @NotNull ItemStack pStack) {
        return pIndex == 0;
    }

    @Override
    public boolean canTakeItem(Container pTarget, int pIndex, ItemStack pStack) {
        return pIndex == 1;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T F) {
        if (F instanceof SoulFurnaceBlockEntity furnace) {
            if (level.isClientSide()) return;

            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            //if (aboveState.getBlock() instanceof ChaliceBlock && furnace.charge < MAX_CHARGE) {
            //    if (aboveState.getValue(ChaliceBlock.fill) == 4) {
            //        level.setBlock(abovePos, aboveState.setValue(ChaliceBlock.fill, 0), 3);
            //        level.playSound(null, abovePos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1f, 1f);
            //        furnace.charge = Math.min(furnace.charge + 1, MAX_CHARGE);
            //        furnace.setChanged();
            //    }
            //}

            if (aboveState.getBlock() == Blocks.GOLD_BLOCK && furnace.charge < MAX_CHARGE) {
                level.setBlock(abovePos, Blocks.AIR.defaultBlockState(), 3);
                level.playSound(null, abovePos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1f, 1f);
                furnace.charge = Math.min(furnace.charge + 1, MAX_CHARGE);
                furnace.setChanged();
            }

            if (furnace.currentRecipe == null && !furnace.getItem(0).isEmpty()) {
                ItemStack inputStack = furnace.getItem(0);
                for (SoulFurnaceRecipe recipe : RECIPES) {
                    if (recipe.input == inputStack.getItem() && furnace.charge >= recipe.requiredCharges) {
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
                furnace.currentProgress++;
                if (furnace.currentProgress >= furnace.processingTime) {
                    level.playSound(null, abovePos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1f, 1f);
                    SoulFurnaceRecipe recipe = furnace.currentRecipe;

                    if (recipe.output == null || recipe.output == Items.AIR) {
                        if (recipe.entityType != null) {
                            BlockPos spawnPos = pos.relative(state.getValue(SoulFurnaceBlock.FACING));
                            Entity entity = recipe.entityType.create(level);
                            if (entity != null) {
                                entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                                level.addFreshEntity(entity);
                            }
                        } else {
                            if (recipe.block() != null) {
                                BlockPos targetPos = pos.relative(state.getValue(SoulFurnaceBlock.FACING));
                                if (level.isEmptyBlock(targetPos)) {
                                    level.setBlockAndUpdate(targetPos, recipe.block().defaultBlockState().mirror(Mirror.FRONT_BACK));

                                    Random r = new Random();
                                    for (int i = 0; i < 25; i++) {
                                        double dx = (r.nextDouble() - 0.5) * 2.5;
                                        double dy = (r.nextDouble() - 0.5) * 1.5;
                                        double dz = (r.nextDouble() - 0.5) * 2.5;
                                        if (level instanceof ServerLevel serverLevel) {
                                            serverLevel.sendParticles(ParticleTypes.POOF, targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1, dx, dy, dz, 0.04);
                                        }
                                    }

                                } else {
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

                                level.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 1f, 1f);
                            }

                        }
                    } else {
                        if (furnace.getItem(1).isEmpty()) {
                            furnace.setItem(1, new ItemStack(recipe.output));
                        } else if (furnace.getItem(1).getItem() == recipe.output) {
                            furnace.getItem(1).grow(1);
                        }
                    }

                    furnace.charge -= recipe.requiredCharges;
                    furnace.currentRecipe = null;
                    furnace.currentProgress = 0;
                    furnace.processingTime = 0;
                    furnace.setChanged();
                }
            }
        }
    }

    public static boolean isValidInput(ItemStack stack) {
        for (SoulFurnaceRecipe recipe : RECIPES) {
            if (recipe.input == stack.getItem()) return true;
        }

        return false;
    }


    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.charge = tag.getInt("Charge");
        this.currentProgress = tag.getInt("Progress");
        this.processingTime = tag.getInt("ProcessingTime");

        if (tag.contains("CurrentRecipeIndex")) {
            int index = tag.getInt("CurrentRecipeIndex");
            if (index >= 0 && index < RECIPES.size()) {
                this.currentRecipe = RECIPES.get(index);
            }
        }

        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Charge", this.charge);
        tag.putInt("Progress", this.currentProgress);
        tag.putInt("ProcessingTime", this.processingTime);

        if (this.currentRecipe != null) {
            int index = RECIPES.indexOf(this.currentRecipe);
            tag.putInt("CurrentRecipeIndex", index);
        } else {
            tag.putInt("CurrentRecipeIndex", -1);
        }

        ContainerHelper.saveAllItems(tag, this.items);
    }

    public int getCharge() {
        return this.charge;
    }

    public int getProgress() {
        return this.currentProgress;
    }

    public int getProcessingTime() {
        return this.processingTime;
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
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new SoulFurnaceContainerMenu(id, inventory, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> charge;
                    case 1 -> currentProgress;
                    case 2 -> processingTime;
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        charge = value;
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
        return RenderUtils.getCurrentTick();
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
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(this.items, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == 0 && !stack.isEmpty() && this.currentRecipe == null) {
            for (SoulFurnaceRecipe recipe : RECIPES) {
                if (recipe.input == stack.getItem() && this.charge >= recipe.requiredCharges) {
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
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public static int getMaxCharges() {
        return MAX_CHARGE;
    }

    public record SoulFurnaceRecipe(Item input, Item output, int requiredCharges, int processTime, EntityType<?> entityType, Block block) { ;; }
}
