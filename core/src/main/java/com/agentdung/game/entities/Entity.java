package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Dùng cho đồ họa Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; // Nếu vẫn cần vẽ hitbox/thanh máu thủ công
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class Entity {
    protected Vector2 position; // Tọa độ x, y
    protected Vector2 velocity;
    protected float speed;
    protected float size;
    protected float angle;

    public Entity(float x, float y, float speed, float size) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.speed = speed;
        this.size = size;
        this.angle = 0;
    }

    // --- CÁC HÀM GETTER / SETTER
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public float getSize() { return size; }
    public void setSize(float size) { this.size = size; }
    public float getAngle() { return angle; }
    public void setAngle(float angle) { this.angle = angle; }

    /**
     * Hàm render mới nhận vào SpriteBatch để vẽ hình ảnh (Sprite/Texture)
     * Đảm bảo tính đa hình cho toàn bộ hệ thống đồ họa mới.
     */
    public abstract void render(SpriteBatch batch, ShapeRenderer shape);

    /**
     * (Tùy chọn) Nếu bạn vẫn cần vẽ các ô màu debug, thanh máu (HP bar) hoặc hiệu ứng ánh sáng cũ,
     * bạn có thể giữ hàm này hoặc viết đè (overload) nó. Nếu không dùng nữa, hãy XÓA HẲN để sạch code.
     */
    // public abstract void renderDebug(ShapeRenderer shape);

    public abstract void update(float delta, Player player, Array<Rectangle> walls);
}
