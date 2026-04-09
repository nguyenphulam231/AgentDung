package com.agentdung.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class PeeSkill extends BaseSkill {
    @Override
    public void activate(Player player, Enemy target, Array<Projectile> projectiles) {
        if (mana >= 1) {
            mana -= 1;
            projectiles.add(new StreamProjectile(
                player.getPosition().x + player.size/2,
                player.getPosition().y + player.size/2,
                player.getAngle(), 500, Color.YELLOW));
        }
    }
    @Override
    public Color getManaColor() { return Color.YELLOW; }
    @Override
    public String getName() { return "Đái"; }
}
