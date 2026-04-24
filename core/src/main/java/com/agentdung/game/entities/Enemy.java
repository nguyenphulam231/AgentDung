package com.agentdung.game.entities;

import com.badlogic.gdx.Gdx; // Nhớ thêm import này để dùng Blending
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20; // Nhớ thêm import này
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy extends Entity {
    private float visionRange;
    private float visionAngle;

    private float effectTimer = 0;
    private float stunTimer = 0;
    private float originalSpeed = 60;
    private float originalVisionRange = 100;
    private boolean isFleeing = false;
    private float visionRecoverySpeed = 25f;

    private Vector2 startPoint;
    private Vector2 endPoint;
    private boolean movingToEnd = true;
    private float currentAngle;
    private float rotationSpeed = 180f;

    public Enemy(float x, float y) {
        super(x, y, 100, 16);
        this.currentAngle = 0;
        this.visionRange = 100;
        this.visionAngle = 60;
        this.originalVisionRange = visionRange;
        this.speed = originalSpeed;
        this.startPoint = new Vector2(x, y);
        this.endPoint = new Vector2(x, y);
    }

    public void setViewDistance(float distance) {
        this.visionRange = distance;
        this.originalVisionRange = distance;
    }

    public void setViewAngle(float angle) {
        this.visionAngle = angle;
    }

    public void setPatrolRoute(float startX, float startY, float endX, float endY) {
        this.startPoint.set(startX, startY);
        this.endPoint.set(endX, endY);
    }

    @Override
    public void update(float delta, Player player, Array<Rectangle> walls) {
        if (this.visionRange < originalVisionRange) {
            this.visionRange = Math.min(originalVisionRange, visionRange + visionRecoverySpeed * delta);
        }

        if (stunTimer > 0) {
            stunTimer -= delta;
            return;
        }

        if (effectTimer > 0) {
            effectTimer -= delta;
        } else {
            this.speed = originalSpeed;
            this.isFleeing = false;
        }

        Vector2 target = movingToEnd ? endPoint : startPoint;
        float dx = target.x - position.x;
        float dy = target.y - position.y;
        float dist = Vector2.dst(position.x, position.y, target.x, target.y);

        float targetAngle;
        if (isFleeing) {
            float fdx = (position.x + size/2) - (player.getPosition().x + player.size/2);
            float fdy = (position.y + size/2) - (player.getPosition().y + player.size/2);
            targetAngle = MathUtils.atan2(fdy, fdx) * MathUtils.radiansToDegrees;
        } else {
            targetAngle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;
        }

        float angleDiff = (targetAngle - currentAngle + 360 + 180) % 360 - 180;
        float rotationStep = rotationSpeed * delta;

        if (Math.abs(angleDiff) <= rotationStep) {
            currentAngle = targetAngle;
        } else {
            currentAngle += Math.signum(angleDiff) * rotationStep;
        }
        this.angle = currentAngle;

        if (dist < 2 && !isFleeing) {
            movingToEnd = !movingToEnd;
        }

        Vector2 velocity = new Vector2(0, 0);
        // Dừng lại khi lệch góc > 20 độ để xoay mượt
        if (isFleeing || Math.abs(angleDiff) < 20) {
            velocity.set(MathUtils.cosDeg(currentAngle) * speed, MathUtils.sinDeg(currentAngle) * speed);
        }

        float oldX = position.x;
        position.x += velocity.x * delta;
        Rectangle enemyRect = new Rectangle(position.x, position.y, size, size);
        for (Rectangle wall : walls) {
            if (enemyRect.overlaps(wall)) {
                position.x = oldX;
                break;
            }
        }

        float oldY = position.y;
        position.y += velocity.y * delta;
        enemyRect.set(position.x, position.y, size, size);
        for (Rectangle wall : walls) {
            if (enemyRect.overlaps(wall)) {
                position.y = oldY;
                break;
            }
        }
    }

    public boolean detects(Player player, Array<Rectangle> walls) {
        if (stunTimer > 0) return false;
        float pX = player.getPosition().x + player.size/2;
        float pY = player.getPosition().y + player.size/2;
        float eX = this.position.x + this.size/2;
        float eY = this.position.y + this.size/2;
        float dist = Vector2.dst(eX, eY, pX, pY);

        if (dist < visionRange) {
            float angleToPlayer = MathUtils.atan2(pY - eY, pX - eX) * MathUtils.radiansToDegrees;
            float relativeAngle = ((angleToPlayer - currentAngle) + 360 + 180) % 360 - 180;

            if (Math.abs(relativeAngle) <= visionAngle / 2) {
                Vector2 hitPoint = getRaycastHit(eX, eY, angleToPlayer, dist, walls);
                return Vector2.dst(eX, eY, hitPoint.x, hitPoint.y) >= dist - 2;
            }
        }
        return false;
    }

    private Vector2 getRaycastHit(float startX, float startY, float angle, float range, Array<Rectangle> walls) {
        float endX = startX + MathUtils.cosDeg(angle) * range;
        float endY = startY + MathUtils.sinDeg(angle) * range;
        int steps = (int)(range / 4);
        for (int i = 1; i <= steps; i++) {
            float checkX = startX + (endX - startX) * ((float)i/steps);
            float checkY = startY + (endY - startY) * ((float)i/steps);
            for (Rectangle wall : walls) {
                if (wall.contains(checkX, checkY)) return new Vector2(checkX, checkY);
            }
        }
        return new Vector2(endX, endY);
    }

    // CẬP NHẬT: Vẽ tầm nhìn ám vàng, trong suốt kiểu ánh đèn
    public void drawVision(ShapeRenderer shape, Array<Rectangle> walls) {
        if (stunTimer <= 0) {
            // --- KÍCH HOẠT CHẾ ĐỘ BLENDING ĐỂ HIỆN TRONG SUỐT ---
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            // --- THIẾT LẬP MÀU ÁM VÀNG (GOLD) ---
            // Tỉ lệ: Red full, Green hơi cao, Blue thấp -> Ra màu vàng ấm
            // Alpha: 0.15f (15%) -> Rất trong suốt, giống ánh đèn yếu
            shape.setColor(1f, 0.85f, 0.2f, 0.4f);

            float eX = position.x + size/2;
            float eY = position.y + size/2;
            int segments = 20;
            float startAngle = currentAngle - visionAngle/2;
            for (int i = 0; i < segments; i++) {
                float a1 = startAngle + (visionAngle / segments) * i;
                float a2 = startAngle + (visionAngle / segments) * (i + 1);
                Vector2 p1 = getRaycastHit(eX, eY, a1, visionRange, walls);
                Vector2 p2 = getRaycastHit(eX, eY, a2, visionRange, walls);
                shape.triangle(eX, eY, p1.x, p1.y, p2.x, p2.y);
            }

            // --- TẮT CHẾ ĐỘ BLENDING (Quan trọng để không ảnh hưởng đến vẽ cái khác) ---
            shape.end(); // Phải end ShapeRenderer tạm thời
            Gdx.gl.glDisable(GL20.GL_BLEND);
            shape.begin(ShapeRenderer.ShapeType.Filled); // Phải begin lại
        }
    }

    @Override
    public void render(ShapeRenderer shape) {
        if (stunTimer > 0) shape.setColor(Color.PURPLE);
        else if (isFleeing) shape.setColor(Color.BLUE);
        else shape.setColor(Color.RED);

        shape.rect(position.x, position.y, size/2, size/2, size, size, 1, 1, currentAngle);
    }

    public void applySpitEffect() { this.visionRange = originalVisionRange * 0.2f; this.speed = originalSpeed * 0.5f; this.effectTimer = 3.0f; }
    public void applyVomitEffect() { this.visionRange = 5; this.stunTimer = 2.0f; this.speed = originalSpeed * 0.3f; this.effectTimer = 5.0f; }
    public void applyPeeEffect() { this.isFleeing = true; this.effectTimer = 2.0f; this.speed = originalSpeed * 1.8f; }
    public void applyPoopEffect() { this.stunTimer = 4.0f; this.speed = originalSpeed * 0.2f; this.effectTimer = 7.0f; }
}
