package com.agentdung.game.screens.play;

import com.agentdung.game.entities.Item;
import com.agentdung.game.entities.VendingMachine;
import java.util.HashMap;
import java.util.Map;

public class PlaySessionState {
    public final Map<Item.ItemType, Integer> inventory = new HashMap<>();

    public boolean hasKey = false;
    public boolean isInventoryOpen = false;
    public boolean isVendingOpen = false;
    public boolean isCaptured = false;
    public boolean isPaused = false;

    public float invisibilityTimer = 0f;
    public float shoesTimer = 0f;
    public float clockTimer = 0f;
    public float lemonTimer = 0f;
    public float orangeTimer = 0f;
    public float carrotTimer = 0f;

    public int amuletCount = 0;
    public VendingMachine activeVending = null;

    public void reset() {
        isCaptured = false;
        isPaused = false;
        isInventoryOpen = false;
        isVendingOpen = false;
        activeVending = null;

        invisibilityTimer = 0f;
        shoesTimer = 0f;
        clockTimer = 0f;
        lemonTimer = 0f;
        orangeTimer = 0f;
        carrotTimer = 0f;

        amuletCount = 0;
        hasKey = false;

        inventory.clear();
        for (Item.ItemType type : Item.ItemType.values()) {
            inventory.put(type, 0);
        }
    }

    public void tickBuffs(float delta) {
        if (invisibilityTimer > 0) invisibilityTimer -= delta;
        if (shoesTimer > 0) shoesTimer -= delta;
        if (clockTimer > 0) clockTimer -= delta;
        if (lemonTimer > 0) lemonTimer -= delta;
        if (orangeTimer > 0) orangeTimer -= delta;
        if (carrotTimer > 0) carrotTimer -= delta;
    }

    public float getLogicDelta(float delta) {
        return clockTimer > 0 ? 0f : delta;
    }

    public boolean handleDetection() {
        if (invisibilityTimer > 0) {
            return false;
        }
        if (amuletCount > 0) {
            amuletCount--;
            inventory.put(Item.ItemType.AMULET, amuletCount);
            invisibilityTimer = 1.5f;
            return false;
        }
        isCaptured = true;
        return true;
    }
}
