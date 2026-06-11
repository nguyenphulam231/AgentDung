package com.agentdung.game.entities;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.player.InventoryComponent;
import com.agentdung.game.player.ManaComponent;
import com.agentdung.game.player.PlayerAnimation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Player extends Entity {

    // --- COMPONENTS ---
    private ManaComponent manaComponent;
    private PlayerAnimation animationComponent;
    private InventoryComponent inventoryComponent;

    public Player(float x, float y, AgentDungGame game) {
        // Gọi lên Constructor của Entity (x, y, speed, size)
        super(x, y, 150, 16);

        // Khởi tạo các Component
        this.manaComponent = new ManaComponent(100f, 15f); // maxMana: 100, regenRate: 15
        this.animationComponent = new PlayerAnimation(game);
        this.inventoryComponent = new InventoryComponent(20); // Sức chứa 20 slot
    }

    @Override
    public void update(float delta) {
        boolean isMoving = velocity.len() > 0.1f;

        // Giao việc cho các component xử lý update của riêng chúng
        manaComponent.update(delta);
        animationComponent.update(delta, isMoving);
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

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        boolean isMoving = velocity.len() > 0.1f;
        // Giao việc vẽ hình cho Animation Component
        animationComponent.render(batch, position.x, position.y, size, angle, isMoving);
    }

    // ---- ĐẠI DIỆN CHO MANA COMPONENT (DELEGATION) ----
    // Giữ nguyên các hàm này để các file cũ gọi đến Player.useMana() không bị lỗi

    public boolean useMana(float amount) {
        return manaComponent.useMana(amount);
    }

    public float getCurrentMana() {
        return manaComponent.getCurrentMana();
    }

    public float getManaPercent() {
        return manaComponent.getManaPercent();
    }

    // ---- ĐẠI DIỆN CHO INVENTORY COMPONENT ----
    public InventoryComponent getInventory() {
        return inventoryComponent;
    }

    // ---- KẾ THỪA TỪ ENTITY ----
    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void dispose() {
        if (animationComponent != null) {
            animationComponent.dispose();
        }
    }
}
