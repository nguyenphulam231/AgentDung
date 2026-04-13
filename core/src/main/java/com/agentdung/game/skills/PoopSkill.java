package com.agentdung.game.skills;

import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.projectiles.StreamProjectile;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class PoopSkill extends BaseSkill {

    public PoopSkill() {
        // Tên "Ị", tốn 40 mana (dạng bột phát), màu nâu
        super("Ị", 40f, new Color(0.5f, 0.25f, 0, 1));
    }

    @Override
    protected void handleEffect(Player player, Enemy target, Array<Projectile> projectiles) {
        // Lớp cha đã trừ 40 mana rồi, ở đây ta chỉ xử lý tạo vật thể

        // Góc 180 độ so với hướng nhìn để đảm bảo bắn ra từ phía sau
        float backAngle = player.getAngle() + 180;

        projectiles.add(new StreamProjectile(
            player.getPosition().x + player.getSize()/2,
            player.getPosition().y + player.getSize()/2,
            backAngle,
            150,
            this.color // Sử dụng luôn màu đã định nghĩa ở constructor
        ));
    }
}
