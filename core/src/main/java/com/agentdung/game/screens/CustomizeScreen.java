package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
    private Texture slotBgTexture;
    private Texture backButtonTexture;

    private Label selectionLabel;
    private BitmapFont font;

    public static class CharacterData {
        public String name;
        public int id;
        public String[] variants; // Mảng lưu tên biến thể riêng biệt của từng nhân vật

        public CharacterData(String name, int id, String[] variants) {
            this.name = name;
            this.id = id;
            this.variants = variants;
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

        // --- ĐỊNH NGHĨA TÊN BIẾN THỂ RIÊNG BIỆT CHO TỪNG NHÂN VẬT THEO ĐÚNG Ý BẠN ---
        characterList.add(new CharacterData("Agent Dung", 1,
            new String[]{"Classic", "Snow", "Desert", "Stealth", "VIP"}));

        characterList.add(new CharacterData("Shadow Agent", 2,
            new String[]{"Ninja", "Assassin", "Ghost", "Midnight", "Eclipse"}));

        characterList.add(new CharacterData("Cyber Soldier", 3,
            new String[]{"Matrix", "Neon", "Mecha", "Quantum", "Overdrive"}));

        characterList.add(new CharacterData("Ghost Fighter", 4,
            new String[]{"Phantom", "Specter", "Wraith", "Haunted", "Spirit"}));

        characterList.add(new CharacterData("Medic Agent", 5,
            new String[]{"Doctor", "Biohazard", "Vaccine", "Nanotech", "FirstAid"}));

        characterList.add(new CharacterData("Heavy Gunner", 6,
            new String[]{"Commando", "Juggernaut", "Doomsday", "Wasteland", "Vulcan"}));
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // --- CẤU HÌNH FONT CHỮ CHUẨN ĐỒ HỌA PIXEL ART SẮC NÉT ---
        font = new BitmapFont();
        font.getData().setScale(1.8f); // Phóng to font chữ lên một chút cho rõ ràng

        // Mẹo LibGDX: Bật vẽ tọa độ nguyên (Integer) giúp font chữ vuông vức, không bị mờ nhòe răng cưa pixel
        font.setUseIntegerPositions(true);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // 1. Load Background Frame cho Customize
        bgTexture = new Texture(Gdx.files.internal("ui/UI_frame_customize.png"));
        Image background = new Image(bgTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        slotBgTexture = new Texture(Gdx.files.internal("ui/UI_slot_background.png"));
        slotBgTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // --- NÚT QUAY LẠI ---
        backButtonTexture = new Texture(Gdx.files.internal("ui/UI_arrow_left.png"));
        backButtonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(backButtonTexture));
        backDrawable.setMinWidth(50f);
        backDrawable.setMinHeight(50f);

        ImageButton backButton = new ImageButton(backDrawable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                game.setScreen(new MenuScreen(game));
            }
        });

        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left();
        topLeftTable.add(backButton).padTop(15f).padLeft(15f);
        stage.addActor(topLeftTable);

        // 2. Tạo Table chứa danh sách cuộn nhân vật
        Table scrollTable = new Table();
        scrollTable.top().left();

        float slotSize = 70f;

        for (CharacterData character : characterList) {
            Table rowTable = new Table();
            rowTable.left();

            for (int i = 0; i < character.variants.length; i++) {
                final int variantId = i + 1;
                final String variantName = character.variants[i]; // Lấy chuẩn tên biến thể riêng biệt của nhân vật này

                String fileName = "thumbnails/thumbnail_player_" + character.id + "_" + variantId + ".png";

                try {
                    Texture slotTex = new Texture(Gdx.files.internal(fileName));
                    slotTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                    loadedTextures.add(slotTex);

                    TextureRegionDrawable baseStyle = new TextureRegionDrawable(new TextureRegion(slotBgTexture));
                    baseStyle.setMinWidth(slotSize);
                    baseStyle.setMinHeight(slotSize);

                    TextureRegionDrawable characterIcon = new TextureRegionDrawable(new TextureRegion(slotTex));
                    float targetCharSize = 60f;
                    characterIcon.setMinWidth(targetCharSize);
                    characterIcon.setMinHeight(targetCharSize);

                    ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
                    buttonStyle.up = baseStyle;
                    buttonStyle.imageUp = characterIcon;

                    ImageButton variantBtn = new ImageButton(buttonStyle);

                    final String charName = character.name;
                    final int charId = character.id;

                    variantBtn.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();

                            game.selectedCharacterId = charId;
                            game.selectedVariantId = variantId;

                            // Cập nhật text hiển thị chuẩn format mong muốn
                            selectionLabel.setText(charName + " - " + variantName);

                            System.out.println("Đã chọn: " + charName + " - Biến thể: " + variantName);
                        }
                    });

                    rowTable.add(variantBtn).size(slotSize).padRight(15f);

                } catch (Exception e) {
                    Gdx.app.error("CustomizeScreen", "Không tìm thấy file ảnh: " + fileName);
                }
            }

            scrollTable.add(rowTable).padBottom(20f).left();
            scrollTable.row();
        }

        // 3. Bọc bảng vào vùng cuộn dọc (Chiều cao thu gọn 260f)
        ScrollPane scrollPane = new ScrollPane(scrollTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        // 4. Khởi tạo Label hiển thị thông tin chữ (ĐÃ ĐỔI SANG MÀU XANH LÁ - Color.GREEN)
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.GREEN);

        // Lấy tên mặc định ban đầu của Agent Dung và biến thể thứ nhất
        selectionLabel = new Label(characterList.get(0).name + " - " + characterList.get(0).variants[0], labelStyle);
        selectionLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

        // 5. Định vị Table chính
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        mainTable.add(scrollPane).size(450f, 260f).padTop(110f).center();
        mainTable.row();

        // Đẩy dòng chữ màu xanh lá nằm gọn ngay dưới bảng cuộn
        mainTable.add(selectionLabel).padTop(25f).center();

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
        if (backButtonTexture != null) backButtonTexture.dispose();
        if (font != null) font.dispose();

        for (Texture tex : loadedTextures) {
            if (tex != null) tex.dispose();
        }
    }
}
