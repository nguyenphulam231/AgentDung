package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
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
    private Texture titleTexture; // Tiêu đề Settings

    // Texture của các nút bấm Toggle
    private Texture soundOnTexture;
    private Texture soundOffTexture;
    private Texture musicOnTexture;
    private Texture musicOffTexture;

    // Các Texture của nhãn chữ (Label dạng ảnh)
    private Texture textMasterTexture;
    private Texture textSfxTexture;
    private Texture textMusicTexture;

    public SettingsScreen(AgentDungGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Nạp file ảnh nền UI_frame_general
        settingsBgTexture = new Texture(Gdx.files.internal("ui/UI_frame_general.png"));
        Image settingsBg = new Image(settingsBgTexture);
        settingsBg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(settingsBg);

        // Nạp ảnh tiêu đề Settings
        titleTexture = new Texture(Gdx.files.internal("ui/UI_title_settings.png"));

        // Nạp ảnh các nút bấm hệ thống và ảnh nhãn chữ tương ứng
        backButtonTexture = new Texture(Gdx.files.internal("ui/UI_arrow_left.png"));
        soundOnTexture = new Texture(Gdx.files.internal("ui/UI_soundon.png"));
        soundOffTexture = new Texture(Gdx.files.internal("ui/UI_soundoff.png"));
        musicOnTexture = new Texture(Gdx.files.internal("ui/UI_musicon.png"));
        musicOffTexture = new Texture(Gdx.files.internal("ui/UI_musicoff.png"));

        textMasterTexture = new Texture(Gdx.files.internal("ui/UI_text_master.png"));
        textSfxTexture = new Texture(Gdx.files.internal("ui/UI_text_sfx.png"));
        textMusicTexture = new Texture(Gdx.files.internal("ui/UI_text_music.png"));

        // Khử mờ đồng loạt cho Pixel Art sắc nét
        settingsBgTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        titleTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        backButtonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        soundOnTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        soundOffTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        musicOnTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        musicOffTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        textMasterTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        textSfxTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        textMusicTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Tạo đối tượng hiển thị hình ảnh từ Texture
        Image titleImage = new Image(titleTexture);
        Image textMaster = new Image(textMasterTexture);
        Image textSfx = new Image(textSfxTexture);
        Image textMusic = new Image(textMusicTexture);

        // --- KHỞI TẠO CÁC NÚT BẤM VÀ ĐỔI KIỂU DRAWABLE ---
        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(backButtonTexture));
        ImageButton backButton = new ImageButton(backDrawable);

        // Chuyển đổi các Texture sang định dạng Drawable
        TextureRegionDrawable soundOn = new TextureRegionDrawable(new TextureRegion(soundOnTexture));
        TextureRegionDrawable soundOff = new TextureRegionDrawable(new TextureRegion(soundOffTexture));
        TextureRegionDrawable musicOn = new TextureRegionDrawable(new TextureRegion(musicOnTexture));
        TextureRegionDrawable musicOff = new TextureRegionDrawable(new TextureRegion(musicOffTexture));

        final ImageButton masterButton = new ImageButton(soundOn, soundOn, soundOff);
        final ImageButton sfxButton = new ImageButton(soundOn, soundOn, soundOff);
        final ImageButton musicButton = new ImageButton(musicOn, musicOn, musicOff);

        // Đồng bộ trạng thái lưu từ game core
        masterButton.setChecked(!game.isMasterOn);
        sfxButton.setChecked(!game.isSfxOn);
        musicButton.setChecked(!game.isMusicOn);

        // --- GẮN SỰ KIỆN CLICK CHUỘT LOGIC ---
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                game.setScreen(new MenuScreen(game));
            }
        });

        masterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.isMasterOn = !masterButton.isChecked();
                game.updateMusicState();
                game.updateSfxState();
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            }
        });

        sfxButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.isSfxOn = !sfxButton.isChecked();
                game.updateSfxState();
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            }
        });

        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.isMusicOn = !musicButton.isChecked();
                game.updateMusicState();
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
            }
        });

        // --- THIẾT KẾ GIAO DIỆN PHÂN CẤP THEO MẪU ---

        // Nút Back ở góc trên cùng bên trái
        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left();
        topLeftTable.add(backButton).size(40f, 40f).padTop(15f).padLeft(15f);
        stage.addActor(topLeftTable);

        // Bảng chứa danh sách tùy chọn đặt lệch phải
        Table optionsTable = new Table();
        optionsTable.left(); // Căn lề trái tổng thể cho bảng

        // Cấu hình thông số kích thước đúng như ảnh mẫu của bạn
        float btnMasterSize = 75f;  // Nút loa tổng lớn hẳn lên
        float btnSubSize = 50f;     // Nút SFX và Music nhỏ hơn ở dưới

        // Hàng 1: Master Audio (Nút lớn + Chữ lớn nằm sát lề trái của bảng)
        optionsTable.add(masterButton).size(btnMasterSize).padRight(15f).padBottom(15f);
        optionsTable.add(textMaster).size(150f, 45f).padBottom(15f).left(); // Chữ Master to hơn
        optionsTable.row();

        // Hàng 2: SFX Audio (Dịch phải bằng padLeft, nút bám sát chữ)
        optionsTable.add(sfxButton).size(btnSubSize).padLeft(45f).padRight(15f).padBottom(15f);
        optionsTable.add(textSfx).size(80f, 32f).padBottom(15f).left();
        optionsTable.row();

        // Hàng 3: Music Audio (Dịch phải đồng bộ với hàng SFX, nút bám sát chữ)
        optionsTable.add(musicButton).size(btnSubSize).padLeft(45f).padRight(15f);
        optionsTable.add(textMusic).size(100f, 32f).left();

        // Bảng bố cục chính tổng thể
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        // Đặt tiêu đề chữ "Settings" vừa vặn (ví dụ: rộng 180px, cao 45px), không lo bị tràn viền
        mainTable.add(titleImage).size(350f, 90f).padTop(60f).padBottom(25f).center();
        mainTable.row();

        // Chèn bảng tùy chọn âm thanh vào chính giữa khung hình dưới tiêu đề
        mainTable.add(optionsTable).center();

        stage.addActor(mainTable);
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
        if (titleTexture != null) titleTexture.dispose();
        if (backButtonTexture != null) backButtonTexture.dispose();

        if (soundOnTexture != null) soundOnTexture.dispose();
        if (soundOffTexture != null) soundOffTexture.dispose();
        if (musicOnTexture != null) musicOnTexture.dispose();
        if (musicOffTexture != null) musicOffTexture.dispose();

        if (textMasterTexture != null) textMasterTexture.dispose();
        if (textSfxTexture != null) textSfxTexture.dispose();
        if (textMusicTexture != null) textMusicTexture.dispose();
    }
}
