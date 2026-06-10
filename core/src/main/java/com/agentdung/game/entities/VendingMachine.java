package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class VendingMachine extends Entity {
    public Rectangle bounds;

    public VendingMachine(float x, float y, float width, float height) {
        super(x, y, 0f, width);
        // Lưu trữ vùng tương tác dựa trên kích thước từ TiledMap đặt ra
        this.bounds = new Rectangle(x, y, width, height);
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, com.badlogic.gdx.graphics.glutils.ShapeRenderer shape) {

    }

    @Override
    public void update(float delta, Player player, Array<Rectangle> walls) {}
}
