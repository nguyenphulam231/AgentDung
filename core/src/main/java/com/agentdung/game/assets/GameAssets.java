package com.agentdung.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class GameAssets {
    private Texture playerTexture;
    private Texture enemyTexture; // Thêm biến lưu texture của quái vật

    /**
     * Gọi hàm này khi bắt đầu game hoặc ở màn hình chọn nhân vật
     */
    public void loadPlayerTexture(int characterId, int variantId) {
        if (playerTexture != null) {
            playerTexture.dispose();
        }
        String path = "images/player" + characterId + "_" + variantId + ".png";
        Gdx.app.log("GameAssets", "Đang nạp sprite sheet động: " + path);
        playerTexture = new Texture(Gdx.files.internal(path));
        playerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    /**
     * Nạp trước asset cho quái vật tuần tra (Gọi trong hàm create() của AgentDungGame)
     */
    public void loadEnemyTexture() {
        if (enemyTexture == null) {
            Gdx.app.log("GameAssets", "Đang nạp sprite sheet quái vật: images/Patroler.png");
            enemyTexture = new Texture(Gdx.files.internal("images/Patroler.png"));
            enemyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    public Texture getPlayerTexture() {
        return playerTexture;
    }

    // Getter lấy ảnh quái vật
    public Texture getEnemyTexture() {
        return enemyTexture;
    }

    /**
     * Giải phóng toàn bộ bộ nhớ khi tắt game
     */
    public void dispose() {
        if (playerTexture != null) {
            playerTexture.dispose();
        }
        if (enemyTexture != null) {
            enemyTexture.dispose();
        }
    }
}
