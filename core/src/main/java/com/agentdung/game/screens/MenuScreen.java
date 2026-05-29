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

public class MenuScreen extends ScreenAdapter {
    private final AgentDungGame game;

    private Stage stage;

    private Texture bgTexture;
    private Texture playButtonTexture;
    private Texture customizeButtonTexture;
    private Texture titleTexture;

    // Khai báo thêm Texture cho 2 nút mới
    private Texture guideButtonTexture;
    private Texture settingsButtonTexture;

    public MenuScreen(AgentDungGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        bgTexture = new Texture(Gdx.files.internal("ui/UI_background.png"));
        playButtonTexture = new Texture(Gdx.files.internal("ui/UI_button_play.png"));
        customizeButtonTexture = new Texture(Gdx.files.internal("ui/UI_button_customize.png"));
        titleTexture = new Texture(Gdx.files.internal("ui/UI_title_agentdung.png"));

        // Load ảnh cho nút Hướng dẫn và Cài đặt
        guideButtonTexture = new Texture(Gdx.files.internal("ui/UI_button_guide.png"));
        settingsButtonTexture = new Texture(Gdx.files.internal("ui/UI_button_settings.png"));

        Image background = new Image(bgTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        Image titleImage = new Image(titleTexture);

        TextureRegionDrawable playDrawable = new TextureRegionDrawable(new TextureRegion(playButtonTexture));
        ImageButton playButton = new ImageButton(playDrawable);

        TextureRegionDrawable customizeDrawable = new TextureRegionDrawable(new TextureRegion(customizeButtonTexture));
        ImageButton customizeButton = new ImageButton(customizeDrawable);

        // Tạo Drawable và ImageButton cho 2 nút mới
        TextureRegionDrawable guideDrawable = new TextureRegionDrawable(new TextureRegion(guideButtonTexture));
        ImageButton guideButton = new ImageButton(guideDrawable);

        TextureRegionDrawable settingsDrawable = new TextureRegionDrawable(new TextureRegion(settingsButtonTexture));
        ImageButton settingsButton = new ImageButton(settingsDrawable);

        // --- Gắn sự kiện click ---
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // THAY ĐỔI Ở ĐÂY: Chuyển sang màn hình chọn Missions thay vì vào thẳng PlayScreen
                game.setScreen(new MissionsScreen(game));
            }
        });

        customizeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Mở giao diện Customize cho Agent Dũng!");
            }
        });

        guideButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GuideScreen(game));
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game));
            }
        });

        // --- Bảng 1: Chứa Tiêu đề và các nút chính (Căn giữa) ---
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(titleImage).size(400f, 100f).padBottom(30f);
        table.row();

        table.add(playButton).padBottom(15f);
        table.row();

        table.add(customizeButton);
        stage.addActor(table);

        // --- Bảng 2: Chứa nút Guide và Settings (Đẩy về góc dưới bên phải) ---
        Table bottomRightTable = new Table();
        bottomRightTable.setFillParent(true);
        bottomRightTable.bottom().right(); // Đẩy toàn bộ nội dung bảng về góc phải dưới

        // Thêm nút Guide và Settings cạnh nhau, thêm khoảng cách (pad) cách lề dưới và lề phải là 20px
        // Giữa nút Guide và nút Settings cách nhau một khoảng bên phải (padRight) là 10px
        bottomRightTable.add(guideButton).padRight(5f).padBottom(14f);
        bottomRightTable.add(settingsButton).padRight(10f).padBottom(14f);

        stage.addActor(bottomRightTable);
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
        if (bgTexture != null) bgTexture.dispose();
        if (playButtonTexture != null) playButtonTexture.dispose();
        if (customizeButtonTexture != null) customizeButtonTexture.dispose();
        if (titleTexture != null) titleTexture.dispose();

        // Giải phóng bộ nhớ cho 2 nút mới để tránh tràn RAM
        if (guideButtonTexture != null) guideButtonTexture.dispose();
        if (settingsButtonTexture != null) settingsButtonTexture.dispose();
    }
}
