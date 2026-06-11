package com.agentdung.game.player;

import com.agentdung.game.assets.GameAssets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerAnimation {
    private Animation<TextureRegion> walkDown, walkUp, walkRight, walkLeft;
    private TextureRegion idleDown, idleUp, idleRight, idleLeft;
    private float stateTime = 0;

    // Kích thước vẽ lên màn hình
    private static final float DRAW_W = 32f;
    private static final float DRAW_H = 33f;

    // Truyền thẳng GameAssets vào thay vì AgentDungGame
    public PlayerAnimation(GameAssets assets) {

        // Lấy Texture từ GameAssets
        Texture spriteSheet = assets.getPlayerTexture();

        int tileW = 32;
        int tileH = 32;

        // Sử dụng hàm split có sẵn của LibGDX
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, tileW, tileH);

        // ---- IDLE FRAMES ----
        idleDown  = tmp[0][0];
        idleRight = tmp[1][0];
        idleUp    = tmp[2][0];

        idleLeft = new TextureRegion(idleRight);
        idleLeft.flip(true, false);

        // ---- WALK ANIMATIONS ----
        float frameDuration = 0.1f;
        int cols = 6;

        walkDown  = new Animation<>(frameDuration, tmp[3]);
        walkRight = new Animation<>(frameDuration, tmp[4]);
        walkUp    = new Animation<>(frameDuration, tmp[5]);

        walkDown.setPlayMode(Animation.PlayMode.LOOP);
        walkRight.setPlayMode(Animation.PlayMode.LOOP);
        walkUp.setPlayMode(Animation.PlayMode.LOOP);

        // Chạy Tây (Lật Sprite chạy Đông)
        TextureRegion[] leftFrames = new TextureRegion[cols];
        for (int i = 0; i < cols; i++) {
            leftFrames[i] = new TextureRegion(tmp[4][i]);
            leftFrames[i].flip(true, false);
        }
        walkLeft = new Animation<>(frameDuration, leftFrames);
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float delta, boolean isMoving) {
        if (isMoving) {
            stateTime += delta;
        }
    }

    private TextureRegion getCurrentFrame(float angle, boolean isMoving) {
        float a = ((angle % 360) + 360) % 360;

        if (isMoving) {
            if      (a < 45 || a >= 315) return walkRight.getKeyFrame(stateTime);
            else if (a < 135)            return walkUp.getKeyFrame(stateTime);
            else if (a < 225)            return walkLeft.getKeyFrame(stateTime);
            else                         return walkDown.getKeyFrame(stateTime);
        } else {
            if      (a < 45 || a >= 315) return idleRight;
            else if (a < 135)            return idleUp;
            else if (a < 225)            return idleLeft;
            else                         return idleDown;
        }
    }

    public void render(SpriteBatch batch, float posX, float posY, float size, float angle, boolean isMoving) {
        TextureRegion currentFrame = getCurrentFrame(angle, isMoving);
        float drawX = posX + (size / 2f) - (DRAW_W / 2f);
        float drawY = posY;
        batch.draw(currentFrame, drawX, drawY, DRAW_W, DRAW_H);
    }

    // Đã xóa hàm dispose() ở đây.
    // Việc giải phóng bộ nhớ giờ là trách nhiệm của GameAssets.
}
