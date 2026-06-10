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

public class GuideScreen extends ScreenAdapter {
    private final AgentDungGame game;
    private Stage stage;
    private Texture guideTexture;
    private Texture backButtonTexture; // Khai báo Texture cho nút quay lại

    public GuideScreen(AgentDungGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Nạp các file ảnh từ assets
        guideTexture = new Texture(Gdx.files.internal("ui/UI_frame_guide.png"));
        backButtonTexture = new Texture(Gdx.files.internal("ui/UI_arrow_left.png"));

        // 2. Tạo ảnh nền hướng dẫn full màn hình
        Image guideImage = new Image(guideTexture);
        guideImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(guideImage);

        // 3. Tạo nút bấm Quay lại (Back Button)
        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(backButtonTexture));
        ImageButton backButton = new ImageButton(backDrawable);

        // 4. Gắn sự kiện click cho nút quay lại để về MenuScreen
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.clickSound != null) game.clickSound.play();
                game.setScreen(new MenuScreen(game));
            }
        });

        // 5. Dùng Table để đẩy nút quay lại lên góc trên bên trái màn hình
        Table topLeftTable = new Table();
        topLeftTable.setFillParent(true);
        topLeftTable.top().left(); // Định vị bảng ở góc TRÊN bên TRÁI
        topLeftTable.add(backButton).size(50f, 50f).padTop(10f).padLeft(10f);

        // Đưa bảng chứa nút vào Stage
        stage.addActor(topLeftTable);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Vẽ Stage (Lúc này tự động vẽ cả ảnh nền và nút back theo đúng thứ tự)
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
        if (guideTexture != null) guideTexture.dispose();
        if (backButtonTexture != null) backButtonTexture.dispose(); // Giải phóng bộ nhớ nút back
    }
}
