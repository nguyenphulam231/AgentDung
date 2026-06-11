package com.agentdung.game.player;

public class ManaComponent {
    private float maxMana;
    private float currentMana;
    private float regenRate;

    public ManaComponent(float maxMana, float regenRate) {
        this.maxMana = maxMana;
        this.currentMana = maxMana; // Khởi tạo đầy mana
        this.regenRate = regenRate;
    }

    public void update(float delta) {
        // Tự động hồi phục nội lực theo thời gian
        if (currentMana < maxMana) {
            currentMana = Math.min(maxMana, currentMana + regenRate * delta);
        }
    }

    public boolean useMana(float amount) {
        if (currentMana >= amount) {
            currentMana -= amount;
            return true;
        }
        return false;
    }

    public float getCurrentMana() {
        return currentMana;
    }

    public float getMaxMana() {
        return maxMana;
    }

    public float getManaPercent() {
        return currentMana / maxMana;
    }
}
