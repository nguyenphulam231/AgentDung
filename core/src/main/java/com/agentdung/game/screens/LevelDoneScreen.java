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
    private Texture bgTexture;
    private Texture btnNextTexture;
    private Texture btnProgressTexture;
    private Texture btnMainTexture;

    public LevelDoneScreen(AgentDungGame game, int world, int level) {
        this.game = game;
        this.completedWorld = world;
        this.completedLevel = level;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Nạp ảnh từ assets (Đã sửa chuẩn 100% cú pháp LibGDX)
        bgTexture = new Texture(Gdx.files.internal("ui/UI_frame_leveldone.png"));
        btnNextTexture = new Texture(Gdx.files.internal("ui/UI_button_nextlevel.png"));
        btnProgressTexture = new Texture(Gdx.files.internal("ui/UI_button_progressmenu.png"));
        btnMainTexture = new Texture(Gdx.files.internal("ui/UI_button_mainmenu.png"));

        // Khử mờ cho Pixel Art giống MenuScreen của bạn
        bgTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnNextTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnProgressTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        btnMainTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // 2. Làm ảnh nền full toàn màn hình y chang các menu khác
        Image background = new Image(bgTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        // 3. Ép size cố định cho ảnh nút từ Drawable gốc giống MenuScreen của bạn (Dễ căn chỉnh, không lo mờ/bé)
        float targetBtnWidth =360f;
        float targetBtnHeight = 50f;

        TextureRegionDrawable nextDrawable = new TextureRegionDrawable(new TextureRegion(btnNextTexture));
        nextDrawable.setMinWidth(targetBtnWidth);
        nextDrawable.setMinHeight(targetBtnHeight);
        ImageButton nextButton = new ImageButton(nextDrawable);

        TextureRegionDrawable progressDrawable = new TextureRegionDrawable(new TextureRegion(btnProgressTexture));
        progressDrawable.setMinWidth(targetBtnWidth);
        progressDrawable.setMinHeight(targetBtnHeight);
        ImageButton progressButton = new ImageButton(progressDrawable);

        TextureRegionDrawable mainDrawable = new TextureRegionDrawable(new TextureRegion(btnMainTexture));
        mainDrawable.setMinWidth(targetBtnWidth);
        mainDrawable.setMinHeight(targetBtnHeight);
        ImageButton mainButton = new ImageButton(mainDrawable);

        // 4. Gắn sự kiện click chuột
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
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
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                game.setScreen(new ProgressScreen(game, completedWorld));
            }
        });

        mainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isMasterOn && game.isSfxOn && game.clickSound != null) game.clickSound.play();
                game.setScreen(new MenuScreen(game));
            }
        });

        // 5. Một Table duy nhất căn giữa màn hình để xếp 3 nút từ trên xuống dưới
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(nextButton).padBottom(20f); // Giãn cách nút 20px
        table.row();
        table.add(progressButton).padBottom(20f);
        table.row();
        table.add(mainButton);

        stage.addActor(table);
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
        if (btnNextTexture != null) btnNextTexture.dispose();
        if (btnProgressTexture != null) btnProgressTexture.dispose();
        if (btnMainTexture != null) btnMainTexture.dispose();
    }
}
