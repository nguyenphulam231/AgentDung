package com.agentdung.game.screens;

import com.agentdung.game.entities.Item;
import com.agentdung.game.items.ItemEffectContext;
import com.agentdung.game.items.ItemEffectRegistry;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class InventoryOverlay {
    private final PlayScreen screen;
    private final BitmapFont font;
    private final Array<ItemSlot> slots = new Array<>();
    private Item.ItemType selectedType = null;
    private final Rectangle btnUseBounds;

    private final float VIRTUAL_WIDTH = 800f;
    private final float VIRTUAL_HEIGHT = 600f;
    private final int MAX_SLOTS = 12;

    public InventoryOverlay(PlayScreen screen) {
        this.screen = screen;
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.2f);
        this.btnUseBounds = new Rectangle();

        for (int i = 0; i < MAX_SLOTS; i++) {
            slots.add(new ItemSlot());
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shape, Matrix4 originalHudMatrix) {
        Matrix4 overlayMatrix = new Matrix4().setToOrtho2D(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        shape.setProjectionMatrix(overlayMatrix);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0, 0, 0, 0.6f);
        shape.rect(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        shape.end();

        batch.setProjectionMatrix(overlayMatrix);
        batch.begin();

        // Sử dụng các Texture từ GameAssets
        Texture titleTex = screen.game.assets.getInvTitleTex();
        Texture bigFrameTex = screen.game.assets.getInvBigFrameTex();
        Texture smallFrameTex = screen.game.assets.getInvSmallFrameTex();
        Texture btnUseTex = screen.game.assets.getInvBtnUseTex();

        float titleX = VIRTUAL_WIDTH / 2f - titleTex.getWidth() / 2f;
        float titleY = VIRTUAL_HEIGHT - titleTex.getHeight() - 30;
        batch.draw(titleTex, titleX, titleY);

        float bigX = VIRTUAL_WIDTH * 0.08f;
        float bigY = VIRTUAL_HEIGHT * 0.18f;
        float bigW = VIRTUAL_WIDTH * 0.38f;
        float bigH = VIRTUAL_HEIGHT * 0.55f;
        batch.draw(bigFrameTex, bigX, bigY, bigW, bigH);

        Array<Item.ItemType> activeItems = new Array<>();
        for (Item.ItemType type : Item.ItemType.values()) {
            if (type == Item.ItemType.COIN) continue;
            int count = screen.state.inventory.getOrDefault(type, 0);
            if (count > 0) activeItems.add(type);
        }

        if (selectedType != null && screen.state.inventory.getOrDefault(selectedType, 0) <= 0) {
            selectedType = null;
        }

        float startSmallX = VIRTUAL_WIDTH * 0.52f;
        float smallY = VIRTUAL_HEIGHT * 0.62f;
        float slotSize = 54f;
        float gap = 15f;

        for (int i = 0; i < MAX_SLOTS; i++) {
            int col = i % 4;
            int row = i / 4;
            float x = startSmallX + col * (slotSize + gap);
            float y = smallY - row * (slotSize + gap);

            ItemSlot slot = slots.get(i);
            slot.bounds.set(x, y, slotSize, slotSize);
            slot.assignedType = null;

            batch.draw(smallFrameTex, x, y, slotSize, slotSize);

            if (i < activeItems.size) {
                Item.ItemType currentType = activeItems.get(i);
                slot.assignedType = currentType;

                Texture texIcon = screen.mapManager.getItemTexture(currentType);
                if (texIcon != null) {
                    batch.draw(texIcon, x + 7, y + 7, slotSize - 14, slotSize - 14);
                }

                int count = screen.state.inventory.get(currentType);
                font.draw(batch, "x" + count, x + slotSize - 25, y + 18);

                if (selectedType == currentType) {
                    batch.end();
                    shape.begin(ShapeRenderer.ShapeType.Line);
                    shape.setColor(Color.YELLOW);
                    shape.rect(x, y, slotSize, slotSize);
                    shape.end();
                    batch.begin();
                }
            }
        }

        int maxRows = (int) Math.ceil(MAX_SLOTS / 4.0);
        float coinDisplayY = smallY - (maxRows * (slotSize + gap)) + 10;

        font.setColor(Color.GOLD);
        font.draw(batch, "YOUR COINS: " + screen.game.globalCoinCount, startSmallX, coinDisplayY);
        font.setColor(Color.WHITE);

        if (selectedType != null) {
            float textX = bigX + 25;
            float textY = bigY + bigH - 40;
            font.setColor(Color.GOLD);
            font.draw(batch, "ITEM: " + selectedType.displayName, textX, textY);
            font.setColor(Color.WHITE);
            font.draw(batch, selectedType.description, textX, textY - 40);

            int ownCount = screen.state.inventory.get(selectedType);
            font.draw(batch, "You own: " + ownCount, bigX + 25, bigY + 45);
        } else {
            font.draw(batch, "Select an item to view info", bigX + 25, bigY + bigH - 40);
        }

        float btnW = 110f;
        float btnH = 40f;
        float btnX = VIRTUAL_WIDTH * 0.90f - btnW;
        float btnY = bigY;
        btnUseBounds.set(btnX, btnY, btnW, btnH);
        batch.draw(btnUseTex, btnX, btnY, btnW, btnH);

        batch.end();
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touch.x = (touch.x / (float) Gdx.graphics.getWidth()) * VIRTUAL_WIDTH;
            touch.y = (1 - touch.y / (float) Gdx.graphics.getHeight()) * VIRTUAL_HEIGHT;

            for (ItemSlot slot : slots) {
                if (slot.bounds.contains(touch.x, touch.y)) {
                    if (slot.assignedType != null) {
                        playClickSound();
                        selectedType = slot.assignedType;
                    }
                    return;
                }
            }

            if (btnUseBounds.contains(touch.x, touch.y) && selectedType != null) {
                useSelectedItem();
            }
        }
    }

    private void useSelectedItem() {
        int count = screen.state.inventory.getOrDefault(selectedType, 0);
        if (count <= 0) return;

        playClickSound();
        screen.state.inventory.put(selectedType, count - 1);

        ItemEffectContext context = new ItemEffectContext(screen.state, screen.skills);
        ItemEffectRegistry.apply(selectedType, context);
        ItemEffectRegistry.onConsumed(selectedType, context);
    }

    private void playClickSound() {
        if (screen.game.isMasterOn && screen.game.isSfxOn && screen.game.assets.getClickSound() != null) {
            screen.game.assets.getClickSound().play();
        }
    }

    public void dispose() {
        // Chỉ giải phóng font, texture đã do GameAssets quản lý
        if (font != null) font.dispose();
    }

    private static class ItemSlot {
        public Rectangle bounds;
        public Item.ItemType assignedType;

        public ItemSlot() {
            this.bounds = new Rectangle();
            this.assignedType = null;
        }
    }
}
