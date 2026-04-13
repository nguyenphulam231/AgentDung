package com.agentdung.game.skills;

import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public abstract class Skill {
    // Các thông số dùng chung cho mọi kỹ năng
    public float mana = 100f;
    public float maxMana = 100f;
    public float cost;
    public float regenSpeed = 5f; // Tốc độ hồi mana mỗi giây

    // Khởi tạo cost cho từng kỹ năng cụ thể
    public Skill(float cost) {
        this.cost = cost;
    }

    // ĐỔI THÀNH BOOLEAN: Trả về true nếu đủ mana để thi triển
    public boolean activate(Player player, Enemy target, Array<Projectile> projectiles) {
        if (mana >= cost) {
            mana -= cost;
            handleEffect(player, target, projectiles); // Gọi logic riêng của từng kỹ năng
            return true;
        }
        return false;
    }

    // Mỗi kỹ năng sẽ tự viết logic "chiêu thức" vào đây (vd: tạo đạn, đặt bẫy)
    protected abstract void handleEffect(Player player, Enemy target, Array<Projectile> projectiles);

    // Logic hồi mana dùng chung cho tất cả
    public void update(float delta) {
        if (mana < maxMana) {
            mana += regenSpeed * delta;
            if (mana > maxMana) mana = maxMana;
        }
    }

    public float getManaPercent() {
        return mana / maxMana;
    }

    // Các hàm getter để lớp con tự định nghĩa màu sắc và tên
    public abstract Color getManaColor();
    public abstract String getName();
}
