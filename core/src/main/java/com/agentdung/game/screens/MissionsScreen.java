package com.agentdung.game.screens;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MissionsScreen extends ScreenAdapter {
    private final AgentDungGame game;
    private Stage stage;

    public MissionsScreen(AgentDungGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);


        Image background = new Image(game.assets.getMissionBgTex());
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        // Nút quay lại
        TextureRegionDrawable backDrawable = new TextureRegionDrawable(
            new TextureRegion(game.assets.getBackBtnTex()));
        ImageButton backButton = new ImageButton(backDrawable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null)
                    game.assets.getClickSound().play();
                game.setScreen(new MenuScreen(game));
            }
        });

        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left();
        topLeftTable.add(backButton).size(40f, 40f).padTop(15f).padLeft(15f);
        stage.addActor(topLeftTable);

        Table scrollTable = new Table();

        float tagWidth = 550f;
        float tagHeight = 70f;

        for (int i = 0; i < 5; i++) {
            final int mapIndex = i + 1;

            int completed = game.completedLevelsReal[i];
            int total = game.totalLevelsReal[i];

            Stack mapStack = new Stack();

            NinePatch mapTagPatch = new NinePatch(game.assets.getMapTagTex(), 10, 10, 10, 10);
            Table bgTable = new Table();
            bgTable.setBackground(new NinePatchDrawable(mapTagPatch));
            mapStack.add(bgTable);

            ImageButton mapTagButton = new ImageButton(new TextureRegionDrawable());
            mapTagButton.setStyle(new ImageButton.ImageButtonStyle());
            mapStack.add(mapTagButton);

            mapTagButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null)
                        game.assets.getClickSound().play();
                    game.setScreen(new ProgressScreen(game, mapIndex));
                }
            });

            Table infoTable = new Table();
            Image titleImage = new Image(game.assets.getMapTitleTex(i));
            infoTable.add(titleImage).left().padLeft(30f).expandX();

            Table progressTextTable = new Table();
            addNumberImagesToTable(progressTextTable, completed);
            progressTextTable.add(new Image(game.assets.getPerTex())).pad(0, 1, 0, 1);
            addNumberImagesToTable(progressTextTable, total);
            infoTable.add(progressTextTable).right().padRight(30f);
            infoTable.padBottom(10f);
            mapStack.add(infoTable);

            float ratio = (float) completed / total;
            float progressWidth = tagWidth * ratio;
            if (progressWidth > 0) {
                Table progressTableWrapper = new Table();
                progressTableWrapper.bottom().left();
                progressTableWrapper.add(new Image(game.assets.getProgressTex()))
                    .width(progressWidth).height(8f)
                    .left().padLeft(4f).padBottom(12f);
                mapStack.add(progressTableWrapper);
            }

            scrollTable.add(mapStack).size(tagWidth, tagHeight).padBottom(12f);
            scrollTable.row();
        }

        ScrollPane scrollPane = new ScrollPane(scrollTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        mainTable.add(new Image(game.assets.getTitleMissionTex())).size(400f, 90f).padBottom(20f);
        mainTable.row();
        mainTable.add(scrollPane).size(tagWidth + 40f, 290f);
        stage.addActor(mainTable);

        stage.setScrollFocus(scrollPane);
    }

    private void addNumberImagesToTable(Table table, int number) {
        String numStr = String.valueOf(number);
        for (int i = 0; i < numStr.length(); i++) {
            int digit = numStr.charAt(i) - '0';
            Image digitImage = new Image(game.assets.getNumberTex(digit));
            if (i < numStr.length() - 1)
                table.add(digitImage).padRight(3f);
            else
                table.add(digitImage);
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
    }
}
