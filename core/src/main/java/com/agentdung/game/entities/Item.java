package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Item extends Entity {

    public enum ItemType {
        ROTTEN_MEAT, // Thịt thiu
        WATER        // Nước uống
    }

    public ItemType type;
    public Rectangle bounds;

    public Item(float x, float y, ItemType type) {
        // Gọi constructor lớp cha Entity: speed = 0 (nằm im), size = 16 (kích thước)
        super(x, y, 0f, 16f);

        this.type = type;
        // Tạo hộp va chạm khớp với vị trí và kích thước của Entity
        this.bounds = new Rectangle(x, y, this.size, this.size);
    }

    @Override
    public void render(ShapeRenderer shape) {
        // Để trống vì chúng ta vẽ Item bằng hình ảnh qua SpriteBatch trong MapManager rồi bạn nhé
    }

    @Override
    public void update(float delta, Player player, Array<Rectangle> walls) {
        // Vật phẩm tĩnh không cần cập nhật di chuyển hay va chạm với tường, để trống tại đây
    }
}
