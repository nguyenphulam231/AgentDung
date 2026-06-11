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
    private BitmapFont font;
    private Label selectionLabel;

    public static class CharacterData {
        public String name;
        public int id;
        public String[] variants;

        public CharacterData(String name, int id, String[] variants) {
            this.name = name;
            this.id = id;
            this.variants = variants;
        }
    }

    private Array<CharacterData> characterList;

    public CustomizeScreen(AgentDungGame game) {
        this.game = game;
        initCharacterData();
        // Assets đã được load từ AgentDungGame, không cần load lại
    }

    private void initCharacterData() {
        characterList = new Array<>();

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

        // --- CẤU HÌNH FONT CHỮ ---
        font = new BitmapFont();
        font.getData().setScale(1.8f);
        font.setUseIntegerPositions(true);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // --- BACKGROUND ---
        Image background = new Image(game.assets.getCustomizeBg());
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        // --- TIÊU ĐỀ ---
        Image titleImage = new Image(game.assets.getTitleCustomize());

        // --- NÚT QUAY LẠI ---
        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(game.assets.getBackBtn()));
        backDrawable.setMinWidth(50f);
        backDrawable.setMinHeight(50f);

        ImageButton backButton = new ImageButton(backDrawable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null) game.assets.getClickSound().play();
                game.setScreen(new MenuScreen(game));
            }
        });

        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left();
        topLeftTable.add(backButton).padTop(15f).padLeft(15f);
        stage.addActor(topLeftTable);

        // --- BẢNG DANH SÁCH NHÂN VẬT ---
        Table scrollTable = new Table();
        scrollTable.top().left();

        float slotSize = 70f;

        for (CharacterData character : characterList) {
            Table rowTable = new Table();
            rowTable.left();

            for (int i = 0; i < character.variants.length; i++) {
                final int variantId = i + 1;
                final String variantName = character.variants[i];

                Texture slotTex = game.assets.getThumbnail(character.id, variantId);

                TextureRegionDrawable baseStyle = new TextureRegionDrawable(new TextureRegion(game.assets.getSlotBg()));
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
                        if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null) game.assets.getClickSound().play();

                        game.selectedCharacterId = charId;
                        game.selectedVariantId = variantId;

                        selectionLabel.setText(charName + " - " + variantName);

                        System.out.println("Character: " + charName + " - Variant: " + variantName);
                    }
                });

                rowTable.add(variantBtn).size(slotSize).padRight(15f);
            }

            scrollTable.add(rowTable).padBottom(20f).left();
            scrollTable.row();
        }

        // --- VÙNG CUỘN ---
        ScrollPane scrollPane = new ScrollPane(scrollTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        // --- LABEL HIỂN THỊ NHÂN VẬT ĐANG CHỌN ---
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.GREEN);
        selectionLabel = new Label(characterList.get(0).name + " - " + characterList.get(0).variants[0], labelStyle);
        selectionLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

        // --- LAYOUT CHÍNH ---
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        mainTable.add(titleImage).size(420f, 90f).padTop(60f).padBottom(15f).center();
        mainTable.row();
        mainTable.add(scrollPane).size(450f, 260f).center();
        mainTable.row();
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
        // Không dispose texture ở đây vì tất cả đã được GameAssets quản lý
        // Chỉ dispose những gì CustomizeScreen tự tạo ra
        if (stage != null) stage.dispose();
        if (font != null) font.dispose();
    }
}
