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

    // --- HIỆU ỨNG ÂM THANH CHO CÁC CHIÊU THỨC ---
    public Sound spitSound;
    public Sound poopSound;
    public Sound peeSound;
    public Sound vomitSound;

    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch;

    // --- LOGIC LƯU TRỮ NHÂN VẬT ĐƯỢC CHỌN ---
    // Mặc định ban đầu là Agent Dũng (ID: 1) - Biến thể 1 (Variant: 1)
    public int selectedCharacterId = 1;
    public int selectedVariantId = 1;

    // --- ĐÃ THÊM: BIẾN TRẠNG THÁI LƯU TRỮ CẤU HÌNH ÂM THANH TỔNG THỂ ---
    public boolean isMasterOn = true;
    public boolean isSfxOn = true;
    public boolean isMusicOn = true;

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

            // --- NẠP CÁC FILE ÂM THANH CHIÊU THỨC MỚI TỪ THƯ MỤC SOUNDS ---
            spitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_spit.ogg"));
            poopSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_poop.ogg"));
            peeSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_pee.ogg"));
            vomitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_vomit.ogg"));

            // --- ĐÃ THÊM: Tự động cập nhật trạng thái nhạc nền theo cấu hình ngay khi khởi động ---
            updateMusicState();

        } catch (Exception e) {
            Gdx.app.error("AgentDungGame", "Không thể tải tài nguyên âm thanh: " + e.getMessage());
        }

        this.setScreen(new MenuScreen(this));
    }

    // --- ĐÃ THÊM: HÀM ĐIỀU KHIỂN VẬT LÝ CHO NHẠC NỀN ---
    public void updateMusicState() {
        if (backgroundMusic != null) {
            // Nhạc nền chỉ được phát khi cả âm thanh tổng (Master) và nhạc nền (Music) đều bật
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

    // --- ĐÃ THÊM: HÀM ĐIỀU KHIỂN VẬT LÝ CHO HIỆU ỨNG ÂM THANH (SFX) ---
    public void updateSfxState() {
        // Nếu âm thanh tổng bị tắt HOẶC hiệu ứng SFX bị tắt
        if (!isMasterOn || !isSfxOn) {
            // Ngắt lập tức toàn bộ các âm thanh kỹ năng đang lặp (looping) như tiếng đái
            com.agentdung.game.handlers.InputHandler.stopLoopingSounds(this);
        }
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

        // --- GIẢI PHÓNG BỘ NHỚ CÁC FILE ÂM THANH CHIÊU THỨC ---
        if (spitSound != null) spitSound.dispose();
        if (poopSound != null) poopSound.dispose();
        if (peeSound != null) peeSound.dispose();
        if (vomitSound != null) vomitSound.dispose();

        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
    }
}
