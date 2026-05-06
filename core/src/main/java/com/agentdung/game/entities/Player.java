package com.agentdung.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player extends Entity {
    private float maxMana = 100f;
    private float currentMana = 100f;
    private float regenRate = 15f;

    // --- SPRITE SHEET ---
    private Texture spriteSheet;
    private Animation<TextureRegion> walkDown, walkUp, walkRight, walkLeft;
    private TextureRegion idleDown, idleUp, idleRight, idleLeft;
    private float stateTime = 0;

    // Kích thước mỗi cột (đều nhau)
    private static final int FRAME_COLS = 6;
    private static final int TILE_W     = 32;

    // Boundary Y thực tế của từng hàng (phân tích từ file PNG)
    // Row 0: idle Nam,  Row 1: idle Đông,  Row 2: idle Bắc
    // Row 3: chạy Nam,  Row 4: chạy Đông,  Row 5: chạy Bắc (trống)
    private static final int[] ROW_Y = { 0,  29,  59,  92, 125, 155 };
    private static final int[] ROW_H = { 29,  30,  33,  33,  30, 165 };

    // Kích thước vẽ lên màn hình — dùng hàng cao nhất để thống nhất
    private static final float DRAW_W = 32f;
    private static final float DRAW_H = 33f; // cao nhất trong các hàng có nội dung

    public Player(float x, float y) {
        super(x, y, 150, 16);

        spriteSheet = new Texture(Gdx.files.internal("images/Player1.png"));
        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Cắt thủ công từng hàng theo boundary thực tế
        TextureRegion[][] rows = new TextureRegion[5][FRAME_COLS];
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < FRAME_COLS; c++) {
                rows[r][c] = new TextureRegion(
                    spriteSheet,
                    c * TILE_W,   // x
                    ROW_Y[r],     // y
                    TILE_W,       // width
                    ROW_H[r]      // height đúng của hàng đó
                );
            }
        }

        // ---- IDLE FRAMES (frame đầu mỗi hàng) ----
        idleDown  = rows[0][0]; // Hàng 0: mặt về phía mình (Nam)
        idleRight = rows[1][0]; // Hàng 1: chếch phải (Đông)
        idleUp    = rows[2][0]; // Hàng 2: quay lưng (Bắc)

        // Tây = flip ngang Đông
        idleLeft = new TextureRegion(idleRight);
        idleLeft.flip(true, false);

        // ---- WALK ANIMATIONS ----
        float frameDuration = 0.1f;

        walkDown  = new Animation<>(frameDuration, rows[3]); // Hàng 3: chạy Nam
        walkRight = new Animation<>(frameDuration, rows[4]); // Hàng 4: chạy Đông
        // Hàng 5 còn trống → tạm dùng idle Bắc lặp lại
        // TODO: đổi thành rows[5] khi vẽ xong animation chạy Bắc
        walkUp    = new Animation<>(frameDuration, rows[2]);

        walkDown.setPlayMode(Animation.PlayMode.LOOP);
        walkRight.setPlayMode(Animation.PlayMode.LOOP);
        walkUp.setPlayMode(Animation.PlayMode.LOOP);

        // Chạy Tây = flip từng frame của chạy Đông
        TextureRegion[] leftFrames = new TextureRegion[FRAME_COLS];
        for (int i = 0; i < FRAME_COLS; i++) {
            leftFrames[i] = new TextureRegion(rows[4][i]);
            leftFrames[i].flip(true, false);
        }
        walkLeft = new Animation<>(frameDuration, leftFrames);
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float delta, Player player, Array<Rectangle> walls) {
        if (currentMana < maxMana) {
            currentMana = Math.min(maxMana, currentMana + regenRate * delta);
        }
        if (velocity.len() > 0.1f) {
            stateTime += delta;
        }
    }

    /**
     * Chọn frame theo góc và trạng thái di chuyển.
     * angle từ atan2 của LibGDX:
     *   0°   = Đông,  90° = Bắc,  180° = Tây,  270° = Nam
     */
    private TextureRegion getCurrentFrame() {
        float a = ((angle % 360) + 360) % 360;

        if (velocity.len() > 0.1f) {
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

    /**
     * Vẽ sprite nhân vật.
     * Căn ngang giữa hitbox, chân cố định tại position.y.
     */
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        float drawX = position.x + (size / 2f) - (DRAW_W / 2f);
        float drawY = position.y;
        batch.draw(currentFrame, drawX, drawY, DRAW_W, DRAW_H);
    }

    /**
     * Vẽ thanh Mana phía trên đầu nhân vật.
     */
    @Override
    public void render(ShapeRenderer shape) {
        float barX = position.x + (size / 2f) - (DRAW_W / 2f);
        float barY = position.y + DRAW_H + 2;

        shape.setColor(Color.GRAY);
        shape.rect(barX, barY, DRAW_W, 3);
        shape.setColor(Color.CYAN);
        shape.rect(barX, barY, DRAW_W * (currentMana / maxMana), 3);
    }

    // ---- PUBLIC HELPERS ----

    public boolean useMana(float amount) {
        if (currentMana >= amount) {
            currentMana -= amount;
            return true;
        }
        return false;
    }

    public float getCurrentMana()      { return currentMana; }
    public float getManaPercent()      { return currentMana / maxMana; }
    public float getAngle()            { return angle; }
    public void  setAngle(float angle) { this.angle = angle; }

    public void dispose() {
        spriteSheet.dispose();
    }
}
