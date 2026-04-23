package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Wall {
    public Rectangle bounds;

    public Wall(float x, float y, float width, float height) {
        // Tiled nạp tọa độ chuẩn, ta chỉ cần lưu lại để check va chạm
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void render(ShapeRenderer shape) {
        // Nếu bạn muốn vẽ tường bằng màu (khi chưa có texture)
        // shape.setColor(0.3f, 0.3f, 0.3f, 1);
        // shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Lưu ý: Nếu đã có mapRenderer.render() ở PlayScreen
        // thì không cần vẽ lại rect này nữa, để nó tàng hình chỉ để check va chạm thôi.
    }
}
