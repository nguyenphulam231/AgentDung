package com.agentdung.game.enemy;

import com.agentdung.game.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class VisionComponent {
    private float visionRange;
    private float visionAngle;
    private float originalVisionRange;
    private float visionRecoverySpeed = 25f;

    public VisionComponent(float visionRange, float visionAngle) {
        this.visionRange = visionRange;
        this.visionAngle = visionAngle;
        this.originalVisionRange = visionRange;
    }

    public void update(float delta) {
        if (this.visionRange < originalVisionRange) {
            this.visionRange = Math.min(originalVisionRange, visionRange + visionRecoverySpeed * delta);
        }
    }

    public void setViewDistance(float distance) {
        this.visionRange = distance;
        this.originalVisionRange = distance;
    }

    public void setViewAngle(float angle) {
        this.visionAngle = angle;
    }

    public float getVisionRange() { return visionRange; }
    public void setVisionRange(float visionRange) { this.visionRange = visionRange; }
    public float getOriginalVisionRange() { return originalVisionRange; }

    public boolean detects(Vector2 enemyPos, float enemySize, float playerSize, float currentAngle, Player targetPlayer, Array<Rectangle> walls, boolean isStunned) {
        if (isStunned || targetPlayer == null) return false;

        float pX = targetPlayer.getPosition().x + playerSize / 2;
        float pY = targetPlayer.getPosition().y + playerSize / 2;
        float eX = enemyPos.x + enemySize / 2;
        float eY = enemyPos.y + enemySize / 2;
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

    public Vector2 getRaycastHit(float startX, float startY, float angle, float range, Array<Rectangle> walls) {
        float endX = startX + MathUtils.cosDeg(angle) * range;
        float endY = startY + MathUtils.sinDeg(angle) * range;
        int steps = (int)(range / 4);
        for (int i = 1; i <= steps; i++) {
            float checkX = startX + (endX - startX) * ((float)i / steps);
            float checkY = startY + (endY - startY) * ((float)i / steps);
            for (Rectangle wall : walls) {
                if (wall.contains(checkX, checkY)) return new Vector2(checkX, checkY);
            }
        }
        return new Vector2(endX, endY);
    }

    public void drawVision(ShapeRenderer shape, Vector2 enemyPos, float enemySize, float currentAngle, Array<Rectangle> walls, boolean isStunned) {
        if (!isStunned) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shape.setColor(1f, 0.85f, 0.2f, 0.4f);

            float eX = enemyPos.x + enemySize / 2;
            float eY = enemyPos.y + enemySize / 2;
            int segments = 20;
            float startAngle = currentAngle - visionAngle / 2;
            for (int i = 0; i < segments; i++) {
                float a1 = startAngle + (visionAngle / segments) * i;
                float a2 = startAngle + (visionAngle / segments) * (i + 1);
                Vector2 p1 = getRaycastHit(eX, eY, a1, visionRange, walls);
                Vector2 p2 = getRaycastHit(eX, eY, a2, visionRange, walls);
                shape.triangle(eX, eY, p1.x, p1.y, p2.x, p2.y);
            }

            shape.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            shape.begin(ShapeRenderer.ShapeType.Filled);
        }
    }
}
