package com.agentdung.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Wall {
    public Rectangle bounds;

    public Wall(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void render(ShapeRenderer shape) {
        shape.setColor(Color.DARK_GRAY);
        shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
