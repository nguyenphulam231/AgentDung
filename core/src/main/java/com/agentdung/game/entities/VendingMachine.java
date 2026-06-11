package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class VendingMachine extends Entity {
    public Rectangle bounds;

    public VendingMachine(float x, float y, float width, float height) {
        super(x, y, 0f, width);
        // Lưu trữ vùng tương tác dựa trên kích thước từ TiledMap đặt ra
        this.bounds = new Rectangle(x, y, width, height);
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        // Thân hàm vẽ máy bán hàng nếu bạn muốn tự xử lý bằng code (hiện tại map đang vẽ qua TiledMap)
    }

    /**
     * ĐÃ SỬA: Đồng bộ hóa cấu trúc Đa hình theo giao kèo mới của lớp cha Entity.
     * Máy bán hàng đứng im nên thân hàm được để trống một cách an toàn.
     */
    @Override
    public void update(float delta) {
        // Không cần xử lý logic chuyển động nội bộ theo từng frame
    }
}
