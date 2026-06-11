package com.agentdung.game.items;

import com.agentdung.game.entities.Item;
import com.agentdung.game.skills.SkillKind;
import com.agentdung.game.skills.SkillManaHelper;
import java.util.EnumMap;
import java.util.Map;

public final class ItemEffectRegistry {
    private static final Map<Item.ItemType, ItemEffect> EFFECTS = new EnumMap<>(Item.ItemType.class);

    static {
        EFFECTS.put(Item.ItemType.BEER, ctx -> {
            SkillManaHelper.gainMana(ctx.skills, SkillKind.VOMIT, 10f);
            SkillManaHelper.gainMana(ctx.skills, SkillKind.PEE, 30f);
        });
        EFFECTS.put(Item.ItemType.AMULET, ctx -> ctx.state.amuletCount++);
        EFFECTS.put(Item.ItemType.CARROT, ctx -> ctx.state.carrotTimer = 10.0f);
        EFFECTS.put(Item.ItemType.CLOCK, ctx -> ctx.state.clockTimer = 2.0f);
        EFFECTS.put(Item.ItemType.INVISIBILITY, ctx -> ctx.state.invisibilityTimer = 2.0f);
        EFFECTS.put(Item.ItemType.LEMON, ctx -> ctx.state.lemonTimer = 10.0f);
        EFFECTS.put(Item.ItemType.ORANGE, ctx -> {
            ctx.state.orangeTimer = 10.0f;
            SkillManaHelper.gainMana(ctx.skills, SkillKind.PEE, 10f);
        });
        EFFECTS.put(Item.ItemType.ROTTEN_EGG, ctx ->
            SkillManaHelper.gainMana(ctx.skills, SkillKind.VOMIT, 20f)
        );
        EFFECTS.put(Item.ItemType.ROTTEN_MEAT, ctx -> {
            SkillManaHelper.gainMana(ctx.skills, SkillKind.VOMIT, 10f);
            SkillManaHelper.gainMana(ctx.skills, SkillKind.POOP, 20f);
        });
        EFFECTS.put(Item.ItemType.SHOES, ctx -> ctx.state.shoesTimer = 5.0f);
        EFFECTS.put(Item.ItemType.WATER, ctx ->
            SkillManaHelper.gainMana(ctx.skills, SkillKind.PEE, 40f)
        );
        EFFECTS.put(Item.ItemType.WHISKEY, ctx ->
            SkillManaHelper.gainMana(ctx.skills, SkillKind.VOMIT, 20f)
        );
    }

    private ItemEffectRegistry() {}

    public static void apply(Item.ItemType type, ItemEffectContext context) {
        ItemEffect effect = EFFECTS.get(type);
        if (effect != null) {
            effect.apply(context);
        }
    }

    public static void onConsumed(Item.ItemType type, ItemEffectContext context) {
        if (type == Item.ItemType.AMULET) {
            context.state.amuletCount--;
        }
    }
}
