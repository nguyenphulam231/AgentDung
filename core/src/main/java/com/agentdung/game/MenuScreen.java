package com.agentdung.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen extends ScreenAdapter {
    AgentDungGame game;
    BitmapFont font;
    SpriteBatch batch;

    public MenuScreen(AgentDungGame game) {
        this.game = game;
        this.font = new BitmapFont();
        this.batch = new SpriteBatch();
        font.getData().setScale(2);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Buttons
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(Color.DARK_GRAY);
        game.shapeRenderer.rect(300, 250, 200, 60); // Nút Map 1
        game.shapeRenderer.rect(300, 150, 200, 60); // Nút Map 2
        game.shapeRenderer.end();

        // Text
        batch.begin();
        font.setColor(Color.YELLOW);
        font.draw(batch, "AGENT DUNG: THE MISSION", 250, 450);
        font.setColor(Color.WHITE);
        font.draw(batch, "MAP 1: TRU SO", 320, 290);
        font.draw(batch, "MAP 2: CAN CU", 320, 190);
        batch.end();

        // mouse click logic
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (mx > 300 && mx < 500) {
                if (my > 250 && my < 310) game.setScreen(new PlayScreen(game, 1));
                if (my > 150 && my < 210) game.setScreen(new PlayScreen(game, 2));
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
