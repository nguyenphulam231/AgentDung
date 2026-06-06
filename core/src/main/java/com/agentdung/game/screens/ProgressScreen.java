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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ProgressScreen extends ScreenAdapter {
    private final AgentDungGame game;
    private final int worldId;

    private Stage stage;
    private Texture backgroundTexture;
    private Texture backButtonTexture;
    private Texture titleTexture; // Thêm biến lưu Texture tiêu đề chữ Progress

    private Texture[] levelTextures;

    public ProgressScreen(AgentDungGame game, int worldId) {
        this.game = game;
        this.worldId = worldId;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Đổi sang nền UI_frame_general đồng bộ với các màn hình khác
        backgroundTexture = new Texture(Gdx.files.internal("ui/UI_frame_general.png"));
        Image bgImage = new Image(backgroundTexture);
        bgImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(bgImage);

        // --- NẠP ẢNH TIÊU ĐỀ "PROGRESS" ---
        titleTexture = new Texture(Gdx.files.internal("ui/UI_title_progress.png"));
        titleTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Image titleImage = new Image(titleTexture);
        // Bạn có thể tùy chỉnh size ảnh tiêu đề ở đây nếu muốn (Ví dụ: 200x50)
        // titleImage.setSize(200f, 50f);

        // 2. Nút quay lại (Back Button) ở góc trái trên
        backButtonTexture = new Texture(Gdx.files.internal("ui/UI_arrow_left.png"));
        backButtonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(backButtonTexture));
        ImageButton backButton = new ImageButton(backDrawable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) {
                    game.clickSound.play();
                }
                game.setScreen(new MissionsScreen(game));
            }
        });

        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left();
        topLeftTable.add(backButton).size(40f, 40f).padTop(15f).padLeft(15f);
        stage.addActor(topLeftTable);

        // 3. Lấy thông tin số lượng và tiến trình từ Game Core
        int totalLevels = game.totalLevelsReal[worldId - 1];
        int completedLevels = game.completedLevelsReal[worldId - 1];

        levelTextures = new Texture[totalLevels];

        // 4. Tạo một bảng chứa danh sách các thanh Level xếp dọc
        Table containerTable = new Table();
        containerTable.top();

        float itemWidth = 530f;
        float itemHeight = 70f;

        for (int i = 1; i <= totalLevels; i++) {
            final int levelNum = i;
            String path;

            if (i <= completedLevels) {
                path = "ui/UI_level_" + i + "_done.png";
            } else {
                path = "ui/UI_level_" + i + "_notdone.png";
            }

            Texture levelTex = new Texture(Gdx.files.internal(path));
            levelTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            levelTextures[i - 1] = levelTex;

            Image levelButton = new Image(new TextureRegionDrawable(new TextureRegion(levelTex)));

            levelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.isMasterOn && game.isSfxOn && game.clickSound != null) {
                        game.clickSound.play();
                    }
                    game.setScreen(new PlayScreen(game, worldId, levelNum));
                }
            });

            containerTable.add(levelButton).width(itemWidth).height(itemHeight).padBottom(15f);
            containerTable.row();
        }

        // 5. Bọc bảng danh sách vào một ô cuộn ScrollPane
        ScrollPane scrollPane = new ScrollPane(containerTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        // 6. Cấu trúc lại Table chính lồng ghép tiêu đề và ScrollPane theo trục dọc
        Table mainLayout = new Table();
        mainLayout.setFillParent(true);
        mainLayout.top(); // Neo từ trên đỉnh xuống để dễ căn khoảng cách padding

        // Đặt ảnh chữ tiêu đề lên hàng đầu tiên
        mainLayout.add(titleImage).size(420f, 90f).padTop(60f).padBottom(15f).center();
        mainLayout.row(); // Xuống hàng để đặt vùng cuộn ngay bên dưới

        // Đặt vùng cuộn chứa danh sách màn chơi
        mainLayout.add(scrollPane).width(550f).height(280f).center();

        stage.addActor(mainLayout);

        // Ưu tiên tiêu điểm cuộn chuột
        stage.setScrollFocus(scrollPane);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (game.isMasterOn && game.isSfxOn && game.clickSound != null) {
                game.clickSound.play();
            }
            game.setScreen(new MissionsScreen(game));
        }
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
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (backButtonTexture != null) backButtonTexture.dispose();
        if (titleTexture != null) titleTexture.dispose(); // Hủy bộ nhớ texture tiêu đề
        if (levelTextures != null) {
            for (Texture tex : levelTextures) {
                if (tex != null) tex.dispose();
            }
        }
    }
}
