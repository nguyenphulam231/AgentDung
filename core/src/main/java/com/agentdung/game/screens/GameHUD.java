package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.skills.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameHUD {
    private final AgentDungGame game;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout;

    private final Rectangle rectPauseBtn;
    private final Rectangle rectBagBtn;

    public GameHUD(AgentDungGame game) {
        this.game = game;
        this.rectPauseBtn = new Rectangle();
        this.rectBagBtn = new Rectangle();
        this.font = new BitmapFont();
        this.glyphLayout = new GlyphLayout();
        this.font.getData().setScale(1.5f);
    }

    public void render(Array<Skill> skills, Matrix4 hudMatrix, boolean hasKey) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        game.batch.setProjectionMatrix(hudMatrix);
        game.batch.begin();

        // 1. Mana bars: Lấy Texture từ GameAssets
        float startX = 20;
        float targetWidth = 150;
        for (int i = 0; i < skills.size; i++) {
            Skill s = skills.get(i);
            // Gọi phương thức getter mới từ GameAssets
            com.badlogic.gdx.graphics.Texture tex = game.assets.getManaTexture(s.getClass());
            if (tex != null) {
                float progress = s.getManaPercent();
                float startY = sh - 40 - (i * 25);
                game.batch.draw(tex, startX, startY, targetWidth * progress, 15, 0, 0, (int)(tex.getWidth() * progress), tex.getHeight(), false, false);
            }
        }

        // 2. Vẽ nút Pause
        float btnSize = 40f;
        float pauseX = sw - btnSize - 20f;
        float pauseY = sh - btnSize - 20f;
        rectPauseBtn.set(pauseX, pauseY, btnSize, btnSize);
        game.batch.draw(game.assets.getBtnPauseTex(), rectPauseBtn.x, rectPauseBtn.y, rectPauseBtn.width, rectPauseBtn.height);

        // 3. Vẽ nút Bag
        float bagX = pauseX - btnSize - 15f;
        float bagY = pauseY;
        rectBagBtn.set(bagX, bagY, btnSize, btnSize);
        game.batch.draw(game.assets.getBtnBagTex(), rectBagBtn.x, rectBagBtn.y, rectBagBtn.width, rectBagBtn.height);

        // 4. Vẽ Coin và số xu
        float coinSize = 30f;
        float coinX = bagX - coinSize - 20f;
        float coinY = bagY + 5f;

        if (game.assets.getCoinTex() != null) {
            game.batch.draw(game.assets.getCoinTex(), coinX, coinY, coinSize, coinSize);
        }

        // Vẽ số xu
        font.setColor(Color.GOLD);
        String coinText = String.valueOf(game.globalCoinCount);
        glyphLayout.setText(font, coinText);

        float textX = coinX - glyphLayout.width - 10f;
        float textY = coinY + 25f;

        font.draw(game.batch, coinText, textX, textY);
        font.setColor(Color.WHITE);

        game.batch.end();
    }

    public void dispose() {
        // Chỉ dispose những gì GameHUD sở hữu (ví dụ: Font)
        if (font != null) font.dispose();
        // Không dispose Texture/Sound ở đây nữa vì GameAssets đảm nhiệm việc đó
    }

    public Rectangle getRectPauseBtn() { return rectPauseBtn; }
    public Rectangle getRectBagBtn() { return rectBagBtn; }
}
