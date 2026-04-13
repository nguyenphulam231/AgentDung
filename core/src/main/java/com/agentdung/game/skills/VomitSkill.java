package com.agentdung.game.skills;

import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.projectiles.StreamProjectile;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class VomitSkill extends BaseSkill {

    public VomitSkill() {
        // Tên "Nôn", Cost 0.8f (tốn mana theo thời gian xịt), màu trắng
        super("Nôn", 0.8f, Color.WHITE);
    }

    @Override
    protected void handleEffect(Player player, Enemy target, Array<Projectile> projectiles) {
        // Lớp cha đã lo phần check mana.
        // Ở đây ta tạo tia nôn màu trắng, tốc độ chậm hơn đái một chút (300) để tạo cảm giác "nặng"
        projectiles.add(new StreamProjectile(
            player.getPosition().x + player.getSize()/2,
            player.getPosition().y + player.getSize()/2,
            player.getAngle(),
            300,
            this.color
        ));
    }
}
