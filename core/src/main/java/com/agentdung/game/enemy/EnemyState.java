package com.agentdung.game.enemy;

public class EnemyState {
    private float effectTimer = 0;
    private float stunTimer = 0;
    private boolean isFleeing = false;
    private float originalSpeed;
    private float speed;

    public EnemyState(float originalSpeed) {
        this.originalSpeed = originalSpeed;
        this.speed = originalSpeed;
    }

    public void update(float delta) {
        if (stunTimer > 0) {
            stunTimer -= delta;
        }

        if (effectTimer > 0) {
            effectTimer -= delta;
        } else {
            this.speed = originalSpeed;
            this.isFleeing = false;
        }
    }

    public boolean isStunned() { return stunTimer > 0; }
    public boolean isFleeing() { return isFleeing; }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void setFleeing(boolean fleeing) { this.isFleeing = fleeing; }
    public void setStunTimer(float timer) { this.stunTimer = timer; }
    public void setEffectTimer(float timer) { this.effectTimer = timer; }
    public float getOriginalSpeed() { return originalSpeed; }
}
