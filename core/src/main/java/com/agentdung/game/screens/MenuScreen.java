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
    private Texture guideButtonTexture;
    private Texture settingsButtonTexture;
    private Texture wikiButtonTexture; // --- THÊM MỚI ---

    private Texture coinTex;
    private BitmapFont font;

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
        guideButtonTexture = new Texture(Gdx.files.internal("ui/UI_button_guide.png"));
        settingsButtonTexture = new Texture(Gdx.files.internal("ui/UI_button_settings.png"));
        wikiButtonTexture = new Texture(Gdx.files.internal("ui/UI_button_wiki.png")); // --- THÊM MỚI ---

        coinTex = new Texture(Gdx.files.internal("images/coin.png"));
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        guideButtonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        settingsButtonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        wikiButtonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // --- THÊM MỚI ---

        Image background = new Image(bgTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        Image titleImage = new Image(titleTexture);
        ImageButton playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(playButtonTexture)));
        ImageButton customizeButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(customizeButtonTexture)));

        float targetBtnWidth = 70f;
        float targetBtnHeight = 70f;

        // Tạo các nút nhỏ
        ImageButton wikiButton = createSmallButton(wikiButtonTexture, targetBtnWidth, targetBtnHeight);
        ImageButton guideButton = createSmallButton(guideButtonTexture, targetBtnWidth, targetBtnHeight);
        ImageButton settingsButton = createSmallButton(settingsButtonTexture, targetBtnWidth, targetBtnHeight);

        // Gán sự kiện
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.assets.getClickSound() != null) game.assets.getClickSound().play();
                game.setScreen(new MissionsScreen(game));
            }
        });

        customizeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.assets.getClickSound() != null) game.assets.getClickSound().play();
                game.setScreen(new CustomizeScreen(game));
            }
        });

        wikiButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.assets.getClickSound() != null) game.assets.getClickSound().play();
                game.setScreen(new WikiScreen(game));
            }
        });

        guideButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.assets.getClickSound() != null) game.assets.getClickSound().play();
                game.setScreen(new GuideScreen(game));
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.assets.getClickSound() != null) game.assets.getClickSound().play();
                game.setScreen(new SettingsScreen(game));
            }
        });

        // Bảng xu
        Table coinTable = new Table();
        coinTable.top().right();
        coinTable.setFillParent(true);
        coinTable.padTop(20).padRight(20);
        coinTable.add(new Image(coinTex)).size(40, 40).padRight(10);
        coinTable.add(new Label(String.valueOf(game.globalCoinCount), new Label.LabelStyle(font, Color.GOLD)));
        stage.addActor(coinTable);

        // Bảng nút chính
        Table table = new Table();
        table.setFillParent(true);
        table.add(titleImage).size(400f, 100f).padBottom(30f);
        table.row();
        table.add(playButton).padBottom(15f);
        table.row();
        table.add(customizeButton);
        stage.addActor(table);

        // Bảng nút góc phải dưới (Wiki + Guide + Settings)
        Table bottomRightTable = new Table();
        bottomRightTable.setFillParent(true);
        bottomRightTable.bottom().right();
        bottomRightTable.add(wikiButton).padRight(5f).padBottom(14f);
        bottomRightTable.add(guideButton).padRight(5f).padBottom(14f);
        bottomRightTable.add(settingsButton).padRight(10f).padBottom(14f);
        stage.addActor(bottomRightTable);
    }

    private ImageButton createSmallButton(Texture tex, float w, float h) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(tex));
        drawable.setMinWidth(w);
        drawable.setMinHeight(h);
        return new ImageButton(drawable);
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
        if (guideButtonTexture != null) guideButtonTexture.dispose();
        if (settingsButtonTexture != null) settingsButtonTexture.dispose();
        if (wikiButtonTexture != null) wikiButtonTexture.dispose();
        if (coinTex != null) coinTex.dispose();
        if (font != null) font.dispose();
    }
}
