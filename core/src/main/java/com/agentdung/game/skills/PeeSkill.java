package com.agentdung.game.skills;

import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.projectiles.StreamProjectile;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class PeeSkill extends BaseSkill {

    public PeeSkill() {
        // D??ng Unicode escape cho "????i"
        super("\u0110\u00e1i", 0.5f, Color.YELLOW, SkillKind.PEE);
    }

    @Override
    protected void handleEffect(Player player, Enemy target, Array<Projectile> projectiles) {
        projectiles.add(new StreamProjectile(
            player.getPosition().x + player.getSize()/2,
            player.getPosition().y + player.getSize()/2,
            player.getAngle(),
            500,
            Color.YELLOW
        ));
    }

}
