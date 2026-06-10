package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Wall {
    public Rectangle bounds;

    public Wall(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

}
