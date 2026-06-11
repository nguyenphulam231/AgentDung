package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class PauseOverlay {
    private final PlayScreen playScreen;
    private final AgentDungGame game;

    private final Rectangle rectResumeBtn;
    private final Rectangle rectRestartBtn;
    private final Rectangle rectMainMenuBtn;

    public PauseOverlay(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.game = playScreen.game;

        // Texture do GameAssets quản lý, không tự load ở đây
        rectResumeBtn = new Rectangle();
        rectRestartBtn = new Rectangle();
        rectMainMenuBtn = new Rectangle();
    }

    public void render(ShapeRenderer shapeRenderer, Matrix4 hudMatrix) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(hudMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.6f));
        shapeRenderer.rect(0, 0, sw, sh);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.batch.setProjectionMatrix(hudMatrix);
        game.batch.begin();

        float titleW = 420f;
        float titleH = 90f;
        float titleX = (sw - titleW) / 2f;
        float titleY = sh * 0.68f;
        game.batch.draw(game.assets.getPausedTitleTex(), titleX, titleY, titleW, titleH);

        float btnW = 360f;
        float btnH = 50f;
        float btnX = (sw - btnW) / 2f;
        float restartY = sh * 0.35f;
        float btnGap = 25f;
        float resumeY = restartY + btnH + btnGap;
        float mainMenuY = restartY - btnH - btnGap;

        rectResumeBtn.set(btnX, resumeY, btnW, btnH);
        rectRestartBtn.set(btnX, restartY, btnW, btnH);
        rectMainMenuBtn.set(btnX, mainMenuY, btnW, btnH);

        game.batch.draw(game.assets.getResumeBtnTex(), rectResumeBtn.x, rectResumeBtn.y, rectResumeBtn.width, rectResumeBtn.height);
        game.batch.draw(game.assets.getRestartBtnTex(), rectRestartBtn.x, rectRestartBtn.y, rectRestartBtn.width, rectRestartBtn.height);
        game.batch.draw(game.assets.getMainMenuBtnTex(), rectMainMenuBtn.x, rectMainMenuBtn.y, rectMainMenuBtn.width, rectMainMenuBtn.height);

        game.batch.end();
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPoint.y = Gdx.graphics.getHeight() - touchPoint.y;

            if (rectResumeBtn.contains(touchPoint.x, touchPoint.y)) {
                playClickSound();
                playScreen.setPaused(false);
            } else if (rectRestartBtn.contains(touchPoint.x, touchPoint.y)) {
                playClickSound();
                playScreen.initLevel(playScreen.currentLevel);
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
        // Texture do GameAssets quản lý, không dispose ở đây
    }
}
