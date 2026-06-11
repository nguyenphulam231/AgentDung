package com.agentdung.game.items;

@FunctionalInterface
public interface ItemEffect {
    void apply(ItemEffectContext context);
}
