package dev.xylonity.companions.common.container;

import dev.xylonity.companions.common.entity.custom.PuppetEntity;
import dev.xylonity.companions.common.item.PuppetArm;
import dev.xylonity.companions.registry.CompanionsMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PuppetContainerMenu extends AbstractContainerMenu {

    private final PuppetEntity puppet;
    private final Container entityInventory;

    public PuppetContainerMenu(int windowId, Inventory playerInv, PuppetEntity puppet) {
        super(CompanionsMenuTypes.PUPPET_CONTAINER.get(), windowId);
        this.puppet = puppet;
        this.entityInventory = puppet.inventory;

        checkContainerSize(this.entityInventory, 2);
        this.entityInventory.startOpen(playerInv.player);

        this.addSlot(new PartSlot(entityInventory, 0, 32, 80));
        this.addSlot(new PartSlot(entityInventory, 1, 128, 80));

        final int PLAYER_INVENTORY_START_Y = 129;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, PLAYER_INVENTORY_START_Y + row * 18));
            }
        }

        final int HOTBAR_START_Y = PLAYER_INVENTORY_START_Y + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, HOTBAR_START_Y));
        }

    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.entityInventory.stillValid(player) && this.puppet.isAlive() && this.puppet.distanceTo(player) < 8.0F;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.entityInventory.stopOpen(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            int containerSize = this.entityInventory.getContainerSize();
            if (index < containerSize) {
                if (!this.moveItemStackTo(stackInSlot, containerSize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, containerSize, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public static class PartSlot extends Slot {
        public PartSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.getItem() instanceof PuppetArm;
        }

    }

}