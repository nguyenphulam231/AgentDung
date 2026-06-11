package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class CapturedOverlay {
    private final PlayScreen playScreen;
    private final AgentDungGame game;

    private final Rectangle rectRestartBtn;
    private final Rectangle rectProgressBtn;
    private final Rectangle rectMainMenuBtn;

    public CapturedOverlay(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.game = playScreen.game;

        rectRestartBtn = new Rectangle();
        rectProgressBtn = new Rectangle();
        rectMainMenuBtn = new Rectangle();
    }

    public void playSound() {
        if (game.isMasterOn && game.isSfxOn && game.assets.getCapturedSound() != null) {
            game.assets.getCapturedSound().play();
        }
    }

    public void render(ShapeRenderer shapeRenderer, Matrix4 hudMatrix) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        // 1. VẼ VIỀN ĐỎ MỜ
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

        // 2. VẼ UI
        game.batch.setProjectionMatrix(hudMatrix);
        game.batch.begin();

        float titleW = 420f, titleH = 90f;
        float titleX = (sw - titleW) / 2f, titleY = sh * 0.68f;
        game.batch.draw(game.assets.getTitleCapturedTex(), titleX, titleY, titleW, titleH);

        float btnW = 360f, btnH = 50f, btnX = (sw - btnW) / 2f;
        float progressY = sh * 0.35f, btnGap = 25f;
        rectRestartBtn.set(btnX, progressY + btnH + btnGap, btnW, btnH);
        rectProgressBtn.set(btnX, progressY, btnW, btnH);
        rectMainMenuBtn.set(btnX, progressY - btnH - btnGap, btnW, btnH);

        game.batch.draw(game.assets.getBtnRestartTex(), rectRestartBtn.x, rectRestartBtn.y, btnW, btnH);
        game.batch.draw(game.assets.getBtnProgressMenuTex(), rectProgressBtn.x, rectProgressBtn.y, btnW, btnH);
        game.batch.draw(game.assets.getBtnMainMenuTex(), rectMainMenuBtn.x, rectMainMenuBtn.y, btnW, btnH);

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
}
