package com.agentdung.game;

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
    private float originalSpeed = 100;
    private float originalVisionRange = 150;
    private boolean isFleeing = false;
    private float visionRecoverySpeed = 15f;

    // Patrol logic
    private Vector2 startPoint;
    private Vector2 endPoint;
    private boolean movingToEnd = true;

    public Enemy(float x, float y) {
        super(x, y, 100, 30);
        this.angle = 0;
        this.visionRange = 150;
        this.visionAngle = 60;
        this.speed = originalSpeed;

        // Patrolers' moving range at 300px default
        this.startPoint = new Vector2(x, y);
        this.endPoint = new Vector2(x + 300, y);
    }

    // Patrol path making function
    public void setPatrolRoute(float startX, float startY, float endX, float endY) {
        this.startPoint.set(startX, startY);
        this.endPoint.set(endX, endY);
    }

    @Override
    public void update(float delta, Player player) {
        // recover sight
        if (this.visionRange < originalVisionRange) {
            this.visionRange += visionRecoverySpeed * delta;
            if (this.visionRange > originalVisionRange) {
                this.visionRange = originalVisionRange;
            }
        }

        // stun
        if (stunTimer > 0) {
            stunTimer -= delta;
            return;
        }

        // timer
        if (effectTimer > 0) {
            effectTimer -= delta;
        } else {
            this.speed = originalSpeed;
            this.isFleeing = false;
        }

        // Moving logic
        if (isFleeing) {
            // got peed
            float dx = (this.position.x + this.size/2) - (player.getPosition().x + player.size/2);
            float dy = (this.position.y + this.size/2) - (player.getPosition().y + player.size/2);
            float angleToFlee = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

            position.x += MathUtils.cosDeg(angleToFlee) * speed * delta;
            position.y += MathUtils.sinDeg(angleToFlee) * speed * delta;
            this.angle = angleToFlee;
        } else {
            // patrol
            Vector2 target = movingToEnd ? endPoint : startPoint;

            float dx = target.x - position.x;
            float dy = target.y - position.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist < 5) {
                movingToEnd = !movingToEnd;
            } else {
                this.angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

                position.x += MathUtils.cosDeg(angle) * speed * delta;
                position.y += MathUtils.sinDeg(angle) * speed * delta;
            }
        }
    }

    @Override
    public void render(ShapeRenderer shape) {
        if (stunTimer <= 0) {
            float alpha = 0.05f + (0.25f * (visionRange / originalVisionRange));
            shape.setColor(1, 1, 0, alpha);
            shape.arc(position.x + size/2, position.y + size/2,
                visionRange, angle - visionAngle/2, visionAngle);
        }

        if (stunTimer > 0) shape.setColor(Color.PURPLE);
        else if (isFleeing) shape.setColor(Color.BLUE);
        else shape.setColor(Color.RED);

        shape.rect(position.x, position.y, size, size);
    }

    // Skills
    public void applySpitEffect() {
        this.visionRange = 30;
        this.speed = originalSpeed * 0.5f;
        this.effectTimer = 2.0f;
    }

    public void applyVomitEffect() {
        this.visionRange = 10;
        this.stunTimer = 2.0f;
        this.speed = originalSpeed * 0.3f;
        this.effectTimer = 5.0f;
    }

    public void applyPeeEffect() {
        this.isFleeing = true;
        this.effectTimer = 1.5f;
        this.speed = originalSpeed * 1.5f; // Hoảng loạn nên chạy nhanh hơn
    }

    public void applyPoopEffect() {
        this.stunTimer = 3.0f;
        this.speed = originalSpeed * 0.2f;
        this.effectTimer = 6.0f;
    }

    public boolean detects(Player player) {
        if (stunTimer > 0) return false;

        float dx = (player.getPosition().x + player.size/2) - (this.position.x + this.size/2);
        float dy = (player.getPosition().y + player.size/2) - (this.position.y + this.size/2);
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist < visionRange) {
            float angleToPlayer = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;
            float relativeAngle = ((angleToPlayer - angle) + 360 + 180) % 360 - 180;
            return Math.abs(relativeAngle) <= visionAngle / 2;
        }
        return false;
    }
}
