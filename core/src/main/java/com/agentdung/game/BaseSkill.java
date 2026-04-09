package com.agentdung.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public abstract class BaseSkill implements Skill {
    protected float mana = 100f;
    protected float maxMana = 100f;
    protected float regenRate = 10f;

    @Override
    public void update(float delta) {
        if (mana < maxMana) mana += regenRate * delta;
    }

    @Override
    public float getManaPercent() { return mana / maxMana; }

}
