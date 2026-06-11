package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 position;
    protected Vector2 velocity;
    protected float speed;
    protected float size; // Changed to protected for Encapsulation
    protected float angle;

    public Entity(float x, float y, float speed, float size) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.speed = speed;
        this.size = size;
        this.angle = 0;
    }

    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public float getSpeed() { return speed; }
    public float getSize() { return size; }
    public float getAngle() { return angle; }
    public void setAngle(float angle) { this.angle = angle; }
    public void setSize(float size) {
        this.size = size;
    }

    public abstract void update(float delta);
    public abstract void render(SpriteBatch batch, ShapeRenderer shape);
}
