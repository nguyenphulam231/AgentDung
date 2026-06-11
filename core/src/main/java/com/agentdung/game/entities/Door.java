package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle; // Đảm bảo đã import

public class Door extends Entity {

    public boolean isNear;
    public boolean hasKey;
    public Rectangle bounds;
    public boolean isOpen = false;
    public float unlockProgress = 0f;
    private float timeToOpen = 2.0f;

    public Door(float x, float y, float width, float height) {
        super(x, y, 0, width); // Gọi constructor của Entity

        this.bounds = new Rectangle(x, y, width, height);
    }

    // 3. Getter để các lớp khác (như CollisionUtils) truy cập được
    public Rectangle getBounds() {
        return this.bounds;
    }

    @Override
    public void update(float delta) {
        // ... logic update của bạn
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        // ... logic render của bạn
    }
}
