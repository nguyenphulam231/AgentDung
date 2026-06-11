package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
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

    public ProgressScreen(AgentDungGame game, int worldId) {
        this.game = game;
        this.worldId = worldId;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        int totalLevels = game.totalLevelsReal[worldId - 1];
        int completedLevels = game.completedLevelsReal[worldId - 1];

        // Load texture qua GameAssets
        game.assets.loadProgressAssets(worldId, totalLevels, completedLevels);

        Image bgImage = new Image(game.assets.getProgressBgTex());
        bgImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(bgImage);

        Image titleImage = new Image(game.assets.getProgressTitleTex());

        TextureRegionDrawable backDrawable = new TextureRegionDrawable(
            new TextureRegion(game.assets.getBackBtnTex()));
        ImageButton backButton = new ImageButton(backDrawable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null)
                    game.assets.getClickSound().play();
                game.setScreen(new MissionsScreen(game));
            }
        });

        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left();
        topLeftTable.add(backButton).size(40f, 40f).padTop(15f).padLeft(15f);
        stage.addActor(topLeftTable);

        Table containerTable = new Table();
        containerTable.top();

        float itemWidth = 530f;
        float itemHeight = 70f;

        for (int i = 1; i <= totalLevels; i++) {
            final int levelNum = i;
            boolean done = i <= completedLevels;

            Image levelButton = new Image(new TextureRegionDrawable(
                new TextureRegion(game.assets.getLevelButtonTex(i, done))));

            levelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null)
                        game.assets.getClickSound().play();
                    game.setScreen(new PlayScreen(game, worldId, levelNum));
                }
            });

            containerTable.add(levelButton).width(itemWidth).height(itemHeight).padBottom(15f);
            containerTable.row();
        }

        ScrollPane scrollPane = new ScrollPane(containerTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        Table mainLayout = new Table();
        mainLayout.setFillParent(true);
        mainLayout.top();
        mainLayout.add(titleImage).size(420f, 90f).padTop(60f).padBottom(15f).center();
        mainLayout.row();
        mainLayout.add(scrollPane).width(550f).height(280f).center();
        stage.addActor(mainLayout);

        stage.setScrollFocus(scrollPane);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null)
                game.assets.getClickSound().play();
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
        // Chỉ dispose stage — texture do GameAssets quản lý
        if (stage != null) stage.dispose();
    }
}
