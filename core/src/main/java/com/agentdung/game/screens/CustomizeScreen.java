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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CustomizeScreen extends ScreenAdapter {
    private final AgentDungGame game;
    private Stage stage;
    private Texture bgTexture;
    private Texture slotBgTexture; // Texture làm nền ô vuông bo góc
    private Texture backButtonTexture; // Thêm Texture cho nút quay lại

    public static class CharacterData {
        public String name;
        public int id;
        public int variantCount;

        public CharacterData(String name, int id, int variantCount) {
            this.name = name;
            this.id = id;
            this.variantCount = variantCount;
        }
    }

    private Array<CharacterData> characterList;
    private Array<Texture> loadedTextures;

    public CustomizeScreen(AgentDungGame game) {
        this.game = game;
        this.loadedTextures = new Array<>();
        initCharacterData();
    }

    private void initCharacterData() {
        characterList = new Array<>();
        characterList.add(new CharacterData("Agent Dũng", 1, 5));
        characterList.add(new CharacterData("Shadow Agent", 2, 5));
        characterList.add(new CharacterData("Cyber Soldier", 3, 5));
        characterList.add(new CharacterData("Ghost Fighter", 4, 5));
        characterList.add(new CharacterData("Medic Agent", 5, 5));
        characterList.add(new CharacterData("Heavy Gunner", 6, 5));
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Load Background Frame cho Customize
        bgTexture = new Texture(Gdx.files.internal("ui/UI_frame_customize.png"));
        Image background = new Image(bgTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        // Load ảnh ô vuông bo góc dùng chung cho tất cả các nút slot
        slotBgTexture = new Texture(Gdx.files.internal("ui/UI_slot_background.png"));
        slotBgTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // --- THÊM NÚT QUAY LẠI (BACK BUTTON) Ở GÓC TRÁI TRÊN ---
        backButtonTexture = new Texture(Gdx.files.internal("ui/UI_arrow_left.png"));
        backButtonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(backButtonTexture));
        // Đặt kích thước hiển thị cho nút quay lại (Ví dụ: 50x50 pixel)
        backDrawable.setMinWidth(50f);
        backDrawable.setMinHeight(50f);

        ImageButton backButton = new ImageButton(backDrawable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.clickSound != null) game.clickSound.play();
                // Chuyển màn hình về lại MenuScreen
                game.setScreen(new MenuScreen(game));
            }
        });

        // Tạo bảng riêng cho nút Back để neo nó cố định ở góc trên bên trái màn hình
        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left();
        // Tạo khoảng cách (padding) so với mép rìa màn hình cho đẹp mắt
        topLeftTable.add(backButton).padTop(15f).padLeft(15f);
        stage.addActor(topLeftTable);
        // -----------------------------------------------------

        // 2. Tạo Table chứa danh sách cuộn
        Table scrollTable = new Table();
        scrollTable.top().left();

        // Định nghĩa kích thước ô vuông bo góc chứa nhân vật (Ví dụ: 70x70)
        float slotSize = 70f;

        for (CharacterData character : characterList) {
            Table rowTable = new Table();
            rowTable.left();

            for (int v = 1; v <= character.variantCount; v++) {
                String fileName = "thumbnails/thumbnail_player_" + character.id + "_" + v + ".png";

                try {
                    Texture slotTex = new Texture(Gdx.files.internal(fileName));
                    slotTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                    loadedTextures.add(slotTex);

                    // Tạo nền ô vuông bo góc cho nút bấm
                    TextureRegionDrawable baseStyle = new TextureRegionDrawable(new TextureRegion(slotBgTexture));
                    baseStyle.setMinWidth(slotSize);
                    baseStyle.setMinHeight(slotSize);

                    // Tạo ảnh nhân vật đè lên trên ô vuông
                    TextureRegionDrawable characterIcon = new TextureRegionDrawable(new TextureRegion(slotTex));

                    // Ép kích thước hiển thị vật lý cho ảnh nhân vật to lên
                    float targetCharSize = 60f;
                    characterIcon.setMinWidth(targetCharSize);
                    characterIcon.setMinHeight(targetCharSize);

                    // Khởi tạo ImageButtonStyle để lồng ảnh nhân vật nằm TRÊN nền ô vuông bo góc
                    ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
                    buttonStyle.up = baseStyle;           // Ảnh nền ô vuông trạng thái bình thường
                    buttonStyle.imageUp = characterIcon;  // Ảnh nhân vật nằm đè lên trên

                    ImageButton variantBtn = new ImageButton(buttonStyle);

                    final int charId = character.id;
                    final int variantId = v;
                    variantBtn.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (game.clickSound != null) game.clickSound.play();
                            System.out.println("Đã chọn nhân vật ID: " + charId + " - Biến thể: " + variantId);
                        }
                    });

                    // Thêm ô vuông vào hàng ngang
                    rowTable.add(variantBtn).size(slotSize).padRight(15f);

                } catch (Exception e) {
                    Gdx.app.error("CustomizeScreen", "Không tìm thấy file ảnh: " + fileName);
                }
            }

            // Ép cái cell chứa hàng này phải tự động dạt về lề TRÁI (.left()) của bảng cuộn chính
            scrollTable.add(rowTable).padBottom(20f).left();
            scrollTable.row();
        }

        // 3. Bọc bảng vào vùng cuộn dọc
        ScrollPane scrollPane = new ScrollPane(scrollTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        // 4. Định vị Table chính chứa ScrollPane nằm DƯỚI chữ Customize
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Đẩy Table lên trên cùng trước, sau đó dùng padTop để hạ xuống đúng vị trí mong muốn
        mainTable.top();

        // Lệnh này đảm bảo bản thân cái "Panel mặt nạ ảo" rộng 450px sẽ nằm chình ình giữa màn hình,
        // trong khi ruột bên trong nó đã được cấu hình dạt trái hoàn toàn ở trên!
        mainTable.add(scrollPane).size(450f, 350f).padTop(110f).center();

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
        if (slotBgTexture != null) slotBgTexture.dispose();
        if (backButtonTexture != null) backButtonTexture.dispose(); // Giải phóng Texture nút quay lại

        for (Texture tex : loadedTextures) {
            if (tex != null) tex.dispose();
        }
    }
}
