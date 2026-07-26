package dev.xylonity.companions.common.blackjack;

import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Every seat keeps its own bet container, so several players share one dealer hand while betting and being paid out independently.
 * Most logic derived from the old cornelius screen implementation
 */
public class CorneliusTable {

    public enum Phase { BETTING, DEALING, PLAYING, DEALER, RESULT }

    public enum SeatState { IDLE, PLAYING, STOOD, BUST, BLACKJACK }

    public enum Result { NONE, WIN, LOSE, TIE, BLACKJACK }

    public static final int MAX_SEATS = 4;
    public static final int MAX_CARDS = 12;
    public static final int BET_SLOTS = 3;

    private static final int DEAL_INTERVAL = 6;
    private static final int RESULT_DURATION = 60;
    private static final int TURN_TIMEOUT = 600;
    private static final int DEALER_STANDS_ON = 17;

    public static class Seat {
        public final UUID playerId;
        public final SimpleContainer bet = new SimpleContainer(BET_SLOTS);
        public final List<Integer> cards = new ArrayList<>();
        public SeatState state = SeatState.IDLE;
        public Result result = Result.NONE;
        public boolean inHand = false;
        public boolean present = false;

        private Seat(UUID playerId) {
            this.playerId = playerId;
        }

    }

    public record HandValue(int total, boolean soft) { ;; }

    private final CorneliusEntity cornelius;
    private final List<Seat> seats = new ArrayList<>();
    private final List<Integer> dealerCards = new ArrayList<>();
    private final ArrayDeque<Integer> dealQueue = new ArrayDeque<>();

    private Phase phase = Phase.BETTING;
    private int timer = 0;
    private int turn = -1;
    private int turnTimer = 0;

    public CorneliusTable(CorneliusEntity cornelius) {
        this.cornelius = cornelius;
    }

    public Phase getPhase() {
        return phase;
    }

