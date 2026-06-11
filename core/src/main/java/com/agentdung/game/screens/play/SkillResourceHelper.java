package com.agentdung.game.screens.play;

import com.agentdung.game.skills.PeeSkill;
import com.agentdung.game.skills.PoopSkill;
import com.agentdung.game.skills.Skill;
import com.agentdung.game.skills.SpitSkill;
import com.agentdung.game.skills.VomitSkill;
import com.badlogic.gdx.utils.Array;

public final class SkillResourceHelper {

    private SkillResourceHelper() {}

    public static void disposeAll(Array<Skill> skills) {
        if (skills == null) return;
        for (Skill skill : skills) {
            if (skill instanceof VomitSkill) ((VomitSkill) skill).dispose();
            if (skill instanceof SpitSkill) ((SpitSkill) skill).dispose();
            if (skill instanceof PoopSkill) ((PoopSkill) skill).dispose();
        }
    }

    public static Array<Skill> createDefaultLoadout() {
        Array<Skill> skills = new Array<>();
        skills.add(new SpitSkill());
        skills.add(new PoopSkill());
        skills.add(new PeeSkill());
        skills.add(new VomitSkill());
        return skills;
    }
}
