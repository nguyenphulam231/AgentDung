package com.agentdung.game.screens;

import com.agentdung.game.entities.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
public class VendingMachineOverlay {
    private final PlayScreen screen;
    private final BitmapFont font;

    private Texture titleTex, bigFrameTex, smallFrameTex, btnBuyTex;
    private final Array<VendingSlot> slots = new Array<>();
    private VendingSlot selectedSlot = null;
    private Rectangle btnBuyBounds;

    private float notEnoughCoinsTimer = 0f;

    private final float VIRTUAL_WIDTH = 800f;
    private final float VIRTUAL_HEIGHT = 600f;

    public VendingMachineOverlay(PlayScreen screen) {
        this.screen = screen;

        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.2f);

        titleTex = new Texture("ui/UI_title_vendingmachine.png");
        bigFrameTex = new Texture("ui/bigframe.png");
        smallFrameTex = new Texture("ui/smallframe.png");
        btnBuyTex = new Texture("ui/UI_button_buy.png");

        btnBuyBounds = new Rectangle();
        setupVendingProducts();
    }

    private void setupVendingProducts() {
        slots.clear();
        for (Item.ItemType type : Item.ItemType.values()) {
            if (type == Item.ItemType.KEY || type == Item.ItemType.COIN) continue;
            slots.add(new VendingSlot(type));
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shape, Matrix4 originalHudMatrix) {
        if (notEnoughCoinsTimer > 0) {
            notEnoughCoinsTimer -= Gdx.graphics.getDeltaTime();
        }

        Matrix4 overlayMatrix = new Matrix4().setToOrtho2D(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        shape.setProjectionMatrix(overlayMatrix);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0, 0, 0, 0.75f);
        shape.rect(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        shape.end();

        batch.setProjectionMatrix(overlayMatrix);
        batch.begin();

        float titleX = VIRTUAL_WIDTH / 2f - titleTex.getWidth() / 2f;
        float titleY = VIRTUAL_HEIGHT - titleTex.getHeight() - 30;
        batch.draw(titleTex, titleX, titleY);

        float bigX = VIRTUAL_WIDTH * 0.08f;
        float bigY = VIRTUAL_HEIGHT * 0.18f;
        float bigW = VIRTUAL_WIDTH * 0.38f;
        float bigH = VIRTUAL_HEIGHT * 0.55f;
        batch.draw(bigFrameTex, bigX, bigY, bigW, bigH);

        float startSmallX = VIRTUAL_WIDTH * 0.52f;
        float smallY = VIRTUAL_HEIGHT * 0.62f;
        float slotSize = 54f;
        float gap = 15f;

        for (int i = 0; i < slots.size; i++) {
            int col = i % 4;
            int row = i / 4;
            float x = startSmallX + col * (slotSize + gap);
            float y = smallY - row * (slotSize + gap);

            VendingSlot slot = slots.get(i);
            slot.bounds.set(x, y, slotSize, slotSize);

            batch.draw(smallFrameTex, x, y, slotSize, slotSize);

            Texture texIcon = screen.mapManager.getItemTexture(slot.type);
            if (texIcon == null) {
                texIcon = screen.mapManager.vendingMachineTexture;
            }

            if (texIcon != null) {
                batch.draw(texIcon, x + 7, y + 7, slotSize - 14, slotSize - 14);
            }

            font.draw(batch, slot.type.price + "C", x + 5, y + 18);

            if (selectedSlot == slot) {
                batch.end();
                shape.begin(ShapeRenderer.ShapeType.Line);
                shape.setColor(Color.GREEN);
                shape.rect(x, y, slotSize, slotSize);
                shape.end();
                batch.begin();
            }
        }

        // --- SỐ XU HIỂN THỊ DƯỚI CÁC Ô VẬT PHẨM ---
        int maxRows = (int) Math.ceil(slots.size / 4.0);
        float coinDisplayY = smallY - (maxRows * (slotSize + gap)) + 10;
        font.setColor(Color.GOLD);
        font.draw(batch, "YOUR COINS: " + screen.game.globalCoinCount, startSmallX, coinDisplayY);
        font.setColor(Color.WHITE);

        if (selectedSlot != null) {
            float textX = bigX + 25;
            float textY = bigY + bigH - 40;
            font.setColor(Color.GREEN);
            font.draw(batch, "PRODUCT: " + selectedSlot.type.displayName, textX, textY);
            font.setColor(Color.WHITE);
            font.draw(batch, selectedSlot.type.description, textX, textY - 40);
            font.draw(batch, "PRICE: " + selectedSlot.type.price + " Coins", bigX + 25, bigY + 80);

            int inBag = screen.state.inventory.getOrDefault(selectedSlot.type, 0);
            font.draw(batch, "In Inventory: " + inBag, bigX + 25, bigY + 45);
        } else {
            font.draw(batch, "Select an item to purchase", bigX + 25, bigY + bigH - 40);
        }

        float btnW = 110f;
        float btnH = 40f;
        float btnX = VIRTUAL_WIDTH * 0.90f - btnW;
        float btnY = bigY;
        btnBuyBounds.set(btnX, btnY, btnW, btnH);
        batch.draw(btnBuyTex, btnX, btnY, btnW, btnH);

        if (notEnoughCoinsTimer > 0) {
            font.setColor(Color.RED);
            if ((int)(notEnoughCoinsTimer * 5) % 2 == 0) {
                font.draw(batch, "You don't have enough coins!", bigX + 25, bigY + 120);
            }
            font.setColor(Color.WHITE);
        }

        batch.end();
    }

    public void handleInput() {
        // Logic phím ESC do PlayScreen quản lý tập trung
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touch.x = (touch.x / (float) Gdx.graphics.getWidth()) * VIRTUAL_WIDTH;
            touch.y = (1 - touch.y / (float) Gdx.graphics.getHeight()) * VIRTUAL_HEIGHT;

            for (VendingSlot slot : slots) {
                if (slot.bounds.contains(touch.x, touch.y)) {
                    if (screen.game.isMasterOn && screen.game.isSfxOn && screen.game.assets.getClickSound() != null) screen.game.assets.getClickSound().play();
                    selectedSlot = slot;
                    notEnoughCoinsTimer = 0f;
                    return;
                }
            }

            if (btnBuyBounds.contains(touch.x, touch.y) && selectedSlot != null) {
                if (screen.game.globalCoinCount >= selectedSlot.type.price) {
                    if (screen.game.isMasterOn && screen.game.isSfxOn && screen.game.assets.getClickSound() != null) screen.game.assets.getClickSound().play();
                    screen.game.globalCoinCount -= selectedSlot.type.price;

                    int curCount = screen.state.inventory.getOrDefault(selectedSlot.type, 0);
                    screen.state.inventory.put(selectedSlot.type, curCount + 1);

                    if (selectedSlot.type == Item.ItemType.AMULET) screen.state.amuletCount++;
                    notEnoughCoinsTimer = 0f;
                } else {
                    notEnoughCoinsTimer = 2.0f;
                    if (screen.game.isMasterOn && screen.game.isSfxOn && screen.game.assets.getClickSound() != null) {
                        screen.game.assets.getClickSound().play();
                    }
                }
            }
        }
    }

    public void dispose() {
        if (titleTex != null) titleTex.dispose();
        if (bigFrameTex != null) bigFrameTex.dispose();
        if (smallFrameTex != null) smallFrameTex.dispose();
        if (btnBuyTex != null) btnBuyTex.dispose();
        if (font != null) font.dispose();
    }

    private static class VendingSlot {
        public Item.ItemType type;
        public Rectangle bounds;

        public VendingSlot(Item.ItemType type) {
            this.type = type;
            this.bounds = new Rectangle();
        }
    }
}
