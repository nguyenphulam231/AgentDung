package com.agentdung.game.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Thêm import Batch hình ảnh
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; // Giữ lại Batch hình khối
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.agentdung.game.entities.Enemy;

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

    /**
     * Hàm render đa hình nâng cấp: Nhận cả hai công cụ vẽ.
     * Giúp hệ thống quản lý vẽ đồng bộ mọi loại đạn cùng lúc.
     */
    public abstract void render(SpriteBatch batch, ShapeRenderer shape);
    public abstract void applyEffect(Enemy enemy);
    public boolean isActive() { return active; }
    public Vector2 getPosition() { return position; }
    public Color getColor() { return color; }
}
