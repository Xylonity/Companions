package dev.xylonity.companions.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.container.CorneliusContainerMenu;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class CorneliusScreen extends AbstractContainerScreen<CorneliusContainerMenu> {

    private static final ResourceLocation TEX_TOP = new ResourceLocation(Companions.MOD_ID, "textures/gui/cornelius_gui_top.png");
    private static final ResourceLocation TEX_BOTTOM = new ResourceLocation(Companions.MOD_ID, "textures/gui/cornelius_gui_bottom.png");

    private static final int ANIM_TICKS = 8;
    private static final int DEAL_INTERVAL_TICKS = 6;

    private int dealDelay = 0;

    private final ArrayDeque<Boolean> dealQueue = new ArrayDeque<>();
    private int resultTicks = 0;

    private final List<FrogCard> playerCards = new ArrayList<>();
    private final List<FrogCard> dealerCards = new ArrayList<>();

    private FrogCard hoveredCard = null;

    private static final float LERP_DELTA = 0.235f;
    private static final int END_GAME_TIME = 30;
    private static final int CARD_W = 24;
    private static final int CARD_H = 36;
    private static final int V_BACK = 144;
    private static final int PLAYER_Y = 14;
    private static final int DEALER_Y = -40;
    private static final int START_Y = -50;
    private static final int SPACING_PLAYER = -10;
    private static final int SPACING_DEALER = -14;
    private static final int MARGIN_PLAYER = 77 - (CARD_W + SPACING_PLAYER);
    private static final int MARGIN_DEALER = 58;

    // hover card tilt data
    private static final float HOVER_SCALE = 1.25f;
    private static final float HOVER_W = CARD_W * HOVER_SCALE;
    private static final float HOVER_H = CARD_H * HOVER_SCALE;

    private Button btnHit;
    private Button btnStand;

    private boolean hitButtonPressed = false;
    private boolean standButtonPressed = false;

    private Phase phase = Phase.WAITING_PLAY;
    private Result result;
    private Result pendingResult = null;

    public CorneliusScreen(CorneliusContainerMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    private int generalMarginLeft(){
        return (this.width - this.imageWidth) / 2;
    }

    private int generalMarginTop(){
        return (this.height - this.imageHeight) / 2;
    }

    private FrogCard newCard2Player() {
        return new FrogCard(
                (width - CARD_W) / 2f, START_Y,
                generalMarginLeft() + MARGIN_PLAYER + playerCards.size() * (CARD_W + SPACING_PLAYER),
                generalMarginTop() + PLAYER_Y + 17,
                new Random().nextInt(13) + 1,
                true
        );
    }

    private FrogCard newCard2Crupier() {
        int idx = dealerCards.size();
        float targetX = generalMarginLeft() + MARGIN_DEALER + idx * (CARD_W + SPACING_DEALER);
        if (idx >= 1) targetX += 10;
        return new FrogCard(
                (width - CARD_W) / 2f,
                START_Y,
                targetX,
                generalMarginTop() + DEALER_Y,
                new Random().nextInt(13)+1,
                idx != 0
        );
    }

    @Override
    protected void init(){
        super.init();

        btnHit = addRenderableWidget(new ImageButton(
                        generalMarginLeft() + 80,
                        generalMarginTop() + PLAYER_Y + 100,
                        43, 29, 0, 227, TEX_TOP, b->
        {
            int c = 0;
            for (int i = 3; i <= 5; i++) {
                if (menu.slots.get(i).getItem().isEmpty()) {
                    c++;
                }
            }

            if (c == 3) return;

            if (phase == Phase.WAITING_PLAY) {
                startGame();
            } else if (phase == Phase.PLAYER_TURN) {
                onHit();
            }
        })
        {

            @Override
            public void onPress(){
                super.onPress();
                if (phase != Phase.WAITING_PLAY) hitButtonPressed = true;
            }

            @Override
            public void renderWidget(@NotNull GuiGraphics g, int mx, int my, float pt) {
                if (hitButtonPressed) {
                    g.blit(TEX_TOP, getX(), getY(), 0, 227, 43, 29);
                }
            }

        });

        btnStand = addRenderableWidget(new ImageButton(
                generalMarginLeft() + 119,
                generalMarginTop() + PLAYER_Y + 100,
                43,29,0,227, TEX_TOP, b-> onStand())
        {

            @Override
            public void onPress() {
                super.onPress();
                if (phase != Phase.WAITING_PLAY) standButtonPressed = true;
            }

            @Override
            public void renderWidget(@NotNull GuiGraphics g, int mx, int my, float pt){
                if (standButtonPressed) {
                    g.blit(TEX_TOP, this.getX(), this.getY(), 211, 227, 46, 29);
                }
            }

        });

    }

    private HandValue fullHandValue(List<FrogCard> cards) {
        int total = 0;
        int aces = 0;
        for (FrogCard card : cards) {
            int v = card.value;
            if (v == 1) {
                total += 11; aces++;
            } else {
                total += Math.min(v, 10);
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10; aces--;
        }

        return new HandValue(total, aces > 0);
    }

    @Override
    public void resize(@NotNull Minecraft mc, int width, int height) {
        int oldLeft = generalMarginLeft();
        int oldTop = generalMarginTop();

        super.resize(mc, width, height);

        shiftCards(playerCards, generalMarginLeft() - oldLeft, generalMarginTop() - oldTop);
        shiftCards(dealerCards, generalMarginLeft() - oldLeft, generalMarginTop() - oldTop);
    }

    private static void shiftCards(List<FrogCard> list, int dx, int dy) {
        for (FrogCard card : list) {
            card.x += dx;
            card.prevX += dx;
            card.targetX += dx;
            card.y += dy;
            card.prevY += dy;
            card.targetY += dy;
        }

    }

    private void startGame(){
        phase = Phase.DEALING;
        playerCards.clear();
        dealerCards.clear();
        dealQueue.clear();
        dealQueue.addAll(List.of(true,false,true,false)); // player - dealer - player - dealer
        dealDelay = 0;

        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode != null)
            mc.gameMode.handleInventoryButtonClick(this.menu.containerId, CorneliusContainerMenu.BUTTON_START_GAME);

    }
    private void onHit() {
        if (phase != Phase.PLAYER_TURN) return;

        dealQueue.add(true);
        btnHit.active = false;
        btnStand.active = false;
        phase = Phase.DEALING;
        dealDelay = 0;
    }

    private void onStand(){
        if (phase!=Phase.PLAYER_TURN) return;

        if (!dealerCards.isEmpty()) {
            dealerCards.get(0).startFlip();
        }

        phase = Phase.DEALER_TURN;
        btnHit.visible = false;
        btnStand.visible = true;
        dealDelay = DEAL_INTERVAL_TICKS;
        standButtonPressed = false;
    }

    @Override
    protected void containerTick() {

        super.containerTick();

        switch(phase) {
            case DEALING -> {
                tickCards();

                if (dealDelay>0) {
                    dealDelay--;
                }

                if (dealDelay==0 && !dealQueue.isEmpty()) {
                    spawnNextCard(dealQueue.poll());
                    dealDelay = DEAL_INTERVAL_TICKS;
                }

                if (dealQueue.isEmpty() && allArrived()) {
                    finishDealing();
                }
            }
            case PLAYER_TURN -> {
                if (handValue(playerCards).total == 21) {
                    showResult(Result.BLACKJACK);
                }

                if (handValue(playerCards).total > 21) {
                    showResult(Result.LOSE);
                }

                if (handValue(dealerCards).total > 21) {
                    showResult(Result.WIN);
                }

                if (handValue(dealerCards).total == 21) {
                    showResult(Result.LOSE);
                }
            }
            case DEALER_TURN -> {
                tickCards();

                if (dealDelay > 0) {
                    dealDelay--;
                    break;
                }

                if (fullHandValue(dealerCards).total < 17 || (fullHandValue(dealerCards).total == 17 && fullHandValue(dealerCards).soft)) {
                    spawnNextCard(false);
                    dealDelay = DEAL_INTERVAL_TICKS;
                } else {
                    if (allArrived() && noCardIsFlipping()) {
                        evaluateGame();
                    }
                }

            }
            case SHOW_RESULT -> {
                if (++resultTicks >= END_GAME_TIME) {
                    resetToWaitingPlay();
                }

            }
            default -> { ;; }
        }

        if (pendingResult != null && allArrived() && noCardIsFlipping()) {
            showResult(pendingResult);
            pendingResult = null;
        }

    }

    private boolean noCardIsFlipping() {
        return Stream.concat(playerCards.stream(), dealerCards.stream()).noneMatch(card -> card.flipping);
    }

    private boolean tickCards() {
        boolean all = true;
        for (FrogCard card : playerCards) {
            if (!card.tick(LERP_DELTA)) {
                all = false;
            }
        }

        for (FrogCard card : dealerCards) {
            if (!card.tick(LERP_DELTA)) {
                all = false;
            }
        }

        return all;
    }

    private void spawnNextCard(boolean toPlayer) {
        if (toPlayer) {
            playerCards.add(newCard2Player());
        } else {
            dealerCards.add(newCard2Crupier());
        }

        if (this.minecraft != null)
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(CompanionsSounds.FLIP_CARD.get(),1.0F));
    }

    private boolean allArrived() {
        for (FrogCard card : playerCards) {
            if (!card.arrived) {
                return false;
            }
        }

        for (FrogCard card : dealerCards) {
            if (!card.arrived) {
                return false;
            }
        }

        return true;
    }

    private void finishDealing() {
        if (phase != Phase.DEALING) return;

        if (playerCards.size() == 2 && handValue(playerCards).total == 21){
            showResult(Result.BLACKJACK);
            return;
        }

        if (dealerCards.size() == 2 && handValue(dealerCards).total == 21 ){
            showResult(Result.LOSE);
            return;
        }

        phase = Phase.PLAYER_TURN;
        btnHit.visible = btnStand.visible = true;
        btnHit.active = btnStand.active = true;
        hitButtonPressed = standButtonPressed = false;
    }

    private void evaluateGame() {
        int player = handValue(playerCards).total;
        int frog = handValue(dealerCards).total;
        if (player > 21) {
            pendingResult = Result.LOSE;
        } else if (frog > 21) {
            pendingResult = Result.WIN;
        } else if (player == frog) {
            pendingResult = Result.TIE;
        } else if (player > frog) {
            pendingResult = Result.WIN;
        } else {
            pendingResult = Result.LOSE;
        }

    }

    private void showResult(Result result){
        this.result = result;
        phase = Phase.SHOW_RESULT;
        resultTicks = 0;
        btnHit.visible = btnStand.visible = false;

        int action;
        switch (result) {
            case WIN -> action = CorneliusContainerMenu.ACTION_WIN;
            case BLACKJACK -> action = CorneliusContainerMenu.ACTION_JACK;
            case TIE -> action = CorneliusContainerMenu.ACTION_TIE;
            default -> // LOSE
                    action = CorneliusContainerMenu.ACTION_LOSE;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode != null) {
            mc.gameMode.handleInventoryButtonClick(this.menu.containerId, action);
        }

    }

    private void resetToWaitingPlay(){
        phase = Phase.WAITING_PLAY;
        playerCards.clear();
        dealerCards.clear();
        dealQueue.clear();
        dealDelay = 0;

        btnHit.visible = true;
        btnHit.active = true;
        btnStand.visible = false;

        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode != null) {
            mc.gameMode.handleInventoryButtonClick(this.menu.containerId, CorneliusContainerMenu.BUTTON_STOP_GAME);
        }

    }

    private void renderResultOverlay(GuiGraphics g) {
        if (phase != Phase.SHOW_RESULT) return;

        int u;
        int v;
        int w;
        int h;
        switch (result) {
            case WIN -> {
                u = 41;
                v = 83;
                w = 69;
                h = 44;
            }
            case TIE -> {
                u = 40;
                v = 135;
                w = 55;
                h = 29;
            }
            case BLACKJACK -> {
                u = 113;
                v = 136;
                w = 91;
                h = 61;
            }
            default // LOSE
                    -> {
                u = 123;
                v = 83;
                w = 78;
                h = 42;
            }
        }

        float bScale;
        if (resultTicks < ANIM_TICKS) {
            bScale = (float) resultTicks / ANIM_TICKS;
        } else if (resultTicks > END_GAME_TIME - ANIM_TICKS) {
            bScale = (float)(END_GAME_TIME - resultTicks) / ANIM_TICKS;
        } else {
            bScale = 1f;
        }

        bScale = Mth.clamp(bScale, 0f, 1f);
        float scale = Util.smoothStep(bScale);

        g.pose().pushPose();

        g.pose().translate((float) this.width / 2, (float) this.height / 2 - 70, 0);
        g.pose().scale(scale * 1.25f, scale * 1.25f, 1f);
        g.pose().translate(-w/2f, -h/2f, 0);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEX_TOP);

        g.blit(TEX_TOP, 0, 0, u, v, w, h);

        g.pose().popPose();

    }

    private void updateHoveredCard(int mx, int my, float pt) {
        if (hoveredCard != null) {
            float cx = Mth.lerp(pt, hoveredCard.prevX, hoveredCard.x) + CARD_W / 2f;
            float cy = Mth.lerp(pt, hoveredCard.prevY, hoveredCard.y) + CARD_H / 2f;
            if (mx >= cx - HOVER_W / 2f && mx < cx + HOVER_W / 2f && my >= cy - HOVER_H / 2f && my < cy + HOVER_H / 2f) {
                return;
            }

            hoveredCard = null;
        }

        for (int i = playerCards.size() - 1; i >= 0; i--) {
            FrogCard card = playerCards.get(i);

            if (!card.faceUp) continue;

            float dx = Mth.lerp(pt, card.prevX, card.x);
            float dy = Mth.lerp(pt, card.prevY, card.y);

            if (mx >= dx && mx < dx + CARD_W && my >= dy && my < dy + CARD_H) {
                hoveredCard = card;
                break;
            }

        }

    }

    private void renderCards(GuiGraphics g, float pt, int mx, int my) {
        for (FrogCard card : dealerCards) {
            renderCard(g, card, pt);
        }

        if (hoveredCard == null) {
            for (FrogCard card : playerCards) {
                renderCard(g, card, pt);
            }

        } else {
            int idx = playerCards.indexOf(hoveredCard);

            for (int i = 0; i < idx; i++) {
                renderCard(g, playerCards.get(i), pt);
            }

            renderHoveredCard(g, pt, mx, my);

            for (int i = idx + 1; i < playerCards.size(); i++) {
                renderCard(g, playerCards.get(i), pt);
            }
        }

    }

    private void renderHoveredCard(GuiGraphics g, float pt, int mx, int my) {
        if (hoveredCard == null) return;

        float dx = Mth.lerp(pt, hoveredCard.prevX, hoveredCard.x);
        float dy = Mth.lerp(pt, hoveredCard.prevY, hoveredCard.y);
        float centerX = dx + CARD_W / 2f;
        float centerY = dy + CARD_H / 2f;

        float tilt = 25f;
        float tiltY = (mx - centerX) / (CARD_W / 2f) * tilt;
        float tiltX = -(my - centerY) / (CARD_H / 2f) * tilt;

        g.pose().pushPose();

        g.pose().translate(centerX, centerY, 10);
        g.pose().scale(1.25f, 1.25f, 1f);
        g.pose().translate(-centerX, -centerY, 0);

        renderCardAt(g, hoveredCard, (int) dx, (int) dy, tiltX, tiltY);

        g.pose().popPose();
    }

    @Override
    protected void renderBg(GuiGraphics g,float pt,int mx,int my){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0,TEX_TOP);
        RenderSystem.setShaderTexture(1,TEX_BOTTOM);

        // top board
        g.blit(TEX_TOP,(this.width - 207) / 2, generalMarginTop() - 50,45,44,208,20);
        // bottom board
        g.blit(TEX_BOTTOM,(this.width - 207) / 2,generalMarginTop() - 30,45,0,208,256);
    }

    private void renderCard(GuiGraphics g, FrogCard c, float pt) {
        renderCardAt(g, c, (int) Mth.lerp(pt, c.prevX, c.x), (int) Mth.lerp(pt, c.prevY, c.y), 0f, 0f);
    }

    private void renderCardAt(GuiGraphics g, FrogCard card, int dx, int dy, float tiltX, float tiltY) {
        RenderSystem.disableCull();

        g.pose().pushPose();

        g.pose().translate(dx + CARD_W / 2f, dy + CARD_H / 2f, 40);
        g.pose().mulPose(Axis.XP.rotationDegrees(tiltX));
        g.pose().mulPose(Axis.YP.rotationDegrees(tiltY));
        g.pose().mulPose(Axis.YP.rotationDegrees(card.getFlipAngle()));
        g.pose().translate(-(dx + CARD_W / 2f), -(dy + CARD_H / 2f), 0);

        if (card.faceUp) {
            int u = (card.value <= 10) ? (card.value - 1) * CARD_W : 0;
            int v = (card.value <= 10) ? 0 : (card.value - 10) * CARD_H;
            g.blit(TEX_TOP, dx, dy, u, v, CARD_W, CARD_H);
        } else {
            g.blit(TEX_TOP, dx, dy, 0, V_BACK, CARD_W, CARD_H);
        }

        g.pose().popPose();

        RenderSystem.enableCull();
    }

    @Override
    public void render(@NotNull GuiGraphics g,int mx,int my,float pt){
        updateHoveredCard(mx, my, pt);

        renderBackground(g);

        super.render(g, mx, my, pt);

        // hotfix for the cards not overlaping correctly
        RenderSystem.disableDepthTest();

        renderCards(g, pt, mx, my);
        renderHoveredCard(g, pt, mx, my);

        renderResultOverlay(g);

        RenderSystem.enableDepthTest();

        renderTooltip(g, mx, my);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics g,int mx,int my){
        // player
        if (handValue(playerCards).total != 0) {
            g.drawString(font, String.valueOf(handValue(playerCards).total),46, 57,0xC3B64D);
        }

        // crupier
        if (handValue(dealerCards).total != 0) {
            g.drawString(font, String.valueOf(handValue(dealerCards).total), 34, -42, 0xC3B64D);
        }

    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(@NotNull T w){
        return super.addRenderableWidget(w);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if ((pKeyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue() || pKeyCode == GLFW.GLFW_KEY_ESCAPE) && phase != Phase.WAITING_PLAY) {
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void onClose() {
        if (phase == Phase.WAITING_PLAY) {
            super.onClose();
        }

    }

    private enum Phase {
        WAITING_PLAY,
        DEALING,
        PLAYER_TURN,
        DEALER_TURN,
        SHOW_RESULT
    }

    private enum Result {
        WIN,
        LOSE,
        TIE,
        BLACKJACK
    }

    private record HandValue(int total, boolean soft) { ;; }

    private HandValue handValue(List<FrogCard> cards){
        int total = 0;
        int aces = 0;
        for (FrogCard c : cards){
            if (!c.faceUp) continue;
            int v = c.value;
            if (v == 1){
                total += 11; aces++;
            } else{
                total += Math.min(v,10);
            }

        }

        while (total > 21 && aces > 0){
            total -= 10;
            aces--;
        }

        return new HandValue(total, aces > 0);
    }

    private static class FrogCard {
        public float x, y, prevX, prevY;
        public float targetX;
        public float targetY;
        public final int value; // 1 (which can be 1 or 11) and 4 10s
        public boolean faceUp;
        public boolean arrived = false;

        public boolean flipping = false;
        public int flipTick = 0;
        public static final int FLIP_TOTAL = 20;

        public FrogCard(float startX, float startY, float targetX, float targetY, int value, boolean faceUp){
            this.x = startX;
            this.y = startY;
            this.prevX = startX;
            this.prevY = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.value = value;
            this.faceUp = faceUp;
        }

        public void startFlip(){
            if (!faceUp && !flipping){
                flipping = true;
                flipTick = 0;
            }
        }

        public boolean tick(float s){
            if (flipping) {
                flipTick++;
                float half = FLIP_TOTAL / 2f;
                if (flipTick == half) {
                    faceUp = true;
                }

                if (flipTick >= FLIP_TOTAL) {
                    flipping = false;
                }

            }

            if (arrived)
                return true;

            prevX = x;
            prevY = y;
            x += (targetX - x) * s;
            y += (targetY - y) * s;

            if (Math.abs(x-targetX) < 0.5f && Math.abs(y - targetY) < 0.5f){
                x = targetX; y = targetY;
                arrived = true;
            }

            return arrived;
        }

        public float getFlipAngle() {
            if (!flipping) return 0f;

            float half = FLIP_TOTAL / 2f;
            if (flipTick <= half) {
                return 90f * (flipTick / half);
            } else {
                return 90f * (1 - ((flipTick - half) / half));
            }

        }

    }

}