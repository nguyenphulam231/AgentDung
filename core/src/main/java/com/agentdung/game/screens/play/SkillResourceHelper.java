package com.agentdung.game.screens.play;

import com.agentdung.game.skills.PiiSkill;
import com.agentdung.game.skills.PuupSkill;
import com.agentdung.game.skills.Skill;
import com.agentdung.game.skills.SupitSkill;
import com.agentdung.game.skills.VomicSkill;
import com.badlogic.gdx.utils.Array;

public final class SkillResourceHelper {

    private SkillResourceHelper() {}

    public static void disposeAll(Array<Skill> skills) {
        if (skills == null) return;
        for (Skill skill : skills) {
            if (skill instanceof VomicSkill) ((VomicSkill) skill).dispose();
            if (skill instanceof SupitSkill) ((SupitSkill) skill).dispose();
            if (skill instanceof PuupSkill) ((PuupSkill) skill).dispose();
        }
    }

    public static Array<Skill> createDefaultLoadout() {
        Array<Skill> skills = new Array<>();
        skills.add(new SupitSkill());
        skills.add(new PuupSkill());
        skills.add(new PiiSkill());
        skills.add(new VomicSkill());
        return skills;
    }
}
