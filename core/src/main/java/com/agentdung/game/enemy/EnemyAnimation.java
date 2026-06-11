package com.agentdung.game.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class EnemyAnimation {
    private Texture spriteSheet;
    private Animation<TextureRegion> walkDown, walkUp, walkRight, walkLeft;
    private TextureRegion idleDown, idleUp, idleRight, idleLeft;
    private float stateTime = 0;

    private static final int FRAME_COLS = 6;
    private static final int TILE_W = 32;
    private static final int[] ROW_Y = { 0, 29, 59, 92, 125, 155 };
    private static final int[] ROW_H = { 29, 30, 33, 33, 30, 165 };

    private static final float DRAW_W = 32f;
    private static final float DRAW_H = 33f;

    public EnemyAnimation() {
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

        idleDown = rows[0][0];
        idleRight = rows[1][0];
        idleUp = rows[2][0];
        idleLeft = new TextureRegion(idleRight);
        idleLeft.flip(true, false);

        float frameDuration = 0.1f;
        walkDown = new Animation<>(frameDuration, rows[3]);
        walkRight = new Animation<>(frameDuration, rows[4]);
        walkUp = new Animation<>(frameDuration, rows[2]);

        walkDown.setPlayMode(Animation.PlayMode.LOOP);
        walkRight.setPlayMode(Animation.PlayMode.LOOP);
        walkUp.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] leftFrames = new TextureRegion[FRAME_COLS];
        for (int i = 0; i < FRAME_COLS; i++) {
            leftFrames[i] = new TextureRegion(rows[4][i]);
            leftFrames[i].flip(true, false);
        }
        walkLeft = new Animation<>(frameDuration, leftFrames);
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float delta, Vector2 velocity) {
        if (velocity.len() > 0.1f) {
            stateTime += delta;
        }
    }

    public TextureRegion getCurrentFrame(float currentAngle, boolean isStunned) {
        float a = ((currentAngle % 360) + 360) % 360;

        if (isStunned) {
            if (a < 45 || a >= 315) return idleRight;
            else if (a < 135) return idleUp;
            else if (a < 225) return idleLeft;
            else return idleDown;
        }

        if (a < 45 || a >= 315) return walkRight.getKeyFrame(stateTime);
        else if (a < 135) return walkUp.getKeyFrame(stateTime);
        else if (a < 225) return walkLeft.getKeyFrame(stateTime);
        else return walkDown.getKeyFrame(stateTime);
    }

    public void render(SpriteBatch batch, Vector2 position, float size, float currentAngle, boolean isStunned, boolean isFleeing) {
        TextureRegion currentFrame = getCurrentFrame(currentAngle, isStunned);
        float drawX = position.x + (size / 2f) - (DRAW_W / 2f);
        float drawY = position.y;

        if (isStunned) {
            batch.setColor(Color.PURPLE);
        } else if (isFleeing) {
            batch.setColor(Color.BLUE);
        } else {
            batch.setColor(Color.WHITE);
        }

        batch.draw(currentFrame, drawX, drawY, DRAW_W, DRAW_H);
        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }
}
