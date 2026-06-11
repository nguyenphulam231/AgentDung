package com.agentdung.game.handlers;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.entities.Enemy;
import com.agentdung.game.entities.Player;
import com.agentdung.game.managers.EntityManager;
import com.agentdung.game.skills.Skill;
import com.badlogic.gdx.utils.Array;

public class SkillActivationContext {
    public final Player player;
    public final Enemy target;
    public final EntityManager entityManager;
    public final AgentDungGame game;
    public final Array<Skill> skills;
    public final int skillIndex;

    public SkillActivationContext(
        Player player,
        Enemy target,
        EntityManager entityManager,
        AgentDungGame game,
        Array<Skill> skills,
        int skillIndex
    ) {
        this.player = player;
        this.target = target;
        this.entityManager = entityManager;
        this.game = game;
        this.skills = skills;
        this.skillIndex = skillIndex;
    }
}
