package com.agentdung.game.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.agentdung.game.entities.Enemy;

public class StreamProjectile extends Projectile {

    public StreamProjectile(float x, float y, float angle, float speed, Color color) {
        super(x, y, angle, speed, color, 0.4f);
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        shape.setColor(color);
        shape.rectLine(
            position.x,
            position.y,
            position.x + velocity.x * 0.05f,
            position.y + velocity.y * 0.05f,
            4
        );
    }

    @Override
    public boolean isSpriteBased() {
        return false;
    }

    @Override
    public void applyEffect(Enemy enemy) {
        if (color.equals(Color.YELLOW)) {
            enemy.applyPeeEffect();
        }
        else if (color.equals(new Color(0.5f, 0.25f, 0, 1))) {
            enemy.applyPoopEffect();
        }
        else if (color.equals(Color.WHITE)) {
            enemy.applyVomitEffect();
        }
    }
}
