package com.agentdung.game.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.agentdung.game.screens.MenuScreen;

public class AgentDungGame extends Game {
    public Music backgroundMusic;
    public Sound clickSound;
    public Sound pickWaterSound;
    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch;

    // --- LOGIC LƯU TRỮ NHÂN VẬT ĐƯỢC CHỌN ---
    // Mặc định ban đầu là Agent Dũng (ID: 1) - Biến thể 1 (Variant: 1)
    public int selectedCharacterId = 1;
    public int selectedVariantId = 1;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/theme_music.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f);
            backgroundMusic.play();

            clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/click_sound.ogg"));

            pickWaterSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pick_water.ogg"));

        } catch (Exception e) {
            Gdx.app.error("AgentDungGame", "Không thể tải tài nguyên âm thanh: " + e.getMessage());
        }

        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();
        if (clickSound != null) clickSound.dispose();

        if (pickWaterSound != null) pickWaterSound.dispose();

        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
    }
}