    public int getTurn() {
        return turn;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public List<Integer> getDealerCards() {
        return dealerCards;
    }

    public boolean isHoleCardHidden() {
        return phase == Phase.DEALING || phase == Phase.PLAYING;
    }

    @Nullable
    public Seat getSeat(UUID playerId) {
        for (final Seat seat : seats) {
            if (seat.playerId.equals(playerId)) {
                return seat;
            }

        }

        return null;
    }

    public int seatIndex(UUID playerId) {
        for (int i = 0; i < seats.size(); i++) {
            if (seats.get(i).playerId.equals(playerId)) {
                return i;
            }

        }

        return -1;
    }

    @Nullable
    public Seat getOrCreateSeat(Player player) {
        final Seat existing = getSeat(player.getUUID());
        if (existing != null) {
            existing.present = true;
            return existing;
        }

        if (seats.size() >= MAX_SEATS) {
            return null;
        }

        final Seat seat = new Seat(player.getUUID());
        seat.present = true;
        seats.add(seat);

        return seat;
    }

    public void leave(Player player) {
        final Seat seat = getSeat(player.getUUID());
        if (seat == null) {
            return;
        }

        seat.present = false;

        if (seat.inHand) {
            if (seat.state == SeatState.PLAYING && turn == seatIndex(seat.playerId)) {
                seat.state = SeatState.STOOD;
                advanceTurn();
            }

            return;
        }

        final boolean gone = !player.isAlive() || (player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected());

        for (int i = 0; i < seat.bet.getContainerSize(); i++) {
            final ItemStack stack = seat.bet.removeItemNoUpdate(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (gone) {
                player.drop(stack, false);
            }
            else {
                player.getInventory().placeItemBackInInventory(stack);
            }

        }

        seats.remove(seat);
    }

    public boolean deal() {
        if (phase != Phase.BETTING) {
            return false;
        }

        boolean anyBet = false;
        for (final Seat seat : seats) {
            seat.cards.clear();
            seat.result = Result.NONE;
            seat.inHand = !seat.bet.isEmpty();
            seat.state = seat.inHand ? SeatState.PLAYING : SeatState.IDLE;
            anyBet |= seat.inHand;
        }

        if (!anyBet) {
            return false;
        }

        dealerCards.clear();
        dealQueue.clear();
        for (int round = 0; round < 2; round++) {
            for (int i = 0; i < seats.size(); i++) {
                if (seats.get(i).inHand) {
                    dealQueue.add(i);
                }

            }

            dealQueue.add(-1);
        }

        phase = Phase.DEALING;
        timer = DEAL_INTERVAL;
        turn = -1;

        return true;
    }

    public void hit(UUID playerId) {
        final Seat seat = currentSeat(playerId);
        if (seat == null) {
            return;
        }

        seat.cards.add(draw());

        final int total = handValue(seat.cards).total();
        if (total > 21) {
            seat.state = SeatState.BUST;
            advanceTurn();
        }
        else if (total == 21 || seat.cards.size() >= MAX_CARDS) {
            seat.state = SeatState.STOOD;
            advanceTurn();
        }
        else {
            turnTimer = 0;
        }

    }

    public void stand(UUID playerId) {
        final Seat seat = currentSeat(playerId);
        if (seat == null) {
            return;
        }

        seat.state = SeatState.STOOD;
        advanceTurn();
    }

    @Nullable
    private Seat currentSeat(UUID playerId) {
        if (phase != Phase.PLAYING || turn < 0 || turn >= seats.size()) {
            return null;
        }

        final Seat seat = seats.get(turn);

        return seat.playerId.equals(playerId) && seat.state == SeatState.PLAYING ? seat : null;
    }

    public void tick() {
        switch (phase) {
            case DEALING -> {
                if (--timer > 0) {
                    return;
                }

                timer = DEAL_INTERVAL;

                if (!dealQueue.isEmpty()) {
                    final int target = dealQueue.poll();
                    if (target < 0) {
                        dealerCards.add(draw());
                    }
                    else {
                        seats.get(target).cards.add(draw());
                    }

                    return;
                }

                finishDeal();
            }
            case PLAYING -> {
                if (turn < 0 || turn >= seats.size()) {
                    advanceTurn();
                    return;
                }

                final Seat seat = seats.get(turn);
                if (seat.state != SeatState.PLAYING || !seat.present || cornelius.level().getPlayerByUUID(seat.playerId) == null) {
                    seat.state = SeatState.STOOD;
                    advanceTurn();
                    return;
                }

                if (++turnTimer >= TURN_TIMEOUT) {
                    seat.state = SeatState.STOOD;
                    advanceTurn();
                }

            }
            case DEALER -> {
                if (--timer > 0) {
                    return;
                }

                timer = DEAL_INTERVAL;

                final HandValue value = handValue(dealerCards);
                if (dealerCards.size() < MAX_CARDS && (value.total() < DEALER_STANDS_ON || (value.total() == DEALER_STANDS_ON && value.soft()))) {
                    dealerCards.add(draw());
                    return;
                }

                settle();
            }
            case RESULT -> {
                if (--timer <= 0) {
                    reset();
                }

            }
            default -> { ;; }
        }

    }

    private void finishDeal() {
        for (final Seat seat : seats) {
            if (seat.inHand && handValue(seat.cards).total() == 21) {
                seat.state = SeatState.BLACKJACK;
            }

        }

        phase = Phase.PLAYING;
        turn = -1;
        advanceTurn();
    }

    private void advanceTurn() {
        turnTimer = 0;

        for (int i = turn + 1; i < seats.size(); i++) {
            final Seat seat = seats.get(i);
            if (seat.inHand && seat.state == SeatState.PLAYING) {
                turn = i;
                return;
            }

        }

        turn = -1;
        phase = Phase.DEALER;
        timer = DEAL_INTERVAL;
    }

    private void settle() {
        final int dealerTotal = handValue(dealerCards).total();
        final boolean dealerBlackjack = dealerCards.size() == 2 && dealerTotal == 21;

        for (final Seat seat : seats) {
            if (!seat.inHand) {
                continue;
            }

            final int total = handValue(seat.cards).total();
            final double multiplier;

            if (seat.state == SeatState.BUST || total > 21) {
                seat.result = Result.LOSE;
                multiplier = 0.0;
            }
            else if (seat.state == SeatState.BLACKJACK) {
                seat.result = dealerBlackjack ? Result.TIE : Result.BLACKJACK;
                multiplier = dealerBlackjack ? 1.0 : 2.5;
            }
            else if (dealerTotal > 21 || total > dealerTotal) {
                seat.result = Result.WIN;
                multiplier = 2.0;
            }
            else if (total == dealerTotal) {
                seat.result = Result.TIE;
                multiplier = 1.0;
            }
            else {
                seat.result = Result.LOSE;
                multiplier = 0.0;
            }

            pay(seat, multiplier);
        }

        phase = Phase.RESULT;
        timer = RESULT_DURATION;
    }

    private void pay(Seat seat, double multiplier) {
        for (int i = 0; i < seat.bet.getContainerSize(); i++) {
            final ItemStack bet = seat.bet.getItem(i);
            if (bet.isEmpty()) {
                continue;
            }

            final int payout = (int) Math.floor(bet.getCount() * multiplier);
            final int kept = Math.min(payout, bet.getMaxStackSize());

            seat.bet.setItem(i, kept > 0 ? new ItemStack(bet.getItem(), kept) : ItemStack.EMPTY);

            if (payout - kept > 0) {
                cornelius.spawnAtLocation(new ItemStack(bet.getItem(), payout - kept));
            }

        }

        seat.bet.setChanged();
    }

    private void reset() {
        phase = Phase.BETTING;
        timer = 0;
        turn = -1;
        turnTimer = 0;
        dealerCards.clear();
        dealQueue.clear();

        for (final Seat seat : seats) {
            seat.cards.clear();
            seat.state = SeatState.IDLE;
            seat.result = Result.NONE;
            seat.inHand = false;
        }

    }

    private int draw() {
        return cornelius.getRandom().nextInt(13) + 1;
    }

    public static HandValue handValue(List<Integer> cards) {
        int total = 0;
        int aces = 0;

        for (final int value : cards) {
            if (value == 1) {
                total += 11;
                aces++;
            }
            else {
                total += Math.min(value, 10);
            }

        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return new HandValue(total, aces > 0);
    }

    public void dropEverything() {
        for (final Seat seat : seats) {
            for (int i = 0; i < seat.bet.getContainerSize(); i++) {
                final ItemStack stack = seat.bet.removeItemNoUpdate(i);
                if (!stack.isEmpty()) {
                    cornelius.spawnAtLocation(stack);
                }

            }

        }

        seats.clear();
        reset();
    }

    public ListTag save(HolderLookup.Provider registries) {
        final ListTag list = new ListTag();

        for (final Seat seat : seats) {
            final CompoundTag tag = new CompoundTag();
            tag.putUUID("Player", seat.playerId);
            tag.put("Bet", seat.bet.createTag(registries));
            list.add(tag);
        }

        return list;
    }

    public void load(ListTag list, HolderLookup.Provider registries) {
        seats.clear();

        for (final Tag raw : list) {
            if (!(raw instanceof CompoundTag tag) || !tag.hasUUID("Player")) {
                continue;
            }

            final Seat seat = new Seat(tag.getUUID("Player"));
            seat.bet.fromTag(tag.getList("Bet", Tag.TAG_COMPOUND), registries);
            seats.add(seat);
        }

        reset();
    }

}
