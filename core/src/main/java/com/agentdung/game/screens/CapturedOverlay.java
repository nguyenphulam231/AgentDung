package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class CapturedOverlay {
    private final PlayScreen playScreen;
    private final AgentDungGame game;

    private final Sound capturedSound;
    private final Texture titleCapturedTex;
    private final Texture btnRestartTex;
    private final Texture btnProgressMenuTex;
    private final Texture btnMainMenuTex;

    private final Rectangle rectRestartBtn;
    private final Rectangle rectProgressBtn;
    private final Rectangle rectMainMenuBtn;

    public CapturedOverlay(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.game = playScreen.game;

        capturedSound = Gdx.audio.newSound(Gdx.files.internal("sounds/captured.ogg"));
        titleCapturedTex = new Texture(Gdx.files.internal("ui/UI_title_captured.png"));
        btnRestartTex = new Texture(Gdx.files.internal("ui/UI_button_restart_red.png"));
        btnProgressMenuTex = new Texture(Gdx.files.internal("ui/UI_button_progressmenu_red.png"));
        btnMainMenuTex = new Texture(Gdx.files.internal("ui/UI_button_mainmenu_red.png"));

        titleCapturedTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnRestartTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnProgressMenuTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnMainMenuTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        rectRestartBtn = new Rectangle();
        rectProgressBtn = new Rectangle();
        rectMainMenuBtn = new Rectangle();
    }

    public void playSound() {
        if (game.isMasterOn && game.isSfxOn && capturedSound != null) {
            capturedSound.play();
        }
    }

    public void render(ShapeRenderer shapeRenderer, Matrix4 hudMatrix) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        // --- 1. VẼ VIỀN ĐỎ MỜ (VIGNETTE) ---
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(hudMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        int edgeLayers = 15;
        float maxEdgeWidth = sw * 0.15f;

        for (int i = 0; i < edgeLayers; i++) {
            float progress = (float) i / edgeLayers;
            float alpha = progress * 0.45f;
            shapeRenderer.setColor(new Color(0.7f, 0f, 0f, alpha));

            float thicknessX = (1f - progress) * maxEdgeWidth;
            float thicknessY = (1f - progress) * maxEdgeWidth * (sh / sw);

            shapeRenderer.rect(0, 0, sw, thicknessY);
            shapeRenderer.rect(0, sh - thicknessY, sw, thicknessY);
            shapeRenderer.rect(0, thicknessY, thicknessX, sh - (2 * thicknessY));
            shapeRenderer.rect(sw - thicknessX, thicknessY, thicknessX, sh - (2 * thicknessY));
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // --- 2. VẼ UI CHỮ VÀ NÚT BẤM ĐỎ ---
        game.batch.setProjectionMatrix(hudMatrix);
        game.batch.begin();

        float titleW = 420f;
        float titleH = 90f;
        float titleX = (sw - titleW) / 2f;
        float titleY = sh * 0.68f;
        game.batch.draw(titleCapturedTex, titleX, titleY, titleW, titleH);

        float btnW = 360f;
        float btnH = 50f;
        float btnX = (sw - btnW) / 2f;

        float progressY = sh * 0.35f;
        float btnGap = 25f;

        float restartY = progressY + btnH + btnGap;
        float mainMenuY = progressY - btnH - btnGap;

        rectRestartBtn.set(btnX, restartY, btnW, btnH);
        rectProgressBtn.set(btnX, progressY, btnW, btnH);
        rectMainMenuBtn.set(btnX, mainMenuY, btnW, btnH);

        game.batch.draw(btnRestartTex, rectRestartBtn.x, rectRestartBtn.y, rectRestartBtn.width, rectRestartBtn.height);
        game.batch.draw(btnProgressMenuTex, rectProgressBtn.x, rectProgressBtn.y, rectProgressBtn.width, rectProgressBtn.height);
        game.batch.draw(btnMainMenuTex, rectMainMenuBtn.x, rectMainMenuBtn.y, rectMainMenuBtn.width, rectMainMenuBtn.height);

        game.batch.end();
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPoint.y = Gdx.graphics.getHeight() - touchPoint.y;

            if (rectRestartBtn.contains(touchPoint.x, touchPoint.y)) {
                playClickSound();
                playScreen.initLevel(playScreen.currentLevel);
            } else if (rectProgressBtn.contains(touchPoint.x, touchPoint.y)) {
                playClickSound();
                game.setScreen(new ProgressScreen(game, playScreen.currentWorld));
            } else if (rectMainMenuBtn.contains(touchPoint.x, touchPoint.y)) {
                playClickSound();
                game.setScreen(new MenuScreen(game));
            }
        }
    }

    private void playClickSound() {
        if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null) {
            game.assets.getClickSound().play();
        }
    }

    public void dispose() {
        if (capturedSound != null) capturedSound.dispose();
        if (titleCapturedTex != null) titleCapturedTex.dispose();
        if (btnRestartTex != null) btnRestartTex.dispose();
        if (btnProgressMenuTex != null) btnProgressMenuTex.dispose();
        if (btnMainMenuTex != null) btnMainMenuTex.dispose();
    }
}
