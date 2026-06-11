package com.agentdung.game.entities;

import com.agentdung.game.assets.GameAssets; // Import GameAssets vào đây
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Server {
    public float x, y, width, height;
    public float hp = 20f;
    public boolean isDestroyed = false;

    // Chỉ giữ biến Texture làm tham chiếu, không tự nạp
    private Texture serverTexture;

    // Cập nhật Constructor nhận thêm GameAssets
    public Server(float x, float y, GameAssets assets) {
        this.x = x;
        this.y = y;
        this.width = 30;
        this.height = 40;

        // Lấy Texture dùng chung từ GameAssets
        this.serverTexture = assets.getServerTexture();
    }

    public void takeDamage(float damage) {
        if (isDestroyed) return;
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            isDestroyed = true;
        }
    }

    // Vẽ hình ảnh Server (Được gọi trong khối SpriteBatch của PlayScreen)
    public void renderSprite(SpriteBatch batch) {
        if (!isDestroyed) {
            batch.draw(serverTexture, x, y, width, height);
        } else {
            // Khi bị phá hủy, làm tối ảnh đi 50% để biểu thị sập nguồn
            batch.setColor(0.3f, 0.3f, 0.3f, 1f);
            batch.draw(serverTexture, x, y, width, height);
            batch.setColor(Color.WHITE); // Trả lại màu mặc định cho các Sprite khác
        }
    }

    // Vẽ thanh máu (Được gọi trong khối ShapeRenderer của PlayScreen)
    public void renderHpBar(ShapeRenderer shape) {
        if (!isDestroyed) {
            shape.setColor(Color.RED);
            shape.rect(x, y + height + 5, width * (hp / 100f), 5);
        }
    }

    public void dispose() {

    }
}
