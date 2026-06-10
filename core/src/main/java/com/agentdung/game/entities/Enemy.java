package com.agentdung.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    // --- SPRITE SYSTEM CHO ENEMY ---
    private Texture spriteSheet;
    private Animation<TextureRegion> walkDown, walkUp, walkRight, walkLeft;
    private TextureRegion idleDown, idleUp, idleRight, idleLeft;
    private float stateTime = 0;

    private static final int FRAME_COLS = 6;
    private static final int TILE_W     = 32;

    // Áp dụng chung quy luật boundary từ Player1 sang Patroler
    private static final int[] ROW_Y = { 0,  29,  59,  92, 125, 155 };
    private static final int[] ROW_H = { 29,  30,  33,  33,  30, 165 };

    private static final float DRAW_W = 32f;
    private static final float DRAW_H = 33f;

    public Enemy(float x, float y) {
        super(x, y, 100, 16);
        this.currentAngle = 0;
        this.visionRange = 100;
        this.visionAngle = 60;
        this.originalVisionRange = visionRange;
        this.speed = originalSpeed;
        this.startPoint = new Vector2(x, y);
        this.endPoint = new Vector2(x, y);

        // ---- KHỞI TẠO VÀ CẮT SPRITE CHO ENEMY ----
        spriteSheet = new Texture(Gdx.files.internal("images/Patroler.png"));
        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        TextureRegion[][] rows = new TextureRegion[5][FRAME_COLS];
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < FRAME_COLS; c++) {
                rows[r][c] = new TextureRegion(
                    spriteSheet,
                    c * TILE_W,
                    ROW_Y[r],
                    TILE_W,
                    ROW_H[r]
                );
            }
        }

        // Idle Frames
        idleDown  = rows[0][0];
        idleRight = rows[1][0];
        idleUp    = rows[2][0];
        idleLeft  = new TextureRegion(idleRight);
        idleLeft.flip(true, false);

        // Walk Animations
        float frameDuration = 0.1f;
        walkDown  = new Animation<>(frameDuration, rows[3]);
        walkRight = new Animation<>(frameDuration, rows[4]);
        walkUp    = new Animation<>(frameDuration, rows[2]);

        walkDown.setPlayMode(Animation.PlayMode.LOOP);
        walkRight.setPlayMode(Animation.PlayMode.LOOP);
        walkUp.setPlayMode(Animation.PlayMode.LOOP);

        // Lật góc chạy bên trái từ bên phải
        TextureRegion[] leftFrames = new TextureRegion[FRAME_COLS];
        for (int i = 0; i < FRAME_COLS; i++) {
            leftFrames[i] = new TextureRegion(rows[4][i]);
            leftFrames[i].flip(true, false);
        }
        walkLeft = new Animation<>(frameDuration, leftFrames);
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);
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
            return; // Khi bị stun thì không di chuyển và không chạy animation
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
        if (isFleeing || Math.abs(angleDiff) < 20) {
            velocity.set(MathUtils.cosDeg(currentAngle) * speed, MathUtils.sinDeg(currentAngle) * speed);
        }

        // Tăng thời gian chuyển động nếu có di chuyển
        if (velocity.len() > 0.1f) {
            stateTime += delta;
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

    /**
     * Thuật toán lấy frame dựa vào góc quay tương tự Player.
     */
    private TextureRegion getCurrentFrame() {
        float a = ((currentAngle % 360) + 360) % 360;

        // Nếu bị choáng (stun), hiển thị dạng đứng yên theo hướng hiện tại
        if (stunTimer > 0) {
            if      (a < 45 || a >= 315) return idleRight;
            else if (a < 135)            return idleUp;
            else if (a < 225)            return idleLeft;
            else                         return idleDown;
        }

        // Khi đang di chuyển tuần tra hoặc bỏ chạy
        if      (a < 45 || a >= 315) return walkRight.getKeyFrame(stateTime);
        else if (a < 135)            return walkUp.getKeyFrame(stateTime);
        else if (a < 225)            return walkLeft.getKeyFrame(stateTime);
        else                         return walkDown.getKeyFrame(stateTime);
    }

    /**
     * ĐỒNG BỘ ĐA HÌNH SONG HÀNH 2 THAM SỐ:
     * Chuyển đổi từ hàm 'draw' cũ thành hàm 'render' chuẩn giao kèo lớp cha Entity.
     * Nhận vào cả batch và shapeRenderer từ hệ thống quản lý truyền xuống.
     */
    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        TextureRegion currentFrame = getCurrentFrame();
        float drawX = position.x + (size / 2f) - (DRAW_W / 2f);
        float drawY = position.y;

        // Tùy biến màu sắc Sprite dựa vào trạng thái hiệu ứng (Sử dụng tính năng setColor của SpriteBatch)
        if (stunTimer > 0) {
            batch.setColor(Color.PURPLE); // Ám tím khi bị choáng
        } else if (isFleeing) {
            batch.setColor(Color.BLUE);   // Ám xanh khi hoảng sợ bỏ chạy
        } else {
            batch.setColor(Color.WHITE);  // Trở lại bình thường
        }

        batch.draw(currentFrame, drawX, drawY, DRAW_W, DRAW_H);

        // Trả lại màu mặc định cho Batch để không làm ảnh hưởng tới các Sprite vẽ phía sau
        batch.setColor(Color.WHITE);
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

    public void drawVision(ShapeRenderer shape, Array<Rectangle> walls) {
        if (stunTimer <= 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

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

            shape.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            shape.begin(ShapeRenderer.ShapeType.Filled);
        }
    }

    public void applySpitEffect() { this.visionRange = originalVisionRange * 0.2f; this.speed = originalSpeed * 0.5f; this.effectTimer = 3.0f; }
    public void applyVomitEffect() { this.visionRange = 5; this.stunTimer = 2.0f; this.speed = originalSpeed * 0.3f; this.effectTimer = 5.0f; }
    public void applyPeeEffect() { this.isFleeing = true; this.effectTimer = 2.0f; this.speed = originalSpeed * 1.8f; }
    public void applyPoopEffect() { this.stunTimer = 4.0f; this.speed = originalSpeed * 0.2f; this.effectTimer = 7.0f; }

    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }
}
