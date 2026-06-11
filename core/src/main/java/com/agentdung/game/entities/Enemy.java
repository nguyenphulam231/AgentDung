package com.agentdung.game.entities;

import com.agentdung.game.assets.GameAssets; // Import GameAssets vào hệ thống entity
import com.agentdung.game.enemy.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Enemy extends Entity {
    private EnemyState state;
    private VisionComponent vision;
    private PatrolComponent patrol;
    private EnemyAnimation animation;
    private EnemyAI ai;

    private Player targetPlayer;

    // Cập nhật Constructor để nhận thêm GameAssets từ màn hình chơi (Screen)
    public Enemy(float x, float y, Player targetPlayer, GameAssets assets) {
        super(x, y, 100, 16);
        this.targetPlayer = targetPlayer;

        this.state = new EnemyState(60f); // originalSpeed
        this.vision = new VisionComponent(100f, 60f); // visionRange, visionAngle
        this.patrol = new PatrolComponent(x, y);

        // Truyền assets vào EnemyAnimation để lấy texture dùng chung
        this.animation = new EnemyAnimation(assets);
        this.ai = new EnemyAI();

        this.speed = state.getSpeed();
    }

    public void setViewDistance(float distance) {
        vision.setViewDistance(distance);
    }

    public void setViewAngle(float angle) {
        vision.setViewAngle(angle);
    }

    public void setPatrolRoute(float startX, float startY, float endX, float endY) {
        patrol.setRoute(startX, startY, endX, endY);
    }

    @Override
    public void update(float delta) {
        state.update(delta);
        vision.update(delta);

        // Đọc giá trị protected từ targetPlayer tại đây và truyền vào AI dưới dạng tham số
        float playerSize = (targetPlayer != null) ? targetPlayer.size : 0f;
        ai.updateMovement(delta, position, velocity, size, playerSize, patrol, state, targetPlayer);
        this.angle = ai.getCurrentAngle();

        animation.update(delta, velocity);
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        animation.render(batch, position, size, ai.getCurrentAngle(), state.isStunned(), state.isFleeing());
    }

    public boolean detects(Array<Rectangle> walls) {
        float playerSize = (targetPlayer != null) ? targetPlayer.size : 0f;
        return vision.detects(position, size, playerSize, ai.getCurrentAngle(), targetPlayer, walls, state.isStunned());
    }

    public void drawVision(ShapeRenderer shape, Array<Rectangle> walls) {
        vision.drawVision(shape, position, size, ai.getCurrentAngle(), walls, state.isStunned());
    }

    public void applySpitEffect() {
        vision.setVisionRange(vision.getOriginalVisionRange() * 0.2f);
        state.setSpeed(state.getOriginalSpeed() * 0.5f);
        state.setEffectTimer(3.0f);
    }

    public void applyVomitEffect() {
        vision.setVisionRange(5f);
        state.setStunTimer(2.0f);
        state.setSpeed(state.getOriginalSpeed() * 0.3f);
        state.setEffectTimer(5.0f);
    }

    public void applyPeeEffect() {
        state.setFleeing(true);
        state.setEffectTimer(2.0f);
        state.setSpeed(state.getOriginalSpeed() * 1.8f);
    }

    public void applyPoopEffect() {
        state.setStunTimer(4.0f);
        state.setSpeed(state.getOriginalSpeed() * 0.2f);
        state.setEffectTimer(7.0f);
    }

    public void dispose() {
    }
}
