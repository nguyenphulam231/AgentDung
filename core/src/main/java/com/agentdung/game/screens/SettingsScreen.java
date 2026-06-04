package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsScreen extends ScreenAdapter {
    private final AgentDungGame game;
    private Stage stage;

    private Texture settingsBgTexture;
    private Texture backButtonTexture;

    // Khai báo các Texture âm thanh và nhạc
    private Texture soundOnTexture;
    private Texture soundOffTexture;
    private Texture musicOnTexture;
    private Texture musicOffTexture;

    public SettingsScreen(AgentDungGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Nạp file ảnh từ assets
        settingsBgTexture = new Texture(Gdx.files.internal("ui/UI_frame_settings.png"));
        backButtonTexture = new Texture(Gdx.files.internal("ui/UI_arrow_left.png"));

        soundOnTexture = new Texture(Gdx.files.internal("ui/UI_soundon.png"));
        soundOffTexture = new Texture(Gdx.files.internal("ui/UI_soundoff.png"));
        musicOnTexture = new Texture(Gdx.files.internal("ui/UI_musicon.png"));
        musicOffTexture = new Texture(Gdx.files.internal("ui/UI_musicoff.png"));

        // 2. Tạo ảnh nền cài đặt full màn hình
        Image settingsBg = new Image(settingsBgTexture);
        settingsBg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(settingsBg);

        // --- KHỞI TẠO CÁC NÚT BẤM ---

        // Nút Quay lại (Back)
        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(backButtonTexture));
        ImageButton backButton = new ImageButton(backDrawable);

        // Chuyển đổi các Texture On/Off sang dạng Drawable để nạp vào nút bấm dạng Toggle (Bật/Tắt)
        TextureRegionDrawable soundOn = new TextureRegionDrawable(new TextureRegion(soundOnTexture));
        TextureRegionDrawable soundOff = new TextureRegionDrawable(new TextureRegion(soundOffTexture));
        TextureRegionDrawable musicOn = new TextureRegionDrawable(new TextureRegion(musicOnTexture));
        TextureRegionDrawable musicOff = new TextureRegionDrawable(new TextureRegion(musicOffTexture));

        // Khởi tạo ImageButton với cấu trúc: ImageButton(ImageUp, ImageDown, ImageChecked)
        final ImageButton masterButton = new ImageButton(soundOn, soundOn, soundOff);
        final ImageButton sfxButton = new ImageButton(soundOn, soundOn, soundOff);
        final ImageButton musicButton = new ImageButton(musicOn, musicOn, musicOff);

        // --- ĐÃ KẾT NỐI: Đặt trạng thái ban đầu dựa theo biến cấu hình hệ thống lưu trong lớp Game ---
        masterButton.setChecked(!game.isMasterOn);
        sfxButton.setChecked(!game.isSfxOn);
        musicButton.setChecked(!game.isMusicOn);

        float bigbtn = 85f;
        float btnSize = 60f;
        masterButton.setSize(bigbtn, bigbtn);
        sfxButton.setSize(btnSize, btnSize);
        musicButton.setSize(btnSize, btnSize);

        // --- ĐỊNH VỊ VỊ TRÍ THỦ CÔNG ĐỂ KHỚP VỚI HÌNH NỀN --

        // 1. Định vị nút Master
        float masterX = Gdx.graphics.getWidth() * 0.30f;
        float masterY = Gdx.graphics.getHeight() * 0.53f;
        masterButton.setPosition(masterX, masterY);

        // 2. Định vị nút SFX
        float sfxX = Gdx.graphics.getWidth() * 0.42f;
        float sfxY = Gdx.graphics.getHeight() * 0.31f;
        sfxButton.setPosition(sfxX, sfxY);

        // 3. Định vị nút Music
        float musicX = Gdx.graphics.getWidth() * 0.42f;
        float musicY = Gdx.graphics.getHeight() * 0.10f;
        musicButton.setPosition(musicX, musicY);

        // --- GẮN SỰ KIỆN CLICK VÀ LOGIC BẬT/TẮT VẬT LÝ ---

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Chỉ phát tiếng click nếu SFX và Master đang bật
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) {
                    game.clickSound.play();
                }
                game.setScreen(new MenuScreen(game));
            }
        });

        masterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cập nhật trạng thái vào Core Game
                game.isMasterOn = !masterButton.isChecked();

                // Thực hiện thay đổi vật lý cho Music và SFX tổng
                game.updateMusicState();
                game.updateSfxState();

                // Chỉ phát tiếng click phản hồi nếu Master vừa được BẬT lên
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) {
                    game.clickSound.play();
                }
                System.out.println("Trạng thái Master Audio chạy thật: " + (game.isMasterOn ? "BẬT" : "TẮT"));
            }
        });

        sfxButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cập nhật trạng thái vào Core Game
                game.isSfxOn = !sfxButton.isChecked();

                // Áp dụng thay đổi (ví dụ dừng âm thanh loop nếu vừa tắt SFX)
                game.updateSfxState();

                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) {
                    game.clickSound.play();
                }
                System.out.println("Trạng thái SFX chạy thật: " + (game.isSfxOn ? "BẬT" : "TẮT"));
            }
        });

        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cập nhật trạng thái vào Core Game
                game.isMusicOn = !musicButton.isChecked();

                // Thực hiện pause/play nhạc nền ngay lập tức
                game.updateMusicState();

                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) {
                    game.clickSound.play();
                }
                System.out.println("Trạng thái Music chạy thật: " + (game.isMusicOn ? "BẬT" : "TẮT"));
            }
        });

        // --- ĐƯA CÁC THÀNH PHẦN VÀO STAGE ---

        // Thêm nút Quay lại vào góc trên bên trái thông qua Table
        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left();
        topLeftTable.add(backButton).size(40f, 40f).padTop(15f).padLeft(15f);
        stage.addActor(topLeftTable);

        // Thêm trực tiếp 3 nút bật tắt âm thanh vào stage để kiểm soát tọa độ X, Y tuyệt đối
        stage.addActor(masterButton);
        stage.addActor(sfxButton);
        stage.addActor(musicButton);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (settingsBgTexture != null) settingsBgTexture.dispose();
        if (backButtonTexture != null) backButtonTexture.dispose();

        if (soundOnTexture != null) soundOnTexture.dispose();
        if (soundOffTexture != null) soundOffTexture.dispose();
        if (musicOnTexture != null) musicOnTexture.dispose();
        if (musicOffTexture != null) musicOffTexture.dispose();
    }
}
