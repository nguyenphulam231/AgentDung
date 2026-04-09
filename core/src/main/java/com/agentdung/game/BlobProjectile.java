package com.agentdung.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BlobProjectile extends Projectile {
    public BlobProjectile(float x, float y, float angle, float speed, Color color) {
        super(x, y, angle, speed, color, 1.2f); // Tồn tại 1.2 giây
    }

    @Override
    public void render(ShapeRenderer shape) {
        shape.setColor(color);
        //drop of spit skill
        shape.circle(position.x, position.y, 6);
    }
}
