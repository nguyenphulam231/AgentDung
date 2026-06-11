package com.agentdung.game.skills;

import com.badlogic.gdx.graphics.Color;

public abstract class BaseSkill extends Skill {
    protected float regenRate = 10f;
    protected String name;
    protected Color color;
    protected final SkillKind kind;

    public BaseSkill(String name, float cost, Color color, SkillKind kind) {
        this(name, cost, color, false, kind);
    }

    public BaseSkill(String name, float cost, Color color, boolean canRegen, SkillKind kind) {
        super(cost, canRegen);
        this.name = name;
        this.color = color;
        this.kind = kind;
        if (canRegen) {
            this.regenSpeed = regenRate;
        }
    }

    @Override
    public SkillKind getKind() {
        return kind;
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
