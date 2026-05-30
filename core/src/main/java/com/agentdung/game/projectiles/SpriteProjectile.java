package com.agentdung.game.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SpriteProjectile extends Projectile {

    private TextureRegion textureRegion;
    private float width;
    private float height;
    private float angle;

    public SpriteProjectile(float x, float y, float angle, float speed, TextureRegion textureRegion) {
        // Đã sửa ở bước trước: Giữ Color.WHITE để PlayScreen nhận diện màu
        super(x, y, angle, speed, Color.WHITE, 0.4f);
        this.textureRegion = textureRegion;
        this.angle = angle;

        // 🔥 ĐÃ SỬA: Lấy trực tiếp kích thước thật (Width/Height) của file ảnh nguồn!
        // Nếu ảnh là 1px, nó sẽ là 1f. Nếu ảnh chiêu khạc là 8px, nó sẽ là 8f.
        this.width = textureRegion.getRegionWidth();
        this.height = textureRegion.getRegionHeight();
    }

    @Override
    public void render(ShapeRenderer shape) {
        // Để trống vì vẽ bằng SpriteBatch
    }

    public void render(SpriteBatch batch) {
        batch.draw(
            textureRegion,
            position.x - width / 2, position.y - height / 2,
            width / 2, height / 2,
            width, height,
            1f, 1f,
            this.angle
        );
    }

    // THÊM CONSTRUCTOR MỚI NÀY VÀO TRONG FILE SpriteProjectile.java
    public SpriteProjectile(float x, float y, float angle, float speed, TextureRegion textureRegion, float customWidth, float customHeight) {
        super(x, y, angle, speed, Color.WHITE, 0.4f);
        this.textureRegion = textureRegion;
        this.angle = angle;

        // Gán kích thước tùy chỉnh do bạn truyền vào từ Skill
        this.width = customWidth;
        this.height = customHeight;
    }
}
