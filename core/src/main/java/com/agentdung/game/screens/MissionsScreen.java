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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MissionsScreen extends ScreenAdapter {
    private final AgentDungGame game;
    private Stage stage;

    private Texture bgTexture;
    private Texture backButtonTexture;
    private Texture mapTagTexture;
    private Texture progressTexture;
    private Texture perTexture;
    private Texture titleMissionsTexture; // Thêm texture cho tiêu đề Missions

    private Texture[] mapTitleTextures;
    private final String[] mapNames = {"UI_Ronin", "UI_Nostra", "UI_Sinal", "UI_Gumi", "UI_Cartel"};

    private final int[] completedLevels = {10, 3, 8, 4, 7};
    private final int[] totalLevels = {10, 10, 10, 10, 10};

    public MissionsScreen(AgentDungGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        bgTexture = new Texture(Gdx.files.internal("ui/UI_background.png"));
        backButtonTexture = new Texture(Gdx.files.internal("ui/UI_arrow_left.png"));
        mapTagTexture = new Texture(Gdx.files.internal("ui/UI_maptag.png"));
        progressTexture = new Texture(Gdx.files.internal("ui/UI_progress.png"));
        perTexture = new Texture(Gdx.files.internal("ui/UI_per.png"));
        titleMissionsTexture = new Texture(Gdx.files.internal("ui/UI_title_mission.png")); // Load tiêu đề

        mapTitleTextures = new Texture[5];
        for (int i = 0; i < 5; i++) {
            mapTitleTextures[i] = new Texture(Gdx.files.internal("ui/" + mapNames[i] + ".png"));
        }

        Image background = new Image(bgTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        // Nút quay lại góc trái trên
        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(backButtonTexture));
        ImageButton backButton = new ImageButton(backDrawable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.clickSound != null) game.clickSound.play();
                game.setScreen(new MenuScreen(game));
            }
        });

        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left();
        topLeftTable.add(backButton).size(40f, 40f).padTop(15f).padLeft(15f);
        stage.addActor(topLeftTable);

        // --- TẠO DANH SÁCH CUỘN BẰNG SCROLLPANE ---
        Table scrollTable = new Table();

        float tagWidth = 550f;
        float tagHeight = 70f;

        for (int i = 0; i < 5; i++) {
            final int mapIndex = i + 1;

            Stack mapStack = new Stack();

            // LỚP 1 (DƯỚI CÙNG): Thanh maptag nền thực tế co giãn bằng NinePatch qua cơ chế Table Background
            // Bạn có thể chỉnh lại 4 thông số 10, 10, 10, 10 cho đúng với số lượng pixel bo góc của file UI_maptag.png
            com.badlogic.gdx.graphics.g2d.NinePatch mapTagPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(mapTagTexture, 10, 10, 10, 10);
            com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable patchDrawable = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(mapTagPatch);

            // Tạo một Table đại diện cho nền, ép kích thước vật lý và gán ảnh nền co giãn vào
            Table bgTable = new Table();
            bgTable.setBackground(patchDrawable);
            mapStack.add(bgTable);

            // Tạo một ImageButton hoàn toàn trong suốt đặt đè lên trên lớp nền để bắt sự kiện click
            ImageButton mapTagButton = new ImageButton(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable());
            mapTagButton.setStyle(new ImageButton.ImageButtonStyle());
            mapStack.add(mapTagButton);

            mapTagButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.clickSound != null) game.clickSound.play();
                    System.out.println("Vào chơi Map " + mapIndex);
                    game.setScreen(new PlayScreen(game, mapIndex));
                }
            });

            // LỚP 2 (Ở GIỮA): Chữ và Số tiến độ
            Table infoTable = new Table();
            infoTable.setFillParent(false);

            Image titleImage = new Image(mapTitleTextures[i]);
            infoTable.add(titleImage).left().padLeft(30f).expandX();

            Table progressTextTable = new Table();
            addNumberImagesToTable(progressTextTable, completedLevels[i]);
            progressTextTable.add(new Image(perTexture)).pad(0, 1, 0, 1);
            addNumberImagesToTable(progressTextTable, totalLevels[i]);
            infoTable.add(progressTextTable).right().padRight(30f);

            // Tự do chỉnh khoảng cách chữ so với đáy maptag thực tế tại đây
            infoTable.padBottom(10f);
            mapStack.add(infoTable);

            // LỚP 3 (TRÊN CÙNG): Thanh UI_progress được đưa vào khung bao
            Table progressTableWrapper = new Table();
            progressTableWrapper.bottom().left(); // Định vị vùng chứa sát đáy trái

            Image progressImage = new Image(progressTexture);
            float ratio = (float) completedLevels[i] / totalLevels[i];
            float progressWidth = tagWidth * ratio;

            // Tự do chỉnh khoảng cách thanh màu vàng so với đáy maptag thực tế tại đây
            progressTableWrapper.add(progressImage)
                .width(progressWidth)
                .height(8f)
                .left()
                .padLeft(4f)
                .padBottom(12f);

            mapStack.add(progressTableWrapper);

            // Thêm vào bảng cuộn
            scrollTable.add(mapStack).size(tagWidth, tagHeight).padBottom(12f);
            scrollTable.row();
        }

        // Tạo cấu trúc cuộn chuột cho danh sách map
        ScrollPane scrollPane = new ScrollPane(scrollTable);
        scrollPane.setScrollingDisabled(true, false); // Khóa cuộn ngang, mở cuộn dọc
        scrollPane.setFadeScrollBars(false);          // Để thanh cuộn luôn hiện rõ

        // Bảng chính chứa cả chữ Missions và ScrollPane danh sách chọn map
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        // Thêm chữ tiêu đề "Missions" vào trên cùng
        Image missionsTitle = new Image(titleMissionsTexture);
        mainTable.add(missionsTitle).size(400f, 90f).padBottom(20f);
        mainTable.row();

        // Thêm khung cuộn ScrollPane ngay phía dưới chữ tiêu đề
        mainTable.add(scrollPane).size(tagWidth + 40f, 290f);

        stage.addActor(mainTable);

        // 🔥 FIX QUAN TRỌNG NHẤT Ở ĐÂY:
        // Ép hệ thống tập trung sự kiện cuộn chuột (Scroll Focus) vào chiếc ScrollPane này ngay khi mở trang.
        stage.setScrollFocus(scrollPane);
    }

    private void addNumberImagesToTable(Table table, int number) {
        String numStr = String.valueOf(number);
        for (int i = 0; i < numStr.length(); i++) {
            char digit = numStr.charAt(i);
            Texture digitTexture = new Texture(Gdx.files.internal("ui/UI_number" + digit + ".png"));
            Image digitImage = new Image(digitTexture);
            if (i < numStr.length() - 1) {
                table.add(digitImage).padRight(3f);
            } else {
                table.add(digitImage);
            }
        }
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
        if (backButtonTexture != null) backButtonTexture.dispose();
        if (mapTagTexture != null) mapTagTexture.dispose();
        if (progressTexture != null) progressTexture.dispose();
        if (perTexture != null) perTexture.dispose();
        if (titleMissionsTexture != null) titleMissionsTexture.dispose(); // Giải phóng bộ nhớ tiêu đề

        if (mapTitleTextures != null) {
            for (Texture t : mapTitleTextures) {
                if (t != null) t.dispose();
            }
        }
    }
}
