package com.agentdung.game.skills;

import com.agentdung.game.projectiles.Projectile;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public abstract class Skill {
    public float mana = 100f;
    public float maxMana = 100f;
    public float cost;
    public float regenSpeed = 5f;
    protected boolean canRegen = false; // M???c ?????nh kh??ng t??? h???i mana

    public Skill(float cost) {
        this.cost = cost;
    }

    public Skill(float cost, boolean canRegen) {
        this.cost = cost;
        this.canRegen = canRegen;
    }

    public boolean activate(Player player, Enemy target, Array<Projectile> projectiles) {
        if (mana >= cost) {
            mana -= cost;
            handleEffect(player, target, projectiles);
            return true;
        }
        return false;
    }

    protected abstract void handleEffect(Player player, Enemy target, Array<Projectile> projectiles);

    // Logic h???i mana: Ch??? ch???y n???u canRegen = true
    public void update(float delta) {
        if (canRegen && mana < maxMana) {
            mana += regenSpeed * delta;
            if (mana > maxMana) mana = maxMana;
        }
    }

    // --- H??m c???ng mana tr???c ti???p khi ??n v???t ph???m ---
    public void gainMana(float amount) {
        this.mana = Math.min(this.maxMana, this.mana + amount);
    }

    public float getManaPercent() {
        return mana / maxMana;
    }

    public abstract Color getManaColor();
    public abstract String getName();
    public abstract SkillKind getKind();
}
