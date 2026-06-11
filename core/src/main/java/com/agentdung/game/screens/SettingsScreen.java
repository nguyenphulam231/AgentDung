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

public class SettingsScreen extends ScreenAdapter {
    private final AgentDungGame game;
    private Stage stage;

    public SettingsScreen(AgentDungGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        game.assets.loadSettingsAssets();

        Image settingsBg = new Image(game.assets.getSettingsBgTex());
        settingsBg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(settingsBg);

        Image titleImage = new Image(game.assets.getSettingsTitleTex());
        Image textMaster = new Image(game.assets.getTextMasterTex());
        Image textSfx = new Image(game.assets.getTextSfxTex());
        Image textMusic = new Image(game.assets.getTextMusicTex());

        TextureRegionDrawable backDrawable = new TextureRegionDrawable(
            new TextureRegion(game.assets.getBackBtnTex()));
        ImageButton backButton = new ImageButton(backDrawable);

        TextureRegionDrawable soundOn = new TextureRegionDrawable(
            new TextureRegion(game.assets.getSoundOnTex()));
        TextureRegionDrawable soundOff = new TextureRegionDrawable(
            new TextureRegion(game.assets.getSoundOffTex()));
        TextureRegionDrawable musicOn = new TextureRegionDrawable(
            new TextureRegion(game.assets.getMusicOnTex()));
        TextureRegionDrawable musicOff = new TextureRegionDrawable(
            new TextureRegion(game.assets.getMusicOffTex()));

        final ImageButton masterButton = new ImageButton(soundOn, soundOn, soundOff);
        final ImageButton sfxButton = new ImageButton(soundOn, soundOn, soundOff);
        final ImageButton musicButton = new ImageButton(musicOn, musicOn, musicOff);

        masterButton.setChecked(!game.isMasterOn);
        sfxButton.setChecked(!game.isSfxOn);
        musicButton.setChecked(!game.isMusicOn);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null)
                    game.assets.getClickSound().play();
                game.setScreen(new MenuScreen(game));
            }
        });

        masterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.isMasterOn = !masterButton.isChecked();
                game.updateMusicState();
                game.updateSfxState();
                if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null)
                    game.assets.getClickSound().play();
            }
        });

        sfxButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.isSfxOn = !sfxButton.isChecked();
                game.updateSfxState();
                if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null)
                    game.assets.getClickSound().play();
            }
        });

        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.isMusicOn = !musicButton.isChecked();
                game.updateMusicState();
                if (game.isMasterOn && game.isSfxOn && game.assets.getClickSound() != null)
                    game.assets.getClickSound().play();
            }
        });

        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left();
        topLeftTable.add(backButton).size(40f, 40f).padTop(15f).padLeft(15f);
        stage.addActor(topLeftTable);

        Table optionsTable = new Table();
        optionsTable.left();

        float btnMasterSize = 75f;
        float btnSubSize = 50f;

        optionsTable.add(masterButton).size(btnMasterSize).padRight(15f).padBottom(15f);
        optionsTable.add(textMaster).size(150f, 45f).padBottom(15f).left();
        optionsTable.row();

        optionsTable.add(sfxButton).size(btnSubSize).padLeft(45f).padRight(15f).padBottom(15f);
        optionsTable.add(textSfx).size(80f, 32f).padBottom(15f).left();
        optionsTable.row();

        optionsTable.add(musicButton).size(btnSubSize).padLeft(45f).padRight(15f);
        optionsTable.add(textMusic).size(100f, 32f).left();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        mainTable.add(titleImage).size(350f, 90f).padTop(60f).padBottom(25f).center();
        mainTable.row();
        mainTable.add(optionsTable).center();
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
        // Chỉ dispose stage — texture do GameAssets quản lý
        if (stage != null) stage.dispose();
    }
}
