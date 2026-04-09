package com.agentdung.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Server {
    public float x, y, width, height;
    public float hp = 100f;
    public boolean isDestroyed = false;

    public Server(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 60;
        this.height = 80;
    }

    public void takeDamage(float damage) {
        if (isDestroyed) return;
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            isDestroyed = true;
        }
    }

    public void render(ShapeRenderer shape) {
        //is destroyed
        if (isDestroyed) {
            shape.setColor(Color.BLACK);
        } else {
            shape.setColor(Color.LIGHT_GRAY);
        }
        shape.rect(x, y, width, height);

        // blood bar of server
        if (!isDestroyed) {
            shape.setColor(Color.RED);
            shape.rect(x, y + height + 5, width * (hp / 100f), 5);
        }
    }
}
