package com.agentdung.game.enemy;

import com.badlogic.gdx.math.Vector2;

public class PatrolComponent {
    private Vector2 startPoint;
    private Vector2 endPoint;
    private boolean movingToEnd = true;

    public PatrolComponent(float startX, float startY) {
        this.startPoint = new Vector2(startX, startY);
        this.endPoint = new Vector2(startX, startY);
    }

    public void setRoute(float startX, float startY, float endX, float endY) {
        this.startPoint.set(startX, startY);
        this.endPoint.set(endX, endY);
    }

    public Vector2 getTarget() {
        return movingToEnd ? endPoint : startPoint;
    }

    public void toggleTarget() {
        movingToEnd = !movingToEnd;
    }
}
