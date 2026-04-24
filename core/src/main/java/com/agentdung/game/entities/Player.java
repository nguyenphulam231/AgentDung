package com.agentdung.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

public class Player extends Entity {
    private float maxMana = 100f;
    private float currentMana = 100f;
    private float regenRate = 15f; // Tăng tốc độ hồi mana một chút cho "đã" tay

    public Player(float x, float y) {
        // Tốc độ 150, kích thước 16 để khớp với ô gạch 16x16
        super(x, y, 150, 16);
    }

    @Override
    public void update(float delta, Player player, Array<Rectangle> walls) {
        // --- PHẦN GÓC XOAY ---
        // Lưu ý: Logic di chuyển tiến/lùi (W/S) đã được PlayScreen xử lý
        // cùng với va chạm tường, nên ở đây ta chỉ giữ lại logic tính góc xoay theo chuột.

        // Hồi mana theo thời gian
        if (currentMana < maxMana) {
            currentMana += regenRate * delta;
        }
    }

    @Override
    public void render(ShapeRenderer shape) {
        // 1. Vẽ thân nhân vật (Màu xanh lá)
        // Dùng rect với đầy đủ tham số để xoay quanh tâm nhân vật
        shape.setColor(Color.GREEN);
        shape.rect(position.x, position.y, size/2f, size/2f, size, size, 1, 1, angle);

        // 2. Vẽ "Mũi" hoặc hướng súng (Màu xanh đậm hơn)
        // Đặt ở phía trước theo góc quay
        shape.setColor(Color.FOREST);
        float noseWidth = size / 2f;
        float noseHeight = size / 4f;
        shape.rect(position.x + size - 2, position.y + (size / 2f) - (noseHeight / 2f),
            - (size / 2f - 2), noseHeight / 2f, noseWidth, noseHeight, 1, 1, angle);

        // 3. Thanh Mana nhỏ phía trên đầu
        shape.setColor(Color.GRAY);
        shape.rect(position.x, position.y + size + 2, size, 3);
        shape.setColor(Color.CYAN);
        shape.rect(position.x, position.y + size + 2, size * (currentMana / maxMana), 3);
    }

    // --- CÁC HÀM TIỆN ÍCH ---

    public boolean useMana(float amount) {
        if (currentMana >= amount) {
            currentMana -= amount;
            return true;
        }
        return false;
    }

    public float getCurrentMana() { return currentMana; }
    public float getManaPercent() { return currentMana / maxMana; }
    public float getAngle() { return angle; }
    public void setAngle(float angle) { this.angle = angle; }
}
