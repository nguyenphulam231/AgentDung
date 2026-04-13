package com.agentdung.game.skills;

import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.projectiles.StreamProjectile;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class PeeSkill extends BaseSkill {

    public PeeSkill() {
        // Gọi constructor của BaseSkill: Tên "Đái", Cost 0.5 (cho mỗi frame xịt), màu vàng
        super("Đái", 0.5f, Color.YELLOW);
    }

    @Override
    protected void handleEffect(Player player, Enemy target, Array<Projectile> projectiles) {
        // Lớp cha Skill đã check mana và trừ mana rồi, ở đây chỉ việc xịt nước!
        projectiles.add(new StreamProjectile(
            player.getPosition().x + player.getSize()/2,
            player.getPosition().y + player.getSize()/2,
            player.getAngle(), // Đảm bảo dùng đúng biến angle của player
            500,
            Color.YELLOW
        ));
    }

    // Các hàm getManaColor() và getName() đã được BaseSkill xử lý thông qua Constructor ở trên,
    // nên bạn có thể xóa chúng đi để code ngắn gọn hơn, trừ khi bạn muốn override đặc biệt.
}
