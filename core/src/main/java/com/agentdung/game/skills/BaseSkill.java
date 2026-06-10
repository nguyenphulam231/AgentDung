package com.agentdung.game.skills;

import com.badlogic.gdx.graphics.Color;

public abstract class BaseSkill extends Skill {
    protected float regenRate = 10f; // Tốc độ hồi riêng của BaseSkill
    protected String name;
    protected Color color;

    public BaseSkill(String name, float cost, Color color) {
        super(cost);
        this.name = name;
        this.color = color;
    }

    // Ghi đè logic update để chỉ cho phép SpitSkill (Hydro-shot) tự hồi mana
    @Override
    public void update(float delta) {
        // SpitSkill(Hydro-shot) thì mới cho tự động hồi theo thời gian
        if (this instanceof SpitSkill) {
            if (mana < maxMana) {
                mana += regenRate * delta;
                if (mana > maxMana) mana = maxMana;
            }
        }

    }

    @Override
    public float getManaPercent() {
        return mana / maxMana;
    }

    @Override
    public Color getManaColor() {
        return color;
    }

    @Override
    public String getName() {
        return name;
    }
}
