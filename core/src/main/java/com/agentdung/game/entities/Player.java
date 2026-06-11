package com.agentdung.game.entities;

import com.agentdung.game.core.AgentDungGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Player extends Entity {
    private float maxMana = 100f;
    private float currentMana = 100f;
    private float regenRate = 15f;

    // --- SPRITE SHEET ---
    private Texture spriteSheet;
    private Animation<TextureRegion> walkDown, walkUp, walkRight, walkLeft;
    private TextureRegion idleDown, idleUp, idleRight, idleLeft;
    private float stateTime = 0;

    // Kích thước vẽ lên màn hình
    private static final float DRAW_W = 32f;
    private static final float DRAW_H = 33f;

    public Player(float x, float y, AgentDungGame game) {
        // Gọi lên Constructor của Entity (x, y, speed, size)
        super(x, y, 150, 16);

        // Tự động dựng đường dẫn động dựa trên cấu hình nhân vật đang được chọn
        String path = "images/player" + game.selectedCharacterId + "_" + game.selectedVariantId + ".png";
        Gdx.app.log("Player", "Đang nạp sprite sheet động: " + path);

        spriteSheet = new Texture(Gdx.files.internal(path));
        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        int tileW = 32;
        int tileH = 32;

        // Sử dụng hàm split có sẵn của LibGDX (tự động cắt theo lưới)
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, tileW, tileH);

        // ---- IDLE FRAMES ----
        // Hàng 0: Nam, 1: Đông, 2: Bắc
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

    /**
     * HÀM UPDATE CHUẨN ĐA HÌNH OOP:
     * Loại bỏ hoàn toàn tham số 'player' và 'walls' dư thừa.
     * Chỉ xử lý việc tự hồi phục nội lực (mana) và đếm thời gian hoạt ảnh di chuyển.
     */
    @Override
    public void update(float delta) {
        // Tự động hồi phục nội lực theo thời gian
        if (currentMana < maxMana) {
            currentMana = Math.min(maxMana, currentMana + regenRate * delta);
        }

        // Cập nhật thời gian hoạt ảnh nếu nhân vật đang thực sự di chuyển
        if (velocity.len() > 0.1f) {
            stateTime += delta;
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Hàm hành vi: Cho phép InputHandler hoặc hệ thống điều khiển cập nhật vector vận tốc.
     * Đảm bảo tính đóng gói bằng cách chuẩn hóa (normalize) vector để tránh lỗi đi chéo nhanh hơn đi thẳng.
     */
    public void setMovementDirection(float x, float y) {
        this.velocity.set(x, y);
        if (this.velocity.len() > 0.1f) {
            this.velocity.nor();
        }
    }

    /**
     * Chọn frame theo góc và trạng thái di chuyển.
     * angle từ atan2 của LibGDX:
     * 0° = Đông, 90° = Bắc, 180° = Tây, 270° = Nam
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

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        TextureRegion currentFrame = getCurrentFrame();
        float drawX = position.x + (size / 2f) - (DRAW_W / 2f);
        float drawY = position.y;
        batch.draw(currentFrame, drawX, drawY, DRAW_W, DRAW_H);
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
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }
}
