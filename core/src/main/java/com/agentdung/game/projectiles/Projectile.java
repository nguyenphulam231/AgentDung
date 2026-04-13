package com.agentdung.game.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class Projectile {
    protected Vector2 position;
    protected Vector2 velocity;
    protected Color color;
    protected float lifeTime;
    protected boolean active = true;

    public Projectile(float x, float y, float angle, float speed, Color color, float life) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed);
        this.color = color;
        this.lifeTime = life;
    }

    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
        lifeTime -= delta;
        if (lifeTime <= 0) active = false;
    }

    public abstract void render(ShapeRenderer shape);

    public boolean isActive() { return active; }

    public Vector2 getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }
}
