package com.agentdung.game.core;

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

    // --- LOGIC LƯU TRỮ NHÂN VẬT ĐƯỢC CHỌN ---
    // Mặc định ban đầu là Agent Dũng (ID: 1) - Biến thể 1 (Variant: 1)
    public int selectedCharacterId = 1;
    public int selectedVariantId = 1;

    // --- BIẾN TRẠNG THÁI LƯU TRỮ CẤU HÌNH ÂM THANH TỔNG THỂ ---
    public boolean isMasterOn = true;
    public boolean isSfxOn = true;
    public boolean isMusicOn = true;

    // --- QUẢN LÝ TIẾN TRÌNH CHƠI THỰC TẾ ---
    // Mảng lưu số lượng level ĐÃ VƯỢT QUA của từng map (Cơ sở dữ liệu bắt đầu từ 0)
    public int[] completedLevelsReal = {0, 0, 0, 0, 0};

    // Mảng định nghĩa TỔNG SỐ LEVEL thực tế đang có trong file assets cho từng map
    // (Ví dụ: Map 1 có 5 file tmx từ map1_1 đến map1_5, bạn có thể tự chỉnh lại số lượng ở đây)
    public final int[] totalLevelsReal = {5, 3, 4, 3, 5};

    // --- QUẢN LÝ BỘ NHỚ TEXTURE SỐ (Tránh rò rỉ RAM) ---
    // Mảng lưu sẵn 10 Texture ảnh số từ 0 đến 9 để dùng chung cho MissionsScreen
    public Texture[] numberTextures;

    // Tên file lưu trữ dữ liệu cục bộ trên ổ cứng của hệ thống Preferences
    private static final String SAVE_PREFS_NAME = "AgentDungGameProgress";

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

            // Tự động cập nhật trạng thái nhạc nền theo cấu hình ngay khi khởi động
            updateMusicState();

            // --- NẠP TRƯỚC CÁC FILE ẢNH SỐ VÀO BỘ NHỚ ĐỆM ---
            numberTextures = new Texture[10];
            for (int i = 0; i < 10; i++) {
                numberTextures[i] = new Texture(Gdx.files.internal("ui/UI_number" + i + ".png"));
            }

            // --- ĐÃ THÊM: Tự động tải lại kỉ lục tiến trình cũ từ ổ cứng ngay khi bật game ---
            loadProgress();

        } catch (Exception e) {
            Gdx.app.error("AgentDungGame", "Không thể tải tài nguyên hệ thống: " + e.getMessage());
        }

        setScreen(new IntroScreen(this)); // ← đổi từ MenuScreen sang IntroScreen
    }

    // --- ĐÃ THÊM: HÀM TỰ ĐỘNG TẢI TIẾN TRÌNH TỪ Ổ CỨNG ---
    public void loadProgress() {
        Preferences prefs = Gdx.app.getPreferences(SAVE_PREFS_NAME);
        for (int i = 0; i < completedLevelsReal.length; i++) {
            // Đọc số màn đã qua của từng Map (Key: map_1, map_2,...), mặc định nếu chưa chơi là 0
            completedLevelsReal[i] = prefs.getInteger("map_" + (i + 1), 0);
        }
        Gdx.app.log("SaveSystem", "Đã tải thành công tiến trình chơi từ bộ nhớ thiết bị.");
    }

    // --- ĐÃ THÊM: HÀM CHỦ ĐỘNG GHI DỮ LIỆU ĐỂ PLAYSCREEN GỌI KHI QUA MÀN ---
    public void saveProgress() {
        Preferences prefs = Gdx.app.getPreferences(SAVE_PREFS_NAME);
        for (int i = 0; i < completedLevelsReal.length; i++) {
            prefs.putInteger("map_" + (i + 1), completedLevelsReal[i]);
        }
        prefs.flush(); // Bắt buộc thực hiện ghi vật lý xuống file lưu trữ
        Gdx.app.log("SaveSystem", "Đã tự động lưu tiến trình chơi mới vào ổ cứng.");
    }

    // --- HÀM ĐIỀU KHIỂN VẬT LÝ CHO NHẠC NỀN ---
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

    // --- HÀM ĐIỀU KHIỂN VẬT LÝ CHO HIỆU ỨNG ÂM THANH (SFX) ---
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

        // --- GIẢI PHÓNG 10 TEXTURE ẢNH SỐ KHI ĐÓNG GAME ---
        if (numberTextures != null) {
            for (Texture t : numberTextures) {
                if (t != null) t.dispose();
            }
        }

        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
    }
}
