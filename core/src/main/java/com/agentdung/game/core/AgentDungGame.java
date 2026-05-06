package com.agentdung.game.core;

import com.agentdung.game.screens.MenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Thêm dòng này
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class AgentDungGame extends Game {
    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch; // Thêm dòng này để các Screen dùng chung

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch(); // Khởi tạo batch ở đây

        // Chuyển đến Menu
        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // Rất quan trọng: Để các Screen có thể render được
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose(); // Nhớ giải phóng bộ nhớ cho batch
    }
}
