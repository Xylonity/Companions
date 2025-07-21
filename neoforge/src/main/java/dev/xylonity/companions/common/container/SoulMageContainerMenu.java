package dev.xylonity.companions.common.container;

import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.registry.CompanionsMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SoulMageContainerMenu extends AbstractContainerMenu {

    private final SoulMageEntity soulMage;
    private final Container entityInventory;

    public SoulMageContainerMenu(int windowId, Inventory playerInv, SoulMageEntity mage) {
        super(CompanionsMenuTypes.SOUL_MAGE_CONTAINER.get(), windowId);
        this.soulMage = mage;
        this.entityInventory = mage.inventory;

        checkContainerSize(this.entityInventory, 3);
        this.entityInventory.startOpen(playerInv.player);

        this.addSlot(new BookSlot(entityInventory, 0, 57, 32));
        this.addSlot(new BookSlot(entityInventory, 1, 111, 32));
        this.addSlot(new BookSlot(entityInventory, 2, 165, 32));

        final int PLAYER_INVENTORY_START_Y = 92;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 39 + col * 18, PLAYER_INVENTORY_START_Y + row * 18));
            }
        }

        final int HOTBAR_START_Y = PLAYER_INVENTORY_START_Y + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 39 + col * 18, HOTBAR_START_Y));
        }

    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.entityInventory.stillValid(player) && this.soulMage.isAlive() && this.soulMage.distanceTo(player) < 8.0F;
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

    public static class BookSlot extends Slot {
        public BookSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.getItem() instanceof AbstractMagicBook;
        }

    }

}