package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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

    public LevelDoneScreen(AgentDungGame game, int world, int level) {
        this.game = game;
        this.completedWorld = world;
        this.completedLevel = level;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Background từ GameAssets
        Image background = new Image(game.assets.getLevelDoneBgTex());
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        // 2. Tiêu đề
        Image titleImage = new Image(game.assets.getTitleLevelDoneTex());

        // 3. Nút bấm sử dụng GameAssets
        float targetBtnWidth = 360f, targetBtnHeight = 50f;
        ImageButton nextButton = createButton(game.assets.getBtnNextTex(), targetBtnWidth, targetBtnHeight);
        ImageButton progressButton = createButton(game.assets.getBtnProgressTex(), targetBtnWidth, targetBtnHeight);
        ImageButton mainButton = createButton(game.assets.getBtnMainTex(), targetBtnWidth, targetBtnHeight);

        // Sự kiện Click
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
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
                playClick();
                game.setScreen(new ProgressScreen(game, completedWorld));
            }
        });

        mainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                game.setScreen(new MenuScreen(game));
            }
        });

        // 4. Bố cục
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        mainTable.add(titleImage).size(420f, 90f).padTop(60f).padBottom(25f).center();
        mainTable.row();
        mainTable.add(nextButton).padBottom(20f);
        mainTable.row();
        mainTable.add(progressButton).padBottom(20f);
        mainTable.row();
        mainTable.add(mainButton);
        stage.addActor(mainTable);
    }

    private ImageButton createButton(com.badlogic.gdx.graphics.Texture tex, float w, float h) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(tex));
        drawable.setMinWidth(w);
        drawable.setMinHeight(h);
        return new ImageButton(drawable);
    }

    private void playClick() {
        if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null)
            game.assets.getClickSound().play();
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
    }
}
