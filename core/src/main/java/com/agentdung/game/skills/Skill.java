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

    public Skill(float cost) {
        this.cost = cost;
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

    // Logic hồi mana mặc định theo thời gian
    public void update(float delta) {
        if (mana < maxMana) {
            mana += regenSpeed * delta;
            if (mana > maxMana) mana = maxMana;
        }
    }

    // --- Hàm cộng mana trực tiếp khi ăn vật phẩm ---
    public void gainMana(float amount) {
        this.mana = Math.min(this.maxMana, this.mana + amount);
    }

    public float getManaPercent() {
        return mana / maxMana;
    }

    public abstract Color getManaColor();
    public abstract String getName();
}
