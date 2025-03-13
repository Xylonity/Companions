package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.SoulFurnaceBlock;
import dev.xylonity.companions.common.container.SoulFurnaceContainerMenu;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
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
import net.minecraft.world.level.block.Blocks;
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

public class SoulFurnaceBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider, Container {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private int charge = 0;
    private static final int MAX_CHARGE = 9;
    private int currentProgress = 0;
    private int processingTime = 0;
    private SoulFurnaceRecipe currentRecipe = null;

    public static final List<SoulFurnaceRecipe> RECIPES = List.of(
            new SoulFurnaceRecipe(Items.IRON_INGOT, Items.GOLD_INGOT, 1, 100, null),
            new SoulFurnaceRecipe(Items.GOLD_INGOT, Items.DIAMOND, 3, 300, null),
            new SoulFurnaceRecipe(Items.DIAMOND, CompanionsItems.SOUL_GEM.get(), 1, 160, null),
            new SoulFurnaceRecipe(CompanionsItems.BIG_BREAD.get(), null, 3, 100, CompanionsEntities.LIVING_CANDLE.get())
    );

    public SoulFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.SOUL_FURNACE.get(), pos, state);
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
                Item inputItem = furnace.getItem(0).getItem();
                for (SoulFurnaceRecipe recipe : RECIPES) {
                    if (recipe.input == inputItem && furnace.charge >= recipe.requiredCharges) {
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
        String recipeInput = tag.getString("CurrentRecipeInput");
        if (!recipeInput.isEmpty()) {
            for (SoulFurnaceRecipe recipe : RECIPES) {
                //ResourceLocation rl = recipe.input.getRegistryName();
                //if (rl != null && rl.toString().equals(recipeInput)) {
                //    this.currentRecipe = recipe;
                //    break;
                //}
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
        //tag.putString("CurrentRecipeInput", currentRecipe != null && currentRecipe.input.getRegistryName() != null ? currentRecipe.input.getRegistryName().toString() : "");
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

        if (index == 0 && !stack.isEmpty()) {
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

    public record SoulFurnaceRecipe(Item input, Item output, int requiredCharges, int processTime, EntityType<?> entityType) { ;; }
}
