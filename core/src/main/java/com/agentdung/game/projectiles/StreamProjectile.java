package com.agentdung.game.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Thêm import SpriteBatch phục vụ đa hình
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.agentdung.game.entities.Enemy;

public class StreamProjectile extends Projectile {

    public StreamProjectile(float x, float y, float angle, float speed, Color color) {
        // Thời gian tồn tại (lifetime) là 0.4 giây giúp tạo hiệu ứng dòng nước tan biến dần
        super(x, y, angle, speed, color, 0.4f);
    }

    /**
     * Triển khai hàm render đa hình mới nhận cả 2 tham số.
     * Vì StreamProjectile chỉ dùng hình khối nguyên thủy, tham số SpriteBatch sẽ được bỏ qua.
     */
    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        shape.setColor(color);

        // Vẽ dòng chảy bằng nét vẽ dày 4px dựa trên vector vận tốc vật lý
        shape.rectLine(
            position.x,
            position.y,
            position.x + velocity.x * 0.05f,
            position.y + velocity.y * 0.05f,
            4
        );
    }
    @Override
    public void applyEffect(Enemy enemy) {

        if (color.equals(Color.YELLOW)) {
            enemy.applyPeeEffect();
        }
        else if (color.equals(new Color(0.5f, 0.25f, 0, 1))) {
            enemy.applyPoopEffect();
        }
        else if (color.equals(Color.WHITE)) {
            enemy.applyVomitEffect();
        }
    }
}
