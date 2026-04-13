package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 position; // Tọa độ x, y
    protected float speed;
    protected float size;
    protected float angle;

    public Entity(float x, float y, float speed, float size) {
        this.position = new Vector2(x, y);
        this.speed = speed;
        this.size = size;
        this.angle = 0;
    }

    public float getSize() { return size; }
    public void setSize(float size) {
        this.size = size;
    }
    public void setAngle(float angle) {
        this.angle = angle;
    }

    public abstract void render(ShapeRenderer shape);

    public abstract void update(float delta, Player player);

    public Vector2 getPosition() { return position; }
}
