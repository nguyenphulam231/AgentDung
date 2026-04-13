package com.agentdung.game.skills;

import com.badlogic.gdx.graphics.Color;

// 1. Đổi từ 'implements' sang 'extends' Skill (vì Skill giờ là abstract class)
public abstract class BaseSkill extends Skill {
    protected float maxMana = 100f;
    protected float regenRate = 10f;
    protected String name;
    protected Color color;

    // 2. Tạo Constructor để các lớp con (Spit, Poop...) truyền thông số lên
    public BaseSkill(String name, float cost, Color color) {
        super(cost); // Truyền cost lên lớp cha Skill để nó quản lý mana
        this.name = name;
        this.color = color;
    }

    // 3. Ghi đè logic update để dùng regenRate của riêng BaseSkill nếu muốn
    @Override
    public void update(float delta) {
        if (mana < maxMana) {
            mana += regenRate * delta;
            if (mana > maxMana) mana = maxMana;
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
