package com.agentdung.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class AgentDungGame extends Game {
    public ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        //Menu appe
        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
