package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class PauseOverlay {
    private final PlayScreen playScreen;
    private final AgentDungGame game;

    private final Texture titlePausedTex;
    private final Texture btnResumeTex;
    private final Texture btnRestartTex;
    private final Texture btnMainMenuTex;

    private final Rectangle rectResumeBtn;
    private final Rectangle rectRestartBtn;
    private final Rectangle rectMainMenuBtn;

    public PauseOverlay(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.game = playScreen.game;

        // Nạp các tài nguyên ảnh phục vụ giao diện Pause từ assets
        titlePausedTex = new Texture(Gdx.files.internal("ui/UI_title_paused.png"));
        btnResumeTex = new Texture(Gdx.files.internal("ui/UI_button_resume_lite.png"));
        btnRestartTex = new Texture(Gdx.files.internal("ui/UI_button_restart_lite.png"));
        btnMainMenuTex = new Texture(Gdx.files.internal("ui/UI_button_mainmenu_lite.png")); // Giữ nguyên nút Main Menu gốc hoặc đổi sang bản lite nếu bạn có asset riêng

        // Khử mờ Pixel Art
        titlePausedTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnResumeTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnRestartTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnMainMenuTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        rectResumeBtn = new Rectangle();
        rectRestartBtn = new Rectangle();
        rectMainMenuBtn = new Rectangle();
    }

    public void render(ShapeRenderer shapeRenderer, Matrix4 hudMatrix) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        // --- 1. VẼ NỀN TỐI MỜ (DIM BACKGROUND) ĐÈ LÊN GAME ĐANG CHƠI ---
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(hudMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.6f)); // Màu đen trong suốt 60%
        shapeRenderer.rect(0, 0, sw, sh);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // --- 2. VẼ TIÊU ĐỀ VÀ NÚT BẤM ---
        game.batch.setProjectionMatrix(hudMatrix);
        game.batch.begin();

        // Vẽ chữ tiêu đề lớn "PAUSED" ở phía trên
        float titleW = 420f;
        float titleH = 90f;
        float titleX = (sw - titleW) / 2f;
        float titleY = sh * 0.68f;
        game.batch.draw(titlePausedTex, titleX, titleY, titleW, titleH);

        // Kích thước nút tiêu chuẩn 360x50
        float btnW = 360f;
        float btnH = 50f;
        float btnX = (sw - btnW) / 2f;

        // Định vị khoảng cách pixel cố định lấy nút Restart làm tâm giữa màn hình (35% chiều cao)
        float restartY = sh * 0.35f;
        float btnGap = 25f;

        float resumeY = restartY + btnH + btnGap;
        float mainMenuY = restartY - btnH - btnGap;

        // Thiết lập tọa độ hitbox để check chạm chuột
        rectResumeBtn.set(btnX, resumeY, btnW, btnH);
        rectRestartBtn.set(btnX, restartY, btnW, btnH);
        rectMainMenuBtn.set(btnX, mainMenuY, btnW, btnH);

        // Vẽ các nút bấm lên màn hình HUD
        game.batch.draw(btnResumeTex, rectResumeBtn.x, rectResumeBtn.y, rectResumeBtn.width, rectResumeBtn.height);
        game.batch.draw(btnRestartTex, rectRestartBtn.x, rectRestartBtn.y, rectRestartBtn.width, rectRestartBtn.height);
        game.batch.draw(btnMainMenuTex, rectMainMenuBtn.x, rectMainMenuBtn.y, rectMainMenuBtn.width, rectMainMenuBtn.height);

        game.batch.end();
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPoint.y = Gdx.graphics.getHeight() - touchPoint.y;

            // Nhấn Resume: Tiếp tục game
            if (rectResumeBtn.contains(touchPoint.x, touchPoint.y)) {
                playClickSound();
                playScreen.setPaused(false);
            }
            // Nhấn Restart: Chơi lại màn hiện tại
            else if (rectRestartBtn.contains(touchPoint.x, touchPoint.y)) {
                playClickSound();
                playScreen.initLevel(playScreen.currentLevel);
            }
            // Nhấn Main Menu: Quay về màn hình chính
            else if (rectMainMenuBtn.contains(touchPoint.x, touchPoint.y)) {
                playClickSound();
                game.setScreen(new MenuScreen(game));
            }
        }
    }

    private void playClickSound() {
        if (game.isMasterOn && game.isSfxOn && game.clickSound != null) {
            game.clickSound.play();
        }
    }

    public void dispose() {
        if (titlePausedTex != null) titlePausedTex.dispose();
        if (btnResumeTex != null) btnResumeTex.dispose();
        if (btnRestartTex != null) btnRestartTex.dispose();
        if (btnMainMenuTex != null) btnMainMenuTex.dispose();
    }
}
