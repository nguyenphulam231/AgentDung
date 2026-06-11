package com.agentdung.game.core;

import com.agentdung.game.assets.GameAssets;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.agentdung.game.screens.IntroScreen;

public class AgentDungGame extends Game {
    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch;

    // --- QUẢN LÝ TÀI NGUYÊN TOÀN CỤC ---
    public GameAssets assets;

    // --- LOGIC LƯU TRỮ ---
    public int selectedCharacterId = 1;
    public int selectedVariantId = 1;
    public boolean isMasterOn = true;
    public boolean isSfxOn = true;
    public boolean isMusicOn = true;
    public int globalCoinCount = 5000;
    public int[] completedLevelsReal = {0, 0, 0, 0, 0};
    public final int[] totalLevelsReal = {5, 3, 4, 3, 5};

    public Texture[] numberTextures;
    private static final String SAVE_PREFS_NAME = "AgentDungGameProgress";
    private Runnable skillSoundStopper;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        // 1. Khởi tạo và nạp toàn bộ tài nguyên qua GameAssets
        assets = new GameAssets();
        assets.loadPlayerTexture(selectedCharacterId, selectedVariantId);
        assets.loadEnemyTexture();
        assets.loadServerTexture();
        assets.loadMapAssets();
        assets.loadSounds();
        assets.loadMenuAssets();
        assets.loadCustomizeAssets();


        // 2. Phát nhạc nền
        updateMusicState();

        // 3. Nạp tài nguyên giao diện
        numberTextures = new Texture[10];
        for (int i = 0; i < 10; i++) {
            numberTextures[i] = new Texture(Gdx.files.internal("ui/UI_number" + i + ".png"));
        }

        setScreen(new IntroScreen(this));
    }

    public void updateMusicState() {
        if (assets.getBackgroundMusic() != null) {
            if (isMasterOn && isMusicOn) {
                if (!assets.getBackgroundMusic().isPlaying()) assets.getBackgroundMusic().play();
            } else {
                if (assets.getBackgroundMusic().isPlaying()) assets.getBackgroundMusic().pause();
            }
        }
    }

    public void setSkillSoundStopper(Runnable stopper) {
        this.skillSoundStopper = stopper;
    }

    public void stopSkillSounds() {
        if (skillSoundStopper != null) skillSoundStopper.run();
    }

    public void updateSfxState() {
        if (!isMasterOn || !isSfxOn) {
            stopSkillSounds();
        }
    }

    public void loadProgress() {
        Preferences prefs = Gdx.app.getPreferences(SAVE_PREFS_NAME);
        for (int i = 0; i < completedLevelsReal.length; i++) {
            completedLevelsReal[i] = prefs.getInteger("map_" + (i + 1), 0);
        }
        this.globalCoinCount = prefs.getInteger("global_coin_count", 5000);
    }

    public void saveProgress() {
        Preferences prefs = Gdx.app.getPreferences(SAVE_PREFS_NAME);
        for (int i = 0; i < completedLevelsReal.length; i++) {
            prefs.putInteger("map_" + (i + 1), completedLevelsReal[i]);
        }
        prefs.putInteger("global_coin_count", this.globalCoinCount);
        prefs.flush();
    }

    @Override
    public void dispose() {
        super.dispose();
        // Giải phóng tập trung qua GameAssets
        if (assets != null) assets.dispose();

        if (numberTextures != null) {
            for (Texture t : numberTextures) if (t != null) t.dispose();
        }
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
    }
}
