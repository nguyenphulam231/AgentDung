package com.agentdung.game.items;

import com.agentdung.game.screens.play.PlaySessionState;
import com.agentdung.game.skills.Skill;
import com.badlogic.gdx.utils.Array;

public class ItemEffectContext {
    public final PlaySessionState state;
    public final Array<Skill> skills;

    public ItemEffectContext(PlaySessionState state, Array<Skill> skills) {
        this.state = state;
        this.skills = skills;
    }
}
