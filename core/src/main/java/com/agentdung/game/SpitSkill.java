package com.agentdung.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class SpitSkill extends BaseSkill {
    @Override
    public void activate(Player player, Enemy target, Array<Projectile> projectiles) {
        if (mana >= 15) {
            mana -= 15;
            // blob
            projectiles.add(new BlobProjectile(
                player.getPosition().x + player.size/2,
                player.getPosition().y + player.size/2,
                player.getAngle(), 400, Color.CYAN));
        }
    }
    @Override
    public Color getManaColor() { return Color.CYAN; }
    @Override
    public String getName() { return "Khạc"; }
}
