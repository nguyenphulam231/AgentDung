package com.agentdung.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class PoopSkill extends BaseSkill {
    @Override
    public void activate(Player player, Enemy target, Array<Projectile> projectiles) {
        if (mana >= 40) {
            mana -= 40;
            // pooping angle
            projectiles.add(new StreamProjectile(
                player.getPosition().x + player.size/2,
                player.getPosition().y + player.size/2,
                player.getAngle() + 180, 150, new Color(0.5f, 0.25f, 0, 1)));
        }
    }
    @Override
    public Color getManaColor() { return new Color(0.5f, 0.25f, 0, 1); }
    @Override
    public String getName() { return "Ị"; }
}
