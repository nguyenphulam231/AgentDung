package com.agentdung.game.skills;

import com.agentdung.game.projectiles.BlobProjectile;
import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class SpitSkill extends BaseSkill {

    public SpitSkill() {
        // Gọi constructor BaseSkill: Tên "Khạc", tốn 15 mana, màu xanh lơ (Cyan)
        super("Khạc", 15f, Color.CYAN);
    }

    @Override
    protected void handleEffect(Player player, Enemy target, Array<Projectile> projectiles) {
        // Lớp cha đã kiểm tra và trừ 15 mana rồi.
        // Ở đây ta chỉ tạo ra một viên đạn dạng Blob (cục nhầy)
        projectiles.add(new BlobProjectile(
            player.getPosition().x + player.getSize()/2,
            player.getPosition().y + player.getSize()/2,
            player.getAngle(), // Sử dụng angle trực tiếp từ Player
            400,
            this.color // Sử dụng màu Color.CYAN đã định nghĩa ở super()
        ));
    }
}
