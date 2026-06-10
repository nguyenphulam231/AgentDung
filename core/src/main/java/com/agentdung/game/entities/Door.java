package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Door {
    public Rectangle bounds;
    public boolean isOpen = false;
    public float unlockProgress = 0f; // 0.0 đến 1.0
    private float timeToOpen = 2.0f; // Mất 2 giây để mở

    public Door(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void update(float delta, boolean isNear, boolean hasKey) {
        if (isOpen) return;

        if (isNear && hasKey) {
            unlockProgress += delta / timeToOpen;
            if (unlockProgress >= 1f) {
                unlockProgress = 1f;
                isOpen = true; // Cửa đã mở!
            }
        } else {
            // Nếu người chơi rời đi khi chưa mở xong, tiến trình bị giảm dần
            unlockProgress -= delta;
            if (unlockProgress < 0) unlockProgress = 0;
        }
    }

    public void render(ShapeRenderer shape) {
        if (isOpen) {
            // Nếu mở rồi vẽ khung xanh mờ báo hiệu đã mở
            shape.setColor(0, 1, 0, 0.3f); // Màu xanh lá mờ
            shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        } else {
            // Nếu chưa mở, vẽ khối cửa đặc
            shape.setColor(new Color(0.4f, 0.2f, 0, 1));
            shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);

            // Vẽ thanh tiến trình (Loading bar) khi đang mở
            if (unlockProgress > 0 && unlockProgress < 1) {
                shape.setColor(Color.GRAY);
                shape.rect(bounds.x, bounds.y + bounds.height + 5, bounds.width, 5);
                shape.setColor(Color.GREEN);
                shape.rect(bounds.x, bounds.y + bounds.height + 5, bounds.width * unlockProgress, 5);
            }
        }
    }
}
