package com.agentdung.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Server {
    public float x, y, width, height;
    public float hp = 20f;
    public boolean isDestroyed = false;

    // Nạp kết cấu hình ảnh cho Server
    private Texture serverTexture;

    public Server(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 60;
        this.height = 80;

        // Khởi tạo Texture từ thư mục assets/images/
        serverTexture = new Texture(Gdx.files.internal("images/server.png"));
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

    // Giải phóng tài nguyên hệ thống tránh tràn bộ nhớ RAM
    public void dispose() {
        if (serverTexture != null) {
            serverTexture.dispose();
        }
    }
}
