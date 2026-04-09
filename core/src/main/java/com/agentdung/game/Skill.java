package com.agentdung.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public interface Skill {

    void activate(Player player, Enemy target, Array<Projectile> projectiles);

    void update(float delta);
    float getManaPercent();
    Color getManaColor();
    String getName();
}
