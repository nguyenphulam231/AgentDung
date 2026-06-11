package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.entities.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class WikiScreen extends ScreenAdapter {
    private final AgentDungGame game;
    private BitmapFont font;
    private ShapeRenderer shape;

    // Xóa hết 5 biến Texture và iconTextures — GameAssets quản lý rồi

    private static final float VW = 800f;
    private static final float VH = 600f;
    private static final float BIG_X        = VW * 0.08f;
    private static final float BIG_Y        = VH * 0.18f;
    private static final float BIG_W        = VW * 0.38f;
    private static final float BIG_H        = VH * 0.55f;
    private static final float GRID_START_X = VW * 0.52f;
    private static final float GRID_START_Y = VH * 0.62f;
    private static final float SLOT_SIZE    = 54f;
    private static final float GAP          = 15f;
    private static final int   GRID_COLS    = 4;
    private boolean inputConsumed = false;
    private float inputDelay = 0f;
    private static final float INPUT_DELAY = 0.1f;

    private final Rectangle backBounds = new Rectangle(20, VH - 60, 45, 45);
    private final Array<Item.ItemType> wikiItems  = new Array<>();
    private final Array<Rectangle>     slotBounds = new Array<>();
    private Item.ItemType selectedItem = null;

    public WikiScreen(AgentDungGame game) {
        this.game = game;
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.2f);
        this.shape = new ShapeRenderer();

        for (Item.ItemType type : Item.ItemType.values()) {
            if (type != Item.ItemType.KEY && type != Item.ItemType.COIN) {
                wikiItems.add(type);
                slotBounds.add(new Rectangle());
            }
        }
    }

    @Override
    public void show() {
        // Đảm bảo assets cần thiết đã được load
        game.assets.loadMenuAssets();   // wikiTitleTex, bgTexture
        game.assets.loadMapAssets();    // itemTextures, bigFrame, smallFrame
        game.assets.loadGuideAssets();  // backBtnTex
        inputConsumed = true;
        inputDelay = INPUT_DELAY;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        if (inputDelay > 0) {
            inputDelay -= delta;
        } else {
            handleInput();
        }

        Matrix4 overlayMatrix = new Matrix4().setToOrtho2D(0, 0, VW, VH);
        game.batch.setProjectionMatrix(overlayMatrix);
        game.batch.begin();

        game.batch.draw(game.assets.getSettingsBgTex(), 0, 0, VW, VH);
        game.batch.draw(game.assets.getBackBtnTex(), backBounds.x, backBounds.y, backBounds.width, backBounds.height);

        Texture titleWiki = game.assets.getWikiTitleTex();
        float titleX = VW / 2f - titleWiki.getWidth() / 2f;
        float titleY = VH - titleWiki.getHeight() - 30f;
        game.batch.draw(titleWiki, titleX, titleY);

        game.batch.draw(game.assets.getVendingBigFrameTex(), BIG_X, BIG_Y, BIG_W, BIG_H);

        float textX = BIG_X + 25f;
        if (selectedItem != null) {
            Texture icon = game.assets.getItemTexture(selectedItem);
            if (icon != null) {
                float iconSize = 64f;
                game.batch.draw(icon, BIG_X + BIG_W / 2f - iconSize / 2f, BIG_Y + BIG_H - 120f, iconSize, iconSize);
            }
            font.setColor(Color.GREEN);
            font.draw(game.batch, selectedItem.displayName, textX, BIG_Y + BIG_H - 130f);
            font.setColor(Color.WHITE);
            font.draw(game.batch, selectedItem.description, textX, BIG_Y + BIG_H - 160f, BIG_W - 50f, 0, true);
            font.setColor(Color.GOLD);
            font.draw(game.batch, "PRICE: " + selectedItem.price + " Coins", textX, BIG_Y + 60f);
            font.setColor(Color.WHITE);
        } else {
            font.draw(game.batch, "Select an item\nto view details", textX, BIG_Y + BIG_H / 2f + 10f);
        }

        for (int i = 0; i < wikiItems.size; i++) {
            int col = i % GRID_COLS;
            int row = i / GRID_COLS;
            float sx = GRID_START_X + col * (SLOT_SIZE + GAP);
            float sy = GRID_START_Y - row * (SLOT_SIZE + GAP);
            slotBounds.get(i).set(sx, sy, SLOT_SIZE, SLOT_SIZE);
            game.batch.draw(game.assets.getVendingSmallFrameTex(), sx, sy, SLOT_SIZE, SLOT_SIZE);
            Texture icon = game.assets.getItemTexture(wikiItems.get(i));
            if (icon != null) game.batch.draw(icon, sx + 8, sy + 8, SLOT_SIZE - 16, SLOT_SIZE - 16);
        }
        game.batch.end();

        shape.setProjectionMatrix(overlayMatrix);
        for (int i = 0; i < wikiItems.size; i++) {
            if (wikiItems.get(i) == selectedItem) {
                shape.begin(ShapeRenderer.ShapeType.Line);
                shape.setColor(Color.GREEN);
                shape.rect(slotBounds.get(i).x, slotBounds.get(i).y, SLOT_SIZE, SLOT_SIZE);
                shape.end();
            }
        }
    }

    private void handleInput() {
        if (inputConsumed) {
            inputConsumed = false;
            return;
        }
        if (!Gdx.input.justTouched()) return;
        if (!Gdx.input.justTouched()) return;
        float tx = (Gdx.input.getX() / (float) Gdx.graphics.getWidth()) * VW;
        float ty = (1f - Gdx.input.getY() / (float) Gdx.graphics.getHeight()) * VH;

        if (backBounds.contains(tx, ty)) {
            if (game.assets.getClickSound() != null) game.assets.getClickSound().play();
            game.setScreen(new MenuScreen(game));
            return;
        }

        for (int i = 0; i < wikiItems.size; i++) {
            if (slotBounds.get(i).contains(tx, ty)) {
                selectedItem = wikiItems.get(i);
                return;
            }
        }
    }

    @Override
    public void dispose() {
        // Texture do GameAssets quản lý, không dispose ở đây
        if (font != null) font.dispose();
        if (shape != null) shape.dispose();
    }
}
