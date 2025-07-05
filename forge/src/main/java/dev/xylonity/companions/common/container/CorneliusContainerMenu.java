package dev.xylonity.companions.common.container;

import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import dev.xylonity.companions.common.item.blockitem.CoinItem;
import dev.xylonity.companions.registry.CompanionsMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CorneliusContainerMenu extends AbstractContainerMenu {

    private final CorneliusEntity cornelius;
    private final Container entityInventory;
    private boolean gameActive = false;

    public static final int BUTTON_START_GAME = 0;
    public static final int BUTTON_STOP_GAME  = 1;
    public static final int ACTION_LOSE = 2;
    public static final int ACTION_TIE = 3;
    public static final int ACTION_WIN = 4;
    public static final int ACTION_JACK = 5;

    public CorneliusContainerMenu(int windowId, Inventory playerInv, CorneliusEntity cornelius) {
        super(CompanionsMenuTypes.CORNELIUS_CONTAINER.get(), windowId);
        this.cornelius = cornelius;
        this.entityInventory = cornelius.inventory;

        checkContainerSize(this.entityInventory, 2);
        this.entityInventory.startOpen(playerInv.player);

        this.addSlot(new MainCoinSlot(entityInventory, 0, 8, 118));
        this.addSlot(new MainCoinSlot(entityInventory, 1, 26, 118));
        this.addSlot(new MainCoinSlot(entityInventory, 2, 44, 118));

        this.addSlot(new CrupierCoinSlot(entityInventory, 3, 62, 7));
        this.addSlot(new CrupierCoinSlot(entityInventory, 4, 80, 7));
        this.addSlot(new CrupierCoinSlot(entityInventory, 5, 98, 7));

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return gameActive ? 1 : 0;
            }

            @Override
            public void set(int value) {
                gameActive = (value != 0);
            }

        });

        final int PLAYER_INVENTORY_START_Y = 144;
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
        return this.entityInventory.stillValid(player) && this.cornelius.isAlive() && this.cornelius.distanceTo(player) < 8.0F;
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

    @Override
    public boolean clickMenuButton(@NotNull Player player, int buttonId) {

        if (buttonId == BUTTON_START_GAME) {
            this.gameActive = true;
            this.broadcastChanges();
            return true;
        }

        if (buttonId == BUTTON_STOP_GAME) {
            this.gameActive = false;
            this.broadcastChanges();
            return true;
        }

        if (buttonId >= ACTION_LOSE && buttonId <= ACTION_JACK) {
            double multipl;
            switch (buttonId) {
                case ACTION_JACK -> multipl = 2.5;
                case ACTION_WIN -> multipl = 2.0;
                case ACTION_TIE -> multipl = 1.0;
                default // LOSE
                        -> multipl = 0.0;
            }

            pay(multipl, player);
            this.gameActive = false;
            this.broadcastChanges();
            return true;
        }

        return super.clickMenuButton(player, buttonId);
    }

    private void pay(double multiplier, Player player) {
        for (int i = 3; i <= 5; i++) {
            ItemStack bet = this.entityInventory.getItem(i);

            if (bet.isEmpty()) continue;

            int payoutTotal = (int) Math.floor(bet.getCount() * multiplier);

            int toSlot = Math.min(payoutTotal, bet.getMaxStackSize());
            int overflow = payoutTotal - toSlot;

            this.entityInventory.setItem(i, new ItemStack(bet.getItem(), toSlot));
            if (overflow > 0) {
                ItemEntity drop = new ItemEntity(player.level(), player.getX(), player.getY() + 1.0, player.getZ(), new ItemStack(bet.getItem(), overflow));
                player.level().addFreshEntity(drop);
            }
        }

        this.entityInventory.setChanged();
    }

    public static class MainCoinSlot extends Slot {
        public MainCoinSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.getItem() instanceof CoinItem;
        }

    }

    public class CrupierCoinSlot extends Slot {
        public CrupierCoinSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.getItem() instanceof CoinItem && !CorneliusContainerMenu.this.gameActive;
        }

        @Override
        public boolean mayPickup(@NotNull Player player) {
            return !CorneliusContainerMenu.this.gameActive;
        }

        @Override
        public boolean allowModification(@NotNull Player pPlayer) {
            return !CorneliusContainerMenu.this.gameActive && super.allowModification(pPlayer);
        }
    }

}