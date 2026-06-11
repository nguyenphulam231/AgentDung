package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen extends ScreenAdapter {
    private final AgentDungGame game;
    private Stage stage;
    private BitmapFont font;

    public MenuScreen(AgentDungGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        // Nền
        Image background = new Image(game.assets.getMenuBg());
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        // Nút bấm chính
        ImageButton playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(game.assets.getBtnPlay())));
        ImageButton customizeButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(game.assets.getBtnCustomize())));

        // Nút nhỏ
        ImageButton wikiButton = createSmallButton(game.assets.getBtnWiki());
        ImageButton guideButton = createSmallButton(game.assets.getBtnGuide());
        ImageButton settingsButton = createSmallButton(game.assets.getBtnSettings());


        playButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.assets.getClickSound().play();
                game.setScreen(new MissionsScreen(game));
            }
        });

        customizeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.assets.getClickSound().play();
                game.setScreen(new CustomizeScreen(game));
            }
        });

        wikiButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.assets.getClickSound().play();
                game.setScreen(new WikiScreen(game));
            }
        });

        guideButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.assets.getClickSound().play();
                game.setScreen(new GuideScreen(game));
            }
        });

        settingsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.assets.getClickSound().play();
                game.setScreen(new SettingsScreen(game));
            }
        });

        // Bảng UI
        Table coinTable = new Table();
        coinTable.top().right();
        coinTable.setFillParent(true);
        coinTable.padTop(20).padRight(20);
        coinTable.add(new Image(game.assets.getCoinTex())).size(40, 40).padRight(10);
        coinTable.add(new Label(String.valueOf(game.globalCoinCount), new Label.LabelStyle(font, Color.GOLD)));
        stage.addActor(coinTable);

        Table table = new Table();
        table.setFillParent(true);
        table.add(new Image(game.assets.getBtnTitle())).size(400f, 100f).padBottom(30f);
        table.row();
        table.add(playButton).padBottom(15f);
        table.row();
        table.add(customizeButton);
        stage.addActor(table);

        Table bottomRightTable = new Table();
        bottomRightTable.setFillParent(true);
        bottomRightTable.bottom().right();
        bottomRightTable.add(wikiButton).padRight(5f).padBottom(14f);
        bottomRightTable.add(guideButton).padRight(5f).padBottom(14f);
        bottomRightTable.add(settingsButton).padRight(10f).padBottom(14f);
        stage.addActor(bottomRightTable);
    }

    private ImageButton createSmallButton(com.badlogic.gdx.graphics.Texture tex) {
        ImageButton btn = new ImageButton(new TextureRegionDrawable(new TextureRegion(tex)));
        btn.getImageCell().size(70f, 70f);
        return btn;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (font != null) font.dispose();
        // Không còn dispose Texture ở đây vì GameAssets lo hết!
    }
}
