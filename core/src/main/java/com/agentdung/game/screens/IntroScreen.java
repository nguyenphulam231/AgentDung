package com.agentdung.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.agentdung.game.core.AgentDungGame;

public class IntroScreen implements Screen {

    private final AgentDungGame game;
    private VideoPlayer videoPlayer;
    private boolean finished = false;

    public IntroScreen(AgentDungGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        videoPlayer.setOnCompletionListener(filename -> {
            finished = true;
        });

        try {
            videoPlayer.load(Gdx.files.internal("videos/intro_agent_dung.webm"));
            videoPlayer.play();
        } catch (Exception e) {
            Gdx.app.error("IntroScreen", "Không load được video", e);
            finished = true;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (finished || Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            goToMainMenu();
            return;
        }

        if (videoPlayer != null) {
            videoPlayer.update();
            Texture frame = videoPlayer.getTexture();
            if (frame != null) {
                game.batch.begin();
                game.batch.draw(frame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                game.batch.end();
            }
        }
    }

    private void goToMainMenu() {
        game.setScreen(new MenuScreen(game));
        dispose();
    }

    @Override public void resize(int width, int height) {}

    @Override
    public void pause() {
        if (videoPlayer != null) videoPlayer.pause();
    }

    @Override
    public void resume() {
        if (videoPlayer != null) videoPlayer.resume();
    }

    @Override public void hide() { dispose(); }

    @Override
    public void dispose() {
        if (videoPlayer != null) {
            videoPlayer.dispose();
            videoPlayer = null;
        }
    }
}
