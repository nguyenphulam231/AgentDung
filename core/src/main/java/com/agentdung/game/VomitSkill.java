package com.agentdung.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class VomitSkill extends BaseSkill {
    @Override
    public void activate(Player player, Enemy target, Array<Projectile> projectiles) {
        if (mana >= 5) {
            mana -= 5;
            projectiles.add(new StreamProjectile(
                player.getPosition().x + player.size/2,
                player.getPosition().y + player.size/2,
                player.getAngle(), 300, Color.WHITE));
        }
    }
    @Override
    public Color getManaColor() { return Color.WHITE; }
    @Override
    public String getName() { return "Nôn"; }
}
