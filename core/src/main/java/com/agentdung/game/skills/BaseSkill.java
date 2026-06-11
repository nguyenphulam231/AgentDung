package com.agentdung.game.skills;

import com.badlogic.gdx.graphics.Color;

public abstract class BaseSkill extends Skill {
    protected float regenRate = 10f; // T???c ????? h???i ri??ng c???a BaseSkill
    protected String name;
    protected Color color;

    public BaseSkill(String name, float cost, Color color) {
        this(name, cost, color, false);
    }

    public BaseSkill(String name, float cost, Color color, boolean canRegen) {
        super(cost, canRegen);
        this.name = name;
        this.color = color;
        if (canRegen) {
            this.regenSpeed = regenRate;
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
