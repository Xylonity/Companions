package dev.xylonity.companions.common.container;

import dev.xylonity.companions.common.blackjack.CorneliusTable;
import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import dev.xylonity.companions.common.item.blockitem.CoinItem;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsMenuTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CorneliusContainerMenu extends AbstractContainerMenu {

    public static final int BUTTON_DEAL = 0;
    public static final int BUTTON_HIT = 1;
    public static final int BUTTON_STAND = 2;

    private static final int MAIN_SLOTS = 3;
    private static final int CONTAINER_SLOTS = MAIN_SLOTS + CorneliusTable.BET_SLOTS;

    public static final int IDX_PHASE = 0;
    public static final int IDX_TURN = 1;
    public static final int IDX_MY_SEAT = 2;
    public static final int IDX_SEAT_COUNT = 3;
    public static final int IDX_MY_RESULT = 4;
    public static final int IDX_MY_STATE = 5;
    public static final int IDX_DEALER_COUNT = 6;
    public static final int IDX_DEALER_CARDS = 7;
    public static final int IDX_MY_CARDS = IDX_DEALER_CARDS + CorneliusTable.MAX_CARDS;
    public static final int IDX_SEATS = IDX_MY_CARDS + CorneliusTable.MAX_CARDS;
    public static final int SEAT_STRIDE = 4;
    private static final int DATA_SIZE = IDX_SEATS + CorneliusTable.MAX_SEATS * SEAT_STRIDE;

    private static final List<Object> ALLOWED_BET_KEYS = new ArrayList<>();
    private static String cachedBets = null;

    private final CorneliusEntity cornelius;
    private final Container entityInventory;
    private final ContainerData data = new SimpleContainerData(DATA_SIZE);

    private final CorneliusTable table;
    private final CorneliusTable.Seat seat;
    private final Player viewer;

    public CorneliusContainerMenu(int windowId, Inventory playerInv, CorneliusEntity cornelius) {
        super(CompanionsMenuTypes.CORNELIUS_MENU.get(), windowId);
        this.cornelius = cornelius;
        this.viewer = playerInv.player;
        this.entityInventory = cornelius.inventory;

        this.table = cornelius.level().isClientSide ? null : cornelius.getTable();
        this.seat = this.table != null ? this.table.getOrCreateSeat(playerInv.player) : null;

        final Container betContainer = this.seat != null ? this.seat.bet : new SimpleContainer(CorneliusTable.BET_SLOTS);

        checkContainerSize(this.entityInventory, MAIN_SLOTS);
        this.entityInventory.startOpen(playerInv.player);

        this.addSlot(new MainCoinSlot(entityInventory, 0, 24, 118));
        this.addSlot(new MainCoinSlot(entityInventory, 1, 42, 118));
        this.addSlot(new MainCoinSlot(entityInventory, 2, 60, 118));

        this.addSlot(new BetSlot(betContainer, 0, 78, 7));
        this.addSlot(new BetSlot(betContainer, 1, 96, 7));
        this.addSlot(new BetSlot(betContainer, 2, 114, 7));

        this.addDataSlots(this.data);

        final int PLAYER_INVENTORY_START_Y = 144;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 24 + col * 18, PLAYER_INVENTORY_START_Y + row * 18));
            }
        }

        final int HOTBAR_START_Y = PLAYER_INVENTORY_START_Y + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 24 + col * 18, HOTBAR_START_Y));
        }

    }

    @Override
    public void broadcastChanges() {
        if (this.table != null) {
            syncTable();
        }

        super.broadcastChanges();
    }

    private void syncTable() {
        this.data.set(IDX_PHASE, this.table.getPhase().ordinal());
        this.data.set(IDX_TURN, this.table.getTurn());
        this.data.set(IDX_MY_SEAT, this.seat == null ? -1 : this.table.seatIndex(this.seat.playerId));
        this.data.set(IDX_SEAT_COUNT, this.table.getSeats().size());
        this.data.set(IDX_MY_RESULT, this.seat == null ? 0 : this.seat.result.ordinal());
        this.data.set(IDX_MY_STATE, this.seat == null ? 0 : this.seat.state.ordinal());

        final List<Integer> dealer = this.table.getDealerCards();
        this.data.set(IDX_DEALER_COUNT, dealer.size());
        for (int i = 0; i < CorneliusTable.MAX_CARDS; i++) {
            final boolean hidden = i == 0 && this.table.isHoleCardHidden();
            this.data.set(IDX_DEALER_CARDS + i, i < dealer.size() && !hidden ? dealer.get(i) : 0);
        }

        final List<Integer> mine = this.seat == null ? List.of() : this.seat.cards;
        for (int i = 0; i < CorneliusTable.MAX_CARDS; i++) {
            this.data.set(IDX_MY_CARDS + i, i < mine.size() ? mine.get(i) : 0);
        }

        final List<CorneliusTable.Seat> seats = this.table.getSeats();
        for (int i = 0; i < CorneliusTable.MAX_SEATS; i++) {
            final int offset = IDX_SEATS + i * SEAT_STRIDE;
            if (i >= seats.size()) {
                this.data.set(offset, 0);
                this.data.set(offset + 1, 0);
                this.data.set(offset + 2, 0);
                this.data.set(offset + 3, 0);
                continue;
            }

            final CorneliusTable.Seat other = seats.get(i);
            final Player player = this.cornelius.level().getPlayerByUUID(other.playerId);
            final int entityId = player == null ? -1 : player.getId();

            this.data.set(offset, (entityId >> 16) & 0xFFFF);
            this.data.set(offset + 1, entityId & 0xFFFF);
            this.data.set(offset + 2, CorneliusTable.handValue(other.cards).total());
            this.data.set(offset + 3, other.state.ordinal());
        }

    }

    public CorneliusTable.Phase phase() {
        final int ordinal = this.data.get(IDX_PHASE);
        final CorneliusTable.Phase[] values = CorneliusTable.Phase.values();

        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : CorneliusTable.Phase.BETTING;
    }

    public int turn() {
        return this.data.get(IDX_TURN);
    }

    public int mySeat() {
        return this.data.get(IDX_MY_SEAT);
    }

    public int seatCount() {
        return Math.min(this.data.get(IDX_SEAT_COUNT), CorneliusTable.MAX_SEATS);
    }

    public CorneliusTable.Result myResult() {
        return CorneliusTable.Result.values()[Math.floorMod(this.data.get(IDX_MY_RESULT), CorneliusTable.Result.values().length)];
    }

    public CorneliusTable.SeatState myState() {
        return CorneliusTable.SeatState.values()[Math.floorMod(this.data.get(IDX_MY_STATE), CorneliusTable.SeatState.values().length)];
    }

    public CorneliusTable.SeatState seatState(int index) {
        return CorneliusTable.SeatState.values()[Math.floorMod(this.data.get(IDX_SEATS + index * SEAT_STRIDE + 3), CorneliusTable.SeatState.values().length)];
    }

    public int seatTotal(int index) {
        return this.data.get(IDX_SEATS + index * SEAT_STRIDE + 2);
    }

    public int seatPlayerId(int index) {
        final int offset = IDX_SEATS + index * SEAT_STRIDE;

        return ((this.data.get(offset) & 0xFFFF) << 16) | (this.data.get(offset + 1) & 0xFFFF);
    }

    public List<Integer> dealerCards() {
        final List<Integer> cards = new ArrayList<>();
        for (int i = 0; i < this.data.get(IDX_DEALER_COUNT) && i < CorneliusTable.MAX_CARDS; i++) {
            cards.add(this.data.get(IDX_DEALER_CARDS + i));
        }

        return cards;
    }

    public List<Integer> myCards() {
        final List<Integer> cards = new ArrayList<>();
        for (int i = 0; i < CorneliusTable.MAX_CARDS; i++) {
            final int value = this.data.get(IDX_MY_CARDS + i);
            if (value <= 0) {
                break;
            }

            cards.add(value);
        }

        return cards;
    }

    public boolean hasSeat() {
        return this.table != null ? this.seat != null : mySeat() >= 0;
    }

    public boolean betsLocked() {
        return phaseOf() != CorneliusTable.Phase.BETTING;
    }

    private CorneliusTable.Phase phaseOf() {
        return this.table != null ? this.table.getPhase() : phase();
    }

    public boolean hasBet() {
        for (int i = MAIN_SLOTS; i < CONTAINER_SLOTS; i++) {
            if (!this.slots.get(i).getItem().isEmpty()) {
                return true;
            }

        }

        return false;
    }

    private boolean isOwner(Player player) {
        return this.cornelius.getOwner() == player;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.entityInventory.stillValid(player) && this.cornelius.isAlive() && this.cornelius.distanceTo(player) < 8.0F;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.entityInventory.stopOpen(player);

        if (this.table != null) {
            this.table.leave(player);
        }

    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < CONTAINER_SLOTS) {
                if (!this.moveItemStackTo(stackInSlot, CONTAINER_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }

            }
            else {
                if (!this.moveItemStackTo(stackInSlot, 0, CONTAINER_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }

            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }

        }

        return itemstack;
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int buttonId) {
        if (this.table == null || this.seat == null) {
            return false;
        }

        switch (buttonId) {
            case BUTTON_DEAL -> {
                this.table.deal();
                this.broadcastChanges();
                return true;
            }
            case BUTTON_HIT -> {
                this.table.hit(player.getUUID());
                this.broadcastChanges();
                return true;
            }
            case BUTTON_STAND -> {
                this.table.stand(player.getUUID());
                this.broadcastChanges();
                return true;
            }
            default -> {
                return super.clickMenuButton(player, buttonId);
            }

        }

    }

    public class MainCoinSlot extends Slot {
        public MainCoinSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.getItem() instanceof CoinItem && CorneliusContainerMenu.this.isOwner(CorneliusContainerMenu.this.viewer);
        }

        @Override
        public boolean mayPickup(@NotNull Player player) {
            return CorneliusContainerMenu.this.isOwner(player);
        }

    }

    public class BetSlot extends Slot {
        public BetSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return isAllowedBet(stack) && CorneliusContainerMenu.this.hasSeat() && !CorneliusContainerMenu.this.betsLocked();
        }

        @Override
        public boolean mayPickup(@NotNull Player player) {
            return !CorneliusContainerMenu.this.betsLocked();
        }

        @Override
        public boolean allowModification(@NotNull Player pPlayer) {
            return !CorneliusContainerMenu.this.betsLocked() && super.allowModification(pPlayer);
        }

    }

    private static boolean isAllowedBet(ItemStack stack) {
        if (stack.isEmpty()) return false;

        parsePossibleBets();

        for (Object object : ALLOWED_BET_KEYS) {
            if (object instanceof Item item) {
                if (stack.is(item)) {
                    return true;
                }

            }
            else if (object instanceof TagKey<?> anyTag) {
                if (stack.is((TagKey<Item>) anyTag)) {
                    return true;
                }

            }

        }

        return false;
    }

    private static void parsePossibleBets() {
        String raw = CompanionsConfig.CORNELIUS_JACKBLACK_BETS;

        if (raw == null) raw = "";
        if (raw.equals(cachedBets)) return;

        ALLOWED_BET_KEYS.clear();
        cachedBets = raw;

        String[] parts = raw.split(";");
        for (String part : parts) {
            String string = part.trim();
            if (string.isEmpty()) continue;

            boolean isTag = string.startsWith("#");
            ResourceLocation resourceLocation = ResourceLocation.tryParse(isTag ? string.substring(1).trim() : string);
            if (resourceLocation == null) continue;

            if (isTag) {
                TagKey<Item> tag = TagKey.create(Registries.ITEM, resourceLocation);
                ALLOWED_BET_KEYS.add(tag);
            }
            else {
                BuiltInRegistries.ITEM.getOptional(resourceLocation).ifPresent(ALLOWED_BET_KEYS::add);
            }

        }

    }

}