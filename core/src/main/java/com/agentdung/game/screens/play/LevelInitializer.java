package com.agentdung.game.screens.play;

import com.agentdung.game.core.AgentDungGame;
import com.agentdung.game.entities.Player;
import com.agentdung.game.handlers.InputHandler;
import com.agentdung.game.managers.EntityManager;
import com.agentdung.game.managers.MapManager;
import com.agentdung.game.screens.GameHUD;
import com.agentdung.game.skills.Skill;
import com.badlogic.gdx.utils.Array;

public class LevelInitializer {
    private final AgentDungGame game;
    private final MapManager mapManager;
    private final EntityManager entityManager;
    private final GameHUD gameHUD;
    private final InputHandler inputHandler;

    public LevelInitializer(
        AgentDungGame game,
        MapManager mapManager,
        EntityManager entityManager,
        GameHUD gameHUD,
        InputHandler inputHandler
    ) {
        this.game = game;
        this.mapManager = mapManager;
        this.entityManager = entityManager;
        this.gameHUD = gameHUD;
        this.inputHandler = inputHandler;
    }

    public LevelSetupResult setup(int world, int level, PlaySessionState state, Array<Skill> existingSkills) {
        inputHandler.stopLoopingSounds();
        state.reset();
        SkillResourceHelper.disposeAll(existingSkills);

        mapManager.loadLevel(world, level);

        Player player = new Player(mapManager.playerSpawn.x, mapManager.playerSpawn.y, game);
        player.setSize(26);

        entityManager.enemyManager.init(mapManager, player);
        entityManager.clearAll();
        gameHUD.loadTextures();

        Array<Skill> skills = SkillResourceHelper.createDefaultLoadout();
        return new LevelSetupResult(player, skills);
    }

    public static final class LevelSetupResult {
        public final Player player;
        public final Array<Skill> skills;

        public LevelSetupResult(Player player, Array<Skill> skills) {
            this.player = player;
            this.skills = skills;
        }
    }
}
