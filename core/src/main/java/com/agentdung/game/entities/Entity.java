package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    // Để protected để lớp con lấy dữ liệu nhanh, hoặc dùng getter công khai
    protected Vector2 position;
    protected Vector2 velocity;
    protected float speed;
    public float size; // Để public hoặc getter để EntityManager check va chạm dễ dàng
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

    /**
     * ĐÃ THÊM: Phương thức Getter công khai cho thuộc tính tốc độ (speed).
     * Giúp EntityManager lấy thông tin để nhân vào toán tử vận tốc,
     * giải quyết dứt điểm lỗi nhân vật bị đứng im tại chỗ khi bấm phím di chuyển.
     */
    public float getSpeed() { return speed; }

    public float getSize() { return size; }
    public float getAngle() { return angle; }
    public void setAngle(float angle) { this.angle = angle; }
    public void setSize(float size) {
        this.size = size;
    }

    // Hợp đồng đa hình bắt buộc
    public abstract void update(float delta);
    public abstract void render(SpriteBatch batch, ShapeRenderer shape);
}
