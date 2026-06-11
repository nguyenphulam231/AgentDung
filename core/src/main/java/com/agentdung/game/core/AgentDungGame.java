package com.agentdung.game.core;

import com.agentdung.game.assets.GameAssets; // Import package assets mới tạo
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.agentdung.game.screens.MenuScreen;
import com.agentdung.game.screens.IntroScreen;

public class AgentDungGame extends Game {
    public Music backgroundMusic;
    public Sound clickSound;
    public Sound pickWaterSound;

    // --- HIỆU ỨNG ÂM THANH CHO CÁC CHIÊU THỨC ---
    public Sound spitSound;
    public Sound poopSound;
    public Sound peeSound;
    public Sound vomitSound;

    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch;

    // --- QUẢN LÝ TÀI NGUYÊN TOÀN CỤC ---
    public GameAssets assets;

    // --- LOGIC LƯU TRỮ NHÂN VẬT ĐƯỢC CHỌN ---
    public int selectedCharacterId = 1;
    public int selectedVariantId = 1;

    // --- BIẾN TRẠNG THÁI LƯU TRỮ CẤU HÌNH ÂM THANH TỔNG THỂ ---
    public boolean isMasterOn = true;
    public boolean isSfxOn = true;
    public boolean isMusicOn = true;

    // --- BIẾN LƯU TRỮ XU TOÀN CỤC XUYÊN SUỐT CÁC LEVEL ---
    public int globalCoinCount = 5000;

    // --- QUẢN LÝ TIẾN TRÌNH CHƠI THỰC TẾ ---
    public int[] completedLevelsReal = {0, 0, 0, 0, 0};
    public final int[] totalLevelsReal = {5, 3, 4, 3, 5};

    // --- QUẢN LÝ BỘ NHỚ TEXTURE SỐ ---
    public Texture[] numberTextures;

    // Tên file lưu trữ dữ liệu cục bộ trên ổ cứng của hệ thống Preferences
    private static final String SAVE_PREFS_NAME = "AgentDungGameProgress";

    private Runnable skillSoundStopper;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        // 1. Khởi tạo đối tượng quản lý tài nguyên
        assets = new GameAssets();

        // 2. Tải trước ảnh nhân vật dựa trên ID mặc định ban đầu
        assets.loadPlayerTexture(selectedCharacterId, selectedVariantId);
        assets.loadEnemyTexture();

        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/theme_music.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f);
            backgroundMusic.play();

            clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/click_sound.ogg"));
            pickWaterSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pick_water.ogg"));

            // --- NẠP CÁC FILE ÂM THANH CHIÊU THỨC MỚI ---
            spitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_spit.ogg"));
            poopSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_poop.ogg"));
            peeSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_pee.ogg"));
            vomitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_vomit.ogg"));

            updateMusicState();

            // --- NẠP TRƯỚC CÁC FILE ẢNH SỐ ---
            numberTextures = new Texture[10];
            for (int i = 0; i < 10; i++) {
                numberTextures[i] = new Texture(Gdx.files.internal("ui/UI_number" + i + ".png"));
            }

            // Tự động tải lại kỉ lục tiến trình cũ và số xu từ ổ cứng ngay khi bật game
            //loadProgress();

        } catch (Exception e) {
            Gdx.app.error("AgentDungGame", "Không thể tải tài nguyên hệ thống: " + e.getMessage());
        }

        setScreen(new IntroScreen(this));
    }

    // --- TỰ ĐỘNG TẢI TIẾN TRÌNH VÀ SỐ XU TỪ Ổ CỨNG ---
    public void loadProgress() {
        Preferences prefs = Gdx.app.getPreferences(SAVE_PREFS_NAME);
        for (int i = 0; i < completedLevelsReal.length; i++) {
            completedLevelsReal[i] = prefs.getInteger("map_" + (i + 1), 0);
        }

        // Đọc số xu tích lũy từ ổ cứng
        this.globalCoinCount = prefs.getInteger("global_coin_count", 5000);

        Gdx.app.log("SaveSystem", "Đã tải thành công tiến trình và số xu tích lũy từ bộ nhớ thiết bị.");
    }

    // --- CHỦ ĐỘNG GHI TIẾN TRÌNH VÀ SỐ XU XUỐNG Ổ CỨNG ---
    public void saveProgress() {
        Preferences prefs = Gdx.app.getPreferences(SAVE_PREFS_NAME);
        for (int i = 0; i < completedLevelsReal.length; i++) {
            prefs.putInteger("map_" + (i + 1), completedLevelsReal[i]);
        }

        // Lưu trữ vật lý số xu hiện tại vào Preferences
        prefs.putInteger("global_coin_count", this.globalCoinCount);

        prefs.flush(); // Bắt buộc thực hiện ghi vật lý xuống file
        Gdx.app.log("SaveSystem", "Đã tự động lưu tiến trình chơi và số xu mới vào ổ cứng.");
    }

    public void updateMusicState() {
        if (backgroundMusic != null) {
            if (isMasterOn && isMusicOn) {
                if (!backgroundMusic.isPlaying()) {
                    backgroundMusic.play();
                }
            } else {
                if (backgroundMusic.isPlaying()) {
                    backgroundMusic.pause();
                }
            }
        }
    }

    public void setSkillSoundStopper(Runnable stopper) {
        this.skillSoundStopper = stopper;
    }

    public void stopSkillSounds() {
        if (skillSoundStopper != null) {
            skillSoundStopper.run();
        }
    }

    public void updateSfxState() {
        if (!isMasterOn || !isSfxOn) {
            stopSkillSounds();
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();

        // Giải phóng bộ nhớ GameAssets để tránh rò rỉ texture nhân vật
        if (assets != null) {
            assets.dispose();
        }

        if (backgroundMusic != null) backgroundMusic.dispose();
        if (clickSound != null) clickSound.dispose();
        if (pickWaterSound != null) pickWaterSound.dispose();

        if (spitSound != null) spitSound.dispose();
        if (poopSound != null) poopSound.dispose();
        if (peeSound != null) peeSound.dispose();
        if (vomitSound != null) vomitSound.dispose();

        if (numberTextures != null) {
            for (Texture t : numberTextures) {
                if (t != null) t.dispose();
            }
        }

        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
    }
}
