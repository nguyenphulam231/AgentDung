package com.agentdung.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class StreamProjectile extends Projectile {
    public StreamProjectile(float x, float y, float angle, float speed, Color color) {
        super(x, y, angle, speed, color, 0.4f);
    }

    @Override
    public void render(ShapeRenderer shape) {
        shape.setColor(color);
        // stream
        // 4px thick line
        shape.rectLine(position.x, position.y,
            position.x + velocity.x * 0.05f,
            position.y + velocity.y * 0.05f, 4);
    }
}
