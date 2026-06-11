package com.agentdung.game.enemy;

import com.agentdung.game.entities.Player;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class EnemyAI {
    private float currentAngle = 0;
    private float rotationSpeed = 180f;

    public EnemyAI() {
        this.currentAngle = 0;
    }

    public void updateMovement(float delta, Vector2 position, Vector2 velocity, float enemySize, float playerSize, PatrolComponent patrol, EnemyState state, Player targetPlayer) {
        if (state.isStunned()) {
            velocity.set(0, 0);
            return;
        }

        Vector2 target = patrol.getTarget();
        float dx = target.x - position.x;
        float dy = target.y - position.y;
        float dist = Vector2.dst(position.x, position.y, target.x, target.y);

        float targetAngle;
        if (state.isFleeing() && targetPlayer != null) {
            float fdx = (position.x + enemySize / 2) - (targetPlayer.getPosition().x + playerSize / 2);
            float fdy = (position.y + enemySize / 2) - (targetPlayer.getPosition().y + playerSize / 2);
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

        if (dist < 2 && !state.isFleeing()) {
            patrol.toggleTarget();
        }

        if (state.isFleeing() || Math.abs(angleDiff) < 20) {
            velocity.set(MathUtils.cosDeg(currentAngle) * state.getSpeed(), MathUtils.sinDeg(currentAngle) * state.getSpeed());
        } else {
            velocity.set(0, 0);
        }
    }

    public float getCurrentAngle() { return currentAngle; }
    public void setCurrentAngle(float angle) { this.currentAngle = angle; }
}
