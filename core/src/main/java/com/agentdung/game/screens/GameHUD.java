package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.skills.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import java.util.Map;

public class GameHUD {
    private final AgentDungGame game;
    private final Map<Class<? extends Skill>, Texture> manaTextures;
    private final BitmapFont font;

    private Texture btnPauseTex;
    private final Rectangle rectPauseBtn;

    private Texture btnBagTex;
    private final Rectangle rectBagBtn;

    private Texture coinTex;

    public GameHUD(AgentDungGame game) {
        this.game = game;
        this.manaTextures = new HashMap<>();
        this.rectPauseBtn = new Rectangle();
        this.rectBagBtn = new Rectangle();
        this.font = new BitmapFont();
        // THIẾT LẬP FONT: Tăng size để chắc chắn nhìn thấy được
        this.font.getData().setScale(1.5f);
    }

    public void loadTextures() {
        clearTextures();

        manaTextures.put(SpitSkill.class, new Texture("ui/UI_mana_spit.png"));
        manaTextures.put(VomitSkill.class, new Texture("ui/UI_mana_vomit.png"));
        manaTextures.put(PeeSkill.class, new Texture("ui/UI_mana_pee.png"));
        manaTextures.put(PoopSkill.class, new Texture("ui/UI_mana_poop.png"));

        btnPauseTex = new Texture(Gdx.files.internal("ui/UI_button_pause.png"));
        btnBagTex = new Texture(Gdx.files.internal("ui/UI_button_bag.png"));
        coinTex = new Texture(Gdx.files.internal("images/coin.png"));
    }

    public void render(Array<Skill> skills, Matrix4 hudMatrix, boolean hasKey) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        game.batch.setProjectionMatrix(hudMatrix);
        game.batch.begin();

        // 1. Mana bars
        float startX = 20;
        float targetWidth = 150;
        for (int i = 0; i < skills.size; i++) {
            Skill s = skills.get(i);
            Texture tex = manaTextures.get(s.getClass());
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
        game.batch.draw(btnPauseTex, rectPauseBtn.x, rectPauseBtn.y, rectPauseBtn.width, rectPauseBtn.height);

        // 3. Vẽ nút Bag
        float bagX = pauseX - btnSize - 15f;
        float bagY = pauseY;
        rectBagBtn.set(bagX, bagY, btnSize, btnSize);
        game.batch.draw(btnBagTex, rectBagBtn.x, rectBagBtn.y, rectBagBtn.width, rectBagBtn.height);

        // 4. Vẽ Coin và số xu
        float coinSize = 30f;
        float coinX = bagX - coinSize - 50f;
        float coinY = bagY + 5f; // Chỉnh lại cho đồng bộ nút Bag

        if (coinTex != null) {
            game.batch.draw(coinTex, coinX, coinY, coinSize, coinSize);
        }

        // Vẽ số xu
        font.setColor(Color.GOLD);
        // Lưu ý: Nếu vẫn không hiện, hãy thử đổi thành Color.RED để kiểm tra
        font.draw(game.batch, String.valueOf(game.globalCoinCount), coinX + coinSize + 10, coinY + 25);
        font.setColor(Color.WHITE);

        game.batch.end();
    }

    public void clearTextures() {
        for (Texture tex : manaTextures.values()) if (tex != null) tex.dispose();
        manaTextures.clear();
        if (btnPauseTex != null) btnPauseTex.dispose();
        if (btnBagTex != null) btnBagTex.dispose();
        if (coinTex != null) coinTex.dispose();
    }

    public void dispose() {
        clearTextures();
        if (font != null) font.dispose();
    }

    public Rectangle getRectPauseBtn() { return rectPauseBtn; }
    public Rectangle getRectBagBtn() { return rectBagBtn; }
}
