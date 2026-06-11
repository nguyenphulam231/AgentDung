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

public class GuideScreen extends ScreenAdapter {
    private final AgentDungGame game;
    private Stage stage;

    public GuideScreen(AgentDungGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Tạo ảnh nền từ GameAssets
        Image guideImage = new Image(game.assets.getGuideFrameTex());
        guideImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(guideImage);

        // 2. Tạo nút bấm Quay lại từ GameAssets
        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(game.assets.getBackBtnTex()));
        ImageButton backButton = new ImageButton(backDrawable);

        // 3. Gắn sự kiện click
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.assets.getClickSound() != null) game.assets.getClickSound().play();
                game.setScreen(new MenuScreen(game));
            }
        });

        // 4. Dùng Table định vị nút
        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left();
        topLeftTable.add(backButton).size(50f, 50f).padTop(10f).padLeft(10f);

        stage.addActor(topLeftTable);
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
        // Chỉ dispose Stage vì Stage là đối tượng được tạo mới trong màn hình này
        if (stage != null) stage.dispose();
        // Không dispose Texture ở đây vì chúng được quản lý tập trung trong GameAssets
    }
}
