package com.agentdung.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Entity {
    private float visionRange;
    private float visionAngle;

    // Effects
    private float effectTimer = 0;
    private float stunTimer = 0;
    private float originalSpeed = 60; // Giảm xuống một chút cho map 16x16 dễ thở hơn
    private float originalVisionRange = 100;
    private boolean isFleeing = false;
    private float visionRecoverySpeed = 25f;

    // Patrol logic
    private Vector2 startPoint;
    private Vector2 endPoint;
    private boolean movingToEnd = true;

    public Enemy(float x, float y) {
        // Gọi Constructor của Entity (x, y, hp, size)
        // Lưu ý: size 16 để khớp với ô gạch 16x16 của bạn
        super(x, y, 100, 16);
        this.angle = 0;
        this.visionRange = 100;
        this.visionAngle = 60;
        this.originalVisionRange = visionRange;
        this.speed = originalSpeed;

        this.startPoint = new Vector2(x, y);
        this.endPoint = new Vector2(x, y); // Sẽ được cập nhật qua setPatrolRoute
    }

    // Các hàm Setter để nhận dữ liệu từ Tiled (Quan trọng để PlayScreen gọi)
    public void setViewDistance(float distance) {
        this.visionRange = distance;
        this.originalVisionRange = distance; // Lưu lại để hồi phục sau khi bị dính skill
    }

    public void setViewAngle(float angle) {
        this.visionAngle = angle;
    }

    public void setPatrolRoute(float startX, float startY, float endX, float endY) {
        this.startPoint.set(startX, startY);
        this.endPoint.set(endX, endY);
    }

    @Override
    public void update(float delta, Player player) {
        // Hồi phục tầm nhìn sau khi bị dính skill (Spit/Vomit)
        if (this.visionRange < originalVisionRange) {
            this.visionRange += visionRecoverySpeed * delta;
            if (this.visionRange > originalVisionRange) {
                this.visionRange = originalVisionRange;
            }
        }

        // Bị choáng (Stun) - không làm gì cả
        if (stunTimer > 0) {
            stunTimer -= delta;
            return;
        }

        // Hết thời gian hiệu ứng thì trả về tốc độ gốc
        if (effectTimer > 0) {
            effectTimer -= delta;
        } else {
            this.speed = originalSpeed;
            this.isFleeing = false;
        }

        // Logic Di chuyển
        if (isFleeing) {
            // Khi bị dính nước tiểu (Pee): Chạy ngược hướng với Dũng
            float dx = (this.position.x + this.size/2) - (player.getPosition().x + player.size/2);
            float dy = (this.position.y + this.size/2) - (player.getPosition().y + player.size/2);
            float angleToFlee = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

            position.x += MathUtils.cosDeg(angleToFlee) * speed * delta;
            position.y += MathUtils.sinDeg(angleToFlee) * speed * delta;
            this.angle = angleToFlee;
        } else {
            // Tuần tra giữa 2 điểm Start và End
            Vector2 target = movingToEnd ? endPoint : startPoint;

            float dx = target.x - position.x;
            float dy = target.y - position.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist < 2) { // Đến gần điểm đích thì đổi hướng
                movingToEnd = !movingToEnd;
            } else {
                // Tính góc xoay dựa trên hướng di chuyển để Vision Cone xoay theo
                this.angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

                position.x += MathUtils.cosDeg(angle) * speed * delta;
                position.y += MathUtils.sinDeg(angle) * speed * delta;
            }
        }
    }

    @Override
    public void render(ShapeRenderer shape) {
        // 1. Vẽ Vision Cone (Hình quạt tầm nhìn)
        if (stunTimer <= 0) {
            // Độ trong suốt tỷ lệ thuận với tầm nhìn hiện tại
            float alpha = 0.1f + (0.3f * (visionRange / originalVisionRange));
            shape.setColor(1, 1, 0, alpha); // Màu vàng nhạt

            // Vẽ tâm ở giữa người lính (size/2)
            shape.arc(position.x + size/2, position.y + size/2,
                visionRange, angle - visionAngle/2, visionAngle);
        }

        // 2. Vẽ thân lính gác
        if (stunTimer > 0) shape.setColor(Color.PURPLE); // Bị choáng (Dẫm phân/Nôn)
        else if (isFleeing) shape.setColor(Color.BLUE);   // Đang chạy trốn
        else shape.setColor(Color.RED);                 // Bình thường

        shape.rect(position.x, position.y, size, size);
    }

    // --- Hệ thống Kỹ năng ảnh hưởng đến lính ---

    public void applySpitEffect() { // Nhổ nước bọt: Giảm tầm nhìn tạm thời
        this.visionRange = originalVisionRange * 0.2f;
        this.speed = originalSpeed * 0.5f;
        this.effectTimer = 3.0f;
    }

    public void applyVomitEffect() { // Nôn mửa: Choáng nặng và mù
        this.visionRange = 5;
        this.stunTimer = 2.0f;
        this.speed = originalSpeed * 0.3f;
        this.effectTimer = 5.0f;
    }

    public void applyPeeEffect() { // Nước tiểu: Hoảng sợ bỏ chạy
        this.isFleeing = true;
        this.effectTimer = 2.0f;
        this.speed = originalSpeed * 1.8f;
    }

    public void applyPoopEffect() { // Dẫm phân: Choáng rất lâu
        this.stunTimer = 4.0f;
        this.speed = originalSpeed * 0.2f;
        this.effectTimer = 7.0f;
    }

    // Logic kiểm tra phát hiện Đặc vụ Dũng
    public boolean detects(Player player) {
        if (stunTimer > 0) return false; // Đang choáng không thấy gì

        float dx = (player.getPosition().x + player.size/2) - (this.position.x + this.size/2);
        float dy = (player.getPosition().y + player.size/2) - (this.position.y + this.size/2);
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        // 1. Kiểm tra khoảng cách
        if (dist < visionRange) {
            // 2. Kiểm tra góc nhìn (Dũng có nằm trong hình quạt không)
            float angleToPlayer = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;
            float relativeAngle = ((angleToPlayer - angle) + 360 + 180) % 360 - 180;
            return Math.abs(relativeAngle) <= visionAngle / 2;
        }
        return false;
    }
}
