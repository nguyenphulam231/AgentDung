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
    private final int worldId; // Map hiện tại được chọn (1 -> 5)

    private Stage stage;
    private Texture backgroundTexture;
    private Texture backButtonTexture;

    // Mảng lưu trữ các texture của level để quản lý bộ nhớ
    private Texture[] levelTextures;

    public ProgressScreen(AgentDungGame game, int worldId) {
        this.game = game;
        this.worldId = worldId;
    }

    @Override
    public void show() {
        // --- ĐỒNG BỘ CHẠY THẬT: Sử dụng ScreenViewport y hệt MissionsScreen ---
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Nạp ảnh nền khung Progress và ép giãn tràn viền toàn màn hình hiển thị
        backgroundTexture = new Texture(Gdx.files.internal("ui/UI_frame_progress.png"));
        Image bgImage = new Image(backgroundTexture);
        bgImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(bgImage);

        // 2. Nút quay lại (Back Button) ở góc trái trên
        backButtonTexture = new Texture(Gdx.files.internal("ui/UI_arrow_left.png"));
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

        // Tạo bảng riêng biệt neo cố định ở góc TOP - LEFT của màn hình
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
        containerTable.top(); // Xếp các phần tử từ trên cùng xuống

        // Định kích thước chuẩn cho các thanh Level dài
        float itemWidth = 530f;
        float itemHeight = 70f;

        for (int i = 1; i <= totalLevels; i++) {
            final int levelNum = i;
            String path;

            // Logic mở khóa: hiển thị trạng thái done cho các màn đã qua hoặc đang mở khóa
            if (i <= completedLevels) {
                path = "ui/UI_level_" + i + "_done.png";
            } else {
                path = "ui/UI_level_" + i + "_notdone.png";
            }

            Texture levelTex = new Texture(Gdx.files.internal(path));
            levelTextures[i - 1] = levelTex;

            Image levelButton = new Image(new TextureRegionDrawable(new TextureRegion(levelTex)));

            // Xử lý sự kiện click vào thanh level để bắt đầu vào màn chơi
            levelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.isMasterOn && game.isSfxOn && game.clickSound != null) {
                        game.clickSound.play();
                    }
                    // Chuyển sang màn chơi thực tế, truyền Map (worldId) và đúng số Level (levelNum)
                    game.setScreen(new PlayScreen(game, worldId, levelNum));
                }
            });

            // Thêm nút vào bảng, thiết lập kích thước và khoảng cách đệm phía dưới giữa các thanh
            containerTable.add(levelButton).width(itemWidth).height(itemHeight).padBottom(15f);
            containerTable.row(); // Xuống dòng cho level kế tiếp
        }

        // 5. Bọc bảng danh sách vào một ô cuộn ScrollPane
        ScrollPane scrollPane = new ScrollPane(containerTable);
        scrollPane.setScrollingDisabled(true, false); // Khóa cuộn ngang, chỉ cuộn dọc
        scrollPane.setFadeScrollBars(false); // Giữ thanh cuộn luôn hiển thị

        // 6. Định vị vùng cuộn nằm gọn bên trong trung tâm của màn hình
        Table mainLayout = new Table();
        mainLayout.setFillParent(true);
        mainLayout.center();
        mainLayout.add(scrollPane).width(550f).height(280f).padTop(50f);

        stage.addActor(mainLayout);

        // Ưu tiên tiêu điểm cuộn chuột cho danh sách này giống hệt MissionsScreen
        stage.setScrollFocus(scrollPane);
    }

    @Override
    public void render(float delta) {
        // Xóa sạch khung hình cũ với nền đen
        ScreenUtils.clear(0, 0, 0, 1);

        // Cập nhật trạng thái Logic và vẽ giao diện Stage
        stage.act(delta);
        stage.draw();

        // Hỗ trợ nút vật lý hệ thống
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
        if (levelTextures != null) {
            for (Texture tex : levelTextures) {
                if (tex != null) tex.dispose();
            }
        }
    }
}
