package dev.xylonity.companions.common.blockentity.container;

import dev.xylonity.companions.common.blockentity.SoulFurnaceBlockEntity;
import dev.xylonity.companions.registry.CompanionsMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SoulFurnaceContainerMenu extends AbstractContainerMenu {
    private final Container furnaceInventory;
    private final ContainerData data;
    private final Player player;

    public SoulFurnaceContainerMenu(int id, Inventory playerInventory, Container furnaceInventory, ContainerData data) {
        super(CompanionsMenuTypes.SOUL_FURNACE.get(), id);
        this.furnaceInventory = furnaceInventory;
        this.data = data;
        this.player = playerInventory.player;

        this.addSlot(new Slot(furnaceInventory, 0, 56, 22) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return SoulFurnaceBlockEntity.isValidInput(stack);
            }
        });

        this.addSlot(new Slot(furnaceInventory, 1, 116, 40) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        int startX = 8;
        int startY = 97;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, startX + col * 18, startY + 58));
        }

        addDataSlots(data);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return furnaceInventory.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();
            int containerSlotCount = 2;
            if (index < containerSlotCount) {
                if (!this.moveItemStackTo(stackInSlot, containerSlotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, containerSlotCount, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public int getCharge() {
        return this.data.get(0);
    }

    public int getProgress() {
        return this.data.get(1);
    }

    public int getProcessingTime() {
        return this.data.get(2);
    }

}
