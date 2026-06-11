package com.agentdung.game.skills;

import com.badlogic.gdx.utils.Array;

public final class SkillManaHelper {

    private SkillManaHelper() {}

    public static void gainMana(Array<Skill> skills, SkillKind kind, float amount) {
        for (Skill skill : skills) {
            if (skill.getKind() == kind) {
                skill.gainMana(amount);
            }
        }
    }
}
