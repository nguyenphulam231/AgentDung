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

public class LevelDoneScreen extends ScreenAdapter {
    private final AgentDungGame game;
    private final int completedWorld;
    private final int completedLevel;

    private Stage stage;
    private Texture bgTexture;
    private Texture btnNextTexture;
    private Texture btnProgressTexture;
    private Texture btnMainTexture;
    private Texture titleTexture; // Thêm biến lưu Texture tiêu đề chữ Level Done

    public LevelDoneScreen(AgentDungGame game, int world, int level) {
        this.game = game;
        this.completedWorld = world;
        this.completedLevel = level;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Cập nhật nền thành UI_frame_general tràn màn hình đồng bộ với các menu khác
        bgTexture = new Texture(Gdx.files.internal("ui/UI_frame_general.png"));
        Image background = new Image(bgTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        // --- NẠP ẢNH TIÊU ĐỀ "LEVEL DONE" ---
        titleTexture = new Texture(Gdx.files.internal("ui/UI_title_leveldone.png"));
        titleTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Image titleImage = new Image(titleTexture);

        // Nạp ảnh các nút bấm từ assets
        btnNextTexture = new Texture(Gdx.files.internal("ui/UI_button_nextlevel.png"));
        btnProgressTexture = new Texture(Gdx.files.internal("ui/UI_button_progressmenu.png"));
        btnMainTexture = new Texture(Gdx.files.internal("ui/UI_button_mainmenu.png"));

        // Khử mờ cho Pixel Art
        bgTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnNextTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnProgressTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnMainTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // 3. Ép size cố định cho ảnh nút từ Drawable gốc
        float targetBtnWidth = 360f;
        float targetBtnHeight = 50f;

        TextureRegionDrawable nextDrawable = new TextureRegionDrawable(new TextureRegion(btnNextTexture));
        nextDrawable.setMinWidth(targetBtnWidth);
        nextDrawable.setMinHeight(targetBtnHeight);
        ImageButton nextButton = new ImageButton(nextDrawable);

        TextureRegionDrawable progressDrawable = new TextureRegionDrawable(new TextureRegion(btnProgressTexture));
        progressDrawable.setMinWidth(targetBtnWidth);
        progressDrawable.setMinHeight(targetBtnHeight);
        ImageButton progressButton = new ImageButton(progressDrawable);

        TextureRegionDrawable mainDrawable = new TextureRegionDrawable(new TextureRegion(btnMainTexture));
        mainDrawable.setMinWidth(targetBtnWidth);
        mainDrawable.setMinHeight(targetBtnHeight);
        ImageButton mainButton = new ImageButton(mainDrawable);

        // 4. Gắn sự kiện click chuột
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                if (completedLevel < game.totalLevelsReal[completedWorld - 1]) {
                    game.setScreen(new PlayScreen(game, completedWorld, completedLevel + 1));
                } else {
                    game.setScreen(new ProgressScreen(game, completedWorld));
                }
            }
        });

        progressButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                game.setScreen(new ProgressScreen(game, completedWorld));
            }
        });

        mainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                game.setScreen(new MenuScreen(game));
            }
        });

        // 5. Cấu trúc lại Table chính để xếp dọc từ trên xuống: Tiêu đề -> Các nút bấm
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top(); // Neo từ đỉnh xuống để đồng bộ khoảng cách với các màn hình khác

        // Thêm tiêu đề chữ "Level Done" lên hàng đầu
        mainTable.add(titleImage).size(420f, 90f).padTop(60f).padBottom(25f).center();
        mainTable.row();

        // Xếp lần lượt các nút thanh ngang nằm ngay dưới tiêu đề
        mainTable.add(nextButton).padBottom(20f);
        mainTable.row();
        mainTable.add(progressButton).padBottom(20f);
        mainTable.row();
        mainTable.add(mainButton);

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
        if (bgTexture != null) bgTexture.dispose();
        if (btnNextTexture != null) btnNextTexture.dispose();
        if (btnProgressTexture != null) btnProgressTexture.dispose();
        if (btnMainTexture != null) btnMainTexture.dispose();
        if (titleTexture != null) titleTexture.dispose(); // Giải phóng Texture tiêu đề tránh rò rỉ bộ nhớ
    }
}
